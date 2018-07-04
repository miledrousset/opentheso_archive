/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.ws.soap;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import mom.trd.opentheso.SelectedBeans.Connexion;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;

/**
 *
 * @author miled.rousset
 */
@WebService(serviceName = "soap")
public class Soap {

    private HikariDataSource ds;
    private Properties prefs;
    
    public Soap() {
        Properties properties= new Properties();
        prefs = new Properties();
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("hikari.properties");
            if(inputStream != null) {
                properties.load(inputStream);
                this.ds = openConnexionPool(properties);
            }
            InputStream inputStream2 = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("preferences.properties");
            if(inputStream2 != null) {
                prefs.load(inputStream2);
            }
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }
     
    private HikariDataSource openConnexionPool(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("minimumIdle")));
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("setMaximumPoolSize")));
        config.setAutoCommit(true);
        config.setIdleTimeout(Integer.parseInt(properties.getProperty("idleTimeout")));
        config.setConnectionTimeout(Integer.parseInt(properties.getProperty("connectionTimeout")));
        config.setConnectionTestQuery(properties.getProperty("connectionTestQuery"));
        config.setDataSourceClassName(properties.getProperty("dataSourceClassName"));
        
        config.addDataSourceProperty("user", properties.getProperty("dataSource.user"));
        config.addDataSourceProperty("password", properties.getProperty("dataSource.password"));
        config.addDataSourceProperty("databaseName", properties.getProperty("dataSource.databaseName"));
        config.addDataSourceProperty("serverName", properties.getProperty("dataSource.serverName"));
        config.addDataSourceProperty("portNumber", properties.getProperty("dataSource.serverPort"));
        
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        try {
            Connection conn = poolConnexion1.getConnection();
            if(conn == null) return null;
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, ex.getClass().getName(), ex.getMessage()); 
            FacesContext.getCurrentInstance().addMessage(null, message);
            poolConnexion1.close();
            return null;
        }
     
        return poolConnexion1;
     }
    
    /**
     * Web service operation
     * @param idConcept
     * @param idThesaurus
     * @return 
     */
    @WebMethod(operationName = "conceptToSkos")
    public String conceptToSkos(@WebParam(name = "idConcept") String idConcept, @WebParam(name = "idThesaurus") String idThesaurus) {
                        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        String skos = exportFromBDD.exportConcept(ds, idThesaurus, idConcept).toString();
        return skos;
    }

    /**
     * Web service operation
     * @param value
     * @param idThesaurus
     * @param lang
     * @return 
     */
    @WebMethod(operationName = "multiConceptToSkos")
    public String multiConceptToSkos(@WebParam(name = "value") String value, @WebParam(name = "idThesaurus") String idThesaurus, @WebParam(name = "lang") String lang) {
        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        
        String skos = exportFromBDD.exportMultiConcept(
                ds, idThesaurus, value, lang).toString();
        return skos;
    }
}
