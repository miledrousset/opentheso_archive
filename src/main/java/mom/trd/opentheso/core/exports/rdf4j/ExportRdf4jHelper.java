/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.rdf4j;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mom.trd.opentheso.SelectedBeans.DownloadBean;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignmentSmall;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeGps;
import mom.trd.opentheso.bdd.helper.nodes.NodeHieraRelation;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUri;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupTraductions;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.helper.nodes.thesaurus.NodeThesaurus;
import mom.trd.opentheso.skosapi.SKOSGPSCoordinates;
import mom.trd.opentheso.skosapi.SKOSProperty;
import mom.trd.opentheso.skosapi.SKOSResource;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;

/**
 *
 * @author Quincy
 */
public class ExportRdf4jHelper {

    private String langueSource;
    private HikariDataSource ds;
    private String formatDate;
    private String adressSite;
    private boolean useArk;
    
    private NodePreference nodePreference;

    private String idTheso;
    private SKOSXmlDocument skosXmlDocument;
    // le thésaurus avec ses traductions
    private NodeThesaurus nodeThesaurus;

    private ArrayList<String> rootGroupList;
    private HashMap<String, String> superGroupHashMap;

    ArrayList<NodeUri> nodeTTs = new ArrayList<>();
    String urlSite;



    public ExportRdf4jHelper() {
        skosXmlDocument = new SKOSXmlDocument();
        superGroupHashMap = new HashMap();

    }

    public boolean setInfos(HikariDataSource ds,
            String formatDate, boolean useArk, String adressSite,String urlSite) {
        this.ds = ds;
        this.formatDate = formatDate;
        this.useArk = useArk;
        this.adressSite = adressSite;
        this.urlSite = urlSite;

        return true;
    }

    public void addConcept(String idThesaurus, DownloadBean downloadBean, List<NodeLang> selectedLanguages) {
        // récupération de tous les concepts
        for (NodeUri nodeTT1 : nodeTTs) {
            SKOSResource sKOSResource = new SKOSResource();
            sKOSResource.addRelation(getUriFromId(idTheso), SKOSProperty.topConceptOf);
            //fils top concept
            addFilsConceptRecursif(idThesaurus, nodeTT1.getIdConcept(), sKOSResource, downloadBean, selectedLanguages);
        }
    }

    public void addBranch(String idThesaurus, String idConcept) {
        idTheso = idThesaurus;
        addFilsConceptRecursif(idTheso, idConcept, new SKOSResource());
    }

    public void addSignleConcept(String idThesaurus, String idConcept) {
        idTheso = idThesaurus;
        ConceptHelper conceptHelper = new ConceptHelper();
        SKOSResource sKOSResource = new SKOSResource();
        NodeConceptExport nodeConcept = conceptHelper.getConceptForExport(ds, idConcept, idThesaurus, false);

        if (nodeConcept == null) {
            return;
        }

    //    concept.setUri(getUriFromId(idConcept));
        sKOSResource.setUri(getUri(nodeConcept));
        sKOSResource.setProperty(SKOSProperty.Concept);

        // prefLabel
        for (NodeTermTraduction traduction : nodeConcept.getNodeTermTraductions()) {
            sKOSResource.addLabel(traduction.getLexicalValue(), traduction.getLang(), SKOSProperty.prefLabel);
        }
        // altLabel
        for (NodeEM nodeEM : nodeConcept.getNodeEM()) {

            sKOSResource.addLabel(nodeEM.getLexical_value(), nodeEM.getLang(), SKOSProperty.altLabel);
        }
        ArrayList<NodeNote> nodeNotes = nodeConcept.getNodeNoteConcept();
        nodeNotes.addAll(nodeConcept.getNodeNoteTerm());
        addNoteGiven(nodeNotes, sKOSResource);
        addGPSGiven(nodeConcept.getNodeGps(), sKOSResource);
        addAlignementGiven(nodeConcept.getNodeAlignmentsList(), sKOSResource);
        addRelationGiven(nodeConcept.getNodeListOfBT(), nodeConcept.getNodeListOfNT(),
                nodeConcept.getNodeListIdsOfRT(), sKOSResource, nodeConcept.getConcept().getIdThesaurus());

        String notation = nodeConcept.getConcept().getNotation();
        String created = nodeConcept.getConcept().getCreated().toString();
        String modified = nodeConcept.getConcept().getModified().toString();

        if (notation != null && !notation.equals("null")) {
            sKOSResource.addNotation(notation);
        }
        if (created != null) {
            sKOSResource.addDate(created, SKOSProperty.created);
        }
        if (modified != null) {
            sKOSResource.addDate(modified, SKOSProperty.modified);
        }
        sKOSResource.addRelation(getUriFromId(idTheso), SKOSProperty.inScheme);
        for (NodeUri nodeUri : nodeConcept.getNodeListIdsOfConceptGroup()) {
            sKOSResource.addRelation(getUriGroupFromNodeUri(nodeUri,idTheso), SKOSProperty.memberOf);
        }           
        sKOSResource.addIdentifier(idConcept, SKOSProperty.identifier);
        
        skosXmlDocument.addconcept(sKOSResource);
    }

