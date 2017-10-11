/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
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
public class getHandleTest {
    
    public getHandleTest() {
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

    
    /*********************************
     *********************************
     * 
     * Test OK 
     * 
     *********************************
     *********************************
    */
    
    /**
     * Fonction pour récupérer l'identifiant Handle non sécurisé
     * 
     * OKKKKKK pour la récupération des identifiants 
     * 
     */
    @Test 
    public void curlGetHandle() {
                    
       String output ="";
        String xmlRecord = "";
        try {
         //   String query = "20.500.11942/TEST";
         //   String requete = "http://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/TEST";
            String requete = "http://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/opentheso3_1";   
            URL myURL = new URL(requete);// + URLEncoder.encode(query, "utf8"));
            HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
            myURLConnection.setRequestMethod("GET");
    //        myURLConnection.setRequestProperty("X-Parse-Application-Id", "");
    //        myURLConnection.setRequestProperty("X-Parse-REST-API-Key", "");
            myURLConnection.setRequestProperty("Content-Type", "application/json");
            myURLConnection.setUseCaches(false);
            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);
            myURLConnection.connect();
            
    //        JSONObject jsonParam = new JSONObject();
    //        jsonParam.put("score", "73453");
            
            OutputStream os = myURLConnection.getOutputStream();
   //        os.write(URLEncoder.encode(jsonParam.toString(),"UTF-8").getBytes());    
            
            BufferedReader br = new BufferedReader(new InputStreamReader((myURLConnection.getInputStream())));
            

            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));
            
            os.close();
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        System.out.println(xmlRecord);
    } 
    
    
    
    
    
    
    
}
