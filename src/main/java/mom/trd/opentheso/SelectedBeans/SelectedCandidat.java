package mom.trd.opentheso.SelectedBeans;

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
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.CandidateHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.ForgetPasswordHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.UserHelper2;
import mom.trd.opentheso.bdd.helper.nodes.NodeAutoCompletion;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeCandidat;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeCandidatValue;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeMessageAdmin;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeProposition;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeTraductionCandidat;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import org.primefaces.event.TabChangeEvent;

@ManagedBean(name = "selectedCandidat", eager = true)
@SessionScoped
public class SelectedCandidat implements Serializable {

    private static final long serialVersionUID = 1L;
    private NodeCandidatValue selected;
    private NodeCandidat infoCdt;
    private String langueEdit = "";
    private String valueEdit = "";
    private String noteEdit = "";
    private String niveauEdit = "";
    private String domaineEdit = "";
    private ArrayList<String> tradInsert;
    private NodeAutoCompletion selectedNvx;
    private String msgValid;
    private String email;
    private String newPass;
    private String confirmPass;
    private String ancianPass;

    private String note = "";
    private String niveau = "";
    private String domaine = "";
    private NodeMessageAdmin msgAdm;

    private String idTheso = "";
    private String langueTheso;

    private Date createdProposition;
    private Date modifiedProposition;

    private String statusCandidat;

    private ArrayList<String> nomsProp;

    // Variables resourcesBundle
//    private boolean arkActive;
//    private String serverAdress;
//    private String identifierType;
    private NodePreference nodePreference;
          
    
    


    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{currentUser}") 
    private CurrentUser2 currentUser;
    
    @ManagedProperty(value = "#{roleOnTheso}")
    private RoleOnThesoBean roleOnThesoBean;    

    /**
     * ************************** INITIALISATION ****************************
     */
    public SelectedCandidat() {
        setPreferences();
    }

    /**
     * Récupération des préférences
     *
     * @return la ressourceBundle des préférences
     */
  /*  private ResourceBundle getBundlePref() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundlePref = context.getApplication().getResourceBundle(context, "pref");
        return bundlePref;
    }*/

    @PostConstruct
    public void initSelectedCandidat() {
        selected = new NodeCandidatValue();
        selected.setEtat("");
        selected.setIdConcept("");
        selected.setValue("");
        noteEdit ="";
        selected.setNbProp(0);
        infoCdt = new NodeCandidat();
        infoCdt.setNodesUser(new ArrayList<>());
        infoCdt.setNodeTraductions(new ArrayList<>());
        selectedNvx = new NodeAutoCompletion();
        tradInsert = new ArrayList<>();
        setPreferences();
        nomsProp = new ArrayList<>();
    }

    /**
     * Réinitialise le candidat (tout est vide)
     */
    public void reInit() {
        selected = new NodeCandidatValue();
        selected.setEtat("");
        selected.setIdConcept("");
        selected.setValue("");
        selected.setNbProp(0);
        infoCdt = new NodeCandidat();
        infoCdt.setNodesUser(new ArrayList<>());
        infoCdt.setNodeTraductions(new ArrayList<>());
        selectedNvx = new NodeAutoCompletion();
        note = "";
        niveau = "";
        domaine = "";
        nomsProp = new ArrayList<>();
    }
    
    private void setPreferences(){
        if (roleOnThesoBean == null || currentUser == null) {
            return;
        }
        nodePreference = roleOnThesoBean.getNodePreference();
    }

