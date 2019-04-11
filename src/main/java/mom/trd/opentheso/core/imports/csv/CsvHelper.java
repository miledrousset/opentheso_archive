/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author miled.rousset
 */
public class CsvHelper {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private String message = "";

    private final ArrayList<ConceptObject> conceptObjects;

    public CsvHelper() {
        conceptObjects = new ArrayList<>();
    }

    public boolean readFile(Reader in){ //String path)  {
        try {
            //Reader in = new FileReader("/Users/Miled/Desktop/sample.csv");
            // Reader in = new FileReader(path);
            String value;
            String values[];
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

            for (CSVRecord record : records) {
                ConceptObject conceptObject = new ConceptObject();
                ArrayList<Label> prefLabels = new ArrayList<>();
                ArrayList<Label> altLabels = new ArrayList<>();                
                
                
                // get the Id
                try {
                    conceptObject.setId(record.get("id"));
                } catch (Exception e) {
                    System.err.println("");
                }
                
                // get the oldId
                try {
                    conceptObject.setOldId(record.get("oldId"));
                } catch (Exception e) {
                    System.err.println("");
                }    
                // get the oldId2
                try {
                    conceptObject.setOldId2(record.get("oldId2"));
                } catch (Exception e) {
                    System.err.println("");
                }  
                // get the oldId3
                try {
                    conceptObject.setOldId3(record.get("oldId3"));
                } catch (Exception e) {
                    System.err.println("");
                }                
                
                // prefLabels fr
                try {
                    value = record.get("skos:prefLabel@fr");
                    Label label = new Label();
                    label.setLabel(value);
                    label.setLang("fr");
                    prefLabels.add(label);
                } catch (Exception e) {
                    System.err.println("");
                }
                
                // prefLabels en
                try {
                    value = record.get("skos:prefLabel@en");
                    Label label = new Label();
                    label.setLabel(value);
                    label.setLang("en");
                    prefLabels.add(label);
                } catch (Exception e) {
                    System.err.println("");
                }
                // prefLabels es
                try {
                    value = record.get("skos:prefLabel@es");
                    Label label = new Label();
                    label.setLabel(value);
                    label.setLang("es");
                    prefLabels.add(label);
                } catch (Exception e) {
                    System.err.println("");
                }
                // prefLabels de
                try {
                    value = record.get("skos:prefLabel@de");
                    Label label = new Label();
                    label.setLabel(value);
                    label.setLang("de");
                    prefLabels.add(label);
                } catch (Exception e) {
                    System.err.println("");
                }
                // prefLabels it
                try {
                    value = record.get("skos:prefLabel@it");
                    Label label = new Label();
                    label.setLabel(value);
                    label.setLang("it");
                    prefLabels.add(label);
                } catch (Exception e) {
                    System.err.println("");
                }
                // prefLabels pt
                try {
                    value = record.get("skos:prefLabel@pt");
                    Label label = new Label();
                    label.setLabel(value);
                    label.setLang("pt");
                    prefLabels.add(label);
                } catch (Exception e) {
                    System.err.println("");
                }                
                
                
                
                
                // altLabels fr
                try {
                    value = record.get("skos:altLabel@fr");
                    values = value.split("##");
                    for (String value1 : values) {
                        Label label = new Label();
                        label.setLabel(value1);
                        label.setLang("fr");
                        altLabels.add(label);
                    }
                } catch (Exception e) {
                    System.err.println("");
                }
                // altLabels en
                try {
                    value = record.get("skos:altLabel@en");
                    values = value.split("##");
                    for (String value1 : values) {
                        Label label = new Label();
                        label.setLabel(value1);
                        label.setLang("en");
                        altLabels.add(label);
                    }
                } catch (Exception e) {
                    System.err.println("");
                } 
                // altLabels es
                try {
                    value = record.get("skos:altLabel@es");
                    values = value.split("##");
                    for (String value1 : values) {
                        Label label = new Label();
                        label.setLabel(value1);
                        label.setLang("es");
                        altLabels.add(label);
                    }
                } catch (Exception e) {
                    System.err.println("");
                }                
                // altLabels de
                try {
                    value = record.get("skos:altLabel@de");
                    values = value.split("##");
                    for (String value1 : values) {
                        Label label = new Label();
                        label.setLabel(value1);
                        label.setLang("de");
                        altLabels.add(label);
                    }
                } catch (Exception e) {
                    System.err.println("");
                }                 
                // altLabels it
                try {
                    value = record.get("skos:altLabel@it");
                    values = value.split("##");
                    for (String value1 : values) {
                        Label label = new Label();
                        label.setLabel(value1);
                        label.setLang("it");
                        altLabels.add(label);
                    }
                } catch (Exception e) {
                    System.err.println("");
                }
                conceptObject.setPrefLabels(prefLabels);
                conceptObject.setAltLabels(altLabels);
                conceptObjects.add(conceptObject);
            }
            return true;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CsvHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ConceptObject> getConceptObjects() {
        return conceptObjects;
    }

    
    
    public class ConceptObject {
        private String id;
        private String oldId;
        private String oldId2;
        private String oldId3;        
        private ArrayList<Label> prefLabels;
        private ArrayList<Label> altLabels;

        public ConceptObject() {
            prefLabels = new ArrayList<>();
            altLabels = new ArrayList<>(); 
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ArrayList<Label> getPrefLabels() {
            return prefLabels;
        }

        public void setPrefLabels(ArrayList<Label> prefLabels) {
            this.prefLabels = prefLabels;
        }

        public ArrayList<Label> getAltLabels() {
            return altLabels;
        }

        public void setAltLabels(ArrayList<Label> altLabels) {
            this.altLabels = altLabels;
        }

        public String getOldId() {
            return oldId;
        }

        public void setOldId(String oldId) {
            this.oldId = oldId;
        }

        public String getOldId2() {
            return oldId2;
        }

        public void setOldId2(String oldId2) {
            this.oldId2 = oldId2;
        }

        public String getOldId3() {
            return oldId3;
        }

        public void setOldId3(String oldId3) {
            this.oldId3 = oldId3;
        }
        
    }
    

    public class Label {

        private String label;
        private String lang;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

    }

}
