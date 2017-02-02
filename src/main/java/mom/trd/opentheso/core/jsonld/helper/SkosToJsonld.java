/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.jsonld.helper;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.tools.StringPlus;
import skos.SKOSConceptScheme;
import skos.SKOSDate;
import skos.SKOSDocumentation;
import skos.SKOSLabel;
import skos.SKOSMapping;
import skos.SKOSProperty;
import skos.SKOSRelation;
import skos.SKOSResource;
import skos.SKOSTopConcept;
import skos.SKOSXmlDocument;

/**
 *
 * @author miled.rousset
 */
public class SkosToJsonld {

    private static final String nameSpaceSkosCore = "http://www.w3.org/2004/02/skos/core#";
    private static final String nameSpaceDcTerms = "http://purl.org/dc/terms/";
    
    private StringBuffer jsonLd; 
    
    public SkosToJsonld() {
    }

    /**
     * à compléter plus tard pour produire du JsonLd sous forme de
     * Classe JsonLdDocument
     * @param skosDocument
     * @return 
     */
/*    private JsonLdDocument setJsonLdDocument(SKOSXmlDocument skosDocument) {
        JsonLdDocument jsonLdDocument = new JsonLdDocument();
        ArrayList<JsonConcept> jsonConcepts = new ArrayList<>();
        
        // boucle pour tous les concepts
        for (SKOSResource skosConcept : skosDocument.getResourcesList()) {
            startConcept();
            // add this concept to Json
            JsonConcept jsonConcept = new JsonConcept();
            jsonConcept.setId(skosConcept.getUri());
            jsonConcept.setNameSpace(nameSpaceSkosCore + "Concept");
            jsonConcepts.add(jsonConcept);
            

        }
        
        
        
        return jsonLdDocument;
    }*/

   /**
     *  Cette fonction permet de transformer un document Skos de type ConceptScheme en JsonLd
     * @param skosDocument
     * @return StringBuffer
     */
    public StringBuffer getJsonLdConceptScheme(SKOSXmlDocument skosDocument) {
        
        if(skosDocument == null) return null;
        
        if(skosDocument.getConceptScheme().getTopConceptsList().isEmpty())
            return emptyJson();
        
        jsonLd = new StringBuffer();
        
        startJson();
        startConcept();
        // boucle pour les Groupes (ConceptScheme)
        SKOSConceptScheme sKOSConceptScheme = skosDocument.getConceptScheme();
        if(sKOSConceptScheme == null) return null;
        
        // add this uri to Json
        addIdConcept(sKOSConceptScheme.getUri());
        
        // add labels
        if(!sKOSConceptScheme.getSkosLabels().isEmpty()) {
            addLabels(sKOSConceptScheme.getSkosLabels());
        }
        
        if(!sKOSConceptScheme.getTopConceptsList().isEmpty()){
            addElementInScheme(sKOSConceptScheme.getTopConceptsList(),
                    "hasTopConcept");
        }
        
        endLastConcept();
        endJson();
        
        return jsonLd;
    }
        
    /**
     *  Cette fonction permet de transformer un document Skos en JsonLd
     * @param skosDocument
     * @return StringBuffer
     */
    public StringBuffer getJsonLdDocument(SKOSXmlDocument skosDocument) {
        
        if(skosDocument == null) return null;
        if(skosDocument.getResourcesList().isEmpty())
            return emptyJson();
        
        jsonLd = new StringBuffer();
        
        startJson();
        
        boolean first = true;
        
        // boucle pour tous les concepts
        for (SKOSResource skosConcept : skosDocument.getResourcesList()) {
            if(!first) {
                endConcept();
            }
            startConcept();

            // add this concept to Json
            addIdConcept(skosConcept.getUri());

            // add dates
            if(!skosConcept.getDateList().isEmpty()) {
                addDates(skosConcept.getDateList());
            }
            
            // add Identifier / second
            if(skosConcept.getSdc() !=null){
                if(!skosConcept.getSdc().getIdentifier().isEmpty()) {
                    addIdentifier(skosConcept.getSdc().getIdentifier());
                }
            }
            
            // add documentations
            if(!skosConcept.getDocumentationsList().isEmpty()){
                addDocumentations(skosConcept.getDocumentationsList());
            }             
            
            // add labels
            if(!skosConcept.getLabelsList().isEmpty()) {
                addLabels(skosConcept.getLabelsList());
            }
            
            // add relations
            if(!skosConcept.getRelationsList().isEmpty()){
                addRelations(skosConcept.getRelationsList());
            }
            
            // add mappings
            if(!skosConcept.getMappings().isEmpty()){
                addMappings(skosConcept.getMappings());
            }
        //    endConcept();
         //   endElement();
            first = false;
        }
        endLastConcept();
        endJson();
        
        return jsonLd;
    }
    
