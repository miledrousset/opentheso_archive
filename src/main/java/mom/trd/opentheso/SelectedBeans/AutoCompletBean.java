package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.OrphanHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.SearchHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ValidateActionHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;

import org.primefaces.event.SelectEvent;

@ManagedBean(name = "autoComp", eager = true)
@SessionScoped

public class AutoCompletBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    String RTtag;

    private NodeAutoCompletion selectedAtt;
    private String idOld;
    private String facetEdit;

    @ManagedProperty(value = "#{theso}")
    private SelectedThesaurus theso;

    @ManagedProperty(value = "#{newtreeBean}")
    private NewTreeBean tree;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme terme;

    @ManagedProperty(value = "#{selectedCandidat}")
    private SelectedCandidat candidat;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    private boolean createValid = false;
    
    // pour permettre de forcer l'insertion des concepts en doublon (pour des besoins spécifiques
    private boolean duplicate = false;
    private boolean forced = false;
    private boolean editPassed = false;    
    

    /**
     * permet de retourner la liste des concepts possibles 
     * pour ajouter une relation NT
     * (en ignorant les relations interdites) 
     * on ignore les concepts de type TT
     * on ignore les concepts de type RT
     * @param value
     * @return 
     */
    public List<NodeAutoCompletion> getAutoCompletForRelationNT(String value) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletForRelationNT(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), value);
        }
        return liste;
    }
    
    /**
     * permet de retourner la liste des concepts possibles 
     * pour ajouter une relation NT
     * (en ignorant les relations interdites) 
     * on ignore les concepts de type TT
     * on ignore les concepts de type RT
     * @param value
     * @return 
     */
    public List<NodeAutoCompletion> getAutoCompletForRelationRT(String value) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), value);
        }
        return liste;
    }
    
    /**
     * permet de retourner la liste des concepts possibles 
     * pour ajouter une relation BT
     * (en ignorant les relations interdites) 
     * @param value
     * @return 
     */
    public List<NodeAutoCompletion> getAutoCompletForRelationBT(String value) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), value);
        }
        return liste;
    }
    
    /**
     * permet de retourner la liste des groupes / collections  
     * contenus dans le thésaurus 
     * @param value
     * @return 
     */
    public List<NodeAutoCompletion> getAutoCompletCollection(String value) {
        selectedAtt = new NodeAutoCompletion();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            liste = new GroupHelper().getAutoCompletionGroup(
                    connect.getPoolConnexion(),
                    theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(),
                    value);
        }
        return liste;
    }
        
    
    public void init() {
        duplicate = false;
        forced = false;
        editPassed = false;
    }
    
    public void initVariables(){
        selectedAtt = null;
    }
    
    public List<NodeAutoCompletion> completTermFullText(String value) {
        selectedAtt = new NodeAutoCompletion();
        SearchHelper searchHelper = new SearchHelper();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            /*
            liste = new SearchHelper().getAutoCompletionIndex(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), value);*/
                
            liste = searchHelper.searchFullText(connect.getPoolConnexion(),
                        value,
                        theso.getThesaurus().getLanguage(),
                        theso.getThesaurus().getId_thesaurus());         
        }
        return liste;
    }
    
    public List<NodeAutoCompletion> completTerm(String value) {
        selectedAtt = new NodeAutoCompletion();
        SearchHelper searchHelper = new SearchHelper();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            /*
            liste = new SearchHelper().getAutoCompletionIndex(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), value);*/
                
            liste = searchHelper.searchTermNewForAutocompletion(connect.getPoolConnexion(),
                        value,
                        theso.getThesaurus().getLanguage(),
                        theso.getThesaurus().getId_thesaurus(), "");         
        }
        return liste;
    }  
    
    /**
     * permet de retourner les infos en temps réel pour un concept 
     * pour afficher les groupes et les définitions
     * @param idConcept
     * @return 
     */
    public String getInfosConcepts(String idConcept) {
        String idLang = theso.getThesaurus().getLanguage();
        String idTheso = theso.getThesaurus().getId_thesaurus();
        String infos = "";
        GroupHelper groupHelper = new GroupHelper();
        NoteHelper noteHelper = new NoteHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> idGroups = conceptHelper.getListGroupIdOfConcept(
                connect.getPoolConnexion(), idConcept, idTheso);
        for (String idGroup : idGroups) {
            infos = infos + " / " + groupHelper.getLexicalValueOfGroup(
                    connect.getPoolConnexion(), idGroup, idTheso, idLang);         
        }
        TermHelper termHelper = new TermHelper();
        String idTerm = termHelper.getIdTermOfConcept(connect.getPoolConnexion(), idConcept, idTheso);
               
        ArrayList<NodeNote> nodeNotes = noteHelper.getListNotesTerm(connect.getPoolConnexion(), idTerm, idTheso, idLang);
        for (NodeNote nodeNote : nodeNotes) {
            infos = infos + " \n" + nodeNote.getLexicalvalue();
        }
        return infos;
    }
    
    public List<NodeAutoCompletion> completExactTerm(String value) {
        selectedAtt = new NodeAutoCompletion();
        SearchHelper searchHelper = new SearchHelper();
        List<NodeAutoCompletion> liste = new ArrayList<>();
        if (theso.getThesaurus().getId_thesaurus() != null && theso.getThesaurus().getLanguage() != null) {
            /*
            liste = new SearchHelper().getAutoCompletionIndex(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(),
                    theso.getThesaurus().getLanguage(), value);*/
                
            liste = searchHelper.searchExactTermNewForAutocompletion(connect.getPoolConnexion(),
                        value,
                        theso.getThesaurus().getLanguage(),
                        theso.getThesaurus().getId_thesaurus(), "");         
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
     * Deprecated #MR
     */
    /*
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
    }*/

    /**
     * Fonction qui permet de retrouver les concepts dans un autre Group en
     * partant d'un TopTerm
     *
     * @param query
     * @return
     * Deprecated #MR
     */
    /*
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
    }*/

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
            if(terme.getIdC().equalsIgnoreCase(selectedAtt.getIdConcept())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("relation.errorRTNT")));
                return;                 
            }
            
            ValidateActionHelper validateActionHelper = new ValidateActionHelper();
            
            if(!validateActionHelper.isAddRelationRTValid(
                    connect.getPoolConnexion(),
                    theso.getThesaurus().getId_thesaurus(),
                    terme.getIdC(),
                    selectedAtt.getIdConcept())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("relation.errorRTNT")));
                return;    
            }
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
     * Ajoute un terme associé special
     */
    public void newSpecialTAsso() {
        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
        } else {
            Term laValeur = terme.getTerme(selectedAtt.getIdConcept());
            if (laValeur == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            } else {
                terme.creerTermeAsso(selectedAtt.getIdConcept(),RTtag);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", laValeur.getLexical_value() + " " + langueBean.getMsg("autoComp.info1")));
            }
            selectedAtt = new NodeAutoCompletion();
            
        }
    }


    
    /**
     * permet d'ajouter le concept à une collection ou groupe
     * @return 
     */
    public boolean addConceptToGroup() {

        // selectedAtt.getIdConcept() est le terme TG à ajouter
        // terme.getIdC() est le terme séléctionné dans l'arbre
        // terme.getIdTheso() est l'id du thésaurus
        if (selectedAtt == null || selectedAtt.getIdGroup().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }
       
        // addConceptToGroup
        GroupHelper groupHelper = new GroupHelper();
        if (!groupHelper.addConceptGroupConcept(connect.getPoolConnexion(),
                selectedAtt.getIdGroup(), terme.getIdC(), terme.getIdTheso())){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }

        tree.reInit();
        tree.reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", selectedAtt.getPrefLabel() + " " + langueBean.getMsg("autoComp.info1")));
        selectedAtt = new NodeAutoCompletion();
        return true;
    }    
            
    
    /**
     * Permet d'ajouter une relation terme spécifique NT pour un concept
     * #MR
     */
    public void newRelationNT() {
        createValid = false;
        if ((terme.getSelectedTermComp() == null ) || 
                (terme.getSelectedTermComp().getIdConcept() == null) ||
                (terme.getSelectedTermComp().getIdConcept().isEmpty())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            return;
        }        
        
        ValidateActionHelper validateActionHelper = new ValidateActionHelper();
        
        // vérification si la relation est cohérente (NT et RT à la fois ?)  
        if(!validateActionHelper.isAddRelationNTValid(
                connect.getPoolConnexion(),
                theso.getThesaurus().getId_thesaurus(),
                terme.getIdC(),
                terme.getSelectedTermComp().getIdConcept())){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("relation.errorNTRT")));
            return;        
        } 
        
        RelationsHelper relationsHelper = new RelationsHelper();
        
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            
            if (!relationsHelper.addRelationNT(conn, 
                    terme.getIdC(),
                    terme.getIdTheso(),
                    terme.getSelectedTermComp().getIdConcept(),
                    terme.getUser().getUser().getIdUser())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return;
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        tree.reInit();
        tree.reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :",
                terme.getSelectedTermComp().getIdConcept() + " " + langueBean.getMsg("tree.info1")));
        terme.setSelectedTermComp(new NodeAutoCompletion());
        createValid = true;
    }
    
  
    
  
    

    public void newSpecialTSpe() {
        createValid = false;
        terme.setValueEdit(terme.getSelectedTermComp().getPrefLabel());
        if (terme.getValueEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            return;
        }

        String valueEdit = terme.getValueEdit().trim();

        // vérification si c'est le même nom, on fait rien
        if (valueEdit.equalsIgnoreCase(terme.getNom())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return;
        }

        // vérification si le term à ajouter existe déjà 
        if ((terme.isTermExist(valueEdit)) != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error6")));
            return;
        }

        String BTtag = null;

        switch (tree.getNTtag()) {
            case "NTG":
                BTtag = "BTG";
                break;
            case "NTP":
                BTtag = "BTP";
                break;
            case "NTI":
                BTtag = "BTI";
                break;
        }

        if (!terme.creerSpecialTermeSpe(((MyTreeNode) tree.getSelectedNode()), BTtag, tree.getNTtag())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return;
        } else {
            tree.reInit();
            tree.reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", valueEdit + " " + langueBean.getMsg("tree.info1")));
        }
        terme.setSelectedTermComp(new NodeAutoCompletion());
        createValid = true;
    }    
    
    /**
     * déprécié par #MR
     * Ajoute un terme spécifique
     */