    private void addFilsConceptRecursif(String idThesaurus, String idPere, SKOSResource sKOSResource, DownloadBean downloadBean, List<NodeLang> selectedLanguages) {

        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList<String> listIdsOfConceptChildren = conceptHelper.getListChildrenOfConcept(ds, idPere, idThesaurus);

        writeConceptInfo(conceptHelper, sKOSResource, idThesaurus, idPere, downloadBean, selectedLanguages);

        for (String idOfConceptChildren : listIdsOfConceptChildren) {
            sKOSResource = new SKOSResource();
            //writeConceptInfo(conceptHelper, concept, idThesaurus, idOfConceptChildren, downloadBean, selectedLanguages);

            //if (conceptHelper.haveChildren(ds, idThesaurus, idOfConceptChildren)) {
            addFilsConceptRecursif(idThesaurus, idOfConceptChildren, sKOSResource, downloadBean, selectedLanguages);
            //}
        }
    }

    private void addFilsConceptRecursif(String idThesaurus, String idPere, SKOSResource sKOSResource) {

        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList<String> listIdsOfConceptChildren = conceptHelper.getListChildrenOfConcept(ds, idPere, idThesaurus);

        writeConceptInfo(conceptHelper, sKOSResource, idThesaurus, idPere);

        for (String idOfConceptChildren : listIdsOfConceptChildren) {
            sKOSResource = new SKOSResource();
            //writeConceptInfo(conceptHelper, concept, idThesaurus, idOfConceptChildren, downloadBean, selectedLanguages);

            //if (conceptHelper.haveChildren(ds, idThesaurus, idOfConceptChildren)) {
            addFilsConceptRecursif(idThesaurus, idOfConceptChildren, sKOSResource);
            //}
        }

    }

    private void writeConceptInfo(ConceptHelper conceptHelper, SKOSResource sKOSResource,
            String idThesaurus, String idOfConceptChildren, DownloadBean downloadBean, List<NodeLang> selectedLanguages) {

        NodeConceptExport nodeConcept = conceptHelper.getConceptForExport(ds, idOfConceptChildren, idThesaurus, false);

        if (nodeConcept == null) {
            return;
        }

        sKOSResource.setUri(getUri(nodeConcept));
        sKOSResource.setProperty(SKOSProperty.Concept);

        // prefLabel
        for (NodeTermTraduction traduction : nodeConcept.getNodeTermTraductions()) {

            boolean isInselectedLanguages = false;
            for (NodeLang nodeLang : selectedLanguages) {
                if (nodeLang.getCode().equals(traduction.getLang())) {
                    isInselectedLanguages = true;

                    break;
                }

            }
            if (isInselectedLanguages) {
                sKOSResource.addLabel(traduction.getLexicalValue(), traduction.getLang(), SKOSProperty.prefLabel);
            }
        }
        // altLabel
        for (NodeEM nodeEM : nodeConcept.getNodeEM()) {
            boolean isInselectedLanguages = false;
            for (NodeLang nodeLang : selectedLanguages) {
                if (nodeLang.getCode().equals(nodeEM.getLang())) {
                    isInselectedLanguages = true;

                    break;
                }

            }
            if (isInselectedLanguages) {
                if(nodeEM.isHiden())
                    sKOSResource.addLabel(nodeEM.getLexical_value(), nodeEM.getLang(), SKOSProperty.hiddenLabel);
                else
                    sKOSResource.addLabel(nodeEM.getLexical_value(), nodeEM.getLang(), SKOSProperty.altLabel);
            }
        }
        ArrayList<NodeNote> nodeNotes = nodeConcept.getNodeNoteConcept();
        nodeNotes.addAll(nodeConcept.getNodeNoteTerm());
        addNoteGiven(nodeNotes, sKOSResource, selectedLanguages);
        addGPSGiven(nodeConcept.getNodeGps(), sKOSResource);
        addAlignementGiven(nodeConcept.getNodeAlignmentsList(), sKOSResource);
        addRelationGiven(nodeConcept.getNodeListOfBT(), nodeConcept.getNodeListOfNT(),
                nodeConcept.getNodeListIdsOfRT(), sKOSResource, nodeConcept.getConcept().getIdThesaurus());

        String notation = nodeConcept.getConcept().getNotation();
        String created = nodeConcept.getConcept().getCreated().toString();
        String modified = nodeConcept.getConcept().getModified().toString();

        if (notation != null && !notation.equals("null")) {
            sKOSResource.addNotation(notation);
        }
        if (created != null) {
            sKOSResource.addDate(created, SKOSProperty.created);
        }
        if (modified != null) {
            sKOSResource.addDate(modified, SKOSProperty.modified);
        }

        sKOSResource.addRelation(getUriFromId(idTheso), SKOSProperty.inScheme);
        for (NodeUri nodeUri : nodeConcept.getNodeListIdsOfConceptGroup()) {
            sKOSResource.addRelation(getUriGroupFromNodeUri(nodeUri,idTheso), SKOSProperty.memberOf);
        }        
        
        sKOSResource.addIdentifier(nodeConcept.getConcept().getIdConcept(), SKOSProperty.identifier);
        
        downloadBean.setProgress_abs(downloadBean.getProgress_abs() + 1);
        double progress = (downloadBean.getProgress_abs() / downloadBean.getSizeOfTheso()) * 100;

        if (progress > 100) {
            progress = 100;
        }

        downloadBean.setProgress_per_100((int) progress);

        skosXmlDocument.addconcept(sKOSResource);

    }

