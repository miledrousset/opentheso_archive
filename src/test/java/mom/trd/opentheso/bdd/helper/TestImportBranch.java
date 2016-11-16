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
public class TestImportBranch {
    
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
/*    @org.junit.Test

    public void testImportBranchAfterGroup() {    
        HikariDataSource conn = openConnexionPool();
        
        String idTheso = "";
        String idGroup = "";
        String path = "/Users/Miled/ownCloud_cnrs/Projets/OpenTheso/artefacts/25.xml";
       
        String dateFromat = "yyyy-MM-dd";
        boolean useArk = false;
        String adresseSite = "http://localhost";      
        int idUser = 1;


        
        FileInputStream file = readFile(path); 
        
        SKOSXmlDocument sKOSXmlDocument = readSkosFile(conn,
                file,
                dateFromat,
                useArk,
                adresseSite);
                

        // permet d'importer une branche entière sous un domaine avec l'alignement automatique à la source
        WriteBranchSkosBDD writeBranchSkosBDD = new WriteBranchSkosBDD(conn);
        //idGroup, idThesaurus, sKOSXmlDocument, dateFormat, ark, adressSite, user);
        writeBranchSkosBDD.importBranchAfterGroup(
                idGroup,
                idTheso,
                sKOSXmlDocument, dateFromat, useArk,
                adresseSite, idUser);
    }*/
    
    /**
     * Test of Tabulate reading.
     */
    @org.junit.Test

    public void TestImportMultiBranchUnderGroup() {    
        HikariDataSource conn = openConnexionPool();
        
        String idTheso = "1";
        String idGroup = "";
        String path = "/Users/Miled/ownCloud_cnrs/GDS_FRANTIQ/Pactols/2016-11-15/Sujets.xml";
       
        String dateFromat = "yyyy-MM-dd";
        boolean useArk = false;
        String adresseSite = "http://localhost";      
        int idUser = 1;


        
        FileInputStream file = readFile(path); 
        
        SKOSXmlDocument sKOSXmlDocument = readSkosFile(conn,
                file,
                dateFromat,
                useArk,
                adresseSite);
                

        // permet d'importer une branche entière avec son domaine en intégrant l'alignement à la source
        WriteBranchSkosBDD writeBranchSkosBDD = new WriteBranchSkosBDD(conn);
        //idGroup, idThesaurus, sKOSXmlDocument, dateFormat, ark, adressSite, user);
        writeBranchSkosBDD.importMultiBranchUnderGroup(
                idTheso,
                idGroup,
                sKOSXmlDocument, dateFromat, useArk,
                adresseSite, idUser);
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
    
    private SKOSXmlDocument readSkosFile(HikariDataSource conn,
            FileInputStream file,
            String dateFromat,
            boolean useArk,
            String adresseSite) {
        
        try {
            ReadFileSKOS readFileSKOS = new ReadFileSKOS();
            readFileSKOS.readBranchFile(conn,
                    file,
                    dateFromat,useArk,
                    adresseSite);
            
            SKOSXmlDocument sKOSXmlDocument = readFileSKOS.getThesaurus();
            return sKOSXmlDocument;
        } catch (Exception ex) {
            Logger.getLogger(TestImportBranch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
        /**
     * Test of Tabulate reading.
     */
    /*@org.junit.Test

    public void testExportAllDatas() {
    /*    HikariDataSource conn = openConnexionPool();
        
        ExportTabulateHelper exportTabulateHelper = new ExportTabulateHelper();
        
        exportTabulateHelper.setThesaurusDatas(conn, "TH_1");
        exportTabulateHelper.exportToTabulate();
        StringBuffer datas = exportTabulateHelper.getTabulateBuff();

        System.out.println(datas.toString());
        conn.close();
    
    }*/

    
    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        
        // Zoomathia
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "opentheso_mom");        
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
