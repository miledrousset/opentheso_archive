package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.BaseDeDoneesHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.tools.MD5Password;

@ManagedBean(name = "currentUser", eager = true)
@SessionScoped

public class CurrentUser implements Serializable {

    private static final long serialVersionUID = 1L;
    private String pseudo = null;
    private String pseudoEdit;
    private String pwd = null;
    private NodeUser user;
    private boolean isLogged = false;

    private NodeUser userEdit;
    private int idEdit;
    private String pseudoAdded;
    private String mailAdded;
    private int roleAdded;
    private String pwdAdded1 = "";
    private String pwdAdded2 = "";
    private String pwdAdded3 = "";
    private boolean alertmail = false;

    //pref
    private String langSourceEdit;

    NodePreference nodePreference;

    private String idTheso;
    private List<String> authorizedTheso;

    private List<String> selectedThesaurus;

    private String versionOfOpentheso;

    private boolean isActive = false;

    /*
    @ManagedProperty(value = "#{langueBean}")*/
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
  
    @PostConstruct
    public void initUser() {
        user = new NodeUser();
        if (connect.getPoolConnexion() != null) {
            versionOfOpentheso = new BaseDeDoneesHelper().getVersionOfOpentheso(connect.getPoolConnexion());
        }

        /*    if (connect.getPoolConnexion() != null) {
            NodePreference np = new UserHelper().getPreferenceUser(connect.getPoolConnexion());
            langSourceEdit = np.getSourceLang();
            alertNbCdtEdit = np.getNbAlertCdt();
            alertCdtEdit = np.isAlertCdt();
            
        }*/
    }
    
    public void updateUser() {
        if(user == null) return;
        if (connect.getPoolConnexion() != null) {
            if(user.getName() != null){
                user = new UserHelper().getInfoUser(connect.getPoolConnexion(), user.getName(), idTheso);
                alertmail = user.isIsAlertMail();
            }
        }
    }
    
    public void initUserNodePref(String idThesaurus) {
        PreferencesHelper preferencesHelper = new PreferencesHelper();
        nodePreference = preferencesHelper.getThesaurusPreference(connect.getPoolConnexion(), idThesaurus);
    }

    /**
     * cette fonction permet de retourner les préférences d'un thésaurus, s'il y
     * en a pas, on les initialises par les valeurs par defaut
     *
     * @param idThesaurus
     * @param workLanguage
     * @return
     */
    public NodePreference getThesaurusPreferences(String idThesaurus, String workLanguage) {
        if(user == null) return null;
        if(user.isIsSuperAdmin()) { // superAdmin
            authorizedTheso = new ThesaurusHelper().getAllIdOfThesaurus(connect.getPoolConnexion());
        } else { 
            authorizedTheso = new UserHelper().getAuthorizedThesaurus(connect.getPoolConnexion(), user.getId());
        }
        PreferencesHelper preferencesHelper = new PreferencesHelper();
        if (connect.getPoolConnexion() != null) {
            nodePreference = preferencesHelper.getThesaurusPreference(connect.getPoolConnexion(), idThesaurus);

            if (nodePreference == null) { // cas où il n'y a pas de préférence pour ce thésaurus, il faut les créer 
                preferencesHelper.initPreferences(connect.getPoolConnexion(),
                        idThesaurus, workLanguage);
                nodePreference = preferencesHelper.getThesaurusPreference(connect.getPoolConnexion(), idThesaurus);

            } else {
                langSourceEdit = nodePreference.getSourceLang();
            //    alertNbCdtEdit = nodePreference.getNbAlertCdt();
            //    alertCdtEdit = nodePreference.isAlertCdt();
                selectedThesaurus = new ArrayList<>();
                selectedThesaurus.add(idTheso);
            }
            idTheso = idThesaurus;
            versionOfOpentheso = new BaseDeDoneesHelper().getVersionOfOpentheso(connect.getPoolConnexion());
            return nodePreference;
        }
    }
 

