/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.rdf4j.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.SelectedBeans.rdf4jFileBean;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.datas.DcElement;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.ToolsHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeGps;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTerm;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.tools.StringPlus;
import mom.trd.opentheso.core.imports.helper.ImportSkosHelper;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import mom.trd.opentheso.skosapi.SKOSProperty;
import mom.trd.opentheso.skosapi.SKOSResource;
import mom.trd.opentheso.skosapi.SKOSCreator;
import mom.trd.opentheso.skosapi.SKOSDate;
import mom.trd.opentheso.skosapi.SKOSDocumentation;
import mom.trd.opentheso.skosapi.SKOSGPSCoordinates;
import mom.trd.opentheso.skosapi.SKOSLabel;
import mom.trd.opentheso.skosapi.SKOSMatch;
import mom.trd.opentheso.skosapi.SKOSNotation;
import mom.trd.opentheso.skosapi.SKOSRelation;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skosapibinding.SKOSManager;
import skos.SKOSConceptScheme;
import uk.ac.manchester.cs.skos.SKOSDatasetImpl;

/**
 *
 * @author Quincy
 */
public class ImportRdf4jHelper {

    private SKOSDataset dataSet;
    private OWLOntology onto = null;
    private SKOSManager sKOSManager;

    private final Map<URI, SKOSDatasetImpl> skosVocabularies;
    private final OWLOntologyManager ontologieManager;
    private String info = "";
    private String error = "";

    private int conceptsCount;
    private final ArrayList<String> idTopConcept;
    private ArrayList<String> idGroups;
    private String idGroupDefault;

    private String langueSource;
    private HikariDataSource ds;
    private String formatDate;
    private String adressSite;
    private int idUser;
    private int idRole;
    private boolean useArk;

    private int resourceCount;

    Thesaurus thesaurus;

    private SKOSXmlDocument skosXmlDocument;

    public ImportRdf4jHelper() {
        this.ontologieManager = OWLManager.createOWLOntologyManager();
        skosVocabularies = new HashMap<>();
        idTopConcept = new ArrayList<>();
        idGroups = new ArrayList<>();
        idGroupDefault = "";
        dataSet = null;

    }

    /**
     * initialisation des paramètres d'import
     *
     * @param ds
     * @param formatDate
     * @param useArk
     * @param adressSite
     * @param idUser
     * @param langueSource
     * @return
     */
    public boolean setInfos(HikariDataSource ds,
            String formatDate, boolean useArk, String adressSite, int idUser,
            int idRole,
            String langueSource) {
        this.ds = ds;
        this.formatDate = formatDate;
        this.useArk = useArk;
        this.adressSite = adressSite;
        this.idUser = idUser;
        this.idRole = idRole;
        this.langueSource = langueSource;

        return true;
    }

