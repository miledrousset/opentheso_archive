package mom.trd.opentheso.core.imports.old;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTerm;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import skos.SKOSConceptScheme;
import skos.SKOSDate;
import skos.SKOSDocumentation;
import skos.SKOSLabel;
import skos.SKOSProperty;
import skos.SKOSRelation;
import skos.SKOSResource;
import skos.SKOSTopConcept;
import skos.SKOSXmlDocument;
import skos.SKOSMapping;

public class WriteBranchSkosBDD {

    private HikariDataSource ds;

    public WriteBranchSkosBDD(HikariDataSource ds) {
        this.ds = ds;
    }

    /**
     * Cette fonction permet d'importer une branche sous un Groupe/domaine donné
     * en intégrant automatiquement les alignements avec la source de la branche
     * @param idThesaurus
     * @param idGroup
     * @param skosDocument
     * @param dateFormat
     * @param useArk
     * @param adressSite 
     * @param idUser 
     */
    public void importBranchAfterGroup(
            String idGroup,
            String idThesaurus,
            SKOSXmlDocument skosDocument, String dateFormat,
            boolean useArk, String adressSite, int idUser) {
        
    //    SKOSConceptScheme conceptScheme = skosDocument.getConceptScheme();
    //    ArrayList<SKOSTopConcept> topConceptsList = conceptScheme.getTopConceptsList();
        
        boolean isfirst = true;
        
        ArrayList<SKOSResource> resourcesList = skosDocument.getResourcesList();
        
        for (SKOSResource resource : resourcesList) {
            
            // si c'est le premier Concept, on l'accroche à l'attache au concept père 
            // on ajoute les relations BT et NT pour faire la chaine
            if(isfirst) {
                writeFirstConceptAfterGroup(resource,
                    idGroup,
                    idThesaurus,
                    "fr", dateFormat, adressSite, useArk, idUser);
                isfirst = false;
                // on insère le premier concept sans les BT
            }
            else {
                writeConceptAfterGroup(resource,
                    idGroup,
                    idThesaurus,
                    "fr", dateFormat, adressSite, useArk, idUser);
            }
        }

    }    
    
    
    /**
     * Cette fonction permet d'importer une branche entière avec plusieurs TT sous un domaine / groupe
     * @param idThesaurus
     * @param idGroup
     * @param skosDocument
     * @param dateFormat
     * @param useArk
     * @param adressSite 
     * @param idUser 
     */
    public void importMultiBranchUnderGroup(
            String idThesaurus,
            String idGroup,
            SKOSXmlDocument skosDocument, String dateFormat,
            boolean useArk, String adressSite, int idUser) {
        
        boolean inScheme = false;
        boolean broader = false; 
        
        ArrayList<SKOSResource> resourcesList = skosDocument.getResourcesList();

        
        // détection de type de concept SKOS (MT / Groupe ou TT ou concept
        // suivant le type, le traitement est différent.
        for (SKOSResource resource : resourcesList) {
            ArrayList<SKOSRelation> relations = resource.getRelationsList();
            for (SKOSRelation relation1 : relations) {
                switch (relation1.getProperty()) {
                    case SKOSProperty.broader:
                        broader = true;
                        break;
                    case SKOSProperty.inScheme:
                        inScheme = true;
                        break;                        
                    default:
                        break;
                }
            }
            if(!inScheme) { //c'est un MT ou group
                // écrire un domaine
                idGroup = writeDomaine(resource, idThesaurus, adressSite, useArk, idUser);
            }
            else {
                if(!broader) { // c'est une TT
                    //écrire un TT
                    writeFirstConceptAfterGroup(resource,
                    idGroup,
                    idThesaurus,
                    "fr", dateFormat, adressSite, useArk, idUser);
                }
                else {
                    // écrire un concept
                    writeConceptAfterGroup(resource,
                        idGroup,
                        idThesaurus,
                        "fr", dateFormat, adressSite, useArk, idUser);
                }
            }
            broader = false;
            inScheme = false;
        }
            // si c'est le premier Concept, on l'accroche à l'attache au concept père 
            // on ajoute les relations BT et NT pour faire la chaine
         /*   if(isfirst) {
                writeFirstConceptAfterGroup(resource,
                    idGroup,
                    idThesaurus,
                    "fr", dateFormat, adressSite, useArk, idUser);
                isfirst = false;
                // on insère le premier concept sans les BT
            }
            else {
                writeConceptAfterGroup(resource,
                    idGroup,
                    idThesaurus,
                    "fr", dateFormat, adressSite, useArk, idUser);
            }*/
        

    } 
    