    /**
     * Connect l'utilisateur si son compte en récupérant toutes les informations lui concernant
     *
     * @return le lien de l'index si le compte existe, un message d'erreur sinon
     * init c'est une parametre que viens du "isUserExist" ou return une 1 si on
     * fait le login normal (usuaire, pass), une 2 si on fait le login avec le
     * motpasstemp (et nous sommes dirigées a la page web de changer le
     * motpasstemp)
     */
    public String connect() {
        int idUser;
        UserHelper userHelper = new UserHelper();
        idUser = userHelper.getIdUser(connect.getPoolConnexion(), pseudo, MD5Password.getEncodedPassword(pwd));
        if(idUser == -1) {
            // utilisateur ou mot de passe n'existent pas
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error1")));
            return "";
        }
        
        // on récupère le compte de l'utilisatreur 
        user = userHelper.getUser(connect.getPoolConnexion(), idUser);
        if(user == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error1")));
            return "";            
        }
        isLogged = true;
        if (user.isPasstomodify()) {
            return "changePass.xhtml?faces-redirect=true";// nouvelle pass web pour changer le motpasstemp
        }
        pseudo = "";
        pwd = "";
        return "index.xhtml?faces-redirect=true";
                
        /**
         * code déprécié par #MR
         */
        /*
        if (userHelper.isUserExist(connect.getPoolConnexion(), pseudo, MD5Password.getEncodedPassword(pwd))) {
            try {
                // on vérifie si l'utilisateur est SuperAdmin, on lui donne tout les droits
                if (userHelper.isAdminUser(connect.getPoolConnexion(), pseudo)) {
                    user = userHelper.getInfoAdmin(connect.getPoolConnexion(), pseudo);
                    if (user == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.roleError")));
                        return "";
                    }
                    user.setIdThesaurus(idTheso);
                    authorizedTheso = new ThesaurusHelper().getAllIdOfThesaurus(connect.getPoolConnexion());
                    FacesContext context = FacesContext.getCurrentInstance();
                    String version_Opentheso = context.getExternalContext().getInitParameter("version");
                    BaseDeDoneesHelper baseDeDonnesHelper = new BaseDeDoneesHelper();
                    baseDeDonnesHelper.updateVersionOpentheso(connect.getPoolConnexion(), version_Opentheso);
                } // on récupère ses droits par rapport au thésaurus en cours
                else {
                    NodeUser nodeUserTemp = userHelper.getInfoUser(connect.getPoolConnexion(), pseudo, idTheso);
                    if (nodeUserTemp == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("info") + " :", langueBean.getMsg("user.roleErrorAll")));
                        nodeUserTemp = userHelper.getInfoUser(connect.getPoolConnexion(), pseudo);
                        if (nodeUserTemp == null) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error1")));
                            return "";
                        }
                    } else {
                        user = userHelper.getInfoUser(connect.getPoolConnexion(), pseudo, idTheso);
                    }
                    authorizedTheso = userHelper.getAuthorizedThesaurus(connect.getPoolConnexion(), user.getId());
                }

                isLogged = true;
                if (userEdit != null) {
                    isActive = userEdit.isIsActive();
                }
             
                if (userHelper.isChangeToPass(connect.getPoolConnexion(), pseudo)) {
                    return "changePass.xhtml?faces-redirect=true";// nouvelle pass web pour changer le motpasstemp
                }
                pseudo = null;
                pwd = null;
                return "index.xhtml?faces-redirect=true";
            } catch (SQLException ex) {
                Logger.getLogger(CurrentUser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // utilisateur ou mot de passe n'existent pas
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error1")));
        return "";*/
    }

    public boolean updateAuthorizedTheso() {
        UserHelper userHelper = new UserHelper();
        authorizedTheso = userHelper.getAuthorizedThesaurus(connect.getPoolConnexion(), user.getId());
        return true;
    }

    /**
     * permet de retourner tous les utilisateurs d'un thésaurus
     *
     * @return
     */
    private ArrayList<NodeUser> selectAllUserOfThesaurus() {
        ArrayList<NodeUser> listeUser = new UserHelper().getAllUsersOfThesaurus(connect.getPoolConnexion(), idTheso);
        return listeUser;
    }

    /**
     * Permet de retourner les les utilisateurs sans exception
     *
     * @return
     */
    private ArrayList<NodeUser> selectAllUsers() {
        ArrayList<NodeUser> listeUser = new UserHelper().getAllUsers(connect.getPoolConnexion());
        return listeUser;
    }

