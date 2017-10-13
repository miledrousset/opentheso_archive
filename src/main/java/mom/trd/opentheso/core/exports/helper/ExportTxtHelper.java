/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.helper;

import com.ctc.wstx.util.StringUtil;
import com.k_int.IR.IRQuery;
import com.k_int.IR.QueryModels.PrefixString;
import com.k_int.IR.SearchException;
import com.k_int.IR.SearchTask;
import com.k_int.IR.Searchable;
import com.k_int.IR.TimeoutExceededException;
import com.k_int.hss.HeterogeneousSetOfSearchable;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.RelationsHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeEM;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeRT;
import mom.trd.opentheso.bdd.helper.nodes.NodeUri;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.helper.nodes.thesaurus.NodeThesaurus;
import mom.trd.opentheso.bdd.tools.AsciiUtils;
import mom.trd.opentheso.bdd.tools.StringPlus;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author miled.rousset
 */
public class ExportTxtHelper {

    private StringBuffer txtBuff;
    private List<NodeGroup> selectedGroups;
    private String selectedLang;
    private String idTheso;
    private HikariDataSource ds;
    private int indentationConcept = 0;
    private int indentationNT = 0;
    private int indentationBT = 0;
    private int indentationRT = 0;
    private int indentationUF = 0;    
    private int indentationTraductions = 0; 
    private int indentationNotes = 0; 
    
    private NodePreference nodePreference;
    private String[] selectedOptions;
    
    public ExportTxtHelper() {
    }

    /**
     * Cette fonction permet de récupérer toutes les données d'un thésaurus puis
     * les charger dans la classe thesaurusDatas en filtrant par langue et
     * Groupe
     *
     * @param ds
     * @param idThesaurus
     * @param selectedLang
     * @param selectedGroups
     * @param nodePreference
     * @param selectedOptions
     */
    public void setThesaurusDatas(HikariDataSource ds,
            String idThesaurus,
            String selectedLang,
            List<NodeGroup> selectedGroups,
            NodePreference nodePreference,
            String[] selectedOptions) {

        this.idTheso = idThesaurus;
        this.selectedLang = selectedLang;
        this.selectedGroups = selectedGroups;
        this.ds = ds;
        this.nodePreference = nodePreference;
        this.selectedOptions = selectedOptions;
    }

    /**
     * permet de préparer le thésaurus au format tabulé. Les données sont écrites
     * dans des variables type StringBuffer suivant les options sélectionnées 
     * dans le tableau String[] selectedOptions
     *
     * @param selectedOptions
     * @return
     */
    public boolean exportToTxtCsv() {
        txtBuff = new StringBuffer();
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        
        NodeThesaurus nodeThesaurus = thesaurusHelper.getNodeThesaurus(ds, idTheso);
        if(nodeThesaurus == null) return false;
        
        // on écrit le label du thésaurus
        writeHeader(nodeThesaurus);
        
        
        // écriture des groupes
        GroupHelper groupHelper = new GroupHelper();
        NodeGroup nodeGroup;
        for (NodeGroup selectedGroup : selectedGroups) {
            nodeGroup = groupHelper.getThisConceptGroup(ds, selectedGroup.getConceptGroup().getIdgroup(), idTheso, selectedLang);
            writeGroup(nodeGroup);
            
            // écriture récursive des concepts 
            writeConcepts(nodeGroup.getConceptGroup().getIdgroup());
            
        }
        return true;
    }

    /**
     * permet d'acrire le titre du thésaurus
     *
     * @return
     */
    private void writeHeader(NodeThesaurus nodeThesaurus) {

        for (Thesaurus thesaurus : nodeThesaurus.getListThesaurusTraduction()) {
            if (thesaurus.getLanguage().equalsIgnoreCase(selectedLang)) {
                txtBuff.append(thesaurus.getTitle());
                txtBuff.append(" (");
                txtBuff.append(idTheso);
                txtBuff.append(" )");
            }
        }
        txtBuff.append("\n");
    }

