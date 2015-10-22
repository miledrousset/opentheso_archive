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
import mom.trd.opentheso.core.imports.tabulate.ReadFileTabule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author miled.rousset
 */
public class TestGetSiteMap {
    
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
        
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> allIds = conceptHelper.getAllIdConceptOfThesaurus(conn, "TH_1");
        
        String url;
	Date actuelle = new Date();
	
//	* Definition du format utilise pour les dates
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//	* Donne la date au format "aaaa-mm-jj"
        String dat = dateFormat.format(actuelle);

        StringBuilder siteMap = new StringBuilder();
        siteMap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        
     //   String format = "dd/MM/yy H:mm:ss";

    //    java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("yyyy-MM-dd");

   //     System.out.println( formater.format( date ) ); 

        
        for (String allId : allIds) {
            
            // http://pactols.frantiq.fr/opentheso/?idc=13412&idt=TH_1
            // c'est l'URL qu'il faut composer.
            
            url = conceptHelper.getIdArkOfConcept(conn, allId, "TH_1");
          //  date = conceptHelper.getModifiedDateOfConcept(conn, allId, "TH_1");

            siteMap.append("  <url>\n");
            siteMap.append("    <loc>");
        //    siteMap.append("http://ark.frantiq.fr/ark:/");
            siteMap.append("http://pactols.frantiq.fr/opentheso/?idc=");
            siteMap.append(allId);
            siteMap.append("&amp;idt=TH_1");
         //   siteMap.append(url);
            siteMap.append("</loc>\n");
            
            if(dat != null) {
                siteMap.append("    <lastmod>");
                siteMap.append(dat);
                siteMap.append("</lastmod>\n");
            }
            siteMap.append("  </url>\n");
        }
        siteMap.append("</urlset>");
        
        System.out.println(siteMap.toString());
        
        conn.close();
    }
    
    
    /**
     * Test of Get datas for SiteMap.
     */
    @org.junit.Test

    public void testGetTheDate() {

	Date actuelle = new Date();
	
//	* Definition du format utilise pour les dates
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//	* Donne la date au format "aaaa-mm-jj"
        String dat = dateFormat.format(actuelle);
        
        System.out.println(dat);
    }

    
    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1000);
        config.setJdbc4ConnectionTest(false);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        /*    config.addDataSourceProperty("user", "opentheso");
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
