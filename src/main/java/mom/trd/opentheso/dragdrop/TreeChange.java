package mom.trd.opentheso.dragdrop;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.SelectedBeans.AutoCompletBean;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import static mom.trd.opentheso.skosapi.SKOSProperty.Collection;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *Class pour gérer certaines transformations de l'arbre
 * @author jm.prudham
 */

public class TreeChange {
   
  
    
   ArrayList<TreeNode> expandedNodes=new ArrayList<>();
   
    public TreeChange() {
    }

    
 
 
   
    /**
     * changeLeafToDirectory
     * méthode pour transformer un noeud qui est feuille de l'arbre en dossier
     * @param draggedNode
     * @param droppedNode 
     */
    public void changeLeafToDirectory(TreeNode draggedNode, TreeNode droppedNode ) {
        ((MyTreeNode)droppedNode).setType("dossier");
        new DefaultTreeNode((MyTreeNode)draggedNode,((MyTreeNode)droppedNode));
       
    }
    
    /**
     * changeToGroupChild
     * cette méthode aligne le type du noeud sur celui de son père
     * @param dragNode
     * @param dropNode 
     */
    public void changeToGroupChild(TreeNode dragNode, TreeNode dropNode) {
        
        
        switch(((MyTreeNode)dropNode).getType()){
            case("group"):((MyTreeNode)dragNode).setType("subGroup");
                            break;
            case("collection"):((MyTreeNode)dragNode).setType("subCollection");
                            break;
            case("thème"):((MyTreeNode)dragNode).setType("subThème");
                            break;
            case("microTheso"):((MyTreeNode)dragNode).setType("subMicroTheso");
                            break;
            case("subGroup"):if(!"subGroup".equals(((MyTreeNode)dragNode).getType()) )
                              ((MyTreeNode)dragNode).setType("dossier");
                                                          
                            break;  
           case("subCollection"):if(!"subCollection".equals(((MyTreeNode)dragNode).getType()) )
                              ((MyTreeNode)dragNode).setType("dossier");
                                                          
                            break; 
            case("subThème"):if(((MyTreeNode)dragNode).getType()!="subThème" )
                              ((MyTreeNode)dragNode).setType("dossier");
                                                          
                            break; 
           case("subMicroTheso"):if(((MyTreeNode)dragNode).getType()!="subMicroTheso" )
                              ((MyTreeNode)dragNode).setType("dossier");
                                                          
                            break; 
        }
        new DefaultTreeNode((MyTreeNode)dragNode,((MyTreeNode)dropNode));
    }
    
    
 

    /**
     * saveExpandedNodes
     * méthode qui parcourt un arbre treeNode pour sauvegarder les neoud 
     * étendus dans une arrayList expandedNodes
     * @param root 
     */
    public void saveExpandedNodes(TreeNode root) {
        for(TreeNode tn :root.getChildren()){
            if(tn.isExpanded()){
                this.expandedNodes.add(tn);
                saveExpandedNodes(tn);
            }
        }
    }
  
    public ArrayList<TreeNode> getExpandedNodes() {
        return expandedNodes;
    }
    
    /**
     * moveConceptTermTocConceptTermSameDomain
     * 
     * déplacement d'un concept vers un noeud du même domaine 
     * 
     * @param connect
     * @param originNodeIdConcept
     * @param BToriginNode
     * @param targetNodeIdConcept
     * @param idTheasurus
     * @param idUser 
     */
    public void moveConceptTermToConceptTermSameDomain(Connexion connect,String originNodeIdConcept,String BToriginNode,String targetNodeIdConcept,String idTheasurus,int idUser){
        ConceptHelper conceptHelper=new ConceptHelper();
        conceptHelper.moveBranch(connect.getPoolConnexion(),originNodeIdConcept,BToriginNode,targetNodeIdConcept,idTheasurus,idUser);
    }
    /**
     * moveConceptTermToConceptTermOtherDomain
     * 
     * déplacement d'un concept vers un autre domaine
     * 
     * @param connect
     * @param originNodeIdConcept
     * @param BToriginNode
     * @param targetNodeIdConcept
     * @param idTheasurus
     * @param idUser
     * @return 
     */
    public boolean moveConceptTermToConceptTermOtherDomain(Connexion connect,String originNodeIdConcept,String originNodeIdGroup,String BToriginNode,String targetNodeIdConcept,String idTheasurus,int idUser){
        ConceptHelper conceptHelper = new ConceptHelper();
        GroupHelper groupHelper = new GroupHelper();
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);

