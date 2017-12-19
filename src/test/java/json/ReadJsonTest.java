/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author miled.rousset
 */
public class ReadJsonTest {

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
 /*   @Test
    public void readJson1() {
        /*
        String personJSONData
                = "  {"
                + "   \"name\": \"Jack\", "
                + "   \"age\" : 13, "
                + "   \"isMarried\" : false, "
                + "   \"address\": { "
                + "     \"street\": \"#1234, Main Street\", "
                + "     \"zipCode\": \"123456\" "
                + "   }, "
                + "   \"phoneNumbers\": [\"011-111-1111\", \"11-111-1111\"] "
                + " }";

        JsonReader reader = Json.createReader(new StringReader(personJSONData));

        JsonObject personObject = reader.readObject();

        reader.close();

        System.out.println("Name   : " + personObject.getString("name"));
        System.out.println("Age    : " + personObject.getInt("age"));
        System.out.println("Married: " + personObject.getBoolean("isMarried"));

        JsonObject addressObject = personObject.getJsonObject("address");
        System.out.println("Address: ");
        System.out.println(addressObject.getString("street"));
        System.out.println(addressObject.getString("zipCode"));

        System.out.println("Phone  : ");
        JsonArray phoneNumbersArray = personObject.getJsonArray("phoneNumbers");
        for (JsonValue jsonValue : phoneNumbersArray) {
            System.out.println(jsonValue.toString());
        }
        
         */

 //   }*/

 /*   @Test
    private void readJson2() {

        String total = " {\"content\":[{\"nb_notices\":\"7\"}],\"debug\":\"\",\"error\":0}\" ";

        JsonReader reader = Json.createReader(new StringReader(total));

        JsonObject personObject = reader.readObject();

        reader.close();

        JsonArray values = personObject.getJsonArray("content");

        for (int i = 0; i < values.size(); i++) {
            try {
                JsonObject item = values.getJsonObject(i);

                String name = item.getString("nb_notices");
                int nb = Integer.parseInt(name);
                System.out.println("Total des notices = " + nb);
            } catch (JsonException e) {
                System.out.println(e.toString());
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }

        /* for (JsonObject jsonValue : values) {
            System.out.println(jsonValue.toString());
        }*/
 /*     JsonArray personArray = reader.readArray();
       
        for (JsonValue jsonVal : personArray) {
            System.out.println(jsonVal.getValueType() + " - "
                    + ((JsonObject) jsonVal).getString("nb_notices"));
        }      
         */
   // }*/

    
    @Test
    public void getJsonData() {
        String urlResource = "http://test.mom.fr";
        JsonObjectBuilder builder = Json.createObjectBuilder();
    
        builder.add("index", "1");
        builder.add("type", "URL");

        // Objet dans l'Objet
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("format", "string");
        job.add("value", urlResource);

        builder.add("data", job.build());
        
        builder.add("ttl", "86400");
        builder.add("permissions", "1110");
        String test = builder.build().toString();
        System.err.println(test);
    }
}
