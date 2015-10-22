/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.tabulate;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeTT;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.thesaurus.NodeThesaurus;

/**
 * cette classe contient toutes les données d'un thésaurus
 * 
 * @author miled.rousset
 */
public class ThesaurusDatas {
    
    // le thésaurus avec ses traductions
    private NodeThesaurus nodeThesaurus;
    
    // la liste des domaines avec les traductions
    private ArrayList<NodeGroupLabel> nodeGroupLabels;
    
    // la liste des Ids des Topconcepts
    private ArrayList<NodeTT> nodeTTs;
    
    // la liste des concepts et les termes traduits
    private ArrayList<NodeConceptExport> nodeConceptExports;

    public ThesaurusDatas() {
        this.nodeGroupLabels = new ArrayList<>();
        this.nodeConceptExports = new ArrayList<>();
    }
    
    /**
     * permet de récupérer toutes les données d'un thésaurus
     * puis les chargées dans les variables de la classe
     * 
     * @param ds
     * @param idThesaurus
     * @return 
     */
    public boolean exportAllDatas(HikariDataSource ds, String idThesaurus) {
        
        // récupération du thésaurus 
        this.nodeThesaurus = new ThesaurusHelper().getNodeThesaurus(ds, idThesaurus);
        
        // récupération des groupes
        ArrayList<String> idGroups = new GroupHelper().getListIdOfGroup(ds, idThesaurus);
        for (String idGroup : idGroups) {
            this.nodeGroupLabels.add(new GroupHelper().getNodeGroupLabel(ds, idGroup, idThesaurus));
    //        System.out.println("idGroup = : " + idGroup);
        }
        
        // récupération des ids des Tops Concepts
        nodeTTs = new ConceptHelper().getAllListIdsOfTopConcepts(ds, idThesaurus);
        
        // récupération de tous les concepts
        for (NodeTT nodeTT1 : nodeTTs) {
            new ConceptHelper().exportAllConcepts(ds, nodeTT1.getIdConcept(),
                    idThesaurus, nodeConceptExports);
        }
        
        return true;
    }

    public NodeThesaurus getNodeThesaurus() {
        return nodeThesaurus;
    }

    public void setNodeThesaurus(NodeThesaurus nodeThesaurus) {
        this.nodeThesaurus = nodeThesaurus;
    }

    public ArrayList<NodeGroupLabel> getNodeGroupLabels() {
        return nodeGroupLabels;
    }

    public void setNodeGroupLabels(ArrayList<NodeGroupLabel> nodeGroupLabels) {
        this.nodeGroupLabels = nodeGroupLabels;
    }

    public ArrayList<NodeTT> getNodeTTs() {
        return nodeTTs;
    }

    public void setNodeTTs(ArrayList<NodeTT> nodeTTs) {
        this.nodeTTs = nodeTTs;
    }

    public ArrayList<NodeConceptExport> getNodeConceptExports() {
        return nodeConceptExports;
    }

    public void setNodeConceptExports(ArrayList<NodeConceptExport> nodeConceptExports) {
        this.nodeConceptExports = nodeConceptExports;
    }
    
}
