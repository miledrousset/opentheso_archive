/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miled.rousset
 */
public class TestRecursive {
    
    public TestRecursive() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    
    @org.junit.Test
    public void getAllChildrensOfConcept(){
        try {
            HikariDataSource hd = openConnexionPool();
            Connection conn = hd.getConnection();
            ConceptHelper conceptHelper = new ConceptHelper();

            
           ArrayList<String> lisIds = new  ArrayList<>();
           lisIds = conceptHelper.getIdsOfBranch(hd, "12866", "TH_1", lisIds);
     //      lisIds = printChildren(hd, conn, "13090", "1" , "TH_1",lisIds);
           int j = 0;
        } catch (SQLException ex) {
            Logger.getLogger(TestRecursive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
   
    private ArrayList<String> printChildren(HikariDataSource hd,
            Connection conn, 
            String idConceptDeTete,
            String idGroup, String idTheso, ArrayList<String> lisIds) {

        ConceptHelper conceptHelper = new ConceptHelper();

    //    ArrayList<String> lisIds = new ArrayList<>();
        
        lisIds.add(idConceptDeTete);
        ArrayList<String> listIdsOfConceptChildren
                = conceptHelper.getListChildrenOfConcept(hd,
                        idConceptDeTete, idTheso);
        


       /* if (!relationsHelper.setRelationMT(conn, idConceptDeTete, idGroup, idTheso)) {
            return false;
        }*/
        System.out.println("idConcept = " + idConceptDeTete);

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
            /*if (!relationsHelper.setRelationMT(conn, listIdsOfConceptChildren1, idGroup, idTheso)) {
                return false;
            }*/
         //   System.out.println("idConcept = " + listIdsOfConceptChildren1);
            printChildren(hd, conn, listIdsOfConceptChildren1,
                    idGroup, idTheso, lisIds);
        }
        return lisIds;
    }
    
    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        
        // Zoomathia
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "pactols");        
        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");        
        
        
/*      config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "pactols");
        config.addDataSourceProperty("databaseName", "OTW");
  */      
        /*
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "frantiq2014");
        config.addDataSourceProperty("databaseName", "pactols");

      //  config.addDataSourceProperty("serverName", "localhost");
        /*config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
                */
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }    
}
