/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.SelectedBeans.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;

/**
 *
 * @author miled.rousset
 */
@ManagedBean(name = "addSubGroup")
@SessionScoped

public class AddSubGroup {

    private String titleGroup;
    private String typeDomainePere;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    public void init(String idGroup, String idTheso) {
        typeDomainePere = new GroupHelper().getTypeGroupPere(connect.getPoolConnexion(), idGroup, idTheso);
    }

    public String getTypeDomainePere() {
        return typeDomainePere;
    }

    public void setTypeDomainePere(String typeDomainePere) {
        this.typeDomainePere = typeDomainePere;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getTitleGroup() {
        return titleGroup;
    }

    public void setTitleGroup(String titleGroup) {
        this.titleGroup = titleGroup;
    }

}
