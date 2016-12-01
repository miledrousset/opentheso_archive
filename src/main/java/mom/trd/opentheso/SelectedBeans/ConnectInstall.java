/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.BaseDeDoneesHelper;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@ManagedBean(name = "poolConnexionInstall", eager = true)
@SessionScoped

public class ConnectInstall implements Serializable {

    private static final long serialVersionUID = 1L;

    private String messages = "";

    private HikariDataSource poolConnexionInstall;
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;
    /*
    Variables de conf pour Hikari pour l'install
     */
    private Integer minimumIdle = 1;
    private Integer setMaximumPoolSize = 1000;
    private boolean autoCommit = true;
    private String connectionTestQuery = "SELECT 1";
    private String dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource";
    private Integer idleTimeout = 30000;
    private Integer connectionTimeout = 30000;

    private String user;
    private String password;
    private String databaseName;
    private String localDatabaseName = "";
    private String serverName;
    private String serverPort;
    private String nomAdmin;
    private String passwordAdmin;
    private Map<String, String> conf;
    private ArrayList<String> ord = new ArrayList<>();

    public boolean createPropertiesFile() {
        int i = 0;
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String serverRealPath = servletContext.getRealPath("/WEB-INF/classes/hikari2.properties");
        File file = new File(serverRealPath);

        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            for (String string : ord) {
                for (Map.Entry e : conf.entrySet()) {
                    if (string == e.getKey()) {
                        br.write("" + e.getKey() + " = " + e.getValue());
                        br.newLine();
                        i++;
                        if (i == 7) {
                            br.newLine();
                            br.newLine();
                        }
                    }
                }

            }
            br.close();
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * Permet d'initialiser les variables de conf pour Hikari
     *
     * @return
     */
    public boolean validateConf() {
        if (minimumIdle < 1) {
            return false;
        }
        if (setMaximumPoolSize < 1) {
            return false;
        }
        if (idleTimeout < 1) {
            return false;
        }
        if (connectionTimeout < 1) {
            return false;
        }
        if (connectionTestQuery == null || "".equals(connectionTestQuery)) {
            return false;
        }
        if (dataSourceClassName == null || "".equals(dataSourceClassName)) {
            return false;
        }
        if (user == null || "".equals(user)) {
            return false;
        }
        if (password == null || "".equals(password)) {
            return false;
        }
        if (databaseName == null || "".equals(databaseName)) {
            return false;
        }
        if (serverName == null || "".equals(serverName)) {
            return false;
        }
        if (serverPort == null || "".equals(serverPort)) {
            return false;
        }
        conf = new HashMap<>();
        conf.put("minimumIdle", "" + minimumIdle);
        ord.add("minimumIdle");
        conf.put("autoCommit", "" + autoCommit);
        ord.add("autoCommit");
        conf.put("setMaximumPoolSize", "" + setMaximumPoolSize);
        ord.add("setMaximumPoolSize");
        conf.put("idleTimeout", "" + idleTimeout);
        ord.add("idleTimeout");
        conf.put("connectionTimeout", "" + connectionTimeout);
        ord.add("connectionTimeout");
        conf.put("connectionTestQuery", connectionTestQuery);
        ord.add("connectionTestQuery");
        conf.put("dataSourceClassName", dataSourceClassName);
        ord.add("dataSourceClassName");
        
        
        conf.put("serverName", serverName);
        ord.add("serverName");
        conf.put("serverPort", serverPort);
        ord.add("serverPort");
        conf.put("user", nomAdmin);
        ord.add("user");
        conf.put("password", passwordAdmin);
        ord.add("password");
        conf.put("databaseName", databaseName);
        ord.add("databaseName");

        return true;
    }

    /**
     * Cette fonction permet de créer le Pool de connexion à l'installation
     *
     * @return
     */
    public boolean openConnexionPoolInstall() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(setMaximumPoolSize);
        config.setAutoCommit(autoCommit);
        config.setConnectionTestQuery(connectionTestQuery);
        config.setDataSourceClassName(dataSourceClassName);

        config.addDataSourceProperty("user", user);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("databaseName", localDatabaseName);
        config.addDataSourceProperty("serverName", serverName);
        config.addDataSourceProperty("portNumber", serverPort);

        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        try {
            poolConnexion1.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ConnectInstall.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, ex.getClass().getName(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            poolConnexion1.close();
            return false;
        }
        poolConnexionInstall = poolConnexion1;
        return true;
    }

    /**
     *
     * @return
     */
    public boolean createBdd() {
        if (!validateConf()) {
            return false;
        }
        messages = "-Connexion en cours !!!!!";
        if (!openConnexionPoolInstall()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("inst.Err1")));
            messages = langueBean.getMsg("inst.Err1");
            return false;

        }
        BaseDeDoneesHelper baseDeDoneesHelper = new BaseDeDoneesHelper();

        //on fait la comprobation de que l'user n'exist pas
        //Si exist ne le creer pas
        messages += "<br>";
        messages += "-Conection complete  ";
        if (!baseDeDoneesHelper.isUserExist(poolConnexionInstall, nomAdmin)) {
            if (!baseDeDoneesHelper.createUser(poolConnexionInstall, nomAdmin, passwordAdmin)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("isnt.Err2")));
                messages += "<br>";
                messages += "-" + langueBean.getMsg("isnt.Err2");
                return false;
            }
            messages += "<br>";
            messages += "-Utilisateur created";
        }
        //on fait la comprobation de que la Bdd n'exist pas
        if (!baseDeDoneesHelper.isBddExist(poolConnexionInstall, databaseName)) {
            if (!baseDeDoneesHelper.createBdD(poolConnexionInstall, databaseName, nomAdmin)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("isnt.Err3")));
                messages += "<br>";
                messages += "-" + langueBean.getMsg("isnt.Err3");
                return false;
            }
            messages += "<br>";
            messages += "-Bdd fait";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("inst.Err4")));
            messages += "<br>" + "-" + langueBean.getMsg("inst.Err4");
            return false;
        }
        closeConnexion();

        // on refait la connexion avec la nouvelle base de données
        localDatabaseName = databaseName;
        if (!openConnexionPoolInstall()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("inst.Err1")));
            return false;
        }
        // injection des tables et initialisation des données dans la base
        messages += "<br>";
        messages += "-Connexion en cours a la nouvelle BDD !!!!!";
        messages += "<br>";

        InputStream inputStream = this.getClass().getResourceAsStream("/install/opentheso_dist_4.1.sql");
        if (!baseDeDoneesHelper.insertDonneées(poolConnexionInstall, inputStream, nomAdmin)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("inst.Err1")));
            messages += "<br>";
            messages += "-Erreur dans la injection de données";
            return false;
        }
        createPropertiesFile();
        messages += "<br>";
        messages += "-C'est tout fait!!!!!";

        return true;
    }

    public void closeConnexion() {
        if (poolConnexionInstall != null) {
            poolConnexionInstall.close();
        }
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
        closeConnexion();
    }

    public String getNomAdmin() {
        return nomAdmin;
    }

    public void setNomAdmin(String nomAdmin) {
        this.nomAdmin = nomAdmin;
    }

    public String getPasswordAdmin() {
        return passwordAdmin;
    }

    public void setPasswordAdmin(String passwordAdmin) {
        this.passwordAdmin = passwordAdmin;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public Integer getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

}