    private StringBuffer emptyJson() {
        /*StringBuffer empty = new StringBuffer("{\n" +
                "  \"@graph\":  [\n" +
                "    {\n" +                
                "    }\n" +
                "  ]\n" +
                "}");
        */
        StringBuffer empty = new StringBuffer("{}");
        return empty;
    }
 
    private void addIdConcept(String uri){
        String conceptId;
        StringPlus stringPlus = new StringPlus();
        uri = stringPlus.normalizeStringForXml(uri);
        conceptId = "      \"@id\": \"" + uri + "\",\n";
        conceptId += "      \"@type\": \"" + nameSpaceSkosCore + "Concept" + "\"";
        
        jsonLd.append(conceptId);
    }
    
    /**
     * Ajout des dates à Json
     * @param dates 
     */
    private void addDates(ArrayList<SKOSDate> dates) {
        String created;
        String modified;
        
        for (SKOSDate date : dates) {
            switch (date.getProperty()) {
                case SKOSProperty.created:
                    endElement();
                    created = "      \"" + nameSpaceDcTerms + "created" + "\": ";
                    created += "\"" + date.getDate() + "\"";
                    jsonLd.append(created);
                    break;
                case SKOSProperty.modified:
                    endElement();
                    modified = "      \"" + nameSpaceDcTerms + "modified" + "\": ";
                    modified += "\"" + date.getDate() + "\"";
                    jsonLd.append(modified);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Ajout des dates à Json
     * @param dates 
     */
    private void addIdentifier(String identifier) {
        String id;
        endElement();
        id = "      \"" + nameSpaceDcTerms + "identifier" + "\": ";
        id += "\"" + identifier + "\"";
        jsonLd.append(id);
    }    
    
    private void addLabels(ArrayList<SKOSLabel> sKOSLabels) {
        ArrayList <SKOSLabel> prefLabel = new ArrayList<>();
        ArrayList <SKOSLabel> altLabel = new ArrayList<>();        
        StringPlus stringPlus = new StringPlus();
        
        for (SKOSLabel sKOSLabel : sKOSLabels) {
            switch (sKOSLabel.getProperty()) {
                case SKOSProperty.prefLabel:
                    prefLabel.add(sKOSLabel);
                    break;
               case SKOSProperty.altLabel:
                    altLabel.add(sKOSLabel);
                    break;    
                default:
                    break;
            }
        }

        // prefLabls
        String prefLabelString;
        if(!prefLabel.isEmpty()) {
            if(prefLabel.size() > 1) {
                endElement();
                prefLabelString = "      \"" + nameSpaceSkosCore + "prefLabel" + "\": [\n";
                boolean first = true;
                for (SKOSLabel prefLabel1 : prefLabel) {
                    if(!first)
                        prefLabelString += ",\n"; 
                    prefLabelString += "        {\n";
                    prefLabelString += "          \"@language\": \"" + prefLabel1.getLanguage() + "\",\n";
                    prefLabelString += "          \"@value\": \"" + stringPlus.normalizeStringForXml(prefLabel1.getLabel()) + "\"\n";
                    prefLabelString += "        }";
                    first = false;
                }
                prefLabelString += "\n      ]";
            } 
            else {
                endElement();
                prefLabelString = "      \"" + nameSpaceSkosCore + "prefLabel" + "\": {\n";
                for (SKOSLabel prefLabel1 : prefLabel) {
                    prefLabelString += "        \"@language\": \"" + prefLabel1.getLanguage() + "\",\n";
                    prefLabelString += "        \"@value\": \"" + stringPlus.normalizeStringForXml(prefLabel1.getLabel()) + "\"\n";            
                    prefLabelString += "      }";
                }
            }
            jsonLd.append(prefLabelString);
        }
        
        // altLables
        String altLabelString; 
        if(!altLabel.isEmpty()) {
            if(altLabel.size() > 1) {
                endElement();
                altLabelString = "      \"" + nameSpaceSkosCore + "altLabel" + "\": [\n";
                boolean first = true;
                for (SKOSLabel altLabel1 : altLabel) {
                    if(!first)
                        altLabelString += ",\n"; 
                    altLabelString += "        {\n";
                    altLabelString += "          \"@language\": \"" + altLabel1.getLanguage() + "\",\n";
                    altLabelString += "          \"@value\": \"" + stringPlus.normalizeStringForXml(altLabel1.getLabel()) + "\"\n";
                    altLabelString += "        }";
                    first = false;
                }
                altLabelString += "\n      ]";
            } 
            else {
                endElement();
                altLabelString = "      \"" + nameSpaceSkosCore + "altLabel" + "\": {\n";
                for (SKOSLabel altLabel1 : altLabel) {
                    altLabelString += "        \"@language\": \"" + altLabel1.getLanguage() + "\",\n";
                    altLabelString += "        \"@value\": \"" + stringPlus.normalizeStringForXml(altLabel1.getLabel()) + "\"\n";            
                    altLabelString += "      }";
                }
            }
            jsonLd.append(altLabelString);
        }
    }
    
    private void addRelations(ArrayList<SKOSRelation> relations) {
        ArrayList <SKOSRelation> narrower = new ArrayList<>();
        ArrayList <SKOSRelation> related = new ArrayList<>();
        ArrayList <SKOSRelation> broader = new ArrayList<>();
        ArrayList <SKOSRelation> inScheme = new ArrayList<>();
        
        for (SKOSRelation relation : relations) {
            switch (relation.getProperty()) {
                case SKOSProperty.broader:
                    broader.add(relation);
                    break;                  
                case SKOSProperty.narrower:
                    narrower.add(relation);
                    break;
                case SKOSProperty.related:
                    related.add(relation);
                    break;
                case SKOSProperty.ConceptScheme:
                    inScheme.add(relation);
                    break;                      
                default:
                    break;
            }
        }
        
        // broader
        if(!broader.isEmpty()) {
            addElementRelation(broader, "broader");
        }
        
        // narrower
        if(!narrower.isEmpty()) {
            addElementRelation(narrower, "narrower");
        }
        
        // related
        if(!related.isEmpty()) {
            addElementRelation(related, "related");
        }
        
        // ConceptScheme
        if(!inScheme.isEmpty()) {
            addElementRelation(inScheme, "inScheme");
        }
    }
    
    private void addDocumentations(ArrayList<SKOSDocumentation> documentations) {
        ArrayList <SKOSDocumentation> changeNote = new ArrayList<>();
        ArrayList <SKOSDocumentation> definition = new ArrayList<>();
        ArrayList <SKOSDocumentation> editorialNote = new ArrayList<>();   
        ArrayList <SKOSDocumentation> example = new ArrayList<>();
        ArrayList <SKOSDocumentation> historyNote = new ArrayList<>();
        ArrayList <SKOSDocumentation> note = new ArrayList<>();
        ArrayList <SKOSDocumentation> scopeNote = new ArrayList<>();   
        
        for (SKOSDocumentation sKOSDocumentation : documentations) {
            switch (sKOSDocumentation.getProperty()) {
                case SKOSProperty.changeNote:
                    changeNote.add(sKOSDocumentation);
                    break;
                case SKOSProperty.definition:
                    definition.add(sKOSDocumentation);
                    break;
                case SKOSProperty.editorialNote:
                    editorialNote.add(sKOSDocumentation);
                    break;
                case SKOSProperty.example:
                    example.add(sKOSDocumentation);
                    break;
                case SKOSProperty.historyNote:
                    historyNote.add(sKOSDocumentation);
                    break;                    
                case SKOSProperty.note:
                    note.add(sKOSDocumentation);
                    break;
                case SKOSProperty.scopeNote:
                    scopeNote.add(sKOSDocumentation);
                    break;
                default:
                    break;
            }
        }
        
        addNote(changeNote, "changeNote");
        addNote(definition, "definition");
        addNote(editorialNote, "editorialNote");
        addNote(example, "example");
        addNote(historyNote, "historyNote");
        addNote(note, "note");
        addNote(scopeNote, "scopeNote");        
    }
    
    private void addNote(ArrayList <SKOSDocumentation> note, String noteType){
        
        StringPlus stringPlus = new StringPlus();
        String labelString;
        if(!note.isEmpty()) {
            if(note.size() > 1) {
                endElement();
                labelString = "      \"" + nameSpaceSkosCore + noteType + "\": [\n";
                boolean first = true;
                for (SKOSDocumentation sKOSDocumentation : note) {
                    if(!first)
                        labelString += ",\n"; 
                    labelString += "        {\n";
                    labelString += "          \"@language\": \"" + sKOSDocumentation.getLanguage() + "\",\n";
                    labelString += "          \"@value\": \"" + stringPlus.normalizeStringForXml(sKOSDocumentation.getText()) + "\"\n";
                    labelString += "        }";
                    first = false;
                }
                labelString += "\n      ]";
            } 
            else {
                endElement();
                labelString = "      \"" + nameSpaceSkosCore + noteType + "\": {\n";
                for (SKOSDocumentation sKOSDocumentation : note) {
                    labelString += "        \"@language\": \"" + sKOSDocumentation.getLanguage() + "\",\n";
                    labelString += "        \"@value\": \"" + stringPlus.normalizeStringForXml(sKOSDocumentation.getText()) + "\"\n";            
                    labelString += "      }";
                }
            }
            jsonLd.append(labelString);
        }         
    }
    
    private void addMappings(ArrayList<SKOSMapping> mappings) {
        ArrayList <SKOSMapping> closeMatch = new ArrayList<>();
        ArrayList <SKOSMapping> exactMatch = new ArrayList<>();        
        
        for (SKOSMapping mapping : mappings) {
            switch (mapping.getProperty()) {
                case SKOSProperty.closeMatch:
                    closeMatch.add(mapping);
                    break;
                case SKOSProperty.exactMatch:
                    exactMatch.add(mapping);
                    break;                    
                default:
                    break;
            }
        }

        // closeMatch
        if(!closeMatch.isEmpty()) {
            addElementMapping(closeMatch, "closeMatch");
        } 
        
        // exactMatch
        if(!exactMatch.isEmpty()) {
            addElementMapping(exactMatch, "exactMatch");
        } 
    }
    
    private void addElementRelation(ArrayList <SKOSRelation> skosRelation, String nameSpace) {
        String element;
        String uri;
        StringPlus stringPlus = new StringPlus();

        if(skosRelation.size() > 1) {
            endElement();

            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": [\n";
            boolean first = true; 
            for (SKOSRelation skosRelation1 : skosRelation) {
                if(!first)
                    element += "        },\n";
                element += "        {\n";
                uri = stringPlus.normalizeStringForXml(skosRelation1.getTargetUri());
                element += "          \"@id\": \"" + uri + "\"\n";
                first = false;
            }
            element += "        }\n";
            element += "      ]";
        } 
        else {
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
            for (SKOSRelation skosRelation1 : skosRelation) {
                uri = stringPlus.normalizeStringForXml(skosRelation1.getTargetUri());
                element += "        \"@id\": \"" + uri + "\"\n";
                element += "      }";
            }
        }
        jsonLd.append(element);
    }
    
    private void addElementInScheme(ArrayList <SKOSTopConcept> sKOSTopConcepts, String nameSpace) {
        String element;
        String uri;
        StringPlus stringPlus = new StringPlus();
        
        if(sKOSTopConcepts.size() > 1) {
            endElement();

            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": [\n";
            boolean first = true; 
            for (SKOSTopConcept sKOSTopConcept : sKOSTopConcepts) {
                if(!first)
                    element += "        },\n";
                element += "        {\n";
                uri = stringPlus.normalizeStringForXml(sKOSTopConcept.getTopConcept());
                element += "          \"@id\": \"" + uri + "\"\n";
                first = false;
            }
            element += "        }\n";
            element += "      ]";
        } 
        else {
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
            for (SKOSTopConcept sKOSTopConcept : sKOSTopConcepts) {
                uri = stringPlus.normalizeStringForXml(sKOSTopConcept.getTopConcept());
                element += "        \"@id\": \"" + uri + "\"\n";
                element += "      }";
            }
        }
        jsonLd.append(element);
    }    
    
    private void addElementMapping(ArrayList <SKOSMapping> skosRelation, String nameSpace) {
        String element;
        String uri;
        StringPlus stringPlus = new StringPlus();
        
        if(skosRelation.size() > 1) {
            boolean first = true;
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": [\n";
            for (SKOSMapping skosRelation1 : skosRelation) {
                if(!first)
                    element += "        },\n";
                element += "        {\n";
                uri = stringPlus.normalizeStringForXml(skosRelation1.getTargetUri());
                element += "          \"@id\": \"" + uri + "\"\n";
                first = false;
            }
            element += "        }\n";
            element += "      ]";
        } 
        else {
            endElement();
            element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
            for (SKOSMapping skosRelation1 : skosRelation) {
                uri = stringPlus.normalizeStringForXml(skosRelation1.getTargetUri());
                element += "        \"@id\": \"" + uri + "\"\n";
                element += "      }";
            }
        }
        jsonLd.append(element);
    }
    
    private void addElementDocumentation(SKOSDocumentation sKOSDocumentation, String nameSpace) {
        StringPlus stringPlus = new StringPlus();
        String element = "      \"" + nameSpaceSkosCore + nameSpace + "\": {\n";
        element += "        \"@language\": \"" + sKOSDocumentation.getLanguage() + "\",\n";
        element += "        \"@value\": \"" + stringPlus.normalizeStringForXml(sKOSDocumentation.getText()) + "\"\n";        
        element += "      }";
        jsonLd.append(element);
    }
    
    private void startConcept() {
        jsonLd.append("    {\n");
    }

    private void endConcept() {
        jsonLd.append("\n    },\n");
    }
    
    private void endElement() {
        jsonLd.append(",\n");
    }
    
    private void endLastConcept() {
        jsonLd.append("\n    }\n");
    }
    
    private void startJson() {
        jsonLd.append("{\n");
        jsonLd.append("  \"@graph\":  [\n");
    }
    
    private void endJson() {
        jsonLd.append("  ]\n");
        jsonLd.append("}\n");
    }    
}
