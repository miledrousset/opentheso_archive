/**
 *
 */
package mom.trd.opentheso.bdd.account;

import java.io.Serializable;

/**
 * @author Persee team
 */
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String firstname;
    private String lastname;
    private String mail;
    private String user;
    private String uid;

    public String toString() {

        return firstname + " " + lastname + " - " + mail + " ( " + user + " / " + uid + " )";
    }
    private Group userGroup;

    /**
     * @return Returns the firstname.
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @return Returns the lastname.
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @return Returns the mail.
     */
    public String getMail() {
        return mail;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return user;
    }

    /**
     * @return Returns the userGroup.
     */
    public Group getUserGroup() {
        return userGroup;
    }

    /**
     * @param firstname The firstname to set.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @param lastname The lastname to set.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @param mail The mail to set.
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @param user The user to set.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @param userGroup The userGroup to set.
     */
    public void setUserGroup(Group userGroup) {
        this.userGroup = userGroup;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
