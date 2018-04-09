/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper.nodes;

/**
 *
 * @author miled.rousset
 * 
 * Pour un utilisateur donnée, cette classe permet de regrouper un role et un thésaurus et un domaine (groupe dans un thésaurus) 
 */
public class NodeUserRoleThesaurus { 
    private int idRole;
    private String roleName;
    private int idGroup;
    private String idThesaurus;
    private String idGroupThesaurus;
    
    public NodeUserRoleThesaurus() {
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getIdThesaurus() {
        return idThesaurus;
    }

    public void setIdThesaurus(String idThesaurus) {
        this.idThesaurus = idThesaurus;
    }

    public String getIdGroupThesaurus() {
        return idGroupThesaurus;
    }

    public void setIdGroupThesaurus(String idGroupThesaurus) {
        this.idGroupThesaurus = idGroupThesaurus;
    }




}
