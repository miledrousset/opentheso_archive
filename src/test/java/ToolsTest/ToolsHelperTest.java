/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolsTest;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.ToolsHelper;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
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
public class ToolsHelperTest {
    
    public ToolsHelperTest() {
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
    @Test
    public void getNewId() {
        ToolsHelper toolsHelper = new ToolsHelper();
        String id = toolsHelper.getNewId(15);
        System.out.println(id);
    }
    
    @Test
    public void getConcept(){
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource ds = connexionTest.getConnexionPool();
        ConceptHelper conceptHelper = new ConceptHelper();
        NodeConcept nodeConcept =  conceptHelper.getConcept(ds, "135", "1", "fr");
        ds.close();
        
    }
    
    @Test
    public void getConceptsWithoutBT(){
        
    }
}
