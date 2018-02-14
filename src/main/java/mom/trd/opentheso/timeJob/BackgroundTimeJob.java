/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import mom.trd.opentheso.bdd.datas.ConceptGroup;
import mom.trd.opentheso.bdd.datas.Languages_iso639;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.LanguageHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;

/**
 *
 * @author jm.prudham
 */
@ManagedBean(name="backgroundTimeJob",eager=true)
@ApplicationScoped
public class BackgroundTimeJob  {
  @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
   
    //attribut liés au backgroundMailSenderHelper 
    private ArrayList<AlertStruct> pas;
    //attribut pour la synchronisation sparql
    private ArrayList<SparqlStruct> SparqlSyn;
//attribut  ScheduleJob
    private ScheduleJob sjPropos;//schedule job de la routine mail cdt proposés
    private ScheduleJob sjValid;//schedule job de la routine mail insertion validation refus d'un candidat
    private ScheduleJob sjSparql;//schedule pour la synchronization avec le serveur sparql
    
  
    
    /**
     * Creates a new instance of BackgroundMailSender
     */
     public BackgroundTimeJob() {
    }
    
    /**
     * init
     * #JM
     * fonction qui doit lancer les routines avec le lancement du serveur(PostConstruct)
     * 
     */
    @PostConstruct
    public void init(){
       BackgroundTimeJobHelper bmsh=new BackgroundTimeJobHelper();
       
       this.pas=bmsh.getPoolAlert(connect);
       if(this.pas==null){
           return;//forcer à cause de  l'install automatique (?)
       }
       
       this.SparqlSyn=bmsh.getSparqlSynchro(connect);
       //précaution il n'y a prioiri pas de possibilité pour que ce soit null
       if(this.SparqlSyn == null){
           return;
       }
       
       this.routineCdtPropos();
       this.routineCdtValidInsertRefuse();
       this.routineSparqlSynchronisation();
    }
    
    @PreDestroy
    public void destroy(){
       
        sjPropos.closeAllJob();
        sjValid.closeAllJob();
        sjSparql.closeAllJob();
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
     * Les routines de synchronisation pour les serveurs sparql
     * on suppose qu'elles sont de  15 minutes maximum
     * donc on les décale de 15 minutes entre elles
     * 
     */
    private void routineSparqlSynchronisation() {
        int count=this.SparqlSyn.size();
        int j=0;
        long[] initialD=new long[count];
        long[] period=new long[count];
        Runnable[] job=new Runnable[count];
        // today    
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
       // date.add(Calendar.DAY_OF_MONTH, 1);
       
        for(int i=0;i<count;i++){
            
          
            
            SimpleDateFormat sdf1=new SimpleDateFormat("HH");
            SimpleDateFormat sdf2=new SimpleDateFormat("mm");
            date.set(Calendar.HOUR_OF_DAY,Integer.parseInt(sdf1.format(this.SparqlSyn.get(i).getHeure())));
            date.set(Calendar.MINUTE,Integer.parseInt(sdf2.format(this.SparqlSyn.get(i).getHeure())));
            initialD[i]=(date.getTime().getTime()-new Date().getTime())/(1000*60);//en minute
            if(initialD[i]<0)initialD[i]+=24*60;//si lheure est passé aujourd hui alors on ajoute  
            period[i]=24*60;
            job[i]=this.synchroSparql(i);
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
          
           
        }
        
     
        this.sjSparql=new ScheduleJob(count, initialD, period, TimeUnit.MINUTES, job);
        this.sjSparql.sendPeriodicMultipleJob();
    }

    
          
    private Runnable synchroSparql(int key){
        SynchroSparql run=new SynchroSparql();
        run.setSparqlStruct(this.SparqlSyn.get(key));
        ArrayList<Languages_iso639> listeLang=new LanguageHelper().
                  getLanguagesOfThesaurus(this.connect.getPoolConnexion(),
                          this.SparqlSyn.get(key).getThesaurus());
        ArrayList<NodeLang> nol=new ArrayList<>();
        for(Languages_iso639 lang:listeLang){
          
            NodeLang nd=new NodeLang();
            nd.setValue(lang.getId_iso639_1());
            nd.setCode(lang.getId_iso639_1());
            nol.add(nd);
        }
        run.setListe_lang(nol);
        ArrayList<String> nog=new GroupHelper().
                getListIdOfGroup(this.connect.getPoolConnexion(),
                        this.SparqlSyn.get(key).getThesaurus());
        
        ArrayList<NodeGroup> groupes=new ArrayList<>();
        for(String group:nog){
           
            NodeGroup ng1=new NodeGroup();
            ng1.setId_group(group);
        
            ConceptGroup cg=new ConceptGroup();
            cg.setIdgroup(group);
            ng1.setConceptGroup(cg);
            groupes.add(ng1);
        }
        run.setListe_group(groupes);
        run.setConn(this.connect);
        return run;
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
