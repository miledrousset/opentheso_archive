/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import com.bordercloud.sparql.Endpoint;
//import com.bordercloud.sparql.EndpointException;
import java.util.ArrayList;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.core.json.helper.JsonHelper;

/**
 *
 * @author miled.rousset
 */
public class AAT_test {

    public AAT_test() {
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

    // récupération des URI qui parlent du concept avec l'entité codé exp : http://www.wikidata.org/entity/Q324926 pour fibule
//    SELECT ?item ?itemLabel WHERE {
//        ?item rdfs:label "fibule"@fr.
//        SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],fr". }.
//      }
    @Test
    public void searchValueSubject() {
        String searchValue = "amphora";//"Amphores puniques";//"amphores";

        //https://www.idref.fr/Sru/Solr?wt=json&version=2.2&start=&rows=100&indent=on&fl=id,ppn_z,affcourt_z&q=subjectheading_t:amphore*%20AND%20recordtype_z:r
        
        
        //"http://vocabsservices.getty.edu/AATService.asmx/AATGetTermMatch?term=amphora&logop=and&notes="
        //String query = "https://www.idref.fr/Sru/Solr?wt=json&version=2.2&start=&rows=100&indent=on&fl=id,ppn_z,affcourt_z&q=subjectheading_t:(##value##)%20AND%20recordtype_z:r";
        String query = "http://vocabsservices.getty.edu/AATService.asmx/AATGetTermMatch?term=##value##&logop=and&notes=";
        if (query.trim().equals("")) {
            return;
        }
        if (searchValue.trim().equals("")) {
            return;
        }

        ArrayList<NodeAlignment> listeAlign = new ArrayList<>();
        // construction de la requête de type (webservices Opentheso)

        String [] splitValues = searchValue.split(" ");
        
        String value = "";
        
        for (String splitValue : splitValues) {
            if(value.isEmpty())
                value = splitValue;
            else
                value = value + " AND " + splitValue;
        }
        value = value.replaceAll(" ", "%20");

        try {
            
            value = URLEncoder.encode(value,"UTF-8");            
            query = query.replace("##value##", value);
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String xmlRecord = "";
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            conn.disconnect();
            System.out.println(xmlRecord);
          //  getValues(xmlRecord);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
//        return listeAlign;
    }
    
    private String clearName(String nom){
        nom = nom.replaceAll("\\[|\\]" , "");        
        
        nom = nom.replaceAll(";", " AND ");            
        nom = nom.replaceAll(" ", " AND ");
        nom = nom.replaceAll("_", " AND ");
        nom = nom.replaceAll("-", " AND ");

       
        return nom;
    }    
    
    @Test
    public void searchValueNames() {
        String mode = "3";
        String prenom = "Jeanne";//"émile"; //"bernard";        
        String nom = "d'Arc";//"jean AND marie";


        //https://www.idref.fr/Sru/Solr?wt=json&q=nom_t:(jean%20AND%20marie)%20AND%20prenom_t:(bernard)&fl=ppn_z,affcourt_z,prenom_s,nom_s&start=0&rows=30&version=2.2
        
        String query = "https://www.idref.fr/Sru/Solr?wt=json&q=nom_t:(##nom##)%20AND%20prenom_t:(##prenom##)%20AND%20recordtype_z:a&fl=ppn_z,affcourt_z,prenom_s,nom_s&start=0&rows=30&version=2.2";

        if (query.trim().equals("")) {
            return;
        }
        if (nom.isEmpty()) {
            query = query.replace("nom_t:(##nom##)%20AND%20", "");
        }
        if (prenom.isEmpty()) {
            query = query.replace("%20AND%20prenom_t:(##prenom##)", "");
        } 
        
        if(!nom.isEmpty()) {
            nom = clearName(nom);
        }
        if(!prenom.isEmpty()) {
            prenom = clearName(prenom);
        } 
        
        ArrayList<NodeAlignment> listeAlign = new ArrayList<>();
        // construction de la requête de type (webservices Opentheso)

   //     nom = nom.replaceAll(" ", "%20");
   //     prenom = prenom.replaceAll(" ", "%20");
    //    query = query.replace("##lang##", lang);

        try {
            nom = URLEncoder.encode(nom,"UTF-8");
            prenom = URLEncoder.encode(prenom,"UTF-8");

            query = query.replace("##nom##", nom);
            query = query.replace("##prenom##", prenom);             
            URL url = new URL(query); 
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String xmlRecord = "";
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
        //    byte[] bytes = xmlRecord.getBytes();
        //    xmlRecord = new String(bytes, Charset.forName("UTF-8"));
            conn.disconnect();
//            StringBuffer sb = new StringBuffer(xmlRecord);
            getNames(xmlRecord);
        } catch (MalformedURLException e) {
                        System.err.println(""+e.toString());
        } catch (IOException e) {
            System.err.println(""+e.toString());
        }
//        return listeAlign;
    }    
    

    private void getValues(String jsonDatas) {
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1;
        JsonArray jsonArray;
        
        String valeur = "";
        String uri = "https://www.idref.fr/";
        String idRef = "";
        
        JsonValue jsonValue;

        try {
            if(jsonObject == null) return;
            if(jsonObject.getJsonObject("response") == null) return;
            jsonArray = jsonObject.getJsonObject("response").getJsonArray("docs");
        } catch (Exception e) {
            System.err.println(e.toString());
            return;
        }
      
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject1 = jsonArray.getJsonObject(i); //jsonObject1.getValueType()
            
            valeur = ((JsonObject) jsonObject1).getString("affcourt_z");
            
            
            idRef = ((JsonObject) jsonObject1).getString("ppn_z");
            System.out.println(valeur);
            System.out.println(idRef);
            System.out.println("url = " + uri+idRef);            
        }        
    }
    
    private void getNames(String jsonDatas) {
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1;
        JsonArray jsonArray;
        
        String valeur = "";
        String uri = "https://www.idref.fr/";
        String idRef = "";
        
        JsonArray jsonArrayNames;
       
        String nom = "";
        String prenom = "";
        
        
        try {
            if(jsonObject == null) return;
            if(jsonObject.getJsonObject("response") == null) return;
            jsonArray = jsonObject.getJsonObject("response").getJsonArray("docs");
        } catch (Exception e) {
            System.err.println(e.toString());
            return;
        }
      
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject1 = jsonArray.getJsonObject(i); //jsonObject1.getValueType()
            
            valeur = ((JsonObject) jsonObject1).getString("affcourt_z");
            idRef = ((JsonObject) jsonObject1).getString("ppn_z");

            // récupération des noms et prénoms
            jsonArrayNames = jsonObject1.getJsonArray("nom_s");
            if(jsonArrayNames != null) {
                for (int j = 0; j < jsonArrayNames.size(); j++) {
                    if(j == 0)
                        nom = jsonArrayNames.getString(j);
                    else
                        nom = nom + "; " + jsonArrayNames.getString(j);
                }
            }

            jsonArrayNames = jsonObject1.getJsonArray("prenom_s");
            if(jsonArrayNames != null) {
                for (int j = 0; j < jsonArrayNames.size(); j++) {
                    if(j == 0)
                        prenom = jsonArrayNames.getString(j);
                    else
                        prenom = prenom+ "; " + jsonArrayNames.getString(j);
                }
            }
            System.out.println(valeur);
            System.out.println(idRef);
            System.out.println("url = " + uri+idRef); 
            
            System.out.println("Noms= " + nom); 
            System.out.println("Prenoms= " + prenom);             
        }        
    }     
   
}
