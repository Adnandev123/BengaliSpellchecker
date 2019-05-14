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
public class Levenshtein implements Distance<String> {
        @Override
        public double eval(String x, String y) {
            int len_x = x.length(), len_y = y.length();
            int[][] row = new int[len_x + 1][len_y + 1];
            int i, j;
            int result;

            for (i = 0; i < len_x + 1; i++)
                row[i][0] = i;
            for (i = 0; i < len_y + 1; i++)
                row[0][i] = i;
            for (i = 1; i <= len_x; ++i) {
                for (j = 1; j <= len_y; ++j) {
                    int cost = 0;
                    if (x.substring(i - 1, i).equals(y.substring(j - 1, j))) {
                        cost = 0;
                    } else {
                        cost = 1;
                    }
                    int replace = row[i - 1][j - 1] + cost;// replace
                    int delete = row[i][j - 1] + 1;// deletion
                    int insert = row[i - 1][j] + 1;// insertion
                    row[i][j] = Math.min(Math.min(replace, delete), insert);
                }
            }
            result = (Integer) (row[len_x][len_y]);

            return result;
        }
    }