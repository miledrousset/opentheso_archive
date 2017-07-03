/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.core.alignment.AlignementSource;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "editAlignement", eager = true)
@SessionScoped

public class EditAlignementSourceBean implements Serializable {

    private String source;
    private String type_rqt;
    private String alignement_format;
    private String requete;
    private String description;
    private int id = 0;
    private ArrayList<AlignementSource> listeAlignementSources;
    private NodeAutoCompletion selectedAlignement = new NodeAutoCompletion();

    private List<AlignementSource> alignementSources = new ArrayList<>();

    private AlignementSource singleAlignementSources = null;

    private ArrayList<NodeAlignment> listAlignValues;
    private boolean viewAlignement = true;
    private boolean editAlignement = false;
    private boolean newAlignement = false;
    private boolean exporSource = false;
    private String type_rqt_nouvelle = "";
    private String alignement_format_nouvelle = "";
    private ArrayList<String> types_type_requetes;
    private ArrayList<String> types_alignement_format;
    private List<String> selectedThesaurus;
    
    private String idOtherTheso;

    private String idThesoForAssosAlig;
    List<NodeAutoCompletion> listTerm;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{user1}")
    private CurrentUser theUser;

    public EditAlignementSourceBean() {
    }

    @PostConstruct
    public void init() {
        idThesoForAssosAlig = "";
        idOtherTheso ="";
    }

    public void setListeAlignementSources(String idTheso) {
        int role = theUser.getUser().getIdRole();
        if (role == 1 || role == 2) {
            AlignmentHelper alignmentHelper = new AlignmentHelper();
            listeAlignementSources = alignmentHelper.getAlignementSourceSAdmin(connect.getPoolConnexion());
            cancel();
        }
    }

    public void initNewAlignement(List<String> authorizedThesaurus) {
        initVariables();
        viewAlignement = false;
        editAlignement = false;
        newAlignement = true;
        exporSource = false;
        selectedThesaurus = authorizedThesaurus;
    }

    public void cancel() {
        initVariables();
        viewAlignement = true;
        editAlignement = false;
        newAlignement = false;
        exporSource = false;
        id = 0;
    }

    private void initVariables() {
        source = "";
        type_rqt = "";
        alignement_format = "";
        requete = "";
        description = "";
        alignementSources = new ArrayList<>();

    }
    
    

    public void loadAlignementTheso(String langue,SelectedTerme selectedTerm) {
        listTerm = new TermHelper().getAutoCompletionTerm(connect.getPoolConnexion(), idOtherTheso,langue, selectedTerm.getNom());
    }

    /**
     * dupplique l'alignement sélectioné dans le currentTheso
     *
     * @param currentIdTheso
     * @param id_user
     */
    public void duplicate(String currentIdTheso, int id_user) {

        //copie
        AlignementSource dupplicateAlignement = new AlignementSource();
        dupplicateAlignement.setSource(singleAlignementSources.getSource() + " - copy");
        dupplicateAlignement.setAlignement_format(singleAlignementSources.getAlignement_format());
        dupplicateAlignement.setDescription(singleAlignementSources.getDescription());
        dupplicateAlignement.setId(singleAlignementSources.getId());
        dupplicateAlignement.setRequete(singleAlignementSources.getRequete());
        dupplicateAlignement.setTypeRequete(singleAlignementSources.getTypeRequete());

        //ajout
        AlignmentHelper alignementHelper = new AlignmentHelper();
        List<String> selectedTheso = new ArrayList<>();
        selectedTheso.add(currentIdTheso);
        alignementHelper.injenctdansBDAlignement(connect.getPoolConnexion(), selectedTheso, dupplicateAlignement, id_user, currentIdTheso);

        //update
        updateSource(currentIdTheso);
    }

