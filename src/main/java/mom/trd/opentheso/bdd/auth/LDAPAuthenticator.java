/**
 * 
 */
package mom.trd.opentheso.bdd.auth;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mom.trd.opentheso.bdd.account.Account;
import mom.trd.opentheso.bdd.account.User;
import mom.trd.opentheso.bdd.auth.data.AuthBean;
import mom.trd.opentheso.bdd.auth.data.LDAPAuthBean;
import mom.trd.opentheso.bdd.auth.exceptions.AuthenticatorConnectionException;
import mom.trd.opentheso.bdd.auth.exceptions.InvalidAuthBeanException;
import mom.trd.opentheso.bdd.auth.Authenticator;

/**
 * @author Persee team
 */
public class LDAPAuthenticator extends Authenticator {
    private static Log log = LogFactory.getLog(LDAPAuthenticator.class);
    private LDAPAuthBean authBean;
    private Properties env;
    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#connect()
     */
    public void connect() throws AuthenticatorConnectionException {
        // Connect only once?
        if (env == null) {
            env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL,  authBean.getURL()  );
            env.put(Context.REFERRAL, "ignore");
                     
        }
    }

    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#login(java.lang.String, java.lang.String)
     */
    public Account login(String login, String password) {
    	
    	Account acc = null;
    	
    	
    	  try {
    		  String dn = dnFromUser(login);
    		  
    		  if(dn == null){
    			  //TODO gerer exception
    			  return null;
    		  }
    		  env.put(Context.SECURITY_PRINCIPAL, dn );
    		  env.put(Context.SECURITY_CREDENTIALS, password);
  	        
  	        InitialDirContext context = new InitialDirContext(env);
  	        
  	        SearchControls ctrls = new SearchControls();
			ctrls.setReturningAttributes(new String[] { authBean.getFirstnameLdap(), authBean.getNameLdap() ,authBean.getMailLdap()});
			ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration<SearchResult> answers;

			answers = context.search(authBean.getScope(),
					"("+authBean.getUidLdap()+"="+login+")", ctrls);

			SearchResult result;
			result= answers.next();
			
		

			//System.out.println();
			String firstname = result.getAttributes().get(authBean.getFirstnameLdap()).get().toString() ;
			String name = result.getAttributes().get(authBean.getNameLdap()).get().toString() ;
			String mail = result.getAttributes().get(authBean.getMailLdap()).get().toString() ;
		
			
  	      // User
            User user = new User();
            user.setUser(login);
            user.setFirstname(firstname);
            user.setLastname(name);
            user.setMail(mail);
            user.setUid(login);
            // Account
            acc = new Account();
            acc.setBaseId(authBean.getBaseId());
            acc.setUser(user);
  	    }
  	    catch (NamingException e) {
  	        
  	    }
    	  return acc;
    }

    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#setAuthBean(fr.persee.aldo.auth.data.AuthBean)
     */
    public void setAuthBean(AuthBean bean) throws InvalidAuthBeanException {
        // We need LDAP-style info
        if (!(bean instanceof LDAPAuthBean)) {
            throw new InvalidAuthBeanException();
        }
        authBean = (LDAPAuthBean) bean;
    }

    /* (non-Javadoc)
     * @see fr.persee.aldo.auth.Authenticator#getClone()
     */
    @Override
    public Authenticator getClone() {
        Authenticator authenticator = new LDAPAuthenticator();
        try {
            authenticator.setAuthBean(authBean);
        } catch (InvalidAuthBeanException iabe) { // Can't happen
            iabe.printStackTrace();
        }
        return authenticator;
    }
   

    private  String dnFromUser(String username) throws NamingException {
	    Properties props = new Properties();
	    props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    props.put(Context.PROVIDER_URL, "ldap://ldap.mondomaine.fr");
	    props.put(Context.REFERRAL, "ignore");

	    InitialDirContext context = new InitialDirContext(props);

	    SearchControls ctrls = new SearchControls();
	    ctrls.setReturningAttributes(new String[] { authBean.getFirstnameLdap(), authBean.getNameLdap()  });
	    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    
	    NamingEnumeration<SearchResult> answers = context.search(authBean.getScope(),
				"("+authBean.getUidLdap()+"="+username+")", ctrls);
	    
	    if(answers != null){
		    SearchResult result = answers.next();
	
		    return  result.getNameInNamespace();
	    }
	    return null;
	}
}
