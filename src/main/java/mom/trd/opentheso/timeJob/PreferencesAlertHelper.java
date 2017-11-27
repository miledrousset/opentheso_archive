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
import java.util.Date;
import mom.trd.opentheso.bdd.helper.Connexion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jm.prudham
 */
public class PreferencesAlertHelper {
     private final Log log = LogFactory.getLog(PreferencesAlertHelper.class);
    
    public AlertStruct loadValues(HikariDataSource ds, String idTheso){
        Connection conn;
        PreparedStatement stmt;
        ResultSet rs;
        boolean alert=false;//valeur par défaut
        Date debut_env_propos=new Date();//valeur par défaut
        Date debut_env_valid=new Date();//valeur par défaut
        int period_env_propos=7;//valeur par défaut
        int period_env_valid=7;//valeur par défaut
        
        try{
            conn=ds.getConnection();
            try{
                String sql="SELECT * FROM routine_mail WHERE id_thesaurus=?";
                stmt=conn.prepareStatement(sql);
                stmt.setString(1,idTheso);
                try{
                    rs=stmt.executeQuery();
                    while(rs.next()){
                        alert=rs.getBoolean("alert_cdt");
                        debut_env_propos=rs.getDate("debut_env_cdt_propos");
                        debut_env_valid=rs.getDate("debut_env_cdt_valid");
                        period_env_propos=rs.getInt("period_env_cdt_propos");
                        period_env_valid=rs.getInt("period_env_cdt_valid");
                    }
                }
                finally{
                    stmt.close();
                }
            }
            finally{
                conn.close();
            }
        }
        catch(SQLException e){
            log.error("error while selection values from routine_alert table for id thesaurus "+idTheso,e);
        }
        
        return new AlertStruct(idTheso,alert,debut_env_propos,debut_env_valid,period_env_propos,period_env_valid);
    }

     /**
     * updateRoutineMail
     * #JM
     * Méthode pour updater la table routine_mail les valeurs 
     * debut_env_cdt_porpos, debut_env_cdt_valid,period_env_cdt_propos,
     * period_env_cdt_valid pour l'envoi des récapitulatifs de porpositions  
     * aux admins et des candidats en insertion ou validation aux utilisateurs 
     * et aux admins
     * 
     * @param thesaurusEnAcces
     * @param date_debut_envoi_mail
     * @param periode_envoi
     * @param poolConnexion
     * @return 
     */
    int updateRoutineMail(String thesaurusEnAcces, Date date_debut_envoi_propos,
            Date date_debut_envoi_valid, int periode_envoi_propos,
            int periode_envoi_valide,HikariDataSource poolConnexion) {
        Connection conn;
        PreparedStatement stmt;
        int ret=-1;
        try{
            conn=poolConnexion.getConnection();
            try{
                String sql="UPDATE routine_mail SET (debut_env_cdt_propos,"
                        + "debut_env_cdt_valid,period_env_cdt_propos,"
                        + "period_env_cdt_valid)=(?,?,?,?) WHERE id_thesaurus=?";
                stmt=conn.prepareStatement(sql);
                java.sql.Date sqlDate1=new java.sql.Date(date_debut_envoi_propos.getTime());
                java.sql.Date sqlDate2=new java.sql.Date(date_debut_envoi_valid.getTime());
                stmt.setDate(1,sqlDate1);
                stmt.setDate(2,sqlDate2);
                stmt.setInt(3, periode_envoi_propos);
                stmt.setInt(4,periode_envoi_valide);
                stmt.setString(5,thesaurusEnAcces);
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
        }
        catch(SQLException e){
            log.error("error while updating preference for thesaurus id "+thesaurusEnAcces,e);
        }
        return ret;
    }
    
    /**
     * cancelAlert
     * #JM
     * fonction pour updater la table routine_mail 
     * et passer le champ alert_cdt à false 
     * 
     * @param thesaurusEnAcces
     * @param ds
     * @return 
     */
    public int setAlert(String thesaurusEnAcces,boolean alert,HikariDataSource ds) {
        Connection conn;
        PreparedStatement stmt;
        int ret=-1;
        try{
            conn=ds.getConnection();
            try{
                String sql="UPDATE routine_mail SET alert_cdt=? WHERE id_thesaurus=?";
                stmt=conn.prepareStatement(sql);
                stmt.setBoolean(1, alert);
                stmt.setString(2,thesaurusEnAcces);
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
        }
        catch(SQLException e){
            log.error("error while updating table preference for id thesaurus "+thesaurusEnAcces, e);
        }
        
        
       return ret;
    }
    /**
     * #JM
     * méthode pour insérer des nouvelles valeurs dans la table routine_mail
     * 
     * 
     * @param poolConnexion
     * @param idTheso
     * @param debutEnvPropos
     * @param debutEnvValid
     * @param periodEnvPropos
     * @param periodEnvValid
     * @return 
     */
    public int insertRoutineMail(HikariDataSource poolConnexion, String idTheso,
            Date debutEnvPropos,Date debutEnvValid,int periodEnvPropos,
            int periodEnvValid){
        int ret=-1;
        Connection conn;
        PreparedStatement stmt;
        try{
            conn=poolConnexion.getConnection();
            try{
                String sql="INSERT INTO routine_mail"
                        + "(id_thesaurus,alert_cdt,debut_env_cdt_propos,"
                        + "debut_env_cdt_valid,period_env_cdt_propos,"
                        + "period_env_cdt_valid)"
                        + " VALUES (?,?,?,?,?,?)";
                stmt=conn.prepareStatement(sql);
                stmt.setString(1,idTheso);
                stmt.setBoolean(2, true);
                stmt.setDate(3, new java.sql.Date(debutEnvPropos.getTime()));
                stmt.setDate(4,new java.sql.Date(debutEnvValid.getTime()));
                stmt.setInt(5,periodEnvPropos);
                stmt.setInt(6,periodEnvValid);
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
        }
        catch(SQLException e){
            log.error("error while inserting in BDD table routine_mail idThesaurus :"
                    +idTheso+" date :"+debutEnvPropos+" , "+debutEnvValid+""
                            + " period :"+periodEnvPropos+", "+periodEnvValid,e);
        }
        
        return ret;
    }

  
}
