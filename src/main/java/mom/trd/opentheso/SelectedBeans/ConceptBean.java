/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;

/**
 *
 * @author miled.rousset
 */

@ManagedBean(name = "conceptbean", eager = true)
@SessionScoped

public class ConceptBean implements Serializable{

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    private int deleteBranchOrphan = 0;
    /**
     * Creates a new instance of ConceptBean
     */
    public ConceptBean() {
    }

    public int getDeleteBranchOrphan() {
        return deleteBranchOrphan;
    }

    public void setDeleteBranchOrphan(int deleteBranchOrphan) {
        this.deleteBranchOrphan = deleteBranchOrphan;
    }
    
    /**
     * permet de supprimer le groupe avec ses concepts
     * @param selectedNode
     */
    public void deleteAllTheGroup(MyTreeNode selectedNode){
    //    String idGroup = selectedNode.getIdCurrentGroup();
        // trouver tous les concepts du groupe
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> idConcepts = conceptHelper.getAllIdConceptOfThesaurusByGroup(
                connect.getPoolConnexion(),
                selectedNode.getIdTheso(),
                selectedNode.getIdCurrentGroup());
     
        // supprimer les concepts
        for (String idConcept : idConcepts) {
            conceptHelper.deleteConceptWithoutControl(connect.getPoolConnexion(),
                    idConcept,
                    selectedNode.getIdTheso(),
                    1);
        }
        // supprimer le groupe (il faut corriger la focntion pour accepter la suppression des Id Handle en meme temps
      //  GroupHelper groupHelper = new GroupHelper();
      //  groupHelper.deleteConceptGroupRollBack(connect.getPoolConnexion(), idGroup, idThesaurus, deleteBranchOrphan);
    }
    
    /**
     * permet de supprimer toute la branche avec ses concepts
     * @param selectedNode
     */
    public void deleteBranch(MyTreeNode selectedNode){
        // trouver tous les concepts du groupe
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> idConcepts = conceptHelper.getIdsOfBranch(
                connect.getPoolConnexion(),
                selectedNode.getIdConcept(),
                selectedNode.getIdTheso());
     
        // supprimer les concepts
        for (String idConcept : idConcepts) {
            conceptHelper.deleteConceptWithoutControl(connect.getPoolConnexion(),
                    idConcept,
                    selectedNode.getIdTheso(),
                    1);
        }
        // supprimer le groupe (il faut corriger la focntion pour accepter la suppression des Id Handle en meme temps
      //  GroupHelper groupHelper = new GroupHelper();
      //  groupHelper.deleteConceptGroupRollBack(connect.getPoolConnexion(), idGroup, idThesaurus, deleteBranchOrphan);
    }    

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }
    
}
