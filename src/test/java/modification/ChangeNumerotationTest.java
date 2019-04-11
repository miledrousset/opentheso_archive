/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modification;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
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
public class ChangeNumerotationTest {
    
    public ChangeNumerotationTest() {
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


    @Test
    public void AlphaNumericToNumeric() {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource ds = connexionTest.getConnexionPool();
        
        String idTheso = "TH_1";
        
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> listId = conceptHelper.getAllNonNumericId(ds, idTheso);
    }
}
