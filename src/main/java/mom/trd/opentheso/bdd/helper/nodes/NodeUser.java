package mom.trd.opentheso.bdd.helper.nodes;

import java.io.Serializable;

public class NodeUser implements Serializable {
    private int id;
    private String name;
    private String mail;
    private int idRole;
    private String role;
    private String idThesaurus;
    private boolean isActive;
    private boolean isAlertMail;
    
    public NodeUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIdThesaurus() {
        return idThesaurus;
    }

    public void setIdThesaurus(String idThesaurus) {
        this.idThesaurus = idThesaurus;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsAlertMail() {
        return isAlertMail;
    }

    public void setIsAlertMail(boolean isAlertMail) {
        this.isAlertMail = isAlertMail;
    }
    
    
    
}