    private void writeConceptInfo(ConceptHelper conceptHelper, SKOSResource sKOSResource,
            String idThesaurus, String idOfConceptChildren) {

        NodeConceptExport nodeConcept = conceptHelper.getConceptForExport(ds, idOfConceptChildren, idThesaurus, false);

        if (nodeConcept == null) {
            return;
        }

        sKOSResource.setUri(getUri(nodeConcept));
        sKOSResource.setProperty(SKOSProperty.Concept);

        // prefLabel
        for (NodeTermTraduction traduction : nodeConcept.getNodeTermTraductions()) {

            sKOSResource.addLabel(traduction.getLexicalValue(), traduction.getLang(), SKOSProperty.prefLabel);
        }
        // altLabel
        for (NodeEM nodeEM : nodeConcept.getNodeEM()) {

            sKOSResource.addLabel(nodeEM.getLexical_value(), nodeEM.getLang(), SKOSProperty.altLabel);
        }
        ArrayList<NodeNote> nodeNotes = nodeConcept.getNodeNoteConcept();
        nodeNotes.addAll(nodeConcept.getNodeNoteTerm());
        addNoteGiven(nodeNotes, sKOSResource);
        addGPSGiven(nodeConcept.getNodeGps(), sKOSResource);
        addAlignementGiven(nodeConcept.getNodeAlignmentsList(), sKOSResource);
        addRelationGiven(nodeConcept.getNodeListOfBT(), nodeConcept.getNodeListOfNT(),
                nodeConcept.getNodeListIdsOfRT(), sKOSResource, nodeConcept.getConcept().getIdThesaurus());

        String notation = nodeConcept.getConcept().getNotation();
        String created = nodeConcept.getConcept().getCreated().toString();
        String modified = nodeConcept.getConcept().getModified().toString();

        if (notation != null && !notation.equals("null")) {
            sKOSResource.addNotation(notation);
        }
        if (created != null) {
            sKOSResource.addDate(created, SKOSProperty.created);
        }
        if (modified != null) {
            sKOSResource.addDate(modified, SKOSProperty.modified);
        }

        sKOSResource.addRelation(getUriFromId(idTheso), SKOSProperty.inScheme);
        for (NodeUri nodeUri : nodeConcept.getNodeListIdsOfConceptGroup()) {
            sKOSResource.addRelation(getUriGroupFromNodeUri(nodeUri,idTheso), SKOSProperty.memberOf);
        }
        
        sKOSResource.addIdentifier(nodeConcept.getConcept().getIdConcept(), SKOSProperty.identifier);
        skosXmlDocument.addconcept(sKOSResource);

    }

    private void addMember(String id, String idThesaurus, SKOSResource resource) {

        RelationsHelper relationsHelper = new RelationsHelper();
        ArrayList<NodeHieraRelation> listChildren = relationsHelper.getListNT(ds, id, idThesaurus);

        for (NodeHieraRelation idChildren : listChildren) {
    //        System.out.println(idChildren.getUri().getIdConcept());
            resource.addRelation(getUriFromNodeUri(idChildren.getUri(), idThesaurus), SKOSProperty.member);
            addMember(idChildren.getUri().getIdConcept(), idThesaurus, resource);
        }

    }


