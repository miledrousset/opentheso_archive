/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Quincy
 */
@ManagedBean(name = "newtreeBean", eager = true)
@SessionScoped

public class NewTreeBean implements Serializable {

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    private TreeNode root;
    private TreeNode selectedNode;
    private ArrayList<TreeNode> selectedNodes;
    private String idThesoSelected;
    private String defaultLanguage;

    public NewTreeBean() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
        selectedNodes = new ArrayList<>();
    }

    private boolean createValid = false;

    /**
     *
     * @param idTheso
     * @param langue
     */
    public void initTree(String idTheso, String langue) {

        //      idThesoSelected = idTheso;
        //      defaultLanguage = langue;
        root = (TreeNode) new DefaultTreeNode("Root", null);

        if (connect.getPoolConnexion() == null) {
            System.err.println("Opentheso n'a pas pu se connecter à la base de données");
            return;
        }
        List<NodeGroup> racineNode = new GroupHelper().getListRootConceptGroup(connect.getPoolConnexion(), idTheso, langue);
        Collections.sort(racineNode);
        for (NodeGroup nodegroup : racineNode) {
            if (nodegroup.getLexicalValue().trim().isEmpty()) {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdthesaurus(),
                        nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(), null,
                        "domaine", nodegroup.getConceptGroup().getIdgroup(), root);
                new DefaultTreeNode("facette", dynamicTreeNode);
            } else {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdthesaurus(),
                        nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(), null,
                        "domaine", nodegroup.getLexicalValue(), root);
                new DefaultTreeNode("facette", dynamicTreeNode);
            }

        }
        if (idTheso != null) {
            //loadOrphan(idTheso, langue);
        }

    }
    
    public void majSearchPermute() {
        selectedTerme.majSearchPermute();
        reInit();
        reExpand();
    }
    
    public void majSearch() {
        selectedTerme.majSearch();
        vue.setOnglet(1);
        reInit();
        reExpand();
    }

    public void onNodeExpand(NodeExpandEvent event) {

        if (!event.getTreeNode().getType().equals("orphan")) {
            ArrayList<NodeConceptTree> liste = null;
            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();
            int type = 3;

            //<Retirer noeuds fictifs>
            if (event.getTreeNode().getChildCount() == 1) {
                event.getTreeNode().getChildren().remove(0);
            }

            MyTreeNode myTreeNode = (MyTreeNode) event.getTreeNode();
            String idConcept = myTreeNode.getIdMot();
            if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso())) {

                myTreeNode.setTypeMot(1);//pour group ?

                liste = groupHelper.getRelationGroupOf(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());

                if (liste == null) {
                    liste = new ArrayList<NodeConceptTree>();
                }
                liste.addAll(conceptHelper.getListTopConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue()));

            } else {
                liste = conceptHelper.getListConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());
            }

            TreeNode treeNode;
            // 1 = domaine/Group, 2 = TT (top Term), 3 = Concept/term 

            // Ajout dans l'arbre
            for (NodeConceptTree nodeConceptTree : liste) {

                String value, idTC, icon;
                if (conceptHelper.haveChildren(connect.getPoolConnexion(), nodeConceptTree.getIdThesaurus(), nodeConceptTree.getIdConcept())
                        || nodeConceptTree.isHaveChildren()) {
                    icon = "dossier";
                    if (nodeConceptTree.isIsGroup()) {
                        icon = "domaine";

                    }

                    if (type == 2) { //CrÃ©ation de topConcepts
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        idTC = value;
                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) event.getTreeNode()).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept() != null) {
                        if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                            icon = "hidden";
                        }
                    }
                    treeNode = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                            ((MyTreeNode) event.getTreeNode()).getLangue(), ((MyTreeNode) event.getTreeNode()).getIdDomaine(),
                            idTC, icon, value, event.getTreeNode());
                    new DefaultTreeNode("fake", treeNode);
                } else {
                    icon = "fichier";
                    if (type == 2) { //CrÃ©ation de topConcepts
                        idTC = nodeConceptTree.getIdConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }

                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) event.getTreeNode()).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) event.getTreeNode()).getIdTheso(),
                            ((MyTreeNode) event.getTreeNode()).getLangue(), ((MyTreeNode) event.getTreeNode()).getIdDomaine(),
                            idTC, icon, value, event.getTreeNode());
                }
