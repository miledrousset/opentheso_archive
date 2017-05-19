/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.rdf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.skosapi.SKOSProperty;
import mom.trd.opentheso.skosapi.SKOSResource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;

/**
 *
 * @author Quincy
 *
 * Classe qui permet le chargement et la lecture de fichier RDF
 */
public class ReadRdf4j {
    
    private static Model model;
    private SKOSXmlDocument sKOSXmlDocument;

    /**
     * constucteur
     */
    /**
     * Chargement des données rdf d'après un InputStream
     *
     * @param is
     * @param type 0 pour skos 1 pour jsonld 2 pour turtle
     */
    public ReadRdf4j(InputStream is, int type) {
        sKOSXmlDocument = new SKOSXmlDocument();
        
        if (type == 0) {
            laodSkosModel(is);
        } else if (type == 1) {
            laodJsonLdModel(is);
        } else {
            laodTurtleModel(is);
        }
        
        readModel();
    }

    /**
     * Cette fonction permet de charger un fichier RDF puis le parcourir avec
     * RIO (une API RDF4J)
     *
     * @param is
     */
    private void laodSkosModel(InputStream is) {
        
        model = null;
        try {
            model = Rio.parse(is, "", RDFFormat.RDFXML);
        } catch (IOException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedRDFormatException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void laodJsonLdModel(InputStream is) {
        
        model = null;
        try {
            model = Rio.parse(is, "", RDFFormat.JSONLD);
        } catch (IOException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedRDFormatException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void laodTurtleModel(InputStream is) {
        
        model = null;
        try {
            model = Rio.parse(is, "", RDFFormat.TURTLE);
        } catch (IOException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedRDFormatException ex) {
            Logger.getLogger(ReadRdf4j.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * structure qui contiens des informations pour la lecture de fichier RDF
     */
    private class ReadStruct {

        Value value;
        IRI property;
        Literal literal;
        SKOSResource resource;
    }

    /**
     * Permet de lire un fichier RDF précédament charger avec la fonction
     * laodModel() les données sont stoqué dans la variable thesaurus
     */
    private void readModel() {
        
        ReadStruct readStruct = new ReadStruct();
        readStruct.resource = null;

        //pour le debug : 
        ArrayList nonReco = new ArrayList();
        
        for (Statement st : model) {
            
            readStruct.value = st.getObject();
            readStruct.property = st.getPredicate();
            if (readStruct.value instanceof Literal) {
                readStruct.literal = (Literal) readStruct.value;
            }

            //Concept or ConceptScheme or Collection or ConceptGroup
            if (readStruct.property.getLocalName().equals("type")) {
                int prop = -1;                
                String type = readStruct.value.toString();
                type = type.toUpperCase();
                if (type.contains("ConceptScheme".toUpperCase())) {
                    prop = SKOSProperty.ConceptScheme;
                    sKOSXmlDocument.setTitle(st.getSubject().stringValue());                    
                } else if (type.contains("ConceptGroup".toUpperCase())) {
                    prop = SKOSProperty.ConceptGroup;
                } else if (type.contains("Collection".toUpperCase())) {
                    prop = SKOSProperty.Collection;
                } else if (type.contains("Concept".toUpperCase())) {
                    prop = SKOSProperty.Concept;
                }
                
                String uri = st.getSubject().stringValue();
                readStruct.resource = new SKOSResource(uri, prop);
                
                if (prop == SKOSProperty.ConceptScheme) {
                    sKOSXmlDocument.setConceptScheme(readStruct.resource);
                } else if (prop == SKOSProperty.ConceptGroup || prop == SKOSProperty.Collection) {
                    sKOSXmlDocument.addGroup(readStruct.resource);
                } else if (prop == SKOSProperty.Concept) {
                    sKOSXmlDocument.addconcept(readStruct.resource);
                }
                
            } //Labelling Properties
            else if (readLabellingProperties(readStruct)) {
                //Dates
                if (readDate(readStruct)) {
                    //Semantic Relationships
                    if (readRelationships(readStruct)) {
                        //Documentation Properties
                        if (readDocumentation(readStruct)) {
                            if (readCreator(readStruct)) {
                                if (readGPSCoordinates(readStruct)) {
                                    if (readNotation(readStruct)) {
                                        if (readIdentifier(readStruct)) {
                                            if (readMatch(readStruct)) {
                                                //test debug
                                                if (!nonReco.contains(readStruct.property.getLocalName())) {
                                                    System.out.println("non reconue : " + readStruct.property.getLocalName());
                                                    nonReco.add(readStruct.property.getLocalName());
                                                }                                                
                                            }
                                        }                                        
                                    }                                    
                                }                                
                            }                            
                        }
                    }
                }
            }            
        }        
    }

    /**
     * lit les balise de Documentation
     *
     * @param readStruct
     * @return false si on a lus une balise de Documentation true sinon
     */
    private boolean readDocumentation(ReadStruct readStruct) {
        if (readStruct.property.getLocalName().equals("definition")) {
            readStruct.resource.addDocumentation(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.definition);            
            return false;
        } else if (readStruct.property.getLocalName().equals("scopeNote")) {
            readStruct.resource.addDocumentation(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.scopeNote);            
            return false;
        } else if (readStruct.property.getLocalName().equals("example")) {
            readStruct.resource.addDocumentation(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.example);            
            return false;
        } else if (readStruct.property.getLocalName().equals("historyNote")) {
            readStruct.resource.addDocumentation(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.historyNote);            
            return false;
        } else if (readStruct.property.getLocalName().equals("editorialNote")) {
            readStruct.resource.addDocumentation(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.editorialNote);            
            return false;
        } else if (readStruct.property.getLocalName().equals("changeNote")) {
            readStruct.resource.addDocumentation(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.changeNote);            
            return false;
        } else if (readStruct.property.getLocalName().equals("note")) {
            readStruct.resource.addDocumentation(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.note);            
            return false;
        } else {
            return true;
        }
        
    }

    /**
     * lit les balise de Relationships
     *
     * @param readStruct
     * @return false si on a lus une balise de Relationships true sinon
     */
    private boolean readRelationships(ReadStruct readStruct) {
        
        if (readStruct.property.getLocalName().equals("broader")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.broader);            
            return false;
        } else if (readStruct.property.getLocalName().equals("narrower")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.narrower);            
            return false;
        } else if (readStruct.property.getLocalName().equals("related")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.related);            
            return false;
        } else if (readStruct.property.getLocalName().equals("hasTopConcept")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.hasTopConcept);            
            return false;
        } else if (readStruct.property.getLocalName().equals("inScheme")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.inScheme);            
            return false;
        } else if (readStruct.property.getLocalName().equals("member")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.member);            
            return false;
        } else if (readStruct.property.getLocalName().equals("topConceptOf")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.topConceptOf);            
            return false;
        } else if (readStruct.property.getLocalName().equals("microThesaurusOf")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.microThesaurusOf);            
            return false;
        } else if (readStruct.property.getLocalName().equals("subGroup")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.subGroup);            
            return false;
        } else if (readStruct.property.getLocalName().equals("superGroup")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.superGroup);            
            return false;
        } else if (readStruct.property.getLocalName().equals("hasMainConcept")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.hasMainConcept);            
            return false;
        } else if (readStruct.property.getLocalName().equals("memberOf")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.memberOf);            
            return false;
        } else if (readStruct.property.getLocalName().equals("mainConceptOf")) {
            readStruct.resource.addRelation(readStruct.value.toString(), SKOSProperty.mainConceptOf);            
            return false;
        } else {
            return true;
        }
    }

    /**
     * lit les balise de Labelling
     *
     * @param readStruct
     * @return false si on a lus une balise de Labelling true sinon
     */
    private boolean readLabellingProperties(ReadStruct readStruct) {
        
        if (readStruct.property.getLocalName().equals("prefLabel")) {
            readStruct.resource.addLabel(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.prefLabel);
            return false;
        } else if (readStruct.property.getLocalName().equals("altLabel")) {
            readStruct.resource.addLabel(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.altLabel);
            return false;
        } else if (readStruct.property.getLocalName().equals("hiddenLabel")) {
            readStruct.resource.addLabel(readStruct.literal.getLabel(), readStruct.literal.getLanguage().get(), SKOSProperty.hiddenLabel);
            return false;
        } else {
            return true;
        }
        
    }

    /**
     * lit les balise de Date
     *
     * @param readStruct
     * @return false si on a lus une balise de Date true sinon
     */
    private boolean readDate(ReadStruct readStruct) {
        
        if (readStruct.property.getLocalName().equals("created")) {
            readStruct.resource.addDate(readStruct.literal.getLabel(), SKOSProperty.created);
            return false;
        } else if (readStruct.property.getLocalName().equals("modified")) {
            readStruct.resource.addDate(readStruct.literal.getLabel(), SKOSProperty.modified);
            return false;
        } else if (readStruct.property.getLocalName().equals("date")) {
            readStruct.resource.addDate(readStruct.literal.getLabel(), SKOSProperty.date);
            return false;
        } else {
            return true;
        }        
    }

    /**
     * lit les balise de Creator
     *
     * @param readStruct
     * @return false si on a lus une balise de Creator true sinon
     */
    private boolean readCreator(ReadStruct readStruct) {
        if (readStruct.property.getLocalName().equals("creator")) {
            readStruct.resource.addCreator(readStruct.literal.getLabel(), SKOSProperty.creator);
            return false;
        } else if (readStruct.property.getLocalName().equals("contributor")) {
            readStruct.resource.addCreator(readStruct.literal.getLabel(), SKOSProperty.contributor);
            return false;
        } else {
            return true;
        }
    }

    /**
     * lit les balise de GPSCoordinates
     *
     * @param readStruct
     * @return false si on a lus une balise de GPSCoordinates true sinon
     */
    private boolean readGPSCoordinates(ReadStruct readStruct) {
        if (readStruct.property.getLocalName().equals("lat")) {
            readStruct.resource.getGPSCoordinates().setLat(readStruct.literal.getLabel());
            return false;
        } else if (readStruct.property.getLocalName().equals("long")) {
            readStruct.resource.getGPSCoordinates().setLon(readStruct.literal.getLabel());
            return false;
        } else {
            return true;
        }
    }

    /**
     * lit les balise de Notation
     *
     * @param readStruct
     * @return false si on a lus une balise de Notation true sinon
     */
    private boolean readNotation(ReadStruct readStruct) {
        if (readStruct.property.getLocalName().equals("notation")) {
            readStruct.resource.addNotation(readStruct.literal.getLabel());
            return false;
        } else {
            return true;
        }
    }

    /**
     * lit les balise de Match
     *
     * @param readStruct
     * @return false si on a lus une balise de Match true sinon
     */
    private boolean readMatch(ReadStruct readStruct) {
        if (readStruct.property.getLocalName().equals("exactMatch")) {
            readStruct.resource.addMatch(readStruct.value.toString(), SKOSProperty.exactMatch);
            return false;
        }
        if (readStruct.property.getLocalName().equals("closeMatch")) {
            readStruct.resource.addMatch(readStruct.value.toString(), SKOSProperty.closeMatch);
            return false;
        }
        if (readStruct.property.getLocalName().equals("broadMatch")) {
            readStruct.resource.addMatch(readStruct.value.toString(), SKOSProperty.broadMatch);
            return false;
        }
        if (readStruct.property.getLocalName().equals("relatedMatch")) {
            readStruct.resource.addMatch(readStruct.value.toString(), SKOSProperty.relatedMatch);
            return false;
        }
        if (readStruct.property.getLocalName().equals("narrowMatch")) {
            readStruct.resource.addMatch(readStruct.value.toString(), SKOSProperty.narrowMatch);
            return false;
        } else {
            return true;
        }
    }

    /**
     * lit les balise de Identifier
     *
     * @param readStruct
     * @return false si on a lus une balise de Identifier true sinon
     */
    private boolean readIdentifier(ReadStruct readStruct) {
        if (readStruct.property.getLocalName().equals("identifier")) {
            readStruct.resource.setIdentifier(readStruct.literal.getLabel());
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @return l'objet qui contiens les données lus par readModel()
     */
    public SKOSXmlDocument getsKOSXmlDocument() {
        return sKOSXmlDocument;
    }
    
}
