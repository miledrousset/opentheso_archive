/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
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
        /*
        InputStream is = null;
        try {
            is = new FileInputStream("test_unesco.rdf");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        ReadRdf4j lect = new ReadRdf4j(is);

        WriteRdf4j writeRdf4j = new WriteRdf4j(lect.getsKOSXmlDocument());

        Rio.write(writeRdf4j.getModel(), System.out, RDFFormat.RDFXML);
        System.out.println("");
         */
    }

    @Test
    public void write1() {

        /*      // To create a blank node for the address, we need a ValueFactory
        ValueFactory vf = SimpleValueFactory.getInstance();
        BNode address = vf.createBNode();

        // Identically to example 03, we create a model with some data
        ModelBuilder builder = new ModelBuilder();
        builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:Picasso")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.FIRST_NAME, "Pablo")
                .add("ex:homeAddress", address) // link the blank node
                .subject(address) // switch the subject
                .add("ex:street", "31 Art Gallery")
                .add("ex:city", "Madrid")
                .add("ex:country", "Spain");

        Model model = builder.build();

        // Instead of simply printing the statements to the screen, we use a Rio writer to
        // write the model in RDF/XML syntax:
        Rio.write(model, System.out, RDFFormat.RDFXML);
         */
    }

    @Test
    public void write2() {

        ValueFactory vf = SimpleValueFactory.getInstance();

        // Create a new RDF model containing information about the painting "The Potato Eaters"
        ModelBuilder builder = new ModelBuilder();
        Model model = builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:PotatoEaters")
                // this painting was created on April 1, 1885
                .add("ex:creationDate", vf.createLiteral("1885-04-01T00:00:00Z", XMLSchema.DATETIME))
                // You can also pass in a Java Date object directly: 
                //.add("ex:creationDate", new GregorianCalendar(1885, Calendar.APRIL, 1).getTime())

                // the painting shows 5 people
                .add("ex:peopleDepicted", 5)
                .build();

        // To see what's in our model, let's just print stuff to the screen
        for (Statement st : model) {
            // we want to see the object values of each property
            IRI property = st.getPredicate();
            Value value = st.getObject();
            if (value instanceof Literal) {
                Literal literal = (Literal) value;
                System.out.println("datatype: " + literal.getDatatype());

                // get the value of the literal directly as a Java primitive.
                if (property.getLocalName().equals("peopleDepicted")) {
                    int peopleDepicted = literal.intValue();
                    System.out.println(peopleDepicted + " people are depicted in this painting");
                } else if (property.getLocalName().equals("creationDate")) {
                    XMLGregorianCalendar calendar = literal.calendarValue();
                    Date date = calendar.toGregorianCalendar().getTime();
                    System.out.println("The painting was created on " + date);
                }

                // you can also just get the lexical value (a string) without worrying about the datatype
                System.out.println("Lexical value: '" + literal.getLabel() + "'");
            }
        }
        Rio.write(model, System.out, RDFFormat.RDFXML);
    }

    @Test
    public void write3() {

        ValueFactory vf = SimpleValueFactory.getInstance();
        BNode address = vf.createBNode();

        // First we do the same thing we did in example 02: create a new ModelBuilder, and add
        // two statements about Picasso.
        ModelBuilder builder = new ModelBuilder();
        builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:Picasso")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.FIRST_NAME, "Pablo")
                // this is where it becomes new: we add the address by linking the blank node
                // to picasso via the `ex:homeAddress` property, and then adding facts _about_ the address
                .add("ex:homeAddress", address) // link the blank node
                .subject(address) // switch the subject
                .add("ex:street", "31 Art Gallery")
                .add("ex:city", "Madrid")
                .add("ex:country", "Spain");

        Model model = builder.build();

        // To see what's in our model, let's just print it to the screen
        for (Statement st : model) {
            System.out.println(st);
        }
        Rio.write(model, System.out, RDFFormat.RDFXML);
    }

    @Test
    public void write4() {

        // We'll use a ModelBuilder to create two named graphs, one containing data about
        // picasso, the other about Van Gogh.
        ModelBuilder builder = new ModelBuilder();
        builder.setNamespace("ex", "http://example.org/");

        // in named graph 1, we add info about Picasso
        builder.namedGraph("ex:namedGraph1")
                .subject("ex:Picasso")
                .add(RDF.TYPE, "type1")
                .add(FOAF.FIRST_NAME, "Pablo");

        // in named graph2, we add info about Van Gogh.
        builder.namedGraph("ex:namedGraph2")
                .subject("ex:VanGogh")
                .add(RDF.TYPE, "type2")
                .add(FOAF.FIRST_NAME, "Vincent");

        // We're done building, create our Model
        Model model = builder.build();

        // each named graph is stored as a separate context in our Model
        for (Resource context : model.contexts()) {
            System.out.println("Named graph " + context + " contains: ");

            // write _only_ the statemements in the current named graph to the console, in N-Triples format
            Rio.write(model.filter(null, null, null, context), System.out, RDFFormat.NTRIPLES);
            System.out.println();
        }
        Rio.write(model, System.out, RDFFormat.RDFXML);
    }

    @Test
    public void writeRio(){ 
    
        FileOutputStream out = null;
    try {
        Graph myGraph = null; // a collection of several RDF statements
            out = new FileOutputStream("test_unesco2.rdf");
            try {
                RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
                writer.startRDF();
                for (Statement st: myGraph) {
                    writer.handleStatement(st);
                }
                writer.endRDF();
            }
            catch (RDFHandlerException e) {
                // oh no, do something!
            }
    }
    catch (FileNotFoundException ex) {
            Logger.getLogger(WriteRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
     // oh no, do something!
    } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(WriteRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
