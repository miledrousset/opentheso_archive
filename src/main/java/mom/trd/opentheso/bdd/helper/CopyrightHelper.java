/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import mom.trd.opentheso.SelectedBeans.Connexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mom.trd.opentheso.bdd.tools.StringPlus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Miled.Rousset
 */
public class CopyrightHelper {
  
    private final Log log = LogFactory.getLog(CopyrightHelper.class);
    private boolean queryReturnResult;
    
    
    /**
    * cette fonction permet de retourner le copyright d'un thésaurus 
    * 
     * @param ds
     * @param idTheso
     * @return 
     * #MR
    */
    public String getCopyright(HikariDataSource ds, String idTheso) {
        String copyright = "";
        String query;
        Statement stmt;
        ResultSet resultSet;
        Connection conn;        

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                query = "SELECT copyright FROM copyright WHERE copyright.id_thesaurus='"+idTheso+"'";
                stmt.executeQuery(query);
                resultSet = stmt.getResultSet();
                if (resultSet.next()) {
                    copyright=resultSet.getString("copyright");
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            this.log.error("error while trying to proced result from database",ex);
        }
        return copyright;        
    }
    
    
    /**
     * cette fonction permet de mettre à jour le copyright du thésaurus
     * (dans ce cas, un copyright est déjà existant) 
     * @param ds
     * @param idTheso
     * @param copyright
     * @return 
     */
    public boolean updateCopyright(HikariDataSource ds, String idTheso, 
            String copyright){
        String query;
        Statement stmt;
        Connection conn;
        boolean status = false;
        copyright = new StringPlus().convertString(copyright);        

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                query = "update copyright SET copyright= '" +
                        copyright + "' WHERE id_thesaurus='" + idTheso + "'";
                stmt.executeUpdate(query);
                status = true; 
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            this.log.error("error while trying to update copyright",ex);
        }
        return status;      
    }

    /**
     * permet d'ajouter un copyright à un thésaurus
     * @param ds
     * @param idTheso
     * @param copyright
     * @return 
     */
    public boolean addCopyright(HikariDataSource ds, String idTheso, 
            String copyright){

        String query;
        Statement stmt;
        Connection conn;
        boolean status = false;
        copyright = new StringPlus().convertString(copyright);
        
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                query = "INSERT INTO copyright (id_thesaurus,copyright) VALUES ('"+
                        idTheso + "','" + copyright + "')";
                stmt.executeUpdate(query);
                status = true; 
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            this.log.error("error while trying to insert a copyright",ex);
        }
        return status;         
    }
    
    /**
     * permet de savoir si le thésaurus a un copyright 
     * @param ds
     * @param idThesaurus
     * @return 
     */
    public boolean isThesoHaveCopyRight(HikariDataSource ds,
            String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_thesaurus from copyright where "
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = true;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if theso have a copyright ", sqle);
        }
        return existe;
    }    

    /**
     * fonction hasQueryReturnResult
     * Getter pour le boolean queryReturnResult (voir manageResult)
     * @return boolean
     */
    public boolean hasQueryReturnResult() {
        return queryReturnResult;
    }

   
    
}