    /**
     * Cette fonction permet de créer un thésaurus avec ses traductions (Import)
     * elle retourne l'identifiant du thésaurus, sinon Null
     *
     * @return
     */
    public String addThesaurus() {
        thesaurus = new Thesaurus();

        SKOSResource conceptScheme = skosXmlDocument.getConceptScheme();

        String creator = null;
        String contributor = null;

        for (SKOSCreator c : conceptScheme.getCreatorList()) {
            if (c.getProperty() == SKOSProperty.creator) {
                creator = c.getCreator();
            } else if (c.getProperty() == SKOSProperty.contributor) {
                contributor = c.getCreator();
            }
        }

        thesaurus.setCreator(creator);
        thesaurus.setContributor(contributor);

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();

        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            String idTheso;
            if (thesaurus.getLanguage() == null) {
                thesaurus.setLanguage(langueSource);
            }
            if ((idTheso = thesaurusHelper.addThesaurusRollBack(conn, adressSite, useArk)) == null) {
                conn.rollback();
                conn.close();
                return null;
            }
            // boucler pour les traductions
            for (SKOSLabel label : skosXmlDocument.getConceptScheme().getLabelsList()) {
                thesaurus.setId_thesaurus(idTheso);
                if (thesaurus.getTitle().isEmpty()) {
                    thesaurus.setTitle("theso_" + idTheso);
                }
                if (thesaurus.getLanguage() == null) {
                    String workLanguage = "fr"; //test
                    thesaurus.setLanguage(workLanguage);
                }
                thesaurus.setTitle(label.getLabel());
                thesaurus.setLanguage(label.getLanguage());

                if (!thesaurusHelper.addThesaurusTraductionRollBack(conn, thesaurus)) {
                    conn.rollback();
                    conn.close();
                    return null;
                }
            }

            UserHelper userHelper = new UserHelper();
            if (!userHelper.addRole(conn, idUser, idRole, idTheso, "")) {
                conn.rollback();
                conn.close();
                return null;
            }
            conn.commit();
            conn.close();
            idGroupDefault = getNewGroupId();
            return null;

        } catch (SQLException ex) {
            Logger.getLogger(ImportSkosHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void addGroups(rdf4jFileBean fileBean) {
        // récupération des groups ou domaine
        GroupHelper groupHelper = new GroupHelper();

        String idGroup;

        for (SKOSResource group : skosXmlDocument.getGroupList()) {
            
            fileBean.setAbs_progress(fileBean.getAbs_progress()+1);
            fileBean.setProgress(fileBean.getAbs_progress() / fileBean.getTotal()*100);

            idGroup = getIdFromUri(group.getUri());

            groupHelper.insertGroup(ds, idGroup, thesaurus.getId_thesaurus(), "MT", group.getNotationList().get(0).getNotation(), adressSite, useArk, idUser);;

            for (SKOSLabel label : group.getLabelsList()) {
                // ajouter les traductions des Groupes
                ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
                conceptGroupLabel.setIdgroup(idGroup);
                conceptGroupLabel.setIdthesaurus(thesaurus.getId_thesaurus());
                conceptGroupLabel.setLang(label.getLanguage());
                conceptGroupLabel.setLexicalvalue(label.getLabel());

                groupHelper.addGroupTraduction(ds, conceptGroupLabel, idUser);
            }
        }

        groupHelper.insertGroup(ds,
                idGroupDefault,
                thesaurus.getId_thesaurus(),
                "MT",
                "", //notation
                adressSite,
                useArk,
                idUser);
        // ajouter les traductions des Groupes
        ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
        conceptGroupLabel.setIdgroup(idGroupDefault);
        conceptGroupLabel.setIdthesaurus(thesaurus.getId_thesaurus());
        conceptGroupLabel.setLang(langueSource);
        conceptGroupLabel.setLexicalvalue("groupDefault");
        groupHelper.addGroupTraduction(ds, conceptGroupLabel, idUser);
    }

    /**
     *
     */
    class AddConceptsStruct {

        Concept concept;
        ConceptHelper conceptHelper;
        SKOSResource conceptResource;
        // pour intégrer les coordonnées GPS 
        NodeGps nodeGps = new NodeGps();
        GpsHelper gpsHelper = new GpsHelper();
        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        ArrayList<NodeTermTraduction> nodeTermTraductionList = new ArrayList<>();
        //Enregister les synonymes et traductions
        ArrayList<NodeEM> nodeEMList = new ArrayList<>();
        // ajout des notes
        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        //ajout des relations 
        ArrayList<HierarchicalRelationship> hierarchicalRelationships = new ArrayList<>();
        // ajout des relations Groups
        ArrayList<String> idGrps = new ArrayList<>();
        // ajout des alignements 
        ArrayList<NodeAlignment> nodeAlignments = new ArrayList<>();
        TermHelper termHelper = new TermHelper();
        NoteHelper noteHelper = new NoteHelper();
        boolean isTopConcept = false;
        AlignmentHelper alignmentHelper = new AlignmentHelper();

        Term term = new Term();

    }

    public void addConcepts(rdf4jFileBean fileBean) {

        AddConceptsStruct acs = new AddConceptsStruct();
        acs.conceptHelper = new ConceptHelper();
        for (SKOSResource conceptResource : skosXmlDocument.getConceptList()) {
            
            fileBean.setAbs_progress(fileBean.getAbs_progress()+1);
            fileBean.setProgress( fileBean.getAbs_progress() / fileBean.getTotal() *100);

            acs.conceptResource = conceptResource;
            acs.concept = new Concept();
            acs.concept.setIdConcept(getIdFromUri(conceptResource.getUri()));
            acs.concept.setIdThesaurus(thesaurus.getId_thesaurus());

            addNotation(acs);
            addGPSCoordinates(acs);
            addLabel(acs);
            addDocumentation(acs);
            addDate(acs);
            addRelation(acs);
            addMatch(acs);

            //autre
            //ajout des termes et traductions
            acs.nodeTerm.setNodeTermTraduction(acs.nodeTermTraductionList);
            acs.nodeTerm.setIdTerm(acs.concept.getIdConcept());
            acs.nodeTerm.setIdConcept(acs.concept.getIdConcept());
            acs.nodeTerm.setIdThesaurus(thesaurus.getId_thesaurus());
            acs.nodeTerm.setSource("");
            acs.nodeTerm.setStatus("");
            acs.nodeTerm.setCreated(acs.concept.getCreated());
            acs.nodeTerm.setModified(acs.concept.getModified());

            // envoie du concept à la BDD 
            if (!isConceptEmpty(acs.nodeTermTraductionList)) {
                if (acs.idGrps.isEmpty()) {
                    acs.concept.setTopConcept(acs.isTopConcept);
                    acs.concept.setIdGroup(idGroupDefault);
                    acs.conceptHelper.insertConceptInTable(ds, acs.concept,
                            adressSite, useArk, idUser);
                } else {
                    for (String idGrp : acs.idGrps) {
                        acs.concept.setTopConcept(acs.isTopConcept);
                        acs.concept.setIdGroup(idGrp);
                        acs.conceptHelper.insertConceptInTable(ds, acs.concept,
                                adressSite, useArk, idUser);
                    }
                }

                acs.termHelper.insertTerm(ds, acs.nodeTerm, idUser);

                try {
                    Connection conn = ds.getConnection();
                    conn.setAutoCommit(false);

                    for (HierarchicalRelationship hierarchicalRelationship : acs.hierarchicalRelationships) {
                        acs.conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser);
                    }
                    conn.commit();
                    conn.close();
                } catch (SQLException ex) {
                }

                for (NodeNote nodeNoteList1 : acs.nodeNotes) {
                    acs.noteHelper.addConceptNote(ds, acs.concept.getIdConcept(), nodeNoteList1.getLang(),
                            thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                }

                for (NodeAlignment nodeAlignment : acs.nodeAlignments) {
                    acs.alignmentHelper.addNewAlignment(ds, nodeAlignment);
                }
                for (NodeEM nodeEMList1 : acs.nodeEMList) {
                    acs.term.setId_concept(acs.concept.getIdConcept());
                    acs.term.setId_term(acs.nodeTerm.getIdTerm());
                    acs.term.setLexical_value(nodeEMList1.getLexical_value());
                    acs.term.setLang(nodeEMList1.getLang());
                    acs.term.setId_thesaurus(thesaurus.getId_thesaurus());
                    acs.term.setSource(nodeEMList1.getSource());
                    acs.term.setStatus(nodeEMList1.getStatus());
                    acs.termHelper.addNonPreferredTerm(ds, acs.term, idUser);
                }

                if (acs.nodeGps.getLatitude() != 0.0) {
                    if (acs.nodeGps.getLongitude() != 0.0) {
                        // insertion des données GPS
                        acs.gpsHelper.insertCoordonees(ds, acs.concept.getIdConcept(),
                                thesaurus.getId_thesaurus(),
                                acs.nodeGps.getLatitude(), acs.nodeGps.getLongitude());
                    }
                }

                for (String idTopConcept1 : idTopConcept) {
                    if (!acs.conceptHelper.setTopConcept(ds, idTopConcept1, thesaurus.getId_thesaurus())) {
                        // erreur;
                    }
                }

                // initialisation des variables
                acs.concept = new Concept();
                acs.nodeTerm = new NodeTerm();
                acs.nodeTermTraductionList = new ArrayList<>();
                acs.nodeEMList = new ArrayList<>();
                acs.nodeNotes = new ArrayList<>();
                acs.nodeAlignments = new ArrayList<>();
                acs.hierarchicalRelationships = new ArrayList<>();
                acs.idGrps = new ArrayList<>();
                acs.isTopConcept = false;
                acs.nodeGps = new NodeGps();

            }
        }
    }

    private void addNotation(AddConceptsStruct acs) {
        for (SKOSNotation notation : acs.conceptResource.getNotationList()) {
            acs.concept.setNotation(notation.getNotation());
        }
    }

    private void addGPSCoordinates(AddConceptsStruct acs) {
        SKOSGPSCoordinates gPSCoordinates = acs.conceptResource.getGPSCoordinates();
        try {
            acs.nodeGps.setLatitude(Double.parseDouble(gPSCoordinates.getLat()));
            acs.nodeGps.setLongitude(Double.parseDouble(gPSCoordinates.getLon()));

        } catch (Exception e) {
            acs.nodeGps.setLatitude(0.0);
            acs.nodeGps.setLongitude(0.0);
        }

    }

    private void addLabel(AddConceptsStruct acs) {
        NodeTermTraduction nodeTermTraduction;

        for (SKOSLabel label : acs.conceptResource.getLabelsList()) {
            if (label.getProperty() == SKOSProperty.prefLabel) {
                nodeTermTraduction = new NodeTermTraduction();
                nodeTermTraduction.setLexicalValue(label.getLabel());
                nodeTermTraduction.setLang(label.getLanguage());
                acs.nodeTermTraductionList.add(nodeTermTraduction);
            } else {
                NodeEM nodeEM = new NodeEM();
                String status = "";
                boolean hiden = false;
                if (label.getProperty() == SKOSProperty.altLabel) {
                    status = "USE";

                } else if (label.getProperty() == SKOSProperty.hiddenLabel) {
                    status = "Hidden";
                    hiden = true;
                }
                nodeEM.setLexical_value(label.getLabel());
                nodeEM.setLang(label.getLanguage());
                nodeEM.setSource("" + idUser);
                nodeEM.setStatus(status);
                nodeEM.setHiden(hiden);
                acs.nodeEMList.add(nodeEM);

            }

        }
    }

    private void addDocumentation(AddConceptsStruct acs) {
        NodeNote nodeNote;
        for (SKOSDocumentation documentation : acs.conceptResource.getDocumentationsList()) {
            String noteTypeCode = "";
            int prop = documentation.getProperty();
            nodeNote = new NodeNote();
            switch (prop) {
                case SKOSProperty.definition:
                    noteTypeCode = "definition";
                    break;
                case SKOSProperty.scopeNote:
                    noteTypeCode = "scopeNote";
                    break;
                case SKOSProperty.example:
                    noteTypeCode = "example";
                    break;
                case SKOSProperty.historyNote:
                    noteTypeCode = "historyNote";
                    break;
                case SKOSProperty.editorialNote:
                    noteTypeCode = "editorialNote";
                    break;
                case SKOSProperty.changeNote:
                    noteTypeCode = "changeNote";
                    break;
                case SKOSProperty.note:
                    noteTypeCode = "note";
                    break;
            }
            nodeNote.setLang(documentation.getLanguage());
            nodeNote.setLexicalvalue(documentation.getText());
            nodeNote.setNotetypecode(noteTypeCode);
            acs.nodeNotes.add(nodeNote);
        }
    }

    private void addDate(AddConceptsStruct acs) {

        //date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
        for (SKOSDate date : acs.conceptResource.getDateList()) {
            if (date.getProperty() == SKOSProperty.created) {
                try {
                    acs.concept.setCreated(simpleDateFormat.parse(date.getDate()));
                } catch (ParseException ex) {
                    Logger.getLogger(ImportRdf4jHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((date.getProperty() == SKOSProperty.modified)) {
                try {
                    acs.concept.setModified(simpleDateFormat.parse(date.getDate()));
                } catch (ParseException ex) {
                    Logger.getLogger(ImportRdf4jHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private void addRelation(AddConceptsStruct acs) {
        HierarchicalRelationship hierarchicalRelationship;
        int prop;
        for (SKOSRelation relation : acs.conceptResource.getRelationsList()) {
            prop = relation.getProperty();
            if (prop == SKOSProperty.narrower
                    || prop == SKOSProperty.broader
                    || prop == SKOSProperty.related) {
                hierarchicalRelationship = new HierarchicalRelationship();
                String role = "";

                switch (prop) {
                    case SKOSProperty.narrower:
                        role = "NT";
                        break;
                    case SKOSProperty.broader:
                        role = "BT";
                        break;
                    case SKOSProperty.related:
                        role = "RT";
                        break;
                }

                hierarchicalRelationship.setIdConcept1(acs.concept.getIdConcept());
                hierarchicalRelationship.setIdConcept2(getIdFromUri(relation.getTargetUri()));
                hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                hierarchicalRelationship.setRole(role);
                acs.hierarchicalRelationships.add(hierarchicalRelationship);

            } else if (prop == SKOSProperty.inScheme) {
                // ?
            } else if (prop == SKOSProperty.memberOf) {
                acs.idGrps.add(getIdFromUri(relation.getTargetUri()));
                //addIdGroupToVector(uri);    ????

            } else if (prop == SKOSProperty.topConceptOf) {
                acs.isTopConcept = true;
            }

        }
    }

    private void addMatch(AddConceptsStruct acs) {
        int prop;
        int id_type = -1;
        NodeAlignment nodeAlignment;
        for (SKOSMatch match : acs.conceptResource.getMatchList()) {
            prop = match.getProperty();
            nodeAlignment = new NodeAlignment();
            switch (prop) {
                case SKOSProperty.closeMatch:
                    id_type = 2;
                    break;
                case SKOSProperty.exactMatch:
                    id_type = 1;
                    break;
                case SKOSProperty.broadMatch:
                    id_type = 3;
                    break;
                case SKOSProperty.narrowMatch:
                    id_type = 5;
                    break;
                case SKOSProperty.relatedMatch:
                    id_type = 4;
                    break;
            }

            nodeAlignment.setId_author(idUser);
            nodeAlignment.setConcept_target("");
            nodeAlignment.setThesaurus_target("");
            nodeAlignment.setUri_target(match.getValue());
            nodeAlignment.setInternal_id_concept(acs.concept.getIdConcept());
            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
            nodeAlignment.setAlignement_id_type(id_type);
            acs.nodeAlignments.add(nodeAlignment);
        }

    }

    private boolean isConceptEmpty(ArrayList<NodeTermTraduction> nodeTermTraductionList) {
        return nodeTermTraductionList.isEmpty();
    }

    private String getIdFromUri(String uri) {
        if (uri.contains("idg=")) {
            if (uri.contains("&")) {
                uri = uri.substring(uri.indexOf("idg=") + 4, uri.indexOf("&"));
            } else {
                uri = uri.substring(uri.indexOf("idg=") + 4, uri.length());
            }
        } else {
            if (uri.contains("idc=")) {
                if (uri.contains("&")) {
                    uri = uri.substring(uri.indexOf("idc=") + 4, uri.indexOf("&"));
                } else {
                    uri = uri.substring(uri.indexOf("idc=") + 4, uri.length());
                }
            } else {
                if (uri.contains("#")) {
                    uri = uri.substring(uri.indexOf("#") + 1, uri.length());
                } else {
                    uri = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
                }
            }
        }

        StringPlus stringPlus = new StringPlus();
        uri = stringPlus.normalizeStringForIdentifier(uri);
        return uri;
    }

    private String getNewGroupId() {
        GroupHelper groupHelper = new GroupHelper();
        ToolsHelper toolsHelper = new ToolsHelper();
        String id = toolsHelper.getNewId(10);
        while (groupHelper.isIdOfGroup(ds, id, thesaurus.getId_thesaurus())) {
            id = toolsHelper.getNewId(10);
        }
        return id;
    }

    public SKOSXmlDocument getRdf4jThesaurus() {
        return skosXmlDocument;
    }

    public void setRdf4jThesaurus(SKOSXmlDocument rdf4jThesaurus) {
        this.skosXmlDocument = rdf4jThesaurus;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public String getInfo() {
        return info;
    }

    public String getError() {
        return error;
    }

}
