/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.ArrayList;

/**
 *Cette classe est utilisé pour avoir une liste de destinataire et un corps de
 * message pour l'envoi par mail
 * @author jm.prudham
 */
class MessageCdt {
    
    private ArrayList<String> corps_message;
    private ArrayList<String> destinataires;

    public MessageCdt(ArrayList<String> corps_message, ArrayList<String> destinataires) {
        this.corps_message = corps_message;
        this.destinataires = destinataires;
    }

    public String getCorps_message() {
        String ret="<div style='width:100%;flex-direction:row;justify-content:flex-start;align-items:flex-start'>";
        for(String str:corps_message){
            ret+=str+" ";
        }
        return ret+"</div>";
    }

    public ArrayList<String> getDestinataires() {
        return destinataires;
    }
    public boolean isEmpty(){
        return corps_message.isEmpty();
    }
    public boolean hasEmptyAddress(){
        return destinataires.isEmpty();
    }

    public void setDestinataires(ArrayList<String> destinataires) {
        this.destinataires = destinataires;
    }
    
    
}
