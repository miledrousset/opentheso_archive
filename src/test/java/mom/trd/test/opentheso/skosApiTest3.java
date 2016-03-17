/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mom.trd.test.opentheso;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFXMLParserFactory;




/**
 *
 * @author miled.rousset
 */
public class skosApiTest3 {
    
    public skosApiTest3() {
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
    public void testReadSkos() {
        
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            m.getOntologyParsers().set(new RDFXMLParserFactory());
            OWLOntology ont = m.loadOntologyFromOntologyDocument(IRI.create("file:/Users/Miled/Desktop/unescothes.rdf"));
            OWLNamedIndividual ind = m.getOWLDataFactory().getOWLNamedIndividual(IRI.create("http://skos.um.es/unescothes/COL205"));
          
            

            
            Set<? extends OWLAxiom> axioms = ont.getAxioms(ind);

            
            for (OWLAxiom ax : axioms) {
                System.out.println("ReadSKOSExample.ReadSKOSExample() " + ax);
            }
            axioms = ont.getAnnotationAssertionAxioms(ind.getIRI());
            for (OWLAxiom ax : axioms) {
                System.out.println("ReadSKOSExample.ReadSKOSExample() " + ax);
            }
            axioms = ont.getDataPropertyAssertionAxioms(ind);
            for (OWLAxiom ax : axioms) {
                System.out.println("ReadSKOSExample.ReadSKOSExample() " + ax);
            }
            axioms = ont.getObjectPropertyAssertionAxioms(ind);
            for (OWLAxiom ax : axioms) {
                System.out.println("ReadSKOSExample.ReadSKOSExample() " + ax);
            }
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(skosApiTest3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
