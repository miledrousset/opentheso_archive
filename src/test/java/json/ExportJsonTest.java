/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import mom.trd.opentheso.core.exports.rdf4j.helper.ExportRdf4jHelper;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author miled.rousset
 */
public class ExportJsonTest {
    JsonArrayBuilder jsonArrayBuilder;    
    public ExportJsonTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void getJson() {
        String idConcept1 = "14767";
        String idConcept2 = "36825";        
        String idTheso = "TH_1";
        int type = 3;

        RDFFormat format = null;
        String extention;

        switch (type) {
            case 0:
                format = RDFFormat.RDFXML;
                extention = "_skos.xml";
                break;
            case 1:
                format = RDFFormat.JSONLD;
                extention = "_json-ld.json";
                break;
            case 2:
                format = RDFFormat.TURTLE;
                extention = "_turtle.ttl";
                break;
            case 3:
                format = RDFFormat.RDFJSON;
                extention = "_json.json";
                break;
        }
        HikariDataSource ds;
        ConnexionTest connexionTest = new ConnexionTest();
        ds = connexionTest.getConnexionPool();
        
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        
        if(nodePreference != null){
            ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
            exportRdf4jHelper.setNodePreference(nodePreference);
            exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso,nodePreference.getCheminSite());
            exportRdf4jHelper.setNodePreference(nodePreference);
            exportRdf4jHelper.addSignleConcept(idTheso, idConcept1);
            exportRdf4jHelper.addSignleConcept(idTheso, idConcept2);
            WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());

            ByteArrayOutputStream out;
            out = new ByteArrayOutputStream();
            Rio.write(writeRdf4j.getModel(), out, format);
            System.out.println(out.toString());
            //file = new ByteArrayContent(out.toByteArray(), "application/xml", idTheso + " " + extention);
        }
        ds.close();
    }
     
    @Test
    public void exportConceptIntoJson() {
        String idThesaurus = "TH_1";
        String idGroup = "2";
        String idLang = "fr";
        
        NodeConcept nodeConcept;

        jsonArrayBuilder = Json.createArrayBuilder();
                
        HikariDataSource ds;
        ConnexionTest connexionTest = new ConnexionTest();
        ds = connexionTest.getConnexionPool();
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> listConcept =  conceptHelper.getAllIdConceptOfThesaurusByGroup(ds, idThesaurus, idGroup);
        
        for (String idConcept : listConcept) {
            nodeConcept = conceptHelper.getConcept(ds, idConcept, idThesaurus, idLang);
            addJsonData(nodeConcept);
        }
        System.out.println(jsonArrayBuilder.build().toString());
    }
    
     private void addJsonData(NodeConcept nodeConcept) {
        JsonObjectBuilder builder = Json.createObjectBuilder();        

        // les infos principales
        builder.add("_id", nodeConcept.getConcept().getIdConcept());
        builder.add("created", nodeConcept.getConcept().getCreated().toString());
        builder.add("modified", nodeConcept.getConcept().getCreated().toString());

        
        
        // pour le tableau de prefLabel 
        JsonArrayBuilder jsonArrayBuilderPrefLabel = Json.createArrayBuilder();
        JsonObjectBuilder prefLabel = Json.createObjectBuilder();
        prefLabel.add("language", nodeConcept.getTerm().getLang());
        prefLabel.add("value", nodeConcept.getTerm().getLexical_value());
        jsonArrayBuilderPrefLabel.add(prefLabel.build());

        for (NodeTermTraduction nodeTermTraduction : nodeConcept.getNodeTermTraductions()) {
            prefLabel.add("language", nodeTermTraduction.getLang());
            prefLabel.add("value", nodeTermTraduction.getLexicalValue());
            jsonArrayBuilderPrefLabel.add(prefLabel.build());
        }
        builder.add("prefLabel", jsonArrayBuilderPrefLabel.build());
        
        
        // pour le tableau de altLabel 
        JsonArrayBuilder jsonArrayBuilderAltLabel = Json.createArrayBuilder();
        JsonObjectBuilder altLabel = Json.createObjectBuilder();
        for (NodeEM nodeEM : nodeConcept.getNodeEM()) {
            altLabel.add("language", nodeEM.getLang());
            altLabel.add("value", nodeEM.getLexical_value());
            jsonArrayBuilderAltLabel.add(altLabel.build());
        }
        builder.add("altLabel", jsonArrayBuilderAltLabel.build());        
        
        // pour le tableau des notes 
        JsonArrayBuilder jsonArrayBuilderNotes = Json.createArrayBuilder();
        JsonObjectBuilder notes = Json.createObjectBuilder();
        for (NodeEM nodeEM : nodeConcept.getNodeEM()) {
            notes.add("language", nodeEM.getLang());
            notes.add("value", nodeEM.getLexical_value());
            jsonArrayBuilderNotes.add(notes.build());
        }
        builder.add("definition", jsonArrayBuilderNotes.build()); 
        jsonArrayBuilder.add(builder.build());
    }    
}