    public void addGroup(String idThesaurus, List<NodeLang> selectedLanguages, List<NodeGroup> selectedGroups) {

        if(idTheso == null || idTheso.isEmpty())
            idTheso = idThesaurus;
        
        GroupHelper groupHelper = new GroupHelper();
        rootGroupList = groupHelper.getListIdOfRootGroup(ds, idTheso);
        NodeGroupLabel nodeGroupLabel;
        
        
        for (String idGroup : rootGroupList) {
            for (NodeGroup nodeGroup : selectedGroups) {
                if (nodeGroup.getConceptGroup().getIdgroup().equals(idGroup)) {
                   
                    nodeGroupLabel = groupHelper.getNodeGroupLabel(ds, idGroup, idThesaurus);
                    
                    SKOSResource sKOSResource = new SKOSResource(getUriFromGroup(nodeGroupLabel), SKOSProperty.ConceptGroup);                    
                    sKOSResource.addRelation(getUriFromGroup(nodeGroupLabel), SKOSProperty.microThesaurusOf);
                    addFilsGroupRcursif(idThesaurus, idGroup, sKOSResource, selectedLanguages);                    
                }
            }
        }
    }
    
    

    private void addFilsGroupRcursif(String idThesaurus, String idPere, SKOSResource sKOSResource, List<NodeLang> selectedLanguages) {

        GroupHelper groupHelper = new GroupHelper();

        ArrayList<String> listIdsOfGroupChildren = groupHelper.getListGroupChildIdOfGroup(ds, idPere, idThesaurus);

        writeGroupInfo(sKOSResource, idThesaurus, idPere, selectedLanguages);

        for (String idOfGroupChildren : listIdsOfGroupChildren) {
            sKOSResource = new SKOSResource();

            //writeGroupInfo(groupHelper, group, idThesaurus, idOfGroupChildren, selectedLanguages);
            //if (!groupHelper.getListGroupChildIdOfGroup(ds, idOfGroupChildren, idThesaurus).isEmpty()) {
            addFilsGroupRcursif(idThesaurus, idOfGroupChildren, sKOSResource, selectedLanguages);
            //}
        }
    }

    private void writeGroupInfo(SKOSResource sKOSResource,
            String idThesaurus, String idOfGroupChildren, List<NodeLang> selectedLanguages) {

        NodeGroupLabel nodeGroupLabel;
        nodeGroupLabel = new GroupHelper().getNodeGroupLabel(ds, idOfGroupChildren, idThesaurus);

        sKOSResource.setUri(getUriFromGroup(nodeGroupLabel));
        sKOSResource.setProperty(SKOSProperty.ConceptGroup);

        for (NodeGroupTraductions traduction : nodeGroupLabel.getNodeGroupTraductionses()) {

            boolean isInSelectedLanguages = false;

            for (NodeLang nodeLang : selectedLanguages) {

                if (nodeLang.getCode().equals(traduction.getIdLang())) {
                    isInSelectedLanguages = true;
                    break;
                }
            }

            if (!isInSelectedLanguages) {
                continue;
            }

            sKOSResource.addLabel(traduction.getTitle(), traduction.getIdLang(), SKOSProperty.prefLabel);

            //dates
            String created;
            String modified;
            created = traduction.getCreated().toString();
            modified = traduction.getModified().toString();
            if (created != null) {
                sKOSResource.addDate(created, SKOSProperty.created);
            }
            if (modified != null) {
                sKOSResource.addDate(modified, SKOSProperty.modified);
            }

        }

        ArrayList<String> childURI = new GroupHelper().getListGroupChildIdOfGroup(ds, idOfGroupChildren, idThesaurus);
        ArrayList<NodeUri> nodeUris = new ConceptHelper().getListIdsOfTopConceptsForExport(ds, idOfGroupChildren, idThesaurus);

        for (NodeUri nodeUri : nodeUris) {
            sKOSResource.addRelation(getUriFromNodeUri(nodeUri, idThesaurus), SKOSProperty.member);
            addMember(nodeUri.getIdConcept(), idThesaurus, sKOSResource);

        }

        for (String id : childURI) {
            sKOSResource.addRelation(getUriFromId(id), SKOSProperty.subGroup);
            superGroupHashMap.put(id, idOfGroupChildren);
        }

    //    addNotes(idOfGroupChildren, group, selectedLanguages);
    //    addGPS(idOfGroupChildren, group);
    //    addAlignement(idOfGroupChildren, group);
    //    addRelation(idOfGroupChildren, group);

        String idSuperGroup = superGroupHashMap.get(idOfGroupChildren);

        if (idSuperGroup != null) {
            sKOSResource.addRelation(getUriFromId(idSuperGroup), SKOSProperty.superGroup);
            superGroupHashMap.remove(idOfGroupChildren);
        }
        
        // ajout de la notation
        if (nodeGroupLabel.getNotation() != null && !nodeGroupLabel.getNotation().equals("null")) {
            if(!nodeGroupLabel.getNotation().isEmpty())
                sKOSResource.addNotation(nodeGroupLabel.getNotation());
        }

        skosXmlDocument.addGroup(sKOSResource);

        //liste top concept
        nodeTTs.addAll(nodeUris);
        for (NodeUri topConcept : nodeTTs) {
            if(skosXmlDocument.getConceptScheme() != null)
                skosXmlDocument.getConceptScheme().addRelation(getUriFromNodeUri(topConcept, idThesaurus), SKOSProperty.hasTopConcept);
        }

    }
    
