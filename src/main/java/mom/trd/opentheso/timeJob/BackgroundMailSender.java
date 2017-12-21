/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import mom.trd.opentheso.bdd.helper.Connexion;

/**
 *
 * @author jm.prudham
 */
@ManagedBean(name="backgroundMailSender",eager=true)
@ApplicationScoped
public class BackgroundMailSender  {
  @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
   
    //attribut liés au backgroundMailSenderHelper 
       private ArrayList<AlertStruct> pas;
    
//attribut  ScheduleJob
    private ScheduleJob sjPropos;//schedule job de la routine cdt proposés
    private ScheduleJob sjValid;//schedule job de la routine insertion validation refus d'un candidat
  
    
  
    
    /**
     * Creates a new instance of BackgroundMailSender
     */
     public BackgroundMailSender() {
    }
    
    /**
     * init
     * #JM
     * fonction qui doit lancer les routines avec le lancement du serveur(PostConstruct)
     * 
     */
    @PostConstruct
    public void init(){
       BackgroundMailSenderHelper bmsh=new BackgroundMailSenderHelper();
       this.pas=bmsh.getPoolAlert(connect);
       this.routineCdtPropos();
       this.routineCdtValidInsertRefuse();
    }
    
    @PreDestroy
    public void destroy(){
       
        sjPropos.closeAllJob();
        sjValid.closeAllJob();
    }
    /**
     * Fonction send()
     * #JM
     * Permet de renvoyer un Runnable paramètré, qui doit faire un select
     * des candidats proposés 
     * @return 
     */
    public Runnable sendPropos(int key){
  
      SendMailProposedCdt smpc=new SendMailProposedCdt();
      smpc.setPoolConnexion(this.connect.getPoolConnexion());
      HashMap<String,Date> id_theso=new HashMap<>();
      id_theso.put(pas.get(key).getThesaurusEnAcces(),
              pas.get(key).getDate_debut_envoi_cdt_propos());
      smpc.setPool_id_theso_since(id_theso);
      return smpc;
      
       
     
    }
    /**
     * #JM
     * méthode qui retourne un Runnable qui doit 
     * effectuer la requête pour une date et un identifiant thésaurus,
     * un select des candidats insérés ou validés ou refusés
     * @return 
     */
     private Runnable sendValid(int key) {
      SendMailValidedInsertedCdt smvic=new SendMailValidedInsertedCdt();
      smvic.setPoolConnexion(this.connect.getPoolConnexion());
      HashMap<String,Date> id_theso=new HashMap<>();
      id_theso.put(pas.get(key).getThesaurusEnAcces(),
              pas.get(key).getDate_debut_envoi_cdt_valid());
      smvic.setPool_id_theso_since(id_theso);
      return smvic;
    }
    
        
     
    
    /**
     * #jm
     *Routine pour envoyer un mail des candidats proposés durant une période
     * donnée aux admins
     */
    public void routineCdtPropos(){
        int i=0;
        int count=pas.size();
        long[] initialD=new long[count];
        long[] period=new long[count];
        Runnable[] job=new Runnable[count];
        for(i=0;i<count;i++){
            Date d=pas.get(i).getDate_debut_envoi_cdt_propos();
            int del=pas.get(i).getPeriod_envoi_cdt_propos();
            long initialDel=this.calculInitialDelay(d,del);
            initialD[i]=initialDel;
            period[i]=(long)del;
             job[i]=this.sendPropos(i);
         
        }
        sjPropos=new ScheduleJob(count, initialD, period, TimeUnit.DAYS,job);
        sjPropos.sendPeriodicMultipleJob();
        
    }
   /**
    * #jm
    * Routine pour envouer les mails aux utilisateurs et aux admins des
    * candidats validés ou insérés ou refusés
    */
    public void routineCdtValidInsertRefuse(){
        int count=pas.size();
        int i=0;
        long[] initialD=new long[count];
        long[] period=new long[count];
        Runnable[] job=new Runnable[count];
        for(i=0;i<count;i++){
            Date d=pas.get(i).getDate_debut_envoi_cdt_valid();
            int del=pas.get(i).getPeriod_envoi_cdt_valid();
            long initialDel=this.calculInitialDelay(d,del);
            initialD[i]=initialDel;
            period[i]=(long)del;
             job[i]=this.sendValid(i);
           
        }
        sjValid=new ScheduleJob(count, initialD, period, TimeUnit.DAYS,job);
        sjValid.sendPeriodicMultipleJob();
        
    }

   
  
    /**
     * calculInitialDelay
     * #JM
     * Fonction qui permet de savoir le nombre de (unité) jour qui reste avant le 
     * lancement du thread périodique
     * @param d
     * @param p
     * @return 
     */
    private long calculInitialDelay(Date d, int p) {
      long diff=new Date().getTime()-d.getTime();
      long day=diff/1000/60/60/24;
      if(p<day){
         return 0; 
      }
      return p-day%p;
    }

  
   
    public Connexion getConnect() {
        return connect;
    }
    
    public void setConnect(Connexion connect) {
        this.connect = connect;
    }


  
    public ScheduleJob getSjPropos() {
        return sjPropos;
    }

    public void setSjPropos(ScheduleJob sjPropos) {
        this.sjPropos = sjPropos;
    }

    public ScheduleJob getSjValid() {
        return sjValid;
    }

    public void setSjValid(ScheduleJob sjValid) {
        this.sjValid = sjValid;
    }

    
    


   
    
}
