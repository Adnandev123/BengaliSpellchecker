/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.spellcheckerContextaware;

import com.pipilika.SpellcheckerForSearch.SpellChecker;
import com.pipilika.SpellcheckerPhonetic.SpellCheckerPhonetic;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author adnan
 */
public class Main {

    public static void main(String args[]) {

        SpellCheckerPhonetic spellChecker = new SpellCheckerPhonetic(Config.WORD_DICTONERY_PATH);
        String sentence = "\"রয়েল বেংল টাইগার\"";// "আমাদের দেশ বর্তি সার্কাস্জ্ম হামলা মামলা দিয়ে এসব দাবিৎয়ে রাখা যাবে বলে মনে হয়না";

        System.out.println("suggessions for sentence : " + sentence);
        Map results = spellChecker.getSpellSuggessionFromSentence(sentence);
        System.out.println(results);

        for (Object word : results.keySet()) {

            String wrongWord = word.toString();
            Map<String, Integer> listObj = (HashMap<String, Integer>) results.get(wrongWord);

            Map.Entry<String, Integer> entry = listObj.entrySet().iterator().next();
            String key = entry.getKey();

            sentence = sentence.replaceAll(wrongWord, key);

            // sentence = sentence.replaceAll(wordMap.toString(), results.get(wordMap.toString()));
        }

        System.out.println(sentence);
    }

}
