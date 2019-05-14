/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.SpellcheckerPhonetic;

import com.pipilika.spellcheckerContextaware.Config;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author adnan
 */
public class SpellcheckerForSearchPhonetic {

    static SpellcheckerForSearchPhonetic spellcheckerForSearch = null;
    SpellCheckerPhonetic spellChecker;
    Map suggessionMap = new HashMap<>();

    public Map getSuggessionMap() {
        return suggessionMap;
    }

    public void setSuggessionMap(Map suggessionMap) {
        this.suggessionMap = suggessionMap;
    }

    public static SpellcheckerForSearchPhonetic getInstance() {

        if (spellcheckerForSearch == null) {
            synchronized (SpellcheckerForSearchPhonetic.class) {
                if (spellcheckerForSearch == null) {

                    System.out.println("Printing Config.. ");

                    System.out.println("PHONETIC_MAP: " + Config.PHONETIC_MAP);
                    System.out.println("WORD_DICTONERY_PATH " + Config.WORD_DICTONERY_PATH);
                    System.out.println("WORD_DICTONERY_WITH_COUNT " + Config.WORD_DICTONERY_WITH_COUNT);

                    spellcheckerForSearch = new SpellcheckerForSearchPhonetic();
                }
            }
        }
        return spellcheckerForSearch;
    }

    public SpellcheckerForSearchPhonetic() {

        spellChecker = new SpellCheckerPhonetic(Config.WORD_DICTONERY_PATH);
    }

    public String getSuggessionFromQuery(String sentence) {
        
        String originalSentence = sentence;
        
        Map results = spellChecker.getSpellSuggessionFromSentence(originalSentence);
        setSuggessionMap(results);

        if (results.isEmpty()) {
            return "";
        } else {
//            System.out.println("spellchecker results " + results);
            for (Object word : results.keySet()) {

                String wrongWord = word.toString();

                Map<String, Integer> listObj = (HashMap<String, Integer>) results.get(wrongWord);

                if (!listObj.isEmpty()) {
                    Map.Entry<String, Integer> entry = listObj.entrySet().iterator().next();
                    String key = entry.getKey();

                    sentence = sentence.replaceAll(wrongWord, key);
                    
                   // System.out.println("final : " + sentence);

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
        SpellcheckerForSearchPhonetic spSearch = SpellcheckerForSearchPhonetic.getInstance();

        System.out.println(spSearch.getSuggessionFromQuery("মোঃ এমদাদুল হক চৌধুরী")); //maskata
        System.out.println(spSearch.getSuggessionMap());

    }

}
