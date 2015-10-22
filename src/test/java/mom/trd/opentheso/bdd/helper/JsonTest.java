/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.imports.old.ReadFileSKOS;
import mom.trd.opentheso.core.jsonld.helper.JsonHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import skos.SKOSXmlDocument;
//import com.google.gson.*;
//import com.google.gson.JsonElement;

/**
 *
 * @author miled.rousset
 */
public class JsonTest {
    
    public JsonTest() {
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
    public void hello() {
        
        //gson test
    /*    String jsonString = "{" + "a" + ":" + "b" + "}";//"{\"a\":\"b\"}"; 
        JsonElement response = new JsonParser().parse(jsonString); 
        System.out.println(jsonString);
        System.out.println(response.toString());
   */ }
    
/*    @Test
    public void testWriteJsonLd() throws IOException, FileNotFoundException, JsonLdError{
    //    Jsonld_test writeJson = new Jsonld_test();
    //    writeJson.test("/Users/Miled/Google Drive/Projets/OpenTheso/Jsonld/exemple/exp1_compressed.jsonld");
        
    }
    
    @Test
    public void testWriteRdfToJsonLd() throws IOException, FileNotFoundException, JsonLdError{
   //     Jsonld_test writeJson = new Jsonld_test();
   //     writeJson.testRDF();//test("/Users/Miled/Google Drive/Projets/OpenTheso/Jsonld/exemple/exp1.jsonld");
        
    }
    */
    
    @Test
    public void testReadSkosBuffer() {
        SKOSXmlDocument sKOSXmlDocument;
        try {
            ReadFileSKOS readFileSKOS = new ReadFileSKOS();
            
            StringBuffer skos = new StringBuffer();
            
            
            
            
            skos.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<rdf:RDF\n" +
"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
"    xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"\n" +
"    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
"    xmlns:dcterms=\"http://purl.org/dc/terms/\">\n" +
"    <skos:Concept rdf:about=\"http://ark.frantiq.fr/ark:/26678/pcrttq9gp2ZMuc\">\n" +
"        <skos:prefLabel xml:lang=\"en\">art</skos:prefLabel>\n" +
"        <skos:prefLabel xml:lang=\"fr\">art</skos:prefLabel>\n" +
"        <skos:prefLabel xml:lang=\"es\">arte</skos:prefLabel>\n" +
"        <skos:prefLabel xml:lang=\"it\">arte</skos:prefLabel>\n" +
"        <skos:prefLabel xml:lang=\"nl\">kunst</skos:prefLabel>\n" +
"        <skos:prefLabel xml:lang=\"de\">Kunst</skos:prefLabel>\n" +
"        <skos:prefLabel xml:lang=\"ar\">فن</skos:prefLabel>\n" +
"        <skos:altLabel xml:lang=\"fr\">Arts</skos:altLabel>\n" +
"        <skos:altLabel xml:lang=\"en\">Arts_en</skos:altLabel>\n" +
"        <skos:inScheme rdf:resource=\"http://pactols.frantiq.fr/?idd=6&amp;idt=TH_1\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtWSbu1fKhxG\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtQ2d2FOaxN5\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrt7SWBi5qQSl\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtWbThvTYYf4\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtTv2xhrEwPW\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtET0l280TUS\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtkOgxvd4Ijy\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrt8e84y5qvKb\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtWSqJyedQws\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtgI8DSt7eam\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtvsFBSmOKN2\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrt4rR2kyMQuW\"/>\n" +
"        <skos:narrower rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtGKMelLKwRd\"/>\n" +
"        <skos:related rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtP6goqQWM9k\"/>\n" +
"        <skos:related rdf:resource=\"http://ark.frantiq.fr/ark:/26678/pcrtb4M5pvMjvr\"/>\n" +
"        <skos:definition xml:lang=\"fr\">Ensemble des oeuvres artistiques d'un pays, d'une époque (Lar.)</skos:definition>\n" +
"        <dcterms:created>2007-02-08</dcterms:created>\n" +
"        <dcterms:modified>2014-04-11</dcterms:modified>\n" +
"        <skos:closeMatch rdf:resource=\"http://www.eionet.europa.eu/gemet/concept/568 \"/>\n" +
"        <skos:closeMatch rdf:resource=\"http://eurovoc.europa.eu/2688\"/>\n" +
"        <skos:closeMatch rdf:resource=\"http://en.wikipedia.org/wiki/Art\"/>\n" +
"    </skos:Concept>\n"+
"</rdf:RDF>");
            
            
           
            
            sKOSXmlDocument = readFileSKOS.readStringBuffer(skos);
            
            JsonHelper jsonHelper = new JsonHelper();
        //    StringBuffer jsonLd = jsonHelper.getJsonLdForSchemaOrg(sKOSXmlDocument);
        
            StringBuffer jsonLd = jsonHelper.getJsonLdForSchemaOrgForConceptScheme(sKOSXmlDocument);
            
            String toto = "";
            
        } catch (Exception ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        
    }    
 
    @Test
    public void testRestService() {
    /*    String correctFilePath = "src/main/resources/hikari.properties";
        try {
            File file = new File(correctFilePath);
            initConfig(file);
        } catch (IOException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
            */
    }
    
    
    protected static void initConfig(File file) throws IOException {
        try {
                if (!file.exists()) {
                        throw new FileNotFoundException("File doesn't exist: "
                                        + file.getAbsolutePath());
                }
                FileInputStream fis = new FileInputStream(file);
                initConfig(fis);
        } catch (IOException ioe) {
                throw ioe;
        }
    }
    
    protected static void initConfig(InputStream is) throws IOException {
        Properties properties = new Properties();
        properties.load(is);
        
        
    }
}
