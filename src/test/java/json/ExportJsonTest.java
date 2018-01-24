/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.ByteArrayOutputStream;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
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
        
        
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        
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
}
