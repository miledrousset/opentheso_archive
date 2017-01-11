/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "alignementparlot", eager = true)
@SessionScoped

public class AlignementParLotBean {

    private ArrayList<String> listOfChildrenInConcept;
    private String nomduterm;
    private int position = 0;
    private boolean fin = false;

    private boolean first = true;
    private boolean last = false;

    private NodeAlignment nodeAli;

    private String uriSelection = null;

    private int alignement_id_type;
    private String id_concept;
    private String id_theso;

    private String erreur = "";
    private String message = "";
    private boolean mettreAJour = false;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    /**
     * Permet de savoir combien d'enfants a le concept selectionnée
     *
     * @param id_Theso
     * @param id_Concept
     */
    public void getListChildren(String id_Theso, String id_Concept) {
        reinitTotal();
        id_concept = id_Concept;
        id_theso = id_Theso;
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        listOfChildrenInConcept = new ArrayList<>();
        listOfChildrenInConcept = conceptHelper.getIdsOfBranch(
                connect.getPoolConnexion(), id_concept, id_Theso, listOfChildrenInConcept);

        if (listOfChildrenInConcept.isEmpty() || listOfChildrenInConcept.size() == 1) {
            last = true;
        }
        nomduterm = selectedTerme.nom;
    }

    /**
     * Cette fonction permet de passer au concept suivant. et fait l'apelation a
     * la funtion pour créer l'alignement (la funtion apelé est dans
     * selecteTerme
     */
    public void nextPosition() {

        if (fin) {
            return;
        }
        erreur = "";
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        if (!mettreAJour) {
            position++;
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            }

            comprobationFin();
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            while (alignmentHelper.dejaAligneParAvecCetteAlignement(connect.getPoolConnexion(),
                    id_concept, id_theso, selectedTerme.alignementSource.getId())) {
                position++;
                comprobationFin();
                id_concept = listOfChildrenInConcept.get(position);
                nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                        selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            }
        } else {
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            }
            comprobationFin();
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        }

        selectedTerme.creerAlignAuto(id_concept, nomduterm);
        position++;
    }
    /**
     *Permet de savoir si c'est le fin de l'Arraylist et sortir du dialog 
     */
    private void comprobationFin() {

        if (position == listOfChildrenInConcept.size() - 1) {
            last = true;
        }
        if (position == listOfChildrenInConcept.size()) {
            fin = true;
        }

    }

    /**
     * reinicialitation du variables
     */
    public void reinitTotal() {
        listOfChildrenInConcept = null;
        nomduterm = "";
        position = 0;
        fin = false;
        first = true;
        last = false;
        erreur = "";
        message = "";
    }

    /**
     * cherche l'alignement que on a selectionée dans l'arrayList d'alignements
     * et ce fait l'apelation a la funtion pour ajouter l'alignement
     */
    public void addAlignement() {
        erreur = "";
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        if (!alignmentHelper.dejaAligneParAvecCetteAlignement(connect.getPoolConnexion(), id_concept, id_theso, selectedTerme.alignementSource.getId())
                || mettreAJour) {
            if (uriSelection.isEmpty()) {
                erreur = "no selected alignment";
                message = "";
            } else {
                for (NodeAlignment nodeAlignment : selectedTerme.getListAlignValues()) {
                    if (nodeAlignment.getUri_target().equals(uriSelection)) {
                        nodeAli = nodeAlignment;
                        nodeAli.setAlignement_id_type(alignement_id_type);
                       // message = "l'alignement va se faire <br>";
                        selectedTerme.ajouterAlignAutoByLot(nodeAli);
                        message += selectedTerme.getMessageAlig();
                        nodeAli = null;
                        nextPosition();
                        uriSelection = null;
                        message += "<br>Concept aligné ...";
                        return;
                    }
                }
            }
        } else {
            nextPosition();
        }
    }
/**
 * Permet de savoir le premiere element que on besoin montrer
 * @param id_concept
 * @param id_theso 
 */
    public void getPreliereElement(String id_concept, String id_theso) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        if (alignmentHelper.dejaAligneParAvecCetteAlignement(connect.getPoolConnexion(), id_concept, id_theso, selectedTerme.alignementSource.getId())) {
            nextPosition();
        } else {
            ConceptHelper conceptHelper = new ConceptHelper();
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            selectedTerme.creerAlignAuto(id_concept, nomduterm);
        }
    }

    ///////////////GET & SET////////////////
    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public ArrayList<String> getListOfChildrenInConcept() {
        return listOfChildrenInConcept;
    }

    public void setListOfChildrenInConcept(ArrayList<String> listOfChildrenInConcept) {
        this.listOfChildrenInConcept = listOfChildrenInConcept;
    }

    public String getNomduterm() {
        return nomduterm;
    }

    public void setNomduterm(String nomduterm) {
        this.nomduterm = nomduterm;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public NodeAlignment getNodeAli() {
        return nodeAli;
    }

    public void setNodeAli(NodeAlignment nodeAli) {
        this.nodeAli = nodeAli;
    }

    public String getUriSelection() {
        return uriSelection;
    }

    public void setUriSelection(String uriSelection) {
        this.uriSelection = uriSelection;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getAlignement_id_type() {
        return alignement_id_type;
    }

    public void setAlignement_id_type(int alignement_id_type) {
        this.alignement_id_type = alignement_id_type;
    }

    public String getErreur() {
        return erreur;
    }

    public void setErreur(String erreur) {
        this.erreur = erreur;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId_concept() {
        return id_concept;
    }

    public void setId_concept(String id_concept) {
        this.id_concept = id_concept;
    }

    public boolean isMettreAJour() {
        return mettreAJour;
    }

    public void setMettreAJour(boolean mettreAJour) {
        this.mettreAJour = mettreAJour;
    }

}
