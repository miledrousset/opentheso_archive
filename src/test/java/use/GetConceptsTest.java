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
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeConceptArkId;
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
public class GetConceptsTest {
    
    public GetConceptsTest() {
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
        String idGroup = "5";
        
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        
        ArrayList<String> lisIds = new ArrayList<>();
        
        ArrayList<String> allIds = conceptHelper.getIdsOfBranch(conn,
                "6385", idTheso, lisIds);//getAllIdConceptOfThesaurusByGroup(conn, idTheso, idGroup);
        StringBuilder file = new StringBuilder();
        
        Term term;
       
        file.append("Id_concept");
        file.append("\t");        
        file.append("prefLabel");
        file.append("\n");
        
        for (String idConcept : allIds) {
            
            term = termHelper.getThisTerm(conn, idConcept, idTheso, idLang);
            if(term != null) {
                // écriture dans le fichier
                file.append(idConcept);
                file.append("\t");
                file.append(term.getLexical_value());
                file.append("\n");
            }
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
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "pactols");
        config.addDataSourceProperty("databaseName", "pactols");

      //  config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("portNumber", "5436");
        config.addDataSourceProperty("serverName", "localhost");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }

}
