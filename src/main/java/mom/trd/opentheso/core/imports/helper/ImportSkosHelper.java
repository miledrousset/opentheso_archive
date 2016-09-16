/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.helper;


import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.SelectedBeans.FileBean;
import mom.trd.opentheso.SelectedBeans.FileBean_progress;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.datas.DcElement;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.ToolsHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTerm;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.tools.StringPlus;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSConceptScheme;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSTypedLiteral;
import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;
import uk.ac.manchester.cs.skos.SKOSDatasetImpl;

/**
 *
 * @author miled.rousset
 */
public class ImportSkosHelper {

    private SKOSDataset dataSet;
    private OWLOntology onto = null;
    private SKOSManager sKOSManager;
    
    private final Map<URI, SKOSDatasetImpl> skosVocabularies;
    private final OWLOntologyManager ontologieManager;
    private String message = "";
    
    private int conceptsCount;
    private final ArrayList <String> idTopConcept;
    private ArrayList<String> idGroups; 
    private String idGroupDefault;
    
    private final Thesaurus thesaurus;
    private String langueSource;
    private HikariDataSource ds;
    private String formatDate;
    private String adressSite;    
    private int idUser;   
    private boolean useArk;

    public ImportSkosHelper() {
        this.ontologieManager = OWLManager.createOWLOntologyManager();
        skosVocabularies = new HashMap<>();
        thesaurus = new Thesaurus();
        idTopConcept = new ArrayList<>();
        idGroups = new ArrayList<>();
        idGroupDefault = "";
        dataSet = null;
    }
    
    /**
     * initialisation des paramètres d'import
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
            String langueSource) {
        this.ds = ds;
        this.formatDate = formatDate;
        this.useArk = useArk;
        this.adressSite = adressSite;
        this.idUser = idUser;
        this.langueSource = langueSource;

        return true;
    }
    
    /**
     * Cette fonction a été modifié pour pouvoir lire un fichier SKOS en passant par un
     * InputStream, l'API standard SKOSAPI n'a pas de constructeur adapté
     * cette fonction permet de lire tout type de fichier SKOS-OWL en s'appuyant 
     * sur l'api (OWLAPI-SKOSAPI)
     * 
     * @param streamFilename
     * @param fileName
     * @return
     * @throws OWLOntologyCreationException
     * @throws SKOSCreationException 
     */
    public boolean readFile(InputStream streamFilename, String fileName
            ) throws OWLOntologyCreationException, SKOSCreationException {

        try {
            sKOSManager = new SKOSManager();
            onto = ontologieManager.loadOntologyFromOntologyDocument(streamFilename);
            SKOSDatasetImpl voc = new SKOSDatasetImpl(sKOSManager, onto);
            if (voc.getURI() != null) {
                String theso = "";
                for (SKOSConceptScheme scheme : voc.getSKOSConceptSchemes()){
                    theso = theso + " " + scheme.getURI();
                }
                message = "new ontology loaded: " + theso;
                    //    voc.getAsOWLOntology().getOntologyID();
              //  System.out.println("new ontology loaded: " + voc.getAsOWLOntology().getOntologyID());
            skosVocabularies.put(voc.getURI(), voc);
            dataSet = voc;
            return true;
        }
        }catch (SKOSCreationException ex) {
            Logger.getLogger(ImportSkosHelper.class.getName()).log(Level.SEVERE, null, ex);
            message = ex.getMessage();
        }
        
        return false;
    }

