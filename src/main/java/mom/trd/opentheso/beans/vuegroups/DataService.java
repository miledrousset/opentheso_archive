/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans.vuegroups;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author miledrousset
 */


public class DataService {
     
    public TreeNode createRoot() {
        TreeNode root = new DefaultTreeNode(new Document("Files", "-", "Folder","milo"), null);
        return root;
    }
    
    public TreeNode addNode(Data data, TreeNode parentNode){
        if(parentNode == null) return null;         
        TreeNode document = new DefaultTreeNode(data, parentNode);
        return document;
    }
}
