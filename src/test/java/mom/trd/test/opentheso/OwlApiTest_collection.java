/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import java.io.File;
import org.semanticweb.skos.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;
import java.util.HashSet;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
//import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFXMLParserFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 *
 * @author miled.rousset
 */
public class OwlApiTest_collection {

    public OwlApiTest_collection() {
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

        // récupération d'un identifiant unique
        /*    ToolsHelper toolsHelper = new ToolsHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        String id = toolsHelper.getNewId(10);
        for (int i = 0; i < 10; i++) {
            id = toolsHelper.getNewId(10);
        }
         */
 /*        try {

            SKOSManager man = new SKOSManager();
//
            SKOSDataset dataSet = man.loadDatasetFromPhysicalURI(URI.create("file:/Users/Miled/Desktop/Bureau2/test_unesco.rdf"));


            //////

            // print out all concepts;
            for (SKOSConcept concept : dataSet.getSKOSConcepts()) {
                System.out.println("Concept: " + concept.getURI());
                // get the narrower concepts
                for (SKOSEntity narrowerConcepts : concept.getSKOSRelatedEntitiesByProperty(dataSet, man.getSKOSDataFactory().getSKOSNarrowerProperty())) {
                    System.err.println("\t hasNarrower: " + narrowerConcepts.getURI());
                }
                // get the broader concepts
                for (SKOSEntity broaderConcepts : concept.getSKOSRelatedEntitiesByProperty(dataSet, man.getSKOSDataFactory().getSKOSBroaderProperty())) {
                    System.err.println("\t hasBroader: " + broaderConcepts.getURI());
                }
                
                    for (SKOSAnnotation anno : concept.getSKOSAnnotations(dataSet)) {
                        System.err.print("\t\tAnnotation: " + anno.getURI() + "-> ");
                        if (anno.isAnnotationByConstant()) {
                            if (anno.getAnnotationValueAsConstant().isTyped()) {
                                SKOSTypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                                System.err.print(con.getLiteral() + " Type: " + con.getDataType().getURI());
                            }
                            else {
                                SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                                System.err.print(con.getLiteral());
                                if (con.hasLang()) {
                                    System.err.print("@" + con.getLang());
                                }
                            }
                            System.err.println("");
                        }
                        else {
                            System.err.println(anno.getAnnotationValue().getURI().toString());
                        }
                    }                

            }



            System.out.println("");
            System.out.println("---------------------");
            System.out.println("");
            System.out.println("Ontology loaded!");

            for (SKOSConceptScheme scheme : dataSet.getSKOSConceptSchemes()) {

                System.out.println("ConceptScheme: " + scheme.getURI());

                // i can get all the concepts from this scheme
                for (SKOSConcept conceptsInScheme : dataSet.getSKOSConcepts()) {

                    System.err.println("\tConcepts: " + conceptsInScheme.getURI());

                    for (SKOSAnnotation anno : conceptsInScheme.getSKOSAnnotations(dataSet)) {
                        System.err.print("\t\tAnnotation: " + anno.getURI() + "-> ");
                        if (anno.isAnnotationByConstant()) {
                            if (anno.getAnnotationValueAsConstant().isTyped()) {
                                SKOSTypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                                System.err.print(con.getLiteral() + " Type: " + con.getDataType().getURI());
                            }
                            else {
                                SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                                System.err.print(con.getLiteral());
                                if (con.hasLang()) {
                                    System.err.print("@" + con.getLang());
                                }
                            }
                            System.err.println("");
                        }
                        else {
                            System.err.println(anno.getAnnotationValue().getURI().toString());
                        }
                    }

                }

            }
             for (SKOSConceptScheme scheme : dataSet.getSKOSConceptSchemes()) {
                 
             }

//
//                for (SKOSConcept con : scheme.getTopConcepts(vocab)) {
//
//                    System.out.println("Top Concept: " + con.getURI());
//
//                }
//
//                int counter = 0;
//
//                for (SKOSConcept con : scheme.getConceptsInScheme(vocab)) {
//                    counter++;
//                    System.out.println("Concept: " + con.getURI().getFragment());
//
//                    for (OWLUntypedConstant type : con.getSKOSPrefLabel(vocab)) {
//                        System.out.println("PrefLabel: " + type.getLiteral() + " lang: " + type.getLang());
//                    }
//
//                    for (OWLUntypedConstant type : con.getSKOSAltLabel(vocab)) {
//                        System.out.println("AltLabel: " + type.getLiteral() + " lang: " + type.getLang());
//                    }
//
//                    for (SKOSConcept concepts : con.getSKOSBroaderConcepts(vocab)) {
//                        System.out.println("\tHas Broader: " + concepts.getURI().getFragment());
//                    }
//
//                }
//                System.out.println("Count: " + counter);
//            }
//
//
//            // see if we can get and find an entity
//
//            SKOSEntity entity = vocab.getSKOSEntity("conker");
//            System.out.println("Entity lookup for Nose " + entity.asSKOSConcept().getAsOWLIndividual().getURI());
//
////            vocab.getAssertions();
////            vocab.conatinsSKOSConcept();
////            vocab.getSKOSObjectRelationAssertions();
////            vocab.getSKOSBroaderAssertions();
////            vocab.getSKOSNarrowerAssertions();
////            vocab.getSKOSRelatedAssertions();
//
//
        } catch (SKOSCreationException e) {
            e.printStackTrace();
        }
//
         */
    }

