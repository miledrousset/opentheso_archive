package mom.trd.opentheso.SelectedBeans;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import mom.trd.opentheso.bdd.helper.ImagesHelper;
import mom.trd.opentheso.core.imports.helper.ImportSkosHelper;
import mom.trd.opentheso.core.imports.helper.ImportTabulateHelper;
import mom.trd.opentheso.core.imports.old.ReadFileSKOS;
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
    
    private Integer progress;  
    private boolean desabled = true;

    private String source = "";
   
    // Import SKOS
    private String formatDate;
    private boolean createNewId;
    
    // Import CVS
    private String sepCol;
    private String sepChamps;
    private String sepLangue;
    private String idTheso;

    
    private ImportSkosHelper importSkosHelper;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    @ManagedProperty(value = "#{currentUser}")
    private CurrentUser2 user;
    
    @ManagedProperty(value = "#{roleOnTheso}")
    private RoleOnThesoBean roleOnTheso;     

    @PostConstruct
    public void initFileBean() {

    }

    public Integer getProgress() {
        if(progress == null) {
            setProgress(0);
            onComplete();
        }
    //    System.out.println("getProgress: " + progress);
        return progress;  
    }  

    public void setProgress(Integer progress) { 
    //    System.out.println("SetProgress called: " + progress);
        this.progress = progress;  
    }  

    public boolean isDesabled() {
        return desabled;
    }

    public void setDesabled(boolean desabled) {
        this.desabled = desabled;
    }

    public void onComplete() { 
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucess !!", "Total =  " + importSkosHelper.getConceptsCount()));  
        vue.setAddSkos2(false);
    }  

    public void cancel() {  
        System.out.println("Cancel called: " + progress);
        progress = null;  
    }      
    
    
    public void startImportSkos2() {
        //System.out.println("Start thesaurus import ! ");

        if(getConceptCount() == 0)
        // chargement dans la base de données 

        //chargement du nom du thesaurus 
        if(!importSkosHelper.addThesaurus()){
           FacesContext.getCurrentInstance().addMessage(null, 
                   new FacesMessage(FacesMessage.SEVERITY_INFO, "Aucune information sur le thésaurus et ses domaines, un thésaurus par defaut sera créer.. "/*langueBean.getMsg("info")*/ + " :", importSkosHelper.getMessage())); 

           if(!importSkosHelper.addDefaultThesaurus()){
               // echec de l'ajout du nom de thésaurus
               FacesContext.getCurrentInstance().addMessage(null, 
                   new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", importSkosHelper.getMessage()));
               return;
           }
        }       
       
        // chargement des concepts
        if(!importSkosHelper.addConcepts_progress(this)){
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Aucun Concept n'a été détecté dans le fichier ... "/*langueBean.getMsg("info")*/ + " :", importSkosHelper.getMessage())); 
            return;
            // echec de l'ajout des concepts 
        }
    
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :", importSkosHelper.getMessage()));

        vue.setAddSkos2(false);
        desabled = true;
        
     /*   
        for (int i = 0; i < count; i++) {
            if(progress == null) return;
            if(count < 100) {
                setProgress(i*(100/count));
            }
            else{
                setProgress(i/(count/100));
            }
            System.out.println("TestFunction - setting progress to: " + i);
            try {
                Thread.sleep(200);
                if(progress == null) {
                    setProgress(100);
                    return;
                }
            } catch (InterruptedException e) {
            }
        }
        setProgress(100);
        System.out.println("Finished Function");*/
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
            if (formatDate == null || formatDate.equals("") || user == null || roleOnTheso.getNodePreference() == null  ) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error2")));
            } else {
                try {
                    boolean useArk = false;
                    useArk = roleOnTheso.getNodePreference().isUseArk();
                    

                    String adressSite = roleOnTheso.getNodePreference().getCheminSite();//bundlePref.getString("cheminSite");
                    new ReadFileSKOS().readFile(connect.getPoolConnexion(), file.getInputstream(),
                            formatDate, useArk, adressSite,
                            user.getUser().getIdUser(),
                            roleOnTheso.getNodePreference().getSourceLang());
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
     * Cette fonction permet de lire un fichier Skos en utilsant le skosapi et owlapi (officiel)
     *
     * @param event
     */
    /*public void readSkos2(FileUploadEvent event) {
        importSkosHelper = null;
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
                    
                    // lecture du fichier SKOS
                    importSkosHelper = new ImportSkosHelper();
                    importSkosHelper.setInfos(connect.getPoolConnexion(), 
                            formatDate, useArk, adressSite, idUser, langueSource);
                    if(! importSkosHelper.readFile(file.getInputstream(), file.getFileName())) {
                        FacesContext.getCurrentInstance().addMessage(null, 
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", importSkosHelper.getMessage()));
                        return;
                    }
                    FacesContext.getCurrentInstance().addMessage(null, 
                                new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :", importSkosHelper.getMessage()));
                    desabled = false;
                                      
                }
//                catch (IOException ex) {
//                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
//                }
                catch (IOException | OWLOntologyCreationException | SKOSCreationException ex) {
                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
                    FacesContext.getCurrentInstance().addMessage(null, 
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", ex.getMessage()));
                }

            }
        }
    } */
    
    /**
     * Cette fonction permet de charger le thésaurus dans la BDD
     * @return 
     */
    public boolean addSkosDatas2(){

        if(importSkosHelper == null) return false;
        
        // initialisation des infos (message)
        importSkosHelper.setMessage("");
        
        // chargement dans la base de données 
        
        //chargement du nom du thesaurus 
        if(!importSkosHelper.addThesaurus()){
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Aucune information sur le thésaurus et ses domaines, un thésaurus par defaut sera créer.. "/*langueBean.getMsg("info")*/ + " :", importSkosHelper.getMessage())); 

            if(!importSkosHelper.addDefaultThesaurus()){
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", importSkosHelper.getMessage()));
                return false;
            }
            // echec de l'ajout du nom de thésaurus
        }

        // ajout des groups ou domaine
       /* if(!importSkosHelper.addGroups()){
            // echec de l'ajout des groups ou domaines
        }*/

        // chargement des concepts
        if(!importSkosHelper.addConcepts()){
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Aucun Concept n'a été détecté dans le fichier ... "/*langueBean.getMsg("info")*/ + " :", importSkosHelper.getMessage())); 
            return false;
            // echec de l'ajout des concepts 
        }
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :", importSkosHelper.getMessage()));
        vue.setAddSkos2(false);
        return true;
    }
    
    public int getConceptCount(){
        if(importSkosHelper == null) return -1;
        return importSkosHelper.getConceptsCount();
    }

    public ImportSkosHelper getImportSkosHelper() {
        return importSkosHelper;
    }
    
    
    /**
     * Cette fonction permet d'insérer un thésaurus en base de données à partir
     * d'un fichier Skos en utilsant leskosapi et owlapi (officiel)
     *
     * @param event
     */
//    public void chargeSkos2(FileUploadEvent event) {
//        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
//            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
//            event.queue();
//        } else {
//            UploadedFile file = event.getFile();
//
//            if (formatDate == null || formatDate.equals("")) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("file.error2")));
//            } else {
//                try {
//                    boolean useArk = false;
//                    ResourceBundle bundlePref = getBundlePref();
//                    if (bundlePref.getString("useArk").equalsIgnoreCase("true")) {
//                        useArk = true;
//                    }
//
//                    String adressSite = bundlePref.getString("cheminSite");
//                    int idUser = selectedTerme.getUser().getUser().getId();
//                    
//                    // lecture du fichier SKOS
//                    ImportSkosHelper importSkosHelper1 = new ImportSkosHelper();
//                    importSkosHelper1.setInfos(connect.getPoolConnexion(), 
//                            formatDate, useArk, adressSite, idUser, langueSource);
//                    if(! importSkosHelper.readFile(file.getInputstream(), file.getFileName())) {
//                        FacesContext.getCurrentInstance().addMessage(null, 
//                                new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", importSkosHelper1.getMessage()));
//                        return;
//                    }
//                    else {
//                        FacesContext.getCurrentInstance().addMessage(null, 
//                                new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :", importSkosHelper1.getMessage()));            
//                    }
//                    
//                    // chargement dans la base de données 
//                    
//                    //chargement du nom du thesaurus 
//                    if(!importSkosHelper1.addThesaurus()){
//                        FacesContext.getCurrentInstance().addMessage(null, 
//                                new FacesMessage(FacesMessage.SEVERITY_INFO, "Aucune information sur le thésaurus et ses domaines, un thésaurus par defaut sera créer.. "/*langueBean.getMsg("info")*/ + " :", importSkosHelper1.getMessage())); 
//                        
//                        if(!importSkosHelper1.addDefaultThesaurus()){
//                            FacesContext.getCurrentInstance().addMessage(null, 
//                                new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", importSkosHelper1.getMessage()));
//                        }
//                        // echec de l'ajout du nom de thésaurus
//                    }
//                    
//                    // ajout des groups ou domaine
//                   /* if(!importSkosHelper.addGroups()){
//                        // echec de l'ajout des groups ou domaines
//                    }*/
//                    
//                    // chargement des concepts
//                    if(!importSkosHelper1.addConcepts()){
//                        FacesContext.getCurrentInstance().addMessage(null, 
//                                new FacesMessage(FacesMessage.SEVERITY_INFO, "Aucun Concept n'a été détecté dans le fichier ... "/*langueBean.getMsg("info")*/ + " :", importSkosHelper1.getMessage())); 
//                        
//                        // echec de l'ajout des concepts 
//                    }
//                    FacesContext.getCurrentInstance().addMessage(null, 
//                                new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :", importSkosHelper1.getMessage()));
//                    
//                    vue.setAddSkos2(false);
//                    
//                }
////                catch (IOException ex) {
////                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
////                }
//                catch (IOException | OWLOntologyCreationException | SKOSCreationException ex) {
//                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
//                    FacesContext.getCurrentInstance().addMessage(null, 
//                                new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", ex.getMessage()));
//                }
//
//            }
//        }
//    }
    
    
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
     //       System.out.println(sepCol + " " + sepChamps + " " + sepLangue + " " + formatDate);
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
                    if(!importTabulateHelper.insertIntoBDD(connect.getPoolConnexion(), idTheso, selectedTerme.getUser().getUser().getIdUser())){
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
        int idUser = selectedTerme.getUser().getUser().getIdUser();
        if(roleOnTheso.getNodePreference() == null) return;
        
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
                    String path = roleOnTheso.getNodePreference().getPathImage();//pathImage; 
                    


                    SimpleDateFormat dateFormatDirectory = new SimpleDateFormat(" dd-MM-yyyy HH-mm-ss");
                    String dateDirectory = dateFormatDirectory.format(new Date());

                    File image = new File(path + selectedTerme.getIdC() + dateDirectory + "." + suffix);
                    OutputStream output = new FileOutputStream(image);

                    IOUtils.copy(input, output);

                    IOUtils.closeQuietly(output);
                    IOUtils.closeQuietly(input);

            //        System.out.println(image.getName());

                    BufferedImage bimg = ImageIO.read(image);
                    int width = bimg.getWidth();
                    int height = bimg.getHeight();
                    if (width > 1024 || height > 768) {
                        resizeToBigImage(image.getName());
                    }

                    if(!resizeImage(image.getName())) {
                        return;
                    }
                    addFiligrane(image.getName(), source, suffix);
                    
                    addFiligrane(roleOnTheso.getNodePreference().getDossierResize()
                            /*dossierResize*/ 
                            + "/" + image.getName(), source, suffix);

                    new ImagesHelper().addImage(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso(), image.getName(), source, idUser);

                } catch (IOException ex) {
                    Logger.getLogger(FileBean.class.getName()).log(Level.SEVERE, null, ex);
                     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", ex.toString()));
                     vue.setAddImage(false);
                     return;
                }

                selectedTerme.setImages(new ImagesHelper().getImage(connect.getPoolConnexion(), selectedTerme.getIdC(), selectedTerme.getIdTheso()));

                vue.setAddImage(false);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("file.info1.1") + " " + source + langueBean.getMsg("file.info1.2") + "."));
                source = "";
            }
        }
    }

    public boolean resizeImage(String fileName) {
        try {
            Thumbnails.of(new File(roleOnTheso.getNodePreference().getPathImage() + fileName))
                    .size(200, 200)
                    .toFile(new File(roleOnTheso.getNodePreference().getPathImage()
                            + roleOnTheso.getNodePreference().getDossierResize() + "/" + fileName));
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ImagesHelper.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", ex.toString()));
        }
        return false;
    }

    public void resizeToBigImage(String fileName) {
        try {
            Thumbnails.of(new File(roleOnTheso.getNodePreference().getPathImage() + fileName))
                    .size(1024, 768)
                    .toFile(new File(roleOnTheso.getNodePreference().getPathImage() + fileName));
        } catch (IOException ex) {
            Logger.getLogger(ImagesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addFiligrane(String fileName, String source, String ext) {

        try {
            // Image to add a text caption to.
            BufferedImage originalImage = ImageIO.read(new File(roleOnTheso.getNodePreference().getPathImage() + fileName));
            // Set up the caption properties
            Font font = new Font("Monospaced", Font.PLAIN, 15);
            Color c = Color.YELLOW;
            Positions position = Positions.BOTTOM_RIGHT;
            int insetPixels = 2;

            // Apply caption to the image
            Caption filter = new Caption(source, font, c, position, insetPixels);
            BufferedImage captionedImage = filter.apply(originalImage);
            ImageIO.write(captionedImage, ext, new File(roleOnTheso.getNodePreference().getPathImage() + fileName));

        } catch (IOException ex) {
            Logger.getLogger(ImagesHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCheminPix() {
        return roleOnTheso.getNodePreference().getPathImage();
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

    public boolean isCreateNewId() {
        return createNewId;
    }

    public void setCreateNewId(boolean createNewId) {
        this.createNewId = createNewId;
    }

    public CurrentUser2 getUser() {
        return user;
    }

    public void setUser(CurrentUser2 user) {
        this.user = user;
    }

    public RoleOnThesoBean getRoleOnTheso() {
        return roleOnTheso;
    }

    public void setRoleOnTheso(RoleOnThesoBean roleOnTheso) {
        this.roleOnTheso = roleOnTheso;
    }
    
}
