package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.LanguageBean;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.FacetHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;

@ManagedBean(name = "autoComp", eager = true)
@SessionScoped

public class AutoCompletBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private NodeAutoCompletion selectedAtt;
    private String idOld;
    private String facetEdit;

    @ManagedProperty(value = "#{theso}")
    private SelectedThesaurus theso;

    @ManagedProperty(value = "#{treeBean}")
    private TreeBean tree;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme terme;

    @ManagedProperty(value = "#{selectedCandidat}")
    private SelectedCandidat candidat;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    public List<NodeAutoCompletion> completTerm(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste;
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), query);
        } else {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), "TH_35", "fr", query);
        }

        return liste;
    }

    public List<NodeAutoCompletion> completNvxCandidat(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), candidat.getIdTheso(), candidat.getLangueTheso(), query);
        return liste;
    }

    /**
     * Ajoute un terme associé
     */
    public void newTAsso() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else {
            Term laValeur = terme.getTerme(selectedAtt.getIdConcept());
            if (laValeur == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            } else {
                terme.creerTermeAsso(selectedAtt.getIdConcept());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", laValeur.getLexical_value() + " " + langueBean.getMsg("autoComp.info1")));
            }
            selectedAtt = new NodeAutoCompletion();
        }
    }

    /**
     * Ajoute un terme générique
     */
    public void newTGene() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else {
            Term laValeur = terme.getTerme(selectedAtt.getIdConcept());
            if (laValeur == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            } else {
                terme.creerTermeGene(selectedAtt.getIdConcept());
                tree.reInit();
                tree.reExpand();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", laValeur.getLexical_value() + " " + langueBean.getMsg("autoComp.info1")));
            }
            selectedAtt = new NodeAutoCompletion();
        }
    }

    /**
     * Ajoute un terme spécifique
     */
    public void newTSpe() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else {
            Term laValeur = terme.getTerme(selectedAtt.getIdConcept());
            if (laValeur == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            } else {
                terme.creerTermeSpe(selectedAtt.getIdConcept());
                tree.reInit();
                tree.reExpand();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", laValeur.getLexical_value() + " " + langueBean.getMsg("autoComp.info1")));
            }
            selectedAtt = new NodeAutoCompletion();
        }
    }

    /**
     * Ajoute une facette
     */
    public void newFacette() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else if (facetEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error3")));
        } else {
            theso.creerFacette(selectedAtt.getIdConcept(), facetEdit);
            facetEdit = "";
        }
    }

    /**
     * Déplace la branche d'un arbre de idOld à l'id du selected att
     *
     * @return
     */
    public boolean moveBranche() {
        ConceptHelper ch = new ConceptHelper();
        HikariDataSource ds = connect.getPoolConnexion();
        ArrayList<String> domsOld = ch.getListGroupIdOfConcept(ds, idOld, terme.getIdTheso());
        ArrayList<String> domsNew = ch.getListGroupIdOfConcept(ds, selectedAtt.getIdConcept(), terme.getIdTheso());
        // pas de changement de domaine.
        if(!domsOld.equals(domsNew)) {
            // fonction pour nettoyer les anciens domaines et remplacer par le nouveau
            // présente un bug à corriger
            recursiveMoveBranche(ds, terme.getIdC(), idOld, domsOld, domsNew);
        }

        if (!new ConceptHelper().moveBranch(ds,
                terme.getIdC(), idOld, selectedAtt.getIdConcept(), terme.getIdTheso(), terme.getUser().getUser().getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

        tree.reInit();
        tree.reExpand();
        terme.getVue().setAddTGen(0);
        return true;
    }

    private void recursiveMoveBranche(HikariDataSource ds, String idC, String idOld, ArrayList<String> domsOld, ArrayList<String> domsNew) {
        if (new ConceptHelper().haveChildren(ds, terme.getIdTheso(), idC)) {
            List<NodeNT> children = new RelationsHelper().getListNT(ds, idC, terme.getIdTheso(), terme.getIdlangue());
            for (NodeNT nnt : children) {
                recursiveMoveBranche(ds, nnt.getIdConcept(), idC, domsOld, domsNew);
            }
        }
        ConceptHelper ch = new ConceptHelper();
        ArrayList<NodeBT> tempBT = new RelationsHelper().getListBT(ds, idC, terme.getIdTheso(), terme.getIdlangue());
        ArrayList<String> idParents = new ArrayList<>();
        for (NodeBT bt : tempBT) {
            idParents.add(bt.getIdConcept());
        }
        ArrayList<String> domParent = ch.getListGroupIdParentOfConceptOtherThan(ds, idParents, terme.getIdTheso(), idOld);
        for (String domNew : domsNew) {
            if (!domParent.contains(domNew)) {
                Concept c = ch.getThisConcept(ds, idC, terme.getIdTheso());
                c.setIdGroup(domNew);
                try {
                    ch.addNewGroupOfConcept(ds.getConnection(), c, terme.getUser().getUser().getId());
                } catch (SQLException ex) {
                    Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (String domOld : domsOld) {
            if (!domParent.contains(domOld) && !domsNew.contains(domOld)) {
                ch.deleteGroupOfConcept(ds, idC, domOld, terme.getIdTheso(), terme.getUser().getUser().getId());
            }
        }
    }

    public NodeAutoCompletion getSelectedAtt() {
        return selectedAtt;
    }

    public void setSelectedAtt(NodeAutoCompletion selectedAtt) {
        this.selectedAtt = selectedAtt;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public SelectedTerme getTerme() {
        return terme;
    }

    public void setTerme(SelectedTerme terme) {
        this.terme = terme;
    }

    public TreeBean getTree() {
        return tree;
    }

    public void setTree(TreeBean tree) {
        this.tree = tree;
    }

    public SelectedCandidat getCandidat() {
        return candidat;
    }

    public void setCandidat(SelectedCandidat candidat) {
        this.candidat = candidat;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getIdOld() {
        return idOld;
    }

    public void setIdOld(String idOld) {
        this.idOld = idOld;
    }

    public String getFacetEdit() {
        return facetEdit;
    }

    public void setFacetEdit(String facetEdit) {
        this.facetEdit = facetEdit;
    }

    public SelectedThesaurus getTheso() {
        return theso;
    }

    public void setTheso(SelectedThesaurus theso) {
        this.theso = theso;
    }

}
