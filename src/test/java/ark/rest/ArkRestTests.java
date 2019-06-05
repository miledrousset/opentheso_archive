/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Properties;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import mom.trd.opentheso.ws.ark.ArkClientRest;
import org.junit.Test;
import org.primefaces.json.JSONObject;

/**
 *
 * @author formation
 */
public class ArkRestTests {

    Client client;
//    String serverHost = "http://193.48.137.86:8084/Arkeo";
    String serverArk = "https://ark.mom.fr/ark:/";
    
    
    // handle
    String serverHandle = "https://hdl.handle.net/";
    String prefixHandle = "20.500.11859";
            
            
            
    String serverHost = "https://ark.mom.fr/Arkeo";
    String user = "demo";
    String password = "demo2";
    String naan = "66666";
    
//    String restPath = "/rest";

    public ArkRestTests() {

//        Properties prop = new Properties();
//        InputStream input = null;
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//        input = Thread.currentThread().getContextClassLoader()
//                .getResourceAsStream("com/properties/Appserver.properties");
//
//        try {
//            prop.load(input);
//        } catch (IOException ex) {
//            Logger.getLogger(ArkRestTests.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        restPath = prop.getProperty("restpath");
//        serverHost = prop.getProperty("serverHost");

        client = Client.create();

    }



     /**
     * test 
     * OK validé par #MR
     */
    @Test
    public void deleteHandle2(){
        String loginResp = login();
        if(loginResp == null) return;
        JSONObject loginrespasjson = new JSONObject(loginResp);
    //    String token = loginrespasjson.getString("token");
    //    String content = loginrespasjson.getString("content");

    //    System.out.println(loginrespasjson);

        String handle = "66666.crt14DKt4GYI7"; 
        //arkString = "[{\"ark\":\"66666/srvcLVuSQ7jzB\",\"naan\":\"66666\",\"handle\":\"srvcLVuSQ7jzB\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso22.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699477734,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:24:37 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv6MvT5bfl4M\",\"naan\":\"66666\",\"handle\":\"/srv6MvT5bfl4M\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso21.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699345568,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:22:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2nvx2J8b6F\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srv2nvx2J8b6F\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso20.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525698729011,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:12:09 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srveFZUqQ1t6x\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srveFZUqQ1t6x\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso18.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525694433467,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 14:00:33 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvNZwNRozRzb\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvNZwNRozRzb\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open135.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247245243,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:47:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvoeBANJXH7l\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvoeBANJXH7l\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open13.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247174077,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:46:14 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvqIeJcfIi2T\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvqIeJcfIi2T\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open12.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662108907,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:15:08 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvcf7poTdO1X\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvcf7poTdO1X\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open11.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662096228,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:14:56 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv8VdRAHfWDI\",\"naan\":\"66666\",\"handle\":\"srv8VdRAHfWDI\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open10.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661666866,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:46 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2pf0fHK0xk\",\"naan\":\"66666\",\"handle\":\"srv2pf0fHK0xk\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://openlol9.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661651246,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:31 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"}]";
        String arkString = "{\"urlTarget\":\"http://miled.mom.fr/1\","
                + "\"title\":null,"
                + "\"creator\":null,"
                + "\"handle_prefix\":\"20.500.11859\","
                + "\"handle\":\""+ handle +"\","
                + "\"handle_stored\":false,"
                + "\"date\":null,"
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\"66666/abcdefghijkl\","
                + "\"name\":null,"
                + "\"qualifier\":null,"
                + "\"modificationDate\":\"2016-11-11\","
                + "\"saved\":false,"
                + "\"naan\":\"66666\","
                + "\"redirect\":true,"
                + "\"dcElements\":[],"
                + "\"userArkId\":1,"
                + "\"owner\":null,\"qualifiers\":[],"
                + "\"format\":\"\","
                + "\"identifier\":\"\","
                + "\"description\":\"\","
                + "\"source\":\"google.com\","
                + "\"subject\":\"\","
                + "\"rights\":\"\","
                + "\"publisher\":\"Mouad\","
                + "\"relation\":\"\","
                + "\"coverage\":\"\","
                + "\"contributor\":\"\"}";
        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource(serverHost + "/rest/ark/deletehandle");

        ClientResponse response = webResource.type("application/json")
                .put(ClientResponse.class, newinput);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        System.out.println(output);
    } 
    

    /**
     * OK validé #MR
     */
    @Test
    public void testlogin() {
        String loginValue = login();
        if(loginValue == null) 
            System.err.println("Erreur de connexion");
        else
            System.out.println(loginValue);
    }    
    
