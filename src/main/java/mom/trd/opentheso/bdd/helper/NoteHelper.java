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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.tools.StringPlus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author miled.rousset
 */
public class NoteHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public NoteHelper() {
    }

    /**
     * Cette focntion permet de retourner la liste des notes pour un concept
     * (type CustomNote, ScopeNote, HistoryNote)
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return ArrayList des notes sous forme de Class NodeNote
     */
    public ArrayList<NodeNote> getListNotesConcept(HikariDataSource ds, String idConcept,
            String idThesaurus, String idLang) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT note.id_note, note.notetypecode,"
                            + " note.lexicalvalue, note.created,"
                            + " note.modified FROM note, note_type"
                            + " WHERE note.notetypecode = note_type.code"
                            + " and note_type.isconcept = true"
                            + " and note.id_concept = '" + idConcept + "'"
                            + " and note.lang ='" + idLang + "'"
                            + " and note.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeNote nodeNote = new NodeNote();
                        nodeNote.setId_concept(idConcept);
                        nodeNote.setId_note(resultSet.getInt("id_note"));
                        nodeNote.setLang(idLang);
                        nodeNote.setLexicalvalue(resultSet.getString("lexicalvalue"));
                        nodeNote.setModified(resultSet.getDate("modified"));
                        nodeNote.setCreated(resultSet.getDate("created"));
                        nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                        nodeNotes.add(nodeNote);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Notes of Concept : " + idConcept, sqle);
        }
        return nodeNotes;
    }

    /**
     * Cette focntion permet de retourner la liste des notes pour un concept
     * (type CustomNote, ScopeNote, HistoryNote) avec toutes les langues
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return ArrayList des notes sous forme de Class NodeNote
     */
    public ArrayList<NodeNote> getListNotesConceptAllLang(HikariDataSource ds, String idConcept,
            String idThesaurus) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        StringPlus stringPlus = new StringPlus();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT note.id_note, note.notetypecode,"
                            + " note.lexicalvalue, note.created,"
                            + " note.modified, note.lang FROM note, note_type"
                            + " WHERE note.notetypecode = note_type.code"
                            + " and note_type.isconcept = true"
                            + " and note.id_concept = '" + idConcept + "'"
                            + " and note.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeNote nodeNote = new NodeNote();
                        nodeNote.setId_concept(idConcept);
                        nodeNote.setId_note(resultSet.getInt("id_note"));
                        nodeNote.setLang(resultSet.getString("lang"));
                        nodeNote.setLexicalvalue(
                            stringPlus.normalizeStringForXml(
                                resultSet.getString("lexicalvalue"))
                            );
                        nodeNote.setModified(resultSet.getDate("modified"));
                        nodeNote.setCreated(resultSet.getDate("created"));
                        nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                        nodeNotes.add(nodeNote);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All Notes of Concept : " + idConcept, sqle);
        }
        return nodeNotes;
    }

    /**
     * Cette focntion permet de retourner la liste des notes pour un Term(type
     * HistoryNote, Definition, EditotrialNote)
     *
     * @param ds
     * @param idThesaurus
     * @param idTerm
     * @param idLang
     * @return ArrayList des notes sous forme de Class NodeNote
     */
    public ArrayList<NodeNote> getListNotesTerm(HikariDataSource ds,
            String idTerm, String idThesaurus, String idLang) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT note.id_note, note.notetypecode,"
                            + " note.lexicalvalue, note.created,"
                            + " note.modified FROM note, note_type"
                            + " WHERE note.notetypecode = note_type.code"
                            + " and note_type.isterm = true"
                            + " and note.id_term = '" + idTerm + "'"
                            + " and note.lang ='" + idLang + "'"
                            + " and note.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeNote nodeNote = new NodeNote();
                        nodeNote.setId_term(idTerm);
                        nodeNote.setId_note(resultSet.getInt("id_note"));
                        nodeNote.setLang(idLang);
                        nodeNote.setLexicalvalue(resultSet.getString("lexicalvalue"));
                        nodeNote.setModified(resultSet.getDate("modified"));
                        nodeNote.setCreated(resultSet.getDate("created"));
                        nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                        nodeNotes.add(nodeNote);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Notes of Term : " + idTerm, sqle);
        }
        return nodeNotes;
    }

    /**
     * Cette focntion permet de retourner la liste des notes pour un Term(type
     * HistoryNote, Definition, EditotrialNote) dans toutes les langues
     *
     * @param ds
     * @param idThesaurus
     * @param idTerm
     * @return ArrayList des notes sous forme de Class NodeNote
     */
    public ArrayList<NodeNote> getListNotesTermAllLang(HikariDataSource ds,
            String idTerm, String idThesaurus) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        StringPlus stringPlus = new StringPlus();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT note.id_note, note.notetypecode,"
                            + " note.lexicalvalue, note.created,"
                            + " note.modified, note.lang FROM note, note_type"
                            + " WHERE note.notetypecode = note_type.code"
                            + " and note_type.isterm = true"
                            + " and note.id_term = '" + idTerm + "'"
                            + " and note.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeNote nodeNote = new NodeNote();
                        nodeNote.setId_term(idTerm);
                        nodeNote.setId_note(resultSet.getInt("id_note"));
                        nodeNote.setLang(resultSet.getString("lang"));
                        nodeNote.setLexicalvalue(
                            stringPlus.normalizeStringForXml(
                                resultSet.getString("lexicalvalue"))
                            );
                        nodeNote.setModified(resultSet.getDate("modified"));
                        nodeNote.setCreated(resultSet.getDate("created"));
                        nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                        nodeNotes.add(nodeNote);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All Notes of Term : " + idTerm, sqle);
        }
        return nodeNotes;
    }

    /**
     * Cette fonction permet d'ajouter une Note Ã  un concept instert dans la
     * table Note
     *
     * @param ds
     * @param idConcept
     * @param idLang
     * @param idThesaurus
     * @param note
     * @param noteTypeCode
     * @param idUser
     * @return
     */
    public boolean addConceptNote(HikariDataSource ds,
            String idConcept, String idLang, String idThesaurus,
            String note, String noteTypeCode, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        note = new StringPlus().convertString(note);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into note "
                            + "(notetypecode, id_thesaurus, id_concept, lang, lexicalvalue)"
                            + " values ("
                            + "'" + noteTypeCode + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'" + idConcept + "'"
                            + ",'" + idLang + "'"
                            + ",'" + note + "')";
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
            log.error("Error while adding Note of Concept : " + idConcept, sqle);
        }
        addConceptNoteHistorique(ds, idConcept, idLang, idThesaurus, note, noteTypeCode, idUser);
        return status;
    }

    /**
     * Cette fonction permet d'ajouter l'historique de l'ajout d'une Note Ã  un
     * term insert dans la table Note_hisorique
     *
     * @param ds
     * @param idTerme
     * @param idLang
     * @param idThesausus
     * @param note
     * @param noteTypeCode
     * @param idUser
     * @return
     */
    public boolean addTermNoteHistorique(HikariDataSource ds,
            String idTerme, String idLang, String idThesausus,
            String note, String noteTypeCode, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        note = new StringPlus().convertString(note);

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into note_historique "
                            + "(notetypecode, id_thesaurus, id_term, lang, lexicalvalue, id_user)"
                            + " values ("
                            + "'" + noteTypeCode + "'"
                            + ",'" + idThesausus + "'"
                            + ",'" + idTerme + "'"
                            + ",'" + idLang + "'"
                            + ",'" + note + "'"
                            + ",'" + idUser + "')";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            log.error("Error while adding Note historique of term : " + idTerme, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet d'ajouter une Note Ã  un concept instert dans la
     * table Note
     *
     * @param ds
     * @param idConcept
     * @param idLang
     * @param idThesausus
     * @param note
     * @param noteTypeCode
     * @param idUser
     * @return
     */
    private boolean addConceptNoteRollback(HikariDataSource ds,
            String idConcept, String idLang, String idThesausus,
            String note, String noteTypeCode, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        note = new StringPlus().convertString(note);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into note "
                            + "(notetypecode, id_thesaurus, id_concept, lang, lexicalvalue)"
                            + " values ("
                            + "'" + noteTypeCode + "'"
                            + ",'" + idThesausus + "'"
                            + ",'" + idConcept + "'"
                            + ",'" + idLang + "'"
                            + ",'" + note + "')";
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
            log.error("Error while adding Note of Concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet d'ajouter l'historique de l'ajout d'une Note Ã  un
     * concept insert dans la table Note_hisorique
     *
     * @param ds
     * @param idConcept
     * @param idLang
     * @param idThesausus
     * @param note
     * @param noteTypeCode
     * @param idUser
     * @return
     */
    public boolean addConceptNoteHistorique(HikariDataSource ds,
            String idConcept, String idLang, String idThesausus,
            String note, String noteTypeCode, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        note = new StringPlus().convertString(note);

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into note_historique "
                            + "(notetypecode, id_thesaurus, id_concept, lang, lexicalvalue, id_user)"
                            + " values ("
                            + "'" + noteTypeCode + "'"
                            + ",'" + idThesausus + "'"
                            + ",'" + idConcept + "'"
                            + ",'" + idLang + "'"
                            + ",'" + note + "'"
                            + ",'" + idUser + "')";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            log.error("Error while adding Note historique of concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette focntion permet de retourner la liste de l'historique des notes
     * pour un concept (type CustomNote, ScopeNote, HistoryNote)
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idTerm
     * @param idLang
     * @return ArrayList des notes sous forme de Class NodeNote
     */
    public ArrayList<NodeNote> getNoteHistoriqueAll(HikariDataSource ds, String idConcept,
            String idThesaurus, String idTerm, String idLang) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_note, notetypecode, lexicalvalue, modified, username FROM note_historique, users"
                            + " WHERE id_thesaurus = '" + idThesaurus + "'"
                            + " and lang ='" + idLang + "'"
                            + " and (id_concept = '" + idConcept + "' OR id_term = '" + idTerm + "' )"
                            + " and note_historique.id_user=users.id_user"
                            + " order by modified DESC";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeNote nodeNote = new NodeNote();
                        nodeNote.setId_concept(idConcept);
                        nodeNote.setId_term(idTerm);
                        nodeNote.setId_note(resultSet.getInt("id_note"));
                        nodeNote.setLang(idLang);
                        nodeNote.setLexicalvalue(resultSet.getString("lexicalvalue"));
                        nodeNote.setModified(resultSet.getDate("modified"));
                        nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                        nodeNote.setIdUser(resultSet.getString("username"));
                        nodeNotes.add(nodeNote);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting all historique Notes of Concept : " + idConcept, sqle);
        }
        return nodeNotes;
    }

    /**
     * Cette focntion permet de retourner la liste de l'historique des notes
     * pour un concept (type CustomNote, ScopeNote, HistoryNote) à une date
     * précise
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idTerm
     * @param idLang
     * @param date
     * @return ArrayList des notes sous forme de Class NodeNote
     */
    public ArrayList<NodeNote> getNoteHistoriqueFromDate(HikariDataSource ds, String idConcept,
            String idThesaurus, String idTerm, String idLang, Date date) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_note, notetypecode, lexicalvalue, modified, username FROM note_historique, users"
                            + " WHERE id_thesaurus = '" + idThesaurus + "'"
                            + " and lang ='" + idLang + "'"
                            + " and (id_concept = '" + idConcept + "' OR id_term = '" + idTerm + "' )"
                            + " and note_historique.id_user=users.id_user"
                            + " and modified <= '" + date.toString()
                            + "' order by modified DESC";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        boolean exist = false;
                        for (NodeNote nn : nodeNotes) {
                            if (nn.getNotetypecode().equals(resultSet.getString("notetypecode"))) {
                                if (nn.getModified().before(resultSet.getDate("modified"))) {
                                    NodeNote nodeNote = new NodeNote();
                                    nodeNote.setId_concept(idConcept);
                                    nodeNote.setId_term(idTerm);
                                    nodeNote.setId_note(resultSet.getInt("id_note"));
                                    nodeNote.setLang(idLang);
                                    nodeNote.setLexicalvalue(resultSet.getString("lexicalvalue"));
                                    nodeNote.setModified(resultSet.getDate("modified"));
                                    nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                                    nodeNote.setIdUser(resultSet.getString("username"));
                                    nodeNotes.add(nodeNote);
                                }
                                exist = true;
                            }
                        }
                        if (!exist) {
                            NodeNote nodeNote = new NodeNote();
                            nodeNote.setId_concept(idConcept);
                            nodeNote.setId_term(idTerm);
                            nodeNote.setId_note(resultSet.getInt("id_note"));
                            nodeNote.setLang(idLang);
                            nodeNote.setLexicalvalue(resultSet.getString("lexicalvalue"));
                            nodeNote.setModified(resultSet.getDate("modified"));
                            nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                            nodeNote.setIdUser(resultSet.getString("username"));
                            nodeNotes.add(nodeNote);
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
            log.error("Error while getting date historique Notes of Concept : " + idConcept, sqle);
        }
        return nodeNotes;
    }

    /**
     * Cette fonction permet d'ajouter une Note Ã  un Term instert dans la table
     * Note
     *
     * @param ds
     * @param idTerm
     * @param idLang
     * @param idThesaurus
     * @param note
     * @param noteTypeCode
     * @param idUser
     * @return
     */
    public boolean addTermNote(HikariDataSource ds,
            String idTerm, String idLang, String idThesaurus,
            String note, String noteTypeCode, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        note = new StringPlus().convertString(note);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into note "
                            + "(notetypecode, id_thesaurus, id_term, lang, lexicalvalue)"
                            + " values ("
                            + "'" + noteTypeCode + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'" + idTerm + "'"
                            + ",'" + idLang + "'"
                            + ",'" + note + "')";
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
            log.error("Error while adding Note of Term : " + idTerm, sqle);
        }
        addTermNoteHistorique(ds, idTerm, idLang, idThesaurus, note, noteTypeCode, idUser);
        return status;
    }

    /**
     * Cette fonction permet de mettre Ã  jour une note de Concept
     *
     * @param ds
     * @param idConcept
     * @param idLang
     * @param idThesaurus
     * @param note
     * @param noteTypeCode
     * @param idUser
     * @return
     */
    public boolean updateConceptNote(HikariDataSource ds,
            String idConcept, String idLang,
            String idThesaurus, String note, String noteTypeCode,
            int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        note = new StringPlus().convertString(note);
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE note set"
                            + " lexicalvalue = '" + note + "',"
                            + " modified = current_date"
                            + " WHERE lang ='" + idLang + "'"
                            + " AND id_thesaurus = '" + idThesaurus + "'"
                            + " AND id_concept = '" + idConcept + "'"
                            + " AND notetypecode = '" + noteTypeCode + "'";
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
            log.error("Error while updating Note of Concept : " + idConcept, sqle);
        }
        addConceptNoteHistorique(ds, idConcept, idLang, idThesaurus, note, noteTypeCode, idUser);
        return status;
    }

    /**
     * Cette fonction permet de mettre Ã  jour une note de Concept
     *
     * @param ds
     * @param idTerm
     * @param idLang
     * @param idThesausus
     * @param note
     * @param noteTypeCode
     * @param idUser
     * @return
     */
    public boolean updateTermNote(HikariDataSource ds,
            String idTerm, String idLang,
            String idThesausus, String note, String noteTypeCode,
            int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        note = new StringPlus().convertString(note);
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE note set"
                            + " lexicalvalue = '" + note + "',"
                            + " modified = current_date"
                            + " WHERE lang ='" + idLang + "'"
                            + " AND id_thesaurus = '" + idThesausus + "'"
                            + " AND id_term = '" + idTerm + "'"
                            + " AND notetypecode = '" + noteTypeCode + "'";
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
            log.error("Error while updating Note of Term : " + idTerm, sqle);
        }
        addTermNoteHistorique(ds, idTerm, idLang, idThesausus, note, noteTypeCode, idUser);
        return status;
    }

    /**
     * Cette fonction permet de savoir si la Note d'un Concept existe ou non
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @param noteTypeCode
     * @return boolean
     */
    public boolean isNoteExistOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang,
            String noteTypeCode) {

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
                    String query = "select id_note from note"
                            + " where id_concept = '" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and lang ='" + idLang + "'"
                            + " and noteTypeCode = '" + noteTypeCode + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if Note of Concept exist : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de savoir si la Note d'un Concept existe ou non
     *
     * @param ds
     * @param idTerm
     * @param idThesaurus
     * @param idLang
     * @param noteTypeCode
     * @return boolean
     */
    public boolean isNoteExistOfTerm(HikariDataSource ds,
            String idTerm, String idThesaurus, String idLang,
            String noteTypeCode) {

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
                    String query = "select id_note from note"
                            + " where id_term = '" + idTerm + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and lang ='" + idLang + "'"
                            + " and noteTypeCode = '" + noteTypeCode + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if Note of Term exist : " + idTerm, sqle);
        }
        return existe;
    }

    
    /**
     * Cette fonction permet de supprimer toutes les notes d'un Concept
     * 
     * @param conn
     * @param idConcept
     * @param idThesaurus
     * @return boolean
     */
    public boolean deleteNotesOfConcept(Connection conn,
            String idConcept, String idThesaurus) {

        Statement stmt;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from note"
                            + " where id_concept = '" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                    return true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting all notes of Concept : " + idConcept, sqle);
        }
        return false;
    }
    
    /**
     * Cette fonction permet de supprimer toutes les notes d'un terme
     * 
     * @param conn
     * @param idTerm
     * @param idThesaurus
     * @return boolean
     */
    public boolean deleteNotesOfTerm(Connection conn,
            String idTerm, String idThesaurus) {

        Statement stmt;
        try {
            // Get connection from pool
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from note"
                            + " where id_term = '" + idTerm + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                    return true;
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting all notes of Term : " + idTerm, sqle);
        }
        return false;
    }  
    
   /**
     * Cette fonction permet de supprimer une note suivant un IdTerme et un type de note
     * 
     * @param ds
     * @param idTerm
     * @param idThesaurus
     * @param idLang
     * @param noteTypeCode
     * @return boolean
     */
    public boolean deleteThisNoteOfTerm(HikariDataSource ds,
            String idTerm, String idThesaurus, String idLang, String noteTypeCode) {
        Connection conn;
        Statement stmt;
        boolean status = false;
        
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {   
                    String query = "delete from note"
                            + " where id_term = '" + idTerm + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and lang = '"+ idLang + "'"
                            + " and notetypecode = '" + noteTypeCode + "'";
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
                log.error("Error while deleting note of term : " + idTerm, sqle);
            }
        return status;
    }
    
   /**
     * Cette fonction permet de supprimer une note suivant un IdConcept et un type de note
     * 
     * @param ds
     * @param idConcept
     * @param idLange
     * @param noteTypeCode
     * @param idThesaurus
     * @return boolean
     */
    public boolean deletethisNoteOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang, String noteTypeCode) {
        
        Connection conn;
        Statement stmt;
        boolean status = false;
        
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {   
                    String query = "delete from note"
                            + " where id_concept = '" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and lang = '"+ idLang + "'"
                            + " and notetypecode = '" + noteTypeCode + "'";
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
                log.error("Error while deleting note of concept : " + idConcept, sqle);
            }
        return status;
    }
    /**
     * pour pouvoir obtener une list des Notes a partir du idTerm
     * sans conter avec le language
     * @param ds
     * @param idTerm
     * @param idThesaurus
     * @return 
     */
    public ArrayList<NodeNote> getListNotesTerm2(HikariDataSource ds,
            String idTerm, String idThesaurus) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT note.id_note, note.lang, note.notetypecode,"
                            + " note.lexicalvalue, note.created,"
                            + " note.modified FROM note, note_type"
                            + " WHERE note.notetypecode = note_type.code"
                            + " and note_type.isterm = true"
                            + " and note.id_term = '" + idTerm + "'"
                            + " and note.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeNote nodeNote = new NodeNote();
                        nodeNote.setId_term(idTerm);
                        nodeNote.setId_note(resultSet.getInt("id_note"));
                        nodeNote.setLexicalvalue(resultSet.getString("lexicalvalue"));
                        nodeNote.setModified(resultSet.getDate("modified"));
                        nodeNote.setCreated(resultSet.getDate("created"));
                        nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                        nodeNote.setLang(resultSet.getString("lang"));
                        nodeNotes.add(nodeNote);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Notes of Term : " + idTerm, sqle);
        }
        return nodeNotes;
    }
      /**
     * pour pouvoir obtener une list des Notes a partir du idConcept
     * sans conter avec le language
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return 
     */
    public ArrayList<NodeNote> getListNotesConcept2(HikariDataSource ds, String idConcept,
            String idThesaurus) {

        ArrayList<NodeNote> nodeNotes = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT note.id_note, note.lang, note.notetypecode,"
                            + " note.lexicalvalue, note.created,"
                            + " note.modified FROM note, note_type"
                            + " WHERE note.notetypecode = note_type.code"
                            + " and note_type.isconcept = true"
                            + " and note.id_concept = '" + idConcept + "'"
                            + " and note.id_thesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeNote nodeNote = new NodeNote();
                        nodeNote.setId_concept(idConcept);
                        nodeNote.setId_note(resultSet.getInt("id_note"));
                        nodeNote.setLexicalvalue(resultSet.getString("lexicalvalue"));
                        nodeNote.setModified(resultSet.getDate("modified"));
                        nodeNote.setCreated(resultSet.getDate("created"));
                        nodeNote.setNotetypecode(resultSet.getString("notetypecode"));
                        nodeNote.setLang(resultSet.getString("lang"));
                        nodeNotes.add(nodeNote);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Notes of Concept : " + idConcept, sqle);
        }
        return nodeNotes;
    }

}

