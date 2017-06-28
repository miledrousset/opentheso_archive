/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.rdf4j.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.SelectedBeans.SelectedTerme;
import mom.trd.opentheso.SelectedBeans.rdf4jFileBean;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
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

/**
 *
 * @author Quincy
 */
public class ImportRdf4jHelper {

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

    String idTheso;

    private int resourceCount;

    Thesaurus thesaurus;

    HashMap<String, String> memberHashMap = new HashMap<>();
    ArrayList<String> hasTopConcceptList = new ArrayList<>();

    private SKOSXmlDocument skosXmlDocument;

    public ImportRdf4jHelper() {
        idTopConcept = new ArrayList<>();
        idGroups = new ArrayList<>();
        idGroupDefault = "";
    }

    /**
     * initialisation des paramètres d'import
     *
     * @param ds
     * @param formatDate
     * @param useArk
     * @param adressSite
     * @param idUser
     * @param idRole
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
     * ajoute un seul concept en choisissant un concept père
     *
     * @param selectedTerme
     */
    public void addSingleConcept(SelectedTerme selectedTerme) throws SQLException, ParseException {

        String idGroup = selectedTerme.getIdDomaine();
        String idConceptPere = selectedTerme.getIdC();
        idTheso = selectedTerme.getIdTheso();

        AddConceptsStruct acs = new AddConceptsStruct();
        acs.conceptHelper = new ConceptHelper();
        //addConcepts
        if (skosXmlDocument.getConceptList().size() == 1) {
            initAddConceptsStruct(acs, skosXmlDocument.getConceptList().get(0));
            addRelationNoBTHiera(acs);
            acs.concept.setTopConcept(false);
            acs.concept.setIdGroup(idGroup);
            acs.conceptHelper.insertConceptInTable(ds, acs.concept, adressSite, useArk, idUser);

            //on lie le nouveau concept au concept père
            HierarchicalRelationship hierarchicalRelationship1 = new HierarchicalRelationship(acs.concept.getIdConcept(), idConceptPere, idTheso, "BT");
            HierarchicalRelationship hierarchicalRelationship2 = new HierarchicalRelationship(idConceptPere, acs.concept.getIdConcept(), idTheso, "NT");
            acs.hierarchicalRelationships.add(hierarchicalRelationship1);
            acs.hierarchicalRelationships.add(hierarchicalRelationship2);

            new GroupHelper().addConceptGroupConcept(ds, idGroup, acs.concept.getIdConcept(), acs.concept.getIdThesaurus());
            finalizeAddConceptStruct(acs);
        } else {
            //erreur il y a plus d'un concept
        }
    }

    /**
     * ajoute une brache en choisissant un concept père
     *
     * @param selectedTerme
     */
    public void addBranch(SelectedTerme selectedTerme) throws ParseException, SQLException {

        String idGroup = selectedTerme.getIdDomaine();
        String idConceptPere = selectedTerme.getIdC();
        idTheso = selectedTerme.getIdTheso();

        SKOSResource root = detectRootOfBranch();

        //on ajoute la racine de la branche
        AddConceptsStruct acs;
        acs = new AddConceptsStruct();
        acs.conceptHelper = new ConceptHelper();
        initAddConceptsStruct(acs, root);
        addRelationNoBTHiera(acs);
        acs.concept.setTopConcept(false);
        acs.concept.setIdGroup(idGroup);
        acs.conceptHelper.insertConceptInTable(ds, acs.concept, adressSite, useArk, idUser);
        //on lie le nouveau concept au concept père
        HierarchicalRelationship hierarchicalRelationship1 = new HierarchicalRelationship(acs.concept.getIdConcept(), idConceptPere, idTheso, "BT");
        HierarchicalRelationship hierarchicalRelationship2 = new HierarchicalRelationship(idConceptPere, acs.concept.getIdConcept(), idTheso, "NT");
        acs.hierarchicalRelationships.add(hierarchicalRelationship1);
        acs.hierarchicalRelationships.add(hierarchicalRelationship2);
        new GroupHelper().addConceptGroupConcept(ds, idGroup, acs.concept.getIdConcept(), acs.concept.getIdThesaurus());
        finalizeAddConceptStruct(acs);

        //on ajoute le reste
        skosXmlDocument.getConceptList().remove(root);
        for (SKOSResource resource : skosXmlDocument.getConceptList()) {
            acs = new AddConceptsStruct();
            acs.conceptHelper = new ConceptHelper();
            initAddConceptsStruct(acs, resource);
            addRelation(acs);
            acs.concept.setTopConcept(false);
            acs.concept.setIdGroup(idGroup);
            acs.conceptHelper.insertConceptInTable(ds, acs.concept, adressSite, useArk, idUser);
            new GroupHelper().addConceptGroupConcept(ds, idGroup, acs.concept.getIdConcept(), acs.concept.getIdThesaurus());
            finalizeAddConceptStruct(acs);
        }

    }

