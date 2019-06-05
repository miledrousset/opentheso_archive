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
import java.net.MalformedURLException;
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
public class ____getHandleTest {
    
    public ____getHandleTest() {
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
         //   String requete = "http://cchum-isi-handle01.in2p3.fr:8001/api/handlesc/20.500.11942/TEST";
         
         
         
            URL myURL = new URL("http://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/11.6538");// + URLEncoder.encode(query, "utf8"));
            HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
    //        conn.connect();
            int status = conn.getResponseCode();
            if(status == 200) {
                // handle existe déjà 
            }
            if(status == 404) {
                // handle n'existe pas
            }
            if(status == 400) {
                // requête non valide
            }            
            conn.disconnect();
            
    //        JSONObject jsonParam = new JSONObject();
    //        jsonParam.put("score", "73453");
   /*         
            OutputStream os = conn.getOutputStream();
   //        os.write(URLEncoder.encode(jsonParam.toString(),"UTF-8").getBytes());    
            
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            
            
           
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));
            
            os.close();*/
            String test = "";
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        System.out.println(xmlRecord);
    } 
    
    
    
    
    
    
    
}
