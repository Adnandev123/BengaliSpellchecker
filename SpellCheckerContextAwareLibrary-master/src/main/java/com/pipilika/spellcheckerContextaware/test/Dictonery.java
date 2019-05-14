/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pipilika.spellcheckerContextaware.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adnan
 */
public class Dictonery {

    //if its number, dont check it    
    public static void main(String args[]) {
        /* This function pre-loads lebeled cluster map (word -> cluster) */

        System.out.println("loading word Dictonery...");
        Set<String> wordSet = new TreeSet();

        File fileDir = new File("/home/adnan/NetBeansProjects/SpellCheckerContextAwareLibrary/unigramANDbigram.txt");
        Writer outFile = null;

        try {
            outFile = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileDir), "UTF8"));
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(Dictonery.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            File rtsnefileDir = new File("/media/adnan/Common/Ngrams With Counts/unigram.txt");
            BufferedReader in = null;
            String str;

            in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(rtsnefileDir), "UTF8"));

            while ((str = in.readLine()) != null) {

                outFile.write(str);
                outFile.write("\n");

            }

            in.close();

        } catch (IOException ex) {
            Logger.getLogger(Dictonery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Dictonery.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            File rtsnefileDir = new File("/home/adnan/Desktop/Dictonery/merge_2010_2017_new.txt");
            BufferedReader in = null;
            String str;

            in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(rtsnefileDir), "UTF8"));

            while ((str = in.readLine()) != null) {

                outFile.write(str);
                outFile.write("\n");
            }

            outFile.close();
            in.close();

        } catch (IOException ex) {
            Logger.getLogger(Dictonery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Dictonery.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
