/**
 * 
 */
package mom.trd.opentheso.bdd.helper;


import org.apache.commons.dbcp.BasicDataSource;


/**
 * @author Persee team
 */
public class BaseHelper {
    
    /**
     * 
     * @param user
     * @param passwd
     * @param url
     * @return
     */
    public static BasicDataSource buildDataSource(String user, String passwd,
            String url) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(user);
        ds.setPassword(passwd);
        ds.setUrl(url);
        return ds;
    }
    /**
     * 
     * @param user
     * @param passwd
     * @param url
     * @param maxActive
     * @param maxIdle
     * @param maxWait
     * @return
     */
    public static BasicDataSource buildDataSource(String user, String passwd,
            String url, int maxActive, int maxIdle, long maxWait) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(user);
        ds.setPassword(passwd);
        ds.setUrl(url);
        // Tweaking pool
        ds.setMaxActive(maxActive);
        ds.setMaxIdle(maxIdle);
        ds.setMaxWait(maxWait);
        return ds;
    }
    /**
     * Build the connection URL String.
     * @param host
     * @param port
     * @param name
     * @return uri
     */
    public static String buildConnectURI(String host, String port, String name) {
        return "jdbc:postgresql://" + host + ":" + port + "/" + name;
    }
    /**
     * Extract the name for the connect URI 
     * (should be created with buildConnectURI)
     * @param connectURI
     * @return
     */
    public static String extractNameFromConnectURI(String connectURI)
    {
    	return connectURI.replaceAll(".*/", "");
    }
    /**
     * Extract the port for the connect URI 
     * (should be created with buildConnectURI)
     * @param connectURI
     * @return
     */
    public static String extractPortFromConnectURI(String connectURI)
    {
    	return connectURI.replaceAll(".*:", "").replaceAll("/.*", "");
    }
    
}
