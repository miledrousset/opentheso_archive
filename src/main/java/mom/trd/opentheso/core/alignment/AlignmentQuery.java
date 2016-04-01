/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.alignment;

import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.imports.old.ReadFileSKOS;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import skos.SKOSDocumentation;
import skos.SKOSLabel;
import skos.SKOSProperty;
import skos.SKOSResource;
import skos.SKOSXmlDocument;

/**
 *
 * @author Carole
 */
public class AlignmentQuery {

    private ArrayList<NodeAlignment> listeAlign;

    /**
     * Cette fonction permet de récupérer les alignements présents sur une
     * source de données pour un concept passé en paramètre
     *
     * @param dest
     * @param idC
     * @param idTheso
     * @param lexicalValue
     * @param lang
     * @param idThesoTarget
     * @return
     */
    public ArrayList<NodeAlignment> query(String dest, String idC, String idTheso, String lexicalValue, String lang, String idThesoTarget) {
        listeAlign = new ArrayList<>();
        switch (dest) {
            case "DBP":
                listeAlign = queryDBPedia(idC, idTheso, lexicalValue, lang);
                break;
            case "WIKI":
                listeAlign = queryWikipedia(idC, idTheso, lexicalValue, lang);
                break;
            case "AGROVOC":
                listeAlign = queryAgrovoc(idC, idTheso, lexicalValue, lang);
                break;
            case "GEMET":
                listeAlign = queryGemet(idC, idTheso, lexicalValue, lang);
                break;
            case "OPENT":
                listeAlign = queryOpentheso(idC, idTheso, lexicalValue, lang, idThesoTarget);
                break;
        }
        return listeAlign;
    }

    /**
     * Cette fonction permet de récupérer les alignements présents sur Wikipedia
     * pour un concept passé en paramètre
     *
     * @param idC
     * @param idTheso
     * @param lexicalValue
     * @param lang
     * @return
     */
    private ArrayList<NodeAlignment> queryWikipedia(String idC, String idTheso, String lexicalValue, String lang) {
        listeAlign = new ArrayList<>();
        try {
            lexicalValue = lexicalValue.replaceAll(" ", "%20");
            URL url = new URL("https://" + lang + ".wikipedia.org/w/api.php?action=query&list=search&srwhat=text&format=xml&srsearch=" + lexicalValue + "&srnamespace=0");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String xmlRecord = "";
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));

