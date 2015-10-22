/**
 * 
 */
package mom.trd.opentheso.bdd.auth.data;

/**
 * @author Persee team
 */
public class LDAPAuthBean extends AuthBean {
    private String host;
    private String login;
    private String baseDN;
    private String passwd;
    private String port;
    private String baseId;
    private String scope;
    private String firstnameLdap;
    private String nameLdap;
    private String uidLdap;
    private String mailLdap;
    

    /*
     * (non-Javadoc)
     * @see fr.persee.aldo.auth.data.AuthBean#getURL()
     */
    public String getURL() {
        String url = "ldap://" + host;
        if (port != null) {
            url += ":" + port;
        }
        //url += "/";
        return url;
    }

    /**
     * @return Returns the baseDN.
     */
    public String getBaseDN() {
        return baseDN;
    }

    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return Returns the login.
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return Returns the passwd.
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * @return Returns the port.
     */
    public String getPort() {
        return port;
    }

    /**
     * @param baseDN The baseDN to set.
     */
    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    /**
     * @param host The host to set.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @param login The login to set.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @param passwd The passwd to set.
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    /**
     * @param port The port to set.
     */
    public void setPort(String port) {
        this.port = port;
    }

	/* (non-Javadoc)
	 * @see fr.persee.aldo.auth.data.AuthBean#getBaseId()
	 */
	@Override
	public String getBaseId()
	{
		return baseId;
	}

	/**
	 * @param baseId the baseId to set
	 */
	public void setBaseId(String baseId)
	{
		this.baseId = baseId;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getFirstnameLdap() {
		return firstnameLdap;
	}

	public void setFirstnameLdap(String firstnameLdap) {
		this.firstnameLdap = firstnameLdap;
	}

	public String getNameLdap() {
		return nameLdap;
	}

	public void setNameLdap(String nameLdap) {
		this.nameLdap = nameLdap;
	}

	public String getUidLdap() {
		return uidLdap;
	}

	public void setUidLdap(String uidLdap) {
		this.uidLdap = uidLdap;
	}

	public String getMailLdap() {
		return mailLdap;
	}

	public void setMailLdap(String mailLdap) {
		this.mailLdap = mailLdap;
	}
}
