/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author miled.rousset
 */
public class CsvReadHelper {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private String message = "";
    ArrayList<String> langs;

    private final ArrayList<ConceptObject> conceptObjects;

    public CsvReadHelper() {
        conceptObjects = new ArrayList<>();
    }

    public boolean setLangs(Reader in){
        langs = new ArrayList<>();
        try {
            Map<String, Integer> headers = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in).getHeaderMap();
            String values[];
            for (String columnName : headers.keySet()) {
                if(columnName.contains("@")) {
                    values = columnName.split("@");
                    if(values[1] != null)
                        if(!langs.contains(values[1]))
                            langs.add(values[1]);//columnName.substring(columnName.indexOf("@"), columnName.indexOf("@" +2)));
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CsvReadHelper.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return !langs.isEmpty(); 
    }
    
    public boolean readFile(Reader in){ //String path)  {
        try {
            //Reader in = new FileReader("/Users/Miled/Desktop/sample.csv");
            // Reader in = new FileReader(path);

            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
           
            Label label;
            ArrayList<Label> labels;

            for (CSVRecord record : records) {
                ConceptObject conceptObject = new ConceptObject();
                ArrayList<Label> prefLabels = new ArrayList<>();
                ArrayList<Label> altLabels = new ArrayList<>();
                ArrayList<Label> definitions = new ArrayList<>();                 
                
                // setId, si l'identifiant n'est pas renseigné, on récupère un NULL 
                conceptObject.setId(getId(record));
                
                // prefLabel
                for (String idLang : langs) {
                    label = getPrefLabel(record, idLang);
                    if(label != null)
                        prefLabels.add(label);
                }

                // altLabel
                for (String idLang : langs) {
                    labels = getAltLabel(record, idLang);
                    if(labels != null){
                        for (Label altLabel : labels) {
                            altLabels.add(altLabel);
                        }
                    }                    
                }
                
                //definition
                for (String idLang : langs) {
                    labels = getDefinition(record, idLang);
                    if(labels != null){
                        for (Label definition : labels) {
                            definitions.add(definition);
                        }
                    }                    
                }                

                conceptObject.setPrefLabels(prefLabels);
                conceptObject.setAltLabels(altLabels);
                conceptObject.setDefinition(definitions);
                conceptObjects.add(conceptObject);
            }
            return true;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CsvReadHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private String getId(CSVRecord record) {
        // set the Id
        try {
            return record.get("id");
        } catch (Exception e) {
            System.err.println("");
        }
        return null;
    }

    private Label getPrefLabel(CSVRecord record, String idLang) {
        try {
            String value = record.get("skos:prefLabel@" + idLang.trim());
            Label label = new Label();
            label.setLabel(value);
            label.setLang(idLang);
            return label;
        } catch (Exception e) {
            System.err.println("");
        }
        return null;
    }
    
    private ArrayList<Label> getAltLabel(CSVRecord record, String idLang){
        try {
            String value = record.get("skos:altLabel@" + idLang.trim());
            String values[] = value.split("##");
            ArrayList<Label> labels = new ArrayList<>();
            for (String value1 : values) {
                Label label = new Label();
                label.setLabel(value1);
                label.setLang(idLang);
                labels.add(label);
            }
            return labels;
        } catch (Exception e) {
            System.err.println("");
        }   
        return null;
    }
    
    private ArrayList<Label> getDefinition(CSVRecord record, String idLang){
        try {
            String value = record.get("skos:definition@" + idLang.trim());
            String values[] = value.split("##");
            ArrayList<Label> labels = new ArrayList<>();
            for (String value1 : values) {
                Label label = new Label();
                label.setLabel(value1);
                label.setLang(idLang);
                labels.add(label);
            }
            return labels;
        } catch (Exception e) {
            System.err.println("");
        }   
        return null;
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
        private ArrayList<Label> prefLabels;
        private ArrayList<Label> altLabels;
        private ArrayList<Label> definition;        

        public ConceptObject() {
            prefLabels = new ArrayList<>();
            altLabels = new ArrayList<>(); 
            definition = new ArrayList<>();
        }

        public ArrayList<Label> getDefinition() {
            return definition;
        }

        public void setDefinition(ArrayList<Label> definition) {
            this.definition = definition;
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
