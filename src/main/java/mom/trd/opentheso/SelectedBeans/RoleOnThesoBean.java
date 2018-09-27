/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AccessThesaurusHelper;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.UserHelper2;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUserRoleGroup;

/**
 *
 * @author Miled Rousset
 */
@ManagedBean(name = "roleOnTheso", eager = true)
@SessionScoped
public class RoleOnThesoBean {
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{currentUser}")
    private CurrentUser2 user;
    @ManagedProperty(value="#{statBean}")
    private StatBean statistic;
    
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;
    
    //liste des thesaurus public suivant les droits de l'utilisateur
    private Map<String, String> listTheso;    
    
    //identifiant du thésaurus sélctionner (repris de selectedThesaurus)
    private String idTheso;
    
    //thesaurus à gérer
    private Thesaurus thesoInfos;
    
    private boolean isSuperAdmin = false;
    private boolean isAdminOnThisTheso = false;
    private boolean isManagerOnThisTheso = false;
    private boolean isContributorOnThisTheso = false;   
    
    private List<String> authorizedTheso;
    
    private NodePreference nodePreference;    
    
    private NodeUserRoleGroup nodeUserRoleGroup;
    
    public RoleOnThesoBean() {
    
    }
    
 //// restructuration de la classe User le 05/04/2018 //////    
    
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////
 ////////////////// Nouvelles fontions //////////////////////////////
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////       
    
    /**
     * permet de récupérer les préférences pour le thésaurus sélectionné 
     * s'il y en a pas, on les initialises par les valeurs par defaut
     * @param idThesaurus 
     * #MR
     */
    public void initUserNodePref(String idThesaurus) {
        if(idThesaurus == null) return;
        PreferencesHelper preferencesHelper = new PreferencesHelper();
        idTheso = idThesaurus;
    /*    if (user.getUser() == null) {
            nodePreference = null;
            return;
        }*/
        if (connect.getPoolConnexion() != null) {
            nodePreference = preferencesHelper.getThesaurusPreferences(connect.getPoolConnexion(), idThesaurus);
            if (nodePreference == null) { // cas où il n'y a pas de préférence pour ce thésaurus, il faut les créer 
                preferencesHelper.initPreferences(connect.getPoolConnexion(),
                        idThesaurus, connect.getWorkLanguage());
                nodePreference = preferencesHelper.getThesaurusPreferences(connect.getPoolConnexion(), idThesaurus);
            }
            return;
        }
        nodePreference = null;
    }    
    
    public void updatePreferences() {
        if (!new PreferencesHelper().updateAllPreferenceUser(connect.getPoolConnexion(), nodePreference, idTheso)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
            return;
        }
        initUserNodePref(idTheso);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info6")));
    }
    
    /**
     * permet d'initialiser la liste des thésaurus suivant les droits
     */
    public void showListTheso(){
        if(user.getUser() == null)
            setPublicThesos();
        else {
            setOwnerThesos();
        }
    }
    
    /**
     * fonction pour sortir une liste (sous forme de hashMap ) de thesaurus 
     * correspondant à l'utilisateur connecté
     * permet de charger les thésaurus autorisés pour l'utilisateur en cours 
     * on récupère les id puis les tradcutions (ceci permet de récupérer les thésaurus non traduits) 
     * #MR
     */
    private void setOwnerThesos(){
        if(user.getUser() == null){
            this.listTheso = new HashMap();
            return;
        }
        authorizedTheso = new ArrayList<>();
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        UserHelper2 userHelper = new UserHelper2();
        if (user.getUser().isIsSuperAdmin()) {
            boolean withPrivateTheso = true;
            authorizedTheso = thesaurusHelper.getAllIdOfThesaurus(connect.getPoolConnexion(), withPrivateTheso);

        } else {
            authorizedTheso = userHelper.getThesaurusOfUser(connect.getPoolConnexion(), user.getUser().getIdUser());
        }
        addAuthorizedThesoToHM();
        
        // permet de définir le role de l'utilisateur sur le group
        if(authorizedTheso.isEmpty())
            setUserRoleGroup(); 
        
//        setUserRoleOnThisTheso();
    }
    // on ajoute les titres + id, sinon l'identifiant du thésauurus
    private void addAuthorizedThesoToHM() {
        if (authorizedTheso == null) {
            return;
        }
        HashMap<String, String> authorizedThesoHM = new LinkedHashMap();
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        String title;
        String lang = connect.getWorkLanguage().toLowerCase();

        for (String idTheso1 : authorizedTheso) {
            title = thesaurusHelper.getTitleOfThesaurus(connect.getPoolConnexion(), idTheso1, lang);
            if (title == null) {
                authorizedThesoHM.put("" + "(" + idTheso1 + ")", idTheso1);
            } else {
                authorizedThesoHM.put(title + "(" + idTheso1 + ")", idTheso1);
            }
        }
        this.listTheso = authorizedThesoHM;
    }     
    
