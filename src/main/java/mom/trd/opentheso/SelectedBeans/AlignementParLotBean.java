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

    private int total;
    private ArrayList<String> listOfPremierChildren;
    private ArrayList<String> listOfChildrenInConcept;
    private String nomduterm;
    private int position=0;
    private boolean fin;
    private NodeAlignment nodeAli;

    private String selectedAlignement;

    private boolean selectedValue;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    public void combianDepuisRacine(String id_Theso, String id_Concept) {
        total=0;
        String id_group;
        id_group = recuparateGroup(id_Theso, id_Concept);
        StatisticHelper statisticHelper = new StatisticHelper();
        listOfChildrenInConcept = new ArrayList<>();
        listOfPremierChildren = new ArrayList<>();
        ConceptHelper conceptHelper = new ConceptHelper();
        listOfPremierChildren = conceptHelper.getListChildrenOfConcept(connect.getPoolConnexion(), id_Concept, id_Theso);
        listOfChildrenInConcept.add(id_Concept);
        remplirToutChildren(id_Theso, id_Concept);
        total = listOfChildrenInConcept.size();
        if(total<0) fin=false;
        AlignmentHelper alignmentHelper = new AlignmentHelper();
    }

    private void remplirToutChildren(String id_theso, String id_Concept) {

        AlignmentHelper alignmentHelper = new AlignmentHelper();
        for (String concept : listOfPremierChildren) {
            listOfChildrenInConcept.add(concept);
            if (alignmentHelper.isHaveChildren(connect.getPoolConnexion(), id_theso, concept)) {
                getiListChildrenOfConcept(id_theso, concept, listOfChildrenInConcept);
            }
        }
    }
    public void sumPosition()
    {
        ConceptHelper conceptHelper = new ConceptHelper();
        position= position+1;
        if (position<=total)
        {
            selectedTerme.setIdC(listOfChildrenInConcept.get(position));
            selectedTerme.setNom(conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), listOfChildrenInConcept.get(position),
                    selectedTerme.getIdTheso(),selectedTerme.getIdlangue()));
            selectedTerme.creerAlignAuto();
        }
        if(position > total)
        {
            fin = true;
            position=0;
            total=0;
        }
        
    }
    private void getiListChildrenOfConcept(String id_Theso, String id_concept, ArrayList<String> listOfChildrendeja) {

        ArrayList<String> childrenTmp = new ArrayList<>();
        ConceptHelper conceptHelper = new ConceptHelper();
        childrenTmp = conceptHelper.getListChildrenOfConcept(connect.getPoolConnexion(), id_concept, id_Theso);
        for (String conceptFil : childrenTmp) {
            listOfChildrenInConcept.add(conceptFil);
            AlignmentHelper alignmentHelper = new AlignmentHelper();
            if (alignmentHelper.isHaveChildren(connect.getPoolConnexion(), id_Theso, conceptFil)) {
                getiListChildrenOfConcept(id_Theso, conceptFil, listOfChildrenInConcept);
            }
        }

    }

    public String recuparateGroup(String id_Theso, String id_Concept) {
        AlignmentHelper alignementHelper = new AlignmentHelper();
        String id_group = alignementHelper.getGroupOfConcept(connect.getPoolConnexion(), id_Theso, id_Concept);
        return id_group;
    }
    public void reinitTotal()
    {
        total=0;
        position= 0;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

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

    public String getSelectedAlignement() {
        return selectedAlignement;
    }

    public void setSelectedAlignement(String selectedAlignement) {
        this.selectedAlignement = selectedAlignement;
    }
    
}
