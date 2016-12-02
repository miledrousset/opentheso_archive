package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.core.alignment.AlignementSource;
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
     * Cette fonction permet d'ajouter un nouvel alignement sur un
     * thésaurus distant pour ce concept
     * 
     * @param ds 
     * @param nodeAlignment 

     * @return  
     */
    public boolean addNewAlignment(HikariDataSource ds,
            NodeAlignment nodeAlignment) {

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
                            + nodeAlignment.getId_author()
                            + ",'" + nodeAlignment.getConcept_target() + "'"
                            + ",'" + nodeAlignment.getThesaurus_target() + "'"
                            + ",'" + nodeAlignment.getUri_target() + "'"
                            + "," + nodeAlignment.getAlignement_id_type() 
                            + ",'" + nodeAlignment.getInternal_id_thesaurus() + "'"
                            + ",'" + nodeAlignment.getInternal_id_concept() + "')";

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
            log.error("Error while adding external alignement with target : " + nodeAlignment.getUri_target(), sqle);
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
                            + " where id = " + idAlignment
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
     * Cette focntion permet de supprimer tous les aligenements d'un concept
     * @param conn
     * @param idConcept
     * @param idThesaurus 
     * @return  
     */
    public boolean deleteAlignmentOfConcept(Connection conn,
            String idConcept, String idThesaurus) {

        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from alignement "
                            + " where internal_id_concept = " + idConcept
                            + " and internal_id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting alignment from thesaurus with idConcept : " + idConcept, sqle);
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
                            + " WHERE id =" + idAlignment
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
                    String query = "SELECT id, created, modified,"
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
                        nodeAlignment.setId_alignement(resultSet.getInt("id"));
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
    
    /**
     * cette fonction permet de récupérer les informations de la table des sources d'alignement
     * @param ds
     * @param id_theso
     * @return 
     */
    public ArrayList<AlignementSource> getAlignementSource(HikariDataSource ds, String id_theso)
    {
        ArrayList<AlignementSource>alignementSources = new ArrayList<>();
        
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        
         try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select "
                            + " alignement_source.source, alignement_source.requete,"
                            + " alignement_source.type_rqt, alignement_source.alignement_format,"
                            + " alignement_source.id from alignement_source, thesaurus_alignement_source"
                            + " WHERE thesaurus_alignement_source.id_alignement_source = alignement_source.id"
                            + " AND thesaurus_alignement_source.id_thesaurus = '" + id_theso + "'";
                    resultSet=stmt.executeQuery(query);
                    while(resultSet.next())
                    {
                        AlignementSource alignementSource = new AlignementSource();
                        alignementSource.setSource(resultSet.getString("source"));
                        alignementSource.setRequete(resultSet.getString("requete"));
                        alignementSource.setTypeRequete(resultSet.getString("type_rqt"));
                        alignementSource.setAlignement_format(resultSet.getString("alignement_format"));
                        alignementSource.setId(resultSet.getInt("id"));
                        alignementSources.add(alignementSource);
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
            log.error("Error while getting colection of Type of Alignment : ", sqle);
        }
        return alignementSources;
    }
    
    
    /**
     * cette fonction permet de récupérer les informations de la table des sources d'alignement
     * @param ds
     * @param id_alignement_source
     * @return 
     */
    public List<String> getSelectedAlignementOfThisTheso(HikariDataSource ds, int id_alignement_source)
    {
        List<String> listAlignementSourceSelected = null;
        
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        
         try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select "
                            + " id_thesaurus from thesaurus_alignement_source"
                            + " WHERE id_alignement_source = " + id_alignement_source;
                    resultSet=stmt.executeQuery(query);
                    listAlignementSourceSelected = new ArrayList<>();
                    while(resultSet.next())
                    {  
                        listAlignementSourceSelected.add(resultSet.getString("id_thesaurus"));

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
            log.error("Error while getting colection of Type of Alignment : ", sqle);
        }
        return listAlignementSourceSelected;
    }    
    
    public ArrayList<AlignementSource> getAlignementSourceSAdmin(HikariDataSource ds)
    {
        ArrayList<AlignementSource>alignementSources = new ArrayList<>();
        
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
         try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select  * from alignement_source";
                    resultSet=stmt.executeQuery(query);
                    while(resultSet.next())
                    {
                        AlignementSource alignementSource = new AlignementSource();
                        alignementSource.setSource(resultSet.getString("source"));
                        alignementSource.setRequete(resultSet.getString("requete"));
                        alignementSource.setTypeRequete(resultSet.getString("type_rqt"));
                        alignementSource.setAlignement_format(resultSet.getString("alignement_format"));
                        alignementSource.setId(resultSet.getInt("id"));
                        alignementSources.add(alignementSource);
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
            log.error("Error while getting colection of Type of Alignment : ", sqle);
        }
        return alignementSources;
    }
    public ArrayList<String> typesRequetes(HikariDataSource ds, 
            String cherche) throws SQLException
    {
        ArrayList<String> les_types = null;
        Statement stmt;
        ResultSet resultSet;
        Connection conn;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt= conn.createStatement();
                    String query="select e.enumlabel\n" +
                                "from pg_type t, pg_enum e\n" +
                                "where t.oid = e.enumtypid\n" +
                                "and t.typname = '"+ cherche+"'";
                    resultSet=stmt.executeQuery(query);
                    les_types = new ArrayList<>();
                    while(resultSet.next())
                    {
                        les_types.add(resultSet.getString("enumlabel"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Types : " + cherche, sqle);
        }
        return les_types;        
    }
    public void injenctdansBDAlignement(HikariDataSource ds, List<String>listThesos,AlignementSource alig)
    {
        for (String listTheso : listThesos) {
            insertAlignementSource(ds, listTheso, alig);
        }
    }
    public void insertAlignementSource(HikariDataSource ds, String idTheso, AlignementSource alig)
    {
        Statement stmt;
        Connection conn;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt= conn.createStatement();

                        String query="Insert into alignement_source"
                            + "(id_thesaurus, source,requete,type_rqt,"
                            + "alignement_format) values('"
                            + idTheso +"','"
                            + alig.getSource()+"','"
                            + alig.getRequete()+"','"
                            + alig.getTypeRequete()+ "','"
                            + alig.getAlignement_format()+"');";
                    stmt.executeQuery(query);
                    
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement : ", sqle);
        } 
    }
    public void update_alignementSource(HikariDataSource ds, AlignementSource alig, int id)
    {
        Statement stmt;
        Connection conn;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt= conn.createStatement();

                        String query="update alignement_source set "
                                + "source ='"+alig.getSource()
                                + "', requete ='"+alig.getRequete()
                                + "', type_rqt ='"+alig.getTypeRequete()
                                + "', alignement_format='"+alig.getAlignement_format()
                                + "' where id ="+ id;
                    stmt.execute(query);
                    
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement : ", sqle);
        } 
        
    }
    
    
    /**
     * permet d'ajouter une source d'alignement à un ou plusieurs thésaurus
     * on supprime d'abord les anciennes valeurs, puis on ajoute les nouvelles
     * @param ds
     * @param authorizedThesaurus
     * @param listThesos
     * @param idAlignement 
     * @return  
     */
    public boolean addSourceAlignementToTheso(HikariDataSource ds,
            ArrayList<Map.Entry<String, String>> authorizedThesaurus,
            List<String> listThesos, int idAlignement) {
        boolean status = false;
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            
            // suppression des anciennes relations
            for (Map.Entry<String, String> auEntry : authorizedThesaurus) {
                if(!deleteSourceAlignementFromTheso(conn,
                        auEntry.getValue(), idAlignement)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }

            for (String listTheso : listThesos) {
                if(!insertSourceAlignementToTheso(conn, listTheso, idAlignement)) {
                    conn.rollback();
                    conn.close();
                    return false;
                }
            }
            conn.commit();
            conn.close();
            status = true;
            
        } catch (SQLException ex) {
            Logger.getLogger(AlignmentHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    private boolean deleteSourceAlignementFromTheso(Connection conn, String idTheso, int idAlignement)
    {
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    stmt= conn.createStatement();

                        String query="delete from thesaurus_alignement_source"
                            + " where id_alignement_source = " + idAlignement
                            + " and id_thesaurus = '" + idTheso + "'";
                    stmt.executeUpdate(query);
                    status = true;
                    
                } finally {
                    stmt.close();
                }
            } finally {
             //   conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement to theasurus : " + idTheso + " id_alignement : " + idAlignement, sqle);
        } 
        return status;
    }
    
    
    private boolean insertSourceAlignementToTheso(Connection conn, String idTheso, int idAlignement)
    {
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    stmt= conn.createStatement();

                        String query="Insert into thesaurus_alignement_source"
                            + "(id_thesaurus, id_alignement_source) values("
                            + "'" + idTheso + "',"
                            + idAlignement + ")";
                    stmt.executeUpdate(query);
                    status = true;
                    
                } finally {
                    stmt.close();
                }
            } finally {
             //   conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while insert new Alignement to theasurus : " + idTheso + " id_alignement : " + idAlignement, sqle);
        }
        return status;
    }
    
    
    public void efaceAligSour(HikariDataSource ds, int id)
    {
        Statement stmt;
        Connection conn;
        
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    stmt= conn.createStatement();

                        String query="delete from alignement_source"
                                + " where id =" + id;
                    stmt.executeUpdate(query);
                    
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while delete Alignement : ", sqle);
        } 
    }
}
