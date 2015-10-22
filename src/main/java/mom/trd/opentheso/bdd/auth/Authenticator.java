/**
 * 
 */
package mom.trd.opentheso.bdd.auth;

import mom.trd.opentheso.bdd.account.Account;
import mom.trd.opentheso.bdd.auth.data.AuthBean;
import mom.trd.opentheso.bdd.auth.exceptions.AuthenticationException;
import mom.trd.opentheso.bdd.auth.exceptions.AuthenticatorConnectionException;
import mom.trd.opentheso.bdd.auth.exceptions.InvalidAuthBeanException;

/**
 * @author Persee team
 */
public abstract class Authenticator {
    /**
     * Establish a "connection" to the directory.
     */
    public abstract void connect() throws AuthenticatorConnectionException;
    /** 
     * @return Returns a new instance (like a clone).
     */
    public abstract Authenticator getClone();
    /**
     * Log into the directory and close the connection to the directory after
     * the "log in" action was consumed (successfully or not).
     * @param login A directory account login.
     * @param password A directory account password.
     * @return Returns The user account.
     * @throws AuthenticationException If an error occured while authenticating
     *         a user.
     */
    public abstract Account login(String login, String password)
            throws AuthenticationException;
    /**
     * @param bean The needed infos to establish a connection to the directory.
     * @throws InvalidAuthBeanException In case we provide invalid infos.
     */
    public abstract void setAuthBean(AuthBean bean)
            throws InvalidAuthBeanException;
}
