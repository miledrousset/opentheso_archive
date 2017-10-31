/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jm.prudham
 */
public class CopyrightHelper {
  
    private final Log log = LogFactory.getLog(CopyrightHelper.class);
    private boolean queryReturnResult;
    
    
    /**
    *function selectCopyright
    *
    * Cette Fonction va trouver la v aleur copyright pour un identifiant de thesaurus
    *Elle est appellée depuis manageResult();
    *
    *@param String IdTheso : cle primaire d'un thesaurus dans la BDD
    *@param Connexion connect : ressource de connexion 
    *@return ResultSet result
    */
    private String selectCopyright(String idTheso, Connexion connect) {
        String resString="";
        ResultSet result;
        try{
            try(Connection conn = connect.getPoolConnexion().getConnection(); Statement stmt = conn.createStatement()) {
                String query="SELECT copyright FROM copyright WHERE copyright.id_thesaurus='"+idTheso+"'";
                result=stmt.executeQuery(query);
                try{
                    if(result.next()){
                        resString=result.getString("copyright");
                        this.queryReturnResult=true;
                        }
                    else{
                  
                        this.queryReturnResult=false;
                
                    }
                }catch(SQLException sqle){
            
                this.log.error("error while trying to proced result from database",sqle);
                }
            }
        }catch(SQLException sqle){
                
            this.log.error("error while selecting copyright form database",sqle);
        }
        
        
        return resString;
        
        
        
    }
    
    /**
     * function UpdateCopyright
     * 
     * Cette function doit mettre à jour avec un update SQl la valeur du copyright pour l'identifiant theausurus
     * @param idTheso : identifiant du thesaurus à mettre à jour
     * @param copyright: nouvelle valeur du copyright
     * @param connect: ressource de connexion
     * @return boolean : true si le nombre de ligne est supérieur à 0
     */
    public boolean updateCopyright(String idTheso,String copyright,Connexion connect){
        
        boolean r=false;
        
        try{
            try(Connection conn = connect.getPoolConnexion().getConnection()) {
                String sql="UPDATE public.copyright SET copyright=? WHERE id_thesaurus=?";

                PreparedStatement stmt=conn.prepareStatement(sql);

                stmt.setString(1,copyright);

                stmt.setString(2,idTheso);

                try{
                    r = (stmt.executeUpdate()) > 0;
                }finally{
                    
                    stmt.close();
                    
                    
                }
            }
        }catch(SQLException sqle){
                
            this.log.error("erro while updating copyright in the database",sqle);    
        }
        
        return r;
            

                
    }
    /**
     * fonction insertCopyright 
     * 
     * Cette fonction doit insérer une valeur de copyright lorsque la valeur n'existe pas et donc que l'identifiant du thesaurus n'existe pas non plus dans la BDD  
     * 
     * @param idTheso : idenitifiant du theasurus à insérer dasn la BDD
     * @param copyright : valeur du copyright à insérer dans la bdd
     * @param connect : ressource de connection
     * @return true si le nombre de ligne modifié est supérieur à 0
     */
    public boolean insertCopyright(String idTheso,String copyright,Connexion connect){
      
        int updateCount=0;
        
        try{
            try(Connection conn = connect.getPoolConnexion().getConnection()) {
                
                String sql="INSERT INTO public.copyright (id_thesaurus,copyright) VALUES (?,?)";
                PreparedStatement stmt=conn.prepareStatement(sql);

                stmt.setString(1,idTheso);

                stmt.setString(2,copyright);
                try{
                    
                    updateCount=stmt.executeUpdate();
                }
                finally{
                    stmt.close();
                }
            }
        }catch(SQLException sqle){
            
            this.log.error("error while insert copyright in the database ", sqle);
        }
    
        return updateCount > 0;
        
        
    }
    
    /**
     * Fonction manageResult
     * Cette fonction doit retourner la valeur du copyright associé au numéro du thesaurus de la BDD 
     * Elle change également la valeur de la variable privée queryReturnResult, si il existe une valeur dans 
     * la bdd cette fonction met queryReturnResul à true, sinon à false
     * @param idTheso : identifant du thésaurus 
     * @param connect : ressource de connexion
     * @return 
     */
    public String manageResult(String idTheso, Connexion connect){
        String resString=this.selectCopyright(idTheso,connect);
         
        return resString;
    }
    /**
     * fonction hasQueryReturnResult
     * Getter pour le boolean queryReturnResult (voir manageResult)
     * @return boolean
     */
    public boolean hasQueryReturnResult() {
        return queryReturnResult;
    }

   
    
}
