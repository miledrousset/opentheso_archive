/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;
import org.glassfish.jersey.jaxb.internal.XmlCollectionJaxbProvider.App;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

//@ManagedBean(name = "treeBean", eager = true)
//@ApplicationScoped
/**
 *
 * @author miled.rousset
 */
public class QueriesPostgresTest {

    public QueriesPostgresTest() {
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

    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        
        config.setMinimumIdle(1);
        config.setAutoCommit(true);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        

        /*    config.addDataSourceProperty("user", "opentheso");
         config.addDataSourceProperty("password", "opentheso");
         config.addDataSourceProperty("databaseName", "OTW");
         */
        
//        dataSource.serverName=localhost
//dataSource.serverPort=5433
//dataSource.user=opentheso
//dataSource.password=opentheso
//dataSource.databaseName=zoo
        
        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "OTW");

        config.addDataSourceProperty("serverName", "localhost");
        //config.addDataSourceProperty("serverName", "opentheso.mom.fr");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }

    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testAddThesaurus() {
     System.out.println("addThesaurus");
     Thesaurus thesaurus;

     thesaurus = new Thesaurus();
     thesaurus.setTitle("test_miled");
     thesaurus.setLanguage("fr");

     ThesaurusHelper instance = new ThesaurusHelper();

     HikariDataSource conn = openConnexionPool();


     String result = instance.addThesaurus(conn, thesaurus);
     System.out.println(result);
        
     instance.deleteThesaurusTraduction(conn, result, "fr");
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testAddTraductionThesaurus() {
     System.out.println("addThesaurus");
     Thesaurus thesaurus;

     thesaurus = new Thesaurus();
     thesaurus.setTitle("test_miled");
     thesaurus.setLanguage("fr");
     thesaurus.setId_thesaurus("5");
                

     ThesaurusHelper instance = new ThesaurusHelper();

     HikariDataSource conn = openConnexionPool();


     boolean result = instance.addThesaurusTraduction(conn, thesaurus);
     System.out.println("" + result);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /* @org.junit.Test
     public void testUpdateThesaurus() {
     System.out.println("addThesaurus");
     Thesaurus thesaurus;

     thesaurus = new Thesaurus();
     thesaurus.setTitle("test_miled_modifie");
     thesaurus.setLanguage("fr");
     thesaurus.setId_thesaurus("ark:/66666/srvq9a5Ll41sk_25");
                

     ThesaurusHelper instance = new ThesaurusHelper();

     HikariDataSource conn = openConnexionPool();


     boolean result = instance.UpdateThesaurus(conn, thesaurus);
     System.out.println("" + result);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testGetListThesaurus() {
     System.out.println("addThesaurus");
     Thesaurus thesaurus;

     thesaurus = new Thesaurus();
     thesaurus.setTitle("test_miled_modifie");
     thesaurus.setLanguage("fr");
     thesaurus.setId_thesaurus("ark:/66666/srvq9a5Ll41sk_25");
                

     ThesaurusHelper instance = new ThesaurusHelper();

     HikariDataSource conn = openConnexionPool();


        
     java.util.Map result = instance.getListThesaurus(conn, "fr");
     System.out.println("" + result.toString());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of getThisLanguage method, of class LanguageHelper.
     */
    /*@Test
     public void testGetThisLanguage() {
     System.out.println("getThisLanguage");
     HikariDataSource ds;

     String idLang = "fr";
     LanguageHelper instance = new LanguageHelper();
     ds = openConnexionPool();
        
     Languages_iso639 result = instance.getThisLanguage(ds, idLang);
     System.out.println(result.getFrench_name());
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testAddConceptGroup() {
     System.out.println("addConceptGroup");
     NodeConceptGroup nodeConceptGroup;

     nodeConceptGroup = new NodeConceptGroup();
     nodeConceptGroup.setIdLang("fr");
     nodeConceptGroup.setLexicalValue("lieux");
        
     ConceptGroup conceptGroup = new ConceptGroup();
     conceptGroup.setIdthesaurus("5");
     conceptGroup.setIdtypecode("MT");
     nodeConceptGroup.setConceptGroup(conceptGroup);
                

     ConceptGroupHelper instance = new ConceptGroupHelper();

     HikariDataSource conn = openConnexionPool();


     String result = instance.addGroup(conn, nodeConceptGroup);
     System.out.println("" + result);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testGetThisConceptGroup() {
     System.out.println("addConceptGroup");
     NodeConceptGroup nodeConceptGroup;
                

     ConceptGroupHelper instance = new ConceptGroupHelper();

     HikariDataSource conn = openConnexionPool();


     nodeConceptGroup = instance.getThisConceptGroup(conn, "ark:/66666/srvq9a5Ll41sk_1", "5", "fr");
     System.out.println("" + nodeConceptGroup.getLexicalValue());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testGetListConceptGroup() {
     System.out.println("getListConceptGroup");
     ArrayList<NodeConceptGroup> nodeConceptGroup;
                

     ConceptGroupHelper instance = new ConceptGroupHelper();

     HikariDataSource conn = openConnexionPool();


     nodeConceptGroup = instance.getListConceptGroup(conn, "ark:/66666/srvq9a5Ll41sk_25", "be");
     //  System.out.println("" + nodeConceptGroup.toString());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testGetConceptGroupTraduction() {
     System.out.println("getConceptGroupTraduction");
     ArrayList<NodeGroupTraductions> nodeGroupTraductionsList;
                

     ConceptGroupHelper instance = new ConceptGroupHelper();

     HikariDataSource conn = openConnexionPool();


     nodeGroupTraductionsList = instance.getGroupTraduction(conn,
     "MT_46", "TH_320", "fr");
     System.out.println("" + nodeGroupTraductionsList.toString());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testGetTopConcepts() {
     System.out.println("getTopConcepts");
     ArrayList<NodeConceptGroup> nodeConceptGroup;
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();
     ArrayList <NodeConceptTree> nodeConceptTree =
     instance.getListTopConcepts(conn,
     "MT_46", "TH_320", "en");

     System.out.println("" + nodeConceptTree.toString());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of haveChildren method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testHaveChildren() {
     System.out.println("haveChildren");
     boolean children = false;
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();
     children  = instance.haveChildren(conn,
     "ark:/66666/srvq9a5Ll41sk_25", "100");
        
        

     //   nodeConceptGroup = instance.getListConceptGroup(conn, "5", "fr");
     System.out.println("" + children);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of haveChildren method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testAddTopConcept() {
       
     System.out.println("addTopConcept");
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();
     Concept concept = new Concept();
     concept.setIdGroup("MT_46");
     concept.setIdThesaurus("TH_320");
     concept.setStatus("D");
     concept.setTopConcept(true);

     Term term = new Term();
     term.setId_thesaurus("TH_320");
     term.setLang("en");
     term.setLexical_value("Asie_en");

        
     String idConcept = "";
     // idConcept = instance.addTopConcept(conn, "MT_46", concept, term);

     System.out.println("" + idConcept);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of haveChildren method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testAddTopConceptTraduction() {
       
     System.out.println("addTopConceptTraduction");
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();

     Term term = new Term();
     term.setId_thesaurus("TH_320");
     term.setId_term("T_14");
     term.setId_concept("C_54");
     term.setLang("fr");
     term.setLexical_value("Asie_fr");

        
     boolean status = instance.addTopConceptTraduction(conn, term);

     System.out.println("" + status);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of haveChildren method, of class QueriesPostgres.
     */
    /*  @org.junit.Test
     public void testAddConcept() {
       
     System.out.println("addConcept");
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();

     Concept concept = new Concept();
     concept.setIdConcept("C_1");
     concept.setIdGroup("MT_46");
     concept.setIdThesaurus("TH_320");
     concept.setStatus("D");
     concept.setTopConcept(false);
        
     Term term = new Term();
     term.setId_thesaurus("TH_320");
     term.setLang("fr");
     term.setLexical_value("France");
     term.setStatus("D");

     String idTopConcept = "C_1";
     String idParent = "C_1";
        
     String idConcept = instance.addConcept(conn, 
     idTopConcept, idParent, 
     concept, term);

     System.out.println("" + idConcept);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of haveChildren method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testAddConceptTraduction() {
       
     System.out.println("addConceptTraduction");
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();

     Term term = new Term();
     term.setId_thesaurus("TH_1");
     term.setId_term("T_");
     term.setLang("en");
     term.setLexical_value("French");

     boolean status = false;
     status = instance.addConceptTraduction(conn, term);

     System.out.println("" + status);
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*    @org.junit.Test
     public void testGetListConcepts() {
     System.out.println("getListConcepts");
     ArrayList<NodeConceptTree> nodeConceptTree;
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();

     nodeConceptTree = instance.getListConcepts(conn,
     "C_1", "TH_1", "fr");

     System.out.println("" + nodeConceptTree.toString());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*   @org.junit.Test
     public void testGetConcept() {
     System.out.println("getConcept");
     NodeConcept nodeConcept;
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();

     nodeConcept = instance.getConcept(conn,
     "C_34", "TH_1", "fr");

     System.out.println("" + nodeConcept.toString());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*   @org.junit.Test
     public void testAddRT() {
     System.out.println("AddRT");
     HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
        
     hierarchicalRelationship.setIdConcept1("T1");
     hierarchicalRelationship.setIdConcept2("T4");
     hierarchicalRelationship.setIdThesaurus("TH_1");
     hierarchicalRelationship.setRole("RT");
        

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();

     //      instance.addAssociativeRelation(conn, hierarchicalRelationship);

     System.out.println("") ;
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testGetNodeAutoCompletion() {
     System.out.println("testGetNodeAutoCompletion");

     List <NodeAutoCompletion> nodeAutoCompletionList;

     TermHelper instance = new TermHelper();

     HikariDataSource conn = openConnexionPool();

     nodeAutoCompletionList = instance.getAutoCompletionTerm(conn, "TH_1" ,"fr" , "a" );

     System.out.println("") ;
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of addThesaurus method, of class QueriesPostgres.
     */
    /*    @org.junit.Test
     public void testGetPathOfConcept() {
     System.out.println("getPathOfConcept");
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();
        
     ArrayList<String> path = new  ArrayList<String>();
        
     path.add("C_26");
        
     ArrayList <ArrayList<String>> tab = new ArrayList<ArrayList<String>>();
     tab = instance.getPathOfConcept(conn,
     "C_26", "TH_1", path, tab);

     System.out.println("" + tab.toString());
        
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
    /*    @org.junit.Test
     public void testGetAutoCompletionTerm() {
     System.out.println("getAutoCompletionTerm");
                

     TermHelper instance = new TermHelper();

     HikariDataSource conn = openConnexionPool();
        
     List <NodeAutoCompletion> nodeAutoCompletion;
        
     nodeAutoCompletion = instance.getAutoCompletionTerm(conn,
     "TH_1", "fr", "a");
        

     System.out.println("" + nodeAutoCompletion.toString());
        
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
    /*    @org.junit.Test
     public void testGetListCandidats() {
     System.out.println("getListCandidats");
                

     CandidateHelper instance = new CandidateHelper();

     HikariDataSource conn = openConnexionPool();
        
     ArrayList <NodeCandidatList> nodeCandidatLists;
        
     nodeCandidatLists = instance.getListCandidatsWaiting(conn, "TH_1", "fr");
        
     ArrayList <NodeUser>  nodeUser = instance.getListUsersOfCandidat(conn, "CA_1", "TH_1");
        
     NodeProposition nodeProposition = instance.getNodePropositionOfUser(conn, "CA_1", "TH_1", 1);
        

     System.out.println("" + nodeUser.toString());
        
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
    /*   @org.junit.Test
     public void testAddCandidat() {
     System.out.println("addCandidat");
                

     ConceptHelper instance = new ConceptHelper();

     HikariDataSource conn = openConnexionPool();
     //    TermHelper term = new TermHelper();
     //       term.deletePropositionCandidat(conn,"CA_1", 1, "TH_1");
     String candidat = instance.addCandidat(conn, 
     "Lili2",
     "fr",
     "TH_1",
     1,
     "ceci est un test",
     null, null);

     TermHelper term = new TermHelper();
     ArrayList <NodeEM> nodeEMs;
     nodeEMs = term.getNonPreferredTerms(conn,"T_1","TH_1","fr");
        
        
     System.out.println("" + nodeEMs.toString());
        
     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
     /*@org.junit.Test
     public void testSearchTerm() {
     System.out.println("SearchTerm");
                

     SearchHelper searchHelper = new SearchHelper();

     HikariDataSource conn = openConnexionPool();
     ArrayList <NodeSearch> searchs = searchHelper.searchTerm
     (conn, "etudes", "fr", "TH_1", "MT_1");
        
     System.out.println("" + searchs.toString());

     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
     
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
     @org.junit.Test
     public void testSearchTermById() {
/*     System.out.println("SearchTermId");
                

     SearchHelper searchHelper = new SearchHelper();

     HikariDataSource conn = openConnexionPool();
     ArrayList <NodeSearch> searchs = searchHelper.searchIdConcept(conn,
             "4000", "TH_1");
             
     System.out.println("" + searchs.toString());

     conn.close();
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
  */   //fail("The test case is a prototype.");
     }
     
     
     
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
    /*    @org.junit.Test
     public void testResizeImage() {
     System.out.println("resizeImage");
                

     ImagesHelper imageHelper = new ImagesHelper();

     //        HikariDataSource conn = openConnexionPool();

        
     System.out.println("" );
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
    /*    @org.junit.Test
     public void testIsIdOfGroup() {
     System.out.println("Is Id of Group ?");
                

     ConceptGroupHelper conceptGroupHelper = new ConceptGroupHelper();

     HikariDataSource conn = openConnexionPool();
     //      boolean test = conceptGroupHelper.isIdOfGroup(conn, "MT_1", "TH_1");

        
     //      System.out.println(""+ test);
        

     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     }*/
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testReadSkos() {
     try {
     System.out.println("Read Skos");
            
            
     ReadFileSKOS parseurReadSKOS = new ReadFileSKOS();
            
     HikariDataSource conn = openConnexionPool();
     //    parseurReadSKOS.readFile(conn, "/Users/Miled/Google Drive/Projets/OpenTheso/Base de teste/pactols_2014-02-04.skos.xml");
     //       parseurReadSKOS.readFile(conn, new FileInputStream("/Users/Miled/Google Drive/Projets/OpenTheso/Base de teste/demo.skos.xml"),"YYYY-MM-DD");
            
     System.out.println("" );
            
            
     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     } catch (Exception ex) {
     Logger.getLogger(QueriesPostgresTest.class.getName()).log(Level.SEVERE, null, ex);
     }
     }*/
    /**
     * Test of AutoCompletionTerm method, of class QueriesPostgres.
     */
    /*@org.junit.Test
     public void testWriteSkos() {
     //   ExportFromBDD exportFromBDD = new ExportFromBDD("/Users/Miled/Google Drive/Projets/OpenTheso/Base de teste/ecrire3.skos.xml");
     //   try {
     System.out.println("Write Skos");
            
     HikariDataSource conn = openConnexionPool();
            
     ExportFromBDD exportFromBDD = new ExportFromBDD();
             
     //         String skos = exportFromBDD.exportThisGroup(conn, "TH_31", "2");
     //         System.out.println("" + skos);        
             
     //        String skos = exportFromBDD.exportGroup(conn, "TH_31", "2");
     //      System.out.println("" + skos);

     //      String skos = exportFromBDD.exportConcept(conn, "TH_31", "3261");

     //      String skos_branche = exportFromBDD.exportBranchOfConcept(conn, "TH_31", "3261");
     //      System.out.println("" + skos_branche);
            
            
     //assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     //fail("The test case is a prototype.");
     } catch (Exception ex) {
     Logger.getLogger(QueriesPostgresTest.class.getName()).log(Level.SEVERE, null, ex);
            
     try {
     exportFromBDD.getWriteSKOS().getWriteFile().close();
     } catch (IOException ex1) {
     Logger.getLogger(QueriesPostgresTest.class.getName()).log(Level.SEVERE, null, ex1);
     }
     } 
     }*/
    /**
     * Test of Get List Permuté.
     */
    /*   @org.junit.Test
     public void testGetListPermute() {
    
     SearchHelper searchHelper = new SearchHelper();
     HikariDataSource conn = openConnexionPool();
     ArrayList <NodePermute> nodePermute = searchHelper.getListPermuteNonPreferredTerm(conn, "TH_22" , "fr", "vi","1");
            
     System.out.println("" + nodePermute.toString());
     }
     */
    /**
     * Test of Split method.
     */
    /*@org.junit.Test
     public void testSplitConceptForPermute() {
    
     TermHelper termHelper = new TermHelper();
     HikariDataSource conn = openConnexionPool();
     //        String value = new ConceptHelper().getLexicalValueOfConcept(conn, "12", "TH_1", "fr");
        
     //        termHelper.splitConceptForPermute(
     //                conn, "12", "fr", "TH_1", value);
            
     System.out.println("");
     }*/
    /**
     * Test of Facet methods.
     */
    /*@org.junit.Test
     public void testFacetMethod() {
    
     FacetHelper facetHelper = new FacetHelper();
     HikariDataSource conn = openConnexionPool();
        
     /*       System.out.println("Ajout d'une Facette");
     int idFacet = facetHelper.addNewFacet(conn,
     "TH_21",
     "Première Facette", "fr",""); 
        
        
        
     System.out.println("Ajout d'une traduction");
     facetHelper.addFacetTraduction(conn,
     idFacet,
     "TH_21",
     "first facet", "en");
        
        
     System.out.println("update d'une traduction");
     facetHelper.updateFacetTraduction(conn,
     idFacet,
     "TH_21", "en",
     "first facet modified");
        
        
     System.out.println("get idConcept of Facet");
     ArrayList <String> list = facetHelper.getIdConceptsOfFacet(conn,
     idFacet, "TH_21");
        
        
     System.out.println("get idConcept of Facet");
     ArrayList listIdFacet = facetHelper.getIdFacetOfConcept(conn,
     "1001", "TH_21");
        
        
     /
        
     ArrayList<NodeFacet> nodeFacetList = facetHelper.getAllFacetsOfThesaurus(conn,
     "TH_21", "fr");
        
     System.out.println("");
     }*/
    /**
     * Test of Facet methods.
     */
    /* @org.junit.Test
     public void testFacetMethod() {
    
     AlignmentHelper aliHelper = new AlignmentHelper();
     HikariDataSource conn = openConnexionPool();
        
     System.out.println("Ajout d'un alignement");
     //    aliHelper.addNewAlignment(conn,
     //           1, "", "", "", 1, "", "");
        
        
     System.out.println("Ajout d'un alignement");
     }*/
    /**
     * Test of Facet methods.
     */
    @org.junit.Test
    public void testGetCodeArk() {

    }

    /**
     * Test of Facet methods.
     */
    /*    @org.junit.Test
     public void testSearchNonPreferedTerm() {
    
     SearchHelper searchHelper = new SearchHelper();
     //   HikariDataSource conn = openConnexionPool();
        
     System.out.println("recherche intelligente");
     //    ArrayList <NodePermute> nodeSearchs =  searchHelper.getListPermute(conn,"TH_35" , "fr","guerre");
        
        
     System.out.println("Ajout d'un alignement");
     }
     */
    /**
     * Test of Facet methods.
     */
    /*@org.junit.Test
     public void testGetAlignementType() {
    
     AlignmentHelper alignmentHelper = new AlignmentHelper();
     HikariDataSource conn = openConnexionPool();
        
     System.out.println("Type d'alignement");
     //       Map p =  alignmentHelper.getAlignmentType(conn);
        
        
     System.out.println("Ajout d'un alignement");
     }*/
    /**
     * Test of Facet methods.
     */
    /*    @org.junit.Test
     public void testGeneratePermutedTable() {
    
     SearchHelper searchHelper = new SearchHelper();

     //     HikariDataSource conn = openConnexionPool();
        
        
     //        searchHelper.generatePermutedTable(conn, "TH_35");
        
     System.out.println("");

     } */
    /**
     * Test of Facet methods.
     */
       @org.junit.Test
     public void testGenerateArkIds() {
    
    /*    ToolsHelper toolsHelper = new ToolsHelper();

        HikariDataSource conn = openConnexionPool();


        toolsHelper.GenerateArkIds(conn, "TH_1");

        System.out.println("");
        conn.close();*/
    } 
     
     
     /**
     * Test of Facet methods.
     */
    @org.junit.Test
    public void testGenerateArkIds_UnSeulCode() {
    /*    HikariDataSource conn = openConnexionPool();

        Ark_Client ark_Client = new Ark_Client();

        ArrayList<DcElement> dcElementsList = new ArrayList<>();


        DcElement dcElement1 = new DcElement();
            // cette fonction permet de remplir la table Permutée
        dcElement1.setName("description");
        dcElement1.setValue("test_valeur1 en francais");
        dcElement1.setLanguage("fr");

        dcElementsList.add(dcElement1);
        
        DcElement dcElement2 = new DcElement();
        dcElement2.setName("description");
        dcElement2.setValue("test_valeur2 en anglais");
        dcElement2.setLanguage("en");
        
        dcElementsList.add(dcElement2);
            
        // String date, String url, String title, String creator, String description, String type
        String idArk = ark_Client.getArkId(
                new FileUtilities().getDate(),
                "http://pactols.frantiq.fr/" + "?idc=" + "334545334340eza0" + "&idt=" + "TH_35",
                "334545334340eza0",
                "Frantiq",
                dcElementsList,
                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection

        conn.close();*/
    }

    /**
     * Test of Facet methods.
     */
    @org.junit.Test

    public void testURLsValides() {

        /*        if(connect != null){
         HikariDataSource ds = connect.getPoolConnexion();
         }*/
        /*     URL url1;
         try {
         url1 = new URL("http://Opentheso3.mom.fr/OpenTheso/?idc=4157&idt=TH_35");
         try {
         for (int i = 0; i < 10; i++) {
                    
         HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();

         // // Gets the status code from an HTTP response message.
         int jh = connection1.getResponseCode();
         /*   int fh = 200;


         if(jh == fh)
         {

         System.out.println(" fichier existe code est: " + jh);
         }
         else
         {
         System.out.println(" fichier introuvable " + jh);
         }*/
        /*          }
         } catch (IOException ex) {
         Logger.getLogger(QueriesPostgresTest.class.getName()).log(Level.SEVERE, null, ex);
         }
         System.out.println(" terminé");
            
            
            
         } catch (MalformedURLException ex) {
         Logger.getLogger(QueriesPostgresTest.class.getName()).log(Level.SEVERE, null, ex);
         }*/
    }
    
    /**
     * Test of Opentheso WebServices.
     */
    @org.junit.Test

    public void testOpenThesoServices() {

        HikariDataSource conn = openConnexionPool();
        
 /*       try {
            ConceptHelper conceptHelper = new ConceptHelper();
         //   HikariDataSource conn = openConnexionPool();
            
            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
            
            hierarchicalRelationship.setIdConcept1("10017");
            hierarchicalRelationship.setIdConcept2("3555");
            hierarchicalRelationship.setIdThesaurus("TH_5");
            hierarchicalRelationship.setRole("BT");
            
            
            conceptHelper.addLinkHierarchicalRelation(conn.getConnection(), hierarchicalRelationship);
            
            System.out.println("");
            conn.close();
            
            //conceptToSkos("300", "TH_35");
        } catch (SQLException ex) {
            conn.close();
        }*/
    }
    
    
    /**
     * Test of Opentheso WebServices.
     */
    @org.junit.Test

    public void testgGtConceptCountOfBranch() {
        
    /*    HikariDataSource conn = openConnexionPool();
        StatisticHelper statisticHelper = new StatisticHelper();
        int nb = 0;
        int total = statisticHelper.getConceptCountOfBranch(conn, "105142", "1");
        System.out.println("" + total);
        System.out.println("" + statisticHelper.getNombreConcept());
        
        conn.close();*/
    }
    
    /**
     * Test of récupération des orphelin dans le thésaurus pour les ranger dans la table des orphelins
     */
    @org.junit.Test

    public void testOrphanDetect() {
        
    /*    HikariDataSource conn = openConnexionPool();
        ToolsHelper toolsHelper = new ToolsHelper();
        boolean test = toolsHelper.orphanDetect(conn, "TH_1");
        System.out.println("" + test);
        
        conn.close();*/
    }
    
    @org.junit.Test

    public void testGetVersion() {
        System.out.println(getClass().getPackage().getImplementationVersion());
        System.out.println(QueriesPostgresTest.class.getPackage().getImplementationVersion());
    /*    HikariDataSource conn = openConnexionPool();
        ToolsHelper toolsHelper = new ToolsHelper();
        boolean test = toolsHelper.orphanDetect(conn, "TH_1");
        System.out.println
        
        conn.close();*/
        System.out.println("");
    }
    

}