    /**
     * Fonction qui permet d'importer les informations d'un thésaurus
     * @return 
     */
    public boolean addThesaurus() {

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();

        idGroupDefault = getNewId();
        for (SKOSConceptScheme scheme : dataSet.getSKOSConceptSchemes()) {
            // récupération du nom de thésaurus
            thesaurus.setTitle(getIdFromUri(scheme.getURI().toString()));
            conceptsCount = dataSet.getSKOSConcepts().size();
            for (SKOSAnnotation anno : scheme.getSKOSAnnotations(dataSet)) {

                if (anno.isAnnotationByConstant()) {
                    if (anno.getAnnotationValueAsConstant().isTyped()) {
                        SKOSTypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                    //    System.err.print(con.getLiteral() + " Type: " + con.getDataType().getURI());
                    }
                    else {
                        SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                    //    System.err.print(con.getLiteral());
                        // title

                        if(anno.getURI().toString().contains("title") ){
                            DcElement dcElement = new DcElement();
                            dcElement.setName("title");
                            dcElement.setValue(con.getLiteral());
                            if (con.hasLang()) {
                                dcElement.setLanguage(con.getLang());
                            }else
                                dcElement.setLanguage(langueSource);
                            thesaurus.addDcElement(dcElement);
                        }
                        else {
                            DcElement dcElement = new DcElement();
                            dcElement.setName("title");
                            dcElement.setValue(con.getLiteral());
                            if (con.hasLang()) {
                                dcElement.setLanguage(con.getLang());
                            }else
                                dcElement.setLanguage(langueSource);
                            thesaurus.addDcElement(dcElement);
                        }

                    }
    //              * created or date;
//                    if(anno.getURI().getPath().contains("date") ){
//                        getThesaurusAnnotation(anno);
//                    }
//    //              * modified;
//                    if(anno.getURI().getPath().contains("modified") ){
//                        getThesaurusAnnotation(anno);
//                    }
    //                * @param coverage
    //                * @param creator
    //                * @param description
    //                * @param format
    //                * @param id_langue
    //                * @param publisher
    //                * @param relation
    //                * @param rights
    //                * @param source
    //                * @param subject
    //                * @param title
    //                * @param type


                }
                // la liste des TopConcept
                else {
                    idTopConcept.add(getIdFromUri(anno.getAnnotationValue().getURI().toString()));
                    //System.err.println(anno.getAnnotationValue().getURI().toString());
                }
            }
            if(!addThesaurusToBdd(thesaurusHelper)) {
                return false;
            }
            
                               /* if(thesaurus.getTitle().isEmpty()) {
                        thesaurus.setTitle("theso_" + idThesaurus);
                    }
                    if(!addThesaurusTraductionRollBack(conn, thesaurus)) {
                        stmt.close();
                        return null;
                    }*/

            if(thesaurus.getDcElement().isEmpty()) {
                if(thesaurus.getTitle().isEmpty()) {
                        thesaurus.setTitle("theso_" + thesaurus.getId_thesaurus());
                }
                else {
                    thesaurus.setTitle(thesaurus.getTitle());
                }
                thesaurus.setLanguage(langueSource);
                thesaurusHelper.addThesaurusTraduction(ds, thesaurus);
            }
            else {
                for (DcElement dcElement : thesaurus.getDcElement()) {
                    thesaurus.setTitle(dcElement.getValue());
                    thesaurus.setLanguage(dcElement.getLanguage());
                    thesaurusHelper.addThesaurusTraduction(ds, thesaurus);
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean addDefaultThesaurus(){
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        thesaurus.setTitle("Theso1");
        if(thesaurus.getLanguage() == null) {
            thesaurus.setLanguage(langueSource);
        }
        if(!addThesaurusToBdd(thesaurusHelper)) {
            return false;
        }
        return true;
    }
    
    /**
     * cette fonction est privée, elle permet de créer le thésaurus dans la BDD et le role de l'utilisateur sur le thésaurus
     * @return 
     */
    private boolean addThesaurusToBdd(ThesaurusHelper thesaurusHelper){

        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            String idTheso;
            if(thesaurus.getLanguage() == null) {
                thesaurus.setLanguage(langueSource);
            }
            if( (idTheso = thesaurusHelper.addThesaurusRollBack(conn, thesaurus, adressSite, useArk)) == null){
                conn.rollback();
                conn.close();
                return false;
            }
            thesaurus.setId_thesaurus(idTheso);

            UserHelper userHelper = new UserHelper();
            int idRole = userHelper.getRoleOfUser(ds, idUser);

            if(!userHelper.addRole(conn, idUser, idRole, idTheso, "")) {
                conn.rollback();
                conn.close();
                return false;                    
            }
            conn.commit();
            conn.close();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ImportSkosHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void addLangsToThesaurus(HikariDataSource ds, String idThesaurus) {

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        Thesaurus thesaurus2 = new Thesaurus();
        ArrayList <String> tabListLang = thesaurusHelper.getAllUsedLanguagesOfThesaurus(ds, idThesaurus);
        
        for (String tabListLang1 : tabListLang) {
            if (!thesaurusHelper.isLanguageExistOfThesaurus(ds, idThesaurus, tabListLang1)) {
                thesaurus2.setId_thesaurus(idThesaurus);
                thesaurus2.setContributor("");
                thesaurus2.setCoverage("");
                thesaurus2.setCreator("");
                thesaurus2.setDescription("");
                thesaurus2.setFormat("");
                thesaurus2.setLanguage(tabListLang1);
                thesaurus2.setPublisher("");
                thesaurus2.setRelation("");
                thesaurus2.setRights("");
                thesaurus2.setSource("");
                thesaurus2.setSubject("");
                thesaurus2.setTitle("theso_" + idThesaurus);
                thesaurus2.setType("");
                thesaurusHelper.addThesaurusTraduction(ds, thesaurus2);
            }
        }
    }    
    
    /**
     * recupération des groups, subgroups 
     * @return 
     */    
    public boolean addGroups() {
        // récupération des groups ou domaine
        
        GroupHelper groupHelper = new GroupHelper();
        
        for (String idGroup : idGroups) {
            groupHelper.insertGroup(ds,
                    idGroup,
                    thesaurus.getId_thesaurus(),
                    "MT",
                    "", //notation 
                    adressSite,
                    useArk,
                    idUser); 
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
        
        
        // partie pour Emmanuelle temporaire 
    /*    
        GroupHelper groupHelper = new GroupHelper();
        idGroup = new ArrayList<>();//getGroups();
        idGroup.add("1");
        groupHelper.insertGroup(ds, idGroup.get(0),
                thesaurus.getId_thesaurus(), "MT", "", "", "",
                adressSite, useArk, idUser);
        
      // ajouter les traductions des Groupes
        ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
            conceptGroupLabel.setIdgroup(idGroup.get(0));
            conceptGroupLabel.setIdthesaurus(thesaurus.getId_thesaurus());
            conceptGroupLabel.setLang(langueSource);
            conceptGroupLabel.setLexicalvalue("group1");

        groupHelper.addGroupTraduction(ds, conceptGroupLabel, idUser);
    */
        return true;
    }
    
    /**
     * récupération des concepts 
     * @return 
     */
    public boolean addConcepts() {
        String uri;
        Value value;
        boolean isTopConcept = true;
        Concept concept = new Concept();
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        NoteHelper noteHelper = new NoteHelper();
        Term term = new Term();
        AlignmentHelper alignmentHelper = new AlignmentHelper();        
        
        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        ArrayList<NodeTermTraduction> nodeTermTraductionList = new ArrayList<>();

        //Enregister les synonymes et traductions
        ArrayList<NodeEM> nodeEMList =  new ArrayList<>();  
        
        // ajout des notes
        ArrayList <NodeNote> nodeNotes = new ArrayList<>();
        
        // ajout des alignements 
        ArrayList <NodeAlignment> nodeAlignments = new ArrayList<>();
        
        //ajout des relations 
        ArrayList <HierarchicalRelationship> hierarchicalRelationships = new ArrayList<>();
        
        // ajout des relations Groups
        
        ArrayList <String> idGrps = new ArrayList<>();
        
        if(dataSet.getSKOSConcepts().isEmpty())
            return false;
        

        conceptsCount = dataSet.getSKOSConcepts().size();
        // i can get all the concepts from this scheme
        for (SKOSConcept conceptsInScheme : dataSet.getSKOSConcepts()) {
            // URI du Concept récupération automatique de l'identifiant
            String id = getIdFromUri(conceptsInScheme.getURI().toString());
            
            if(id == null || id.isEmpty()) {
                message = message + "identifiant null pour l'URI : " + conceptsInScheme.getURI().toString();
                continue;
            }
            else {
                concept.setIdConcept(id);
            }
            concept.setIdThesaurus(thesaurus.getId_thesaurus());
//            concept.setIdGroup(idGroup.get(0));
            concept.setNotation("");
            
                        // skos:notation
                       /* if(anno.getURI().getFragment().equalsIgnoreCase("notation")) {
                            value = getValue(anno);
                            NodeTermTraduction nodeTermTraduction = new NodeTermTraduction();
                            nodeTermTraduction.setLexicalValue(value.getValue());
                            nodeTermTraduction.setLang(value.getLang());
                            nodeTermTraductionList.add(nodeTermTraduction);
                        }*/              
            
            concept.setStatus("");
            concept.setIdArk(conceptsInScheme.getURI().toString());

            for (SKOSAnnotation anno : conceptsInScheme.getSKOSAnnotations(dataSet)) {
                
                // c'est une valeur
                if (anno.isAnnotationByConstant()) {
                    
                    // balises SKOS
                    if(anno.getURI().getFragment() != null){
                        
                        // get notation
                        if(anno.getURI().getFragment().equalsIgnoreCase("notation")) {
                            value = getValue(anno);
                            concept.setNotation(value.getValue());
                        }                        
                        
                        // get altLabels
                        if(anno.getURI().getFragment().equalsIgnoreCase("prefLabel")) {
                            value = getValue(anno);
                            NodeTermTraduction nodeTermTraduction = new NodeTermTraduction();
                            nodeTermTraduction.setLexicalValue(value.getValue());
                            nodeTermTraduction.setLang(value.getLang());
                            nodeTermTraductionList.add(nodeTermTraduction);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("altLabel")) {
                            value = getValue(anno);
                            NodeEM nodeEM = new NodeEM();
                            nodeEM.setLexical_value(value.getValue());
                            nodeEM.setLang(value.getLang());    
                            nodeEM.setSource("" + idUser);
                            nodeEM.setStatus("USE");
                            nodeEM.setHiden(false);
                            nodeEMList.add(nodeEM);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("hiddenLabel")) {
                            value = getValue(anno);
                            NodeEM nodeEM = new NodeEM();
                            nodeEM.setLexical_value(value.getValue());
                            nodeEM.setLang(value.getLang());    
                            nodeEM.setSource("" + idUser);
                            nodeEM.setStatus("Hidden");
                            nodeEM.setHiden(true);
                            nodeEMList.add(nodeEM);
                        }
                      
                        
                        // get notes
                        if(anno.getURI().getFragment().equalsIgnoreCase("definition")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("definition");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("note")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("note");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("scopeNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("scopeNote");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("editorialNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("editorialNote");
                            nodeNotes.add(nodeNote); 
                        }                        
                        if(anno.getURI().getFragment().equalsIgnoreCase("historyNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("historyNote");
                            nodeNotes.add(nodeNote);
                        }                  
                    }
                    
                    // balises DublinCore dc
                    else{
                        uri = getIdFromUri(anno.getURI().toString());
                        if(uri.equalsIgnoreCase("created") || uri.equalsIgnoreCase("date")){
                            concept = addDates(concept, formatDate, getValue(anno).getValue(),
                                    "created");
                        }
                        if(uri.equalsIgnoreCase("modified")){
                            concept = addDates(concept, formatDate, getValue(anno).getValue(),
                                    "modified");
                        }
                        // get the identifier from dcterms ceci peut être aussi l'identifiant ark
                        if(uri.equalsIgnoreCase("identifier")) {
                            concept.setIdArk(getValue(anno).getValue());
                        }
                    }
                }
                // c'est une relation
                else {
                    // balises SKOS
                    if(anno.getURI().getFragment() != null){
                        // get relations hiérarchiques
                        if(anno.getURI().getFragment().equalsIgnoreCase("narrower")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("NT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("broader")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("BT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                            isTopConcept = false;
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("related")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("RT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                        }

                        // get scheme
                        if(anno.getURI().getFragment().equalsIgnoreCase("inScheme")) {
                        //    uri = anno.getAnnotationValue().getURI().toString();
                        }
                        
                        //get Groups
                        if(anno.getURI().getFragment().equalsIgnoreCase("memberOf")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            idGrps.add(uri);
                            addIdGroupToVector(uri);
                        }
                        
                        // get Alignements
                        if(anno.getURI().getFragment().equalsIgnoreCase("closeMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(2);
                            nodeAlignments.add(nodeAlignment);

                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("exactMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(1);
                            nodeAlignments.add(nodeAlignment);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("broadMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(3);
                            nodeAlignments.add(nodeAlignment);
                        }        
                        if(anno.getURI().getFragment().equalsIgnoreCase("narrowMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(5);
                            nodeAlignments.add(nodeAlignment);
                        } 
                        if(anno.getURI().getFragment().equalsIgnoreCase("relatedMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(4);
                            nodeAlignments.add(nodeAlignment);
                        }                         
                    }                    
                }
            }
            
            //ajout des termes et traductions
            nodeTerm.setNodeTermTraduction(nodeTermTraductionList);
            nodeTerm.setIdTerm(concept.getIdConcept());
            nodeTerm.setIdConcept(concept.getIdConcept());

            nodeTerm.setIdThesaurus(thesaurus.getId_thesaurus());
            nodeTerm.setSource("");
            nodeTerm.setStatus("");
            nodeTerm.setCreated(concept.getCreated());
            nodeTerm.setModified(concept.getModified());
            
            // envoie du concept à la BDD

            // conctrole si le concept est vide aucun prefLable, on l'ignore
            if(!isConceptEmpty(nodeTermTraductionList)) {
                if(idGrps.isEmpty()) {
                    concept.setTopConcept(isTopConcept);
                    concept.setIdGroup(idGroupDefault);
                    conceptHelper.insertConceptInTable(ds, concept,
                            adressSite, useArk, idUser);
                }
                else {
                    for (String idGrp : idGrps) {
                        concept.setTopConcept(isTopConcept);
                        concept.setIdGroup(idGrp);
                        conceptHelper.insertConceptInTable(ds, concept,
                                adressSite, useArk, idUser);
                    }
                }


                termHelper.insertTerm(ds, nodeTerm, idUser);

                try {
                    Connection conn = ds.getConnection();
                    conn.setAutoCommit(false);

                    for (HierarchicalRelationship hierarchicalRelationship : hierarchicalRelationships) {
                        conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser);

                    }
                    conn.commit();
                    conn.close();
                }
                catch (SQLException ex) {

                }

                for (NodeNote nodeNoteList1 : nodeNotes) {
                    if(nodeNoteList1.getNotetypecode().contains("scopeNote")){
                        noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("historyNote")){
                        noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("definition")){
                        noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("editorialNote")){
                        noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("note")){
                        noteHelper.addConceptNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }                
                }

                for (NodeAlignment nodeAlignment : nodeAlignments) {
                    alignmentHelper.addNewAlignment(ds,nodeAlignment);
                }

                for (NodeEM nodeEMList1 : nodeEMList) {
                    term.setId_concept(concept.getIdConcept());
                    term.setId_term(nodeTerm.getIdTerm());
                    term.setLexical_value(nodeEMList1.getLexical_value());
                    term.setLang(nodeEMList1.getLang());
                    term.setId_thesaurus(thesaurus.getId_thesaurus());
                    term.setSource(nodeEMList1.getSource());
                    term.setStatus(nodeEMList1.getStatus());
                    termHelper.addNonPreferredTerm(ds, term, idUser);                
                }
            }
            
            
            // initialisation des variables
            concept = new Concept();
            term = new Term();
            nodeTerm = new NodeTerm();
            nodeTermTraductionList = new ArrayList<>();
            nodeEMList =  new ArrayList<>();
            nodeNotes = new ArrayList<>();
            nodeAlignments = new ArrayList<>();
            hierarchicalRelationships = new ArrayList<>();
            idGrps = new ArrayList<>();
            isTopConcept = true;
        }
               
        // insérrer les TopConcepts

        for (String idTopConcept1 : idTopConcept) {
            if(!conceptHelper.setTopConcept(ds, idTopConcept1, thesaurus.getId_thesaurus())){
                // erreur;
            }                
        }
        
        addGroups();
        
        addLangsToThesaurus(ds, thesaurus.getId_thesaurus());
        message = message + "\n nombre de Concepts importés : " + conceptsCount;
        return true;
    }    
    
    /**
     * récupération des concepts 
     * @param fileBean
     * @return 
     */
    public boolean addConcepts_progress(FileBean fileBean) {
        String uri;
        Value value;
        boolean isTopConcept = true;
        Concept concept = new Concept();
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        NoteHelper noteHelper = new NoteHelper();
        Term term = new Term();
        AlignmentHelper alignmentHelper = new AlignmentHelper();        
        
        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        ArrayList<NodeTermTraduction> nodeTermTraductionList = new ArrayList<>();

        //Enregister les synonymes et traductions
        ArrayList<NodeEM> nodeEMList =  new ArrayList<>();  
        
        // ajout des notes
        ArrayList <NodeNote> nodeNotes = new ArrayList<>();
        
        // ajout des alignements 
        ArrayList <NodeAlignment> nodeAlignments = new ArrayList<>();
        
        //ajout des relations 
        ArrayList <HierarchicalRelationship> hierarchicalRelationships = new ArrayList<>();
        
        // ajout des relations Groups
        
        ArrayList <String> idGrps = new ArrayList<>();
        
        if(dataSet.getSKOSConcepts().isEmpty())
            return false;
        
        int i=0;
        // i can get all the concepts from this scheme
        for (SKOSConcept conceptsInScheme : dataSet.getSKOSConcepts()) {
            // URI du Concept récupération automatique de l'identifiant
            String id = getIdFromUri(conceptsInScheme.getURI().toString());
            if(id == null || id.isEmpty()) {
                message = message + "identifiant null pour l'URI : " + conceptsInScheme.getURI().toString();
                continue;
            }
            else {
                concept.setIdConcept(id);
            }
            concept.setIdThesaurus(thesaurus.getId_thesaurus());
//            concept.setIdGroup(idGroup.get(0));
            concept.setNotation("");
            concept.setStatus("");
            concept.setIdArk(conceptsInScheme.getURI().toString());

            for (SKOSAnnotation anno : conceptsInScheme.getSKOSAnnotations(dataSet)) {
                
                // c'est une valeur
                if (anno.isAnnotationByConstant()) {
                    
                    // balises SKOS
                    if(anno.getURI().getFragment() != null){
                        
                        // get altLabels
                        if(anno.getURI().getFragment().equalsIgnoreCase("prefLabel")) {
                            value = getValue(anno);
                            NodeTermTraduction nodeTermTraduction = new NodeTermTraduction();
                            nodeTermTraduction.setLexicalValue(value.getValue());
                            nodeTermTraduction.setLang(value.getLang());
                            nodeTermTraductionList.add(nodeTermTraduction);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("altLabel")) {
                            value = getValue(anno);
                            NodeEM nodeEM = new NodeEM();
                            nodeEM.setLexical_value(value.getValue());
                            nodeEM.setLang(value.getLang());    
                            nodeEM.setSource("" + idUser);
                            nodeEM.setStatus("USE");
                            nodeEM.setHiden(false);
                            nodeEMList.add(nodeEM);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("hiddenLabel")) {
                            value = getValue(anno);
                            NodeEM nodeEM = new NodeEM();
                            nodeEM.setLexical_value(value.getValue());
                            nodeEM.setLang(value.getLang());    
                            nodeEM.setSource("" + idUser);
                            nodeEM.setStatus("Hidden");
                            nodeEM.setHiden(true);
                            nodeEMList.add(nodeEM);
                        }
                        
                        // get notes
                        if(anno.getURI().getFragment().equalsIgnoreCase("definition")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("definition");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("note")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("note");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("scopeNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("scopeNote");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("editorialNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("editorialNote");
                            nodeNotes.add(nodeNote); 
                        }                        
                        if(anno.getURI().getFragment().equalsIgnoreCase("historyNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("historyNote");
                            nodeNotes.add(nodeNote);
                        }                  
                    }
                    
                    // balises DublinCore dc
                    else{
                        uri = getIdFromUri(anno.getURI().toString());
                        if(uri.equalsIgnoreCase("created") || uri.equalsIgnoreCase("date")){
                            concept = addDates(concept, formatDate, getValue(anno).getValue(),
                                    "created");
                        }
                        if(uri.equalsIgnoreCase("modified")){
                            concept = addDates(concept, formatDate, getValue(anno).getValue(),
                                    "modified");
                        }
                        // get the identifier from dcterms ceci peut être aussi l'identifiant ark
                        if(uri.equalsIgnoreCase("identifier")) {
                            concept.setIdArk(getValue(anno).getValue());
                        }
                    }
                }
                // c'est une relation
                else {
                    // balises SKOS
                    if(anno.getURI().getFragment() != null){
                        // get relations hiérarchiques
                        if(anno.getURI().getFragment().equalsIgnoreCase("narrower")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("NT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("broader")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("BT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                            isTopConcept = false;
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("related")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("RT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                        }

                        // get scheme
                        if(anno.getURI().getFragment().equalsIgnoreCase("inScheme")) {
                        //    uri = anno.getAnnotationValue().getURI().toString();
                        }
                        
                        //get Groups
                        if(anno.getURI().getFragment().equalsIgnoreCase("memberOf")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            idGrps.add(uri);
                            addIdGroupToVector(uri);
                        }
                        
                        // get Alignements
                        if(anno.getURI().getFragment().equalsIgnoreCase("closeMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(2);
                            nodeAlignments.add(nodeAlignment);

                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("exactMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(1);
                            nodeAlignments.add(nodeAlignment);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("broadMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(3);
                            nodeAlignments.add(nodeAlignment);
                        }        
                        if(anno.getURI().getFragment().equalsIgnoreCase("narrowMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(5);
                            nodeAlignments.add(nodeAlignment);
                        } 
                        if(anno.getURI().getFragment().equalsIgnoreCase("relatedMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(4);
                            nodeAlignments.add(nodeAlignment);
                        }                         
                    }                    
                }
            }
            
            //ajout des termes et traductions
            nodeTerm.setNodeTermTraduction(nodeTermTraductionList);
            nodeTerm.setIdTerm(concept.getIdConcept());
            nodeTerm.setIdConcept(concept.getIdConcept());

            nodeTerm.setIdThesaurus(thesaurus.getId_thesaurus());
            nodeTerm.setSource("");
            nodeTerm.setStatus("");
            nodeTerm.setCreated(concept.getCreated());
            nodeTerm.setModified(concept.getModified());
            
            // envoie du concept à la BDD

            // conctrole si le concept est vide aucun prefLable, on l'ignore
            if(!isConceptEmpty(nodeTermTraductionList)) {
                if(idGrps.isEmpty()) {
                    concept.setTopConcept(isTopConcept);
                    concept.setIdGroup(idGroupDefault);
                    conceptHelper.insertConceptInTable(ds, concept,
                            adressSite, useArk, idUser);
                }
                else {
                    for (String idGrp : idGrps) {
                        concept.setTopConcept(isTopConcept);
                        concept.setIdGroup(idGrp);
                        conceptHelper.insertConceptInTable(ds, concept,
                                adressSite, useArk, idUser);
                    }
                }


                termHelper.insertTerm(ds, nodeTerm, idUser);

                try {
                    Connection conn = ds.getConnection();
                    conn.setAutoCommit(false);

                    for (HierarchicalRelationship hierarchicalRelationship : hierarchicalRelationships) {
                        conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser);

                    }
                    conn.commit();
                    conn.close();
                }
                catch (SQLException ex) {

                }

                for (NodeNote nodeNoteList1 : nodeNotes) {
                    if(nodeNoteList1.getNotetypecode().contains("scopeNote")){
                        noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("historyNote")){
                        noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("definition")){
                        noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("editorialNote")){
                        noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("note")){
                        noteHelper.addConceptNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }                
                }

                for (NodeAlignment nodeAlignment : nodeAlignments) {
                    alignmentHelper.addNewAlignment(ds,nodeAlignment);
                }

                for (NodeEM nodeEMList1 : nodeEMList) {
                    term.setId_concept(concept.getIdConcept());
                    term.setId_term(nodeTerm.getIdTerm());
                    term.setLexical_value(nodeEMList1.getLexical_value());
                    term.setLang(nodeEMList1.getLang());
                    term.setId_thesaurus(thesaurus.getId_thesaurus());
                    term.setSource(nodeEMList1.getSource());
                    term.setStatus(nodeEMList1.getStatus());
                    termHelper.addNonPreferredTerm(ds, term, idUser);                
                }
            }
            
            
            // initialisation des variables
            concept = new Concept();
            term = new Term();
            nodeTerm = new NodeTerm();
            nodeTermTraductionList = new ArrayList<>();
            nodeEMList =  new ArrayList<>();
            nodeNotes = new ArrayList<>();
            nodeAlignments = new ArrayList<>();
            hierarchicalRelationships = new ArrayList<>();
            idGrps = new ArrayList<>();
            isTopConcept = true;
            
            if(conceptsCount < 100) {
                fileBean.setProgress(i*(100/conceptsCount));
            }
            else {
                fileBean.setProgress(i/(conceptsCount/100));
            } 
         //   System.out.println("TestFunction - setting progress to: " + i);
            
            try {
                Thread.sleep(200);
                if(fileBean.getProgress() == null) {
                    fileBean.setProgress(100);
                    return false;
                }
            } catch (InterruptedException e) {
            }
            i++;
            
        }
               
        // insérrer les TopConcepts

        for (String idTopConcept1 : idTopConcept) {
            if(!conceptHelper.setTopConcept(ds, idTopConcept1, thesaurus.getId_thesaurus())){
                // erreur;
            }                
        }
        
        addGroups();
        
        addLangsToThesaurus(ds, thesaurus.getId_thesaurus());
        message = message + "\n nombre de Concepts importés : " + conceptsCount;
        fileBean.setProgress(100);
     //   System.out.println("Finished Function");
        return true;
    }
    
    /**
     * récupération des concepts 
     * @param fileBean
     * @return 
     */
    public boolean addConcepts_progress(FileBean_progress fileBean) {
        String uri;
        Value value;
        boolean isTopConcept = true;
        Concept concept = new Concept();
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        NoteHelper noteHelper = new NoteHelper();
        Term term = new Term();
        AlignmentHelper alignmentHelper = new AlignmentHelper();        
        
        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        ArrayList<NodeTermTraduction> nodeTermTraductionList = new ArrayList<>();

        //Enregister les synonymes et traductions
        ArrayList<NodeEM> nodeEMList =  new ArrayList<>();  
        
        // ajout des notes
        ArrayList <NodeNote> nodeNotes = new ArrayList<>();
        
        // ajout des alignements 
        ArrayList <NodeAlignment> nodeAlignments = new ArrayList<>();
        
        //ajout des relations 
        ArrayList <HierarchicalRelationship> hierarchicalRelationships = new ArrayList<>();
        
        // ajout des relations Groups
        
        ArrayList <String> idGrps = new ArrayList<>();
        
        if(dataSet.getSKOSConcepts().isEmpty())
            return false;
        
        int i=0;
        // i can get all the concepts from this scheme
        for (SKOSConcept conceptsInScheme : dataSet.getSKOSConcepts()) {
            // URI du Concept récupération automatique de l'identifiant
            String id = getIdFromUri(conceptsInScheme.getURI().toString());
            if(id == null || id.isEmpty()) {
                message = message + "identifiant null pour l'URI : " + conceptsInScheme.getURI().toString();
                continue;
            }
            else {
                concept.setIdConcept(id);
            }
            concept.setIdThesaurus(thesaurus.getId_thesaurus());
//            concept.setIdGroup(idGroup.get(0));
            concept.setNotation("");
            concept.setStatus("");
            concept.setIdArk(conceptsInScheme.getURI().toString());

            for (SKOSAnnotation anno : conceptsInScheme.getSKOSAnnotations(dataSet)) {
                
                // c'est une valeur
                if (anno.isAnnotationByConstant()) {
                    
                    // balises SKOS
                    if(anno.getURI().getFragment() != null){
                        
                        // get altLabels
                        if(anno.getURI().getFragment().equalsIgnoreCase("prefLabel")) {
                            value = getValue(anno);
                            NodeTermTraduction nodeTermTraduction = new NodeTermTraduction();
                            nodeTermTraduction.setLexicalValue(value.getValue());
                            nodeTermTraduction.setLang(value.getLang());
                            nodeTermTraductionList.add(nodeTermTraduction);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("altLabel")) {
                            value = getValue(anno);
                            NodeEM nodeEM = new NodeEM();
                            nodeEM.setLexical_value(value.getValue());
                            nodeEM.setLang(value.getLang());    
                            nodeEM.setSource("" + idUser);
                            nodeEM.setStatus("USE");
                            nodeEM.setHiden(false);
                            nodeEMList.add(nodeEM);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("hiddenLabel")) {
                            value = getValue(anno);
                            NodeEM nodeEM = new NodeEM();
                            nodeEM.setLexical_value(value.getValue());
                            nodeEM.setLang(value.getLang());    
                            nodeEM.setSource("" + idUser);
                            nodeEM.setStatus("Hidden");
                            nodeEM.setHiden(true);
                            nodeEMList.add(nodeEM);
                        }
                        
                        // get notes
                        if(anno.getURI().getFragment().equalsIgnoreCase("definition")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("definition");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("note")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("note");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("scopeNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("scopeNote");
                            nodeNotes.add(nodeNote);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("editorialNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("editorialNote");
                            nodeNotes.add(nodeNote); 
                        }                        
                        if(anno.getURI().getFragment().equalsIgnoreCase("historyNote")) {
                            value = getValue(anno);
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setLang(value.getLang());
                            nodeNote.setLexicalvalue(value.getValue());
                            nodeNote.setNotetypecode("historyNote");
                            nodeNotes.add(nodeNote);
                        }                  
                    }
                    
                    // balises DublinCore dc
                    else{
                        uri = getIdFromUri(anno.getURI().toString());
                        if(uri.equalsIgnoreCase("created") || uri.equalsIgnoreCase("date")){
                            concept = addDates(concept, formatDate, getValue(anno).getValue(),
                                    "created");
                        }
                        if(uri.equalsIgnoreCase("modified")){
                            concept = addDates(concept, formatDate, getValue(anno).getValue(),
                                    "modified");
                        }
                        // get the identifier from dcterms ceci peut être aussi l'identifiant ark
                        if(uri.equalsIgnoreCase("identifier")) {
                            concept.setIdArk(getValue(anno).getValue());
                        }
                    }
                }
                // c'est une relation
                else {
                    // balises SKOS
                    if(anno.getURI().getFragment() != null){
                        // get relations hiérarchiques
                        if(anno.getURI().getFragment().equalsIgnoreCase("narrower")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("NT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("broader")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("BT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                            isTopConcept = false;
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("related")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
                            hierarchicalRelationship.setIdConcept1(concept.getIdConcept());
                            hierarchicalRelationship.setIdConcept2(uri);
                            hierarchicalRelationship.setIdThesaurus(thesaurus.getId_thesaurus());
                            hierarchicalRelationship.setRole("RT");
                            hierarchicalRelationships.add(hierarchicalRelationship);
                        }

                        // get scheme
                        if(anno.getURI().getFragment().equalsIgnoreCase("inScheme")) {
                        //    uri = anno.getAnnotationValue().getURI().toString();
                        }
                        
                        //get Groups
                        if(anno.getURI().getFragment().equalsIgnoreCase("memberOf")) {
                            uri = getIdFromUri(anno.getAnnotationValue().getURI().toString());
                            idGrps.add(uri);
                            addIdGroupToVector(uri);
                        }
                        
                        // get Alignements
                        if(anno.getURI().getFragment().equalsIgnoreCase("closeMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(2);
                            nodeAlignments.add(nodeAlignment);

                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("exactMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(1);
                            nodeAlignments.add(nodeAlignment);
                        }
                        if(anno.getURI().getFragment().equalsIgnoreCase("broadMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(3);
                            nodeAlignments.add(nodeAlignment);
                        }        
                        if(anno.getURI().getFragment().equalsIgnoreCase("narrowMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(5);
                            nodeAlignments.add(nodeAlignment);
                        } 
                        if(anno.getURI().getFragment().equalsIgnoreCase("relatedMatch")) {
                            uri = anno.getAnnotationValue().getURI().toString();
                            NodeAlignment nodeAlignment = new NodeAlignment();
                            nodeAlignment.setId_author(idUser);
                            nodeAlignment.setConcept_target("");
                            nodeAlignment.setThesaurus_target("");
                            nodeAlignment.setUri_target(uri);
                            nodeAlignment.setInternal_id_concept(concept.getIdConcept());
                            nodeAlignment.setInternal_id_thesaurus(thesaurus.getId_thesaurus());
                            nodeAlignment.setAlignement_id_type(4);
                            nodeAlignments.add(nodeAlignment);
                        }                         
                    }                    
                }
            }
            
            //ajout des termes et traductions
            nodeTerm.setNodeTermTraduction(nodeTermTraductionList);
            nodeTerm.setIdTerm(concept.getIdConcept());
            nodeTerm.setIdConcept(concept.getIdConcept());

            nodeTerm.setIdThesaurus(thesaurus.getId_thesaurus());
            nodeTerm.setSource("");
            nodeTerm.setStatus("");
            nodeTerm.setCreated(concept.getCreated());
            nodeTerm.setModified(concept.getModified());
            
            // envoie du concept à la BDD

            // conctrole si le concept est vide aucun prefLable, on l'ignore
            if(!isConceptEmpty(nodeTermTraductionList)) {
                if(idGrps.isEmpty()) {
                    concept.setTopConcept(isTopConcept);
                    concept.setIdGroup(idGroupDefault);
                    conceptHelper.insertConceptInTable(ds, concept,
                            adressSite, useArk, idUser);
                }
                else {
                    for (String idGrp : idGrps) {
                        concept.setTopConcept(isTopConcept);
                        concept.setIdGroup(idGrp);
                        conceptHelper.insertConceptInTable(ds, concept,
                                adressSite, useArk, idUser);
                    }
                }


                termHelper.insertTerm(ds, nodeTerm, idUser);

                try {
                    Connection conn = ds.getConnection();
                    conn.setAutoCommit(false);

                    for (HierarchicalRelationship hierarchicalRelationship : hierarchicalRelationships) {
                        conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser);

                    }
                    conn.commit();
                    conn.close();
                }
                catch (SQLException ex) {

                }

                for (NodeNote nodeNoteList1 : nodeNotes) {
                    if(nodeNoteList1.getNotetypecode().contains("scopeNote")){
                        noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("historyNote")){
                        noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("definition")){
                        noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("editorialNote")){
                        noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }
                    if(nodeNoteList1.getNotetypecode().contains("note")){
                        noteHelper.addConceptNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(),
                                thesaurus.getId_thesaurus(), nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
                    }                
                }

                for (NodeAlignment nodeAlignment : nodeAlignments) {
                    alignmentHelper.addNewAlignment(ds,nodeAlignment);
                }

                for (NodeEM nodeEMList1 : nodeEMList) {
                    term.setId_concept(concept.getIdConcept());
                    term.setId_term(nodeTerm.getIdTerm());
                    term.setLexical_value(nodeEMList1.getLexical_value());
                    term.setLang(nodeEMList1.getLang());
                    term.setId_thesaurus(thesaurus.getId_thesaurus());
                    term.setSource(nodeEMList1.getSource());
                    term.setStatus(nodeEMList1.getStatus());
                    termHelper.addNonPreferredTerm(ds, term, idUser);                
                }
            }
            
            
            // initialisation des variables
            concept = new Concept();
            term = new Term();
            nodeTerm = new NodeTerm();
            nodeTermTraductionList = new ArrayList<>();
            nodeEMList =  new ArrayList<>();
            nodeNotes = new ArrayList<>();
            nodeAlignments = new ArrayList<>();
            hierarchicalRelationships = new ArrayList<>();
            idGrps = new ArrayList<>();
            isTopConcept = true;
            
            if(conceptsCount < 100) {
                fileBean.setProgress(i*(100/conceptsCount));
            }
            else {
                fileBean.setProgress(i/(conceptsCount/100));
            } 
         //   System.out.println("TestFunction - setting progress to: " + i);
            
            try {
                Thread.sleep(200);
                if(fileBean.getProgress() == null) {
                    fileBean.setProgress(100);
                    return false;
                }
            } catch (InterruptedException e) {
            }
            i++;
            
        }
               
        // insérrer les TopConcepts

        for (String idTopConcept1 : idTopConcept) {
            if(!conceptHelper.setTopConcept(ds, idTopConcept1, thesaurus.getId_thesaurus())){
                // erreur;
            }                
        }
        
        addGroups();
        
        addLangsToThesaurus(ds, thesaurus.getId_thesaurus());
        message = message + "\n nombre de Concepts importés : " + conceptsCount;
        fileBean.setProgress(100);
     //   System.out.println("Finished Function");
        return true;
    }    
    
    /**
     * test si le concept ne possède aucun PrefLabel, il sera ignorer 
     */
    private boolean isConceptEmpty (ArrayList<NodeTermTraduction> nodeTermTraductionList){
        return nodeTermTraductionList.isEmpty();
    }
    
    /**
     * permet d'ajouter l'id du group en éliminant les doublons
     * @param idGrp
     * @return 
     */
    private void addIdGroupToVector (String idGrp) {
        if(idGroups.contains(idGrp)) return;
        idGroups.add(idGrp);
    }
    
    /**
     * Permet de renseigner les dates au format accepté par SQL
     * @param concept
     * @param simpleFormatDate
     * @param typeDate
     * @return 
     */
    private Concept addDates(Concept concept, String simpleFormatDate,
            String date, String typeDate) {
        SimpleDateFormat formatDate1 = new SimpleDateFormat(simpleFormatDate);
        try {
            if(typeDate.equalsIgnoreCase("created")) {

                    concept.setCreated(formatDate1.parse(date));

            }
            if(typeDate.equalsIgnoreCase("modified")) {
                concept.setModified(formatDate1.parse(date));
            }  
        } catch (ParseException ex) {
            Logger.getLogger(ImportSkosHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return concept;
    }    
    
    private Value getValue(SKOSAnnotation anno){

        Value value = new Value(); 
        // valeurs ou données avec la relation SKOS = 
        // Annotation: http://www.w3.org/2004/02/skos/core#prefLabel-> arte@e
        if (anno.getAnnotationValueAsConstant().isTyped()) {
            SKOSTypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
            value.setValue(con.getLiteral());
          //  System.err.print(con.getLiteral() + " Type: " + con.getDataType().getURI());
        }
        else {
            SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
            value.setValue(con.getLiteral());
            if (con.hasLang()) {
                if(!con.getLang().isEmpty())
                    value.setLang(con.getLang());
                else
                    // la langue par defaut
                    value.setLang(langueSource);
            }
        }
        return value;
    }
    
    private String getNewId(){
        ConceptHelper conceptHelper = new ConceptHelper();
        ToolsHelper toolsHelper = new ToolsHelper();
        String id = toolsHelper.getNewId(10);
        while (conceptHelper.isIdExiste(ds, id, thesaurus.getId_thesaurus())) {
            id = toolsHelper.getNewId(10);
        }
        return id;
    }
    
    private String getIdFromUri(String uri) {
        if (uri.contains("idg=")) {
            if(uri.contains("&")){
                uri = uri.substring(uri.indexOf("idg=") + 4, uri.indexOf("&"));
            }
            else {
                uri = uri.substring(uri.indexOf("idg=") + 4, uri.length());
            }
        }
        else {
            if (uri.contains("idc=")) {
                if(uri.contains("&")){
                    uri = uri.substring(uri.indexOf("idc=") + 4, uri.indexOf("&"));
                }
                else {
                    uri = uri.substring(uri.indexOf("idc=") + 4, uri.length());
                }
            }
            else {
                if (uri.contains("#")) {
                    uri = uri.substring(uri.indexOf("#") + 1, uri.length());
                }
                else
                {
                    uri = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
                }
            }
        }
        
        
        StringPlus stringPlus = new StringPlus();
        uri =stringPlus.normalizeStringForIdentifier(uri);
        return uri;
    }  

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    

    public int getConceptsCount() {
        if(dataSet == null) return -1;
        conceptsCount = dataSet.getSKOSConcepts().size();
        
        return conceptsCount;
    }
    
    
    
}
