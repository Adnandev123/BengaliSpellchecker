/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.SpellcheckerPhonetic;

import com.pipilika.spellcheckerContextaware.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author adnan
 */
public class SpellCheckerPhonetic {

    Phonetics ph;

    public SpellCheckerPhonetic(String path) {
        loadDictoneryWithCount(Config.WORD_DICTONERY_WITH_COUNT);
        ph = new Phonetics();
    }

    public Map<String, Integer> dictoneryWordCountMap = new HashMap();

    public void loadDictoneryWithCount(String path) {
        System.out.println("loading word Dictionary...");

        try {
            File rtsnefileDir = new File(path);
            BufferedReader in = null;
            String str;

            in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(rtsnefileDir), "UTF8"));

            int totalWordsLoaded = 0;

            while ((str = in.readLine()) != null) {

                try {
                    //System.out.println(str);
                    String[] splited = str.split(">");
                    String word = splited[0];
                    int count = Integer.parseInt(splited[1].toString().trim());
                    if (!dictoneryWordCountMap.containsKey(word.trim())) {
                        dictoneryWordCountMap.put(word.trim(), count);
                    }

                    if (totalWordsLoaded % 1000 == 0) {
                        System.out.println("Total words loaded: " + totalWordsLoaded);
                    }

                    totalWordsLoaded++;

                } catch (Exception e) {

                }
            }

