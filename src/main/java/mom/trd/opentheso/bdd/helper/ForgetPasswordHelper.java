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

    /**
     * s'appeléer depuis donwloadBean ou on pass le nom et le mail del usuaire
     * que a demandé le nouvelle pass
     *
     * @param ds
     * @param nom
     * @param mail
     * @throws javax.mail.MessagingException
     */
    public boolean forgotPass(HikariDataSource ds, String mail) throws MessagingException {
        String nouvellePass = "";
        String nouvelleSansMD5 = "";
        email = mail;//change a l'heure de partir ca vien de donwloadBean

        Statement stmt;
        UserHelper userHelper = new UserHelper();
        if (userHelper.isUserMailExist(ds, mail)) {
            if (email != null) {
                try {
                    Connection conn = ds.getConnection();
                    stmt = conn.createStatement();
                    try {
                        String query = "Select id_user from users where mail ='" + email + "'";
                        stmt.executeQuery(query);
                        nouvelleSansMD5 = genererNouvellePass();
                        nouvellePass = MD5Password.getEncodedPassword(nouvelleSansMD5);
                        envoimail(email, nouvelleSansMD5);
                        insertNP(ds, nouvellePass);
                    } finally {
                        stmt.close();
                        conn.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
     * cette funtion on permit de créer une password aleatoire que on garde en
     * passsansmd5
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
                    String query = "SELECT username FROM users WHERE password='" + ancianencodify + "' and active=true";
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
    public boolean faireChangePass(HikariDataSource ds, String Pass, String ConfirmPass, String ancien, String mail) throws SQLException {
        String passwordencoding = MD5Password.getEncodedPassword(Pass);
        Statement stmt, stmt1;
        boolean ok = false;
        if (cestlememmepass(ds, ancien)) {
            ancien = MD5Password.getEncodedPassword(ancien);//transform le pass en format encrypt pour la BDD
            try {
                Connection conn = ds.getConnection();
                stmt = stmt1 = conn.createStatement();
                try {
                    System.out.println(Pass);
                    String queryAjouPass = "update users set password ='" + passwordencoding
                            + "',passtomodify = false where mail ='" + mail + "'";//on mettre a jour le nouvelle pass dans le memme colon que le motpasstemp
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
        }
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
     * @param email
     * @param pass
     * @throws MessagingException
     */
    private void envoimail(String email, String pass) throws MessagingException {

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
        msg.setText("Cher utilisateur. Votre nouvelle pass  c'est " + pass);

        SMTPTransport transport = (SMTPTransport) session.getTransport(bundlePref.getString("transportMail"));
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

}
