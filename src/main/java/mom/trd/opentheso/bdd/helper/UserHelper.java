package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;

public class UserHelper {

    public UserHelper() {

    }

    public boolean isUserExist(HikariDataSource ds, String log, String pwd) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT username FROM users WHERE username='" + log + "' AND password='" + pwd + "' and active=true";
                    resultSet = stmt.executeQuery(query);
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        return true;
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

        return false;
    }
    
    public boolean isUserLoginExist(HikariDataSource ds, String log) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT username FROM users WHERE username='" + log + "'";
                    resultSet = stmt.executeQuery(query);
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        return true;
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

        return false;
    }
    
    public boolean isUserMailExist(HikariDataSource ds, String mail) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT mail FROM users WHERE mail='" + mail + "'";
                    resultSet = stmt.executeQuery(query);
                    resultSet.next();
                    if (resultSet.getRow() != 0) {
                        return true;
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

        return false;
    }

    /**
     * cette fonction permet de retourner les informations sur l'utilisateur et son role
     * @param ds
     * @param logName
     * @param idThesaurus
     * @return 
     */
    public NodeUser getInfoUser(HikariDataSource ds, String logName, String idThesaurus) {
        NodeUser nu = new NodeUser();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT roles.name, user_role.id_role,"
                            + " user_role.id_thesaurus,"
                            + " users.username, users.id_user, users.mail "
                            + " FROM users, user_role, roles WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = roles.id AND" 
                            + " users.username = '" + logName + "' AND" 
                            + " user_role.id_thesaurus = '" + idThesaurus + "'";
  
  
  
                /*   String query = "SELECT users.id_user, username, mail, id_role, name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND username='" + logName + "' and active=true";*/
                    resultSet = stmt.executeQuery(query);

                    if(resultSet.next()) {
                        nu.setId(resultSet.getInt("id_user"));
                        nu.setMail(resultSet.getString("mail"));
                        nu.setIdRole(resultSet.getInt("id_role"));
                        nu.setName(logName);
                        nu.setRole("roles.name");
                        nu.setIdThesaurus(idThesaurus);
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

        return nu;
    }
    
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

                    if(resultSet.next()){
                        np = new NodePreference();
                        np.setAlertCdt(resultSet.getBoolean("alert_cdt"));
                        np.setNbAlertCdt(resultSet.getInt("nb_alert_cdt"));
                        np.setSourceLang(resultSet.getString("source_lang"));                        
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
     * @param ds
     * @param idThesaurus
     * @param workLanguage 
     * @param nb_alert_cdt 
     * @param alert_cdt 
     */
    public void updatePreferences(HikariDataSource ds, String idThesaurus,
            String workLanguage, int nb_alert_cdt,
            boolean alert_cdt){
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
     * @param ds
     * @param idThesaurus
     * @param workLanguage 
     * @param nb_alert_cdt 
     * @param alert_cdt 
     */
    public void initPreferences(HikariDataSource ds, String idThesaurus,
            String workLanguage, int nb_alert_cdt,
            boolean alert_cdt){
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
                            + "," + alert_cdt +")";
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
    
    
    public ArrayList<String> getMailAdmin(HikariDataSource ds) {
        ArrayList<String> lesMails = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT mail FROM users  "
                            + "WHERE id_role=1 and active=true";
                    resultSet = stmt.executeQuery(query);

                    while(resultSet.next()) {
                        lesMails.add(resultSet.getString("mail"));
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

        return lesMails;
    }
    
    public ArrayList<String> getMailContributor(HikariDataSource ds, String idCdt) {
        ArrayList<String> lesMails = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT DISTINCT mail FROM users, term_candidat, concept_term_candidat "
                            + "WHERE users.id = term_candidat.contributor "
                            + "AND term_candidat.id_term = concept_term_candidat.id_term "
                            + "AND concept_term_candidat.id_concept = '" + idCdt + "'";
                    resultSet = stmt.executeQuery(query);

                    resultSet.next();
                    lesMails.add(resultSet.getString("mail"));
                    
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lesMails;
    }
 
    /**
     * cette fonction permet de retourner la liste des utlisateurs par thésaurus
     * mais filtrés par role : exp: un admin peut voir et modifier que les admins et ceux qui sont en dessous
     * 
     * @param ds
     * @param idThesaurus
     * @param idRoleFrom
     * @return 
     */
    public ArrayList<NodeUser> getAuthorizedUsers(HikariDataSource ds, String idThesaurus, int idRoleFrom) {
        ArrayList<NodeUser> listUser = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT roles.name, user_role.id_role,"
                            + " user_role.id_thesaurus,"
                            + " users.username, users.id_user, users.mail "
                            + " FROM users, user_role, roles WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = roles.id AND" 
                            + " user_role.id_thesaurus = '" + idThesaurus + "'"
                            + " and roles.id >= " + idRoleFrom ;
                    
                   /*query = "SELECT users.id_user, username, mail, id_role, "
                            + "name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND active=true ORDER BY username";*/
                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
                        NodeUser nu = new NodeUser();
                        nu.setId(resultSet.getInt("id_user"));
                        nu.setMail(resultSet.getString("mail"));
                        nu.setIdRole(resultSet.getInt("id_role"));
                        nu.setName(resultSet.getString("username"));
                        nu.setRole(resultSet.getString("name"));
                        listUser.add(nu);
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

        return listUser;
    }



    /**
     * cette fonction permet de retourner la liste des utlisateurs par thésaurus
     * @param ds
     * @param idThesaurus
     * @return 
     */
    public ArrayList<NodeUser> getAllUsers(HikariDataSource ds, String idThesaurus) {
        ArrayList<NodeUser> listUser = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT roles.name, user_role.id_role,"
                            + " user_role.id_thesaurus,"
                            + " users.username, users.id_user, users.mail "
                            + " FROM users, user_role, roles WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = roles.id AND" 
                            + " user_role.id_thesaurus = '" + idThesaurus + "'";
                    
                   /*query = "SELECT users.id_user, username, mail, id_role, "
                            + "name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND active=true ORDER BY username";*/
                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
                        NodeUser nu = new NodeUser();
                        nu.setId(resultSet.getInt("id_user"));
                        nu.setMail(resultSet.getString("mail"));
                        nu.setIdRole(resultSet.getInt("id_role"));
                        nu.setName(resultSet.getString("username"));
                        nu.setRole(resultSet.getString("name"));
                        listUser.add(nu);
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

        return listUser;
    }

    public void updatePwd(HikariDataSource ds, int idUser, String newPwd) {
        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE users set password = '" + newPwd
                            + "' WHERE id_user = " + idUser;
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
    
    public void updateMail(HikariDataSource ds, int idUser, String newMail) {
        Connection conn;
        Statement stmt;
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "UPDATE users set mail = '" + newMail
                            + "' WHERE id_user = " + idUser;
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

    public void deleteUser(HikariDataSource ds, int idUser) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from users where"
                            + " id_user =" + idUser;
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
    
    public void desactivateUser(HikariDataSource ds, int idUser) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update users set active=false where"
                            + " id_user =" + idUser;
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
    
    public void updateRoleUser(HikariDataSource ds, int idUser, int newRole, String idThesaurus) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update user_role set id_role="+newRole+" where"
                            + " id_user =" + idUser
                            + " and id_thesaurus = '" + idThesaurus + "'";
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
    
     public void updatePreferenceUser(HikariDataSource ds, NodePreference np){//,             String idThesaurus) {
        Connection conn;
        Statement stmt;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update preferences set source_lang='"+np.getSourceLang()+
                            "', nb_alert_cdt="+np.getNbAlertCdt()+", alert_cdt="+np.isAlertCdt()+" where"
                            + " id_pref = 1";
                          //  + " id_thesaurus ='" + idThesaurus + "'";
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
    
    public ArrayList<Entry<String, String>> getRoles(HikariDataSource ds) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Map map = new HashMap();

        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id, name from roles";

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            map.put(resultSet.getString("id"), resultSet.getString("name"));
                        }
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
        ArrayList<Entry<String, String>> listeRoles = new ArrayList<>(map.entrySet());
        return listeRoles;
    }
    

    /**
     * Cette fonction permet de retourner la liste des roles autorisés
     * pour un Role donné (c'est la liste qu'un utilisateur a le droit d'attribué à un nouvel utilisateur)
     * @param ds
     * @param idRoleFrom
     * @return 
     */
    public ArrayList<Entry<String, String>> getAuthorizedRoles(HikariDataSource ds,
            int idRoleFrom) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        Map map = new HashMap();
        
        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "select id, name from roles "
                            + " where id >= " + idRoleFrom;

                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            map.put(resultSet.getString("id"), resultSet.getString("name"));
                        }
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
        ArrayList<Entry<String, String>> listeRoles = new ArrayList<>(map.entrySet());
        return listeRoles;
    }    
    
    

    /**
     * Cette fonction permet d'ajouter un utilisateur (permet le rollBack en cas d'erreur
     * @param conn
     * @param name
     * @param mail
     * @param pwd
     * @param role
     * @return 
     */
    public boolean addUser(Connection conn, String name, String mail, String pwd, int role) {

        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into users"
                            + "(username,password,active,mail)"
                            + " values ("
                            + "'" + name + "'"
                            + ", '" + pwd + "'"
                            + "," + true
                            + ", '" + mail + "')";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status; 
    }
    
    /**
     * cette fonction permet de retourner l'identifiant d'un utlisateur suivant son Nom
     * @param conn
     * @param userName
     * @return 
     */
    public int getIdUser(Connection conn, String userName) {
        int idUser = -1; 
        Statement stmt;
        ResultSet resultSet;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_user FROM users"
                            + " WHERE"
                            + " username = '" + userName.trim() + "'";
                    resultSet = stmt.executeQuery(query);
                    if(resultSet.next()) {
                        NodeUser nu = new NodeUser();
                        idUser = resultSet.getInt("id_user");
                    }
                } finally {
                    stmt.close();
                }
            } finally {
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idUser;
    }    
  
    /**
     * Cette fonction permet d'ajouter un role pour un utilisateur (permet le rollBack en cas d'erreur
     * @param conn
     * @param idUser
     * @param idRole
     * @param idThesaurus
     * @param idGroup
     * @return 
     */
    public boolean addRole(Connection conn, int idUser, int idRole, 
            String idThesaurus, String idGroup) {

        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Insert into user_role"
                            + "(id_user, id_role,id_thesaurus,id_group)"
                            + " values ("
                            + idUser + ","
                            + idRole + ","
                            + "'" + idThesaurus + "',"
                            + "'" + idGroup + "')";

                    stmt.executeUpdate(query);
                    status = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status; 
    }    

}
