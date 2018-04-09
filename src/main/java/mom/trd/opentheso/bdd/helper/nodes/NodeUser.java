package mom.trd.opentheso.bdd.helper.nodes;

import java.io.Serializable;
import java.util.ArrayList;

public class NodeUser implements Serializable {
    private int idUser;
    private String name;
    private String mail;
    private int idRole;
    private String role;
    private boolean isActive;
    private boolean isAlertMail;
    private boolean isSuperAdmin = false;
    private boolean passtomodify;
    
    ArrayList<NodeUserRoleThesaurus> nodeUserRoleThesaurus;
    ArrayList<NodeUserGroup> nodeUserGroup;    
    
    public NodeUser() {
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
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

    public boolean isIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public boolean isPasstomodify() {
        return passtomodify;
    }

    public void setPasstomodify(boolean passtomodify) {
        this.passtomodify = passtomodify;
    }

    public ArrayList<NodeUserRoleThesaurus> getNodeUserRoleThesaurus() {
        return nodeUserRoleThesaurus;
    }

    public void setNodeUserRoleThesaurus(ArrayList<NodeUserRoleThesaurus> nodeUserRoleThesaurus) {
        this.nodeUserRoleThesaurus = nodeUserRoleThesaurus;
    }

    public ArrayList<NodeUserGroup> getNodeUserGroup() {
        return nodeUserGroup;
    }

    public void setNodeUserGroup(ArrayList<NodeUserGroup> nodeUserGroup) {
        this.nodeUserGroup = nodeUserGroup;
    }
    
    
}
