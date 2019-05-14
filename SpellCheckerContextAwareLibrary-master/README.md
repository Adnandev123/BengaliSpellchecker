Context aware Spell Checker for Bengali
============================================

This is a context aware spell checker module for Bengali implemented in JAVA. This can be used as a standalone library. This spellchecker 
use context words as well as take phonetic distance to retrive spell suggestions. 

This module use BKTree with Levenshtein distance for first retrival of candidate spell sugestions, and use N-gram model to take context information into account. This spellchecker is currently being used in Bengali search engine https://www.pipilika.com

### How to use:

    SpellcheckerForSearch spSearch = SpellcheckerForSearch.getInstance();
    System.out.println(spSearch.getSuggessionFromQuery("পদ্দা সেতু")); 
    
### Methods

A complete overview of the spellchjecker can be found in the application section of paper named "Pipilika N-gram Viewer: An Efficient Large Scale N-gram Model for Bengali". Paper link: <https://ieeexplore.ieee.org/document/8554474/>

 

