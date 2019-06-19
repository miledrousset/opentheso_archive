/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import mom.trd.opentheso.bdd.helper.CandidateHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.core.imports.csv.CsvImportHelper;
import mom.trd.opentheso.core.imports.csv.CsvReadHelper;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import mom.trd.opentheso.core.imports.rdf4j.helper.ImportRdf4jHelper;
import mom.trd.opentheso.skosapi.SKOSDocumentation;
import mom.trd.opentheso.skosapi.SKOSLabel;
import mom.trd.opentheso.skosapi.SKOSMatch;
import mom.trd.opentheso.skosapi.SKOSProperty;
import mom.trd.opentheso.skosapi.SKOSResource;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author Quincy
 */
@ManagedBean(name = "rdf4jFileBean")
@ViewScoped
public class rdf4jFileBean implements Serializable {

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    private double progress = 0;
    private double progress_abs = 0;

    @ManagedProperty(value = "#{newtreeBean}")
    private NewTreeBean tree;
    
    @ManagedProperty(value = "#{currentUser}") 
    private CurrentUser2 currentUser;
    
    @ManagedProperty(value = "#{roleOnTheso}")
    private RoleOnThesoBean roleOnTheso;     
    
    private int typeImport;
    private String selectedIdentifier ="sans";
    private String prefixHandle;
    
    
    // import CSV
    private char delimiterCsv = ',';
    private int choiceDelimiter = 0;
    private String thesaurusName;
    private ArrayList <CsvReadHelper.ConceptObject> conceptObjects;
    private ArrayList<String> langs;    
    
    /*
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;    
     */
    private String formatDate = "yyyy-MM-dd";
    private String uri;
    private double total;

    private boolean uploadEnable = true;
    private boolean BDDinsertEnable = false;

    private SKOSXmlDocument sKOSXmlDocument;
    private String info = "";
    private StringBuffer error = new StringBuffer();
    private String warning = "";

    /**
     *
     */
    public void init() {
        info = "";
        error = new StringBuffer();
        warning = "";
        uri = "";
        formatDate = "yyyy-MM-dd";
        total = 0;
        uploadEnable = true;
        BDDinsertEnable = false;
        selectedIdentifier = "sans";
        sKOSXmlDocument = null;
        if(conceptObjects != null)
            conceptObjects.clear();
        if(langs != null)
            langs.clear();

    }
    
    public void actionChoice() {
        if(choiceDelimiter == 0)
            delimiterCsv = ',';
        if(choiceDelimiter == 1)
            delimiterCsv = ';';
        if(choiceDelimiter == 2)
            delimiterCsv = '\t';         
    }

