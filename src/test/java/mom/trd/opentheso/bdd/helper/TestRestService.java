/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.exports.tabulate.ThesaurusDatas;
import mom.trd.opentheso.core.exports.tabulate.TabulateDocument;
import mom.trd.opentheso.core.exports.helper.ExportTabulateHelper;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.imports.old.ReadFileSKOS;
import mom.trd.opentheso.core.imports.old.WriteBranchSkosBDD;
import mom.trd.opentheso.core.imports.old.WriteSkosBDD;
import mom.trd.opentheso.core.imports.tabulate.ReadFileTabule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import skos.SKOSXmlDocument;

/**
 *
 * @author miled.rousset
 */
public class TestRestService {
    
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
     * Test of Tabulate reading.
     */
    @org.junit.Test

    public void testExportGroup() {
 /*       HikariDataSource conn = openConnexionPool();
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk("http://ark.frantiq.fr/ark:/");
        exportFromBDD.setServerAdress("http://pactols.frantiq.fr/");
        StringBuffer skos = exportFromBDD.exportGroup(conn,"TH_1", "2");
        
        if(skos == null)
            System.out.println("");
        else 
            System.err.println(skos.toString());

        conn.close();
   */ }

    
    /**
     *  permet de retourner les concepts d'une branche à l'envers(en partant du concept jusqu'au concept TT)
     */
    @org.junit.Test
    public void testGetInvertPathConcept() {
        
/*        ArrayList<String> path = new ArrayList<>();
        ArrayList<ArrayList<String>> tabId = new ArrayList<>();
        
        HikariDataSource conn = openConnexionPool();
        
        ConceptHelper conceptHelper = new ConceptHelper();
        path.add("5");
        tabId = conceptHelper.getPathOfConceptWithoutGroup(conn, "5", "1", path, tabId);
        
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk("http://ark.frantiq.fr/ark:/");
        exportFromBDD.setServerAdress("http://pactols.frantiq.fr/");
        StringBuffer skos = exportFromBDD.exportConceptByLot(conn,"1", tabId);
        
        if(skos == null)
            System.out.println("");
        else 
            System.err.println(skos.toString());

        conn.close();
        */
    }
    
    
    /**
     * permet de retourner les concepts à partir d'une date donnée (Delta de création ou modification)
     */
    @org.junit.Test
    public void testDeltaOfConcept() {
        
        Date actuelle = new Date();
	
//	* Definition du format utilise pour les dates
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//	* Donne la date au format "aaaa-mm-jj"
        String date = dateFormat.format(actuelle);
        
        date = "2015-06-01";
        
        ArrayList<String> tabId = new ArrayList<>();
        ArrayList<ArrayList<String>> multiTabId = new ArrayList<>();
        
        HikariDataSource conn = openConnexionPool();
        ConceptHelper conceptHelper = new ConceptHelper();

        tabId = conceptHelper.getConceptsDelta(conn, "1", date);
        if(tabId == null) {
            conn.close();
            return;
        }
        if(tabId.isEmpty()) {
            conn.close();
            return;
        }
        
        multiTabId.add(tabId);
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk("http://ark.frantiq.fr/ark:/");
        exportFromBDD.setServerAdress("http://pactols.frantiq.fr/");
        StringBuffer skos = exportFromBDD.exportConceptByLot(conn,"1", multiTabId);
        
        if(skos == null)
            System.out.println("");
        else 
            System.err.println(skos.toString());

        conn.close();
    }    
    
    
    
    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(100);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        
        // Zoomathia
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "4.2.2");        
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
