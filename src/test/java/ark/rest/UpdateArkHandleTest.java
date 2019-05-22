/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.ws.ark.ArkHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author miled.rousset
 */
public class UpdateArkHandleTest {
    
    public UpdateArkHandleTest() {
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


    /**
     * permet de récupérer les idHandle de Arkéo
     * pour les concepts qui ne l'ont pas en local dans le thésaurus 
     * mais que ces idHandle existent sur le serveur Arkéo 
     * ceci concerne les thésaurus qui étaient gérés avec Arkéo
     * avant d'intégrer la gestion des id Handle (dans Arkéo)
     */
    @Test
    public void addMissingHandle() {
        // retrieves all id of concepts that do not have an id handle
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource ds = connexionTest.getConnexionPool();
        
        String idTheso = "4";
        String idArk;
        String idHandle;
        
        PreferencesHelper preferencesHelper = new PreferencesHelper();
        NodePreference nodePreference = preferencesHelper.getThesaurusPreferences(ds, idTheso);        
        if(nodePreference == null) return;
        
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> listIds = conceptHelper.getAllIdConceptOfThesaurusWithoutHandle(ds, idTheso);
        
        ArkHelper arkHelper = new ArkHelper(nodePreference);
        if(!arkHelper.login()) {
            ds.close();
            return ;
        }
        int count = 0;
        for (String idConcept : listIds) {
        //String idConcept =  "30782"; ///'76609/crtHP3exIEqNL'      
            idArk = conceptHelper.getIdArkOfConcept(ds, idConcept, idTheso);
            if(idArk != null)
                if(!idArk.isEmpty()) {
                    if(!arkHelper.getArkFromArkeo(idArk)){
                        System.out.println("Erreur Handle pour le concept : " + idConcept);
                        ds.close();
                        return;
                    }
                    idHandle = arkHelper.getIdHandle();
                    if(idHandle != null) {
                        if(!conceptHelper.updateHandleIdOfConcept(ds, idConcept, idTheso, idHandle)){
                            System.out.println("Erreur Handle pour le concept : " + idConcept);
                            ds.close();
                            return;
                        }
                        count = count + 1;
                    }
                }
        }
        System.err.println("Total idConcepts sans Handle = " + listIds.size());        
        System.err.println("Total traité = " + count);
        ds.close();
    }
    
    
    /**
     * Permet de :
     * - Vérifier si l'identifiant Ark existe sur le serveur Arkéo
     * - S'il existe, on le met à jour pour l'URL
     * - s'il n'existe pas, on le créé
     *
     * #MR
     */
    @Test
    public void verifyAllArk() {
        // retrieves all id of concepts that do not have an id handle
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource ds = connexionTest.getConnexionPool();
        
        String idTheso = "TH_1";

        String idHandle;
        
        String idConcept;
        String idArk;        
        
        
        PreferencesHelper preferencesHelper = new PreferencesHelper();
        NodePreference nodePreference = preferencesHelper.getThesaurusPreferences(ds, idTheso);        
        if(nodePreference == null) return;
        
        ConceptHelper conceptHelper = new ConceptHelper();
        conceptHelper.setNodePreference(nodePreference);
        HashMap<String, String> idConceptsArks = conceptHelper.getAllIdArkOfThesaurusMap(ds, idTheso);
        
        int count = 0;
        conceptHelper.updateArkId(ds, idTheso, "8464", "26678/pcrtbOx8gg4AcF");
     //   26678/pcrtbOx8gg4AcF
        
    /*    for (Map.Entry<String, String> id : idConceptsArks.entrySet()) {
            idConcept = id.getKey();
            idArk = id.getValue();
            if(idArk != null)
                if(!idArk.isEmpty()) {
                    if(!conceptHelper.updateArkId(ds, idTheso, idConcept, idArk)){
                        System.out.println("Erreur Ark pour le concept : " + idConcept);
                        ds.close();
                        return;
                    }
                    System.err.println(idConcept + " : est traité");
                    count = count + 1; 
                }
        }*/
        System.err.println("Total idConcepts sans Handle = " + idConceptsArks.size());        
        System.err.println("Total traité = " + count);
        ds.close();
    }    
}
