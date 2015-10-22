package mom.trd.opentheso.SelectedBeans;

import java.util.ArrayList;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Relation;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;

@ManagedBean(name="histo", eager=true)
@RequestScoped
public class HistoriqueBean {
    private int typeHisto = -1; // 0 = date, 1 = all
    private Date dateHisto;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    private ArrayList<Concept> conceptHisto;
    private ArrayList<NodeGroup> groupHisto;
    private ArrayList<NodeGroup> groupTradHisto;
    private ArrayList<NodeNote> noteHisto;
    private ArrayList<Relation> relationHisto;
    private ArrayList<NodeEM> nonPrefTermHisto;
    private ArrayList<Term> termHisto;

    public HistoriqueBean() {
        
    }
    
    public void load(String idThesaurus, String idConcept, String idTerm, String lang) {
        if(typeHisto == 0 && dateHisto != null) {
            conceptHisto = new ConceptHelper().getConceptHisoriqueFromDate(connect.getPoolConnexion(), idConcept, idThesaurus, dateHisto);
            groupHisto = new GroupHelper().getGroupHistoriqueFromDate(connect.getPoolConnexion(), idConcept, idThesaurus, dateHisto);
            noteHisto = new NoteHelper().getNoteHistoriqueFromDate(connect.getPoolConnexion(), idConcept, idThesaurus, idTerm, lang, dateHisto);
            relationHisto = new RelationsHelper().getRelationHistoriqueFromDate(connect.getPoolConnexion(), idConcept, idThesaurus, dateHisto, lang);
            nonPrefTermHisto = new TermHelper().getNonPreferredTermsHistoriqueFromDate(connect.getPoolConnexion(), idTerm, idThesaurus, lang, dateHisto);
            termHisto = new TermHelper().getTermsHistoriqueFromDate(connect.getPoolConnexion(), idTerm, idThesaurus, lang, dateHisto);
        } else if(typeHisto == 1) {
            conceptHisto = new ConceptHelper().getConceptHisoriqueAll(connect.getPoolConnexion(), idConcept, idThesaurus);
            groupHisto = new GroupHelper().getGroupHistoriqueAll(connect.getPoolConnexion(), idConcept, idThesaurus);
            noteHisto = new NoteHelper().getNoteHistoriqueAll(connect.getPoolConnexion(), idConcept, idThesaurus, idTerm, lang);
            relationHisto = new RelationsHelper().getRelationHistoriqueAll(connect.getPoolConnexion(), idConcept, idThesaurus, lang);
            nonPrefTermHisto = new TermHelper().getNonPreferredTermsHistoriqueAll(connect.getPoolConnexion(), idTerm, idThesaurus, lang);
            termHisto = new TermHelper().getTermsHistoriqueAll(connect.getPoolConnexion(), idTerm, idThesaurus, lang);
        }
    }
    
    public ArrayList<Concept> getConceptHisto() {
        return conceptHisto;
    }

    public void setConceptHisto(ArrayList<Concept> conceptHisto) {
        this.conceptHisto = conceptHisto;
    }

    public ArrayList<NodeGroup> getGroupHisto() {
        return groupHisto;
    }

    public void setGroupHisto(ArrayList<NodeGroup> groupHisto) {
        this.groupHisto = groupHisto;
    }

    public ArrayList<NodeGroup> getGroupTradHisto() {
        return groupTradHisto;
    }

    public void setGroupTradHisto(ArrayList<NodeGroup> groupTradHisto) {
        this.groupTradHisto = groupTradHisto;
    }

    public ArrayList<NodeNote> getNoteHisto() {
        return noteHisto;
    }

    public void setNoteHisto(ArrayList<NodeNote> noteHisto) {
        this.noteHisto = noteHisto;
    }

    public ArrayList<Relation> getRelationHisto() {
        return relationHisto;
    }

    public void setRelationHisto(ArrayList<Relation> relationHisto) {
        this.relationHisto = relationHisto;
    }

    public ArrayList<NodeEM> getNonPrefTermHisto() {
        return nonPrefTermHisto;
    }

    public void setNonPrefTermHisto(ArrayList<NodeEM> nonPrefTermHisto) {
        this.nonPrefTermHisto = nonPrefTermHisto;
    }

    public ArrayList<Term> getTermHisto() {
        return termHisto;
    }

    public void setTermHisto(ArrayList<Term> termHisto) {
        this.termHisto = termHisto;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public int getTypeHisto() {
        return typeHisto;
    }

    public void setTypeHisto(int typeHisto) {
        this.typeHisto = typeHisto;
    }

    public Date getDateHisto() {
        return dateHisto;
    }

    public void setDateHisto(Date dateHisto) {
        this.dateHisto = dateHisto;
    }
    
    
}