    /**
     * permet d'acrire le group du thésaurus
     *
     * @return
     */
    private void writeGroup(NodeGroup nodeGroup) {
        if (nodeGroup.getIdLang().equalsIgnoreCase(selectedLang)) {
            txtBuff.append(nodeGroup.getLexicalValue());
            txtBuff.append(" (");
            txtBuff.append(nodeGroup.getConceptGroup().getIdgroup());
            txtBuff.append(" )");
        }
        txtBuff.append("\n\n");
    }    
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @param idGroup 
     */
    private void writeConcepts(String idGroup) {
        
        ConceptHelper conceptHelper = new ConceptHelper();
        
        
        ArrayList<NodeUri> nodeUris;
        // la liste des TopConcepts pour un Groupe
        nodeUris = conceptHelper.getListIdsOfTopConceptsForExport(ds, idGroup, idTheso);
        if(nodeUris == null) return;
        
        // pour compter le décalage (indentations)
        nodeUris.forEach((nodeUri) -> {
            String indentation = "";
            countIndentation(nodeUri.getIdConcept(), indentation);
        });
        
        // on écrit les concepts et le décalage par tabulation
        nodeUris.forEach((nodeUri) -> {
            String indentation = "";
            writeConceptsInfo(nodeUri.getIdConcept(), indentation);
            writeConceptRecursive(nodeUri.getIdConcept(), indentation);
        });
    }

    /**
     * permet de compter le nombre de décalalge pour chaque type de données 
     * concept : pour l'hiérarchie
     * NT : pour le nombre maxi de NT existant dans le thésaurus ou le domaine choisi
     * RT ...
     * UF ....
     * 
     * @param idConcept
     * @param indentation 
     */
    private void countIndentation(String idConcept, String indentation) {
        ArrayList<NodeNT> childList = new RelationsHelper().getListNT(ds, idConcept, idTheso, selectedLang);
        if (childList == null) return;
        // indentation des concepts par hiérarchie
        indentation += "\t";
        int tot = StringUtils.countMatches(indentation, "\t");
        if(tot > indentationConcept) 
            indentationConcept = tot;
        
        // indentation des NT nombre maxi
        int totNT = new RelationsHelper().getCountOfNT(ds, idConcept, idTheso);
        if(totNT > indentationNT) 
            indentationNT = totNT;

        // indentation des BT nombre maxi
        int totBT = new RelationsHelper().getCountOfBT(ds, idConcept, idTheso);
        if(totBT > indentationBT) 
            indentationBT = totBT;
        
        // indentation des RT nombre maxi
        int totRT = new RelationsHelper().getCountOfRT(ds, idConcept, idTheso);
        if(totRT > indentationRT) 
            indentationRT = totRT;    
        
        // indentation des UF nombre maxi
        int totUF = new RelationsHelper().getCountOfUF(ds, idConcept, idTheso);
        if(totUF > indentationUF) 
            indentationUF = totUF;
        
        // indentation traductions
        int totTraductions = new TermHelper().getCountOfTraductions(ds, idConcept, idTheso);
        if(totTraductions > indentationTraductions) 
            indentationTraductions = totTraductions;
        
        // indentation notes
        int totNotes = new NoteHelper().getCountOfNotes(ds, idConcept, idTheso);
        if(totNotes > indentationNotes) 
            indentationNotes = totNotes;        
        
        
        for (NodeNT nodeNT : childList) {
            countIndentation(nodeNT.getIdConcept(), indentation);
        }
    }      
    