    public void chargeSkos(FileUploadEvent event) {
        progress = 0;

        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            InputStream is = null;
            try {
                try {
                    is = event.getFile().getInputstream();
                } catch (IOException ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }
                ReadRdf4j readRdf4j = null;
                try {
                    readRdf4j = new ReadRdf4j(is, 0);
                } catch (IOException ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.toString());
                }
                if(readRdf4j==null) {
                    error.append(System.getProperty("line.separator"));
                    error.append("Erreur de format RDF !!!");
                    showError();
                    return;
                }
                warning = readRdf4j.getMessage();
                progress = 100;
                sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                uri = sKOSXmlDocument.getTitle();
                uploadEnable = false;
                BDDinsertEnable = true;

                info = "File correctly loaded";

            } catch (Exception e) {
                System.out.println("erreur :" + e.getMessage());
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
            } finally {
                showError();
            }

        }
    }

    /**
     * permet de charger des fichier de type skos, json-ld ou turtle avec la
     * variable membre typeImport pour choisir le type 0=skos 1=jsons-ld
     * 2=turtle
     *
     * @param event
     */
    public void chargeFile(FileUploadEvent event) {
        error = new StringBuffer();
        info = "";
        warning = "";
        switch (typeImport) {
            case 0:
                chargeSkos(event);
                break;
            case 1:
                chargeJsonLd(event);
                break;
            case 2:
                chargeTurtle(event);
                break;
            case 3:
                chargeCsv(event);
                break;
        }

    }

    private void showError() {
        if(info != null) {
            if (!info.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", info));
            }
        }
        if(error != null) {
            if (error.length() != 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error :", error.toString()));
            }
        }
        if(warning != null) {
            if (!warning.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning :", warning));
            }
        }

    }

    /**
     *
     * @param event
     */
    public void chargeJsonLd(FileUploadEvent event) {
        progress = 0;

        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            InputStream is = null;
            try {
                try {
                    is = event.getFile().getInputstream();
                } catch (IOException ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }
                ReadRdf4j readRdf4j;
                try {
                    readRdf4j = new ReadRdf4j(is, 1);
                    warning = readRdf4j.getMessage();
                    progress = 100;
                    sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                    total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                    uri = sKOSXmlDocument.getTitle();
                    uploadEnable = false;
                    BDDinsertEnable = true;
                    info = "File correctly loaded";                    
                } catch (IOException ex) {
                   error.append(System.getProperty("line.separator"));
                   error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }
            } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
            } finally {
                showError();
            }

        }
    }
    
    /**
     *
     * @param event
     */
    public void chargeJson(FileUploadEvent event) {
        progress = 0;

        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            InputStream is = null;
            try {
                try {
                    is = event.getFile().getInputstream();
                } catch (IOException ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }
                ReadRdf4j readRdf4j;
                try {
                    readRdf4j = new ReadRdf4j(is, 3);
                    warning = readRdf4j.getMessage();
                    progress = 100;
                    sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                    total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                    uri = sKOSXmlDocument.getTitle();
                    uploadEnable = false;
                    BDDinsertEnable = true;
                    info = "File correctly loaded";                    
                } catch (IOException ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }

            } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
            } finally {
                showError();
            }

        }
    }    

    /**
     *
     * @param event
     */
    public void chargeCsv(FileUploadEvent event) {
        progress = 0;

        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            try {
                CsvReadHelper csvReadHelper;
                try {
                    csvReadHelper = new CsvReadHelper(delimiterCsv);
                    Reader reader1 = new InputStreamReader(event.getFile().getInputstream());
                    if(! csvReadHelper.setLangs(reader1)){
                        error.append(csvReadHelper.getMessage());
                    }
                    Reader reader2 = new InputStreamReader(event.getFile().getInputstream());            
                    if (!csvReadHelper.readFile(reader2)) {
                        error.append(csvReadHelper.getMessage());
                    }                    
                    
                    warning = csvReadHelper.getMessage();
                    progress = 100;
                    
                    conceptObjects = csvReadHelper.getConceptObjects();
                    if(conceptObjects != null) {
                        langs = csvReadHelper.getLangs();
                        total = conceptObjects.size();
                        uri = "";//csvReadHelper.getUri();
                        uploadEnable = false;
                        BDDinsertEnable = true;
                        info = "File correctly loaded";                    
                    }
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }

            } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
            } finally {
                showError();
            }

        }
    }    
    
    /**
     *
     * @param event
     */
    public void chargeTurtle(FileUploadEvent event) {
        progress = 0;

        if (!PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else {
            InputStream is = null;
            try {
                try {
                    is = event.getFile().getInputstream();
                } catch (IOException ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }
                ReadRdf4j readRdf4j;
                try {
                    readRdf4j = new ReadRdf4j(is, 2);
                    warning = readRdf4j.getMessage();
                    progress = 100;
                    sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                    total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                    uri = sKOSXmlDocument.getTitle();
                    uploadEnable = false;
                    BDDinsertEnable = true;
                    info = "File correctly loaded";                    
                } catch (IOException ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                } catch (Exception ex) {
                    error.append(System.getProperty("line.separator"));
                    error.append(ex.getMessage());
                }
            } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
            } finally {
                showError();
            }

        }
    }

    
    /**
     * insrt un thésaurus dans la BDD
     *
     * 
     * #### temporaire ### 
     * ### permet d'ajouter une liste de candidats d'après un fichier SKOS ####
     */
    public void insertCandidatesBDD() {
        error = new StringBuffer();
        info = "";
        warning = "";
        
        
        CandidateHelper candidateHelper = new CandidateHelper();
        String idParentConcept;
        String notes;
        String idArk;        
        String idConcept = null;

        progress = 0;
        progress_abs = 0;
            
        for (SKOSResource sKOSResource : sKOSXmlDocument.getConceptList()) {
            
            for (SKOSLabel sKOSLabel : sKOSResource.getLabelsList()) {
                idParentConcept = "";
                if(sKOSLabel.getProperty() == SKOSProperty.prefLabel) {
                    if(sKOSLabel.getLanguage().equalsIgnoreCase("fr")) {
                        notes = getNotes(sKOSResource);
                        idArk = getIdParentConcept(sKOSResource);
                        if(idArk != null)
                            idParentConcept = new ConceptHelper().getIdConceptFromArkId(connect.getPoolConnexion(), idArk);
                        try {
                            Connection conn = connect.getPoolConnexion().getConnection();
                            idConcept = candidateHelper.addCandidat_rollBack(conn,
                                    sKOSLabel.getLabel(),
                                    sKOSLabel.getLanguage(),
                                    roleOnTheso.getIdTheso(),
                                    currentUser.getUser().getIdUser(),
                                    notes,
                                    idParentConcept,
                                    "");
                            if(idConcept == null) {
                                conn.rollback();
                                conn.close();
                                continue;
                            }
                            conn.commit();
                            conn.close();
                      
                        } catch (SQLException ex) {
                            java.util.logging.Logger.getLogger(rdf4jFileBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        if(idConcept != null) {
                            candidateHelper.addTermCandidatTraduction(
                                connect.getPoolConnexion(),
                                idConcept,
                                sKOSLabel.getLabel(),
                                sKOSLabel.getLanguage(),
                                roleOnTheso.getIdTheso(),
                                currentUser.getUser().getIdUser());
                        }
                    }
                }
            }
        }            
    }    
    
    private String getIdParentConcept(SKOSResource sKOSResource) {
        String idArk;
        for (SKOSMatch sKOSMatch : sKOSResource.getMatchList()) {
            if(sKOSMatch.getValue().contains("https://ark.frantiq.fr")) {    
                if(sKOSMatch.getProperty() == SKOSProperty.broadMatch) {
                    idArk = sKOSMatch.getValue().substring(sKOSMatch.getValue().indexOf("ark:/")+5);
                    return idArk;
                }
            }
        }
        return null;
    }
    
    private String getNotes(SKOSResource sKOSResource) {
        String notes = "";
        for (SKOSDocumentation sDocumentation : sKOSResource.getDocumentationsList()) {
            if(!sDocumentation.getText().isEmpty()) {
                if(sDocumentation.getProperty() == SKOSProperty.changeNote) {
                    if(notes.isEmpty())
                        notes = sDocumentation.getText();
                    else 
                        notes = notes + " ####  " + sDocumentation.getText();
                }
            }
        }
        return notes;
    }    
    


    /**
     * insert un thésaurus dans la BDD (CSV)
     *
     * @param selectedUserGroup
     */
    public void insertCsvBDD(String selectedUserGroup) {
        if(conceptObjects == null) return;
        if(conceptObjects.isEmpty()) return;
        
        error = new StringBuffer();
        info = "";
        warning = "";

        int idGroup; 
        
        if(currentUser.getUser().isIsSuperAdmin()) 
            idGroup = -1;
        else
            idGroup = Integer.parseInt(selectedUserGroup);
        
        try {
            progress = 0;
            progress_abs = 0;
            CsvImportHelper csvImportHelper = new CsvImportHelper(roleOnTheso.getNodePreference());
            csvImportHelper.setInfos(
                    formatDate, 
                    currentUser.getUser().getIdUser(), idGroup,
                    connect.getWorkLanguage());


            progress_abs++;
            progress = progress_abs / total * 100;

            if(!csvImportHelper.addTheso(
                        connect.getPoolConnexion(),
                        this,
                        thesaurusName, conceptObjects,
                        langs)) {
                error.append(csvImportHelper.getMessage());
            }

            //new UserHelper().addRole(connect.getPoolConnexion().getConnection(), idUser,idRole, ImportRdf4jHelper.getIdFromUri(uri) , "");
            
            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;

            info = "Thesaurus correctly insert into data base";
            info = info + "\n" + csvImportHelper.getMessage().toString();
//            showError();

        } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
        } finally {
            showError();
        }

    }        
    
    /**
     * insert un thésaurus dans la BDD (Skos)
     *
     * @param selectedUserGroup
     */
    public void insertBDD(String selectedUserGroup) {
        error = new StringBuffer();
        info = "";
        warning = "";

        int idGroup; 
        
        if(currentUser.getUser().isIsSuperAdmin()) 
            idGroup = -1;
        else
            idGroup = Integer.parseInt(selectedUserGroup);
        
        try {
            progress = 0;
            progress_abs = 0;
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(connect.getPoolConnexion(),
                    formatDate, 
                    currentUser.getUser().getIdUser(), idGroup,
                    connect.getWorkLanguage());
            
            // pour récupérer les identifiants pérennes type Ark ou Handle
            importRdf4jHelper.setIdentifierType(selectedIdentifier);
            
            importRdf4jHelper.setPrefixHandle(prefixHandle);
            importRdf4jHelper.setNodePreference(roleOnTheso.getNodePreference());
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addThesaurus();
            } catch (SQLException ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            } catch (Exception ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            }
            progress_abs++;
            progress = progress_abs / total * 100;
            importRdf4jHelper.addGroups(this);

            try {
                importRdf4jHelper.addConcepts(this);
            } catch (SQLException ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            } catch (ParseException ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            } catch (Exception ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            }
            
            //new UserHelper().addRole(connect.getPoolConnexion().getConnection(), idUser,idRole, ImportRdf4jHelper.getIdFromUri(uri) , "");
            
            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;

            info = "Thesaurus correctly insert into data base";
            info = info + "\n" + importRdf4jHelper.getMessage().toString();
//            showError();

        } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
        } finally {
            showError();
        }

    }

    /**
     * ajoute un seul concept a la base de données
     *
     * @param selectedUserGroup
     * @param selectedTerme
     */
    public void inserSingleConcept(String selectedUserGroup, SelectedTerme selectedTerme) {
        int idGroup; 
        
        if(currentUser.getUser().isIsSuperAdmin()) 
            idGroup = -1;
        else
            idGroup = Integer.parseInt(selectedUserGroup);        
        try {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(connect.getPoolConnexion(), formatDate, 
                    currentUser.getUser().getIdUser(),
                    idGroup, 
                    connect.getWorkLanguage());
            // pour récupérer les identifiants pérennes type Ark ou Handle
            importRdf4jHelper.setIdentifierType(selectedIdentifier);
            
            importRdf4jHelper.setPrefixHandle(prefixHandle);
            importRdf4jHelper.setNodePreference(roleOnTheso.getNodePreference());
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addSingleConcept(selectedTerme);
            } catch (SQLException ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            } catch (ParseException ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            } catch (Exception ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            }
            tree.reInit();
            tree.reExpand();
            tree.getSelectedTerme().majTerme((MyTreeNode) tree.getSelectedNode());

            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;
        } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
        } finally {
            showError();
        }

    }

    /**
     * ajoute une branche a la base de données
     *
     * @param selectedUserGroup
     * @param selectedTerme
     */
    public void inserBranch(String selectedUserGroup, SelectedTerme selectedTerme) {
        int idGroup; 
        
        if(currentUser.getUser().isIsSuperAdmin()) 
            idGroup = -1;
        else
            idGroup = Integer.parseInt(selectedUserGroup);     
        try {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(connect.getPoolConnexion(), formatDate,
                    currentUser.getUser().getIdUser(),
                    idGroup, 
                    connect.getWorkLanguage());
            // pour récupérer les identifiants pérennes type Ark ou Handle
            importRdf4jHelper.setIdentifierType(selectedIdentifier);
            
            importRdf4jHelper.setPrefixHandle(prefixHandle);
            importRdf4jHelper.setNodePreference(roleOnTheso.getNodePreference());
            
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addBranch(selectedTerme);
            } catch (ParseException ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            } catch (SQLException ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            } catch (Exception ex) {
                error.append(System.getProperty("line.separator"));
                error.append(ex.getMessage());
            }
            tree.reInit();
            tree.reExpand();
            tree.getSelectedTerme().majTerme((MyTreeNode) tree.getSelectedNode());

            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;

        } catch (Exception e) {
                error.append(System.getProperty("line.separator"));
                error.append(e.toString());
        } finally {
            showError();
        }

    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isUploadEnable() {
        return uploadEnable;
    }

    public void setUploadEnable(boolean uploadEnable) {
        this.uploadEnable = uploadEnable;
    }

    public boolean isBDDinsertEnable() {
        return BDDinsertEnable;
    }

    public void setBDDinsertEnable(boolean BDDinsertEnable) {
        this.BDDinsertEnable = BDDinsertEnable;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public SKOSXmlDocument getsKOSXmlDocument() {
        return sKOSXmlDocument;
    }

    public void setsKOSXmlDocument(SKOSXmlDocument sKOSXmlDocument) {
        this.sKOSXmlDocument = sKOSXmlDocument;
    }

    public int getProgress() {
        return (int) progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getAbs_progress() {
        return progress_abs;
    }

    public void setAbs_progress(double abs_progress) {
        this.progress_abs = abs_progress;
    }

    public double getProgress_abs() {
        return progress_abs;
    }

    public void setProgress_abs(double progress_abs) {
        this.progress_abs = progress_abs;
    }

    public NewTreeBean getTree() {
        return tree;
    }

    public void setTree(NewTreeBean tree) {
        this.tree = tree;
    }

    public int getTypeImport() {
        return typeImport;
    }

    public void setTypeImport(int typeImport) {
        this.typeImport = typeImport;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getError() {
        return error.toString();
    }

    public void setError(StringBuffer error) {
        this.error = error;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public boolean warningIsEmpty() {
        return warning == null || warning.isEmpty();
    }

    public String getSelectedIdentifier() {
        return selectedIdentifier;
    }

    public void setSelectedIdentifier(String selectedIdentifier) {
        this.selectedIdentifier = selectedIdentifier;
    }

    public String getPrefixHandle() {
        return prefixHandle;
    }

    public void setPrefixHandle(String prefixHandle) {
        this.prefixHandle = prefixHandle;
    }

    public CurrentUser2 getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser2 currentUser) {
        this.currentUser = currentUser;
    }

    public RoleOnThesoBean getRoleOnTheso() {
        return roleOnTheso;
    }

    public void setRoleOnTheso(RoleOnThesoBean roleOnTheso) {
        this.roleOnTheso = roleOnTheso;
    }

    public char getDelimiterCsv() {
        return delimiterCsv;
    }

    public void setDelimiterCsv(char delimiterCsv) {
        this.delimiterCsv = delimiterCsv;
    }


    public String getThesaurusName() {
        return thesaurusName;
    }

    public void setThesaurusName(String thesaurusName) {
        this.thesaurusName = thesaurusName;
    }

    public int getChoiceDelimiter() {
        return choiceDelimiter;
    }

    public void setChoiceDelimiter(int choiceDelimiter) {
        this.choiceDelimiter = choiceDelimiter;
    }

}
