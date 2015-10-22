package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.LanguageBean;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;
import mom.trd.opentheso.bdd.tools.MD5Password;

@ManagedBean(name = "user1", eager = true)
@SessionScoped

public class CurrentUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String name = null;
    private String pwd = null;
    private NodeUser user;
    private boolean isLogged = false;
    
    private int idEdit;
    private String nameEdit;
    private String mailEdit;
    private String roleEdit;
    private String pwdEdit1 = "";
    private String pwdEdit2 = "";
    private String pwdEdit3 = "";
    private String langSourceEdit;
    private int alertNbCdtEdit;
    private boolean alertCdtEdit;
    
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;
    
    @ManagedProperty(value = "#{vue}")
    private Vue vue;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    @PostConstruct
    public void initUser() {
        user = new NodeUser();
        if (connect.getPoolConnexion() != null) {
            NodePreference np = new UserHelper().getPreferenceUser(connect.getPoolConnexion());
            langSourceEdit = np.getSourceLang();
            alertNbCdtEdit = np.getNbAlertCdt();
            alertCdtEdit = np.isAlertCdt();
        }
    }
    
    /**
     * Connect l'utilisateur si le compte existe
     * @return le lien de l'index si le compte existe, un message d'erreur sinon
     */
    public String action() {
        if(new UserHelper().isUserExist(connect.getPoolConnexion(), name, MD5Password.getEncodedPassword(pwd))) {
            isLogged = true; 
            user = new UserHelper().getInfoUser(connect.getPoolConnexion(), name);
            name = null;
            pwd = null;
            return "index.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error1")));
        }
        return "";
    }
    
    public ArrayList<NodeUser> selectAllUser() {
        ArrayList<NodeUser> listeUser = new UserHelper().getAllUsers(connect.getPoolConnexion());
        return listeUser;
    }
    
    public ArrayList<Entry<String,String>> selectAllRoles() {
        return new UserHelper().getRoles(connect.getPoolConnexion());
    }
    
    public void changePwd() {
        if(pwdEdit1 == null || pwdEdit1.equals("") || pwdEdit2 == null || pwdEdit2.equals("") || pwdEdit3 == null || pwdEdit3.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error2")));
        } else if (!pwdEdit2.equals(pwdEdit3)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error3")));
        } else if(!new UserHelper().isUserExist(connect.getPoolConnexion(), user.getName(), MD5Password.getEncodedPassword(pwdEdit1))) {
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
        if(mailEdit == null || mailEdit.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error6")));
        }else if(!mailEdit.contains("@") || mailEdit.lastIndexOf(".") < mailEdit.indexOf("@")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error7")));
        } else if(new UserHelper().isUserMailExist(connect.getPoolConnexion(), mailEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error8")));
        } else {
            new UserHelper().updateMail(connect.getPoolConnexion(), user.getId(), mailEdit);
            user.setMail(mailEdit);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info5")));
        }
        mailEdit = "";
    }

    public void delUser(int idUser) {
        new UserHelper().deleteUser(connect.getPoolConnexion(), idUser);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info2")));
    }
    
    public void addUser() {
        if(pwdEdit1 == null || pwdEdit1.equals("") || pwdEdit2 == null || pwdEdit2.equals("") || nameEdit == null || nameEdit.equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error2")));
        } else if (!pwdEdit1.equals(pwdEdit2)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error3")));
        } else if (new UserHelper().isUserLoginExist(connect.getPoolConnexion(), nameEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error5")));
        } else {
            new UserHelper().addUser(connect.getPoolConnexion(), nameEdit, mailEdit, MD5Password.getEncodedPassword(pwdEdit1), Integer.parseInt(roleEdit));
            vue.setAddUser(false);
            mailEdit = "";
            pwdEdit1 = "";
            pwdEdit2 = "";
            nameEdit = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info3")));
        }
    }
    
    public void editUser() {
        new UserHelper().updateRoleUser(connect.getPoolConnexion(), idEdit, Integer.parseInt(roleEdit));
        nameEdit = "";
        vue.setEditUser(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info4")));
    }
    
    public void selectUser(NodeUser nu) {
        nameEdit = nu.getName();
        idEdit = nu.getId();
        roleEdit = nu.getIdRole();
        vue.setEditUser(true);
    }
    
    public void reInit() {
        pwdEdit1 = "";
        pwdEdit2 = "";
        pwdEdit3 = "";
        nameEdit = "";
        vue.setEditUser(false);
        vue.setAddUser(false);
    }
    
    public void editPrefUser() {
        NodePreference np = new NodePreference();
        np.setAlertCdt(alertCdtEdit);
        np.setSourceLang(langSourceEdit);
        np.setNbAlertCdt(alertNbCdtEdit);
        new UserHelper().updatePreferenceUser(connect.getPoolConnexion(), np);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info6")));
    }
    
    public boolean haveRights(int min) {
        return Integer.parseInt(user.getIdRole()) <= min;
    }
    
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

    public String getRoleEdit() {
        return roleEdit;
    }

    public void setRoleEdit(String roleEdit) {
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

    public int getAlertNbCdtEdit() {
        return alertNbCdtEdit;
    }

    public void setAlertNbCdtEdit(int alertNbCdtEdit) {
        this.alertNbCdtEdit = alertNbCdtEdit;
    }

    public boolean isAlertCdtEdit() {
        return alertCdtEdit;
    }

    public void setAlertCdtEdit(boolean alertCdtEdit) {
        this.alertCdtEdit = alertCdtEdit;
    }
}