    /**
     * fonction recursive qui sert a ecrire tout les fils des term
     *
     * @param id
     * @param indentation
     * @param paragraphs
     * @param idToDoc
     */
    private void writeConceptRecursive(String idConcept, String indentation) {
        ArrayList<NodeNT> childList = new RelationsHelper().getListNT(ds, idConcept, idTheso, selectedLang);
        if (childList == null) return;
        indentation += "\t";
        for (NodeNT nodeNT : childList) {
            writeConceptsInfo(nodeNT.getIdConcept(), indentation);
            writeConceptRecursive(nodeNT.getIdConcept(), indentation);
        }
    }    
 
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @return 
     */
    private void writeConceptsInfo(String idConcept, String indentation) {
        ConceptHelper conceptHelper = new ConceptHelper();
        GroupHelper groupHelper = new GroupHelper();
        NodeConcept nodeConcept =  conceptHelper.getConcept(ds, idConcept, idTheso,selectedLang);
        StringPlus stringPlus = new StringPlus();
        
        if(nodeConcept == null) return;
        
        boolean first = true;
        // Id_concept
        txtBuff.append(idConcept);
        txtBuff.append("\t");
        
        // on écrit le Term_concept
        txtBuff.append(indentation);
        
        if(nodeConcept.getTerm().getLexical_value().isEmpty()) {
            txtBuff.append("(");
            txtBuff.append(idConcept);
            txtBuff.append(")");
        } else {
            txtBuff.append(nodeConcept.getTerm().getLexical_value());
        }
        int tot = StringUtils.countMatches(indentation, "\t");
        if(tot < indentationConcept) {
            addTabulate (indentationConcept - tot);
        }
        
        //on écrit les occurences 
        txtBuff.append("\t");
        txtBuff.append("Nb:");
        txtBuff.append(totalOfNotices(idConcept));
        
        // on écrit la relation TG
        txtBuff.append("\t");        
        if(!nodeConcept.getNodeBT().isEmpty()) {
        for (NodeBT nodeBT : nodeConcept.getNodeBT()) {
                if(!first) 
                    txtBuff.append("; ");
                txtBuff.append("BT");
                txtBuff.append(":");
                txtBuff.append(nodeBT.getTitle());
                first = false;
            }
        }
        first = true;
        // on écrit la relation NT 
        txtBuff.append("\t");
        if(!nodeConcept.getNodeNT().isEmpty()) {
            for (NodeNT nodeNT : nodeConcept.getNodeNT()) {
                if(!first) 
                    txtBuff.append("; ");                
                txtBuff.append("NT");
                txtBuff.append(":");
                txtBuff.append(nodeNT.getTitle());
                first = false;
            }
        }
        first = true;
        // on écrit la relation RT 
        txtBuff.append("\t");
        if(!nodeConcept.getNodeRT().isEmpty()) {
            for (NodeRT nodeRT : nodeConcept.getNodeRT()) {
                if(!first) 
                    txtBuff.append("; ");                
                txtBuff.append("RT");
                txtBuff.append(":");
                txtBuff.append(nodeRT.getTitle());
                first = false;
            }
        } 
        first = true;
        // on écrit la relation UF 
        txtBuff.append("\t");
        if(!nodeConcept.getNodeEM().isEmpty()) {
            for (NodeEM nodeEM : nodeConcept.getNodeEM()) {
                if(!first) 
                    txtBuff.append("; ");                
                txtBuff.append("UF");
                txtBuff.append(":");
                txtBuff.append(nodeEM.getLexical_value());
                first = false;
            }
        }         
        first = true;
        // on écrit les Groupes 
        txtBuff.append("\t");
        ArrayList<String> listIdGroup = groupHelper.getListIdGroupOfConcept(ds, idTheso, idConcept);
        if(listIdGroup != null) {
            if(!listIdGroup.isEmpty()) {
                for (String idGroup : listIdGroup) {
                    if(!first) 
                        txtBuff.append("; ");
                    txtBuff.append("GR");
                    txtBuff.append(":");
                    txtBuff.append(groupHelper.getLexicalValueOfGroup(ds, idGroup, idTheso, selectedLang));               
                    first = false;
                }
            }
        }
        
        // on écrit les notes 
        first = true;
        txtBuff.append("\t");
        if(!nodeConcept.getNodeNotesConcept().isEmpty()){ 
            for (NodeNote nodeNote1 : nodeConcept.getNodeNotesConcept()) {
                if(!first) 
                    txtBuff.append("## ");
                txtBuff.append(nodeNote1.getNotetypecode());
                txtBuff.append(":");
                txtBuff.append(stringPlus.clearNewLine(nodeNote1.getLexicalvalue()));
                first = false;
            }
        }
        first = true;
        if(!nodeConcept.getNodeNotesTerm().isEmpty()){
            for (NodeNote nodeNote1 : nodeConcept.getNodeNotesTerm()) {
                if(!first) 
                    txtBuff.append("## ");
                txtBuff.append(nodeNote1.getNotetypecode());
                txtBuff.append(":");
                txtBuff.append(stringPlus.clearNewLine(nodeNote1.getLexicalvalue()));
                first = false;
            }
        }
        txtBuff.append("\n");        
    }
    
