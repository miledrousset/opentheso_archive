package mom.trd.opentheso.dragdrop;


import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.SelectedBeans.AutoCompletBean;
import mom.trd.opentheso.SelectedBeans.SelectedTerme;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.SelectedBeans.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
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
     * @return  
     */
    public boolean moveConceptTermToConceptTermSameDomain(Connexion connect,String originNodeIdConcept,String BToriginNode,String targetNodeIdConcept,String idTheasurus,int idUser){
        ConceptHelper conceptHelper=new ConceptHelper();
        return conceptHelper.moveBranch(connect.getPoolConnexion(),originNodeIdConcept,BToriginNode,targetNodeIdConcept,idTheasurus,idUser);
    }
    /**
     * moveConceptTermToConceptTermOtherDomain
     * 
     * déplacement d'un concept vers un autre domaine
     * 
     * @param connect
     * @param originNodeIdConcept
     * @param originNodeIdGroup
     * @param BToriginNode
     * @param targetNodeIdConcept
     * @param targetNodeIdGroupe
     * @param idTheasurus
     * @param idUser
     * @return 
     */
    public boolean moveConceptTermToConceptTermOtherDomain(Connexion connect,
            String originNodeIdConcept,String originNodeIdGroup,String BToriginNode,
            String targetNodeIdConcept,String targetNodeIdGroupe,String idTheasurus,int idUser){
        ConceptHelper conceptHelper = new ConceptHelper();
        GroupHelper groupHelper = new GroupHelper();
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            //on récupère les identifiants  des BT pour un groupe #jm
            ArrayList<String> listeIdGroupBT=conceptHelper.getAllBTOfConceptOfThisGroup(connect.getPoolConnexion(),originNodeIdConcept,originNodeIdGroup, idTheasurus);
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
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), originNodeIdConcept, idTheasurus);  
            if(listeIdGroupBT.size()==1){
                
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, originNodeIdGroup, idTheasurus)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
               
            }
            
            listeIdGroupBT=conceptHelper.getAllBTOfConceptOfThisGroup(connect.getPoolConnexion(),originNodeIdConcept,targetNodeIdGroupe, idTheasurus);
            if(listeIdGroupBT.isEmpty()){ 
               if (!groupHelper.addDomainToBranch(conn,lisIds, targetNodeIdGroupe, idTheasurus,idUser)) {
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
            //on récupère les identifiants  des BT pour un groupe #jm
            ArrayList<String> listeIdGroupBT=conceptHelper.getAllBTOfConceptOfThisGroup(connect.getPoolConnexion(),originNodeIdConcept,originDomain, idThesaurus);
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
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), originNodeIdConcept, idThesaurus);
            
            if(listeIdGroupBT.size()==1){
            // on supprime l'ancien Groupe de la branche 
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, originDomain, idThesaurus)) {

                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            // on ajoute le nouveau domaine à la branche
            listeIdGroupBT=conceptHelper.getAllBTOfConceptOfThisGroup(connect.getPoolConnexion(),originNodeIdConcept,TargetNodeDomaine, idThesaurus);
            if(listeIdGroupBT.isEmpty()){ 

                if (!groupHelper.addDomainToBranch(conn,lisIds, TargetNodeDomaine, idThesaurus,idUser)) {

                    conn.rollback();
                    conn.close();
                    return false;
                }
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
    
    public boolean moveTopTermToOtherDomaine(Connexion connect,
            String originNodeIdConcept,String originIdDomain,
            String targetNodeIdDomain,String idThesaurus,int idUser){
             GroupHelper groupHelper=new GroupHelper();
             ConceptHelper conceptHelper=new ConceptHelper();
                // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), originNodeIdConcept, idThesaurus);
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);


                // on déplace la branche au nouveau concept puis création de TG-TS (on ajoute la relation BT du concept, puis on supprime  
                // au concept la relation TT

                /*on  modifie le domaine */
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, originIdDomain, idThesaurus)) {
                        conn.rollback();
                        conn.close();
                        return false;
                }
                if (!groupHelper.addDomainToBranch(conn,lisIds, targetNodeIdDomain, idThesaurus,idUser)) {
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
     * note: c'est la seul façon de déplacer un sous groupe
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

    public boolean moveTopTermToConceptOtherDomaine(Connexion connect, String idConceptOrigin, String idBT, String idOriginGroup, String targetidConcept, String targetIGroup, String idThesoSelected, int id) {
        try{
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            GroupHelper groupHelper = new GroupHelper();
            ConceptHelper conceptHelper=new ConceptHelper();
            RelationsHelper relationHelper=new RelationsHelper();
            // on récupère les Ids des concepts à modifier 
            ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), idConceptOrigin, idThesoSelected);  
        
                if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, idOriginGroup, idThesoSelected)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
               
          
          
               if (!groupHelper.addDomainToBranch(conn,lisIds, targetIGroup, idThesoSelected,id)) {
                             conn.rollback();
                             conn.close();
                             return false;
                     }
            
            if(!relationHelper.addRelationBT(conn, idConceptOrigin, idThesoSelected, targetidConcept, id)){
               conn.rollback();
               conn.close();
               return false;  
            }
            if(!relationHelper.deleteRelationTT(conn, idConceptOrigin, idThesoSelected, id)){
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

    public ArrayList<String> getPathFromSelectedConcept(Connexion conn,SelectedTerme selectedTerme) {
         
        String id=selectedTerme.getIdC();
        String theso=selectedTerme.getIdTheso();
      
        GroupHelper gh=new GroupHelper();
        ArrayList<NodeGroup> groups= gh.getListGroupOfConcept(conn.getPoolConnexion(), theso,id,selectedTerme.getIdlangue());
        
        ConceptHelper ch=new ConceptHelper();
        ArrayList<String> result=new ArrayList<>();
        ArrayList<String> bt=new ArrayList<>();
        ArrayList<String> groupIds=new ArrayList<>();
        for(NodeGroup ng : groups ){
        groupIds.add(ng.getConceptGroup().getIdgroup());
        bt.addAll(ch.getAllBTOfConceptOfThisGroup(conn.getPoolConnexion(), id, ng.getConceptGroup().getIdgroup(), theso));
        }
        
        result.addAll(bt);
        while(!bt.isEmpty()){
            ArrayList<String> tmp=new ArrayList<>();
            for(String idBt : bt){
                for(String idGroup : groupIds){
                tmp.addAll(ch.getAllBTOfConceptOfThisGroup(conn.getPoolConnexion(), idBt, idGroup, theso));
            
                }
            }
            bt=new ArrayList<>(tmp);
            result.addAll(bt);
        }
        for(String idGroup : groupIds){
          String fat=gh.getIdFather(conn.getPoolConnexion(), idGroup, theso);
          result.add(fat);
          while(fat!=null && !fat.isEmpty()){
              fat=gh.getIdFather(conn.getPoolConnexion(), fat, theso);
              result.add(fat);
          } 
        }
       
        
        result.addAll(groupIds);
        return result;
    }
  
}
