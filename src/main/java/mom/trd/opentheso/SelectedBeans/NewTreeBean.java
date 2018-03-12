/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.OrphanHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeRT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.dragdrop.StructIdBroaderTerm;
import mom.trd.opentheso.dragdrop.TreeChange;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
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

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{conceptbean}")
    private ConceptBean conceptbean;

    private TreeNode root;
    private TreeNode selectedNode;
    private ArrayList<TreeNode> selectedNodes;
    private String idThesoSelected;
    private String defaultLanguage;
    private ArrayList<String> orphans;

    private boolean duplicate = false;
    private boolean forced = false;
    private boolean editPassed = false;

    public NewTreeBean() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
        selectedNodes = new ArrayList<>();
    }



    private String NTtag;

    /**
     * * attributs pour le drag and drop***************
     */
    private MyTreeNode draggedNode;
    private MyTreeNode droppedNode;
    private String parentId;
    private ArrayList<StructIdBroaderTerm> idsBT;
    private ArrayList<String> idsBTRemoveNode;

    /**
     * ************************************************
     */
    /**
     * attributs pour l'alignement des domaines et des BT
     * 
     */
      
    public ArrayList<String> groupIds=new ArrayList<>();
    public ArrayList<String> groupLexicalValues=new ArrayList<>();
    public String idGroupAlign="";
    
    
    /*************************************************************************/
    
    /***atttribut pour l'ajout multiple de NT ****/
    private byte[] multipleNT;
    
    /**************************************************************************/
    /************attribut pour la numérotation des groupes et des sous groupes
     * 
     */
    
    int nouveauSuffixe;
    String ancienPrefixe;
    
    /**
     *
     * @param idTheso
     * @param langue
     */
    private String getTypeOfGroup(String typeCode) {
        String type;
        switch (typeCode) {

            case "G":
                type = "group";
                break;
            case "C":
                type = "collection";
                break;
            case "T":
                type = "thème";
                break;
            case "MT":
            default:
                type = "microTheso";
                break;
        }
        return type;
    }

    private String getTypeOfSubGroup(String typeCode) {
        String type;
        switch (typeCode) {

            case "G":
                type = "subGroup";
                break;
            case "C":
                type = "subCollection";
                break;
            case "T":
                type = "subThème";
                break;
            case "MT":
            default:
                type = "subMicroTheso";
                break;
        }
        return type;
    }

    public void init() {
        duplicate = false;
        forced = false;
        editPassed = false;
    }

    /**
     * Pour détecter les agents d'indexation
     *
     * @return
     */
    public String getBrowserName() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String userAgent = externalContext.getRequestHeaderMap().get("User-Agent");

        if (userAgent.toLowerCase().contains("slurp")) {
            return "agent";
        }
        if (userAgent.toLowerCase().contains("msnbot")) {
            return "agent";
        }
        if (userAgent.toLowerCase().contains("googlebot")) {
            return "agent";
        }
        return "notagent";
    }

    public void initTree(String idTheso, String langue) {

        //      idThesoSelected = idTheso;
        //      defaultLanguage = langue;
        root = (TreeNode) new DefaultTreeNode("Root", null);
        int count=1;//attribut pour la numérotation des groupes
        if (connect.getPoolConnexion() == null) {
            System.err.println("Opentheso n'a pas pu se connecter à la base de données");
            return;
        }
        List<NodeGroup> racineNode = new GroupHelper().getListRootConceptGroup(connect.getPoolConnexion(), idTheso, langue);
        Collections.sort(racineNode);
        ArrayList<MyTreeNode> listeNode=new ArrayList<>();
        // Les premiers noeuds de l'arbre sont de type Groupe (isGroup = true)
        for (NodeGroup nodegroup : racineNode) {

            String typeCode = nodegroup.getConceptGroup().getIdtypecode();
            String type = getTypeOfGroup(typeCode);

            if (nodegroup.getLexicalValue().trim().isEmpty()) {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdthesaurus(),
                        nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdtypecode(),
                        null,
                        type, nodegroup.getConceptGroup().getIdgroup(), root);
                ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
               
                 new DefaultTreeNode("facette", dynamicTreeNode);
            } else {
                TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdthesaurus(),
                        nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(),
                        nodegroup.getConceptGroup().getIdtypecode(),
                        null,
                        type, nodegroup.getLexicalValue(), null);
                ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
                /****code pour la numérotation des groupes ******************/
                GroupHelper groupHelper= new GroupHelper();
                 String suffix=groupHelper.getSuffixFromNode(connect.getPoolConnexion(), nodegroup.getConceptGroup().getIdthesaurus(),nodegroup.getConceptGroup().getIdgroup());
                    
                    if(suffix.equalsIgnoreCase("0") || suffix.equalsIgnoreCase("00")){
                     
                       suffix=""+count;
                       count++;
                       groupHelper.saveSuffixFromNode(connect.getPoolConnexion(), nodegroup.getConceptGroup().getIdthesaurus(), nodegroup.getConceptGroup().getIdgroup(),suffix);
                    }
                ((MyTreeNode)dynamicTreeNode).setPrefix(suffix);//ici c'est un groupe donc pas de suffix
                ((MyTreeNode)dynamicTreeNode).setData(((MyTreeNode)dynamicTreeNode).getNumerotation()+" "+dynamicTreeNode.getData());
                /*****fin de code pour la numérotation des groupes **********/
                new DefaultTreeNode("facette", dynamicTreeNode);
                listeNode.add((MyTreeNode)dynamicTreeNode);
                
            }
            
          
            
        }
        /***ici on trie la liste des groupes d après le champ data***/
             Collections.sort(listeNode,new TreeNodeComparator());
             /*et on l'ajoute au root **/
            for(MyTreeNode mtn :listeNode){
                 MyTreeNode tmp=new MyTreeNode(1,mtn.getIdConcept(),mtn.getIdTheso(),mtn.getLangue(),
                mtn.getIdConcept(),mtn.getTypeDomaine(),mtn.getIdTopConcept(),
                        mtn.getType(),mtn.getData(),root);
                tmp.setPrefix(mtn.getPrefix());
                tmp.setSuffix(mtn.getSuffix());
              
                tmp.setIsGroup(true);
                new DefaultTreeNode(null, tmp);
            }
            /***fin ***/
        if (idTheso != null) {
            loadOrphan(idTheso, langue);
        }

    }

    public void loadOrphan(String idTheso, String langue) {
        String typeNode, value = "";
        /*
        this.typeMot = type;
      this.idMot = id;
      this.idTheso = idT;
      this.langue = l;
      this.idDomaine = idD;
      this.typeDomaine = typeDomaine; 
      this.idTopConcept = idTC;
         */
        TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, null, idTheso, langue, null, null, null, "orphan", langueBean.getMsg("index.orphans"), root);
        orphans = new OrphanHelper().getListOrphanId(connect.getPoolConnexion(), idTheso);
        for (String idC : orphans) {
            if (new ConceptHelper().haveChildren(connect.getPoolConnexion(), idTheso, idC)) {
                typeNode = "dossier";
            } else {
                typeNode = "fichier";
            }
            Term term = new TermHelper().getThisTerm(connect.getPoolConnexion(), idC, idTheso, langue);
            if (term != null) {
                value = term.getLexical_value() + " (id_" + idC + ")";
                Concept temp = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idC, idTheso);
                MyTreeNode mtn = new MyTreeNode(3, idC, idTheso, langue, "Orphan", null, null, typeNode, value, dynamicTreeNode);
                if (typeNode.equals("dossier")) {
                    new DefaultTreeNode("fake", mtn);
                }
            }

        }

    }

    public void majSearchPermute() {
        selectedTerme.majSearchPermute();
        reInit();
        reExpand();
    }

    public void majSearch() {
        selectedTerme.majSearch();
        vue.setOnglet(1);
        reInit();
        reExpand();

    }
    public void majSearch2() {
        selectedTerme.majSearch();
        vue.setOnglet(3);
        
    }

    /**
     * cette fonction permet de déplier le noeud sélectionné "event" est de type
     * TreeNode, donc on a toutes les informations sur le noeud sélectionné
     *
     * @param event
     */
    public void onNodeExpand(NodeExpandEvent event) {

        onNodeExpand(event.getTreeNode());

    }

    public void onNodeExpand(TreeNode treeNode) {

        if (!treeNode.getType().equals("orphan")) {
            ArrayList<NodeConceptTree> listeSubGroup = new ArrayList<>();
            ArrayList<NodeConceptTree> listeConcept;
            ArrayList<String> idGroupList;

            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();
            int type = 3;
            boolean isTopTerm;

            //<Retirer noeuds fictifs>
            if (treeNode.getChildCount() == 1) {
                treeNode.getChildren().remove(0);
            }

            MyTreeNode myTreeNode = (MyTreeNode) treeNode;

            // id du concept ou group sélectionné qu'il faut déployer
            String idSelectedNode = myTreeNode.getIdConcept();

            if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso())) {
                // if (myTreeNode.isIsGroup() || myTreeNode.isIsSubGroup()) { //pour détecter les noeuds type Group/collecton/MT/Thèmes ...
                myTreeNode.setTypeConcept(1);//pour group ?
                //      myTreeNode.setIsGroup(true);

                // on récupère la liste des sous_groupes (s'il y en a)
                listeSubGroup = groupHelper.getRelationGroupOf(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso(), myTreeNode.getLangue());

                if (listeSubGroup == null) {
                    listeSubGroup = new ArrayList<>();
                }
                // pour récupérer les concepts mélangés avec les Sous_Groupes
                listeConcept = conceptHelper.getListTopConcepts(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso(), myTreeNode.getLangue());

            } else {
                listeConcept = conceptHelper.getListConcepts(connect.getPoolConnexion(), idSelectedNode, myTreeNode.getIdTheso(), myTreeNode.getLangue());
                //    myTreeNode.setIsTopConcept(true);
            }

            MyTreeNode treeNode2 = null;
            // 1 = domaine/Group, 2 = TT (top Term), 3 = Concept/term 
            // myTreeNode.isIsGroup() myTreeNode.isIsSubGroup()
            // 
            String value = "";
            String idTC = "";
            String icon;
            /**
             * Ajout des sous_Groupes (MT, C, G, T ..)
             */
            int count=0;//attribut pour la numérotation des sous groupes
            ArrayList<MyTreeNode> listeTreeNode=new ArrayList<>();//attribut pour le trie
            /*la partie de code suivant peut comporter des éléments inutiles**/
            for (NodeConceptTree nodeConceptTreeGroup : listeSubGroup) {
                treeNode2 = null;
                value = nodeConceptTreeGroup.getTitle();
                if (groupHelper.haveSubGroup(connect.getPoolConnexion(), nodeConceptTreeGroup.getIdThesaurus(), nodeConceptTreeGroup.getIdConcept())
                        || nodeConceptTreeGroup.isHaveChildren()) {
                    
                    icon = getTypeOfSubGroup(myTreeNode.getTypeDomaine());

                    treeNode2 = new MyTreeNode(1, nodeConceptTreeGroup.getIdConcept(), ((MyTreeNode) treeNode).getIdTheso(),
                            ((MyTreeNode) treeNode).getLangue(), nodeConceptTreeGroup.getIdConcept(),
                            ((MyTreeNode) treeNode).getTypeDomaine(),
                            idTC, icon, value, null);
                    ((MyTreeNode) treeNode2).setIsSubGroup(true);
                    listeTreeNode.add(treeNode2);
                   
                    ((MyTreeNode) treeNode2).setIdParent(myTreeNode.getIdConcept());
                     /***code poour la numérotation des sous groupes ****/
                    ((MyTreeNode)treeNode2).setPrefix(myTreeNode.getNumerotation());
                    String suffix=groupHelper.getSuffixFromNode(connect.getPoolConnexion(), nodeConceptTreeGroup.getIdThesaurus(), nodeConceptTreeGroup.getIdConcept());
                    count+=5;
                    //a priori par défaut un getInt renvoit 0 si champ vide (cf groupHelper.getSuffixFromNode)
                    if(suffix.equalsIgnoreCase("0") || suffix.equalsIgnoreCase("00")){
                     
                       if(10<=count){suffix=""+count;}else {suffix="0"+count;}
                       
                       groupHelper.saveSuffixFromNode(connect.getPoolConnexion(), nodeConceptTreeGroup.getIdThesaurus(), nodeConceptTreeGroup.getIdConcept(),suffix);
                    }
                    if(suffix.length()<2)suffix="0"+suffix;
                    ((MyTreeNode)treeNode2).setSuffix(suffix);
                    ((MyTreeNode)treeNode2).setData(((MyTreeNode)treeNode2).getNumerotation()+"  "+treeNode2.getData());
                    /**fin code numérotation des sous groupes*****/
                    new DefaultTreeNode(null, treeNode2);
                }
            /**fin de la partie de code pouvant comporter des éléments inutiles*/
            }
            /**afin de classer les sous groupes avec la numérotation on trie
            *la liste de treeNode on est obligé de récréer un noeuds MyTreeNode pour
            * chaque treenode de la liste, pour pouvoir l'accrocher à l'arbre
            * 
            *#jm
             **/
            
            Collections.sort(listeTreeNode,new TreeNodeComparator());
            for(MyTreeNode mtn : listeTreeNode){
                MyTreeNode tmp=new MyTreeNode(1,mtn.getIdConcept(),mtn.getIdTheso(),mtn.getLangue(),
                mtn.getIdConcept(),mtn.getTypeDomaine(),mtn.getIdTopConcept(),
                        mtn.getType(),mtn.getData(),treeNode);
                tmp.setPrefix(mtn.getPrefix());
                tmp.setSuffix(mtn.getSuffix());
                tmp.setIdParent(myTreeNode.getIdConcept());
                tmp.setIsSubGroup(true);
                new DefaultTreeNode(null, tmp);
                
            }
            /***fin du tri et des ajouts des sous groupes dans l'arbre ****/
            // Ajout dans l'arbre des concepts
            for (NodeConceptTree nodeConceptTree : listeConcept) {
                isTopTerm = false;
                treeNode2 = null;
                if (conceptHelper.haveChildren(connect.getPoolConnexion(), nodeConceptTree.getIdThesaurus(), nodeConceptTree.getIdConcept())
                        || nodeConceptTree.isHaveChildren()) {
                    icon = "dossier";

                    if (nodeConceptTree.isIsTopTerm()) { //Création de topConcepts
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        idTC = value;
                        isTopTerm = true;
                    } else { //Création de concepts
                        idTC = ((MyTreeNode) treeNode).getIdTopConcept();
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
                    treeNode2 = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) treeNode).getIdTheso(),
                            ((MyTreeNode) treeNode).getLangue(), ((MyTreeNode) treeNode).getIdCurrentGroup(),
                            ((MyTreeNode) treeNode).getTypeDomaine(),
                            idTC, icon, value, treeNode);
                    if (isTopTerm) {
                        ((MyTreeNode) treeNode2).setIsTopConcept(true);
                    }
                    idGroupList = groupHelper.getListIdGroupOfConcept(connect.getPoolConnexion(),
                            nodeConceptTree.getIdThesaurus(),
                            nodeConceptTree.getIdConcept());
                    ((MyTreeNode) treeNode2).setOtherGroup(idGroupList);
                    ((MyTreeNode) treeNode2).setIdParent(myTreeNode.getIdConcept());
                    new DefaultTreeNode(null, treeNode2);
                } else {
                    icon = "fichier";
                    // if (type == 2) { //Création des topConcepts
                    if (nodeConceptTree.isIsTopTerm()) { // cas de TT
                        //type=2;
                        idTC = nodeConceptTree.getIdConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        isTopTerm = true;

                    } else { //Création de concepts
                        //type=3;
                        idTC = ((MyTreeNode) treeNode).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    treeNode2 = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) treeNode).getIdTheso(),
                            ((MyTreeNode) treeNode).getLangue(), ((MyTreeNode) treeNode).getIdCurrentGroup(),
                            ((MyTreeNode) treeNode).getTypeDomaine(),
                            idTC, icon, value, treeNode);
                    if (isTopTerm) {
                        ((MyTreeNode) treeNode2).setIsTopConcept(true);
                    }
                    idGroupList = groupHelper.getListIdGroupOfConcept(connect.getPoolConnexion(),
                            nodeConceptTree.getIdThesaurus(),
                            nodeConceptTree.getIdConcept());
                    ((MyTreeNode) treeNode2).setOtherGroup(idGroupList);
                }
                ((MyTreeNode) treeNode2).setIdParent(myTreeNode.getIdConcept());
            }
        }

    }

    /**
     *
     * @param event
     */
    public void onNodeSelect(NodeSelectEvent event) {

        //if (((MyTreeNode) event.getTreeNode()).getIdDomaine() != null) {
        selectedTerme.majTerme((MyTreeNode) selectedNode);
        //}
        vue.setOnglet(0);
        selectedTerme.setTree(0);
        // this.parentOrigine=(MyTreeNode)selectedNode.getParent();
        RequestContext.getCurrentInstance().update("principale");
    }

    /**
     * permet de savoir si un groupe ou sous Groupe ont des fils ?
     *
     * @return
     */
    public boolean haveSubGroup() {
        GroupHelper groupHelper = new GroupHelper();
        return groupHelper.haveSubGroup(connect.getPoolConnexion(),
                idThesoSelected, ((MyTreeNode) selectedNode).getIdCurrentGroup());
    }

    /**
     * Permet de mettre à jour l'arbre et le terme à la sélection d'un index
     * rapide par autocomplétion
     */
    public void majIndexRapidSearch() {
        selectedTerme.majIndexRapidSearch(idThesoSelected, defaultLanguage);
        reInit();
        reExpand();
        vue.setOnglet(0);
    }

    public void changeTerme(String id, int type) {

        selectedNode.setSelected(false);

        for (TreeNode node : selectedNodes) {
            node.setSelected(false);
        }

        String idTC;
        if (((MyTreeNode)selectedNode).isIsGroup() || ((MyTreeNode)selectedNode).isIsSubGroup()) {//type == 2) { //On vient d'un domaine
            idTC = id;
        } else {
            idTC = selectedTerme.getIdTopConcept();
        }
                /// ????? à comprendre pourquoi ?????
        if (type == 0) {
            boolean temp = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), id, selectedTerme.getIdTheso()).isTopConcept();
            if (temp) {
                type = 2;
            } else {
                type = 3;
            }
        }

        MyTreeNode mTN = new MyTreeNode(type, id, selectedTerme.getIdTheso(),
                selectedTerme.getIdlangue(), selectedTerme.getIdDomaine(), selectedTerme.getTypeDomaine(), idTC, null, null, null);
        selectedTerme.majTerme(mTN);

        reExpand();
        vue.setOnglet(0);
    }

    public void reExpand() {
        if (selectedNode == null) {
            //      selectedNode = new MyTreeNode(0, "", "", "", "", "", "domaine", "", root);
        }
        //    selectedNode.setSelected(false);
        for (TreeNode tn : selectedNodes) {
            tn.setSelected(false);
        }
        selectedNodes = new ArrayList<>();
        ArrayList<String> first = new ArrayList<>();
        first.add(selectedTerme.getIdC());
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        paths = new ConceptHelper().getPathOfConcept(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), first, paths);
        if (paths != null) {
            reExpandTree(paths, selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        }

    }

    /**
     * Expansion automatique de la racine
     *
     * @param listeId
     * @param idTheso
     * @param langue
     */
    private void reExpandTree(ArrayList<ArrayList<String>> listeId, String idTheso, String langue) {
        //  if(selectedNodes.isEmpty()){

        if (root.getChildCount() == 0) {
            // On recrÃ©e la racine
            List<NodeGroup> racineNode = new GroupHelper().getListRootConceptGroup(connect.getPoolConnexion(), idTheso, langue);
            Collections.sort(racineNode);

            for (NodeGroup nodegroup : racineNode) {
                TreeNode dynamicTreeNode;

                String typeCode = nodegroup.getConceptGroup().getIdtypecode();
                String type = getTypeOfGroup(typeCode);
                // intégration des groupes
                if (nodegroup.getLexicalValue().trim().isEmpty()) {
                    dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdthesaurus(),
                            nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdtypecode(), null,
                            type, nodegroup.getConceptGroup().getIdgroup(), root);
                    ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
                } else {
                    dynamicTreeNode = (TreeNode) new MyTreeNode(1, nodegroup.getConceptGroup().getIdgroup(), nodegroup.getConceptGroup().getIdthesaurus(),
                            nodegroup.getIdLang(), nodegroup.getConceptGroup().getIdgroup(),
                            nodegroup.getConceptGroup().getIdtypecode(), null,
                            type, nodegroup.getLexicalValue(), root);
                    ((MyTreeNode) dynamicTreeNode).setIsGroup(true);
                }
                new DefaultTreeNode("fake", dynamicTreeNode);

                for (ArrayList<String> tabId : listeId) {
                    // Si c'est le chemin, on Ã©tend
                    if (tabId.size() > 1 && tabId.get(0) != null) {
                        if (tabId.get(0).equals(nodegroup.getConceptGroup().getIdgroup())) {
                            reExpandChild(tabId, (MyTreeNode) dynamicTreeNode, 1);
                        }
                    } else {
                        if (tabId.get(1).equals(nodegroup.getConceptGroup().getIdgroup())) {
                            dynamicTreeNode.setSelected(true);
                            selectedNode = dynamicTreeNode;
                            selectedNodes.add(dynamicTreeNode);
                        } else {
                            dynamicTreeNode.setSelected(false);
                        }
                    }
                }
            }
            loadOrphan(idTheso, langue);
            //loadOrphan(idTheso, langue);
            for (TreeNode tn : root.getChildren()) {
                if (tn.getType().equals("orphan")) {
                    for (TreeNode tn2 : tn.getChildren()) {
                        for (ArrayList<String> tabId : listeId) {
                            if (tabId.size() == 2 && tabId.get(1).equals(((MyTreeNode) tn2).getIdConcept())) {
                                tn2.setSelected(true);
                                selectedNode = tn2;
                                selectedNodes.add(tn2);

                            } else {
                                if (tabId.get(1).equals(((MyTreeNode) tn2).getIdConcept())) {
                                    reExpandChild(tabId, (MyTreeNode) tn2, 2);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            List<TreeNode> racineNode = root.getChildren();
            for (TreeNode dynamicTreeNode : racineNode) {
                if (!dynamicTreeNode.getType().equals("orphan")) {
                    for (ArrayList<String> tabId : listeId) {
                        // Si c'est le , on Ã©tend
                        if (tabId.size() > 1 && tabId.get(0) != null) {
                            if (tabId.get(0).equals(((MyTreeNode) dynamicTreeNode).getIdCurrentGroup())) {
                                reExpandChild(tabId, (MyTreeNode) dynamicTreeNode, 1);
                            }
                        } else {
                            if (tabId.get(1).equals(((MyTreeNode) dynamicTreeNode).getIdCurrentGroup())) {
                                dynamicTreeNode.setSelected(true);
                                selectedNode = dynamicTreeNode;
                                selectedNodes.add(dynamicTreeNode);
                            } else {
                                dynamicTreeNode.setSelected(false);
                            }
                        }
                    }
                } else {
                    for (TreeNode tn2 : dynamicTreeNode.getChildren()) {
                        for (ArrayList<String> tabId : listeId) {
                            if (tabId.size() == 2 && tabId.get(1).equals(((MyTreeNode) tn2).getIdConcept())) {
                                tn2.setSelected(true);
                                selectedNode = tn2;
                                selectedNodes.add(tn2);

                            } else {
                                if (tabId.get(1).equals(((MyTreeNode) tn2).getIdConcept())) {
                                    reExpandChild(tabId, (MyTreeNode) tn2, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void reExpandChild(ArrayList<String> listeId, TreeNode node, int cpt) {
        if (!node.isExpanded()) {
            ArrayList<NodeConceptTree> listeConcept;
            ArrayList<NodeConceptTree> listeSubGroup = new ArrayList<>();
            ArrayList<String> idGroupList;
            ConceptHelper conceptHelper = new ConceptHelper();
            GroupHelper groupHelper = new GroupHelper();
            int type = 3;

            if (node.getChildCount() == 1) {
                node.getChildren().remove(0);
            }

            MyTreeNode myTreeNode = (MyTreeNode) node;
            String idConcept = myTreeNode.getIdConcept();
            if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso())) {

                myTreeNode.setTypeConcept(1);//pour group ?
                myTreeNode.setIsGroup(true);

                listeSubGroup = groupHelper.getRelationGroupOf(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());

                if (listeSubGroup == null) {
                    listeSubGroup = new ArrayList<>();
                }

                // pour récupérer les concepts mélangés avec les Sous_Groupes
                listeConcept = conceptHelper.getListTopConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());

            } else {
                listeConcept = conceptHelper.getListConcepts(connect.getPoolConnexion(), idConcept, myTreeNode.getIdTheso(), myTreeNode.getLangue());
                //        myTreeNode.setIsTopConcept(true);
            }

            TreeNode treeNode = null;
            String value = "";
            String idTC = "";
            String icon;
            boolean isTopTerm;
            /**
             * Ajout des Groupes (MT, C, G, T ..)
             */
            for (NodeConceptTree nodeConceptTreeGroup : listeSubGroup) {

                value = nodeConceptTreeGroup.getTitle();
                if (groupHelper.haveSubGroup(connect.getPoolConnexion(), nodeConceptTreeGroup.getIdThesaurus(), nodeConceptTreeGroup.getIdConcept())
                        || nodeConceptTreeGroup.isHaveChildren()) {

                    icon = getTypeOfSubGroup(myTreeNode.getTypeDomaine());

                    treeNode = new MyTreeNode(1, nodeConceptTreeGroup.getIdConcept(), myTreeNode.getIdTheso(),
                            myTreeNode.getLangue(), myTreeNode.getIdCurrentGroup(),
                            myTreeNode.getTypeDomaine(),
                            idTC, icon, value, myTreeNode);
                    ((MyTreeNode) treeNode).setIsSubGroup(true);
                    ((MyTreeNode) treeNode).setIdCurrentGroup(nodeConceptTreeGroup.getIdConcept());
                    new DefaultTreeNode("fake", treeNode);
                    if (listeId.get(cpt).equals(((MyTreeNode) treeNode).getIdConcept())) {
                        if (cpt + 1 < listeId.size()) {
                            treeNode.setSelected(false);
                            reExpandChild(listeId, treeNode, cpt + 1);
                        } else {
                            treeNode.setSelected(true);
                            selectedNode = treeNode;
                            selectedNodes.add(treeNode);
                        }
                    }

                }
            }

            // Ajout dans l'arbre
            for (NodeConceptTree nodeConceptTree : listeConcept) {
                isTopTerm = false;
                if (conceptHelper.haveChildren(connect.getPoolConnexion(), nodeConceptTree.getIdThesaurus(), nodeConceptTree.getIdConcept())
                        || nodeConceptTree.isHaveChildren()) {
                    icon = "dossier";
                    if (nodeConceptTree.isIsGroup()) {

                        icon = "domaine";
                        //String type = getTypeOfGroup(typeCode);

                    } else if (nodeConceptTree.isIsSubGroup()) {
                        icon = getTypeOfSubGroup(myTreeNode.getTypeDomaine());
                    }

                    if (nodeConceptTree.isIsTopTerm()) { //CrÃ©ation de topConcepts
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        idTC = value;
                        isTopTerm = true;
                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) node).getIdTopConcept();
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
                    treeNode = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) node).getIdTheso(),
                            ((MyTreeNode) node).getLangue(), ((MyTreeNode) node).getIdCurrentGroup(),
                            ((MyTreeNode) node).getTypeDomaine(),
                            idTC, icon, value, node);
                    if (isTopTerm) {
                        ((MyTreeNode) treeNode).setIsTopConcept(true);
                    }
                    idGroupList = groupHelper.getListIdGroupOfConcept(connect.getPoolConnexion(),
                            nodeConceptTree.getIdThesaurus(),
                            nodeConceptTree.getIdConcept());
                    ((MyTreeNode) treeNode).setOtherGroup(idGroupList);
                    new DefaultTreeNode("fake", treeNode);

                    if (listeId.get(cpt).equals(((MyTreeNode) treeNode).getIdConcept())) {
                        if (cpt + 1 < listeId.size()) {
                            treeNode.setSelected(false);
                            reExpandChild(listeId, treeNode, cpt + 1);
                        } else {
                            treeNode.setSelected(true);
                            selectedNode = treeNode;
                            selectedNodes.add(treeNode);
                        }
                    }
                } else {
                    icon = "fichier";
                    if (nodeConceptTree.isIsTopTerm()) { //CrÃ©ation de topConcepts
                        idTC = nodeConceptTree.getIdConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                        isTopTerm = true;

                    } else { //CrÃ©ation de concepts
                        idTC = ((MyTreeNode) node).getIdTopConcept();
                        if (nodeConceptTree.getTitle().trim().isEmpty()) {
                            value = nodeConceptTree.getIdConcept();
                        } else {
                            value = nodeConceptTree.getTitle();
                        }
                    }
                    if (nodeConceptTree.getStatusConcept().equals("hidden")) {
                        icon = "hidden";
                    }
                    treeNode = new MyTreeNode(type, nodeConceptTree.getIdConcept(), ((MyTreeNode) node).getIdTheso(),
                            ((MyTreeNode) node).getLangue(), ((MyTreeNode) node).getIdCurrentGroup(),
                            ((MyTreeNode) node).getTypeDomaine(),
                            idTC, icon, value, node);
                    if (isTopTerm) {
                        ((MyTreeNode) treeNode).setIsTopConcept(true);
                    }

                    idGroupList = groupHelper.getListIdGroupOfConcept(connect.getPoolConnexion(),
                            nodeConceptTree.getIdThesaurus(),
                            nodeConceptTree.getIdConcept());
                    ((MyTreeNode) treeNode).setOtherGroup(idGroupList);

                    if (listeId.get(cpt).equals(((MyTreeNode) treeNode).getIdConcept())) {
                        treeNode.setSelected(true);
                        selectedNode = treeNode;
                        selectedNodes.add(treeNode);
                    } else {
                        treeNode.setSelected(false);
                    }

                }
            }
            node.setExpanded(true);
        } else {
            List<TreeNode> children = node.getChildren();
            for (TreeNode mtn : children) {
                if (listeId.get(cpt).equals(((MyTreeNode) mtn).getIdConcept())) {
                    if (cpt + 1 < listeId.size()) {
                        mtn.setSelected(false);
                        reExpandChild(listeId, mtn, cpt + 1);
                    } else {
                        mtn.setSelected(true);
                        selectedNode = mtn;
                        selectedNodes.add(mtn);
                    }
                }
            }
        }
    }

    public void reInit() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
    }



    /**
     * ************************** ACTIONS SELECTEDTERME
     * ***************************
     */
    /**
     * Supprime le groupe sélectionné
     */
    public void delGroup() {
        new GroupHelper().deleteConceptGroup(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), selectedTerme.getUser().getUser().getId());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info7")));
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.reInitTerme();
    }

    /**
     * Cette fonction permet de supprimer le ou les orphelins (une branche à
     * partir du concept orphelin sélectionné
     *
     * @return
     */
    public boolean delOrphans() {
        if (!deleteOrphanBranch(connect.getPoolConnexion(),
                selectedTerme.getIdC(), selectedTerme.getIdTheso(), selectedTerme.getUser().getUser().getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            return false;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info8")));
        conceptbean.setDeleteBranchOrphan(0);
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.reInitTerme();
        return true;
    }

    public boolean renameGroup() {
        MyTreeNode myTreeNode = (MyTreeNode) selectedNode;

        String valueEdit = selectedTerme.getNomEdit().trim();
        if (valueEdit.isEmpty()) {
            return false;
        }

        // si c'est la même valeur, on fait rien
        if (selectedTerme.getNom().trim().equals(valueEdit)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error2")));
            //    selectedTerme.setNomEdit(selectedTerme.getNom());
            return false;
        }

        // vérification si le Groupe à ajouter existe déjà 
        if (new GroupHelper().isDomainExist(connect.getPoolConnexion(), valueEdit, myTreeNode.getIdTheso(), myTreeNode.getLangue())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error6")));
            return false;
        }

        if (myTreeNode.isIsGroup() || myTreeNode.isIsSubGroup()) {
            if (!selectedTerme.editGroupName(myTreeNode.getIdTheso(),
                    myTreeNode.getIdConcept(), myTreeNode.getLangue(),
                    valueEdit)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "erreur BDD"));
                //    selectedTerme.setNomEdit(selectedTerme.getNom());
                return false;
            }
            myTreeNode.setData(valueEdit);
            selectedTerme.setNom(valueEdit);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", valueEdit + " " + langueBean.getMsg("tree.info2")));
            return true;
        }
        return false;
    }
    public void changeGroupType(String type){
       MyTreeNode myTN=(MyTreeNode)this.selectedNode;
       if(!myTN.isIsGroup() && !myTN.isIsSubGroup()){
           //normalement impossible
           return;
       }
       if(myTN.getTypeDomaine().equals(type)){
           return;
       }
       else{
           myTN.setTypeDomaine(type);
           GroupHelper gh=new GroupHelper();
           gh.updateTypeGroup(this.connect.getPoolConnexion(), type, idThesoSelected,myTN.getIdConcept());
           ConceptHelper ch=new ConceptHelper();
           this.onNodeExpand(myTN);
           for(TreeNode sbn: myTN.getChildren()){
               if(((MyTreeNode)sbn).isIsSubGroup())changeGroupType(type,sbn);
               
           }
       }
       reInit();
     this.initTree(idThesoSelected, myTN.getLangue());

      reExpand();

    }
     public void changeGroupType(String type, TreeNode sbn ){
       
         
        ((MyTreeNode)sbn).setTypeDomaine(type);
        GroupHelper gh=new GroupHelper();
        gh.updateTypeGroup(this.connect.getPoolConnexion(), type, idThesoSelected,((MyTreeNode)sbn).getIdConcept());
        this.onNodeExpand(sbn);
           for(TreeNode sn: sbn.getChildren()){
               if(((MyTreeNode)sn).isIsSubGroup())changeGroupType(type,sn);
               
           }
     }

    /**
     * Permet de supprimer un concept seul, il ne faut pas qu'il est des fils,
     * sinon la suppression va échouer
     *
     * @return
     */
    public boolean deleteConcept() {

        ConceptHelper conceptHelper = new ConceptHelper();
        if (selectedTerme.getUser().nodePreference == null) {
            return false;
        }
        conceptHelper.setNodePreference(selectedTerme.getUser().nodePreference);
        if (!conceptHelper.deleteConcept(connect.getPoolConnexion(), selectedTerme.getIdC(),
                selectedTerme.getIdTheso(), selectedTerme.getUser().getUser().getId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "La suppression a échoué !!"));
            return false;
        }

        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.reInitTerme();
        selectedTerme.setSelectedNode((MyTreeNode) selectedNode.getParent());
        selectedTerme.majTerme((MyTreeNode) selectedNode.getParent());
        selectedNode = (MyTreeNode) selectedNode.getParent();
        reExpand();
        return true;
    }

    /**
     * Fonction recursive qui permet de supprimer une branche d'orphelins un
     * concept de tête et thesaurus. La suppression est descendante qui ne
     * supprime pas les autres branches remontantes.
     *
     * @param conn
     * @param idConcept
     * @param idTheso
     * @return
     */
    private boolean deleteOrphanBranch(HikariDataSource ds, String idConcept, String idTheso, int idUser) {

        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList<String> listIdsOfConceptChildren
                = conceptHelper.getListChildrenOfConcept(ds,
                        idConcept, idTheso);

        if (!conceptHelper.deleteConceptForced(ds, idConcept, idTheso, idUser)) {
            return false;
        }

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
            //     if(!conceptHelper.deleteConceptForced(ds, listIdsOfConceptChildren1, idTheso, idUser))
            //        return false;
            deleteOrphanBranch(ds, listIdsOfConceptChildren1, idTheso, idUser);
        }
        return true;
    }

    /**
     * Change le nom du terme courant avec mise à jour dans l'arbre Choix du
     * type de d'objet sélectionné (Group, sousGroup, Concept)
     *
     */
    public void editNomT() {
        duplicate = false;
        if (selectedTerme == null) {
            return;
        }
        /*        if (selectedTerme.getSelectedTermComp() == null) {
            return;
        }
        selectedTerme.setNomEdit(selectedTerme.getSelectedTermComp().getTermLexicalValue());        
         */
        // si c'est la même valeur, on fait rien
        if (selectedTerme.getNom().trim().equals(selectedTerme.getNomEdit())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error2")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
            return;
        }

        // saisie d'une valeur vide
        if (selectedTerme.getNomEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error1")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
            return;
        }

        // si le terme existe, il faut proposer le choix de valider ou non !!
        if (!forced) { // ici l'utilisateur a accepté de valider un doublon, donc on ne controle plus le terme 
            if (new TermHelper().isTermExistForEdit(connect.getPoolConnexion(),
                    selectedTerme.getNomEdit().trim(),
                    idThesoSelected, selectedTerme.getIdT(), selectedTerme.getIdlangue())) {
                // traitement des doublons
                duplicate = true;
                editPassed = true;
                return;
            }
        }

        if (selectedTerme.getIdT() != null && !selectedTerme.getIdT().equals("")) {
            selectedTerme.editTerme(1);
        } else {
            // le terme n'existe pas encore
            if (!selectedTerme.editTerme(2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                selectedTerme.setNomEdit(selectedTerme.getNom());
                return;
            }
        }

        if (selectedNode != null) {
            //((MyTreeNode) selectedNode).setData(temp + " (Id_" + selectedTerme.getIdC() + ")");
            ((MyTreeNode) selectedNode).setData(selectedTerme.getNom());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", selectedTerme.getNom() + " " + langueBean.getMsg("tree.info2")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
        }

        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
        forced = false;
        duplicate = false;
        editPassed = true;
    }

    /**
     * Fonction qui permet de modifier le nom d'un concept avec un autre en
     * doublon (action autorisée après validation de l'utilisateur)
     */
    public void renameWithoutControl() {
        if (selectedTerme.getIdT() != null && !selectedTerme.getIdT().equals("")) {
            selectedTerme.editTerme(1);
        } else {
            // le terme n'existe pas encore
            if (!selectedTerme.editTerme(2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                selectedTerme.setNomEdit(selectedTerme.getNom());
                return;
            }
        }

        if (selectedNode != null) {
            //((MyTreeNode) selectedNode).setData(temp + " (Id_" + selectedTerme.getIdC() + ")");
            ((MyTreeNode) selectedNode).setData(selectedTerme.getNom());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", selectedTerme.getNom() + " " + langueBean.getMsg("tree.info2")));
            selectedTerme.setNomEdit(selectedTerme.getNom());
        }

        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
    }

    /**
     * permet de savoir si le neoud sélectionné est un Group
     *
     * @return
     */
    public boolean isGroup() {
        if (selectedNode == null) {
            return false;
        }
        return ((MyTreeNode) selectedNode).isIsGroup();
    }

    /**
     * permet de savoir si le noeud sélectionné est un sousGroupe
     *
     * @return
     */
    public boolean isSubGroup() {
        if (selectedNode == null) {
            return false;
        }
        return ((MyTreeNode) selectedNode).isIsSubGroup();
    }
    
    /**
     * permet de savoir si le noeud sélectionné est un TopTerme
     *
     * @return
     */
    public boolean isTopTerm() {
        if (selectedNode == null) {
            return false;
        }
        return ((MyTreeNode) selectedNode).isIsTopConcept();
    }    

    /**
     * Supprime la relation hiÃ©rarchique qui lie le terme courant au terme dont
     * l'id est passÃ© en paramÃ¨tre puis met l'arbre Ã  jour. Si type vaut 0,
     * le terme courant est le fils, si type vaut 1, le terme courant est le
     * pÃ¨re.
     *
     * @param id
     * @param type
     */
    public void suppRel(String id, int type) {
        if (type == 0) {
            // type 0 = suppression de la relation gÃ©nÃ©rique 
            if (!selectedTerme.delGene(id)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return;
            }

        } else {
            // type 1 = suppression de la relation spÃ©cifique
            if (!selectedTerme.delSpe(id)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
                return;
            }
        }

        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info3")));
    }

    public boolean desactivateConcept() {
        if (!selectedTerme.deprecateConcept()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error3")));
            return false;
        }
        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info4")));
        vue.setAddTInfo(0);
        return true;

    }

    public boolean getConceptForFusion() {
        if (selectedTerme.getSelectedTermComp() == null || !selectedTerme.loadConceptFusion()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error5")));
            vue.setAddTInfo(0);
            return false;
        }
        vue.setAddTInfo(3);
        return true;
    }

    public void initConceptFusion() {
        selectedTerme.initConceptFusion();
    }

    /**
     * Fusionne les concepts avec mise à  jour dans l'abre
     */
    public void fusionConcept() {
        if (selectedTerme.getConceptFusionId().equals(selectedTerme.getIdC())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), langueBean.getMsg("error")));
            selectedTerme.setConceptFusionId(null);
            selectedTerme.setConceptFusionAlign(null);
            selectedTerme.setConceptFusionNodeRT(null);
        } else {
            int idUser = selectedTerme.getUser().getUser().getId();
            for (NodeRT rt : selectedTerme.getConceptFusionNodeRT()) {
                HierarchicalRelationship hr = new HierarchicalRelationship(rt.getIdConcept(), selectedTerme.getConceptFusionId(), selectedTerme.getIdTheso(), "RT");
                new ConceptHelper().addAssociativeRelation(connect.getPoolConnexion(), hr, idUser);
            }
            for (NodeAlignment na : selectedTerme.getConceptFusionAlign()) {
                new AlignmentHelper().addNewAlignment(connect.getPoolConnexion(),
                        idUser, na.getConcept_target(), na.getThesaurus_target(), na.getUri_target(), na.getAlignement_id_type(),
                        selectedTerme.getConceptFusionId(), selectedTerme.getIdTheso(), 0);
            }
            new ConceptHelper().addConceptFusion(connect.getPoolConnexion(), selectedTerme.getConceptFusionId(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), idUser);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info6")));
            reInit();
            reExpand();

        }
        selectedTerme.setSelectedTermComp(new NodeAutoCompletion());
        vue.setAddTInfo(0);
    }

    /*       if (selectedTerme.getChoixdesactive().equals("0")) {
            if (!selectedTerme.delConcept()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error3")));
                return false;
            }
            reInit();
            reExpand();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info4")));
            vue.setAddTInfo(0);
            return true;
        } else if (selectedTerme.getChoixdesactive().equals("1")) {
            if (selectedTerme.getSelectedTermComp() == null || !selectedTerme.loadConceptFusion()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error5")));
                vue.setAddTInfo(0);
                return false;
            }
            vue.setAddTInfo(3);
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error6")));
            vue.setAddTInfo(0);
            return false;
        }*/
    public boolean reactivConcept() {
        if (!selectedTerme.reactivConcept()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.error4")));
            return false;
        }
        reInit();
        reExpand();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info5")));
        return true;
    }

    /**
     * *
     * Nouvelles fonctions par Miled Rousset
     */
    /**
     * Permet de modifier la valeur de la notation d'un concept
     */
    public void editNotation() {
        if (selectedTerme == null) {
            return;
        }
        if (selectedTerme.getIdT() != null && !selectedTerme.getIdT().equals("")) {

            if (!selectedTerme.updateNotation()) {
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("tree.info2")));
        }
    }

    /**
     * ***********************fin des nouvelles
     * fonctions*************************
     */
    /**
     * onCollapse #JM
     *
     * @param event
     */
    public void onNodeCollapse(NodeCollapseEvent event) {
        ((MyTreeNode) event.getTreeNode()).setExpanded(false);

    }

    /**
     * *******************fonctions drag drop #JM*****************************
     */
    /**
     * ***********************************************************************
     */
    /**
     * Fonction pour récupérer l'éveneme drag drop de l'arbre
     *
     * @param event
     */
    public void onDragDrop(TreeDragDropEvent event) {
        MyTreeNode dragNode = (MyTreeNode) event.getDragNode();
        MyTreeNode dropNode = (MyTreeNode) event.getDropNode();
        idsBT = new ArrayList<>();
        idsBTRemoveNode = new ArrayList<>();
        TreeChange treeChange = new TreeChange();
        //int dropIndex = event.getDropIndex();
        this.draggedNode = dragNode;
        this.parentId = dragNode.getIdParent();
        this.droppedNode = dropNode;

        ArrayList<String> idsbt = null;
        RelationsHelper relationsHelper = new RelationsHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        GroupHelper groupHelper = new GroupHelper();
        if (dragNode.isIsTopConcept()) {
            idsbt = dragNode.getOtherGroup();
        } else {
            idsbt = relationsHelper.getListIdOfBT(connect.getPoolConnexion(), dragNode.getIdConcept(), idThesoSelected);
        }
        ArrayList<StructIdBroaderTerm> sibt = new ArrayList<>();
        for (String id : idsbt) {
            StructIdBroaderTerm elem = new StructIdBroaderTerm();

            String idLang = (selectedTerme.getIdlangue().isEmpty()) ? defaultLanguage : selectedTerme.getIdlangue();
            String idGroup = conceptHelper.getGroupIdOfConcept(connect.getPoolConnexion(), id, idThesoSelected);
            String lexicalValue = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id, idThesoSelected, idLang);
            String lexicalValueGroup = groupHelper.getLexicalValueOfGroup(connect.getPoolConnexion(), idGroup, idThesoSelected, idLang);

            elem.setIdBroaderTerm(id);
            elem.setIdGroupBroaderTerm(idGroup);
            elem.setLexicalValueTerm(lexicalValue);
            elem.setLexicalValueGroup(lexicalValueGroup);

            sibt.add(elem);
        }
        idsBT.addAll(sibt);

        if ("fichier".equals(event.getDropNode().getType())) {
            treeChange.changeLeafToDirectory(event.getDragNode(), event.getDropNode());
        }

        if (!"fichier".equals(event.getDropNode().getType()) && !"dossier".equals(event.getDropNode().getType())) {
            treeChange.changeToGroupChild(event.getDragNode(), event.getDropNode());
        }

        treeChange.saveExpandedNodes(root);

    }

    /**
     * confrimDrop Fonction qui est appelée depuis le dialogue de confirmation
     * du drag drop de la page JSF , permet de préparer le traitement des
     * paramètres et appelle ensuite la fonction qui correspond à l'évenement
     * produit dans le tree
     */
    public void confirmDrop() {

       
        if ((droppedNode.isIsSubGroup() || droppedNode.isIsGroup()) && (!draggedNode.isIsSubGroup())) {
            for (StructIdBroaderTerm elem : this.idsBT) {
                this.idsBTRemoveNode.add(elem.getIdBroaderTerm());
            }
        }else{
          this.idsBTRemoveNode.add(parentId);
        }
        callTreeHandle();
        callreExpande();
        idsBT = new ArrayList<>();
        idsBTRemoveNode = new ArrayList<>();
    }

    /**
     * callTreeHandle méthode pour appeler la bonne méthode suivant les noeuds
     * parents et enfants
     */
    public void callTreeHandle() {
        if (droppedNode.getIdCurrentGroup() == draggedNode.getIdCurrentGroup()
                && draggedNode.isIsTopConcept() && !droppedNode.isIsGroup() && !droppedNode.isIsSubGroup() && !droppedNode.isIsTopConcept()) {
            fromTTToConceptDomain();
        }
        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && draggedNode.isIsTopConcept() && !droppedNode.isIsGroup() && !droppedNode.isIsSubGroup() && !droppedNode.isIsTopConcept()) {
            fromTTToConceptOtherDomain();
        }

        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && draggedNode.isIsTopConcept() && droppedNode.isIsSubGroup()) {
            fromTTToSubGroupOtherDomain();
        }

        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && draggedNode.isIsTopConcept() && droppedNode.isIsGroup()) {
            fromTTToGroupOtherDomain();
        }

        if (droppedNode.getIdCurrentGroup() == draggedNode.getIdCurrentGroup()
                && droppedNode.isIsTopConcept() && !draggedNode.isIsGroup() && !draggedNode.isIsSubGroup() && !draggedNode.isIsTopConcept()) {
            fromConceptToTTDomain();
        }
        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && droppedNode.isIsTopConcept() && !draggedNode.isIsGroup() && !draggedNode.isIsSubGroup() && !draggedNode.isIsTopConcept()) {
            fromConceptToTTOtherDomain();
        }
        if (droppedNode.getIdCurrentGroup() == draggedNode.getIdCurrentGroup()
                && droppedNode.isIsSubGroup() && !draggedNode.isIsGroup() && !draggedNode.isIsSubGroup() && !draggedNode.isIsTopConcept()) {
            fromConceptToSubGroupDomain();
        }
        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && droppedNode.isIsSubGroup() && !draggedNode.isIsGroup() && !draggedNode.isIsSubGroup() && !draggedNode.isIsTopConcept()) {
            fromConceptToSubGroupOtherDomain();
        }
        if (droppedNode.getIdCurrentGroup() == draggedNode.getIdCurrentGroup()
                && droppedNode.isIsGroup() && !draggedNode.isIsGroup() && !draggedNode.isIsSubGroup() && !draggedNode.isIsTopConcept()) {
            fromConceptToGroupDomain();
        }
        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && droppedNode.isIsGroup() && !draggedNode.isIsGroup() && !draggedNode.isIsSubGroup() && !draggedNode.isIsTopConcept()) {
            fromConceptToGroupOtherDomain();
        }

        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && draggedNode.isIsSubGroup() && droppedNode.isIsGroup()) {
            fromSubGroupToGroupOtherDomain();
        }

        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && droppedNode.isIsSubGroup() && draggedNode.isIsSubGroup()) {
            fromSubGoupToSubGroupOtherDomain();
        }

        if (droppedNode.getIdCurrentGroup() == draggedNode.getIdCurrentGroup()
                && droppedNode.isIsTopConcept() && draggedNode.isIsTopConcept()) {
            fromTTToTT();
        }
        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && droppedNode.isIsTopConcept() && draggedNode.isIsTopConcept()) {
            fromTTToTTOtherDomain();
        }
        if (droppedNode.getIdCurrentGroup() == draggedNode.getIdCurrentGroup()
                && !droppedNode.isIsTopConcept() && !droppedNode.isIsSubGroup() && !droppedNode.isIsGroup()
                && !draggedNode.isIsTopConcept() && !draggedNode.isIsSubGroup() && !droppedNode.isIsGroup()) {
            fromConceptToConcept();
        }
        if (droppedNode.getIdCurrentGroup() != draggedNode.getIdCurrentGroup()
                && !droppedNode.isIsTopConcept() && !droppedNode.isIsSubGroup() && !droppedNode.isIsGroup()
                && !draggedNode.isIsTopConcept() && !draggedNode.isIsSubGroup() && !droppedNode.isIsGroup()) {
            fromConceptToConceptOtherDomain();
        }
    }

    /*Fonction qui doivent gérer les déplacement des noeuds vis à vis de la bdd**/
    /**
     * déplacement d'un groupe: ce déplacement n'est pas prévu pour le moment *
     */
    /**
     * ****************déplacement d'un sous groupe********************
     */
    /**
     * fromSubGroupToGroupOtherDomain déplace un sous groupe vers un groupe d'un
     * autre domaine
     */
    public void fromSubGroupToGroupOtherDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveSubGroupToSubGroupDomain(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), draggedNode.getType(), droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

       // System.out.println("from subgroup to group other domain");
    }

    /**
     * fromSubGroupToSubGroupOtherDomain() déplace un sous groupe vers un autre
     * sous groupe d'un autre domaine
     */
    public void fromSubGoupToSubGroupOtherDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveSubGroupToSubGroupDomain(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), draggedNode.getType(), droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

       // System.out.println("from sub grou to subgroup other domain");
    }

    /* public void fromSubGroupToConceptOtherDomain(){
        System.out.println(" from sub group to  concept other  domain"); 
    }
    public void fromSubGroupToConceptDomain(){
         System.out.println(" from sub group to  concept   domain");
    }*/
    /**
     * ******************déplacement d'un top term************************* **
     */
    public void fromTTToGroupOtherDomain() {
       
            TreeChange tc = new TreeChange();
            tc.moveTopTermToOtherDomaine(connect, draggedNode.getIdConcept(), draggedNode.getIdCurrentGroup(), droppedNode.getIdCurrentGroup(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
       
       // System.out.println("from top term to group other domain");
    }

    /**
     * fromTTToSubGroupOtherDomain déplace un top terme vers un sous groupe d'un
     * autre domaine
     */
    public void fromTTToSubGroupOtherDomain() {
          TreeChange tc = new TreeChange();
            tc.moveTopTermToOtherDomaine(connect, draggedNode.getIdConcept(), draggedNode.getIdCurrentGroup(), droppedNode.getIdCurrentGroup(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
       
        //System.out.println("from top term to sub group other domain");
    }

    /**
     * fromTTTOTTTOtherDomain déplace un top terme vers un top terme d'un autre
     * domaine
     */
    public void fromTTToTTOtherDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveTopTermToConceptOtherDomaine(connect, draggedNode.getIdConcept(),
                    draggedNode.getIdCurrentGroup(), idBT, droppedNode.getIdConcept(),
                    droppedNode.getIdCurrentGroup(), idThesoSelected,
                    this.selectedTerme.getUser().getUser().getId());
        
        }

       // System.out.println("from to top term to top term other domain");
    }

    /**
     * fromTTToTT() déplace un top terme dans un autre top terme
     *
     */
    public void fromTTToTT() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveTopTermToConceptSameDomaine(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
          
        }

       // System.out.println("from top term to top term domain");
    }

    /**
     * fromTTToConceptDomain déplace un top terme vers un concept du même
     * domaine
     */
    public void fromTTToConceptDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveTopTermToConceptSameDomaine(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
           
        }
      //  System.out.println(" from top term to concept  domain");
    }

    /**
     * fromTTToConceptOtherDomain déplace un top terme vers une branche dans un
     * autre domaine
     */
    public void fromTTToConceptOtherDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
      tc.moveTopTermToConceptOtherDomaine(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), droppedNode.getIdConcept(),droppedNode.getIdCurrentGroup(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
           
        }
        //System.out.println(" from top term to concept  other domain");
    }

    /**
     * *****************déplacement d'un concept************************** **
     */
    /**
     * formConceptToGroupOtherDomain
     *
     * déplacement d'un concept vers un goupe d'un autre domaine
     *
     */
    public void fromConceptToGroupOtherDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveConceptToGroupOtherDomain(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), droppedNode.getIdConcept(), droppedNode.getIdCurrentGroup(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

       // System.out.println(" from concept to group other domain");
    }

    public void fromConceptToGroupDomain() {
           for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.momveConceptToGroupSameDomain(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

       // System.out.println(" from concept to group  domain");
    }

    /**
     * fromConceptToSubGroupOtherDomain déplacement d'un concept vers un sous
     * groupe d'un autre domaine
     */
    public void fromConceptToSubGroupOtherDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveConceptToGroupOtherDomain(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), droppedNode.getIdConcept(), droppedNode.getIdCurrentGroup(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

       // System.out.println(" from concept  to  sub group other domain   domain");
    }

    /**
     * fromConceptToSubGroupDomain
     *
     * déplace un concept dnas un sous groupe
     */
    public void fromConceptToSubGroupDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.momveConceptToGroupSameDomain(connect, draggedNode.getIdConcept(), idBT, draggedNode.getIdCurrentGroup(), droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

       // System.out.println(" from   concept to sub group domain");
    }

    /**
     * fromConceptToTTOtherDomain déplace un concept vers un top terme d'un
     * autre domaine
     */
    public void fromConceptToTTOtherDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveConceptTermToConceptTermOtherDomain(connect, draggedNode.getIdConcept(), draggedNode.getIdCurrentGroup(), idBT, droppedNode.getIdConcept(), droppedNode.getIdCurrentGroup(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

        //System.out.println(" from concept m to top term other  domain");
    }

    /**
     * fromConceptToTTDomain déplace un concept vers un toipo terme du même
     * domaine
     */
    public void fromConceptToTTDomain() {
        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveConceptTermToConceptTermSameDomain(connect,
                    draggedNode.getIdConcept(), idBT, droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());

        }
       // System.out.println(" from concept m to top term  domain");
    }

    /**
     * fromConceptToConcept déplace un concept vers un autre concept dans le
     * même domaine
     */
    public void fromConceptToConcept() {

        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveConceptTermToConceptTermSameDomain(connect,
                    draggedNode.getIdConcept(), idBT, droppedNode.getIdConcept(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());

        }
       // System.out.println("from concept to concept domain");
    }

    /**
     * fromConceptToConceptOtherDomain déplacement d'un concept vers un concept
     * d'un autre domaine
     */
    public void fromConceptToConceptOtherDomain() {

        for (String idBT : idsBTRemoveNode) {
            TreeChange tc = new TreeChange();
            tc.moveConceptTermToConceptTermOtherDomain(connect, draggedNode.getIdConcept(),
                    draggedNode.getIdCurrentGroup(), idBT, droppedNode.getIdConcept(),
                    droppedNode.getIdCurrentGroup(), idThesoSelected, this.selectedTerme.getUser().getUser().getId());
        }

       // System.out.println("from concept to concept other domain");
    }

    /**
     * fin des fonctions qui doivent gérer le déplacement d'un noeuds vis à vis
     * de la bdd
     */
    /**
     * infirmDrop Méthode qui est appelée depuis la boîte de confirmation du
     * drag and drop en cas d'annulation des modifications
     */
    public void infirmDrop() {

        callreExpande();
    }

    private void callreExpande() {

        TreeChange tc = new TreeChange();
        tc.saveExpandedNodes(root);
        if (selectedTerme.getIdlangue().isEmpty()) {
            initTree(idThesoSelected, defaultLanguage);
        } else {
            initTree(idThesoSelected, selectedTerme.getIdlangue());
        }
        this.reExpandNodes(root, tc);
        idsBT = new ArrayList<>();
    }

    private void reExpandNodes(TreeNode root, TreeChange tc) {
        for (TreeNode tn : root.getChildren()) {
            if (tc.getExpandedNodes().contains(tn)) {
                tn.setExpanded(true);
                this.onNodeExpand(tn);
                reExpandNodes(tn, tc);
            }
        }
    }

    public boolean renderValid() {
        if (draggedNode != null && droppedNode != null) {
            return draggedNode.isIsGroup() == false && ((draggedNode.isIsSubGroup() && droppedNode.isIsGroup()) || (draggedNode.isIsSubGroup() && droppedNode.isIsSubGroup()) || draggedNode.isIsSubGroup() == false);
        } else {
            return false;
        }

    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * *********************************fin des fonctions drag drop
     * ******************************************
     * **********************************************************************
     */
    
    /*******fonction de contrôle de l'arbre
    /***********************************************************************/
    
    
    /**
     * 
     * @param lang 
     */
    public void getGroupAndSubGroup(String lang){
        this.groupIds=new ArrayList<>();
        this.groupLexicalValues=new ArrayList<>();
        ArrayList<NodeGroup> ncg=new GroupHelper().getListRootConceptGroup(connect.getPoolConnexion(), idThesoSelected,lang);
        for(NodeGroup ng:ncg){
            
            this.groupIds.add(ng.getConceptGroup().getIdgroup());
          
            
            this.groupLexicalValues.add(  ng.getLexicalValue()+" ("+ng.getConceptGroup().getIdgroup()+")");
        }
        
    }
    /**
     * méthode pour vérifier et forcer que les éléments d'un groupe ou d'un 
     * sous groupe possède bien le nom de ce groupe comme nom de  domaine
     * sinon on doit l'ajouter 
     * 
     */
    public void alignDomain(){
        String idGroup=null;
       for(String gid :this.groupIds){
        if(this.idGroupAlign.contains(gid)){
            idGroup=gid;
            break;
        }
       }
        for( TreeNode tn :this.root.getChildren()){
            onNodeExpand(tn);
            if(((MyTreeNode)tn).getIdCurrentGroup()==null)continue;
            if(((MyTreeNode)tn).getIdCurrentGroup().equals(idGroup)){
                    
                    alignDomainAux(tn,idGroup);
                  
            }
        }
        
    }
    /**************fonction pour aligner les domaines**************************/
    public void alignDomainAux(TreeNode mtn,String idGroup){
        
        for(TreeNode child : mtn.getChildren()){
            onNodeExpand(child);
            if( ((MyTreeNode)child).isIsSubGroup()){
                alignDomainAux((MyTreeNode)child,((MyTreeNode)child).getIdCurrentGroup());
            }
            else if(((MyTreeNode)child).getIdCurrentGroup()!=idGroup ){
                 //si le groupe du domaine n'est pas le groupe principal:
                 //on regarde dans les other groupes   
                if(!((MyTreeNode)child).getOtherGroup().contains(idGroup)){
                   //si le groupe n'apparait aps dans les other groupe alors on ajoute
                   //sur le noeud courant et dans la bdd
                    ArrayList<String>otherGroup=((MyTreeNode)child).getOtherGroup();
                   otherGroup.add(idGroup);
                   ((MyTreeNode)child).setOtherGroup(otherGroup);
                    GroupHelper gh=new GroupHelper();
                    gh.addConceptGroupConcept(this.connect.getPoolConnexion(), idGroup,((MyTreeNode)child).getIdConcept(), this.idThesoSelected);
                }    
                alignDomainAux(child,idGroup);
            }
        }
        
    }
    
  /*fonction pour importer de multiples NT ****/
    public void addMultipleNT(){
         InputStream is=null;
         ArrayList<String> narrowerTerm=new ArrayList<>();
        try{
        
            BufferedReader buff=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.multipleNT)));
            String tmp=null;
            while((tmp=buff.readLine())!=null){
                String[] lineNT=tmp.split("\t");
                for(String nt : lineNT){
                    if(!nt.trim().equals(""))narrowerTerm.add(nt.trim());
                }
                
            }
        }
        catch(Exception e){
           System.out.println("Error while reading uploaded file "+e);
        }
        finally{
            try{
            if(is!=null)is.close();
            }
            catch(IOException e){
                System.out.println("error closing inputSteeam in methodd addMultipleNT "+e );
            }
        }
        for(String nt : narrowerTerm){
            NodeAutoCompletion nac=new NodeAutoCompletion();
            nac.setTermLexicalValue(nt);
           this.selectedTerme.setSelectedTermComp(nac);
       //    newTSpe();
        }
    }
    
     public void handleFileUpload(FileUploadEvent event){
         this.multipleNT=event.getFile().getContents();
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    /**fin fonction **/
   
     /**fonction pour changer la numérotation d'un souis groupe **************/
     
     public void changeNumerotation(){
         GroupHelper gh= new GroupHelper();
         gh.saveSuffixFromNode(this.connect.getPoolConnexion(), idThesoSelected, ((MyTreeNode)this.selectedNode).getIdConcept(), ""+this.nouveauSuffixe);
          reInit();
     this.initTree(idThesoSelected, ((MyTreeNode)this.selectedNode).getLangue());

     // reExpand();
         
     }
     
     public void loadNumerotation(boolean group){
         if(group){
          this.ancienPrefixe=null;
          this.nouveauSuffixe=Integer.parseInt(((MyTreeNode)this.selectedNode).getNumerotation());
         }
         else{
         this.nouveauSuffixe=Integer.parseInt(((MyTreeNode)this.selectedNode).getSuffix());
         this.ancienPrefixe=((MyTreeNode)this.selectedNode).getPrefix();
         }
     }
     
     /*********************************fin************************************/
     
     public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(MyTreeNode root) {
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

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }


    public String getIdThesoSelected() {
        return idThesoSelected;
    }

    public void setIdThesoSelected(String idThesoSelected) {
        this.idThesoSelected = idThesoSelected;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public ConceptBean getConceptbean() {
        return conceptbean;
    }

    public void setConceptbean(ConceptBean conceptbean) {
        this.conceptbean = conceptbean;
    }

    public String getNTtag() {
        return NTtag;
    }

    public void setNTtag(String NTtag) {
        this.NTtag = NTtag;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
        this.duplicate = forced;
    }

    public boolean isEditPassed() {
        return editPassed;
    }

    public void setEditPassed(boolean editPassed) {
        this.editPassed = editPassed;
    }

    public ArrayList<StructIdBroaderTerm> getIdsBT() {
        return idsBT;
    }

    public void setIdsBT(ArrayList<StructIdBroaderTerm> idsBT) {
        this.idsBT = idsBT;
    }

    public MyTreeNode getDraggedNode() {
        if (draggedNode == null) {
            //pour éviter un null pointer dans le xhtml :§
            return new MyTreeNode(0, "00", "00", "En", "00", "00", "00", "", "", null);
        }
        return draggedNode;
    }

    public void setDraggedNode(MyTreeNode draggedNode) {
        this.draggedNode = draggedNode;
    }

    public MyTreeNode getDroppedNode() {
        if (droppedNode == null) {
            return new MyTreeNode(0, "00", "00", "En", "00", "00", "00", "", "", null);
        }
        return droppedNode;
    }

    public void setDroppedNode(MyTreeNode droppedNode) {
        this.droppedNode = droppedNode;
    }

    /**
     * permet de savoir si le concept est une branche ou non, si Null, on envoie
     * true pour éviter la réponse (a des fils)
     *
     * @return
     */
    public boolean isHaveChildren() {
        if(selectedNode == null) return true;
        return !selectedNode.isLeaf();
    }

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<String> groupIds) {
        this.groupIds = groupIds;
    }


    public String getIdGroupAlign() {
        return idGroupAlign;
    }

    public void setIdGroupAlign(String idGroupAlign) {
        this.idGroupAlign = idGroupAlign;
    }

    public ArrayList<String> getGroupLexicalValues() {
        return groupLexicalValues;
    }

    public void setGroupLexicalValues(ArrayList<String> groupLexicalValues) {
        this.groupLexicalValues = groupLexicalValues;
    }

    public int getNouveauSuffixe() {
        return nouveauSuffixe;
    }

    public void setNouveauSuffixe(int nouveauSuffixe) {
        this.nouveauSuffixe = nouveauSuffixe;
    }

    public String getAncienPrefixe() {
        return ancienPrefixe;
    }

    public void setAncienPrefixe(String ancienPrefixe) {
        this.ancienPrefixe = ancienPrefixe;
    }
    
  
}