    /**
     * retoure la liste des utilisateurs suivant leurs droits
     *
     * @return
     */
    public ArrayList<NodeUser> selectAuthorizedUser() {
        UserHelper userHelper = new UserHelper();
        int idRoleFrom = 1;

        if (user.getIdRole() == 1) {// l'utilisateur est superAdmin
            return selectAllUsers();

        }

        if (user.getIdRole() == 2) {
            idRoleFrom = 2; // l'utilisateur est admin
        }
        if (user.getIdRole() > 2) {
            idRoleFrom = 3; // l'utilisateur est autre        
        }
        ArrayList<NodeUser> listeUser = userHelper.getAuthorizedUsers(connect.getPoolConnexion(), idTheso, idRoleFrom);
        return listeUser;
    }

    
    public boolean updateAlertMail() {
        try {
            UserHelper userHelper = new UserHelper();
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if(!userHelper.setAlertMailForUser(
                    connect.getPoolConnexion().getConnection(),user.getId(), alertmail)){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                conn.rollback();
                conn.close(); 
                return false;
            }
            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("conf.alertMailMessage")));
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(CurrentUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public ArrayList<Entry<String, String>> selectAllRoles() {
        return new UserHelper().getRoles(connect.getPoolConnexion());
    }

    /**
     * Cette fonction permet de retourner la liste des roles autorisés pour un
     * Role donné (c'est la liste qu'un utilisateur a le droit d'attribué à un
     * nouvel utilisateur)
     *
     * @return
     */
    public ArrayList<Entry<String, String>> selectAuthorizedRoles() {
        int idRoleFrom = 3;
        if (user.getIdRole() == 1) {
            idRoleFrom = 1; // l'utilisateur est superAdmin
        }
        if (user.getIdRole() == 2) {
            idRoleFrom = 2; // l'utilisateur est admin
        }
        if (user.getIdRole() > 2) {
            idRoleFrom = 3; // l'utilisateur est autre
        }
        return new UserHelper().getAuthorizedRoles(connect.getPoolConnexion(), idRoleFrom);
    }

    public void changePwd() {
        if (pwdAdded1 == null || pwdAdded1.equals("") || pwdAdded2 == null || pwdAdded2.equals("") || pwdAdded3 == null || pwdAdded3.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error2")));
        } else if (!pwdAdded2.equals(pwdAdded3)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error3")));
        } else if (!new UserHelper().isUserExist(connect.getPoolConnexion(), user.getName(), MD5Password.getEncodedPassword(pwdAdded1))) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error4")));
        } else {
            new UserHelper().updatePwd(connect.getPoolConnexion(), user.getId(), MD5Password.getEncodedPassword(pwdAdded2));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info1")));
        }
        pwdAdded1 = "";
        pwdAdded2 = "";
        pwdAdded3 = "";
    }
  
    public void changeMail() {
        if (mailAdded == null || mailAdded.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error6")));
        } else if (!mailAdded.contains("@") || mailAdded.lastIndexOf(".") < mailAdded.indexOf("@")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error7")));
        } else if (new UserHelper().isUserMailExist(connect.getPoolConnexion(), mailAdded)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error8")));
        } else {
            new UserHelper().updateMail(connect.getPoolConnexion(), user.getId(), mailAdded);
            user.setMail(mailAdded);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info5")));
        }
        mailAdded = "";
    }    
    

