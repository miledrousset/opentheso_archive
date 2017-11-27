/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import com.sun.mail.smtp.SMTPTransport;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *Cette classe doit configurer un envoi mail d'après les proprités du fichier
 * préférences
 * Doit charger un message 
 * Doit enfin l'envoyer en mail
 * @author jm.prudham
 */

public class PreferencesMail {
    private final Log log = LogFactory.getLog(PreferencesMail.class);
    private Properties properties;
    Session session;
    Message msg;
    boolean status=false;
    
    /**
     * Constructeur
     * #JM
     * Le constructeur lit les propriété du fichier preferences
     */
    public PreferencesMail(){
        properties = new Properties();
        try {
            InputStream inputStream = Thread.currentThread()
                   .getContextClassLoader().getResourceAsStream("preferences.properties");
            if (inputStream != null) {
                    properties.load(inputStream);
                }
            }
        catch (IOException ex) {
                log.error("error loading PreferenceMail()",ex);
            }
    }
    /**
     * loadConfig()
     * #JM
     * Doit configurer l'obhet MimeMessage avec les propriété chargés par 
     * le constructeur
     */
    public void loadConfig(){
       
                java.util.Properties props = new java.util.Properties();
                props.setProperty("mail.transport.protocol", properties.getProperty("protocolMail"));
                props.setProperty("mail.smtp.host", properties.getProperty("hostMail"));
                props.setProperty("SMTP_PORT_PROPERTY",properties.getProperty("portMail"));
                props.setProperty("mail.smtp.auth",properties.getProperty("authMail"));
                session = Session.getInstance(props);
                msg = new MimeMessage(session);
                try{
                msg.setFrom(new InternetAddress(properties.getProperty("mailFrom")));
                
                }
                catch(MessagingException  e1){
                    log.error("error seting destinators for message msg "+msg.toString(),e1);
                }
               
    }
    /**
     * configMessage
     * 
     * Doit configurer le MimeMessage, avec une liste de destinataires, un
     * corps de message , et un sujet de message, les destinataires doivent
     * être séparés par une virgule dans la chaîne de caractère en paramètre
     * si il y a des crochets dans la chaîne ils sont supprimés(issus de toString)
     * Vérifie que les adresses passés dans la liste de destinataires sont au 
     * bon format
     * @param destinataires : String 
     * @param corps_message : String
     * @param sujet 
     */
    public void configMessage( MessageCdt message,String sujet){
            
            ArrayList<String> destinataires=message.getDestinataires();
            try{
                for(String address:destinataires){
                    if(address.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")){
                        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
                    }
                }
            }
            catch(MessagingException e ){
                    log.error("error while set recipient internet address for message"+msg.toString(),e);
            }
            try{
               
               msg.setSubject(sujet);
            }
            catch(MessagingException e ){
                    log.error("error while set subject for message"+msg.toString(),e);
            }
            try{
                String content=addHtml(message.getCorps_message(),sujet);
               msg.setContent(content,"text/html; charset=utf-8");
            }
            catch(MessagingException e ){
                    log.error("error while set Text for message"+msg.toString(),e);
            }
    }
    /**
     * sendmessage
     * #JM
     * Doit envoyer le message MimeMessage msg qui a été préalablement configuré
     * avec les autres méthodes de la classe
     */
    public void sendMessage(){
            SMTPTransport transport=null;
            try{
               transport = (SMTPTransport) session.getTransport(properties.getProperty("transportMail"));
           
                try{
                    transport.connect();
                    try{    
                    transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));

                    }
                    finally{
                        transport.close();
                    }
                 }catch(MessagingException e){
                    log.error("error while connecting or sending message on transport ",e);

                 }
            }
            catch(NoSuchProviderException e){
                log.error("error while geting transport from session :"+session.toString()+" properties"+properties.getProperty("transportMail"),e);
            }
                status = true;
    }
    
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Message getMsg() {
        return msg;
    }

    public boolean isStatus() {
        return status;
    }

    private String addHtml(String corps_message,String sujet) {
        String head=" <body>\n <div><h1>"+sujet+"</h1>";
        String footer="</div>\n  </body>";
        String all=head+corps_message.replace("\\[","").replace("\\]","")+footer;
        return all;
    }
    
}
