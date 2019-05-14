/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.spellcheckerContextaware;

/**
 *
 * @author adnan
 */
public class Config {

    public static final String RESOURCE_BASE_DIR = "/Users/mahbuburrubtalha/NetBeansProjects/Pipilika/SpellCheckerContextAwareLibrary/";
    public static String WORD_DICTONERY_PATH = (System.getenv("SPELL_WORD_DICTONERY_PATH") == null) ? RESOURCE_BASE_DIR + "resources/min 30 final.txt" : System.getenv("SPELL_WORD_DICTONERY_PATH");
    public static String WORD_DICTONERY_WITH_COUNT = (System.getenv("SPELL_WORD_DICTONERY_WITH_COUNT") == null) ? RESOURCE_BASE_DIR + "resources/unigramANDbigram.txt" : System.getenv("SPELL_WORD_DICTONERY_WITH_COUNT");

    public static String PHONETIC_MAP = (System.getenv("PHONETIC_MAP") == null) ? RESOURCE_BASE_DIR + "resources/phonetics_new.json" : System.getenv("PHONETIC_MAP");
//    public static String PHONETIC_MAP = "/spell_resources/phonetics_new.json";
///Users/mahbuburrubtalha/NetBeansProjects/Pipilika/SpellCheckerContextAwareLibrary/
    ///home/adnan/NetBeansProjects/SpellCheckerContextAwareLibrary
}
