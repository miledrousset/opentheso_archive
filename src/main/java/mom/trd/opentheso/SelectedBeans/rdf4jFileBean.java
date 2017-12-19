/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.core.imports.rdf4j.ReadRdf4j;
import mom.trd.opentheso.core.imports.rdf4j.helper.ImportRdf4jHelper;
import mom.trd.opentheso.skosapi.SKOSXmlDocument;
import org.eclipse.rdf4j.model.Model;
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
    private int typeImport;
    /*
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;    
     */
    private String formatDate;
    private String uri;
    private double total;

    private boolean uploadEnable = true;
    private boolean BDDinsertEnable = false;

    private SKOSXmlDocument sKOSXmlDocument;
    private String info;
    private String error;
    private String warning;

    /**
     *
     * @param event
     */
    public void init() {
        info = "";
        error = "";
        warning = "";
        uri = "";
        formatDate = "";
        total = 0;
        uploadEnable = true;
        BDDinsertEnable = false;
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
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                ReadRdf4j readRdf4j = null;
                try {
                    readRdf4j = new ReadRdf4j(is, 0, this);
                } catch (IOException ex) {
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                if(readRdf4j==null) return;
                progress = 100;
                sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                uri = sKOSXmlDocument.getTitle();
                uploadEnable = false;
                BDDinsertEnable = true;

                info = "File correctly loaded";

            } catch (Exception e) {
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
        error = "";
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

        }

    }

    private void showError() {
        if (!info.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", info));
        }
        if (!error.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error :", error));
        }
        if (!warning.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning :", warning));
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
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                ReadRdf4j readRdf4j = null;
                try {
                    readRdf4j = new ReadRdf4j(is, 1, this);
                } catch (IOException ex) {
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }

                progress = 100;
                sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                uri = sKOSXmlDocument.getTitle();
                uploadEnable = false;
                BDDinsertEnable = true;
                info = "File correctly loaded";
            } catch (Exception e) {
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
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                ReadRdf4j readRdf4j = null;
                try {
                    readRdf4j = new ReadRdf4j(is, 3, this);
                } catch (IOException ex) {
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                if(readRdf4j == null) return;

                progress = 100;
                sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                uri = sKOSXmlDocument.getTitle();
                uploadEnable = false;
                BDDinsertEnable = true;
                info = "File correctly loaded";
            } catch (Exception e) {
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
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                ReadRdf4j readRdf4j = null;
                try {
                    readRdf4j = new ReadRdf4j(is, 2, this);
                } catch (IOException ex) {
                    error = ex.getMessage();
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                progress = 100;
                sKOSXmlDocument = readRdf4j.getsKOSXmlDocument();
                total = sKOSXmlDocument.getConceptList().size() + sKOSXmlDocument.getGroupList().size() + 1;
                uri = sKOSXmlDocument.getTitle();
                uploadEnable = false;
                BDDinsertEnable = true;

                info = "File correctly loaded";
            } catch (Exception e) {
            } finally {
                showError();
            }

        }
    }

    /**
     * insrt un thésaurus dans la BDD
     *
     * @param idUser
     * @param idRole
     */
    public void insertBDD(int idUser, int idRole) {
        error = "";
        info = "";
        warning = "";

        try {
            progress = 0;
            progress_abs = 0;
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(connect.getPoolConnexion(), formatDate, uploadEnable, "adresse", idUser, idRole, /*langueBean.getIdLangue()*/ "fr");
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addThesaurus();
            } catch (SQLException ex) {
                error = ex.getMessage();
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            progress_abs++;
            progress = progress_abs / total * 100;
            importRdf4jHelper.addGroups(this);
            try {
                importRdf4jHelper.addConcepts(this);
            } catch (SQLException ex) {
                error = ex.getMessage();
            } catch (ParseException ex) {
                error = ex.getMessage();
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            
            //new UserHelper().addRole(connect.getPoolConnexion().getConnection(), idUser,idRole, ImportRdf4jHelper.getIdFromUri(uri) , "");
            
            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;

            info = "Thesaurus correctly insert into data base";

        } catch (Exception e) {

        } finally {
            showError();
        }

    }

    /**
     * ajoute un seul concept a la base de données
     *
     * @param idUser
     * @param idRole
     * @param selectedTerme
     */
    public void inserSingleConcept(int idUser, int idRole, SelectedTerme selectedTerme) {
        try {

        } catch (Exception e) {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(connect.getPoolConnexion(), formatDate, uploadEnable, "adresse", idUser, idRole, /*langueBean.getIdLangue()*/ "fr");
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addSingleConcept(selectedTerme);
            } catch (SQLException ex) {
                error = ex.getMessage();
            } catch (ParseException ex) {
                error = ex.getMessage();
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            tree.reInit();
            tree.reExpand();
            tree.getSelectedTerme().majTerme((MyTreeNode) tree.getSelectedNode());

            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;
        } finally {
            showError();
        }

    }

    /**
     * ajoute une branche a la base de données
     *
     * @param idUser
     * @param idRole
     * @param selectedTerme
     */
    public void inserBranch(int idUser, int idRole, SelectedTerme selectedTerme) {

        try {
            ImportRdf4jHelper importRdf4jHelper = new ImportRdf4jHelper();
            importRdf4jHelper.setInfos(connect.getPoolConnexion(), formatDate, uploadEnable, "adresse", idUser, idRole, /*langueBean.getIdLangue()*/ "fr");
            importRdf4jHelper.setRdf4jThesaurus(sKOSXmlDocument);
            try {
                importRdf4jHelper.addBranch(selectedTerme);
            } catch (ParseException ex) {
                error = ex.getMessage();
            } catch (SQLException ex) {
                error = ex.getMessage();
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            tree.reInit();
            tree.reExpand();
            tree.getSelectedTerme().majTerme((MyTreeNode) tree.getSelectedNode());

            uploadEnable = true;
            BDDinsertEnable = false;
            uri = null;
            total = 0;

        } catch (Exception e) {
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
        return error;
    }

    public void setError(String error) {
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

}