    /**
     * permet d'ajouter une branche entière d'un domaine ou microthésaurus
     * @param idThesaurus
     * @param idGroup 
     * #MR
     */
    public void addWholeGroup(String idThesaurus, String idGroup) {
        SKOSResource sKOSResource = new SKOSResource(getUriFromId(idGroup), SKOSProperty.ConceptGroup);
        sKOSResource.addRelation(getUriFromId(idThesaurus), SKOSProperty.microThesaurusOf);
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        List<NodeLang> languagesOfTheso = thesaurusHelper.getAllUsedLanguagesOfThesaurusNode(ds, idThesaurus);
        
        addFilsGroupRcursif(idThesaurus, idGroup, sKOSResource, languagesOfTheso);
        
        for (NodeUri nodeTT1 : nodeTTs) {
            SKOSResource sKOSResource1 = new SKOSResource();
            sKOSResource1.addRelation(getUriFromId(idTheso), SKOSProperty.topConceptOf);
            //fils top concept
            addFilsConceptRecursif(idThesaurus, nodeTT1.getIdConcept(), sKOSResource1);
        }      
    }
    
    
    
    
    public void addSingleGroup(String idThesaurus, String idGroup) {

        NodeGroupLabel nodeGroupLabel;
        nodeGroupLabel = new GroupHelper().getNodeGroupLabel(ds, idGroup, idThesaurus);
        SKOSResource sKOSResource = new SKOSResource();
        sKOSResource.setUri(getUriFromGroup(nodeGroupLabel));
        sKOSResource.setProperty(SKOSProperty.ConceptGroup);

        for (NodeGroupTraductions traduction : nodeGroupLabel.getNodeGroupTraductionses()) {
            sKOSResource.addLabel(traduction.getTitle(), traduction.getIdLang(), SKOSProperty.prefLabel);
            //dates
            String created;
            String modified;
            created = traduction.getCreated().toString();
            modified = traduction.getModified().toString();
            if (created != null) {
                sKOSResource.addDate(created, SKOSProperty.created);
            }
            if (modified != null) {
                sKOSResource.addDate(modified, SKOSProperty.modified);
            }
        }
        
        // pour exporter les membres (tous les concepts du group
    /*    ArrayList<NodeUri> nodeUris = new ConceptHelper().getListIdsOfTopConceptsForExport(ds, idGroup, idThesaurus);
        for (NodeUri nodeUri : nodeUris) {
            sKOSResource.addRelation(getUriFromNodeUri(nodeUri, idThesaurus), SKOSProperty.member);
            addMember(nodeUri.getIdConcept(), idThesaurus, sKOSResource);
        }*/

        ArrayList<String> childURI = new GroupHelper().getListGroupChildIdOfGroup(ds, idGroup, idThesaurus);
        HashMap<String, String> superGroupHashMapTemp = new HashMap();
        for (String id : childURI) {
            sKOSResource.addRelation(getUriFromId(id), SKOSProperty.subGroup);
            superGroupHashMapTemp.put(id, idGroup);
        }
        String idSuperGroup = superGroupHashMapTemp.get(idGroup);

        if (idSuperGroup != null) {
            sKOSResource.addRelation(getUriFromId(idSuperGroup), SKOSProperty.superGroup);
            superGroupHashMapTemp.remove(idGroup);
        }
        sKOSResource.addIdentifier(idGroup, SKOSProperty.identifier);
        skosXmlDocument.addGroup(sKOSResource);
    }    

