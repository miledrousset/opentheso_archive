/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.tools;

import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.swing.JFileChooser;
import mom.trd.opentheso.SelectedBeans.DownloadBean;
import mom.trd.opentheso.core.exports.privatesdatas.importxml.importxml;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "CreerBD", eager = true)
@SessionScoped

public class CreateBDD {

    private final String dbDrvr = "org.postgresql.Driver";
    private String dbName = "";
    private final String dbHost = "jdbc:postgresql://localhost:5433/";
    private final String dbPort = "3306";

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void createBdD() throws SQLException, ClassNotFoundException, IOException {
        DownloadBean injection = new DownloadBean();
        importxml impo = new importxml();
        if (dbName != null) {
            String chaineTables = "";
            String chaineUpdate = "";
            ResultSet resul;
            Statement stmt, stmt2, stmt3;
            Connection conn = conextion();
            try {
                System.out.println(dbName);
                chaineUpdate = updateBDD();
                stmt = conn.createStatement();
                try {

                    String query = "create Database " + dbName + " with owner opentheso";
                    stmt.executeUpdate(query);
                    System.out.println("BDD hecha)");
                } finally {
                    stmt.close();
                    conn.close();
                }
                Connection connuovelle = conextion2();
                stmt3 = stmt2 = connuovelle.createStatement();
                try {
                    chaineTables = avoirContentpourTables(connuovelle);
                    System.out.println("archivo transformado Strign");
                    try {
                        stmt2.execute(chaineTables);
                        System.out.println("tablas dentro");
                        stmt3.execute(chaineUpdate);
                        System.out.println("MAJ dentro");
                    } finally {
                        stmt2.close();
                        stmt3.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Connection conn = ds.getConnection();
                File fichero;
                System.out.println("pa buscar el fichero");
                JFileChooser fileChooser = new JFileChooser();
                int seleccion = fileChooser.showOpenDialog(null);
                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    System.out.println("a mandar datos dentro");
                    fichero = fileChooser.getSelectedFile();
                    impo.ouvreFichier(connuovelle, fichero);
                }
                connuovelle.close();
            } catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Connection conextion() throws ClassNotFoundException {
        Connection con = null;
        String usuaire = "postgres";
        String pass = "admin";
        try {
            Class.forName(dbDrvr);

            con = DriverManager.getConnection(dbHost, usuaire, pass);
            System.out.println("he entrado conn1");
        } catch (SQLException sqle) {
            return null;
        }

        return con;
    }

    public Connection conextion2() throws ClassNotFoundException {
        Connection con = null;
        String usuaire = "postgres";
        String pass = "admin";
        String dbHostFinal = "jdbc:postgresql://localhost:5433/" + dbName;
        try {
            Class.forName(dbDrvr);

            con = DriverManager.getConnection(dbHostFinal, usuaire, pass);
            System.out.println("toy saliendo conn2");
        } catch (SQLException sqle) {
            return null;
        }

        return con;
    }

    private static String avoirContentpourTables(Connection c) throws IOException, SQLException {
        String sCadena = "";
        String retorno = "";
        boolean first = true;

        File fichier = new File("C:\\Users\\antonio.perez\\Documents\\NetBeansProjects\\opentheso\\src\\main\\resources\\install\\opentheso_dist_4.0.9.sql");
        if (!fichier.exists()) {
            return null;
        }
        try {

            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), "UTF8"));
            while ((sCadena = bf.readLine()) != null) {
                if (!sCadena.contains("--")) {
                    if (!sCadena.isEmpty()) {
                        retorno += sCadena;
                        retorno += "\n";
                    }
                }
            }

        } catch (FileNotFoundException fnfe) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
        return retorno;
    }

    private static String updateBDD() {
        String sCadena = "";
        String retorno = "";
        boolean first = true;
        File fichier = new File("C:\\Users\\antonio.perez\\Desktop\\maj_BDD.txt");
        if (!fichier.exists()) {
            return null;
        }
        try {

            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), "UTF8"));
            while ((sCadena = bf.readLine()) != null) {
                if (!sCadena.contains("--")) {
                    if (!sCadena.isEmpty()) {
                        retorno += sCadena;
                        retorno += "\n";
                    }
                }
            }

        } catch (FileNotFoundException fnfe) {
            return null;
        } catch (IOException ioe) {
            return null;
        }

        return retorno;
    }
}
