/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import mom.trd.opentheso.core.exports.rdf4j.helper.ExportRdf4jHelper;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author miled.rousset
 */
public class ExportSkosByLotTest {

    private HikariDataSource conn;

    public ExportSkosByLotTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void exportSkosByLot() throws FileNotFoundException, IOException {
        BufferedReader bf = null;
        try {

            ConnexionTest connexionTest = new ConnexionTest();
            conn = connexionTest.getConnexionPool();
            String idTheso = "TH_1";
            // lecture du fichier tabulé /Users/Miled/
            String path = "/Users/Miled/Desktop/Lieux PACTOLS/lieux_supprimés.csv";
            //    String path = "C:/Users/jm.prudham/Desktop/candidatsPactols.csv";
            File fileReader = new File(path);
            String line;
            ArrayList<String> idConcepts = new ArrayList<>();
            //    stringBuilder.append("Numéro BDD\tnom d'origine\tnom PACTOLS\tId PACTOLS\tURL Ark\tDéfinition\tTerme générique\tSynonyme\n");

            //    new InputStreamReader(file));
            //  BufferedWriter bw = openFile("/Users/Miled/Desktop/Lieux_nontrouvés_exportés.rdf");
            //  if(bw == null) return;
            int i = 0;
            //    File file = new File(path);
            File file = new File("/Users/Miled/Desktop/Lieux_nontrouvés_exportés.rdf");
            FileOutputStream fos = null;

            bf = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileReader), "UTF8"));
            while ((line = bf.readLine()) != null) {
                idConcepts.add(line.trim());
            }

            WriteRdf4j writeRdf4j = loadExportHelper(idTheso, idConcepts);
            ByteArrayOutputStream out;
            out = new ByteArrayOutputStream();
            Rio.write(writeRdf4j.getModel(), out, RDFFormat.RDFXML);
            StreamedContent streamedContent = new ByteArrayContent(out.toByteArray(), "application/xml", "export." + "rdf");
            conn.close();
            try {

                fos = new FileOutputStream(file);

                // Writes bytes from the specified byte array to this file output stream
                fos.write(out.toByteArray());

            } catch (FileNotFoundException e) {

                System.out.println("File not found" + e);

            } catch (IOException ioe) {

                System.out.println("Exception while writing file " + ioe);

            } finally {

                // close the streams using close method
                try {

                    if (fos != null) {

                        fos.close();

                    }

                } catch (IOException ioe) {

                    System.out.println("Error while closing stream: " + ioe);

                }

            }

        } catch (UnsupportedEncodingException ex) {

            System.out.println(ex);

        } finally {

            try {
                if(bf!=null)
                    bf.close();

            } catch (IOException ex) {

                System.out.println(ex);

            }

        }

        //  bw.append(streamedContent.getStream().toString());
//        } catch (IOException ex) {
//            Logger.getLogger(CompareConceptTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
        //   closeFile(bw);
    }

    private WriteRdf4j loadExportHelper(String idTheso, ArrayList<String> idConcepts) {

        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreference(conn, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(conn, "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        for (String idConcept : idConcepts) {
            exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        }

        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }

    private BufferedWriter openFile(String path) {
        try {
            BufferedWriter br = new BufferedWriter(
                    (new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)));
            return br;
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return null;
    }

    private void closeFile(BufferedWriter bw) {
        try {
            bw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