    private String getId(String uri) {
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
        return uri;
    }      
    
    private String writeDomaine(SKOSResource resource, String id_thesaurus,
            String adressSite, boolean useArk, int idUser) {

        GroupHelper conceptGroupHelper = new GroupHelper();

        String idConceptGroup = getId(resource.getUri());
        conceptGroupHelper.insertGroup(ds, idConceptGroup,
                id_thesaurus, "D", "",
                adressSite, useArk, idUser);

        // ajouter les traductions des Groupes
        ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
        for (int i = 0; i < resource.getLabelsList().size(); i++) {
            conceptGroupLabel.setIdgroup(idConceptGroup);
            conceptGroupLabel.setIdthesaurus(id_thesaurus);
            conceptGroupLabel.setLang(resource.getLabelsList().get(i).getLanguage());
            conceptGroupLabel.setLexicalvalue(resource.getLabelsList().get(i).getLabel());
            conceptGroupHelper.addGroupTraduction(ds, conceptGroupLabel, idUser);
        }
        return idConceptGroup;
        
      
    }    
    
    private boolean writeFirstConceptAfterGroup(SKOSResource resource,
            String idGroup,
            String idThesaurus,
            String id_langueSource, String dateFormat,
            String adressSite, boolean useArk, int idUser) {

        RelationsHelper relationsHelper = new RelationsHelper();
        
        
        // ajout du concept dans la base
        Concept concept = new Concept();
        ConceptHelper conceptHelper = new ConceptHelper();

        if(idGroup.trim().isEmpty()) return false;

        String idC = getId(resource.getUri());
        concept.setIdConcept(idC);

        //  concept.setCreated(null);
        concept.setIdThesaurus(idThesaurus);
        concept.setNotation("");
        concept.setStatus("");
        concept.setTopConcept(true);
        concept.setIdGroup(idGroup);
        concept = addDates(resource.getDateList(), concept, dateFormat);

        conceptHelper.insertConceptInTable(ds, concept,
                        adressSite, useArk, idUser);

        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        nodeTerm.setNodeTermTraduction(getTraductionConcept(resource.getLabelsList()));
       
        nodeTerm.setIdTerm(idC);
        nodeTerm.setIdConcept(idC);

        nodeTerm.setIdThesaurus(idThesaurus);
        nodeTerm.setSource("");
        nodeTerm.setStatus("");
        nodeTerm = addDatesTerm(resource.getDateList(), nodeTerm, dateFormat);
        
        TermHelper termHelper = new TermHelper();
        termHelper.insertTerm(ds, nodeTerm, idUser);

        // ajouter les notes
        ArrayList <NodeNote> nodeNoteList = addNotes(resource.getDocumentationsList());
        NoteHelper noteHelper = new NoteHelper();
        for (NodeNote nodeNoteList1 : nodeNoteList) {
            if(nodeNoteList1.getNotetypecode().contains("scopeNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("historyNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("definition")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("editorialNote")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
        }
        
        //Enregistrer les relations sans les BT
        writeRelationsList(resource.getRelationsList(), concept.getIdConcept(), idThesaurus, idUser);
        
        
        ArrayList <SKOSMapping> sKOSMappings = resource.getMappings();
        // Enregistrer les Mappings (alignements)
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        for (SKOSMapping sKOSMapping : sKOSMappings) {
            if(sKOSMapping.getProperty() == SKOSProperty.exactMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }
            if(sKOSMapping.getProperty() == SKOSProperty.closeMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    2,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }

        }
        
        // Ajouter l'id du concept importé pour l'aligner avec le thésaurus source
        alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    resource.getUri(), //URI de la source 
                 //   sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
        
        
        //Enregister les synonymes et traductions
         ArrayList <NodeEM> nodeEMs = writeLabelsList(ds, resource.getLabelsList());
         
         Term term = new Term();
         for (int i = 0; i < nodeEMs.size(); i++) {
            term.setId_concept(concept.getIdConcept());
            term.setId_term(nodeTerm.getIdTerm());
            term.setLexical_value(nodeEMs.get(i).getLexical_value());
            term.setLang(nodeEMs.get(i).getLang());
            term.setId_thesaurus(idThesaurus);
            term.setSource(nodeEMs.get(i).getSource());
            term.setStatus(nodeEMs.get(i).getStatus());
            termHelper.addNonPreferredTerm(ds, term, idUser);
        }        
        
        return true;
    }          
    
    
    /**
     * Cette fonction permet d'importer une branche sous un Concept
     * @param idConcept
     * @param idThesaurus
     * @param idGroup
     * @param skosDocument
     * @param dateFormat
     * @param useArk
     * @param adressSite 
     * @param idUser 
     */
    public void importBranchAfterConcept(
            String idConcept,
            String idGroup,
            String idThesaurus,
            SKOSXmlDocument skosDocument, String dateFormat,
            boolean useArk, String adressSite, int idUser) {
        
    //    SKOSConceptScheme conceptScheme = skosDocument.getConceptScheme();
    //    ArrayList<SKOSTopConcept> topConceptsList = conceptScheme.getTopConceptsList();
        
        boolean isfirst = true;
        
        ArrayList<SKOSResource> resourcesList = skosDocument.getResourcesList();
        
        for (SKOSResource resource : resourcesList) {
            
            // si c'est le premier Concept, on l'accroche à l'attache au concept père 
            // on ajoute les relations BT et NT pour faire la chaine
            if(isfirst) {
                writeFirstConcept(resource,
                    idConcept,
                    idGroup,
                    idThesaurus,
                    "fr", dateFormat, adressSite, useArk, idUser);
                isfirst = false;
                // on insère le premier concept sans les BT
            }
            else {
                writeConcept(resource,
                    idConcept,
                    idGroup,
                    idThesaurus,
                    "fr", dateFormat, adressSite, useArk, idUser);
            }
        }

    }
    
    private void writeConceptAfterGroup(SKOSResource resource,
            String idGroup,
            String idThesaurus,
            String id_langueSource, String dateFormat,
            String adressSite, boolean useArk, int idUser) {

        // ajout du concept dans la base
        Concept concept = new Concept();
        ConceptHelper conceptHelper = new ConceptHelper();

        String idC = getId(resource.getUri());
        concept.setIdConcept(idC);

        //  concept.setCreated(null);
        concept.setIdThesaurus(idThesaurus);
        concept.setNotation("");
        concept.setStatus("");
        concept.setTopConcept(false);
        concept.setIdGroup(idGroup);
        concept = addDates(resource.getDateList(), concept, dateFormat);

        if(idGroup.trim().isEmpty()) return;

        conceptHelper.insertConceptInTable(ds, concept,
                        adressSite, useArk, idUser);

        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        nodeTerm.setNodeTermTraduction(getTraductionConcept(resource.getLabelsList()));
       
        nodeTerm.setIdTerm(idC);
        nodeTerm.setIdConcept(idC);

        nodeTerm.setIdThesaurus(idThesaurus);
        nodeTerm.setSource("");
        nodeTerm.setStatus("");
        nodeTerm = addDatesTerm(resource.getDateList(), nodeTerm, dateFormat);
        
        TermHelper termHelper = new TermHelper();
        termHelper.insertTerm(ds, nodeTerm, idUser);

        // ajouter les notes
        ArrayList <NodeNote> nodeNoteList = addNotes(resource.getDocumentationsList());
        NoteHelper noteHelper = new NoteHelper();
        for (NodeNote nodeNoteList1 : nodeNoteList) {
            if(nodeNoteList1.getNotetypecode().contains("scopeNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("historyNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("definition")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("editorialNote")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
        }
        
        //Enregistrer les relations

        writeRelationsList(resource.getRelationsList(), concept.getIdConcept(), idThesaurus, idUser);
        
        
        
        
        ArrayList <SKOSMapping> sKOSMappings = resource.getMappings();
        // Enregistrer les Mappings (alignements)
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        for (SKOSMapping sKOSMapping : sKOSMappings) {
            if(sKOSMapping.getProperty() == SKOSProperty.exactMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }
            if(sKOSMapping.getProperty() == SKOSProperty.closeMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    2,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }

        }
        
        // Ajouter l'id du concept importé pour l'aligner avec le thésaurus source
        alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    resource.getUri(), //URI de la source 
                 //   sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
        
        //Enregister les synonymes et traductions
         ArrayList <NodeEM> nodeEMs = writeLabelsList(ds, resource.getLabelsList());
         
         Term term = new Term();
         for (int i = 0; i < nodeEMs.size(); i++) {
            term.setId_concept(concept.getIdConcept());
            term.setId_term(nodeTerm.getIdTerm());
            term.setLexical_value(nodeEMs.get(i).getLexical_value());
            term.setLang(nodeEMs.get(i).getLang());
            term.setId_thesaurus(idThesaurus);
            term.setSource(nodeEMs.get(i).getSource());
            term.setStatus(nodeEMs.get(i).getStatus());
            termHelper.addNonPreferredTerm(ds, term, idUser);
        }
         
    }      
    
    public boolean writeFirstConcept(SKOSResource resource,
            String idConcept,
            String idGroup,
            String idThesaurus,
            String id_langueSource, String dateFormat,
            String adressSite, boolean useArk, int idUser) {

        RelationsHelper relationsHelper = new RelationsHelper();
        
        try {
            relationsHelper.addRelationBT(ds.getConnection(), getId(getId(resource.getUri())), idThesaurus, idConcept, idUser);
        } catch (SQLException ex) {
            Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // ajout du concept dans la base
        Concept concept = new Concept();
        ConceptHelper conceptHelper = new ConceptHelper();
        if(idConcept.trim().isEmpty()) return false;
        if(idGroup.trim().isEmpty()) return false;

        String idC = getId(resource.getUri());
        concept.setIdConcept(idC);

        //  concept.setCreated(null);
        concept.setIdThesaurus(idThesaurus);
        concept.setNotation("");
        concept.setStatus("");
        concept.setTopConcept(false);
        concept.setIdGroup(idGroup);
        concept = addDates(resource.getDateList(), concept, dateFormat);

        conceptHelper.insertConceptInTable(ds, concept,
                        adressSite, useArk, idUser);

        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        nodeTerm.setNodeTermTraduction(getTraductionConcept(resource.getLabelsList()));
       
        nodeTerm.setIdTerm(idC);
        nodeTerm.setIdConcept(idC);

        nodeTerm.setIdThesaurus(idThesaurus);
        nodeTerm.setSource("");
        nodeTerm.setStatus("");
        nodeTerm = addDatesTerm(resource.getDateList(), nodeTerm, dateFormat);
        
        TermHelper termHelper = new TermHelper();
        termHelper.insertTerm(ds, nodeTerm, idUser);

        // ajouter les notes
        ArrayList <NodeNote> nodeNoteList = addNotes(resource.getDocumentationsList());
        NoteHelper noteHelper = new NoteHelper();
        for (NodeNote nodeNoteList1 : nodeNoteList) {
            if(nodeNoteList1.getNotetypecode().contains("scopeNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("historyNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("definition")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("editorialNote")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
        }
        
        //Enregistrer les relations sans les BT
        writeFirstRelationsList(resource.getRelationsList(), concept.getIdConcept(), idThesaurus, idUser);
        
        
        ArrayList <SKOSMapping> sKOSMappings = resource.getMappings();
        // Enregistrer les Mappings (alignements)
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        for (SKOSMapping sKOSMapping : sKOSMappings) {
            if(sKOSMapping.getProperty() == SKOSProperty.exactMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }
            if(sKOSMapping.getProperty() == SKOSProperty.closeMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    2,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }

        }
        
        // Ajouter l'id du concept importé pour l'aligner avec le thésaurus source
        alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    resource.getUri(), //URI de la source 
                 //   sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
        
        
        //Enregister les synonymes et traductions
         ArrayList <NodeEM> nodeEMs = writeLabelsList(ds, resource.getLabelsList());
         
         Term term = new Term();
         for (int i = 0; i < nodeEMs.size(); i++) {
            term.setId_concept(concept.getIdConcept());
            term.setId_term(nodeTerm.getIdTerm());
            term.setLexical_value(nodeEMs.get(i).getLexical_value());
            term.setLang(nodeEMs.get(i).getLang());
            term.setId_thesaurus(idThesaurus);
            term.setSource(nodeEMs.get(i).getSource());
            term.setStatus(nodeEMs.get(i).getStatus());
            termHelper.addNonPreferredTerm(ds, term, idUser);
        }        
        
        return true;
    }        

    private void writeConcept(SKOSResource resource,
            String idConcept,
            String idGroup,
            String idThesaurus,
            String id_langueSource, String dateFormat,
            String adressSite, boolean useArk, int idUser) {

        // ajout du concept dans la base
        Concept concept = new Concept();
        ConceptHelper conceptHelper = new ConceptHelper();
        if(idConcept.trim().isEmpty()) return;

        String idC = getId(resource.getUri());
        concept.setIdConcept(idC);

        //  concept.setCreated(null);
        concept.setIdThesaurus(idThesaurus);
        concept.setNotation("");
        concept.setStatus("");
        concept.setTopConcept(false);
        concept.setIdGroup(idGroup);
        concept = addDates(resource.getDateList(), concept, dateFormat);

        if(idGroup.trim().isEmpty()) return;

        conceptHelper.insertConceptInTable(ds, concept,
                        adressSite, useArk, idUser);

        //ajout des termes et traductions
        NodeTerm nodeTerm = new NodeTerm();
        nodeTerm.setNodeTermTraduction(getTraductionConcept(resource.getLabelsList()));
       
        nodeTerm.setIdTerm(idC);
        nodeTerm.setIdConcept(idC);

        nodeTerm.setIdThesaurus(idThesaurus);
        nodeTerm.setSource("");
        nodeTerm.setStatus("");
        nodeTerm = addDatesTerm(resource.getDateList(), nodeTerm, dateFormat);
        
        TermHelper termHelper = new TermHelper();
        termHelper.insertTerm(ds, nodeTerm, idUser);

        // ajouter les notes
        ArrayList <NodeNote> nodeNoteList = addNotes(resource.getDocumentationsList());
        NoteHelper noteHelper = new NoteHelper();
        for (NodeNote nodeNoteList1 : nodeNoteList) {
            if(nodeNoteList1.getNotetypecode().contains("scopeNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("historyNote")){
                noteHelper.addConceptNote(ds, concept.getIdConcept(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("definition")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
            if(nodeNoteList1.getNotetypecode().contains("editorialNote")){
                noteHelper.addTermNote(ds, nodeTerm.getIdTerm(), nodeNoteList1.getLang(), idThesaurus, nodeNoteList1.getLexicalvalue(), nodeNoteList1.getNotetypecode(), idUser);
            }
        }
        
        //Enregistrer les relations

        writeRelationsList(resource.getRelationsList(), concept.getIdConcept(), idThesaurus, idUser);
        
        
        
        
        ArrayList <SKOSMapping> sKOSMappings = resource.getMappings();
        // Enregistrer les Mappings (alignements)
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        for (SKOSMapping sKOSMapping : sKOSMappings) {
            if(sKOSMapping.getProperty() == SKOSProperty.exactMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }
            if(sKOSMapping.getProperty() == SKOSProperty.closeMatch){
                alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    sKOSMapping.getTargetUri(), // URI
                    2,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
            }

        }
        
        // Ajouter l'id du concept importé pour l'aligner avec le thésaurus source
        alignmentHelper.addNewAlignment(ds,
                    1, // user 
                    "", //concept target
                    "", //thesaurus target
                    resource.getUri(), //URI de la source 
                 //   sKOSMapping.getTargetUri(), // URI
                    1,
                    concept.getIdConcept(),
                    idThesaurus,0
                );
        
        //Enregister les synonymes et traductions
         ArrayList <NodeEM> nodeEMs = writeLabelsList(ds, resource.getLabelsList());
         
         Term term = new Term();
         for (int i = 0; i < nodeEMs.size(); i++) {
            term.setId_concept(concept.getIdConcept());
            term.setId_term(nodeTerm.getIdTerm());
            term.setLexical_value(nodeEMs.get(i).getLexical_value());
            term.setLang(nodeEMs.get(i).getLang());
            term.setId_thesaurus(idThesaurus);
            term.setSource(nodeEMs.get(i).getSource());
            term.setStatus(nodeEMs.get(i).getStatus());
            termHelper.addNonPreferredTerm(ds, term, idUser);
        }
         
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private boolean isDescriptor(SKOSResource resource, ArrayList<SKOSTopConcept> topConceptsList) {
        for (int j = 0; j < topConceptsList.size(); j++) {
            if (resource.getUri().compareToIgnoreCase(topConceptsList.get(j).getTopConcept()) == 0) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getIdOftopConcepts(ArrayList<SKOSTopConcept> topConceptsList) {
        ArrayList<String> idTopConcept_temp = new ArrayList<>();
        for (SKOSTopConcept topConceptsList1 : topConceptsList) {
            idTopConcept_temp.add(getId(topConceptsList1.getTopConcept()));
        }
        return idTopConcept_temp;
    }
                

    public void addLangsToThesaurus(HikariDataSource ds, String idThesaurus) {

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        ArrayList <String> tabListLang = thesaurusHelper.getAllUsedLanguagesOfThesaurus(ds, idThesaurus);
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
                thesaurus.setTitle("");
                thesaurus.setType("");
                thesaurusHelper.addThesaurusTraduction(ds, thesaurus);
            }
        }

    }


    public void writeRelationsListTopConcept(ArrayList<SKOSRelation> relationsList,
            String idConcept, String idThesaurus, int idUser) {
        ConceptHelper conceptHelper = new ConceptHelper();
        HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
        for (int i = 0; i < relationsList.size(); i++) {
            SKOSRelation relation = relationsList.get(i);
            switch (relation.getProperty()) {

                case SKOSProperty.narrower:
                    hierarchicalRelationship.setIdConcept1(idConcept);
                    hierarchicalRelationship.setIdConcept2(getId(relation.getTargetUri()));
                    hierarchicalRelationship.setIdThesaurus(idThesaurus);
                    hierarchicalRelationship.setRole("NT");
                    try {
                        Connection conn = ds.getConnection();
                        conn.setAutoCommit(false);
                        if(!conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser)) {
                            conn.rollback();
                            conn.close();
                        }
                        conn.commit();
                        conn.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //    query.addRelation(id_terme, "NT", getId(relation.getTargetUri()), id_thesaurus);
                    break;
                case SKOSProperty.related:
                    hierarchicalRelationship.setIdConcept1(idConcept);
                    hierarchicalRelationship.setIdConcept2(getId(relation.getTargetUri()));
                    hierarchicalRelationship.setIdThesaurus(idThesaurus);
                    hierarchicalRelationship.setRole("RT");
                    
                    try {
                        try (Connection conn = ds.getConnection()) {
                            conn.setAutoCommit(false);
                            if(!conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser)) {
                                conn.rollback();
                                conn.close();
                            }
                            conn.commit();
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //     query.addRelation(id_terme, "RT", getId(relation.getTargetUri()), id_thesaurus);
                    break;
                default:
                    break;
            }
        }
    }

    
    
    public void writeFirstRelationsList(ArrayList<SKOSRelation> relationsList,
            String idConcept, String idThesaurus, int idUser) {
        ConceptHelper conceptHelper = new ConceptHelper();
        HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
        for (SKOSRelation relation : relationsList) {
            switch (relation.getProperty()) {

                case SKOSProperty.narrower:
                    hierarchicalRelationship.setIdConcept1(idConcept);
                    hierarchicalRelationship.setIdConcept2(getId(relation.getTargetUri()));
                    hierarchicalRelationship.setIdThesaurus(idThesaurus);
                    hierarchicalRelationship.setRole("NT");
                    
                    try {
                        try (Connection conn = ds.getConnection()) {
                            conn.setAutoCommit(false);
                            if(!conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser)) {
                                conn.rollback();
                                conn.close();
                            }
                            conn.commit();
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    //    query.addRelation(id_terme, "NT", getId(relation.getTargetUri()), id_thesaurus);
                    break;
                default:
                    break;
            }
        }
    }        
        
    public void writeRelationsList(ArrayList<SKOSRelation> relationsList,
            String idConcept, String idThesaurus, int idUser) {
        ConceptHelper conceptHelper = new ConceptHelper();
        HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
        for (SKOSRelation relation : relationsList) {
            switch (relation.getProperty()) {

                case SKOSProperty.narrower:
                    hierarchicalRelationship.setIdConcept1(idConcept);
                    hierarchicalRelationship.setIdConcept2(getId(relation.getTargetUri()));
                    hierarchicalRelationship.setIdThesaurus(idThesaurus);
                    hierarchicalRelationship.setRole("NT");
                    
                    try {
                        try (Connection conn = ds.getConnection()) {
                            conn.setAutoCommit(false);
                            if(!conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser)) {
                                conn.rollback();
                                conn.close();
                            }
                            conn.commit();
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    //    query.addRelation(id_terme, "NT", getId(relation.getTargetUri()), id_thesaurus);
                    break;
                case SKOSProperty.broader:
                    hierarchicalRelationship.setIdConcept1(idConcept);
                    hierarchicalRelationship.setIdConcept2(getId(relation.getTargetUri()));
                    hierarchicalRelationship.setIdThesaurus(idThesaurus);
                    hierarchicalRelationship.setRole("BT");
                    
                    try {
                        try (Connection conn = ds.getConnection()) {
                            conn.setAutoCommit(false);
                            if(!conceptHelper.addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser)) {
                                conn.rollback();
                                conn.close();
                            }
                            conn.commit();
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }                     
                    //    query.addRelation(id_terme, "BT", getId(relation.getTargetUri()), id_thesaurus);
                    break;
                default:
                    break;
            }
        }
    }

    public ArrayList <NodeEM> writeLabelsList(HikariDataSource ds, ArrayList<SKOSLabel> labelsList) {

        ArrayList<NodeEM> nodeEMList =  new ArrayList<>();
        for (int i = 0; i < labelsList.size(); i++) {
            SKOSLabel label = labelsList.get(i);

            switch (label.getProperty()) {
                case SKOSProperty.altLabel:
                    NodeEM nodeEM = new NodeEM();
                    nodeEM.setLexical_value(label.getLabel());
                    nodeEM.setLang(label.getLanguage());    
                    nodeEM.setSource("");
                    nodeEM.setStatus("USE");
                    nodeEM.setHiden(false);
                    nodeEMList.add(nodeEM);
                    break;
                default:
                    break;
            }
        }
        return nodeEMList;
    }

/*    public String getId(String uri) {
        if (uri.contains("#")) {
            uri = uri.substring(uri.indexOf("#") + 1, uri.length());
        }
        else
        {
            uri = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        }
        return uri;
    }
*/
    /* Pour un concept */
    public ArrayList<NodeTermTraduction> getTraductionConcept(ArrayList<SKOSLabel> labelsList) {
        ArrayList<NodeTermTraduction> nodeTermTraductionList = new ArrayList<>();
        for (int i = 0; i < labelsList.size(); i++) {
            SKOSLabel label = labelsList.get(i);
            switch (label.getProperty()) {
                case SKOSProperty.prefLabel:

                    NodeTermTraduction nodeTermTraduction = new NodeTermTraduction();
                    nodeTermTraduction.setLexicalValue(label.getLabel());
                    nodeTermTraduction.setLang(label.getLanguage());
                    nodeTermTraductionList.add(nodeTermTraduction);

                    break;
                default:
                    break;
            }
        }
        return nodeTermTraductionList;
    }

    public ArrayList <NodeNote> addNotes(ArrayList<SKOSDocumentation> documentationsList) {

        ArrayList <NodeNote> nodeNotes = new ArrayList<>();
        for (int i = 0; i < documentationsList.size(); i++) {
            SKOSDocumentation documentation = documentationsList.get(i);
            switch (documentation.getProperty()) {
                case SKOSProperty.scopeNote:
                    NodeNote nodeNote = new NodeNote();
                    nodeNote.setLang(documentation.getLanguage());
                    nodeNote.setLexicalvalue(documentation.getText());
                    nodeNote.setNotetypecode("scopeNote");
                    nodeNotes.add(nodeNote);
                    break;
                case SKOSProperty.historyNote:
                    NodeNote nodeNote2 = new NodeNote();
                    nodeNote2.setLang(documentation.getLanguage());
                    nodeNote2.setLexicalvalue(documentation.getText());
                    nodeNote2.setNotetypecode("historyNote");
                    nodeNotes.add(nodeNote2);
                    break;
                case SKOSProperty.definition:
                    NodeNote nodeNote3 = new NodeNote();
                    nodeNote3.setLang(documentation.getLanguage());
                    nodeNote3.setLexicalvalue(documentation.getText());
                    nodeNote3.setNotetypecode("definition");
                    nodeNotes.add(nodeNote3);
                    break;
                case SKOSProperty.editorialNote:
                    NodeNote nodeNote4 = new NodeNote();
                    nodeNote4.setLang(documentation.getLanguage());
                    nodeNote4.setLexicalvalue(documentation.getText());
                    nodeNote4.setNotetypecode("editorialNote");
                    nodeNotes.add(nodeNote4);
                    break;                    
                default:
                    break;
            }
        }
        return nodeNotes;
    }

    public Concept addDates(ArrayList<SKOSDate> dateList,
            Concept concept, String simpleFormatDate) {
        for (int i = 0; i < dateList.size(); i++) {
            SKOSDate date = dateList.get(i);
            SimpleDateFormat formatDate = new SimpleDateFormat(simpleFormatDate);
            switch (date.getProperty()) {
                case SKOSProperty.created:
                    try {
                        concept.setCreated(formatDate.parse(date.getDate()));
                    } catch (ParseException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case SKOSProperty.modified:
                    try {
                        concept.setModified(formatDate.parse(date.getDate()));
                    } catch (ParseException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    break;
            }
        }
        return concept;
    }
    
    public NodeTerm addDatesTerm(ArrayList<SKOSDate> dateList,
            NodeTerm nodeTerm, String simpleFormatDate) {
        for (int i = 0; i < dateList.size(); i++) {
            SKOSDate date = dateList.get(i);
            SimpleDateFormat formatDate = new SimpleDateFormat(simpleFormatDate);
            switch (date.getProperty()) {
                case SKOSProperty.created:
                    try {
                        nodeTerm.setCreated(formatDate.parse(date.getDate()));
                    } catch (ParseException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case SKOSProperty.modified:
                    try {
                        nodeTerm.setModified(formatDate.parse(date.getDate()));
                    } catch (ParseException ex) {
                        Logger.getLogger(WriteBranchSkosBDD.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    break;
            }
        }
        return nodeTerm;
    }

}
