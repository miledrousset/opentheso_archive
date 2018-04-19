/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imports;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.faces.event.PhaseId;
import mom.trd.opentheso.SelectedBeans.SelectedTerme;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import mom.trd.opentheso.core.exports.rdf4j.helper.ExportRdf4jHelper;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import mom.trd.opentheso.core.imports.rdf4j.helper.ImportRdf4jHelper;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Exceptions;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author miled.rousset
 */
public class ImportSkosByLotTest {

    private HikariDataSource conn;

    public ImportSkosByLotTest() {
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

    @Test
    private void importSkosByLot() {
        String path = "/Users/Miled/Desktop/Lieux PACTOLS/lieux_supprimés.csv";
        
        SKOSXmlDocument sKOSXmlDocument;
        InputStream is = null;
            try {
                try {
                    is = new FileInputStream(path);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                ReadRdf4j readRdf4j = null;
                try {
                    readRdf4j = new ReadRdf4j(is, 0, null);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());                
                }
                if (readRdf4j == null) {
                    return;
                }
                sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                int total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                String uri = sKOSXmlDocument.getTitle();


            } catch (Exception e) {
                    System.out.println(e.getMessage());
            } finally {
            }

        }
    
        /**
     * ajoute un seul concept a la base de données
     *
     * @param idUser
     * @param idRole
     * @param selectedTerme
     */
    private void insertConcepts(SelectedTerme selectedTerme) {
   /*     info = "";
        error = "";
        warning = "";
        uri = "";
        formatDate = "yyyy-MM-dd";
        total = 0;
        uploadEnable = true;
        BDDinsertEnable = false;
        identifierType = "sans";        
        
        try {

        } catch (Exception e) {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(conn, formatDate, uploadEnable, "adresse", idUser, idRole, /*langueBean.getIdLangue() "fr");
 /*           importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addSingleConcept(selectedTerme);
            } catch (SQLException ex) {
                error = ex.getMessage();
            } catch (ParseException ex) {
                error = ex.getMessage();
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            tree.reInit();
            tree.reExpand();
            tree.getSelectedTerme().majTerme((MyTreeNode) tree.getSelectedNode());

            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;
        } finally {
            showError();
        }*/

    }



}
