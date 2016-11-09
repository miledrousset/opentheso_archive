/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.ws.rs.core.Configuration;
import mom.trd.opentheso.core.exports.privatesdatas.importxml.importxml;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.rdf.model.Property;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author antonio.perez
 */
public class BaseDeDoneesHelper implements Serializable {

    /**
     * Paremetres fixes
     */
    private final String dbDrvr = "org.postgresql.Driver";
    private String dbName = "";
    private final String dbHost = "jdbc:postgresql://localhost:5433/";
    private final String dbPort = "3306";
    
    private final Log log = LogFactory.getLog(ThesaurusHelper.class); 
    private Date datecreation;
    private String DBnameout;
    private String versionDB;
    private ArrayList<BaseDeDoneesHelper> infodor = new ArrayList<>();

   

    /**
     * Funtion principal de la class, creation de toutes les statements et de le
     * connections faire la BDD avec le nom proporcioné, inyection de les
     * tables, MAJ, et données de la basse;
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public boolean createBdD(String DBname) throws SQLException, ClassNotFoundException, IOException {
        dbName = DBname;
        HikariDataSource ds = new HikariDataSource();
        String aux="";
        importxml impo = new importxml();
        if (dbName != null) {
            String chaineTables;
            //String chaineUpdate;
            Statement stmt, stmt2, stmt3;
            Connection conn = conextion();
            try {
                //chaineUpdate = updateBDD();// chaineUpdate contiens la information pour faire le MAJ a la dernier version
                stmt = conn.createStatement();
                try {
                    String query = "create Database " + dbName + " with owner opentheso";
                    //stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                    conn.close();
                }
                Connection connuovelle = conextion2();//avec conextion2 il y a une connection a la BDD que on viens de créer
                stmt2 = connuovelle.createStatement();
                try {
                    //chaineTables = avoirContentpourTables(aux);// tout la information de touts le tables et "Insert into languages_iso639"
                    try {
                       // stmt2.execute(chaineTables);
                        //stmt3.execute(chaineUpdate);
                    } finally {
                        stmt2.close();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
                File fichero;
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter ft= new FileNameExtensionFilter("*.xml", "xml");
                fileChooser.setFileFilter(ft);
                fileChooser.setVisible(true);
                int seleccion = fileChooser.showOpenDialog(null);
                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    fichero = fileChooser.getSelectedFile();
                    //impo.ouvreFichier(connuovelle, fichero);
                }
                changerlaBDD(dbName);// Change la connection a la nouvelle BDD
                //remplirinfo(connuovelle, aux);
                connuovelle.close();
            } catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    /**
     * Funtion que permet de avoir une connection avec la BDD pour pouvoir faire
     * la nouvelle BDD; Nous donne un type connection
     *
     * @return
     * @throws ClassNotFoundException
     */
    public Connection conextion() throws ClassNotFoundException {
        Connection con = null;
        String usuaire = "postgres";
        String pass = "admin";
        try {
            Class.forName(dbDrvr);
            con = DriverManager.getConnection(dbHost, usuaire, pass);
        } catch (SQLException sqle) {
            return null;
        }

        return con;
    }

    /**
     * Ici nous pouvons avoir une conexion a la BDD que on viens de créer dbName
     * c'est le nombre que l'utilisateur a donné a la BDD; nous donne un type
     * connection
     *
     * @return
     * @throws ClassNotFoundException
     */
    public Connection conextion2() throws ClassNotFoundException {
        Connection con = null;
        String usuaire = "postgres";
        String pass = "admin";
        String dbHostFinal = "jdbc:postgresql://localhost:5433/" + dbName;
        try {
            Class.forName(dbDrvr);

            con = DriverManager.getConnection(dbHostFinal, usuaire, pass);
        } catch (SQLException sqle) {
            return null;
        }

        return con;
    }

