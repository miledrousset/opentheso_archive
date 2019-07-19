/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignement;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import org.junit.Test;

/**
 *
 * @author miled.rousset
 */
public class ImportFromWikidataTest {

    public ImportFromWikidataTest() {
    }

    /**
     * permet d'importer un fichier d'alignement en provenance de Wikidata
     * Format json
     * requête Sparql pour Pactols
     * SELECT ?item ?itemLabelFr ?itemLabelEn ?idPactols (SAMPLE(?idAAT) AS ?idAAT) (SAMPLE(?idBNF) AS ?idBNF) (SAMPLE(?idRef) AS ?idRef)
        WHERE 
        {
          ?item wdt:P4212 ?idPactols.
          OPTIONAL { ?item wdt:P1014 ?idAAT }
          OPTIONAL { ?item wdt:P268 ?idBNF }
          OPTIONAL { ?item wdt:P269 ?idRef }  
          OPTIONAL { ?item rdfs:label ?itemLabelFr . FILTER(LANG(?itemLabelFr) = "fr") . }
          OPTIONAL { ?item rdfs:label ?itemLabelEn . FILTER(LANG(?itemLabelEn) = "en") . }
        } GROUP BY ?item ?itemLabelFr ?itemLabelEn ?idPactols
     *
     */
    @Test
    public void importAlignmentJson() {
        String path = "/Users/Miled/Desktop/wikidata.json";
        InputStreamReader in;
        JsonArray jsonArray = null;
        
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource ds = connexionTest.getConnexionPool();

        try {
            in = new InputStreamReader(
                    new FileInputStream(path), "UTF8");

            JsonReader reader = Json.createReader(in);

            jsonArray = reader.readArray();
            //         JsonObject personObject = reader.readObject();

            reader.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ImportFromWikidataTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImportFromWikidataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (jsonArray == null) {
            return;
        }

        int countWikidata = 0;
        int countAAT = 0;
        int countIdRef = 0;
        int countBNF = 0;
        
        String uriWikidata;
        String idArk;
        String idAAT;
        String uriAAT;
        
        String idBNF;
        String uriBNF;   
        String idRef;
        String uriIdRef;           
        
        String idConcept;
        String idthesaurus = "TH_1";
        ConceptHelper conceptHelper = new ConceptHelper();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        JsonObject jsonObject;
        for (JsonValue jsonValue : jsonArray) {
            jsonObject = jsonValue.asJsonObject();
            uriWikidata = "";
            idArk = "";
            idAAT = "";
            uriAAT = "";
            
            idBNF = "";
            uriBNF = "";
            idRef = "";
            uriIdRef = "";             
            try {
                uriWikidata = jsonObject.getString("item");
                uriWikidata = uriWikidata.replaceAll("http://", "https://");
                //https://www.wikidata.org/wiki/Q7064
            } catch (Exception e) {
            }
            try {
                idArk = jsonObject.getString("idPactols");
                //pcrt8m83X5qJgW
            } catch (Exception e) {
            }
            try {
                idAAT = jsonObject.getString("idAAT");
                uriAAT = "http://vocab.getty.edu/page/aat/" + idAAT;
                //http://vocab.getty.edu/page/aat/300106326
                
            } catch (Exception e) {
            }
            try {
                idBNF = jsonObject.getString("idBNF");
                uriBNF = "https://catalogue.bnf.fr/ark:/12148/cb" + idBNF;
                //https://catalogue.bnf.fr/ark:/12148/cb11996115g
                
            } catch (Exception e) {
            }
            try {
                idRef = jsonObject.getString("idRef");
                uriIdRef = "https://www.idref.fr/" + idRef;
                //https://www.idref.fr/02874005X
                
            } catch (Exception e) {
            }             
            
            
            //System.out.println(uriWikidata + " ; " + idArk + " ; " + idAAT);
            if(idArk == null || idArk.isEmpty()) continue;
            if(uriWikidata == null || uriWikidata.isEmpty()) continue;
            
            idConcept = conceptHelper.getIdConceptFromArkId(ds, "26678/" + idArk);
            if(idConcept == null) continue;
            alignmentHelper.addNewAlignment(ds, 1, "", "Wikidata", uriWikidata, 1, idConcept, idthesaurus, 0);
            countWikidata = countWikidata + 1;
            
            if(uriAAT != null) {
                if(!uriAAT.isEmpty()) {
                    alignmentHelper.addNewAlignment(ds, 1, "", "AAT", uriAAT, 1, idConcept, idthesaurus, 0);
                    countAAT = countAAT + 1;
                }
            }
            if(uriBNF != null) {
                if(!uriBNF.isEmpty()) {
                    alignmentHelper.addNewAlignment(ds, 1, "", "BNF", uriBNF, 1, idConcept, idthesaurus, 0);
                    countBNF = countBNF + 1;
                }
            }
            if(uriIdRef != null) {
                if(!uriIdRef.isEmpty()) {            
                    alignmentHelper.addNewAlignment(ds, 1, "", "IdRef", uriIdRef, 1, idConcept, idthesaurus, 0);
                    countIdRef = countIdRef + 1;
                }
            }
        }
        System.out.println("count Wikidata =  " + countWikidata);
        System.out.println("count AAT =  " + countAAT);   
        System.out.println("count BNF =  " + countBNF); 
        System.out.println("count IdRef =  " + countIdRef);        
    }
}
