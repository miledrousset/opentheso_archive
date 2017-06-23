/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.helper;

/**
 *
 * @author antonio.perez
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sun.mail.smtp.SMTPTransport;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.SelectedBeans.LanguageBean;
import mom.trd.opentheso.bdd.tools.MD5Password;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import javax.faces.bean.ManagedProperty;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import mom.trd.opentheso.SelectedBeans.CurrentUser;

/**
 *
 * @author antonio.perez
 */
public class ForgetPasswordHelper {

    private String email;
    private String nomUsu;
    private String passsansmd5;
    private String newpass;
    private String confirmpass;

    private String emailTitle;
    private String emailMessage;
    private String pseudoMessage;

    @ManagedProperty(value = "#{user1}")
    private CurrentUser user;

    /**
     * s'appeléer depuis donwloadBean ou on pass le nom et le mail del usuaire
     * que a demandé le nouvelle pass
     *
     * @param ds
     * @param mail
     * @return
     * @throws javax.mail.MessagingException
     */
    public boolean forgotPass(HikariDataSource ds, String mail) throws MessagingException {
        String nouvellePass;
        String nouvelleSansMD5;
        email = mail;//change a l'heure de partir ca vien de donwloadBean
        String pseudo;
        if (email == null) {
            return false;
        }
        UserHelper userHelper = new UserHelper();
        if (userHelper.isUserMailExist(ds, mail)) {
            ToolsHelper toolsHelper = new ToolsHelper();
            nouvelleSansMD5 = toolsHelper.getNewId(10);
//            genererNouvellePass();
            nouvellePass = MD5Password.getEncodedPassword(nouvelleSansMD5);
            pseudo = userHelper.getNameUser(ds, mail);
            envoiEmail(email, nouvelleSansMD5, pseudo);
            insertNP(ds, nouvellePass);
            return true;
        }
        return false;
    }

    /**
     * Injection a la BDD de le pass en motpasstemp del usuaire que la demandé
     *
     * @param ds
     * @param nouvellePass
     */
    private void insertNP(HikariDataSource ds, String nouvellePass) {
        Statement stmt;
        try {
            Connection conn = ds.getConnection();
            stmt = conn.createStatement();
            try {
                String queryAjouPass = "update users set password ='" + nouvellePass + "', passtomodify = true where mail = '" + email + "'";
                stmt.executeUpdate(queryAjouPass);
            } finally {
                stmt.close();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * cette funtion c'est la funtion que realise le update dans la table et
     * efface le ancien motpasstemp;
     *
     * @param ds
     * @param Pass
     * @param ConfirmPass
     * @param id
     * @return
     * @throws SQLException
     */
    public boolean faireChangePass(HikariDataSource ds, String Pass, String ConfirmPass, int id) throws SQLException {
        String passwordencoding = MD5Password.getEncodedPassword(Pass);
        Statement stmt, stmt1;
        boolean ok = false;
        //if (cestlememmepass(ds, ancien)) {
        //ancien = MD5Password.getEncodedPassword(ancien);//transform le pass en format encrypt pour la BDD
        try {
            Connection conn = ds.getConnection();
            stmt = stmt1 = conn.createStatement();
            try {
                //System.out.println(Pass);
                String queryAjouPass = "update users set password ='" + passwordencoding
                        + "',passtomodify = false where id_user ='" + id + "'";//on mettre a jour le nouvelle pass dans le memme colon que le motpasstemp
                stmt.executeUpdate(queryAjouPass);
                ok = true;
            } finally {
                stmt.close();
                stmt1.close();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        //}
        return ok;
    }

    /**
     *
     * @return
     */
    private ResourceBundle getBundlePref() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
        return bundlePref;
    }

    /**
     * Envoi une email a "nonUsu" a la direcction "email" pour l'indiquer son
     * nouvelle "pass"
     *
     * @param email
     * @param pass
     * @throws MessagingException
     */
    private void envoiEmail(String email, String pass, String pseudo) throws MessagingException {

        //ResourceBundle bundlePref = getBundlePref();
        java.util.Properties props = new java.util.Properties();
        props.setProperty("mail.transport.protocol", user.getNodePreference().getProtcolMail());
        props.setProperty("mail.smtp.host", user.getNodePreference().getHostMail());
        Integer temp = user.getNodePreference().getPortMail();
        props.setProperty("mail.smtp.port", temp.toString());
        Boolean temp2 = user.getNodePreference().isAuthMail();
        props.setProperty("mail.smtp.auth",temp2.toString());
        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(user.getNodePreference().getMailFrom()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        msg.setSubject(emailTitle); /// mot.titlePass

        msg.setText(emailMessage + pass + "\n" + pseudoMessage + pseudo);

        SMTPTransport transport = (SMTPTransport) session.getTransport(user.getNodePreference().getTransportMail());
        transport.connect();
        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    /**
     * getter and setter
     *
     * @return
     */
    public String getNewpass() {
        return newpass;
    }

    public void setNewpass(String newpass) {
        this.newpass = newpass;
    }

    public String getConfirmpass() {
        return confirmpass;
    }

    public void setConfirmpass(String confirmpass) {
        this.confirmpass = confirmpass;
    }

    public String getNomUsu() {
        return nomUsu;
    }

    public void setNomUsu(String nomUsu) {
        this.nomUsu = nomUsu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasssansmd5() {
        return passsansmd5;
    }

    public void setPasssansmd5(String passsansmd5) {
        this.passsansmd5 = passsansmd5;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public void setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public String getPseudoMessage() {
        return pseudoMessage;
    }

    public void setPseudoMessage(String pseudoMessage) {
        this.pseudoMessage = pseudoMessage;
    }

}