    /**
     * Avec le fichier de données nous prendons tout pour faire le la creation
     * de tout les tables, se returne une String que se utilises dans la funtion
     * principal.
     *
     * @return
     */
    public static String avoirContentpourTables(String aux) throws IOException, SQLException {
        String sCadena = "";
        String retorno = "";
        boolean first = true;
        File fichero=null;
        
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filtre= new FileNameExtensionFilter("*.sql", "sql");
        fileChooser.setFileFilter(filtre);
        fileChooser.setVisible(true);
        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            fileChooser.setVisible(true);
            fichero = fileChooser.getSelectedFile();
        }
        aux =fileChooser.getName(fichero);
        try {

            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fichero), "UTF8"));
            while ((sCadena = bf.readLine()) != null) {
                if (!sCadena.contains("--")) {//ne prendre pas le lignes que commence par -- (contiens)
                    if (!sCadena.isEmpty()) {
                        if (!sCadena.contains("INSERT INTO")) {//efface toutes les lignes de "INSERT INTO"
                            retorno += sCadena;
                            retorno += "\n";
                        }
                        if (sCadena.contains("INSERT INTO languages_iso639")) {// moins le languages, ils sont de dont
                            retorno += sCadena;
                            retorno += "\n";
                        }
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

    /**
     * Avec le fichier de MAJ on prendre lo que besoin pour faire le maj, se
     * returne une String que se utilises dans la funtion principal.
     *
     * @return
     */
//    private static String updateBDD() {
//        String sCadena = "";
//        String retorno = "";
//        boolean first = true;
//        File fichier = new File("C:\\Users\\antonio.perez\\Desktop\\maj_BDD.txt");//Path de le fichier
//        if (!fichier.exists()) {
//            return null;
//        }
//        try {
//
//            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), "UTF8"));//utf8 pour avoir une bonne reconocenses de le caracters
//            while ((sCadena = bf.readLine()) != null) {
//                if (!sCadena.contains("--")) {//efface les lignes que contiens "--"
//                    if (!sCadena.isEmpty()) {// et les lignes que ils sont vide
//                        retorno += sCadena;
//                        retorno += "\n";
//                    }
//                }
//            }
//
//        } catch (FileNotFoundException fnfe) {
//            return null;
//        } catch (IOException ioe) {
//            return null;
//        }
//
//        return retorno;
//    }
    /**
     * Fait la recherche dans le fichier "hikari.properties" pour changer la
     * conexion a la BDD; La nouvelle conexion a la BDD serais avec le nom que
     * on a donné; Atention ne ferme pas la connection c ici!!!!!
     *
     * @param c
     * @param namebDD
     * @throws IOException
     * @throws SQLException
     */
    private static void changerlaBDD(String namebDD) throws IOException, SQLException {
        String sCadena = "";
        String premierparti = "";//contiens le fichier "hikari.properties" jusqu'à trouver le mot clés de la connection
        String ecrit = "dataSource.databaseName=" + namebDD; //nameBDD c'est le nom donné pour l'administrateur pour la BDD
        String deuxiemeparti = "";
        boolean first = true;
        boolean sault = false;
        String fichi = null;
        Properties p = new Properties();
        try {
            p.load(Configuration.class.getClassLoader().getResourceAsStream("hikari.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String algo = p.toString();
        String test2 = p.getProperty("dataSource.databaseName");
        System.out.println(test2);
        p.setProperty("dataSource.databaseName", "miled");
        //FileOutputStream fos = new FileOutputStream(file);
        //p.store(fos, null);
        String test1 = p.getProperty("dataSource.databaseName");
        System.out.println(test1);
        File fichier = new File("C:\\Users\\antonio.perez\\Documents\\NetBeansProjects\\opentheso\\src\\main\\resources\\hikari.properties");//path fichier
        if (fichier.exists()) {
            try {

                BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), "UTF8"));
                while ((sCadena = bf.readLine()) != null) {//on fait la comprobation de que la ligne n'est pas vide
                    if (!sCadena.contains("#")) {//et que la ligne n'est pas commenté (le lignes commentes ne marche pas)

                        if (sCadena.contains("dataSource.databaseName")) {
                            //quand se trouve les mot clés, c'est fini de incluire dans la premierparti; first=false;
                            first = false;
                            sault = true;// avec ça, ne introduire pas la ligne de le mot clés
                        }
                    }
                    if (first)//jusqu'à trouver dataSource.databaseName
                    {
                        premierparti += sCadena;
                        premierparti += "\r\n";//\r c'est pour le reconosences de le fichier de le \n
                    } else if (!sault) {
                        deuxiemeparti += sCadena;// apres trouver dataSource.databaseName
                        deuxiemeparti += "\r\n";
                    }
                    sault = false;
                }
                bf.close();
            } catch (FileNotFoundException fnfe) {

            } catch (IOException ioe) {

            }
            PrintWriter wr = null;
            try {
                wr = new PrintWriter(fichier);// ecrit dans le fichier ouvert
                wr.print(premierparti);
                wr.println(ecrit);// il est dans la definition, "dataSource.databaseName=" + nameBDD
                wr.println(deuxiemeparti);

            } catch (Exception e) {
                e.printStackTrace();
            }
            wr.close();
        }
    }

    private void remplirinfo(Connection conn, String  dbver) throws SQLException {
        java.util.Date datetoday = new java.util.Date();
        Statement stmt;
        if(dbver !=null)
        {            
            try {
                try {
                    stmt = conn.createStatement();
                    try {
                        String query=" insert into infodb values ('"
                                +datetoday+"', '"
                                +dbName+"', '"
                                +dbver+"');";
                        stmt.executeQuery(query);
                    }
                    finally {
                        stmt.close();
                    }
                } 
                finally {
                    conn.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE,null,ex);
            }
        }

    }
    public ArrayList<BaseDeDoneesHelper> info_out(HikariDataSource ds) throws SQLException, IOException, XmlPullParserException
    {
        BaseDeDoneesHelper outinfo = new BaseDeDoneesHelper();
        Statement stmt = null;
        Connection conn;
        ResultSet rs;
         try {
            conn = ds.getConnection();
            try
            { 
                stmt = conn.createStatement();
                String query="Select * from infodb  where id =(select max(id) from infodb)";
                rs=stmt.executeQuery(query);
                if(rs.next())
                {
                   outinfo.setDatecreation(rs.getDate("created"));
                   outinfo.setDBnameout(rs.getString("nomdb"));
                   outinfo.setVersionDB(rs.getString("version"));  
                }
                infodor.add(outinfo);
            }finally{
                stmt.close();
            }
         }catch (SQLException sqle) {
            
            log.error("Error while load info : ");
                     
         }
        return infodor;
    }
    public String getDbName() {
        return dbName;//seulement en minuscule!!!!
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Date getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(Date datecreation) {
        this.datecreation = datecreation;
    }

    public String getDBnameout() {
        return DBnameout;
    }

    public void setDBnameout(String DBnameout) {
        this.DBnameout = DBnameout;
    }

    public String getVersionDB() {
        return versionDB;
    }

    public void setVersionDB(String versionDB) {
        this.versionDB = versionDB;
    }
    
}
