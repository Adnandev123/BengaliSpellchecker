/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.SpellcheckerPhonetic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipilika.spellcheckerContextaware.BKTree;
import com.pipilika.spellcheckerContextaware.Config;
import com.pipilika.spellcheckerContextaware.Distance;
import com.pipilika.spellcheckerContextaware.Levenshtein;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adnan
 */
public class Phonetics {

    Map<String, String> finalMap = new HashMap();
    Map<String, ArrayList<String>> phoneticMap = new HashMap();
    BKTree<String> bkTree;

    public Phonetics() {
        
        createPhoneticmap();
        
        System.out.println("Creating BK Tree");
        this.bkTree = createBKTree(Config.WORD_DICTONERY_PATH);
        System.out.println("BK Tree Created");

    }

    public void createPhoneticmap() {
        
        System.out.println("Creating Phonetic Map.");
        
        HashMap<String, Object> result = null;

        System.out.println("Phonetic Location: "+ Config.PHONETIC_MAP);
        
        File JSON_SOURCE = new File(Config.PHONETIC_MAP);
        
        try {
            result = new ObjectMapper().readValue(JSON_SOURCE, HashMap.class);
        } catch (IOException ex) {
            Logger.getLogger(Phonetics.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList lists = (ArrayList) result.get("patterns");

        HashMap map;
        for (Object list : lists) {

            map = (HashMap) list;

            String bn = map.get("bn").toString();
            String en = map.get("en").toString();

            finalMap.put(bn, en);

        }

        System.out.println("Phonetic map created");

    }

    /*this function loads the dictonery for once*/
    private TreeSet getinputDictonery(String path) {
        /* This function pre-loads lebeled cluster map (word -> cluster) */

        System.out.println("loading word Dictionary...");
        TreeSet<String> inputTree = new TreeSet<String>();
        int count = 0;

        try {
            File rtsnefileDir = new File(path);
            BufferedReader in = null;
            String str;
            String phonetic;

            in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(rtsnefileDir), "UTF8"));

            while ((str = in.readLine()) != null) {
                str = str.trim().replaceAll(" +", " ");
                phonetic = getPhonetic(str);
                phonetic = getPhoneticStructure(phonetic);

                //System.out.println(phonetic);
                if (phoneticMap.containsKey(phonetic)) {
                    ArrayList simillerList = phoneticMap.get(phonetic);
                    simillerList.add(str);
                    phoneticMap.put(phonetic, simillerList);
                } else {
                    ArrayList simillerList = new ArrayList();
                    simillerList.add(str);
                    phoneticMap.put(phonetic, simillerList);

                }

                inputTree.add(phonetic);

                if (count % 1000 == 0) {
                    System.out.println("Total data loaded: " + count);
                }

                count++;

            }

            in.close();
        } catch (IOException ex) {
            Logger.getLogger(SpellCheckerPhonetic.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(inputTree.size() + " words loaded");

        return inputTree;
    }

    /*this function creates BK-tree based on loaded dictonery*/
    private BKTree createBKTree(String path) {

        Distance<String> d = new Levenshtein();
        BKTree<String> bkTree = BKTree.build(getinputDictonery(path), d);

        return bkTree;

    }

    public List getSuggessionFromBKTree(String word) {
        System.out.println("Getting suggessions from pipilika spellchecker...");

        List<String> suggessionList = new ArrayList();
        int radius = 1;
        // System.out.println(word +" : "+wordLength);
        do {
            Set<String> results = bkTree.searchWithin(word, (double) radius);
            //Collections.sort(list, this);
            if (results == null || results.isEmpty()) {
                //   System.out.println("[none]\n");
            } else {
                for (Object object : results) {
                    // System.out.println(object + " " + "\n");
                    suggessionList.add(object.toString());
                }
            }
            radius++;

        } while (suggessionList.isEmpty() && radius <= 4);//suggessionList.isEmpty() &&

        return suggessionList;

    }

    public List getSuggessionList(String word) {
//        System.out.println("word: " + word);

        List<String> suggessionListPhonetic = new ArrayList<String>();
        List<String> suggessionListWord = new ArrayList<String>();
        List<String> suggessionListWordExact = new ArrayList<String>();

        suggessionListPhonetic = getSuggessionFromBKTree(word);

        for (String phonetic : suggessionListPhonetic) {
            if (phoneticMap.containsKey(phonetic)) {

                for (String st : phoneticMap.get(phonetic)) {

                    if (phonetic.equals(word) && SpellcheckerForSearchPhonetic.getInstance().spellChecker.dictoneryWordCountMap.containsKey(st)) {
                        if (SpellcheckerForSearchPhonetic.getInstance().spellChecker.dictoneryWordCountMap.get(st) > 300) {
//                            System.out.println(st + " " + SpellcheckerForSearchPhonetic.getInstance().spellChecker.dictoneryWordCountMap.get(st));
                            suggessionListWordExact.add(st);
                        }
                    }

                    suggessionListWord.add(st);
                }
            }
        }

//        System.out.println(suggessionListPhonetic);

        if (suggessionListWordExact.isEmpty()) {
//            System.out.println("not contains: " + suggessionListWord);

            return suggessionListWord;
        } else {
//            System.out.println("contains : " + suggessionListWordExact);

            return suggessionListWordExact;
        }

    }

    public String removeNoiseFromQuery(String sentence) {

        if (sentence == null) {
            return "";
        }

        sentence = sentence.replaceAll("\\(", " ");
        sentence = sentence.replaceAll("\\)", " ");
        sentence = sentence.replaceAll("\\[", " ");
        sentence = sentence.replaceAll("\\]", " ");
        sentence = sentence.replaceAll("৷", " ");
        sentence = sentence.replaceAll("।", " "); // “
        sentence = sentence.replaceAll("“", " ");//
        sentence = sentence.replaceAll("\\?", " "); //,
        sentence = sentence.replaceAll("\\?", " "); //,
        sentence = sentence.replaceAll(",", " ");
        sentence = sentence.replaceAll("-", " "); //‘
        sentence = sentence.replaceAll("‘", " "); //’
        sentence = sentence.replaceAll("’", " "); //*
        sentence = sentence.replaceAll("\\*", " ");
        sentence = sentence.replaceAll("\\.", " ");
        sentence = sentence.replaceAll("\"", " ");
        sentence = sentence.replaceAll("[a-zA-Z]", " "); //[\u09E6-\u09EF]
        sentence = sentence.replaceAll("[\u09E6-\u09EF]", " "); //[\u09E6-\u09EF]

        sentence = sentence.trim().replaceAll(" +", " "); //?

        sentence = sentence.trim();

        return sentence;
    }

    public String getPhonetic(String st) {

        //"রবীন্দ্রনাথও এত ভালো সেলফ পোট্রেইট আঁকতে পারে নাই!"
        //  System.out.println(st);
        st = removeNoiseFromQuery(st);

        if (st.length() == 0) {
            return "";
        }

        String stConstEn = st;
        String stConstBn = st;
        boolean breakloop = false;

        int i = 0;

        while (breakloop != true) {

            //    System.out.println(st);
            st = st.substring(0, st.length() - 1);
            st = st.replaceAll("[\u200c]", "").trim();
            //    System.out.println("to found :" + st); //র

            if (finalMap.containsKey(st)) {

                //     System.out.println("found in phonetic: " + st);
                stConstEn = stConstEn.replaceFirst(st, finalMap.get(st));
                stConstBn = stConstBn.replaceFirst(st, "").replaceAll("[\u200c]", "").trim();;
                st = stConstBn;

                //      System.out.println(stConstEn +" "+stConstBn);
            }

            if (st.toCharArray().length == 1 && finalMap.containsKey(st) == false) {
                //     System.out.println("not found in phonetic: " + st);
                stConstEn = stConstEn.replaceFirst(st, st);
                stConstBn = stConstBn.replaceFirst(st, "").replaceAll("[\u200c]", "").trim();;
                st = stConstBn;

                //    System.out.println(stConstEn +" "+stConstBn);
            }

            //    System.out.println(stConstBn.length());
            if (stConstBn.length() <= 1) {

                if (finalMap.containsKey(stConstBn)) {
                    stConstEn = stConstEn.replaceFirst(st, finalMap.get(stConstBn));
                }
                breakloop = true;
            }

        }

        //    System.out.println(stConstEn);
        return stConstEn;
    }

    public String getPhoneticStructure(String phonetic) {
        phonetic = phonetic.toLowerCase();
        //    phonetic = phonetic.toLowerCase().replaceAll("a", "-").replaceAll("e", "-").replaceAll("i", "-").replaceAll("o", "-").replaceAll("u", "-").replaceAll("y", "-");
        // phonetic = phonetic.replaceAll("-+", "-");

        return phonetic;
    }

    public static void main(String[] args) {

        Phonetics ph = new Phonetics();

        //    System.out.println(ph.phoneticMap.get("গ্যাস"));
        //   System.out.println(ph.phoneticMap.get("গেস"));
        System.out.println("Phonetic done");

        System.out.println(ph.getSuggessionList(ph.getPhoneticStructure(ph.getPhonetic("\"পিপিলিকা\""))));

    }

}
