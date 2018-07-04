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
import java.text.DateFormat;
import java.util.Locale;
import org.junit.Test;
import org.primefaces.json.JSONObject;

/**
 *
 * @author formation
 */
public class ArkRestTests {

    Client client;
    String loginresp;

    public ArkRestTests() {

        client = Client.create();

    }

    //@Test
    public void testlogin(boolean post) {

        WebResource webResource = client
                .resource("http://localhost:8082/rest/login/username=demo&password=demo2&naan=66666&post=" + post + "");

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        //System.out.println("Output from Server .... \n");
        //System.out.println(output);
        loginresp = output;
        //return output;
    }

    @Test
    public void testGetHandle() {

        WebResource webResource = client
                .resource("http://localhost:8084/rest/ark/handle=MOM2");

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
    public void testGetArk() {
        testlogin(false);
        JSONObject jsonObj = new JSONObject(loginresp);
        String token = jsonObj.getString("token");
        WebResource webResource = client
                .resource("http://localhost:8084/rest/ark/naan=66666&id=srvcLVuSQ7jzB&access_token=" + token);

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
    public void testGetArks() {
        testlogin(false);
        JSONObject jsonObj = new JSONObject(loginresp);
        String token = jsonObj.getString("token");
        WebResource webResource = client
                .resource("http://localhost:8084/rest/ark/offset=0&limit=10&access_token=" + token);

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
        testlogin(false);
        JSONObject jsonObj = new JSONObject(loginresp);
        String token = jsonObj.getString("token");
        WebResource webResource = client
                .resource("http://localhost:8084/rest/ark/access_token=" + token);

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
     * permet d'ajouter un seul ARK 
     */
    @Test
    public void testAddSingleArk() {
        testlogin(true);
        JSONObject loginrespasjson = new JSONObject(loginresp);
        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");

        System.out.println(loginrespasjson);

        //arkString = "[{\"ark\":\"66666/srvcLVuSQ7jzB\",\"naan\":\"66666\",\"handle\":\"srvcLVuSQ7jzB\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso22.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699477734,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:24:37 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv6MvT5bfl4M\",\"naan\":\"66666\",\"handle\":\"/srv6MvT5bfl4M\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso21.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699345568,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:22:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2nvx2J8b6F\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srv2nvx2J8b6F\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso20.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525698729011,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:12:09 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srveFZUqQ1t6x\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srveFZUqQ1t6x\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso18.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525694433467,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 14:00:33 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvNZwNRozRzb\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvNZwNRozRzb\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open135.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247245243,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:47:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvoeBANJXH7l\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvoeBANJXH7l\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open13.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247174077,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:46:14 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvqIeJcfIi2T\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvqIeJcfIi2T\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open12.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662108907,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:15:08 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvcf7poTdO1X\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvcf7poTdO1X\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open11.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662096228,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:14:56 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv8VdRAHfWDI\",\"naan\":\"66666\",\"handle\":\"srv8VdRAHfWDI\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open10.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661666866,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:46 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2pf0fHK0xk\",\"naan\":\"66666\",\"handle\":\"srv2pf0fHK0xk\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://openlol9.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661651246,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:31 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"}]";
        String arkString = "{\"urlTarget\":\"http://openthesobas.mom.fr\","
                + "\"title\":null,"
                + "\"creator\":null,"
                + "\"handle_prefix\":\"20.500.11859\","
                + "\"handle\":\"\","
                + "\"handle_stored\":false,"
                + "\"date\":null,"
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\"\","
                + "\"name\":null,"
                + "\"qualifier\":null,"
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
                + "\"contributor\":\"\"}";
        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource("http://localhost:8082/rest/ark/single");

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
    public void testAddMultipleArk() {
        testlogin(true);
        JSONObject loginrespasjson = new JSONObject(loginresp);
        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");

        System.out.println(loginrespasjson);

        //arkString = "[{\"ark\":\"66666/srvcLVuSQ7jzB\",\"naan\":\"66666\",\"handle\":\"srvcLVuSQ7jzB\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso22.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699477734,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:24:37 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv6MvT5bfl4M\",\"naan\":\"66666\",\"handle\":\"/srv6MvT5bfl4M\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso21.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525699345568,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:22:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2nvx2J8b6F\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srv2nvx2J8b6F\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso20.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525698729011,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 15:12:09 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srveFZUqQ1t6x\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srveFZUqQ1t6x\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://opentheso18.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525694433467,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"7 mai 2018 14:00:33 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvNZwNRozRzb\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvNZwNRozRzb\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open135.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247245243,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:47:25 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvoeBANJXH7l\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvoeBANJXH7l\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open13.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1525247174077,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"2 mai 2018 09:46:14 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvqIeJcfIi2T\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvqIeJcfIi2T\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open12.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662108907,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:15:08 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srvcf7poTdO1X\",\"naan\":\"66666\",\"handle\":\"20.500.11859/srvcf7poTdO1X\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open11.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524662096228,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:14:56 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv8VdRAHfWDI\",\"naan\":\"66666\",\"handle\":\"srv8VdRAHfWDI\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://open10.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661666866,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:46 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"},{\"ark\":\"66666/srv2pf0fHK0xk\",\"naan\":\"66666\",\"handle\":\"srv2pf0fHK0xk\",\"handle_prefix\":\"20.500.11859\",\"handle_stored\":true,\"urlTarget\":\"http://openlol9.mom.fr\",\"linkup\":true,\"redirect\":true,\"userArkId\":1,\"modificationDate\":1524661651246,\"title\":null,\"creator\":null,\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"name\":null,\"qualifier\":null,\"saved\":false,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[],\"owner\":null,\"qualifiers\":[],\"identifier\":\"\",\"format\":\"\",\"subject\":\"\",\"source\":\"\",\"description\":\"\",\"modificationDateAsString\":\"25 avril 2018 15:07:31 CEST\",\"relation\":\"\",\"publisher\":\"\",\"contributor\":\"\",\"rights\":\"\",\"coverage\":\"\"}]";
        String arkString = "[{\"urlTarget\":\"http://opentheso312.mom.fr\","
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
                .resource("http://localhost:8084/rest/ark/multiple");

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
    public void testUpdateArk() {
        testlogin(true);

        JSONObject loginrespasjson = new JSONObject(loginresp);
        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");

        //System.out.println("loginrespasjson => " + loginrespasjson);
        String arkString = "{\"urlTarget\":\"http://opentheso5.mom.fr\","
                + "\"title\":miled,"
                + "\"creator\":null,"
                + "\"date\":null,"
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\"66666/srv0N9gRX02Bo\","
                + "\"name\":null,"
                + "\"qualifier\":null,"
                + "\"modificationDate\":\"10 janvier 2014 08:32:52 CEST\","
                + "\"saved\":false,"
                + "\"naan\":\"66666\","
                + "\"redirect\":true,"
                + "\"dcElements\":[{\"name\":\"creator\","
                + "\"value\":\"TRETRE\","
                + "\"language\":\"fr\","
                + "\"type\":\"CREATOR\","
                + "\"placeholder\":false}],"
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

        /* 
        
        String a = "{\"urlTarget\":\"http://toyti.fr\","
                + "\"title\":null,"
                + "\"creator\":null,"
                + "\"date\":null,"
                + "\"type\":\"Service\","
                + "\"language\":\"fr\","
                + "\"linkup\":true,"
                + "\"ark\":\"66666/srvbmiOo4YbbM\","
                + "\"name\":null,"
                + "\"qualifier\":null,"
                + "\"modificationDate\":\"10 janvier 2014 07:32:52 CET\","
                + "\"saved\":false,"
                + "\"naan\":\"66666\","
                + "\"redirect\":true,"
                + "\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},"
                + "\"dcElements\":[{\"name\":\"format\","
                +       "\"value\":\"\","
                +       "\"language\":\"fr\","
                +       "\"type\":\"FORMAT\","
                +       "\"placeholder\":false},"
                + "{\"name\":\"identifier\",\"value\":\"\",\"language\":\"fr\",\"type\":\"ID\",\"placeholder\":false},{\"name\":\"description\",\"value\":\"\",\"language\":\"fr\",\"type\":\"DESC\",\"placeholder\":false},{\"name\":\"source\",\"value\":\"\",\"language\":\"fr\",\"type\":\"SOURCE\",\"placeholder\":false},{\"name\":\"subject\",\"value\":\"\",\"language\":\"fr\",\"type\":\"SUBJECT\",\"placeholder\":false},{\"name\":\"rights\",\"value\":\"\",\"language\":\"fr\",\"type\":\"RIGHTS\",\"placeholder\":false},{\"name\":\"publisher\",\"value\":\"\",\"language\":\"fr\",\"type\":\"PUB\",\"placeholder\":false},{\"name\":\"relation\",\"value\":\"\",\"language\":\"fr\",\"type\":\"REL\",\"placeholder\":false},{\"name\":\"coverage\",\"value\":\"\",\"language\":\"fr\",\"type\":\"COVERAGE\",\"placeholder\":false},{\"name\":\"contributor\",\"value\":\"\",\"language\":\"fr\",\"type\":\"CONTRIB\",\"placeholder\":false},{\"name\":\"creator\",\"value\":\"TRETRE\",\"language\":\"fr\",\"type\":\"CREATOR\",\"placeholder\":false}],\"userArkId\":1,\"owner\":null,\"qualifiers\":[],\"description\":\"\",\"identifier\":\"\",\"format\":\"\",\"modificationDateAsString\":\"10 janvier 2014 07:32:52 CET\",\"publisher\":\"\",\"contributor\":\"\",\"relation\":\"\",\"rights\":\" - \",\"source\":\"\",\"coverage\":\"\",\"subject\":\"\"}";
         */
//"{\"urlTarget\":\"http://toutou.fr\",\"title\":\"testttile\",\"creator\":\"mouad\",\"date\":null,\"type\":\"Service\",\"language\":\"fr\",\"linkup\":true,\"ark\":\"\",\"name\":null,\"qualifier\":null,\"modificationDate\":\"2016-11-11\",\"saved\":false,\"naan\":\"66666\",\"redirect\":true,\"arkeoId\":{\"idLength\":10,\"id\":null,\"prefix\":null},\"dcElements\":[{\"name\":\"format\",\"value\":\"\",\"language\":\"fr\",\"type\":\"FORMAT\",\"placeholder\":false},{\"name\":\"identifier\",\"value\":\"\",\"language\":\"fr\",\"type\":\"ID\",\"placeholder\":false},{\"name\":\"description\",\"value\":\"\",\"language\":\"fr\",\"type\":\"DESC\",\"placeholder\":false},{\"name\":\"source\",\"value\":\"\",\"language\":\"fr\",\"type\":\"SOURCE\",\"placeholder\":false},{\"name\":\"subject\",\"value\":\"\",\"language\":\"fr\",\"type\":\"SUBJECT\",\"placeholder\":false},{\"name\":\"rights\",\"value\":\"\",\"language\":\"fr\",\"type\":\"RIGHTS\",\"placeholder\":false},{\"name\":\"publisher\",\"value\":\"\",\"language\":\"fr\",\"type\":\"PUB\",\"placeholder\":false},{\"name\":\"relation\",\"value\":\"\",\"language\":\"fr\",\"type\":\"REL\",\"placeholder\":false},{\"name\":\"coverage\",\"value\":\"\",\"language\":\"fr\",\"type\":\"COVERAGE\",\"placeholder\":false},{\"name\":\"contributor\",\"value\":\"\",\"language\":\"fr\",\"type\":\"CONTRIB\",\"placeholder\":false},{\"name\":\"creator\",\"value\":\"mouad\",\"language\":\"fr\",\"type\":\"CREATOR\",\"placeholder\":false}],\"userArkId\":1,\"owner\":null,\"qualifiers\":[],\"format\":\"\",\"identifier\":\"\",\"description\":\"\",\"source\":\"\",\"relation\":\"\",\"publisher\":\"\",\"subject\":\"\",\"rights\":\" - \",\"contributor\":\"\",\"coverage\":\"\"}";
        ObjectMapper mapper = new ObjectMapper();

        DateFormat longDateFormatFR = DateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.LONG, new Locale("FR", "fr"));
        mapper.setDateFormat(longDateFormatFR);
        String jsonInString = null;
        String json;
        /*
        try {
            ark = mapper.readValue(arkString, Ark.class);
            ark.getDcElements().add(new DCElement(DCElement.EL_CREATOR, "TRETRE", "fr"));           
            arkString = mapper.writeValueAsString(ark);
       
        } catch (IOException ex) {
            Logger.getLogger(ArkRestTests.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        //System.out.println("GAMBATEEEEEEEEEE BAAAKA <3");
        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource("http://localhost:8084/rest/ark/");

        ClientResponse response = webResource.type("application/json")
                .post(ClientResponse.class, newinput);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);
        System.out.println(output);

    }

}
