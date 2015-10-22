/**
 * 
 */
package mom.trd.opentheso.bdd.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import mom.trd.opentheso.bdd.account.Account;
import mom.trd.opentheso.bdd.account.Group;
import mom.trd.opentheso.bdd.account.User;
import mom.trd.opentheso.bdd.auth.data.AuthBean;
import mom.trd.opentheso.bdd.auth.data.DBAuthBean;
import mom.trd.opentheso.bdd.auth.exceptions.AuthenticationException;
import mom.trd.opentheso.bdd.auth.exceptions.AuthenticatorConnectionException;
import mom.trd.opentheso.bdd.auth.exceptions.InvalidAuthBeanException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Persee team
 */
public class DBAuthenticator extends Authenticator {
    private static Log log = LogFactory.getLog(DBAuthenticator.class);
    private DBAuthBean authBean;
    private Connection conn;
    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#connect()
     */
    public void connect() throws AuthenticatorConnectionException {
        try {
            // Register the JDBC driver
            Class.forName("org.postgresql.Driver");
            // Get a connection to the database
            conn = DriverManager.getConnection(authBean.getURL(), authBean
                    .getLogin(), authBean.getPasswd());
        } catch (ClassNotFoundException cnfe) {
            log.fatal("Couldn't find driver class.", cnfe);
            throw new AuthenticatorConnectionException();
        } catch (SQLException sqle) {
            log.fatal("Couldn't connect to database.", sqle);
            throw new AuthenticatorConnectionException();
        }
    }

    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#login(java.lang.String, java.lang.String)
     */
    public Account login(String login, String password) throws AuthenticationException {
        Account acc = null;
        try {
            // Get statement from newly created SQL connection
            Statement stmt = this.conn.createStatement();
            
            // Check if the pair (login, password) is valid
            String query = "SELECT * FROM users WHERE login='" + login 
                + "' AND passwd='" + password + "'";
            // Execute query and store result
            ResultSet rs = stmt.executeQuery(query);
            if (!rs.next()) { // Auth failed
                log.error("Authentication failed for user " + login);
                return null;
            }
            
            // User
            User user = new User();
            user.setUser(login);
            user.setFirstname(rs.getString("firstname"));
            user.setLastname(rs.getString("lastname"));
            user.setMail(rs.getString("mail"));
            
            // Account
            acc = new Account();
            acc.setBaseId(authBean.getBaseId());
            acc.setUser(user);
            
            // Login/password verified. Get the user groups.
            query = "SELECT group_id, default_group FROM user_groups " 
                + "WHERE user_login='" + login + "'";
            // Execute query and store result
            rs = stmt.executeQuery(query);
            Vector<Group> groups = new Vector<Group>(10);
            while (rs.next()) {
                Group group = new Group();
                if (rs.getBoolean("default_group")) { // Default user group
                    group.setId(Integer.toString(rs.getInt("group_id")));
                    user.setUserGroup(group);
                } else {
                    group.setId(Integer.toString(rs.getInt("group_id")));
                    groups.add(group);
                }
            }
            // Account groups
            if (groups.size() > 0) {
                acc.setGroups((Group[]) groups.toArray(new Group[groups.size()]));
            }
            // Free JDBC ressources
            rs.close();
            stmt.close();
        } catch (Exception e) {
            log.fatal("Error while authenticating the user " + login, e);
            throw new AuthenticationException();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqle) {
                    log.fatal("Error while closing the connection to the database", sqle);
                }
            }
        }
        return acc;
    }

    /*
     * (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#setAuthBean(fr.persee.aldo.auth.data.AuthBean)
     */
    public void setAuthBean(AuthBean bean) throws InvalidAuthBeanException {
        // We need database info
        if (!(bean instanceof DBAuthBean)) {
            throw new InvalidAuthBeanException();
        }
        authBean = (DBAuthBean) bean;
    }
    
	/**
	 * @return the authBean
	 */
	public DBAuthBean getAuthBean()
	{
		return authBean;
	}
	
	/* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#getInstance()
     */
    @Override
    public DBAuthenticator getClone() {
    	DBAuthenticator authenticator = new DBAuthenticator();
        try {
            authenticator.setAuthBean(authBean);
        } catch (InvalidAuthBeanException iabe) { // Can't happen
            iabe.printStackTrace();
        }
        return authenticator;
    }

}
