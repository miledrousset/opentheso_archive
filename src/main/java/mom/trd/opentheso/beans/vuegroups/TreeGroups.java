/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans.vuegroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
//import javax.enterprise.context.SessionScoped;

import javax.faces.context.FacesContext;
import mom.trd.opentheso.SelectedBeans.Connexion;
import mom.trd.opentheso.SelectedBeans.ExternalResources;
import mom.trd.opentheso.SelectedBeans.NewTreeBean;
import mom.trd.opentheso.SelectedBeans.SelectedTerme;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import mom.trd.opentheso.bdd.helper.nodes.NodeIdValue;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author miledrousset
 */


@ManagedBean(name = "treeGroups", eager = true)
@SessionScoped

//@SessionScoped
//@Named("treeGroups")

public class TreeGroups implements Serializable {
    
    private DataService dataService;
     
    private TreeNode selectedNode;
    private TreeNode root;
     
//    @Inject 
//    private Connexion connexion;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connexion;
    
    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    @ManagedProperty(value = "#{externalResources}")
    private ExternalResources externalResources;
    
    @ManagedProperty(value = "#{newtreeBean}")
    private NewTreeBean newTreeBean;    
        
    

    private String idTheso;
    private String idLang;
    
   
    public void initialise(String idTheso, String idLang) {
        this.idTheso = idTheso;
        this.idLang = idLang;
        dataService = new DataService();
        root = dataService.createRoot();
        addFirstNodes();
    }
    
    private boolean addFirstNodes() {
        GroupHelper groupHelper = new GroupHelper();
        TreeNodeData data;

        // liste des groupes de premier niveau
        List<NodeGroup> racineNode = groupHelper.getListRootConceptGroup(
                connexion.getPoolConnexion(),
                idTheso,
                idLang);

        for (NodeGroup nodeGroup : racineNode) {
            data = new TreeNodeData(
                    nodeGroup.getConceptGroup().getIdgroup(),
                    nodeGroup.getLexicalValue(),
                    nodeGroup.getConceptGroup().getNotation(),
                    true,//isgroup
                    false,//isSubGroup
                    false,//isConcept
                    "group"
                    );
            if(nodeGroup.isIsHaveChildren())
                dataService.addNodeWithChild("group", data, root);
            else
                dataService.addNodeWithoutChild("group", data, root);

        }
        return true;
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
     
    public void onNodeExpand(NodeExpandEvent event) {
        DefaultTreeNode parent = (DefaultTreeNode) event.getTreeNode();
         if (parent.getChildCount() == 1 && parent.getChildren().get(0).getData().toString().equals("DUMMY")) {
             parent.getChildren().remove(0);
             addGroupsChild(parent);
             addConceptsChild(parent);
         }
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    private boolean addGroupsChild(TreeNode parent) {
        GroupHelper groupHelper = new GroupHelper();
        TreeNodeData data;

        ArrayList<NodeGroup> listeSubGroup = groupHelper.getListChildsOfGroup(
                connexion.getPoolConnexion(),
                ((TreeNodeData)parent.getData()).getNodeId(),
                idTheso,
                idLang);
        if(listeSubGroup == null) {
            parent.setType("group");
            return true;
        }
        for (NodeGroup nodeGroup : listeSubGroup) {
            data = new TreeNodeData(
                    nodeGroup.getConceptGroup().getIdgroup(),
                    nodeGroup.getLexicalValue(),
                    nodeGroup.getConceptGroup().getNotation(),
                    false,//isgroup
                    true,//isSubGroup
                    false,//isConcept
                    "subGroup"
                    );
            if(nodeGroup.isIsHaveChildren())
                dataService.addNodeWithChild("subGroup", data, parent);
            else
                dataService.addNodeWithoutChild("subGroup", data, parent);
        }
        return true;
    }
    
    private boolean addConceptsChild(TreeNode parent) {
        ConceptHelper conceptHelper = new ConceptHelper();
        TreeNodeData data;

        ArrayList<NodeIdValue> listeConceptsOfGroup = conceptHelper.getListConceptsOfGroup(
                connexion.getPoolConnexion(),
                idTheso,
                idLang,
                ((TreeNodeData)parent.getData()).getNodeId());
        if(listeConceptsOfGroup == null || listeConceptsOfGroup.isEmpty()) {
            parent.setType("group");
            return true;
        }
        for (NodeIdValue nodeGroup : listeConceptsOfGroup) {
            data = new TreeNodeData(
                    nodeGroup.getId(),
                    nodeGroup.getValue(),
                    "",
                    false,//isgroup
                    false,//isSubGroup
                    true,//isConcept
                    "concept"
                    );
            dataService.addNodeWithoutChild("concept", data, parent);
        }
        return true;
    }    
 
    public void onNodeCollapse(NodeCollapseEvent event) {
  /*      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
        */
    }
 
    public void onNodeSelect(NodeSelectEvent event) {
        
        /// test pour modifier le label du node, il suffit de renommer le node et ca marche automatiquement
        /// ((TreeNodeData)selectedNode.getData()).setName("name2");
        ///
        
        externalResources.loadImages(idTheso, ((TreeNodeData)selectedNode.getData()).getNodeId());
        MyTreeNode myTreeNode = new MyTreeNode(3, ((TreeNodeData)selectedNode.getData()).getNodeId(),
                idTheso,
                idLang,
                "", "", "", null, null, null);
        selectedTerme.majTerme(myTreeNode);
        newTreeBean.setSelectedNode(myTreeNode);
    }
 
    public void onNodeUnselect(NodeUnselectEvent event) {
    /*    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);*/
    }

    
    
    
    
    
    public Connexion getConnexion() {
        return connexion;
    }

    public void setConnexion(Connexion connexion) {
        this.connexion = connexion;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public ExternalResources getExternalResources() {
        return externalResources;
    }

    public void setExternalResources(ExternalResources externalResources) {
        this.externalResources = externalResources;
    }

    public NewTreeBean getNewTreeBean() {
        return newTreeBean;
    }

    public void setNewTreeBean(NewTreeBean newTreeBean) {
        this.newTreeBean = newTreeBean;
    }
   
}
