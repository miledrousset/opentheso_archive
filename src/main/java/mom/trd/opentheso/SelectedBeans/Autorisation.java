/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;


import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import mom.trd.opentheso.autorisation.AutorisationHelper;
import mom.trd.opentheso.autorisation.AutorisationStruct;

/**
 *
 * @author jm.prudham
 */

@ManagedBean(name ="autorisation", eager = true)
@SessionScoped
public class Autorisation {
@ManagedProperty(value = "#{poolConnexion}")
private Connexion connect;
    @ManagedProperty(value = "#{currentUser}") 
    private CurrentUser2 currentUser;


private ArrayList<AutorisationStruct> listAutorisation=new ArrayList<>();
/**
     * Creates a new instance of autorisation
     */
    public Autorisation() {
    }
    public void actionButton(ActionEvent event){
        System.out.println("action event "+event.toString());
    }
    public void init(){
        AutorisationHelper ah=new AutorisationHelper();
        listAutorisation=ah.getRights(connect.getPoolConnexion(),currentUser.getUser().getIdUser());
    }

    public ArrayList<AutorisationStruct> getListAutorisation() {
        return listAutorisation;
    }

    public void setListAutorisation(ArrayList<AutorisationStruct> listAutorisation) {
        this.listAutorisation = listAutorisation;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public CurrentUser2 getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser2 currentUser) {
        this.currentUser = currentUser;
    }
    
}
