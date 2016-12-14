/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.alignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author antonio.perez
 */
public class GpsQuery {

    private ArrayList<NodeAlignment> listeAlign;

    public ArrayList<NodeAlignment> queryGps(String idC, String idTheso,
            String lexicalValue, String lang,
            String requete) {
        listeAlign = new ArrayList<>();

        lexicalValue = lexicalValue.replaceAll(" ", "%20");
        requete = requete.replace("##lang##", lang);
        requete = requete.replace("##value##", lexicalValue);
        try {

            URL url = new URL(requete);
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
                NodeList nodes = doc.getElementsByTagName("geoname");

                // iterate the employees
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    NodeList p = element.getElementsByTagName("name");
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
                            if (lexicalValue.trim().equalsIgnoreCase(title.trim())) {
                                na.setConcept_target(title);
                                na.setDef_target(snippet);
                                na.setInternal_id_concept(idC);
                                na.setInternal_id_thesaurus(idTheso);
                                na.setThesaurus_target("Wikipedia");
                                na.setUri_target("https://" + lang + ".wikipedia.org/wiki/" + title.replaceAll(" ", "_"));
                                listeAlign.add(0, na);
                            } else {
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
            } catch (ParserConfigurationException | SAXException | IOException e) {
            }

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return listeAlign;
    }
}
