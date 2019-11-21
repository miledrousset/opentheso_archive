/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignement;

import com.bordercloud.sparql.Endpoint;
import com.bordercloud.sparql.EndpointException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import com.bordercloud.sparql.Endpoint;
//import com.bordercloud.sparql.EndpointException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;
import mom.trd.opentheso.core.alignment.helper.CurlHelper;
import mom.trd.opentheso.core.json.helper.JsonHelper;

/**
 *
 * @author miled.rousset
 */
public class WikidataTest {

    public WikidataTest() {
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
    // @Test
    public void searchValue() {
        try {
            Endpoint sp = new Endpoint("https://query.wikidata.org/sparql", false);

            String querySelect = "SELECT ?item ?itemLabel ?itemDescription WHERE {\n"
                    + "  ?item rdfs:label \"fibula\"@fr.\n"
                    + "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],fr\". }\n"
                    + "}";

            HashMap rs = sp.query(querySelect);

            HashMap<String, HashMap> rs3_queryPopulationInFrance = sp.query(querySelect);

            ArrayList<HashMap<String, Object>> rows_queryPopulationInFrance = (ArrayList) rs3_queryPopulationInFrance.get("result").get("rows");
            for (HashMap<String, Object> hashMap : rows_queryPopulationInFrance) {
                System.out.println("URI : " + hashMap.get("item"));
                System.out.println("URI : " + hashMap.get("itemLabel"));
                System.out.println("URI : " + hashMap.get("itemDescription"));
            }

            //    printResult(rs, 30);
        } catch (EndpointException eex) {
            System.out.println(eex);
            eex.printStackTrace();
        }
    }

    @Test
    public void getTraductionWikidata() {
        String uri = "https://www.wikidata.org/entity/Q7748";
        String datas = getJsonFromURL(uri);

        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(datas);
        jsonObject.getString("");
    }

    @Test
    public void getDatasCurlJson() {
        CurlHelper curlHelper = new CurlHelper();
        curlHelper.setHeader1("Accept");
        curlHelper.setHeader2("application/json");

        String uri = "https://www.wikidata.org/entity/Q178401";//"https://www.wikidata.org/entity/Q178401";//Q7748";Q324926
        String datas = curlHelper.getDatasFromUriHttps(uri);
        String entity = uri.substring(uri.lastIndexOf("/") + 1);

        getTraductions(datas, entity); // OK pour les traductions
        getImages(datas, entity); // OK pour les images

        // les descriptions
        getDescriptions(datas, entity); // OK pour les descriptions

        // les aligenements
    }

    private void getDescriptions(String jsonDatas, String entity) {
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1;
        JsonValue jsonValue;

        try {
            jsonObject1 = jsonObject.getJsonObject("entities").getJsonObject(entity).getJsonObject("descriptions");
        } catch (Exception e) {
            System.err.println(e.toString());
            return;
        }
        try {
            jsonValue = jsonObject1.getJsonObject("en").get("language");
            System.out.println(jsonValue.toString().replace("\"", ""));
            jsonValue = jsonObject1.getJsonObject("en").get("value");
            System.out.println(jsonValue.toString().replace("\"", ""));
        } catch (Exception e) {
        }

        try {
            jsonValue = jsonObject1.getJsonObject("fr").get("language");
            System.out.println(jsonValue.toString().replace("\"", ""));
            jsonValue = jsonObject1.getJsonObject("fr").get("value");
            System.out.println(jsonValue.toString().replace("\"", ""));
        } catch (Exception e) {
        }

        try {
            jsonValue = jsonObject1.getJsonObject("ar").get("language");
            System.out.println(jsonValue.toString().replace("\"", ""));
            jsonValue = jsonObject1.getJsonObject("ar").get("value");
            System.out.println(jsonValue.toString().replace("\"", ""));
        } catch (Exception e) {
        }
    }

    private void getImages(String jsonDatas, String entity) {

        // pour construire l'URL de Wikimedia, il faut ajouter 
        // http://commons.wikimedia.org/wiki/Special:FilePath/
        // puis le nom de l'image
        StringBuffer url = new StringBuffer();
        url.append("https://commons.wikimedia.org/wiki/Special:FilePath/");
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1 = null;

        JsonObject jsonObject2 = null;
        JsonValue jsonValue;

        try {
            jsonObject1 = jsonObject.getJsonObject("entities").getJsonObject(entity).getJsonObject("claims");//.getJsonObject("P18");
        } catch (Exception e) {
            System.err.println(e.toString());
            return;
        }

        try {
            //jsonValue =   jsonObject1.getJsonObject("P18");
            //System.out.println(jsonValue);            
            JsonArray jsonArray = jsonObject1.getJsonArray("P18");

            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject2 = jsonArray.getJsonObject(0);

                jsonValue = jsonObject2.getJsonObject("mainsnak").getJsonObject("datavalue").get("value");
                url.append(jsonValue.toString().replace("\"", ""));
                System.out.println(url.toString());
            }

        } catch (Exception e) {
        }

    }

    private void getTraductions(String jsonDatas, String entity) {
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1 = null;
        JsonValue jsonValue;

        try {
            jsonObject1 = jsonObject.getJsonObject("entities").getJsonObject(entity).getJsonObject("labels");
        } catch (Exception e) {
            System.err.println(e.toString());
            return;
        }
        try {
            jsonValue = jsonObject1.getJsonObject("en").get("language");
            System.out.println(jsonValue.toString().replace("\"", ""));
            jsonValue = jsonObject1.getJsonObject("en").get("value");
            System.out.println(jsonValue.toString().replace("\"", ""));
        } catch (Exception e) {
        }

        try {
            jsonValue = jsonObject1.getJsonObject("fr").get("language");
            System.out.println(jsonValue.toString().replace("\"", ""));
            jsonValue = jsonObject1.getJsonObject("fr").get("value");
            System.out.println(jsonValue.toString().replace("\"", ""));
        } catch (Exception e) {
        }

        try {
            jsonValue = jsonObject1.getJsonObject("ar").get("language");
            System.out.println(jsonValue.toString().replace("\"", ""));
            jsonValue = jsonObject1.getJsonObject("ar").get("value");
            System.out.println(jsonValue.toString().replace("\"", ""));
        } catch (Exception e) {
        }
    }

    private String getJsonFromURL(String uri) {
        String output = "";
        String xmlRecord = "";
        try {
            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            InputStream in2 = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in2, "UTF-8"));
            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(line);
            }

            int status = conn.getResponseCode();
            InputStream in = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
            // status = 200 = La lecture a réussie

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));

            System.err.println("Status de la réponse : " + status);
            System.out.println(xmlRecord);
            conn.disconnect();

        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return xmlRecord;
        /*    HandleClient handleClient = new HandleClient();
        String test = handleClient.getHandle(pass, pathKey, pathCert, urlHandle, idHandle, internalId);
        System.err.println(test);
        System.err.println(handleClient.getMessage());*/
    }

    /*    public void printResult(HashMap rs , int size) {

      for (Object variable : (ArrayList) rs.get("result").get("variables")) {
        System.out.print(String.format("%-"+size+"."+size+"s", variable ) + " | ");
      }
      System.out.print("\n");
      for (HashMap value : (ArrayList>) rs.get("result").get("rows")) {
        //System.out.print(value);
        /* for (String key : value.keySet()) {
         System.out.println(value.get(key));
         }*/
 /*        for (String variable : (ArrayList) rs.get("result").get("variables")) {
          //System.out.println(value.get(variable));
          System.out.print(String.format("%-"+size+"."+size+"s", value.get(variable)) + " | ");
        }
        System.out.print("\n");
      }
    }*/
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
