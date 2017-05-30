/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import com.google.common.io.Files;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.junit.Test;
import org.xml.sax.SAXException;

import org.apache.fop.apps.*;
import org.apache.avalon.framework.configuration.Configuration;import org.apache.avalon.framework.configuration.ConfigurationException;
/**
 *
 * @author Quincy
 */
public class ApacheFOP {

    @Test
    public void start_test() {

        try {

            test_basic();

        } catch (SAXException ex) {
            Logger.getLogger(ApacheFOP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ApacheFOP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ApacheFOP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConfigurationException ex) {
            Logger.getLogger(ApacheFOP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ApacheFOP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void test_basic() throws SAXException, IOException, TransformerConfigurationException, TransformerException, ConfigurationException, URISyntaxException {

        // Step 1: Construct a FopFactory by specifying a reference to the configuration file
        // (reuse if you plan to render multiple documents!)
        

        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        Configuration cfg = cfgBuilder.buildFromFile(new File("fop-config.xml"));
        URI baseURI =new URI("https://www.testUri.com");
        FopFactoryBuilder builder;
        builder = new FopFactoryBuilder(baseURI).setConfiguration(cfg);
        
        FopFactory fopFactory = builder.build();

        // Step 2: Set up output stream.
        // Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("test-fop.pdf")));

        try {
            // Step 3: Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // Step 4: Setup JAXP using identity transformer
            Source xslt = new StreamSource(new File("skos-alpha.xsl"));
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslt); // identity transformer

            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream
            Source src = new StreamSource(new File("test_unesco.rdf"));

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(src, res);

        } finally {
            //Clean-up
            out.close();
        }

    }

    public void test_sparna() throws IOException, TransformerConfigurationException, TransformerException {
        String input = readFile("test_unesco.rdf");
        System.out.println(input);

        //init XSLT
        File initialFile = new File("skos-alpha.xsl");
        InputStream is = Files.asByteSource(initialFile).openStream();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer t = factory.newTransformer(new StreamSource(is));

        //Apply XSLT
        StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(input.getBytes("UTF-8")));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult xslResult = new StreamResult(new OutputStreamWriter(baos, "UTF-8"));
        t.setParameter("docId", "jeTestAvecRandomId");

        t.transform(xmlSource, xslResult);

        System.out.println("------------------------------------");

    }

    String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

}
