/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import mom.trd.opentheso.bdd.helper.CandidateHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.UserHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author jm.prudham
 */
public class BackgroundMailSenderHelper {
    
    private final Log log = LogFactory.getLog(BackgroundMailSenderHelper.class);
    
    //la date de début de lancement de l'alerte et la période de l'alert :
    //private HashMap<Date,Integer> poolAlert=new HashMap<>();
    
    //attribut pour avoir l'identifiant d'un thésaurus et la date 
    //depuis laquelle on regarde les changements :
    //private HashMap<String,Date> idTheso_since=new HashMap<>();
    
    /**
     * getPoolAlert
     * #JM
     * Méthode pour récupérer les thesaurus où alert_cdt est à true dans
     * la table routine_mail, on récupère dans le hashMap pool_alert 
     * la date de début de période d'envoi de mail et la période d'envoi
     * puis on retourne ce hashMap
     * On attribut aussi au hashmap idTheso_since l'identifiant thésaurus 
     * et la date depuis laquelle débute la période actuelle 
     * pour chacunes des alerts
     * 
     * 
     * @param ds
     * 
     * @return 
     */
    public ArrayList<AlertStruct> getPoolAlert(Connexion ds) {
        Connection conn2;
        Statement stmt = null;
        ResultSet rs = null;
      
        ArrayList<AlertStruct> alertStructs=null;
        
        try{
        conn2=ds.getPoolConnexion().getConnection();
            try{
            String sql="SELECT *"
           
                    + " FROM routine_mail WHERE alert_cdt=true";
            
            stmt=conn2.createStatement();
           
                try{    
                    rs=stmt.executeQuery(sql);
                   
                    
                   
                    //pas=new PoolAlertStruct();
                    alertStructs = new ArrayList<>();
                    while(rs.next()){
                        AlertStruct alertStruct = new AlertStruct();
                        alertStruct.setAlertB(rs.getBoolean("alert_cdt"));
                        alertStruct.setDate_debut_envoi_cdt_propos( rs.getDate("debut_env_cdt_propos"));
                        alertStruct.setDate_debut_envoi_cdt_valid(rs.getDate("debut_env_cdt_valid"));
                        alertStruct.setPeriod_envoi_cdt_propos(rs.getInt("period_env_cdt_propos"));
                        alertStruct.setPeriod_envoi_cdt_valid(rs.getInt("period_env_cdt_valid"));
                        alertStruct.setThesaurusEnAcces(rs.getString("id_thesaurus"));
                        alertStructs.add(alertStruct);
                    }
                       
                }
                finally{
                    stmt.close();
                }
            }    
            finally{
                conn2.close();
            }
        }
        catch(SQLException e){
            log.error("error while getting alert thesurus in getpoolAlert in backgroundMailSenderHelper ",e);
        }
        
        return alertStructs;
    }


      
   
    
    /**
     * updateRoutine
     * #JM
     * Met la date du jour dans la table routine
     * après que le thread ait été effectué pour une colonne en paramètre
     * ( soit début_env_cdt_propos soit debut_env_cdt_valid) 
     * @param poolConnexion
     * @param colonne
     * @param idTheso
     * @param date
     * @return 
     */
    public int updateRoutine(HikariDataSource poolConnexion, String colonne,
            String idTheso, Date date) {
       Connection conn;
       PreparedStatement stmt;
       int ret=-1;
       try{
           conn=poolConnexion.getConnection();
           try{
               String sql="UPDATE routine_mail SET "+colonne+"=? WHERE id_thesaurus=?";
               stmt=conn.prepareStatement(sql);
               stmt.setDate(1, new java.sql.Date(date.getTime()));
               stmt.setString(2,idTheso);
               try{
                   ret=stmt.executeUpdate();
               }
               finally{
                   stmt.close();
               }
           }
           finally{
               conn.close();
           }
       }catch(SQLException e){
           log.error("error while updating table routine_mail for id_thesaurus "+idTheso, e);
       }
       
       return ret;
    }
    /**
     * selectMessageProposeCdt
     *#JM
     *
     * récupère un corps de message et une liste de destinataire, constuits avec
     * ces valeurs un objet MessageCdt puis le retourne
     * 
     * @param idTheso
     * @param d1
     * @param d2
     * @param poolConnexion
     * @return 
     */
    MessageCdt selectMessageProposedCdt(String idTheso, Date d1,Date d2,
            HikariDataSource poolConnexion) {
        
        ArrayList<String> ret=new CandidateHelper().getListOfCdtDuringPeriod(idTheso, d1,d2, poolConnexion);
        ArrayList<String> destinataires=new UserHelper().getMailAdmin(poolConnexion, idTheso); 
        ArrayList<String> dest=reduce(destinataires);
        MessageCdt mess=new MessageCdt(ret, dest);
        return mess;
    }
    /**
     * selectMessageValidedInsertedCdt
     * #JM
     * récupère un corps de message correspondant aux candidats validé ou 
     * insérés ou refusés et une liste d'adresse mail
     * crée un objet MessageCdt avec ce corps de message et cette liste de 
     * destinataires puis le retourne
     * 
     * 
     * 
     * @param idTheso
     * @param d1
     * @param d2
     * @param poolConnexion
     * @return 
     */
     MessageCdt selectMessageValidedInsertedCdt(String idTheso, Date d1,Date d2,
             HikariDataSource poolConnexion) {
       
        ArrayList<String> destinataires=new UserHelper().getMailAdmin(poolConnexion, idTheso);
        destinataires.addAll(new UserHelper().getMailUserForCandidat(poolConnexion, idTheso, d1, d2));
        ArrayList<String> ret=new CandidateHelper().getInsertedValidedRefusedCdtDuringPeriod(poolConnexion, d1, d2, idTheso);
        MessageCdt mess=new MessageCdt(ret,reduce(destinataires));
        return mess;
    }
    
     /**
      * #JM
      * Méthode qui supprime les doublons dans une arrayList de mail
      * @param destinataires
      * @return 
      */
    private ArrayList<String> reduce(ArrayList<String> destinataires) {
        destinataires.sort(String::compareToIgnoreCase);
        String tmp="";
        ArrayList<String> ret=new ArrayList<>();
        for (String look : destinataires) {
            if(tmp.compareToIgnoreCase(look)==0){
                continue;
            }
            ret.add(look);
            tmp=look;
        }
        return ret;
    }

  
    
    

   

   
    
    
}
