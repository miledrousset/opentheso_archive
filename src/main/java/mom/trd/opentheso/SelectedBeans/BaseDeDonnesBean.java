/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.BaseDeDoneesHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.ForgetPasswordHelper;
import mom.trd.opentheso.core.exports.helper.ExportPrivatesDatas;
import mom.trd.opentheso.core.exports.helper.ExportStatistiques;
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
    @ManagedProperty(value = "#{theso}")
    private SelectedThesaurus theso;   
    
    private String email;
    private String dbName;
    private StreamedContent file;
    private StreamedContent fileDownload;
    
    public String tete="";
    public String totalconcept=""; 
    public String nondescr =""; 
    public String termesNonTra ="";
    public String notes = "";
    public String ConceptOrphan = "";

    public BaseDeDonnesBean() {
    }
    

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
        String table = "";

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
            stream = new ByteArrayInputStream(write.getXml().toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", "backupOpentheso_" + datetoday + ".xml");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    public void inyectionaBDD() throws ClassNotFoundException, SQLException {
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
    /**
     * permet de savoir comme s'appele notre BDD 
     * @return le nom de la BDD
     */
    private String getNameOfBdd(){
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePool = context.getApplication().getResourceBundle(context, "conHikari");
        return bundlePool.getString("dataSource.databaseName");
    }
    
    
    /**
     * permet de faire la mise à jour de la BDD
     * 
     */
    public void maJBdd()
    {
        String ownerName="";
        InputStream inputStream = this.getClass().getResourceAsStream("/install/maj_bdd_current.sql");
        BaseDeDoneesHelper baseDeDoneesHelper = new BaseDeDoneesHelper();
        ownerName =baseDeDoneesHelper.getNameOwner(connect.getPoolConnexion(), getNameOfBdd());
        if(!baseDeDoneesHelper.insertDonneées(connect.getPoolConnexion(),inputStream, ownerName))
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("error") + " :", langueBean.getMsg("bdd.error")));
        }
        else
        {
            baseDeDoneesHelper.updateVersionBdd(connect.getPoolConnexion());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("bdd.fait")));
        }
    }

    /**
     * Permet de recuperer le mot de pass pour faire le login dans Opentheso
     * @throws MessagingException 
     */
    public void oublieMonPass() throws MessagingException {
        ForgetPasswordHelper forgetPassword = new ForgetPasswordHelper();
        
        forgetPassword.setEmailTitle(langueBean.getMsg("mot.titlePass"));
        forgetPassword.setEmailMessage(langueBean.getMsg("mot.textPass"));
        forgetPassword.setPseudoMessage(langueBean.getMsg("mot.textPseudo"));
        
        if(forgetPassword.forgotPass(connect.getPoolConnexion(), email))
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("mot.envoy")+ email)); 
        }
        else
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.info8")));
        }
        email = null;
    }
/**
 * 
 * @throws SQLException
 * @throws IOException
 * @throws ClassNotFoundException 
 */
    public void newDB() throws SQLException, IOException, ClassNotFoundException {
        InputStream inputStream = this.getClass().getResourceAsStream("/install/opentheso_dist_4.1.sql");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        if (dbName != null) {
            BaseDeDoneesHelper basedonne = new BaseDeDoneesHelper();

            if (!basedonne.createBdD(connect.getPoolConnexion(), dbName,"")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("error")));
            }
            // le connextion ici;
            
            if(!basedonne.insertDonneées(connect.getPoolConnexion(), inputStream, "")){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("error")));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("bdd.name")));
        }


    }

    public ArrayList<BaseDeDoneesHelper> loadinfoDB() throws SQLException, IOException {
        BaseDeDoneesHelper basedone = new BaseDeDoneesHelper();
        info = basedone.info_out(connect.getPoolConnexion());
        return info;
    }
    public StreamedContent genererdocument() throws SQLException
    {
        remplirText();
        ExportStatistiques expo= new ExportStatistiques();
        envoytext(expo);
        expo.recuperatefils(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(), 
                theso.getThesaurus().getLanguage(),1);
        InputStream stream;
        java.util.Date datetoday = new java.util.Date();

        try {
            stream = new ByteArrayInputStream(expo.getDocument().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", "export "+theso.getThesaurus().getId_thesaurus() + datetoday + ".txt");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }
    public StreamedContent pdf() throws SQLException, Exception
    {
        Thesaurus thesaurus= new Thesaurus();
        ExportStatistiques expo= new ExportStatistiques();
        expo.recuperatefils(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(), 
                theso.getThesaurus().getLanguage(),2);
        Document pdf = new Document(PageSize.LETTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer;
        Paragraph para = new Paragraph(expo.getDocument());
        writer = PdfWriter.getInstance(pdf, baos);
        if (!pdf.isOpen()) {
            pdf.open();
        }
        pdf.addTitle("theso");
        pdf.add(para);
       //Adding content to pdf
        pdf.close();
        InputStream stream = new ByteArrayInputStream(baos.toByteArray());
        fileDownload = new DefaultStreamedContent(stream, "application/pdf", "Thésaurus"+thesaurus.getId_thesaurus()+".pdf");

       return fileDownload;
    }
    public void remplirText()
    {
        tete= langueBean.getMsg("theso");
        totalconcept= langueBean.getMsg("exp.numTotal");
        nondescr =langueBean.getMsg("stat.statTheso3"); 
        termesNonTra =langueBean.getMsg("stat.statTheso4");
        notes = langueBean.getMsg("stat.statTheso5");
        ConceptOrphan = langueBean.getMsg("stat.statTheso6");        
    }
    public void envoytext(ExportStatistiques expo)
    {
        expo.setTete(tete);
        expo.setTotalconcept(totalconcept);
        expo.setNondescr(nondescr);
        expo.setTermesNonTra(termesNonTra);
        expo.setNotes(notes);
        expo.setConceptOrphan(ConceptOrphan);
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

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public String getTete() {
        return tete;
    }

    public void setTete(String tete) {
        this.tete = tete;
        
    }

    public String getTotalconcept() {
        return totalconcept;
    }

    public void setTotalconcept(String totalconcept) {
        this.totalconcept = totalconcept;
    }

    public String getNondescr() {
        return nondescr;
    }

    public void setNondescr(String nondescr) {
        this.nondescr = nondescr;
    }

    public String getTermesNonTra() {
        return  termesNonTra;
    }

    public void setTermesNonTra(String termesNonTra) {
        this.termesNonTra = termesNonTra;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getConceptOrphan() {
        return ConceptOrphan;
    }

    public void setConceptOrphan(String ConceptOrphan) {
        this.ConceptOrphan = ConceptOrphan;
    }

    public SelectedThesaurus getTheso() {
        return theso;
    }

    public void setTheso(SelectedThesaurus theso) {
        this.theso = theso;
    }

   
    

}
