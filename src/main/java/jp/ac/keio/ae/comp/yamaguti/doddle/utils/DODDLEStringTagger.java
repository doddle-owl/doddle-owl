/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved. 
 * 
 * This file is part of DODDLE-OWL.
 * 
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.io.*;
import java.util.*;

/**
 * 
 * Chasen(ver 2.3.3), Mecab(ver 0.93)を利用して，文書を入力として，品詞，単語，原形を獲得するクラス
 * 
 * @author takeshi morita
 */
public class DODDLEStringTagger {
    
    public enum MophologicalAnalyzerType {
        CHASEN,
        MECAB
    }
    
    private static Process process;
    private MophologicalAnalyzerType type;
    public static String Japanese_Morphological_Analyzer = "C:/Program Files/Chasen/chasen.exe";
    private static DODDLEStringTagger tagger;
    
    private DODDLEStringTagger() {
        if (Japanese_Morphological_Analyzer.indexOf("chasen") != -1) {
            type=MophologicalAnalyzerType.CHASEN;
        } else if (Japanese_Morphological_Analyzer.indexOf("mecab") != -1) {
            type=MophologicalAnalyzerType.MECAB;
        }       
    }
    
    private static void initProcess() {
        try {
            Runtime rt = Runtime.getRuntime();
            process = rt.exec(Japanese_Morphological_Analyzer);
        } catch(IOException ioe) {
            ioe.printStackTrace();  
        }
    }
    
    private BufferedReader getBufferedReader() {
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }
    
    private BufferedWriter getBufferedWriter() {
        return new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }
    
    public List<DODDLEToken> analyze(String text) {
        List<DODDLEToken> tokenList = new ArrayList<DODDLEToken>();
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            initProcess();
            writer = getBufferedWriter();
            // System.out.println("get writer");
            writer.write(text);
            // System.out.println("write text");
            
            reader = getBufferedReader();
            // System.out.println("get reader");
            while(reader.ready()) {
                String line = reader.readLine();        
                if (type == MophologicalAnalyzerType.CHASEN) {
                    String[]  lines = line.split("\t");
                    if (4 <= lines.length) {
                        // System.out.println(line);
                        tokenList.add(new DODDLEToken(lines[3], lines[2], lines[0]));
                    }
                } else if (type == MophologicalAnalyzerType.MECAB) {
                    String[] lines = line.split("\t");
                    if (lines.length == 2) {
                        String[] attrs = lines[1].split(",");
                        tokenList.add(new DODDLEToken(attrs[0], attrs[6], lines[0]));
                    }
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        return tokenList; 
    }

    public static DODDLEStringTagger getInstance() {
        if (tagger == null) {
            tagger = new DODDLEStringTagger();
        }
        return tagger;
    }
    
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("InputDocument.txt"));        
        String text = "";
        while (reader.ready()) {
            String line = reader.readLine();
            text += line;
        }
        reader.close();
        /*
         * System.out.println(text); StringTagger tagger =
         * StringTagger.getInstance(); List<Token> tokenList =
         * tagger.analyze(text); for (Token token: tokenList) {
         * System.out.print(token.getPos()+"\t");
         * System.out.print(token.getString()+"\t");
         * System.out.print(token.getBasicString()+"\t"); System.out.println(); }
         */
        System.out.println("************************************");
        DODDLEStringTagger tagger2 = DODDLEStringTagger.getInstance();
        List<DODDLEToken> tokenList = tagger2.analyze("０５年度以降、四半期ごとに５～８％程度の伸びを続けており、今年７～９月の３カ月間だけで約９０万件増えた。光ファイバー回線などを使い、従来の固定電話と同じ番号を使う方式のＩＰ電話の伸びが目立つ。 ");
        for (DODDLEToken token: tokenList) {
            System.out.print(token.getPos()+"\t");
            System.out.print(token.getString()+"\t");
            System.out.print(token.getBasicString()+"\t");
            System.out.println();
        }
    }
}
