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
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.tools.MD5Password;

@ManagedBean(name = "user1", eager = true)
@SessionScoped

public class CurrentUser implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name = null;
    private String pseudo;
    private String pwd = null;
    private NodeUser user;
    private boolean isLogged = false;

    private NodeUser userEdit;
    private int idEdit;
    private String nameEdit;
    private String mailEdit;
    private int roleEdit;
    private String pwdEdit1 = "";
    private String pwdEdit2 = "";
    private String pwdEdit3 = "";
    private boolean alertmail = false;

    //pref
    private String langSourceEdit;

    NodePreference nodePreference;

    private String idTheso;
    private List<String> authorizedTheso;

    private List<String> selectedThesaurus;

    private boolean isHaveWriteToCurrentThesaurus = false;

    private String versionOfOpentheso;

    private boolean isActive = false;

    
  /*  @ManagedProperty(value = "#{langueBean}")*/
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
        nodePreference = preferencesHelper.getThesaurusPreferences(connect.getPoolConnexion(), idThesaurus);
    }


 

    /**
     * Connect l'utilisateur si le compte existe
     *
     * @return le lien de l'index si le compte existe, un message d'erreur sinon
     * init c'est une parametre que viens du "isUserExist" ou return une 1 si on
     * fait le login normal (usuaire, pass), une 2 si on fait le login avec le
     * motpasstemp (et nous sommes dirigées a la page web de changer le
     * motpasstemp)
     */
    public String connect() {

        UserHelper userHelper = new UserHelper();
        if (userHelper.isUserExist(connect.getPoolConnexion(), name, MD5Password.getEncodedPassword(pwd))) {
            try {
                // on vérifie si l'utilisateur est SuperAdmin, on lui donne tout les droits
                if (userHelper.isSuperAdmin(connect.getPoolConnexion(), name)) {
                    user = userHelper.getInfoAdmin(connect.getPoolConnexion(), name);
                    if (user == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.roleError")));
                        return "";
                    }
                    user.setIdThesaurus(idTheso);
                    authorizedTheso = new ThesaurusHelper().getAllIdOfThesaurus(connect.getPoolConnexion(), true);
                    FacesContext context = FacesContext.getCurrentInstance();
                    String version_Opentheso = context.getExternalContext().getInitParameter("version");
                    BaseDeDoneesHelper baseDeDonnesHelper = new BaseDeDoneesHelper();
                    baseDeDonnesHelper.updateVersionOpentheso(connect.getPoolConnexion(), version_Opentheso);
                } // on récupère ses droits par rapport au thésaurus en cours
                else {
                    NodeUser nodeUserTemp = userHelper.getInfoUser(connect.getPoolConnexion(), name, idTheso);
                    if (nodeUserTemp == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("info") + " :", langueBean.getMsg("user.roleErrorAll")));
                        nodeUserTemp = userHelper.getInfoUser(connect.getPoolConnexion(), name);
                        if (nodeUserTemp == null) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error1")));
                            return "";
                        }
                    } else {
                        user = userHelper.getInfoUser(connect.getPoolConnexion(), name, idTheso);
                    }
                    authorizedTheso = userHelper.getAuthorizedThesaurus(connect.getPoolConnexion(), user.getId());
                }

                isLogged = true;
                if (userEdit != null) {
                    isActive = userEdit.isIsActive();
                }
             
                if (userHelper.isChangeToPass(connect.getPoolConnexion(), name)) {
                    return "changePass.xhtml?faces-redirect=true";// nouvelle pass web pour changer le motpasstemp
                }
                name = null;
                pwd = null;
                return "index.xhtml?faces-redirect=true";
            } catch (SQLException ex) {
                Logger.getLogger(CurrentUser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       
       
        // utilisateur ou mot de passe n'existent pas
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error1")));
        return "";
      
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
        if (pwdEdit1 == null || pwdEdit1.equals("") || pwdEdit2 == null || pwdEdit2.equals("") || pwdEdit3 == null || pwdEdit3.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error2")));
        } else if (!pwdEdit2.equals(pwdEdit3)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error3")));
        } else if (!new UserHelper().isUserExist(connect.getPoolConnexion(), user.getName(), MD5Password.getEncodedPassword(pwdEdit1))) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error4")));
        } else {
            new UserHelper().updatePwd(connect.getPoolConnexion(), user.getId(), MD5Password.getEncodedPassword(pwdEdit2));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info1")));
        }
        pwdEdit1 = "";
        pwdEdit2 = "";
        pwdEdit3 = "";
    }
  
    public void changeMail() {
        if (mailEdit == null || mailEdit.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error6")));
        } else if (!mailEdit.contains("@") || mailEdit.lastIndexOf(".") < mailEdit.indexOf("@")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error7")));
        } else if (new UserHelper().isUserMailExist(connect.getPoolConnexion(), mailEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error8")));
        } else {
            new UserHelper().updateMail(connect.getPoolConnexion(), user.getId(), mailEdit);
            user.setMail(mailEdit);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info5")));
        }
        mailEdit = "";
    }    
    

    public void renamePseudo() {
        if (pseudo == null || pseudo.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error6")));
        } else if (new UserHelper().isPseudoExist(connect.getPoolConnexion(), pseudo)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error5")));
        } else {
            new UserHelper().updatePseudo(connect.getPoolConnexion(), user.getId(), pseudo);
            user.setName(pseudo);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info5")));
        }
        name  = pseudo;
        pseudo = "";
    }

    public void delUser(int idUser) {
        new UserHelper().deleteUser(connect.getPoolConnexion(), idUser);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info2")));
    }

    public void addUser() throws SQLException {
        if (pwdEdit1 == null || pwdEdit1.equals("") || pwdEdit2 == null || pwdEdit2.equals("") || nameEdit == null || nameEdit.equals("") || mailEdit.equals("") || mailEdit == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error2")));
        } else if (!pwdEdit1.equals(pwdEdit2)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error3")));
        } else if (new UserHelper().isUserLoginExist(connect.getPoolConnexion(), nameEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error5")));
        } else {
            UserHelper userHelper = new UserHelper();
            Connection conn;
            conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            int idUser = -1;

            if (userHelper.isUserMailExist(conn, mailEdit)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.info7")));
            } else {
                try {
                    // si l'utilisateur est un superAdmin, il peut créer un superAdmin                
                    // Cas de création de SuperAdmin
                    if (user.getIdRole() == 1) {
                        // ajout de l'utilisateur
                        if (!userHelper.addUser(conn, nameEdit, mailEdit, MD5Password.getEncodedPassword(pwdEdit1), roleEdit)) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }
                        // récupération de l'Id du User
                        idUser = userHelper.getIdUser(conn, nameEdit);
                        if (idUser == -1) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }

                        // ajout du role 
                        if (!userHelper.addRole(conn, idUser, roleEdit, idTheso, "")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }
                    }

                    // Cas de création d'admin (par thésaurus)
                    if (user.getIdRole() == 2) {
                        // ajout de l'utilisateur
                        if (!userHelper.addUser(conn, nameEdit, mailEdit, MD5Password.getEncodedPassword(pwdEdit1), roleEdit)) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }
                        // récupération de l'Id du User
                        idUser = userHelper.getIdUser(conn, nameEdit);
                        if (idUser == -1) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                            conn.rollback();
                            conn.close();
                            return;
                        }

                        // ajout du role 
                        if (!userHelper.addRole(conn, idUser, roleEdit, idTheso, "")) {
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
                    userHelper.updateAddUserHistorique(conn, nameEdit);
                    
                    conn.commit();
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CurrentUser.class.getName()).log(Level.SEVERE, null, ex);
                }

                mailEdit = "";
                pwdEdit1 = "";
                pwdEdit2 = "";
                nameEdit = "";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info3")));
            }
        }
    }

    public void editUserRole() {
        UserHelper userHelper = new UserHelper();
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if (!userHelper.updateRoleUser(conn, idEdit, roleEdit, selectedThesaurus)) {
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
        nameEdit = "";
        vue.setEditUser(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info4")));
    }

    public void selectUser(NodeUser nu) {
        nameEdit = nu.getName();
        idEdit = nu.getId();
        roleEdit = nu.getIdRole();
        vue.setEditUser(true);
        selectedThesaurus = new UserHelper().getAuthorizedThesaurus(connect.getPoolConnexion(),
                idEdit);
        UserHelper userHelper = new UserHelper();
        userEdit = userHelper.getInfoUser(connect.getPoolConnexion(), nameEdit, idTheso);
        isActive = userEdit.isIsActive();
        alertmail = userEdit.isIsAlertMail();
    }

    public void reInit() {
        pwdEdit1 = "";
        pwdEdit2 = "";
        pwdEdit3 = "";
        nameEdit = "";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPwdEdit1() {
        return pwdEdit1;
    }

    public void setPwdEdit1(String pwdEdit1) {
        this.pwdEdit1 = pwdEdit1;
    }

    public String getPwdEdit2() {
        return pwdEdit2;
    }

    public void setPwdEdit2(String pwdEdit2) {
        this.pwdEdit2 = pwdEdit2;
    }

    public String getPwdEdit3() {
        return pwdEdit3;
    }

    public void setPwdEdit3(String pwdEdit3) {
        this.pwdEdit3 = pwdEdit3;
    }

    public String getNameEdit() {
        return nameEdit;
    }

    public void setNameEdit(String nameEdit) {
        this.nameEdit = nameEdit;
    }

    public int getRoleEdit() {
        return roleEdit;
    }

    public void setRoleEdit(int roleEdit) {
        this.roleEdit = roleEdit;
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

    public String getMailEdit() {
        return mailEdit;
    }

    public void setMailEdit(String mailEdit) {
        this.mailEdit = mailEdit;
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

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public boolean isAlertmail() {
        return alertmail;
    }

    public void setAlertmail(boolean alertmail) {
        this.alertmail = alertmail;
    }

 
}