    public void renamePseudo() {
        if (pseudoEdit == null || pseudoEdit.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error6")));
        } else if (new UserHelper().isPseudoExist(connect.getPoolConnexion(), pseudoEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error5")));
        } else {
            new UserHelper().updatePseudo(connect.getPoolConnexion(), user.getId(), pseudoEdit);
            user.setName(pseudoEdit);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info5")));
        }
        pseudo  = pseudoEdit;
        pseudoEdit = "";
    }

    public void delUser(int idUser) {
        new UserHelper().deleteUser(connect.getPoolConnexion(), idUser);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info2")));
    }

    public void addUser() throws SQLException {
        if (pwdAdded1 == null || pwdAdded1.equals("") || pwdAdded2 == null || pwdAdded2.equals("") || pseudoAdded == null || pseudoAdded.equals("") || mailAdded.equals("") || mailAdded == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error2")));
        } else if (!pwdAdded1.equals(pwdAdded2)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error3")));
        } else if (new UserHelper().isUserLoginExist(connect.getPoolConnexion(), pseudoAdded)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error5")));
        } else {
            UserHelper userHelper = new UserHelper();
            Connection conn;
            conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            int idUser = -1;

            if (userHelper.isUserMailExist(conn, mailAdded)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.info7")));
            } else {
                try {
                    // si l'utilisateur est un superAdmin, il peut créer un superAdmin                
                    // Cas de création de SuperAdmin
                    if (user.getIdRole() == 1) {
                        // ajout de l'utilisateur
                        if (!userHelper.addUser(conn, pseudoAdded, mailAdded, MD5Password.getEncodedPassword(pwdAdded1), roleAdded)) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }
                        // récupération de l'Id du User
                        idUser = userHelper.getIdUser(conn, pseudoAdded);
                        if (idUser == -1) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }

                        // ajout du role 
                        if(idTheso != null) { // si idTheso = null, cas où on a aucun thésaurus actif, on n'applique pas les droits au thésaurus en cours
                            if (!userHelper.addRole(conn, idUser, roleAdded, idTheso, "")) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                                conn.rollback();
                                conn.close();
                                return;
                            }
                        }
                    }

                    // Cas de création d'admin (par thésaurus)
                    if (user.getIdRole() == 2) {
                        // ajout de l'utilisateur
                        if (!userHelper.addUser(conn, pseudoAdded, mailAdded, MD5Password.getEncodedPassword(pwdAdded1), roleAdded)) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }
                        // récupération de l'Id du User
                        idUser = userHelper.getIdUser(conn, pseudoAdded);
                        if (idUser == -1) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }

                        // ajout du role 
                        if (!userHelper.addRole(conn, idUser, roleAdded, idTheso, "")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }
                    }
                    // mise à jour des alerts mails pour l'utilisateur
                    if(alertmail) {
                        if(idUser != -1)
                            if(!userHelper.setAlertMailForUser(conn,idUser, true)){
                               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                                conn.rollback();
                                conn.close();
                                return; 
                            }
                    }
                    userHelper.updateAddUserHistorique(conn, pseudoAdded);
                    
