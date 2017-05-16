/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import fr.mom.arkeo.soap.DcElement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mom.trd.opentheso.bdd.datas.ConceptGroup;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupTraductions;
import mom.trd.opentheso.bdd.tools.FileUtilities;
import mom.trd.opentheso.bdd.tools.StringPlus;
import mom.trd.opentheso.ws.ark.Ark_Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author miled.rousset
 */
public class GroupHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public GroupHelper() {
    }

    public void addSubGroup(HikariDataSource ds,
            String fatherNodeID, String childNodeID, String idThesaurus) {

        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            String relation = "sub";
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into relation_group "
                            + "(id_group1, id_thesaurus, relation, id_group2)"
                            + "values ("
                            + "'" + fatherNodeID + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'" + relation + "'"
                            + ",'" + childNodeID + "'"
                            + ")";

                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding relation : " + sqle);
        }

    }

    public void addConceptGroupConcept(HikariDataSource ds,
            String groupID, String conceptID, String idThesaurus) {

        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into concept_group_concept "
                            + "(idgroup, idthesaurus, idconcept)"
                            + "values ("
                            + "'" + groupID + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'" + conceptID + "'"
                            + ")";

                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while addConceptGroupConcept : " + sqle);
        }

    }

    public ArrayList<String> getListGroupChildIdOfGroup(HikariDataSource ds,
            String idGRoup, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList<String> idGroupParentt = new ArrayList<>();
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_group2 from relation_group where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_group1 = '" + idGRoup + "'"
                            + " and relation='sub'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            idGroupParentt.add(resultSet.getString("id_group2"));
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
            log.error("Error while getting Id of group of Concept : " + idGRoup, sqle);
        }
        return idGroupParentt;
    }

    /**
     * Fonction qui permet de supprimer un domaine de la branche donnée avec un
     * concept de tête un domaine et thesaurus
     *
     * @param conn
     * @param lisIds
     * @param idGroup
     * @param idTheso
     * @return
     */
    public boolean deleteAllDomainOfBranch(Connection conn,
            ArrayList<String> lisIds, // identifiants des concepts
            String idGroup, String idTheso) {

        RelationsHelper relationsHelper = new RelationsHelper();
        for (String id : lisIds) {
            if (!relationsHelper.deleteRelationMT(conn, id, idGroup, idTheso)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fonction qui permet de supprimer un domaine de la branche donnée avec un
     * concept de tête un domaine et thesaurus
     *
     * @param conn
     * @param lisIds
     * @param idGroup
     * @param idTheso
     * @return
     */
    public boolean setDomainToBranch(Connection conn,
            ArrayList<String> lisIds, // identifiants des concepts
            String idGroup, String idTheso) {

        RelationsHelper relationsHelper = new RelationsHelper();

        for (String id : lisIds) {
            if (!relationsHelper.setRelationMT(conn, id, idGroup, idTheso)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fonction qui permet de supprimer un domaine de la branche donnée avec un
     * concept de tête un domaine et thesaurus
     *
     * @param conn
     * @param lisIds
     * @param idGroup
     * @param idTheso
     * @param idUser
     * @return
     */
    public boolean addDomainToBranch(Connection conn,
            ArrayList<String> lisIds, // identifiants des concepts
            String idGroup, String idTheso, int idUser) {

        RelationsHelper relationsHelper = new RelationsHelper();

        for (String id : lisIds) {
            if (!relationsHelper.addRelationMT(conn, id, idTheso, idGroup, idUser)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cette fonction permet d'ajouter un group (MT, domaine etc..) avec le
     * libellé
     *
     * @param ds
     * @param nodeConceptGroup
     * @param urlSite
     * @param isArkActive
     * @param idUser
     * @return
     */
    public String addGroup(HikariDataSource ds,
            NodeGroup nodeConceptGroup,
            String urlSite, boolean isArkActive, int idUser) {

        String idConceptGroup = "";//"ark:/66666/srvq9a5Ll41sk";
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        nodeConceptGroup.setLexicalValue(
                new StringPlus().convertString(nodeConceptGroup.getLexicalValue()));

        /*
         * récupération de l'identifiant Ark pour le ConceptGroup
         * de type : ark:/66666/srvq9a5Ll41sk
         */
        /**
         * Controler si l'identifiant du Group existe
         */
        // à faire
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select max(id) from concept_group";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    int idNumeriqueGroup = resultSet.getInt(1);
                    idConceptGroup = "MT_" + (++idNumeriqueGroup);

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
                                urlSite + "?idg=" + idConceptGroup + "&idt=" + nodeConceptGroup.getConceptGroup().getIdthesaurus(),
                                "", "", dcElementsList, "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
                    }
                    /**
                     * Ajout des informations dans la table de ConceptGroup
                     */
                    query = "Insert into concept_group values ("
                            + "'" + idConceptGroup + "'"
                            + ",'" + idArk + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getIdthesaurus() + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getIdtypecode() + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getNotation() + "'"
                            + ")";
                                                                                
                    stmt.executeUpdate(query);

                    ConceptGroupLabel conceptGroupLabel = new ConceptGroupLabel();
                    conceptGroupLabel.setIdgroup(idConceptGroup);
                    conceptGroupLabel.setIdthesaurus(nodeConceptGroup.getConceptGroup().getIdthesaurus());
                    conceptGroupLabel.setLang(nodeConceptGroup.getIdLang());
                    conceptGroupLabel.setLexicalvalue(nodeConceptGroup.getLexicalValue());
                    addGroupTraduction(ds, conceptGroupLabel, idUser);
                    addGroupHistorique(ds, nodeConceptGroup, urlSite, idArk, idUser, idConceptGroup);
                    
                    
                    
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle){
            // Log exception
            log.error("Error while adding ConceptGroup : " + idConceptGroup, sqle);
        }
        return idConceptGroup;
    }

    /**
     * Cette fonction permet d'ajouter un group (MT, domaine etc..) avec le
     * libellé dans l'historique
     *
     * @param ds
     * @param nodeConceptGroup
     * @param urlSite
     * @param idArk
     * @param idUser
     * @param idConceptGroup
     * @return
     */
    public int addGroupHistorique(HikariDataSource ds,
            NodeGroup nodeConceptGroup,
            String urlSite, String idArk, int idUser, String idConceptGroup) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        nodeConceptGroup.setLexicalValue(
                new StringPlus().convertString(nodeConceptGroup.getLexicalValue()));
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into concept_group_historique "
                            + "(idgroup, id_ark, idthesaurus, idtypecode, idparentgroup, notation, idconcept, id_user)"
                            + "values ("
                            + "'" + idConceptGroup + "'"
                            + ",'" + idArk + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getIdthesaurus() + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getIdtypecode() + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getIdparentgroup() + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getNotation() + "'"
                            + ",'" + nodeConceptGroup.getConceptGroup().getIdconcept() + "'"
                            + ",'" + idUser + "'" + ")";

                    stmt.executeUpdate(query);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding ConceptGroup : " + nodeConceptGroup.getConceptGroup().getId(), sqle);
        }
        return nodeConceptGroup.getConceptGroup().getId();
    }

    /**
     * Cette fonction permet de récupérer l'historique d'un groupe
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeGroup> getGroupHistoriqueAll(HikariDataSource ds,
            String idConcept, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ArrayList<NodeGroup> nodeGroupList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "select idgroup, id_ark, idtypecode, idparentgroup, notation, modified, username from concept_group_historique, users "
                            + "where idconcept = '" + idConcept + "'"
                            + " and idthesaurus = '" + idThesaurus + "'"
                            + " and concept_group_historique.id_user=users.id_user"
                            + " order by modified DESC";

                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet != null) {
                        nodeGroupList = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeGroup nodeGroup = new NodeGroup();
                            nodeGroup.setIdUser(resultSet.getString("username"));
                            nodeGroup.setModified(resultSet.getDate("modified"));
                            nodeGroup.getConceptGroup().setId(resultSet.getInt("idgroup"));
                            nodeGroup.getConceptGroup().setIdARk(resultSet.getString("id_ark"));
                            nodeGroup.getConceptGroup().setIdthesaurus(idThesaurus);
                            nodeGroup.getConceptGroup().setIdtypecode(resultSet.getString("idtypecode"));
                            nodeGroup.getConceptGroup().setIdparentgroup(resultSet.getString("idparentgroup"));
                            nodeGroup.getConceptGroup().setNotation(resultSet.getString("notation"));
                            nodeGroup.getConceptGroup().setIdconcept(resultSet.getString("idconcept"));
                            nodeGroupList.add(nodeGroup);
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
            log.error("Error while getting All historique group of concept : " + idConcept, sqle);
        }
        return nodeGroupList;
    }

    /**
     * Cette fonction permet de récupérer l'historique d'un groupe à une date
     * précise
     *
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @param date
     * @return
     */
    public ArrayList<NodeGroup> getGroupHistoriqueFromDate(HikariDataSource ds,
            String idConcept, String idThesaurus, Date date) {

        Connection conn;
        Statement stmt;
        ArrayList<NodeGroup> nodeGroupList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "select idgroup, id_ark, idtypecode, idparentgroup, notation, modified, username from concept_group_historique, users "
                            + "where idconcept = '" + idConcept + "'"
                            + " and idthesaurus = '" + idThesaurus + "'"
                            + " and concept_group_historique.id_user=users.id_user"
                            + " and modified <= '" + date.toString()
                            + "' order by modified DESC";

                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet != null) {
                        nodeGroupList = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeGroup nodeGroup = new NodeGroup();
                            nodeGroup.setIdUser(resultSet.getString("username"));
                            nodeGroup.setModified(resultSet.getDate("modified"));
                            nodeGroup.getConceptGroup().setId(resultSet.getInt("idgroup"));
                            nodeGroup.getConceptGroup().setIdARk(resultSet.getString("id_ark"));
                            nodeGroup.getConceptGroup().setIdthesaurus(idThesaurus);
                            nodeGroup.getConceptGroup().setIdtypecode(resultSet.getString("idtypecode"));
                            nodeGroup.getConceptGroup().setIdparentgroup(resultSet.getString("idparentgroup"));
                            nodeGroup.getConceptGroup().setNotation(resultSet.getString("notation"));
                            nodeGroup.getConceptGroup().setIdconcept(resultSet.getString("idconcept"));
                            nodeGroupList.add(nodeGroup);
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
            log.error("Error while getting date historique group of concept : " + idConcept, sqle);
        }
        return nodeGroupList;
    }

    /**
     * Cette fonction permet d'ajouter un group (MT, domaine etc..) avec le
     * libellé dans le cas d'un import avec idGroup existant
     *
     * @param ds
     * @param idThesaurus
     * @param typeCode
     * @param idGroup
     * @param notation
     * @param urlSite
     * @param isArkActive
     * @param idUser
     * @return
     */
    public boolean insertGroup(HikariDataSource ds,
            String idGroup, String idThesaurus,
            String typeCode,
            String notation,
            String urlSite, boolean isArkActive,
            int idUser) {

        //     idGroup = "MT_" + idGroup;//"ark:/66666/srvq9a5Ll41sk";
        Connection conn;
        Statement stmt;
        boolean status = false;

        /*
         * récupération de l'identifiant Ark pour le ConceptGroup
         * de type : ark:/66666/srvq9a5Ll41sk
         */
        /**
         * Controler si l'identifiant du Group existe
         */
        // à faire
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String idArk = "";

                    if (isArkActive) {
                        ArrayList<DcElement> dcElementsList = new ArrayList<>();
                        Ark_Client ark_Client = new Ark_Client();
                        idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                urlSite + "?idc=" + idGroup + "&idt=" + idThesaurus,
                                "", "", dcElementsList, "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
                    }

                    /**
                     * Ajout des informations dans la table de ConceptGroup
                     */
                    String query = "Insert into concept_group values ("
                            + "'" + idGroup + "'"
                            + ",'" + idArk + "'"
                            + ",'" + idThesaurus + "'"
                            + ",'" + typeCode + "'"
                            + ",'" + notation + "'"
                            + ")";

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
            log.error("Error while adding ConceptGroup : " + idGroup, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de retourner les traductions d'un domaine sans
     * celle qui est en cours
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @param idLang
     * @return
     */
    public ArrayList<NodeGroupTraductions> getGroupTraduction(HikariDataSource ds,
            String idGroup, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ArrayList<NodeGroupTraductions> nodeGroupTraductionsList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "select lang, lexicalvalue from concept_group_label "
                            + "where idgroup = '" + idGroup + "'"
                            + " and idthesaurus = '" + idThesaurus + "'"
                            + " and lang != '" + idLang + "'";

                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet != null) {
                        nodeGroupTraductionsList = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeGroupTraductions nodeGroupTraductions = new NodeGroupTraductions();
                            // cas du Group non traduit
                            nodeGroupTraductions.setIdLang(resultSet.getString("lang"));
                            nodeGroupTraductions.setTitle(resultSet.getString("lexicalvalue"));
                            nodeGroupTraductionsList.add(nodeGroupTraductions);
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
            log.error("Error while getting traduction of Group : " + idGroup, sqle);
        }
        return nodeGroupTraductionsList;
    }

    /**
     * Cette fonction permet de retourner les traductions d'un domaine
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeGroupTraductions> getAllGroupTraduction(HikariDataSource ds,
            String idGroup, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ArrayList<NodeGroupTraductions> nodeGroupTraductionsList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "select lang, lexicalvalue, created, modified from concept_group_label "
                            + "where idgroup = '" + idGroup + "'"
                            + " and idthesaurus = '" + idThesaurus + "'";

                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet != null) {
                        nodeGroupTraductionsList = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeGroupTraductions nodeGroupTraductions = new NodeGroupTraductions();
                            // cas du Group non traduit
                            nodeGroupTraductions.setIdLang(resultSet.getString("lang"));
                            nodeGroupTraductions.setTitle(resultSet.getString("lexicalvalue"));
                            nodeGroupTraductions.setCreated(resultSet.getDate("created"));
                            nodeGroupTraductions.setModified(resultSet.getDate("modified"));
                            nodeGroupTraductionsList.add(nodeGroupTraductions);
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
            log.error("Error while getting All traduction of Group : " + idGroup, sqle);
        }
        return nodeGroupTraductionsList;
    }

    /**
     * Cette fonction permet de rajouter une traduction de domaine
     *
     * @param ds
     * @param conceptGroupLabel
     * @param idUser
     * @return
     */
    public boolean addGroupTraduction(HikariDataSource ds,
            ConceptGroupLabel conceptGroupLabel, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        conceptGroupLabel.setLexicalvalue(
                new StringPlus().convertString(conceptGroupLabel.getLexicalvalue()));
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "Insert into concept_group_label "
                            + "(lexicalvalue, created, modified,lang, idthesaurus, idgroup)"
                            + "values ("
                            + "'" + conceptGroupLabel.getLexicalvalue() + "'"
                            + ",current_date"
                            + ",current_date"
                            + ",'" + conceptGroupLabel.getLang() + "'"
                            + ",'" + conceptGroupLabel.getIdthesaurus() + "'"
                            + ",'" + conceptGroupLabel.getIdgroup() + "'" + ")";

                    stmt.executeUpdate(query);
                    status = true;
                    addGroupTraductionHistorique(ds, conceptGroupLabel, idUser);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding traduction to ConceptGroupLabel : " + conceptGroupLabel.getIdgroup(), sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de rajouter une traduction de domaine en historique
     *
     * @param ds
     * @param conceptGroupLabel
     * @param idUser
     * @return
     */
    public boolean addGroupTraductionHistorique(HikariDataSource ds,
            ConceptGroupLabel conceptGroupLabel, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        conceptGroupLabel.setLexicalvalue(
                new StringPlus().convertString(conceptGroupLabel.getLexicalvalue()));
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "Insert into concept_group_label_historique "
                            + "(lexicalvalue,lang, idthesaurus, idgroup, id_user)"
                            + "values ("
                            + "'" + conceptGroupLabel.getLexicalvalue() + "'"
                            + ",'" + conceptGroupLabel.getLang() + "'"
                            + ",'" + conceptGroupLabel.getIdthesaurus() + "'"
                            + ",'" + conceptGroupLabel.getIdgroup() + "'"
                            + ",'" + idUser + "'" + ")";

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
            log.error("Error while adding traduction to ConceptGroupLabel : " + conceptGroupLabel.getIdgroup(), sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de récupérer l'historique des traductions d'un
     * groupe
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @param lang
     * @return
     */
    public ArrayList<NodeGroup> getGroupTraductionsHistoriqueAll(HikariDataSource ds,
            String idGroup, String idThesaurus, String lang) {

        Connection conn;
        Statement stmt;
        ArrayList<NodeGroup> nodeGroupList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "select lexicalvalue, modified, idgroup, username from concept_group_label_historique, users "
                            + "where id = '" + idGroup + "'"
                            + " and lang = '" + lang + "'"
                            + " and idthesaurus = '" + idThesaurus + "'"
                            + " and concept_group_label_historique.id_user=users.id_user"
                            + "' order by modified DESC";

                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet != null) {
                        nodeGroupList = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeGroup nodeGroup = new NodeGroup();
                            nodeGroup.getConceptGroup().setIdgroup(idGroup);
                            nodeGroup.setIdUser(resultSet.getString("username"));
                            nodeGroup.setModified(resultSet.getDate("modified"));
                            nodeGroup.getConceptGroup().setId(resultSet.getInt("idgroup"));
                            nodeGroup.getConceptGroup().setIdthesaurus(idThesaurus);
                            nodeGroup.setLexicalValue(resultSet.getString("lexicalvalue"));
                            nodeGroup.setIdLang(lang);
                            nodeGroupList.add(nodeGroup);
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
            log.error("Error while getting All traductions historique of group : " + idGroup, sqle);
        }
        return nodeGroupList;
    }

    /**
     * Cette fonction permet de récupérer l'historique des traductions d'un
     * groupe à une date précise
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @param lang
     * @return
     */
    public ArrayList<NodeGroup> getGroupTraductionsHistoriqueFromDate(HikariDataSource ds,
            String idGroup, String idThesaurus, String lang, Date date) {

        Connection conn;
        Statement stmt;
        ArrayList<NodeGroup> nodeGroupList = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {

                    String query = "select lexicalvalue, modified, idgroup, username from concept_group_label_historique, users "
                            + "where id = '" + idGroup + "'"
                            + " and lang = '" + lang + "'"
                            + " and idthesaurus = '" + idThesaurus + "'"
                            + " and concept_group_label_historique.id_user=users.id_user"
                            + " and modified <= '" + date.toString()
                            + "' order by modified DESC";

                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet != null) {
                        nodeGroupList = new ArrayList<>();
                        while (resultSet.next()) {
                            NodeGroup nodeGroup = new NodeGroup();
                            nodeGroup.getConceptGroup().setIdgroup(idGroup);
                            nodeGroup.setIdUser(resultSet.getString("username"));
                            nodeGroup.setModified(resultSet.getDate("modified"));
                            nodeGroup.getConceptGroup().setId(resultSet.getInt("idgroup"));
                            nodeGroup.getConceptGroup().setIdthesaurus(idThesaurus);
                            nodeGroup.setLexicalValue(resultSet.getString("lexicalvalue"));
                            nodeGroup.setIdLang(lang);
                            nodeGroupList.add(nodeGroup);
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
            log.error("Error while getting date traductions historique of group : " + idGroup, sqle);
        }
        return nodeGroupList;
    }

    public ArrayList<NodeConceptTree> getRelationGroupOf(HikariDataSource ds,
            String idConceptGroup, String idThesaurus, String idLang) {
        ArrayList<NodeConceptTree> nodeConceptTrees = null;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        /*
        Select * from concept_group_label where lang = 'fr' and idgroup = ( select id_group2 
						from relation_group 
						where relation ='sub' and id_group1='COL001' and id_thesaurus='8PWENn6esK'
						)
         */

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT * FROM concept_group_label WHERE "
                            + "lang = '" + idLang + "' and "
                            + "idthesaurus = '" + idThesaurus + "' and "
                            + "idgroup IN ("
                            + "SELECT id_group2 FROM relation_group WHERE "
                            + "relation = 'sub' and "
                            + "id_group1 = '" + idConceptGroup + "' and "
                            + "id_thesaurus = '" + idThesaurus + "' "
                            + ")";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodeConceptTrees = new ArrayList<NodeConceptTree>();
                    while (resultSet.next()) {
                        NodeConceptTree nodeConceptTree = new NodeConceptTree();
                        nodeConceptTree.setIdConcept(resultSet.getString("idgroup"));
                        nodeConceptTree.setIdLang(idLang);
                        nodeConceptTree.setIdThesaurus(idThesaurus);
                        nodeConceptTree.setTitle(resultSet.getString("lexicalvalue"));
                        nodeConceptTree.setStatusConcept("");
                        nodeConceptTree.setHaveChildren(true);
                        nodeConceptTree.setIsGroup(true);
                        nodeConceptTrees.add(nodeConceptTree);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while  getChildrenOf: " + idThesaurus, sqle);
        }

        return nodeConceptTrees;
    }

    public String getIdFather(HikariDataSource ds,
            String idGRoup, String idThesaurus) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String idFather = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_group1 from relation_group where id_thesaurus = '"
                            + idThesaurus + "'"
                            + " and id_group2 = '" + idGRoup + "'"
                            + " and relation='sub'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        idFather = resultSet.getString("id_group1");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while  getChildrenOf: " + idThesaurus, sqle);
        }

        return idFather;
    }

    /**
     * Permet de retourner un NodeConceptGroup par identifiant, par thésaurus et
     * par langue / ou null si rien cette fonction ne retourne pas les détails
     * et les traductions
     *
     * @param ds le pool de connexion
     * @param idConceptGroup
     * @param idThesaurus
     * @param idLang
     * @return Objet Class NodeConceptGroup
     */
    public NodeGroup getThisConceptGroup(HikariDataSource ds,
            String idConceptGroup, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        NodeGroup nodeConceptGroup = null;
        ConceptGroup conceptGroup = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT * from concept_group where "
                            + " idgroup = '" + idConceptGroup + "'"
                            + " and idthesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        resultSet.next();
                        if (resultSet.getRow() != 0) {
                            conceptGroup = new ConceptGroup();
                            conceptGroup.setIdgroup(idConceptGroup);
                            conceptGroup.setIdthesaurus(idThesaurus);
                            conceptGroup.setIdARk(resultSet.getString("id_ark"));
                            conceptGroup.setIdtypecode(resultSet.getString("idtypecode"));
                            conceptGroup.setNotation(resultSet.getString("notation"));
                        }
                    }
                    if (conceptGroup != null) {
                        query = "SELECT * FROM concept_group_label WHERE"
                                + " idgroup = '" + conceptGroup.getIdgroup() + "'"
                                + " AND idthesaurus = '" + idThesaurus + "'"
                                + " AND lang = '" + idLang + "'";

                        stmt.executeQuery(query);
                        resultSet = stmt.getResultSet();
                        if (resultSet != null) {
                            nodeConceptGroup = new NodeGroup();
                            resultSet.next();
                            if (resultSet.getRow() == 0) {
                                // cas du Group non traduit
                                nodeConceptGroup.setLexicalValue("");
                                nodeConceptGroup.setIdLang(idLang);

                            } else {
                                nodeConceptGroup.setLexicalValue(resultSet.getString("lexicalvalue"));
                                nodeConceptGroup.setIdLang(idLang);
                                nodeConceptGroup.setCreated(resultSet.getDate("created"));
                                nodeConceptGroup.setModified(resultSet.getDate("modified"));
                            }
                            nodeConceptGroup.setConceptGroup(conceptGroup);
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
            log.error("Error while adding element : " + idThesaurus, sqle);
        }
        return nodeConceptGroup;
    }

    public ArrayList<NodeGroup> getThisConceptGroup2(HikariDataSource ds,
            String idConceptGroup, String idThesaurus, String idLang, ArrayList<NodeGroup> nodeConceptGroupList) throws SQLException {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        NodeGroup nodeConceptGroup = null;
        ConceptGroup conceptGroup = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT * from permuted where "
                            + " id_group = '" + idConceptGroup + "'"
                            + "  and id_thesaurus = '" + idThesaurus + "'";
                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
                        nodeConceptGroup = new NodeGroup();
                        int orden = resultSet.getInt(1);
                        nodeConceptGroup.setOrde(orden);
                        nodeConceptGroup.setId_concept(resultSet.getString("id_concept"));
                        nodeConceptGroup.setId_group(idConceptGroup);
                        nodeConceptGroup.setId_theso(idThesaurus);
                        nodeConceptGroup.setIdLang(resultSet.getString("id_lang"));
                        nodeConceptGroup.setLexicalValue(resultSet.getString("lexical_value"));
                        nodeConceptGroup.setIspreferredterm(resultSet.getBoolean("ispreferredterm"));
                        nodeConceptGroup.setOriginal_value(resultSet.getString("original_value"));
                        nodeConceptGroupList.add(nodeConceptGroup);
                    }
                    nodeConceptGroup.setConceptGroup(conceptGroup);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while adding element : " + idThesaurus, sqle);
        }
        return nodeConceptGroupList;
    }

    /**
     * Permet de retourner un NodeGroupLable des Labels d'un Group / idThesaurus
     * Ce qui représente un domaine d'un thésaurus avec toutes les traductions
     *
     * @param ds le pool de connexion
     * @param idConceptGroup
     * @param idThesaurus
     * @return Objet ArrayLis ConceptGroupLabel
     */
    public NodeGroupLabel getNodeGroupLabel(HikariDataSource ds,
            String idConceptGroup, String idThesaurus) {

        NodeGroupLabel nodeGroupLabel = new NodeGroupLabel();

        nodeGroupLabel.setIdGroup(idConceptGroup);
        nodeGroupLabel.setIdThesaurus(idThesaurus);

        nodeGroupLabel.setNodeGroupTraductionses(getAllGroupTraduction(ds, idConceptGroup, idThesaurus));

        return nodeGroupLabel;
    }

    /**
     * Permet de retourner le lexical Value of Group
     *
     * @param ds le pool de connexion
     * @param idConceptGroup
     * @param idThesaurus
     * @param idLang
     * @return Objet Class NodeConceptGroup
     */
    public String getLexicalValueOfGroup(HikariDataSource ds,
            String idConceptGroup, String idThesaurus, String idLang) {

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
                    String query = "SELECT lexicalvalue FROM concept_group_label"
                            + " WHERE idthesaurus = '" + idThesaurus + "'"
                            + " and idgroup = '" + idConceptGroup + "'"
                            + " and lang  = '" + idLang + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        lexicalValue = resultSet.getString("lexicalvalue");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting lexical value of Group : " + idConceptGroup, sqle);
        }
        return lexicalValue;
    }

    /**
     * Cette fonction permet de supprimer un Terme avec toutes les dépendances
     * (Prefered term dans toutes les langues) et (nonPreferedTerm dans toutes
     * les langues)
     *
     * @param ds
     * @param idTerm
     * @param idThesaurus
     * @param idUser
     * @return
     */
    public boolean deleteGroup(HikariDataSource ds,
            String idTerm, String idThesaurus, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from term where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_term  = '" + idTerm + "'";
                    stmt.executeUpdate(query);

                    // Suppression de la relation Term_Concept
                    query = "delete from preferred_term where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_term  = '" + idTerm + "'";
                    stmt.executeUpdate(query);

                    // suppression des termes synonymes
                    query = "delete from non_preferred_term where"
                            + " id_thesaurus = '" + idThesaurus + "'"
                            + " and id_term  = '" + idTerm + "'";
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
            log.error("Error while deleting Term and relations : " + idTerm, sqle);
        }
        return status;
    }

    /**
     * Cette fonction permet de supprimer un groupe et ses traductions
     * (utilisable uniquement s'il est vide)
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @param idUser
     * @return
     */
    public boolean deleteConceptGroup(HikariDataSource ds,
            String idGroup, String idThesaurus, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    // Suppression des traductions
                    String query = "delete from concept_group_label where"
                            + " idthesaurus = '" + idThesaurus + "'"
                            + " and idgroup  = '" + idGroup + "'";
                    stmt.executeUpdate(query);

                    // Suppression du groupe
                    query = "delete from concept_group where"
                            + " idthesaurus = '" + idThesaurus + "'"
                            + " and idgroup  = '" + idGroup + "'";
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
            log.error("Error while deleting group : " + idGroup, sqle);
        }
        return status;
    }

    /**
     * Permet de retourner une ArrayList de NodeConceptGroup par langue et par
     * thésaurus / ou null si rien cette fonction ne retourne pas les détails et
     * les traductions Si le Domaine n'est pas traduit dans la langue en cours,
     * on récupère l'identifiant pour l'afficher à la place
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @param idLang
     * @return Objet Class ArrayList NodeConceptGroup
     */
    public ArrayList<NodeGroup> getListConceptGroup(HikariDataSource ds,
            String idThesaurus, String idLang) {

        ArrayList<NodeGroup> nodeConceptGroupList;
        ArrayList tabIdConceptGroup = getListIdOfGroup(ds, idThesaurus);

        nodeConceptGroupList = new ArrayList<>();
        for (Object tabIdGroup1 : tabIdConceptGroup) {
            NodeGroup nodeConceptGroup;
            nodeConceptGroup = getThisConceptGroup(ds, tabIdGroup1.toString(), idThesaurus, idLang);
            if (nodeConceptGroup == null) {
                return null;
            }
            nodeConceptGroupList.add(nodeConceptGroup);
        }

        return nodeConceptGroupList;
    }

    public ArrayList<NodeGroup> getListRootConceptGroup(HikariDataSource ds,
            String idThesaurus, String idLang) {

        ArrayList<NodeGroup> nodeConceptGroupList;
        ArrayList tabIdConceptGroup = getListIdOfRootGroup(ds, idThesaurus);

        nodeConceptGroupList = new ArrayList<>();
        for (Object tabIdGroup1 : tabIdConceptGroup) {
            NodeGroup nodeConceptGroup;
            nodeConceptGroup = getThisConceptGroup(ds, tabIdGroup1.toString(), idThesaurus, idLang);
            if (nodeConceptGroup == null) {
                return null;
            }
            nodeConceptGroupList.add(nodeConceptGroup);
        }

        return nodeConceptGroupList;

    }

    public ArrayList<NodeGroup> getListConceptGroup2(HikariDataSource ds,
            String idThesaurus, String idLang) throws SQLException {

        ArrayList<NodeGroup> nodeConceptGroupList;
        ArrayList tabIdConceptGroup = getListIdOfGroup(ds, idThesaurus);

        nodeConceptGroupList = new ArrayList<>();
        for (Object tabIdGroup1 : tabIdConceptGroup) {
            NodeGroup nodeConceptGroup;
            getThisConceptGroup2(ds, tabIdGroup1.toString(), idThesaurus, idLang, nodeConceptGroupList);
        }

        return nodeConceptGroupList;
    }

    /**
     * Cette fonction permet de récupérer la liste des domaines pour
     * l'autocomplétion
     *
     * @param ds
     * @param idThesaurus
     * @param text
     * @param idLang
     * @return Objet class Concept
     */
    public List<NodeAutoCompletion> getAutoCompletionGroup(HikariDataSource ds,
            String idThesaurus, String idLang, String text) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        List<NodeAutoCompletion> nodeAutoCompletionList = null;
        text = new StringPlus().convertString(text);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT concept_group_label.idgroup,"
                            + " concept_group_label.lexicalvalue FROM concept_group_label"
                            + " WHERE "
                            + " concept_group_label.idthesaurus = '" + idThesaurus + "'"
                            + " AND concept_group_label.lang = '" + idLang + "'"
                            + " AND unaccent_string(concept_group_label.lexicalvalue) ILIKE unaccent_string('" + text + "%')"
                            + " ORDER BY concept_group_label.lexicalvalue ASC LIMIT 20";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodeAutoCompletionList = new ArrayList<>();
                    while (resultSet.next()) {
                        if (resultSet.getRow() != 0) {
                            NodeAutoCompletion nodeAutoCompletion = new NodeAutoCompletion();
                            nodeAutoCompletion.setIdConcept("");
                            nodeAutoCompletion.setTermLexicalValue("");
                            nodeAutoCompletion.setGroupLexicalValue(resultSet.getString("lexicalvalue"));
                            nodeAutoCompletion.setIdGroup(resultSet.getString("idgroup"));
                            nodeAutoCompletionList.add(nodeAutoCompletion);
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
            log.error("Error while getting List of autocompletion of Text : " + text, sqle);
        }

        return nodeAutoCompletionList;
    }

    /**
     * Cette fonction permet de récupérer la liste des domaines sauf celui en
     * cours pour l'autocomplétion
     *
     * @param ds
     * @param idThesaurus
     * @param idGroup
     * @param text
     * @param idLang
     * @return Objet class Concept
     */
    public List<NodeAutoCompletion> getAutoCompletionOtherGroup(HikariDataSource ds,
            String idThesaurus,
            String idGroup, // le Group à ignorer
            String idLang, String text) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        List<NodeAutoCompletion> nodeAutoCompletionList = null;
        text = new StringPlus().convertString(text);

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT concept_group_label.idgroup,"
                            + " concept_group_label.lexicalvalue FROM concept_group_label"
                            + " WHERE "
                            + " concept_group_label.idthesaurus = '" + idThesaurus + "'"
                            + " AND concept_group_label.lang = '" + idLang + "'"
                            + " AND concept_group_label.idgroup != '" + idGroup + "'"
                            + " AND unaccent_string(concept_group_label.lexicalvalue) ILIKE unaccent_string('" + text + "%')"
                            + " ORDER BY concept_group_label.lexicalvalue ASC LIMIT 20";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    nodeAutoCompletionList = new ArrayList<>();
                    while (resultSet.next()) {
                        if (resultSet.getRow() != 0) {
                            NodeAutoCompletion nodeAutoCompletion = new NodeAutoCompletion();
                            nodeAutoCompletion.setIdConcept("");
                            nodeAutoCompletion.setTermLexicalValue("");
                            nodeAutoCompletion.setGroupLexicalValue(resultSet.getString("lexicalvalue"));
                            nodeAutoCompletion.setIdGroup(resultSet.getString("idgroup"));
                            nodeAutoCompletionList.add(nodeAutoCompletion);
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
            log.error("Error while getting List of autocompletion of Text : " + text, sqle);
        }

        return nodeAutoCompletionList;
    }

    /**
     * Permet de retourner une ArrayList de String (idGroup) par thésaurus / ou
     * null si rien
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @return Objet Class ArrayList NodeConceptGroup
     */
    public ArrayList<String> getListIdOfGroup(HikariDataSource ds,
            String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList tabIdConceptGroup = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select idgroup from concept_group where idthesaurus = '" + idThesaurus + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    tabIdConceptGroup = new ArrayList();
                    while (resultSet.next()) {
                        tabIdConceptGroup.add(resultSet.getString("idgroup"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Id or Groups of thesaurus : " + idThesaurus, sqle);
        }
        return tabIdConceptGroup;
    }

    public ArrayList<String> getListIdOfRootGroup(HikariDataSource ds,
            String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList tabIdConceptGroup = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select idgroup from concept_group where idthesaurus = '" + idThesaurus + "' and  idgroup NOT IN ( SELECT id_group2 FROM relation_group WHERE relation = 'sub')";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    tabIdConceptGroup = new ArrayList();
                    while (resultSet.next()) {
                        tabIdConceptGroup.add(resultSet.getString("idgroup"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting List Id or Groups of thesaurus : " + idThesaurus, sqle);
        }
        return tabIdConceptGroup;
    }

    /**
     * Permet de retourner la liste des Groupes pour un Concept et un thésaurus
     * donné
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @param idConcept
     * @return Objet Class ArrayList NodeConceptGroup
     */
    public ArrayList<String> getListIdGroupOfConcept(HikariDataSource ds,
            String idThesaurus, String idConcept) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList tabIdConceptGroup = null;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    /*String query = "select id_group from concept where id_thesaurus = '" + idThesaurus + "'"
                            + " and id_concept = '" + idConcept + "'";*/

                    String query = "select idgroup from concept_group_concept where idthesaurus = '" + idThesaurus + "'"
                            + " and idconcept = '" + idConcept + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        tabIdConceptGroup = new ArrayList<>();
                        while (resultSet.next()) {
                            tabIdConceptGroup.add(resultSet.getString("idgroup"));
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
            log.error("Error while getting List Id or Groups of Concept : " + idConcept, sqle);
        }
        return tabIdConceptGroup;
    }

    /**
     * Cette fonction permet de récupérer l'identifiant Ark sinon renvoie un une
     * chaine vide
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @return Objet class Concept
     */
    public String getIdArkOfGroup(HikariDataSource ds, String idGroup, String idThesaurus) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String ark = "";
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id_ark from concept_group where"
                            + " idthesaurus = '" + idThesaurus + "'"
                            + " and idgroup = '" + idGroup + "'";
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
            log.error("Error while getting idArk of Group : " + idGroup, sqle);
        }
        return ark;
    }

    /**
     * Permet de mettre à jour un Domaine suivant un identifiant un thésaurus et
     * une langue donnés
     *
     * @param ds
     * @param conceptGroupLabel
     * @param idUser
     * @return true or false
     */
    public boolean updateConceptGroupLabel(HikariDataSource ds, ConceptGroupLabel conceptGroupLabel, int idUser) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        conceptGroupLabel.setLexicalvalue(
                new StringPlus().convertString(conceptGroupLabel.getLexicalvalue()));

        /**
         * On met à jour tous les chmamps saufs l'idThesaurus, la date de
         * creation en utilisant et la langue
         */
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE concept_group_label "
                            + "set lexicalvalue='" + conceptGroupLabel.getLexicalvalue() + "',"
                            + " modified = current_date"
                            + " WHERE lang ='" + conceptGroupLabel.getLang() + "'"
                            + " AND idthesaurus='" + conceptGroupLabel.getIdthesaurus() + "'"
                            + " AND idgroup ='" + conceptGroupLabel.getIdgroup() + "'";

                    stmt.executeUpdate(query);
                    status = true;
                    addGroupTraductionHistorique(ds, conceptGroupLabel, idUser);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while updating ConceptGroupLable : " + conceptGroupLabel.getLexicalvalue() + " lang = " + conceptGroupLabel.getLang(), sqle);
        }
        return status;

    }

    /**
     * Cette fonction permet de savoir si l'identifiant est un identifiant de
     * Groupe ou non
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @return boolean
     */
    public boolean isIdOfGroup(HikariDataSource ds,
            String idGroup, String idThesaurus) {

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
                    String query = "select idgroup from concept_group where "
                            + " idgroup = '" + idGroup + "'"
                            + " and idthesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    existe = (resultSet.getRow() != 0);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while Asking if Is Id of Group : " + idGroup, sqle);
        }
        return existe;
    }

    /**
     * Cette fonction permet de savoir si le Group est vide (pas de concepts)
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @return
     */
    public boolean isEmptyDomain(HikariDataSource ds,
            String idGroup, String idThesaurus) {

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
                            + " WHERE id_thesaurus='" + idThesaurus + "'"
                            + " AND id_group='" + idGroup + "'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    group = (resultSet.getRow() == 0);

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while testing if Group have Concept : " + idGroup, sqle);
        }
        return group;
    }

    /**
     * Cette fonction permet de savoir si le Domaine existe dans cette langue
     *
     * @param ds
     * @param title
     * @param idThesaurus
     * @param idLang
     * @return boolean
     */
    public boolean isDomainExist(HikariDataSource ds,
            String title, String idThesaurus, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;
        title = new StringPlus().convertString(title);
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select idgroup from concept_group_label where "
                            + "unaccent_string(lexicalvalue) ilike "
                            + "unaccent_string('" + title
                            + "')  and lang = '" + idLang
                            + "' and idthesaurus = '" + idThesaurus
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
            log.error("Error while asking if Title of Term exist : " + title, sqle);
        }
        return existe;
    }
    
    
    /**
     * 
     * @param ds
     * @param listType 
 * 
     */
    public void getPossibleTypeGroupForAdd(HikariDataSource ds,ArrayList<String> listType){
         Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Select label from concept_group_type";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        listType.add(resultSet.getString("label"));
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getPossibleTypeGroupForAdd : " + sqle);
        }
        
    }
    /**
     * 
     * @param ds
     * @param idPere
     * @param idTheso
     * @return 
     */
    public String geteTypeGroupPere(HikariDataSource ds,String idPere, String idTheso){
         Connection conn;
        Statement stmt;
        ResultSet resultSet;
        String typeGroupPere = null;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Select idtypecode from concept_group where idgroup = '" + idPere + "' AND idthesaurus = '" + idTheso +"'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        typeGroupPere =resultSet.getString("idtypecode");
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getPossibleTypeGroupForAdd : " + sqle);
        }
        
        return typeGroupPere;
    }

}
