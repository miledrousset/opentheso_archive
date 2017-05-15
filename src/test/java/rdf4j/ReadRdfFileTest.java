/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Quincy
 */
public class ReadRdfFileTest {
    
    public ReadRdfFileTest() {
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
    public void startRead() {
        
        /*
        System.out.println("\n\n\n ---- TEST 1 ----");
        test1();
        System.out.println("-----------");
        
        System.out.println("\n\n\n ---- TEST 2 ----");
        test2();
        System.out.println("-----------");
        
        System.out.println("\n\n\n ---- TEST 3 ----");
        test3();
        System.out.println("\n-----------");
        
        System.out.println("\n\n\n ---- TEST 4 ----");
        test4();
        System.out.println("\n-----------");
        */
        
     /*   
        InputStream is = null;
        try {
            is = new FileInputStream("testrdf.rdf");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ReadRdf4j lect = new ReadRdf4j(is);
        System.out.println(lect);
       */ 

    }
    
    public  void test1(){
        
        ModelBuilder builder = new ModelBuilder();
        Model model = builder.setNamespace("ex", "http://example.org/")
                .subject("ex:PotatoEaters")
                    // this painting was created in April 1885
                    .add("ex:creationDate", new GregorianCalendar(1885, Calendar.APRIL, 1).getTime())
                    // it depicts 5 people
                    .add("ex:peopleDepicted", 5)
                .build();
        
        
        
        for (Statement st: model) {
            IRI property = st.getPredicate();
            Value value = st.getObject();
            if (value instanceof Literal) {
            Literal literal = (Literal)value;
            //System.out.println("datatype: " + literal.getDatatype());

            // get the value of the literal directly as a Java primitive.
            if (property.getLocalName().equals("peopleDepicted")) {
                int peopleDepicted = literal.intValue();
                System.out.println(peopleDepicted + " people are depicted in this painting");
            }
            else if (property.getLocalName().equals("creationDate")) {
                XMLGregorianCalendar calendar = literal.calendarValue();
                Date date = calendar.toGregorianCalendar().getTime();
                System.out.println("The painting was created on " + date);
            }

            // You can also just get the lexical value (a string) without
            // worrying about the datatype
            //System.out.println("Lexical value: '" + literal.getLabel() + "'");

            }
        }
        
    
    }
    
    public static void test2(){
        ValueFactory vf = SimpleValueFactory.getInstance();
        ModelBuilder builder = new ModelBuilder();
        Model model = builder
            .setNamespace("ex", "http://example.org/")
            .subject("ex:PotatoEaters")
                // In English, this painting is called "The Potato Eaters"
                .add(DC.TITLE, vf.createLiteral("The Potato Eaters", "en"))
                // In Dutch, it's called "De Aardappeleters"
                .add(DC.TITLE,  vf.createLiteral("De Aardappeleters", "nl"))
            .build();

        // To see what's in our model, let's just print it to the screen
        for(Statement st: model) {
            // we want to see the object values of each statement
            Value value = st.getObject();
            if (value instanceof Literal) {
                Literal title = (Literal)value;
                System.out.println("language: " + title.getLanguage().orElse("unknown"));
                System.out.println(" title: " + title.getLabel());
            }
        }
        

    
    }
    
    public static void test3(){
       // To create a blank node for the address, we need a ValueFactory
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
		.subject(address)			// switch the subject
		.add("ex:street", "31 Art Gallery")
		.add("ex:city", "Madrid")
		.add("ex:country", "Spain");

		Model model = builder.build();

	// Instead of simply printing the statements to the screen, we use a Rio writer to
	// write the model in RDF/XML syntax:
        
        Rio.write(model, System.out, RDFFormat.RDFXML);
        
        File file = new File("test.rdf");
        
        FileWriter writer = null;
        try {   
            file.createNewFile();
            writer = new FileWriter(file);        
        } catch (IOException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Rio.write(model, writer, RDFFormat.RDFXML);

        

    
    }
    
    @Test
    public  void test4(){
        
        // read the file 'example-data-artists.ttl' as an InputStream.
        InputStream is = null;
        
        try {
            is = new FileInputStream("test_unesco - Copie.rdf");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Model model = null;
        
        try {
            model = Rio.parse(is, "", RDFFormat.RDFXML);
            
            
        } catch (IOException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedRDFormatException ex) {
            Logger.getLogger(ReadRdfFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        for (Statement statement: model) {
            System.out.println(statement);
        }
        
        System.out.println("\n----------------");
        
        for(Statement st: model) {
            // we want to see the object values of each statement
            Value value = st.getObject();
            IRI property = st.getPredicate();

            if(property.getLocalName().equals("type"))
                    System.out.println("\n......\n");
            
            
            if(property.getLocalName().equals("notation")){
                 Literal title = (Literal)value;
                 System.out.println("notation: " + title.getLabel());
            }
            else if(property.getLocalName().equals("lat")){
                Literal title = (Literal)value;
                 System.out.println("latitude : " + title.getLabel());
            }
            else if(property.getLocalName().equals("long")){
                Literal title = (Literal)value;
                 System.out.println("longitude : " + title.getLabel());
            }
            else if(property.getLocalName().equals("created")){
                Literal title = (Literal)value;
                 System.out.println("created : " + title.getLabel());
            }
            else if(property.getLocalName().equals("modified")){
                Literal title = (Literal)value;
                 System.out.println("modified : " + title.getLabel());
            }
            else if (value instanceof Literal ) {
                
                String pref = "";
                
                if(property.getLocalName().equals("prefLabel")) pref = " // pref label";
                
                
                Literal title = (Literal)value;
                System.out.println("language: " + title.getLanguage().orElse("unknown"));
                System.out.println(" title: " + title.getLabel() + pref);
            }
            
            
            else
            {
                System.out.println("   ****  " + value + " //// " + property.getLocalName());
                if(property.getLocalName().equals("type")){
                    System.out.println("        URL: " + st.getSubject() );
                }
            }
            
            
            
        }

      
       Rio.write(model, System.out, RDFFormat.RDFXML);
        
        
        
        
        
        
    
    }
        
    
    
    
}
