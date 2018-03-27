/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.k_int.IR.IRQuery;
import com.k_int.IR.QueryModels.PrefixString;
import com.k_int.IR.SearchException;
import com.k_int.IR.SearchTask;
import com.k_int.IR.Searchable;
import com.k_int.IR.TimeoutExceededException;
import com.k_int.hss.HeterogeneousSetOfSearchable;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
//import mom.trd.opentheso.SelectedBeans.SelectedTerme;
import mom.trd.opentheso.bdd.helper.nodes.NodeFusion;
import mom.trd.opentheso.bdd.tools.AsciiUtils;
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
           // france id=6385; total = 364063 notices
           // Alpes id=3759; total = 3214 notices;
           // France physique id=6396; total = 11898 notices;
           // Brocéliande id=26236; total = 11 notices;
           // Gaule id=6492; total = 8153 notices
           
           
           
           lisIds = conceptHelper.getIdsOfBranch(hd, "8364", "TH_1", lisIds);
           
           if(lisIds.isEmpty()) return;
           int total = totalOfNotices(lisIds);
     //      lisIds = printChildren(hd, conn, "13090", "1" , "TH_1",lisIds);
           int j = 0;
            System.out.println("total des notices = " + total);
        } catch (SQLException ex) {
            Logger.getLogger(TestRecursive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int totalOfNotices(ArrayList<String> lisIds) {
        int total = 0;
        Properties p = new Properties();
        p.put("CollectionDataSourceClassName", "com.k_int.util.Repository.XMLDataSource");
        p.put("RepositoryDataSourceURL", "file:" + "/Users/Miled/NetBeansProjects/opentheso/src/main/webapp/repositories.xml");
        p.put("XSLConverterConfiguratorClassName", "com.k_int.IR.Syntaxes.Conversion.XMLConfigurator");
        p.put("ConvertorConfigFile", "/Users/Miled/NetBeansProjects/opentheso/src/main/webapp/SchemaMappings.xml");
        Searchable federated_search_proxy = new HeterogeneousSetOfSearchable();
        federated_search_proxy.init(p);
        try {
            IRQuery e = new IRQuery();
            //   e.collections = new Vector<String>();
            e.collections.add("KOHA/biblios");
            e.hints.put("default_element_set_name", "f");
            e.hints.put("small_set_setname", "f");
            e.hints.put("record_syntax", "unimarc");
            for (String idConcept : lisIds) {
                e.query = new PrefixString((new StringBuilder("@attrset bib-1 @attr 1=Koha-Auth-Number \"")).append(AsciiUtils.convertNonAscii("" + idConcept)).append("\"").toString());
                SearchTask st = federated_search_proxy.createTask(e, null);
                st.evaluate(5000);
                total = total + st.getTaskResultSet().getFragmentCount();
            }

        } catch (TimeoutExceededException | SearchException srch_e) {
            srch_e.printStackTrace();
        }
        return total;
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
        config.setMaximumPoolSize(100);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        
        // Zoomathia
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "frantiq2014");
        config.addDataSourceProperty("databaseName", "pactols");        
        config.addDataSourceProperty("portNumber", "5432");
        config.addDataSourceProperty("serverName", "pactols.frantiq.fr");  

        
        // Zoomathia
    /*    config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "pactols");        
        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");        
      */  
        
/*      config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "pactols");
        config.addDataSourceProperty("databaseName", "OTW");
  */      
  /*      config.addDataSourceProperty("portNumber", "5432");
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "frantiq2014");
        config.addDataSourceProperty("databaseName", "193.48.137.33");
*/
      //  config.addDataSourceProperty("serverName", "localhost");
        /*config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
                */
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }    
}
