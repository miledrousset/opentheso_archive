/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans.vuegroups;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author miledrousset
 */

@Named
@ApplicationScoped
public class DocumentService {
     
    public TreeNode createDocuments() {
        TreeNode root = new DefaultTreeNode(new Document("Files", "-", "Folder","milo"), null);
         
        TreeNode documents = new DefaultTreeNode(new Document("Documents", "-", "Folder","milo"), root);
        TreeNode pictures = new DefaultTreeNode(new Document("Pictures", "-", "Folder","milo"), root);
        TreeNode movies = new DefaultTreeNode(new Document("Movies", "-", "Folder","milo"), root);
         
        TreeNode work = new DefaultTreeNode(new Document("Work", "-", "Folder","milo"), documents);
        TreeNode primefaces = new DefaultTreeNode(new Document("PrimeFaces", "-", "Folder","milo"), documents);
         
        //Documents
        TreeNode expenses = new DefaultTreeNode("document", new Document("Expenses.doc", "30 KB", "Word Document","milo"), work);
        TreeNode resume = new DefaultTreeNode("document", new Document("Resume.doc", "10 KB", "Word Document","milo"), work);
        TreeNode refdoc = new DefaultTreeNode("document", new Document("RefDoc.pages", "40 KB", "Pages Document","milo"), primefaces);
         
        //Pictures
        TreeNode barca = new DefaultTreeNode("picture", new Document("barcelona.jpg", "30 KB", "JPEG Image","milo"), pictures);
        TreeNode primelogo = new DefaultTreeNode("picture", new Document("logo.jpg", "45 KB", "JPEG Image","milo"), pictures);
        TreeNode optimus = new DefaultTreeNode("picture", new Document("optimusprime.png", "96 KB", "PNG Image","milo"), pictures);
         
        //Movies
        TreeNode pacino = new DefaultTreeNode(new Document("Al Pacino", "-", "Folder","milo"), movies);
        TreeNode deniro = new DefaultTreeNode(new Document("Robert De Niro", "-", "Folder","milo"), movies);
         
        TreeNode scarface = new DefaultTreeNode("mp3", new Document("Scarface", "15 GB", "Movie File","milo"), pacino);
        TreeNode carlitosWay = new DefaultTreeNode("mp3", new Document("Carlitos' Way", "24 GB", "Movie File","milo"), pacino);
         
        TreeNode goodfellas = new DefaultTreeNode("mp3", new Document("Goodfellas", "23 GB", "Movie File","milo"), deniro);
        TreeNode untouchables = new DefaultTreeNode("mp3", new Document("Untouchables", "17 GB", "Movie File","milo"), deniro);
         
        return root;
    }
}
