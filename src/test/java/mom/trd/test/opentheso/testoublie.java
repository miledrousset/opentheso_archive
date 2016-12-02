/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.tools.MD5Password;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author antonio.perez
 */
public class testoublie {

    public testoublie() {
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
    // public void hello() {}
    public void faireRechercheEmail() throws SQLException {
        HikariDataSource conn = openConnexionPool();
        String email = "iii";
        String nouvellePass = "";
        vide(conn, email);
        ResultSet resultSet, resultSet2;
    }

    private void vide(HikariDataSource ds, String email) throws SQLException {
        String nouvellePass ="";
        Statement stmt;
        ResultSet resultSet;
        try {
            Connection conn = ds.getConnection();
            stmt = conn.createStatement();
            try {
                String query = "Select username from users where mail ='" + email +"'";
                resultSet = stmt.executeQuery(query);
                if (resultSet != null) {
                    nouvellePass = MD5Password.getEncodedPassword(genererNouvellePass());
                    String queryAjoute = "alter table users add motpasstemp varchar(100)";
                    stmt.executeQuery(queryAjoute);
                    }
            } finally {
                stmt.close();
                conn.close();
                insertNP(ds, nouvellePass, email);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void insertNP(HikariDataSource ds, String nouvellePass, String email)
    {
        Statement stmt;
        try {
            Connection conn = ds.getConnection();
            stmt = conn.createStatement();
            try {
                String queryAjouPass = "update users set motpasstemp ='"+ nouvellePass + "' where mail = '" + email + "'";   
                stmt.executeQuery(queryAjouPass);
                } finally {
                stmt.close();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String genererNouvellePass() {
        String code = "";
        int sum = 0;
        String[] alfa = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                 "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        while (sum < 10) {
            int numRandon = (int) Math.round(Math.random() * 36);
            code += alfa[numRandon];
            sum++;
        }
        System.out.println(code);
        return code;
    }

    /*
        try {

            Connection conn = ds.getConnection();
            stmt = conn.createStatement();
            try {
                // récupération des noms des colonnes de la table
                String query = "Select username from user where mail =" + email;
                resultSet = stmt.executeQuery(query);
                if (resultSet!= null)
                {
                    nouvellePass=genererNouvellePass();
                    //envoyerNouvellePass();
                    String queryAjoute ="alter table users add motpasstemp varchar(10)";
                    resultSet2 = stmt.executeQuery(queryAjoute);
                    String queryAjouPass ="update users set motpasstemp ='"
                            + nouvellePass +"' where mail = '" + email +"'"; 

                    
                }
                
            } finally {
                stmt.close();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
     */

    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        config.setMinimumIdle(1);
        config.setAutoCommit(true);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);

        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "otw_test");

        config.addDataSourceProperty("serverName", "localhost");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }
}
