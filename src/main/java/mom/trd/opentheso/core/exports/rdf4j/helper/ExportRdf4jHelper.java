/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.rdf4j.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mom.trd.opentheso.SelectedBeans.DownloadBean;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeGps;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.NodeUri;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
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

    private String idTheso;
    private SKOSXmlDocument skosXmlDocument;
    // le thésaurus avec ses traductions
    private NodeThesaurus nodeThesaurus;

    private ArrayList<String> rootGroupList;
    private HashMap<String, String> superGroupHashMap;

    ArrayList<NodeUri> nodeTTs =  new ArrayList<>();

    public ExportRdf4jHelper() {
        skosXmlDocument = new SKOSXmlDocument();
        superGroupHashMap = new HashMap<String, String>();

    }

    public boolean setInfos(HikariDataSource ds,
            String formatDate, boolean useArk, String adressSite) {
        this.ds = ds;
        this.formatDate = formatDate;
        this.useArk = useArk;
        this.adressSite = adressSite;

        return true;
    }

    public void addConcept(String idThesaurus, DownloadBean downloadBean, List<NodeLang> selectedLanguages) {
        // récupération de tous les concepts
        for (NodeUri nodeTT1 : nodeTTs) {

            SKOSResource concept = new SKOSResource();
            concept.addRelation(getUriFromId(idTheso), SKOSProperty.topConceptOf);

            //fils top concept
            addFilsConcpetRecursif(idThesaurus, nodeTT1.getIdConcept(), concept, downloadBean, selectedLanguages);

        }
    }

    private void addFilsConcpetRecursif(String idThesaurus, String idPere, SKOSResource concept, DownloadBean downloadBean, List<NodeLang> selectedLanguages) {

        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList<String> listIdsOfConceptChildren = conceptHelper.getListChildrenOfConcept(ds, idPere, idThesaurus);

        writeConceptInfo(conceptHelper, concept, idThesaurus, idPere, downloadBean, selectedLanguages);

        for (String idOfConceptChildren : listIdsOfConceptChildren) {
            concept = new SKOSResource();
            //writeConceptInfo(conceptHelper, concept, idThesaurus, idOfConceptChildren, downloadBean, selectedLanguages);

            //if (conceptHelper.haveChildren(ds, idThesaurus, idOfConceptChildren)) {
                addFilsConcpetRecursif(idThesaurus, idOfConceptChildren, concept, downloadBean, selectedLanguages);
            //}
        }

    }

    private void writeConceptInfo(ConceptHelper conceptHelper, SKOSResource concept,
            String idThesaurus, String idOfConceptChildren, DownloadBean downloadBean, List<NodeLang> selectedLanguages) {

        NodeConceptExport nodeConcept = conceptHelper.getConceptForExport(ds, idOfConceptChildren, idThesaurus, false);

        if (nodeConcept == null) {
            return;
        }

        concept.setUri(getUriFromId(idOfConceptChildren));
        concept.setProperty(SKOSProperty.Concept);

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
                concept.addLabel(traduction.getLexicalValue(), traduction.getLang(), SKOSProperty.prefLabel);
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
                concept.addLabel(nodeEM.getLexical_value(), nodeEM.getLang(), SKOSProperty.altLabel);
            }
        }

        addNoteGiven(nodeConcept.getNodeNoteConcept(), concept, selectedLanguages);
        addGPSGiven(nodeConcept.getNodeGps(), concept);
        addAlignementGiven(nodeConcept.getNodeAlignmentsList(), concept);
        addRelationUriGiven(nodeConcept.getNodeListIdsOfBT(), nodeConcept.getNodeListIdsOfNT(), nodeConcept.getNodeListIdsOfRT(), concept);

        String notation = nodeConcept.getConcept().getNotation();
        String created = nodeConcept.getConcept().getCreated().toString();
        String modified = nodeConcept.getConcept().getModified().toString();

        if (notation != null && !notation.equals("null")) {
            concept.addNotation(notation);
        }
        if (created != null) {
            concept.addDate(created, SKOSProperty.created);
        }
        if (modified != null) {
            concept.addDate(modified, SKOSProperty.modified);
        }

        concept.addRelation(getUriFromId(idTheso), SKOSProperty.inScheme);

        downloadBean.setProgress_abs(downloadBean.getProgress_abs() + 1);
        double progress = (downloadBean.getProgress_abs() / downloadBean.getSizeOfTheso()) * 100;

        if (progress > 100) {
            progress = 100;
        }

        downloadBean.setProgress_per_100((int) progress);

        skosXmlDocument.addconcept(concept);

    }

    private void addMember(String id, String idThesaurus, SKOSResource resource) {

        RelationsHelper relationsHelper = new RelationsHelper();
        ArrayList<String> listChildren = relationsHelper.getListIdsOfNT(ds, id, idThesaurus);

        for (String idChildren : listChildren) {
            resource.addRelation(getUriFromId(idChildren), SKOSProperty.member);
            addMember(idChildren, idThesaurus, resource);
        }

    }

    /*
    private void addSelectedGroupRecursif(){
        
    }*/
    public void addGroup(String idThesaurus, List<NodeLang> selectedLanguages, List<NodeGroup> selectedGroups) {

        rootGroupList = new GroupHelper().getListIdOfRootGroup(ds, idTheso);

        for (String idGroup : rootGroupList) {

            boolean isInselectedGroups = false;
            for (NodeGroup nodeGroup : selectedGroups) {
                if (nodeGroup.getConceptGroup().getIdgroup().equals(idGroup)) {
                    isInselectedGroups = true;
                    break;
                }
            }
            if (!isInselectedGroups) {
                continue;
            }

            SKOSResource group = new SKOSResource(getUriFromId(idGroup), SKOSProperty.ConceptGroup);
            group.addRelation(getUriFromId(idThesaurus), SKOSProperty.microThesaurusOf);

            addFilsGroupRcursif(idThesaurus, idGroup, group, selectedLanguages);

        }

    }

    private void addFilsGroupRcursif(String idThesaurus, String idPere, SKOSResource group, List<NodeLang> selectedLanguages) {

        GroupHelper groupHelper = new GroupHelper();

        ArrayList<String> listIdsOfGroupChildren = groupHelper.getListGroupChildIdOfGroup(ds, idPere, idThesaurus);

        writeGroupInfo(groupHelper, group, idThesaurus, idPere, selectedLanguages);

        for (String idOfGroupChildren : listIdsOfGroupChildren) {
            group = new SKOSResource();

            //writeGroupInfo(groupHelper, group, idThesaurus, idOfGroupChildren, selectedLanguages);

            //if (!groupHelper.getListGroupChildIdOfGroup(ds, idOfGroupChildren, idThesaurus).isEmpty()) {
                addFilsGroupRcursif(idThesaurus, idOfGroupChildren, group, selectedLanguages);
            //}
        }

    }

    private void writeGroupInfo(GroupHelper groupHelper, SKOSResource group,
            String idThesaurus, String idOfGroupChildren, List<NodeLang> selectedLanguages) {

        NodeGroupLabel nodeGroupLabel;
        nodeGroupLabel = new GroupHelper().getNodeGroupLabel(ds, idOfGroupChildren, idThesaurus);

        group.setUri(getUriFromId(idOfGroupChildren));
        group.setProperty(SKOSProperty.ConceptGroup);

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

            group.addLabel(traduction.getTitle(), traduction.getIdLang(), SKOSProperty.prefLabel);

            //dates
            String created = null;
            String modified = null;
            created = traduction.getCreated().toString();
            modified = traduction.getModified().toString();
            if (created != null) {
                group.addDate(created, SKOSProperty.created);
            }
            if (modified != null) {
                group.addDate(modified, SKOSProperty.modified);
            }

        }

        ArrayList<String> childURI = new GroupHelper().getListGroupChildIdOfGroup(ds, idOfGroupChildren, idThesaurus);
        ArrayList<NodeConceptTree> nodeConceptTrees = new ConceptHelper().getListTopConcepts(ds, idOfGroupChildren, idThesaurus, "fr");

        
        for (NodeConceptTree node : nodeConceptTrees) {
            String id = node.getIdConcept();
            group.addRelation(getUriFromId(id), SKOSProperty.member);
            addMember(id, idThesaurus, group);

        }

        for (String id : childURI) {
            group.addRelation(getUriFromId(id), SKOSProperty.subGroup);
            superGroupHashMap.put(id, idOfGroupChildren);
        }

        addNotes(idOfGroupChildren, group, selectedLanguages);
        addGPS(idOfGroupChildren, group);
        addAlignement(idOfGroupChildren, group);
        addRelation(idOfGroupChildren, group);

        String idSuperGroup = superGroupHashMap.get(idOfGroupChildren);

        if (idSuperGroup != null) {
            group.addRelation(getUriFromId(idSuperGroup), SKOSProperty.superGroup);
            superGroupHashMap.remove(idOfGroupChildren);
        }

        skosXmlDocument.addGroup(group);

        //liste top concept
        nodeTTs.addAll(new ConceptHelper().getListIdsOfTopConceptsForExport(ds, idOfGroupChildren, idThesaurus));
        for (NodeUri topConcept : nodeTTs) {
            skosXmlDocument.getConceptScheme().addRelation(getUriFromId(topConcept.getIdConcept()), SKOSProperty.hasTopConcept);
        }

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
            if (creator != null) {
                conceptScheme.addCreator(creator, SKOSProperty.creator);
            }
            if (contributor != null) {
                conceptScheme.addCreator(creator, SKOSProperty.contributor);
            }
            if (title != null && language != null) {
                conceptScheme.addLabel(title, language, SKOSProperty.prefLabel);
            }

            //dates
            String created = null;
            String modified = null;
            created = thesaurus.getCreated().toString();
            modified = thesaurus.getModified().toString();
            if (created != null) {
                conceptScheme.addDate(created, SKOSProperty.created);
            }
            if (modified != null) {
                conceptScheme.addDate(modified, SKOSProperty.modified);
            }

        }

        skosXmlDocument.setConceptScheme(conceptScheme);

    }

    private void addAlignement(String id, SKOSResource resource) {
        AlignmentHelper helper = new AlignmentHelper();

        ArrayList<NodeAlignment> nodeAlignments = helper.getAllAlignmentOfConcept(ds, id, idTheso);
        addAlignementGiven(nodeAlignments, resource);

    }

    private void addAlignementGiven(ArrayList<NodeAlignment> nodeAlignments, SKOSResource resource) {
        for (NodeAlignment alignment : nodeAlignments) {

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

    private void addRelation(String id, SKOSResource resource) {

        RelationsHelper helper = new RelationsHelper();
        ArrayList<String> btList = helper.getListIdOfBT(ds, id, idTheso);
        ArrayList<String> ntList = helper.getListIdsOfNT(ds, id, idTheso);
        ArrayList<String> rtList = helper.getListIdsOfRT(ds, id, idTheso);

        addRelationGiven(btList, ntList, rtList, resource);

    }

    private void addRelationGiven(ArrayList<String> btList, ArrayList<String> ntList, ArrayList<String> rtList, SKOSResource resource) {
        for (String rt : rtList) {
            resource.addRelation(getUriFromId(rt), SKOSProperty.related);
        }
        for (String nt : ntList) {
            resource.addRelation(getUriFromId(nt), SKOSProperty.narrower);
        }
        for (String bt : btList) {
            resource.addRelation(getUriFromId(bt), SKOSProperty.broader);
        }
    }

    private void addRelationUriGiven(ArrayList<NodeUri> btList, ArrayList<NodeUri> ntList, ArrayList<NodeUri> rtList, SKOSResource resource) {
        for (NodeUri rt : rtList) {
            resource.addRelation(getUriFromId(rt.getIdConcept()), SKOSProperty.related);
        }
        for (NodeUri nt : ntList) {
            resource.addRelation(getUriFromId(nt.getIdConcept()), SKOSProperty.narrower);
        }
        for (NodeUri bt : btList) {
            resource.addRelation(getUriFromId(bt.getIdConcept()), SKOSProperty.broader);
        }
    }

    private void addNotes(String id, SKOSResource resource, List<NodeLang> selectedLanguages) {

        NoteHelper noteHelper = new NoteHelper();
        ArrayList<NodeNote> nodeNotes = null;

        nodeNotes = noteHelper.getListNotesConceptAllLang(ds, id, idTheso);
        nodeNotes.addAll(noteHelper.getListNotesTermAllLang(ds, id, idTheso));

        addNoteGiven(nodeNotes, resource, selectedLanguages);

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

            int prop = -1;
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

    private void addGPS(String id, SKOSResource resource) {

        GpsHelper gpsHelper = new GpsHelper();
        NodeGps gps = null;
        gps = gpsHelper.getCoordinate(ds, id, idTheso);
        addGPSGiven(gps, resource);

    }

    private void addGPSGiven(NodeGps gps, SKOSResource resource) {
        if (gps == null) {
            return;
        }

        double lat = gps.getLatitude();
        double lon = gps.getLongitude();

        resource.setGPSCoordinates(new SKOSGPSCoordinates(lat, lon));

    }

    public String getUriFromId(String ID) {

        String uri = null;

        uri = "https://test/" + ID;

        return uri;
    }

    public SKOSXmlDocument getSkosXmlDocument() {
        return skosXmlDocument;
    }

    public void setSkosXmlDocument(SKOSXmlDocument skosXmlDocument) {
        this.skosXmlDocument = skosXmlDocument;
    }

}
