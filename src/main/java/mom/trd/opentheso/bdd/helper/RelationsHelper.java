/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.datas.Relation;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeNT;
import mom.trd.opentheso.bdd.helper.nodes.NodeRT;
import mom.trd.opentheso.bdd.helper.nodes.NodeUri;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author miled.rousset
 */
public class RelationsHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public RelationsHelper() {
    }

    /**
     * Cette fonction permet de rajouter une relation type Groupe ou domaine à
     * un concept
     *
     * @param conn
     * @param idConcept
     * @param idGroup
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */
    public boolean addRelationMT(Connection conn,
            String idConcept, String idThesaurus,
            String idGroup, int idUser) {

        Statement stmt;
        boolean status = false;

        String query;
        Savepoint savepoint = null;

        try {
            // Get connection from pool
            savepoint = conn.setSavepoint();
            try {
                stmt = conn.createStatement();
                try {
                    /*  if (!new RelationsHelper().addRelationHistorique(conn, idConcept, idThesaurus, idConcept, "MT", idUser, "ADD")) {
                        return false;
                    }*/
                    query = "Insert into concept"
                            + "(id_concept, id_thesaurus, id_ark, top_concept, id_group)"
                            + " values ("
                            + "'" + idConcept + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'',"
                            + "false"
                            + ",'" + idGroup + "')";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            if (sqle.getSQLState().equalsIgnoreCase("23505")) {
                try {
                    if (savepoint != null) {
                        conn.rollback(savepoint);
                        status = true;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RelationsHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                log.error("Error while adding relation MT of Concept : " + idConcept, sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet de rÃ©cupÃ©rer les termes gÃ©nÃ©riques d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class Concept
     */
    public ArrayList<NodeBT> getListBT(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeBT> nodeListBT = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT hierarchical_relationship.id_concept2,"
                                 + " concept.status FROM hierarchical_relationship,"
                                 + " concept WHERE "
                                 + " concept.id_thesaurus = hierarchical_relationship.id_thesaurus"
                                 + " AND "
                                 + " concept.id_concept = hierarchical_relationship.id_concept1"
                                 + " AND"
                                 + " hierarchical_relationship.id_thesaurus = '" + idThesaurus + "'"
                                 + " AND"
                                 + " hierarchical_relationship.id_concept1 = '" + idConcept + "'"
                                 + " AND"
                                 + " hierarchical_relationship.role = 'BT'";
                    
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        nodeListBT = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeBT nodeBT = new NodeBT();
                            nodeBT.setIdConcept(resultSet.getString("id_concept2"));
                            nodeBT.setStatus(resultSet.getString("status"));
                            nodeListBT.add(nodeBT);
                        }
                    }
                    for (NodeBT nodeBT : nodeListBT) {
                        query = "SELECT term.lexical_value, term.status FROM term, preferred_term"
                                + " WHERE preferred_term.id_term = term.id_term"
                                + " and preferred_term.id_concept ='" + nodeBT.getIdConcept() + "'"
                                + " and term.lang = '" + idLang + "'"
                                + " and term.id_thesaurus = '" + idThesaurus + "'"
                                + " order by upper(unaccent_string(lexical_value)) DESC";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            resultSet.next();
                            if (resultSet.getRow() == 0) {
                                nodeBT.setTitle("");
                                nodeBT.setStatus("");
                            } else {
                                if (resultSet.getString("lexical_value") == null || resultSet.getString("lexical_value").equals("")) {
                                    nodeBT.setTitle("");
                                } else {
                                    nodeBT.setTitle(resultSet.getString("lexical_value"));
                                }
                                if (resultSet.getString("status") == null || resultSet.getString("status").equals("")) {
                                    nodeBT.setStatus("");
                                } else {
                                    nodeBT.setStatus(resultSet.getString("status"));
                                }

                            }
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting BT of Concept : " + idConcept, sqle);
        }
        Collections.sort(nodeListBT);
        return nodeListBT;
    }

    /**
     * Cette fonction permet de rÃ©cupÃ©rer la liste des Ids des termes
     * gÃ©nÃ©riques d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public ArrayList<String> getListIdOfBT(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> listIdOfBt = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "BT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        listIdOfBt.add(resultSet.getString("id_concept2"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Ids of BT of Concept : " + idConcept, sqle);
        }
        return listIdOfBt;
    }

    /**
     * Cette fonction permet de rajouter une relation terme gÃ©nÃ©rique Ã  un
     * concept
     *
     * @param conn
     * @param idConceptNT
     * @param idThesaurus
     * @param idConceptBT
     * @param idUser
     * @return boolean
     */
    public boolean addRelationBT(Connection conn,
            String idConceptNT, String idThesaurus,
            String idConceptBT, int idUser) {

        Statement stmt;
        boolean status = false;

        try {
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                try {
                    if (!new RelationsHelper().addRelationHistorique(conn, idConceptNT, idThesaurus, idConceptBT, "BT", idUser, "ADD")) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                    if (!new RelationsHelper().addRelationHistorique(conn, idConceptBT, idThesaurus, idConceptNT, "NT", idUser, "ADD")) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                    String query = "Insert into hierarchical_relationship"
                            + "(id_concept1, id_thesaurus, role, id_concept2)"
                            + " values ("
                            + "'" + idConceptNT + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'BT'"
                            + ",'" + idConceptBT + "')";

                    stmt.executeUpdate(query);
                    query = "Insert into hierarchical_relationship"
                            + "(id_concept1, id_thesaurus, role, id_concept2)"
                            + " values ("
                            + "'" + idConceptBT + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'NT'"
                            + ",'" + idConceptNT + "')";
                    stmt.executeUpdate(query);
                    status = true;
                    // conn.commit();
                } finally {
                    stmt.close();
                }
            } finally {
                //         conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            //  if (sqle.getMessage().contains("duplicate key value violates unique constraint")) {

            if (!sqle.getSQLState().equalsIgnoreCase("23505")) {
                log.error("Error while adding relation BT of Concept : " + idConceptNT, sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet de rajouter une relation dans l'historique
     *
     * @param conn
     * @param idConcept1
     * @param idThesaurus
     * @param idConcept2
     * @param role
     * @param idUser
     * @param action
     * @return boolean
     */
    public boolean addRelationHistorique(Connection conn,
            String idConcept1, String idThesaurus,
            String idConcept2, String role, int idUser, String action) {

        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into hierarchical_relationship_historique"
                            + "(id_concept1, id_thesaurus, role, id_concept2, id_user, action)"
                            + " values ("
                            + "'" + idConcept1 + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'" + role + "'"
                            + ",'" + idConcept2 + "'"
                            + ",'" + idUser + "'"
                            + ",'" + action + "')";

                    stmt.executeUpdate(query);
                    status = true;
                    // System.err.println(query);
                } finally {
                    stmt.close();
                }
            } finally {
                //         conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            // if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
            if (!sqle.getSQLState().equalsIgnoreCase("23505")) {
                log.error("Error while adding relation historique of Concept : " + idConcept1, sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet de récupérer la liste de l'historique des relations
     * d'un concept d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param lang
     * @return Objet class Concept
     */
    public ArrayList<Relation> getRelationHistoriqueAll(HikariDataSource ds,
            String idConcept, String idThesaurus, String lang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<Relation> listRel = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select lexical_value, id_concept2, role, action, hierarchical_relationship_historique.modified, username "
                            + "from hierarchical_relationship_historique, users, preferred_term, term"
                            + " where hierarchical_relationship_historique.id_thesaurus = '" + idThesaurus + "'"
                            + " and hierarchical_relationship_historique.id_concept1=preferred_term.id_concept"
                            + " and preferred_term.id_term=term.id_term"
                            + " and term.lang='" + lang + "'"
                            + " and term.id_thesaurus='" + idThesaurus + "'"
                            + " and ( id_concept1 = '" + idConcept + "'"
                            + " or id_concept2 = '" + idConcept + "' )"
                            + " and hierarchical_relationship_historique.id_user=users.id_user"
                            + " order by hierarchical_relationship_historique.modified DESC";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        listRel = new ArrayList<>();
                        while (resultSet.next()) {
                            Relation r = new Relation();
                            r.setId_relation(resultSet.getString("role"));
                            r.setId_concept1(resultSet.getString("lexical_value"));
                            r.setId_concept2(resultSet.getString("id_concept2"));
                            r.setModified(resultSet.getDate("modified"));
                            r.setIdUser(resultSet.getString("username"));
                            r.setAction(resultSet.getString("action"));
                            r.setId_thesaurus(idThesaurus);
                            listRel.add(r);
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting all relation historique of Concept : " + idConcept, sqle);
        }
        return listRel;
    }

    /**
     * Cette fonction permet de récupérer la liste de l'historique des relations
     * d'un concept à une date précise d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param date
     * @param lang
     * @return Objet class Concept
     */
    public ArrayList<Relation> getRelationHistoriqueFromDate(HikariDataSource ds,
            String idConcept, String idThesaurus, Date date, String lang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<Relation> listRel = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select lexical_value, id_concept2, role, action, hierarchical_relationship_historique.modified, username "
                            + "from hierarchical_relationship_historique, users, preferred_term, term"
                            + " where hierarchical_relationship_historique.id_thesaurus = '" + idThesaurus + "'"
                            + " and hierarchical_relationship_historique.id_concept1=preferred_term.id_concept"
                            + " and preferred_term.id_term=term.id_term"
                            + " and term.lang='" + lang + "'"
                            + " and term.id_thesaurus='" + idThesaurus + "'"
                            + " and ( id_concept1 = '" + idConcept + "'"
                            + " or id_concept2 = '" + idConcept + "' )"
                            + " and hierarchical_relationship_historique.id_user=users.id_user"
                            + " and hierarchical_relationship_historique.modified <= '" + date.toString()
                            + "' order by hierarchical_relationship_historique.modified ASC";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        listRel = new ArrayList<>();

                        while (resultSet.next()) {
                            if (resultSet.getString("action").equals("DEL")) {
                                for (Relation rel : listRel) {
                                    if (rel.getId_concept1().equals(resultSet.getString("lexical_value")) && rel.getId_concept2().equals(resultSet.getString("id_concept2")) && rel.getAction().equals("ADD") && rel.getId_relation().equals(resultSet.getString("role"))) {
                                        listRel.remove(rel);
                                        break;
                                    }
                                }
                            } else {
                                Relation r = new Relation();
                                r.setId_relation(resultSet.getString("role"));
                                r.setId_concept1(resultSet.getString("lexical_value"));
                                r.setId_concept2(resultSet.getString("id_concept2"));
                                r.setModified(resultSet.getDate("modified"));
                                r.setIdUser(resultSet.getString("username"));
                                r.setAction(resultSet.getString("action"));
                                r.setId_thesaurus(idThesaurus);
                                listRel.add(r);
                            }

                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting date relation historique of Concept : " + idConcept, sqle);
        }
        return listRel;
    }

    /**
     * Cette fonction permet de supprimer une relation terme gÃ©nÃ©rique Ã  un
     * concept
     *
     * @param conn
     * @param idConceptNT
     * @param idThesaurus
     * @param idConceptBT
     * @param idUser
     * @return boolean
     */
    public boolean deleteRelationBT(Connection conn,
            String idConceptNT, String idThesaurus,
            String idConceptBT, int idUser) {

        Statement stmt;
        boolean status = false;

        try {

            try {
                stmt = conn.createStatement();
                try {
                    if (!new RelationsHelper().addRelationHistorique(conn, idConceptBT, idThesaurus, idConceptNT, "NT", idUser, "DEL")) {
                        return false;
                    }
                    if (!new RelationsHelper().addRelationHistorique(conn, idConceptNT, idThesaurus, idConceptBT, "BT", idUser, "DEL")) {
                        return false;
                    }
                    String query = "delete from hierarchical_relationship"
                            + " where id_concept1 ='" + idConceptNT + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and role = 'BT'"
                            + " and id_concept2 = '" + idConceptBT + "'";

                    stmt.executeUpdate(query);
                    query = "delete from hierarchical_relationship"
                            + " where id_concept1 ='" + idConceptBT + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and role = 'NT'"
                            + " and id_concept2 = '" + idConceptNT + "'";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //       conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting relation BT of Concept : " + idConceptNT, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de supprimer une relation terme associÃ© Ã  un
     * concept
     *
     * @param ds
     * @param idConcept1
     * @param idThesaurus
     * @param idConcept2
     * @param idUser
     * @return boolean
     */
    public boolean deleteRelationRT(HikariDataSource ds,
            String idConcept1, String idThesaurus,
            String idConcept2, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            try {
                stmt = conn.createStatement();
                try {

                    if (!new RelationsHelper().addRelationHistorique(conn, idConcept1, idThesaurus, idConcept2, "RT", idUser, "DEL")) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                    if (!new RelationsHelper().addRelationHistorique(conn, idConcept2, idThesaurus, idConcept1, "RT", idUser, "DEL")) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                    String query = "delete from hierarchical_relationship"
                            + " where id_concept1 ='" + idConcept1 + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and role = 'RT'"
                            + " and id_concept2 = '" + idConcept2 + "'";

                    stmt.executeUpdate(query);
                    query = "delete from hierarchical_relationship"
                            + " where id_concept1 ='" + idConcept2 + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and role = 'RT'"
                            + " and id_concept2 = '" + idConcept1 + "'";

                    stmt.executeUpdate(query);
                    status = true;
                    conn.commit();
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting relation RT of Concept : " + idConcept1, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de supprimer la relation TT d'un concept
     *
     * @param conn
     * @param idConcept
     * @param idGroup
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */
    public boolean deleteRelationTT(Connection conn,
            String idConcept, String idGroup, String idThesaurus,
            int idUser) {

        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn.setAutoCommit(false);
            try {
                stmt = conn.createStatement();
                try {

                    if (!new RelationsHelper().addRelationHistorique(conn, idConcept, idThesaurus, idConcept, "TT", idUser, "DEL")) {
                        return false;
                    }

                    String query = "UPDATE Concept set"
                            + " top_concept = false,"
                            + " modified = current_date"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus = '" + idThesaurus + "'"
                            + " AND id_group = '" + idGroup + "'";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting relation TT of Concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de supprimer la relation MT ou domaine à un concept
     *
     * @param conn
     * @param idConcept
     * @param idGroup
     * @param idThesaurus
     * @return boolean
     */
    public boolean deleteRelationMT(Connection conn,
            String idConcept, String idGroup, String idThesaurus) {

        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn.setAutoCommit(false);
            try {
                stmt = conn.createStatement();
                try {

                    /*    if (!new RelationsHelper().addRelationHistorique(conn, idConcept, idThesaurus, idConcept, "TT", idUser, "DEL")) {
                        return false;
                    }*/
                    String query = "UPDATE concept set"
                            + " id_group = '" + "" + "',"
                            + " modified = current_date"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus = '" + idThesaurus + "'"
                            + " AND id_group = '" + idGroup + "'";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting relation TT of Concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet d'ajouter une relation MT ou domaine à un concept
     *
     * @param conn
     * @param idConcept
     * @param idGroup
     * @param idThesaurus
     * @return boolean
     */
    public boolean setRelationMT(Connection conn,
            String idConcept, String idGroup, String idThesaurus) {

        Statement stmt;
        boolean status = false;
        String query;
        Savepoint savepoint = null;

        try {
            // Get connection from pool
            savepoint = conn.setSavepoint();
            try {
                stmt = conn.createStatement();
                try {

                    /*    if (!new RelationsHelper().addRelationHistorique(conn, idConcept, idThesaurus, idConcept, "TT", idUser, "DEL")) {
                        return false;
                    }*/
                    query = "UPDATE concept set"
                            + " id_group = '" + idGroup + "',"
                            + " modified = now()"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            if (sqle.getSQLState().equalsIgnoreCase("23505")) {
                try {
                    if (savepoint != null) {
                        conn.rollback(savepoint);
                        status = true;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RelationsHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                log.error("Error while adding relation MT of Concept : " + idConcept, sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet d'ajouter une relation TT à un concept
     *
     * @param conn
     * @param idConcept
     * @param idGroup
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */
    public boolean addRelationTT(Connection conn,
            String idConcept, String idGroup, String idThesaurus,
            int idUser) {

        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn.setAutoCommit(false);
            try {
                stmt = conn.createStatement();
                try {

                    if (!new RelationsHelper().addRelationHistorique(conn, idConcept, idThesaurus, idConcept, "TT", idUser, "ADD")) {
                        return false;
                    }

                    String query = "UPDATE Concept set"
                            + " top_concept = true,"
                            + " modified = current_date"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //    conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding relation TT of Concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de supprimer toutes les relations d'un concept
     *
     * @param conn
     * @param idConcept
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */
    public boolean deleteAllRelationOfConcept(Connection conn,
            String idConcept, String idThesaurus, int idUser) {

        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from hierarchical_relationship"
                            + " where id_concept1 ='" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    query = "delete from hierarchical_relationship"
                            + " where id_concept2 ='" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeUpdate(query);

                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //      conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting All relations of Concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de changer le status du concept en TopConcept ou
     * non Le Concept n'est pas supprimÃ©
     *
     * @param conn
     * @param idConcept
     * @param idThesaurus
     * @param idGroup
     * @param status
     * @param idUser
     * @return boolean
     */
    public boolean setRelationTopConcept(Connection conn,
            String idConcept, String idThesaurus, String idGroup, boolean status, int idUser) {

        Statement stmt;
        boolean resultat = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE Concept set"
                            + " top_concept = " + status + ","
                            + " modified = current_date"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus = '" + idThesaurus + "'"
                            + " AND id_group = '" + idGroup + "'";

                    stmt.executeUpdate(query);
                    resultat = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //       conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while setting relation TopConcept of Concept : " + idConcept, sqle);
        }
        return resultat;
    }

    /**
     * Cette fonction permet de rÃ©cupÃ©rer la liste des Id des termes
     * gÃ©nÃ©riques d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public ArrayList<String> getListIdBT(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> listIdBT = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "BT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        listIdBT = new ArrayList<>();
                        while (resultSet.next()) {
                            listIdBT.add(resultSet.getString("id_concept2"));
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Liste ID of BT Concept : " + idConcept, sqle);
        }
        return listIdBT;
    }

    /**
     * Cette fonction permet de récupérer les termes spécifiques d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class Concept
     */
    /*public ArrayList <NodeNT> getListNT(HikariDataSource ds,
     String idConcept, String idThesaurus, String idLang) {

     Connection conn;
     Statement stmt;
     ResultSet resultSet;
     ArrayList<NodeNT> nodeListNT = null;

     try {
     // Get connection from pool
     conn = ds.getConnection();
     try {
     stmt = conn.createStatement();
     try {
     String query = "SELECT term.lexical_value, " +
     " preferred_term.id_concept, concept.status" +
     " FROM term,preferred_term,concept,hierarchical_relationship" +
     " WHERE preferred_term.id_term = term.id_term AND" +
     " preferred_term.id_thesaurus = term.id_thesaurus AND" +
     " concept.id_concept = preferred_term.id_concept AND" +
     " concept.id_thesaurus = preferred_term.id_thesaurus AND" +
     " hierarchical_relationship.id_concept2 = concept.id_concept" +
     " and concept.id_thesaurus = '" + idThesaurus + "'" +
     " and hierarchical_relationship.role = 'NT'" +
     " and hierarchical_relationship.id_concept1 = '" + idConcept + "'" +
     " and term.lang = '" + idLang + "'" +
     " ORDER BY upper((unaccent_string(term.lexical_value))) ASC;";
                    
     stmt.executeQuery(query);
     resultSet = stmt.getResultSet();
     if (resultSet != null) {
     nodeListNT = new ArrayList<>();
     while (resultSet.next()) {
     NodeNT nodeNT = new NodeNT();
     nodeNT.setIdConcept(resultSet.getString("id_concept"));
     nodeNT.setStatus(resultSet.getString("status"));
     if(resultSet.getString("lexical_value").trim().equals(""))
     nodeNT.setTitle("");
     else
     nodeNT.setTitle(resultSet.getString("lexical_value").trim());
     nodeListNT.add(nodeNT);
     }
     }

     } finally {
     stmt.close();
     }
     } finally {
     conn.close();
     }
     } catch (SQLException sqle) {
     // Log exception
     log.error("Error while getting ListConcept of Concept : " + idConcept, sqle);
     }
     //  Collections.sort(nodeConceptTree);
     return nodeListNT;
     }*/
    public ArrayList<NodeNT> getListNT(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeNT> nodeListNT = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "NT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            NodeNT nodeNT = new NodeNT();
                            nodeNT.setIdConcept(resultSet.getString("id_concept2"));
                            nodeListNT.add(nodeNT);
                        }
                    }
                    for (NodeNT nodeNT : nodeListNT) {
                        query = "SELECT term.lexical_value, term.status FROM term, preferred_term"
                                + " WHERE preferred_term.id_term = term.id_term"
                                + " and preferred_term.id_concept ='" + nodeNT.getIdConcept() + "'"
                                + " and term.lang = '" + idLang + "'"
                                + " and term.id_thesaurus = '" + idThesaurus + "'"
                                + " order by lexical_value DESC";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            resultSet.next();
                            if (resultSet.getRow() == 0) {
                                nodeNT.setTitle("");
                                nodeNT.setStatus("");
                            } else {
                                if (resultSet.getString("lexical_value") == null || resultSet.getString("lexical_value").equals("")) {
                                    nodeNT.setTitle("");
                                } else {
                                    nodeNT.setTitle(resultSet.getString("lexical_value"));
                                }
                                if (resultSet.getString("status") == null || resultSet.getString("status").equals("")) {
                                    nodeNT.setStatus("");
                                } else {
                                    nodeNT.setStatus(resultSet.getString("status"));
                                }
                            }
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting NT of Concept : " + idConcept, sqle);
        }
        Collections.sort(nodeListNT);
        return nodeListNT;
    }

    /**
     * cette fonction est pour trier les concept NT par date chronologique
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return
     */
    public ArrayList<NodeNT> getListNTOrderByDate(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeNT> nodeListNT = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select hierarchical_relationship.id_concept2, concept.modified "
                            + " FROM concept, hierarchical_relationship"
                            + " where concept.id_thesaurus = '" + idThesaurus + "'"
                            + " and hierarchical_relationship.id_thesaurus = concept.id_thesaurus "
                            + " and concept.id_concept = hierarchical_relationship.id_concept2 "
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "NT" + "'"
                            + " order by modified DESC";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            NodeNT nodeNT = new NodeNT();
                            nodeNT.setIdConcept(resultSet.getString("id_concept2"));
                            nodeListNT.add(nodeNT);
                        }
                    }
                    for (NodeNT nodeNT : nodeListNT) {
                        query = "SELECT term.lexical_value, term.status FROM term, preferred_term"
                                + " WHERE preferred_term.id_term = term.id_term"
                                + " and preferred_term.id_concept ='" + nodeNT.getIdConcept() + "'"
                                + " and term.lang = '" + idLang + "'"
                                + " and term.id_thesaurus = '" + idThesaurus + "'";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            resultSet.next();
                            if (resultSet.getRow() == 0) {
                                nodeNT.setTitle("");
                                nodeNT.setStatus("");
                            } else {
                                if (resultSet.getString("lexical_value") == null || resultSet.getString("lexical_value").equals("")) {
                                    nodeNT.setTitle("");
                                } else {
                                    nodeNT.setTitle(resultSet.getString("lexical_value"));
                                }
                                if (resultSet.getString("status") == null || resultSet.getString("status").equals("")) {
                                    nodeNT.setStatus("");
                                } else {
                                    nodeNT.setStatus(resultSet.getString("status"));
                                }
                            }
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting NT of Concept : " + idConcept, sqle);
        }

        return nodeListNT;
    }

    /**
     * Cette fonction permet de rÃ©cupÃ©rer la liste des Ids des termes
     * spÃ©cifiques d'un concept UtilisÃ©e pour l'export des Concepts
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Array String
     */
    public ArrayList<String> getListIdsOfNT(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> nodeListIdsNT = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "NT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        nodeListIdsNT.add(resultSet.getString("id_concept2"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Ids of NT of Concept : " + idConcept, sqle);
        }
        return nodeListIdsNT;
    }

    /**
     * Cette fonction permet de savoir si le Concept a une relation NT si oui,
     * on ne le supprime pas pour Ã©viter de supprimer toute la chaine
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public boolean isRelationNTExist(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "NT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    existe = resultSet.next();
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if relation NT exist of Concept : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de rÃ©cupÃ©rer les termes associÃ©s d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class Concept
     */
    public ArrayList<NodeRT> getListRT(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeRT> nodeListRT = null;

        try {

            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2, status from hierarchical_relationship, concept"
                            + " where hierarchical_relationship.id_thesaurus = '" + idThesaurus + "'"
                            + " and hierarchical_relationship.id_concept2 = concept.id_concept"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "RT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        nodeListRT = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeRT nodeRT = new NodeRT();
                            nodeRT.setIdConcept(resultSet.getString("id_concept2"));
                            nodeRT.setStatus(resultSet.getString("status"));
                            nodeListRT.add(nodeRT);
                        }
                    }
                    for (NodeRT nodeRT : nodeListRT) {
                        query = "SELECT term.lexical_value FROM"
                                + " term, preferred_term WHERE"
                                + " term.id_term = preferred_term.id_term"
                                + " and preferred_term.id_concept = '" + nodeRT.getIdConcept() + "'"
                                + " and term.lang = '" + idLang + "'"
                                + " and term.id_thesaurus = '" + idThesaurus + "'"
                                + " order by upper(unaccent_string(term.lexical_value))";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            resultSet.next();
                            if (resultSet.getRow() == 0) {
                                nodeRT.setTitle("");

                            } else {

                                nodeRT.setTitle(resultSet.getString("lexical_value"));

                            }
                        }
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting RT of Concept : " + idConcept, sqle);
        }
        return nodeListRT;
    }

    /**
     * Cette fonction permet de rÃ©cupÃ©rer la liste des Ids des termes
     * associÃ©s d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public ArrayList<String> getListIdsOfRT(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> listIdsOfRT = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept1 = '" + idConcept + "'"
                            + " and role = '" + "RT" + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        listIdsOfRT.add(resultSet.getString("id_concept2"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting list Ids of RT of Concept : " + idConcept, sqle);
        }
        return listIdsOfRT;
    }

}
