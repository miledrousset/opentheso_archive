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
    /*@org.junit.Test

    public void testReadSkosFile() {
    
        HikariDataSource conn = openConnexionPool();
        
        ReadFileSKOS readFileSKOS = new ReadFileSKOS();
        

        
        try {
            
            FileInputStream file = new FileInputStream(
                    "/Users/Miled/Google Drive/Projets/OpenTheso/demo Nice-Cepam/branches Pactols/Sujets/faune.rdf");
            readFileSKOS.readBranchFile(conn,
                    file,
                    "yyyy-MM-dd",false, "http://localhost");
            
            
            SKOSXmlDocument sKOSXmlDocument = readFileSKOS.getThesaurus();
            //System.out.println("test");
            
            
            WriteBranchSkosBDD writeBranchSkosBDD = new WriteBranchSkosBDD(conn);*/
            
            
            
            
            // permet d'iporter une branche externe au thésaurus à partir d'un domaine (toute une branche)
            
//            importBranchAfterGroup(
//            String idGroup,
//            String idThesaurus,
//            SKOSXmlDocument skosDocument, String dateFormat,
//            boolean useArk, String adressSite, int idUser)            
    /*        writeBranchSkosBDD.importBranchAfterGroup(
                    "MT_7",
                    "1",
                    sKOSXmlDocument, "yyyy-MM-dd", false, "http://localhost",
                    1);*/
            
            
            // permet d'importer une branche externe au thésaurus à parti d'un concept
            //
//            public void importBranchAfterConcept(
//            String idConcept,
//            String idGroup,
//            String idThesaurus,
//            SKOSXmlDocument skosDocument, String dateFormat,
//            boolean useArk, String adressSite, int idUser)
            //
          /*  writeBranchSkosBDD.importBranchAfterConcept("1",
                    "MT_1",
                    "1",
                    sKOSXmlDocument, "yyyy-MM-dd", false, "http://localhost",
                    1);*/
            
            
       /* } catch (Exception ex) {
            Logger.getLogger(TestGetSiteMap.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
        
    }*/
    
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

    
   /* private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1000);
        config.setJdbc4ConnectionTest(false);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        
        // Zoomathia
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "Zoomathia");        
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
        /*HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }

*/
    
}
