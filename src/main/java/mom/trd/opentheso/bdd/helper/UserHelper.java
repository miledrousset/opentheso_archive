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

    public NodeUser getInfoUser(HikariDataSource ds, String log) {
        NodeUser nu = new NodeUser();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT users.id, username, mail, id_role, name, description FROM users, roles  "
                            + "WHERE users.id_role=roles.id AND username='" + log + "' and active=true";
                    resultSet = stmt.executeQuery(query);

                    resultSet.next();
                    nu.setId(resultSet.getInt("id"));
                    nu.setMail(resultSet.getString("mail"));
                    nu.setIdRole(resultSet.getString("id_role"));
                    nu.setName(log);
                    nu.setRole("roles.name");
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
    
    public NodePreference getPreferenceUser(HikariDataSource ds) {
        NodePreference np = new NodePreference();
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT * FROM preferences";
                    resultSet = stmt.executeQuery(query);

                    resultSet.next();
                    np.setAlertCdt(resultSet.getBoolean("alert_cdt"));
                    np.setNbAlertCdt(resultSet.getInt("nb_alert_cdt"));
                    np.setSourceLang(resultSet.getString("source_lang"));
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
                    String query = "SELECT users.id, username, mail, id_role, name, description FROM users, roles  WHERE users.id_role=roles.id AND active=true ORDER BY username";
                    resultSet = stmt.executeQuery(query);

                    while (resultSet.next()) {
                        NodeUser nu = new NodeUser();
                        nu.setId(resultSet.getInt("id"));
                        nu.setMail(resultSet.getString("mail"));
                        nu.setIdRole(resultSet.getString("id_role"));
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
                            + "' WHERE id = " + idUser;
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
                            + "' WHERE id = " + idUser;
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
                    String query = "update users set active=false where"
                            + " id =" + idUser;
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
    
    public void updateRoleUser(HikariDataSource ds, int idUser, int newRole) {
        Connection conn;
        Statement stmt;

        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "update users set id_role="+newRole+" where"
                            + " id =" + idUser;
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
    
     public void updatePreferenceUser(HikariDataSource ds, NodePreference np) {
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

    public void addUser(HikariDataSource ds, String name, String mail, String pwd, int role) {
        Connection conn;
        Statement stmt;
        ResultSet resultSet;

        try {
            // Get connection from pool
            conn = ds.getConnection();

            try {
                stmt = conn.createStatement();
                try {
                    String query = "select max(id) from users";
                    stmt.executeQuery(query);
                    resultSet = stmt.getResultSet();
                    resultSet.next();
                    int idNumeriqueUser = resultSet.getInt(1) + 1;

                    query = "Insert into users values ("
                            + "'" + idNumeriqueUser + "'"
                            + ", '" + name + "'"
                            + ", '" + pwd + "'"
                            + ", " + role
                            + ", true"
                            + ", '" + mail + "')";

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
}
