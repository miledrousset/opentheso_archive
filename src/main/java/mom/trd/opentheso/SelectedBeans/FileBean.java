package mom.trd.opentheso.SelectedBeans;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import mom.trd.DownloadBean;
import mom.trd.LanguageBean;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.ImagesHelper;
import mom.trd.opentheso.core.exports.tabulate.TabulateDocument;
import mom.trd.opentheso.core.imports.helper.ImportTabulateHelper;
import mom.trd.opentheso.core.imports.old.ReadFileSKOS;
import mom.trd.opentheso.core.imports.tabulate.ImportTabuleIntoBDD;
import mom.trd.opentheso.core.imports.tabulate.ReadFileTabule;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Caption;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "fileBean", eager = true)
@SessionScoped

public class FileBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String source = "";
    private String pathImage;
    private String dossierResize;
    // Import SKOS
    private String formatDate;
    // Import CVS
    private String sepCol;
    private String sepChamps;
    private String sepLangue;
    private String idTheso;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    /**
     * Récupération des préférences
     *
     * @return la ressourceBundle de spréférences
     */
    private ResourceBundle getBundlePref() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
        return bundlePref;
    }

    @PostConstruct
    public void initFileBean() {
        ResourceBundle bundlePref = getBundlePref();
        pathImage = bundlePref.getString("pathImage");
        dossierResize = bundlePref.getString("dossierResize");
    }

    /**
     * Cette fonction permet d'insérer un thésaurus en base de données à partir
     * d'un fichier Skos
     *
     * @param event
     */
    public void chargeSkos(FileUploadEvent event) {
        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            UploadedFile file = event.getFile();
            if (formatDate == null || formatDate.equals("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error2")));
            } else {
                try {
                    boolean useArk = false;
                    ResourceBundle bundlePref = getBundlePref();
                    if (bundlePref.getString("useArk").equalsIgnoreCase("true")) {
                        useArk = true;
                    }

                    String adressSite = bundlePref.getString("cheminSite");
                    int idUser = selectedTerme.getUser().getUser().getId();
                    new ReadFileSKOS().readFile(connect.getPoolConnexion(), file.getInputstream(), formatDate, useArk, adressSite, idUser);
                } catch (IOException ex) {
                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                vue.setAddSkos(false);
            }
        }
    }

    /**
     * Cette fonction permet d'insérer un thésaurus en base de données à partir
     * d'un fichier CSV
     *
     * @param event
     */
    public void chargeCsv(FileUploadEvent event) {
        ImportTabulateHelper importTabulateHelper = new ImportTabulateHelper();
        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            System.out.println(sepCol + " " + sepChamps + " " + sepLangue + " " + formatDate);
            UploadedFile file = event.getFile();
            if (sepCol == null || sepCol.equals("") || sepChamps == null || sepChamps.equals("") || sepLangue == null || sepLangue.equals("") || formatDate == null || formatDate.equals("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error3")));
            } else {
                try {
                    if(!importTabulateHelper.readFile(file.getInputstream(), sepCol, sepChamps, sepLangue, formatDate)){
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error3")));   
                    }
                //    ArrayList<String> fields = importTabulateHelper.getFieldsList();
                //    ArrayList<TabulateDocument> tabulateDocuments = importTabulateHelper.getTabulateDocumentList();
                    if(!importTabulateHelper.insertIntoBDD(connect.getPoolConnexion(), idTheso, selectedTerme.getUser().getUser().getId())){
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error3")));   
                    }
                //    System.out.println(fields);
                //    System.out.println(tabulateDocuments);
                    vue.setAddSkos(false);
                } catch (Exception ex) {
                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                vue.setAddSkos(false);

            }
        }
    }