    @Test
    public void testReadOWL() {

  /*      try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            m.getOntologyParsers().set(new RDFXMLParserFactory());

            //    OWLOntology ont = m.loadOntologyFromOntologyDocument(new File("/Users/Miled/Desktop/Bureau2/test_unesco.rdf"));
            OWLOntology baseOnt = m
                    .loadOntologyFromOntologyDocument(new File("/Users/Miled/Desktop/Bureau2/test_unesco.rdf"));
            Set<OWLAxiom> axioms = baseOnt.getAxioms();

            /*    for (OWLAxiom ax : axioms) {
                System.out.println("ReadSKOSExample.ReadSKOSExample() " + ax);
            }
             */
 /*           printAxioms(axioms);
            Set<OWLClass> classes = baseOnt.getClassesInSignature();
            printClasses(classes);
            printLogicalAxioms(baseOnt.getLogicalAxioms());

            /*             Iterator<OWLClass> iterator =  classes.iterator();
                while (iterator.hasNext()) {
                    OWLClass next = iterator.next();
                    if(next.isClassExpressionLiteral()) {
                        if(next.getIRI().toString().contains("Collection")) {
                             System.out.println("collection ici");
                                for (OWLAxiom ax : axioms) {
                                    System.out.println("ReadSKOSExample.ReadSKOSExample() " + ax);
                                }
                        }
                        if(next.getIRI().toString().contains("ConceptScheme")) {
                             System.out.println("Scheme ici");
                        }
                        if(next.getIRI().toString().contains("Concept")) {
                             System.out.println("Concept ici");
                        }                       
                    }
                    System.out.println(next);
                }
             */

 /*
            
            OWLNamedIndividual ind = m.getOWLDataFactory().getOWLNamedIndividual(IRI.create(
                    "http://skos.um.es/unescothes/COL001"));

            
            
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
             */
  /*      } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(OwlApiTest_collection.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    private static void printLogicalAxioms(Set<OWLLogicalAxiom> logicalAxioms) {
        System.out.println("ALL LOGICAL AXIOMS (" + logicalAxioms.size() + ")");

    }

    private static void printClasses(Set<OWLClass> classes) {
        System.out.println("ALL CLASSES (" + classes.size() + ")");
        for (OWLClass c : classes) {
            System.out.println(c.toString());
        }
        System.out.println("-----------------------------------");
    }

    private static void printAxioms(Set<OWLAxiom> axioms) {

        Set<OWLAxiom> axIndividual = new HashSet<OWLAxiom>();
        Set<OWLAxiom> axDataProperty = new HashSet<OWLAxiom>();
        Set<OWLAxiom> axObjectProperty = new HashSet<OWLAxiom>();
        Set<OWLAxiom> axClass = new HashSet<OWLAxiom>();
        Set<OWLAxiom> axOther = new HashSet<OWLAxiom>();

        for (OWLAxiom a : axioms) {
            a.getSignature();
            if ((a instanceof OWLClassAxiom)) {
                axClass.add(a);
            } else if (a instanceof OWLDataPropertyAxiom) {
                axDataProperty.add(a);
            } else if (a instanceof OWLObjectPropertyAxiom) {
                axDataProperty.add(a);
            } else if (a instanceof OWLIndividualAxiom) {
                axIndividual.add(a);
            } else {
                axOther.add(a);
            }
        }

        System.out.println("ALL AXIOMS (" + axioms.size() + ")");
        for (OWLAxiom ax : axIndividual) {
            String line1;
            line1 = ax.toString() + " TYPE: Individual";

            if(line1.contains("Collection")) {
                System.out.println(line1);
                for (OWLAxiom ax2 : axOther) {
                    String line2;
                    line2 = ax2.toString() + " TYPE: collection";
                    System.out.println(line2);
                }
                for (OWLAxiom ax1 : axDataProperty) {
                String line;
                line = ax1.toString() + " TYPE: DataProperty";
                System.out.println(line);
                }
                for (OWLAxiom ax2 : axObjectProperty) {
                    String line;
                    line = ax2.toString() + " TYPE: ObjectProperty";
                    System.out.println(line);
                }
                for (OWLAxiom ax3 : axClass) {
                    String line;
                    line = ax3.toString() + " TYPE: Class";
                    System.out.println(line);
                }
                for (OWLAxiom ax4 : axOther) {
                    String line;
                    line = ax4.toString() + " TYPE: Other";
                    System.out.println(line);
                }
            }
        /*    else
            {
                System.out.println(line1);
                for (OWLAxiom ax2 : axOther) {
                    String line2;
                    line2 = ax2.toString() + " TYPE: Other";
                    System.out.println(line2);
                } 
                for (OWLAxiom ax1 : axDataProperty) {
                String line;
                line = ax1.toString() + " TYPE: DataProperty";
                System.out.println(line);
                }
                for (OWLAxiom ax2 : axObjectProperty) {
                    String line;
                    line = ax2.toString() + " TYPE: ObjectProperty";
                    System.out.println(line);
                }
                for (OWLAxiom ax3 : axClass) {
                    String line;
                    line = ax3.toString() + " TYPE: Class";
                    System.out.println(line);
                }
                for (OWLAxiom ax4 : axOther) {
                    String line;
                    line = ax4.toString() + " TYPE: Other";
                    System.out.println(line);
                }                
            }*/
        }
        for (OWLAxiom ax : axDataProperty) {
            String line;
            line = ax.toString() + " TYPE: DataProperty";
            System.out.println(line);
        }
        for (OWLAxiom ax : axObjectProperty) {
            String line;
            line = ax.toString() + " TYPE: ObjectProperty";
            System.out.println(line);
        }
        for (OWLAxiom ax : axClass) {
            String line;
            line = ax.toString() + " TYPE: Class";
            System.out.println(line);
        }
        for (OWLAxiom ax : axOther) {
            String line;
            line = ax.toString() + " TYPE: Other";
            System.out.println(line);
        }
        System.out.println("-----------------------------------");

    }

    /*
    
    public void shouldLoad() throws OWLOntologyCreationException {
         // Get hold of an ontology manager
         OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
         // Let's load an ontology from the web
         IRI iri = IRI
                 .create("http://owl.cs.manchester.ac.uk/co-ode-files/ontologies/pizza.owl");
         OWLOntology pizzaOntology = manager
                 .loadOntologyFromOntologyDocument(iri);
         System.out.println("Loaded ontology: " + pizzaOntology);
         // Remove the ontology so that we can load a local copy.
         manager.removeOntology(pizzaOntology);
         // We can also load ontologies from files. Download the pizza ontology
         // from http://owl.cs.manchester.ac.uk/co-ode-files/ontologies/pizza.owl
         // and put it
         // somewhere on your hard drive Create a file object that points to the
         // local copy
         File file = new File("/tmp/pizza.owl");
         // Now load the local copy
         OWLOntology localPizza = manager.loadOntologyFromOntologyDocument(file);
         System.out.println("Loaded ontology: " + localPizza);
         // We can always obtain the location where an ontology was loaded from
         IRI documentIRI = manager.getOntologyDocumentIRI(localPizza);
         System.out.println("    from: " + documentIRI);
         // Remove the ontology again so we can reload it later
         manager.removeOntology(pizzaOntology);
         // In cases where a local copy of one of more ontologies is used, an
         // ontology IRI mapper can be used to provide a redirection mechanism.
         // This means that ontologies can be loaded as if they were located on
         // the web. In this example, we simply redirect the loading from
         // http://owl.cs.manchester.ac.uk/co-ode-files/ontologies/pizza.owl to
         // our local copy
         // above.
         manager.addIRIMapper(new SimpleIRIMapper(iri, IRI.create(file)));
         // Load the ontology as if we were loading it from the web (from its
         // ontology IRI)
         IRI pizzaOntologyIRI = IRI
                 .create("http://owl.cs.manchester.ac.uk/co-ode-files/ontologies/pizza.owl");
         OWLOntology redirectedPizza = manager.loadOntology(pizzaOntologyIRI);
         System.out.println("Loaded ontology: " + redirectedPizza);
         System.out.println("    from: "
                 + manager.getOntologyDocumentIRI(redirectedPizza));
         // Note that when imports are loaded an ontology manager will be
         // searched for mappings
    }    
     */
}
