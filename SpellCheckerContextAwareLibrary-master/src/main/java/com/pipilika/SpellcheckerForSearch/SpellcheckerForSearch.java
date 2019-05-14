/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.SpellcheckerForSearch;

import com.pipilika.spellcheckerContextaware.Config;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author adnan
 */
public class SpellcheckerForSearch {

    static SpellcheckerForSearch spellcheckerForSearch = null;
    SpellChecker spellChecker;
    Map suggessionMap = new HashMap<>();

    public Map getSuggessionMap() {
        return suggessionMap;
    }

    public void setSuggessionMap(Map suggessionMap) {
        this.suggessionMap = suggessionMap;
    }

    public static SpellcheckerForSearch getInstance() {

        if (spellcheckerForSearch == null) {
            synchronized (SpellcheckerForSearch.class) {
                if (spellcheckerForSearch == null) {
                    spellcheckerForSearch = new SpellcheckerForSearch();
                }
            }
        }
        return spellcheckerForSearch;
    }

    public SpellcheckerForSearch() {

        spellChecker = new SpellChecker(Config.WORD_DICTONERY_PATH);
    }

    public String getSuggessionFromQuery(String sentence) {
        String originalSentence = sentence;

        Map results = spellChecker.getSpellSuggessionFromSentence(originalSentence);
        setSuggessionMap(results);

        if (results.isEmpty()) {
            return "";
        } else {
            //System.out.println("spellchecker results " + results);
            for (Object word : results.keySet()) {

                String wrongWord = word.toString();

                Map<String, Integer> listObj = (HashMap<String, Integer>) results.get(wrongWord);

                if (!listObj.isEmpty()) {
                    Map.Entry<String, Integer> entry = listObj.entrySet().iterator().next();
                    String key = entry.getKey();

                    sentence = sentence.replaceAll(wrongWord, key);

                }
                // sentence = sentence.replaceAll(wordMap.toString(), results.get(wordMap.toString()));
            }

            if (sentence.equals(originalSentence)) {
                return "";
            } else {
                return sentence;
            }

        }
    }

    public static void main(String arg[]) {
        SpellcheckerForSearch spSearch = SpellcheckerForSearch.getInstance();

//        System.out.println(spSearch.getSuggessionFromQuery("পদ্দা সেতু")); //maskata
//        System.out.println(spSearch.getSuggessionMap());

    }

}
