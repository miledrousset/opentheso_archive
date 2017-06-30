/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper.nodes;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.tools.StringPlus;

/**
 *
 * @author Quincy
 */
public class PreferencesHelper {

    public NodePreference getThesaurusPreference(HikariDataSource ds, String idThesaurus) {
        NodePreference np = null;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT * FROM preferences where id_thesaurus = '" + idThesaurus + "'";
                    resultSet = stmt.executeQuery(query);

                    if (resultSet.next()) {
                        np = new NodePreference();
                        np.setAlertCdt(resultSet.getBoolean("alert_cdt"));
                        np.setNbAlertCdt(resultSet.getInt("nb_alert_cdt"));
                        np.setSourceLang(resultSet.getString("source_lang"));
                        np.setIdentifierType(resultSet.getInt("identifier_type"));
                        np.setUseArk(resultSet.getBoolean("use_ark"));
                        np.setServeurArk(resultSet.getString("server_ark"));
                        np.setPathImage(resultSet.getString("path_image"));
                        np.setDossierResize(resultSet.getString("dossier_resize"));
                        /*
                        np.setProtcolMail(resultSet.getString("protocol_mail"));
                        np.setHostMail(resultSet.getString("host_mail"));
                        np.setPortMail(resultSet.getInt("port_mail"));
                        np.setAuthMail(resultSet.getBoolean("auth_mail"));
                        np.setMailForm(resultSet.getString("mail_from"));
                        np.setTransportMail(resultSet.getString("transport_mail"));
                        */
                        np.setBddActive(resultSet.getBoolean("bdd_active"));
                        np.setBddUseId(resultSet.getBoolean("bdd_use_id"));
                        np.setUrlBdd(resultSet.getString("url_bdd"));
                        np.setZ3950acif(resultSet.getBoolean("z3950actif"));
                        np.setCollectionAdresse(resultSet.getString("collection_adresse"));
                        np.setNoticeUrl(resultSet.getString("notice_url"));
                        np.setUrlEncode(resultSet.getString("url_encode"));
                        np.setPathNotice1(resultSet.getString("path_notice1"));
                        np.setPathNotice2(resultSet.getString("path_notice2"));
                        np.setCheminSite(resultSet.getString("chemin_site"));

                    }

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return np;
    }
    
    

