/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark;

import fr.mom.arkeo.soap.DcElement;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.tools.FileUtilities;
import mom.trd.opentheso.ws.ark.Ark_Client;
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
public class VerifyArkIdTest {

    public VerifyArkIdTest() {
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
     * Pour détecter si l'identifiant Ark existe ou non
     * Retourne l'identifiant Ark s'il existe, sinon null
     * Test
     */
    @org.junit.Test
    public void testGetInfosArk() {
 /*       Ark_Client ark_Client = new Ark_Client();
        String idArk = ark_Client.getInfosArkId("66666/pcrtY349MZULU0");
        System.out.println(idArk);
   */ }

    
    
    
    
    
    /**
     * pour créer un identifiant Ark, si l'URL n'existe pas, l'identifiant Ark
     * est créé, si l'URL existe déjà, c'est l'identifiant Ark existant qui est
     * retourné
     * 
     * La création du Ark est accompagnée de la création des MétaDatas DC et le reste.
     * 
     * Par contre, si l'indentifiant Ark existe, aucun changement DC n'est effectué
     *
     * Test
     */
    @org.junit.Test
    public void testGetCodeArk() {

  /*      String idArk;

        // String date, String url, String title, String creator, String description, String type
        ArrayList<DcElement> dc = new ArrayList<>();

        DcElement dcElement = new DcElement();
        dcElement.setName("description");
        dcElement.setValue("la description_modifiés3");
        dcElement.setLanguage("fr");
        dc.add(dcElement);

        DcElement dcElement2 = new DcElement();
        dcElement2.setName("title");
        dcElement2.setValue("le titre_modifiés3");
        dcElement2.setLanguage("fr");
        dc.add(dcElement2);

        DcElement dcElement3 = new DcElement();
        dcElement3.setName("title");
        dcElement3.setValue("le titre_anglais_modifiés3");
        dcElement3.setLanguage("en");

        dc.add(dcElement3);

        DcElement dcElement4 = new DcElement();
        dcElement4.setName("identifier");
        dcElement4.setValue("222222LLL");
        dcElement4.setLanguage("fr");

        dc.add(dcElement4);

        Ark_Client ark_Client = new Ark_Client();

        idArk = ark_Client.getArkId(
                new FileUtilities().getDate(),
                "http://test.frantiq.fr/" + "?idc=" + "222222LLL" + "&idt=TH_1",
                "art#13370_modifiés2",
                "Frantiq",
                dc,//new ArrayList<DcElement>(),//"Pactols de Frantiq; Concept:art; id:13370"),
                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection

        System.out.println("ark.result=" + idArk);
  */  }
    
    
    
    
    
    /**
     * Si l'URL n'existe pas, retourn un null
     * permet de mettre à jour uniquement les Métas-données DC
     * 
     * Test permet de modifier les Metas données DC.
     */
    @org.junit.Test
    public void testUpdateMetaArk() {    
    
    /*    HikariDataSource conn = openConnexionPool();
*/

        Ark_Client ark_Client = new Ark_Client();
        ArrayList<DcElement> dcElementsList = new ArrayList<>();


        DcElement dcElement1 = new DcElement();
            // cette fonction permet de remplir la table Permutée
        dcElement1.setName("title");
        dcElement1.setValue("test_valeur1 en francais");
        dcElement1.setLanguage("fr");

        dcElementsList.add(dcElement1);
        
        DcElement dcElement2 = new DcElement();
        dcElement2.setName("title");
        dcElement2.setValue("test_valeur2 en anglais");
        dcElement2.setLanguage("en");
        
        dcElementsList.add(dcElement2);
        
        DcElement dcElement3 = new DcElement();
        dcElement3.setName("description");
        dcElement3.setValue("description en francais");
        dcElement3.setLanguage("fr");
        
        dcElementsList.add(dcElement3);        
            
        // String date, String url, String title, String creator, String description, String type
        String idArk = ark_Client.updateArkId(
                new FileUtilities().getDate(), "http://test.frantiq.fr/?idc=222222LLL&idt=TH_1",
             //   "http://patatos.frantiq.fr/" + "?idt=" + "id0000" + "&idt=" + "TH_35",
                "222222LLL",
                "Frantiq2",
                dcElementsList,
                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection

//        conn.close();*/    
        
       System.out.println("ark.result=" +idArk);
    }    

}
