/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.autorisation;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jm.prudham
 */
public class AutorisationHelper {
    
    
     private final Log log = LogFactory.getLog(AutorisationHelper.class);
    
    

    public ArrayList<AutorisationStruct> getRights(HikariDataSource poolConnexion, int id) {
      PreparedStatement stmt;
      ResultSet rs;
      ArrayList<AutorisationStruct> ret=new ArrayList<>();
      String sql="SELECT * FROM user_role INNER JOIN roles on user_role.id_role=roles.id WHERE id_user=? ";
      try{
          Connection conn=poolConnexion.getConnection();
          try{
              stmt=conn.prepareStatement(sql);
              stmt.setInt(1, id);
              try{
                rs=stmt.executeQuery(); 
                ret=this.handleResultSet(rs);
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
          log.error("problème de requête sql ",e);
      }
      return ret;
    }

    private ArrayList<AutorisationStruct> handleResultSet(ResultSet rs) {
        ArrayList<AutorisationStruct> ret=new ArrayList<>();
       try{
        while(rs.next()){
            AutorisationStruct as=new AutorisationStruct();
            try{
                as.setThesaurus(rs.getString("id_thesaurus"));
                as.setTypeDroitNum(rs.getInt("id_role"));
                as.setTypeDroitValue(rs.getString("name"));
            }
            finally{
              ret.add(as);
            }
               
        }
       }
       catch(SQLException e){
         log.error("problème avec le traitement du result set",e);   
            }
        return ret;
    }
    
}
