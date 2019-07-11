package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;

@ManagedBean(name = "broaderTerm", eager = true)
@ViewScoped

public class BroaderTerm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    @ManagedProperty(value = "#{newtreeBean}")
    private NewTreeBean tree;    

    private StringBuilder message;
    
    
    @PostConstruct
    public void initConf() {
    }

    public void init() {
        message = new StringBuilder();
    }      
    
    
    /**
     * Cette fonction permet d'ajouter une relation TG à un concept Le TG existe
     * déjà dans le thésaurus, donc c'est une relation à créer
     * #MR
     * @param selectedAtt
     * @param mySelectedTreeNode
     * @param idUser
     * @return
     */
    public boolean addBroader(NodeAutoCompletion selectedAtt,
                        MyTreeNode mySelectedTreeNode,
                        int idUser) {

        // selectedAtt.getIdConcept() est le terme TG à ajouter
        

        if (selectedAtt == null || selectedAtt.getIdConcept().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error1")));
            return false;
        }
        if (selectedAtt.getIdConcept().equals(mySelectedTreeNode.getIdConcept())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.impossible")));
            return false;
        }
        // vérification si la relation est cohérente (BT et RT à la fois ?)  
        if(!isAddRelationBTValid(mySelectedTreeNode.getIdConcept(), selectedAtt.getIdConcept())){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("relation.errorNTRT")));
            return false;        
        } 
        
        // addTermeGene(idNT, idBT)
        if (!addBroader__(mySelectedTreeNode, selectedAtt.getIdConcept(), idUser)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("autoComp.error2")));
            return false;
        }

        tree.reInit();
        tree.reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", selectedAtt.getPrefLabel() + " " + langueBean.getMsg("autoComp.info1")));
        return true;
    }
    
    /**
     * Ajoute une relation terme générique au concept courant
     *
     * @param idNT
     * @param idBT
     * @return true or false
     */
 /*   private boolean addBroader__1(String idNT, String idBT) {
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if (termeGenerique.isEmpty()) {
                // c'était un orphelin
                if (!new OrphanHelper().deleteOrphan(conn, idNT, idTheso)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            //On ajoute la realtion BT au concept
            if (!new RelationsHelper().addRelationBT(conn, idNT, idTheso, idBT, user.getUser().getIdUser())) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }*/
    
    /**
     * Ajoute une relation terme générique au concept courant
     *
     * @param mySelectedTreeNode
     * @param idBroaderToAdd
     * @param idUser
     * #MR
     * @return true or false
     */
    public boolean addBroader__(MyTreeNode mySelectedTreeNode,
            String idBroaderToAdd, int idUser) {

        ConceptHelper conceptHelper = new ConceptHelper();
        RelationsHelper relationsHelper = new RelationsHelper();
        GroupHelper groupHelper = new GroupHelper();

        // la liste des groupes du nouveau concept BT
        ArrayList<String> listIdGroupOfNewBT = groupHelper.getListIdGroupOfConcept(
                connect.getPoolConnexion(), mySelectedTreeNode.getIdTheso(),
                idBroaderToAdd);
        
        // la liste des groupes du concept séléctionné
        ArrayList<String> listIdGroupOfConcept = groupHelper.getListIdGroupOfConcept(
                connect.getPoolConnexion(), mySelectedTreeNode.getIdTheso(), mySelectedTreeNode.getIdConcept());        
        
        // on récupère les Ids des concepts à modifier (toute la branche, ceci permet de gérer la polyhiérarchie entre les collections 
        ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), mySelectedTreeNode.getIdConcept(),mySelectedTreeNode.getIdTheso());
        
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
        
            for (String idGroup : listIdGroupOfNewBT) {
                // si le concept séléctionné n'a pas ce groupe du nouveau BT, alors on le lui ajoute
                if(!listIdGroupOfConcept.contains(idGroup)) {
                    // on ajoute le nouveau domaine à la branche
                    if (!groupHelper.addDomainToBranch(conn, lisIds, idGroup, mySelectedTreeNode.getIdTheso(), idUser)) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                        conn.rollback();
                        conn.close();
                        message.append("Erreur dans l'ajout de groupe à la branche");
                        return false;
                    }
                }
            }
            //On ajoute la realtion BT au concept
            if (!relationsHelper.addRelationBT(conn, 
                    mySelectedTreeNode.getIdConcept(),
                    mySelectedTreeNode.getIdTheso(),
                    idBroaderToAdd, idUser)) {
                conn.rollback();
                conn.close();
                message.append("Erreur dans l'ajout de la relation BT au concept");
                return false;
            }            
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
            message.append(ex.toString());
            return false;
        }        
        return true;
    }    
    
    
    
    private boolean isAddRelationBTValid(String idConcept1, String idConcept2) {
        RelationsHelper relationsHelper = new RelationsHelper();
        if(idConcept1.equalsIgnoreCase(idConcept2)) return false;
        
        // relations RT et BT en même temps interdites
        if(relationsHelper.isConceptHaveRelationRT(connect.getPoolConnexion(),
                idConcept1, idConcept2, tree.getIdThesoSelected()) == true){ 
            return false;
        }
        
        // relations BT et NT en même temps interdites
        if(relationsHelper.isConceptHaveRelationNTorBT(connect.getPoolConnexion(),
                idConcept2, idConcept1, tree.getIdThesoSelected()) == true){ 
            return false;
        }
        
        // relation entre frères est interdite 
        if(relationsHelper.isConceptHaveBrother(connect.getPoolConnexion(),
                idConcept1, idConcept2, tree.getIdThesoSelected()) == true){ 
            return false;
        }        
        
        return true;
    }
    
    
    
    
    
    
    
    
    /**
     * Permet de supprimer la relation Broader (terme générique)
     * Si le concept n'a plus de relation BT, il sera placé dans les orphelinsÒ
     * #MR
     * @param mySelectedTreeNode
     * @param idBroaderToDelete
     * @param idUser
     */
    public void deleteBroader(MyTreeNode mySelectedTreeNode,
            String idBroaderToDelete,
            int idUser) {
        if (!deleteBroader__(mySelectedTreeNode, idBroaderToDelete, idUser)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", message.toString()));//langueBean.getMsg("error")));
            return;
        }
        tree.reInit();
        tree.reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info3")));
    }
    
    /**
     * Supprime la relation hiérarchique qui lie le terme courant à son père
     *
     * @param idBTtoDelete l'identifiant du père
     * @return true or false
     * #MR
     */
    private boolean deleteBroader__(MyTreeNode mySelectedTreeNode,
            String idBroaderToDelete, int idUser) {


        ConceptHelper conceptHelper = new ConceptHelper();
        RelationsHelper relationsHelper = new RelationsHelper();
        GroupHelper groupHelper = new GroupHelper();
  //      OrphanHelper orphanHelper = new OrphanHelper();
        
        ArrayList<String> listBT = relationsHelper.getListIdBT(
                connect.getPoolConnexion(),
                mySelectedTreeNode.getIdConcept(),
                mySelectedTreeNode.getIdTheso());
        
        // premier cas, si la branche n'a qu'un BT, alors elle devient TopTerm dans la collection courante
        if (listBT.size() == 1) {
            // Le concept devient TopTerm
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);
                if (!relationsHelper.deleteRelationBT(conn,
                        mySelectedTreeNode.getIdConcept(),
                        mySelectedTreeNode.getIdTheso(),
                        idBroaderToDelete, idUser)) {
                    conn.rollback();
                    conn.close();
                    message.append("Erreur lors de la suppression du terme générique");
                    return false;
                }
                if(!conceptHelper.setTopConcept(connect.getPoolConnexion(),
                        mySelectedTreeNode.getIdConcept(),
                        mySelectedTreeNode.getIdTheso())){
                    conn.rollback();
                    conn.close();
                    message.append("Erreur pour passer le concept en TopTerme");
                    return false;
                }
                conn.commit();
                conn.close();

//                termeGenerique = new ArrayList<>();
//                vue.setAddTGen(0);
                return true;

            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                message.append(ex.toString());
                return false;
            }
        }

        // deuxième cas où la branche a plusieurs termes générique
        if (listBT.size() > 1) {
            try {
                Connection conn = connect.getPoolConnexion().getConnection();
                conn.setAutoCommit(false);            
            
                // si la branche est dans le même domaine, alors on supprime seulement la relation BT

                // on récupère les BT du concept sélectionné
               // ArrayList<String> idBTs = relationsHelper.getListIdBT(connect.getPoolConnexion(), idC, idTheso);
                //on enlève le BT à supprimer
                listBT.remove(idBroaderToDelete);

                // on récupère les domaines du concept BT à supprimer
                ArrayList<String> idGroupsOfConceptBTtoDelete = groupHelper.getListIdGroupOfConcept(
                        connect.getPoolConnexion(), mySelectedTreeNode.getIdTheso(),idBroaderToDelete); 
                ArrayList<String> idGroupsOfBT = new ArrayList<>();

                for (String idBT : listBT) {
                    // on récupère les domaines du BT sélectionné
                    idGroupsOfBT.addAll(groupHelper.getListIdGroupOfConcept(
                            connect.getPoolConnexion(), mySelectedTreeNode.getIdTheso(), idBT));
                }

                // on compare les Groupes des autres BT qui restent au groupe du BT à supprimer
                for (String idGroupOfConceptBTtoDelete : idGroupsOfConceptBTtoDelete) {
                    if(!idGroupsOfBT.contains(idGroupOfConceptBTtoDelete)) {
                        // c'est un groupe à supprimer du BT

                        // on récupère les Ids des concepts à modifier
                        ArrayList<String> lisIds = conceptHelper.getIdsOfBranch(
                                connect.getPoolConnexion(), mySelectedTreeNode.getIdConcept(), mySelectedTreeNode.getIdTheso());  
                        if (!groupHelper.deleteAllDomainOfBranch(conn, lisIds, idGroupOfConceptBTtoDelete, mySelectedTreeNode.getIdTheso())) {
                            conn.rollback();
                            conn.close();
                            message.append("Erreur lors de la suppression du concept du groupe");
                            return false;
                        }
                    }
                }

                if (!relationsHelper.deleteRelationBT(conn,
                        mySelectedTreeNode.getIdConcept(), mySelectedTreeNode.getIdTheso(),
                        idBroaderToDelete, idUser)) {
                    conn.rollback();
                    conn.close();
                    message.append("Erreur lors de la suppression du terme générique");
                    return false;
                }
                conn.commit();
                conn.close();

                tree.getSelectedTerme().majTerme(mySelectedTreeNode);
                return true;

            } catch (SQLException ex) {
                Logger.getLogger(SelectedTerme.class.getName()).log(Level.SEVERE, null, ex);
                 message.append(ex.toString());
                return false;
            }
        }

        return false;
    }
    
    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public NewTreeBean getTree() {
        return tree;
    }

    public void setTree(NewTreeBean tree) {
        this.tree = tree;
    }
   
}
