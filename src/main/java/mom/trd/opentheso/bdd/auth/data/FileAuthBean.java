/**
 * 
 */
package mom.trd.opentheso.bdd.auth.data;

import java.io.File;

/**
 * @author Persee team
 */
public class FileAuthBean extends AuthBean {
    private File file;
    private String baseId;
    /*
     * (non-Javadoc)
     * @see fr.persee.aldo.auth.data.AuthBean#getURL()
     */
    public String getURL() {
        return file.toURI().toString();
    }
    /**
     * @return Returns the file.
     */
    public File getFile() {
        return file;
    }
    /**
     * @param file The file to set.
     */
    public void setFile(File file) {
        this.file = file;
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
