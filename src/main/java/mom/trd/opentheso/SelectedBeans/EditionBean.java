/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import com.google.common.collect.HashBiMap;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AccessThesaurusHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.UserHelper2;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUserRoleGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author miled.rousset
 */
@javax.faces.bean.ManagedBean(name = "editionBean", eager = true)
@javax.faces.bean.ViewScoped
public class EditionBean implements Serializable {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);    
    private Thesaurus editTheso;
    private Thesaurus editTraduction;
    
    
    private ArrayList<Map.Entry<String, String>> editThesoTraductions;    
    
    private SelectItem[] langues;
    private boolean isPrivate = false;
    
    private Map<String, String> userRoleGroups;
    private String selectedUserGroup;    
    
    private String langueTrad = "";
    private String nomTrad = "";    
    
    private String idTheso;
    
    
//    @Inject 
    @ManagedProperty("#{langueBean}")
    private LanguageBean langueBean;
    
    @ManagedProperty(value = "#{roleOnTheso}")
    private RoleOnThesoBean roleOnThesoBean;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    @ManagedProperty(value = "#{currentUser}") 
    private CurrentUser2 currentUser;
    
    @ManagedProperty(value = "#{vue}")
    private Vue vue;
    
    
    @PostConstruct
    public void init(){
        editTheso = new Thesaurus();
        userRoleGroups = new LinkedHashMap();
        
        ArrayList<NodeUserRoleGroup> nodeUserRoleGroups
                = new UserHelper2().getUserRoleGroup(connect.getPoolConnexion(), currentUser.getUser().getIdUser());

        for (NodeUserRoleGroup nodeUserRoleGroup : nodeUserRoleGroups) {
            if(nodeUserRoleGroup.getIdRole() < 3) {
                userRoleGroups.put("" +nodeUserRoleGroup.getIdGroup(), nodeUserRoleGroup.getGroupName());
                selectedUserGroup = "" + nodeUserRoleGroup.getIdGroup(); 
            }
        }
        vue.setCreat(false);
        vue.setTrad(false);
        vue.setEdit(false);
       
    }
    /**
     * Creates a new instance of EditionBean
     */
    public EditionBean() {
    }
    
    /**
     * Création d'un nouveau thésaurus avec l'ajout du théso dans le groupe de l'utilisateur en cours
     *
     */
    public void ajouterTheso() {
        NodePreference nodePreference = roleOnThesoBean.getNodePreference();
        
        boolean arkActive = false;
        String urlSite = "";
        String workLanguage;
        
        if(!currentUser.getUser().isIsSuperAdmin()){
            if(selectedUserGroup == null || selectedUserGroup.isEmpty()){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "pas de Groupe sélectionné"));
                return;
            }
        }
        
        if (editTheso.getTitle() == null || editTheso.getTitle().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error1")));
        } else {
            ThesaurusHelper th = new ThesaurusHelper();
            if(nodePreference == null){
                th.setIdentifierType("2"); // numérique
                workLanguage = connect.getWorkLanguage();
            }
            else {
                th.setIdentifierType("" + nodePreference.getIdentifierType());
                arkActive = nodePreference.isUseArk();
                urlSite = nodePreference.getCheminSite();
                workLanguage = nodePreference.getSourceLang();
            }

            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);

                String idThesaurus = th.addThesaurusRollBack(conn, urlSite, arkActive);
                if (idThesaurus == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error1")));
                    conn.rollback();
                    conn.close();
                    return;
                }
                if (editTheso.getTitle().isEmpty()) {
                    editTheso.setTitle("theso_" + idThesaurus);
                }
                if (editTheso.getLanguage() == null) {
                    editTheso.setLanguage(workLanguage);
                }
                editTheso.setId_thesaurus(idThesaurus);

                if (!th.addThesaurusTraductionRollBack(conn, editTheso)) {
                    conn.rollback();
                    conn.close();
                    return;
                }

                
                // ajout de role pour le thésaurus à l'utilisateur en cours
                UserHelper2 userHelper = new UserHelper2();
                if(!currentUser.getUser().isIsSuperAdmin()) {
                    if (!userHelper.addThesoToGroup(conn, idThesaurus, Integer.parseInt(selectedUserGroup))) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                        conn.rollback();
                        conn.close();
                        return;
                    }
                }
                conn.commit();
                conn.close();
                
                // appliquer les préférences du Domaine au nouveau thésaurus
                if(nodePreference != null){
                    if (!new PreferencesHelper().addPreference(connect.getPoolConnexion(), nodePreference, idThesaurus)) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                        return;
                    }
                }
            } catch (SQLException ex) {
                log.error("Error while adding traductions of thésaurus ", ex);
            }
        /*    maj();
            arrayTheso = new ArrayList<>(th.getListThesaurus(connect.getPoolConnexion(), langueSource).entrySet());
            tree.getSelectedTerme().getUser().updateAuthorizedTheso();
            */
          //  vue.setCreat(false);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info1.1") + " " + editTheso.getTitle() + " " + langueBean.getMsg("theso.info1.2")));
            setVisibility(idTheso, isPrivate);
            editTheso = new Thesaurus();
        }
        // tree.getSelectedTerme().getUser().getUser().getId();
    }    
    
    /**
     * fonction qui permet de modifier la visibilité d'un thésaurus (privé ou public 
     * #MR
     * @param idTheso
     * @param isPrivateValue
     */
    private boolean setVisibility(String idTheso, boolean isPrivateValue){
        AccessThesaurusHelper accessThesaurusHelper = new AccessThesaurusHelper();
        if(!accessThesaurusHelper.updateVisibility(connect.getPoolConnexion(),
                idTheso, isPrivateValue)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
            return false;
        }
        return true;
    }
    
    /**
     * permet de récupérer la tradcution d'un thésaurus dans la langue séléctionnée 
     * @param idLang 
     */
    public void initEditTheso(String idLang){
        if(idTheso == null || idTheso.isEmpty()) return;
       
        editTraduction = new ThesaurusHelper().getThisThesaurus(connect.getPoolConnexion(), idTheso, idLang);
    }
    
    /**
     * Récupérer le thésaurus sélectionné pour l'édition (pas le thésaurus en cours)
     *
     * @param idTheso
     */
    public void initTraductionTheso(String idTheso) {
        this.idTheso = idTheso;
        String sourceLanguage = new PreferencesHelper().getWorkLanguageOfTheso(connect.getPoolConnexion(), idTheso);
        if(sourceLanguage == null) 
            sourceLanguage = connect.getWorkLanguage();
        editTheso = new ThesaurusHelper().getThisThesaurus(connect.getPoolConnexion(), idTheso, sourceLanguage);
        editThesoTraductions = new ArrayList<>(new ThesaurusHelper().getMapTraduction(connect.getPoolConnexion(), idTheso).entrySet());
    }    
    
    /**
     * Permet de modifier les informations du thésaurus
     * @return 
     */
    public boolean editerTheso() {
        if(idTheso == null || idTheso.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error1")));
            return false;
        }
        if (editTraduction.getTitle() == null || editTraduction.getTitle().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error1")));
            return false;
        } else {
            editTraduction.setId_thesaurus(idTheso);
            new ThesaurusHelper().UpdateThesaurus(connect.getPoolConnexion(), editTraduction);
            vue.setEdit(false);
            initTraductionTheso(idTheso);
            return true;
        }
    }
    
    /**
     * Permet de supprimer le thésaurus
     * @param idTheso1
     * @return 
     */
    public boolean supprimerTheso(String idTheso1) {
        PreferencesHelper preferencesHelper = new PreferencesHelper();
        NodePreference nodePreference = preferencesHelper.getThesaurusPreferences(connect.getPoolConnexion(), idTheso1);
        if(nodePreference != null) {
            // suppression des Identifiants Handle
            ConceptHelper conceptHelper = new ConceptHelper();
            conceptHelper.setNodePreference(nodePreference);
            conceptHelper.deleteAllIdHandle(connect.getPoolConnexion(), idTheso1);
        }
        
        
        // suppression du thesaurus du group/projet
        UserHelper2 userHelper = new UserHelper2();
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if(!userHelper.deleteThesoFromGroup(conn, idTheso1)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("info") + " :", "BDD problem !! "));
                conn.rollback();
                conn.commit();
                return false;
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // suppression complète du thésaurus        
        ThesaurusHelper th = new ThesaurusHelper();
        th.deleteThesaurus(connect.getPoolConnexion(), idTheso1);
 
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :", langueBean.getMsg("edition.ThesoDeleted")));
        return true;
    }    
    
    /**
     * changeVisibility
     * #jm
     * fonction pour passer de visibilité privée à publique
     */
    public void changeVisibility() {
        if(editTheso.isPrivateTheso()) { // si c'est privé, on le rend publique
            if(setVisibility(idTheso, false))
                editTheso.setPrivateTheso(false);
        } else {
             // si c'est publique, on le rend privé
            if(setVisibility(idTheso, true))
                editTheso.setPrivateTheso(true);            
        }        
    }    

    /**
     * Création d'une traduction d'un thésaurus
     * @return 
     */
    public boolean addNewLanguage() {
        if (nomTrad.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error1")));
            return false;
        } else {
            Thesaurus thesoTemp = new Thesaurus();
            thesoTemp.setId_thesaurus(idTheso);
            thesoTemp.setLanguage(langueTrad);
            thesoTemp.setTitle(nomTrad);
            new ThesaurusHelper().addThesaurusTraduction(connect.getPoolConnexion(), thesoTemp);
            initTraductionTheso(idTheso);
        }
        nomTrad = "";
        return true; 
    }    
    
    /**
     * Suppression une traduction au thésaurus
     *
     * @param idLang
     * @return 
     */
    public boolean deleteLanguage(String idLang) {
        if(!new ThesaurusHelper().deleteThesaurusTraduction(
                connect.getPoolConnexion(),idTheso,
                idLang)) 
            return false;
        initTraductionTheso(idTheso);
        return true;
    }    
    
    
    public Thesaurus getEditTheso() {
        return editTheso;
    }

    public void setEditTheso(Thesaurus editTheso) {
        this.editTheso = editTheso;
    }

    public SelectItem[] getLangues() {
        return langues;
    }

    public void setLangues(SelectItem[] langues) {
        this.langues = langues;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public RoleOnThesoBean getRoleOnThesoBean() {
        return roleOnThesoBean;
    }

    public void setRoleOnThesoBean(RoleOnThesoBean roleOnThesoBean) {
        this.roleOnThesoBean = roleOnThesoBean;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public CurrentUser2 getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser2 currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Map<String, String> getUserRoleGroups() {
        return userRoleGroups;
    }

    public void setUserRoleGroups(Map<String, String> userRoleGroups) {
        this.userRoleGroups = userRoleGroups;
    }

    public String getSelectedUserGroup() {
        return selectedUserGroup;
    }

    public void setSelectedUserGroup(String selectedUserGroup) {
        this.selectedUserGroup = selectedUserGroup;
    }

    public ArrayList<Map.Entry<String, String>> getEditThesoTraductions() {
        return editThesoTraductions;
    }

    public void setEditThesoTraductions(ArrayList<Map.Entry<String, String>> editThesoTraductions) {
        this.editThesoTraductions = editThesoTraductions;
    }

    public String getLangueTrad() {
        return langueTrad;
    }

    public void setLangueTrad(String langueTrad) {
        this.langueTrad = langueTrad;
    }

    public String getNomTrad() {
        return nomTrad;
    }

    public void setNomTrad(String nomTrad) {
        this.nomTrad = nomTrad;
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public Thesaurus getEditTraduction() {
        return editTraduction;
    }

    public void setEditTraduction(Thesaurus editTraduction) {
        this.editTraduction = editTraduction;
    }


    
}
