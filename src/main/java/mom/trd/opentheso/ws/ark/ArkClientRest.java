package mom.trd.opentheso.ws.ark;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;


import java.io.StringReader;
import java.util.Properties;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import org.primefaces.json.JSONObject;

public final class ArkClientRest {
    private Properties propertiesArk;  
    Client client;
    
    private String jsonArk;
    
    public ArkClientRest() {
    }
   
    /**
     * defition des propriétés du serveur Ark
     * @param propertiesArk 
     * #MR
     */
    public void setPropertiesArk(Properties propertiesArk) {
        this.propertiesArk = propertiesArk;
    }    
    
    /**
     * pour établir la connexion
     * @return
     * #MR
     */
    public String login() {
        client = Client.create();
        WebResource webResource = client
                .resource(
                        propertiesArk.getProperty("serverHost") +
                        "/rest/login/username=" +
                        propertiesArk.getProperty("user") + 
                        "&password=" +
                        propertiesArk.getProperty("password") +
                        "&naan=" + 
                        propertiesArk.getProperty("idNaan"));

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
           /* throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());*/
            return null;
        }
        return response.getEntity(String.class);
    }       
    
    
    
    /**
     * permet de vérifier si l'identifiant Ark exsite
     * @param ark
     * @return 
     */
    public boolean isArkExist(String ark) {
        client = Client.create();
        String idArk = ark.substring(ark.indexOf("/")+1);
        String naan = ark.substring(0, ark.indexOf("/"));
        WebResource webResource = client.resource(propertiesArk.getProperty("serverHost") +
                        "/rest/ark/naan=" + 
                        naan + 
                        "&id=" +
                        idArk);
        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);
        if (response.getStatus() != 200) {
           // throw new RuntimeException("Failed : HTTP error code : "
           //         + response.getStatus());
            return false;
        }
        String retour = response.getEntity(String.class);
//        System.out.println(jsonArk);
        return isExist(retour);
    }     
    
    private boolean isExist(String jsonResponse){
        if(jsonResponse == null) return false;
        JsonReader reader = Json.createReader(new StringReader(jsonResponse));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        JsonString values = jsonObject.getJsonString("description");
        if(values != null){
            if(values.getString().contains("Inexistant ARK")) return false;
            else
                if(values.getString().contains("Ark retreived")) return true;
        }
        return false; 
    }

    /**
     * permet d'ajouter un identifiant Ark et Handle
     * @param arkString
     * @return 
     */
    public boolean addArk(String arkString) {
        jsonArk = null;
        String loginResp = login();
        if(loginResp == null) return false;
        
        JSONObject loginrespasjson = new JSONObject(loginResp);
/*        String token = loginrespasjson.getString("token");
        String content = loginrespasjson.getString("content");
*/
 //       System.out.println(loginrespasjson);
        loginrespasjson.put("content", arkString);
        String newinput = loginrespasjson.toString();

        WebResource webResource = client
                .resource(propertiesArk.getProperty("serverHost")
                        + "/rest/ark/single");

        ClientResponse response = webResource.type("application/json")
                .put(ClientResponse.class, newinput);
        if (response.getStatus() == 200) {
            jsonArk = response.getEntity(String.class);
            return true;
        }
        return false;
    }    

    public String getIdArk() {
        if(jsonArk == null) return null;
        JsonReader reader = Json.createReader(new StringReader(jsonArk));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        JsonString values = jsonObject.getJsonString("Ark");
        if(values != null){
            if(values.getString().isEmpty()) return null;
            return values.getString().trim();
        }
        return null;
    }
    
    public String getIdHandle() {
        if(jsonArk == null) return null;        
        JsonReader reader = Json.createReader(new StringReader(jsonArk));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        JsonString values = jsonObject.getJsonString("Handle");
        if(values != null) {
            if(values.getString().isEmpty()) return null;
            return values.getString().trim();
        }
        return null;
    }    
    
 
    
}
