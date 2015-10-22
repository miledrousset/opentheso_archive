package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AlignmentHelper {
    
    private final Log log = LogFactory.getLog(ThesaurusHelper.class);
    
    public AlignmentHelper() {
        
    }

    /**
     * Cette fonction permet d'ajouter un nouvel alignement sur un
     * thésaurus distant pour ce concept
     * 
     * @param ds 
     * @param author 
     * @param conceptTarget 
     * @param thesaurusTarget 
     * @param uriTarget 
     * @param idTypeAlignment 
     * @param idConcept 
     * @param idThesaurus 
     * @return  
     */
    public boolean addNewAlignment(HikariDataSource ds,
            int author,
            String conceptTarget, String thesaurusTarget,
            String uriTarget, int idTypeAlignment,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;

        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into alignement "
                            + "(author, concept_target, thesaurus_target,"
                            + " uri_target, alignement_id_type,"
                            + " internal_id_thesaurus, internal_id_concept)"
                            + " values ("
                            + author
                            + ",'" + conceptTarget + "'"
                            + ",'" + thesaurusTarget + "'"
                            + ",'" + uriTarget + "'"
                            + "," + idTypeAlignment 
                            + ",'" + idThesaurus + "'"
                            + ",'" + idConcept + "')";

                    stmt.executeUpdate(query);
                    
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding external alignement with target : " + uriTarget, sqle);
        }
        return status;
    }
    
    /**
     * Cette focntion permet de supprimer un alignement 
     * @param ds 
     * @param idAlignment 
     * @param idThesaurus 
     * @return  
     */
    public boolean deleteAlignment(HikariDataSource ds,
            int idAlignment, String idThesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from alignement "
                            + " where id_alignement = " + idAlignment
                            + " and internal_id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting alignment from thesaurus with idAlignment : " + idAlignment, sqle);
        }
        return status;
    }    
    
    /**
     * Cette fonction permet de mettre à jour un Terme à la table Term, en
     * paramètre un objet Classe Term
     *
     * @param ds
     * @param idAlignment
     * @param conceptTarget
     * @param thesaurusTarget
     * @param idConcept
     * @param idTypeAlignment
     * @param uriTarget
     * @param idThesaurus
     * @return
     */
    public boolean updateAlignment(HikariDataSource ds,
            int idAlignment,
            String conceptTarget, String thesaurusTarget,
            String uriTarget, int idTypeAlignment,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE alignement set"
                            + " concept_target = '" + conceptTarget + "',"
                            + " modified = current_date,"
                            + " thesaurus_target = '" + thesaurusTarget + "',"
                            + " uri_target = '" + uriTarget + "',"
                            + " alignement_id_type = " + idTypeAlignment + ","
                            + " thesaurus_target = '" + thesaurusTarget + "'"
                            + " WHERE id_alignement =" + idAlignment
                            + " AND internal_id_thesaurus = '" + idThesaurus + "'"
                            + " AND internal_id_concept = '" + idConcept + "'";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating Alignment : " + idAlignment, sqle);
        }
        return status;
    }
    
    /**
     * Cette fonction permet de retourner la liste des alignements pour un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class 
     */
    public ArrayList<NodeAlignment> getAllAlignmentOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeAlignment> nodeAlignmentList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_alignement, created, modified,"
                            + " author, thesaurus_target, concept_target,"
                            + " uri_target, alignement_id_type, internal_id_thesaurus,"
                            + " internal_id_concept FROM alignement"
                            + " where internal_id_concept = '" + idConcept + "'"
                            + " and internal_id_thesaurus ='" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodeAlignmentList = new ArrayList<>();
                    while (resultSet.next()) {
                        NodeAlignment nodeAlignment = new NodeAlignment();
                        nodeAlignment.setId_alignement(resultSet.getInt("id_alignement"));
                        nodeAlignment.setCreated(resultSet.getDate("created"));
                        nodeAlignment.setModified(resultSet.getDate("modified"));
                        nodeAlignment.setId_author(resultSet.getInt("author"));
                        nodeAlignment.setThesaurus_target(resultSet.getString("thesaurus_target"));
                        nodeAlignment.setConcept_target(resultSet.getString("concept_target"));
                        nodeAlignment.setUri_target(resultSet.getString("uri_target").trim());
                        nodeAlignment.setAlignement_id_type(resultSet.getInt("alignement_id_type"));
                        nodeAlignment.setInternal_id_thesaurus(resultSet.getString("internal_id_thesaurus"));
                        nodeAlignment.setInternal_id_concept(resultSet.getString("internal_id_concept"));
                        
                        nodeAlignmentList.add(nodeAlignment);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All list of alignment of Concept  : " + idConcept, sqle);
        }
        return nodeAlignmentList;
    }
    
    /**
     * Retourne la liste des types d'alignements sous forme de MAP (id + Nom)
     *
     * @param ds
     * @return
     */
    public HashMap<String, String> getAlignmentType(HikariDataSource ds) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        HashMap<String, String> map = new HashMap<>();
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id, label_skos from alignement_type";
                            
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while(resultSet.next()) {
                        map.put(
                                String.valueOf(resultSet.getInt("id")),
                                resultSet.getString("label_skos"));
                    }
                    resultSet.close();

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Map of Type of Alignment : " + map.toString(), sqle);
        }
        return map;
    }    
    
}
