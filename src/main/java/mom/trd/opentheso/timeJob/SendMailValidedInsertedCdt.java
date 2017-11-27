/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.Date;

/**
 * Cette classe doit permettre de recupérer une liste de candidat
 * qui ont été insérés ou validés ou refusés sur une perdiode donnée
 * et une liste de destinataires dans un objet MessageCdt
 * puis doit effectuer l'envoi mail en appellant la méthode de la classe mère
 * send()
 *
 * @author jm.prudham
 */
public class SendMailValidedInsertedCdt extends SendMail implements Runnable {

    @Override
    public void run() {
        String idTheso=getTheso();
        Date d=id_theso.get(idTheso);
        System.out.println("valided inserted cdt date in methode get "+d.toString());
        MessageCdt mess=getValue(idTheso,d);
       int ret=updateDateRoutine(idTheso,new Date());
       System.out.println("retour de la fonction updateDateRoutine dans SendMailValidedInsertedCdt : "+ret+" id thesaurus "+super.getTheso() );
       id_theso.replace(idTheso,new Date());
       this.send(mess);
    }

   /**
    * #JM
    * recupère un objet MessageCdt qui a comme attribut la liste des 
    * destinataires et le corps du message
    * @param idTheso
    * @param d
    * @return 
    */
    MessageCdt getValue(String idTheso, Date d) {
        return new BackgroundMailSenderHelper().
                selectMessageValidedInsertedCdt(idTheso,d,new Date(),poolConnexion);
    }
     /**
      * #JM
     * Permet de mettre une nouvelle date comme début de période de recherche
     * de modifiation ou de création de candidat
     * 
     * @param idTheso
     * @param date
     * @return 
     */
    protected int updateDateRoutine( String idTheso, Date date) {
        String colonne="debut_env_cdt_valid";//colonne dans la bdd dans la table routine_mail
        return new BackgroundMailSenderHelper().updateRoutine(this.poolConnexion,colonne,idTheso,date);
    }
    /**
     * 
     * @param mess 
     */
      void send(MessageCdt mess){
        System.out.println("send in SendMAilValidedInserted ");
        super.send(mess,"rapport d\'activité des candidats insérés validés ou refusés");
    }
    
    
}
