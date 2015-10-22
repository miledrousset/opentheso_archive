/**
 * 
 */
package mom.trd.opentheso.bdd.account;

import java.io.Serializable;

/**
 * @author Persee team
 */
public class Account implements Serializable {
    /**
    * 
    */
    private static final long serialVersionUID = 1L;
    private Group[] groups;
    private User user;
    private String baseId;
    /**
     * @return Returns the groups.
     */
    public Group[] getGroups() {
        return groups;
    }
    /**
     * @return Returns the user.
     */
    public User getUser() {
        return user;
    }
    /**
     * @param groups The groups to set.
     */
    public void setGroups(Group[] groups) {
        this.groups = groups;
    }
    /**
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
	/**
	 * @return the baseId
	 */
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
