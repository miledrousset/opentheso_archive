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
import mom.trd.opentheso.bdd.helper.StatisticHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import org.glassfish.external.statistics.Statistic;

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
    private boolean fin;
    private boolean first = true;
    private NodeAlignment nodeAli;

    private String uriSelection;

    private int alignement_id_type;
    private boolean selectedValue;

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
    public void combianDepuisRacine(String id_Theso, String id_Concept) {
        first = true;
        ConceptHelper conceptHelper = new ConceptHelper();
        listOfChildrenInConcept = new ArrayList<>();
        listOfChildrenInConcept = conceptHelper.getIdsOfBranch(
                connect.getPoolConnexion(), id_Concept, id_Theso, listOfChildrenInConcept);
        
//        listOfChildrenInConcept.add(id_Concept);
//        remplirToutChildren(id_Theso, id_Concept);
        if(listOfChildrenInConcept.isEmpty()) {
            fin = true;
        }
        if (first) {
            nomduterm = selectedTerme.nom;
            first = false;
        }
    }

    /**
     * Cette fonction permet de passer au concept suivant. et fait l'apelation a
     * la funtion pour créer l'alignement (la funtion apelé est dans
     * selecteTerme
     */
    public void nextPosition() {
        ConceptHelper conceptHelper = new ConceptHelper();
        position++;
        String idConcept;

        if (position >= listOfChildrenInConcept.size() - 1) {
            fin = true;
        }
        if (position < listOfChildrenInConcept.size()) {
            idConcept = listOfChildrenInConcept.get(position);
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), idConcept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            selectedTerme.creerAlignAuto(idConcept, nomduterm);
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
    }

    /**
     * cherche l'alignement que on a selectionée dans l'arrayList d'alignements
     * et ce fait l'apelation a la funtion pour ajouter l'alignement
     */
    public void addAlignement() {
        for (NodeAlignment nodeAlignment : selectedTerme.getListAlignValues()) {
            if (nodeAlignment.getUri_target().equals(uriSelection)) {
                nodeAli = nodeAlignment;
                nodeAli.setAlignement_id_type(alignement_id_type);
                selectedTerme.ajouterAlignAutoByLot(nodeAli);
                nodeAli = null;
                nextPosition();
                if (fin) {
                    reinitTotal();
                }
                return;

            }
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

}
