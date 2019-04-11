/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.csv;

/// Classe temporaire en attendant de restructurer l'import CSV

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;



/**
 *
 * @author miled.rousset
 */
public class InsertBDD {

    public InsertBDD() {
    }
    
    public void addSingleConcept(
            HikariDataSource ds,
            String idTheso,
            String idConceptPere,
            String idGroup,
            int idUser,
            ArrayList<CsvHelper.ConceptObject> conceptObjects) {

        Concept concept = new Concept();
        Term term = new Term();
        ConceptHelper conceptHelper = new ConceptHelper();
        RelationsHelper relationsHelper = new RelationsHelper();
        GroupHelper groupHelper = new GroupHelper();
        
        //addConcepts NT
        for (CsvHelper.ConceptObject conceptObject : conceptObjects) {
            concept.setIdConcept(conceptObject.getId());
            concept.setIdThesaurus(idTheso);
            concept.setTopConcept(false);
            concept.setIdGroup(idGroup);
            conceptHelper.insertConceptInTable(ds, concept, idUser);
            
            //on lie le nouveau concept au concept père
            relationsHelper.insertHierarchicalRelation(ds,
                    concept.getIdConcept(),
                    idTheso,
                    "BT",
                    idConceptPere);
            relationsHelper.insertHierarchicalRelation(ds,
                    idConceptPere,
                    idTheso,
                    "NT",
                    concept.getIdConcept());
            
            // prefLabels and altLabels
            for (CsvHelper.ConceptObject conceptObject1 : conceptObjects) {
                ArrayList<CsvHelper.Label> preLabels = conceptObject1.getPrefLabels();
                ArrayList<CsvHelper.Label> altLabels = conceptObject1.getAltLabels();
                
                for (CsvHelper.Label preLabel : preLabels) {
                    
                }
            
                
            }            
            
            
            groupHelper.addConceptGroupConcept(ds, idGroup, concept.getIdConcept(), concept.getIdThesaurus());            
        }
    }
    
}
