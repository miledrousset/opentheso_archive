package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;
import mom.trd.opentheso.bdd.helper.nodes.NodeTypeRelation;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "narrowerTerm", eager = true)
@ViewScoped

public class NarrowerTerm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme terme;
    
    @ManagedProperty(value = "#{newtreeBean}")
    private NewTreeBean tree;    
   
    private NodeNT selectedNodeNT;
    private NodeTypeRelation selectedRelation;    
    private String relationType;
    
    private ArrayList<NodeTypeRelation> typesRelationsNT;
    
    private ArrayList<NodeNT> listNT = new ArrayList<>();

    private boolean createValid = false;
    
    // pour permettre de forcer l'insertion des concepts en doublon (pour des besoins spécifiques
    private boolean duplicate = false;
    

   
    
    @PostConstruct
    public void initConf() {
        RelationsHelper relationsHelper = new RelationsHelper();
        typesRelationsNT = relationsHelper.getTypesRelationsNT(connect.getPoolConnexion());
    }

    public void init() {
        duplicate = false;
        createValid = false;
    }      
    
    public void initNT(ArrayList<NodeNT> listNTs) {
        this.listNT = listNTs;
    }
    
    public void updateNewRelation(String idConcept1, String idTheso){
        RelationsHelper relationsHelper = new RelationsHelper();
        String inverseRelation = "BT";
        switch (relationType) {
            case "NT" :
                inverseRelation = "BT";
                break;
            case "NTG":
                inverseRelation = "BTG";
                break;
            case "NTP":
                inverseRelation = "BTP";
                break;
            case "NTI":
                inverseRelation = "BTI";
                break;
        }                
            
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if(!relationsHelper.updateRelationNT(conn, idConcept1,
                    selectedNodeNT.getIdConcept(), idTheso,
                    relationType, inverseRelation)) {
                conn.rollback();
                conn.close();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error-BDD")));           
                return;
            }
            conn.commit();
            conn.close();
        } catch (Exception e) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, e);
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", e.toString()));
            return;
        }
        
        for (NodeNT nodeNT : listNT) {
            if(nodeNT == selectedNodeNT)
                selectedNodeNT.setRole(relationType);
        }
    }

    /**
     * Permet de créer un nouveau concept en vérifiant sa validité
     * @param idTheso 
     */
    public void newTSpe(String idTheso) {
        createValid = false;
        MyTreeNode selectedNode = (MyTreeNode)tree.getSelectedNode();
        
        String value = terme.getSelectedTermComp().getPrefLabel();
        value = value.trim();
        terme.setValueEdit(value);
      //  terme.setValueEdit(terme.getSelectedTermComp().getTermLexicalValue());
        if (value.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            return;
        }

//        String valueEdit = terme.getValueEdit().trim();

        // vérification si c'est le même nom, on fait rien
        if (value.equalsIgnoreCase(terme.nom.trim())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return;
        }

        TermHelper termHelper = new TermHelper();
        
        // vérification si le term à ajouter existe déjà 
        if (termHelper.isTermEqualTo(connect.getPoolConnexion(), value, idTheso, selectedNode.getLangue()) != null) {
            duplicate = true;
            return;
        }

        if (!terme.creerTermeSpe(selectedNode, relationType, null, "")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return;
        } else {
            tree.reInit();
            tree.reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", value + " " + langueBean.getMsg("tree.info1")));
        }
        terme.setSelectedTermComp(new NodeAutoCompletion());
        createValid = true;
        PrimeFaces pf = PrimeFaces.current();
        if (pf.isAjaxRequest()) {
            pf.ajax().update("idAddNewNTDlg");
        }
//        RequestContext.getCurrentInstance().update("idNtEditDlg");
    }
    
    /**
     * permet de créer un concept en doublon, après avoir eu la validation de l'utilisateur
     */
    public void newTSDupplicated() {
        if (!terme.creerTermeSpe(((MyTreeNode) tree.getSelectedNode()), relationType, null, "")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return;
        } else {
            tree.reInit();
            tree.reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", terme.getSelectedTermComp().getPrefLabel() + " " + langueBean.getMsg("tree.info1")));
        }
        terme.setSelectedTermComp(new NodeAutoCompletion());
        createValid = true;
        duplicate = false;
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

    public NodeNT getSelectedNodeNT() {
        return selectedNodeNT;
    }

    public void setSelectedNodeNT(NodeNT selectedNodeNT) {
        this.selectedNodeNT = selectedNodeNT;
    }

    public ArrayList<NodeTypeRelation> getTypesRelationsNT() {
        return typesRelationsNT;
    }

    public void setTypesRelationsNT(ArrayList<NodeTypeRelation> typesRelationsNT) {
        this.typesRelationsNT = typesRelationsNT;
    }

    public NodeTypeRelation getSelectedRelation() {
        return selectedRelation;
    }

    public void setSelectedRelation(NodeTypeRelation selectedRelation) {
        this.selectedRelation = selectedRelation;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }



    public ArrayList<NodeNT> getListNT() {
        return listNT;
    }

    public void setListNT(ArrayList<NodeNT> listNT) {
        this.listNT = listNT;
    }

    public boolean isCreateValid() {
        return createValid;
    }

    public void setCreateValid(boolean createValid) {
        this.createValid = createValid;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public SelectedTerme getTerme() {
        return terme;
    }

    public void setTerme(SelectedTerme terme) {
        this.terme = terme;
    }


    public NewTreeBean getTree() {
        return tree;
    }

    public void setTree(NewTreeBean tree) {
        this.tree = tree;
    }




  
    
}