    /**
     * permet de rajouter les tabulations manquantes
     * @param count 
     */
    private void addTabulate (int count) {
        for (int i = 0; i < count-1; i++){
            txtBuff.append("\t");
        }
    }
            
    /**
     * fonction temporaire qui ne marche qu'avec Koha
     */
    private int totalOfNotices(String idConcept) {
        int tot = 0;
        if(nodePreference == null) return 0;
        
        if (nodePreference.isZ3950actif()) {
            Properties p = new Properties();
            p.put("CollectionDataSourceClassName", "com.k_int.util.Repository.XMLDataSource");
            p.put("RepositoryDataSourceURL", "file:" + nodePreference.getPathNotice1());
            p.put("XSLConverterConfiguratorClassName", "com.k_int.IR.Syntaxes.Conversion.XMLConfigurator");
            p.put("ConvertorConfigFile", nodePreference.getPathNotice2());   

            Searchable federated_search_proxy = new HeterogeneousSetOfSearchable();
            federated_search_proxy.init(p);
            
            try {
                IRQuery e = new IRQuery();
                //   e.collections = new Vector<String>();
                e.collections.add("KOHA/biblios");
                e.hints.put("default_element_set_name", "f");
                e.hints.put("small_set_setname", "f");
                e.hints.put("record_syntax", "unimarc");

                e.query = new PrefixString((new StringBuilder("@attrset bib-1 @attr 1=Koha-Auth-Number \"")).append(AsciiUtils.convertNonAscii("" + idConcept)).append("\"").toString());
                SearchTask st = federated_search_proxy.createTask(e, null);
                st.evaluate(5000);
                tot = st.getTaskResultSet().getFragmentCount();
                st.destroyTask();
                federated_search_proxy.destroy();
            } catch (TimeoutExceededException | SearchException srch_e) {
               // srch_e.printStackTrace();
            }
        }
        return tot;
    }    

 
    /////////////////////////////////////////
    /////////////////////////////////////////
    //// export des Notes ///////////////////
    /////////////////////////////////////////
    /////////////////////////////////////////
    
    /**
     * permet de préparer le thésaurus au format tabulé les données sont écrites
     * dans une variable type StringBuffer
     *
     * @return
     */
    public boolean exportNotes() {
        txtBuff = new StringBuffer();
        
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        
        NodeThesaurus nodeThesaurus = thesaurusHelper.getNodeThesaurus(ds, idTheso);
        if(nodeThesaurus == null) return false;
        
        // on écrit le label du thésaurus
        writeHeader(nodeThesaurus);
        
        
        // écriture des groupes
        GroupHelper groupHelper = new GroupHelper();
        NodeGroup nodeGroup;
        for (NodeGroup selectedGroup : selectedGroups) {
            nodeGroup = groupHelper.getThisConceptGroup(ds, selectedGroup.getConceptGroup().getIdgroup(), idTheso, selectedLang);
            writeGroup(nodeGroup);
            
            // écriture récursive des concepts 
            writeConceptsNote(nodeGroup.getConceptGroup().getIdgroup());
            
        }
        return true;
    }      
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @param idGroup 
     */
    private void writeConceptsNote(String idGroup) {
        ConceptHelper conceptHelper = new ConceptHelper();
        
        ArrayList<NodeUri> nodeUris;
        // la liste des TopConcepts pour un Groupe
        nodeUris = conceptHelper.getListIdsOfTopConceptsForExport(ds, idGroup, idTheso);
        if(nodeUris == null) return;
        
        for (NodeUri nodeUri : nodeUris) {
            writeConceptNoteInfo(nodeUri.getIdConcept());
            writeConceptNoteRecursive(nodeUri.getIdConcept());
        }
    }


