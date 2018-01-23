/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;


import java.io.IOException;
import java.util.HashMap;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.AccessThesaurusHelper;

/**
 *
 * @author jm.prudham
 */
@ManagedBean(name = "accessThesaurusBean", eager = true)
@SessionScoped
public class AccessThesaurusBean {
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty (value="#{theso}")
    private SelectedThesaurus theso;
    @ManagedProperty(value = "#{user1}")
    private CurrentUser user;
    
  
    //liste des thesaurus public ou privée de l'utilisateur si l'utilisateur est connecté
    private HashMap listAccess;
    //cache de la liste pour switch entre la liste des thésaurus privé et la liste normale
    private HashMap listAccessCache;
    //booleen pour switcher entre les deux listes
    private boolean onlyOwned;
    
    //identifiant du thésaurus sélctionner (repris de selectedThesaurus)
    private String idTheso;
    
    //thesaurus à gérer
    private Thesaurus accessThesaurus;
    
 
   
    /**
     * Creates a new instance of accessThesaurus
     */
    
    public AccessThesaurusBean() {
    
    }
    
    /**
     * fonction pour sortir une liste (sous forme de hasmap ) de thesaurus 
     * correspondant à l'utilisateur connecté, et de thésaurus publics,
     * on suppose que l'identifiant user =0 signifie qu'aucun utilisateur n'est connecté
     *#JM 
     * 
     */
    public void accessList(){
        AccessThesaurusHelper ath = new AccessThesaurusHelper();
        int idUser=user.getUser().getId();
        String lng=theso.getWorkLanguage();
        if(idUser==0){
            this.setListAccess(ath.getListThesaurus(connect.getPoolConnexion(),lng ));
            
        }
        else{
            if(user.haveRights(1)){
                this.setListAccess(ath.getListThesaurusSA(connect.getPoolConnexion(),lng));
            }else
            {
            
                this.setListAccess(ath.getListThesaurusUser(connect.getPoolConnexion(),lng, idUser));
            }
        }
        
        /***message pour le drag and drop #jm****/
         if( (user.getUser().getName()!= null) && (user.isIsHaveWriteToCurrentThesaurus()) )
       {
        String message="drag & dop activé !";
        FacesContext context = FacesContext.getCurrentInstance();
         
        context.addMessage(null, new FacesMessage("Successful",  "info : " + message) );
       }
        /********************************************/
       
    }
   
    /**
     * Fonction accessAThesaurus
     * fonction pour afficher un Thesaurus avec tous ses champs, dans la variable privée thesaurus
     * #JM
     * @param idTheso 
     */
    public void accessAThesaurus(String idTheso){
        AccessThesaurusHelper ath = new AccessThesaurusHelper();
        String idLang =theso.getWorkLanguage();
        this.accessThesaurus=ath.getAThesaurus(connect.getPoolConnexion(), idTheso, idLang);
        
    }
    
    /**
     * Fonction accessOwnerThesaurus
     * #JM
     * Fonction pour récupérer les thésaurus liée à l'utilisateur 
     * Si on  a déjà chargé une fois cette liste on ne fera pas de seconde requète sur la BDD
     * depuis la page d'index il  y'a pas de justification en effet à faire des requêtes à chaque fois que l'on c
     *utilise la checkbox boolean .....
     * todo: vérifier que lorsuque l'on passe par le menu edition dans une sessions, il y a bien un appelle 
     * notamment dans le cas ou l'on ajoute un thésaurus....
     * 
     */
    public void accessOwnerThesaurus(){
    /*     if(listAccessCache!=null && !(listAccessCache.isEmpty())){
         return; 
        }*/
        AccessThesaurusHelper ath=new AccessThesaurusHelper();
        int idUser=user.getUser().getId();
        String idLang=theso.getWorkLanguage();
        this.listAccessCache=new HashMap(this.listAccess);
        this.setListAccess(ath.getListThesaurusOwned(this.connect.getPoolConnexion(), idLang, idUser));
        
        
    }
    
    /**
     * fonction addVisbility
     * #JM
     * fonction pour update ou insert d'une valeur dans visibility dans la table thesaurus via la classe accessThesaurusHelper 
     */
    public void addVisibility(){
        boolean visible=this.accessThesaurus.isVisibility();
        String id=theso.getEditTheso().getId_thesaurus();
        AccessThesaurusHelper ath=new AccessThesaurusHelper();
        int ret=ath.insertVisibility(this.connect.getPoolConnexion(),visible,id);
        System.out.println("valeur retour insertVibility ="+ret);
        theso.setEditTheso(new Thesaurus());
    }
    
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
        this.accessThesaurus=null;
        this.listAccess=null;
        this.listAccessCache=null;
        this.onlyOwned=false;
        FacesContext.getCurrentInstance().getExternalContext().redirect("deco.xhtml");
        
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

    public CurrentUser getUser() {
        return user;
    }

    public void setUser(CurrentUser user) {
        this.user = user;
    }
    /**
     * Fonction getListaccess
     * #JM
     * Cette fonction gère les appels de la liste de thesaurus, si on a déjà eu un appel pour chacune des variables
     * listAccess et lisAccessCache , on ne rappelle pas la fonction accessList
     * @return HashMap la valeur listAccess 
     */
    public HashMap getListAccess() {
        
        if(listAccess==null || listAccess.isEmpty() || listAccessCache==null || listAccessCache.isEmpty() ){
        this.accessList();
        if(user.getUser().getId()!=0){
            this.setOnlyOwned(true);
            this.accessOwnerThesaurus();
            
        }
        }
       
        return listAccess;
    }

    public void setListAccess(HashMap listAccess) {
        this.listAccess = listAccess;
    }

    /**
     * Fonction getAccessThesaurus,
     * #JM
     * Si il n'y pas de valeur de thesaurus dans la variable accessThesaurus
     * alors c'est qu'on doit créer un thésaurus vide
     * @return 
     */
    public Thesaurus getAccessThesaurus() {
        if(accessThesaurus==null){
            accessThesaurus=new Thesaurus();
        }
        return accessThesaurus;
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
            this.accessThesaurus=new Thesaurus();
        }
        else{
        this.accessThesaurus = thesaurus;
        }
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }

    public boolean isOnlyOwned() {
        return onlyOwned;
    }
    /**
     * Fonction setOnlyOwned
     * fonction qui prend en cahrge la check box du panneau de connexion au thesaurus de la page d'index
     * aucune valeur n'est chargé depuis la base de donnée si les variables listAccess et listAccessCache sont affecté 
     * de valeurs non vide et non null
     * la fonction fait seulement un swith en tre les deux variables pour les garder en cache 
     *
     * @param onlyOwned boolean 
     */
    public void setOnlyOwned(boolean onlyOwned) {
        if(this.onlyOwned==onlyOwned){
            return;
        }
        if(listAccess!=null && listAccessCache!=null && !(listAccess.isEmpty()) && !(listAccessCache.isEmpty()) ){
            HashMap tmp=new HashMap(listAccess);
            this.listAccess=new HashMap(listAccessCache);
            this.listAccessCache=new HashMap(tmp);
        }
        this.onlyOwned = onlyOwned;
    }

   
    /**
     * changeVisibility
     * #jm
     * fonction pour passer de visibilité privée à publique
     */
    public void changeVisibility() {
        boolean changeVisibility =this.accessThesaurus.isVisibility();
        this.accessThesaurus.setVisibility(!(changeVisibility));
        this.addVisibility();
        System.out.println("visbility change "+this.accessThesaurus.isVisibility());
    }
    
    
    
   

    
   
}
