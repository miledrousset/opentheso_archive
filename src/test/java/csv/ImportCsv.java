/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import mom.trd.opentheso.core.imports.csv.CsvReadHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author miled.rousset
 */
public class ImportCsv {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public ImportCsv() {
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

    @Test
    public void readFile() throws FileNotFoundException {
        try {
            //Reader in = new FileReader("/Users/Miled/Desktop/sample.csv");
            Reader in = new FileReader("/Users/Miled/Desktop/listNT.csv");
            
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            
            for (CSVRecord record : records) {
                String id = record.get("id");
                try {
                    String prefLabel_en = record.get("skos:prefLabel@es");
                } catch (Exception e) {
                    System.err.println("");
                }

                String prefLabel_fr = record.get("skos:prefLabel@fr");
                String altLabel_en = record.get("skos:altLabel@en");
                String altLabel_fr = record.get("skos:altLabel@fr");                
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ImportCsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void readFileTest() throws FileNotFoundException {
        CsvReadHelper csvHelper = new CsvReadHelper();
        String path = "/Users/Miled/Desktop/listNT.csv";
      //  csvHelper.readFile(path);
        ArrayList<CsvReadHelper.ConceptObject> conceptObjects = csvHelper.getConceptObjects();
        int i = 0;
    }    

}
