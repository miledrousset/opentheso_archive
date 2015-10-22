package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatConcept;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StatisticHelper {
    
    private final Log log = LogFactory.getLog(ThesaurusHelper.class);
    
    public StatisticHelper() {
        
    }
    
    public int getNbCpt(HikariDataSource ds, String idThesaurus) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        int count = 0;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT count(id_concept) FROM concept WHERE"
                            + " id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of concept of thesaurus : " + idThesaurus, sqle);
        }
        return count;
    }
    
    public int getNbDescOfGroup(HikariDataSource ds, String idThesaurus, String idGroup) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        int count = 0;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT count(id_concept) FROM concept WHERE"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_group = '" + idGroup + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of decriptor of group : " + idGroup, sqle);
        }
        return count;
    }
    
    public int getNbNonDescOfGroup(HikariDataSource ds, String idThesaurus, String idGroup, String langue) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        int count = 0;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT count(non_preferred_term.id_term) FROM concept, preferred_term, non_preferred_term WHERE"
                            + " concept.id_concept=preferred_term.id_concept"
                            + " and preferred_term.id_term=non_preferred_term.id_term"
                            + " and concept.id_thesaurus = '" + idThesaurus + "'"
                            + " and concept.id_group = '" + idGroup + "'"
                            + " and non_preferred_term.lang = '" + langue + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of non-decriptor of group : " + idGroup, sqle);
        }
        return count;
    }
    
    public int getNbTradOfGroup(HikariDataSource ds, String idThesaurus, String idGroup, String langue) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        int count = 0;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT count(DISTINCT term.id_term) FROM concept, preferred_term, term WHERE"
                            + " concept.id_concept = preferred_term.id_concept"
                            + " and preferred_term.id_term = term.id_term"
                            + " and concept.id_thesaurus = '" + idThesaurus + "'"
                            + " and concept.id_group = '" + idGroup + "'"
                            + " and term.lang = '" + langue + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of traduction of group : " + idGroup, sqle);
        }
        return count;
    }
    
    public int getNbDefinitionNoteOfGroup(HikariDataSource ds, String idThesaurus, String langue, String idGroup) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        int count = 0;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT count(DISTINCT note.id_note) FROM concept, note WHERE"
                            + " concept.id_concept = note.id_concept"
                            + " and concept.id_thesaurus = '" + idThesaurus + "'"
                            + " and concept.id_group = '" + idGroup + "'"
                            + " and note.lang = '" + langue + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting count of definition note of group : " + idGroup, sqle);
        }
        return count;
    }
    
    public ArrayList<NodeStatConcept> getStatConceptCreat(HikariDataSource ds, String begin, String end, String idThesaurus, String langue) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeStatConcept> list = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Select DISTINCT concept.id_concept, concept.created, concept.modified, concept.id_group, term.lexical_value "
                            + "FROM concept, preferred_term, term"
                            + " WHERE concept.id_concept= preferred_term.id_concept"
                            + " AND  preferred_term.id_term=term.id_term"
                            + " AND concept.id_thesaurus = '" + idThesaurus + "'"
                            + " AND concept.created <= '" + end + "'"
                            + " AND concept.created >= '" + begin + "'"
                            + " AND term.lang = '" + langue.trim() + "'"
                            + " LIMIT 100";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeStatConcept nsc = new NodeStatConcept();
                        nsc.setDateCreat(resultSet.getDate("created"));
                        nsc.setDateEdit(resultSet.getDate("modified"));
                        String temp = new GroupHelper().getThisConceptGroup(ds, resultSet.getString("id_group"), idThesaurus, langue).getLexicalValue();
                        nsc.setGroup(temp + "(" + resultSet.getString("id_group") + ")");
                        nsc.setIdConcept(resultSet.getString("id_concept"));
                        nsc.setValue(resultSet.getString("lexical_value"));
                        list.add(nsc);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List statistic of Concept in thesaurus : " + idThesaurus, sqle);
        }
        return list;
    }
    
    public ArrayList<NodeStatConcept> getStatConceptEdit(HikariDataSource ds, String begin, String end, String idThesaurus, String langue) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeStatConcept> list = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Select DISTINCT concept.id_concept, concept.created, concept.modified, concept.id_group, term.lexical_value "
                            + "FROM concept, preferred_term, term"
                            + " WHERE concept.id_concept= preferred_term.id_concept"
                            + " AND  preferred_term.id_term=term.id_term"
                            + " AND concept.id_thesaurus = '" + idThesaurus + "'"
                            + " AND concept.modified <= '" + end + "'"
                            + " AND concept.modified >= '" + begin + "'"
                            + " AND term.lang = '" + langue.trim() + "'"
                            + " LIMIT 100";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeStatConcept nsc = new NodeStatConcept();
                        nsc.setDateCreat(resultSet.getDate("created"));
                        nsc.setDateEdit(resultSet.getDate("modified"));
                        String temp = new GroupHelper().getThisConceptGroup(ds, resultSet.getString("id_group"), idThesaurus, langue).getLexicalValue();
                        nsc.setGroup(temp + "(" + resultSet.getString("id_group") + ")");
                        nsc.setIdConcept(resultSet.getString("id_concept"));
                        nsc.setValue(resultSet.getString("lexical_value"));
                        list.add(nsc);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List statistic of Concept in thesaurus : " + idThesaurus, sqle);
        }
        return list;
    }
}