    /**
     * OK validé #MR
     */    
    @Test
    public void testGetArk() {

    String idArk = "crt0eTJm32hkG";
    WebResource webResource = client.resource(serverHost + 
                        "/rest/ark/naan=" + 
                        naan + 
                        "&id=" +
                        idArk);
        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        String output = response.getEntity(String.class);
        System.out.println(output);
    }    
    
    
    /**
     * OK validé #MR
     */       
    @Test
    public void testGetHandle() {
        String idHandle = "66666.crtW58Rkc4Fhm";//IGLS_8-1_0237";
        WebResource webResource = client
                .resource(serverHost +
                        "/rest/ark/handle=" +
                        idHandle);

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        String output = response.getEntity(String.class);
        System.out.println(output);
    }
    
    
    /**
     * OK validé #MR
     */ 
    @Test
    public void testAddSingleArkWithDefinedArk() {
        String loginResp = login();
        if(loginResp == null) return;
        
        String url = "http://iglsdev.mom.fr/IGLS/Image/IGLS_8-1_02377645fggg";
        String title = "IGLS_8-1_02377645fggg";
        String idArk = "66666/IGLS_8-1_02377645fggg";
        
        
        JSONObject loginrespasjson = new JSONObject(loginResp);
        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");

        System.out.println(loginrespasjson);

        //arkString = "[{\"ark\":\"66666/srvcLVuSQ7jzB\",\"naan\":\"66666\",\"handle\":\"srvcLVuSQ7jzB\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso22.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699477734,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:24:37 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv6MvT5bfl4M\",\"naan\":\"66666\",\"handle\":\"/srv6MvT5bfl4M\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso21.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699345568,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:22:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2nvx2J8b6F\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srv2nvx2J8b6F\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso20.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525698729011,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:12:09 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srveFZUqQ1t6x\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srveFZUqQ1t6x\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso18.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525694433467,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 14:00:33 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvNZwNRozRzb\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvNZwNRozRzb\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open135.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247245243,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:47:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvoeBANJXH7l\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvoeBANJXH7l\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open13.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247174077,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:46:14 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvqIeJcfIi2T\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvqIeJcfIi2T\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open12.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662108907,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:15:08 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvcf7poTdO1X\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvcf7poTdO1X\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open11.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662096228,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:14:56 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv8VdRAHfWDI\",\"naan\":\"66666\",\"handle\":\"srv8VdRAHfWDI\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open10.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661666866,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:46 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2pf0fHK0xk\",\"naan\":\"66666\",\"handle\":\"srv2pf0fHK0xk\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://openlol9.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661651246,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:31 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"}]";
        String arkString = "{\"urlTarget\":\""+ url + "\","
                + "\"title\":\" "+ title + "\","
                
                + "\"creator\":\"test\","
                + "\"handle_prefix\":\"20.500.11859\","
                + "\"handle\":\"\","
                + "\"handle_stored\":false,"
                + "\"date\":null,"
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\""+ idArk + "\","
                + "\"name\":null,"
                + "\"qualifier\":null,"
                + "\"modificationDate\":\"2016-11-11\","
                + "\"saved\":false,"
                + "\"naan\":\"" + naan + "\","
                + "\"redirect\":true,"
                + "\"dcElements\":[],"
                + "\"userArkId\":1,"
                + "\"owner\":null,\"qualifiers\":[],\"format\":\"\","
                + "\"identifier\":\"\","
                + "\"description\":\"\","
                + "\"source\":\"google.com\","
                + "\"subject\":\"\","
                + "\"rights\":\"\","
                + "\"publisher\":\"Mouad\","
                + "\"relation\":\"\","
                + "\"coverage\":\"\","
                + "\"contributor\":\"\"}";
        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource(serverHost + "/rest/ark/single");

        ClientResponse response = webResource.type("application/json")
                .put(ClientResponse.class, newinput);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        System.out.println(output);
    }

    
    /**
     * OK validé #MR
     */ 
    @Test
    public void testAddSingleArk() {
        String loginResp = login();
        if(loginResp == null) return;
        
        String url = "http://miled.fr/1";
        String title = "IGLS_8-1_02377";
        String idArk = "abcdefghijk";
        
        
        JSONObject loginrespasjson = new JSONObject(loginResp);
        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");

        System.out.println(loginrespasjson);

        //arkString = "[{\"ark\":\"66666/srvcLVuSQ7jzB\",\"naan\":\"66666\",\"handle\":\"srvcLVuSQ7jzB\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso22.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699477734,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:24:37 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv6MvT5bfl4M\",\"naan\":\"66666\",\"handle\":\"/srv6MvT5bfl4M\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso21.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699345568,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:22:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2nvx2J8b6F\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srv2nvx2J8b6F\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso20.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525698729011,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:12:09 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srveFZUqQ1t6x\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srveFZUqQ1t6x\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso18.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525694433467,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 14:00:33 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvNZwNRozRzb\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvNZwNRozRzb\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open135.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247245243,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:47:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvoeBANJXH7l\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvoeBANJXH7l\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open13.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247174077,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:46:14 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvqIeJcfIi2T\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvqIeJcfIi2T\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open12.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662108907,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:15:08 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvcf7poTdO1X\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvcf7poTdO1X\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open11.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662096228,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:14:56 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv8VdRAHfWDI\",\"naan\":\"66666\",\"handle\":\"srv8VdRAHfWDI\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open10.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661666866,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:46 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2pf0fHK0xk\",\"naan\":\"66666\",\"handle\":\"srv2pf0fHK0xk\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://openlol9.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661651246,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:31 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"}]";
        String arkString = "{\"urlTarget\":\""+ url + "\","
                + "\"title\":\" "+ title + "\","
                
                + "\"creator\":\"\","
                + "\"handle_prefix\":\"20.500.11859\","
                + "\"handle\":\"\","
                + "\"handle_stored\":false,"
                + "\"date\":\"2016-11-11\","
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\""+ idArk + "\","
                + "\"name\":null,"
                + "\"qualifier\":null,"
                + "\"modificationDate\":\"2016-11-11\","
                + "\"saved\":false,"
                + "\"naan\":\"" + naan + "\","
                + "\"redirect\":true,"
                + "\"dcElements\":[],"
                + "\"userArkId\":1,"
                + "\"owner\":null,"
                + "\"qualifiers\":[],"
                + "\"format\":\"\","
                + "\"identifier\":\"\","
                + "\"description\":\"\","
                + "\"source\":\"\","
                + "\"subject\":\"\","
                + "\"rights\":\"\","
                + "\"publisher\":\"\","
                + "\"relation\":\"\","
                + "\"coverage\":\"\","
                + "\"contributor\":\"\"}";
        
        
    //    arkString = "{\"urlTarget\":\"http://mondomaine.fr/ddfgffdg\",\"title\":\" vasevase\",\"creator\":\"ff\",\"handle_prefix\":\"20.500.11859\",\"handle\":\"\",\"handle_stored\":false,\"date\":\"null\",\"type\":\"Service\",\"language\":\"fr\",\"linkup\":true,\"ark\":\"\",\"name\":\"null\",\"qualifier\":\"null\",\"modificationDate\":\"2018-07-24\",\"saved\":false,\"naan\":\"66666\",\"redirect\":true,\"dcElements\":[],\"userArkId\":1,\"owner\":null,\"qualifiers\":[],\"format\":\"\",\"identifier\":\"\",\"description\":\"\",\"source\":\"ghfgh\",\"subject\":\"\",\"rights\":\"\",\"publisher\":\"gfdg\",\"relation\":\"\",\"coverage\":\"\",\"contributor\":\"\"}";
       
 
        
        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource(serverHost + "/rest/ark/single");

        ClientResponse response = webResource.type("application/json")
                .put(ClientResponse.class, newinput);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        String idArkReturned = getIArk(output);
        System.out.println(idArkReturned);        
        System.out.println(output);
    }
    
