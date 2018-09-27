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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.ToolsHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.UserHelper2;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeGps;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTerm;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.tools.StringPlus;
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
    private boolean defaultGroupToAdd;
    
    private String langueSource;
    private HikariDataSource ds;
    private String formatDate;
    private String adressSite;
    private int idUser;
    private int idGroupUser;


//    private boolean useArk;
    private String identifierType;
    private String prefixHandle;

    private NodePreference nodePreference;
    private StringBuilder message;
    
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
        message = new StringBuilder();
        defaultGroupToAdd = false;
    }

    /**
     * Classe pour construire un concept sépcifique
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
    
    /**
     * initialisation des paramètres d'import
     *
     * @param ds
     * @param formatDate
     * @param idGroup
     * @param idUser
     * @param langueSource
     * @return
     */
    public boolean setInfos(HikariDataSource ds,
            String formatDate, int idUser,
            int idGroupUser,
            String langueSource) {
        this.ds = ds;
        this.formatDate = formatDate;
        this.idUser = idUser;
        this.idGroupUser = idGroupUser;
        this.langueSource = langueSource;
        return true;
    }

    /**
     * ajoute un seul concept en choisissant un concept père
     *
     * @param selectedTerme
     * @throws java.sql.SQLException
     * @throws java.text.ParseException
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
            acs.conceptHelper.insertConceptInTable(ds, acs.concept, idUser);

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
     * @throws java.text.ParseException
     * @throws java.sql.SQLException
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
        acs.conceptHelper.insertConceptInTable(ds, acs.concept, idUser);
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
            acs.conceptHelper.insertConceptInTable(ds, acs.concept, idUser);
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
                        || relationProp == SKOSProperty.broaderInstantial
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
     * @throws java.sql.SQLException
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
        String idTheso1;
        if (thesaurus.getLanguage() == null) {
            thesaurus.setLanguage(langueSource);
        }
        if ((idTheso1 = thesaurusHelper.addThesaurusRollBack(conn, "", false)) == null) {
            conn.rollback();
            conn.close();
            return null;
        }
        // Si le Titre du thésaurus n'est pas detecter, on donne un nom par defaut
        if (skosXmlDocument.getConceptScheme().getLabelsList().isEmpty()) {
            if (thesaurus.getTitle().isEmpty()) {
                thesaurus.setTitle("theso_" + idTheso1);
                //thesaurusHelper.addThesaurusTraduction(ds, thesaurus);
            }
        }

        thesaurus.setId_thesaurus(idTheso1);
        this.idTheso = idTheso1;
        // boucler pour les traductions
        for (SKOSLabel label : skosXmlDocument.getConceptScheme().getLabelsList()) {
            thesaurus.setTitle(label.getLabel());
            thesaurus.setLanguage(label.getLanguage());
            if (thesaurus.getLanguage() == null) {
                thesaurus.setLanguage("fr"); // cas où la langue n'est pas définie dans le SKOS
            }
            if (!thesaurusHelper.addThesaurusTraductionRollBack(conn, thesaurus)) {
                conn.rollback();
                conn.close();
                return null;
            }
        }

        // ajouter le thésaurus dans le group de l'utilisateur
        if(idGroupUser != -1){ // si le groupeUser = - 1, c'est le cas d'un SuperAdmin, alors on n'intègre pas le thésaurus dans un groupUser
            UserHelper2 userHelper = new UserHelper2();
            if (!userHelper.addThesoToGroup(conn, thesaurus.getId_thesaurus(),
                    idGroupUser)) {
                conn.rollback();
                conn.close();
                return null;
            }
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
            groupHelper.insertGroup(ds, idGroup, thesaurus.getId_thesaurus(), type, notationValue, "", false, idUser);

            //sub group
            String idSubGroup;
            //concept group concept
            String idSubConcept;
            for (SKOSRelation relation : group.getRelationsList()) {
                int prop = relation.getProperty();
                switch (prop) {
                    case SKOSProperty.subGroup:
                        idSubGroup = getIdFromUri(relation.getTargetUri());
                        groupHelper.addSubGroup(ds, idGroup, idSubGroup, thesaurus.getId_thesaurus());
                        break;
                    case SKOSProperty.member:
                        // option cochée
                     /*   if(identifierType.equalsIgnoreCase("sans")){
                            idSubConcept = getIdFromUri(relation.getTargetUri());
                        } else {*/
                           // Récupération de l'Id d'origine sauvegardé à l'import (idArk -> identifier)
                            idSubConcept = getOriginalId(relation.getTargetUri());
                   //     }
                        groupHelper.addConceptGroupConcept(ds, idGroup, idSubConcept, thesaurus.getId_thesaurus());
                        memberHashMap.put(relation.getTargetUri(), idGroup);
                        break;
                    case SKOSProperty.hasTopConcept:
                        hasTopConcceptList.add(relation.getTargetUri());
                        break;
                    default:
                        break;
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
/*
        groupHelper.insertGroup(ds,
                idGroupDefault,
                thesaurus.getId_thesaurus(),
                "MT",
                "", //notation
                adressSite,
                useArk,
                idUser);
*/
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
     * Permet d'ajouter le Groupe par défaut pour les concepts qui sont orphelins
     * @return 
     */
    private void addGroupDefault(){
        if(defaultGroupToAdd) { 
            GroupHelper groupHelper = new GroupHelper();
            groupHelper.insertGroup(ds,
                    idGroupDefault,
                    thesaurus.getId_thesaurus(),
                    "MT",
                    "", //notation
                    "",
                    false,
                    idUser);

            // Création du domaine par défaut 
            // ajouter les traductions des Groupes
            ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
            conceptGroupLabel.setIdgroup(idGroupDefault);
            conceptGroupLabel.setIdthesaurus(thesaurus.getId_thesaurus());

            conceptGroupLabel.setLang(langueSource);
            conceptGroupLabel.setLexicalvalue("groupDefault");
            groupHelper.addGroupTraduction(ds, conceptGroupLabel, idUser);
        }
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
                    acs.conceptHelper.insertConceptInTable(ds, acs.concept, idUser);

                    new GroupHelper().addConceptGroupConcept(ds, idGroupDefault, acs.concept.getIdConcept(), acs.concept.getIdThesaurus());
                    defaultGroupToAdd = true;
                } else {
                    for (String idGrp : acs.idGrps) {
                        acs.concept.setTopConcept(acs.isTopConcept);
                        acs.concept.setIdGroup(idGrp);
                        
                        if(!acs.conceptHelper.insertConceptInTable(ds, acs.concept, idUser)){
                            System.out.println("Erreur sur le Concept = " + acs.concept.getIdConcept());
                        }
                    }
                }

                finalizeAddConceptStruct(acs);

            }
        }

        addLangsToThesaurus(ds, idTheso);
        addGroupDefault();
    }

    private void finalizeAddConceptStruct(AddConceptsStruct acs) throws SQLException {
        acs.termHelper.insertTerm(ds, acs.nodeTerm, idUser);

        RelationsHelper relationsHelper = new RelationsHelper();
       
        for (HierarchicalRelationship hierarchicalRelationship : acs.hierarchicalRelationships) {
            if(!relationsHelper.insertHierarchicalRelation(ds,
                    hierarchicalRelationship.getIdConcept1(),
                    hierarchicalRelationship.getIdThesaurus(),
                    hierarchicalRelationship.getRole(),
                    hierarchicalRelationship.getIdConcept2())) {
                //System.out.println("Erreur sur la relation = " + acs.concept.getIdConcept() + " ## " + hierarchicalRelationship.getRole());
                message.append(System.getProperty("line.separator"));
                message.append("Erreur sur la relation = ");
                message.append(acs.concept.getIdConcept());
                message.append(" ## ");
                message.append(hierarchicalRelationship.getRole());
            } 
        }

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
            acs.term.setHidden(nodeEMList1.isHiden());
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
        
        String idConcept = getOriginalId(conceptResource.getUri());
        acs.concept.setIdConcept(idConcept);
        
        // option cochée
        if(identifierType.equalsIgnoreCase("ark")){
            acs.concept.setIdArk(getIdArkFromUri(conceptResource.getUri()));
        }
        if(identifierType.equalsIgnoreCase("handle")){
            acs.concept.setIdHandle(getIdHandleFromUri(conceptResource.getUri()));
        }

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
        String idConcept2;
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
                case SKOSProperty.narrowerInstantial:
                    role = "NTI";
                    break;
                case SKOSProperty.related:
                    role = "RT";
                    break;
                case SKOSProperty.relatedHasPart:
                    role = "RHP";
                    break;
                case SKOSProperty.relatedPartOf:
                    role = "RPO";
                    break;

                default:
                    role = "";

            }
            if (!role.equals("")) {
                hierarchicalRelationship.setIdConcept1(acs.concept.getIdConcept());
                // option cochée
                //if(identifierType.equalsIgnoreCase("sans")){
                //    idConcept2 = getIdFromUri(relation.getTargetUri());
                //} else {
                   // Récupération des Id Ark ou Handle
                    idConcept2 = getOriginalId(relation.getTargetUri());
                //}
                
                hierarchicalRelationship.setIdConcept2(idConcept2);
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
        String idConcept2;
        
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
                case SKOSProperty.narrowerInstantial:
                    role = "NTI";
                    break;
                case SKOSProperty.broader:
                    role = "BT";
                    break;
                case SKOSProperty.broaderGeneric:
                    role = "BTG";
                    break;
                case SKOSProperty.broaderInstantial:
                    role = "BTI";
                    break;
                case SKOSProperty.broaderPartitive:
                    role = "BTP";
                    break;
                case SKOSProperty.related:
                    role = "RT";
                    break;
                case SKOSProperty.relatedHasPart:
                    role = "RHP";
                    break;
                case SKOSProperty.relatedPartOf:
                    role = "RPO";
                    break;
                default:
                    role = "";
            }

            if (!role.equals("")) {
                hierarchicalRelationship.setIdConcept1(acs.concept.getIdConcept());
                
                // option cochée
                //if(identifierType.equalsIgnoreCase("sans")){
                //    idConcept2 = getIdFromUri(relation.getTargetUri());
                //} else {
                   // Récupération des Id Ark ou Handle
                    idConcept2 = getOriginalId(relation.getTargetUri());
                //}
                hierarchicalRelationship.setIdConcept2(idConcept2);
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
            String uri = acs.conceptResource.getUri();
            String idPere = memberHashMap.get(uri);

            if (idPere != null) {
                acs.idGrps.add(idPere);
                memberHashMap.remove(uri);
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

    private static String getIdFromUri(String uri) {
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
    
    /**
     * Permet de retourner l'id d'origine d'un concept SKOS
     * en s'appuyant sur le DC:identifier sauvegardé à l'import
     * Si l'identifiant d'origine n'a pas été trouvé, c'est l'ID de l'URI qui est récupéré
     *     
     * @return 
     * #MR
     */
    private String getOriginalId(String uri) {
        String originalId;
        if(skosXmlDocument.getEquivalenceUriArkHandle().isEmpty() ||
                skosXmlDocument.getEquivalenceUriArkHandle().get(uri) == null)
            return getIdFromUri(uri);

        originalId = skosXmlDocument.getEquivalenceUriArkHandle().get(uri).toString();
        if(originalId == null) {
            if(message.length() != 0)
                message.append(System.getProperty("line.separator"));
            message.append("Identifiant (DC:Identifier) non détecté pour l'URL:");
            message.append(uri);
            originalId = getIdFromUri(uri);
            return originalId;
        }
        return originalId;
    }
    
    private String getIdArkFromUri(String uri) {
        // URI de type Ark
        String id = null;
        if (uri.contains("ark:/")) {
            id = uri.substring(uri.indexOf("ark:/") + 5, uri.length());
        } 
        return id;
    }
    
    private String getIdHandleFromUri(String uri) {
        // URI de type Handle
        String id = null;
        if (uri.contains(prefixHandle)) {
            id = uri.substring(uri.indexOf(prefixHandle), uri.length());
        } 
        return id;
    }    

    public void addLangsToThesaurus(HikariDataSource ds, String idThesaurus) {

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        ArrayList<String> tabListLang = thesaurusHelper.getAllUsedLanguagesOfThesaurus(ds, idThesaurus);
        for (int i = 0; i < tabListLang.size(); i++) {
            if (!thesaurusHelper.isLanguageExistOfThesaurus(ds, idThesaurus, tabListLang.get(i).trim())) {
                Thesaurus thesaurus1 = new Thesaurus();
                thesaurus1.setId_thesaurus(idThesaurus);
                thesaurus1.setContributor("");
                thesaurus1.setCoverage("");
                thesaurus1.setCreator("");
                thesaurus1.setDescription("");
                thesaurus1.setFormat("");
                thesaurus1.setLanguage(tabListLang.get(i));
                thesaurus1.setPublisher("");
                thesaurus1.setRelation("");
                thesaurus1.setRights("");
                thesaurus1.setSource("");
                thesaurus1.setSubject("");
                thesaurus1.setTitle("theso_" + idThesaurus);
                thesaurus1.setType("");
                thesaurusHelper.addThesaurusTraduction(ds, thesaurus1);
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

    public NodePreference getNodePreference() {
        return nodePreference;
    }

    public void setNodePreference(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

    public StringBuilder getMessage() {
        return message;
    }

    public void setMessage(StringBuilder message) {
        this.message = message;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getPrefixHandle() {
        return prefixHandle;
    }

    public void setPrefixHandle(String prefixHandle) {
        this.prefixHandle = prefixHandle;
    }

}
