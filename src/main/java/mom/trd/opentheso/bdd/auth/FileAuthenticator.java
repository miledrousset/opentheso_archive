/**
 * 
 */
package mom.trd.opentheso.bdd.auth;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import mom.trd.opentheso.bdd.account.Account;
import mom.trd.opentheso.bdd.auth.data.AuthBean;
import mom.trd.opentheso.bdd.auth.data.FileAuthBean;
import mom.trd.opentheso.bdd.auth.exceptions.AuthenticatorConnectionException;
import mom.trd.opentheso.bdd.auth.exceptions.InvalidAuthBeanException;
import mom.trd.opentheso.bdd.auth.Authenticator;

/**
 * @author Persee team
 */
public class FileAuthenticator extends Authenticator {
    private static Log log = LogFactory.getLog(FileAuthenticator.class);
    private FileAuthBean authBean;
    @SuppressWarnings("unused")
	private Document doc;
    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#connect()
     */
    public void connect() throws AuthenticatorConnectionException {
        File authFile = authBean.getFile();
        // Check if file exists
        if (!authFile.exists()) {
            log.fatal("Authentication file is missing.");
            throw new AuthenticatorConnectionException();
        }
        // Check if file is a file (!)
        if (!authFile.isFile()) {
            log.fatal("Authentication file is not a valid file.");
            throw new AuthenticatorConnectionException();
        }
        if (!authFile.canRead()) {
            log.fatal("Authentication file can't be read.");
            throw new AuthenticatorConnectionException();
        }
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(authFile);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#login(java.lang.String, java.lang.String)
     */
    public Account login(String login, String password) {
        // TODO Do something with the Document 
        // Need to see what I'm gonna put in it before...
        
        return null;
    }

    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#setAuthBean(fr.persee.aldo.auth.data.AuthBean)
     */
    public void setAuthBean(AuthBean bean) throws InvalidAuthBeanException {
        // We need file info
        if (!(bean instanceof FileAuthBean)) {
            throw new InvalidAuthBeanException();
        }
        authBean = (FileAuthBean) bean;
    }

    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#getClone()
     */
    @Override
    public Authenticator getClone() {
        Authenticator authenticator = new FileAuthenticator();
        try {
            authenticator.setAuthBean(authBean);
        } catch (InvalidAuthBeanException iabe) { // Can't happen
            iabe.printStackTrace();
        }
        return authenticator;
    }
}
