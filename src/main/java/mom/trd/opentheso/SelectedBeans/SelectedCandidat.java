package mom.trd.opentheso.SelectedBeans;

import com.sun.mail.smtp.SMTPTransport;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import mom.trd.LanguageBean;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.CandidateHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeCandidat;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeCandidatList;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeMessageAdmin;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeProposition;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeTraductionCandidat;
import org.primefaces.event.TabChangeEvent;

@ManagedBean(name = "selectedCandidat", eager = true)
@SessionScoped
public class SelectedCandidat implements Serializable {

    private static final long serialVersionUID = 1L;
    private NodeCandidatList selected;
    private NodeCandidat infoCdt;
    private String langueEdit = "";
    private String valueEdit = "";
    private String noteEdit = "";
    private String niveauEdit = "";
    private String domaineEdit = "";
    private ArrayList<String> tradInsert;
    private NodeAutoCompletion selectedNvx;
    private String msgValid;

    private String note = "";
    private String niveau = "";
    private String domaine = "";
    private NodeMessageAdmin msgAdm;

    private String idTheso = "";
    private String langueTheso;

    private Date createdProposition;
    private Date modifiedProposition;

    private ArrayList<String> nomsProp;

    // Variables resourcesBundle
    private boolean arkActive;
    private String serverAdress;

