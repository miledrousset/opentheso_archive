/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;


import java.util.Date;


/**
 *Cette classe doit permettre d'envoyer aux admins d'un thésaurus 
 * pour lequel la valeur alert_cdt est à true dans la table préférence
 * la liste des candidats proposés depuis la date d
 * @author jm.prudham
 */
public class SendMailProposedCdt extends SendMail implements Runnable {

    @Override
    public void run() {
         
      String idTheso;
      Date date;
      idTheso=getTheso(); 
      date=id_theso.get(idTheso);
      MessageCdt mess=this.getValue(idTheso,date);
      int ret=this.updateDateRoutine(idTheso,new Date());
      id_theso.replace(idTheso,new Date());
      mess.setDestinataires(mails);
      this.send(mess);
      
    }
    /**
     * getValue
     * #JM
     * Fonction qui permet de récupérer un objet MessageCdt d'après
     * l'identifiant thésaurus et la date après laquelle on cherche 
     * des modification ou des création en appellant la fonction 
     * selecMessageProposedCdt 
     * @param idTheso
     * @param d 
     * @return ArrayList<String>
     */
  
    public MessageCdt getValue(String idTheso, Date d) {
        
        
        return new BackgroundTimeJobHelper().selectMessageProposedCdt(idTheso,d,new Date(),poolConnexion);
       
        
    }
    
     /**
      * #JM
     *Permet de mettre à jour une date de début pour une recherche de routine 
     * 
     * @param idTheso
     * @param date
     * @return 
     */
    protected int updateDateRoutine( String idTheso, Date date) {
        String colonne="debut_env_cdt_propos";//colonne dans la bdd dans la table routine_mail
          
       
        return new BackgroundTimeJobHelper().updateRoutine(this.poolConnexion,colonne,idTheso,date);
    }
    /**
     * #JM
     * 
     * @param mess 
     */
    public void send(MessageCdt mess){
       
        super.send(mess,"rapport d\'activité des candidats proposés");
    }
    
}
