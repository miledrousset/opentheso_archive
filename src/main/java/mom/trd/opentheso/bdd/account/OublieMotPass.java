/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.bdd.account;

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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import mom.trd.opentheso.SelectedBeans.SelectedCandidat;
import mom.trd.opentheso.bdd.helper.UserHelper;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "oublie", eager = true)
@SessionScoped

public class OublieMotPass {

    private String email;
    private String nomUsu;
    private String passsansmd5;
    private String newpass;
    private String confirmpass;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    /**
     * s'appeléer depuis donwloadBean ou on pass le nom et le mail del usuaire
     * que a demandé le nouvelle pass
     *
     * @param ds
     * @param nom
     * @param mail
     */
    public void vide(HikariDataSource ds, String nom, String mail) throws MessagingException {
        String nouvellePass = "";
        String nouvelleSansMD5 = "";
        nomUsu = nom;
        email = mail;//change a l'heure de partir ca vien de donwloadBean
        Statement stmt;
        if (email != null && nomUsu != null) {
            try {
                Connection conn = ds.getConnection();
                stmt = conn.createStatement();
                try {
                    String query = "Select id_user from users where mail ='" + email + "'";
                    stmt.executeQuery(query);
                    nouvelleSansMD5 = genererNouvellePass();
                    nouvellePass = MD5Password.getEncodedPassword(nouvelleSansMD5);
                    envoimail(nomUsu, email, nouvelleSansMD5); 
                    insertNP(ds, nouvellePass);
                } finally {
                    stmt.close();
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Injection a la BDD de le pass en motpasstemp, del usuaire que la demandé
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
                String queryAjouPass = "update users set motpasstemp ='" + nouvellePass + "' where mail = '" + email + "'AND username ='" + nomUsu + "'";
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
     * cette funtion on permit de créer une password
     *
     * @return
     */
    private String genererNouvellePass() {
        String code = "";
        int sum = 0;
        String[] alfa = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        while (sum < 10) {
            int numRandon = (int) Math.round(Math.random() * 36);
            code += alfa[numRandon];
            sum++;
        }
        System.out.println(code);

        passsansmd5 = code;
        return code;
    }

    /**
     * cette funtion c'est pour verifier que c'est pass que on besoin changer
     *
     * @param ds
     * @param ancien
     * @return
     * @throws SQLException
     */
    private boolean cestlememmepass(HikariDataSource ds, String ancien) throws SQLException {
        Statement stmt;
        Connection conn;
        ResultSet resultSet;
        boolean sort = false;
        String ancianencodify = MD5Password.getEncodedPassword(ancien);
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    String query = "SELECT username FROM users WHERE motpasstemp='" + ancianencodify + "' and active=true";
                    resultSet = stmt.executeQuery(query);

                    if (resultSet.next()) {
                        sort = true;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sort;
    }

    /**
     * cette funtion c'est la funtion que realise le update dans la table et
     * efface le ancien motpasstemp;
     *
     * @param ds
     * @param Pass
     * @param ConfirmPass
     * @param ancien
     * @return
     * @throws SQLException
     */
    public boolean faireChangePass(HikariDataSource ds, String Pass, String ConfirmPass, String ancien) throws SQLException {
        String passwordencoding = MD5Password.getEncodedPassword(Pass);
        Statement stmt, stmt1;
        boolean ok = false;
        if (cestlememmepass(ds, ancien)) {
            ancien = MD5Password.getEncodedPassword(ancien);
            try {
                Connection conn = ds.getConnection();
                stmt = stmt1 = conn.createStatement();
                try {
                    System.out.println(Pass);
                    String queryAjouPass = "update users set password ='" + passwordencoding + "' where motpasstemp ='" + ancien + "'";
                    String queryEfacepasstemp = "update users set motpasstemp = NULL where motpasstemp ='" + ancien + "'";
                    stmt.executeUpdate(queryAjouPass);
                    stmt1.executeUpdate(queryEfacepasstemp);
                    ok = true;
                } finally {
                    stmt.close();
                    stmt1.close();
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ok;
    }

    private ResourceBundle getBundlePref() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
        return bundlePref;
    }

    private void envoimail(String nomUsu, String email, String pass) throws MessagingException {

        ResourceBundle bundlePref = getBundlePref();

        java.util.Properties props = new java.util.Properties();
        props.setProperty("mail.transport.protocol", bundlePref.getString("protocolMail"));
        props.setProperty("mail.smtp.host", bundlePref.getString("hostMail"));
        props.setProperty("mail.smtp.port", bundlePref.getString("portMail"));
        props.setProperty("mail.smtp.auth", bundlePref.getString("authMail"));
        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(bundlePref.getString("mailFrom")));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        msg.setSubject("Recuperation de Pass");
        msg.setText("Cher utilisateur " + nomUsu + ". Votre nouvelle pass  c'est " + pass);

        SMTPTransport transport = (SMTPTransport) session.getTransport(bundlePref.getString("transportMail"));
        transport.connect();
        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
        transport.close();

        /*
        String host= "localhost";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session sesion= Session.getDefaultInstance(props, new javax.mail.Authenticator() 
        {
            protected PasswordAuthentication getPasswordAuthentication() 
            {
                return new PasswordAuthentication("username","password");
            }
        });

        try{
            MimeMessage message = new MimeMessage(sesion);
            message.setFrom(new InternetAddress("admin@domaine.fr"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("recu pass");
            message.setContent("<h1>test d'essai</h1>","text/html");
            message.setText("Dear"+ nomUsu+ "your new pass is "+ pass);
            Transport.send(message);
            System.out.println("enviado");
        }
        catch(MessagingException e){
            throw new RuntimeException(e);
        }*/
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

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

}