    /**
     * fonction pour récupérer la liste (sous forme de hashMap ) de thesaurus 
     * tous les thésaurus sauf privés
     *#MR 
     * 
     */
    private void setPublicThesos(){
        boolean withPrivateTheso = false;
    /*    AccessThesaurusHelper ath = new AccessThesaurusHelper();
        this.listTheso = ath.getListThesaurus(connect.getPoolConnexion(),connect.getWorkLanguage());*/
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        authorizedTheso = thesaurusHelper.getAllIdOfThesaurus(connect.getPoolConnexion(), withPrivateTheso);
        addAuthorizedThesoToHM();     
    }   
    
    /**
     * permet de savoir si l'utilisateur a les droits sur ce thésaurus
     * @return 
     */
    public boolean isIsHaveWriteToCurrentThesaurus() {
        if (user.getUser() == null) {
            return false;
        }
        if (idTheso == null) {
            return false;
        }
        if (user.getUser().isIsSuperAdmin()) {
            return true;
        }
        return authorizedTheso.contains(idTheso);
    }
    
    /**
     * Permet de définir le role d'un utilisateur sur le thésaurus en cours 
     * le groupe du thésaurus est trouvé automatiquement, si l'utilisateur est SuperAdmin, pas besoin du groupe
     *
     * #MR
     */
    public void setUserRoleOnThisTheso() {
        if(user.getUser() == null) {
            nodeUserRoleGroup = null;
            return;
        }
        if(idTheso == null) {
            nodeUserRoleGroup = null;
            return;
        }   
        initRoles();
        UserHelper2 userHelper = new UserHelper2();
        
        if(user.getUser().isIsSuperAdmin()) {
            nodeUserRoleGroup = user.getUserRoleOnThisGroup(-1); // cas de superadmin, on a accès à tous les groupes
        } else { 
            int idGroup = userHelper.getGroupOfThisTheso(connect.getPoolConnexion(), idTheso);
            nodeUserRoleGroup = user.getUserRoleOnThisGroup(idGroup);
        }
        if(nodeUserRoleGroup == null) return;
        
        if(nodeUserRoleGroup.getIdRole() == 1) 
            setIsSuperAdmin(true);        
        if(nodeUserRoleGroup.getIdRole() == 2) 
            setIsAdminOnThisTheso(true);
        if(nodeUserRoleGroup.getIdRole() == 3) 
            setIsManagerOnThisTheso(true);
        if(nodeUserRoleGroup.getIdRole() == 4) 
            setIsContributorOnThisTheso(true);        
    }  
    
    /**
     * permet de récuperer le role de l'utilisateur sur le group 
     * applelé en cas où le group n'a aucun thésaurus pour que l'utilisateur 
     * puisse créer des thésaurus et gérer les utilisateur pour le group 
     * il faut être Admin
     */
    private void setUserRoleGroup() {
        UserHelper2 userHelper = new UserHelper2();
        ArrayList<NodeUserRoleGroup> nodeUserRoleGroups  = userHelper.getUserRoleGroup(connect.getPoolConnexion(), user.getUser().getIdUser());
        for (NodeUserRoleGroup nodeUserRoleGroup1 : nodeUserRoleGroups) {
            if(nodeUserRoleGroup1.isIsAdmin())
                setIsAdminOnThisTheso(true);
        }
    }
    
    private void initRoles(){
        setIsSuperAdmin(false);        
        setIsAdminOnThisTheso(false);
        setIsManagerOnThisTheso(false);
        setIsContributorOnThisTheso(false);   
    }
    
   
    
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////
 //////// fin des nouvelles fontions ////////////////////////////////
 ////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////          
    
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Fonction accessAThesaurus
     * fonction pour afficher un Thesaurus avec tous ses champs, dans la variable privée thesaurus
     * #JM
     * @param idTheso 
     */
    public void accessAThesaurus(String idTheso){
        AccessThesaurusHelper ath = new AccessThesaurusHelper();
        this.thesoInfos=ath.getAThesaurus(connect.getPoolConnexion(), idTheso, connect.getWorkLanguage());
    }
    