    public void addThesaurus(String idThesaurus, List<NodeLang> selectedLanguages) {

        nodeThesaurus = new ThesaurusHelper().getNodeThesaurus(ds, idThesaurus);
        String uri = getUriFromId(nodeThesaurus.getIdThesaurus());
        SKOSResource conceptScheme = new SKOSResource(uri, SKOSProperty.ConceptScheme);
        idTheso = nodeThesaurus.getIdThesaurus();
        String creator;
        String contributor;
        String title;
        String language;

        for (Thesaurus thesaurus : nodeThesaurus.getListThesaurusTraduction()) {

            boolean isInSelectedLanguages = false;

            for (NodeLang nodeLang : selectedLanguages) {
                if (nodeLang.getCode().equals(thesaurus.getLanguage())) {
                    isInSelectedLanguages = true;
                    break;
                }
            }

            if (!isInSelectedLanguages) {
                break;
            }

            creator = thesaurus.getCreator();
            contributor = thesaurus.getContributor();
            title = thesaurus.getTitle();
            language = thesaurus.getLanguage();

            /*[...]*/
            if (creator != null && !creator.equalsIgnoreCase("null")) {
                conceptScheme.addCreator(creator, SKOSProperty.creator);
            }
            if (contributor != null && !contributor.equalsIgnoreCase("null")) {
                conceptScheme.addCreator(creator, SKOSProperty.contributor);
            }
            if (title != null && language != null) {
                conceptScheme.addLabel(title, language, SKOSProperty.prefLabel);
            }

            //dates
            String created = thesaurus.getCreated().toString();
            String modified = thesaurus.getModified().toString();
            if (created != null) {
                conceptScheme.addDate(created, SKOSProperty.created);
            }
            if (modified != null) {
                conceptScheme.addDate(modified, SKOSProperty.modified);
            }

        }

        skosXmlDocument.setConceptScheme(conceptScheme);

    }

    private void addAlignementGiven(ArrayList<NodeAlignmentSmall> nodeAlignments, SKOSResource resource) {
        for (NodeAlignmentSmall alignment : nodeAlignments) {

            int prop = -1;
            switch (alignment.getAlignement_id_type()) {

                case 1:
                    prop = SKOSProperty.exactMatch;
                    break;
                case 2:
                    prop = SKOSProperty.closeMatch;
                    break;
                case 3:
                    prop = SKOSProperty.broadMatch;
                    break;
                case 4:
                    prop = SKOSProperty.relatedMatch;
                    break;
                case 5:
                    prop = SKOSProperty.narrowMatch;
                    break;
            }

            resource.addMatch(alignment.getUri_target(), prop);

        }

    }

    private void addRelationGiven(ArrayList<NodeHieraRelation> btList, ArrayList<NodeHieraRelation> ntList,
            ArrayList<NodeHieraRelation> rtList, SKOSResource resource, String idTheso) {
        for (NodeHieraRelation rt : rtList) {
            int prop;

            switch (rt.getRole()) {
                case "RHP":
                    prop = SKOSProperty.relatedHasPart;
                    break;
                case "RPO":
                    prop = SKOSProperty.relatedPartOf;
                    break;
                default:
                    prop = SKOSProperty.related;
            }
            resource.addRelation(getUriFromNodeUri(rt.getUri(), idTheso), prop);
        }
        for (NodeHieraRelation nt : ntList) {
            int prop;
            switch (nt.getRole()) {
                case "NTG":
                    prop = SKOSProperty.narrowerGeneric;
                    break;
                case "NTP":
                    prop = SKOSProperty.narrowerPartitive;
                    break;
                case "NTI":
                    prop = SKOSProperty.narrowerInstantial;
                    break;
                default:
                    prop = SKOSProperty.narrower;
            }
            resource.addRelation(getUriFromNodeUri(nt.getUri(), idTheso), prop);
        }
        for (NodeHieraRelation bt : btList) {

            int prop;
            switch (bt.getRole()) {
                case "BTG":
                    prop = SKOSProperty.broaderGeneric;
                    break;
                case "BTP":
                    prop = SKOSProperty.broaderPartitive;
                    break;
                case "BTI":
                    prop = SKOSProperty.broaderInstantial;
                    break;
                default:
                    prop = SKOSProperty.broader;
            }
            resource.addRelation(getUriFromNodeUri(bt.getUri(), idTheso), prop);
        }
    }

