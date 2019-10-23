/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignement;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import com.bordercloud.sparql.Endpoint;
//import com.bordercloud.sparql.EndpointException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;

import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.core.exports.privatesdatas.LineOfData;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import mom.trd.opentheso.core.json.helper.JsonHelper;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author miled.rousset
 */
public class AAT_test {

    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public AAT_test() {
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
    public void searchValueSubject() {
        String searchValue =
                "camion";
                //"camion à échelles";
                //amphora type";//"Amphores puniques";//"amphores";

        //https://www.idref.fr/Sru/Solr?wt=json&version=2.2&start=&rows=100&indent=on&fl=id,ppn_z,affcourt_z&q=subjectheading_t:amphore*%20AND%20recordtype_z:r
        //"http://vocabsservices.getty.edu/AATService.asmx/AATGetTermMatch?term=amphora&logop=and&notes="
        //String query = "https://www.idref.fr/Sru/Solr?wt=json&version=2.2&start=&rows=100&indent=on&fl=id,ppn_z,affcourt_z&q=subjectheading_t:(##value##)%20AND%20recordtype_z:r";
        String query = "http://vocabsservices.getty.edu/AATService.asmx/AATGetTermMatch?term=##value##&logop=and&notes=";
        if (query.trim().equals("")) {
            return;
        }
        if (searchValue.trim().equals("")) {
            return;
        }

        ArrayList<NodeAlignment> listeAlign;
        try {

            searchValue = URLEncoder.encode(searchValue, "UTF-8");
            query = query.replace("##value##", searchValue);
            URL url = new URL(query);
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
            conn.disconnect();
            System.out.println(xmlRecord);

            listeAlign = getValuesXml2(xmlRecord);
            String tt = "";
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }

//        return listeAlign;
    }

    private String clearName(String nom) {
        nom = nom.replaceAll("\\[|\\]", "");

        nom = nom.replaceAll(";", " AND ");
        nom = nom.replaceAll(" ", " AND ");
        nom = nom.replaceAll("_", " AND ");
        nom = nom.replaceAll("-", " AND ");

        return nom;
    }

    private ArrayList<NodeAlignment> getValuesXml2(String xmlDatas) {
        ArrayList<NodeAlignment> listeAlign = new ArrayList<>();
        try {
            String localName = "";
            String text;
            String originalText;
            String id;
            String uri;
            //    try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader r = factory.createXMLStreamReader(new StringReader(xmlDatas));
            NodeAlignment nodeAlignment = new NodeAlignment();
            while (r.hasNext()) {
                int event = r.next();
                if (event == r.START_ELEMENT) {
                    if (r.hasName()) {
                        localName = r.getLocalName();
                        for (int i = 0; i < r.getAttributeCount(); i++) {
                        /*    r.getAttributeLocalName(i);
                            System.out.println("name of attribute = " + r.getAttributeLocalName(i));                            
                            r.getAttributeValue(i);
                            System.out.println("Value of attribute = " + r.getAttributeValue(i));     */                       
                        }
                    }
                }
                if (event == r.CHARACTERS) {
                    if(localName.equalsIgnoreCase("preferred_term")) {
                        originalText = new String(r.getTextCharacters(), r.getTextStart(), r.getTextLength());
                        if(!originalText.trim().isEmpty()) {
                            System.out.println("PreferredTerm / originalTerm = " + originalText);
                            nodeAlignment.setDef_target(originalText);
                        }
                    }
                    if(localName.equalsIgnoreCase("subject_id")) {
                        id = new String(r.getTextCharacters(), r.getTextStart(), r.getTextLength());
                        if(!id.trim().isEmpty()) {
                            uri = "http://vocab.getty.edu/page/aat/" + id;
                            System.out.println("Uri = " + uri);
                            nodeAlignment.setUri_target(uri);
                        }
                    }
                    if(localName.equalsIgnoreCase("term")) {
                        text = new String(r.getTextCharacters(), r.getTextStart(), r.getTextLength());
                        if(!text.trim().isEmpty()) {
                            System.out.println("searched value = " + text);
                            nodeAlignment.setConcept_target(text);
                        }
                    }                     
                }
                if (event == r.END_ELEMENT) {
                    if (r.hasName())
                        localName = r.getLocalName();
                    if (localName.equalsIgnoreCase("subject")) {
                        listeAlign.add(nodeAlignment);
                        nodeAlignment = new NodeAlignment();
                        System.out.println("END_ELEMENT : " + localName);
                    }
                }
            }
 //           listeAlign.add(nodeAlignment);

        } catch (XMLStreamException ex) {
            Logger.getLogger(AAT_test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listeAlign;
    }

}
