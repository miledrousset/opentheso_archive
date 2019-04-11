/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package candidats.imports;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import mom.trd.opentheso.bdd.helper.CandidateHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import mom.trd.opentheso.skosapi.SKOSDocumentation;
import mom.trd.opentheso.skosapi.SKOSLabel;
import mom.trd.opentheso.skosapi.SKOSMatch;
import mom.trd.opentheso.skosapi.SKOSProperty;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import mom.trd.opentheso.skosapi.SKOSResource;

/**
 *
 * @author miled.rousset
 */
public class ImportCandidatsTest {
    private String message;
    Logger logger = LoggerFactory.getLogger(ImportCandidatsTest.class);
    public ImportCandidatsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
    public void readFileSkos() {
        // lecture du fichier tabulé /Users/Miled/
        String path = "/Users/Miled/Desktop/Marion LAME/candidates_skos.xml";
        SKOSXmlDocument sKOSXmlDocument;
       
        FileInputStream file;
        try {
            file = new FileInputStream(path);
            sKOSXmlDocument = loadSkos(file);
            if(sKOSXmlDocument == null){
                
                return;
            }
           
            addCandidates(sKOSXmlDocument);
        } catch (FileNotFoundException ex) {
            logger.error(ex.toString());
        }
    }
    
    private boolean addCandidates( SKOSXmlDocument sKOSXmlDocument) {
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource ds = connexionTest.getConnexionPool();
        CandidateHelper candidateHelper = new CandidateHelper();
        String notes;
        int compteur = 0;
        String idTheso = "23";
        String idConcept = null;
        int idUser = 44;
        
        String idArk;
        String idParentConcept = "";
        
        for (SKOSResource sKOSResource : sKOSXmlDocument.getConceptList()) {
            
            for (SKOSLabel sKOSLabel : sKOSResource.getLabelsList()) {
                idParentConcept = "";
                if(sKOSLabel.getProperty() == SKOSProperty.prefLabel) {
                    if(sKOSLabel.getLanguage().equalsIgnoreCase("fr")) {
                        notes = getNotes(sKOSResource);
                        idArk = getIdParentConcept(sKOSResource);
                        if(idArk != null)
                            idParentConcept = new ConceptHelper().getIdConceptFromArkId(ds, idArk);
                        try {
                            Connection conn = ds.getConnection();
                            idConcept = candidateHelper.addCandidat_rollBack(conn,
                                    sKOSLabel.getLabel(),
                                    sKOSLabel.getLanguage(),
                                    idTheso,
                                    idUser,
                                    notes,
                                    idParentConcept,
                                    "");
                            if(idConcept == null) {
                                conn.rollback();
                                conn.close();
                                continue;
                            }
                            conn.commit();
                            conn.close();
                            compteur = compteur +1;                        
                        } catch (SQLException ex) {
                            java.util.logging.Logger.getLogger(ImportCandidatsTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        if(idConcept != null) {
                            candidateHelper.addTermCandidatTraduction(
                                ds,
                                idConcept,
                                sKOSLabel.getLabel(),
                                sKOSLabel.getLanguage(),
                                idTheso,
                                idUser);
                        }
                    }
                }
            }
        }
        System.out.println("Total = " + compteur);
        return true;
    }
    
    private String getIdParentConcept(SKOSResource sKOSResource) {
        String idArk;
        for (SKOSMatch sKOSMatch : sKOSResource.getMatchList()) {
            if(sKOSMatch.getValue().contains("https://ark.frantiq.fr")) {    
                if(sKOSMatch.getProperty() == SKOSProperty.broadMatch) {
                    idArk = sKOSMatch.getValue().substring(sKOSMatch.getValue().indexOf("ark:/")+5);
                    return idArk;
                }
            }
        }
        return null;
    }
    
    //30 = definition, 34 = editorialNote, 36= Note, 74= Terme spécifique, 72= Terme générique, 
    private String getNotes(SKOSResource sKOSResource) {
        String notes = "";
        String propriete = "";
        
        for (SKOSDocumentation sDocumentation : sKOSResource.getDocumentationsList()) {
            if(!sDocumentation.getText().isEmpty()) {
            /*    if(sDocumentation.getProperty() == SKOSProperty.definition)//30)
                    propriete = "definition";
                if(sDocumentation.getProperty() == SKOSProperty.editorialNote) //34)
                    propriete = "editorialNote";
                if(sDocumentation.getProperty() == SKOSProperty.note)//36)
                    propriete = "Note";*/
                if(sDocumentation.getProperty() == SKOSProperty.changeNote) {
                    if(notes.isEmpty())
                        notes = sDocumentation.getText();
                    else 
                        notes = notes + " ####  " + sDocumentation.getText();
                }
            }
        }
   /*     for (SKOSMatch sKOSMatch : sKOSResource.getMatchList()) {
            if(sKOSMatch.getValue().contains("https://ark.frantiq.fr")) {    
                if(sKOSMatch.getProperty() == 74)
                    propriete = "Terme spécifique";
                if(sKOSMatch.getProperty() == 72)
                    propriete = "Terme générique";
                  
                if(notes.isEmpty())
                    notes = propriete + "--> " + sKOSMatch.getValue();
                else 
                    notes = notes + " ####  " + propriete + "--> " + sKOSMatch.getValue();   
            }
        }*/
        return notes;
    }
    

    
    private SKOSXmlDocument loadSkos(InputStream path) {
       
        ReadRdf4j readRdf4j = null;
        try {
            readRdf4j = new ReadRdf4j(path, 0);
        } catch (IOException ex) {
            message =  ex.getMessage();
        } catch (Exception ex) {
            message = ex.toString();
        }
        if(readRdf4j==null) {
            message = "Erreur de format RDF !!!";
            return null;
        }

        SKOSXmlDocument sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
        int total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size();
        String uri = sKOSXmlDocument.getTitle();
        return sKOSXmlDocument;

        
        
    }
    
 
    
}
