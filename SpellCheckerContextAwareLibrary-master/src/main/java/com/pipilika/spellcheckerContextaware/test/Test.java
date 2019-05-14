/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.spellcheckerContextaware.test;

import com.pipilika.SpellcheckerPhonetic.SpellcheckerForSearchPhonetic;

/**
 *
 * @author adnan
 */
public class Test {
    
    public static void main(String args[]){
        
         SpellcheckerForSearchPhonetic spSearch = SpellcheckerForSearchPhonetic.getInstance();
         
         System.out.println(spSearch.getSuggessionFromQuery("শেক্সপিয়রের উপন্যাস"));
         System.out.println(spSearch.getSuggessionMap());
         
//        try {
//            File rtsnefileDir = new File("/home/adnan/NetBeansProjects/SpellCheckerContextAwareLibrary/test/test.txt");
//            BufferedReader in = null;
//            String str;
//
//            in = new BufferedReader(
//                    new InputStreamReader(
//                            new FileInputStream(rtsnefileDir), "UTF8"));
//
//            while ((str = in.readLine()) != null) {
//             spSearch = SpellcheckerForSearchPhonetic.getInstance();
//             
//             System.out.println(str);
//             
//             
//             
//            System.out.println(spSearch.getSuggessionFromQuery(str));
//
//             
//             
//             System.out.println("================================");
//
//            }
//
//            in.close();
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
}
