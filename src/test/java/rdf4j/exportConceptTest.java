/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdf4j;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.ByteArrayOutputStream;
import java.util.List;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import mom.trd.opentheso.core.exports.rdf4j.ExportRdf4jHelper;
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
public class exportConceptTest {
    
    public exportConceptTest() {
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
    public void hello() {}
    
    private WriteRdf4j loadExportHelper(String idTheso, String idArk) {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        
        
        ConceptHelper conceptHelper = new ConceptHelper();

        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(conn, idTheso);
        if(nodePreference == null) return null;
        
        String idConcept = conceptHelper.getIdConceptFromArkId(conn, idArk);
        
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(conn, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        conn.close();
        return writeRdf4j;
    }

    @Test
    public void exportConcept() {
        String idTheso = "TH_1";
        String idArk = "26678/pcrtj2nMTJw5fg";
        String format = "text/turtle";

        RDFFormat rDFFormat = null;
        String extention = "";

        switch (format) {
            case "application/rdf+xml":
                rDFFormat = RDFFormat.RDFXML;
                extention = ".rdf";
                break;
            case "application/ld+json":
                rDFFormat = RDFFormat.JSONLD;
                extention = ".jsonld";
                break;
            case "text/turtle":
                rDFFormat = RDFFormat.TURTLE;
                extention = ".ttl";
                break;
            case "application/json":
                rDFFormat = RDFFormat.RDFJSON;
                extention = ".json";
                break;                
        }

        WriteRdf4j writeRdf4j = loadExportHelper(idTheso, idArk);
        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, rDFFormat);
        System.out.println(out.toString());
//        file = new ByteArrayContent(out.toByteArray(), "application/xml", idTheso + " " + extention);
    }    
    
}
