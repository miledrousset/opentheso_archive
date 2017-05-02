/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Quincy
 */
public class WriteRdfFileTest {
    
    public WriteRdfFileTest() {
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
    // @Test
    // public void hello() {}
    
    @Test
    public void startWrite() {
        
        InputStream is = null;
        try {
            is = new FileInputStream("testrdf.rdf");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       ReadRdf4j lect = new ReadRdf4j(is);
       
       
        
       WriteRdf4j writeRdf4j = new WriteRdf4j(lect.getsKOSXmlDocument());
       
       
       Rio.write(writeRdf4j.getModel(), System.out, RDFFormat.RDFXML);
       System.out.println("");
    
    }
    
    
    
    
}
