/**
 * 
 */
package mom.trd.opentheso.bdd.account;

import java.io.Serializable;

/**
 * @author Persee team
 */
public class Group implements Serializable {
    /**
    * 
    */
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