    @Test
    public void detectArk(){
        ArkClientRest arkClientRest = new ArkClientRest();

        //     String idArk = "{\"ark description\":\"Ark Added.\",\"handle description\":\"Handle Added.\",\"idArk\":\"srvy4btF7VkX8\",\"Ark\":\"66666/srvy4btF7VkX8\",\"status\":\"Success\",\"Handle\":\"20.500.11859/66666.srvy4btF7VkX8\",\"token\":\"25bqisma4g1hfbh7qgcadrvlk4\"}";

        String url = "http://miled.fr/1";
        String idArk = "{\"urlTarget\":\" "+ url + "\","
                + "\"title\":\" vasevase\",\"creator\":\"\",\"handle_prefix\":\"20.500.11859\","
                + "\"handle\":\"\",\"handle_stored\":false,\"date\":\"2018-07-24\","
                + "\"type\":\"crt\",\"language\":\"fr\",\"linkup\":true,"
                + "\"ark\":\"\",\"name\":\"\",\"qualifier\":\"\",\"modificationDate\":\"2018-07-24\","
                + "\"saved\":false,\"naan\":\"66666\",\"redirect\":true,\"dcElements\":[],"
                + "\"userArkId\":1,\"owner\":\"\",\"qualifiers\":[],"
                + "\"format\":\"\",\"identifier\":\"\",\"description\":\"\","
                + "\"source\":\"\",\"subject\":\"\",\"rights\":\"\","
                + "\"publisher\":\"\",\"relation\":\"\",\"coverage\":\"\",\"contributor\":\"\"}";
        
        String idArkReturned = arkClientRest.getIdArk();
        String idHandleReturned = arkClientRest.getIdHandle();
        System.out.println(idArkReturned);
        System.out.println(idHandleReturned);        
        
        
        
    }
    
    
    private String getIArk(String jsonText) {
        //{"responseCode":1,"handle":"20.500.11942/opentheso443"}
        JsonReader reader = Json.createReader(new StringReader(jsonText));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        JsonString values = jsonObject.getJsonString("idArk");
        if(values != null)
            return values.getString();
      /*  for (int i = 0; i < values.size(); i++) {

                JsonObject item = values.getJsonObject(i);
                try {
                    idHandle = item.getString("nb_notices");
                }
                catch (JsonException e) {
                    System.out.println(e.toString());
                }
                catch (Exception ex) {
                    System.out.println(ex.toString());
                }
        }*/
        return null;
    }    
    
