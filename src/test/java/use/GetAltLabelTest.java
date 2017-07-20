/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package use;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeTab2Levels;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author miled.rousset
 */
public class GetAltLabelTest {
    
    public GetAltLabelTest() {
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
        HikariDataSource conn = openConnexionPool();
        
        String idTheso = "TH_1";
        String idLang = "fr";
        String idGroup = "6";
        
        boolean passed = false;
        
        
       // ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        NoteHelper noteHelper = new NoteHelper();
        
        
        //ArrayList<NodeConceptArkId> allIds = conceptHelper.getAllConceptArkIdOfThesaurus(conn, idTheso);
        StringBuilder file = new StringBuilder();

        ArrayList<NodeEM> nodeEMs;
        ArrayList<NodeTab2Levels> nodeConceptTermId = termHelper.getAllIdOfNonPreferredTermsByGroup(conn, idTheso, idGroup);
        
        Term term;
        ArrayList<NodeNote> nodeNotes;
       
        file.append("Id_concept");
        file.append("\t");
        file.append("prefLabel");
        file.append("\t");
        file.append("altLabel");
        file.append("\t");
        file.append("définition");
        file.append("\n");
        
        for (NodeTab2Levels nodeTab2Levels : nodeConceptTermId) {
            nodeEMs = termHelper.getNonPreferredTerms(conn, nodeTab2Levels.getIdTerm(), idTheso, idLang);
            
            if(!nodeEMs.isEmpty()) {
                
                term = termHelper.getThisTerm(conn, nodeTab2Levels.getIdConcept(), idTheso, idLang);
                nodeNotes = noteHelper.getListNotesTerm(conn, nodeTab2Levels.getIdTerm(), idTheso, idLang);
                
                // écriture dans le fichier
                file.append(nodeTab2Levels.getIdConcept());
                file.append("\t");
                file.append(term.getLexical_value());
                file.append("\t");
                
                for (NodeEM nodeEM : nodeEMs) {
                    if(passed) {
                        file.append("##");
                    }
                    file.append(nodeEM.getLexical_value());
                    passed = true;

                }
                file.append("\t");
                for (NodeNote nodeNote : nodeNotes) {
                    if(nodeNote.getNotetypecode().equalsIgnoreCase("definition")) {
                        file.append(nodeNote.getLexicalvalue());
                    }
                }
                file.append("\n");
            }
            passed = false;
        }
        
        System.out.println(file.toString());
        
        conn.close();
    }
        
    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        /*    config.addDataSourceProperty("user", "opentheso");
         config.addDataSourceProperty("password", "opentheso");
         config.addDataSourceProperty("databaseName", "OTW");
         */
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "pactols");

      //  config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }

}