                    conn.commit();
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CurrentUser.class.getName()).log(Level.SEVERE, null, ex);
                }

                mailAdded = "";
                pwdAdded1 = "";
                pwdAdded2 = "";
                pseudoAdded = "";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info3")));
            }
        }
    }

    public void editUserRole() {
        UserHelper userHelper = new UserHelper();
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if (!userHelper.updateRoleUser(conn, idEdit, roleAdded, selectedThesaurus)) {
                conn.rollback();
                conn.close();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                return;
            }
            // permet de mettre à jour le status de l'utilisateur (actif ou pas)
            if (!userHelper.updateStatusUser(conn, idEdit, isActive)) {
                conn.rollback();
                conn.close();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                return;
            }

            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(CurrentUser.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        pseudoAdded = "";
        vue.setEditUser(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info4")));
    }

    public void selectUser(NodeUser nu) {
        pseudoAdded = nu.getName();
        idEdit = nu.getId();
        roleAdded = nu.getIdRole();
        vue.setEditUser(true);
        selectedThesaurus = new UserHelper().getAuthorizedThesaurus(connect.getPoolConnexion(),
                idEdit);
        UserHelper userHelper = new UserHelper();
        userEdit = userHelper.getInfoUser(connect.getPoolConnexion(), pseudoAdded, idTheso);
        isActive = userEdit.isIsActive();
        alertmail = userEdit.isIsAlertMail();
    }

    public void reInit() {
        pwdAdded1 = "";
        pwdAdded2 = "";
        pwdAdded3 = "";
        pseudoAdded = "";
        vue.setEditUser(false);
        vue.setAddUser(false);
    }

    public void editAllPref() {
    //    nodePreference.setAlertCdt(alertCdtEdit);
        nodePreference.setSourceLang(langSourceEdit);
     //   nodePreference.setNbAlertCdt(roleEdit);
    //    nodePreference.setNbAlertCdt(alertNbCdtEdit);

        if (!new PreferencesHelper().updateAllPreferenceUser(connect.getPoolConnexion(), nodePreference, idTheso)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
            return;
        }
        initUserNodePref(idTheso);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info6")));
    }

    public boolean haveRights(int min) {
        if (user.getIdRole() < 1) {
            return false;
        }
        return user.getIdRole() <= min;
    }

    /**
     * vérification si l'utilisateur en cours a le droit d'effacer cet
     * utilisateur
     *
     * @param idUserToDelete
     * @return
     */
    /*  public boolean haveRightsToDelete(int idUserToDelete) {
        if(user.getIdRole() < 1) return false;
        
        return user.getIdRole() <= idUserToDelete;
    }*/
    public String afficheEncode() {
        return MD5Password.getEncodedPassword("demo");
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public NodeUser getUser() {
        return user;
    }

    public void setUser(NodeUser user) {
        this.user = user;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public String getPwdAdded1() {
        return pwdAdded1;
    }

    public void setPwdAdded1(String pwdAdded1) {
        this.pwdAdded1 = pwdAdded1;
    }

    public String getPwdAdded2() {
        return pwdAdded2;
    }

    public void setPwdAdded2(String pwdAdded2) {
        this.pwdAdded2 = pwdAdded2;
    }

    public String getPwdAdded3() {
        return pwdAdded3;
    }

    public void setPwdAdded3(String pwdAdded3) {
        this.pwdAdded3 = pwdAdded3;
    }

    public String getPseudoAdded() {
        return pseudoAdded;
    }

    public void setPseudoAdded(String pseudoAdded) {
        this.pseudoAdded = pseudoAdded;
    }

    public int getRoleAdded() {
        return roleAdded;
    }

    public void setRoleAdded(int roleAdded) {
        this.roleAdded = roleAdded;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public int getIdEdit() {
        return idEdit;
    }

    public void setIdEdit(int idEdit) {
        this.idEdit = idEdit;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getMailAdded() {
        return mailAdded;
    }

    public void setMailAdded(String mailAdded) {
        this.mailAdded = mailAdded;
    }

    public String getLangSourceEdit() {
        return langSourceEdit;
    }

    public void setLangSourceEdit(String langSourceEdit) {
        this.langSourceEdit = langSourceEdit;
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }

    public List<String> getSelectedThesaurus() {
        return selectedThesaurus;
    }

    public void setSelectedThesaurus(List<String> selectedThesaurus) {
        this.selectedThesaurus = selectedThesaurus;
    }

    public boolean isIsHaveWriteToCurrentThesaurus() {
        if(user == null) return false;
        if(user.getIdRole() == 1) return true;
        
        if (idTheso == null) {
            return false;
        }
        return authorizedTheso.contains(idTheso);
    }

    public void setIsHaveWriteToCurrentThesaurus(boolean isHaveWriteToCurrentThesaurus) {
        this.isHaveWriteToCurrentThesaurus = isHaveWriteToCurrentThesaurus;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getVersionOfOpentheso() {
        return versionOfOpentheso;
    }

    public void setVersionOfOpentheso(String versionOfOpentheso) {
        this.versionOfOpentheso = versionOfOpentheso;
    }

    public NodeUser getUserEdit() {
        return userEdit;
    }

    public void setUserEdit(NodeUser userEdit) {
        this.userEdit = userEdit;
    }

    public NodePreference getNodePreference() {
        return nodePreference;
    }

    public void setNodePreference(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

    public List<String> getAuthorizedTheso() {
        return authorizedTheso;
    }

    public void setAuthorizedTheso(List<String> authorizedTheso) {
        this.authorizedTheso = authorizedTheso;
    }

    public String getPseudoEdit() {
        return pseudoEdit;
    }

    public void setPseudoEdit(String pseudoEdit) {
        this.pseudoEdit = pseudoEdit;
    }

    public boolean isAlertmail() {
        return alertmail;
    }

    public void setAlertmail(boolean alertmail) {
        this.alertmail = alertmail;
    }

 
}
