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
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.FacetHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.OrphanHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.core.exports.old.WriteFileSKOS;
import org.primefaces.event.SelectEvent;

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
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), query);
        }
        return liste;
    }

    /**
     * Fonction qui permet de retrouver les concepts dans un même Group en
     * partant d'un concept
     *
     * @param query
     * @return
     */
    public List<NodeAutoCompletion> getListTerm(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(),
                    terme.getIdC(), // les termes qu'il faut éviter dans la recherche (le terme lui même et le BT)
                    theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), terme.getIdDomaine(), query);
        }
        return liste;
    }

    /**
     * Fonction qui permet de retrouver les concepts dans un même Group en
     * partant d'un TopTerm
     *
     * @param query
     * @return
     */
    public List<NodeAutoCompletion> getListTermFromThisGroup(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(),
                    terme.getIdC(), // le terme séléctionné qu'il faut éviter dans la recherche
                    theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), terme.getIdDomaine(), query);
        }
        return liste;
    }

    /**
     * Fonction qui permet de retrouver les concepts dans un autre Group en
     * partant d'un TopTerm
     *
     * @param query
     * @return
     */
    public List<NodeAutoCompletion> getListTermOfOtherGroup(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTermOfOtherGroup(connect.getPoolConnexion(),
                    terme.getIdC(), // le terme séléctionné qu'il faut éviter dans la recherche
                    theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), terme.getIdDomaine(), query);
        }
        return liste;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

    public List<NodeAutoCompletion> completSearchTerm(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), query);
        }
        return liste;
    }

    public List<NodeAutoCompletion> completGroup(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new GroupHelper().getAutoCompletionGroup(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), query);
        }
        return liste;
    }

    public List<NodeAutoCompletion> completOtherGroup(String query) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new GroupHelper().getAutoCompletionOtherGroup(connect.getPoolConnexion(),
                    theso.getThesaurus().getId_thesaurus(),
                    terme.getIdDomaine(),
                    theso.getThesaurus().getLanguage(), query);
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
     * Cette fonction permet d'ajouter une relation TG à un concept Le TG existe
     * déjà dans le thésaurus, donc c'est une relation à créer
     *
     * @return
     */
    public boolean addTGene() {

        // selectedAtt.getIdConcept() est le terme TG à ajouter
        // terme.getIdC() est le terme séléctionné dans l'arbre
        // terme.getIdTheso() est l'id du thésaurus
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }
        if (selectedAtt.getIdConcept().equals(terme.getIdC())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return false;
        }

        
        // addTermeGene(idNT, idBT)
        if (!terme.addTermeGene(terme.getIdC(), selectedAtt.getIdConcept())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            return false;
        }

        tree.reInit();
        tree.reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", selectedAtt.getTermLexicalValue() + " " + langueBean.getMsg("autoComp.info1")));
        selectedAtt = new NodeAutoCompletion();
        return true;
    }
    
    /**
     * Cette fonction permet d'ajouter une relation TG d'unn autre Group
     * Le TG existe déjà dans le thésaurus, donc c'est une relation à créer
     * en ajoutant en plus le nouveau domaine à la branche
     *
     * // Auteur Miled Rousset
     * 
     * @return
     */
    public boolean addTGeneOtherGroup() {

        // selectedAtt.getIdConcept() est le terme TG à ajouter
        // terme.getIdC() est le terme séléctionné dans l'arbre
        // terme.getIdTheso() est l'id du thésaurus
        
        ConceptHelper conceptHelper = new ConceptHelper();
        GroupHelper groupHelper = new GroupHelper();
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }
        if (selectedAtt.getIdConcept().equals(terme.getIdC())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return false;
        }

        if (!terme.addTermeGeneOtherGroup(terme.getIdC(), terme.getIdDomaine(), selectedAtt.getIdConcept())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            return false;
        }
        
        tree.reInit();
        tree.reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", selectedAtt.getTermLexicalValue() + " " + langueBean.getMsg("autoComp.info1")));
        selectedAtt = new NodeAutoCompletion();
        return true;
    }    

    /**
     * Ajoute un terme spécifique
     */
    public void newTSpe() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return;
        }

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
     * *****
     *
     * Auteur : Miled Rousset Nouvelles fonctions pour le déplacement de
     * branches Elles sont stables avec RollBack
     *
     *****
     */
    /**
     * Permet de déplacer une branche à l'intérieur d'un même domaine et d'un
     * concept à un autre
     *
     * @return
     */
    public boolean moveBranchToConcept() {

        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }
        ConceptHelper conceptHelper = new ConceptHelper();
        HikariDataSource ds = connect.getPoolConnexion();

        // permet de déplacer une branche simplement, en cas d'erreur, rien n'est écrit 
        if (!conceptHelper.moveBranch(ds,
                terme.getIdC(), idOld, selectedAtt.getIdConcept(), terme.getIdTheso(), terme.getUser().getUser().getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

        tree.reInit();
        tree.reExpand();
//        terme.getVue().setMoveBranch(0);
        return true;
    }

    /**
     * Déplace la branche du thésaurus, d'un domaine à un concept
     *
     * @return
     */
    public boolean moveBrancheFromDomainToConcept() {
        // idOld = MT actuel, c'est le domaine 
        // selectedAtt.getIdConcept = l'id du concept de destination
        // terme.getIdC = le concept sélectionné
        // List selectedNode (c'est le noeud complet sélectionné)

        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }

        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            ConceptHelper conceptHelper = new ConceptHelper();

            // on déplace la branche au nouveau concept puis création de TG-TS (on ajoute la relation BT du concept, puis on supprime  
            // au concept la relation TT
            if (!conceptHelper.moveBranchFromMT(conn, terme.getIdC(),
                    selectedAtt.getIdConcept(),
                    terme.getIdDomaine(),
                    terme.getIdTheso(),
                    terme.getUser().getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

            tree.reInit();
            tree.reExpand();
            selectedAtt = new NodeAutoCompletion();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Déplace la branche vers la racine du Group (même
     * Groupe/Domaine/Collection), la tete de la branche devient un TT
     * (TopTerme)
     *
     * @return
     */
    public boolean moveBrancheToTopTerm() {
        // idOld = TG actuel
        // selectedAtt.getIdGroup() = l'id  domaine de destination
        // terme.getIdC = le concept sélectionné
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);

            /*    if (selectedAtt == null || selectedAtt.getIdGroup().equals("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
                return false;
            }*/
            ConceptHelper conceptHelper = new ConceptHelper();

            // on déplace la branche au domaine (on coupe les relations BT du concept, puis on afecte 
            // au concept la relation TT
            if (!conceptHelper.moveBranchToMT(conn, terme.getIdC(),
                    idOld, terme.getIdDomaine(), terme.getIdTheso(),
                    terme.getUser().getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

            tree.reInit();
            tree.reExpand();
            selectedAtt = new NodeAutoCompletion();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    ///////// 
    //// Other Group
    /////////
    /**
     * Déplace la branche vers la racine d'un autre Group
     * (Groupe/Domaine/Collection), la tete de la branche devient un TT
     * (TopTerme)
     *
     * @return
     */
    public boolean moveBrancheToGroupOfOtherGroup() {
        // idOld = TG actuel
        // selectedAtt.getIdGroup() = l'id  domaine de destination
        // terme.getIdC = le concept sélectionné
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);

            /*    if (selectedAtt == null || selectedAtt.getIdGroup().equals("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
                return false;
            }*/
            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();

            // on déplace la branche au domaine (on coupe les relations BT du concept, puis on afecte 
            // au concept la relation TT
            if (!conceptHelper.moveBranchToAnotherMT(conn, terme.getIdC(),
                    idOld, terme.getIdDomaine(), // ancien Group
                    selectedAtt.getIdGroup(), // nouveau Group
                    terme.getIdTheso(),
                    terme.getUser().getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = new  ArrayList<>();
            lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), selectedAtt.getIdGroup(), terme.getIdTheso(), lisIds);
            
            
            // on supprime l'ancien Groupe de la branche 
            if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, terme.getIdDomaine(), terme.getIdTheso())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            // on ajoute le nouveau domaine à la branche
            if (!groupHelper.setDomainToBranch(conn, lisIds, selectedAtt.getIdGroup(), terme.getIdTheso())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

            tree.reInit();
            tree.reExpand();
            selectedAtt = new NodeAutoCompletion();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Déplace la branche du thésaurus, d'un domaine à un concept d'un autre
     * Group
     *
     * @return
     */
    public boolean moveBrancheFromDomainToConceptOtherGroup() {
        // idOld = MT actuel, c'est le domaine 
        // selectedAtt.getIdConcept = l'id du concept de destination
        // terme.getIdC = le concept sélectionné
        // List selectedNode (c'est le noeud complet sélectionné)

        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }

        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();

            // on déplace la branche au nouveau concept puis création de TG-TS (on ajoute la relation BT du concept, puis on supprime  
            // au concept la relation TT
            if (!conceptHelper.moveBranchFromMT(conn, terme.getIdC(),
                    selectedAtt.getIdConcept(),
                    terme.getIdDomaine(),
                    terme.getIdTheso(),
                    terme.getUser().getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }
            
            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = new  ArrayList<>();
            lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), selectedAtt.getIdGroup(), terme.getIdTheso(), lisIds);            

            // on supprime l'ancien Groupe de la branche 
            ArrayList<String> domsOld = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());
            for (String domsOld1 : domsOld) {
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, domsOld1, terme.getIdTheso())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            // on ajoute le nouveau domaine à la branche
            ArrayList<String> domsNew = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), selectedAtt.getIdConcept(), terme.getIdTheso());
            for (String domsNew1 : domsNew) {

                if (!groupHelper.setDomainToBranch(conn, lisIds, domsNew1, terme.getIdTheso())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));

            tree.reInit();
            tree.reExpand();
            selectedAtt = new NodeAutoCompletion();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Permet de déplacer une branche vers un autre domaine mais vers un concept
     * uniquement
     *
     * @return
     */
    public boolean moveBranchToConceptOtherGroup() {
        // idOld = MT actuel, c'est le domaine 
        // selectedAtt.getIdConcept = l'id du concept de destination
        // terme.getIdC = le concept sélectionné
        // List selectedNode (c'est le noeud complet sélectionné)
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }
        ConceptHelper conceptHelper = new ConceptHelper();
        GroupHelper groupHelper = new GroupHelper();
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);

            // permet de déplacer une branche simplement, en cas d'erreur, rien n'est écrit 
            if (!conceptHelper.moveBranchToConceptOtherGroup(conn,
                    terme.getIdC(),
                    idOld,
                    selectedAtt.getIdConcept(),
                    terme.getIdTheso(),
                    terme.getUser().getUser().getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return false;
            }
            
            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = new  ArrayList<>();
            lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), selectedAtt.getIdGroup(), terme.getIdTheso(), lisIds);  
            
            
            // on supprime l'ancien Groupe de la branche 
            ArrayList<String> domsOld = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());
            for (String domsOld1 : domsOld) {
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, domsOld1, terme.getIdTheso())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            // on ajoute le nouveau domaine à la branche
            ArrayList<String> domsNew = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), selectedAtt.getIdConcept(), terme.getIdTheso());
            for (String domsNew1 : domsNew) {

                if (!groupHelper.setDomainToBranch(conn,lisIds, domsNew1, terme.getIdTheso())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("autoComp.info2")));
            tree.reInit();
            tree.reExpand();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }



    /**
     * *****
     *
     * Fin des fonctions pour le déplacement de branches Auteur : Miled Rousset
     *
     *****
     */
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    


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
