package mom.trd.opentheso.SelectedBeans;

import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.FacetHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeFacet;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "ssTree", eager = true)
@SessionScoped

public class UnderTree implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private TreeNode root;
    private TreeNode selectedNode; 
    private ArrayList<TreeNode> selectedNodes;
    
    @ManagedProperty(value="#{selectedTerme}")
    private SelectedTerme selectedTerme;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
/**************************** INITIALISATION ****************************/
    
    /**
     * Constructeur
     */
    public UnderTree() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
        selectedNodes = new ArrayList<>();
    }
    
    /**
     *  Vide l'arbre
     */
    public void reInit() {
        root = (TreeNode) new DefaultTreeNode("Root", null);
    }
    
    /**
     * Récupère les facettes pour remplir la racine de l'arbre selon la langue et le thésaurus sélectionnés
     * @param idTheso
     * @param langue 
     */
    public void initTree(String idTheso, String langue) {
        List<NodeConceptTree> racineNode = new FacetHelper().getIdParentOfFacet(connect.getPoolConnexion(), idTheso, langue);
        for(NodeConceptTree nct : racineNode) {
            TreeNode dynamicTreeNode = (TreeNode) new MyTreeNode(1, nct.getIdConcept(), idTheso, langue, "", "","", "dossier", nct.getTitle() + "(" + nct.getIdConcept() + ")", root);
            new DefaultTreeNode("fake", dynamicTreeNode);
        }
    }

/**************************** ACTIONS TREE ****************************/
    
    /**
     * Expension de l'abre sur un seul noeud (ouvert manuellement)
     * @param event Le noeud ouvert
     */
    public void onNodeExpand(NodeExpandEvent event) {
        String theso = ((MyTreeNode)event.getTreeNode()).getIdTheso();
        String lang = ((MyTreeNode)event.getTreeNode()).getLangue();
        
        // Récupération des facettes
        ArrayList<Integer> listIdFacet = new FacetHelper().getIdFacetUnderConcept(connect.getPoolConnexion(), ((MyTreeNode)event.getTreeNode()).getIdConcept(), theso);
        ArrayList<NodeFacet> listFacet = new ArrayList<>();
        for(Integer id : listIdFacet) {
            NodeFacet nf = new FacetHelper().getThisFacet(connect.getPoolConnexion(), id, theso, lang);
            listFacet.add(nf);
        }
        
        //<Retirer noeuds fictifs>
        if(event.getTreeNode().getChildCount() == 1){
            event.getTreeNode().getChildren().remove(0);
        }
            
        // Récupération et insertion des facettes avec leurs concepts
        for(NodeFacet nf : listFacet) {
            new MyTreeNode(1, String.valueOf(nf.getIdFacet()), theso, lang, "", "", "", "facette", nf.getLexicalValue() + "(" + String.valueOf(nf.getIdFacet()) + ")", event.getTreeNode());
        
            ArrayList<String> listIdC = new FacetHelper().getIdConceptsOfFacet(connect.getPoolConnexion(), nf.getIdFacet(), theso);
            
            ArrayList<NodeConceptTree> liste = new ArrayList<>();
            for(String id : listIdC) {
                String value = new TermHelper().getThisTerm(connect.getPoolConnexion(), id, theso, lang).getLexical_value();
                NodeConceptTree nct = new NodeConceptTree();
                nct.setHaveChildren(false);
                nct.setIdConcept(id);
                nct.setIdLang(lang);
                nct.setIdThesaurus(theso);
                nct.setTitle(value);
                liste.add(nct);
            }

            // Ajout dans l'arbre
            for(NodeConceptTree nct : liste) {
                new MyTreeNode(3, nct.getIdConcept(), theso, lang, "", "","", "fichier", nct.getTitle()+ "(" + nct.getIdConcept() + ")", event.getTreeNode());
            }
        }
    }
    
    /**
     * Affiche les informations du terme correspondant au noeud selectionné dans l'arbre
     * @param event le noeud selectionné 
     */
    public void onNodeSelect(NodeSelectEvent event) {
        if(!event.getTreeNode().getType().equals("facette")) {
            MyTreeNode temp = (MyTreeNode)selectedNode;
            temp.setTypeConcept(3);
            selectedTerme.majTerme(temp);
            selectedTerme.setTree(3);
        } else {
            selectedTerme.reInitTerme();
        }
    }
    
    public void insertNewConcept() {
        selectedTerme.insertInFacet();
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
    }
    
    public void delConcept() {
        selectedTerme.delFromFacet();
        reInit();
        initTree(selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
    }
    
/**************************** GETTERS SETTERS ****************************/
    
    /**
     * @return
     */
    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public ArrayList<TreeNode> getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(ArrayList<TreeNode> selectedNodes) {
        this.selectedNodes = selectedNodes;
    }
}
