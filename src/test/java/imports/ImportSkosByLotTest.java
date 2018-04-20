/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imports;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import mom.trd.opentheso.SelectedBeans.SelectedTerme;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import mom.trd.opentheso.core.imports.rdf4j.helper.ImportRdf4jHelper;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public void importSkosByLot() {
        String path = "/Users/Miled/Desktop/Lieux_nontrouvés_exportés.rdf";
        
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
                insertConcepts(sKOSXmlDocument);

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
    private void insertConcepts(SKOSXmlDocument sKOSXmlDocument) {

        String formatDate = "yyyy-MM-dd";
        String idThesaurus = "TH_1";
        String idGroup = "5";
                

        String identifierType = "ark";        
        ConnexionTest connexionTest = new ConnexionTest();
        conn = connexionTest.getConnexionPool();
        try {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(conn, formatDate, true, "adresse", 1, 1, /*langueBean.getIdLangue()*/ "fr");
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            importRdf4jHelper.setIdentifierType(identifierType);
            try {
                importRdf4jHelper.addLotOfConcepts(sKOSXmlDocument.getConceptList(),
                        idThesaurus, idGroup);
            } catch (SQLException ex) {
                 System.out.println(ex.getMessage());
            } catch (ParseException ex) {
                 System.out.println(ex.getMessage());
            } catch (Exception ex) {
                 System.out.println(ex.getMessage());
            }
        } catch (Exception e) {

        } finally {
        }
        conn.close();
    }



}
