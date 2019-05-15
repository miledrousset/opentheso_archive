/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mom.trd.opentheso.SelectedBeans.RoleOnThesoBean;
import mom.trd.opentheso.SelectedBeans.SelectedTerme;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.core.exports.rdf4j.ExportRdf4jHelper;
import mom.trd.opentheso.core.imports.rdf4j.helper.ImportRdf4jHelper;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 *
 * @author miled.rousset
 */
public class CopyAndPasteHelper {

    public CopyAndPasteHelper() {
    }
    
    public boolean pasteConceptLikeNT(
            String currentIdTheso,
            String currentIdConcept,
            String idConceptToMove) {

        return true;
    }
    
    public boolean pasteConceptLikeNTOfGroup(
            String currentIdTheso,
            String currentIdGroup,
            String idConceptToMove) {

        return true;
    }    
    
    public boolean pasteBranchLikeNT(
            HikariDataSource ds,
            String currentIdTheso,
            String currentIdConcept,
            String fromIdTheso,
            String fromIdConcept,
            SelectedTerme selectedTerme, 
            RoleOnThesoBean roleOnThesoBean,
            String identifierType) {
        
        // récupération des concepts du thésaurus de départ
        SKOSXmlDocument sKOSXmlDocument = getBranch(ds,
                fromIdTheso, fromIdConcept);
        
        if(sKOSXmlDocument == null) return false;
        
        // import des concepts dans le thésaurus actuel
        
        if(!addBranch(ds, sKOSXmlDocument, selectedTerme, roleOnThesoBean, identifierType)) {
            return false;
        }

        return true;
    }
    
    private SKOSXmlDocument getBranch(
            HikariDataSource ds,
            String fromIdTheso,
            String fromIdConcept) {
        RDFFormat format = RDFFormat.RDFXML;

        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, fromIdTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, fromIdTheso, nodePreference.getCheminSite());
        exportRdf4jHelper.setNodePreference(nodePreference);
        
        exportRdf4jHelper.addBranch(fromIdTheso, fromIdConcept);
        
        return exportRdf4jHelper.getSkosXmlDocument();
        
/*        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        
        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, format);
        return out.toString();*/
    }
    
    private boolean addBranch(HikariDataSource ds,
            SKOSXmlDocument sKOSXmlDocument,
            SelectedTerme selectedTerme, RoleOnThesoBean roleOnTheso,
            String identifierType) {
        int idGroup = -1; 
        String formatDate = "yyyy-MM-dd";
    
        try {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(ds, formatDate,
                    roleOnTheso.getUser().getUser().getIdUser(),
                    idGroup, 
                    selectedTerme.getIdlangue());//connect.getWorkLanguage());
            // pour récupérer les identifiants pérennes type Ark ou Handle
            importRdf4jHelper.setIdentifierType(identifierType);
            
            importRdf4jHelper.setPrefixHandle("");
            importRdf4jHelper.setNodePreference(roleOnTheso.getNodePreference());
            
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addBranch(selectedTerme);
                return true;
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {

        }
        return false;
    }    
    
    
    public boolean pasteBranchLikeNTOfGroup(
            String currentIdTheso,
            String currentIdGroup,
            String idConceptToMove) {

        return true;
    }  
    
    public boolean pasteGroup(
            HikariDataSource ds,
            String currentIdTheso,
            String fromIdTheso,
            String fromIdConcept,
            RoleOnThesoBean roleOnThesoBean,
            String identifierType) {
        
        // récupération du Groupe entier avec ses concepts du thésaurus de départ
        SKOSXmlDocument sKOSXmlDocument = getBranchGroup(ds,
                fromIdTheso, fromIdConcept);
        
        if(sKOSXmlDocument == null) return false;
        
        // import du groupe entier avec ses concepts dans le thésaurus actuel
        
        if(!addBranchGroup(ds, currentIdTheso, sKOSXmlDocument, roleOnThesoBean,
                identifierType)) {
            return false;
        }

        return true;
    }
    
    private SKOSXmlDocument getBranchGroup(
            HikariDataSource ds,
            String fromIdTheso,
            String fromIdGroup) {

        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(ds, fromIdTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, fromIdTheso, nodePreference.getCheminSite());
        exportRdf4jHelper.setNodePreference(nodePreference);
        
        exportRdf4jHelper.addWholeGroup(fromIdTheso, fromIdGroup);
        
        return exportRdf4jHelper.getSkosXmlDocument();
    }
    
    private boolean addBranchGroup(HikariDataSource ds,
            String idtheso,
            SKOSXmlDocument sKOSXmlDocument, RoleOnThesoBean roleOnTheso,
            String identifierType) {
        int idGroup = -1; 
        String formatDate = "yyyy-MM-dd";
    
        try {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(ds, formatDate,
                    roleOnTheso.getUser().getUser().getIdUser(),
                    idGroup,
                    roleOnTheso.getConnect().getWorkLanguage());//connect.getWorkLanguage());
            // pour récupérer les identifiants pérennes type Ark ou Handle
            importRdf4jHelper.setIdentifierType(identifierType);
            
            importRdf4jHelper.setPrefixHandle("");
            importRdf4jHelper.setNodePreference(roleOnTheso.getNodePreference());
            
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addWholeGroup(idtheso, sKOSXmlDocument);
                return true;
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {

        }
        return false;
    }        
    
    
}
