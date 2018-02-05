package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import fr.mom.arkeo.soap.DcElement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.tools.FileUtilities;
import mom.trd.opentheso.ws.ark.ArkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import skos.SKOSProperty;

public class ToolsHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public ToolsHelper() {

    }


    /**
     * Cette fonction permet de regénerer ou générer tous les identifiants Ark 
     * du thésaurus 
     *
     * @param ds
     * @param idThesaurus
     * @return ArrayList de NodePermute
     */
    public boolean GenerateArkIds(HikariDataSource ds,
            String idThesaurus) {

        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        ArrayList<NodeTermTraduction> nodeTermTraductionList;

        // Génération des Id Ark pour les concepts
        ArrayList<String> tabIdConcept = conceptHelper.getAllIdConceptOfThesaurus(ds, idThesaurus);

        String idArk;
        ArkClient ark_Client = new ArkClient();
        ArrayList<DcElement> dcElementsList = new ArrayList<>();
        
        for (String idConcept : tabIdConcept) {
         

            dcElementsList.clear();
            nodeTermTraductionList = termHelper.getAllTraductionsOfConcept(ds, idConcept, idThesaurus);
            for (NodeTermTraduction nodeTermTraduction : nodeTermTraductionList) {
                DcElement dcElement = new DcElement();
                // cette fonction permet de remplir la table Permutée
                dcElement.setName("description");
                dcElement.setValue(nodeTermTraduction.getLexicalValue());
                dcElement.setLanguage(nodeTermTraduction.getLang());
                dcElementsList.add(dcElement);
            }
            // String date, String url, String title, String creator, String description, String type
            idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                "http://pactols.frantiq.fr/" + "?idc=" + idConcept + "&idt="+idThesaurus,
                                idConcept,
                                "Frantiq",
                                dcElementsList,
                                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
            conceptHelper.updateArkIdOfConcept(ds, idConcept, idThesaurus, idArk);
        }

        return true;
    }
    
    /**
     * Fonction qui permet de repérer les termes orphelins et les ranger dans les orphelins.
     * @param ds
     * @param idThesaurus
     * @return 
     */
    public boolean orphanDetect(HikariDataSource ds,
            String idThesaurus) {
        
        ConceptHelper conceptHelper = new ConceptHelper();
        RelationsHelper relationsHelper = new RelationsHelper();
        OrphanHelper orphanHelper = new OrphanHelper();
        ArrayList<String> idBT;
        ArrayList<String> idConcept1WhereIsNT;

        // récupération de tous les Id concepts du thésaurus
        ArrayList<String> tabIdConcept = conceptHelper.getAllIdConceptOfThesaurus(ds, idThesaurus);
        
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            for (String idConcept : tabIdConcept) {
                idBT = relationsHelper.getListIdBT(ds, idConcept, idThesaurus);
                idConcept1WhereIsNT=relationsHelper.getListIdWhichHaveNt(ds, idConcept, idThesaurus);
                if(idBT.isEmpty() && idConcept1WhereIsNT.isEmpty()) {
                    if(!conceptHelper.isTopConcept(ds, idConcept, idThesaurus)){
                        // le concept est orphelin
                        if(!orphanHelper.isOrphanExist(ds, idConcept, idThesaurus)) {
                            if(!orphanHelper.addNewOrphan(conn, idConcept, idThesaurus)){
                                conn.rollback();
                                conn.close();
                                return false;
                            }
                        }
                    }
                }
                else{
                    if(!(idBT.containsAll(idConcept1WhereIsNT)) ){
                        //alors il manque des BT
                        ArrayList<String> BTmiss =new ArrayList<>(idConcept1WhereIsNT);
                        BTmiss.removeAll(idBT);
                         //on ajoute la différence
                        for(String miss: BTmiss){
                          if(!relationsHelper.insertHierarchicalRelation(conn, idConcept, idThesaurus,"BT", miss)){
                              conn.rollback();
                                conn.close();
                               return false;
                          }
                        }
                    }
                     if(!(idConcept1WhereIsNT.containsAll(idBT))){
                        //il manque des NT pour certain idBT
                        ArrayList<String> NTmiss =new ArrayList<>(idBT);
                        NTmiss.removeAll(idConcept1WhereIsNT);
                        //on jaoute la différence
                        for(String miss:NTmiss){
                            if(!relationsHelper.insertHierarchicalRelation(conn, miss, idThesaurus,"NT", idConcept)){
                              conn.rollback();
                                conn.close();
                              return false;
                            }
                        }
                    }
                }
            }
            conn.commit();
            conn.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ToolsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public String getNewId(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; 
        StringBuilder pass = new StringBuilder();
        for(int x=0;x<length;x++)   {
           int i = (int)Math.floor(Math.random() * (chars.length() -1));
           pass.append(chars.charAt(i));
        }
        return pass.toString();
    }
    
    public Date getDate() {
        return new java.util.Date();
    }
    
}
