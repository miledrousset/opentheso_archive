/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.SelectedBeans.SelectedThesaurus;
import mom.trd.opentheso.SelectedBeans.Connexion;

/**
 *
 * @author jm.prudham
 */
@ManagedBean(name= "preferencesAlertBean",eager=true)
@SessionScoped 
public class PreferencesAlertBean {
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value ="#{backgroundTimeJob}")
    private BackgroundTimeJob bms;
    @ManagedProperty(value="#{theso}")
    private SelectedThesaurus st;
    //attribut pour le helper
    
    AlertStruct as=null;
      //début attribut prévu pour le front end
    
    private String thesaurusEnAcces;
    private boolean alert;
    private Date date_debut_envoi_cdt_propos;
    private Date date_debut_envoi_cdt_valid;
    private int period_envoi_cdt_propos;
    private int period_envoi_cdt_valid;
    //attribut pour le front end sparql
    
    private String adresse_serveur="";
    private String mot_de_passe="";
    private String nom_d_utilisateur="";
    private String graph="";
    private boolean synchronisation=false;
    private Date heure=new Date();
    
    /**
     * Creates a new instance of PrefrencesAlertBean
     */
    public PreferencesAlertBean() {
    }
    
    /**
     * updatePreferencesAlertCanceled
     * #JM 
     * PF
     * Fonction qui permet de passer la valeur alert_cdt de la table 
     * routine_mail  à false,
     * l'appel à init() permet de charger le nouveau pool de thread correct 
     */
    public void updatePreferenceAlert(){
        if(as==null || (st.getThesaurus().getId_thesaurus() == null ? this.thesaurusEnAcces != null : !st.getThesaurus().getId_thesaurus().equals(this.thesaurusEnAcces))){
            loadValues();
        }
         PreferencesAlertHelper pah=new PreferencesAlertHelper();
         int ret=pah.setAlert(thesaurusEnAcces,this.alert,connect.getPoolConnexion());
    //     System.out.println("retour de la fonction cancelAlert "+ret);
         if(ret<1){
             insertRoutineMail();
         }
        
        this.bms.destroy();
        this.bms.init();
    }
    
    /**
     * updateRoutineMail 
     * #JM 
     * PF
     * fonction à appeller  pour changer les valeurs dans le table routine_mail
     * l'appel à init() permet de lancer immédiatement les threads avec de nouvelle valeur
     */
    public void updateRoutineMail(){
        if(as==null  || (st.getThesaurus().getId_thesaurus() == null ? this.thesaurusEnAcces != null : !st.getThesaurus().getId_thesaurus().equals(this.thesaurusEnAcces))){
            loadValues();
        }
        PreferencesAlertHelper pah=new PreferencesAlertHelper();
         int ret=pah.updateRoutineMail(thesaurusEnAcces,date_debut_envoi_cdt_propos,
                 date_debut_envoi_cdt_valid,period_envoi_cdt_propos,
                 period_envoi_cdt_valid,connect.getPoolConnexion());
         
    //     System.out.println("retour de la fonction addAlert "+ret);
         if(ret<1){
             insertRoutineMail();
         }
        
         if(this.bms.getSjPropos()!=null){
            this.bms.getSjPropos().closeAllJob();
         }
         if(this.bms.getSjValid()!=null){
            this.bms.getSjValid().closeAllJob();
         }
        this.bms.init();
        
    }

   public String getThesaurusEnAcces() {
       thesaurusEnAcces=st.getThesaurus().getId_thesaurus();
       loadValues();
       return thesaurusEnAcces;
    }

    public void setThesaurusEnAcces(String thesaurusEnAcces) {
        this.thesaurusEnAcces = thesaurusEnAcces;
    }

    public boolean isAlert() {
        if(as==null || (st.getThesaurus().getId_thesaurus() == null ? this.thesaurusEnAcces != null : !st.getThesaurus().getId_thesaurus().equals(this.thesaurusEnAcces))){
            loadValues();
        }
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public Date getDate_debut_envoi_cdt_propos() {
        if(as==null || (st.getThesaurus().getId_thesaurus() == null ? this.thesaurusEnAcces != null : !st.getThesaurus().getId_thesaurus().equals(this.thesaurusEnAcces))){
            loadValues();
        }
        return date_debut_envoi_cdt_propos;
    }

    public void setDate_debut_envoi_cdt_propos(Date date_debut_envoi_cdt_propos) {
      
        this.date_debut_envoi_cdt_propos = date_debut_envoi_cdt_propos;
    }

    public Date getDate_debut_envoi_cdt_valid() {
          if(as==null || (st.getThesaurus().getId_thesaurus() == null ? this.thesaurusEnAcces != null : !st.getThesaurus().getId_thesaurus().equals(this.thesaurusEnAcces))){
            loadValues();
        }
        return date_debut_envoi_cdt_valid;
    }

    public void setDate_debut_envoi_cdt_valid(Date date_debut_envoi_cdt_valid) {
        this.date_debut_envoi_cdt_valid = date_debut_envoi_cdt_valid;
    }

    public int getPeriod_envoi_cdt_propos() {
          if(as==null || (st.getThesaurus().getId_thesaurus() == null ? this.thesaurusEnAcces != null : !st.getThesaurus().getId_thesaurus().equals(this.thesaurusEnAcces))){
            loadValues();
        }
        return period_envoi_cdt_propos;
    }

    public void setPeriod_envoi_cdt_propos(int period_envoi_cdt_propos) {
        this.period_envoi_cdt_propos = period_envoi_cdt_propos;
    }

    public int getPeriod_envoi_cdt_valid() {
          if(as==null || (st.getThesaurus().getId_thesaurus() == null ? this.thesaurusEnAcces != null : !st.getThesaurus().getId_thesaurus().equals(this.thesaurusEnAcces))){
            loadValues();
        }
        return period_envoi_cdt_valid;
    }

    public void setPeriod_envoi_cdt_valid(int period_envoi_cdt_valid) {
        this.period_envoi_cdt_valid = period_envoi_cdt_valid;
    }
 

    private void loadValues() {
        PreferencesAlertHelper pah=new PreferencesAlertHelper();
        as=pah.loadValues(connect.getPoolConnexion(),st.getThesaurus().getId_thesaurus());
        this.thesaurusEnAcces=st.getThesaurus().getId_thesaurus();
        this.alert=as.isAlertB();
        this.date_debut_envoi_cdt_propos=as.getDate_debut_envoi_cdt_propos();
        this.date_debut_envoi_cdt_valid=as.getDate_debut_envoi_cdt_valid();
        this.period_envoi_cdt_propos=as.getPeriod_envoi_cdt_propos();
        this.period_envoi_cdt_valid=as.getPeriod_envoi_cdt_valid();
        
    }

    private int insertRoutineMail() {
        PreferencesAlertHelper pah=new PreferencesAlertHelper();
        int ret=pah.insertRoutineMail(connect.getPoolConnexion(),
                thesaurusEnAcces, date_debut_envoi_cdt_propos,
                date_debut_envoi_cdt_valid, period_envoi_cdt_propos,
                period_envoi_cdt_valid);
        
        return ret;
    }
    public boolean insertPreferencesSparql(){
        boolean ret=false; 
        PreferencesAlertHelper pah=new PreferencesAlertHelper();
       
        ret=pah.isYetInTablePreferencesSparql(thesaurusEnAcces,connect.getPoolConnexion());
        if(ret){
            ret=pah.updatePreferencesSparql(adresse_serveur,mot_de_passe,nom_d_utilisateur,
                    graph,synchronisation,thesaurusEnAcces,
                      new java.sql.Date(heure.getTime()),connect.getPoolConnexion());
                            
        }
        else{
            ret=pah.insertIntoPreferencesSparql(adresse_serveur, mot_de_passe,
               nom_d_utilisateur, graph, synchronisation, thesaurusEnAcces,
               new java.sql.Date(heure.getTime()),connect.getPoolConnexion());
        }
       if(!ret){
   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR+"", "problème  à l'insertion sur la table preferences sparql"));
            } 
       else {      
       
          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.FACES_MESSAGES+"", "mise à jour de la table preferences sparql"));

       }
       bms.destroy();
       bms.init();
       return ret;
    }
    
    public void loadPreferencesSparql(){
        PreferencesAlertHelper pah=new PreferencesAlertHelper();
        SparqlStruct ss=pah.getSparqlPreferences(thesaurusEnAcces,this.connect.getPoolConnexion());
        this.adresse_serveur=ss.getAdresseServeur();
        this.graph=ss.getGraph();
        this.heure=ss.getHeure();
        this.mot_de_passe=ss.getMot_de_passe();
        this.nom_d_utilisateur=ss.getNom_d_utilisateur();
        this.synchronisation=ss.isSynchro();
        
        
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public BackgroundTimeJob getBms() {
        return bms;
    }

    public void setBms(BackgroundTimeJob bms) {
        this.bms = bms;
    }

    public SelectedThesaurus getSt() {
        return st;
    }

    public void setSt(SelectedThesaurus st) {
        this.st = st;
    }

    public void nullifyAs() {
        this.as = null;
    }

    public AlertStruct getAs() {
        return as;
    }

    public void setAs(AlertStruct as) {
        this.as = as;
    }

    public String getAdresse_serveur() {
        return (adresse_serveur==null)?"" : adresse_serveur;
    }

    public void setAdresse_serveur(String adresse_serveur) {
        this.adresse_serveur = adresse_serveur;
    }

    public String getMot_de_passe() {
        return (mot_de_passe==null)?"":mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    public String getNom_d_utilisateur() {
        return (nom_d_utilisateur==null)?"":nom_d_utilisateur;
    }

    public void setNom_d_utilisateur(String nom_d_utilisateur) {
        this.nom_d_utilisateur = nom_d_utilisateur;
    }

    public String getGraph() {
        return (graph==null)?"":graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public boolean isSynchronisation() {
        return synchronisation;
    }

    public void setSynchronisation(boolean synchronisation) {
        this.synchronisation = synchronisation;
    }

    public Date getHeure() {
        return (heure==null)? new Date():heure;
    }

    public void setHeure(Date heure) {
        this.heure = heure;
    }
    
    
    public void multipleBeanAction(){
        st.setPreferenceOfThesaurus();
        if(this.as.isAlertB()!=this.alert){
            this.updatePreferenceAlert();
        }
        if(this.as.getDate_debut_envoi_cdt_propos() != this.date_debut_envoi_cdt_propos 
                || this.as.getDate_debut_envoi_cdt_valid() != this.date_debut_envoi_cdt_valid
                || this.as.getPeriod_envoi_cdt_propos() != this.getPeriod_envoi_cdt_propos()
                || this.as.getPeriod_envoi_cdt_valid()!=this.getPeriod_envoi_cdt_valid()){
            this.updateRoutineMail();
        }
        nullifyAs();
    }
    
}