            conn.disconnect();

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xmlRecord));

                Document doc = db.parse(is);
                NodeList nodes = doc.getElementsByTagName("search");

                // iterate the employees
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    NodeList p = element.getElementsByTagName("p");
                    if (p != null && p.getLength() > 0) {
                        for (int j = 0; j < p.getLength(); j++) {
                            String title = "", snippet = "";
                            Element el = (org.w3c.dom.Element) p.item(j);
                            if (el.hasAttribute("title")) {
                                title = el.getAttribute("title");
                            }
                            if (el.hasAttribute("snippet")) {
                                snippet = el.getAttribute("snippet") + "...";
                            }
                            NodeAlignment na = new NodeAlignment();
                            //si le titre est équivalent, on le place en premier
                            if(lexicalValue.trim().equalsIgnoreCase(title.trim())) {
                                na.setConcept_target(title);
                                na.setDef_target(snippet);
                                na.setInternal_id_concept(idC);
                                na.setInternal_id_thesaurus(idTheso);
                                na.setThesaurus_target("Wikipedia");
                                na.setUri_target("https://" + lang + ".wikipedia.org/wiki/" + title.replaceAll(" ", "_"));
                                listeAlign.add(0, na);
                            }
                            else {
                                na.setConcept_target(title);
                                na.setDef_target(snippet);
                                na.setInternal_id_concept(idC);
                                na.setInternal_id_thesaurus(idTheso);
                                na.setThesaurus_target("Wikipedia");
                                na.setUri_target("https://" + lang + ".wikipedia.org/wiki/" + title.replaceAll(" ", "_"));
                                listeAlign.add(na);
                            }
                            
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return listeAlign;
    }

    /**
     * Cette fonction permet de récupérer les alignements présents sur DBPedia
     * pour un concept passé en paramètre
     *
     * @param idC
     * @param idTheso
     * @param lexicalValue
     * @param lang
     * @return
     */
    private ArrayList<NodeAlignment> queryDBPedia(String idC, String idTheso, String lexicalValue, String lang) {
        listeAlign = new ArrayList<>();
        if (lexicalValue.contains(" ")) {
            lexicalValue = lexicalValue.substring(0, lexicalValue.indexOf(" "));
        }
        lexicalValue = String.valueOf(lexicalValue.charAt(0)).toUpperCase() + lexicalValue.substring(1);
        String sparqlQueryString1 =
                
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT * WHERE {"
                + "       ?s rdfs:label ?label ."
                + "       ?s rdfs:comment ?comment ."
                + "       ?s dbpedia-owl:thumbnail ?thumbnail ."
                + "       ?s foaf:isPrimaryTopicOf ?primaryTopicOf ."
                + "       FILTER(regex(?s,\"resource/" + lexicalValue + "*\"))"
                + "       FILTER(lang(?label) = \"" + lang + "\")"
                + "       FILTER(lang(?comment) = \"" + lang + "\")"
                + "   }";
                
                
        System.out.println(sparqlQueryString1);
        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution qs = results.next();
            NodeAlignment na = new NodeAlignment();
            na.setInternal_id_concept(idC);
            na.setInternal_id_thesaurus(idTheso);
            na.setConcept_target(qs.get("label").toString());
            na.setDef_target(qs.get("comment").toString());
            na.setThesaurus_target("DBPedia");
            na.setUri_target(qs.get("primaryTopicOf").toString());
            listeAlign.add(na);
        }

        qexec.close();
        return listeAlign;
    }

    /**
     * Cette fonction permet de récupérer les alignements présents sur Agrovoc
     * pour un concept passé en paramètre
     *
     * @param idC
     * @param idTheso
     * @param lexicalValue
     * @param lang
     * @return
     */
    private ArrayList<NodeAlignment> queryAgrovoc(String idC, String idTheso, String lexicalValue, String lang) {
        listeAlign = new ArrayList<>();
        lexicalValue = String.valueOf(lexicalValue.charAt(0)).toUpperCase() + lexicalValue.substring(1);
        String sparqlQueryString1 = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "SELECT * WHERE {"
                + "       ?uri skos:prefLabel ?pl ."
                + "       FILTER(regex(?pl,\"" + lexicalValue + "*\"))"
                + "       FILTER ( (lang(?pl)=\"" + lang + "\") )"
                + "       OPTIONAL { "
                + "       ?uri skos:scopeNote ?def ."
                + "       FILTER ( (lang(?def)=\"" + lang + "\") )"
                + "       }"
                + "   }";
        System.out.println(sparqlQueryString1);
        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("URL A DEFINIR", query);

        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution qs = results.next();
            NodeAlignment na = new NodeAlignment();
            na.setInternal_id_concept(idC);
            na.setInternal_id_thesaurus(idTheso);
            na.setConcept_target(qs.get("pl").toString());
            na.setDef_target(qs.get("def").toString());
            na.setThesaurus_target("Agrovoc");
            na.setUri_target(qs.get("uri").toString());
            listeAlign.add(na);
        }

        qexec.close();
        return listeAlign;
    }

    /**
     * Cette fonction permet de récupérer les alignements présents sur Gemet
     * pour un concept passé en paramètre
     *
     * @param idC
     * @param idTheso
     * @param lexicalValue
     * @param lang
     * @return
     */
    private ArrayList<NodeAlignment> queryGemet(String idC, String idTheso, String lexicalValue, String lang) {
        listeAlign = new ArrayList<>();
        if (lexicalValue.contains(" ")) {
            lexicalValue = lexicalValue.substring(0, lexicalValue.indexOf(" "));
        }
        String sparqlQueryString1 = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "SELECT * WHERE {"
                + "       ?uri skos:prefLabel ?pl ."
                + "       FILTER(regex(?pl,\"" + lexicalValue + "*\"))"
                + "       FILTER ( (lang(?pl)=\"" + lang + "\") )"
                + "       OPTIONAL { "
                + "       ?uri skos:scopeNote ?def ."
                + "       FILTER ( (lang(?def)=\"" + lang + "\") )"
                + "       }"
                + "   }";
        System.out.println(sparqlQueryString1);

        /*String endpointURL = "http://cr.eionet.europa.eu/sparql";
         SPARQLRepository crEndpoint = new SPARQLRepository(endpointURL);
         RepositoryConnection conn = null;
         try {
         crEndpoint.initialize();
         conn = crEndpoint.getConnection();
         TupleQuery q = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQueryString1);
         TupleQueryResult bindings = q.evaluate();
         while (bindings.hasNext()) {
         BindingSet b = bindings.next();
         NodeAlignment na = new NodeAlignment();
         na.setInternal_id_concept(idC);
         na.setInternal_id_thesaurus(idTheso);
         na.setConcept_target(b.getBinding("pl").getValue().stringValue());
         na.setDef_target(b.getBinding("def").getValue().stringValue());
         na.setThesaurus_target("Gemet");
         na.setUri_target(b.getBinding("uri").getValue().stringValue());
         listeAlign.add(na);
         }
         } catch (Exception e) {
         e.printStackTrace();
         } finally {
         try {
         conn.close();
         } catch (RepositoryException e) {
         e.printStackTrace();
         }
         }*/
        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://cr.eionet.europa.eu/sparql", query);

        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution qs = results.next();
            NodeAlignment na = new NodeAlignment();
            na.setInternal_id_concept(idC);
            na.setInternal_id_thesaurus(idTheso);
            na.setConcept_target(qs.get("pl").toString());
            na.setDef_target(qs.get("def").toString());
            na.setThesaurus_target("Gemet");
            na.setUri_target(qs.get("uri").toString());
            listeAlign.add(na);
        }

        qexec.close();
        return listeAlign;
    }

    /**
     * Cette fonction permet de récupérer les alignements présents sur Opentheso
     * pour un concept passé en paramètre et un thésaurus donné
     *
     * @param idC
     * @param idTheso
     * @param lexicalValue
     * @param lang
     * @return
     */
    private ArrayList<NodeAlignment> queryOpentheso(String idC, String idTheso, String lexicalValue, String lang, String idThesoTarget) {
        listeAlign = new ArrayList<>();
        try {
            URL url = new URL(idThesoTarget);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String xmlRecord = "";
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));

            conn.disconnect();

            StringBuffer sb = new StringBuffer(xmlRecord);
            try {
                SKOSXmlDocument sxd = new ReadFileSKOS().readStringBuffer(sb);
                for (SKOSResource resource : sxd.getResourcesList()) {
                    NodeAlignment na = new NodeAlignment();
                    na.setInternal_id_concept(idC);
                    na.setInternal_id_thesaurus(idTheso);
                    na.setThesaurus_target("OpenTheso");
                    na.setUri_target(resource.getUri());
                    for(SKOSLabel label : resource.getLabelsList()) {
                        switch (label.getProperty()) {
                            case SKOSProperty.prefLabel:
                                if(label.getLanguage().equals(lang)) {
                                    na.setConcept_target(label.getLabel());
                                }
                                break;
                            case SKOSProperty.altLabel:
                                if(label.getLanguage().equals(lang)) {
                                    if(na.getConcept_target_alt().isEmpty()) {
                                        na.setConcept_target_alt(label.getLabel());
                                    } 
                                    else {
                                        na.setConcept_target_alt(
                                                na.getConcept_target_alt() + ";" + label.getLabel());                                        
                                    }
                                }
                                break;                                
                            default:
                                break;
                        }
                    }

                    for(SKOSDocumentation sd : resource.getDocumentationsList()) {
                        if(sd.getProperty() == SKOSProperty.definition && sd.getLanguage().equals(lang)) {
                            na.setDef_target(sd.getText());
                        }
                    }
                    listeAlign.add(na);
                }
            } catch (Exception ex) {
                Logger.getLogger(AlignmentQuery.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return listeAlign;
    }

    public ArrayList<NodeAlignment> getListeAlign() {
        return listeAlign;
    }

    public void setListeAlign(ArrayList<NodeAlignment> listeAlign) {
        this.listeAlign = listeAlign;
    }
}
