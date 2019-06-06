/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import mom.trd.opentheso.SelectedBeans.Connexion;
import javax.faces.model.SelectItem;
import mom.trd.opentheso.bdd.helper.GroupHelper;

/**
 *
 * @author miled.rousset
 */

@ManagedBean(name = "addGroup")
@ViewScoped


public class AddGroup {

    private String selectedGroupType;
    private String titleGroup;
   
    private List<SelectItem> listGroupType;    

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    


    
    public void init(){
        titleGroup = "";
        listGroupType = new GroupHelper().getAllGroupType(connect.getPoolConnexion());
        if(!listGroupType.isEmpty()) 
            selectedGroupType = listGroupType.get(0).getLabel();
    }

    public List<SelectItem> getListGroupType() {
        return listGroupType;
    }

    public void setListGroupType(List<SelectItem> listGroupType) {
        this.listGroupType = listGroupType;
    }

    public String getSelectedGroupType() {
        return selectedGroupType;
    }

    public void setSelectedGroupType(String selectedGroupType) {
        this.selectedGroupType = selectedGroupType;
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
