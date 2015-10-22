package mom.trd.opentheso.bdd.helper.nodes.concept;

import java.util.ArrayList;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeImage;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;
import mom.trd.opentheso.bdd.helper.nodes.NodeRT;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;

public class NodeConcept {

    //BT termes génériques
    private ArrayList <NodeBT> nodeBT;

    //pour gérer le concept
    private Concept concept;

    // pour gérer le terme et ses valeurs 
    private Term term;

    //NT pour les termes spécifiques
    private ArrayList <NodeNT> nodeNT;

    //RT related term
    private ArrayList <NodeRT> nodeRT;

    //EM ou USE synonymes ou employé pour
    private ArrayList<NodeEM> nodeEM;

    // notes gestion de toutes les notes
    private ArrayList<NodeNote> nodeNotesTerm;
    
        // notes gestion de toutes les notes
    private ArrayList<NodeNote> nodeNotesConcept;

    //images (permet de gérer plusieurs images
    private ArrayList<NodeImage> nodeimages;

    //pour la liste des domaines du Concept
    private ArrayList<NodeGroup> nodeConceptGroup;
    
    //les traductions ddu Term
    private ArrayList <NodeTermTraduction> nodeTermTraductions;

    public NodeConcept() {
        nodeNotesTerm = new ArrayList<>();
        nodeNotesConcept = new ArrayList<>();
    }

    public ArrayList<NodeBT> getNodeBT() {
        return nodeBT;
    }

    public void setNodeBT(ArrayList<NodeBT> nodeBT) {
        this.nodeBT = nodeBT;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public ArrayList<NodeNT> getNodeNT() {
        return nodeNT;
    }

    public void setNodeNT(ArrayList<NodeNT> nodeNT) {
        this.nodeNT = nodeNT;
    }

    public ArrayList<NodeRT> getNodeRT() {
        return nodeRT;
    }

    public void setNodeRT(ArrayList<NodeRT> nodeRT) {
        this.nodeRT = nodeRT;
    }

    public ArrayList<NodeEM> getNodeEM() {
        return nodeEM;
    }

    public void setNodeEM(ArrayList<NodeEM> nodeEM) {
        this.nodeEM = nodeEM;
    }

    public ArrayList<NodeNote> getNodeNotesTerm() {
        return nodeNotesTerm;
    }

    public void setNodeNotesTerm(ArrayList<NodeNote> nodeNotesTerm) {
        this.nodeNotesTerm = nodeNotesTerm;
    }

    public ArrayList<NodeNote> getNodeNotesConcept() {
        return nodeNotesConcept;
    }

    public void setNodeNotesConcept(ArrayList<NodeNote> nodeNotesConcept) {
        this.nodeNotesConcept = nodeNotesConcept;
    }

    public ArrayList<NodeImage> getNodeimages() {
        return nodeimages;
    }

    public void setNodeimages(ArrayList<NodeImage> nodeimages) {
        this.nodeimages = nodeimages;
    }

    public ArrayList<NodeGroup> getNodeConceptGroup() {
        return nodeConceptGroup;
    }

    public void setNodeConceptGroup(ArrayList<NodeGroup> nodeConceptGroup) {
        this.nodeConceptGroup = nodeConceptGroup;
    }

    public ArrayList<NodeTermTraduction> getNodeTermTraductions() {
        return nodeTermTraductions;
    }

    public void setNodeTermTraductions(ArrayList<NodeTermTraduction> nodeTermTraductions) {
        this.nodeTermTraductions = nodeTermTraductions;
    }

    
    
}
