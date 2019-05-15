/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.csv;

import com.zaxxer.hikari.HikariDataSource;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;


/**
 *
 * @author miled.rousset
 */
public class CsvImportHelper {

    private String message = "";
    private NodePreference nodePreference;
    
    public CsvImportHelper(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

    public String getMessage() {
        return message;
    }
    
    public void addSingleConcept(
            HikariDataSource ds,
            String idTheso,
            String idConceptPere,
            String idGroup,
            int idUser,
            CsvReadHelper.ConceptObject conceptObject) {

        boolean first = true;
        String idConcept = null;
        String idTerm = null;
        
        // ajout du concept
        ConceptHelper conceptHelper = new ConceptHelper();
        conceptHelper.setNodePreference(nodePreference);
        Concept concept = new Concept();
        TermHelper termHelper = new TermHelper();
        
        // On vérifie si le conceptPere est un Groupe, alors il faut ajouter un TopTerm, sinon, c'est un concept avec des reraltions
        if(idConceptPere == null)
            concept.setTopConcept(true);
        else
            concept.setTopConcept(false);
        
        concept.setIdGroup(idGroup);
        concept.setIdThesaurus(idTheso);
        concept.setStatus("");
        concept.setNotation("");
        concept.setIdConcept(conceptObject.getId());        

        Term term = new Term();
        term.setId_thesaurus(idTheso);
        
        // ajout des PrefLabel
        for (CsvReadHelper.Label prefLabel : conceptObject.getPrefLabels()) {
            if(first) {
                term.setLang(prefLabel.getLang());
                term.setLexical_value(prefLabel.getLabel());
                term.setSource("");
                term.setStatus("");                
                idConcept = conceptHelper.addConcept(ds, idConceptPere, "NT", concept, term, idUser); 
                if(idConcept == null) {
                    message = message + "\n" + "erreur dans l'intégration du concept " + prefLabel.getLabel();
                }
                idTerm = termHelper.getIdTermOfConcept(ds, idConcept, idTheso);
                if(idTerm == null) {
                    message = message + "\n" + "erreur dans l'intégration du concept " + prefLabel.getLabel();
                }                
                first = false;
            } // ajout des traductions
            else {
                if (idConcept != null) {
                    term.setId_thesaurus(idTheso);
                    term.setLang(prefLabel.getLang());
                    term.setLexical_value(prefLabel.getLabel());
                    term.setId_term(idTerm);
                    term.setContributor(idUser);
                    term.setCreator(idUser);
                    term.setSource("");
                    term.setStatus("");
                    if (!conceptHelper.addConceptTraduction(ds, term, idUser)) {
                        message = message + "\n" + "erreur dans l'intégration du terme " + prefLabel.getLabel();
                    }
                }
            }
        }
        
        NoteHelper noteHelper = new NoteHelper();
        // add définition
        if (idConcept != null) {
            for (CsvReadHelper.Label definition : conceptObject.getDefinition()) {
                    noteHelper.addTermNote(ds,idTerm,
                                definition.getLang(),
                                idTheso,
                                definition.getLabel(),
                                "definition", idUser);
            }
        }
        
        
        // add altLabel
        if (idConcept != null) {
            for (CsvReadHelper.Label altLabel : conceptObject.getAltLabels()) {
                term.setId_term(idTerm);
                term.setId_thesaurus(idTheso);
                term.setLang(altLabel.getLang());
                term.setLexical_value(altLabel.getLabel());
                term.setHidden(false);
                term.setStatus("USE");
                term.setSource("");    
            
                if (!termHelper.addNonPreferredTerm(ds,
                        term, idUser)) {
                    message = message + "\n" + "erreur dans l'intégration du synonyme : " + altLabel.getLabel();
                }
            }
        }
        
    }
}
