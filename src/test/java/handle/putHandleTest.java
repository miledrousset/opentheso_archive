/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import mom.trd.test.opentheso.HandleTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 *
 * @author miled.rousset
 */
public class putHandleTest {
    
    public putHandleTest() {
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
     * Test en cours ne marche pas
     * 
     *********************************
     *********************************
    */
 
    
    
    /**
     * Fonction pour créer un nouvel identifiant
     * 
     * 
     */
    @Test 
    public void curlPutHandle() {
                    
       String output = "";
        String xmlRecord = "";

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        try {
            URL url = new URL("https://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/opentheso4.3.2");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // -X PUT
            conn.setRequestMethod("PUT");

            // -H "Content-Type: application/json"
            conn.setRequestProperty("Content-Type", "application/json");

            //-H 'Authorization: Handle clientCert="true"'
            // --cert ./cert.pem --key ./privatekey.pem 
            conn.setRequestProperty("Authorization", "Handle " + "clientCert=\"true\"");

            /*     HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {


            httpsConnection.setSSLSocketFactory(sc.getSocketFactory());
            httpsConnection.setHostnameVerifier(allHostsValid);
             */
            conn.setDoOutput(true);

            // -d '{"index":1,"type":"URL",
            // "data":{"format":"string","value":"http://www.huma-num.fr"},"ttl":86400,"permissions":"1110"}'
            String data = "{\"index\":1,\"type\":\"URL\","
                    + "\"data\":{\"format\":\"string\",\"value\":\"http://opentheso.mom.fr\"},\"ttl\":86400,\"permissions\":\"1110\"}";

            OutputStream os = conn.getOutputStream();
            
            OutputStreamWriter out = new OutputStreamWriter(os);
            out.write(data);


            System.out.println(out.toString());//Files.copy(Paths.get("/home/myNewFile.txt"), out));
            
            InputStream in = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in); 
            
            out.close();

            in.close();
            
//            os.write(URLEncoder.encode(jsonParam.toString(),"UTF-8").getBytes());    

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));

            System.out.println(xmlRecord);
            os.close();

            /*     try (OutputStream out = conn.getOutputStream()) {
                System.out.println(out.toString());//Files.copy(Paths.get("/home/myNewFile.txt"), out));
            }*/
        } catch (MalformedURLException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(xmlRecord);
    } 
    
    
    public boolean verify(String urlHostName, SSLSession session) {
        System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
        return true;
    }
    
    
    
    
}
