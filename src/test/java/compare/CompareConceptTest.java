/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compare;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.SearchHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.TestGetSiteMap;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;
import mom.trd.opentheso.bdd.tools.StringPlus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    public void testCompareCSVToTheso() {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        
        
        String idTheso = "TH_1";
        String idLang = "fr";
        String idGroup = "6";
        
        ConceptHelper conceptHelper = new ConceptHelper();
        StringPlus stringPlus = new StringPlus();
        
        NodeConcept nodeConcept;
        SearchHelper searchHelper = new SearchHelper();
        ArrayList<NodeAutoCompletion> nodeSearchs;
        StringBuilder stringBuilder = new StringBuilder();

        // lecture du fichier tabulé /Users/Miled/
        String path = "/Users/miledrousset/Desktop/codePatriarche.csv";
       
       
        FileInputStream file = readFile(path);         
        if(file == null) return;
        String line;
        String lineTmp;
        String[] lineOrigine;
        String conceptOrigine;

        
        boolean first = true;
        
        stringBuilder.append("nom origine\tprefLabel PACTOLS\taltLabel PACTOLS\tId PACTOLS\tURL Ark\tDéfinition\tTerme générique\tSynonyme");

        System.out.println(stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.capacity());

        BufferedReader bf = new BufferedReader(new InputStreamReader(file));
        try {
            while ((line = bf.readLine()) != null) {
             //   lineOrigine = line.split("\t");
             //  if(lineOrigine.length < 2) continue;
                
                lineTmp = stringPlus.unaccentLowerString(line);
                
                nodeSearchs = searchHelper.searchFullText(conn, lineTmp, idLang, idTheso);
                
         /*       stringBuilder.append(lineOrigine[0]);
                stringBuilder.append("\t");*/
                stringBuilder.append(line.trim());
                //stringBuilder.append(" #### ");

                if(nodeSearchs.isEmpty()) {
                    stringBuilder.append("\t");
                    stringBuilder.append("\t");
                    stringBuilder.append("\t");
                    stringBuilder.append("\t");
                    stringBuilder.append("\t");
                    stringBuilder.append("\t");
                    stringBuilder.append("\t");                    
                } else {
                
                    first = true;
                    for (NodeAutoCompletion nodeSearch : nodeSearchs) {
                        if(!first){
                            // stringBuilder.append(" $$$$ ");
                            stringBuilder.append("\n");
                        }
                        stringBuilder.append("\t");
                        stringBuilder.append(nodeSearch.getPrefLabel());
                        
                        stringBuilder.append("\t");
                        stringBuilder.append(nodeSearch.getAltLabel());                        
                        
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
                            if(nodeNote.getNotetypecode().equalsIgnoreCase("definition")){
                                stringBuilder.append(stringPlus.clearNewLine(nodeNote.getLexicalvalue()));
                                stringBuilder.append(" ## ");
                            }
                        }

                        // BT
                        stringBuilder.append("\t");
                        for (NodeBT nodeBT : nodeConcept.getNodeBT()) {
                            stringBuilder.append(nodeBT.getTitle());
                            stringBuilder.append(" ## ");
                        }

                        // UF
                        stringBuilder.append("\t");
                        for (NodeEM nodeEM : nodeConcept.getNodeEM()) {
                            stringBuilder.append(nodeEM.getLexical_value());
                            stringBuilder.append(" ## ");
                        }
                        first = false;
                    }
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
    
    @Test
    public void testIsTopTerm() {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        ConceptHelper conceptHelper = new ConceptHelper();
        boolean isTopConcept = conceptHelper.isTopConcept(conn, "1421", "TH_1");
        conn.close();
    }
            
    
    @Test
    public void testSearchGIN(){
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        
        
        String idTheso = "2";
        String idLang = "fr";
        String idGroup = "5";
        
        ConceptHelper conceptHelper = new ConceptHelper();
        StringPlus stringPlus = new StringPlus();
        
        NodeConcept nodeConcept;
        SearchHelper searchHelper = new SearchHelper();
        ArrayList<NodeSearch> nodeSearchs;        
        
      //  nodeSearchs = searchHelper.searchTermNew(conn, "saint", idLang, idTheso, idGroup, 1, true);
        
        TermHelper termHelper = new TermHelper();
        List<NodeAutoCompletion> nodeAutoCompletionList = termHelper.getAutoCompletionTerm(conn, idTheso, idLang, "saint");
      
  
  
        String test = "écolÉôïçèù";
        test = test.toLowerCase();
        test = stringPlus.unaccentLowerString(test);
        
        test = "مزهرية";
        test = stringPlus.unaccentLowerString(test);
        
        test = test.toUpperCase();
        test = test.toLowerCase();
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
