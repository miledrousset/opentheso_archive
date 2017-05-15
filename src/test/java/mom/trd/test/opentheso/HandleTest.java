/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.json.JSONObject;

/**
 *
 * @author miled.rousset
 */
public class HandleTest {

    public HandleTest() {
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
            String requete = "http://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/TEST";
            
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * PUT mais ne marche pas encore.
     */
    

    @Test
    /*
        > curl -k -X PUT -H "Content-Type: application/json" -H 'Authorization: Handle clientCert="true"'
        --cert ./cert.pem --key ./privatekey.pem -d '{"index":1,"type":"URL",
        "data":{"format":"string","value":"http://www.huma-num.fr"},"ttl":86400,"permissions":"1110"}'
        "https://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/TEST"    
     */
    public void curtToJava() {
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
            URL url = new URL("https://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/opentheso3_1");
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
                    + "\"data\":{\"format\":\"string\",\"value\":\"http://www.opentheso3.mom.fr\"},\"ttl\":86400,\"permissions\":\"1110\"}";

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
    }
    
    
    
    
    
    
    
    
    
    
    

    public boolean verify(String urlHostName, SSLSession session) {
        System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
        return true;
    }

    /* sample
    curl -X GET \
  -H "X-Parse-Application-Id: 1234" \
  -H "X-Parse-REST-API-Key: abdhchc" \
  -G \
  --data-urlencode 'where={"playerName":"Sean Plott","cheatMode":false}' \
  https://api.parse.com/1/classes/GameScore
     */
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
/*    @Test
    public void getHandle() {
        try {
            String query = "20.500.11942/TEST";
//"{\"playerName\":\"Sean Plott\",\"cheatMode\":false}";
            URL url = new URL("https://cchum-isi-handle01.in2p3.fr:8001/api/handles/" + URLEncoder.encode(query, "utf8"));
//"https://api.parse.com/1/classes/GameScore?where=" + URLEncoder.encode(query, "utf8"));
            
            
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Parse-Application-Id","*****");
            conn.setRequestProperty("X-Parse-REST-API-Key","****");
            
          //  String input = "{\"nom\":\""+nom+"\",\"val1\":\""+val1+"\",\"val2\":\""+val2+"\",\"val3\":\""+val3+"\"}";
            
            System.out.println(input);
            
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            
            
            
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        } catch (IOException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }*/

    /**
     * Fonction pour récupérer l'identifiant Handle ( sécurisé mais ne marche
     * pas encore )
     */

    /*
    @Test
    public void curlGetHandle() {

        String output = "";
        String xmlRecord = "";
        try {
            //   String query = "20.500.11942/TEST";
            String requete = "https://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/TEST";

            URL myURL = new URL(requete);// + URLEncoder.encode(query, "utf8"));
            HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
            myURLConnection.setRequestMethod("GET");
            myURLConnection.setRequestProperty("X-Parse-Application-Id", getCertificate());
            myURLConnection.setRequestProperty("X-Parse-REST-API-Key", getKey());
            myURLConnection.setRequestProperty("Content-Type", "application/json");

            //-H 'Authorization: Handle clientCert="true"'
            // --cert ./cert.pem --key ./privatekey.pem
            myURLConnection.setRequestProperty("Authorization", "Handle " + "clientCert=\"true\"");

//            Map map = new HashMap();
//            map.
            // -d '{"index":1,"type":"URL",
            // "data":{"format":"string","value":"http://www.huma-num.fr"},"ttl":86400,"permissions":"1110"}'            
            JSONObject jsonParam = new JSONObject();
            jsonParam.append("index", "1");
            jsonParam.append("type", "URL");

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
    }*/
    /**
     * Fonction pour lire le fichier du certificat
     */
    private String getCertificate2() {

        byte[] key = null;
        try {
            File f = new File("cert.pem");
            FileInputStream fs = new FileInputStream(f);
            key = new byte[(int) f.length()];
            int n = 0;
            while (n < key.length) {
                key[(n++)] = ((byte) fs.read());
            }
            fs.read(key);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return key.toString();
    }

    /**
     * Fonction pour lire le fichier du certificat
     */
    private String getCertificate() {
        BufferedReader br;

        StringBuilder cert = new StringBuilder();
        StringBuilder key = new StringBuilder();

        String line = "";
        try {
            br = new BufferedReader(new FileReader("cert.pem"));

            line = br.readLine();
            while (line != null) {
                cert.append(line);
                //   cert.append(System.lineSeparator());
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cert.toString();
    }

    /**
     * Fonction pour lire le fichier Key
     */
    private String getKey() {
        BufferedReader br;

        StringBuilder key = new StringBuilder();

        String line = "";
        try {
            br = new BufferedReader(new FileReader("privatekey.pem"));

            line = br.readLine();
            while (line != null) {
                key.append(line);
                //     key.append(System.lineSeparator());
                line = br.readLine();
            }
            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HandleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return key.toString();
    }

    /*
        try {

            String url = "https://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/TEST_java";

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            conn.setRequestMethod("PUT");

            String userpass = "user" + ":" + "pass";
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
            conn.setRequestProperty ("Authorization", basicAuth);

            String data =  "{\"format\":\"json\",\"pattern\":\"#\"}";
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.close();

            new InputStreamReader(conn.getInputStream());   

            } catch (Exception e) {
            e.printStackTrace();
        }        
        
     */
    // String query = "{\"playerName\":\"Sean Plott\",\"cheatMode\":false}";
//		URL url = new URL("https://api.parse.com/1/classes/GameScore?where=" + URLEncoder.encode(query, "utf8"));

    /*   
        String output ="";
        String xmlRecord = "";
        try {
         //   String query = "20.500.11942/TEST";
            String requete = "https://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/TEST";
            
            URL myURL = new URL(requete);// + URLEncoder.encode(query, "utf8"));
            HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
            myURLConnection.setRequestMethod("PUT");
            myURLConnection.setRequestProperty("X-Parse-Application-Id", cert.toString());
            myURLConnection.setRequestProperty("X-Parse-REST-API-Key", key.toString());
            myURLConnection.setRequestProperty("Content-Type", "application/json");
            myURLConnection.setUseCaches(false);
            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);
            myURLConnection.connect();
            
    //        JSONObject jsonParam = new JSONObject();
    //        jsonParam.put("score", "73453");
            
            OutputStream os = myURLConnection.getOutputStream();
   //        os.write(URLEncoder.encode(jsonParam.toString(),"UTF-8").getBytes());    
            
            br = new BufferedReader(new InputStreamReader((myURLConnection.getInputStream())));
            

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
    }    */
 /*   @Test
    public void queryOpentheso() {
        

        // construction de la requête de type (webservices Opentheso)
        
/*        lexicalValue = lexicalValue.replaceAll(" ", "%20");
        requete = requete.replace("##lang##", lang);
        requete = requete.replace("##value##", lexicalValue);
     */
 /*       String query = "20.500.11942/TEST";
        String requete = "https://cchum-isi-handle01.in2p3.fr:8001/api/handles/";

        try {
            URL url = new URL(requete +URLEncoder.encode(query, "utf8"));
           
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
OutputStream os = conn.getOutputStream();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String xmlRecord = "";
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));
            conn.disconnect();
            StringBuffer sb = new StringBuffer(xmlRecord);
          
            /*
            try {
                SKOSXmlDocument sxd = new ReadFileSKOS().readStringBuffer(sb);
                for (SKOSResource resource : sxd.getResourcesList()) {
                    NodeAlignment na = new NodeAlignment();
                    na.setInternal_id_concept(idC);
                    na.setInternal_id_thesaurus(idTheso);
                    na.setThesaurus_target("Pactols");
                    na.setUri_target(resource.getUri());
                    for(SKOSLabel label : resource.getLabelsList()) {
                        switch (label.getProperty()) {
                            case SKOSProperty.prefLabel:
                                if(label.getLanguage().equals(lang)) {
                                    na.setConcept_target(label.getLabel());
                                }
                                break;
                            case SKOSProperty.altLabel:
                                if(label.getLanguage().equals(lang)) {
                                    if(na.getConcept_target_alt().isEmpty()) {
                                        na.setConcept_target_alt(label.getLabel());
                                    } 
                                    else {
                                        na.setConcept_target_alt(
                                                na.getConcept_target_alt() + ";" + label.getLabel());                                        
                                    }
                                }
                                break;                                
                            default:
                                break;
                        }
                    }

                    for(SKOSDocumentation sd : resource.getDocumentationsList()) {
                        if(sd.getProperty() == SKOSProperty.definition && sd.getLanguage().equals(lang)) {
                            na.setDef_target(sd.getText());
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(AlignmentQuery.class.getName()).log(Level.SEVERE, null, ex);
            }*/
 /*
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }*/
}
