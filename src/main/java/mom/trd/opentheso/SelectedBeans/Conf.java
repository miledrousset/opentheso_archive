package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "conf", eager = true)
@SessionScoped

public class Conf implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    private ArrayList<NodePrefs> nodePrefsAdmins;
    private ArrayList<NodePrefs> nodePrefsSuperAdmins;
    
    private NodePrefs selectedPrefAdmin;
    private NodePrefs selectedPrefSuperAdmin;
    
    @PostConstruct
    public void initConf() {

        nodePrefsAdmins = new ArrayList<>();
        addPrefAdmin("1",langueBean.getMsg("conf.modifPref"));//"Préférences");
        addPrefAdmin("2",langueBean.getMsg("conf.newId"));//"Générer des nouveaux identifiants");
        addPrefAdmin("3",langueBean.getMsg("copyright.addCopyrightTo"));
        addPrefAdmin("4",langueBean.getMsg("admin.regenerateArk"));//"Générer les identifiants Ark");
        addPrefAdmin("5",langueBean.getMsg("admin.regenerateHandle"));//"Générer les identifiants Ark");
        addPrefAdmin("6","Passez tous les identifiants alphnumériques en numériques");        
       
        nodePrefsSuperAdmins = new ArrayList<>();
        addPrefSuperAdmin("1",langueBean.getMsg("conf.sparql"));
        addPrefSuperAdmin("2",langueBean.getMsg("bdd.act"));
        
    }
    
    private void addPrefAdmin(String id, String value) {
        NodePrefs nodePrefsAdmin = new NodePrefs();
        nodePrefsAdmin.setId(id);
        nodePrefsAdmin.setPref(value);
        nodePrefsAdmins.add(nodePrefsAdmin);
    }
    
    private void addPrefSuperAdmin(String id, String value) {
        NodePrefs nodePrefsSuperAdmin = new NodePrefs();
        nodePrefsSuperAdmin.setId(id);
        nodePrefsSuperAdmin.setPref(value);
        nodePrefsSuperAdmins.add(nodePrefsSuperAdmin);
    }    

    public void clearSelect(){
        selectedPrefAdmin = null;
        selectedPrefSuperAdmin = null;
    }
    
    /**
     * Classe pour initialiser les variables de configuration
     */
    public class NodePrefs {
        private String pref;
        private String id;

        public String getPref() {
            return pref;
        }

        public void setPref(String pref) {
            this.pref = pref;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        
        
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public ArrayList<NodePrefs> getNodePrefsAdmins() {
        return nodePrefsAdmins;
    }

    public void setNodePrefsAdmins(ArrayList<NodePrefs> nodePrefsAdmins) {
        this.nodePrefsAdmins = nodePrefsAdmins;
    }

    public ArrayList<NodePrefs> getNodePrefsSuperAdmins() {
        return nodePrefsSuperAdmins;
    }

    public void setNodePrefsSuperAdmins(ArrayList<NodePrefs> nodePrefsSuperAdmins) {
        this.nodePrefsSuperAdmins = nodePrefsSuperAdmins;
    }

    public NodePrefs getSelectedPrefAdmin() {
        return selectedPrefAdmin;
    }

    public void setSelectedPrefAdmin(NodePrefs selectedPrefAdmin) {
        this.selectedPrefAdmin = selectedPrefAdmin;
    }

    public NodePrefs getSelectedPrefSuperAdmin() {
        return selectedPrefSuperAdmin;
    }

    public void setSelectedPrefSuperAdmin(NodePrefs selectedPrefSuperAdmin) {
        this.selectedPrefSuperAdmin = selectedPrefSuperAdmin;
    }
    
   
    
    
}
