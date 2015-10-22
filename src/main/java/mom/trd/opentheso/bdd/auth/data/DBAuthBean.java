/**
 * 
 */
package mom.trd.opentheso.bdd.auth.data;


/**
 * @author Persee team
 */
public class DBAuthBean extends AuthBean {
    private String host;
    private String login;
    private String name;
    private String passwd;
    private String port;
    private String versionExternal;
    private String dbId;
    private String baseId;
    
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
     * @return Returns the name.
     */
    public String getName() {
        return name;
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
    /*
     * (non-Javadoc)
     * @see fr.persee.aldo.auth.data.AuthBean#getURL()
     */
    public String getURL() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + name;
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
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
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
	/**
	 * @return the versionExternal
	 */
	public String getVersionExternal()
	{
		return versionExternal;
	}
	/**
	 * @param versionExternal the versionExternal to set
	 */
	public void setVersionExternal(String versionExternal)
	{
		this.versionExternal = versionExternal;
	}
	/**
	 * @return the dbId
	 */
	public String getDbId()
	{
		return dbId;
	}
	/**
	 * @param dbId the dbId to set
	 */
	public void setDbId(String dbId)
	{
		this.dbId = dbId;
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
}