    private void addNoteGiven(ArrayList<NodeNote> nodeNotes, SKOSResource resource, List<NodeLang> selectedLanguages) {
        for (NodeNote note : nodeNotes) {

            boolean isInselectedLanguages = false;
            for (NodeLang nodeLang : selectedLanguages) {
                if (nodeLang.getCode().equals(note.getLang())) {
                    isInselectedLanguages = true;

                    break;
                }

            }
            if (!isInselectedLanguages) {
                continue;
            }

            int prop;
            switch (note.getNotetypecode()) {
                case "scopeNote":
                    prop = SKOSProperty.scopeNote;
                    break;
                case "historyNote":
                    prop = SKOSProperty.historyNote;
                    break;
                case "definition":
                    prop = SKOSProperty.definition;
                    break;
                case "editorialNote":
                    prop = SKOSProperty.editorialNote;
                    break;
                default:
                    prop = SKOSProperty.note;
                    break;
            }
            resource.addDocumentation(note.getLexicalvalue(), note.getLang(), prop);
        }
    }

    private void addNoteGiven(ArrayList<NodeNote> nodeNotes, SKOSResource resource) {
        for (NodeNote note : nodeNotes) {
            int prop;
            switch (note.getNotetypecode()) {
                case "scopeNote":
                    prop = SKOSProperty.scopeNote;
                    break;
                case "historyNote":
                    prop = SKOSProperty.historyNote;
                    break;
                case "definition":
                    prop = SKOSProperty.definition;
                    break;
                case "editorialNote":
                    prop = SKOSProperty.editorialNote;
                    break;
                default:
                    prop = SKOSProperty.note;
                    break;
            }
            resource.addDocumentation(note.getLexicalvalue(), note.getLang(), prop);
        }
    }

    private void addGPSGiven(NodeGps gps, SKOSResource resource) {
        if (gps == null) {
            return;
        }

        double lat = gps.getLatitude();
        double lon = gps.getLongitude();

        resource.setGPSCoordinates(new SKOSGPSCoordinates(lat, lon));

    }

    public String getUriFromId(String id) {
        String uri;
        uri = urlSite + id;
        return uri;
    }
    
    /**
     * Cette fonction permet de retourner l'URI du concept avec identifiant Ark
     * : si renseigné sinon l'URL du Site
     *
     * @param nodeConceptExport
     * @return
     */
    private String getUri(NodeConceptExport nodeConceptExport) {
        String uri = "";
        if (nodeConceptExport == null) {
            //      System.out.println("nodeConcept = Null");
            return uri;
        }
        if (nodeConceptExport.getConcept() == null) {
            //    System.out.println("nodeConcept.getConcept = Null");
            return uri;
        }
        
        // Choix de l'URI pour l'export : 
        // Si Handle est actif, on le prend en premier 
        // sinon,  on vérifie si Ark est actif, 
        // en dernier, on prend l'URL basique d'Opentheso
        // 1 seule URI est possible pour l'export par concept
        

        // URI de type Ark
        if (nodeConceptExport.getConcept().getIdArk() != null) {
            if (!nodeConceptExport.getConcept().getIdArk().trim().isEmpty()) {
                uri = nodePreference.getServeurArk() + nodeConceptExport.getConcept().getIdArk();
                return uri;
            }
        }
        // URI de type Handle
        if (nodeConceptExport.getConcept().getIdHandle() != null) {
            if (!nodeConceptExport.getConcept().getIdHandle().trim().isEmpty()) {
                uri = "https://hdl.handle.net/" + nodeConceptExport.getConcept().getIdHandle();
                return uri;
            }
        }        
        // si on ne trouve pas ni Handle, ni Ark
    //    uri = nodePreference.getCheminSite() + nodeConceptExport.getConcept().getIdConcept();
//        uri = nodePreference.getCheminSite() + "?idc=" + nodeConceptExport.getConcept().getIdConcept()
//                        + "&idt=" + nodeConceptExport.getConcept().getIdThesaurus();
        uri = nodePreference.getCheminSite() + nodeConceptExport.getConcept().getIdConcept();

        
        return uri;
    }
    
    
    /**
     * Cette fonction permet de retourner l'URI du concept avec identifiant Ark
     * : si renseigné sinon l'URL du Site
     *
     * @param nodeConceptExport
     * @return
     */
    private String getUriFromGroup(NodeGroupLabel nodeGroupLabel) {
        String uri = "";
        if (nodeGroupLabel == null) {
            //      System.out.println("nodeConcept = Null");
            return uri;
        }
        if (nodeGroupLabel.getIdGroup() == null) {
            //    System.out.println("nodeConcept.getConcept = Null");
            return uri;
        }
        
        // Choix de l'URI pour l'export : 
        // Si Handle est actif, on le prend en premier 
        // sinon,  on vérifie si Ark est actif, 
        // en dernier, on prend l'URL basique d'Opentheso
        // 1 seule URI est possible pour l'export par concept
        

        // URI de type Ark
        if (nodeGroupLabel.getIdArk() != null) {
            if (!nodeGroupLabel.getIdArk().trim().isEmpty()) {
                uri = nodePreference.getServeurArk() + nodeGroupLabel.getIdArk();
                return uri;
            }
        }
        // URI de type Handle
        if (nodeGroupLabel.getIdHandle() != null) {
            if (!nodeGroupLabel.getIdHandle().trim().isEmpty()) {
                uri = "https://hdl.handle.net/" + nodeGroupLabel.getIdHandle();
                return uri;
            }
        }        
        // si on ne trouve pas ni Handle, ni Ark
//        uri = nodePreference.getCheminSite() + nodeGroupLabel.getIdGroup();
//        uri = nodePreference.getCheminSite() + "?idg=" + nodeGroupLabel.getIdGroup()
//                    + "&idt=" + nodeGroupLabel.getIdThesaurus();

        uri = nodePreference.getCheminSite() + nodeGroupLabel.getIdGroup();
        return uri;
    }
    
