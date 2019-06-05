/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignement;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author miled.rousset
 */
public class AddAlignmentToOriginalThesoTest {
    
    public AddAlignmentToOriginalThesoTest() {
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
    /**
     * Permet d'ajouter les alignements vers le thésaurus original
     * C'est le cas d'un thésaurus importé avec ses identifiants d'origine,
     * il est alors possible d'insérer un alignement à tous les concepts vers le thésaurus de départ
     * 
     */
    @Test
    public void addAlignmentToOrigin() {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        
        String idTheso = "40";
        String uri = "http://vocab.getty.edu/page/aat/";
    //    String idConcept = "300391251";
        
        ConceptHelper conceptHelper = new ConceptHelper();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        ArrayList<String> allIds = conceptHelper.getAllIdConceptOfThesaurus(conn, idTheso);
        for (String idConcept : allIds) {
            alignmentHelper.addNewAlignment(conn, 1, "", "Getty-AAT", uri + idConcept, 1, idConcept, idTheso, 0);
        }
        
        conn.close();
    }

}
