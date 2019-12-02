/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans.vuegroups;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.SelectedBeans.Connexion;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author miledrousset
 */

//@Named("treeGroups")
@ManagedBean(name = "treeGroups", eager = true)
@SessionScoped
public class TreeGroups implements Serializable {
    
    private DataService dataService;
     
    private TreeNode selectedNode;
    private TreeNode root;
     
    
    @PostConstruct
    public void init() {
        dataService = new DataService();
        root = dataService.createRoot();
        TreeNode treeNode = dataService.addNode(new Data("name", "size", "type", "miled"),root);  
        ////
        // il faut écrire le noeud et ses enfants 
        /////
        dataService.addNode(new Data("name", "size", "type", "miled"),treeNode);
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
    //    Data data = new Data("name", "size", "type", "miled");
        dataService.addNode(new Data("name", "size", "type", "miled"),event.getTreeNode());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void onNodeCollapse(NodeCollapseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void onNodeSelect(NodeSelectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void onNodeUnselect(NodeUnselectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