    /**
     * OK validé #MR
     */ 
    @Test
    public void testUpdateArk() {
        String loginResp = login();
        if(loginResp == null) return;
        
        String newUrl = "http://iglsdev.mom.fr/IGLS/Image/imageDe";
        String newTitle = "IGLS_8-1_02377PP";
        String idArk = "66666/IGLS_8-1_02377645fggg";        
        
        JSONObject loginrespasjson = new JSONObject(loginResp);
        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");

        //System.out.println("loginrespasjson => " + loginrespasjson);
        String arkString = "{\"urlTarget\":\"" + newUrl +"\","
                + "\"title\":\"" + newTitle + "\","
                + "\"creator\":null,"
                + "\"date\":\"2018-07-23\","
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\"" + idArk +"\","
                + "\"name\":null,"
                + "\"qualifier\":null,"
                + "\"modificationDate\":\"2018-07-23\","
                + "\"saved\":false,"
                + "\"naan\":\"66666\","
                + "\"redirect\":true,"
                + "\"dcElements\":[{\"name\":\"creator\","
                + "\"value\":\"TRETRE\","
                + "\"language\":\"fr\"}],"
                + "\"userArkId\":1,"
                + "\"owner\":null,\"qualifiers\":[],\"format\":\"\","
                + "\"identifier\":\"\","
                + "\"description\":\"\","
                + "\"source\":\"\","
                + "\"subject\":\"\","
                + "\"rights\":\"\","
                + "\"publisher\":\"\","
                + "\"relation\":\"\","
                + "\"coverage\":\"\","
                + "\"contributor\":\"\"}";
        
        ObjectMapper mapper = new ObjectMapper();
        DateFormat longDateFormatFR = DateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.LONG, new Locale("FR", "fr"));
        mapper.setDateFormat(longDateFormatFR);
        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource(serverHost + "/rest/ark/");

