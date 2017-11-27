/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compare;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.SearchHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.TestGetSiteMap;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeTab2Levels;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author miled.rousset
 */
public class CompareConceptTest {
    
    public CompareConceptTest() {
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

    public void testExportAllDatas() {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        
        
        String idTheso = "TH_1";
        String idLang = "fr";
        String idGroup = "6";
        
        ConceptHelper conceptHelper = new ConceptHelper();
        NodeConcept nodeConcept;
        SearchHelper searchHelper = new SearchHelper();
        ArrayList<NodeSearch> nodeSearchs;
        StringBuilder stringBuilder = new StringBuilder();

        // lecture du fichier tabulé /Users/Miled/
        String path = "/Users/Miled/Desktop/mots-clefs.csv";
       
       
        FileInputStream file = readFile(path);         
        if(file == null) return;
        String line;
        String lineTmp;
        
        BufferedReader bf = new BufferedReader(new InputStreamReader(file));
        try {
            while ((line = bf.readLine()) != null) {
                lineTmp = removeStopWords(line);
                nodeSearchs = searchHelper.searchTerm(conn, lineTmp, idLang, idTheso, idGroup, 2, false);
                stringBuilder.append(line);
                //stringBuilder.append(" #### ");
                stringBuilder.append("\t");
                for (NodeSearch nodeSearch : nodeSearchs) {
                    stringBuilder.append(nodeSearch.getLexical_value());
                    stringBuilder.append("\t");
                    stringBuilder.append(nodeSearch.getIdConcept());

                    
                    // récupération des données d'un Concept
                    nodeConcept = conceptHelper.getConcept(conn, nodeSearch.getIdConcept(), idTheso, idLang);
                    
                    // URL
                    stringBuilder.append("\t");
                    if(nodeConcept.getConcept().getIdArk() != null || !nodeConcept.getConcept().getIdArk().isEmpty()) {
                        stringBuilder.append("http://ark.frantiq.fr/ark:/");
                        stringBuilder.append(nodeConcept.getConcept().getIdArk());
                    }
                    
                    // définition
                    stringBuilder.append("\t");
                    for (NodeNote nodeNote : nodeConcept.getNodeNotesTerm()) {
                        if(nodeNote.getNotetypecode().equalsIgnoreCase("definition"))
                            stringBuilder.append(nodeNote.getLexicalvalue());
                    }
                    
                   // stringBuilder.append(" $$$$ ");
                    stringBuilder.append("\n");
                    stringBuilder.append("\t");
                    
                }

                System.out.println(stringBuilder.toString());
                stringBuilder.delete(0, stringBuilder.capacity());
            }
        } catch (IOException ex) {
            Logger.getLogger(CompareConceptTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //ArrayList<NodeConceptArkId> allIds = conceptHelper.getAllConceptArkIdOfThesaurus(conn, idTheso);
     //   StringBuilder file = new StringBuilder();

      /*  ArrayList<String> allIds = conceptHelper.getAllIdConceptOfThesaurusByGroup(conn, idTheso, idGroup);
        
        
        for (String idConcept : allIds) {

            nodeConcept = conceptHelper.getConcept(conn, idConcept, idTheso, idLang);
            System.out.println("idConcept = " + idConcept + "  " + nodeConcept.getTerm().getLexical_value());
            
        }*/
      
        conn.close();
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