    /**
     * fonction addVisbility
     * #JM
     * fonction pour update ou insert d'une valeur dans visibility dans la table thesaurus via la classe accessThesaurusHelper 
     */
 /*   public void addVisibility(){
        boolean visible=this.thesoInfos.isVisibility();
        String id=theso.getEditTheso().getId_thesaurus();
        AccessThesaurusHelper ath=new AccessThesaurusHelper();
        int ret=ath.insertVisibility(this.connect.getPoolConnexion(),visible,id);
        System.out.println("valeur retour insertVibility ="+ret);
        theso.setEditTheso(new Thesaurus());
    }*/
    
    public void supprVisibility(String id){
        AccessThesaurusHelper ath=new AccessThesaurusHelper();
        
        int ret=ath.supprVisibility(this.connect.getPoolConnexion(),id);
        System.out.println("valeur retour supprVibility ="+ret);
    }
    /**
     * Fonction cleanSession
     * #JM
     * Fonction pour remettre les valeurs par défauts à ce bean
     * et que l'on rappelle bien une nouvelle fois la base de données
     * Provoque une redirection via deco.xhtml, pour être en cohérence avec l'implémentation précédente
     * (c'était la façon de se déconnecté depuis l'index dans les versions précédentes)
     * @throws IOException 
     */
    public void cleanSession() throws IOException{
        this.thesoInfos=null;
        setPublicThesos();
        this.statistic.reInit();
        user.setUser(null);
        FacesContext.getCurrentInstance().getExternalContext().redirect("deco.xhtml");
    }
   

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public CurrentUser2 getUser() {
        return user;
    }

    public void setUser(CurrentUser2 user) {
        this.user = user;
    }


    
    /**
     * Fonction getAccessThesaurus,
     * #JM
     * Si il n'y pas de valeur de thesaurus dans la variable accessThesaurus
     * alors c'est qu'on doit créer un thésaurus vide
     * @return 
     */
    public Thesaurus getAccessThesaurus() {
        if(thesoInfos==null){
            thesoInfos=new Thesaurus();
        }
        return thesoInfos;
    }
    /**
     * fonction setAccessThesaurus
     * #JM
     * fonction pour attribuer une valeur à au thesaurus en accès, la varaible est accessThesaurus
     * si on passe en paramètre de ce setter une valeur à null, le setter créer un nouveau thesaurus
     * @param thesaurus 
     */
    public void setAccessThesaurus(Thesaurus thesaurus) {
        if(thesaurus==null){
            this.thesoInfos=new Thesaurus();
        }
        else{
        this.thesoInfos = thesaurus;
        }
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }
   
    /**
     * changeVisibility
     * #jm
     * fonction pour passer de visibilité privée à publique
     */
    public void changeVisibility() {
        boolean changeVisibility =this.thesoInfos.isPrivateTheso();
        this.thesoInfos.setPrivateTheso(!(changeVisibility));
 //       this.addVisibility();
        System.out.println("visbility change "+this.thesoInfos.isPrivateTheso());
    }

    public StatBean getStatistic() {
        return statistic;
    }

    public void setStatistic(StatBean statistic) {
        this.statistic = statistic;
    }

    public boolean isIsAdminOnThisTheso() {
        return isAdminOnThisTheso;
    }

    public void setIsAdminOnThisTheso(boolean isAdminOnThisTheso) {
        this.isAdminOnThisTheso = isAdminOnThisTheso;
    }

    public boolean isIsManagerOnThisTheso() {
        return isManagerOnThisTheso;
    }

    public void setIsManagerOnThisTheso(boolean isManagerOnThisTheso) {
        this.isManagerOnThisTheso = isManagerOnThisTheso;
    }

    public boolean isIsContributorOnThisTheso() {
        return isContributorOnThisTheso;
    }

    public void setIsContributorOnThisTheso(boolean isContributorOnThisTheso) {
        this.isContributorOnThisTheso = isContributorOnThisTheso;
    }

    public boolean isIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public Map<String, String> getListTheso() {
        return listTheso;
    }

    public void setListTheso(HashMap<String, String> listTheso) {
        this.listTheso = listTheso;
    }

    public List<String> getAuthorizedTheso() {
        return authorizedTheso;
    }

    public void setAuthorizedTheso(List<String> authorizedTheso) {
        this.authorizedTheso = authorizedTheso;
    }

    public NodePreference getNodePreference() {
        return nodePreference;
    }

    public void setNodePreference(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

    public NodeUserRoleGroup getNodeUserRoleGroup() {
        return nodeUserRoleGroup;
    }

    public void setNodeUserRoleGroup(NodeUserRoleGroup nodeUserRoleGroup) {
        this.nodeUserRoleGroup = nodeUserRoleGroup;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

}
