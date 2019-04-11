package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
//import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;



@ManagedBean (name = "poolConnexion", eager = true)
@ApplicationScoped
public class Connexion implements Serializable {
//    Logger logger = LoggerFactory.getLogger(Connexion.class);
    static Logger logger = Logger.getLogger(Connexion.class);
    
    private static final long serialVersionUID = 1L;
    
    private HikariDataSource poolConnexion = null;
    private String workLanguage = "fr";
    private String defaultThesaurusId;
    
    
    @PostConstruct
    public void initPref() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
        workLanguage = bundlePref.getString("workLanguage");
        defaultThesaurusId = bundlePref.getString("defaultThesaurusId");
        
    }    
    
    public ResourceBundle getBundlePool(){
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            ResourceBundle bundlePool = context.getApplication().getResourceBundle(context, "conHikari");
            return bundlePool;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return null;
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
        logger.setLevel(Level.ERROR);

        ResourceBundle resourceBundle = getBundlePool();
        if(resourceBundle == null){
            // retour pour installation
            return null;
        }        
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", resourceBundle.getString("dataSourceClassName"));
        props.setProperty("dataSource.user", resourceBundle.getString("dataSource.user"));
        props.setProperty("dataSource.password", resourceBundle.getString("dataSource.password"));
        props.setProperty("dataSource.databaseName", resourceBundle.getString("dataSource.databaseName"));
        
        props.setProperty("dataSource.serverName", resourceBundle.getString("dataSource.serverName"));
        props.setProperty("dataSource.portNumber", resourceBundle.getString("dataSource.serverPort"));        
        
    //    props.put("dataSource.logWriter", new PrintWriter(System.out));
        
        
        HikariConfig config = new HikariConfig(props);

        config.setMinimumIdle(Integer.parseInt(resourceBundle.getString("minimumIdle")));
        config.setMaximumPoolSize(Integer.parseInt(resourceBundle.getString("setMaximumPoolSize")));
        config.setIdleTimeout(Integer.parseInt(getBundlePool().getString("idleTimeout")));
        config.setConnectionTimeout(Integer.parseInt(getBundlePool().getString("connectionTimeout")));        
        config.setAutoCommit(true);

        
        
    //   config.setIdleTimeout(Integer.parseInt(getBundlePool().getString("idleTimeout")));
    //   config.setConnectionTimeout(Integer.parseInt(getBundlePool().getString("connectionTimeout")));
    //  config.setJdbc4ConnectionTest(false);
    

    /*    config.setConnectionTestQuery(resourceBundle.getString("connectionTestQuery"));
        config.setDataSourceClassName(resourceBundle.getString("dataSourceClassName"));
        
        config.addDataSourceProperty("user", resourceBundle.getString("dataSource.user"));
        config.addDataSourceProperty("password", resourceBundle.getString("dataSource.password"));
        config.addDataSourceProperty("databaseName", resourceBundle.getString("dataSource.databaseName"));
        config.addDataSourceProperty("serverName", resourceBundle.getString("dataSource.serverName"));
        config.addDataSourceProperty("portNumber", resourceBundle.getString("dataSource.serverPort"));

        config.addDataSourceProperty("dataSource.logWriter", new PrintWriter(System.out));*/
        
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        try {
    //        poolConnexion1.getParentLogger().getLevel(); //setLevel(Level.FATAL);
            poolConnexion1.getConnection();

        } catch (SQLException ex) {
            logger.error(ex.toString());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, ex.getClass().getName(), ex.getMessage()); 
            FacesContext.getCurrentInstance().addMessage(null, message);
            poolConnexion1.close();
            return null;
        }
        return poolConnexion1;
     }

    public void closeConnexion() {
        if(poolConnexion != null)
            poolConnexion.close();
    }

    void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getWorkLanguage() {
        return workLanguage;
    }

    public void setWorkLanguage(String workLanguage) {
        this.workLanguage = workLanguage;
    }

    public String getDefaultThesaurusId() {
        return defaultThesaurusId;
    }

    public void setDefaultThesaurusId(String defaultThesaurusId) {
        this.defaultThesaurusId = defaultThesaurusId;
    }
    
}
