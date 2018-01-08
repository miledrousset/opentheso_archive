/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compare;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.SearchHelper;
import mom.trd.opentheso.bdd.helper.TestGetSiteMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author miled.rousset
 */
public class CompareCandidatConceptTest {
    
    public CompareCandidatConceptTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of Get datas for SiteMap.
     */
    @org.junit.Test

    public void compareCandidatPactols() {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        
        
        String idTheso = "TH_1";
        String idLang = "fr";
        String idGroup = "6";
        SearchHelper searchHelper = new SearchHelper();
        ArrayList<String> nodeSearchTerm;
        HashMap<String, String> hashMapSyn;
        StringBuilder stringBuilder = new StringBuilder();

        // lecture du fichier tabulé /Users/Miled/
    //    String path = "/Users/Miled/Desktop/candidatsPactols.csv";

        String path = "C:/Users/jm.prudham/Desktop/candidatsPactols.csv";
       
       
        String line;
       
        boolean first;
        
    //    stringBuilder.append("Numéro BDD\tnom d'origine\tnom PACTOLS\tId PACTOLS\tURL Ark\tDéfinition\tTerme générique\tSynonyme\n");

            //    new InputStreamReader(file));
        
        BufferedWriter bw = openFile("C:/Users/jm.prudham/Desktop/candidatsPactols_out.csv");
        if(bw == null) return;
      //  int i=0;
        try {
            File file = new File(path);  
            BufferedReader bf = new BufferedReader(
                  new InputStreamReader(
                      new FileInputStream(file), "UTF8"));
            while ((line = bf.readLine()) != null) {
            /*    lineOrigine = line.split("\t");
                if(lineOrigine.length < 2) continue;
                
                lineTmp = removeStopWords(lineOrigine[1]);
                */
                nodeSearchTerm = searchHelper.simpleSearchPreferredTerm(conn, line.trim(), idLang, idTheso, idGroup);
                hashMapSyn = searchHelper.simpleSearchNonPreferredTerm(conn, line.trim(), idLang, idTheso, idGroup);
                stringBuilder.append(line.trim());
                //stringBuilder.append(" #### ");
                stringBuilder.append("\t");
                first = true;
                for (String term : nodeSearchTerm) {
                    if(!first){
                        stringBuilder.append("##"); 
                    }
                    stringBuilder.append(term);
                    first = false;
                }
                stringBuilder.append("\t");
                first = true;
                for (Map.Entry<String, String> entry : hashMapSyn.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if(!first){
                        stringBuilder.append("##"); 
                    }
                    stringBuilder.append("Alt: ");
                    stringBuilder.append(value);
                    stringBuilder.append("-> Pref: ");
                    stringBuilder.append(key);
                    first = false;
                }
             
            //    System.out.println(stringBuilder.toString());
                bw.write(stringBuilder.toString());
                bw.newLine();
                stringBuilder.delete(0, stringBuilder.capacity());
           /*     i++;
                if(i==1000) {
                    closeFile(bw);
                    conn.close();
                    return;
                }*/
            }
        } catch (IOException ex) {
            Logger.getLogger(CompareConceptTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        closeFile(bw);
        conn.close();
    }
    
    private BufferedWriter openFile(String path) {
        try {
            BufferedWriter br = new BufferedWriter(
                    (new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)));
            return br;
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return null;
    }
    
    private void closeFile(BufferedWriter bw) {
        try {
            bw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }    
 
    private String removeStopWords(String line){
        String[] words = line.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        
        Set<String> stopWordsSet = new HashSet<>();
        stopWordsSet.add("à");
        stopWordsSet.add("de");
        stopWordsSet.add("le");
        stopWordsSet.add("la");
        
        for(String word : words)
        {
            if(!stopWordsSet.contains(word.toLowerCase()))
            {
                stringBuilder.append(word);
             //   line = line.replaceAll(word, "");
            }
        }

        return stringBuilder.toString().trim();
    }
    
    /**
     * lecture du fichier
     * @param path
     * @return 
     */
    private FileInputStream readFile(String path) {

        FileInputStream file;
        try {
            file = new FileInputStream(path);
            return file;
        } catch (Exception ex) {
            Logger.getLogger(TestGetSiteMap.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
        return null;
    }    
    
}
