/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import fr.mom.arkeo.soap.DcElement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.HierarchicalRelationship;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeBT;
import mom.trd.opentheso.bdd.helper.nodes.NodeFusion;
import mom.trd.opentheso.bdd.helper.nodes.NodeMetaData;
import mom.trd.opentheso.bdd.helper.nodes.NodeTT;
import mom.trd.opentheso.bdd.helper.nodes.NodeUri;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConcept;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;
import mom.trd.opentheso.bdd.tools.FileUtilities;
import mom.trd.opentheso.ws.ark.Ark_Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author miled.rousset
 */
public class ConceptHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    // 1=numericId ; 2=alphaNumericId
    private String identifierType = "1";

    public ConceptHelper() {
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    /**
     * ************************************************************
     * /**************************************************************
     * Nouvelles fonctions stables auteur Miled Rousset
     * /**************************************************************
     * /*************************************************************
     */
    /**
     * Cette fonction permet de retrouver tous tes identifiants d'une branche en
     * partant du concetp en paramètre
     *
     * @param hd
     * @param idConceptDeTete
     * @param idGroup
     * @param idTheso
     * @param lisIds
     * @return
     */
    public ArrayList<String> getIdsOfBranch(HikariDataSource hd,
            String idConceptDeTete,
            String idTheso,
            ArrayList<String> lisIds) {

        lisIds.add(idConceptDeTete);

        ArrayList<String> listIdsOfConceptChildren
                = getListChildrenOfConcept(hd, idConceptDeTete, idTheso);
        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
            getIdsOfBranch(hd, listIdsOfConceptChildren1,
                    idTheso, lisIds);
        }
        return lisIds;
    }