    /**
     * fonction recursive qui sert a ecrire tout les fils des term
     *
     * @param id
     * @param indentation
     * @param paragraphs
     * @param idToDoc
     */
    private void writeConceptNoteRecursive(String idConcept) {
        ArrayList<NodeNT> childList = new RelationsHelper().getListNT(ds, idConcept, idTheso, selectedLang);
        if (childList == null) return;
        for (NodeNT nodeNT : childList) {
            writeConceptNoteInfo(nodeNT.getIdConcept());
            writeConceptNoteRecursive(nodeNT.getIdConcept());
        }
    }    
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @return 
     */
    private void writeConceptNoteInfo(String idConcept) {
       NoteHelper noteHelper = new NoteHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        StringPlus stringPlus = new StringPlus();
        TermHelper termHelper = new TermHelper();
        
        // notes de type Concept
        ArrayList<NodeNote> nodeNoteConcept = noteHelper.getListNotesConcept(ds, idConcept, idTheso, selectedLang);
        // notes de type terme
        ArrayList<NodeNote> nodeNoteTerm = noteHelper.getListNotesTerm(ds,
                termHelper.getIdTermOfConcept(ds, idConcept, idTheso),
                idTheso, selectedLang);

        boolean first = true;
        if(nodeNoteConcept.isEmpty() && nodeNoteTerm.isEmpty()) return;

        txtBuff.append(idConcept);
        txtBuff.append("\t");
        txtBuff.append(conceptHelper.getLexicalValueOfConcept(ds, idConcept, idTheso, selectedLang));
        txtBuff.append("\t");
        for (NodeNote nodeNote1 : nodeNoteTerm) {
            if(!first) 
                txtBuff.append("## ");
            txtBuff.append(nodeNote1.getNotetypecode());
            txtBuff.append(": ");
            txtBuff.append(stringPlus.clearNewLine(nodeNote1.getLexicalvalue()));
            first = false;
        }
        first = true;
        for (NodeNote nodeNote1 : nodeNoteConcept) {
            if(!first) 
                txtBuff.append("## ");
            txtBuff.append(nodeNote1.getNotetypecode());
            txtBuff.append(": ");
            txtBuff.append(stringPlus.clearNewLine(nodeNote1.getLexicalvalue()));
            first = false;
        }        
        txtBuff.append("\n");
    }    
    
    /////////////////////////////////////////
    /////////////////////////////////////////
    //// export des Synonymes ///////////////
    ////    AltLabel      ///////////////////
    /////////////////////////////////////////
    /////////////////////////////////////////
    
    /**
     * permet de préparer le thésaurus au format tabulé les données sont écrites
     * dans une variable type StringBuffer
     *
     * @return
     */
    public boolean exportAltLabel() {
        txtBuff = new StringBuffer();
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        NodeThesaurus nodeThesaurus = thesaurusHelper.getNodeThesaurus(ds, idTheso);
        if(nodeThesaurus == null) return false;
        
        // on écrit le label du thésaurus
        writeHeader(nodeThesaurus);
        
        // écriture des groupes
        GroupHelper groupHelper = new GroupHelper();
        NodeGroup nodeGroup;
        for (NodeGroup selectedGroup : selectedGroups) {
            nodeGroup = groupHelper.getThisConceptGroup(ds, selectedGroup.getConceptGroup().getIdgroup(), idTheso, selectedLang);
            writeGroup(nodeGroup);
            
            // écriture récursive des concepts 
            writeConceptAltLabel(nodeGroup.getConceptGroup().getIdgroup());
        }
        return true;
    }      
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @param idGroup 
     */
    private void writeConceptAltLabel(String idGroup) {
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<NodeUri> nodeUris;
        // la liste des TopConcepts pour un Groupe
        nodeUris = conceptHelper.getListIdsOfTopConceptsForExport(ds, idGroup, idTheso);
        if(nodeUris == null) return;
        
        for (NodeUri nodeUri : nodeUris) {
            writeConceptAltLabelInfo(nodeUri.getIdConcept());
            writeConceptAltLabelRecursive(nodeUri.getIdConcept());
        }
    }

