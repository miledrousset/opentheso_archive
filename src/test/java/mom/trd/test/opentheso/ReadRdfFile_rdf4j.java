/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miled.rousset
 */
public class ReadRdfFile_rdf4j {
    
    public ReadRdfFile_rdf4j() {
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
    public void readFile() {
        
        File file=new File("/Users/Miled/Desktop/Bureau2/test_unesco.rdf");
        
        
        RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
        Model model = new LinkedHashModel();
        rdfParser.setRDFHandler(new StatementCollector(model));

        Rio.write(model, System.out, RDFFormat.TURTLE);
        try {
           rdfParser.parse(new FileReader(file), "http://example.org");
     /*       for (Statement statement : model) {
                
        //        writeLine(statement.getObject().stringValue(), statement.getSubject().stringValue());

                System.out.println("LocalName = " + statement.getPredicate().getLocalName());

                System.out.println("objet = " + statement.getObject().stringValue());
                System.out.println("predicat = " + statement.getPredicate());
                System.out.println("URI = " + statement.getSubject());
                System.out.println("");
               
           //     model.getNamespace(statement.getClass().getgetObject().stringValue());

            } */
            for (Statement statement2 : model) {
                Literal literal = (SimpleLiteral)statement2;
                System.out.println(literal.getLabel());
   //             System.out.println(literal.getLanguage());
                System.out.println(literal.getDatatype());
            }
        }
        
        

        
        catch (IOException e) {
          // handle IO problems (e.g. the file could not be read)
        }
        catch (RDFParseException e) {
          // handle unrecoverable parse error
        }
        catch (RDFHandlerException e) {
          // handle a problem encountered by the RDFHandler
        }        
    }
    
    private String getPropertie(String uri) {
        if(!uri.contains("#")) return null;
        return uri.substring(uri.indexOf("#")+1, uri.length());    
    }
    
    private void writeLine (String prefix, String uri) {
        
        String value = getPropertie(prefix);
        if(value != null) {
            switch (value) {

                case "ConceptScheme":

                  //  System.out.println("ConceptScheme : " + uri);
                    break;
                case "Collection":
                    System.out.println("Collection : " + uri);
                    break;
                default:
                    break;
            }
        }

    }    
    
  
}
