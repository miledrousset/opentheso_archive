/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.ws.cron;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.sauronsoftware.cron4j.Scheduler;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level; 
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.core.cron.TimerExportThesaurus;

/**
 * REST Web Service
 *
 * @author miled.rousset
 */
@Path("cron")
public class Cron {

    @Context
    private UriInfo context;

    private HikariDataSource ds;
    private Properties prefs;    
    private Scheduler scheduler = null;
    /**
     * Creates a new instance of resources
     */
    public Cron() {
/*        Properties properties= new Properties();
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
            scheduler = new Scheduler();
            
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }*/
    }
    
    private void startCron() {
        if(scheduler == null) return;
                // Prepares the task.
        TimerExportThesaurus task = new TimerExportThesaurus(ds, prefs);
        // Creates the scheduler.

        /*
        Following are examples of crontab expressions and how they would interpreted as a recurring schedule.

            * * * * *
            This pattern causes a task to be launched every minute.

            5 * * * *
            This pattern causes a task to be launched once every hour and at the fifth minute of the hour (00:05, 01:05, 02:05 etc.).

            * 12 * * Mon
            This pattern causes a task to be launched every minute during the 12th hour of Monday.

            * 12 16 * Mon
            This pattern causes a task to be launched every minute during the 12th hour of Monday, 16th, but only if the day is the 16th of the month.

            59 11 * * 1,2,3,4,5
            This pattern causes a task to be launched at 11:59AM on Monday, Tuesday, Wednesday, Thursday and Friday. Every sub-pattern can contain two or more comma separated values.

            59 11 * * 1-5
        */
        // Schedules the task, once every minute.
   //     scheduler.schedule("* * * * *", task);
        // Starts the scheduler.
    //    scheduler.start();
        // Stays alive for five minutes.
    /*    try {
            Thread.sleep(5L * 60L * 1000L);
        } catch (InterruptedException e) {
            System.err.println("erreur");
        }*/
        // Stops the scheduler.
        //scheduler.stop();

    }
    
    private void stopCron() {
        if(scheduler != null)
            scheduler.stop();
    }
    
    private HikariDataSource openConnexionPool(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("minimumIdle")));
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("setMaximumPoolSize")));
        config.setAutoCommit(true);
    //    config.setIdleTimeout(Integer.parseInt(properties.getProperty("idleTimeout")));
    //    config.setConnectionTimeout(Integer.parseInt(properties.getProperty("connectionTimeout")));
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
     * La partie REST pour produire du SKOS
     * @param start
     * @return 
     */
    
/*    @Path("/{value}")
    @GET
    @Produces("application/Text;charset=UTF-8")
    public String putConcept(@PathParam("value") String start){
        if(start.equalsIgnoreCase("start")){
            startCron();
            //ds.close();
            return "OK";
        }
        if(start.equalsIgnoreCase("stop")){
            stopCron();
            ds.close();
            return "OK";
        }
        return "Error";        
    }
    */
    
}
