package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.Connexion;

@ManagedBean(name = "langueBean", eager = true)
@SessionScoped
public class LanguageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String currentBundle;
    private String idLangue;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{user1}")
    private CurrentUser user;

    private ResourceBundle getBundleLangue(String l) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundleLangue = context.getApplication().getResourceBundle(context, l);
        return bundleLangue;
    }

    /**
     * Constructeur
     */
    public LanguageBean() {
        //    FacesContext context = FacesContext.getCurrentInstance();
    }

    @PostConstruct
    public void InitLanguageBean() {
        user.setLangueBean(this);
        if (connect.getPoolConnexion() != null && user != null && user.getNodePreference() != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            //ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
            String langInit =  user.getNodePreference().getSourceLang();//bundlePref.getString("workLanguage");
                    // String langInit = new UserHelper().getPreferenceUser(connect.getPoolConnexion()).getSourceLang();
                    currentBundle = "langue_" + langInit;
            idLangue = langInit.toUpperCase();
        } else {
            currentBundle = "langue_fr";
            idLangue = "FR";
        }
    }

    public void changeLangue(String l) {
        currentBundle = "langue_" + l;
        idLangue = l.toUpperCase();
    }

    public String getMsg(String msg) {
        return getBundleLangue(currentBundle).getString(msg);
    }

    public String getIdLangue() {
        return idLangue;
    }

    public void setIdLangue(String idLangue) {
        this.idLangue = idLangue;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getCurrentBundle() {
        return currentBundle;
    }

    public void setCurrentBundle(String currentBundle) {
        this.currentBundle = currentBundle;
    }

    public CurrentUser getUser() {
        return user;
    }

    public void setUser(CurrentUser user) {
        this.user = user;
    }
    
    
}
