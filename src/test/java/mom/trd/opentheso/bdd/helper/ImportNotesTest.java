/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.exports.tabulate.TabulateDocument;
import mom.trd.opentheso.core.imports.tabulate.ReadFileTabule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miled.rousset
 */
public class ImportNotesTest {

    public ImportNotesTest() {
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

    /**
     * Test of Tabulate reading.
     */
    @org.junit.Test

    public void testExportGroup() {
        // ouverture de la connexion
        HikariDataSource ds = openConnexionPool();

        Connection conn;
        Statement stmt;

        // lecture du fichier Notes au format SQL : exp :
        //    INSERT INTO note (id, notetypecode, id_thesaurus, id_term, id_concept, lang, lexicalvalue, created, modified) 
        //    VALUES (7, 'definition'## '1'## 'pcrt4dPYOENkZg'## ''## 'fr'## 'Potier athénien de figures noires, actif de 555 à 525'## '2015-05-26 16:48:45.86834'## '2015-05-26 16:48:45.86834');
        InputStreamReader isr;
        BufferedReader br;
        String ligne;
        String colonnes;
        String query;

        String colonnesV[];
        String valeursV[];
        
        int compteur = 0;

        String queryColonne = "insert into note (";

        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        try {
            InputStream inputStream = new FileInputStream("/Users/Miled/Desktop/notes22.sql");
            isr = new InputStreamReader(inputStream, "UTF-8");
            br = new BufferedReader(isr);

            try {
                while ((ligne = br.readLine()) != null) {
                    if (!ligne.isEmpty()) {
                        // detection des colonnes
                        if ((ligne.substring(0, 4)).equalsIgnoreCase("COPY")) {
                            colonnes = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")"));
                            // récupération desligne.substring(0, 5)) colonnes 
                            colonnesV = colonnes.split(",");
                            if (colonnesV.length == 0) {
                                System.err.println("Erreur de récupération des colonnes");
                                break;
                            }
                            columns.addAll(Arrays.asList(colonnesV));
                            for (int i = 0; i < columns.size(); i++) {
                                if (i != 0) {
                                    queryColonne +=  columns.get(i).trim();
                                    if (!(i == columns.size() - 1)) {
                                        queryColonne += ",";
                                    }
                                    else
                                        queryColonne += ")";
                                }
                            }

                        } else {

                            // récupération des valeurs
                            valeursV = ligne.split("\t");
                            values.addAll(Arrays.asList(valeursV));

                            // on teste si la récupération se passe bien, on écrit les données
                            if (columns.size() == values.size()) {

                                if (!columns.isEmpty()) {
                                    if (!values.isEmpty()) {
                                        // intégration des notes dans la BDD
                                        query = queryColonne + " values(";

                                        for (int i = 0; i < columns.size(); i++) {
                                            if (i == 0) {
                                                //    query += values.get(i).trim() + ",";
                                            } else {
                                                query += "'" + values.get(i).trim() + "'";
                                                if (!(i == columns.size() - 1)) {
                                                    query += ",";
                                                } else {
                                                    query += ")";
                                                }
                                            }
                                        }
                                    //    System.out.println(query);
                                                try {
                                            // Get connection from pool
                                            conn = ds.getConnection();
                                            try {
                                                stmt = conn.createStatement();
                                                try {
                                                    stmt.executeUpdate(query);
                                                    compteur++;
                                                } finally {
                                                    stmt.close();
                                                }
                                            } finally {
                                                conn.close();
                                            }
                                        } catch (SQLException sqle) {
                                            // Log exception
                                            System.err.println("Error while adding Note with query : " + query);
                                        }

                                        //    insert into note values (2, 'definition', '1', 'pcrtYqaRyYeIyU', '\N', 'fr', '(VII-VIe s. av.J.C.)', '2015-05-26 16:48:45.698545', '2015-05-26 16:48:45.698545')
                                        //   System.out.println(query);
                                        values.clear();
                                    }
                                }
//                                System.out.println(columns.toString());
//                                System.out.println(values.toString());
//                                values.clear();
                            } else {
                                System.err.println("Erreur à la ligne : " + ligne);
                            }
                        }
                        //    System.err.println(ligne);

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ImportNotesTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ImportNotesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        ds.close();
        System.out.println("Nombre de lignes ajoutées = " + compteur);

    }

    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(100);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        // Zoomathia
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "zoo");
        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");

        /*      config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "pactols");
        config.addDataSourceProperty("databaseName", "OTW");
         */
 /*
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "frantiq2014");
        config.addDataSourceProperty("databaseName", "pactols");

      //  config.addDataSourceProperty("serverName", "localhost");
        /*config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
         */
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;

    }

}
