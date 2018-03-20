/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.util.Comparator;
import org.primefaces.model.TreeNode;

/**
 *Cette classe doit permettre de mettre en ordre un liste de treeNOdes
 * numérotés !
 * @author jm.prudham
 */
public class TreeNodeComparator implements Comparator<TreeNode> {

    @Override
    public int compare(TreeNode o1, TreeNode o2) {
        if(o1 == null || o2 == null) return -1;
        if(o1.getData().toString().split(" ")[0].isEmpty()) return 0; 
        if(o2.getData().toString().split(" ")[0].isEmpty()) return 0;
        
        
        String[] tab=o1.getData().toString().split(" ")[0].split("\\.");
        String[] tab2=o2.getData().toString().split(" ")[0].split("\\.");
        int elem1=Integer.parseInt(tab[tab.length-1]);
        int elem2=Integer.parseInt(tab2[tab2.length-1]);
        if(elem1<elem2)return -1;
        if(elem2<elem1)return 1;
        return 0;
        
    }
    
    
}
