/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.CopyrightHelper;

/**
 *
 * @author jm.prudham
 */


@ManagedBean(name = "copyrightBean", eager = true)
@SessionScoped

public class CopyrightBean {
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty (value="#{theso}")
    private SelectedThesaurus theso;
    private String copyright;
    private boolean alreadyExist=false;
    private boolean displayBrutText=false;

 
    
   
   // private boolean isLoaded=false;
    /**
     * Creates a new instance of CopyrightBean
     */
    public CopyrightBean() {
        
    }
   
      /**
     * function readCopyright() 
     * 
     * cherche un copyright correspondant à l' id_theso dans la table copyright 
     * renvoit true en cas de succès false si le champ n'existe pas
     * 
     * @return boolean
     */
    private boolean readCopyright(){
        
        boolean ret=false;
        if (theso == null || theso.getThesaurus().getId_thesaurus() == null) { 
        
            return ret;
        
        }
        else{
                
            CopyrightHelper copyrightHelper = new CopyrightHelper();
            String idTheso=theso.getThesaurus().getId_thesaurus();
            this.copyright=copyrightHelper.manageResult(idTheso,this.connect);
            ret=copyrightHelper.hasQueryReturnResult();
        }
      return ret;  
    }
    /**
     * Function updateCopyright
     * 
     * Sauve la valeur du copyright dans la base de donnée
     * renvoit true si OK false sinon,
     * 
     * 
     * @return boolean 
     */
    public boolean updateCopyright(){
        CopyrightHelper copyrightHelper = new CopyrightHelper();
        boolean ret;
        if(theso == null) {
              ret=false;
        }
        else{
             
             
              String idTheso=theso.getThesaurus().getId_thesaurus();
              
              
            
              if(this.alreadyExist){
                    

                    ret=copyrightHelper.updateCopyright(idTheso,this.copyright,this.connect);
                  
              }
              else{
                    
                    ret=copyrightHelper.insertCopyright(idTheso,this.copyright,this.connect);
               }
         }
        if(ret){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Copyright updaté"));
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "erreur pendant l'exécution sql"));
        }
        return ret;
    }
    
  
    public String getCopyright(){
          
        this.alreadyExist=this.readCopyright();
        return this.copyright;  
        
    }
    
    public void updateAndHide() {
        
        this.updateCopyright();
        this.displayBrutText=false;
    }
    
    public void setCopyright(String c){
        this.copyright=c;
        
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public SelectedThesaurus getTheso() {
        return theso;
    }

    public void setTheso(SelectedThesaurus theso) {
        this.theso = theso;
    }

    public boolean isDisplayBrutText() {
        return displayBrutText;
    }

    public void setDisplayBrutText(boolean displayBrutText) {
        this.displayBrutText = displayBrutText;
    }
    
    
}
