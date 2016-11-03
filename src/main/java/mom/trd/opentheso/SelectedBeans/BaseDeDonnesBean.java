/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import mom.trd.opentheso.bdd.helper.BaseDeDoneesHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.ForgetPasswordHelper;
import mom.trd.opentheso.core.exports.helper.ExportPrivatesDatas;
import mom.trd.opentheso.core.exports.privatesdatas.WriteXml;
import mom.trd.opentheso.core.exports.privatesdatas.importxml.importxml;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "baseDeDonnes", eager = true)
@SessionScoped
public class BaseDeDonnesBean implements Serializable {

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    private String email;
    private StreamedContent file;
    private String dbName;

    public ArrayList<BaseDeDoneesHelper> info;

    /**
     * Cette fonction permet de télécharger les tables et les données ce qui
     * permet de sauvegarder toutes les données privées pour les mise à jour
     *
     * @param toutTables
     * @return
     */
    public StreamedContent backUpBaseDonnees(ArrayList<String> toutTables) {
        ArrayList<Table> sortirXml;
        ExportPrivatesDatas backUp = new ExportPrivatesDatas();
        Iterator<String> it1 = toutTables.iterator();
        WriteXml write = new WriteXml();
        write.writeHead();
        write.start();
        String table;

        // date du jour
        java.util.Date datetoday = new java.util.Date();

        while (it1.hasNext()) {
            table = it1.next();
            sortirXml = backUp.getDatasOfTable(connect.getPoolConnexion(), table);
            write.WriteIntoXML(sortirXml, table);
        }
        write.end();
        InputStream stream;

        try {
            stream = new ByteArrayInputStream(write.getXml().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", "backupOpentheso_" + datetoday + ".xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    public void inyectionaBDD(ActionEvent e) throws ClassNotFoundException, SQLException {
        File fichero;
        importxml impo = new importxml();
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter ft = new FileNameExtensionFilter("*.xml", "xml");
        fileChooser.setVisible(true);
        fileChooser.setFileFilter(ft);
        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            fichero = fileChooser.getSelectedFile();
            impo.ouvreFichier2(connect.getPoolConnexion(), fichero);
        }
    }

    public void oublieMonPass() throws MessagingException {
        ForgetPasswordHelper forgetPassword = new ForgetPasswordHelper();
        forgetPassword.forgotPass(connect.getPoolConnexion(), email);
        email = null;
    }

    public void newDB() throws SQLException, IOException, ClassNotFoundException {
        if (dbName != null) {
            BaseDeDoneesHelper basedonne = new BaseDeDoneesHelper();

            if (!basedonne.createBdD(dbName)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("error")));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("bdd.name")));
        }

    }

    public ArrayList<BaseDeDoneesHelper> loadinfoDB() throws SQLException {
        BaseDeDoneesHelper basedone = new BaseDeDoneesHelper();
        info = basedone.info_out(connect.getPoolConnexion());
        return info;
    }

    public ArrayList<BaseDeDoneesHelper> getInfo() {
        return info;
    }

    public void setInfo(ArrayList<BaseDeDoneesHelper> info) {
        this.info = info;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

}
