/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignement.wikidata;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.bordercloud.sparql.Endpoint;
import com.bordercloud.sparql.EndpointException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author miled.rousset
 */
public class WikidataTest {

    public WikidataTest() {
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

    
    
    // récupération des URI qui parlent du concept avec l'entité codé exp : http://www.wikidata.org/entity/Q324926 pour fibule
    
//    SELECT ?item ?itemLabel WHERE {
//        ?item rdfs:label "fibule"@fr.
//        SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],fr". }.
//      }
    
    @Test
    public void searchValue() {
        try {
            Endpoint sp = new Endpoint("https://query.wikidata.org/sparql", false);

            String querySelect = "SELECT ?item ?itemLabel ?itemDescription WHERE {\n" +
                                    "  ?item rdfs:label \"fibula\"@fr.\n" +
                                    "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],fr\". }\n" +
                                    "}";

            HashMap rs = sp.query(querySelect);

            HashMap<String, HashMap> rs3_queryPopulationInFrance = sp.query(querySelect);

            ArrayList<HashMap<String, Object>> rows_queryPopulationInFrance = (ArrayList) rs3_queryPopulationInFrance.get("result").get("rows");
            for (HashMap<String, Object> hashMap : rows_queryPopulationInFrance) {
                System.out.println("URI : " + hashMap.get("item"));
                System.out.println("URI : " + hashMap.get("itemLabel"));
                System.out.println("URI : " + hashMap.get("itemDescription"));
            }

        //    printResult(rs, 30);

        } catch (EndpointException eex) {
            System.out.println(eex);
            eex.printStackTrace();
        }
    }

/*    public void printResult(HashMap rs , int size) {

      for (Object variable : (ArrayList) rs.get("result").get("variables")) {
        System.out.print(String.format("%-"+size+"."+size+"s", variable ) + " | ");
      }
      System.out.print("\n");
      for (HashMap value : (ArrayList>) rs.get("result").get("rows")) {
        //System.out.print(value);
        /* for (String key : value.keySet()) {
         System.out.println(value.get(key));
         }*/
/*        for (String variable : (ArrayList) rs.get("result").get("variables")) {
          //System.out.println(value.get(variable));
          System.out.print(String.format("%-"+size+"."+size+"s", value.get(variable)) + " | ");
        }
        System.out.print("\n");
      }
    }*/

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
