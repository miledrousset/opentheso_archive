/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mom.trd.opentheso.bdd.helper;

import fr.mom.arkeo.soap.Account;
import fr.mom.arkeo.soap.Ark;
import fr.mom.arkeo.soap.ArkManager;
import fr.mom.arkeo.soap.ArkManagerService;
import fr.mom.arkeo.soap.DcElement;
import fr.mom.arkeo.soap.Login;
import fr.mom.arkeo.soap.LoginService;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.namespace.QName;
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
public class ArkeoJUnitTest {
    
    public ArkeoJUnitTest() {
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
    
        /**
     * Test of Facet methods.
     */
    @org.junit.Test
    public void testGetCodeArk() {
    
        
        String idArk = "";

     // String date, String url, String title, String creator, String description, String type
        ArrayList<DcElement> dc = new ArrayList<>();
        
        DcElement dcElement = new DcElement();
        dcElement.setName("description");
        dcElement.setValue("la description_modifiés2");
        dcElement.setLanguage("fr");
        dc.add(dcElement);
        
        DcElement dcElement2 = new DcElement();
        dcElement2.setName("title");
        dcElement2.setValue("le titre_modifiés2");
        dcElement2.setLanguage("fr");
        dc.add(dcElement2);
        
        DcElement dcElement3 = new DcElement();
        dcElement3.setName("title");
        dcElement3.setValue("le titre_anglais_modifiés2");
        dcElement3.setLanguage("en");
        
        dc.add(dcElement3);
        
        DcElement dcElement4 = new DcElement();
        dcElement4.setName("identifier");
        dcElement4.setValue("222222");
        dcElement4.setLanguage("fr");
        
        dc.add(dcElement4);
        
        Ark_Client ark_Client = new Ark_Client();
                        idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                "http://test.frantiq.fr/" + "?idc=" + "222222" + "&idt=TH_1",
                                "art#13370_modifiés2",
                                "Frantiq",
                                dc,//new ArrayList<DcElement>(),//"Pactols de Frantiq; Concept:art; id:13370"),
                                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
       
        System.out.println("ark.result=" + idArk); 
        
/*        String idArk = ark_Client.getArkId(
                            new FileUtilities().getDate(),
                            "http://localhost:8081/OpenTheso/?idc=C_241588&idt=TH_34",
                            "","", "");
         System.out.println("ark.result=" + idArk);
 */       
       
//     	Account account = login("76609","bruno.morandiere","xxxx");
/*     	Account account = login("66666","trd","trd123");

        System.out.println("authentification.result=" + account.getUser().getFirstname()+ " "+ account.getUser().getLastname());       
        Ark inputArk = new Ark();
        String currentDate = new FileUtilities().getDate(); 
        inputArk.setDate(currentDate);
      //  inputArk.setUrlTarget("http://opentheso3.mom.fr");
        inputArk.setUrlTarget("http://localhost:8081/OpenTheso/?idc=C_241588&idt=TH_34");
        inputArk.setTitle("test_miled");
       
        inputArk.setCreator("Miled");
  
        inputArk.setDescription("test_opentheso");
        Ark returnedArk = createArk(account,inputArk);
        
        
        
        System.out.println("ark.result=" +returnedArk.getArk());*/
  //  }
    }
    
        
        /**
     * Test of Facet methods.
     */
    @org.junit.Test
    public void testAddCodeArk() {    
    
    /*    HikariDataSource conn = openConnexionPool();
*/
        Ark_Client ark_Client = new Ark_Client();

        ArrayList<DcElement> dcElementsList = new ArrayList<>();


        DcElement dcElement1 = new DcElement();
            // cette fonction permet de remplir la table Permutée
        dcElement1.setName("title");
        dcElement1.setValue("test_valeur1 en francais_modifiés");
        dcElement1.setLanguage("fr");

        dcElementsList.add(dcElement1);
        
        DcElement dcElement2 = new DcElement();
        dcElement2.setName("title");
        dcElement2.setValue("test_valeur2 en anglais modifiés");
        dcElement2.setLanguage("en");
        
        dcElementsList.add(dcElement2);
        
        DcElement dcElement3 = new DcElement();
        dcElement3.setName("description");
        dcElement3.setValue("description en francais modifiés");
        dcElement3.setLanguage("fr");
        
        dcElementsList.add(dcElement3);        
            
        // String date, String url, String title, String creator, String description, String type
        String idArk = ark_Client.getArkId(
                new FileUtilities().getDate(),
                "http://pactols.frantiq.fr/" + "?idc=" + "334545334340ezaeeza0" + "&idt=" + "TH_35",
                "3345453343eeeeza0_modifiés",
                "Frantiq",
                dcElementsList,
                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection

//        conn.close();*/    
    }
   
       /**
     * Test of Facet methods.
     */
    @org.junit.Test
    public void testUpdateArk() {    
    
    /*    HikariDataSource conn = openConnexionPool();
*/

        Ark_Client ark_Client = new Ark_Client();
        ArrayList<DcElement> dcElementsList = new ArrayList<>();


        DcElement dcElement1 = new DcElement();
            // cette fonction permet de remplir la table Permutée
        dcElement1.setName("title");
        dcElement1.setValue("test_valeur1 en francais_modifiés");
        dcElement1.setLanguage("fr");

        dcElementsList.add(dcElement1);
        
        DcElement dcElement2 = new DcElement();
        dcElement2.setName("title");
        dcElement2.setValue("test_valeur2 en anglais modifiés");
        dcElement2.setLanguage("en");
        
        dcElementsList.add(dcElement2);
        
        DcElement dcElement3 = new DcElement();
        dcElement3.setName("description");
        dcElement3.setValue("description en francais modifiés");
        dcElement3.setLanguage("fr");
        
        dcElementsList.add(dcElement3);        
            
        // String date, String url, String title, String creator, String description, String type
        String idArk = ark_Client.updateArkId(
                new FileUtilities().getDate(),
                "http://patatos.frantiq.fr/" + "?idc=" + "id0000" + "&idt=" + "TH_35",
                "3345453343eeeeza0_modifiés",
                "Frantiq",
                dcElementsList,
                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection

//        conn.close();*/    
        
        System.out.println("ark.result=" +idArk);
    }
    
}
