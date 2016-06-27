package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.OrphanHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeRT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "treeBean", eager = true)
@SessionScoped

public class TreeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private TreeNode root;
    private TreeNode selectedNode;
    private ArrayList<TreeNode> selectedNodes;
    private ArrayList<String> orphans;
    private boolean createValid = false;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    @ManagedProperty(value = "#{ssTree}")
    private UnderTree ssTree;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;
    
    @ManagedProperty(value = "#{conceptbean}")
    private ConceptBean conceptbean;

    
    @PostConstruct
    public void initTerme() {
        int i = 0;
    }
    
    /**
     * ************************** INITIALISATION ***************************
     */
    /**
     * Constructeur
     */
    public TreeBean() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
        selectedNodes = new ArrayList<>();
        //  initTree("TH_44", "fr");
    }

    /**
     * RÃ©cupÃ¨re les domaines pour remplir la racine de l'arbre selon la langue
     * et le thÃ©saurus sÃ©lectionnÃ©s
     *
     * @param idTheso
     * @param langue
     */
    public void initTree(String idTheso, String langue) {

        if (connect.getPoolConnexion() == null) {
            System.err.println("!!!!! Opentheso n'a pas pu se connecter à la base de données !!!!!!! ");
            return;
        }
        List<NodeGroup> racineNode = new GroupHelper().getListConceptGroup(connect.getPoolConnexion(), idTheso, langue);
        Collections.sort(racineNode);
        for (NodeGroup n : racineNode) {
            if (n.getLexicalValue().trim().isEmpty()) {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, n.getConceptGroup().getIdgroup(), n.getConceptGroup().getIdthesaurus(),
                        n.getIdLang(), n.getConceptGroup().getIdgroup(), null,
                        "domaine", n.getConceptGroup().getIdgroup(), root);
                new DefaultTreeNode("fake", dynamicTreeNode);
            } else {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, n.getConceptGroup().getIdgroup(), n.getConceptGroup().getIdthesaurus(),
                        n.getIdLang(), n.getConceptGroup().getIdgroup(), null,
                        "domaine", n.getLexicalValue(), root);
                new DefaultTreeNode("fake", dynamicTreeNode);
            }

        }
        if (idTheso != null) {
            loadOrphan(idTheso, langue);
        }
    }

    public String getBrowserName() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String userAgent = externalContext.getRequestHeaderMap().get("User-Agent");

        if (userAgent.toLowerCase().contains("slurp")) {
            return "agent";
        }
        if (userAgent.toLowerCase().contains("msnbot")) {
            return "agent";
        }
        if (userAgent.toLowerCase().contains("googlebot")) {
            return "agent";
        }
        return "notagent";
    }

    /**
     * Vide l'arbre
     */
    public void reInit() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
    }

    public void loadOrphan(String idTheso, String langue) {
        String typeNode, value = "";
        TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, null, idTheso, langue, null, null, "orphan", langueBean.getMsg("index.orphans"), root);
        orphans = new OrphanHelper().getListOrphanId(connect.getPoolConnexion(), idTheso);
        for (String idC : orphans) {
            if (new ConceptHelper().haveChildren(connect.getPoolConnexion(), idTheso, idC)) {
                typeNode = "dossier";
            } else {
                typeNode = "fichier";
            }
            Term term = new TermHelper().getThisTerm(connect.getPoolConnexion(), idC, idTheso, langue);
            if (term != null) {
                value = term.getLexical_value() + " (id_" + idC + ")";
                Concept temp = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idC, idTheso);
                MyTreeNode mtn = new MyTreeNode(3, idC, idTheso, langue, "Orphan", null, typeNode, value, dynamicTreeNode);
                if (typeNode.equals("dossier")) {
                    new DefaultTreeNode("fake", mtn);
                }
            }

        }

    }

    /**
     * ************************** ACTIONS TREE ***************************
     */
    
    /**
     * Permet de mettre à jour l'arbre et le terme à la sélection d'un résultat de recherche
     */
    public void majSearch() {
        selectedTerme.majSearch();
        reInit();
        reExpand();
    }
    
    /**
     * Permet de mettre à jour l'arbre et le terme à la sélection d'un index rapide par autocomplétion
     */
    public void majIndexRapidSearch() {
        selectedTerme.majIndexRapidSearch();
        reInit();
        reExpand();
    }    
    

    /**
     * Permet de mettre à jour l'arbre et le terme à la sélection d'un résultat de recherche permutée
     */
    public void majSearchPermute() {
        selectedTerme.majSearchPermute();
        reInit();
        reExpand();
    }
    
    /**
     * Expension de l'abre sur un seul noeud (ouvert manuellement)
     *
     * @param event Le noeud ouvert
     */
    public void onNodeExpand(NodeExpandEvent event) {
        if (!event.getTreeNode().getType().equals("orphan")) {
            ArrayList<NodeConceptTree> liste;
            ConceptHelper ch = new ConceptHelper();
            int type = 3;

            //<Retirer noeuds fictifs>
            if (event.getTreeNode().getChildCount() == 1) {
                event.getTreeNode().getChildren().remove(0);
            }

            if (((MyTreeNode) event.getTreeNode()).getTypeMot() == 1) {
                liste = ch.getListTopConcepts(connect.getPoolConnexion(), ((MyTreeNode) event.getTreeNode()).getIdMot(),
                        ((MyTreeNode) event.getTreeNode()).getIdTheso(), ((MyTreeNode) event.getTreeNode()).getLangue());
                type = 2;
            } else {
                liste = ch.getListConcepts(connect.getPoolConnexion(), ((MyTreeNode) event.getTreeNode()).getIdMot(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                        ((MyTreeNode) event.getTreeNode()).getLangue());
            }

            TreeNode tn;

            // Ajout dans l'arbre
            for (NodeConceptTree nct : liste) {
                ConceptHelper help = new ConceptHelper();
                String value, idTC, icon;
                if (help.haveChildren(connect.getPoolConnexion(), nct.getIdThesaurus(), nct.getIdConcept())) {
                    icon = "dossier";
                    if (type == 2) { //CrÃ©ation de topConcepts
                        if (nct.getTitle().trim().isEmpty()) {
                            value = nct.getIdConcept();
                        } else {
                            value = nct.getTitle();
                        }
                        idTC = value;
                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) event.getTreeNode()).getIdTopConcept();
                        if (nct.getTitle().trim().isEmpty()) {
                            value = nct.getIdConcept();
                        } else {
                            value = nct.getTitle();
                        }
                    }
                    if(nct.getStatusConcept() != null){
                        if (nct.getStatusConcept().equals("hidden")) {
                            icon = "hidden";
                        }
                    }
                    tn = new MyTreeNode(type, nct.getIdConcept(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                            ((MyTreeNode) event.getTreeNode()).getLangue(), ((MyTreeNode) event.getTreeNode()).getIdDomaine(),
                            idTC, icon, value, event.getTreeNode());
                    new DefaultTreeNode("fake", tn);
                } else {
                    icon = "fichier";
                    if (type == 2) { //CrÃ©ation de topConcepts
                        idTC = nct.getIdConcept();
                        if (nct.getTitle().trim().isEmpty()) {
                            value = nct.getIdConcept();
                        } else {
                            value = nct.getTitle();
                        }

                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) event.getTreeNode()).getIdTopConcept();
                        if (nct.getTitle().trim().isEmpty()) {
                            value = nct.getIdConcept();
                        } else {
                            value = nct.getTitle();
                        }
                    }
                    if (nct.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    new MyTreeNode(type, nct.getIdConcept(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                            ((MyTreeNode) event.getTreeNode()).getLangue(), ((MyTreeNode) event.getTreeNode()).getIdDomaine(),
                            idTC, icon, value, event.getTreeNode());
                }
            }
        }
    }

    /**
     * Affiche les informations du terme correspondant au noeud selectionnÃ©
     * dans l'arbre
     *
     * @param event le noeud selectionnÃ©
     */
    public void onNodeSelect(NodeSelectEvent event) {
    //    if (!event.getTreeNode().getType().equals("orphan")) {
        // séparation des cliques entre les orphélins et les concepts
        if (!((MyTreeNode) event.getTreeNode()).getIdDomaine().equalsIgnoreCase("orphan")) {
            selectedTerme.majTerme((MyTreeNode) selectedNode);
        } else {
            selectedTerme.majTerme((MyTreeNode) selectedNode);
        }
        vue.setOnglet(0);
        selectedTerme.setTree(0);
    }

    /**
     * Expansion automatique de l'arbre sur un ou plusieurs chemin(s) entier(s)
     */
    public void reExpand() {
        if (selectedNode == null) {
      //      selectedNode = new MyTreeNode(0, "", "", "", "", "", "domaine", "", root);
        }
    //    selectedNode.setSelected(false);
        for (TreeNode tn : selectedNodes) {
            tn.setSelected(false);
        }
        selectedNodes = new ArrayList<>();
        ArrayList<String> first = new ArrayList<>();
        first.add(selectedTerme.getIdC());
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        paths = new ConceptHelper().getPathOfConcept(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), first, paths);
        reExpandTree(paths, selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
    }

    /**
     * Expansion automatique de la racine
     *
     * @param listeId
     * @param idTheso
     * @param langue
     */
    private void reExpandTree(ArrayList<ArrayList<String>> listeId, String idTheso, String langue) {
      //  if(selectedNodes.isEmpty()){
        if (root.getChildCount() == 0) {
            // On recrÃ©e la racine
            List<NodeGroup> racineNode = new GroupHelper().getListConceptGroup(connect.getPoolConnexion(), idTheso, langue);
            Collections.sort(racineNode);

            for (NodeGroup n : racineNode) {
                TreeNode dynamicTreeNode;
                if (n.getLexicalValue().trim().isEmpty()) {
                    dynamicTreeNode = (TreeNode) new MyTreeNode(1, n.getConceptGroup().getIdgroup(), n.getConceptGroup().getIdthesaurus(),
                            n.getIdLang(), n.getConceptGroup().getIdgroup(), null,
                            "domaine", n.getConceptGroup().getIdgroup(), root);
                } else {
                    dynamicTreeNode = (TreeNode) new MyTreeNode(1, n.getConceptGroup().getIdgroup(), n.getConceptGroup().getIdthesaurus(),
                            n.getIdLang(), n.getConceptGroup().getIdgroup(), null,
                            "domaine", n.getLexicalValue(), root);
                }

                new DefaultTreeNode("fake", dynamicTreeNode);
                for (ArrayList<String> tabId : listeId) {
                    // Si c'est le chemin, on Ã©tend
                    if (tabId.size() > 1 && tabId.get(0) != null) {
                        if (tabId.get(0).equals(n.getConceptGroup().getIdgroup())) {
                            reExpandChild(tabId, (MyTreeNode) dynamicTreeNode, 1);
                        }
                    } else {
                        if (tabId.get(1).equals(n.getConceptGroup().getIdgroup())) {
                            dynamicTreeNode.setSelected(true);
                            selectedNode = dynamicTreeNode;
                            selectedNodes.add(dynamicTreeNode);
                        } else {
                            dynamicTreeNode.setSelected(false);
                        }
                    }
                }
            }
            loadOrphan(idTheso, langue);
            for (TreeNode tn : root.getChildren()) {
                if (tn.getType().equals("orphan")) {
                    for (TreeNode tn2 : tn.getChildren()) {
                        for (ArrayList<String> tabId : listeId) {
                            if (tabId.size() == 2 && tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                tn2.setSelected(true);
                                selectedNode = tn2;
                                selectedNodes.add(tn2);

                            } else {
                                if (tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                    reExpandChild(tabId, (MyTreeNode) tn2, 2);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            List<TreeNode> racineNode = root.getChildren();
            for (TreeNode dynamicTreeNode : racineNode) {
                if (!dynamicTreeNode.getType().equals("orphan")) {
                    for (ArrayList<String> tabId : listeId) {
                        // Si c'est le chemin, on Ã©tend
                        if (tabId.size() > 1 && tabId.get(0) != null) {
                            if (tabId.get(0).equals(((MyTreeNode) dynamicTreeNode).getIdDomaine())) {
                                reExpandChild(tabId, (MyTreeNode) dynamicTreeNode, 1);
                            }
                        } else {
                            if (tabId.get(1).equals(((MyTreeNode) dynamicTreeNode).getIdDomaine())) {
                                dynamicTreeNode.setSelected(true);
                                selectedNode = dynamicTreeNode;
                                selectedNodes.add(dynamicTreeNode);
                            } else {
                                dynamicTreeNode.setSelected(false);
                            }
                        }
                    }
                } else {
                    for (TreeNode tn2 : dynamicTreeNode.getChildren()) {
                        for (ArrayList<String> tabId : listeId) {
                            if (tabId.size() == 2 && tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                tn2.setSelected(true);
                                selectedNode = tn2;
                                selectedNodes.add(tn2);

                            } else {
                                if (tabId.get(1).equals(((MyTreeNode) tn2).getIdMot())) {
                                    reExpandChild(tabId, (MyTreeNode) tn2, 2);
                                }
                            }
                        }
                    }
                }
            }
        }


    }


    
    public void scrollToVisible() {
        RequestContext.getCurrentInstance().update("accordeonTree:form:tree");//:divArbreTheso:form:scrollPanelTree");
        RequestContext.getCurrentInstance().scrollTo("accordeonTree:form:tree:5_27_6_1");
        RequestContext.getCurrentInstance().update("accordeonTree:form:tree");//"accordeonTree:form:tree");
   //     RequestContext.getCurrentInstance().update("form:tree");
    }
    
    
    /**
     * Expansion automatique des branches suivant la racine
     *
     * @param listeId
     * @param node
     * @param cpt
     */
    private void reExpandChild(ArrayList<String> listeId, TreeNode node, int cpt) {
        if (!node.isExpanded()) {
            ArrayList<NodeConceptTree> liste;
            ConceptHelper ch = new ConceptHelper();
            int type = 3;

            //<Retirer noeuds fictifs>
            if (node.getChildCount() == 1) {
                node.getChildren().remove(0);
            }

            if (((MyTreeNode) node).getTypeMot() == 1) {
                liste = ch.getListTopConcepts(connect.getPoolConnexion(), ((MyTreeNode) node).getIdMot(), ((MyTreeNode) node).getIdTheso(),
                        ((MyTreeNode) node).getLangue());
                type = 2;
            } else {
                liste = ch.getListConcepts(connect.getPoolConnexion(), ((MyTreeNode) node).getIdMot(), ((MyTreeNode) node).getIdTheso(),
                        ((MyTreeNode) node).getLangue());
            }

            TreeNode tn;

            // Ajout dans l'arbre
            for (NodeConceptTree nct : liste) {
                ConceptHelper help = new ConceptHelper();
                String topC;
                if (type == 2) {
                    topC = nct.getIdConcept();
                } else {
                    topC = ((MyTreeNode) node).getIdTopConcept();
                }
                String value, icon;
                if (help.haveChildren(connect.getPoolConnexion(), nct.getIdThesaurus(), nct.getIdConcept())) {
                    icon = "dossier";
                    if (nct.getTitle().trim().isEmpty()) {
                        value = nct.getIdConcept();
                    } else {
                        value = nct.getTitle();
                    }
                    if(nct.getStatusConcept() != null){
                        if (nct.getStatusConcept().equals("hidden")) {
                            icon = "hidden";
                        }
                    }
                    tn = new MyTreeNode(type, nct.getIdConcept(), ((MyTreeNode) node).getIdTheso(), ((MyTreeNode) node).getLangue(),
                            ((MyTreeNode) node).getIdDomaine(), topC, icon, value, node);
                    new DefaultTreeNode("fake", tn);
                    if (listeId.get(cpt).equals(((MyTreeNode) tn).getIdMot())) {
                        if (cpt + 1 < listeId.size()) {
                            tn.setSelected(false);
                            reExpandChild(listeId, tn, cpt + 1);
                        } else {
                            tn.setSelected(true);
                            selectedNode = tn;
                            selectedNodes.add(tn);
                        }
                    }
                } else {
                    icon = "fichier";
                    if (nct.getTitle().trim().isEmpty()) {
                        value = nct.getIdConcept();

                    } else {
                        value = nct.getTitle();
                    }
                    if (nct.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    tn = new MyTreeNode(type, nct.getIdConcept(), ((MyTreeNode) node).getIdTheso(), ((MyTreeNode) node).getLangue(),
                            ((MyTreeNode) node).getIdDomaine(), topC, icon, value,
                            node);
                    if (listeId.get(cpt).equals(((MyTreeNode) tn).getIdMot())) {
                        tn.setSelected(true);
                        selectedNode = tn;
                        selectedNodes.add(tn);
                    } else {
                        tn.setSelected(false);
                    }
                }
            }
            node.setExpanded(true);
        } else {
            ArrayList<TreeNode> children = (ArrayList<TreeNode>) node.getChildren();
            for (TreeNode mtn : children) {
                if (listeId.get(cpt).equals(((MyTreeNode) mtn).getIdMot())) {
                    if (cpt + 1 < listeId.size()) {
                        mtn.setSelected(false);
                        reExpandChild(listeId, mtn, cpt + 1);
                    } else {
                        mtn.setSelected(true);
                        selectedNode = mtn;
                        selectedNodes.add(mtn);
                    }
                }
            }
        }
    }

    /**
     * ************************** ACTIONS SELECTEDTERME ***************************
     */
    
    /**
     * Supprime le groupe sélectionné
     */
    public void delGroup() {
        new GroupHelper().deleteConceptGroup(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), selectedTerme.getUser().getUser().getId());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info7")));
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.reInitTerme();
    }

    /**
     * Cette fonction permet de supprimer le ou les orphelins (une branche à partir du concept orphelin sélectionné
     * @return 
     */
    public boolean delOrphans() {
        if(!deleteOrphanBranch(connect.getPoolConnexion(),
                selectedTerme.getIdC(), selectedTerme.getIdTheso(), selectedTerme.getUser().getUser().getId())){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info8")));
        conceptbean.setDeleteBranchOrphan(0);
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.reInitTerme();
        return true;
    }
    
        /**
     * Fonction recursive qui permet de supprimer une branche d'orphelins un concept de tête 
     * et thesaurus. La suppression est descendante qui ne supprime pas les autres branches remontantes.
     * @param conn
     * @param idConcept
     * @param idTheso
     * @return 
     */
    private boolean deleteOrphanBranch(HikariDataSource ds, String idConcept, String idTheso, int idUser) {

        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList <String> listIdsOfConceptChildren = 
                conceptHelper.getListChildrenOfConcept(ds,
                        idConcept, idTheso);

        if(!conceptHelper.deleteConceptForced(ds, idConcept, idTheso, idUser))
            return false;

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
       //     if(!conceptHelper.deleteConceptForced(ds, listIdsOfConceptChildren1, idTheso, idUser))
        //        return false;
            deleteOrphanBranch(ds, listIdsOfConceptChildren1, idTheso, idUser);
        }
        return true;
    }    
    
    
    /**
     * Mise à jour du terme et de l'abre lors de la selection d'un terme lié
     * au terme courant
     *
     * @param id identifiant du terme courant (idGroup si c'est un domaine,
     * idConcept sinon)
     * @param type 1 pour domaine, 2 pour topConcept, 3 pour concept
     */
    public void changeTerme(String id, int type) {
        String idTC;
        if (type == 2) { //On vient d'un domaine
            idTC = id;
        } else {
            idTC = selectedTerme.getIdTopConcept();
        }
        if (type == 0) {
            boolean temp = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), id, selectedTerme.getIdTheso()).isTopConcept();
            if (temp) {
                type = 2;
            } else {
                type = 3;
            }
        }

        MyTreeNode mTN = new MyTreeNode(type, id, selectedTerme.getIdTheso(), selectedTerme.getIdlangue(), selectedTerme.getIdDomaine(), idTC, null, null, null);
        selectedTerme.majTerme(mTN);
        reExpand();
        vue.setOnglet(0);
    }

    public boolean isCreateValid() {
        return createValid;
    }
    
    
    /**
     * cette fonction permet d'ajouter un nouveau terme spécifique (si le terme existe, on propose de créer une relation) 
     */
    public void newTSpe() {
        createValid = false;
        selectedTerme.setValueEdit(selectedTerme.getSelectedTermComp().getTermLexicalValue());
        if (selectedTerme.getValueEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            return;
        }
        
        String valueEdit = selectedTerme.getValueEdit().trim();
        
        // vérification si c'est le même nom, on fait rien
        if (valueEdit.equalsIgnoreCase(selectedTerme.getNom())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return;
        }
        String idTerm;
        String idConceptLocal;
        // vérification si le term à ajouter existe déjà 
        if( (idTerm = selectedTerme.isTermExist(valueEdit)) != null) {
            idConceptLocal = selectedTerme.getIdConceptOf(idTerm);
            // on vérifie si c'est autorisé de créer une relation ici
            selectedTerme.isCreateAuthorizedForTS(idConceptLocal);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error6")));
                return;
        }
        
        
        if (!selectedTerme.creerTermeSpe()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return;
        } else {
            reInit();
            reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", valueEdit + " " + langueBean.getMsg("tree.info1")));
        }
        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
        createValid = true;
    }
    
    /**
     * Change le nom du terme courant avec mise à jour dans l'abre
     */
    public void editNomT() {
        if(selectedTerme == null) return;
        if(selectedTerme.getSelectedTermComp() == null) return;
        // si c'est la même valeur, on fait rien
        String valueEdit = selectedTerme.getSelectedTermComp().getTermLexicalValue().trim();
        if(selectedTerme.getNom().trim().equals(valueEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error2")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
            return;
        }
        
        String idTerm;
        String idConceptLocal;
        // vérification si le term à ajouter existe déjà 
        if( (idTerm = selectedTerme.isTermExist(valueEdit)) != null) {
            idConceptLocal = selectedTerme.getIdConceptOf(idTerm);
            // on vérifie si c'est autorisé de créer une relation ici
            selectedTerme.isCreateAuthorizedForTS(idConceptLocal);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error6")));
                return;
        }        
        
        selectedTerme.setNomEdit(selectedTerme.getSelectedTermComp().getTermLexicalValue());
        if (selectedTerme.getNomEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
        } else {
            String temp = selectedTerme.getNomEdit();
            // cas d'un domaine
            if (selectedTerme.getType() == 1) {
                if (selectedTerme.getNom() == null || selectedTerme.getNom().equals("")) {
                    selectedTerme.editTerme(3);
                } else {
                    selectedTerme.editTerme(4);
                }
            } else {
                if (selectedTerme.getIdT() != null && !selectedTerme.getIdT().equals("")) {
                    selectedTerme.editTerme(1);
                } else {
                    // le terme n'existe pas encore
                    if(!selectedTerme.editTerme(2)) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                        selectedTerme.setNomEdit(selectedTerme.getNom());
                        return;
                    }
                }

            }
            if (selectedNode != null) {
                //((MyTreeNode) selectedNode).setData(temp + " (Id_" + selectedTerme.getIdC() + ")");
                ((MyTreeNode) selectedNode).setData(temp);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", temp + " " + langueBean.getMsg("tree.info2")));
                selectedTerme.setNomEdit(selectedTerme.getNom());
            }
        }
        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
    }

    /**
     * Supprime la relation hiÃ©rarchique qui lie le terme courant au terme dont
     * l'id est passÃ© en paramÃ¨tre puis met l'arbre Ã  jour. Si type vaut 0,
     * le terme courant est le fils, si type vaut 1, le terme courant est le
     * pÃ¨re.
     *
     * @param id
     * @param type
     */
    public void suppRel(String id, int type) {
        if (type == 0) {
            // type 0 = suppression de la relation gÃ©nÃ©rique 
            if (!selectedTerme.delGene(id)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return;
            }

        } else {
            // type 1 = suppression de la relation spÃ©cifique
            if (!selectedTerme.delSpe(id)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return;
            }
        }

        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info3")));
    }

    public boolean desactivateConcept() {
        if (!selectedTerme.deprecateConcept()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error3")));
            return false;
        }
        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info4")));
        vue.setAddTInfo(0);
        return true;
        
    }
    
    public boolean getConceptForFusion() {
        if (selectedTerme.getSelectedTermComp() == null || !selectedTerme.loadConceptFusion()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error5")));
            vue.setAddTInfo(0);
            return false;
        }
        vue.setAddTInfo(3);
        return true;
    }
    
    public void initConceptFusion (){
        selectedTerme.initConceptFusion();
    }
  
    
    /**
     * Fusionne les concepts avec mise à  jour dans l'abre
     */
    public void fusionConcept() {
        if (selectedTerme.getConceptFusionId().equals(selectedTerme.getIdC())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), langueBean.getMsg("error")));
            selectedTerme.setConceptFusionId(null);
            selectedTerme.setConceptFusionAlign(null);
            selectedTerme.setConceptFusionNodeRT(null);
        } else {
            int idUser = selectedTerme.getUser().getUser().getId();
            for (NodeRT rt : selectedTerme.getConceptFusionNodeRT()) {
                HierarchicalRelationship hr = new HierarchicalRelationship(rt.getIdConcept(), selectedTerme.getConceptFusionId(), selectedTerme.getIdTheso(), "RT");
                new ConceptHelper().addAssociativeRelation(connect.getPoolConnexion(), hr, idUser);
            }
            for (NodeAlignment na : selectedTerme.getConceptFusionAlign()) {
                new AlignmentHelper().addNewAlignment(connect.getPoolConnexion(), idUser, na.getConcept_target(), na.getThesaurus_target(), na.getUri_target(), na.getAlignement_id_type(), selectedTerme.getConceptFusionId(), selectedTerme.getIdTheso());
            }
            new ConceptHelper().addConceptFusion(connect.getPoolConnexion(), selectedTerme.getConceptFusionId(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), idUser);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info6")));
            reInit();
            reExpand();

        }
        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
        vue.setAddTInfo(0);
    }    
        
 /*       if (selectedTerme.getChoixdesactive().equals("0")) {
            if (!selectedTerme.delConcept()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error3")));
                return false;
            }
            reInit();
            reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info4")));
            vue.setAddTInfo(0);
            return true;
        } else if (selectedTerme.getChoixdesactive().equals("1")) {
            if (selectedTerme.getSelectedTermComp() == null || !selectedTerme.loadConceptFusion()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error5")));
                vue.setAddTInfo(0);
                return false;
            }
            vue.setAddTInfo(3);
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error6")));
            vue.setAddTInfo(0);
            return false;
        }*/        
        
    

    public boolean reactivConcept() {
        if (!selectedTerme.reactivConcept()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error4")));
            return false;
        }
        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info5")));
        return true;
    }

    
    
    
    
    
    
    
    
    
    
    
    
/***
 * Nouvelles fonctions par Miled Rousset
 */    
    
    
    /**
     * Permet de modifier la valeur de la notation d'un concept
     */
    public void editNotation() {
        if(selectedTerme == null) return;
        if (selectedTerme.getIdT() != null && !selectedTerme.getIdT().equals("")) {
           
            if(!selectedTerme.updateNotation()) {
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info2")));
        }
    }
    
    
/**
 *  Fin des nouvelles fonctions 
 */    
    
    
    
    
    /**
     * ************************** GETTERS SETTERS ***************************
     */
    /**
     *
     * @return
     */
    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public UnderTree getSsTree() {
        return ssTree;
    }

    public void setSsTree(UnderTree ssTree) {
        this.ssTree = ssTree;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public ConceptBean getConceptbean() {
        return conceptbean;
    }

    public void setConceptbean(ConceptBean conceptbean) {
        this.conceptbean = conceptbean;
    }


    
    
}
