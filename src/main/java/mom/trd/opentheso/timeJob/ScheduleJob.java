/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jm.prudham
 */
public class ScheduleJob {
    
    private ScheduledExecutorService scheduler;
    private final int count;
    private final long[] initialDelay;
    private final long[] delay;
    private final TimeUnit tu;
    private final Runnable uniquejob;
    private final Runnable[] multipleJob;
    
    /**
     * Constructeur ScheduleJob
     * #JM
     * Permet de créer un ScheduleJob avec une liste multiJob et les valeurs
     * initialDely et delay qui correspondent aux jobs
     * 
     * @param count
     * @param initialDelay
     * @param delay
     * @param tu
     * @param multipleJob 
     */
    public ScheduleJob(int count,long[] initialDelay,long[] delay,
            TimeUnit tu,Runnable[] multipleJob) {
        this.count=count;
        this.initialDelay=initialDelay;
        this.delay=delay;
        this.tu=tu;
        this.uniquejob=null;
        this.multipleJob=multipleJob;
    
    }
    /**
     * Constructeur ScheduleJob
     * #JM
     * constructeur au cas il s'agirait de la même tache (même requête sql)
     * mais avec différents threads(?ou pas si tableau de taille 1?)
     * @param count
     * @param initialDelay
     * @param tu
     * @param job 
     */
    public ScheduleJob(int count,long[] initialDelay,TimeUnit tu,Runnable job) {
        this.count=count;
        this.initialDelay=initialDelay;
        this.tu=tu;
        this.uniquejob=job;
        this.delay=null;
        this.multipleJob=null;
    
    
    }
    
    /**
     * sendPeriodicMultipleJob
     *#JM
     * Permet de lancer des jobs multiples selon differents initialDelay et delay
     *
     */
    public void sendPeriodicMultipleJob(){
        scheduler=Executors.newScheduledThreadPool(count);
        for(int i=0;i<count;i++){
       
            scheduler.scheduleAtFixedRate(multipleJob[i], initialDelay[i], delay[i], tu);
           
        }
        System.out.println("les threads sont terminés ?"+scheduler.isTerminated());
        
    }
    /**
     *sendUniqueJob
     * #JM
     * Permet de lancer un job unique selon un tableau d' initialDelay
     * (?)
     */
    public void sendUniqueJob(){
        scheduler=Executors.newScheduledThreadPool(count);
        for(int i=0;i<count;i++){
            scheduler.schedule(uniquejob, initialDelay[i], tu);
        }
    }
    /**
     *sendMultipleJob
     * #JM
     * Permet de lancer une liste de Job selon une liste d'initial delay
     * (?)
     */
    public void sendMultipleJob(){
         scheduler=Executors.newScheduledThreadPool(count);
        for(int i=0;i<count;i++){
            scheduler.schedule(multipleJob[i], initialDelay[i], tu);
        }
        
    }
    /**
     * closeAllJob
     * #JM
     * permet normalement de fermer correctement les threads
     */
    void closeAllJob() {
        scheduler.shutdown();
    }
    
    
    
}