/*    public void newTSpe() {
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

    }*/

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
                terme.getIdC(), idOld, selectedAtt.getIdConcept(), terme.getIdTheso(), terme.getUser().getUser().getIdUser())) {
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
                    terme.getUser().getUser().getIdUser())) {
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
                    terme.getUser().getUser().getIdUser())) {
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
                    terme.getUser().getUser().getIdUser())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());
            
            
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
                    terme.getUser().getUser().getIdUser())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }
            
            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());            

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
                    terme.getUser().getUser().getIdUser())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return false;
            }
            
            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());  
            
            
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

    
    //////////////////////////////////////
    //////////////////////////////////////
    /// partie pour les orphelins ////////
    //////////////////////////////////////
    //////////////////////////////////////
    
    
    /**
     * Permet de déplacer une branche des orphelins vers un vers un concept
     * uniquement
     *
     * @return
     */
    public boolean moveBranchFromOrphinToConcept() {
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
                    terme.getUser().getUser().getIdUser())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return false;
            }
            
            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());  
            
            
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
            
            OrphanHelper orphanHelper = new OrphanHelper();
            if(!orphanHelper.deleteOrphanBranch2(conn, terme.getIdC(), terme.getIdTheso(), terme.getUser().getUser().getIdUser())) {
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
    public boolean moveBrancheFromOrphinToGroup() {
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
                    terme.getUser().getUser().getIdUser())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                conn.rollback();
                conn.close();
                return false;
            }

            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), terme.getIdC(), terme.getIdTheso());
            
            
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
            
            OrphanHelper orphanHelper = new OrphanHelper();
            if(!orphanHelper.deleteOrphanBranch2(conn, terme.getIdC(), terme.getIdTheso(), terme.getUser().getUser().getIdUser())) {
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
    ///// * Fin des fonctions pour le déplacement de branches Auteur : Miled Rousset







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

    public NewTreeBean getTree() {
        return tree;
    }

    public void setTree(NewTreeBean tree) {
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

    public String getRTtag() {
        return RTtag;
    }

    public void setRTtag(String RTtag) {
        this.RTtag = RTtag;
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

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public boolean isEditPassed() {
        return editPassed;
    }

    public void setEditPassed(boolean editPassed) {
        this.editPassed = editPassed;
    }

}
