package mom.trd.opentheso.SelectedBeans;

import com.sun.xml.bind.v2.schemagen.xmlschema.Import;
import mom.trd.opentheso.core.exports.privatesdatas.WriteXml;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.swing.JFileChooser;
import mom.trd.opentheso.bdd.account.OublieMotPass;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.tools.CreateBDD;
import mom.trd.opentheso.core.exports.helper.ExportPrivatesDatas;
import mom.trd.opentheso.core.exports.helper.ExportStatistiques;
import mom.trd.opentheso.core.exports.helper.ExportTabulateHelper;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.exports.old.ExportFromBDD_Frantiq;
import mom.trd.opentheso.core.exports.privatesdatas.importxml.importxml;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import mom.trd.opentheso.core.jsonld.helper.JsonHelper;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import skos.SKOSXmlDocument;

@ManagedBean(name = "downloadBean", eager = true)
@SessionScoped

public class DownloadBean implements Serializable {

    private String skos;
    private StreamedContent file;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean languageBean;

    private String serverArk;
    private boolean arkActive;
    private String serverAdress;
    private String email;
    private String nomUsu;
    private String newPass;
    private String confirmPass;
    private String ancianPass;
    private String dbName;

    
    @PostConstruct
    public void initTerme() {
        ResourceBundle bundlePref = getBundlePref();
        String temp = bundlePref.getString("useArk");
        arkActive = temp.equals("true");
        serverArk = bundlePref.getString("serverArk");
        serverAdress = bundlePref.getString("cheminSite");
    }

