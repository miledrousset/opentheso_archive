/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
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

import org.json.JSONArray;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import org.apache.jena.atlas.json.JsonAccess;
import org.json.JSONObject;

/**
 *
 * @author miled.rousset
 */
public class GemetTest {

    public GemetTest() {
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

    // récupération des URI qui parlent du concept avec l'entité codé exp : 
    // http://www.eionet.europa.eu/gemet/getConceptsMatchingKeyword?keyword=environnement&search_mode=3&thesaurus_uri=http://www.eionet.europa.eu/gemet/concept/&language=fr
    // pour environnement
    @Test
    public void searchValue() {
        String mode = "3";
        String lexicalValue = "environnement";
        String lang = "fr";

        String query = "http://www.eionet.europa.eu/gemet/getConceptsMatchingKeyword?keyword=##value##&search_mode=3&thesaurus_uri=http://www.eionet.europa.eu/gemet/concept/&language=##lang##";

        if (query.trim().equals("")) {
            return;
        }
        if (lexicalValue.trim().equals("")) {
            return;
        }

        ArrayList<NodeAlignment> listeAlign = new ArrayList<>();
        // construction de la requête de type (webservices Opentheso)

        lexicalValue = lexicalValue.replaceAll(" ", "%20");
        query = query.replace("##lang##", lang);
        query = query.replace("##value##", lexicalValue);

        try {
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));
            conn.disconnect();
            StringBuffer sb = new StringBuffer(xmlRecord);
            readJson1(sb.toString());

            /*        try {
                SKOSXmlDocument sxd = new ReadFileSKOS().readStringBuffer(sb);
                for (SKOSResource resource : sxd.getResourcesList()) {
                    NodeAlignment na = new NodeAlignment();
                    na.setInternal_id_concept(idC);
                    na.setInternal_id_thesaurus(idTheso);
                    na.setThesaurus_target(source);//"Pactols");
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
                    listeAlign.add(na);
                }
            } catch (Exception ex) {
                Logger.getLogger(AlignmentQuery.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
//        return listeAlign;
    }

    //   @Test
    private void readJson1(String json) {
        String concept = "";
        String uri = "";
        
        try {
            JSONArray jArray = new JSONArray(json);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jb = jArray.getJSONObject(i);
                concept = jb.getJSONObject("preferredLabel").getString("string");
                uri = jb.getString("uri");
                System.out.println(concept + "  " + uri );
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
