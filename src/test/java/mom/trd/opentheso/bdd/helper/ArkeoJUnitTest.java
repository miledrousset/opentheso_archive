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
        
 /*       Ark_Client ark_Client = new Ark_Client();
                        idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                "http://pactols.frantiq.fr/" + "?idc=" + "13370" + "&idt=TH_1",
                                "art#13370",
                                "Frantiq",
                                new ArrayList<DcElement>(),//"Pactols de Frantiq; Concept:art; id:13370"),
                                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
       
        System.out.println("ark.result=" + idArk); */
        
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
   
    
}