    /**
     * fonction recursive qui sert a ecrire tout les fils des term
     *
     * @param id
     * @param indentation
     * @param paragraphs
     * @param idToDoc
     */
    private void writeConceptAltLabelRecursive(String idConcept) {
        ArrayList<NodeNT> childList = new RelationsHelper().getListNT(ds, idConcept, idTheso, selectedLang);
        if (childList == null) {
            return;
        }
        for (NodeNT nodeNT : childList) {
            writeConceptAltLabelInfo(nodeNT.getIdConcept());
            writeConceptAltLabelRecursive(nodeNT.getIdConcept());
        }
    }    
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @return 
     */
    private void writeConceptAltLabelInfo(String idConcept) {
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        
        // NodeRT pour retrouver tous les RT d'un concept
        ArrayList<NodeEM> nodeEMs = termHelper.getNonPreferredTerms(ds,
                termHelper.getIdTermOfConcept(ds, idConcept, idTheso),
                idTheso, selectedLang);
        if(nodeEMs == null) return;
        
        boolean first = true;
        if(nodeEMs.isEmpty()) return;

        txtBuff.append(idConcept);
        txtBuff.append("\t");
        txtBuff.append(conceptHelper.getLexicalValueOfConcept(ds, idConcept, idTheso, selectedLang));
        txtBuff.append("\t");
        for (NodeEM nodeEM : nodeEMs) {
            if(!first) 
                txtBuff.append("; ");
            txtBuff.append(nodeEM.getLexical_value());
            first = false;
        }
        txtBuff.append("\n");
    }
    
    /////////////////////////////////////////
    /////////////////////////////////////////
    //// export des termes associés /////////
    ////////////// RT ///////////////////////
    /////////////////////////////////////////
    
    /**
     * permet de préparer le thésaurus au format tabulé les données sont écrites
     * dans une variable type StringBuffer
     *
     * @return
     */
    public boolean exportRT() {
        txtBuff = new StringBuffer();
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        NodeThesaurus nodeThesaurus = thesaurusHelper.getNodeThesaurus(ds, idTheso);
        if(nodeThesaurus == null) return false;
        
        // on écrit le label du thésaurus
        writeHeader(nodeThesaurus);
        
        // écriture des groupes
        GroupHelper groupHelper = new GroupHelper();
        NodeGroup nodeGroup;
        for (NodeGroup selectedGroup : selectedGroups) {
            nodeGroup = groupHelper.getThisConceptGroup(ds, selectedGroup.getConceptGroup().getIdgroup(), idTheso, selectedLang);
            writeGroup(nodeGroup);
            
            // écriture récursive des concepts 
            writeConceptAltLabel(nodeGroup.getConceptGroup().getIdgroup());
        }
        return true;
    }      
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @param idGroup 
     */
    private void writeConceptRT(String idGroup) {
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<NodeUri> nodeUris;
        // la liste des TopConcepts pour un Groupe
        nodeUris = conceptHelper.getListIdsOfTopConceptsForExport(ds, idGroup, idTheso);
        if(nodeUris == null) return;
        
        for (NodeUri nodeUri : nodeUris) {
            writeConceptRTInfo(nodeUri.getIdConcept());
            writeConceptRTRecursive(nodeUri.getIdConcept());
        }
    }

    /**
     * fonction recursive qui sert a ecrire tout les fils des term
     *
     * @param id
     * @param indentation
     * @param paragraphs
     * @param idToDoc
     */
    private void writeConceptRTRecursive(String idConcept) {
        ArrayList<NodeNT> childList = new RelationsHelper().getListNT(ds, idConcept, idTheso, selectedLang);
        if (childList == null) {
            return;
        }
        for (NodeNT nodeNT : childList) {
            writeConceptRTInfo(nodeNT.getIdConcept());
            writeConceptRTRecursive(nodeNT.getIdConcept());
        }
    }    
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @return 
     */
    private void writeConceptRTInfo(String idConcept) {
        RelationsHelper relationsHelper = new RelationsHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
               
        // NodeRT pour retrouver tous les RT d'un concept
        ArrayList<NodeRT> nodeRTs = relationsHelper.getListRT(ds, idConcept, idTheso, idTheso);
        if(nodeRTs == null) return;
        
        boolean first = true;
        if(nodeRTs.isEmpty()) return;

        txtBuff.append(idConcept);
        txtBuff.append("\t");
        txtBuff.append(conceptHelper.getLexicalValueOfConcept(ds, idConcept, idTheso, selectedLang));
        txtBuff.append("\t");
        for (NodeRT nodeRT : nodeRTs) {
            if(!first) 
                txtBuff.append("; ");
            txtBuff.append(nodeRT.getTitle());
            txtBuff.append(" (");
            txtBuff.append(nodeRT.getIdConcept());
            txtBuff.append(")");
            first = false;
        }
        txtBuff.append("\n");
    }
    
