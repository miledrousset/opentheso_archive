/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;

/**
 *
 * @author antonio.perez
 */
public class BaseDeDoneesHelper implements Serializable {

    /**
     * Paremetres fixes
     */
    
    /**
     * Permet de savoir si le nom de le utilisateur déjà exist o pas
     * @param ds
     * @param nomAdmin
     * @return 
     */
    public boolean isUserExist(HikariDataSource ds, String nomAdmin)
    {
        ResultSet resultSet;
        try {
            Connection conn = ds.getConnection();
            Statement stmt;
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT usename from pg_user";
                    resultSet = stmt.executeQuery(query);
                    while(resultSet.next())
                    {
                        if(resultSet.getString("usename").equals(nomAdmin)) return true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, sqle);
                return false;
        }
        return false;
    }
    
    public boolean isBddExist(HikariDataSource ds, String databasename)
    {
        ResultSet resultSet;
        try {
            Connection conn = ds.getConnection();
            Statement stmt;
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT datname from pg_database;";
                    resultSet = stmt.executeQuery(query);
                    while(resultSet.next())
                    {
                        if(resultSet.getString("datname").equals(databasename)) return true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, sqle);
                return false;
        } 
        return false;
    }
    /**
     * 
     * @param ds
     * @param nomAdmin
     * @param pass
     * @return 
     */
    public boolean createUser(HikariDataSource ds, String nomAdmin, String pass)
    {
        try {
            Connection conn = ds.getConnection();
            Statement stmt;
            try {
                stmt = conn.createStatement();
                try {
                    String query = "CREATE USER "+ nomAdmin
                            +" PASSWORD '"+ pass+"';";
                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, sqle);
                return false;
        }
        return true;
    }
    
    /**
     * Funtion principal de la class, creation de toutes les statements et de le
     * connections faire la BDD avec le nom proporcioné, inyection de les
     * tables, MAJ, et données de la basse;
     *
     * @param ds
     * @param DBname
     * @param user
     * @return
     */
    public boolean createBdD(HikariDataSource ds, String DBname, String user) {
        if (DBname != null) {
            Statement stmt;
            try {
                Connection conn = ds.getConnection();
                stmt = conn.createStatement();
                try {
                    String query = "create Database " + DBname +" OWNER "+ user;
                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    
//    public boolean changerPermis(HikariDataSource ds, String nomAdmin, String databaseName)
//    {
//        Statement stmt;
//            try {
//                Connection conn = ds.getConnection();
//                stmt = conn.createStatement();
//                try {
//                    String query = "GRANT ALL PRIVILEGES ON DATABASE "+ databaseName+" to "+nomAdmin;
//                    stmt.executeUpdate(query);
//                    query = "ALTER DATABASE "+ databaseName+" owner to "+nomAdmin;
//                    stmt.execute(query);
//                } finally {
//                    stmt.close();
//                    conn.close();
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
//                return false;
//            }
//    return true;
//    }
    
    
    
    
    
    /**
     * Funtion que permet de avoir une connection avec la BDD pour pouvoir faire
     * la nouvelle BDD; Nous donne un type connection
     *
     * @param ds
     * @param inputStream
     * @param userName
     * @return
     */
    public boolean insertDonneées(HikariDataSource ds, InputStream inputStream, String userName) {
        Statement stmt;
        String scriptBdd;
        try {
            Connection conn = ds.getConnection();
            stmt = conn.createStatement();
            scriptBdd = prepareScript(inputStream,userName);// tout la information de touts le tables et "Insert into languages_iso639"
            try {
                stmt.execute(scriptBdd);
            } finally {
                stmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Avec le fichier de données nous prendons tout pour faire le la creation
     * de tout les tables, se returne une String que se utilises dans la funtion
     * principal.
     *
     * @param inputstream
     * @param userName
     * @return
     */
    public String prepareScript(InputStream inputstream, String userName) {
        String line;
        String retorno = "";

        BufferedReader bf;

        try {
            bf = new BufferedReader(new InputStreamReader(inputstream, "UTF8"));
            while ((line = bf.readLine()) != null) {
            if (!line.contains("--")) {//ne prendre pas le lignes que commence par -- (contiens)
                if (!line.isEmpty()) {
                    if( line.contains("SET role = opentheso"));
                    {                            
                        line = line.replace("opentheso", userName);
                  
                    }
                    retorno += line;
                    retorno += "\n";
                }
            }
        }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BaseDeDoneesHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BaseDeDoneesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }


}
