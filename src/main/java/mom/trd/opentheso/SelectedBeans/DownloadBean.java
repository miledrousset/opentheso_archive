package mom.trd.opentheso.SelectedBeans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.core.exports.helper.ExportTabulateHelper;
import mom.trd.opentheso.core.exports.helper.ExportThesaurus;
import mom.trd.opentheso.core.exports.helper.ExportTxtHelper;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.exports.old.ExportFromBDD_Frantiq;
import mom.trd.opentheso.core.exports.pdf.WritePdf;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import mom.trd.opentheso.core.exports.rdf4j.ExportRdf4jHelper;
import mom.trd.opentheso.core.jsonld.helper.JsonldHelper;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.primefaces.model.ByteArrayContent;
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

    @ManagedProperty(value = "#{currentUser}") 
    private CurrentUser2 currentUser;
    
    @ManagedProperty(value = "#{roleOnTheso}")
    private RoleOnThesoBean roleOnTheso;  

    private String nomUsu;

    private int progress_per_100 = 0;

    public int getProgress_per_100() {
        return progress_per_100;
    }

    public void setProgress_per_100(int progress_per_100) {
        this.progress_per_100 = progress_per_100;
    }

    public int getProgress_abs() {
        return progress_abs;
    }

    public void setProgress_abs(int progress_abs) {
        this.progress_abs = progress_abs;
    }

    public double getSizeOfTheso() {
        return sizeOfTheso;
    }

    private int progress_abs = 0;
    private double sizeOfTheso;

    public int getProgress() {
        return progress_per_100;
    }

    @PostConstruct
    public void initTerme() {
        if (currentUser.getUser() == null || roleOnTheso.getNodePreference() == null) {
            return;
        }
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

        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        return exportFromBDD.exportConcept(connect.getPoolConnexion(),
                idTheso,
                idC).toString();

        //   new ExportFromBDD().exportConcept(connect.getPoolConnexion(), idTheso, idC).toString();
    }

    public String groupSkos(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        return exportFromBDD.exportThisGroup(connect.getPoolConnexion(), idTheso, idGroup).toString();
    }

    public String groupJsonLd(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        StringBuffer skos_local = exportFromBDD.exportThisGroup(connect.getPoolConnexion(), idTheso, idGroup);

        JsonldHelper jsonHelper = new JsonldHelper();
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
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

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

        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

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

    private WriteRdf4j loadExportHelper(String idTheso, List<NodeLang> selectedLanguages, List<NodeGroup> selectedGroups) {
        progress_per_100 = 0;
        progress_abs = 0;
        ConceptHelper conceptHelper = new ConceptHelper();

        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(connect.getPoolConnexion(), idTheso);
        if(nodePreference == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Info :", "!!! Veuillez ouvrir le thésaurus pour l'initialiser avant export !!!"));
            return null;
        }
        
        sizeOfTheso = conceptHelper.getConceptCountOfThesaurus(connect.getPoolConnexion(), idTheso);
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(connect.getPoolConnexion(), "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());
        exportRdf4jHelper.addThesaurus(idTheso, selectedLanguages);
        exportRdf4jHelper.addGroup(idTheso, selectedLanguages, selectedGroups);
        exportRdf4jHelper.addConcept(idTheso, this, selectedLanguages);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;
    }

    public StreamedContent thesoToFile(String idTheso,
            List<NodeLang> selectedLanguages,
            List<NodeGroup> selectedGroups, int type) {

        RDFFormat format = null;
        String extention = "";
        file = null;

        switch (type) {
            case 0:
                format = RDFFormat.RDFXML;
                extention = "_skos.xml";
                break;
            case 1:
                format = RDFFormat.JSONLD;
                extention = "_json-ld.json";
                break;
            case 2:
                format = RDFFormat.TURTLE;
                extention = "_turtle.ttl";
                break;
            case 3:
                format = RDFFormat.RDFJSON;
                extention = "_json.json";
                break;                
        }

        WriteRdf4j writeRdf4j = loadExportHelper(idTheso, selectedLanguages, selectedGroups);
        if(writeRdf4j != null) {
            ByteArrayOutputStream out;
            out = new ByteArrayOutputStream();
            Rio.write(writeRdf4j.getModel(), out, format);
            file = new ByteArrayContent(out.toByteArray(), "application/xml", idTheso + " " + extention);
        }
        progress_per_100 = 0;
        progress_abs = 0;
        return file;
    }

    /**
     * Cette fonction permet d'exporter un thésaurus en SKOS en précisant les
     * langues et les domaines à exporter
     *
     * @param idTheso
     * @param selectedLanguages
     * @param selectedGroups
     * @return
     */
    public StreamedContent thesoToSkosAdvanced(String idTheso,
            List<NodeLang> selectedLanguages,
            List<NodeGroup> selectedGroups) {

        progress_per_100 = 0;
        progress_abs = 0;

        ConceptHelper conceptHelper = new ConceptHelper();
        sizeOfTheso = conceptHelper.getAllIdConceptOfThesaurus(connect.getPoolConnexion(), idTheso).size();

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        StringBuffer skos_local = exportFromBDD.exportThesaurusAdvanced(
                connect.getPoolConnexion(), idTheso,
                selectedLanguages, selectedGroups, this);

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(skos_local.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "application/xml", idTheso + "_skos.rdf");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        progress_per_100 = 0;
        progress_abs = 0;

        return file;
    }

    /*
    public StreamedContent thesoToSkosRdf4j(String idTheso,
            List<NodeLang> selectedLanguages,
            List<NodeGroup> selectedGroups) {

        WriteRdf4j writeRdf4j = loadExportHelper(idTheso);
        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, RDFFormat.RDFXML);
        file = new ByteArrayContent(out.toByteArray(), "application/xml", idTheso + "_skos.xml");
        progress_per_100 = 0;
        progress_abs = 0;
        return file;
    }

    public StreamedContent thesoToJsonLdRdf4j(String idTheso,
            List<NodeLang> selectedLanguages,
            List<NodeGroup> selectedGroups) {

        WriteRdf4j writeRdf4j = loadExportHelper(idTheso);
        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, RDFFormat.JSONLD);
        file = new ByteArrayContent(out.toByteArray(), "application/json", idTheso + "_Json-LD.json");
        progress_per_100 = 0;
        progress_abs = 0;
        return file;
    }

    public StreamedContent thesoToTurtleRdf4j(String idTheso,
            List<NodeLang> selectedLanguages,
            List<NodeGroup> selectedGroups) {

        WriteRdf4j writeRdf4j = loadExportHelper(idTheso);
        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, RDFFormat.TURTLE);
        file = new ByteArrayContent(out.toByteArray(), "application/x-turtle", idTheso + "_Turtle.ttl");
        progress_per_100 = 0;
        progress_abs = 0;
        return file;
    }
     */
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

        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

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
     * Cette fonction permet d'exporter un concept en SKOS en temps réel dans la
     * page principale
     *
     * @param idConcept
     * @param idTheso
     * @param type
     * @return
     */
    public StreamedContent conceptToFile(String idConcept, String idTheso, int type) {

        RDFFormat format = null;
        String extention = "";

        switch (type) {
            case 0:
                format = RDFFormat.RDFXML;
                extention = "_skos.xml";
                break;
            case 1:
                format = RDFFormat.JSONLD;
                extention = "_json-ld.json";
                break;
            case 2:
                format = RDFFormat.TURTLE;
                extention = "_turtle.ttl";
                break;
            case 3:
                format = RDFFormat.RDFJSON;
                extention = "_turtle.ttl";
                break;
        }
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(connect.getPoolConnexion(), idTheso);
        
        if(nodePreference == null) return null;
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(connect.getPoolConnexion(), "dd-mm-yyyy", false, idTheso,nodePreference.getCheminSite());
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, format);
        file = new ByteArrayContent(out.toByteArray(), "application/xml", idTheso + " " + extention);

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

        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        InputStream stream;
        StringBuffer skos_local = exportFromBDD.exportConcept(connect.getPoolConnexion(),
                idTheso, idC);

        JsonldHelper jsonHelper = new JsonldHelper();
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
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

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
     * Cette fonction permet de retourner une branche en SKOS
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    public StreamedContent brancheToFile(String idConcept, String idTheso,int type) {
        
        
        RDFFormat format = null;
        String extention = "";

        switch (type) {
            case 0:
                format = RDFFormat.RDFXML;
                extention = "_skos.xml";
                break;
            case 1:
                format = RDFFormat.JSONLD;
                extention = "_json-ld.json";
                break;
            case 2:
                format = RDFFormat.TURTLE;
                extention = "_turtle.ttl";
                break;
        }
        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(connect.getPoolConnexion(), idTheso);
        if(nodePreference == null) return null;
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setInfos(connect.getPoolConnexion(), "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());
        exportRdf4jHelper.setNodePreference(nodePreference);
        
        exportRdf4jHelper.addBranch(idTheso, idConcept);

        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, format);
        file = new ByteArrayContent(out.toByteArray(), "application/xml", idTheso + " " + extention);
      
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
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        StringBuffer skos_local = exportFromBDD.exportBranchOfConcept(connect.getPoolConnexion(), idTheso, idConcept);

        JsonldHelper jsonHelper = new JsonldHelper();
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
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

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
     * Cette fonction permet de retourner pour téléchargement un groupe en
     * JsonLd
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    public StreamedContent thisGroupToJsonLd(String idGroup, String idTheso) {
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        StringBuffer skos_local = exportFromBDD.exportThisGroup(connect.getPoolConnexion(), idTheso, idGroup);

        JsonldHelper jsonHelper = new JsonldHelper();
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
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

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
        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

        StringBuffer skos_local = exportFromBDD.exportGroup(connect.getPoolConnexion(), idTheso, idGroup);

        JsonldHelper jsonHelper = new JsonldHelper();
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

        exportFromBDD.setServerAdress(roleOnTheso.getNodePreference().getCheminSite());
        exportFromBDD.setServerArk(roleOnTheso.getNodePreference().getServeurArk());
        exportFromBDD.setArkActive(roleOnTheso.getNodePreference().isUseArk());

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
     * @return 
     */
    public StreamedContent thesoCsv(String idTheso) {

        progress_per_100 = 0;
        progress_abs = 0;

        ConceptHelper conceptHelper = new ConceptHelper();
        sizeOfTheso = conceptHelper.getAllIdConceptOfThesaurus(connect.getPoolConnexion(), idTheso).size();

        ExportTabulateHelper exportTabulateHelper = new ExportTabulateHelper();

        exportTabulateHelper.setThesaurusDatas(connect.getPoolConnexion(), idTheso);
        exportTabulateHelper.exportToTabulate();
        StringBuffer temp = exportTabulateHelper.getTabulateBuff();
        InputStream stream;
        try {
            stream = new ByteArrayInputStream(temp.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "text/csv", "downloadedCsv.csv");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }
    
    /**
     * Cette fonction permet d'exporter un thésaurus au format CSV à partir de
     * son identifiant. 
     * Il est filtré par Langue et par Groupe
     * Le résultat est enregistré dans la variable 'skos' du
     * downloadBean si la taille est petite, ou dans la variable 'file' du
     * downloadBean sinon. Dans le premier cas on affiche la variable, dans le
     * second cas l'utilisateur télécharge de fichier.
     *
     * @param idTheso
     * @param selectedLanguages
     * @param selectedGroups
     * @return 
     */
    public StreamedContent thesoCsv2(String idTheso,
            List<NodeLang> selectedLanguages,
            List<NodeGroup> selectedGroups) {

        progress_per_100 = 0;
        progress_abs = 0;

        ConceptHelper conceptHelper = new ConceptHelper();
        sizeOfTheso = conceptHelper.getAllIdConceptOfThesaurus(connect.getPoolConnexion(), idTheso).size();

        ExportTabulateHelper exportTabulateHelper = new ExportTabulateHelper();

        exportTabulateHelper.setThesaurusDatas(connect.getPoolConnexion(),
                idTheso, selectedLanguages, selectedGroups);
        exportTabulateHelper.exportToTabulate();
        StringBuffer temp = exportTabulateHelper.getTabulateBuff();
        InputStream stream;
        try {
            stream = new ByteArrayInputStream(temp.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "text/csv", "downloadedCsv.csv");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }    

    public StreamedContent thesoPDF(String idTheso, List<NodeLang> selectedLanguages,
            List<NodeGroup> selectedGroups, String codeLang, String codeLang2, int type) {

        progress_per_100 = 0;
        progress_abs = 0;

        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(connect.getPoolConnexion(), idTheso);
        if(nodePreference == null) return null;
        
        sizeOfTheso = new ConceptHelper().getAllIdConceptOfThesaurus(connect.getPoolConnexion(), idTheso).size();
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setInfos(connect.getPoolConnexion(), "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.addThesaurus(idTheso, selectedLanguages);
        exportRdf4jHelper.addGroup(idTheso, selectedLanguages, selectedGroups);
        exportRdf4jHelper.addConcept(idTheso, this, selectedLanguages);


        WritePdf writePdf = new WritePdf(exportRdf4jHelper.getSkosXmlDocument(), codeLang, codeLang2, type);

        InputStream stream;
        stream = new ByteArrayInputStream(writePdf.getOutput().toByteArray());
        file = new DefaultStreamedContent(stream, "application/pdf", "test.pdf");

        return file;
    }
    
    /**
     * Permet d'exporter le thésauurus au format txt (tabulé et hiérarchisé)
     * avec des options de colonnes supplémentaires NT, BT, RT, Notes, traductions 
     * ceci figure dans le tableau (String[] selectedOptions)
     * @param idTheso
     * @param selectedGroups
     * @param codeLang
     * @param selectedOptions
     * @return 
     * MR
     */
    public StreamedContent thesoTxtCsv(String idTheso,
            List<NodeGroup> selectedGroups, String codeLang,
            ArrayList<String> selectedOptions) {

        progress_per_100 = 0;
        progress_abs = 0;

        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(connect.getPoolConnexion(), idTheso);
        if(nodePreference == null) return null;
        

        ExportTxtHelper exportTxtHelper = new ExportTxtHelper();
        exportTxtHelper.setThesaurusDatas(connect.getPoolConnexion(), idTheso, codeLang, selectedGroups,
                nodePreference, selectedOptions);
        exportTxtHelper.exportToTxtCsv();

        InputStream stream;

        try {
            stream = new ByteArrayInputStream(exportTxtHelper.getTxtBuff().toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "text/csv", "Txt_" + idTheso + ".csv");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return file;
    } 

    public StreamedContent getAllAltLabels(String idTheso,
            String codeLang) {

        progress_per_100 = 0;
        progress_abs = 0;

        ExportThesaurus exportThesaurus = new ExportThesaurus();
        StringBuilder stringBuilder = exportThesaurus.exportAltLabel(connect.getPoolConnexion(),
                idTheso, codeLang);
        if(stringBuilder == null) return null;

        InputStream stream;
        try {
            stream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "text/csv", "AltLabels_" + idTheso +".csv");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;        
    }
    
    /**
     * récupère tous les identifiants Ark, Handle, IdConcept, PrefLable, Note, alignement
     * @param idTheso
     * @param codeLang
     * @param selectedGroups
     * @return 
     */
    public StreamedContent getIdentifiers(String idTheso,
            String codeLang, List<NodeGroup> selectedGroups) {

        progress_per_100 = 0;
        progress_abs = 0;

        NodePreference nodePreference =  new PreferencesHelper().getThesaurusPreferences(connect.getPoolConnexion(), idTheso);
        if(nodePreference == null) return null;
        
        ExportThesaurus exportThesaurus = new ExportThesaurus();
        StringBuilder stringBuilder = exportThesaurus.exportIdentifier(
                connect.getPoolConnexion(),
                idTheso, codeLang,
                selectedGroups, nodePreference);
        if(stringBuilder == null) return null;

        InputStream stream;
        try {
            stream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
            file = new DefaultStreamedContent(stream, "text/csv", "Identifiers_" + idTheso +".csv");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        languageBean.getMsg("info") + " :", exportThesaurus.getMessage().toString()));
        
        return file;        
    }    
    
   /**
     * récupère tous les alignements au foramt SKOS
     * @param idTheso
     * @param selectedGroups
     * @return 
     */
    public StreamedContent getAlignments(String idTheso,
            List<NodeGroup> selectedGroups) {

        progress_per_100 = 0;
        progress_abs = 0;

        WriteRdf4j writeRdf4j = loadExportAlignment(selectedGroups, idTheso);
        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();
        Rio.write(writeRdf4j.getModel(), out, RDFFormat.RDFXML);
        file = new ByteArrayContent(out.toByteArray(), "application/xml", idTheso + "_alignment" + "_skos.xml");
        progress_per_100 = 0;
        progress_abs = 0;
        return file;        
        
        
    }      
    private WriteRdf4j loadExportAlignment(List<NodeGroup> selectedGroups, String idTheso) {
        progress_per_100 = 0;
        progress_abs = 0;
        
        NodePreference nodePreference = new PreferencesHelper().getThesaurusPreferences(connect.getPoolConnexion(), idTheso);
        if (nodePreference == null) {
            return null;
        }
        
        ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
        exportRdf4jHelper.setNodePreference(nodePreference);
        exportRdf4jHelper.setInfos(connect.getPoolConnexion(), "dd-mm-yyyy", false, idTheso, nodePreference.getCheminSite());

        exportRdf4jHelper.getAllAlignment(selectedGroups, idTheso);                
        WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());
        return writeRdf4j;        
    }


    /**
     * Applelation de la funtion pour realiser l'injection a la BDD; on puex
     * choisir le fichier dans une fenetre que se ouvre;
     */
    /**
     * Applelation de la funtion avec les parametres pour avoir le motpasstemp
     */
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

    public String getNomUsu() {
        return nomUsu;
    }

    public void setNomUsu(String nomUsu) {
        this.nomUsu = nomUsu;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
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


}