    @ManagedProperty(value = "#{user1}")
    private CurrentUser theUser;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    /**
     * ************************** INITIALISATION ****************************
     */
    public SelectedCandidat() {
        ResourceBundle bundlePref = getBundlePref();
        String temp = bundlePref.getString("useArk");
        arkActive = temp.equals("true");
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

    @PostConstruct
    public void initSelectedCandidat() {
        selected = new NodeCandidatList();
        selected.setEtat("");
        selected.setIdConcept("");
        selected.setValue("");
        selected.setNbProp(0);
        infoCdt = new NodeCandidat();
        infoCdt.setNodesUser(new ArrayList<NodeUser>());
        infoCdt.setNodeTraductions(new ArrayList<NodeTraductionCandidat>());
        selectedNvx = new NodeAutoCompletion();
        tradInsert = new ArrayList<>();
    }

    /**
     * Réinitialise le candidat (tout est vide)
     */
    public void reInit() {
        selected = new NodeCandidatList();
        selected.setEtat("");
        selected.setIdConcept("");
        selected.setValue("");
        selected.setNbProp(0);
        infoCdt = new NodeCandidat();
        infoCdt.setNodesUser(new ArrayList<NodeUser>());
        infoCdt.setNodeTraductions(new ArrayList<NodeTraductionCandidat>());
        note = "";
        niveau = "";
        domaine = "";
    }

    /**
     * ************************** MISE A JOUR ****************************
     */
    /**
     * Changement de la proposition courrante à la sélection du nom d'un
     * utilisateur dans la table des propositions
     *
     * @param event
     */
    public void onChange(TabChangeEvent event) {
        String nom = (String) event.getData();
        for (NodeUser nu : infoCdt.getNodesUser()) {
            if (nom.equals(nu.getName())) {
                NodeProposition np = new CandidateHelper().getNodePropositionOfUser(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, nu.getId());
                note = np.getNote();
                niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langueTheso) + " (" + np.getIdConceptParent() + ")";
                domaine = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), np.getIdGroup(), idTheso, langueTheso) + " (" + np.getIdGroup() + ")";
                break;
            }
        }
    }

    /**
     * Récupération des informations d'un candidat lors de sa sélection dans la
     * table des candidats
     *
     * @param theso
     * @param langue
     */
    public void maj(String theso, String langue) {
        idTheso = theso;
        langueTheso = langue;
        infoCdt.setNodesUser(new CandidateHelper().getListUsersOfCandidat(connect.getPoolConnexion(), selected.getIdConcept(), theso));
        infoCdt.setNodeTraductions(new CandidateHelper().getNodeTraductionCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, langue));
        nomsProp = new ArrayList<>();
        for (NodeUser nuse : infoCdt.getNodesUser()) {
            nomsProp.add(nuse.getName());
        }
        if (selected.getIdConcept() != null && !selected.getIdConcept().equals("")) {
            NodeProposition np = new CandidateHelper().getNodePropositionOfUser(connect.getPoolConnexion(), selected.getIdConcept(), theso, infoCdt.getNodesUser().get(0).getId());
            note = np.getNote();
            niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langue) + " (" + np.getIdConceptParent() + ")";
            domaine = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), np.getIdGroup(), idTheso, langue) + " (" + np.getIdGroup() + ")";

            modifiedProposition = np.getModified();
            createdProposition = np.getCreated();
            if (selected.getEtat().equals("r") || selected.getEtat().equals("v") || selected.getEtat().equals("i")) {
                msgAdm = new CandidateHelper().getMessageAdmin(connect.getPoolConnexion(), idTheso, selected.getIdConcept());
            } else {
                msgAdm = new NodeMessageAdmin();
            }
        }

    }

    /**
     * ************************** INSERTION ****************************
     */
    /**
     * Création d'un nouveau candidat (avec une première proposition)
     *
     * @param theso
     * @param langue
     */
    public void newCandidat(String theso, String langue) {
        if (selectedNvx != null) {
            niveauEdit = selectedNvx.getIdConcept();
        } else {
            niveauEdit = "";
        }

        if (domaineEdit == null) {
            domaineEdit = "";
        }

        new CandidateHelper().addCandidat(connect.getPoolConnexion(), valueEdit, langue, theso, theUser.getUser().getId(), noteEdit, niveauEdit, domaineEdit);
        selectedNvx = new NodeAutoCompletion();
        noteEdit = "";
        domaineEdit = "";
        niveauEdit = "";
        valueEdit = "";
    }

    /**
     * Ajoute une proposition au candidat courant
     *
     * @param langue
     * @return
     */
    public boolean newPropCandidat(String langue) {
        try {
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
            if (selectedNvx != null) {
                niveauEdit = selectedNvx.getIdConcept();
            } else {
                niveauEdit = "";
            }

            if (domaineEdit == null) {
                domaineEdit = "";
            }

            if (!new CandidateHelper().addPropositionCandidat_RollBack(
                    conn,
                    selected.getIdConcept(), theUser.getUser().getId(),
                    idTheso, noteEdit, niveauEdit, domaineEdit)) {
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
            NodeProposition np = new CandidateHelper().getNodePropositionOfUser(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, infoCdt.getNodesUser().get(0).getId());
            infoCdt.setNodesUser(new CandidateHelper().getListUsersOfCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso));
            modifiedProposition = np.getModified();
            createdProposition = np.getCreated();
            nomsProp = new ArrayList<>();
            for (NodeUser nuse : infoCdt.getNodesUser()) {
                nomsProp.add(nuse.getName());
            }
            note = np.getNote();
            niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langue) + " (" + np.getIdConceptParent() + ")";
            domaine = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), np.getIdGroup(), idTheso, langue) + " (" + np.getIdGroup() + ")";

            selected.setNbProp(selected.getNbProp() + 1);

            // envoie d'email d'alerte !!
            int minAlert = new UserHelper().getPreferenceUser(connect.getPoolConnexion()).getNbAlertCdt();
            if (selected.getNbProp() >= minAlert) {
                ArrayList<String> lesMails = new UserHelper().getMailAdmin(connect.getPoolConnexion());
                for (String mail : lesMails) {
                    if (mail != null && !mail.trim().equals("")) {
                        envoyerMailAlertNb(selected.getValue(), mail, minAlert);
                    }
                }
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    /**
     * Un mail est envoyé à l'adresse mail passée en paramètre pour signaler
     * qu'un candidat a atteint le seuil de propositions activant l'alerte
     *
     * @param candidat
     * @param dest
     * @param minAlert
     * @return
     */
    public boolean envoyerMailAlertNb(String candidat, String dest, int minAlert) {
        try {
            boolean alert = new UserHelper().getPreferenceUser(connect.getPoolConnexion()).isAlertCdt();
            if (alert) {
                ResourceBundle bundlePref = getBundlePref();

                java.util.Properties props = new java.util.Properties();
                props.setProperty("mail.transport.protocol", bundlePref.getString("protocolMail"));
                props.setProperty("mail.smtp.host", bundlePref.getString("hostMail"));
                props.setProperty("mail.smtp.port", bundlePref.getString("portMail"));
                props.setProperty("mail.smtp.auth", bundlePref.getString("authMail"));
                Session session = Session.getInstance(props);

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(bundlePref.getString("mailFrom")));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(dest));
                msg.setSubject("Proposition de candidat");
                msg.setText("Le candidat " + candidat + " a été proposé au moins " + minAlert + " fois.");

                SMTPTransport transport = (SMTPTransport) session.getTransport(bundlePref.getString("transportMail"));
                transport.connect();
                transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
                transport.close();
            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * Un mail est envoyé à l'adresse mail passée en paramètre pour signaler
     * qu'un candidat a été validé
     *
     * @param candidat
     * @param dest
     * @return
     */
    public boolean envoyerMailAlertValid(String candidat, String dest) {
        try {
            boolean alert = new UserHelper().getPreferenceUser(connect.getPoolConnexion()).isAlertCdt();
            if (alert) {
                ResourceBundle bundlePref = getBundlePref();

                java.util.Properties props = new java.util.Properties();
                props.setProperty("mail.transport.protocol", bundlePref.getString("protocolMail"));
                props.setProperty("mail.smtp.host", bundlePref.getString("hostMail"));
                props.setProperty("SMTP_PORT_PROPERTY", bundlePref.getString("portMail"));
                props.setProperty("mail.smtp.auth", bundlePref.getString("authMail"));
                Session session = Session.getInstance(props);

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(bundlePref.getString("mailFrom")));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(dest));
                msg.setSubject("Gestion de candidats");
                msg.setText("Le candidat " + candidat + " a été validé par le(a) terminologue : " + theUser.getUser().getName() + ".");

                SMTPTransport transport = (SMTPTransport) session.getTransport(bundlePref.getString("transportMail"));
                transport.connect();
                transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
                transport.close();
            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * Un mail est envoyé à l'adresse mail passée en paramètre pour signaler
     * qu'un candidat a été refusé
     *
     * @param candidat
     * @param dest
     * @return
     */
    public boolean envoyerMailAlertRefut(String candidat, String dest) {
        try {
            boolean alert = new UserHelper().getPreferenceUser(connect.getPoolConnexion()).isAlertCdt();
            if (alert) {
                ResourceBundle bundlePref = getBundlePref();

                java.util.Properties props = new java.util.Properties();
                props.setProperty("mail.transport.protocol", bundlePref.getString("protocolMail"));
                props.setProperty("mail.smtp.host", bundlePref.getString("hostMail"));
                props.setProperty("SMTP_PORT_PROPERTY", bundlePref.getString("portMail"));
                props.setProperty("mail.smtp.auth", bundlePref.getString("authMail"));
                Session session = Session.getInstance(props);

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(bundlePref.getString("mailFrom")));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(dest));
                msg.setSubject("Gestion de candidats");
                msg.setText("Le candidat " + candidat + " a été refusé par le(a) terminologue : " + theUser.getUser().getName() + ".");

                SMTPTransport transport = (SMTPTransport) session.getTransport(bundlePref.getString("transportMail"));
                transport.connect();
                transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
                transport.close();
            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public void newTradCdt(String idT, String langue) {
        new CandidateHelper().addTermCandidatTraduction(connect.getPoolConnexion(), selected.getIdConcept(), valueEdit, langueEdit.trim(), idT, theUser.getUser().getId());
        infoCdt.setNodeTraductions(new CandidateHelper().getNodeTraductionCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, langue));
    }

    /**
     * ************************** SUPPRESSION ****************************
     */
    /**
     * Supprime la proposition. Si c'est la dernière, le candidat est
     * intégralement supprimé.
     */
    public void delPropCdt() {
        if (infoCdt.getNodesUser().size() == 1) { // Suppression du concept
            new CandidateHelper().deleteConceptCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso);
            reInit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info1")));
        } else { // Suppression de la proposition
            new CandidateHelper().deletePropositionCandidat(connect.getPoolConnexion(), selected.getIdConcept(), theUser.getUser().getId(), idTheso);
            infoCdt.setNodesUser(new CandidateHelper().getListUsersOfCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso));
            nomsProp = new ArrayList<>();
            for (NodeUser nuse : infoCdt.getNodesUser()) {
                nomsProp.add(nuse.getName());
            }
            note = "";
            niveau = "";
            domaine = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info2")));
        }
    }

    /**
     * Supprime la traduction séléctionnée
     *
     * @param langue
     */
    public void delTradCdt(String langue) {
        new CandidateHelper().deleteTraductionTermCandidat(connect.getPoolConnexion(), selected.getIdConcept(), langue, idTheso, theUser.getUser().getId());
        for (NodeTraductionCandidat nt : infoCdt.getNodeTraductions()) {
            if (nt.getIdLang().equals(langue)) {
                infoCdt.getNodeTraductions().remove(nt);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info3")));
    }

    /**
     * ************************** EDITION ****************************
     */
    /**
     * Passe un candidat de son état courant à l'état validé. Un mail est envoyé
     * aux admin et au contributor si l'option d'envois de mail est activée
     */
    public void toValid() {
        CandidateHelper ch = new CandidateHelper();
        ch.updateCandidatStatus(connect.getPoolConnexion(), "v", idTheso, selected.getIdConcept());
        ch.addAdminMessage(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, theUser.getUser().getId(), msgValid);
        msgValid = "";
        selected.setEtat("v");

        // envoie d'email d'alerte !!
        ArrayList<String> lesMails = new UserHelper().getMailAdmin(connect.getPoolConnexion());
        ArrayList<String> contribs = new UserHelper().getMailContributor(connect.getPoolConnexion(), selected.getIdConcept());
        for (String contrib : contribs) {
            if (!lesMails.contains(contrib)) {
                lesMails.add(contrib);
            }
        }
        for (String mail : lesMails) {
            if (mail != null && !mail.trim().equals("")) {
                envoyerMailAlertValid(selected.getValue(), mail);
            }

        }

        vue.setAddValidCdt(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info4")));
    }

    /**
     * Passe un candidat de son état courant à l'état refusé. Un mail est envoyé
     * aux admin et au contributor si l'option d'envois de mail est activée
     */
    public void toRefus() {
        CandidateHelper ch = new CandidateHelper();
        ch.updateCandidatStatus(connect.getPoolConnexion(), "r", idTheso, selected.getIdConcept());
        ch.addAdminMessage(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, theUser.getUser().getId(), msgValid);
        // gestion message
        msgValid = "";
        selected.setEtat("r");

        // envoie d'email d'alerte !!
        ArrayList<String> lesMails = new UserHelper().getMailAdmin(connect.getPoolConnexion());
        ArrayList<String> contribs = new UserHelper().getMailContributor(connect.getPoolConnexion(), selected.getIdConcept());
        for (String contrib : contribs) {
            if (!lesMails.contains(contrib)) {
                lesMails.add(contrib);
            }
        }
        for (String mail : lesMails) {
            if (mail != null && !mail.trim().equals("")) {
                envoyerMailAlertRefut(selected.getValue(), mail);
            }

        }

        vue.setAddValidCdt(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info5")));
    }

    /**
     * Passe un candidat de son état courant à l'état inséré. Les modifications
     * nécessaires en bases de données sont traitées ici.
     *
     * @return
     */
    public boolean toInsert() {

        if (selectedNvx != null) {
            niveauEdit = selectedNvx.getIdConcept();
        } else {
            niveauEdit = null;
        }
        if (domaineEdit == null || domaineEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sCdt.error1")));
            return false;
        }

        if (tradInsert.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sCdt.error2")));
            return false;
        }
        if (new TermHelper().isTermExist(connect.getPoolConnexion(),
                selected.getValue().trim(), idTheso, langueTheso)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error2")));
            return false;
        }

        HashMap<String, String> trad = new HashMap<>();
        if (tradInsert.contains(langueTheso)) {
            trad.put(langueTheso, selected.getValue());
            tradInsert.remove(langueTheso);
        }
        for (String l : tradInsert) {
            for (NodeTraductionCandidat ntc : infoCdt.getNodeTraductions()) {
                if (l.equals(ntc.getIdLang())) {
                    trad.put(l, ntc.getTitle());
                }
            }
        }
        ArrayList<Entry<String, String>> temp = new ArrayList<>(trad.entrySet());

        ConceptHelper instance = new ConceptHelper();

        Concept concept = new Concept();
        concept.setIdGroup(domaineEdit);
        concept.setIdThesaurus(idTheso);
        concept.setStatus("D");

        String langueTemp = temp.get(0).getKey();

        Term terme = new Term();
        terme.setId_thesaurus(idTheso);
        terme.setLang(temp.get(0).getKey());
        terme.setLexical_value(temp.get(0).getValue());
        temp.remove(0);

        String idc;

        if (niveauEdit == null || niveauEdit.trim().equals("")) { // Top concept
            idc = instance.addTopConcept(connect.getPoolConnexion(), idTheso, concept, terme, serverAdress, arkActive, theUser.getUser().getId());
        } else { // concept
            idc = instance.addConcept(connect.getPoolConnexion(), niveauEdit, concept, terme, serverAdress, arkActive, theUser.getUser().getId());
        }

        if (!temp.isEmpty()) {
            String idt = new TermHelper().getThisTerm(connect.getPoolConnexion(), idc, idTheso, langueTemp).getId_term();
            for (Entry<String, String> e : temp) {
                terme = new Term();
                terme.setId_concept(idc);
                terme.setId_term(idt);
                terme.setId_thesaurus(idTheso);
                terme.setLang(e.getKey());
                terme.setLexical_value(e.getValue());
                new TermHelper().addTraduction(connect.getPoolConnexion(), terme, theUser.getUser().getId());
            }
        }

        CandidateHelper ch = new CandidateHelper();
        ch.updateCandidatStatus(connect.getPoolConnexion(), "i", idTheso, selected.getIdConcept());
        ch.addAdminMessage(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, theUser.getUser().getId(), msgValid);
        msgValid = "";
        selected.setEtat("i");
        vue.setAddInsertCdt(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info6")));
        return true;
    }

    /**
     * Permet de modifier un candidat, ce qui implique que personne n'a encore
     * fait de proposition dessus et que c'est le contributor qui le modifit.
     */
    public void editMyCandidat() {
        if (valueEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sCdt.error3")));
        } else {
            new CandidateHelper().updateMotCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, valueEdit);
            selected.setValue(valueEdit);
            valueEdit = "";
            vue.setEditCandidat(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info7")));
        }

    }

    public void initEditProp() {
        NodeProposition np = new CandidateHelper().getNodePropositionOfUser(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, theUser.getUser().getId());
        noteEdit = np.getNote();
        niveauEdit = np.getIdConceptParent();
        domaineEdit = np.getIdGroup();
        vue.setEditPropCandidat(true);
    }

    /**
     * Permet d'éditer sa propre proposition
     *
     * @param langue
     */
    public void editMyProp(String langue) {
        if (selectedNvx != null) {
            niveauEdit = selectedNvx.getIdConcept();
        } else {
            niveauEdit = "";
        }

        if (domaineEdit == null) {
            domaineEdit = "";
        }

        new CandidateHelper().updatePropositionCandidat(connect.getPoolConnexion(), selected.getIdConcept(), theUser.getUser().getId(), idTheso, noteEdit, niveauEdit, domaineEdit);
        NodeProposition np = new CandidateHelper().getNodePropositionOfUser(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, infoCdt.getNodesUser().get(0).getId());
        infoCdt.setNodesUser(new CandidateHelper().getListUsersOfCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso));

        note = np.getNote();
        niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langue) + " (" + np.getIdConceptParent() + ")";
        domaine = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), np.getIdGroup(), idTheso, langue) + " (" + np.getIdGroup() + ")";

        vue.setEditPropCandidat(false);
        selectedNvx = new NodeAutoCompletion();
        domaineEdit = "";
        niveauEdit = "";
        noteEdit = "";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info8")));
    }

    /**
     * ************************** AUTRE ****************************
     */
    /**
     * Vérifie si l'utilisateur courrant a déjà fait une proposition sur le
     * candidat sélectionné
     *
     * @return vrai si c'est le cas, faux sinon
     */
    public boolean userHaveProp() {
        for (NodeUser nu : infoCdt.getNodesUser()) {
            if (nu.getId() == theUser.getUser().getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * ************************** GETTERS SETTERS ****************************
     */
    /**
     *
     * @return
     */
    public NodeCandidatList getSelected() {
        return selected;
    }

    public void setSelected(NodeCandidatList selected) {
        this.selected = selected;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getLangueEdit() {
        return langueEdit;
    }

    public void setLangueEdit(String langueEdit) {
        this.langueEdit = langueEdit;
    }

    public String getValueEdit() {
        return valueEdit;
    }

    public void setValueEdit(String valueEdit) {
        this.valueEdit = valueEdit;
    }

    public String getNoteEdit() {
        return noteEdit;
    }

    public void setNoteEdit(String noteEdit) {
        this.noteEdit = noteEdit;
    }

    public String getNiveauEdit() {
        return niveauEdit;
    }

    public void setNiveauEdit(String niveauEdit) {
        this.niveauEdit = niveauEdit;
    }

    public String getDomaineEdit() {
        return domaineEdit;
    }

    public void setDomaineEdit(String domaineEdit) {
        this.domaineEdit = domaineEdit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public ArrayList<String> getNomsProp() {
        return nomsProp;
    }

    public void setNomsProp(ArrayList<String> nomsProp) {
        this.nomsProp = nomsProp;
    }

    public NodeCandidat getInfoCdt() {
        return infoCdt;
    }

    public void setInfoCdt(NodeCandidat infoCdt) {
        this.infoCdt = infoCdt;
    }

    public CurrentUser getTheUser() {
        return theUser;
    }

    public void setTheUser(CurrentUser theUser) {
        this.theUser = theUser;
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }

    public NodeAutoCompletion getSelectedNvx() {
        return selectedNvx;
    }

    public void setSelectedNvx(NodeAutoCompletion selectedNvx) {
        this.selectedNvx = selectedNvx;
    }

    public String getLangueTheso() {
        return langueTheso;
    }

    public void setLangueTheso(String langueTheso) {
        this.langueTheso = langueTheso;
    }

    public String getMsgValid() {
        return msgValid;
    }

    public void setMsgValid(String msgValid) {
        this.msgValid = msgValid;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public ArrayList<String> getTradInsert() {
        return tradInsert;
    }

    public void setTradInsert(ArrayList<String> tradInsert) {
        this.tradInsert = tradInsert;
    }

    public NodeMessageAdmin getMsgAdm() {
        return msgAdm;
    }

    public void setMsgAdm(NodeMessageAdmin msgAdm) {
        this.msgAdm = msgAdm;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public Date getCreatedProposition() {
        return createdProposition;
    }

    public void setCreatedProposition(Date createdProposition) {
        this.createdProposition = createdProposition;
    }

    public Date getModifiedProposition() {
        return modifiedProposition;
    }

    public void setModifiedProposition(Date modifiedProposition) {
        this.modifiedProposition = modifiedProposition;
    }

}
