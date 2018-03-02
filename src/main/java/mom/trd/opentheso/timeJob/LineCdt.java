/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.Date;

/**
 *
 * @author jm.prudham
 */
public class LineCdt {
    private String id_thesaurus;
    private String title_thesaurus;
    private String Id_concept;
    private String valeur_lexical;
    private Date created;
    private Date modified;
    private String status;
    private String admin_message;
    private String note;
    
    
    
    public LineCdt() {
    
    }

    public void setId_thesaurus(String id_thesaurus) {
        this.id_thesaurus = id_thesaurus;
    }

    public void setTitle_thesaurus(String title_thesaurus) {
        this.title_thesaurus = title_thesaurus;
    }

    public void setId_concept(String Id_concept) {
        this.Id_concept = Id_concept;
    }

    public void setValeur_lexical(String valeur_lexical) {
        this.valeur_lexical = valeur_lexical;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdmin_message(String admin_message) {
        this.admin_message = admin_message;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    
    public String getMessage(){
        // #MR
        StringBuilder message = new StringBuilder();
        String status1;
        switch(status){
            case("a"):
                status1="en attente";
                break;
            case("r"):
                status1="refusé";
                break;                        
            case("v"):
                status1="validé";
                break;                     
            case("i"):
                status1="inséré";
                break;                         
            default:
                status1="inconnu";
                break;                 
        }         
        
        message.append("<table style=\"width: 715px; height: 321px;\" border=\"0\">");
        message.append("<tbody>"); 
        message.append("<tr>");
        message.append("<td style=\"width: 152.3px;\"><i> Candidat :");
        message.append("</i></td>");
        message.append("<td style=\"width: 546.7px;\"><b>");
        message.append(valeur_lexical);
        message.append("</b></td>");
        message.append("</tr>");
        message.append("<tr>");
        message.append("<td><i>id :");
        message.append("</i></td>");
        message.append("<td><b>");
        message.append(Id_concept);
        message.append("</b></td>");
        message.append("</tr>");
        message.append("<tr>");
        message.append("<td><i>créé le :");
        message.append("</i></td>");
        message.append("<td style=\"height: 18.2px;\"><b>");
        message.append(created);
        message.append("</b></td>");
        message.append("</tr>");
        message.append("<tr>");
        message.append("<td><i>modifié le :");
        message.append("</i></td>");
        message.append("<td><b>");
        message.append(modified);
        message.append("</b></td>");
        message.append("</tr>");
        message.append("<tr>");
        message.append("<td><i>Status :");
        message.append("</i></td>");
        message.append("<td><b>");
        message.append(status1);
        message.append("</b></td>");
        message.append("</tr>");
        message.append("<tr>");
        message.append("<td><i>Note :");
        message.append("</i></td>");
        if(note != null && !note.isEmpty()) {
            message.append("<td><b>");
            message.append(note);
            message.append("</b></td>");
            message.append("</tr>");
        }
        if(admin_message != null && !admin_message.isEmpty()) {
            message.append("<tr>");
            message.append("<td><i>Message de l'admin :");
            message.append("</i></td>");
            message.append("<td><b>");
            message.append(admin_message);
            message.append("</b></td>");
            message.append("</tr>");
        }
        message.append("<tr>");
        message.append("<td><i>Thésaurus :");
        message.append("</i></td>");
        message.append("<td><b>");
        message.append(title_thesaurus);
        message.append("</b></td>");
        message.append("</tr>");
        message.append("</tbody>");
        message.append("</table>");
        message.append("<br>");
        
        return message.toString();
        
        // ancien formatage 
        /*
        String ret="<table><legend>information sur le candidat</legend>";
        ret+="<tr><td>id_thesaurus</td><td>"+id_thesaurus+" </td><td> titre thésaurus </td><td>"+title_thesaurus+"</td></tr>";
        ret+="<tr><td>id_concept</td><td>"+Id_concept+" </td><td>valeur lexical du concept</td><td>"+valeur_lexical+"</td></tr>";
        ret+="<tr><td>créer le </td><td>"+created+" </td><td>modifier le </td><td>"+modified+" </td></tr>";
        String stat="";
        switch(status){
            case("a"):stat="<tr><td colspan='2'>Status</td> <td clospan='2' >en attente </td></tr>";
                        break;
            case("r"):stat="<tr><td colspan='2'>Status</td> <td clospan='2' > refusé </td></tr>";
                        break;
            case("v"):stat="<tr><td colspan='2'>Status</td> <td clospan='2' >  validé </td></tr>";
                        break;
            case("i"): stat="<tr><td colspan='2'>Status</td> <td clospan='2' >  inséré </td></tr>";
                        break;
            default:stat="<tr><td colspan='2'>Status</td> <td clospan='2' >  inconnu </td></tr>";
                    break;
        }
        ret+=stat;
        ret+="<tr><td colspan='2'>Note</td> <td clospan='2' >"+note+"</td></tr>";
        ret+="<tr><td colspan='2'>Note</td> <td clospan='2' >"+admin_message+"</td></tr>";
        ret+="</table>";    
        return ret;
        */    
    }
    
    
    
    
}
