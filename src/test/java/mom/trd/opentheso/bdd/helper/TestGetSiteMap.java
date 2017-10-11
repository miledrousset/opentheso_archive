/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import mom.trd.opentheso.bdd.helper.nodes.NodeConceptArkId;
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
        ArrayList<NodeConceptArkId> nodeConceptArkIds = conceptHelper.getAllConceptArkIdOfThesaurus(conn, "TH_1");
    //    ArrayList<String> allIds = conceptHelper.getAllIdConceptOfThesaurus(conn, "TH_1");
        
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

        

        for (NodeConceptArkId nodeConceptArkId : nodeConceptArkIds) {
            // http://ark.frantiq.fr/ark:/26678/pcrtoBSSWiOt51
            // c'est l'URL qu'il faut composer.

            siteMap.append("  <url>\n");
            siteMap.append("    <loc>");

        //    siteMap.append("http://pactols.frantiq.fr/opentheso/?idc=");
            
        //    if(nodeConceptArkId.getIdArk() == null || nodeConceptArkId.getIdArk().isEmpty()){
                siteMap.append("http://pactols.frantiq.fr/opentheso/?idc=");
                siteMap.append(nodeConceptArkId.getIdConcept());
                siteMap.append("&amp;idt=TH_1");
         /*   } else {
                siteMap.append("http://ark.frantiq.fr/ark:/");
                siteMap.append(nodeConceptArkId.getIdArk());
            }*/
            //siteMap.append(url);
            siteMap.append("</loc>\n");
            
            if(dat != null) {
                siteMap.append("    <lastmod>");
                siteMap.append(dat);
                siteMap.append("</lastmod>\n");
                
                siteMap.append("    <changefreq>");
                siteMap.append("monthly");
                siteMap.append("</changefreq>\n");
                
                siteMap.append("    <priority>0.9</priority>\n");
            }
            siteMap.append("  </url>\n");            
        }
        /*    for (String allId : allIds) { 
            // http://pactols.frantiq.fr/opentheso/?idc=13412&idt=TH_1
            // c'est l'URL qu'il faut composer.
            
            url = conceptHelper.getIdArkOfConcept(conn, allId, "TH_1");
          //  date = conceptHelper.getModifiedDateOfConcept(conn, allId, "TH_1");

            siteMap.append("  <url>\n");
            siteMap.append("    <loc>");
            siteMap.append("http://ark.frantiq.fr/ark:/");
        //    siteMap.append("http://pactols.frantiq.fr/opentheso/?idc=");
            
            
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
        */
        
        
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
        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }


    
}
