package mom.trd.opentheso.core.imports.old;

import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.stream.*;

import skos.SKOSConceptScheme;
import skos.SKOSProperty;
import skos.SKOSResource;
import skos.SKOSXmlDocument;
import skos.SKOSMapping;

public class ReadFileSKOS {

    private static boolean error = false;
    private static String localName = "";
    private static XMLStreamReader xmlr;

    private static SKOSXmlDocument thesaurus;
    private static SKOSResource resource;
    private static SKOSConceptScheme conceptScheme;
    private static String uri;
    private static String langPrefLabel;
    private static String langAltLabel;
    private static String langScopeNote;
    private static String langHistoryNote;
    private static String langDefinitionNote;
    private static String langEditorialNote;
    
    private static boolean isConceptScheme;

    
    public ReadFileSKOS() {
    }

    
    public void readFile(HikariDataSource ds, InputStream filename, 
            String formatDate, boolean useArk, String adressSite, int idUser, String langueSource) throws Exception {

        thesaurus = new SKOSXmlDocument();

        try {
			//
            // Get an input factory
            //
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
			// System.out.println("FACTORY: " + xmlif);

			//	
            // Instantiate a reader UTF8
            //
            xmlr = xmlif.createXMLStreamReader(filename, "UTF-8");

			// System.out.println("READER: " + xmlr + "\n");
			//
            // Parse the XML
            //
            while (xmlr.hasNext()) {
                if (!error) {
                    printEvent(xmlr);
                }
                xmlr.next();
            }

			//
            // Close the reader
            //
            xmlr.close();

            if (!error) {
                WriteSkosBDD writeSkosBDD = new WriteSkosBDD(ds);
                writeSkosBDD.writeThesaurus(thesaurus, formatDate, useArk, adressSite, idUser, langueSource);
            } else {
                thesaurus = null;
            }

        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
    }
    
    public void readBranchFile(HikariDataSource ds, InputStream filename, 
            String formatDate, boolean useArk, String adressSite) throws Exception {

        thesaurus = new SKOSXmlDocument();

        try {
			//
            // Get an input factory
            //
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
			// System.out.println("FACTORY: " + xmlif);

			//	
            // Instantiate a reader UTF8
            //
            xmlr = xmlif.createXMLStreamReader(filename, "UTF-8");

			// System.out.println("READER: " + xmlr + "\n");
			//
            // Parse the XML
            //
            while (xmlr.hasNext()) {
                if (!error) {
                    printEvent(xmlr);
                }
                xmlr.next();
            }

			//
            // Close the reader
            //
            xmlr.close();

        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
    }    
    
    public SKOSXmlDocument readStringBuffer(StringBuffer skos) throws Exception {

        thesaurus = new SKOSXmlDocument();

        try {
            // Get an input factory
            // String xmlContent;
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            StringReader stringReader = new StringReader(skos.toString());
            
            XMLStreamReader xMLStreamReader = inputFactory.createXMLStreamReader(stringReader);
           // XMLStreamReader xMLStreamReader = xMLStreamReader.createXMLStreamReader(inputFactory, "UTF-8");

            // Parse the XML
            //
            while (xMLStreamReader.hasNext()) {
                if (!error) {
              //      System.err.println(xMLStreamReader.toString());
                    printEvent(xMLStreamReader);
                }
                xMLStreamReader.next();
            }

			//
            // Close the reader
            //
            xMLStreamReader.close();
            return thesaurus;

        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    private static String enleverLesLettres(String id) {
        //	String text = "12zer45aaz43";

        int i = id.indexOf("ark:/12148/");
        if (i != -1) {
//			System.out.println(id);
            id = id.substring(i + "ark:/12148/".length(), id.length());
			//			System.out.println(text);
            //xmlr.getElementText();
	/*		id = id.replaceAll("a", "1");
             id = id.replaceAll("b", "2");
             id = id.replaceAll("c", "3");
             id = id.replaceAll("d", "4");
             id = id.replaceAll("e", "5");
             id = id.replaceAll("f", "6");
             id = id.replaceAll("g", "7");
             id = id.replaceAll("h", "8");
             id = id.replaceAll("i", "9");
             id = id.replaceAll("j", "1");
             id = id.replaceAll("k", "2");
             id = id.replaceAll("l", "3");
             id = id.replaceAll("m", "4");
             id = id.replaceAll("n", "5");
             id = id.replaceAll("o", "6");
             id = id.replaceAll("p", "7");
             id = id.replaceAll("q", "8");
             id = id.replaceAll("r", "9");
             id = id.replaceAll("s", "1");
             id = id.replaceAll("t", "2");
             id = id.replaceAll("u", "3");
             id = id.replaceAll("v", "4");
             id = id.replaceAll("w", "5");
             id = id.replaceAll("x", "6");
             id = id.replaceAll("y", "7");
             id = id.replaceAll("z", "8");
             */
            id = id.replaceAll("\\D+", "");
            id = "#" + id;
        }
        return id;
    }

    private static void printEvent(XMLStreamReader xmlr) {
        int start = -1;
        int length = -1;

        switch (xmlr.getEventType()) {

            case XMLStreamConstants.START_DOCUMENT:
                //  System.out.println("START_DOCUMENT : " + localName);
                break;

            /**
             * START_ELEMENT: Le nom de la balise. Exemple:
             * <dcterms:created>2007-02-08</dcterms:created>
             * localName = created
             */
            case XMLStreamConstants.START_ELEMENT:
                String localAttribute = "";
                String attributeValue = "";

                if (xmlr.hasName()) {
                    localName = xmlr.getLocalName();
                }

                /**
                 * Permet de retrouver les attributs. Exemple:
                 * <skos:Concept
                 * rdf:about="http://frantiq.mom.fr/PACTOLS/concept#1">
                 * ATTRIBUTE: http://frantiq.mom.fr/PACTOLS/concept#1
                 */
                for (int i = 0; i < xmlr.getAttributeCount(); i++) {
                    localAttribute = xmlr.getAttributeLocalName(i);
                    attributeValue = xmlr.getAttributeValue(i);

                    if ("RDF".equals(localName)) {
                        //  System.out.println("START_ELEMENT : " + localName);
                    }

                    if ("ConceptScheme".equals(localName)) {
                        if ("about".equals(localAttribute)) {
				 //       System.out.println("START_ELEMENT : " + localName);
                            //       System.out.println("ATTRIBUTE : " + attributeValue);
                            conceptScheme = new SKOSConceptScheme(attributeValue);
                            isConceptScheme = true;
                        }
                    }

                    if ("hasTopConcept".equals(localName)) {
                        //    System.out.println("START_ELEMENT : " + localName);
                    }

                    if ("Concept".equals(localName)) {
                        if ("about".equals(localAttribute)) {
				  //      System.out.println("START_ELEMENT : " + localName);
                            //       System.out.println("ATTRIBUTE : " + attributeValue);
                            //MT (hasTopConcept)
                            uri = attributeValue;
                            //autres
                            resource = new SKOSResource(uri);
                        }
                    }

                    if ("prefLabel".equals(localName)) {
                        if ("lang".equals(localAttribute)) {
				  //      System.out.println("START_ELEMENT : " + localName);
                            //      System.out.println("ATTRIBUTE : " + attributeValue);
                            langPrefLabel = attributeValue;
                        }
                    }

                    if ("altLabel".equals(localName)) {
                        if ("lang".equals(localAttribute)) {
				  //      System.out.println("START_ELEMENT : " + localName);
                            //      System.out.println("ATTRIBUTE : " + attributeValue);
                            langAltLabel = attributeValue;
                        }
                    }

                    if ("scopeNote".equals(localName)) {
                        if ("lang".equals(localAttribute)) {
				  //      System.out.println("START_ELEMENT : " + localName);
                            //      System.out.println("ATTRIBUTE : " + attributeValue);
                            langScopeNote = attributeValue;
                        }
                    }

                    if ("historyNote".equals(localName)) {
                        if ("lang".equals(localAttribute)) {
				  //      System.out.println("START_ELEMENT : " + localName);
                            //      System.out.println("ATTRIBUTE : " + attributeValue);
                            langHistoryNote = attributeValue;
                        }
                    }
                    
                    if (localName.equalsIgnoreCase("definition")) {
                        if (localAttribute.equalsIgnoreCase("lang")) {
				  //      System.out.println("START_ELEMENT : " + localName);
                            //      System.out.println("ATTRIBUTE : " + attributeValue);
                            langDefinitionNote = attributeValue;
                        }
                    }
                    
                    if ("editorialNote".equals(localName)) {
                        if ("lang".equals(localAttribute)) {
				  //      System.out.println("START_ELEMENT : " + localName);
                            //      System.out.println("ATTRIBUTE : " + attributeValue);
                            langEditorialNote = attributeValue;
                        }
                    }
                    

                    if ("created".equals(localName)) {
                        //    System.out.println("START_ELEMENT : " + localName);
                    }

                    if ("modified".equals(localName)) {
                        //    System.out.println("START_ELEMENT : " + localName);
                    }

                    if ("inScheme".equals(localName)) {
                        if ("resource".equals(localAttribute)) {
				    //    System.out.println("START_ELEMENT : " + localName);
                            //    System.out.println("ATTRIBUTE : " + attributeValue);
                            uri = attributeValue;
                            resource.addRelation(uri, SKOSProperty.inScheme);
                        }
                    }

                    if ("broader".equals(localName)) {
                        if ("resource".equals(localAttribute)) {
				    //    System.out.println("START_ELEMENT : " + localName);
                            //     System.out.println("ATTRIBUTE : " + attributeValue);
                            uri = attributeValue;
                            resource.addRelation(uri, SKOSProperty.broader);
                        }
                    }

                    if ("narrower".equals(localName)) {
                        if ("resource".equals(localAttribute)) {
				     //   System.out.println("START_ELEMENT : " + localName);
                            //   System.out.println("ATTRIBUTE : " + attributeValue);
                            uri = attributeValue;

                            resource.addRelation(uri, SKOSProperty.narrower);
                        }
                    }

                    if ("related".equals(localName)) {
                        if ("resource".equals(localAttribute)) {
				    //    System.out.println("START_ELEMENT : " + localName);
                            //     System.out.println("ATTRIBUTE : " + attributeValue);
                            uri = attributeValue;
                            resource.addRelation(uri, SKOSProperty.related);
                        }
                    }
                    
                    if ("closeMatch".equals(localName)) {
                        if ("resource".equals(localAttribute)) {
				    //    System.out.println("START_ELEMENT : " + localName);
                            //     System.out.println("ATTRIBUTE : " + attributeValue);
                            uri = attributeValue;
                            resource.addMapping(uri, SKOSMapping.closeMatch);
                        }
                    }
                    if ("exactMatch".equals(localName)) {
                        if ("resource".equals(localAttribute)) {
				    //    System.out.println("START_ELEMENT : " + localName);
                            //     System.out.println("ATTRIBUTE : " + attributeValue);
                            uri = attributeValue;
                            resource.addMapping(uri, SKOSMapping.exactMatch);
                        }
                    }                    
                    
                }
                break;

		// case XMLStreamConstants.SPACE:
            /**
             * CHARACTERS: Le contenu de la balise. Exemple:
             * <dcterms:created>2007-02-08</dcterms:created>
             * CHARACTERS: 2007-02-08
             *
             */
            case XMLStreamConstants.CHARACTERS:
                start = xmlr.getTextStart();
                length = xmlr.getTextLength();

                String text = new String(xmlr.getTextCharacters(), start, length);
//			System.out.println(text);
            //    text = ajouterQuotes(text);

                if(resource !=null) {
                    if (text.trim().length() > 0) {

                        if ("prefLabel".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            if(isConceptScheme) {
                                conceptScheme.addLabel(text, langPrefLabel, SKOSProperty.prefLabel);
                            }
                            else
                                resource.addLabel(text, langPrefLabel, SKOSProperty.prefLabel);
                        }

                        if ("altLabel".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            resource.addLabel(text, langAltLabel, SKOSProperty.altLabel);
                        }

                        if ("scopeNote".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            resource.addDocumentation(text, langScopeNote, SKOSProperty.scopeNote);
                        }

                        if ("historyNote".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            resource.addDocumentation(text, langHistoryNote, SKOSProperty.historyNote);
                        }

                        if ("definition".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            resource.addDocumentation(text, langDefinitionNote, SKOSProperty.definition);
                        }
                        if ("editorialNote".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            resource.addDocumentation(text, langEditorialNote, SKOSProperty.editorialNote);
                        }

                        if ("created".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            resource.addDate(text, SKOSProperty.created);
                        }

                        if ("modified".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);

                            resource.addDate(text, SKOSProperty.modified);
                        }
                        if ("identifier".equals(localName)) {
                            //	System.out.println("CHARACTERS : " + text);
                            resource.addIdentifier(text, SKOSProperty.identifier);
                        }

                    }
                }
                
                break;

            case XMLStreamConstants.END_ELEMENT:
                /**
                 * localName correspond à la balise en cours Exemple:
                 * <skos:prefLabel xml:lang="fr">Oeuvres</skos:prefLabel>
                 * localName = prefLabel
                 */
                if (xmlr.hasName()) {
                    localName = xmlr.getLocalName();

                    if ("RDF".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                    }

                    if ("ConceptScheme".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);   	     
                        thesaurus.setConceptScheme(conceptScheme);
                        //A la fin du bloc ConceptScheme, vider les ressources du thésaurus
                        thesaurus.clearResourcesList();
                        isConceptScheme = false;
                    }

                    if ("hasTopConcept".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                        conceptScheme.addTopConcept(uri);
                        uri = null;
                    }

                    if ("Concept".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                        thesaurus.addResource(resource);
                        resource = null;
                    }

                    if ("prefLabel".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                        langPrefLabel = null;
                    }

                    if ("altLabel".equals(localName)) {
                        //   System.out.println("END_ELEMENT : " + localName);
                        langAltLabel = null;
                    }

                    if ("scopeNote".equals(localName)) {
                        //   System.out.println("END_ELEMENT : " + localName);
                        langScopeNote = null;
                    }

                    if ("historyNote".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                        langHistoryNote = null;
                    }

                    if ("created".equals(localName)) {
                        //   System.out.println("END_ELEMENT : " + localName);
                    }

                    if ("modified".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                    }

                    if ("inScheme".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                    }

                    if ("broader".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                    }

                    if ("narrower".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                    }

                    if ("related".equals(localName)) {
                        //    System.out.println("END_ELEMENT : " + localName);
                    }

                }
                break;

            case XMLStreamConstants.COMMENT:
                //   System.out.println("COMMENT : "+ localName);
                break;

            case XMLStreamConstants.END_DOCUMENT:
                //   System.out.println("END_DOCUMENT : " + localName);
                break;

            default:
                break;
        }
    }

    /*private static void stopReader(){
     try {
     JOptionPane.showMessageDialog(null, "Erreur de format XML ou de cohérence\nTerme = " + blocTerme.idTerme);
     xmlr.close();
     error = true;
			
     } catch (XMLStreamException e) {
     e.printStackTrace();
     }
     }*/
    private static String ajouterQuotes(String ligne) {
        String ligne_prete = "";
        String ligne_temp;
        int index;

        while (true) {
            index = ligne.indexOf("'");
            if (index == -1) {
                return (ligne_prete + ligne);
            }
            ligne_temp = ligne.substring(0, index + 1);
            ligne_prete = ligne_prete + ligne_temp.concat("'");
            ligne = ligne.substring(index + 1, ligne.length());
        }
    }

    public SKOSXmlDocument getThesaurus() {
        return thesaurus;
    }

    
    
    
}
