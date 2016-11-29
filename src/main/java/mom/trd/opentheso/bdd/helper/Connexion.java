package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


@ManagedBean (name = "poolConnexion", eager = true)
@ApplicationScoped


public class Connexion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private HikariDataSource poolConnexion = null;
    
    private HikariDataSource poolConnexionInstall;
    
    /*
    Variables de conf pour Hikari pour l'install
    */
    private Integer minimumIdle = 1;
    private Integer setMaximumPoolSize = 1000;
    private boolean autoCommit = true;
//   config.setIdleTimeout(Integer.parseInt(getBundlePool().getString("idleTimeout")));
//   config.setConnectionTimeout(Integer.parseInt(getBundlePool().getString("connectionTimeout")));
//  config.setJdbc4ConnectionTest(false);
    private String connectionTestQuery = "SELECT 1";
    private String dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource";

    private String user;
    private String password;
    private String databaseName;
    private String serverName;
    private String serverPort;
    
    
    public ResourceBundle getBundlePool() {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundlePool = context.getApplication().getResourceBundle(context, "conHikari");
            return bundlePool;
    }

    public Connexion() {
        this.poolConnexion = openConnexionPool();
    }

    public HikariDataSource getPoolConnexion() {
        return poolConnexion;
    }

    public void setPoolConnexion(HikariDataSource poolConnexion) {
        this.poolConnexion = poolConnexion;
    }

    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(Integer.parseInt(getBundlePool().getString("minimumIdle")));
        config.setMaximumPoolSize(Integer.parseInt(getBundlePool().getString("setMaximumPoolSize")));
        config.setAutoCommit(true);
    //   config.setIdleTimeout(Integer.parseInt(getBundlePool().getString("idleTimeout")));
    //   config.setConnectionTimeout(Integer.parseInt(getBundlePool().getString("connectionTimeout")));
    //  config.setJdbc4ConnectionTest(false);
        config.setConnectionTestQuery(getBundlePool().getString("connectionTestQuery"));
        config.setDataSourceClassName(getBundlePool().getString("dataSourceClassName"));
        
        config.addDataSourceProperty("user", getBundlePool().getString("dataSource.user"));
        config.addDataSourceProperty("password", getBundlePool().getString("dataSource.password"));
        config.addDataSourceProperty("databaseName", getBundlePool().getString("dataSource.databaseName"));
        config.addDataSourceProperty("serverName", getBundlePool().getString("dataSource.serverName"));
        config.addDataSourceProperty("portNumber", getBundlePool().getString("dataSource.serverPort"));
        
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        try {
            poolConnexion1.getConnection();
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
     * Permet d'initialiser les variables de conf pour Hikari
     */
    public boolean validateConf() {
        return true;
    }
    
    /**
     * Cette fonction permet de créer le Pool de connexion à l'installation  
     */
    public void openConnexionPoolInstall() {
        if(!validateConf()) return;
        
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(setMaximumPoolSize);
        config.setAutoCommit(autoCommit);
        config.setConnectionTestQuery(connectionTestQuery);
        config.setDataSourceClassName(dataSourceClassName);
        
        config.addDataSourceProperty("user", user);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("databaseName", databaseName);
        config.addDataSourceProperty("serverName", serverName);
        config.addDataSourceProperty("portNumber", serverPort);
        
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        try {
            poolConnexion1.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, ex.getClass().getName(), ex.getMessage()); 
            FacesContext.getCurrentInstance().addMessage(null, message);
            poolConnexion1.close();
            return;
        }
        poolConnexionInstall = poolConnexion1;
     }    
    
    
    public void closeConnexion() {
        if(poolConnexion != null)
            poolConnexion.close();
    }

    void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(Integer minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public Integer getSetMaximumPoolSize() {
        return setMaximumPoolSize;
    }

    public void setSetMaximumPoolSize(Integer setMaximumPoolSize) {
        this.setMaximumPoolSize = setMaximumPoolSize;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    
    public String getConnectionTestQuery() {
        return connectionTestQuery;
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
    }

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public HikariDataSource getPoolConnexionInstall() {
        return poolConnexionInstall;
    }

    public void setPoolConnexionInstall(HikariDataSource poolConnexionInstall) {
        this.poolConnexionInstall = poolConnexionInstall;
    }
    
    public void test() {
        int i = 0;
    }


    
}