    /**
     * Cette fonction permet de mettre à jour les préférences d'un thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @param workLanguage
     * @param nb_alert_cdt
     * @param alert_cdt
     */
    public void updatePreferences(HikariDataSource ds, String idThesaurus,
            String workLanguage, int nb_alert_cdt,
            boolean alert_cdt) {
        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE preferences set id_thesaurus  = '" + idThesaurus + "',"
                            + " source_lang = '" + workLanguage + "',"
                            + " nb_alert_cdt = " + nb_alert_cdt
                            + " alert_cdt = " + alert_cdt
                            + " WHERE id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cette fonction permet d'initialiser les préférences d'un thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @param workLanguage
     * @param nb_alert_cdt
     * @param alert_cdt
     */
    public void initPreferences(HikariDataSource ds, String idThesaurus,
            String workLanguage, int nb_alert_cdt,
            boolean alert_cdt) {
        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "insert into preferences "
                            + "(id_thesaurus,source_lang,nb_alert_cdt, alert_cdt)"
                            + " values"
                            + " ('" + idThesaurus + "',"
                            + "'" + workLanguage + "',"
                            + nb_alert_cdt
                            + "," + alert_cdt + ")";
                    stmt.executeUpdate(query);
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Permet de mettre à jour la préférence d'un thésaurus
     *
     * @param ds
     * @param np
     * @param idThesaurus
     * @return
     */
    public boolean updatePreferenceUser(HikariDataSource ds, NodePreference np, String idThesaurus) {
        Connection conn;
        Statement stmt;

        boolean status = false;
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update preferences set source_lang='" + np.getSourceLang()
                            + "', nb_alert_cdt=" + np.getNbAlertCdt() + ", alert_cdt=" + np.isAlertCdt() + " where"
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    /**
     * Permet de mettre à jour toutes les préférence
     *
     * @param ds
     * @param np
     * @param idThesaurus
     * @return
     */
    public boolean updateAllPreferenceUser(HikariDataSource ds, NodePreference np, String idThesaurus) {
        Connection conn;
        Statement stmt;
        boolean status = false;
        StringPlus stringPlus = new StringPlus();

        np = normalizeDatas(np);
        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update preferences set "
                            + "source_lang='" + stringPlus.convertString(np.getSourceLang()) + "'"
                            + ", nb_alert_cdt='" + np.getNbAlertCdt() + "'"
                            + ", alert_cdt='" + np.isAlertCdt() + "'"
                            + ",identifier_type='" + np.getIdentifierType() + "'"
                            + ", use_ark='"+np.isUseArk() + "'"
                            + ", server_ark='"+stringPlus.convertString(np.getServeurArk()) + "'"
                            + ",path_image='"+stringPlus.convertString(np.getPathImage()) + "'"
                            + ", dossier_resize='"+stringPlus.convertString(np.getDossierResize()) + "'"
                        /*    + ", protocol_mail='"+stringPlus.convertString(np.getProtcolMail()) + "'"
                            + ", host_mail='"+stringPlus.convertString(np.getHostMail()) + "'"
                            + ", port_mail='"+np.getPortMail() + "'"
                            + ", auth_mail='"+np.isAuthMail() + "'"
                            + ", mail_from='"+stringPlus.convertString(np.getMailForm()) + "'"
                            + ", transport_mail='"+stringPlus.convertString(np.getTransportMail()) + "'"
                            */
                            + ", bdd_active='"+np.isBddActive() + "'"
                            + ", bdd_use_id='"+np.isBddUseId() + "'"
                            + ", url_bdd='"+stringPlus.convertString(np.getUrlBdd()) + "'"
                            + ", z3950actif='"+np.getZ3950acif() + "'"
                            + ", collection_adresse='"+stringPlus.convertString(np.getCollectionAdresse()) + "'"
                            + ", notice_url='"+ stringPlus.convertString(np.getNoticeUrl())+ "'"
                            + ", url_encode='"+stringPlus.convertString(np.getUrlEncode()) + "'"
                            + ", path_notice1='"+stringPlus.convertString(np.getPathNotice1()) + "'"
                            + ", path_notice2='"+stringPlus.convertString(np.getPathNotice2()) + "'"
                            + ", chemin_site='"+stringPlus.convertString(np.getCheminSite())+"'"
                            + " WHERE"
                            + " id_thesaurus = '" + idThesaurus + "'";
                    stmt.executeUpdate(query);
                    status = true;

                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }
    
    /**
     * permet de nettoyer les "/" et préparer les paramètres correctement 
     * @param nodePreference
     * @return 
     */
    private NodePreference normalizeDatas(NodePreference nodePreference) {

        // vérification des "/" à la fin 
        if(!nodePreference.getCheminSite().isEmpty()) {
            if(!nodePreference.getCheminSite().substring(nodePreference.getCheminSite().length() - 1, nodePreference.getCheminSite().length()).equalsIgnoreCase("/")) {
                nodePreference.setCheminSite(nodePreference.getCheminSite() + "/");
            }
        }
        if(!nodePreference.getServeurArk().isEmpty()) {
            if(!nodePreference.getServeurArk().substring(nodePreference.getServeurArk().length() - 1, nodePreference.getServeurArk().length()).equalsIgnoreCase("/")) {
                nodePreference.setServeurArk(nodePreference.getServeurArk() + "/");
            }
        }
        if(!nodePreference.getPathImage().isEmpty()) {
            if(!nodePreference.getPathImage().substring(nodePreference.getPathImage().length() - 1, nodePreference.getPathImage().length()).equalsIgnoreCase("/")) {
                nodePreference.setPathImage(nodePreference.getPathImage() + "/");
            }
        }         
        return nodePreference;
    } 
    
    

}
