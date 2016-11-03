package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.StatisticHelper;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatConcept;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatTheso;

@ManagedBean(name = "statBean")
@SessionScoped

public class StatBean implements Serializable {
    
    
    private int typeStat;
    private int typeDate;
    private Date begin;
    private Date end;
    private int nbCpt;
    private ArrayList<NodeStatTheso> statTheso;
    private ArrayList<NodeStatConcept> statConcept;
    
    @ManagedProperty(value = "#{vue}")
    private Vue vue;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    public StatBean() {
        statTheso = new ArrayList<>();
        statConcept = new ArrayList<>();
    }
    
    public void reInit() {
        typeStat = 0;
        typeDate = 0;
        nbCpt = 0;
        statTheso = new ArrayList<>();
        statConcept = new ArrayList<>();
    }
    
    public void chooseStat(String idTheso, String langue) {
        if(typeStat == 1) {
            loadStatTheso(idTheso, langue);
        } else if(typeStat == 2) {
            loadStatCpt(idTheso);
        }
    }

    public void loadStatTheso(String idTheso, String langue) {
        statTheso = new ArrayList<>();
        nbCpt = new StatisticHelper().getNbCpt(connect.getPoolConnexion(), idTheso);
        ArrayList<NodeGroup> lng = new GroupHelper().getListConceptGroup(connect.getPoolConnexion(), idTheso, langue);
        Collections.sort(lng);
        StatisticHelper sh = new StatisticHelper();
        for(NodeGroup ng : lng) {
            NodeStatTheso nst = new NodeStatTheso();
            nst.setGroup(ng.getLexicalValue() + "(" + ng.getConceptGroup().getIdgroup() + ")");
            nst.setNbDescripteur(sh.getNbDescOfGroup(connect.getPoolConnexion(), idTheso, ng.getConceptGroup().getIdgroup()));
            nst.setNbNonDescripteur(sh.getNbNonDescOfGroup(connect.getPoolConnexion(), idTheso, ng.getConceptGroup().getIdgroup(), langue));
            nst.setNbNoTrad(nst.getNbDescripteur() - sh.getNbTradOfGroup(connect.getPoolConnexion(), idTheso, ng.getConceptGroup().getIdgroup(), langue));
            nst.setNbNotes(sh.getNbDefinitionNoteOfGroup(connect.getPoolConnexion(), idTheso, langue, ng.getConceptGroup().getIdgroup()));
            nst.setNbNotes(0);
            statTheso.add(nst);
        }
        vue.setStatTheso(true);
        vue.setStatCpt(false);
        vue.setStatPermuted(false);
        
    }
    
    public void loadStatCpt(String idTheso) {
        vue.setStatTheso(false);
        vue.setStatCpt(true);
        vue.setStatPermuted(false);
    }
    
    public void findCpt(String idTheso, String langue) {
        if(typeDate == 1) {
            statConcept = new StatisticHelper().getStatConceptCreat(connect.getPoolConnexion(), begin.toString(), end.toString(), idTheso, langue);
        } else if(typeDate == 2) {
            statConcept = new StatisticHelper().getStatConceptEdit(connect.getPoolConnexion(), begin.toString(), end.toString(), idTheso, langue);
        }
    }
    
    public int getNbCpt() {
        return nbCpt;
    }

    public void setNbCpt(int nbCpt) {
        this.nbCpt = nbCpt;
    }
    
    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public ArrayList<NodeStatTheso> getStatTheso() {
        return statTheso;
    }

    public void setStatTheso(ArrayList<NodeStatTheso> statTheso) {
        this.statTheso = statTheso;
    }

    public ArrayList<NodeStatConcept> getStatConcept() {
        return statConcept;
    }

    public void setStatConcept(ArrayList<NodeStatConcept> statConcept) {
        this.statConcept = statConcept;
    }

    public int getTypeStat() {
        return typeStat;
    }

    public void setTypeStat(int typeStat) {
        this.typeStat = typeStat;
    }

    public int getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(int typeDate) {
        this.typeDate = typeDate;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
