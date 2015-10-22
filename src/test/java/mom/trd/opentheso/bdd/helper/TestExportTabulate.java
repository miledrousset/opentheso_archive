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
import mom.trd.opentheso.core.imports.tabulate.ReadFileTabule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author miled.rousset
 */
public class TestExportTabulate {
    
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

    public void testReadTabulate() {
    
        ReadFileTabule readFileTabule = new ReadFileTabule();
        try {
          /*  readFileTabule.setReadFile(
                    new FileInputStream("/Users/Miled/Desktop/maquette_tabulé.csv"),
                    String FieldSeparate,
                    String subfieldSeparate,
                    String langSeparate, 
                            String formatDate);
                  */
           readFileTabule.setReadFile(
                    new FileInputStream("/Users/Miled/Desktop/maquette_tabulé.csv"),
                    ";",
                    "##",
                    "::",
                    "dd/MM/yyyy");                            
            readFileTabule.setFields();
            readFileTabule.setDatas();
            ArrayList<String> fields = readFileTabule.getFieldsList();
            ArrayList<TabulateDocument> tabulateDocuments = readFileTabule.getTabulateDocumentList();
            System.out.println("test");
            
        } catch (Exception ex) {
            Logger.getLogger(TestExportTabulate.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
        /**
     * Test of Tabulate reading.
     */
    @org.junit.Test

    public void testExportAllDatas() {
        HikariDataSource conn = openConnexionPool();
        
        ExportTabulateHelper exportTabulateHelper = new ExportTabulateHelper();
        
        exportTabulateHelper.setThesaurusDatas(conn, "TH_1");
        exportTabulateHelper.exportToTabulate();
        StringBuffer datas = exportTabulateHelper.getTabulateBuff();

        System.out.println(datas.toString());
        conn.close();
    
    }

    
    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1000);
 //       config.setJdbc4ConnectionTest(false);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        /*   config.addDataSourceProperty("user", "opentheso");
         config.addDataSourceProperty("password", "opentheso");
         config.addDataSourceProperty("databaseName", "OTW");
         */
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "frantiq2014");
        config.addDataSourceProperty("databaseName", "pactols");

      //  config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("portNumber", "5432");
        config.addDataSourceProperty("serverName", "opentheso.mom.fr");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }


    
}
