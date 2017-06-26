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
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;

public class UserHelper {

    public UserHelper() {

    }

    /**
     * Cette fonction permet de récupérer l'Id de l'utilisateur d'après son
     * Login et son passe
     *
     * @param ds
     * @param login
     * @param pwd
     * @return
     */
    public boolean isUserExist(HikariDataSource ds, String login, String pwd) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        boolean existe = false;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_user FROM users WHERE username='" + login + "' AND password='" + pwd + "' and active=true";
                    resultSet = stmt.executeQuery(query);
                    //resultSet.first();
                    //resultSet.next();
                    if (resultSet.next()) {
                        existe = true;
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
        return existe;
    }

    /**
     * Permet de savoir si l'utilisateur n'a qu'un seul role
     *
     * @param ds
     * @param idUser
     * @return
     */
    public boolean isLastThesoOfUser(HikariDataSource ds, int idUser) {
        Connection conn;
        Statement stmt;
        int count = 0;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_user FROM user_role WHERE id_user =" + idUser;
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        count++;
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

        if (count == 1) {
            return true;
        } else {
            return false;
        }
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
    public boolean isUserMailExist(Connection conn, String mail) {
        Statement stmt;
        ResultSet resultSet;
        try {
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
                //conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * cette fonction permet de retourner les informations sur l'utilisateur et
     * son role par rapport à un thésaurus
     *
     * @param ds
     * @param logName
     * @param idThesaurus
     * @return
     */
    public NodeUser getInfoUser(HikariDataSource ds, String logName, String idThesaurus) {
        NodeUser nu = null;
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
                            + " users.username, users.id_user, users.mail,"
                            + " users.active"
                            + " FROM users, user_role, roles WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = roles.id AND"
                            + " users.username = '" + logName + "'";
                    //+ " AND" 
                    //+ " user_role.id_thesaurus = '" + idThesaurus + "'";

                    /*   String query = "SELECT users.id_user, username, mail, id_role, name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND username='" + logName + "' and active=true";*/
                    resultSet = stmt.executeQuery(query);

                    if (resultSet.next()) {
                        nu = new NodeUser();
                        nu.setId(resultSet.getInt("id_user"));
                        nu.setMail(resultSet.getString("mail"));
                        nu.setIdRole(resultSet.getInt("id_role"));
                        nu.setIsActive(resultSet.getBoolean("active"));
                        nu.setName(logName);
                        nu.setRole(resultSet.getString("name"));
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

    /**
     * cette fonction permet de retourner les informations sur l'utilisateur et
     * son role
     *
     * @param ds
     * @param logName
     * @return
     */
    public NodeUser getInfoUser(HikariDataSource ds, String logName) {
        NodeUser nu = null;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT roles.name, user_role.id_role,"
                            + " users.username, users.id_user, users.mail "
                            + " FROM users, user_role, roles WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = roles.id AND"
                            + " users.username = '" + logName + "'";
                    //+ " AND" 
                    //+ " user_role.id_thesaurus = '" + idThesaurus + "'";

                    /*   String query = "SELECT users.id_user, username, mail, id_role, name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND username='" + logName + "' and active=true";*/
                    resultSet = stmt.executeQuery(query);

                    if (resultSet.next()) {
                        nu = new NodeUser();
                        nu.setId(resultSet.getInt("id_user"));
                        nu.setMail(resultSet.getString("mail"));
                        nu.setIdRole(resultSet.getInt("id_role"));
                        nu.setName(logName);
                        nu.setRole(resultSet.getString("name"));
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

    /**
     * cette fonction permet de retourner l'identifiant du role de l'utilisateur
     *
     * @param ds
     * @param idUser
     * @return
     */
    public int getRoleOfUser(HikariDataSource ds, int idUser) {
        int idRole = -1;
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT user_role.id_role"
                            + " FROM user_role WHERE"
                            + " user_role.id_user = " + idUser;
                    resultSet = stmt.executeQuery(query);

                    if (resultSet.next()) {
                        idRole = resultSet.getInt("id_role");
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

        return idRole;
    }

    public String getNameUser(HikariDataSource ds, int iden) {

        String name = "";
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT username from users "
                            + " WHERE id_user =" + iden;

                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        name = resultSet.getString("username");
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

        return name;
    }
    
    /**
     * permet de retourner le pseudo de l'utilisateur d'après son Email
     * @param ds
     * @param email
     * @return 
     */
    public String getNameUser(HikariDataSource ds, String email) {

        String name = "";
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT username from users "
                            + " WHERE mail ='" + email + "'";

                    resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        name = resultSet.getString("username");
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

        return name;
    }    

    /**
     * cette fonction permet de retourner les informations sur l'utilisateur et
     * son role
     *
     * @param ds
     * @param logName
     * @return
     */
    public NodeUser getInfoAdmin(HikariDataSource ds, String logName) {
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
                            + " users.username = '" + logName + "'";

                    /*   String query = "SELECT users.id_user, username, mail, id_role, name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND username='" + logName + "' and active=true";*/
                    resultSet = stmt.executeQuery(query);

                    if (resultSet.next()) {
                        nu.setId(resultSet.getInt("id_user"));
                        nu.setMail(resultSet.getString("mail"));
                        nu.setIdRole(resultSet.getInt("id_role"));
                        nu.setName(logName);
                        nu.setRole(resultSet.getString("name"));
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

    

    /**
     *
     * permet de retourner la liste des admins pour un thésaurus pour leur
     * envoyer des alertes
     *
     * @param ds
     * @param idThesaurus
     * @return
     */
    public ArrayList<String> getMailAdmin(HikariDataSource ds,
            String idThesaurus) {
        ArrayList<String> lesMails = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT mail FROM users, user_role"
                            + " WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = 2 AND"
                            + " users.active = true AND"
                            + " user_role.id_thesaurus = '" + idThesaurus + "'";
                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
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

    /**
     * permet de retourner le mail d'un contributeur (avertissement sur les
     * candidats par mail)
     *
     * @param ds
     * @param idCdt
     * @param idThesaurus
     * @return
     */
    public ArrayList<String> getMailContributor(HikariDataSource ds, String idCdt,
            String idThesaurus) {
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
                            + "WHERE users.id_user = term_candidat.contributor "
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
     * mais filtrés par role : exp: un admin peut voir et modifier que les
     * admins et ceux qui sont en dessous
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
                            + " users.username, users.id_user, users.mail,"
                            + " users.active "
                            + " FROM users, user_role, roles WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = roles.id AND"
                            + " user_role.id_thesaurus = '" + idThesaurus + "'"
                            + " and roles.id >= " + idRoleFrom
                            + " ORDER BY username";

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
                        nu.setIsActive(resultSet.getBoolean("active"));
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
     * Permet de retourner la liste des thésaurus autorisées pour un utilisateur
     *
     * @param ds
     * @param idUser
     * @return
     */
    public List<String> getAuthorizedThesaurus(HikariDataSource ds,
            int idUser) {
        List<String> idThesausus = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_thesaurus"
                            + " FROM user_role WHERE"
                            + " id_user = " + idUser;

                    /*query = "SELECT users.id_user, username, mail, id_role, "
                            + "name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND active=true ORDER BY username";*/
                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
                        idThesausus.add(resultSet.getString("id_thesaurus"));
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

        return idThesausus;

    }
    
    

    /**
     * cette fonction permet de retourner la liste des utlisateurs par thésaurus
     *
     * @param ds
     * @param idThesaurus
     * @return
     */
    public ArrayList<NodeUser> getAllUsersOfThesaurus(HikariDataSource ds, String idThesaurus) {
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
                            + " ORDER BY username";

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
     * permet de retourner la liste de tous les utilisateurs sans exception avec
     * leurs role
     *
     * @param ds
     * @return
     */
    public ArrayList<NodeUser> getAllUsers(HikariDataSource ds) {
        ArrayList<NodeUser> listUser = new ArrayList<>();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT DISTINCT roles.name, user_role.id_role,"
                            + " users.username, users.id_user, users.mail, users.active "
                            + " FROM users, user_role, roles WHERE"
                            + " users.id_user = user_role.id_user AND"
                            + " user_role.id_role = roles.id"
                            + " ORDER BY username";

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
                        nu.setIsActive(resultSet.getBoolean("active")); 
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

    /**
     * Permet de supprimer tous les roles d'un utilisateur
     *
     * @param conn
     * @param idUser
     * @return
     */
    public boolean deleteAllRoleOfUser(Connection conn, int idUser) {

        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from user_role where"
                            + " id_user =" + idUser;
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
     * Permet de supprimer le role sur le thésaurus
     *
     * @param conn
     * @param idTheso
     * @return
     */
    public boolean deleteOnlyTheThesoFromRole(Connection conn,
            String idTheso) {

        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "update user_role set"
                            + " id_thesaurus =''"
                            + " where id_thesaurus = '" + idTheso + "'";
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
     * Permet de supprimer tous les roles d'un utilisateur
     *
     * @param conn
     * @param idUser
     * @param idThesaurus
     * @return
     */
    public boolean deleteThisRoleForThisThesaurus(Connection conn, int idUser,
            String idThesaurus) {

        Statement stmt;
        boolean status = false;

        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "delete from user_role where"
                            + " id_user =" + idUser
                            + " and id_thesaurus = '" + idThesaurus + "'";
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
                    query = "delete from user_role where"
                            + " id_user = "+idUser;
                    stmt.executeUpdate(query);
                    query = "update users_historique "
                            + " set delete = '" + new ToolsHelper().getDate()
                            + " ' where id_user = '"+idUser+"'";
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

    /**
     * Cette fonction permet de changer le role d'un utlisateur et ses droits
     * sur un ou plusieurs thésaurus
     *
     * @param conn
     * @param idUser
     * @param newRole
     * @param roleOnIdThesaurus
     * @return
     */
    public boolean updateRoleUser(Connection conn, int idUser, int newRole,
            List<String> roleOnIdThesaurus) {

        // suppression de tous les roles avant de les récréer 
        if (!deleteAllRoleOfUser(conn, idUser)) {
            return false;
        }

        // cas où l'utilisateur n'a de droits sur aucun thésaurus, on conserve son role seulement
        if (roleOnIdThesaurus.isEmpty()) {
            if (!addRole(conn, idUser, newRole, "", "")) {
                return false;
            }
        }

        for (String roleOnIdThesauru : roleOnIdThesaurus) {
            if (!addRole(conn, idUser, newRole, roleOnIdThesauru, "")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cette fonction permet de savoir si l'utilisateur a deja ce role sur un
     * thésaurus
     *
     * @param ds
     * @param idUser
     * @param role
     * @param idThesaurus
     * @return
     */
    public boolean isRoleExist(HikariDataSource ds,
            int idUser, int role, String idThesaurus) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT id_user FROM user_role WHERE"
                            + " id_user = " + idUser
                            + " AND"
                            + " id_role = " + role
                            + " AND"
                            + " id_thesaurus = '" + idThesaurus + "'";
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
     * On vérifie si le User est suprAdmin
     *
     * @param ds
     * @param userName
     * @return
     */
    public boolean isAdminUser(HikariDataSource ds,
            String userName) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT users.id_user FROM users, user_role"
                            + " WHERE "
                            + " users.id_user = user_role.id_user"
                            + " AND"
                            + " users.username = '" + userName + "'"
                            + " AND"
                            + " user_role.id_role = 1";
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
     * On vérifie si le User est suprAdmin par Id
     *
     * @param ds
     * @param idUser
     * @return
     */
    public boolean isAdminUser(HikariDataSource ds,
            int idUser) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT users.id_user FROM user_role"
                            + " WHERE "
                            + " users.id_user = " + idUser
                            + " AND"
                            + " user_role.id_role = 1";
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
     * Cette fonction permet de changer le role d'un utlisateur et ses droits
     * sur un ou plusieurs thésaurus
     *
     * @param ds
     * @param idUser
     * @param newRole
     * @param roleOnIdThesaurus
     * @return
     */
    public boolean updateRoleUser(HikariDataSource ds, int idUser, int newRole,
            String roleOnIdThesaurus) {
        Connection conn;
        Statement stmt;
        boolean status = false;

        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update user_role set id_role=" + newRole
                            + ", id_thesaurus = '" + roleOnIdThesaurus + "'"
                            + " where"
                            + " id_user =" + idUser
                            + " and id_thesaurus = '" + roleOnIdThesaurus + "'";
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
     * Cette fonction permet de retourner la liste des roles autorisés pour un
     * Role donné (c'est la liste qu'un utilisateur a le droit d'attribué à un
     * nouvel utilisateur)
     *
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
     * Cette fonction permet d'ajouter un utilisateur (permet le rollBack en cas
     * d'erreur
     *
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
     * cette fonction permet de retourner l'identifiant d'un utlisateur suivant
     * son Nom
     *
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
                    if (resultSet.next()) {
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
     * Cette fonction permet d'ajouter un role pour un utilisateur (permet le
     * rollBack en cas d'erreur
     *
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
    
    /**
     * Cette fonction permet de mettre à jour le status de l'utilisateur
     *
     * @param conn
     * @param idUser
     * @param isActive
     * @return
     */
    public boolean updateStatusUser(Connection conn, int idUser, boolean isActive) {

        Statement stmt;
        boolean status = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Update users set active ="+isActive
                            + " where  users.id_user = "+ idUser;
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
    
    

    public boolean isChangeToPass(HikariDataSource ds, String name) throws SQLException {
        boolean needchange = false;
        Statement stmt;
        Connection conn = ds.getConnection();
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "select passtomodify from users where username = '" + name + "'";
                    ResultSet resultset = stmt.executeQuery(query);
                    if (resultset.next()) {
                        needchange = resultset.getBoolean("passtomodify");
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
        return needchange;
    }
    public boolean isneededpass(HikariDataSource ds, int id ) 
    {
        Statement stmt;
        boolean need=false;
        try {
            Connection conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Select passtomodify from users where id_user = '"+id+"'";
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        need = rs.getBoolean("passtomodify");
                    }
                    }finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return need;
    }
    
    /**
     * permet de savoir si l'utilisateur est désactivé 
     * @param ds
     * @param id
     * @return 
     */
    public boolean isActiveUser(HikariDataSource ds, int id)
    {
        Statement stmt;
        boolean active = false;
        try {
            Connection conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "Select active from users where id_user = '"+id+"'";
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        active = rs.getBoolean("active");
                    }
                    }
                finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return active;
    }
    public boolean updateAddUserHistorique(Connection conn, String nameEdit)
    {
        Statement stmt;
        boolean update = false;
        try {
            try {
                stmt = conn.createStatement();
                try {
                    String query = "INSERT INTO users_historique (id_user, username)" 
                            + " SELECT id_user, username from users"
                            + " where username = '"+ nameEdit+"'";
                    stmt.executeUpdate(query);
                    update = true;
                } finally {
                    stmt.close();
                }
            } finally {
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return update;
    }
}