/*
        public ArrayList<String> getIdsOfBranchParLot(HikariDataSource hd,
            String idConceptDeTete,
            String idTheso,
            ArrayList<String> lisIds, int id_alignement_source, ArrayList<String> tmp) {

        lisIds.add(idConceptDeTete);

        ArrayList<String> listIdsOfConceptChildren
                = getListChildrenOfConceptNotExist(hd, idConceptDeTete, idTheso,id_alignement_source);
        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
            getIdsOfBranchParLot(hd, listIdsOfConceptChildren1,
                    idTheso, lisIds, id_alignement_source,tmp);
        }
        return lisIds;
    }*/
    /**
     * ************************************************************
     * /**************************************************************
     * Fin des nouvelles fonctions stables auteur Miled Rousset
     * /**************************************************************
     * /*************************************************************
     */
    /**
     * Cette fonction permet d'ajouter un Top Concept avec le libellé et les
     * relations Si l'opération échoue, on rollback les modifications
     *
     * @param ds
     * @param idParent
     * @param concept
     * @param term
     * @param urlSite
     * @param isArkActive
     * @param idUser
     * @return null si le term existe ou si erreur, sinon le numero de Concept
     */
    public String addTopConcept(HikariDataSource ds,
            String idParent,
            Concept concept, Term term,
            String urlSite, boolean isArkActive, int idUser) {

        Connection conn = null;

        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            TermHelper termHelper = new TermHelper();
            // controle si le term existe avant de rajouter un concept
            if (termHelper.isTermExist(ds, term.getLexical_value(),
                    term.getId_thesaurus(), term.getLang())) {
                conn.close();
                return null;
            }

            concept.setTopConcept(true);
            String idConcept = addConceptInTable(conn, concept, idUser);
            if (idConcept == null) {
                conn.rollback();
                conn.close();
                return null;
            }

            String idTerm = termHelper.addTerm(conn, term, idConcept, idUser);
            if (idTerm == null) {
                conn.rollback();
                conn.close();
                return null;
            }
            term.setId_term(idTerm);

            // cette fonction permet de remplir la table Permutée
            termHelper.splitConceptForPermute(ds, idConcept,
                    getGroupIdOfConcept(ds, idConcept, term.getId_thesaurus()),
                    term.getId_thesaurus(),
                    term.getLang(),
                    term.getLexical_value());

            // Si on arrive ici, c'est que tout va bien 
            // alors c'est le moment de récupérer le code ARK
            if (isArkActive) {
                NodeMetaData nodeMetaData = new NodeMetaData();
                nodeMetaData.setCreator(term.getSource());
                nodeMetaData.setTitle(term.getLexical_value());
                nodeMetaData.setDcElementsList(new ArrayList<DcElement>());

                if (!addIdArk(conn, idConcept, concept.getIdThesaurus(),
                        urlSite, nodeMetaData, idUser)) {
                    conn.rollback();
                    conn.close();
                    return null;
                }
            }

            conn.commit();
            conn.close();
            return idConcept;

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex1) {
            }
        }
        return null;
    }

    /**
     * Cette fonction permet d'ajouter une traduction à un TopConcept
     *
     * @param ds
     * @param term
     * @param idUser
     * @return null si le term existe ou si erreur, sinon le numero de Concept
     */
    public boolean addTopConceptTraduction(
            HikariDataSource ds, Term term, int idUser) {

        TermHelper termHelper = new TermHelper();
        // controle si le term existe avant de rajouter un concept
        if (termHelper.isTermExist(ds, term.getLexical_value(),
                term.getId_thesaurus(), term.getLang())) {
            return false;
        }
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            if (!termHelper.addTermTraduction(conn, term, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();

            // cette fonction permet de remplir la table Permutée
            termHelper.splitConceptForPermute(ds, term.getId_concept(),
                    getGroupIdOfConcept(ds, term.getId_concept(), term.getId_thesaurus()),
                    term.getId_thesaurus(),
                    term.getLang(),
                    term.getLexical_value());

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex1) {
            }
        }
        return false;
    }

    /**
     * Cette fonction permet d'ajouter un Concept complet à la base avec le
     * libellé et les relations Si l'opération échoue, elle envoi un NULL et ne
     * modifie pas la base de données
     *
     * @param ds
     * @param idParent
     * @param concept
     * @param term
     * @param urlSite
     * @param isArkActive
     * @param idUser
     * @return null si le term existe ou si erreur, sinon le numero de Concept
     */
    public String addConcept(HikariDataSource ds,
            String idParent,
            Concept concept, Term term,
            String urlSite, boolean isArkActive, int idUser) {

        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            TermHelper termHelper = new TermHelper();
            // controle si le term existe avant de rajouter un concept
            /*if (termHelper.isTermExist(ds, term.getLexical_value(),
             term.getId_thesaurus(), term.getLang())) {
             conn.close();
             return null;
             }*/
            concept.setTopConcept(false);

            String idConcept = addConceptInTable(conn, concept, idUser);
            if (idConcept == null) {
                conn.rollback();
                conn.close();
                return null;
            }

            String idTerm = termHelper.addTerm(conn, term, idConcept, idUser);
            if (idTerm == null) {
                conn.rollback();
                conn.close();
                return null;
            }
            term.setId_term(idTerm);

            /**
             * ajouter le lien hiérarchique
             */
            HierarchicalRelationship hierarchicalRelationship = new HierarchicalRelationship();
            hierarchicalRelationship.setIdConcept1(idParent);
            hierarchicalRelationship.setIdConcept2(idConcept);
            hierarchicalRelationship.setIdThesaurus(concept.getIdThesaurus());
            hierarchicalRelationship.setRole("NT");

            if (!addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser)) {
                conn.rollback();
                conn.close();
                return null;
            }

            hierarchicalRelationship.setIdConcept1(idConcept);
            hierarchicalRelationship.setIdConcept2(idParent);
            hierarchicalRelationship.setIdThesaurus(concept.getIdThesaurus());
            hierarchicalRelationship.setRole("BT");

            if (!addLinkHierarchicalRelation(conn, hierarchicalRelationship, idUser)) {
                conn.rollback();
                conn.close();
                return null;
            }

            // cette fonction permet de remplir la table Permutée
            termHelper.splitConceptForPermute(ds, idConcept,
                    getGroupIdOfConcept(ds, idConcept, term.getId_thesaurus()),
                    term.getId_thesaurus(),
                    term.getLang(),
                    term.getLexical_value());

            // Si on arrive ici, c'est que tout va bien 
            // alors c'est le moment de récupérer le code ARK
            if (isArkActive) {
                NodeMetaData nodeMetaData = new NodeMetaData();
                nodeMetaData.setCreator(term.getSource());
                nodeMetaData.setTitle(term.getLexical_value());
                nodeMetaData.setDcElementsList(new ArrayList<DcElement>());

                if (!addIdArk(conn, idConcept, concept.getIdThesaurus(),
                        urlSite, nodeMetaData, idUser)) {
                    conn.rollback();
                    conn.close();
                    return null;
                }
            }

            conn.commit();
            conn.close();
            return idConcept;

        } catch (SQLException ex) {
            try {
                Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex1) {
            }
        }
        return null;
    }

    /**
     * Cette fonction permet de fusionner deux concepts. Le premier concept
     * reste, le second passe en état 'fusionné'.
     *
     * @param ds
     * @param idConcept1
     * @param idConcept2
     * @param idTheso
     * @param idUser
     * @return
     */
    public boolean addConceptFusion(HikariDataSource ds,
            String idConcept1, String idConcept2, String idTheso, int idUser) {
        boolean status = false;
        String idArk = "";
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            Concept concept = getThisConcept(ds, idConcept2, idTheso);
            concept.setStatus("hidden");

            if (!addConceptHistorique(conn, concept, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            } else if (!updateStatusConcept(ds, idConcept2, idTheso, "hidden")) {
                conn.rollback();
                conn.close();
                return false;
            }
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into concept_fusion "
                            + "(id_concept1, id_concept2, id_thesaurus, id_user)"
                            + " values ("
                            + "'" + idConcept1 + "'"
                            + ",'" + idConcept2 + "'"
                            + ",'" + idTheso + "'"
                            + ",'" + idUser + "')";
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
            log.error("Error while melting Concept : " + idConcept1 + " and " + idConcept2, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de désactiver un concept (hidden)
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @param idUser
     * @return
     */
    public boolean desactiveConcept(HikariDataSource ds, String idConcept,
            String idTheso, int idUser) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            try {
                stmt = conn.createStatement();
                try {
                    Concept concept = getThisConcept(ds, idConcept, idTheso);
                    concept.setStatus("hidden");

                    if (!addConceptHistorique(conn, concept, idUser)) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                    String query = "UPDATE concept "
                            + "set status='hidden'"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idTheso + "'";

                    stmt.executeUpdate(query);
                    conn.commit();
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error during desactivation of Concept : " + idConcept, sqle);
            return false;
        }
        return true;
    }

    /**
     * Cette fonction permet de réactiver un concept (!hidden)
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @param idUser
     * @return
     */
    public boolean reactiveConcept(HikariDataSource ds, String idConcept,
            String idTheso, int idUser) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    Concept concept = getThisConcept(ds, idConcept, idTheso);
                    concept.setStatus("D");

                    if (!addConceptHistorique(conn, concept, idUser)) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                    String query = "UPDATE concept "
                            + "set status='D'"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idTheso + "'";

                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error during reactivation of Concept : " + idConcept, sqle);
            return false;
        }
        return true;
    }

    /**
     * Cette fonction permet de supprimer un Concept avec ses relations et
     * traductions
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */
    public boolean deleteConcept(HikariDataSource ds,
            String idConcept, String idThesaurus, int idUser) {

        TermHelper termHelper = new TermHelper();
        RelationsHelper relationsHelper = new RelationsHelper();
        NoteHelper noteHelper = new NoteHelper();
        AlignmentHelper alignmentHelper = new AlignmentHelper();

        // controle si le Concept a des fils avant de le supprimer
        if (relationsHelper.isRelationNTExist(ds, idConcept, idThesaurus)) {
            return false;
        }

        String idTerm = new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus);
        if (idTerm == null) {
            return false;
        }

        // suppression du term avec les traductions et les synonymes
        // gestion du Rollback en cas d'erreur
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            if (!termHelper.deleteTerm(conn, idTerm, idThesaurus, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }

            if (!relationsHelper.deleteAllRelationOfConcept(conn, idConcept, idThesaurus, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }

            if (!noteHelper.deleteNotesOfConcept(conn, idConcept, idThesaurus)) {
                conn.rollback();
                conn.close();
                return false;
            }

            if (!noteHelper.deleteNotesOfTerm(conn, idTerm, idThesaurus)) {
                conn.rollback();
                conn.close();
                return false;
            }

            if (!alignmentHelper.deleteAlignmentOfConcept(conn, idConcept, idThesaurus)) {
                conn.rollback();
                conn.close();
                return false;
            }

            if (!deleteConceptFromTable(conn, idConcept, idThesaurus, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            return false;
        }
    }

    /**
     * Cette fonction permet de supprimer un Concept avec ses relations et
     * traductions, notes, alignements, ... pas de controle s'il a des fils,
     * c'est une suppression définitive
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */
    public boolean deleteConceptForced(HikariDataSource ds,
            String idConcept, String idThesaurus, int idUser) {

        TermHelper termHelper = new TermHelper();
        RelationsHelper relationsHelper = new RelationsHelper();

        String idTerm = new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus);
        if (idTerm == null) {
            /// c'est à dire que le concept n'a aucune traduction (cas de concept corrompu)
            //       return false;
        }

        // suppression du term avec les traductions et les synonymes
        // gestion du Rollback en cas d'erreur
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            if (!termHelper.deleteTerm(conn, idTerm, idThesaurus, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }

            if (!relationsHelper.deleteAllRelationOfConcept(conn, idConcept, idThesaurus, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }

            if (!deleteConceptFromTable(conn, idConcept, idThesaurus, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            return false;
        }
    }

    public boolean deleteGroupOfConcept(HikariDataSource ds,
            String idConcept, String idGroup, String idThesaurus, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from concept where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'"
                            + " and id_group ='" + idGroup + "'";
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
            log.error("Error while deleting Concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de supprimer le concept par ID de la table Concept
     *
     * @param conn
     * @param idConcept
     * @param idThesaurus
     * @param idUser
     * @return boolean
     */
    public boolean deleteConceptFromTable(Connection conn,
            String idConcept, String idThesaurus, int idUser) {

        Statement stmt;
        boolean status = false;
        String idterm = "";
        ResultSet resulset;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from concept where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'";
                    stmt.executeUpdate(query);

                    query = "delete from permuted where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'";
                    stmt.executeUpdate(query);

                    query = "select id_term from preferred_term where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'";
                    resulset = stmt.executeQuery(query);
                    while (resulset.next()) {
                        idterm = resulset.getString(1);
                    }
                    query = "delete from preferred_term where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'"
                            + " and id_term = '" + idterm + "'";
                    stmt.executeUpdate(query);

                    query = "delete from term where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_term ='" + idterm + "'";
                    stmt.executeUpdate(query);

                    bushenfants(conn, idConcept, idThesaurus);

                    query = "delete from hierarchical_relationship where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept1 ='" + idConcept + "'";
                    stmt.executeUpdate(query);

                    query = "delete from images where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'";
                    stmt.executeUpdate(query);

                    query = "delete from note where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'";
                    stmt.executeUpdate(query);

                    query = "delete from note where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_term ='" + idterm + "'";
                    stmt.executeUpdate(query);

                    query = "delete from hierarchical_relationship where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept2 ='" + idConcept + "'";
                    stmt.executeUpdate(query);

                    query = "delete from concept_orphan where"
                            + " id_thesaurus ='" + idThesaurus + "'"
                            + " and id_concept ='" + idConcept + "'";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                //     conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting Concept : " + idConcept, sqle);
        }
        return status;
    }

    private void bushenfants(Connection conn, String idConcept, String idTheso) {
        Statement stmt;
        ArrayList<String> conceptabush = new ArrayList<>();
        ResultSet resulset;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_thesaurus ='" + idTheso
                            + "' and id_concept1 ='" + idConcept
                            + "' and role ='NT'";
                    resulset = stmt.executeQuery(query);
                    while (resulset.next()) {
                        conceptabush.add(resulset.getString(1));
                    }
                    for (int i = 0; i < conceptabush.size(); i++) {
                        query = "Insert into concept_orphan (id_concept, id_thesaurus)"
                                + " values('" + conceptabush.get(i) + "', '" + idTheso + "')";
                        stmt.execute(query);
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                //     conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while deleting Concept : " + idConcept, sqle);
        }
    }

    /**
     * Cette fonction permet de déplacer une Branche
     *
     * @param ds
     * @param idConcept
     * @param idOldConceptBT
     * @param idNewConceptBT
     * @param idThesaurus
     * @param idUser
     * @return true or false
     */
    public boolean moveBranch(HikariDataSource ds,
            String idConcept,
            String idOldConceptBT, String idNewConceptBT,
            String idThesaurus, int idUser) {
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);

            if (!new RelationsHelper().deleteRelationBT(conn, idConcept, idThesaurus, idOldConceptBT, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }
            if (!new RelationsHelper().addRelationBT(conn, idConcept, idThesaurus, idNewConceptBT, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);

        }
        return false;
    }

    /**
     * Cette fonction permet de déplacer une Branche vers un concept d'un autre
     * Groupe
     *
     * @param conn
     * @param idConcept
     * @param idOldConceptBT
     * @param idNewConceptBT
     * @param idThesaurus
     * @param idUser
     * @return true or false
     */
    public boolean moveBranchToConceptOtherGroup(
            Connection conn,
            String idConcept,
            String idOldConceptBT, String idNewConceptBT,
            String idThesaurus, int idUser) {
        try {
            if (!new RelationsHelper().deleteRelationBT(conn, idConcept, idThesaurus, idOldConceptBT, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }
            if (!new RelationsHelper().addRelationBT(conn, idConcept, idThesaurus, idNewConceptBT, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);

        }
        return false;
    }

    /**
     * Cette fonction permet de déplacer une Branche vers un domaine Le domaine
     * de destination est le même que la branche (déplamcent dans le même
     * domaine)
     *
     * @param conn
     * @param idConcept
     * @param idOldConceptBT
     * @param idMT
     * @param idThesaurus
     * @param idUser
     * @return true or false
     */
    public boolean moveBranchToMT(Connection conn,
            String idConcept,
            String idOldConceptBT, String idMT,
            String idThesaurus, int idUser) {
        try {
            RelationsHelper relationsHelper = new RelationsHelper();
            conn.setAutoCommit(false);

            if (!relationsHelper.deleteRelationBT(conn, idConcept, idThesaurus, idOldConceptBT, idUser)) {
                return false;
            }
            return relationsHelper.addRelationTT(conn, idConcept, idMT, idThesaurus, idUser);

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);

        }
        return false;
    }

    /**
     * Cette fonction permet de déplacer une Branche vers un domaine Le domaine
     * de destination est le même que la branche (déplamcent dans le même
     * domaine)
     *
     * @param conn
     * @param idConcept
     * @param idOldConceptBT
     * @param oldMT
     * @param idNewMT
     * @param idThesaurus
     * @param idUser
     * @return true or false
     */
    public boolean moveBranchToAnotherMT(Connection conn,
            String idConcept,
            String idOldConceptBT,
            String oldMT,
            String idNewMT,
            String idThesaurus, int idUser) {
        try {
            RelationsHelper relationsHelper = new RelationsHelper();
            conn.setAutoCommit(false);

            if (!relationsHelper.deleteRelationBT(conn, idConcept, idThesaurus, idOldConceptBT, idUser)) {
                return false;
            }
            // on attribue la relation TT  au concept qui va passer à la racine d'un autre Group,
            // mais comme on est en mode Autocommit= false, l'ancien Group du concept ne change pas tant qu'on a pas Commité  
            return relationsHelper.addRelationTT(conn, idConcept, oldMT, idThesaurus, idUser);

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);

        }
        return false;
    }

    /**
     * Cette fonction permet de déplacer une Branche d'un domaine vers un
     * concept dans le thésaurus Le domaine de destination est le même que la
     * branche (déplacement dans le même domaine)
     *
     * @param conn
     * @param idConcept
     * @param idNewConcept
     * @param idMT
     * @param idThesaurus
     * @param idUser
     * @return true or false
     */
    public boolean moveBranchFromMT(Connection conn,
            String idConcept,
            String idNewConcept, String idMT,
            String idThesaurus, int idUser) {
        try {
            RelationsHelper relationsHelper = new RelationsHelper();
            conn.setAutoCommit(false);

            if (!relationsHelper.deleteRelationTT(conn, idConcept, idMT, idThesaurus, idUser)) {
                return false;
            }
            return relationsHelper.addRelationBT(conn, idConcept, idThesaurus, idNewConcept, idUser);
        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);

        }
        return false;
    }

    /**
     * Cette fonction permet de supprimer un ConceptCandidat
     *
     * @param ds
     * @param idConcept
     * @param idLang
     * @param idThesaurus
     * @return null si le term existe ou si erreur, sinon le numero de Concept
     */
    /*   public boolean deleteCandidat(HikariDataSource ds,
     String idConcept,
     String idLang, String idThesaurus) {

     TermHelper termHelper = new TermHelper();

     if(! deleteConceptCandidat(ds, idConcept, idThesaurus))
     return false;

     String idTermCandidat = termHelper.addTermCandidat(ds, lexical_value, idLang, idThesaurus, contributor);
     if (idTermCandidat == null) {
     return null;
     }
        
     if(!addRelationConceptTermCandidat(ds, idConceptCandidat,
     idTermCandidat, idThesaurus))
     return null;
        
     termHelper.addPropositionCandidat(ds, idConceptCandidat, contributor, idThesaurus, note, idParentConcept, idGroup);

     return idConceptCandidat;
     }*/
    /**
     * Cette fonction permet d'ajouter une traduction à un terme
     *
     * @param ds
     * @param term
     * @param idUser
     * @return null si le term existe ou si erreur, sinon le numero de Concept
     */
    public boolean addConceptTraduction(
            HikariDataSource ds, Term term, int idUser) {

        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            TermHelper termHelper = new TermHelper();
            // controle si le term existe avant de rajouter un concept
            if (termHelper.isTermExist(ds, term.getLexical_value(),
                    term.getId_thesaurus(), term.getLang())) {
                return false;
            }

            if (!termHelper.addTermTraduction(conn, term, idUser)) {
                conn.rollback();
                conn.close();
                return false;
            }

            conn.commit();
            conn.close();

            // cette fonction permet de remplir la table Permutée
            termHelper.splitConceptForPermute(ds, term.getId_concept(),
                    getGroupIdOfConcept(ds, term.getId_concept(), term.getId_thesaurus()),
                    term.getId_thesaurus(),
                    term.getLang(),
                    term.getLexical_value());

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex);
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex1) {
                Logger.getLogger(ConceptHelper.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return false;
    }

    /**
     * Cette fonction permet d'ajouter une relation à la table
     * hierarchicalRelationship
     *
     * @param conn
     * @param hierarchicalRelationship
     * @param idUser
     * @return
     */
    public boolean addLinkHierarchicalRelation(Connection conn,
            HierarchicalRelationship hierarchicalRelationship, int idUser) {

        //     Connection conn;
        Statement stmt;

        try {
            //conn.setAutoCommit(false);
            // Get connection from pool
            //       conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    if (!new RelationsHelper().addRelationHistorique(conn, hierarchicalRelationship.getIdConcept1(), hierarchicalRelationship.getIdThesaurus(), hierarchicalRelationship.getIdConcept2(), hierarchicalRelationship.getRole(), idUser, "ADD")) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                    String query = "Insert into hierarchical_relationship"
                            + "(id_concept1, id_thesaurus, role, id_concept2)"
                            + " values ("
                            + "'" + hierarchicalRelationship.getIdConcept1() + "'"
                            + ",'" + hierarchicalRelationship.getIdThesaurus() + "'"
                            + ",'" + hierarchicalRelationship.getRole() + "'"
                            + ",'" + hierarchicalRelationship.getIdConcept2() + "')";
                    stmt.executeUpdate(query);
                    //  conn.commit();
                } finally {
                    stmt.close();
                }
            } finally {
                // conn.close();
            }
        } catch (SQLException sqle) {
            // To avoid dupplicate Key
            //   System.out.println(sqle.toString());
            if (!sqle.getSQLState().equalsIgnoreCase("23505")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cette fonction permet d'ajouter un Concept à la table Concept, en
     * paramètre un objet Classe Concept
     *
     * @param ds
     * @param hierarchicalRelationship
     * @param idUser
     */
    public void addAssociativeRelation(HikariDataSource ds,
            HierarchicalRelationship hierarchicalRelationship, int idUser) { // Role RT pour terme associés

        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                try {
                    if (!new RelationsHelper().addRelationHistorique(conn, hierarchicalRelationship.getIdConcept1(), hierarchicalRelationship.getIdThesaurus(), hierarchicalRelationship.getIdConcept2(), hierarchicalRelationship.getRole(), idUser, "ADD")) {
                        conn.rollback();
                        conn.close();
                        return;
                    }

                    if (!new RelationsHelper().addRelationHistorique(conn, hierarchicalRelationship.getIdConcept2(), hierarchicalRelationship.getIdThesaurus(), hierarchicalRelationship.getIdConcept1(), hierarchicalRelationship.getRole(), idUser, "ADD")) {
                        conn.rollback();
                        conn.close();
                        return;
                    }

                    String query = "Insert into hierarchical_relationship"
                            + "(id_concept1, id_thesaurus, role, id_concept2)"
                            + " values ("
                            + "'" + hierarchicalRelationship.getIdConcept1() + "'"
                            + ",'" + hierarchicalRelationship.getIdThesaurus() + "'"
                            + ",'" + hierarchicalRelationship.getRole() + "'"
                            + ",'" + hierarchicalRelationship.getIdConcept2() + "')";

                    stmt.executeUpdate(query);

                    query = "Insert into hierarchical_relationship"
                            + "(id_concept1, id_thesaurus, role, id_concept2)"
                            + " values ("
                            + "'" + hierarchicalRelationship.getIdConcept2() + "'"
                            + ",'" + hierarchicalRelationship.getIdThesaurus() + "'"
                            + ",'" + hierarchicalRelationship.getRole() + "'"
                            + ",'" + hierarchicalRelationship.getIdConcept1() + "')";
                    stmt.executeUpdate(query);
                    conn.commit();
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            //    if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
            if (!sqle.getSQLState().equalsIgnoreCase("23505")) {
                log.error("Error while adding hierarchicalRelationship RT : "
                        + hierarchicalRelationship.getIdConcept1(), sqle);
            }
        }

    }

    /**
     * Cette fonction permet d'ajouter un Concept à la table Concept, en
     * paramètre un objet Classe Concept
     *
     * @param conn
     * @param concept
     * @param idUser
     * @return
     */
    public String addConceptInTable(Connection conn,
            Concept concept, int idUser) {

        String idConcept = null;
        String idArk = "";
        //   Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            //     conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                String query;
                try {
                    if (identifierType.equalsIgnoreCase("1")) { // identifiants types alphanumérique
                        ToolsHelper toolsHelper = new ToolsHelper();
                        idConcept = toolsHelper.getNewId(10);
                        while (isIdExiste(conn, idConcept, concept.getIdThesaurus())) {
                            idConcept = toolsHelper.getNewId(10);
                        }
                        concept.setIdConcept(idConcept);
                    } else {
                        query = "select max(id) from concept";
                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        resultSet.next();
                        int idNumerique = resultSet.getInt(1);
                        idNumerique++;
                        idConcept = "" + (idNumerique);
                        // si le nouveau Id existe, on l'incrémente
                        while (isIdExiste(conn, idConcept, concept.getIdThesaurus())) {
                            idConcept = "" + (++idNumerique);
                        }
                        concept.setIdConcept(idConcept);
                    }

                    query = "Insert into concept "
                            + "(id_concept, id_thesaurus, id_ark, status, notation, top_concept, id_group)"
                            + " values ("
                            + "'" + idConcept + "'"
                            + ",'" + concept.getIdThesaurus() + "'"
                            + ",'" + idArk + "'"
                            + ",'" + concept.getStatus() + "'"
                            + ",'" + concept.getNotation() + "'"
                            + "," + concept.isTopConcept()
                            + ",'" + concept.getIdGroup() + "')";

                    stmt.executeUpdate(query);

                    /**
                     * Ajout des informations dans la table Concept
                     */
                    if (!addConceptHistorique(conn, concept, idUser)) {
                        stmt.close();
                        return null;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                //  conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
                log.error("Error while adding Concept : " + idConcept, sqle);
            }
            idConcept = null;
        }
        return idConcept;
    }

    /**
     * cette funtion permet de savoir si le Id_concept déjà est utilicée
     *
     * @param conn
     * @param id_Concept
     * @return
     * @throws SQLException
     */
//    public boolean ilPeux(Connection conn, String id_Concept) throws SQLException {
//        Statement stmt;
//        ResultSet resultSet;
//
//        try {
//            // Get connection from pool
//            //     conn = ds.getConnection();
//            try {
//                stmt = conn.createStatement();
//                String query;
//                try {
//                    query = "SELECT id_concept from concept where id_concept ='" + id_Concept + "'";
//                    resultSet = stmt.executeQuery(query);
//                    if (!resultSet.next()) {
//                        return true;
//                    }
//
//                } finally {
//                    stmt.close();
//                }
//            } finally {
//                //  conn.close();
//            }
//        } catch (SQLException sqle) {
//            // Log exception
//            if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
//                log.error("Error while adding Concept : " + id_Concept, sqle);
//            }
//        }
//        return false;
//    }
    /**
     * Cette fonction permet de savoir si l'ID du concept existe ou non
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return boolean
     */
    public boolean isIdExiste(HikariDataSource ds,
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
                    String query = "select id_concept from concept where "
                            + "id_concept = '" + idConcept
                            + "' and id_thesaurus = '" + idThesaurus
                            + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
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
            log.error("Error while asking if id exist : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de savoir si l'ID du concept existe ou non
     *
     * @param conn
     * @param idConcept
     * @param idThesaurus
     * @return boolean
     */
    public boolean isIdExiste(Connection conn,
            String idConcept, String idThesaurus) {

        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept where "
                            + "id_concept = '" + idConcept
                            + "' and id_thesaurus = '" + idThesaurus
                            + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if id exist : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de savoir si l'ID du concept existe ou non
     *
     * @param conn
     * @param idConcept
     * @return boolean
     */
    public boolean isIdExiste(Connection conn,
            String idConcept) {

        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept where "
                            + "id_concept = '" + idConcept + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if id exist : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de savoir si l'ID du concept existe ou non
     *
     * @param conn
     * @param idThesaurus
     * @param notation
     * @return boolean
     */
    public boolean isNotationExist(Connection conn,
            String idThesaurus, String notation) {

        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;

        if (notation.isEmpty()) {
            return false;
        }
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept where "
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and notation ilike '" + notation.trim() + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = resultSet.getRow() != 0;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while asking if Notation exist : " + notation, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet d'ajouter l'historique d'un concept
     *
     * @param conn
     * @param concept
     * @param idUser
     * @return
     */
    public boolean addConceptHistorique(Connection conn,
            Concept concept, int idUser) {
        boolean status = false;
        String idArk = "";
        //   Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            //     conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into concept_historique "
                            + "(id_concept, id_thesaurus, id_ark, status, notation, top_concept, id_group, id_user)"
                            + " values ("
                            + "'" + concept.getIdConcept() + "'"
                            + ",'" + concept.getIdThesaurus() + "'"
                            + ",'" + idArk + "'"
                            + ",'" + concept.getStatus() + "'"
                            + ",'" + concept.getNotation() + "'"
                            + "," + concept.isTopConcept()
                            + ",'" + concept.getIdGroup() + "'"
                            + ",'" + idUser + "')";
                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
                //  conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
                log.error("Error while adding Concept : " + concept.getIdConcept(), sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet de récupérer l'historique d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return String idGroup
     */
    public ArrayList<Concept> getConceptHisoriqueAll(HikariDataSource ds,
            String idConcept, String idThesaurus) {
        ArrayList<Concept> listeConcept = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT modified, status, notation, top_concept, id_group, username from concept_historique, users where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'"
                            + " and concept_historique.id_user=users.id_user"
                            + " order by modified DESC";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            Concept c = new Concept();
                            c.setIdConcept(idConcept);
                            c.setIdThesaurus(idThesaurus);
                            c.setModified(resultSet.getDate("modified"));
                            c.setStatus(resultSet.getString("status"));
                            c.setNotation(resultSet.getString("notation"));
                            c.setTopConcept(resultSet.getBoolean("top_concept"));
                            c.setIdGroup(resultSet.getString("id_group"));
                            c.setUserName(resultSet.getString("username"));
                            listeConcept.add(c);
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
            log.error("Error while getting historique of Concept : " + idConcept, sqle);
        }
        return listeConcept;
    }

    /**
     * Cette fonction permet de récupérer l'historique d'un concept à une date
     * précise
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param date
     * @return String idGroup
     */
    public ArrayList<Concept> getConceptHisoriqueFromDate(HikariDataSource ds,
            String idConcept, String idThesaurus, java.util.Date date) {
        ArrayList<Concept> listeConcept = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT modified, status, notation, top_concept, id_group, username from concept_historique, users where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'"
                            + " and concept_historique.id_user=users.id_user"
                            + " and modified <= '" + date
                            + "' order by modified DESC";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            Concept c = new Concept();
                            c.setIdConcept(idConcept);
                            c.setIdThesaurus(idThesaurus);
                            c.setModified(resultSet.getDate("modified"));
                            c.setStatus(resultSet.getString("status"));
                            c.setNotation(resultSet.getString("notation"));
                            c.setTopConcept(resultSet.getBoolean("top_concept"));
                            c.setIdGroup(resultSet.getString("id_group"));
                            c.setUserName(resultSet.getString("username"));
                            listeConcept.add(c);
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
            log.error("Error while getting date historique of Concept : " + idConcept, sqle);
        }
        return listeConcept;
    }

    /**
     *
     * @param conn
     * @param idConcept
     * @param idThesaurus
     * @param urlSite
     * @param nodeMetaData
     * @return
     */
    private boolean addIdArk(Connection conn,
            String idConcept,
            String idThesaurus,
            String urlSite,
            NodeMetaData nodeMetaData,
            int idUser) {
        /**
         * récupération du code Ark via WebServices
         *
         */
        Ark_Client ark_Client = new Ark_Client();
        String idArk = ark_Client.getArkId(
                new FileUtilities().getDate(),
                urlSite + "?idc=" + idConcept + "&idt=" + idThesaurus,
                nodeMetaData.getTitle(), // title
                nodeMetaData.getCreator(), // creator
                nodeMetaData.getDcElementsList(),
                "pcrt" // pcrt : p= pactols, crt=code DCMI pour collection
        ); // description
        if (idArk == null) {
            return false;
        }

        return updateArkIdOfConcept(conn, idConcept,
                idThesaurus, idArk);
    }

    /**
     * Cette fonction permet d'ajouter un domaine à un Concept dans la table
     * Concept, en paramètre un objet Classe Concept
     *
     * @param conn
     * @param concept
     * @param idUser
     * @return true or false
     */
    public boolean addNewGroupOfConcept(Connection conn,
            Concept concept, int idUser) {

        Statement stmt;
        boolean status = false;
        try {
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                try {

                    if (!addConceptHistorique(conn, concept, idUser)) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                    String query = "Insert into concept "
                            + "(id_concept, id_thesaurus, id_ark, status, notation, top_concept, id_group)"
                            + " values ("
                            + "'" + concept.getIdConcept() + "'"
                            + ",'" + concept.getIdThesaurus() + "'"
                            + ",'" + concept.getIdArk() + "'"
                            + ",'" + concept.getStatus() + "'"
                            + ",'" + concept.getNotation() + "'"
                            + "," + concept.isTopConcept()
                            + ",'" + concept.getIdGroup() + "')";

                    stmt.executeUpdate(query);
                    status = true;
                    conn.commit();
                } finally {
                    stmt.close();
                }
            } finally {
                //         conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
                log.error("Error while adding Concept : " + concept.getIdConcept(), sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet d'insérrer un Concept dans la table Concept avec un
     * idConcept existant (Import ou Orphelin) Rollback
     *
     * @param conn
     * @param concept
     * @param urlSite
     * @param isArkActive
     * @param idUser
     * @return
     */
    public boolean insertConceptInTableRollBack(Connection conn,
            Concept concept, String urlSite, boolean isArkActive, int idUser) {

        Statement stmt;
        boolean status = false;
        if (concept.getCreated() == null) {
            concept.setCreated(new java.util.Date());
        }
        if (concept.getModified() == null) {
            concept.setModified(new java.util.Date());
        }
        try {
            // Get connection from pool
            String query;
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                try {

                    /**
                     * récupération du code Ark via WebServices
                     *
                     */
                    String idArk = "";
                    if (isArkActive) {
                        ArrayList<DcElement> dcElementsList = new ArrayList<>();
                        Ark_Client ark_Client = new Ark_Client();
                        idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                urlSite + "?idc=" + concept.getIdConcept() + "&idt=" + concept.getIdThesaurus(),
                                "", "", dcElementsList, "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
                    }
                    /**
                     * Ajout des informations dans la table Concept
                     */
                    if (!addConceptHistorique(conn, concept, idUser)) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }
                    if (concept.getCreated() == null || concept.getModified() == null) {

                        query = "Insert into concept "
                                + "(id_concept, id_thesaurus, id_ark, status, notation, top_concept, id_group)"
                                + " values ("
                                + "'" + concept.getIdConcept() + "'"
                                + ",'" + concept.getIdThesaurus() + "'"
                                + ",'" + idArk + "'"
                                + ",'" + concept.getStatus() + "'"
                                + ",'" + concept.getNotation() + "'"
                                + "," + concept.isTopConcept()
                                + ",'" + concept.getIdGroup() + "')";
                    } else {
                        query = "Insert into concept "
                                + "(id_concept, id_thesaurus, id_ark, created, modified, status, notation, top_concept, id_group)"
                                + " values ("
                                + "'" + concept.getIdConcept() + "'"
                                + ",'" + concept.getIdThesaurus() + "'"
                                + ",'" + idArk + "'"
                                + ",'" + concept.getCreated() + "'"
                                + ",'" + concept.getModified() + "'"
                                + ",'" + concept.getStatus() + "'"
                                + ",'" + concept.getNotation() + "'"
                                + "," + concept.isTopConcept()
                                + ",'" + concept.getIdGroup() + "')";
                    }

                    stmt.executeUpdate(query);
                    status = true;
                    conn.commit();
                } finally {
                    stmt.close();
                }
            } finally {

            }
        } catch (SQLException sqle) {
            // Log exception
            if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
                log.error("Error while adding Concept : " + concept.getIdConcept(), sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet d'insérrer un Concept dans la table Concept avec un
     * idConcept existant (Import)
     *
     * @param ds
     * @param concept
     * @param urlSite
     * @param isArkActive
     * @param idUser
     * @return
     */
    public boolean insertConceptInTable(HikariDataSource ds,
            Concept concept, String urlSite, boolean isArkActive, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        if (concept.getCreated() == null) {
            concept.setCreated(new java.util.Date());
        }
        if (concept.getModified() == null) {
            concept.setModified(new java.util.Date());
        }
        try {
            // Get connection from pool
            conn = ds.getConnection();
            String query;
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                try {

                    /**
                     * récupération du code Ark via WebServices
                     *
                     */
                    String idArk = "";
                    if (isArkActive) {
                        ArrayList<DcElement> dcElementsList = new ArrayList<>();
                        Ark_Client ark_Client = new Ark_Client();
                        idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                urlSite + "?idc=" + concept.getIdConcept() + "&idt=" + concept.getIdThesaurus(),
                                "", "", dcElementsList, "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
                        concept.setIdArk(idArk);
                    } else {
                        concept.setIdArk("");
                    }

                    /**
                     * Ajout des informations dans la table Concept
                     */
                    if (!addConceptHistorique(conn, concept, idUser)) {
                        conn.rollback();
                        conn.close();
                        return false;
                    }

                    if (concept.getCreated() == null || concept.getModified() == null) {
                        query = "Insert into concept "
                                + "(id_concept, id_thesaurus, id_ark, status, notation, top_concept, id_group)"
                                + " values ("
                                + "'" + concept.getIdConcept() + "'"
                                + ",'" + concept.getIdThesaurus() + "'"
                                + ",'" + concept.getIdArk() + "'"
                                + ",'" + concept.getStatus() + "'"
                                + ",'" + concept.getNotation() + "'"
                                + "," + concept.isTopConcept()
                                + ",'" + concept.getIdGroup() + "')";
                    } else {
                        query = "Insert into concept "
                                + "(id_concept, id_thesaurus, id_ark, created, modified, status, notation, top_concept, id_group)"
                                + " values ("
                                + "'" + concept.getIdConcept() + "'"
                                + ",'" + concept.getIdThesaurus() + "'"
                                + ",'" + concept.getIdArk() + "'"
                                + ",'" + concept.getCreated() + "'"
                                + ",'" + concept.getModified() + "'"
                                + ",'" + concept.getStatus() + "'"
                                + ",'" + concept.getNotation() + "'"
                                + "," + concept.isTopConcept()
                                + ",'" + concept.getIdGroup() + "')";
                    }

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
            if (!sqle.getMessage().contains("duplicate key value violates unique constraint")) {
                log.error("Error while adding Concept : " + concept.getIdConcept(), sqle);
            }
        }
        return status;
    }

    /**
     * Cette fonction permet de récupérer un Concept par son id et son thésaurus
     * sous forme de classe Concept (sans les relations) ni le Terme
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public Concept getThisConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Concept concept = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select * from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        concept = new Concept();
                        concept.setIdConcept(idConcept);
                        concept.setIdThesaurus(idThesaurus);
                        concept.setIdArk(resultSet.getString("id_ark"));
                        concept.setCreated(resultSet.getDate("created"));
                        concept.setModified(resultSet.getDate("modified"));
                        concept.setStatus(resultSet.getString("status"));
                        concept.setNotation(resultSet.getString("notation"));
                        concept.setTopConcept(resultSet.getBoolean("top_concept"));
                        concept.setIdGroup(resultSet.getString("id_group"));
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
            log.error("Error while getting Concept : " + idConcept, sqle);
        }
        return concept;
    }

    /**
     * Cette fonction permet de récupérer la date de modificatin du Concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public Date getModifiedDateOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Date date = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select modified from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        date = resultSet.getDate("modified");
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
            log.error("Error while getting modified date of Concept : " + idConcept, sqle);
        }
        return date;
    }

    /**
     * Cette fonction permet de récupérer la liste des Id concept d'un thésaurus
     * (cette fonction sert pour la génération de la table Permuté
     *
     * @param ds
     * @param idThesaurus
     * @return ArrayList
     */
    public ArrayList<String> getAllIdConceptOfThesaurus(HikariDataSource ds,
            String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> tabIdConcept = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept where id_thesaurus = '"
                            + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    while (resultSet.next()) {
                        tabIdConcept.add(resultSet.getString("id_concept"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting All IdConcept of Thesaurus : " + idThesaurus, sqle);
        }
        return tabIdConcept;
    }

    /**
     * Cette fonction permet d'exporter tous les concepts d'un thésaurus et les
     * charger dans la classe No
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param nodeConceptExports
     * @return
     */
    public ArrayList<NodeConceptExport> exportAllConcepts(HikariDataSource ds,
            String idConcept, String idThesaurus,
            ArrayList<NodeConceptExport> nodeConceptExports) {

        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList<String> listIdsOfConceptChildren = conceptHelper.getListChildrenOfConcept(ds, idConcept, idThesaurus);

        NodeConceptExport nodeConcept = conceptHelper.getConceptForExport(ds, idConcept, idThesaurus, false);

        //    System.out.println("IdConcept = " + idConcept);
        /// attention il y a un problème ici, il faut vérifier pourquoi nous avons un Concept Null
        if (nodeConcept.getConcept() == null) {
            System.err.println("Attention Null proche de = : " + idConcept);
            int k = 0;
            return null;
        }

        nodeConceptExports.add(nodeConcept);

        for (String listIdsOfConceptChildren1 : listIdsOfConceptChildren) {
            nodeConcept = conceptHelper.getConceptForExport(ds, listIdsOfConceptChildren1, idThesaurus, false);
            nodeConceptExports.add(nodeConcept);
            if (!nodeConcept.getNodeListIdsOfNT().isEmpty()) {
                for (int j = 0; j < nodeConcept.getNodeListIdsOfNT().size(); j++) {

                    exportAllConcepts(ds,
                            nodeConcept.getNodeListIdsOfNT().get(j).getIdConcept(),
                            idThesaurus, nodeConceptExports);
                }
            }
        }
        return nodeConceptExports;
    }

    /**
     * Cette fonction permet de récupérer le nom d'un Concept sinon renvoie un
     * une chaine vide
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class Concept
     */
    public String getLexicalValueOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String lexicalValue = "";
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select lexical_value from term, preferred_term where"
                            + " preferred_term.id_term = term.id_term AND"
                            + " preferred_term.id_thesaurus = term.id_thesaurus"
                            + " and term.id_thesaurus = '" + idThesaurus + "'"
                            + " and preferred_term.id_concept = '" + idConcept + "'"
                            + " and term.lang = '" + idLang + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    if (resultSet.next()) {

                        lexicalValue = resultSet.getString("lexical_value");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting LexicalValue of Concept : " + idConcept, sqle);
        }
        return lexicalValue;
    }

    /**
     * Cette fonction permet de récupérer l'identifiant Ark sinon renvoie un une
     * chaine vide
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class Concept
     */
    public String getIdArkOfConcept(HikariDataSource ds, String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String ark = "";
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_ark from concept where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    if (resultSet.next()) {

                        ark = resultSet.getString("id_ark");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting idArk of Concept : " + idConcept, sqle);
        }
        return ark;
    }

    /**
     * Cette fonction permet de récupérer l'identifiant du Concept d'après
     * l'idArk
     *
     * @param ds
     * @param arkId
     * @return IdConcept
     */
    public String getIdConceptFromArkId(HikariDataSource ds, String arkId) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idConcept = null;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept where"
                            + " id_ark = '" + arkId + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    if (resultSet.next()) {
                        idConcept = resultSet.getString("id_concept");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting idConcept by idArk : " + arkId, sqle);
        }
        return idConcept;
    }

    /**
     * Cette fonction permet de récupérer l'identifiant du Concept d'après
     * l'idArk
     *
     * @param ds
     * @param arkId
     * @return IdConcept
     */
    public String getIdThesaurusFromArkId(HikariDataSource ds, String arkId) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idThesaurus = null;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_thesaurus from concept where"
                            + " id_ark = '" + arkId + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    if (resultSet.next()) {
                        idThesaurus = resultSet.getString("id_thesaurus");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting idThesaurus by idArk : " + arkId, sqle);
        }
        return idThesaurus;
    }

    /**
     * Cette fonction permet de récupérer l'identifiant du Group d'un Concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return String idGroup
     */
    public String getGroupIdOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idGroup = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_group from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        if (resultSet.next()) {
                            idGroup = resultSet.getString("id_group");
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
            log.error("Error while getting Id of group of Concept : " + idConcept, sqle);
        }
        return idGroup;
    }

    public void insertID_grouptoPermuted(HikariDataSource ds, String id_thesaurus, String id_concept) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idGroup = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "update permuted set id_group = (select id_group from concept"
                            + " where id_thesaurus = '" + id_thesaurus
                            + "' and id_concept = '" + id_concept
                            + "') where  id_concept ='" + id_concept
                            + "'";
                    stmt.execute(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Id of group of Concept : " + id_concept, sqle);
        }

    }

    /**
     * Cette fonction permet de récupérer les identifiants des Group d'un
     * Concept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return String idGroup
     */
    public ArrayList<String> getListGroupIdOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> idGroup = new ArrayList<>();
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_group from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            idGroup.add(resultSet.getString("id_group"));
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
            log.error("Error while getting Id of group of Concept : " + idConcept, sqle);
        }
        return idGroup;
    }

    /**
     * Cette fonction permet de récupérer les identifiants des Group d'un
     * Concept dont il est le fils direct
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return String idGroup
     */
    public ArrayList<String> getListGroupParentIdOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> idGroup = new ArrayList<>();
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_group from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'"
                            + " and top_concept=true";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            idGroup.add(resultSet.getString("id_group"));
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
            log.error("Error while getting Id of group of Concept : " + idConcept, sqle);
        }
        return idGroup;
    }

    /**
     * Cette fonction permet de récupérer les identifiants des Group des parents
     * d'un concept SAUF les groupes du parent passé en paramètre
     *
     * @param ds
     * @param idConceptParent
     * @param idThesaurus
     * @param idNoGroup le parent dont on ne souhaite pas avoir les groupes
     * @return String idGroup
     */
    public ArrayList<String> getListGroupIdParentOfConceptOtherThan(HikariDataSource ds,
            ArrayList<String> idConceptParent, String idThesaurus, String idNoGroup) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> idGroup = new ArrayList<>();
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT DISTINCT id_group from concept where id_thesaurus = '"
                            + idThesaurus + "' and (";
                    for (String s : idConceptParent) {
                        query += "id_concept = '" + s + "' or ";
                    }
                    query = query.substring(0, query.length() - 4);
                    query += ") and id_concept != '" + idNoGroup + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            idGroup.add(resultSet.getString("id_group"));
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
            log.error("Error while getting Id group of parent of Concept", sqle);
        }
        return idGroup;
    }

    /**
     * Cettte fonction permet de retourner la liste des TopConcept avec IdArk
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeUri> getListIdsOfTopConceptsForExport(HikariDataSource ds,
            String idGroup, String idThesaurus) {

        ArrayList<String> listIdTopConcept = getListIdsOfTopConcepts(ds, idGroup, idThesaurus);
        ArrayList<NodeUri> listIdTopConcept_Ark = getListIdArkOfConcept(ds, listIdTopConcept, idThesaurus);

        return listIdTopConcept_Ark;
    }

    /**
     * Cette fonction permet de récupérer la liste des Ids of Topconcepts
     * suivant l'id du groupe et le thésaurus
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @return Objet class NodeConceptTree
     */
    public ArrayList<String> getListIdsOfTopConcepts(HikariDataSource ds,
            String idGroup, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> listIdOfTopConcept = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_group = '" + idGroup + "'"
                            + " and top_concept = true";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        listIdOfTopConcept.add(resultSet.getString("id_concept"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Ids of TopConcept of Group : " + idGroup, sqle);
        }
        return listIdOfTopConcept;
    }

    /**
     * Cette fonction permet de récupérer la liste des Ids of Topconcepts pour
     * un thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @return Objet class NodeTT
     */
    public ArrayList<NodeTT> getAllListIdsOfTopConcepts(HikariDataSource ds,
            String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeTT> listIdOfTopConcept = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept,"
                            + "id_ark, id_group from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and top_concept = true";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        NodeTT nodeTT = new NodeTT();
                        nodeTT.setIdConcept(resultSet.getString("id_concept"));
                        nodeTT.setIdArk(resultSet.getString("id_ark"));
                        nodeTT.setIdGroup(resultSet.getString("id_group"));
                        listIdOfTopConcept.add(nodeTT);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting Ids of TopConcept of thésaurus : " + idThesaurus, sqle);
            listIdOfTopConcept = null;
        }
        return listIdOfTopConcept;
    }

    /**
     * Cette fonction permet de récupérer la liste des Topconcepts suivant l'id
     * du groupe et le thésaurus sous forme de classe NodeConceptTree (sans les
     * relations)
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @param idLang
     * @return Objet class NodeConceptTree
     */
    public ArrayList<NodeConceptTree> getListTopConcepts(HikariDataSource ds,
            String idGroup, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeConceptTree> nodeConceptTree = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept, status from concept where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_group = '" + idGroup + "'"
                            + " and top_concept = true";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        nodeConceptTree = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeConceptTree nodeConceptTree1 = new NodeConceptTree();
                            nodeConceptTree1.setIdConcept(resultSet.getString("id_concept"));
                            nodeConceptTree1.setStatusConcept(resultSet.getString("status"));
                            nodeConceptTree1.setIdThesaurus(idThesaurus);
                            nodeConceptTree1.setIdLang(idLang);
                            nodeConceptTree.add(nodeConceptTree1);
                        }
                    }
                    for (NodeConceptTree nodeConceptTree1 : nodeConceptTree) {
                        query = "SELECT term.lexical_value FROM term, preferred_term"
                                + " WHERE preferred_term.id_term = term.id_term"
                                + " and preferred_term.id_concept ='" + nodeConceptTree1.getIdConcept() + "'"
                                + " and term.lang = '" + idLang + "'"
                                + " and term.id_thesaurus = '" + idThesaurus + "'";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            resultSet.next();
                            if (resultSet.getRow() == 0) {
                                nodeConceptTree1.setTitle("");

                            } else {

                                nodeConceptTree1.setTitle(resultSet.getString("lexical_value"));

                            }
                            nodeConceptTree1.setHaveChildren(
                                    haveChildren(ds, idThesaurus, nodeConceptTree1.getIdConcept())
                            );
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
            log.error("Error while getting TopConcept of Group : " + idGroup, sqle);
        }
        Collections.sort(nodeConceptTree);
        return nodeConceptTree;
    }

    /**
     * Cette fonction permet de rendre un Concept de type Topconcept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return
     */
    public boolean setTopConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE concept "
                            + "set top_concept = true"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idThesaurus + "'";

                    stmt.executeUpdate(query);
                    return true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating group of concept : " + idConcept, sqle);
        }
        return false;
    }

    /**
     * Cette fonction permet de savoir si le Concept est un TopConcept
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idGroup
     * @return boolean
     */
    public boolean isTopConcept(HikariDataSource ds,
            String idConcept, String idThesaurus, String idGroup) {

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
                    String query = "select top_concept from concept where "
                            + " id_concept = '" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'"
                            + " and id_group = '" + idGroup + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = resultSet.getBoolean("top_concept");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Asking if TopConcept : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de savoir si le Concept est un TopConcept sans
     * définir le group (pour permettre de nettoyer les orphelins)
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return boolean
     */
    public boolean isTopConcept(HikariDataSource ds,
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
                    String query = "select top_concept from concept where "
                            + " id_concept = '" + idConcept + "'"
                            + " and id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        existe = resultSet.getBoolean("top_concept");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Asking if TopConcept : " + idConcept, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de récupérer les Ids des concepts suivant l'id du
     * Concept-Père et le thésaurus sous forme de classe tableau
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet Array String
     */
    public ArrayList<String> getListChildrenOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> listIdsOfConcept = new ArrayList<>();

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
                        listIdsOfConcept.add(resultSet.getString("id_concept2"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List of Id of Concept : " + idConcept, sqle);
        }
        return listIdsOfConcept;
    }

    public ArrayList<String> getListChildrenOfConceptNotExist(HikariDataSource ds,
            String idConcept, String idThesaurus, int id_alignement_source) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> listIdsOfConcept = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept2 from hierarchical_relationship"
                            + " where id_concept2 not in ( SELECT "
                            + " hierarchical_relationship.id_concept2"
                            + " FROM "
                            + " public.alignement,"
                            + " public.hierarchical_relationship "
                            + " WHERE "
                            + " alignement.internal_id_concept = hierarchical_relationship.id_concept2 AND"
                            + " alignement.internal_id_thesaurus = hierarchical_relationship.id_thesaurus AND"
                            + " alignement.id_alignement_source = "+id_alignement_source +" AND "
                            + " hierarchical_relationship.role = 'NT'"
                            + " AND hierarchical_relationship.id_thesaurus = '"+idThesaurus+"'"
                            + " and hierarchical_relationship.id_concept1 = '"+idConcept+"')"
                            + " and id_thesaurus = '"+idThesaurus+"'"
                            + " and role ='NT'"
                            + " and id_concept1= '"+idConcept+"'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        listIdsOfConcept.add(resultSet.getString("id_concept2"));
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List of Id of Concept : " + idConcept, sqle);
        }
        return listIdsOfConcept;
    }

    /**
     * Cette fonction permet de récupérer la liste des concepts suivant l'id du
     * Concept-Père et le thésaurus sous forme de classe NodeConceptTree (sans
     * les relations)
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class NodeConceptTree
     */
    /*public ArrayList<NodeConceptTree> getListConcepts(HikariDataSource ds,
     String idConcept, String idThesaurus, String idLang) {

     Connection conn;
     Statement stmt;
     ResultSet resultSet;
     ArrayList<NodeConceptTree> nodeConceptTree = null;

     try {
     // Get connection from pool
     conn = ds.getConnection();
     try {
     stmt = conn.createStatement();
     try {
     String query = "SELECT DISTINCT term.lexical_value, term.lang,"
     + " term.id_thesaurus, preferred_term.id_concept, concept.status"
     + " FROM term,preferred_term,concept,hierarchical_relationship"
     + " WHERE preferred_term.id_term = term.id_term AND"
     + " preferred_term.id_thesaurus = term.id_thesaurus AND"
     + " concept.id_concept = preferred_term.id_concept AND"
     + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
     + " hierarchical_relationship.id_concept2 = concept.id_concept"
     + " and concept.id_thesaurus = '" + idThesaurus + "'"
     + " and hierarchical_relationship.role = 'NT'"
     + " and hierarchical_relationship.id_concept1 = '" + idConcept + "'"
     + " and term.lang = '" + idLang + "'";
     //" ORDER BY unaccent_string(term.lexical_value) ASC;";

     stmt.executeQuery(query);
     resultSet = stmt.getResultSet();
     if (resultSet != null) {
     nodeConceptTree = new ArrayList<>();
     while (resultSet.next()) {
     NodeConceptTree nodeConceptTree1 = new NodeConceptTree();
     nodeConceptTree1.setIdConcept(resultSet.getString("id_concept"));
     nodeConceptTree1.setStatusConcept(resultSet.getString("status"));
     nodeConceptTree1.setIdThesaurus(idThesaurus);
     nodeConceptTree1.setIdLang(idLang);
     if (resultSet.getString("lexical_value").trim().equals("")) {
     nodeConceptTree1.setTitle("");
     } else {
     nodeConceptTree1.setTitle(resultSet.getString("lexical_value").trim());
     }
     nodeConceptTree1.setHaveChildren(
     haveChildren(ds, idThesaurus, nodeConceptTree1.getIdConcept()));
     nodeConceptTree.add(nodeConceptTree1);
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
     Collections.sort(nodeConceptTree);
     return nodeConceptTree;
     }*/
    public ArrayList<NodeConceptTree> getListConcepts(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeConceptTree> nodeConceptTree = null;

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
                        nodeConceptTree = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeConceptTree nodeConceptTree1 = new NodeConceptTree();
                            nodeConceptTree1.setIdConcept(resultSet.getString("id_concept2"));
                            nodeConceptTree1.setIdThesaurus(idThesaurus);
                            nodeConceptTree1.setIdLang(idLang);
                            nodeConceptTree.add(nodeConceptTree1);
                        }
                    }
                    for (NodeConceptTree nodeConceptTree1 : nodeConceptTree) {
                        /* désactivé, ne marche pas pour les termes dépréciés

                        query = "SELECT term.lexical_value, term.status FROM term, preferred_term"
                                + " WHERE preferred_term.id_term = term.id_term"
                                + " and preferred_term.id_concept ='"
                                + nodeConceptTree1.getIdConcept() + "'"
                                + " and term.lang = '" + idLang + "'"
                                + " and term.id_thesaurus = '" + idThesaurus + "'";

                         */

                        query = "SELECT term.lexical_value, concept.status"
                                + " FROM concept, preferred_term, term"
                                + " WHERE concept.id_concept = preferred_term.id_concept AND"
                                + " concept.id_thesaurus = preferred_term.id_thesaurus AND"
                                + " preferred_term.id_term = term.id_term AND"
                                + " preferred_term.id_thesaurus = term.id_thesaurus AND"
                                + " concept.id_concept = '" + nodeConceptTree1.getIdConcept() + "' AND"
                                + " term.lang = '" + idLang + "' AND"
                                + " term.id_thesaurus = '" + idThesaurus + "';";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            resultSet.next();
                            if (resultSet.getRow() == 0) {
                                nodeConceptTree1.setTitle("");
                                nodeConceptTree1.setStatusConcept("");
                            } else {
                                nodeConceptTree1.setTitle(resultSet.getString("lexical_value"));
                                nodeConceptTree1.setStatusConcept(resultSet.getString("status"));
                            }
                            nodeConceptTree1.setHaveChildren(
                                    haveChildren(ds, idThesaurus, nodeConceptTree1.getIdConcept())
                            );
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
        Collections.sort(nodeConceptTree);
        return nodeConceptTree;
    }

    /**
     * Cette fonction permet de récupérer toutes les informations concernant un
     * Concept par son id et son thésaurus et la langue On récupère aussi les
     * IdArk si Ark est actif
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param isArkActive
     * @return Objet class NodeConcept
     */
    public NodeConceptExport getConceptForExport(HikariDataSource ds,
            String idConcept, String idThesaurus, boolean isArkActive) {

        NodeConceptExport nodeConceptExport = new NodeConceptExport();

        // récupération des BT
        RelationsHelper relationsHelper = new RelationsHelper();

        ArrayList<NodeUri> nodeListIdOfBT_Ark = getListIdArkOfConcept(ds,
                relationsHelper.getListIdBT(ds, idConcept, idThesaurus),
                idThesaurus);
        nodeConceptExport.setNodeListIdsOfBT(nodeListIdOfBT_Ark);

        //récupération du Concept
        Concept concept = getThisConcept(ds, idConcept, idThesaurus);

        /**
         * Attention si on passe par le null, ca veut dire qu'il y a une
         * incohérence dans la base à corriger !!!!!
         */
        if (concept == null) {
            return null;
        }
        nodeConceptExport.setConcept(concept);

        AlignmentHelper alignmentHelper = new AlignmentHelper();
        ArrayList<NodeAlignment> nodeAlignmentList = alignmentHelper.getAllAlignmentOfConcept(ds, idConcept, idThesaurus);
        nodeConceptExport.setNodeAlignmentsList(nodeAlignmentList);

        //récupération des termes spécifiques
        ArrayList<NodeUri> nodeListIdsOfNT_Ark = getListIdArkOfConcept(ds,
                relationsHelper.getListIdsOfNT(ds, idConcept, idThesaurus),
                idThesaurus);
        nodeConceptExport.setNodeListIdsOfNT(nodeListIdsOfNT_Ark);

        //récupération des termes associés
        ArrayList<NodeUri> nodeListIdsOfRT_Ark = getListIdArkOfConcept(ds,
                relationsHelper.getListIdsOfRT(ds, idConcept, idThesaurus),
                idThesaurus);
        nodeConceptExport.setNodeListIdsOfRT(nodeListIdsOfRT_Ark);

        //récupération des Non Prefered Term
        nodeConceptExport.setNodeEM(new TermHelper().getAllNonPreferredTerms(ds,
                new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus),
                idThesaurus));

        //récupération des traductions
        nodeConceptExport.setNodeTermTraductions(new TermHelper().getAllTraductionsOfConcept(ds, idConcept, idThesaurus));

        //récupération des Groupes ou domaines
        ArrayList<NodeUri> nodeListIdsOfConceptGroup_Ark = getListIdArkOfGroup(ds,
                new GroupHelper().getListIdGroupOfConcept(ds, idThesaurus, idConcept),
                idThesaurus);
        nodeConceptExport.setNodeListIdsOfConceptGroup(nodeListIdsOfConceptGroup_Ark);

        //récupération des notes du Terme
        String idTerm = new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus);
        nodeConceptExport.setNodeNoteTerm(new NoteHelper().getListNotesTermAllLang(ds, idTerm, idThesaurus));
        //récupération des Notes du Concept
        nodeConceptExport.setNodeNoteConcept(new NoteHelper().getListNotesConceptAllLang(ds, idConcept, idThesaurus));

        return nodeConceptExport;
    }

    public ArrayList<NodeFusion> getConceptFusion(HikariDataSource ds,
            String idConcept, String idLang, String idThesaurus) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<NodeFusion> nf = new ArrayList<>();
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_concept1, id_concept2 from concept_fusion where"
                            + " concept_fusion.id_thesaurus = '" + idThesaurus + "'"
                            + " AND (concept_fusion.id_concept1 = '" + idConcept + "'"
                            + " OR concept_fusion.id_concept2 = '" + idConcept + "')";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();

                    while (resultSet.next()) {
                        NodeFusion n = new NodeFusion();
                        n.setIdConcept1(resultSet.getString("id_concept1"));
                        n.setIdConcept2(resultSet.getString("id_concept2"));
                        n.setLexicalValue1(getLexicalValueOfConcept(ds, resultSet.getString("id_concept1"), idThesaurus, idLang));
                        n.setLexicalValue2(getLexicalValueOfConcept(ds, resultSet.getString("id_concept2"), idThesaurus, idLang));
                        nf.add(n);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting fusion of Concept : " + idConcept, sqle);
        }
        return nf;
    }

    /**
     * Cette fonction permet de récupérer les Id Ark d'une liste d'Identifiants
     * de Concepts et les rajouter dans le tableau de NodeUri
     *
     * @param nodeListIdOfConcept
     * @param idThesaurus
     * @return ArrayList<NodeUri>
     */
    private ArrayList<NodeUri> getListIdArkOfConcept(
            HikariDataSource ds,
            ArrayList<String> nodeListIdOfConcept,
            String idThesaurus) {

        ArrayList<NodeUri> nodeListIdOfConcept_idArk = new ArrayList<>();

        String idArk;
        for (String nodeListIdOfConcept1 : nodeListIdOfConcept) {
            idArk = getIdArkOfConcept(ds, nodeListIdOfConcept1, idThesaurus);
            NodeUri nodeUri = new NodeUri();
            if (idArk == null || idArk.trim().isEmpty()) {
                nodeUri.setIdArk("");
            } else {
                nodeUri.setIdArk(idArk);
            }
            nodeUri.setIdConcept(nodeListIdOfConcept1);
            nodeListIdOfConcept_idArk.add(nodeUri);
        }
        return nodeListIdOfConcept_idArk;
    }

    /**
     * Cette fonction permet de récupérer les Id Ark d'une liste d'Identifiants
     * de Groups et les rajouter dans le tableau de NodeUri
     *
     * @param nodeListIdOfGroup
     * @param idThesaurus
     * @return ArrayList<NodeUri>
     */
    private ArrayList<NodeUri> getListIdArkOfGroup(
            HikariDataSource ds,
            ArrayList<String> nodeListIdOfGroup,
            String idThesaurus) {

        ArrayList<NodeUri> nodeListIdOfGroup_idArk = new ArrayList<>();

        String idArk;
        for (String nodeListIdOfGroup1 : nodeListIdOfGroup) {
            idArk = new GroupHelper().getIdArkOfGroup(ds, nodeListIdOfGroup1, idThesaurus);
            NodeUri nodeUri = new NodeUri();
            if (idArk == null || idArk.trim().isEmpty()) {
                nodeUri.setIdArk("");
            } else {
                nodeUri.setIdArk(idArk);
            }
            nodeUri.setIdConcept(nodeListIdOfGroup1);
            nodeListIdOfGroup_idArk.add(nodeUri);
        }
        return nodeListIdOfGroup_idArk;
    }

    /**
     * Cette fonction permet de récupérer toutes les informations concernant un
     * ou plusieurs Concept par une chaîne de caractère, le thésaurus et la
     * langue
     *
     * @param ds
     * @param value
     * @param idThesaurus
     * @param idLang
     * @param isArkActif
     * @return Objet class NodeConcept
     */
    public ArrayList<NodeConceptExport> getMultiConceptForExport(HikariDataSource ds,
            String value, String idThesaurus, String idLang, boolean isArkActif) {

        ArrayList<NodeConceptExport> listNce = new ArrayList<>();

        //Récupération des concept
        ArrayList<NodeSearch> listRes = new SearchHelper().searchTerm(ds, value, idLang, idThesaurus, "", 1, false);
        for (NodeSearch ns : listRes) {
            Concept concept = getThisConcept(ds, ns.getIdConcept(), idThesaurus);
            NodeConceptExport nce = new NodeConceptExport();
            nce.setConcept(concept);
            listNce.add(nce);
        }

        for (NodeConceptExport nce : listNce) {
            String idConcept = nce.getConcept().getIdConcept();
            RelationsHelper relationsHelper = new RelationsHelper();

            // récupération des BT
            ArrayList<NodeUri> nodeListIdOfBT_Ark = getListIdArkOfConcept(ds,
                    relationsHelper.getListIdBT(ds, idConcept, idThesaurus),
                    idThesaurus);
            nce.setNodeListIdsOfBT(nodeListIdOfBT_Ark);

            //récupération des termes spécifiques
            ArrayList<NodeUri> nodeListIdOfNT_Ark = getListIdArkOfConcept(ds,
                    relationsHelper.getListIdsOfNT(ds, idConcept, idThesaurus),
                    idThesaurus);
            nce.setNodeListIdsOfNT(nodeListIdOfNT_Ark);

            //récupération des termes associés
            ArrayList<NodeUri> nodeListIdOfRT_Ark = getListIdArkOfConcept(ds,
                    relationsHelper.getListIdsOfRT(ds, idConcept, idThesaurus),
                    idThesaurus);
            nce.setNodeListIdsOfRT(nodeListIdOfRT_Ark);

            //récupération des Non Prefered Term
            nce.setNodeEM(new TermHelper().getAllNonPreferredTerms(ds, new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus), idThesaurus));

            //récupération des traductions
            nce.setNodeTermTraductions(new TermHelper().getAllTraductionsOfConcept(ds, idConcept, idThesaurus));

            //récupération des Groupes
            ArrayList<NodeUri> nodeListIdsOfConceptGroup_Ark = getListIdArkOfGroup(ds,
                    new GroupHelper().getListIdGroupOfConcept(ds, idThesaurus, idConcept),
                    idThesaurus);
            nce.setNodeListIdsOfConceptGroup(nodeListIdsOfConceptGroup_Ark);

            //récupération des notes du Terme
            String idTerm = new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus);
            nce.setNodeNoteTerm(new NoteHelper().getListNotesTermAllLang(ds, idTerm, idThesaurus));

            //récupération des Notes du Concept
            nce.setNodeNoteConcept(new NoteHelper().getListNotesConceptAllLang(ds, idConcept, idThesaurus));

            // récupération des Alignements
            nce.setNodeAlignmentsList(new AlignmentHelper().getAllAlignmentOfConcept(ds, idConcept, idThesaurus));
        }

        return listNce;
    }

    /**
     * Cette fonction permet de récupérer toutes les informations concernant un
     * ou plusieurs Concept par une chaîne de caractère, suivant le thésaurus,
     * la langue et le group
     *
     * @param ds
     * @param value
     * @param idThesaurus
     * @param idGroup
     * @param idLang
     * @param isArkActif
     * @return Objet class NodeConcept
     */
    public ArrayList<NodeConceptExport> getMultiConceptForExport(HikariDataSource ds,
            String value,
            String idLang,
            String idGroup,
            String idThesaurus,
            boolean isArkActif) {

        ArrayList<NodeConceptExport> listNce = new ArrayList<>();

        //Récupération des concept
        ArrayList<NodeSearch> listRes = new SearchHelper().searchTerm(ds, value, idLang, idThesaurus, idGroup, 1, false);
        for (NodeSearch ns : listRes) {
            Concept concept = getThisConcept(ds, ns.getIdConcept(), idThesaurus);
            NodeConceptExport nce = new NodeConceptExport();
            nce.setConcept(concept);
            listNce.add(nce);
        }

        for (NodeConceptExport nce : listNce) {
            String idConcept = nce.getConcept().getIdConcept();
            RelationsHelper relationsHelper = new RelationsHelper();

            // récupération des BT
            ArrayList<NodeUri> nodeListIdOfBT_Ark = getListIdArkOfConcept(ds,
                    relationsHelper.getListIdBT(ds, idConcept, idThesaurus),
                    idThesaurus);
            nce.setNodeListIdsOfBT(nodeListIdOfBT_Ark);

            //récupération des termes spécifiques
            ArrayList<NodeUri> nodeListIdOfNT_Ark = getListIdArkOfConcept(ds,
                    relationsHelper.getListIdsOfNT(ds, idConcept, idThesaurus),
                    idThesaurus);
            nce.setNodeListIdsOfNT(nodeListIdOfNT_Ark);

            //récupération des termes associés
            ArrayList<NodeUri> nodeListIdOfRT_Ark = getListIdArkOfConcept(ds,
                    relationsHelper.getListIdsOfRT(ds, idConcept, idThesaurus),
                    idThesaurus);
            nce.setNodeListIdsOfRT(nodeListIdOfRT_Ark);

            //récupération des Non Prefered Term
            nce.setNodeEM(new TermHelper().getAllNonPreferredTerms(ds, new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus), idThesaurus));

            //récupération des traductions
            nce.setNodeTermTraductions(new TermHelper().getAllTraductionsOfConcept(ds, idConcept, idThesaurus));

            //récupération des Groupes
            ArrayList<NodeUri> nodeListIdsOfConceptGroup_Ark = getListIdArkOfGroup(ds,
                    new GroupHelper().getListIdGroupOfConcept(ds, idThesaurus, idConcept),
                    idThesaurus);
            nce.setNodeListIdsOfConceptGroup(nodeListIdsOfConceptGroup_Ark);

            //récupération des notes du Terme
            String idTerm = new TermHelper().getIdTermOfConcept(ds, idConcept, idThesaurus);
            nce.setNodeNoteTerm(new NoteHelper().getListNotesTermAllLang(ds, idTerm, idThesaurus));

            //récupération des Notes du Concept
            nce.setNodeNoteConcept(new NoteHelper().getListNotesConceptAllLang(ds, idConcept, idThesaurus));

            // récupération des Alignements
            nce.setNodeAlignmentsList(new AlignmentHelper().getAllAlignmentOfConcept(ds, idConcept, idThesaurus));
        }

        return listNce;
    }

    /**
     * Cette fonction permet de récupérer toutes les informations concernant un
     * Concept par son id et son thésaurus et la langue
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param idLang
     * @return Objet class NodeConcept
     */
    public NodeConcept getConcept(HikariDataSource ds,
            String idConcept, String idThesaurus, String idLang) {
        NodeConcept nodeConcept = new NodeConcept();

        // récupération des BT
        RelationsHelper relationsHelper = new RelationsHelper();
        ArrayList<NodeBT> nodeListBT = relationsHelper.getListBT(ds, idConcept, idThesaurus, idLang);
        nodeConcept.setNodeBT(nodeListBT);

        //récupération du Concept
        Concept concept = getThisConcept(ds, idConcept, idThesaurus);
        nodeConcept.setConcept(concept);

        //récupération du Terme
        TermHelper termHelper = new TermHelper();
        Term term = termHelper.getThisTerm(ds, idConcept, idThesaurus, idLang);
        nodeConcept.setTerm(term);

        //récupération des termes spécifiques
        nodeConcept.setNodeNT(relationsHelper.getListNT(ds, idConcept, idThesaurus, idLang));

        //récupération des termes associés
        nodeConcept.setNodeRT(relationsHelper.getListRT(ds, idConcept, idThesaurus, idLang));

        //récupération des Non Prefered Term
        nodeConcept.setNodeEM(termHelper.getNonPreferredTerms(ds, term.getId_term(), idThesaurus, idLang));

        //récupération des traductions
        nodeConcept.setNodeTermTraductions(termHelper.getTraductionsOfConcept(ds, idConcept, idThesaurus, idLang));

        NoteHelper noteHelper = new NoteHelper();

        //récupération des notes du Concept
        nodeConcept.setNodeNotesConcept(noteHelper.getListNotesConcept(
                ds, idConcept, idThesaurus, idLang));
        //récupération des notes du term        
        nodeConcept.setNodeNotesTerm(noteHelper.getListNotesTerm(ds, term.getId_term(),
                idThesaurus, idLang));

        return nodeConcept;
    }

    /**
     * Cette fonction permet de retourner l'id du Concept d'après un idTerm
     *
     * @param ds
     * @param idTerm
     * @param idThesaurus
     * @return idConcept
     */
    public String getIdConceptOfTerm(HikariDataSource ds,
            String idTerm, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idConcept = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_concept FROM"
                            + " preferred_term WHERE"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_term = '" + idTerm + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        idConcept = resultSet.getString("id_concept");
                    } else {
                        return null;
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting idConcept of idTerm : " + idConcept, sqle);
        }
        return idConcept;
    }

    /**
     * Cette fonction permet de savoir si un concept a des fils ou non suivant
     * l'id du Concept et l'id du thésaurus sous forme de classe Concept (sans
     * les relations)
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return Objet class NodeConceptTree
     */
    public boolean haveChildren(HikariDataSource ds,
            String idThesaurus, String idConcept) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean children = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select count(*)  from hierarchical_relationship"
                            + " where id_thesaurus='" + idThesaurus + "'"
                            + " and id_concept1='" + idConcept + "'"
                            + " and role='NT'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        if (resultSet.getInt(1) != 0) {
                            children = true;
                        } else {
                            children = false;
                        }
                        resultSet.close();
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while testing if haveChildren of Concept : " + idConcept, sqle);
        }
        return children;
    }

    /**
     * Focntion récursive pour trouver le chemin complet d'un concept en partant
     * du Concept lui même pour arriver à la tête on peut rencontrer plusieurs
     * têtes en remontant, alors on construit à chaque fois un chemin complet.
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param firstPath
     * @param path
     * @param tabId
     * @return Vector Ce vecteur contient tous les Path des BT d'un id_terme
     * exemple (327,368,100,#,2251,5555,54544,8789,#) ici deux path disponible
     * il faut trouver le path qui correspond au microthesaurus en cours pour
     * l'afficher en premier
     */
    public ArrayList<ArrayList<String>> getInvertPathOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus,
            ArrayList<String> firstPath,
            ArrayList<String> path,
            ArrayList<ArrayList<String>> tabId) {

        RelationsHelper relationsHelper = new RelationsHelper();

        ArrayList<String> resultat = relationsHelper.getListIdBT(ds, idConcept, idThesaurus);
        if (resultat.size() > 1) {
            for (String path1 : path) {
                firstPath.add(path1);
            }
        }
        if (resultat.isEmpty()) {
            path.add(getGroupIdOfConcept(ds, idConcept, idThesaurus));
            ArrayList<String> pathTemp = new ArrayList<>();
            for (String path2 : firstPath) {
                pathTemp.add(path2);
            }
            for (String path1 : path) {
                if (pathTemp.indexOf(path1) == -1) {
                    pathTemp.add(path1);
                }
            }
            tabId.add(pathTemp);
            path.clear();
        }

        for (String resultat1 : resultat) {
            path.add(resultat1);
            getInvertPathOfConcept(ds, resultat1, idThesaurus, firstPath, path, tabId);
        }

        return tabId;
    }

    public ArrayList<ArrayList<String>> getPathOfConcept(HikariDataSource ds,
            String idConcept, String idThesaurus, ArrayList<String> path, ArrayList<ArrayList<String>> tabId) {

        ArrayList<String> fistPath = new ArrayList<>();
        ArrayList<ArrayList<String>> tabIdInvert = getInvertPathOfConcept(ds, idConcept,
                idThesaurus,
                fistPath,
                path, tabId);

        for (int i = 0; i < tabIdInvert.size(); i++) {
            ArrayList<String> pathTemp = new ArrayList<>();
            for (int j = tabIdInvert.get(i).size(); j > 0; j--) {
                pathTemp.add(tabIdInvert.get(i).get(j - 1));
            }
            tabIdInvert.remove(i);
            tabIdInvert.add(i, pathTemp);
        }
        return tabIdInvert;
    }

    public void updateGroupOfConcept(HikariDataSource ds, String idConcept, String idNewDomaine, String idOldDomaine, String idTheso) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE concept "
                            + "set id_group='" + idNewDomaine + "',"
                            + " modified = current_date"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idTheso + "'"
                            + " AND id_group ='" + idOldDomaine + "'";

                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating group of concept : " + idConcept, sqle);
        }
    }

    /**
     * Cette fonction permet d'ajouter un Ark Id au concept ou remplacer l'Id
     * existant
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @param idArk
     */
    public void updateArkIdOfConcept(HikariDataSource ds, String idConcept,
            String idTheso, String idArk) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE concept "
                            + "set id_ark='" + idArk + "'"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idTheso + "'";

                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating or adding ArkId of Concept : " + idConcept, sqle);
        }
    }

    /**
     * Cette fonction permet de modifier le status d'un concept
     *
     * @param ds
     * @param idConcept
     * @param idTheso
     * @param status
     */
    private boolean updateStatusConcept(HikariDataSource ds, String idConcept,
            String idTheso, String status) {
        Connection conn;
        Statement stmt;
        boolean res = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE concept "
                            + "set status='" + status + "'"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idTheso + "'";

                    stmt.executeUpdate(query);
                    res = true;
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating status of Concept : " + idConcept, sqle);
        }
        return res;
    }

    /**
     * Cette fonction permet d'ajouter un Ark Id au concept ou remplacer l'Id
     * existant
     *
     * @param conn
     * @param idConcept
     * @param idTheso
     * @param idArk
     * @return
     */
    public boolean updateArkIdOfConcept(Connection conn, String idConcept,
            String idTheso, String idArk) {

        Statement stmt;
        boolean status = false;
        try {

            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE concept "
                            + "set id_ark='" + idArk + "'"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idTheso + "'";

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
            log.error("Error while updating or adding ArkId of Concept : " + idConcept, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de mettre à jour la notation pour un concept
     *
     * @param conn
     * @param idConcept
     * @param idTheso
     * @param notation
     * @return
     */
    public boolean updateNotation(Connection conn, String idConcept,
            String idTheso, String notation) {

        Statement stmt;
        boolean status = false;
        try {

            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE concept "
                            + "set notation ='" + notation + "'"
                            + " WHERE id_concept ='" + idConcept + "'"
                            + " AND id_thesaurus='" + idTheso + "'";

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
            log.error("Error while updating or adding ArkId of Concept : " + idConcept, sqle);
        }
        return status;
    }

    public boolean haveThisGroup(HikariDataSource ds, String idConcept, String idDomaine, String idTheso) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean group = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_concept FROM concept"
                            + " WHERE id_thesaurus='" + idTheso + "'"
                            + " AND id_concept='" + idConcept + "'"
                            + " AND id_group='" + idDomaine + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    group = (resultSet.getRow() != 0);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while testing if haveChildren of Concept : " + idConcept, sqle);
        }
        return group;
    }
    public String getPereConcept(HikariDataSource ds, String id_theso, String id_concept)
    {
        String conceptPere="";
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_concept2 FROM hierarchical_relationship"
                            + " WHERE id_thesaurus='" + id_theso + "'"
                            + " AND id_concept1='" + id_concept + "'"
                            + " AND role ='BT'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if(resultSet.next())
                     conceptPere = resultSet.getString("id_concept2");
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while get le pere du concept : " + id_concept, sqle);
        }
        return conceptPere;
    }
}
