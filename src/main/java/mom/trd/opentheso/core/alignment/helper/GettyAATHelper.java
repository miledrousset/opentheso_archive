/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.alignment.helper;

import com.bordercloud.sparql.Endpoint;
import com.bordercloud.sparql.EndpointException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.core.alignment.SelectedResource;
import mom.trd.opentheso.core.json.helper.JsonHelper;

/**
 *
 * @author miled.rousset
 */
public class GettyAATHelper {

    private StringBuffer messages;
   // private ArrayList<NodeAlignment> listAlignValues;
    
    // les informations récupérées de Wikidata
    private ArrayList<SelectedResource> resourceAATTraductions;
    private ArrayList<SelectedResource> resourceAATDefinitions; 
    private ArrayList<SelectedResource> resourceAATImages; 
    
    
    public GettyAATHelper() {
        messages = new StringBuffer();
    }
    
    /**
     * Alignement du thésaurus vers la source Wikidata en Sparql et en retour du Json
     * @param idC
     * @param idTheso
     * @param lexicalValue
     * @param lang
     * @param query
     * @param source
     * @return 
     */
    public ArrayList<NodeAlignment> queryAAT(String idC, String idTheso,
            String lexicalValue, String lang, 
            String query, String source) {
        
        ArrayList<NodeAlignment> listAlignValues = new ArrayList<>();
        
        if (query.trim().equals("")) {
            return null;
        }
        if (lexicalValue.trim().equals("")) {
            return null;
        }
        
        String [] splitValues = lexicalValue.split(" ");
        
        String value = "";
        
        for (String splitValue : splitValues) {
            if(value.isEmpty())
                value = splitValue;
            else
                value = value + " AND " + splitValue;
        }
    //    value = value.replaceAll(" ", "%20");        

        ArrayList<NodeAlignment> listeAlign;
        // construction de la requête de type (webservices Opentheso)

        try {
            value = URLEncoder.encode(value,"UTF-8");            
            query = query.replace("##value##", value);            
            URL url = new URL(query);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                messages.append("Failed : HTTP error code : ");
                messages.append(conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String records = "";
            while ((output = br.readLine()) != null) {
                records += output;
            }

            conn.disconnect();
            listeAlign = getValuesSubject(records, idC, idTheso, source);

        } catch (MalformedURLException e) {
            messages.append(e.toString());
            return null;
        } catch (IOException e) {
            messages.append(e.toString());
            return null;            
        }
        return listeAlign;
    }       

    private ArrayList<NodeAlignment> getValuesSubject(String jsonDatas, 
                    String idC, String idTheso, String source) {
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1;
        JsonArray jsonArray;
        ArrayList<NodeAlignment> listAlignValues = new ArrayList<>();
        

        String uri = "https://www.idref.fr/";
        
        //String valeur = "";
        //String idRef = "";
        //JsonValue jsonValue;

        try {
            if(jsonObject == null) return null;
            if(jsonObject.getJsonObject("response") == null) return null;
            jsonArray = jsonObject.getJsonObject("response").getJsonArray("docs");
        } catch (Exception e) {
            System.err.println(e.toString());
            return null;
        }
      
        for (int i = 0; i < jsonArray.size(); i++) {
            NodeAlignment na = new NodeAlignment();
            na.setInternal_id_concept(idC);
            na.setInternal_id_thesaurus(idTheso);
            
            jsonObject1 = jsonArray.getJsonObject(i); //jsonObject1.getValueType()
           
            // label ou Nom
            try {
                jsonObject1.getString("affcourt_z");
                na.setConcept_target(((JsonObject) jsonObject1).getString("affcourt_z"));
            } catch (Exception e) {
                continue;
            }            
            
            // description
            na.setDef_target("");
            na.setThesaurus_target(source);

            // URI
            try {
                jsonObject1.getString("ppn_z");
                na.setUri_target(uri + ((JsonObject) jsonObject1).getString("ppn_z"));
            } catch (Exception e) {
                continue;
            }             
            na.setUri_target(uri + ((JsonObject) jsonObject1).getString("ppn_z"));

            listAlignValues.add(na);
            
        /*    valeur = ((JsonObject) jsonObject1).getString("affcourt_z");
            idRef = ((JsonObject) jsonObject1).getString("ppn_z");

            System.out.println(valeur);
            System.out.println(idRef);
            System.out.println("url = " + uri+idRef); */
        }
        return listAlignValues;
    }
    
    
    /**
     * Cette fonction permet de récupérer les options de Wikidata 
     * Images, alignements, traductions....ource
     * @param selectedNodeAlignment
     * @param selectedOptions
     * @param thesaurusUsedLanguageWithoutCurrentLang
     * @param thesaurusUsedLanguage
     */
    public void setOptionsFromWikidata(
            NodeAlignment selectedNodeAlignment,
            List<String> selectedOptions,
            ArrayList<String> thesaurusUsedLanguageWithoutCurrentLang,
            ArrayList<String> thesaurusUsedLanguage) {
        if (selectedNodeAlignment == null) {
            return;
        }
        CurlHelper curlHelper = new CurlHelper();
        curlHelper.setHeader1("Accept");
        curlHelper.setHeader2("application/json");
        
        String uri = selectedNodeAlignment.getUri_target();//."https://www.wikidata.org/entity/Q178401";//"https://www.wikidata.org/entity/Q178401";//Q7748";Q324926
        String datas = curlHelper.getDatasFromUri(uri);
        String entity = uri.substring(uri.lastIndexOf("/") + 1);

        
        for (String selectedOption : selectedOptions) {
            switch (selectedOption) {
                case "langues":
                    resourceAATTraductions = getTraductions(datas, entity, thesaurusUsedLanguageWithoutCurrentLang);
                    break;
                case "notes":
                    resourceAATDefinitions = getDescriptions(datas, entity, thesaurusUsedLanguage);
                    break;
                case "images":
                    resourceAATImages = getImages(datas, entity);
                    break;                    
            }
        }
    }
    
    /**
     * récupération des traductions
     * @param jsonDatas
     * @param entity
     * @param languages
     * @return 
     */
    private ArrayList<SelectedResource> getTraductions(
            String jsonDatas, String entity,
            ArrayList<String> languages) {
        ArrayList<SelectedResource> traductions = new ArrayList<>();
        
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1;
        JsonValue jsonValue;
        
        String lang;
        String value;

        try {
            jsonObject1 = jsonObject.getJsonObject("entities").getJsonObject(entity).getJsonObject("labels");
        } catch (Exception e) {
            //System.err.println(e.toString());
            return null;
        }
        for (String language : languages) {
            try {
                SelectedResource selectedResource = new SelectedResource();
                jsonValue = jsonObject1.getJsonObject(language).get("language");
                lang = jsonValue.toString().replace("\"", "");
                selectedResource.setIdLang(lang);
                jsonValue = jsonObject1.getJsonObject(language).get("value");
                value = jsonValue.toString().replace("\"", "");
                selectedResource.setGettedValue(value);
                traductions.add(selectedResource);
            } catch (Exception e) {
            }
        }
        return traductions;
    }
    
    /**
     * permet de récupérer les descriptions de wikidata
     * @param jsonDatas
     * @param entity
     * @param languages
     * @return 
     */
    private ArrayList<SelectedResource> getDescriptions(
            String jsonDatas, String entity,
            ArrayList<String> languages) {
        ArrayList<SelectedResource> descriptions = new ArrayList<>();
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1;
        JsonValue jsonValue;

        String lang;
        String value;
        
        try {
            jsonObject1 = jsonObject.getJsonObject("entities").getJsonObject(entity).getJsonObject("descriptions");
        } catch (Exception e) {
            //System.err.println(e.toString());
            return null;
        }
        
        for (String language : languages) {
            try {
                SelectedResource selectedResource = new SelectedResource();
                jsonValue = jsonObject1.getJsonObject(language).get("language");
                lang = jsonValue.toString().replace("\"", "");
                selectedResource.setIdLang(lang);
                jsonValue = jsonObject1.getJsonObject(language).get("value");
                value = jsonValue.toString().replace("\"", "");
                selectedResource.setGettedValue(value);
                descriptions.add(selectedResource);
            } catch (Exception e) {
            }
        }
        return descriptions;
    }
    
    /**
     * permet de récupérer les images de Wikidata
     * @param jsonDatas
     * @param entity 
     * @return  
     */
    private ArrayList<SelectedResource> getImages(String jsonDatas, String entity) {
        // pour construire l'URL de Wikimedia, il faut ajouter 
        // http://commons.wikimedia.org/wiki/Special:FilePath/
        // puis le nom de l'image

        String fixedUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/";
        
        JsonHelper jsonHelper = new JsonHelper();
        JsonObject jsonObject = jsonHelper.getJsonObject(jsonDatas);

        //    JsonObject test = jsonObject.getJsonObject("entities");
        JsonObject jsonObject1;

        JsonObject jsonObject2;
        JsonValue jsonValue;
        
        ArrayList<SelectedResource> imagesUrls = new ArrayList<>();

        try {
            jsonObject1 = jsonObject.getJsonObject("entities").getJsonObject(entity).getJsonObject("claims");//.getJsonObject("P18");
        } catch (Exception e) {
            //System.err.println(e.toString());
            return null;
        }

        try {
            JsonArray jsonArray = jsonObject1.getJsonArray("P18");
            for (int i = 0; i < jsonArray.size(); i++) {
                SelectedResource selectedResource = new SelectedResource();
                jsonObject2 = jsonArray.getJsonObject(i);
                jsonValue = jsonObject2.getJsonObject("mainsnak").getJsonObject("datavalue").get("value");
                selectedResource.setGettedValue(fixedUrl + jsonValue.toString().replace("\"", ""));
                imagesUrls.add(selectedResource);
            }

        } catch (Exception e) {
        }
        return imagesUrls;
    }

    public String getMessages() {
        return messages.toString();
    }

    public ArrayList<SelectedResource> getResourceAATTraductions() {
        return resourceAATTraductions;
    }

    public ArrayList<SelectedResource> getResourceAATDefinitions() {
        return resourceAATDefinitions;
    }

    public ArrayList<SelectedResource> getResourceAATImages() {
        return resourceAATImages;
    }


    
    
}