        ClientResponse response = webResource.type("application/json")
                .post(ClientResponse.class, newinput);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        System.out.println(output);

    }
    
 
    
    
    
    
    
    
    
    
    
    
    

    @Test
    public void testAddMultipleArk() {
        String loginResp = login();
        if(loginResp == null) return;

        JSONObject loginrespasjson = new JSONObject(loginResp);
        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");

        System.out.println(loginrespasjson);

        //arkString = "[{\"ark\":\"66666/srvcLVuSQ7jzB\",\"naan\":\"66666\",\"handle\":\"srvcLVuSQ7jzB\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso22.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699477734,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:24:37 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv6MvT5bfl4M\",\"naan\":\"66666\",\"handle\":\"/srv6MvT5bfl4M\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso21.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699345568,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:22:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2nvx2J8b6F\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srv2nvx2J8b6F\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso20.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525698729011,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:12:09 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srveFZUqQ1t6x\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srveFZUqQ1t6x\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso18.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525694433467,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 14:00:33 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvNZwNRozRzb\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvNZwNRozRzb\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open135.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247245243,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:47:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvoeBANJXH7l\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvoeBANJXH7l\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open13.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247174077,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:46:14 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvqIeJcfIi2T\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvqIeJcfIi2T\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open12.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662108907,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:15:08 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvcf7poTdO1X\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvcf7poTdO1X\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open11.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662096228,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:14:56 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv8VdRAHfWDI\",\"naan\":\"66666\",\"handle\":\"srv8VdRAHfWDI\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open10.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661666866,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:46 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2pf0fHK0xk\",\"naan\":\"66666\",\"handle\":\"srv2pf0fHK0xk\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://openlol9.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661651246,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:31 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"}]";
        String arkString = "[{\"urlTarget\":\"http://opentheso107.mom.fr\","
                + "\"title\":\"\","
                + "\"creator\":\"\","
                + "\"handle_prefix\":\"20.500.11859\","
                + "\"handle\":\"\","
                + "\"handle_stored\":false,"
                + "\"date\":\"\","
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\"\","
                + "\"name\":\"\","
                + "\"qualifier\":\"\","
                + "\"modificationDate\":\"2016-11-11\","
                + "\"saved\":false,"
                + "\"naan\":\"66666\","
                + "\"redirect\":true,"
                + "\"dcElements\":[],"
                + "\"userArkId\":1,"
                + "\"owner\":null,\"qualifiers\":[],\"format\":\"\","
                + "\"identifier\":\"\","
                + "\"description\":\"\","
                + "\"source\":\"google.com\","
                + "\"subject\":\"\","
                + "\"rights\":\"\","
                + "\"publisher\":\"Mouad\","
                + "\"relation\":\"\","
                + "\"coverage\":\"\","
                + "\"contributor\":\"\"},"
                + "{\"urlTarget\":\"http://opentheso108.mom.fr\","
                + "\"title\":\"\","
                + "\"creator\":\"\","
                + "\"handle_prefix\":\"20.500.11859\","
                + "\"handle\":\"\","
                + "\"handle_stored\":false,"
                + "\"date\":\"\","
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\"\","
                + "\"name\":\"\","
                + "\"qualifier\":\"\","
                + "\"modificationDate\":\"2016-11-11\","
                + "\"saved\":false,"
                + "\"naan\":\"66666\","
                + "\"redirect\":true,"
                + "\"dcElements\":[],"
                + "\"userArkId\":1,"
                + "\"owner\":null,\"qualifiers\":[],\"format\":\"\","
                + "\"identifier\":\"\","
                + "\"description\":\"\","
                + "\"source\":\"google.com\","
                + "\"subject\":\"\","
                + "\"rights\":\"\","
                + "\"publisher\":\"Mouad\","
                + "\"relation\":\"\","
                + "\"coverage\":\"\","
                + "\"contributor\":\"\"}]";

        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource(serverHost + //restPath +
                        "/ark/multiple");

        ClientResponse response = webResource.type("application/json")
                .put(ClientResponse.class, newinput);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        System.out.println(output);

    }
    
    @Test
    public void isHandleExist() {
        String output = "";
        String urlHandle = "http://193.48.137.68:8000/api/handles/";
        String prefix = "20.500.11859";

        String internalId = "66666.crt2hbt7fWNBn";
        Client client = Client.create();

        WebResource webResource = client
                .resource("http://193.48.137.68:8000/api/handles/" + prefix + "/" + internalId);

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        output = response.getEntity(String.class);

        org.json.JSONObject json = new org.json.JSONObject(output);
        if (json.getInt("responseCode") == 100) {

            System.err.println("n'existe pas");
        }
        if (json.getInt("responseCode") == 1) {

            System.err.println("existe");
        }
    }    

    @Test
    public void testVerifyArk(){
        ArkClientRest arkClientRest = new ArkClientRest();
        
        Properties propertiesArk = new Properties();
        propertiesArk.setProperty("serverHost", "https://ark.mom.fr/Arkeo");//nodePreference.getServeurArk());
        propertiesArk.setProperty("idNaan", "");
        propertiesArk.setProperty("user", "");
        propertiesArk.setProperty("password", "" );
        arkClientRest.setPropertiesArk(propertiesArk);        
        
        
        if(!arkClientRest.isArkExist("66666/crtOt5MNUvBHl")) {
            System.err.println("N'exite pas");
        }
   
    }
    
    @Test
    public void testGetArks() {
        WebResource webResource = client
                .resource(serverHost +// restPath +
                        "/ark/offset=0&limit=10");

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        String output = response.getEntity(String.class);
        System.out.println(output);

    }

    @Test
    public void testGetAllArks() {

        WebResource webResource = client
                .resource(serverHost + //restPath +
                        "/ark");

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        String output = response.getEntity(String.class);
        System.out.println(output);

    }    
    
    /**
     * pour établir la connexion
     * @param post 
     */
    private String login() {
        WebResource webResource = client
                .resource(serverHost + "/rest/login/username=" +
                        user + 
                        "&password=" +
                        password +
                        "&naan=" + 
                        naan);

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
           /* throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());*/
            return null;
        }
        return response.getEntity(String.class);
    }    

}