    /**
     * Cette fonction permet de retourner l'URI du concept avec identifiant Ark
     * : si renseigné sinon l'URL du Site
     *
     * @param nodeConceptExport
     * @return
     */
    private String getUriGroupFromNodeUri(NodeUri nodeUri, String idTheso) {
        String uri = "";
        if (nodeUri == null) {
            //      System.out.println("nodeConcept = Null");
            return uri;
        }
        
        // Choix de l'URI pour l'export : 
        // Si Handle est actif, on le prend en premier 
        // sinon,  on vérifie si Ark est actif, 
        // en dernier, on prend l'URL basique d'Opentheso
        // 1 seule URI est possible pour l'export par concept
        
        // URI de type Ark
        if (nodeUri.getIdArk() != null) {
            if (!nodeUri.getIdArk().trim().isEmpty()) {
                uri = nodePreference.getServeurArk() + nodeUri.getIdArk();
                return uri;
            }
        }         
        // URI de type Handle
        if (nodeUri.getIdHandle() != null) {
            if (!nodeUri.getIdHandle().trim().isEmpty()) {
                uri = "https://hdl.handle.net/" + nodeUri.getIdHandle();
                return uri;
            }
        } 

        // si on ne trouve pas ni Handle, ni Ark
    //    uri = nodePreference.getCheminSite() + nodeUri.getIdConcept();
//        uri = nodePreference.getCheminSite() + "?idg=" + nodeUri.getIdConcept()
//                        + "&idt=" + idTheso;
        uri = nodePreference.getCheminSite() + nodeUri.getIdConcept();
        
        return uri;
    }   
    
    /**
     * Cette fonction permet de retourner l'URI du concept avec identifiant Ark
     * : si renseigné sinon l'URL du Site
     *
     * @param nodeConceptExport
     * @return
     */
    private String getUriFromNodeUri(NodeUri nodeUri, String idTheso) {
        String uri = "";
        if (nodeUri == null) {
            //      System.out.println("nodeConcept = Null");
            return uri;
        }
        
        // Choix de l'URI pour l'export : 
        // Si Handle est actif, on le prend en premier 
        // sinon,  on vérifie si Ark est actif, 
        // en dernier, on prend l'URL basique d'Opentheso
        // 1 seule URI est possible pour l'export par concept
        
        // URI de type Ark
        if (nodeUri.getIdArk() != null) {
            if (!nodeUri.getIdArk().trim().isEmpty()) {
                uri = nodePreference.getServeurArk() + nodeUri.getIdArk();
                return uri;
            }
        }         
        // URI de type Handle
        if (nodeUri.getIdHandle() != null) {
            if (!nodeUri.getIdHandle().trim().isEmpty()) {
                uri = "https://hdl.handle.net/" + nodeUri.getIdHandle();
                return uri;
            }
        } 

        // si on ne trouve pas ni Handle, ni Ark
    //    uri = nodePreference.getCheminSite() + nodeUri.getIdConcept();
//        uri = nodePreference.getCheminSite() + "?idc=" + nodeUri.getIdConcept()
//                        + "&idt=" + idTheso;   
//                        //+ "&amp;idt=" + idTheso;
                        
        uri = nodePreference.getCheminSite() + nodeUri.getIdConcept();                         
        return uri;
    }       

    public SKOSXmlDocument getSkosXmlDocument() {
        return skosXmlDocument;
    }

    public void setSkosXmlDocument(SKOSXmlDocument skosXmlDocument) {
        this.skosXmlDocument = skosXmlDocument;
    }

    public NodePreference getNodePreference() {
        return nodePreference;
    }

    public void setNodePreference(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

    
}
