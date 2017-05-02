/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.rdf4j.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeTT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupTraductions;
import mom.trd.opentheso.bdd.helper.nodes.thesaurus.NodeThesaurus;
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
    
    private SKOSXmlDocument skosXmlDocument;
    // le thésaurus avec ses traductions
    private NodeThesaurus nodeThesaurus;
    

    public ExportRdf4jHelper() {
        skosXmlDocument = new SKOSXmlDocument();
        
        
    }
    
    public boolean setInfos(HikariDataSource ds,
            String formatDate, boolean useArk, String adressSite) {
        this.ds = ds;
        this.formatDate = formatDate;
        this.useArk = useArk;
        this.adressSite = adressSite;


        return true;
    }
    
    public void addConcept(String idThesaurus){
        // récupération des ids des Tops Concepts
        ArrayList<NodeTT> nodeTTs = new ConceptHelper().getAllListIdsOfTopConcepts(ds, idThesaurus);
        
        // récupération de tous les concepts
        for (NodeTT nodeTT1 : nodeTTs) {
            
           SKOSResource concept = new SKOSResource(getUriFromId(nodeTT1.getIdConcept()), SKOSProperty.Concept); 
           
           concept.addLabel("test", "fr", SKOSProperty.prefLabel);
           
           skosXmlDocument.addconcept(concept);
           
            
        }
    }
    
    public void addGroup(String idThesaurus){
        NodeGroupLabel nodeGroupLabel;
        ArrayList<String> idGroups = new GroupHelper().getListIdOfGroup(ds, idThesaurus);
        for (String idGroup : idGroups) {
            nodeGroupLabel = new GroupHelper().getNodeGroupLabel(ds, idGroup, idThesaurus);
            
            
            SKOSResource group = new SKOSResource(getUriFromId(idGroup), SKOSProperty.ConceptGroup); // ou Collection ?
            
           for(NodeGroupTraductions traduction :  nodeGroupLabel.getNodeGroupTraductionses()){
               group.addLabel(traduction.getTitle(), traduction.getIdLang(),SKOSProperty.prefLabel);
           }
            
            
            skosXmlDocument.addGroup(group);
        }
        
        
        
        
        
    }
    
    public void addThesaurus(String idThesaurus){
        
        nodeThesaurus = new ThesaurusHelper().getNodeThesaurus(ds, idThesaurus);
        
        String uri = getUriFromId(nodeThesaurus.getIdThesaurus());
        SKOSResource conceptScheme = new SKOSResource(uri,SKOSProperty.ConceptScheme);
        
        String creator;
        String contributor;
        String title;
        String language;
        
        for(Thesaurus thesaurus : nodeThesaurus.getListThesaurusTraduction()){
            creator = thesaurus.getCreator();
            contributor = thesaurus.getContributor();
            title = thesaurus.getTitle();
            language = thesaurus.getLanguage();
            
            /*[...]*/
            
            if(creator!=null)
                conceptScheme.addCreator(creator, SKOSProperty.creator);
            if(contributor!=null)
                conceptScheme.addCreator(creator, SKOSProperty.contributor);
            if(title!=null && language!= null)
                conceptScheme.addLabel(title, language, SKOSProperty.prefLabel);

        }
        
        
        
        
        
        skosXmlDocument.setConceptScheme(conceptScheme);
        
    }
    
    
    
    
    
    
    
    
    public String getUriFromId(String ID){
        
        String uri = null;
        
        uri = "https://test/"+ID;
        
        return uri;
    }
    
    
    

    public SKOSXmlDocument getSkosXmlDocument() {
        return skosXmlDocument;
    }

    public void setSkosXmlDocument(SKOSXmlDocument skosXmlDocument) {
        this.skosXmlDocument = skosXmlDocument;
    }

    
    
    
    
}