            in.close();
        } catch (IOException ex) {
            Logger.getLogger(SpellCheckerPhonetic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SpellCheckerPhonetic.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Count Dictonery size : " + dictoneryWordCountMap.size());
        return;
    }

    /*given a sentence, it returns spell suggession results*/
    public Map getSpellSuggessionFromSentence(String sentence) {

        sentence = removeNoiseFromQuery(sentence);
        String[] words = sentence.split("\\s+");
        //  System.out.println(sentence.length());

        StringTokenizer st = new StringTokenizer(sentence);
        ArrayList wrongWords = new ArrayList<>();
        Map<String, Map<String, Integer>> suggession = new HashMap<>();
        Map suggessionmap = new HashMap();

        if (sentence.length() == 0) {
            return suggession;
        }

        //for first word of the sentence, special case.
        int queryLength = words.length;

        String firstword = null;
        String lastword = null;

        String lastWordContext = null;
        String fastWordContext = null;

        firstword = words[0];

        if (queryLength == 1) {  // have only one word, consider dictionary
            int wordCount = 0;
            if (dictoneryWordCountMap.containsKey(sentence)) {
                wordCount = dictoneryWordCountMap.get(sentence);
            }

            if (wordCount < 2000) {

                suggessionmap = getSpellSuggession("none", sentence.trim(), "none");
                suggession.put(firstword, suggessionmap);
                return suggession;

            } else {
                return suggession;
            }
        }

        if (queryLength > 1) {
            lastWordContext = words[queryLength - 2];
            fastWordContext = words[1];
            lastword = words[queryLength - 1];
        }

        suggessionmap = new HashMap();
        Map<String, String> alreadyCorrected = new HashMap();

        for (String ngram : ngrams(3, sentence)) {
            // System.out.println(ngram);

            for (String alreadyCorrectedWrongWord : alreadyCorrected.keySet()) {
                if (ngram.contains(alreadyCorrectedWrongWord)) {
                    ngram = ngram.replace(alreadyCorrectedWrongWord, alreadyCorrected.get(alreadyCorrectedWrongWord));
                }
            }

            String FirstWordFromNgram = ngram.split(" ")[0];
            String candidateWordInNgram = ngram.split(" ")[1];
            String lastWordFromNgram = ngram.split(" ")[2];

            int wordCount = 0;
            if (dictoneryWordCountMap.containsKey(candidateWordInNgram)) {
                wordCount = dictoneryWordCountMap.get(candidateWordInNgram);
            }

            // if (wrongWords.contains(candidateWordInNgram)) {
            if (wordCount < 2000) {
                // System.out.println("after replace " + ngram.replace(lastWord, " ").trim());
                suggessionmap = new HashMap();
                suggessionmap = getSpellSuggession(FirstWordFromNgram.trim(), candidateWordInNgram.trim(), lastWordFromNgram.trim());
                // System.out.println(candidateWordInNgram.trim() + " : " + suggessionmap);
                if (!suggessionmap.isEmpty()) {

                    suggession.put(candidateWordInNgram, suggessionmap);

                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) suggessionmap.entrySet().iterator().next();
                    alreadyCorrected.put(candidateWordInNgram, entry.getKey());
                }
            }
        }

        suggessionmap = new HashMap();

        if (words.length > 1) {
            for (String alreadyCorrectedWrongWord : alreadyCorrected.keySet()) {
                if (fastWordContext.equals(alreadyCorrectedWrongWord)) {
                    fastWordContext = fastWordContext.replace(alreadyCorrectedWrongWord, alreadyCorrected.get(alreadyCorrectedWrongWord));
                }
            }

            int wordCount = 0;
            if (dictoneryWordCountMap.containsKey(firstword + " " + fastWordContext)) {
                wordCount = dictoneryWordCountMap.get(firstword + " " + fastWordContext);
            }

            if (wordCount < 500) {
                suggessionmap = new HashMap();
                suggessionmap = getSpellSuggession("none", firstword, fastWordContext);

                if (!suggessionmap.isEmpty()) {
                    suggession.put(firstword, suggessionmap);
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) suggessionmap.entrySet().iterator().next();
                    alreadyCorrected.put(firstword, entry.getKey());
                }
            }
        }

        if (queryLength > 1) {
            for (String alreadyCorrectedWrongWord : alreadyCorrected.keySet()) {
                if (lastWordContext.equals(alreadyCorrectedWrongWord)) {
                    lastWordContext = lastWordContext.replace(alreadyCorrectedWrongWord, alreadyCorrected.get(alreadyCorrectedWrongWord));
                }
            }

            int wordCount = 0;
            if (dictoneryWordCountMap.containsKey(lastWordContext + " " + lastword)) {
                wordCount = dictoneryWordCountMap.get(lastWordContext + " " + lastword);
            }

            if (wordCount < 500) {

                suggessionmap = new HashMap();
                suggessionmap = getSpellSuggession(lastWordContext, lastword, "none");

                if (!suggessionmap.isEmpty()) {
                    suggession.put(lastword, suggessionmap);
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) suggessionmap.entrySet().iterator().next();
                    alreadyCorrected.put(lastword, entry.getKey());
                }
            }

        }

        return suggession;
    }

    public Map<String, Integer> getSpellSuggession(String preContext, String word, String postContext) {

        //  System.out.println("pre post sent : " + preContext + " " + word + " " + postContext);
        Map<String, Integer> suggessionMap = new HashMap<String, Integer>();
        Map<String, Integer> wordCountmap = new HashMap<String, Integer>();
        ArrayList<String> suggessionList = new ArrayList<String>();
        boolean allZeroCount = true;

        suggessionList = (ArrayList<String>) ph.getSuggessionList(ph.getPhoneticStructure(ph.getPhonetic(word)));

        for (String candidate : suggessionList) {

            try {
                int nGramCountPhrase = 0;
                int nGramCountWord = 0;
//                int nGramCountPhrase = nGramAPI.getNgramCount(phrase + " " + candidate);
//                int nGramCountWord = nGramAPI.getNgramCount(candidate);
                if (dictoneryWordCountMap.containsKey(preContext + " " + candidate)) {
                    nGramCountPhrase = dictoneryWordCountMap.get(preContext + " " + candidate);

                    //   System.out.println(preContext + " " + candidate + " :" + nGramCountPhrase);
                }

                if (dictoneryWordCountMap.containsKey(candidate + " " + postContext)) {
                    nGramCountPhrase = nGramCountPhrase + dictoneryWordCountMap.get(candidate + " " + postContext);

                    //  System.out.println(candidate + " " + postContext + " :" + nGramCountPhrase);
                }

                // System.out.println("pre post sent : " + preContext + " " + candidate + " " + postContext + " count: " + nGramCountPhrase);
                if (dictoneryWordCountMap.containsKey(candidate)) {
                    nGramCountWord = dictoneryWordCountMap.get(candidate);
                }

                if (nGramCountPhrase > 0) {
                    suggessionMap.put(candidate, nGramCountPhrase);
                    allZeroCount = false;
                }

                if (nGramCountWord > 0) {
                    wordCountmap.put(candidate, nGramCountWord);
                }

            } catch (Exception ex) {
                Logger.getLogger(SpellCheckerPhonetic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // if all candidate context count is zero, sort by candidate count
        if (allZeroCount == true) {
            suggessionMap = new HashMap();
            suggessionMap = wordCountmap;
        }

        suggessionMap = sortByValue(suggessionMap);
//        System.out.println(suggessionMap.size());

        return suggessionMap;
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
        sentence = sentence.replaceAll("“", " ");
        sentence = sentence.replaceAll("\\?", " "); //,
        sentence = sentence.replaceAll("\\?", " "); //,
        sentence = sentence.replaceAll(",", " ");
        sentence = sentence.replaceAll("-", " "); //‘
        sentence = sentence.replaceAll("‘", " "); //’
        sentence = sentence.replaceAll("’", " "); //*
        sentence = sentence.replaceAll("\\*", " ");
        sentence = sentence.replaceAll("\\.", " ");
        sentence = sentence.replaceAll("\\.", " ");
        sentence = sentence.replaceAll("\"", " ");
        sentence = sentence.replaceAll("[a-zA-Z]", " "); //[\u09E6-\u09EF]
        sentence = sentence.replaceAll("[\u09E6-\u09EF]", " "); //[\u09E6-\u09EF]

        sentence = sentence.trim().replaceAll(" +", " "); //?

        sentence = sentence.trim();
        
        System.out.println("after trim : " + sentence);

        return sentence;
    }

    public boolean getSpellCheck(String word) {

        Pattern p = Pattern.compile("[\u09E6-\u09EF]"); //RegEx for (২৩)
        Matcher m = p.matcher(word);
        if (m.find()) {
            return true;
        }
        Set<String> result = ph.bkTree.searchWithin(ph.getPhoneticStructure(ph.getPhonetic(word)), (double) 0);
        //   System.out.print(" answer:" + result);

        if (result.size() > 0) {
            return true; // not wrong
        } else {
            return false; // wrong
        }

    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {

        List<Map.Entry<K, V>> list
                = new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++) {
            ngrams.add(concat(words, i, i + n));
        }
        return ngrams;
    }

    private String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append((i > start ? " " : "") + words[i]);
        }
        return sb.toString();
    }

    public static void main(String args[]) {

        SpellcheckerForSearchPhonetic spSearch = SpellcheckerForSearchPhonetic.getInstance();
        Phonetics ph = new Phonetics();

        System.out.println(spSearch.getSuggessionFromQuery("\"সেকড়ের \"সন্দানে\"")); // সহীহ ইবনে হিব্বান

    }

}