    /**
     * detecte la racine d'une branche
     *
     * @return root
     */
    private SKOSResource detectRootOfBranch() {
        SKOSResource root = null;
        HashMap<String, String> uriRessourcePere = new HashMap<>();
        ArrayList<String> uriList = new ArrayList<>();
        for (SKOSResource resource : skosXmlDocument.getConceptList()) {
            String uri = resource.getUri();
            uriList.add(uri);
            for (SKOSRelation relation : resource.getRelationsList()) {
                int relationProp = relation.getProperty();
                if (relationProp == SKOSProperty.broader
                        || relationProp == SKOSProperty.broaderGeneric
                        || relationProp == SKOSProperty.broaderInstantive
                        || relationProp == SKOSProperty.broaderPartitive) {
                    uriRessourcePere.put(uri, relation.getTargetUri());
                }
            }
        }
        for (SKOSResource resource : skosXmlDocument.getConceptList()) {
            String uri = resource.getUri();
            //si la ressource n'a pas de père alors c'est la racine
            if (uriRessourcePere.get(uri) == null || !uriList.contains(uriRessourcePere.get(uri))) {
                root = resource;
            }
        }
        return root;
    }

    /**
     * Cette fonction permet de créer un thésaurus avec ses traductions (Import)
     * elle retourne l'identifiant du thésaurus, sinon Null
     *
     * @return
     */
    public String addThesaurus() throws SQLException {
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
        thesaurusHelper.setIdentifierType("2");
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
        // Si le Titre du thésaurus n'est pas detecter, on donne un nom par defaut
        if (skosXmlDocument.getConceptScheme().getLabelsList().isEmpty()) {
            if (thesaurus.getTitle().isEmpty()) {
                thesaurus.setTitle("theso_" + idTheso);
                //thesaurusHelper.addThesaurusTraduction(ds, thesaurus);
            }
        }

        thesaurus.setId_thesaurus(idTheso);
        this.idTheso = idTheso;
        // boucler pour les traductions
        for (SKOSLabel label : skosXmlDocument.getConceptScheme().getLabelsList()) {

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

        for (SKOSRelation relation : skosXmlDocument.getConceptScheme().getRelationsList()) {
            hasTopConcceptList.add(relation.getTargetUri());
        }

        return null;
    }

    public void addGroups(rdf4jFileBean fileBean) {
        // récupération des groups ou domaine
        GroupHelper groupHelper = new GroupHelper();

        String idGroup;

        for (SKOSResource group : skosXmlDocument.getGroupList()) {

            fileBean.setAbs_progress(fileBean.getAbs_progress() + 1);
            fileBean.setProgress(fileBean.getAbs_progress() / fileBean.getTotal() * 100);

            idGroup = getIdFromUri(group.getUri());

            ArrayList<SKOSNotation> notationList = group.getNotationList();
            SKOSNotation notation = null;
            if (notationList != null && !notationList.isEmpty()) {
                notation = notationList.get(0);
            }
            String notationValue;
            if (notation == null) {
                notationValue = "";
            } else {
                notationValue = notation.getNotation();
            }

            String type;
            switch (group.getProperty()) {
                case SKOSProperty.Collection:
                    type = "C";
                    break;
                case SKOSProperty.ConceptGroup:
                    type = "G";
                    break;
                case SKOSProperty.MicroThesaurus:
                default:
                    type = "MT";
                    break;
                case SKOSProperty.Theme:
                    type = "T";
                    break;
            }
            groupHelper.insertGroup(ds, idGroup, thesaurus.getId_thesaurus(), type, notationValue, adressSite, useArk, idUser);

            //sub group
            String idSubGroup = null;
            //concept group concept
            String idSubConcept = null;
            for (SKOSRelation relation : group.getRelationsList()) {
                int prop = relation.getProperty();
                if (prop == SKOSProperty.subGroup) {
                    idSubGroup = getIdFromUri(relation.getTargetUri());
                    groupHelper.addSubGroup(ds, idGroup, idSubGroup, thesaurus.getId_thesaurus());
                } else if (prop == SKOSProperty.member) {
                    idSubConcept = getIdFromUri(relation.getTargetUri());
                    groupHelper.addConceptGroupConcept(ds, idGroup, idSubConcept, thesaurus.getId_thesaurus());

                    memberHashMap.put(relation.getTargetUri(), idGroup);
                } else if (prop == SKOSProperty.hasTopConcept) {

                    hasTopConcceptList.add(relation.getTargetUri());
                }

            }

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

        // Création du domaine par défaut 
        // ajouter les traductions des Groupes
        /*ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
        conceptGroupLabel.setIdgroup(idGroupDefault);
        conceptGroupLabel.setIdthesaurus(thesaurus.getId_thesaurus());
        
        conceptGroupLabel.setLang(langueSource);
        conceptGroupLabel.setLexicalvalue("groupDefault");
        groupHelper.addGroupTraduction(ds, conceptGroupLabel, idUser);*/
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

    public void addConcepts(rdf4jFileBean fileBean) throws SQLException, ParseException {

        AddConceptsStruct acs = new AddConceptsStruct();
        acs.conceptHelper = new ConceptHelper();
        for (SKOSResource conceptResource : skosXmlDocument.getConceptList()) {

            fileBean.setAbs_progress(fileBean.getAbs_progress() + 1);
            fileBean.setProgress(fileBean.getAbs_progress() / fileBean.getTotal() * 100);

            initAddConceptsStruct(acs, conceptResource);
            addRelation(acs);

            // envoie du concept à la BDD 
            if (!isConceptEmpty(acs.nodeTermTraductionList)) {
                if (acs.idGrps.isEmpty()) {
                    acs.concept.setTopConcept(acs.isTopConcept);
                    acs.concept.setIdGroup(idGroupDefault);
                    acs.conceptHelper.insertConceptInTable(ds, acs.concept, adressSite, useArk, idUser);

                    new GroupHelper().addConceptGroupConcept(ds, idGroupDefault, acs.concept.getIdConcept(), acs.concept.getIdThesaurus());
                } else {
                    for (String idGrp : acs.idGrps) {
                        acs.concept.setTopConcept(acs.isTopConcept);
                        acs.concept.setIdGroup(idGrp);
                        acs.conceptHelper.insertConceptInTable(ds, acs.concept,
                                adressSite, useArk, idUser);
                    }
                }

                finalizeAddConceptStruct(acs);

            }
        }

        addLangsToThesaurus(ds, idTheso);
    }

    private void finalizeAddConceptStruct(AddConceptsStruct acs) throws SQLException {
        acs.termHelper.insertTerm(ds, acs.nodeTerm, idUser);

        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);

        for (HierarchicalRelationship hierarchicalRelationship : acs.hierarchicalRelationships) {
            acs.conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser);
        }
        conn.commit();
        conn.close();

        // For Concept : customnote ; scopeNote ; historyNote
        // For Term : definition; editorialNote; historyNote;
        for (NodeNote nodeNoteList1 : acs.nodeNotes) {

            if (nodeNoteList1.getNotetypecode().equals("customnote") || nodeNoteList1.getNotetypecode().equals("scopeNote") || nodeNoteList1.getNotetypecode().equals("historyNote") || nodeNoteList1.getNotetypecode().equals("note")) {
                acs.noteHelper.addConceptNote(ds, acs.concept.getIdConcept(), nodeNoteList1.getLang(),
                        idTheso, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }

            if (nodeNoteList1.getNotetypecode().equals("definition") || nodeNoteList1.getNotetypecode().equals("editorialNote")) {
                acs.noteHelper.addTermNote(ds, acs.nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                        idTheso, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }

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

        if (acs.nodeGps.getLatitude() != null && acs.nodeGps.getLongitude() != null) {
            // insertion des données GPS
            acs.gpsHelper.insertCoordonees(ds, acs.concept.getIdConcept(),
                    thesaurus.getId_thesaurus(),
                    acs.nodeGps.getLatitude(), acs.nodeGps.getLongitude());
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

    private void initAddConceptsStruct(AddConceptsStruct acs, SKOSResource conceptResource) throws ParseException {
        acs.conceptResource = conceptResource;
        acs.concept = new Concept();
        acs.concept.setIdConcept(getIdFromUri(conceptResource.getUri()));
        acs.concept.setIdThesaurus(idTheso);

        addNotation(acs);
        addGPSCoordinates(acs);
        addLabel(acs);
        addDocumentation(acs);
        addDate(acs);

        addMatch(acs);

        //autre
        //ajout des termes et traductions
        acs.nodeTerm.setNodeTermTraduction(acs.nodeTermTraductionList);
        acs.nodeTerm.setIdTerm(acs.concept.getIdConcept());
        acs.nodeTerm.setIdConcept(acs.concept.getIdConcept());
        acs.nodeTerm.setIdThesaurus(idTheso);
        acs.nodeTerm.setSource("");
        acs.nodeTerm.setStatus("");
        acs.nodeTerm.setCreated(acs.concept.getCreated());
        acs.nodeTerm.setModified(acs.concept.getModified());
    }

    private void addNotation(AddConceptsStruct acs) {
        acs.concept.setNotation("");
        for (SKOSNotation notation : acs.conceptResource.getNotationList()) {
            String value = notation.getNotation();
            if (value != null) {
                acs.concept.setNotation(value);
            }

        }
    }

    private void addGPSCoordinates(AddConceptsStruct acs) {
        SKOSGPSCoordinates gPSCoordinates = acs.conceptResource.getGPSCoordinates();
        try {
            acs.nodeGps.setLatitude(Double.parseDouble(gPSCoordinates.getLat()));
            acs.nodeGps.setLongitude(Double.parseDouble(gPSCoordinates.getLon()));

        } catch (Exception e) {
            acs.nodeGps.setLatitude(null);
            acs.nodeGps.setLongitude(null);
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

    private void addDate(AddConceptsStruct acs) throws ParseException {

        //date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
        for (SKOSDate date : acs.conceptResource.getDateList()) {
            if (date.getProperty() == SKOSProperty.created) {

                acs.concept.setCreated(simpleDateFormat.parse(date.getDate()));

            } else if ((date.getProperty() == SKOSProperty.modified)) {

                acs.concept.setModified(simpleDateFormat.parse(date.getDate()));

            }
        }

    }

    private void addRelationNoBTHiera(AddConceptsStruct acs) {
        HierarchicalRelationship hierarchicalRelationship;
        int prop;
        for (SKOSRelation relation : acs.conceptResource.getRelationsList()) {
            prop = relation.getProperty();
            hierarchicalRelationship = new HierarchicalRelationship();
            String role;

            switch (prop) {
                case SKOSProperty.narrower:
                    role = "NT";
                    break;
                case SKOSProperty.narrowerGeneric:
                    role = "NTG";
                    break;
                case SKOSProperty.narrowerPartitive:
                    role = "NTP";
                    break;
                case SKOSProperty.narrowerInstantive:
                    role = "NTI";
                    break;
                case SKOSProperty.related:
                    role = "RT";
                    break;
                default:
                    role = "";

            }
            if (!role.equals("")) {
                hierarchicalRelationship.setIdConcept1(acs.concept.getIdConcept());
                hierarchicalRelationship.setIdConcept2(getIdFromUri(relation.getTargetUri()));
                hierarchicalRelationship.setIdThesaurus(idTheso);
                hierarchicalRelationship.setRole(role);
                acs.hierarchicalRelationships.add(hierarchicalRelationship);

            } else if (prop == SKOSProperty.inScheme) {
                // ?
                /*} else if (prop == SKOSProperty.memberOf) {
                acs.idGrps.add(getIdFromUri(relation.getTargetUri()));
                //addIdGroupToVector(uri);    ????
                 */
            } else if (prop == SKOSProperty.topConceptOf) {
                acs.isTopConcept = true;

            }

            if (hasTopConcceptList.contains(acs.conceptResource.getUri())) {
                acs.isTopConcept = true;
            }
            String idConcept = acs.conceptResource.getUri();
            String idPere = memberHashMap.get(idConcept);

            if (idPere != null) {
                acs.idGrps.add(idPere);
                memberHashMap.remove(idConcept);
            }

        }

    }

    private void addRelation(AddConceptsStruct acs) {
        HierarchicalRelationship hierarchicalRelationship;
        int prop;
        for (SKOSRelation relation : acs.conceptResource.getRelationsList()) {
            prop = relation.getProperty();

            hierarchicalRelationship = new HierarchicalRelationship();
            String role;

            switch (prop) {
                case SKOSProperty.narrower:
                    role = "NT";
                    break;
                case SKOSProperty.narrowerGeneric:
                    role = "NTG";
                    break;
                case SKOSProperty.narrowerPartitive:
                    role = "NTP";
                    break;
                case SKOSProperty.narrowerInstantive:
                    role = "NTI";
                    break;
                case SKOSProperty.broader:
                    role = "BT";
                    break;
                case SKOSProperty.broaderGeneric:
                    role = "BTG";
                    break;
                case SKOSProperty.broaderInstantive:
                    role = "BTI";
                    break;
                case SKOSProperty.broaderPartitive:
                    role = "BTP";
                    break;
                case SKOSProperty.related:
                    role = "RT";
                    break;
                default:
                    role = "";
            }

            if (!role.equals("")) {
                hierarchicalRelationship.setIdConcept1(acs.concept.getIdConcept());
                hierarchicalRelationship.setIdConcept2(getIdFromUri(relation.getTargetUri()));
                hierarchicalRelationship.setIdThesaurus(idTheso);
                hierarchicalRelationship.setRole(role);
                acs.hierarchicalRelationships.add(hierarchicalRelationship);

            } else if (prop == SKOSProperty.inScheme) {
                // ?
                /*} else if (prop == SKOSProperty.memberOf) {
                acs.idGrps.add(getIdFromUri(relation.getTargetUri()));
                //addIdGroupToVector(uri);    ????
                 */
            } else if (prop == SKOSProperty.topConceptOf) {
                acs.isTopConcept = true;

            }

            if (hasTopConcceptList.contains(acs.conceptResource.getUri())) {
                acs.isTopConcept = true;
            }
            String idConcept = acs.conceptResource.getUri();
            String idPere = memberHashMap.get(idConcept);

            if (idPere != null) {
                acs.idGrps.add(idPere);
                memberHashMap.remove(idConcept);
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
            nodeAlignment.setInternal_id_thesaurus(idTheso);
            nodeAlignment.setAlignement_id_type(id_type);
            acs.nodeAlignments.add(nodeAlignment);
        }

    }

    private boolean isConceptEmpty(ArrayList<NodeTermTraduction> nodeTermTraductionList) {
        return nodeTermTraductionList.isEmpty();
    }

    public static String getIdFromUri(String uri) {
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

    public void addLangsToThesaurus(HikariDataSource ds, String idThesaurus) {

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        ArrayList<String> tabListLang = thesaurusHelper.getAllUsedLanguagesOfThesaurus(ds, idThesaurus);
        for (int i = 0; i < tabListLang.size(); i++) {
            if (!thesaurusHelper.isLanguageExistOfThesaurus(ds, idThesaurus, tabListLang.get(i).trim())) {
                Thesaurus thesaurus = new Thesaurus();
                thesaurus.setId_thesaurus(idThesaurus);
                thesaurus.setContributor("");
                thesaurus.setCoverage("");
                thesaurus.setCreator("");
                thesaurus.setDescription("");
                thesaurus.setFormat("");
                thesaurus.setLanguage(tabListLang.get(i));
                thesaurus.setPublisher("");
                thesaurus.setRelation("");
                thesaurus.setRights("");
                thesaurus.setSource("");
                thesaurus.setSubject("");
                thesaurus.setTitle("theso_" + idThesaurus);
                thesaurus.setType("");
                thesaurusHelper.addThesaurusTraduction(ds, thesaurus);
            }
        }

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

}
