/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.rdf4j;

import mom.trd.opentheso.skosapi.SKOSCreator;
import mom.trd.opentheso.skosapi.SKOSDate;
import mom.trd.opentheso.skosapi.SKOSDocumentation;
import mom.trd.opentheso.skosapi.SKOSGPSCoordinates;
import mom.trd.opentheso.skosapi.SKOSLabel;
import mom.trd.opentheso.skosapi.SKOSMatch;
import mom.trd.opentheso.skosapi.SKOSNotation;
import mom.trd.opentheso.skosapi.SKOSProperty;
import mom.trd.opentheso.skosapi.SKOSRelation;
import mom.trd.opentheso.skosapi.SKOSResource;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;

/**
 *
 * @author Quincy
 */
public class WriteRdf4j {

    private static Model model;
    private static ModelBuilder builder;
    private SKOSXmlDocument xmlDocument;
    ValueFactory vf;

    /**
     *
     * @param xmlDocument
     */
    public WriteRdf4j(SKOSXmlDocument xmlDocument) {
        this.xmlDocument = xmlDocument;
        vf = SimpleValueFactory.getInstance();
        loadModel();
        writeModel();
        model = builder.build();
    }

    private void loadModel() {
        builder = new ModelBuilder();
        builder.setNamespace("skos", "http://www.w3.org/2004/02/skos/core#");
        builder.setNamespace("dc", "http://purl.org/dc/elements/1.1/");
        builder.setNamespace("dcterms", "http://purl.org/dc/terms/");
        builder.setNamespace("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        builder.setNamespace("iso-thes", "http://purl.org/iso25964/skos-thes#");

    }

    private void writeModel() {
        writeConceptScheme();
        writeGroup();
        writeConcept();
    }

    private void writeConcept() {
        for (SKOSResource concept : xmlDocument.getConceptList()) {
            builder.subject(vf.createIRI(concept.getUri()));
            builder.add(RDF.TYPE, SKOS.CONCEPT);
            writeLabel(concept);
            writeRelation(concept);
            writeMatch(concept);
            writeNotation(concept);
            writeDate(concept);
            writeCreator(concept);
            writeDocumentation(concept);
            writeGPS(concept);
        }
    }

    private void writeGroup() {
        for (SKOSResource group : xmlDocument.getGroupList()) {
            builder.subject(vf.createIRI(group.getUri()));
            builder.add(RDF.TYPE, SKOS.COLLECTION);
            writeLabel(group);
            writeRelation(group);
            writeMatch(group);
            writeNotation(group);
            writeDate(group);
            writeCreator(group);
            writeDocumentation(group);
            writeGPS(group);
        }
    }

    private void writeConceptScheme() {
        SKOSResource conceptScheme = xmlDocument.getConceptScheme();
        builder.subject(vf.createIRI(conceptScheme.getUri()));//createURI(conceptScheme.getUri()));

        builder.add(RDF.TYPE, SKOS.CONCEPT_SCHEME);

        writeLabel(conceptScheme);
        writeRelation(conceptScheme);
        writeMatch(conceptScheme);
        writeNotation(conceptScheme);
        writeDate(conceptScheme);
        writeCreator(conceptScheme);
        writeDocumentation(conceptScheme);
        writeGPS(conceptScheme);
    }

    private void writeGPS(SKOSResource resource) {
        SKOSGPSCoordinates gps = resource.getGPSCoordinates();
        String lat = gps.getLat();
        String lon = gps.getLon();
        if (lat != null && lon != null) {
            builder.add("geo:lat", Double.parseDouble(lat));
            builder.add("geo:long", Double.parseDouble(lon));
        }

    }

    private void writeDocumentation(SKOSResource resource) {
        int prop;
        for (SKOSDocumentation doc : resource.getDocumentationsList()) {
            prop = doc.getProperty();
            Literal literal = vf.createLiteral(doc.getText(), doc.getLanguage());
            if (prop == SKOSProperty.definition) {
                builder.add(SKOS.NOTE, literal);
            } else if (prop == SKOSProperty.scopeNote) {
                builder.add(SKOS.SCOPE_NOTE, literal);
            } else if (prop == SKOSProperty.example) {
                builder.add(SKOS.EXAMPLE, literal);
            } else if (prop == SKOSProperty.historyNote) {
                builder.add(SKOS.HISTORY_NOTE, literal);
            } else if (prop == SKOSProperty.editorialNote) {
                builder.add(SKOS.EDITORIAL_NOTE, literal);
            } else if (prop == SKOSProperty.changeNote) {
                builder.add(SKOS.CHANGE_NOTE, literal);
            } else if (prop == SKOSProperty.note) {
                builder.add(SKOS.NOTE, literal);
            }
        }
    }

    private void writeCreator(SKOSResource resource) {
        int prop;
        for (SKOSCreator creator : resource.getCreatorList()) {
            prop = creator.getProperty();
            if (prop == SKOSProperty.creator) {
                builder.add(DCTERMS.CREATOR, creator.getCreator());
            } else if (prop == SKOSProperty.contributor) {
                builder.add(DCTERMS.CONTRIBUTOR, creator.getCreator());
            }
        }
    }

    private void writeDate(SKOSResource resource) {
        int prop;
        for (SKOSDate date : resource.getDateList()) {
            prop = date.getProperty();
            if (prop == SKOSProperty.created) {
                builder.add(DCTERMS.CREATED, date.getDate());
            } else if (prop == SKOSProperty.modified) {
                builder.add(DCTERMS.MODIFIED, date.getDate());
            } else if (prop == SKOSProperty.date) {
                builder.add(DCTERMS.DATE, date.getDate());
            }
        }

    }

    private void writeNotation(SKOSResource resource) {
        for (SKOSNotation notation : resource.getNotationList()) {
            builder.add(SKOS.NOTATION, notation.getNotation());
        }
    }

    private void writeLabel(SKOSResource resource) {
        int prop;
        for (SKOSLabel label : resource.getLabelsList()) {
            prop = label.getProperty();
            Literal literal = vf.createLiteral(label.getLabel(), label.getLanguage());
            if (prop == SKOSProperty.prefLabel) {
                builder.add(SKOS.PREF_LABEL, literal);
            } else if (prop == SKOSProperty.altLabel) {
                builder.add(SKOS.ALT_LABEL, literal);
            }
        }
    }

    private void writeMatch(SKOSResource resource) {
        int prop;
        for (SKOSMatch match : resource.getMatchList()) {
            prop = match.getProperty();
            IRI uri = vf.createIRI(match.getValue());
            if (prop == SKOSProperty.exactMatch) {
                builder.add(SKOS.EXACT_MATCH, uri);
            } else if (prop == SKOSProperty.closeMatch) {
                builder.add(SKOS.CLOSE_MATCH, uri);
            } else if (prop == SKOSProperty.broadMatch) {
                builder.add(SKOS.BROAD_MATCH, uri);
            } else if (prop == SKOSProperty.relatedMatch) {
                builder.add(SKOS.RELATED_MATCH, uri);
            } else if (prop == SKOSProperty.narrowMatch) {
                builder.add(SKOS.NARROW_MATCH, uri);
            }

        }
    }

    private void writeRelation(SKOSResource resource) {
        int prop;
        for (SKOSRelation relation : resource.getRelationsList()) {
            IRI uri = vf.createIRI(relation.getTargetUri());
            prop = relation.getProperty();
            if (prop == SKOSProperty.member) {
                builder.add(SKOS.MEMBER, uri);
            } else if (prop == SKOSProperty.broader) {
                builder.add(SKOS.BROADER, uri);
            } else if (prop == SKOSProperty.narrower) {
                builder.add(SKOS.NARROWER, uri);
            } else if (prop == SKOSProperty.related) {
                builder.add(SKOS.RELATED, uri);
            } else if (prop == SKOSProperty.hasTopConcept) {
                builder.add(SKOS.HAS_TOP_CONCEPT, uri);
            } else if (prop == SKOSProperty.inScheme) {
                builder.add(SKOS.IN_SCHEME, uri);
            } else if (prop == SKOSProperty.topConceptOf) {
                builder.add(SKOS.TOP_CONCEPT_OF, uri);
            } else if (prop == SKOSProperty.subGroup) {
                builder.add("iso-thes:subGroup", uri);
            } else if (prop == SKOSProperty.microThesaurusOf) {
                builder.add("iso-thes:microThesaurusOf", uri);
            } else if (prop == SKOSProperty.superGroup) {
                builder.add("iso-thes:superGroup", uri);
            }

        }

    }

    //********
    public Model getModel() {
        return model;
    }

    public SKOSXmlDocument getsKOSXmlDocument() {
        return xmlDocument;
    }

    public void setsKOSXmlDocument(SKOSXmlDocument xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

}
