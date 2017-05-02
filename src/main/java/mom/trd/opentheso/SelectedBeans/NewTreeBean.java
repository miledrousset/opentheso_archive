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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
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

    private TreeNode root;
    private TreeNode selectedNode;
    private ArrayList<TreeNode> selectedNodes;

    public NewTreeBean() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
        selectedNodes = new ArrayList<>();
    }

    /**
     *
     * @param idTheso
     * @param langue
     */
    @PostConstruct
    public void initTree() {
        String idTheso = "ycjOekDAmF";
        String langue = "fr";
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
                
/*
                
                
                
                String icon ="";
                String lexicalValue;
                //detection type
                if (nodeConceptTree.isIsGroup()) {
                    icon = "domaine";
                    type = 1; //group
                } else if(nodeConceptTree.isIsTopTerm()){
                    if(nodeConceptTree.isHaveChildren()){
                        icon = "dosier";
                        type = 3; 
                    }  
                    else{
                        icon = "fichier";
                        type = 3;
                    } 
                }
                
                
                // ******** création ***********
                
                //nom
                if (nodeConceptTree.getTitle().trim().isEmpty()) {
                    lexicalValue = nodeConceptTree.getIdConcept();
                } else {
                    lexicalValue = nodeConceptTree.getTitle();
                }
                
                //node
                idConcept = nodeConceptTree.getIdConcept();
                String idTheso = nodeConceptTree.getIdThesaurus();
                String idLangue = nodeConceptTree.getIdLang();
                String idDomaine = ((MyTreeNode) event.getTreeNode()).getIdDomaine();
                String idTopConcept = ((MyTreeNode) event.getTreeNode()).getIdTopConcept(); //pas sur


                treeNode = new MyTreeNode(type, idConcept, idTheso, idLangue, idDomaine, idTopConcept, icon, lexicalValue, event.getTreeNode());
                
                //permet d'afficher la possiblilité d'extend
                if(nodeConceptTree.isHaveChildren())
                    new DefaultTreeNode("fake", treeNode);
                
*/
///*      
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

}