    /**
     * Récupération des préférences
     *
     * @return la ressourceBundle des préférences
     */
    private ResourceBundle getBundlePref() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
        return bundlePref;
    }

    public String conceptSkos(String idC, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();

        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        return exportFromBDD.exportConcept(connect.getPoolConnexion(),
                idTheso,
                idC).toString();

        //   new ExportFromBDD().exportConcept(connect.getPoolConnexion(), idTheso, idC).toString();
    }

    public String groupSkos(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        return exportFromBDD.exportThisGroup(connect.getPoolConnexion(), idTheso, idGroup).toString();
    }

    public String groupJsonLd(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportThisGroup(connect.getPoolConnexion(), idTheso, idGroup);

        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos_local);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
        return jsonLd.toString();
    }

    /*    public void branchSkos(String idC, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer temp = exportFromBDD.exportBranchOfConcept(connect.getPoolConnexion(), idTheso, idC);
        if (temp.length() <= 1500000) {
            //    if(temp.length() <= 150) {
            skos = temp.toString();
            vue.setBranchToSkos(true);
        } else {
            InputStream stream;
            try {
                stream = new ByteArrayInputStream(temp.toString().getBytes("UTF-8"));
                file = new DefaultStreamedContent(stream, "application/xml ", "downloadedSkos.xml");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            vue.setBranchToSkosFile(true);
        }
    }*/
    public void branchGroupSkos(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer temp = exportFromBDD.exportGroup(connect.getPoolConnexion(), idTheso, idGroup);
        if (temp.length() <= 1500000) {
            skos = temp.toString();
            vue.setBranchToSkos(true);
        } else {
            InputStream stream;
            try {
                stream = new ByteArrayInputStream(temp.toString().getBytes("UTF-8"));
                file = new DefaultStreamedContent(stream, "application/xml ", "downloadedSkos.xml");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            vue.setBranchToSkosFile(true);
        }
    }

    /**
     * Cette fonction permet d'exporter un thésaurus au format SKOS à partir de
     * son identifiant. Le résultat est enregistré dans la variable 'skos' du
     * downloadBean si la taille est petite, ou dans la variable 'file' du
     * downloadBean sinon. Dans le premier cas on affiche la variable, dans le
     * second cas l'utilisateur télécharge de fichier.
     *
     * @param idTheso
     * @return
     */
    public StreamedContent thesoSkos(String idTheso) {

        /**
         * Cette initialisation est pour exporter les PACTOLS au format accepté
         * par Koha
         */
        //ExportFromBDD_Frantiq exportFromBDD = new ExportFromBDD_Frantiq();
        /**
         * ici c'est la classe à utiliser pour un export standard au foramt SKOS
         */
        ExportFromBDD exportFromBDD = new ExportFromBDD();

        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportThesaurus(connect.getPoolConnexion(), idTheso);
        InputStream stream;

        try {
            stream = new ByteArrayInputStream(skos_local.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idTheso + "_skos.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        vue.setThesoToSkosCsvFile(true);
        return file;


        /*        if (temp.length() <= 1500000) {
            skos = temp.toString();
            vue.setThesoToSkosCsv(true);
        } else {
            InputStream stream;
            try {
                stream = new ByteArrayInputStream(temp.toString().getBytes("UTF-8"));
                file = new DefaultStreamedContent(stream, "application/xml ", "downloadedSkos.xml");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            vue.setThesoToSkosCsvFile(true);
        }*/
    }

    /**
     * Cette fonction permet d'exporter un concept en SKOS en temps réel dans la
     * page principale
     *
     * @param idC
     * @param idTheso
     * @return
     */
    public StreamedContent conceptToSkos(String idC, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();

        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        InputStream stream;
        StringBuffer skos_local = exportFromBDD.exportConcept(connect.getPoolConnexion(),
                idTheso, idC);
        try {
            stream = new ByteArrayInputStream(skos_local.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idC + "_skos.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;

        //   new ExportFromBDD().exportConcept(connect.getPoolConnexion(), idTheso, idC).toString();
    }

    /**
     * Cette fonction permet d'exporter un concept en JsonLd
     *
     * @param idC
     * @param idTheso
     * @return
     */
    public StreamedContent conceptToJsonLd(String idC, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();

        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        InputStream stream;
        StringBuffer skos_local = exportFromBDD.exportConcept(connect.getPoolConnexion(),
                idTheso, idC);

        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos_local);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
        if (jsonLd == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, languageBean.getMsg("error") + " :", languageBean.getMsg("index.exportJsonError")));
            return file;
        }

        try {
            stream = new ByteArrayInputStream(jsonLd.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idC + "_jsonLd.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;

        //   new ExportFromBDD().exportConcept(connect.getPoolConnexion(), idTheso, idC).toString();
    }

    /**
     * Cette fonction permet de retourner une branche en SKOS
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    public StreamedContent brancheToSkos(String idConcept, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportBranchOfConcept(connect.getPoolConnexion(), idTheso, idConcept);

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(skos_local.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idConcept + "_Branch_skos.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    /**
     * Cette fonction permet de retourner une branche en JsonLd
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    public StreamedContent brancheToJsonLd(String idConcept, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportBranchOfConcept(connect.getPoolConnexion(), idTheso, idConcept);

        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos_local);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(jsonLd.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idConcept + "_Branch_jsonld.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    /**
     * Cette fonction permet de retourner pour téléchargement un groupe en SKOS
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    public StreamedContent thisGroupToSkos(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportThisGroup(connect.getPoolConnexion(), idTheso, idGroup);

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(skos_local.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idGroup + "_Group_skos.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

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

    /**
     * Cette fonction permet de retourner pour téléchargement un groupe en
     * JsonLd
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    public StreamedContent thisGroupToJsonLd(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportThisGroup(connect.getPoolConnexion(), idTheso, idGroup);

        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos_local);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(jsonLd.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idGroup + "_Group_jsonld.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    /**
     * Cette fonction permet de retourner la branche antière d'un groupe en SKOS
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    public StreamedContent groupToSkos(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportGroup(connect.getPoolConnexion(), idTheso, idGroup);

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(skos_local.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idGroup + "_Group_skos.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    /**
     * Cette fonction permet de retourner la branche entière d'un groupe en
     * JsonLd
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    public StreamedContent groupToJsonLd(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer skos_local = exportFromBDD.exportGroup(connect.getPoolConnexion(), idTheso, idGroup);

        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos_local);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(jsonLd.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idGroup + "_Group_jsonld.xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    /**
     * Cette fonction permet d'exporter un thésaurus au format SKOS2 new Version
     * à partir de son identifiant. Le résultat est enregistré dans la variable
     * 'skos' du downloadBean si la taille est petite, ou dans la variable
     * 'file' du downloadBean sinon. Dans le premier cas on affiche la variable,
     * dans le second cas l'utilisateur télécharge de fichier.
     *
     * @param idTheso
     */
    public void thesoSkos2(String idTheso) {

        /**
         * Cette initialisation est pour exporter les PACTOLS au format accepté
         * par Koha
         */
        //ExportFromBDD_Frantiq exportFromBDD = new ExportFromBDD_Frantiq();
        /**
         * ici c'est la classe à utiliser pour un export standard au foramt SKOS
         */
    }

    /**
     * Cette fonction permet d'exporter un thésaurus au format SKOS à partir de
     * son identifiant. Le résultat est enregistré dans la variable 'skos' du
     * downloadBean si la taille est petite, ou dans la variable 'file' du
     * downloadBean sinon. Dans le premier cas on affiche la variable, dans le
     * second cas l'utilisateur télécharge de fichier.
     *
     * @param idTheso
     */
    public void thesoSkosFrantiq(String idTheso) {

        /**
         * Cette initialisation est pour exporter les PACTOLS au format accepté
         * par Koha
         */
        ExportFromBDD_Frantiq exportFromBDD = new ExportFromBDD_Frantiq();
        /**
         * ici c'est la classe à utiliser pour un export standard au foramt SKOS
         */
        // ExportFromBDD exportFromBDD = new ExportFromBDD();

        exportFromBDD.setServerAdress(serverAdress);
        exportFromBDD.setServerArk(serverArk);
        exportFromBDD.setArkActive(arkActive);

        StringBuffer temp = exportFromBDD.exportThesaurus(connect.getPoolConnexion(), idTheso);
        if (temp.length() <= 1500000) {
            skos = temp.toString();
            vue.setThesoToSkosCsv(true);
        } else {
            InputStream stream;
            try {
                stream = new ByteArrayInputStream(temp.toString().getBytes("UTF-8"));
                file = new DefaultStreamedContent(stream, "application/xml ", "downloadedSkos.xml");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            vue.setThesoToSkosCsvFile(true);
        }
    }

    /**
     * Cette fonction permet d'exporter un thésaurus au format CSV à partir de
     * son identifiant. Le résultat est enregistré dans la variable 'skos' du
     * downloadBean si la taille est petite, ou dans la variable 'file' du
     * downloadBean sinon. Dans le premier cas on affiche la variable, dans le
     * second cas l'utilisateur télécharge de fichier.
     *
     * @param idTheso
     */
    public void thesoCsv(String idTheso) {

        ExportTabulateHelper exportTabulateHelper = new ExportTabulateHelper();

        exportTabulateHelper.setThesaurusDatas(connect.getPoolConnexion(), idTheso);
        exportTabulateHelper.exportToTabulate();
        StringBuffer temp = exportTabulateHelper.getTabulateBuff();
        if (temp.length() <= 1500000) {
            skos = temp.toString();
            vue.setThesoToSkosCsv(true);
        } else {
            InputStream stream;
            try {
                stream = new ByteArrayInputStream(temp.toString().getBytes("UTF-8"));
                file = new DefaultStreamedContent(stream, "text/csv", "downloadedCsv.csv");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            vue.setThesoToSkosCsvFile(true);
        }
    }
/**
 * Applelation de la funtion pour realiser l'injection a la BDD;
 * on puex choisir le fichier dans une fenetre que se ouvre;
 */
  
/**
 * Applelation de la funtion avec les parametres pour avoir le motpasstemp
 */
    public void oublieMonPass() throws MessagingException {
        OublieMotPass apple = new OublieMotPass();
        apple.vide(connect.getPoolConnexion(), nomUsu, email);
        nomUsu = null;
        email = null;

    }
    public void inyectionaBDD() throws ClassNotFoundException, SQLException{
        File fichero;
        importxml impo = new importxml();
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            fichero = fileChooser.getSelectedFile();
            impo.ouvreFichier2(connect.getPoolConnexion(), fichero);
        }
        System.out.println("saliendo************************************");
        
    }
    /**
     * Applelation de la funtion avec les parametres pour changer le mot de
     * pass.
     * on fait le comprobation pour voir si tout c'est bon 
     * @throws SQLException 
     */
    public void fchangepass() throws SQLException{
        boolean sort=false;
        OublieMotPass apple = new OublieMotPass();
        CurrentUser user = new CurrentUser();
        if (newPass == null ? confirmPass != null : !newPass.equals(confirmPass))
        {
            sort=true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, languageBean.getMsg("error") + " :", languageBean.getMsg("user.error3")));
        }
        if(newPass == null || newPass.equals("") || confirmPass == null || confirmPass.equals("") || ancianPass == null || ancianPass.equals("")) {
            sort=true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, languageBean.getMsg("error") + " :", languageBean.getMsg("user.error2")));
        }
        else if(!sort){ 
            if(apple.faireChangePass(connect.getPoolConnexion(),newPass,confirmPass, ancianPass))
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(languageBean.getMsg("info") + " :", languageBean.getMsg("user.info1")));
            }
            else FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, languageBean.getMsg("error") + " :", languageBean.getMsg("error")));
        }
        
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getSkos() {
        return skos;
    }

    public void setSkos(String skos) {
        this.skos = skos;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public LanguageBean getLanguageBean() {
        return languageBean;
    }

    public void setLanguageBean(LanguageBean languageBean) {
        this.languageBean = languageBean;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public String getAncianPass() {
        return ancianPass;
    }

    public void setAncianPass(String ancianPass) {
        this.ancianPass = ancianPass;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomUsu() {
        return nomUsu;
    }

    public void setNomUsu(String nomUsu) {
        this.nomUsu = nomUsu;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    

}
