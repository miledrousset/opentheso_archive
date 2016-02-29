/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.util.ArrayList;
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
        HikariDataSource conn = openConnexionPool();
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk("http://ark.frantiq.fr/ark:/");
        exportFromBDD.setServerAdress("http://pactols.frantiq.fr/");
        StringBuffer skos = exportFromBDD.exportGroup(conn,"TH_1", "2");
        
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
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "frantiq2014");
        config.addDataSourceProperty("databaseName", "pactols");        
        config.addDataSourceProperty("portNumber", "5432");
        config.addDataSourceProperty("serverName", "pactols.frantiq.fr");        
        
        
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
