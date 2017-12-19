/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.SelectedBeans.SelectedThesaurus;
import mom.trd.opentheso.bdd.helper.Connexion;

/**
 *
 * @author jm.prudham
 */
@ManagedBean(name= "preferencesAlertBean",eager=true)
@SessionScoped 
public class PreferencesAlertBean {
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value ="#{backgroundMailSender}")
    private BackgroundMailSender bms;
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
        
        if(this.bms.getSjPropos()!=null){
            this.bms.getSjPropos().closeAllJob();
         }
         if(this.bms.getSjValid()!=null){
            this.bms.getSjValid().closeAllJob();
         }
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

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public BackgroundMailSender getBms() {
        return bms;
    }

    public void setBms(BackgroundMailSender bms) {
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

    
    public void multipleBeanAction(){
        st.majPref();
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
