/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author antonio.perez
 */
public class testcreationconexionBD {
    
    public testcreationconexionBD() {
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
    
    // public void hello() {}
            
    private final String dbDrvr = "com.mysql.jdbc.Driver"; 
    private String namBDD = "prueba"; 
    private final String dbHost = "jdbc:mysql://localhost:3306/" + namBDD; 
    private final String dbPort = "3306"; 
    private String mensajeError = ""; 

    public String getNamBDD() {
        return namBDD;
    }

    public void setNamBDD(String nameBDD) {
        this.namBDD = nameBDD;
    }
    @Test
    public  void avoirContent(HikariDataSource ds) {

        String sCadena = "";
        String retorno = "";
        Statement stmt;
        Connection conn;
        
        File  fichier = new File("C:\\Users\\antonio.perez\\Documents\\NetBeansProjects\\opentheso\\src\\main\\resources\\install\\opentheso_dist_4.0.9.sql");
        if (fichier.exists()) {
           
            try {

                BufferedReader bf = new BufferedReader(new FileReader(fichier));
                while ((sCadena = bf.readLine()) != null) {
                    retorno += sCadena;
                }

            } catch (FileNotFoundException fnfe) {

            } catch (IOException ioe) {
  
            }
        
            try 
            {
                Connection connection = ds.getConnection();
                stmt = connection.createStatement();
                try 
                {
                    // récupération des noms des colonnes de la table
                    String query = "create Database prueba with owner opentheso";
                    System.out.println(query);
                    stmt.executeUpdate(query);
                }
                finally {
                        stmt.close();
                        connection.close();
                }
            } 
            catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    } 
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
