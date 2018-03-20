/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import fr.mom.arkeo.soap.DcElement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import mom.trd.opentheso.bdd.datas.ConceptGroup;
import mom.trd.opentheso.bdd.datas.ConceptGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeGroupType;
import mom.trd.opentheso.bdd.helper.nodes.NodeGroupSousGroup;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptTree;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupTraductions;
import mom.trd.opentheso.bdd.tools.FileUtilities;
import mom.trd.opentheso.bdd.tools.StringPlus;
import mom.trd.opentheso.ws.ark.ArkClient;
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

    /**
     * Ajout d'un groupe à un concept avec Rollback
     * @param conn
     * @param groupID
     * @param conceptID
     * @param idThesaurus 
     * @return  
     */
    public boolean addConceptGroupConcept(Connection conn,
            String groupID, String conceptID, String idThesaurus) {

        Statement stmt;
        boolean status = false;
        try {

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
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
           //     conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while addConceptGroupConcept : " + sqle);
        }
        return status;
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

        if (nodeConceptGroup.getConceptGroup().getNotation() == null) {
            nodeConceptGroup.getConceptGroup().setNotation("");
        }

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
                    idConceptGroup = nodeConceptGroup.getConceptGroup().getIdtypecode() + ++idNumeriqueGroup;

                    /**
                     * récupération du code Ark via WebServices
                     *
                     */
                    String idArk = "";

                    if (isArkActive) {
                        ArrayList<DcElement> dcElementsList = new ArrayList<>();
                        ArkClient ark_Client = new ArkClient();
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
        } catch (SQLException sqle) {
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
                        ArkClient ark_Client = new ArkClient();
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
     * permet de retourner le nombre de Groups pour un concept
     * @param ds
     * @param idConcept
     * @param idThesaurus
     * @return 
     */
    public int getCountOfGroups(HikariDataSource ds,
            String idConcept, String idThesaurus) {

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
                    String query = "SELECT count(concept_group_concept.idgroup)" +
                        " FROM concept_group_concept" +
                        " WHERE" +
                        " concept_group_concept.idconcept = '" + idConcept +"'" +
                        " AND" +
                        " concept_group_concept.idthesaurus = '" + idThesaurus + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
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
            log.error("Error while getting count of Groups of Concept : " + idConcept, sqle);
        }
        return count;
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
     * @param date
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

    /**
     * Permet de retourner la liste des sous_groupes d'un Group (type G/C/MT/T)
     * si le group n'est pas traduit, on récupère l'ID à la place 
     * 
     * @param ds
     * @param idConceptGroup
     * @param idThesaurus
     * @param idLang
     * @return
     * #MR
     */
    public ArrayList<NodeConceptTree> getRelationGroupOf(HikariDataSource ds,
            String idConceptGroup, String idThesaurus, String idLang) {
        ArrayList<NodeConceptTree> nodeConceptTrees;
        ArrayList<String> lisIdGroups = getListGroupChildIdOfGroup(ds, idConceptGroup, idThesaurus);
        if(lisIdGroups == null) return null;
        if(lisIdGroups.isEmpty()) return null;

        nodeConceptTrees = new ArrayList<>();
        for (String idGroup : lisIdGroups) {
            NodeConceptTree nodeConceptTree = new NodeConceptTree();
            nodeConceptTree.setIdConcept(idGroup);
            nodeConceptTree.setIdLang(idLang);
            nodeConceptTree.setIdThesaurus(idThesaurus);
            nodeConceptTree.setTitle(getLexicalValueOfGroup(ds, idGroup, idThesaurus, idLang));
            nodeConceptTree.setStatusConcept("");
            nodeConceptTree.setHaveChildren(true);
            nodeConceptTree.setIsGroup(false);
            nodeConceptTree.setIsSubGroup(true);
            nodeConceptTrees.add(nodeConceptTree);
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
     * Cette fonction permet de supprimer une traduciton à un groupe
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @param idLang
     * @return
     * #MR
     */
    
    public boolean deleteGroupTraduction(HikariDataSource ds,
            String idGroup, String idThesaurus,
            String idLang) {

        Connection conn;
        Statement stmt;
        boolean status = false;
        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from concept_group_label where"
                            + " idthesaurus = '" + idThesaurus + "'"
                            + " and idgroup = '" + idGroup + "'"
                            + " and lang = '" + idLang + "'";
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
            log.error("Error while deleting traduction of Group : " + idGroup, sqle);
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

    /**
     * permet de retournner la liste des domaines de premier niveau (MT, G, C, T
     * )
     *
     * @param ds
     * @param idThesaurus
     * @param idLang
     * @return
     */
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

    public ArrayList<String> getListIdOfGroup(Connection conn,
            String idThesaurus) {

        Statement stmt;
        ResultSet resultSet;
        ArrayList tabIdConceptGroup = null;

        try {
            // Get connection from pool
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
     * Permet de retourner l'hiérarchie complète d'un groupe 
     * en partant du groupe ou sous-groupe pour arriver à la racine.
     * exp pour le sous_groupe (environnement) : Education -> Education science -> environnement 
     * 
     * @param ds
     * @param idThesaurus
     * @param idGroup
     * @return 
     * #MR
     */
    public NodeGroup getGroupHierarchy (HikariDataSource ds, 
            String idThesaurus, String idGroup) {
        NodeGroup nodeGroup = new NodeGroup();
        
     //   nodeGroup. 
      //  getSubGroup()
      return nodeGroup;
    }
    
    public String getSubGroup(HikariDataSource ds, 
            String idThesaurus, String idGroup) {
        return "";
    }
    
    /**
     * Permet de retourner la liste Id des Groupes pour un Concept et un thésaurus
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
     * Permet de retourner la liste des Groupes pour un Concept et un thésaurus
     * donné
     *
     * @param ds le pool de connexion
     * @param idThesaurus
     * @param idConcept
     * @param idLang
     * @return Objet Class ArrayList NodeGroup
     */
    public ArrayList<NodeGroup> getListGroupOfConcept(HikariDataSource ds,
            String idThesaurus, String idConcept, String idLang) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        ArrayList <NodeGroup> nodeGroups = new ArrayList<>();

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
                    while (resultSet.next()) {
                        NodeGroup nodeGroup = getThisConceptGroup(ds, 
                                resultSet.getString("idgroup"),
                                idThesaurus, idLang);
                        if(nodeGroup != null) 
                            nodeGroups.add(nodeGroup);
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
        return nodeGroups;
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
                    String query = "SELECT idgroup FROM concept_group_concept"
                            + " WHERE idthesaurus='" + idThesaurus + "'"
                            + " AND idgroup='" + idGroup + "'";

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
     * Cette fonction permet de savoir si le Domaine existe dans cette langue
     *
     * @param ds
     * @param idGroup
     * @param idThesaurus
     * @param idLang
     * @return boolean
     */
    public boolean isHaveTraduction(HikariDataSource ds,
            String idGroup, String idThesaurus, String idLang) {

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
                    String query = "select idgroup from concept_group_label where "
                            + " idgroup ='" + idGroup + "'"
                            + " and lang = '" + idLang + "'"
                            + " and idthesaurus = '" + idThesaurus + "'";
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
            log.error("Error while asking if traduction exist of Group : " + idGroup, sqle);
        }
        return existe;
    }    

    /**
     * Fonction qui permet de retourner la liste des Type de Groupe
     *
     * @param ds
     * @return
     */
    public List<SelectItem> getAllGroupType(HikariDataSource ds) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        List<SelectItem> nodeGroupTypes = new ArrayList<>();

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Select code, label, skoslabel from concept_group_type";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    while (resultSet.next()) {
                        SelectItem nodeGroupType = new SelectItem();
                        nodeGroupType.setValue(resultSet.getString("code"));
                        nodeGroupType.setLabel(resultSet.getString("label"));
                        nodeGroupTypes.add(nodeGroupType);
                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            // Log exception
            log.error("Error while getting list of Group Type : " + sqle);
        }
        return nodeGroupTypes;
    }

    /**
     * permet de retourner le type de group du père uniquement
     *
     * @param ds
     * @param idPere
     * @param idTheso
     * @return
     */
    public String getTypeGroupPere(HikariDataSource ds, String idPere, String idTheso) {
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
                    String query = "Select idtypecode from concept_group where idgroup = '" + idPere + "' AND idthesaurus = '" + idTheso + "'";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        typeGroupPere = resultSet.getString("idtypecode");
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

    /**
     * Cette fonction permet de savoir si un group ou sous_group a des
     * sous_group ou non suivant l'id du Group ou sous_group et l'id du
     * thésaurus #MR.
     *
     * @param ds
     * @param idThesaurus
     * @param idGroup
     * @return
     */
    public boolean haveSubGroup(HikariDataSource ds,
            String idThesaurus, String idGroup) {

        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean subGroup = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select count(*)  from relation_group"
                            + " where id_thesaurus ='" + idThesaurus + "'"
                            + " and id_group1 ='" + idGroup + "'"
                            + " and relation ='sub'";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        if (resultSet.getInt(1) != 0) {
                            subGroup = true;
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
            log.error("Error while testing if haveSubGroup of Group : " + idGroup, sqle);
        }
        return subGroup;
    }

    /**
     * Change l'id d'un group dans la table concept_group
     *
     * @param conn
     * @param idTheso
     * @param idGroup
     * @param newIdGroup
     * @throws java.sql.SQLException
     */
    public void setIdGroup(Connection conn, String idTheso, String idGroup, String newIdGroup) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        try {
            String query = "UPDATE concept_group"
                    + " SET idgroup = '" + newIdGroup + "'"
                    + " WHERE idgroup = '" + idGroup + "'"
                    + " AND idthesaurus = '" + idTheso + "'";
            stmt.execute(query);

        } finally {
            stmt.close();
        }
    }

    /**
     * Change l'id d'un group dans la table concept_group_concept
     *
     * @param conn
     * @param idTheso
     * @param idGroup
     * @param newIdGroup
     * @throws java.sql.SQLException
     */
    public void setIdGroupConcept(Connection conn, String idTheso, String idGroup, String newIdGroup) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        try {
            String query = "UPDATE concept_group_concept"
                    + " SET idgroup = '" + newIdGroup + "'"
                    + " WHERE idgroup = '" + idGroup + "'"
                    + " AND idthesaurus = '" + idTheso + "'";
            stmt.execute(query);

        } finally {
            stmt.close();
        }
    }

    /**
     * Change l'id d'un group dans la table concept_group_historique
     *
     * @param conn
     * @param idTheso
     * @param idGroup
     * @param newIdGroup
     * @throws java.sql.SQLException
     */
    public void setIdGroupHisto(Connection conn, String idTheso, String idGroup, String newIdGroup) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        try {
            String query = "UPDATE concept_group_historique"
                    + " SET idgroup = '" + newIdGroup + "'"
                    + " WHERE idgroup = '" + idGroup + "'"
                    + " AND idthesaurus = '" + idTheso + "'";
            stmt.execute(query);

        } finally {
            stmt.close();
        }
    }

    /**
     * Change l'id d'un group dans la table concept_group_label
     *
     * @param conn
     * @param idTheso
     * @param idGroup
     * @param newIdGroup
     * @throws java.sql.SQLException
     */
    public void setIdGroupLabel(Connection conn, String idTheso, String idGroup, String newIdGroup) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        try {
            String query = "UPDATE concept_group_label"
                    + " SET idgroup = '" + newIdGroup + "'"
                    + " WHERE idgroup = '" + idGroup + "'"
                    + " AND idthesaurus = '" + idTheso + "'";
            stmt.execute(query);

        } finally {
            stmt.close();
        }
    }

    /**
     * Change l'id d'un group dans la table concept_group_label_historique
     *
     * @param conn
     * @param idTheso
     * @param idGroup
     * @param newIdGroup
     * @throws java.sql.SQLException
     */
    public void setIdGroupLabelHisto(Connection conn, String idTheso, String idGroup, String newIdGroup) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        try {
            String query = "UPDATE concept_group_label_historique"
                    + " SET idgroup = '" + newIdGroup + "'"
                    + " WHERE idgroup = '" + idGroup + "'"
                    + " AND idthesaurus = '" + idTheso + "'";
            stmt.execute(query);

        } finally {
            stmt.close();
        }
    }

    /**
     * Change l'id d'un group dans la table relation_group
     *
     * @param conn
     * @param idTheso
     * @param idGroup
     * @param newIdGroup
     * @throws java.sql.SQLException
     */
    public void setIdGroupRelation(Connection conn, String idTheso, String idGroup, String newIdGroup) throws SQLException {
        Statement stmt;
        stmt = conn.createStatement();
        try {
            String query = "UPDATE relation_group"
                    + " SET id_group1 = '" + newIdGroup + "'"
                    + " WHERE id_group1 = '" + idGroup + "'"
                    + " AND id_thesaurus = '" + idTheso + "'";
            query += ";";
            query += "UPDATE relation_group"
                    + " SET id_group2 = '" + newIdGroup + "'"
                    + " WHERE id_group2 = '" + idGroup + "'"
                    + " AND id_thesaurus = '" + idTheso + "'";
            stmt.execute(query);

        } finally {
            stmt.close();
        }
    }
    /**
     * moveSubGroupToSubGroup
     * 
     * méthode pour supprimer l'enregistrement de la bdd de donnée de la 
     * précédente liaison ancien groupe  -> sous groupe de la table relation group
     * pour ajouter la nouvelle relation nouveau groupe-> sous groupe
     * de la table relation group
     * 
     * pour forcer le type passer en paramètre à être enreggistré dans la table
     * concept_group
     * @param conn
     * @param originNodeIdConcept
     * @param targetNodeIdConcept
     * @param BTOrigin
     * @param type
     * @param idThesaurus
     * @param idUser
     * @return 
     */
     public boolean moveSubGroupToSubGroup(Connection conn, String originNodeIdConcept, String targetNodeIdConcept, String BTOrigin, String type, String idThesaurus, int idUser) {
        PreparedStatement stmt;
        boolean ret=true;
        //on supprime la branche du BT d'origine
        String sql="DELETE FROM relation_group WHERE id_group1=? AND id_thesaurus=? AND relation=? AND id_group2=?";
        try{
            
        
            stmt=conn.prepareStatement(sql);
            stmt.setString(1,BTOrigin);
            stmt.setString(2,idThesaurus);
            stmt.setString(3,"sub");
            stmt.setString(4,originNodeIdConcept);
            
            try{
                stmt.execute();
            }
            finally{
                stmt.close();
        
            }
        
        }
        catch(SQLException e){
            log.error("error while deleting in table relation_group for group1 :"+BTOrigin+" group 2"+originNodeIdConcept,e);
            return false;
            
        }
        //on ajoute la branche sur la targetNode
        sql="INSERT INTO relation_group(id_group1,id_thesaurus,relation,id_group2) VALUES (?,?,?,?)";
        try{
            stmt=conn.prepareCall(sql);
            stmt.setString(1,targetNodeIdConcept);
            stmt.setString(2,idThesaurus);
            stmt.setString(3,"sub");
            stmt.setString(4,originNodeIdConcept);
            try{
                stmt.execute();
            }
            finally{
                stmt.close();
            }
        }
        catch(SQLException e){
            log.error("error while insert into table relation group for id_group 1 :"+targetNodeIdConcept+" id_group2"+originNodeIdConcept,e);
            return false;
        }
        
        //finalement on force le type dans la table concept_group
        String typeCode="G";
        switch(type){
            case("collection"):
            case("subCollection"):typeCode="C";
            break;
            case("group"):
            case("subGroup"):typeCode="G";            
            break;
            case("thème"):
            case("subThème"):typeCode="T";
            break;
            case("microTheso"):
            case("subMicroTheso"):typeCode="MT";
            break;
        }
        sql="UPDATE concept_group SET idtypecode=? WHERE idgroup=? AND idthesaurus=?";
        try{
            stmt=conn.prepareCall(sql);
            stmt.setString(1,typeCode);
            stmt.setString(2,originNodeIdConcept);
            stmt.setString(3,idThesaurus);
        }   
        catch(SQLException e){
            log.error("error while update concept_group for id group"+originNodeIdConcept,e);
            return false;
        }
        return true;
    }

    public void updateTypeGroup(HikariDataSource poolConnexion, String type, String idThesoSelected, String idConcept) {
       PreparedStatement stmt;
       Connection conn;
      
       try{
           conn=poolConnexion.getConnection();
           try{
               String sql="UPDATE concept_group SET idtypecode=? WHERE idgroup=? AND idthesaurus=?";
               stmt=conn.prepareStatement(sql);
               stmt.setString(1, type);
               stmt.setString(2, idConcept);
               stmt.setString(3,idThesoSelected);
               try{
                   stmt.execute();
               }
               finally{
                   stmt.close();
               }
           }
           finally{
               conn.close();
           }
       }
       catch(SQLException e){
           log.error("error while updating group type "+idConcept+" "+idThesoSelected+" "+type,e);
       }
       
       
    }

    public String getSuffixFromNode(HikariDataSource poolConnexion, String idThesaurus, String idGroup) {
        PreparedStatement stmt;
        Connection conn;
        ResultSet rs;
        String ret="";
        try{
            conn=poolConnexion.getConnection();
            try{
                String sql="SELECT numerotation FROM concept_group WHERE idthesaurus=? and idgroup=? ";
                stmt=conn.prepareStatement(sql);
                stmt.setString(1,idThesaurus);
                stmt.setString(2,idGroup);
                try{
                    rs=stmt.executeQuery();
                    rs.next();
                    int tmp=rs.getInt("numerotation");
                    ret=""+tmp; 
                }
                finally{
                    stmt.close();
                }
            }finally{
                conn.close();
            }
        }
        catch(SQLException e){
            log.error("error while getting stuff from "+idGroup+" theso "+idThesaurus, e);
        }
        return ret;
    }

    public void saveSuffixFromNode(HikariDataSource poolConnexion, String idThesaurus, String idConcept, String suffix) {
       Connection conn;
       PreparedStatement stmt;
       try{
           conn=poolConnexion.getConnection();
           try{
               String sql="UPDATE concept_group SET numerotation=? WHERE idthesaurus=? AND idgroup=? ";
               stmt=conn.prepareStatement(sql);
               stmt.setInt(1,Integer.parseInt(suffix));
               stmt.setString(2,idThesaurus);
               stmt.setString(3,idConcept);
               try{
                   stmt.execute();
               }
               finally{
                stmt.close();       
                }
           }
           finally{
               conn.close();
           }
           
       }catch(SQLException e){
           log.error("error while updating table concept group for thsaurus "+idThesaurus+" idconcept "+idConcept, e);
       }
    }

   
}
