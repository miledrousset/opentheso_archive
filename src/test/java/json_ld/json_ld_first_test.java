/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json_ld;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Quincy
 */
public class json_ld_first_test {

  

    @Test
    public void start_test() throws FileNotFoundException, IOException, JsonLdError {
        
        
        System.out.println("");
        System.out.println("");
        
        System.out.println("*******************************************************");
        System.out.println("*                     JSON LD                         *");
        System.out.println("*******************************************************");
        
        InputStream inputStream = new FileInputStream("input_test.json");
        // Read the file into an Object (The type of this object will be a List, Map, String, Boolean,
        // Number or null depending on the root object in the file).
        Object jsonObject = JsonUtils.fromInputStream(inputStream);
        // Create a context JSON map containing prefixes and definitions
        Map context = new HashMap();
        // Customise context...
        // Create an instance of JsonLdOptions with the standard JSON-LD options
        JsonLdOptions options = new JsonLdOptions();
        // Customise options...
        // Call whichever JSONLD function you want! (e.g. compact)
        Object compact = JsonLdProcessor.compact(jsonObject, context, options);
        // Print out the result (or don't, it's your call!)
        System.out.println(JsonUtils.toPrettyString(compact));
        
        
        
        System.out.println("*******************************************************");
        System.out.println("*                     org.JSON                        *");
        System.out.println("*******************************************************");
        
        inputStream = new FileInputStream("input_test.json");
        JSONObject obj = new JSONObject(IOUtils.toString(inputStream));
        
        System.out.println(obj.getString("name")); 
        
        obj.put("test", "writeIsWorking");

        System.out.println(obj.getString("test"));
        System.out.println("");
        
        System.out.println(obj.toString());
        System.out.println("");
        
        
        System.out.println("*******************************************************");
        System.out.println("*                     RDJF WRITE                      *");
        System.out.println("*******************************************************");
        
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
        
        Rio.write(model, System.out, RDFFormat.JSONLD);
        System.out.println("");
        System.out.println("");
        
        
        System.out.println("*******************************************************");
        System.out.println("*                     RDJF READ                       *");
        System.out.println("*******************************************************");
        
        inputStream = new FileInputStream("input_test.json");
        model = Rio.parse(inputStream, "", RDFFormat.JSONLD);
        Rio.write(model, System.out, RDFFormat.JSONLD);
        System.out.println("");
        for(Statement st: model) {
            // we want to see the object values of each statement
            Value value = st.getObject();
            IRI property = st.getPredicate();
    
            if(property.getLocalName().equals("name")){
                Literal title = (Literal)value;
                System.out.println("name: " + title.getLabel());
                
            }
        }
        
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        
    }

}