/*    public void chargeCsv(FileUploadEvent event) {
        ReadFileTabule readFileTabule = new ReadFileTabule();
        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            System.out.println(sepCol + " " + sepChamps + " " + sepLangue + " " + formatDate);
            UploadedFile file = event.getFile();
            if (sepCol == null || sepCol.equals("") || sepChamps == null || sepChamps.equals("") || sepLangue == null || sepLangue.equals("") || formatDate == null || formatDate.equals("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error3")));
            } else {
                try {
                    readFileTabule.setReadFile(file.getInputstream(), sepCol, sepChamps, sepLangue, formatDate);
                    readFileTabule.setFields();
                    readFileTabule.setDatas();
                    ArrayList<String> fields = readFileTabule.getFieldsList();
                    ArrayList<TabulateDocument> tabulateDocuments = readFileTabule.getTabulateDocumentList();
                    //new ImportTabuleIntoBDD().insertDatas(connect.getPoolConnexion(), source, tabulateDocuments, idUser);
                    System.out.println(fields);
                    System.out.println(tabulateDocuments);
                    vue.setAddSkos(false);
                } catch (Exception ex) {
                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                vue.setAddSkos(false);

            }
        }
    }*/

    public void chargeImage(FileUploadEvent event) {
        int idUser = selectedTerme.getUser().getUser().getId();
        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            UploadedFile file = event.getFile();
            if (source == null || source.equals("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error1")));
            } else {
                try {
                    // Traitement
                    String suffix = FilenameUtils.getExtension(file.getFileName());
                    InputStream input = file.getInputstream();
                    String path = pathImage;

                    SimpleDateFormat dateFormatDirectory = new SimpleDateFormat(" dd-MM-yyyy HH-mm-ss");
                    String dateDirectory = dateFormatDirectory.format(new Date());

                    File image = new File(path + selectedTerme.getIdC() + dateDirectory + "." + suffix);
                    OutputStream output = new FileOutputStream(image);

                    IOUtils.copy(input, output);

                    IOUtils.closeQuietly(output);
                    IOUtils.closeQuietly(input);

                    System.out.println(image.getName());

                    BufferedImage bimg = ImageIO.read(image);
                    int width = bimg.getWidth();
                    int height = bimg.getHeight();
                    if (width > 1024 || height > 768) {
                        resizeToBigImage(image.getName());
                    }

                    resizeImage(image.getName());
                    addFiligrane(image.getName(), source, suffix);
                    addFiligrane(dossierResize + "/" + image.getName(), source, suffix);

                    new ImagesHelper().addImage(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), image.getName(), source, idUser);

                } catch (IOException ex) {
                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
                }

                selectedTerme.setImages(new ImagesHelper().getImage(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), idUser));

                vue.setAddImage(false);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("file.info1.1") + " " + source + langueBean.getMsg("file.info1.2") + "."));
                source = "";
            }
        }
    }

    public void resizeImage(String fileName) {
        try {
            Thumbnails.of(new File(pathImage + fileName))
                    .size(160, 160)
                    .toFile(new File(pathImage + dossierResize + "/" + fileName));
        } catch (IOException ex) {
            Logger.getLogger(ImagesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resizeToBigImage(String fileName) {
        try {
            Thumbnails.of(new File(pathImage + fileName))
                    .size(1024, 768)
                    .toFile(new File(pathImage + fileName));
        } catch (IOException ex) {
            Logger.getLogger(ImagesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addFiligrane(String fileName, String source, String ext) {

        try {
            // Image to add a text caption to.
            BufferedImage originalImage = ImageIO.read(new File(pathImage + fileName));
            // Set up the caption properties
            Font font = new Font("Monospaced", Font.PLAIN, 15);
            Color c = Color.YELLOW;
            Positions position = Positions.BOTTOM_RIGHT;
            int insetPixels = 2;

            // Apply caption to the image
            Caption filter = new Caption(source, font, c, position, insetPixels);
            BufferedImage captionedImage = filter.apply(originalImage);
            ImageIO.write(captionedImage, ext, new File(pathImage + fileName));

        } catch (IOException ex) {
            Logger.getLogger(ImagesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCheminPix() {
        return pathImage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public String getDossierResize() {
        return dossierResize;
    }

    public void setDossierResize(String dossierResize) {
        this.dossierResize = dossierResize;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getSepCol() {
        return sepCol;
    }

    public void setSepCol(String sepCol) {
        this.sepCol = sepCol;
    }

    public String getSepChamps() {
        return sepChamps;
    }

    public void setSepChamps(String sepChamps) {
        this.sepChamps = sepChamps;
    }

    public String getSepLangue() {
        return sepLangue;
    }

    public void setSepLangue(String sepLangue) {
        this.sepLangue = sepLangue;
    }
    
    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }    

}