//*/
            }
        }

    }

    /**
     *
     * @param event
     */
    public void onNodeSelect(NodeSelectEvent event) {

        if (((MyTreeNode) event.getTreeNode()).getIdDomaine() != null) {
            selectedTerme.majTerme((MyTreeNode) selectedNode);
        }
        vue.setOnglet(0);
        selectedTerme.setTree(0);

    }

    /**
     * Permet de mettre à jour l'arbre et le terme à la sélection d'un index
     * rapide par autocomplétion
     */
    public void majIndexRapidSearch() {
        selectedTerme.majIndexRapidSearch(idThesoSelected, defaultLanguage);
        reInit();
        reExpand();
    }

    public void changeTerme(String id, int type) {
        
        selectedNode.setSelected(false);
        
        for (TreeNode node : selectedNodes){
            node.setSelected(false);
        }
        
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
            List<NodeGroup> racineNode = new GroupHelper().getListRootConceptGroup(connect.getPoolConnexion(), idTheso, langue);
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
            //loadOrphan(idTheso, langue);
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
                        // Si c'est le , on Ã©tend
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

    private void reExpandChild(ArrayList<String> listeId, TreeNode node, int cpt) {
        if (!node.isExpanded()) {
            ArrayList<NodeConceptTree> liste = null;
            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();
            int type = 3;

            if (node.getChildCount() == 1) {
                node.getChildren().remove(0);
            }

            MyTreeNode myTreeNode = (MyTreeNode) node;
            String idConcept = myTreeNode.getIdMot();
            if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso())) {

                myTreeNode.setTypeMot(1);//pour group ?

                liste = groupHelper.getRelationGroupOf(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());

                if (liste == null) {
                    liste = new ArrayList<NodeConceptTree>();
                }
                liste.addAll(conceptHelper.getListTopConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue()));

            } else {
                liste = conceptHelper.getListConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());
            }

            TreeNode tn = null;

            // Ajout dans l'arbre
            for (NodeConceptTree nodeConceptTree : liste) {

                String value, idTC, icon;
                if (conceptHelper.haveChildren(connect.getPoolConnexion(), nodeConceptTree.getIdThesaurus(), nodeConceptTree.getIdConcept())
                        || nodeConceptTree.isHaveChildren()) {
                    icon = "dossier";
                    if (nodeConceptTree.isIsGroup()) {
                        icon = "domaine";

                    }

                    if (type == 2) { //CrÃ©ation de topConcepts
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        idTC = value;
                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) node).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept() != null) {
                        if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                            icon = "hidden";
                        }
                    }
                    tn = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) node).getIdTheso(),
                            ((MyTreeNode) node).getLangue(), ((MyTreeNode) node).getIdDomaine(),
                            idTC, icon, value, node);
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
                    if (type == 2) { //CrÃ©ation de topConcepts
                        idTC = nodeConceptTree.getIdConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }

                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) node).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    tn = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) node).getIdTheso(),
                            ((MyTreeNode) node).getLangue(), ((MyTreeNode) node).getIdDomaine(),
                            idTC, icon, value, node);

                    if (listeId.get(cpt).equals(((MyTreeNode) tn).getIdMot())) {
                        tn.setSelected(true);
                        selectedNode = tn;
                        selectedNodes.add(tn);
                    } else {
                        tn.setSelected(false);
                    }

                }

//*/
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

    public void reInit() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
    }

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
        if ((idTerm = selectedTerme.isTermExist(valueEdit)) != null) {
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

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public ArrayList<TreeNode> getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(ArrayList<TreeNode> selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
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

    public boolean isCreateValid() {
        return createValid;
    }

    public void setCreateValid(boolean createValid) {
        this.createValid = createValid;
    }

    public String getIdThesoSelected() {
        return idThesoSelected;
    }

    public void setIdThesoSelected(String idThesoSelected) {
        this.idThesoSelected = idThesoSelected;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

}