            // permet de déplacer une branche simplement, en cas d'erreur, rien n'est écrit 
            if (!conceptHelper.moveBranchToConceptOtherGroup(conn,
                   originNodeIdConcept,
                    BToriginNode,
                    targetNodeIdConcept,
                    idTheasurus,
                    idUser)) {
                return false;
            }
            
            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = new  ArrayList<>();
            lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), originNodeIdConcept, idTheasurus, lisIds);  
            
            //on récupère les identifiants des groups des BT #jm
            ArrayList<String> listeBt=conceptHelper.getIdBtFromAConcept(connect.getPoolConnexion().getConnection(), idTheasurus, originNodeIdConcept);
            ArrayList<String> listeIdGroupBT=new ArrayList<>();
            for(String id :listeBt){
               listeIdGroupBT.add(conceptHelper.getGroupIdOfConcept(connect.getPoolConnexion(), id, idTheasurus));
                
            }
            int freq=Collections.frequency(listeIdGroupBT,originNodeIdGroup);
            // on supprime l'ancien Groupe de la branche 
            /*ArrayList<String> domsOld = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(), originNodeIdConcept, idTheasurus);
            for (String domsOld1 : domsOld) {
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, domsOld1, idTheasurus)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }*/
            if(freq==1){
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, originNodeIdGroup, idTheasurus)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            // on ajoute le nouveau domaine à la branche
            ArrayList<String> domsNew = conceptHelper.getListGroupIdOfConcept(connect.getPoolConnexion(),targetNodeIdConcept, idTheasurus);
            for (String domsNew1 : domsNew) {

                if (!groupHelper.setDomainToBranch(conn,lisIds, domsNew1, idTheasurus)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            conn.commit();
            conn.close();
    }
        catch(SQLException e){
             Logger.getLogger(TreeChange.class.getName()).log(Level.SEVERE, "erreur dans le déplacement de la branche à un domaine différent de l'origine", e);
        }
        return true;
    }
    
    /**
     * moveConceptToGroupOtherDomain
     * 
     * la tête de la branche devient un top term dans un autre groupe
     * 
     * @param connect
     * @param originNodeIdConcept
     * @param BToriginNode
     * @param originDomain
     * @param targetNodeIdConcept
     * @param TargetNodeDomaine
     * @param idThesaurus
     * @param idUser
     * @return 
     */
    public boolean moveConceptToGroupOtherDomain(Connexion connect,String originNodeIdConcept,String BToriginNode,String originDomain,String targetNodeIdConcept,String TargetNodeDomaine,String idThesaurus,int idUser){
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
            if (!conceptHelper.moveBranchToAnotherMT(conn, originNodeIdConcept,
                    BToriginNode, originDomain, // ancien Group
                    TargetNodeDomaine, // nouveau Group
                    idThesaurus,
                    idUser)) {
              
                conn.rollback();
                conn.close();
                return false;
            }

            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = new  ArrayList<>();
            lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), originNodeIdConcept, idThesaurus, lisIds);
            
            
            // on supprime l'ancien Groupe de la branche 
            if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, originDomain, idThesaurus)) {
         
                conn.rollback();
                conn.close();
                return false;
            }

            // on ajoute le nouveau domaine à la branche
            if (!groupHelper.setDomainToBranch(conn, lisIds, TargetNodeDomaine, idThesaurus)) {
            
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();
      
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(TreeChange.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    /**
     * moveConceptToGroupOtherDomain
     * 
     * La tête de la branche devient un top term dans le même domaine d'origine
     * 
     * @param connect
     * @param originNodeIdConcept
     * @param BToriginNode
     * @param originDomain
     * @param targetNodeIdConcept
     * @param idThesaurus
     * @param idUser
     * @return 
     */
    public boolean momveConceptToGroupSameDomain(Connexion connect,String originNodeIdConcept,String BToriginNode,String originDomain,String targetNodeIdConcept,String idThesaurus,int idUser){
        
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
            if (!conceptHelper.moveBranchToMT(conn, originNodeIdConcept,
                    BToriginNode, originDomain, idThesaurus,
                    idUser)) {
              
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();
          
          
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(TreeChange.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * moveTopTermToConceptSameDomain
     * 
     * le top term se déplace dans une hierarchie
     * note : cette fonction est utilisé aussi pour déplacer un top
     * terme en dehors du groupe
     * 
     * @param connect
     * @param originNodeIdConcept
     * @param BToriginNode
     * @param originIdDomain
     * @param targetNodeIdConcept
     * @param idThesaurus
     * @param idUser
     * @return 
     */
    public boolean moveTopTermToConceptSameDomaine(Connexion connect,String originNodeIdConcept,String BToriginNode,String originIdDomain,String targetNodeIdConcept,String idThesaurus,int idUser){
      

        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            ConceptHelper conceptHelper = new ConceptHelper();

            // on déplace la branche au nouveau concept puis création de TG-TS (on ajoute la relation BT du concept, puis on supprime  
            // au concept la relation TT
            if (!conceptHelper.moveBranchFromMT(conn, originNodeIdConcept,
                    targetNodeIdConcept,
                    originIdDomain,
                    idThesaurus,
                    idUser)) {
              
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();
         

         
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
      
    /**
     * moveSubGroupToSubGroupDomain
     * 
     * déplace un sousgroupe vers un autre sous groupe
     * note: c'est la seul façon de déplace un sous groupe
     * 
     * @param connect
     * @param originNodeIdConcept
     * @param BToriginNode
     * @param originIdDomain
     * @param type
     * @param targetNodeIdConcept
     * @param idThesaurus
     * @param idUser : paramètre non usité (pour une utilisation ultérieur)
     * @return 
     */
    public boolean moveSubGroupToSubGroupDomain(Connexion connect,String originNodeIdConcept,String BToriginNode,String originIdDomain,String type,String targetNodeIdConcept,String idThesaurus,int idUser){
        
        try{
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            GroupHelper groupHelper = new GroupHelper();
            if(!groupHelper.moveSubGroupToSubGroup(conn, originNodeIdConcept,
                    targetNodeIdConcept,
                    BToriginNode,
                    type,
                    idThesaurus,
                    idUser)){
               conn.rollback();
               conn.close();
               return false;   
            }
            conn.commit();
            conn.close();
         

         
            return true;
        }
        catch(SQLException ex) {
            Logger.getLogger(AutoCompletBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
  
}