    /**
     * Met dans l'etat "séléctioné" les sources dans la table qui coresponde au
     * tesaurus selectioné
     *
     * @param idTheso
     */
    public void selectSourceOfThesoInTable(String idTheso) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ArrayList<AlignementSource> alignementOfTheso = alignmentHelper.getAlignementSource(connect.getPoolConnexion(), idTheso);
        alignementSources = alignementOfTheso;

    }

    public void insertIntoAlignementSource(String currentIdTheso, List<String> selectedThesaurus, int id_user) {

        AlignementSource alignementSource = new AlignementSource();
        alignementSource.setAlignement_format(alignement_format);
        alignementSource.setDescription(description);
        alignementSource.setId(id);
        alignementSource.setRequete(requete);
        alignementSource.setSource(source);
        alignementSource.setTypeRequete(type_rqt);

        AlignmentHelper alignementHelper = new AlignmentHelper();
        alignementHelper.injenctdansBDAlignement(connect.getPoolConnexion(), selectedThesaurus, alignementSource, id_user, currentIdTheso);

        setListeAlignementSources(currentIdTheso);
        cancel();
    }

    public void editionSource() {
        viewAlignement = false;
        editAlignement = true;
        newAlignement = false;
        exporSource = false;
    }

    public void updateSource(String idTheso) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        alignmentHelper.update_alignementSource(connect.getPoolConnexion(), alignementSources, id);
        setListeAlignementSources(idTheso);
        cancel();
    }

    public void exporterSource() {
        idThesoForAssosAlig = "";
        viewAlignement = false;
        editAlignement = false;
        newAlignement = false;
        exporSource = true;

    }

    public void exportAlignement(String idTheso) {

        AlignmentHelper alignmentHelper = new AlignmentHelper();

        alignmentHelper.deleteAllALignementSourceOfTheso(connect.getPoolConnexion(), idTheso);

        for (AlignementSource alignementSource : alignementSources) {
            source = alignementSource.getSource();
            requete = alignementSource.getRequete();

            if (!alignmentHelper.addSourceAlignementToTheso(connect.getPoolConnexion(), idTheso, alignementSource.getId())) {

            }
        }

        idThesoForAssosAlig = "";
    }

    public void effaceAlignementSource(String idTheso) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        alignmentHelper.efaceAligSour(connect.getPoolConnexion(), id);
        setListeAlignementSources(idTheso);
        cancel();
    }

    ///////
    //////
    // getter and setter 
    //////
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType_rqt() {
        return type_rqt;
    }

    public void setType_rqt(String type_rqt) {
        this.type_rqt = type_rqt;
    }

    public String getAlignement_format() {
        return alignement_format;
    }

    public void setAlignement_format(String alignement_format) {
        this.alignement_format = alignement_format;
    }

    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
    }

    public ArrayList<AlignementSource> getListeAlignementSources() {
        return listeAlignementSources;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public List<AlignementSource> getAlignementSources() {
        return alignementSources;
    }

    public void setAlignementSources(List<AlignementSource> alignementSources) {
        this.alignementSources = alignementSources;

        for (AlignementSource alignementSource : alignementSources) {
            this.id = alignementSource.getId();
        }
    }

    public ArrayList<NodeAlignment> getListAlignValues() {
        return listAlignValues;
    }

    public void setListAlignValues(ArrayList<NodeAlignment> listAlignValues) {
        this.listAlignValues = listAlignValues;
    }

    public boolean isViewAlignement() {
        return viewAlignement;
    }

    public void setViewAlignement(boolean viewAlignement) {
        this.viewAlignement = viewAlignement;
    }

    public boolean isEditAlignement() {
        return editAlignement;
    }

    public void setEditAlignement(boolean editAlignement) {
        this.editAlignement = editAlignement;
    }

    public String getAlignement_format_nouvelle() throws SQLException {
        return alignement_format_nouvelle;
    }

    public void setAlignement_format_nouvelle(String alignement_format_nouvelle) {
        this.alignement_format_nouvelle = alignement_format_nouvelle;
    }

    public ArrayList<String> getTypes_type_requetes() throws SQLException {
        AlignmentHelper aligneAlignmentHelper = new AlignmentHelper();
        types_type_requetes = aligneAlignmentHelper.typesRequetes(connect.getPoolConnexion(), "alignement_type_rqt");
        return types_type_requetes;
    }

    public void setTypes_type_requetes(ArrayList<String> types_type_requetes) {
        this.types_type_requetes = types_type_requetes;
    }

    public String getType_rqt_nouvelle() {
        return type_rqt_nouvelle;
    }

    public void setType_rqt_nouvelle(String type_rqt_nouvelle) {
        this.type_rqt_nouvelle = type_rqt_nouvelle;
    }

    public ArrayList<String> getTypes_alignement_format() throws SQLException {
        AlignmentHelper aligneAlignmentHelper = new AlignmentHelper();
        types_alignement_format = aligneAlignmentHelper.typesRequetes(connect.getPoolConnexion(), "alignement_format");
        return types_alignement_format;
    }

    public void setTypes_alignement_format(ArrayList<String> types_alignement_format) {
        this.types_alignement_format = types_alignement_format;
    }

    public List<String> getSelectedThesaurus() {
        return selectedThesaurus;
    }

    public void setSelectedThesaurus(List<String> selectedThesaurus) {
        this.selectedThesaurus = selectedThesaurus;
    }

    public boolean isNewAlignement() {
        return newAlignement;
    }

    public void setNewAlignement(boolean newAlignement) {
        this.newAlignement = newAlignement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExporSource() {
        return exporSource;
    }

    public void setExporSource(boolean exporSource) {
        this.exporSource = exporSource;
    }

    public CurrentUser getTheUser() {
        return theUser;
    }

    public void setTheUser(CurrentUser theUser) {
        this.theUser = theUser;
    }

    public void onRowSelect(SelectEvent event) {
        if (exporSource) {
            AlignmentHelper alignmentHelper = new AlignmentHelper();
            selectedThesaurus
                    = alignmentHelper.getSelectedAlignementOfThisTheso(connect.getPoolConnexion(), ((AlignementSource) event.getObject()).getId());
        }
    }

    public void onRowSelect(ToggleSelectEvent event) {
        if (exporSource) {

            AlignmentHelper alignmentHelper = new AlignmentHelper();

            for (AlignementSource align : listeAlignementSources) {
                selectedThesaurus = alignmentHelper.getSelectedAlignementOfThisTheso(connect.getPoolConnexion(), align.getId());
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlignementSource getSingleAlignementSources() {

        if (alignementSources.size() == 1) {
            singleAlignementSources = alignementSources.get(0);
        }

        return singleAlignementSources;
    }

    public void setSingleAlignementSources(AlignementSource singleAlignementSources) {

        this.singleAlignementSources = singleAlignementSources;
        id = singleAlignementSources.getId();

        if (alignementSources.isEmpty()) {
            alignementSources.add(singleAlignementSources);
        } else {
            alignementSources.set(0, singleAlignementSources);
        }

    }
    
    

    public String getIdThesoForAssosAlig() {
        return idThesoForAssosAlig;
    }

    public void setIdThesoForAssosAlig(String idThesoForAssosAlig) {
        this.idThesoForAssosAlig = idThesoForAssosAlig;
    }

    public List<NodeAutoCompletion> getListTerm() {
        return listTerm;
    }

    public void setListTerm(List<NodeAutoCompletion> listTerm) {
        this.listTerm = listTerm;
    }

    public String getIdOtherTheso() {
        return idOtherTheso;
    }

    public void setIdOtherTheso(String idOtherTheso) {
        this.idOtherTheso = idOtherTheso;
    }

    public NodeAutoCompletion getSelectedAlignement() {
        return selectedAlignement;
    }

    public void setSelectedAlignement(NodeAutoCompletion selectedAlignement) {
        this.selectedAlignement = selectedAlignement;
    }



    

}