    /////////////////////////////////////////
    /////////////////////////////////////////
    ////// export des traductions ///////////
    /////////////////////////////////////////
    /////////////////////////////////////////
    
    /**
     * permet de préparer le thésaurus au format tabulé les données sont écrites
     * dans une variable type StringBuffer
     *
     * @return
     */
    public boolean exportTraductions() {
        txtBuff = new StringBuffer();
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        NodeThesaurus nodeThesaurus = thesaurusHelper.getNodeThesaurus(ds, idTheso);
        if(nodeThesaurus == null) return false;
        
        // on écrit le label du thésaurus
        writeHeader(nodeThesaurus);
        
        // écriture des groupes
        GroupHelper groupHelper = new GroupHelper();
        NodeGroup nodeGroup;
        for (NodeGroup selectedGroup : selectedGroups) {
            nodeGroup = groupHelper.getThisConceptGroup(ds, selectedGroup.getConceptGroup().getIdgroup(), idTheso, selectedLang);
            writeGroup(nodeGroup);
            
            // écriture récursive des concepts 
            writeConceptTraductions(nodeGroup.getConceptGroup().getIdgroup());
        }
        return true;
    }      
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @param idGroup 
     */
    private void writeConceptTraductions(String idGroup) {
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<NodeUri> nodeUris;
        // la liste des TopConcepts pour un Groupe
        nodeUris = conceptHelper.getListIdsOfTopConceptsForExport(ds, idGroup, idTheso);
        if(nodeUris == null) return;
        
        for (NodeUri nodeUri : nodeUris) {
            writeConceptTraductionsInfo(nodeUri.getIdConcept());
            writeConceptTraductionsRecursive(nodeUri.getIdConcept());
        }
    }

    /**
     * fonction recursive qui sert a ecrire tout les fils des term
     *
     * @param id
     * @param indentation
     * @param paragraphs
     * @param idToDoc
     */
    private void writeConceptTraductionsRecursive(String idConcept) {
        ArrayList<NodeNT> childList = new RelationsHelper().getListNT(ds, idConcept, idTheso, selectedLang);
        if (childList == null) {
            return;
        }
        for (NodeNT nodeNT : childList) {
            writeConceptTraductionsInfo(nodeNT.getIdConcept());
            writeConceptTraductionsRecursive(nodeNT.getIdConcept());
        }
    }    
    
    /**
     * Permet d'écrire les concepts en bouclant par récursivité pour définir les niveaux
     * @return 
     */
    private void writeConceptTraductionsInfo(String idConcept) {
        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        
        // NodeTraductions pour retrouver toutes les traductions d'un concept
        ArrayList<NodeTermTraduction> nodeTermTraductions = termHelper.getTraductionsOfConcept(ds, idConcept, idTheso, selectedLang);
        if(nodeTermTraductions == null) return;
        
        boolean first = true;
        if(nodeTermTraductions.isEmpty()) return;

        txtBuff.append(idConcept);
        txtBuff.append("\t");
        txtBuff.append(conceptHelper.getLexicalValueOfConcept(ds, idConcept, idTheso, selectedLang));
        txtBuff.append("\t");
        for (NodeTermTraduction nodeTermTraduction : nodeTermTraductions) {
            if(!first) 
                txtBuff.append("; ");
            txtBuff.append(nodeTermTraduction.getLexicalValue());
            txtBuff.append(" (");
            txtBuff.append(nodeTermTraduction.getLang());
            txtBuff.append(")");
            first = false;
        }
        txtBuff.append("\n");
    }    
    
    public StringBuffer getTxtBuff() {
        return txtBuff;
    }
}
