/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author jm.prudham
 */
public class  SendMail implements Runnable {
    protected final Log log = LogFactory.getLog(SendMail.class);
    protected HikariDataSource poolConnexion;
    protected HashMap<String,Date> id_theso;//<th_thesaurus,date debut période>
    protected ArrayList<String> mails;
    //protected String idLang;
    
    
    
    
   
    @Override
    public void run(){
   
    }

    public HikariDataSource getPoolConnexion() {
        return poolConnexion;
    }

    public void setPoolConnexion(HikariDataSource poolConnexion) {
        this.poolConnexion = poolConnexion;
    }

    public void setPool_id_theso_since(HashMap<String,Date> pool_id_theso) {
        this.id_theso = pool_id_theso;
    }

   
    

   
    /**
     * Send
     * 
     * Méthode pour appeler un PreferenceMail et faire l'envoi du mail
     * via le PM
     * @param mess
     * @param sujet
     */
    public synchronized void  send(MessageCdt mess,String sujet) {
        if(mess.getCorps_message()==null || mess.isEmpty()  ){
           // log.error("empty corps message or wrong lenght in send() mail method");
            return;
        }
        if(mess.getDestinataires()==null || mess.hasEmptyAddress()){
            log.error("the message has not 'destinataires' fields");
            return;
        }
     
      
        PreferencesMail pm=new PreferencesMail();
        
        pm.loadConfig();
        pm.configMessage(mess,sujet);
        pm.sendMessage();
        try{
           File logmail=new File("/data/opentheso/update/logMail"+new Date().getTime()); 
           BufferedWriter bfw=new BufferedWriter(new FileWriter(logmail));
           bfw.write("----------------- log du mail---------------------------- ");
           bfw.newLine();
           bfw.write("   propriété :"+pm.getProperties().toString());
           bfw.newLine();
           bfw.write("\n------------------------------------------------------\n");
           bfw.newLine();
           bfw.write("    status :  "+pm.isStatus());
           bfw.newLine();
           for(String dest:mess.getDestinataires()){
                bfw.write("   destinataire :"+dest);
            }
           bfw.newLine();
            bfw.write("   message:     "+pm.getMsg());
            bfw.newLine();
            bfw.flush();
            bfw.close();
            
        }
        catch(   IOException e ){
            log.error("error while writing log",e);
        }
        
      
        
        return;
    }
    
      /**
     * fonction pour prendre la clé du hahMap id_theso
     * @return 
     */ 
    protected String getTheso()  {
         return this.id_theso.entrySet().iterator().next().getKey();
    }

    public ArrayList<String> getMails() {
        return mails;
    }

    public void setMails(ArrayList<String> mails) {
        this.mails = mails;
    }
   
    
    
    
    
   
    
    
    
}