    /**
     * permet de retourner le total des candidats d'un thésaurus 
     * @return 
     */
    public int totalOfCandidat(){
        CandidateHelper candidateHelper = new CandidateHelper();
        return candidateHelper.getCountOfCandidat(connect.getPoolConnexion(), idTheso);
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
                modifiedProposition = np.getModified();
                if(!np.getIdConceptParent().isEmpty()) {
                    niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langueTheso) + " (" + np.getIdConceptParent() + ")";
                    showGroup(np);
           //         domaine = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), np.getIdGroup(), idTheso, langueTheso) + " (" + np.getIdGroup() + ")";
                }
                break;
            }
        }
    }
    
    /**
     * permet de récupérer les Groups du concept sur lequel il faut accrocher le candidat
     * @param np 
     */
    private void showGroup(NodeProposition np) {
        boolean first = true;        
        ArrayList<NodeGroup> nodeGroups = new GroupHelper().getListGroupOfConcept(connect.getPoolConnexion(), idTheso, np.getIdConceptParent(), langueTheso);
        for (NodeGroup nodeGroup : nodeGroups) {
            if(first) {
                domaine = nodeGroup.getLexicalValue();
                first = false;
            } else {
                domaine = domaine + "; " + nodeGroup.getLexicalValue();
            }
        }         
    }

    /**
     * $$$$$$$$$ fonction à revoir, trop de connection, à optimiser $$$$$$$$$
     */
    /**
     * Récupération des informations d'un candidat lors de sa sélection dans la
     * table des candidats
     *
     * @param theso
     * @param langue
     */
    public void maj(String theso, String langue) {
        // if(theso == null) return;
        idTheso = theso;
        langueTheso = langue;
        domaine = "";

        infoCdt.setNodesUser(new CandidateHelper().getListUsersOfCandidat(connect.getPoolConnexion(), selected.getIdConcept(), theso));
        infoCdt.setNodeTraductions(new CandidateHelper().getNodeTraductionCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, langue));
        nomsProp = new ArrayList<>();
        if(infoCdt.getNodesUser().isEmpty())
            reInit();
        for (NodeUser nuse : infoCdt.getNodesUser()) {
            nomsProp.add(nuse.getName());
        }
        if (selected.getIdConcept() != null && !selected.getIdConcept().isEmpty() && !infoCdt.getNodesUser().isEmpty()) {
            NodeProposition np = new CandidateHelper().getNodePropositionOfUser(connect.getPoolConnexion(), selected.getIdConcept(), theso, infoCdt.getNodesUser().get(0).getId());
            if (np == null) { // erreur
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error-BDD")));
                return;
            }
            note = np.getNote();
            niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langue) + " (" + np.getIdConceptParent() + ")";
            if(!np.getIdConceptParent().isEmpty()) {
                niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langueTheso) + " (" + np.getIdConceptParent() + ")";
                showGroup(np);
       //         domaine = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), np.getIdGroup(), idTheso, langueTheso) + " (" + np.getIdGroup() + ")";
            }
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
     * @return
     */
    public boolean newCandidat(String theso, String langue) {
        if (selectedNvx != null) {
            niveauEdit = selectedNvx.getIdConcept();
        } else {
            niveauEdit = "";
        }

        if (domaineEdit == null) {
            domaineEdit = "";
        }

        Connection conn;
        try {
            conn = connect.getPoolConnexion().getConnection();
            String id_candidat = new CandidateHelper().addCandidat_rollBack(conn, valueEdit, langue,
                    theso, currentUser.getUser().getIdUser(), noteEdit, niveauEdit, domaineEdit);
            if (id_candidat == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error-BDD")));
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error-BDD")));
            return false;
        }
        initNewProposal();
        return true;
    }
    
    public void initNewProposal() {
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
            if(selectedNvx != null) {
                if(!selectedNvx.getIdConcept().isEmpty())
                    if(!setLevelInfos(selectedNvx.getIdConcept(), langue)) return false;
            } else {
                niveauEdit = "";
            }  
            
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
                    selected.getIdConcept(), currentUser.getUser().getIdUser(),
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
            if(!np.getIdConceptParent().isEmpty()) {
                niveau = new ConceptHelper().getLexicalValueOfConcept(connect.getPoolConnexion(), np.getIdConceptParent(), idTheso, langueTheso) + " (" + np.getIdConceptParent() + ")";
                showGroup(np);
       //         domaine = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), np.getIdGroup(), idTheso, langueTheso) + " (" + np.getIdGroup() + ")";
            }
            selected.setNbProp(selected.getNbProp() + 1);

            // envoie d'email d'alerte !!
          /*  int minAlert = new PreferencesHelper().getThesaurusPreference(connect.getPoolConnexion(), idTheso).getNbAlertCdt();
            if (selected.getNbProp() >= minAlert) {
                ArrayList<String> lesMails = new UserHelper().getMailAdmin(connect.getPoolConnexion(), idTheso);
                for (String mail : lesMails) {
                    if (mail != null && !mail.trim().equals("")) {
                        String message =  "Le candidat " + selected.getValue() + " a atteint le seuil d'alerte de " + minAlert + " fois.";
                        envoyerMailAlert(mail, message);
                    }
                }
            }*/
            initNewProposal();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * permet d'initialiser les Groupes pour le niveau de concept sélectionné dans le thésauurus 
     * @param idConcept
     * @param idLang
     * @return 
     */
    private boolean setLevelInfos(String idConcept, String idLang){
        GroupHelper groupHelper = new GroupHelper();
        ArrayList<NodeGroup> nodeGroups = groupHelper.getListGroupOfConcept(connect.getPoolConnexion(),
                idTheso, idConcept, idLang);
        if(nodeGroups == null) return false;
        String tmp = "";
        boolean first = true;
        for (NodeGroup nodeGroup : nodeGroups) {
            if(!first)
                tmp = tmp + "; ";
            tmp = tmp + nodeGroup.getLexicalValue();
            first = false;
        }
        selectedNvx.setGroupLexicalValue(tmp);
        return true;
    }

    //////////////////////////////////////////////////////////////////
    ///// regroupement des alertes mails sur une seule fonction //////
    //////////////////////////////////////////////////////////////////
    //#MR
    /**
     * Un mail est envoyé à l'adresse mail passée en paramètre pour signaler
     * un évènmenet en paramètre
     *
     * Deprecated
     * 
     * @param dest
     * @param message
     * @return
     */
    /*
    public boolean envoyerMailAlert(String dest, String message) {
        boolean status = false;
        try {
            boolean alert = new PreferencesHelper().getThesaurusPreference(connect.getPoolConnexion(), idTheso).isAlertCdt();
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
                msg.setText(message);

                SMTPTransport transport = (SMTPTransport) session.getTransport(bundlePref.getString("transportMail"));
                transport.connect();
                transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
                transport.close();
                status = true;
            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SelectedCandidat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }    */


    /**
     * permet d'ajouter les tradcutions aux candidats
     * @param idT
     * @param langue
     * @return 
     */
    public boolean newTradCdt(String idT, String langue) {
        if (!new CandidateHelper().addTermCandidatTraduction(connect.getPoolConnexion(), 
                selected.getIdConcept(), valueEdit, langueEdit.trim(), idT, currentUser.getUser().getIdUser())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("Error-BDD")));
            return false;
        }
        infoCdt.setNodeTraductions(new CandidateHelper().getNodeTraductionCandidat(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, langue));
        return true;
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
            new CandidateHelper().deletePropositionCandidat(connect.getPoolConnexion(),
                    selected.getIdConcept(), currentUser.getUser().getIdUser(), idTheso);
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
        new CandidateHelper().deleteTraductionTermCandidat(connect.getPoolConnexion(),
                selected.getIdConcept(), langue, idTheso, currentUser.getUser().getIdUser());
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
     * aux contributeurs si l'option d'envoi de mail est activée
     */
    public void toValid() {
        CandidateHelper ch = new CandidateHelper();
        ch.updateCandidatStatus(connect.getPoolConnexion(), "v", idTheso, selected.getIdConcept());
        ch.addAdminMessage(connect.getPoolConnexion(), selected.getIdConcept(), idTheso, 
                currentUser.getUser().getIdUser(), msgValid);
    //    msgValid = "";
        selected.setEtat("v");

        
    //    ArrayList<String> lesMails = new UserHelper().getMailAdmin(connect.getPoolConnexion(), idTheso);
//        ArrayList<String> contribs = new UserHelper().getMailContributor(connect.getPoolConnexion(), selected.getIdConcept(), idTheso);

        // récupération des contributeurs du candidat
        ArrayList<NodeUser> contribs = ch.getListUsersOfCandidat(connect.getPoolConnexion(),
            selected.getIdConcept(), idTheso);
        
    /*    for (NodeUser nodeUser : contribs) {
            if (!lesMails.contains(contrib)) {
                lesMails.add(contrib);
            }
        }*/
        /*
        for (NodeUser nodeUser : contribs) {
            if (nodeUser.getMail() != null &&  !nodeUser.getMail().trim().equals("")) {
                String message =  "Votre candidat " + selected.getValue() + " a été validé par le(a) terminologue : " + theUser.getUser().getName() + ".";
                message = message + "\n avec le message suivant : " + msgValid + ".";
                envoyerMailAlert(nodeUser.getMail(), message);
            }
        }*/
        msgValid = "";
        vue.setAddValidCdt(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sCdt.info4")));
    }

    /**
     * Passe un candidat de son état courant à l'état refusé. Un mail est envoyé
     * aux contributeurs si l'option d'envoi de mail est activée
     */
    public void toRefus() {
        CandidateHelper ch = new CandidateHelper();
        ch.updateCandidatStatus(connect.getPoolConnexion(), "r", idTheso, selected.getIdConcept());
        ch.addAdminMessage(connect.getPoolConnexion(), selected.getIdConcept(), idTheso,
                currentUser.getUser().getIdUser(), msgValid);
        // gestion message

        selected.setEtat("r");

        // envoie d'email d'alerte !!
        ArrayList<NodeUser> contribs = ch.getListUsersOfCandidat(connect.getPoolConnexion(),
            selected.getIdConcept(), idTheso);
        /*
        for (NodeUser nodeUser : contribs) {
            if (nodeUser.getMail() != null && !nodeUser.getMail().trim().equals("")) {
                String message = "Le candidat " + selected.getValue() + " a été refusé par le(a) terminologue : " + theUser.getUser().getName() + ".";
                message = message + "\n avec le message suivant : " + msgValid + ".";
                envoyerMailAlert(nodeUser.getMail(), message);
            }

        }*/
        msgValid = "";
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

        setPreferences();
        if (selectedNvx == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sCdt.error4")));
            return false;
        }
        niveauEdit = selectedNvx.getIdConcept();
        /*else {
            niveauEdit = null;
        }*/
        
        
        
     /*   if (domaineEdit == null || domaineEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sCdt.error1")));
            return false;
        }*/

        GroupHelper groupHelper = new GroupHelper();
        ArrayList<String> idGroups = groupHelper.getListIdGroupOfConcept(connect.getPoolConnexion(), idTheso, niveauEdit);
     
        if (tradInsert.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("sCdt.error2")));
            return false;
        }
        if (new TermHelper().isTermExist(connect.getPoolConnexion(),
                selected.getValue().trim(), idTheso, langueTheso)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("tree.error2")));
            return false;
        }
        
        if (idGroups.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "No Group Error !!"));
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

        instance.setNodePreference(nodePreference);
        Concept concept = new Concept();
        concept.setIdGroup(idGroups.get(0));
        concept.setIdThesaurus(idTheso);
        concept.setStatus("D");
        concept.setNotation("");

        String langueTemp = temp.get(0).getKey();

        Term terme = new Term();
        terme.setId_thesaurus(idTheso);
        terme.setLang(temp.get(0).getKey());
        terme.setLexical_value(temp.get(0).getValue());
        temp.remove(0);

        String idc;

    //    if (niveauEdit == null || niveauEdit.trim().equals("")) { // Top concept
     //       idc = instance.addTopConcept(connect.getPoolConnexion(), idTheso, concept, terme, serverAdress, arkActive, theUser.getUser().getId());
      //  } else { // concept
            idc = instance.addConcept(connect.getPoolConnexion(), niveauEdit, "NT", concept,
                    terme, currentUser.getUser().getIdUser());
       // }
        // permet d'ajouter les domaines au nouveau concept
        for (String idGroup : idGroups) {
            groupHelper.addConceptGroupConcept(connect.getPoolConnexion(), idGroup, niveauEdit, idTheso);
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
                new TermHelper().addTraduction(connect.getPoolConnexion(),
                        terme, currentUser.getUser().getIdUser());
            }
        }

        CandidateHelper ch = new CandidateHelper();
        ch.updateCandidatStatus(connect.getPoolConnexion(), "i", idTheso, selected.getIdConcept());
        ch.addAdminMessage(connect.getPoolConnexion(), selected.getIdConcept(),
                idTheso, currentUser.getUser().getIdUser(), msgValid);
        
        
        // message d'alerte 
    /*    ArrayList<NodeUser> contribs = ch.getListUsersOfCandidat(connect.getPoolConnexion(),
            selected.getIdConcept(), idTheso);*/
        
    /*    for (NodeUser nodeUser : contribs) {
            if (!lesMails.contains(contrib)) {
                lesMails.add(contrib);
            }
        }*/
    
        /*
        for (NodeUser nodeUser : contribs) {
            if (nodeUser.getMail() != null &&  !nodeUser.getMail().trim().equals("")) {
                String message =  "Votre candidat " + selected.getValue() + " a été intégré au thésaurus par le(a) terminologue : " + theUser.getUser().getName() + ".";
                envoyerMailAlert(nodeUser.getMail(), message);
            }
        } */       
        
        
        
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
        NodeProposition np = new CandidateHelper().getNodePropositionOfUser(connect.getPoolConnexion(),
                selected.getIdConcept(), idTheso, currentUser.getUser().getIdUser());
        noteEdit = np.getNote();
        niveauEdit = niveau;//np.getIdConceptParent();
        domaineEdit =domaine;
        selectedNvx.setIdConcept(np.getIdConceptParent());
        selectedNvx.setDefinition(noteEdit);
        selectedNvx.setPrefLabel(niveau);
        
        vue.setEditPropCandidat(true);
    }

    public boolean setStatusToInsert() {
        CandidateHelper candidateHelper = new CandidateHelper();
        if (candidateHelper.setStatusCandidatToInserted(connect.getPoolConnexion(), selected.getIdConcept(),
                idTheso, currentUser.getUser().getIdUser())) {
            return true;
        } else {
            return false;
        }
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

        new CandidateHelper().updatePropositionCandidat(connect.getPoolConnexion(), selected.getIdConcept(),
                currentUser.getUser().getIdUser(), idTheso, noteEdit, niveauEdit, domaineEdit);
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

    public boolean needchangerpass() throws SQLException {
        UserHelper2 user = new UserHelper2();
        if (user.isneededpass(connect.getPoolConnexion(), currentUser.getUser().getIdUser())) {
            return true;
        }
        return false;
    }

    /**
     * Applelation de la funtion avec les parametres pour changer le mot de
     * pass. on fait le comprobation pour voir si tout c'est bon
     *
     * @return
     * @throws SQLException
     */
    public String fchangepass() throws SQLException {
        boolean sort = false;
        ForgetPasswordHelper forgetPassword = new ForgetPasswordHelper();

        if (newPass == null ? confirmPass != null : !newPass.equals(confirmPass)) {
            sort = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error3")));
        }
        if (newPass == null || newPass.trim().equals("") || confirmPass == null || confirmPass.trim().equals("")) {
            sort = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("user.error2")));
        } else if (!sort) {
            if (forgetPassword.faireChangePass(connect.getPoolConnexion(), newPass, confirmPass,
                    currentUser.getUser().getIdUser())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("user.info1")));
                return "index.xhtml?faces-redirect=true";

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            }
        }
        return "";// nouvelle pass web pour changer le motpasstemp
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
            if (nu.getId() == currentUser.getUser().getIdUser()) {
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
    public NodeCandidatValue getSelected() {
        return selected;
    }

    public void setSelected(NodeCandidatValue selected) {
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

    public String getStatusCandidat() {
        return statusCandidat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean setStatusCandidat(String idCandidat) {
        CandidateHelper candidateHelper = new CandidateHelper();
        if (!candidateHelper.setStatusCandidatToInserted(
                connect.getPoolConnexion(),
                idCandidat,
                idTheso,
                currentUser.getUser().getIdUser())) {
            return false;
        } else {
            selected.setEtat("i");
        }
        return true;
    }


    public CurrentUser2 getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser2 currentUser) {
        this.currentUser = currentUser;
    }

    public RoleOnThesoBean getRoleOnThesoBean() {
        return roleOnThesoBean;
    }

    public void setRoleOnThesoBean(RoleOnThesoBean roleOnThesoBean) {
        this.roleOnThesoBean = roleOnThesoBean;
    }

    public NodePreference getNodePreference() {
        return nodePreference;
    }

    public void setNodePreference(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

}
