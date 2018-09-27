package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;

@ManagedBean(name = "note", eager = true)
@SessionScoped

public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    @ManagedProperty(value = "#{currentUser}")
    private CurrentUser2 user;    
    
    
    private String note;
    private String definition;
    private String noteApplication;
    private String noteHistorique;
    private String noteEditoriale;

    private ArrayList<NodeNote> nodeNoteTermList;
    private ArrayList<NodeNote> nodeNoteConceptList;    
    private boolean allLangue = false;
    
    public String icon = "+";    
        
    @PostConstruct
    public void initConf() {
    }
    
    private void init() {
        noteEditoriale = "";
        definition = "";
        noteHistorique = "";
        noteApplication = "";
        note = "";
    }

    private void initNotes(String idC, String idT, String idTheso, String idLang) {
        // NodeNote contient la note avec le type de note, il faut filtrer pour trouver la bonne note
        // For Concept : customnote ; scopeNote ; historyNote
        // For Term : definition; editorialNote; historyNote; 
        init();
        nodeNoteTermList = new NoteHelper().getListNotesTerm(connect.getPoolConnexion(), idT, idTheso, idLang);
        for (NodeNote nodeNoteList1 : nodeNoteTermList) {
            if (nodeNoteList1 != null) {
                // cas d'une noteEditoriale
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("editorialNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteEditoriale = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de definitionNote
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("definition")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        definition = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de HistoryNote
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("historyNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteHistorique = nodeNoteList1.getLexicalvalue();
                    }
                }
            }
        }
        nodeNoteConceptList = new NoteHelper().getListNotesConcept(connect.getPoolConnexion(), idC, idTheso, idLang);
        for (NodeNote nodeNoteList1 : nodeNoteConceptList) {
            if (nodeNoteList1 != null) {
                // cas de Note d'application
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("scopeNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteApplication = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de HistoryNote
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("historyNote")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        noteHistorique = nodeNoteList1.getLexicalvalue();
                    }
                }
                // cas de Note
                if (nodeNoteList1.getNotetypecode().equalsIgnoreCase("note")) {
                    if (nodeNoteList1.getLexicalvalue() != null) {
                        note = nodeNoteList1.getLexicalvalue();
                    }
                }
            }
        }
    }
    
        /**
     * Crée ou modifie la définition du terme courant
     */
    public void editDef(String idC, String idT, String idTheso, String idLang) {
        int idUser = user.getUser().getIdUser();
        if (definition.isEmpty()) {
            deleteThisNoteOfConcept("note",idC, idT, idTheso, idLang);
            return;
        }
        if (new NoteHelper().isNoteExistOfTerm(connect.getPoolConnexion(), idT, idTheso, idLang, "definition")) {
            new NoteHelper().updateTermNote(connect.getPoolConnexion(), idT, idLang, idTheso, definition, "definition", idUser);
        } else {
            new NoteHelper().addTermNote(connect.getPoolConnexion(), idT, idLang, idTheso, definition, "definition", idUser);
        }
        majNotes(idC, idT, idTheso, idLang);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info3")));
    }

    /**
     * Cette fontion permet de modifié l'information du note
     *
     */
    public void editNote(String idC, String idT, String idTheso, String idLang) {
        int idUser = user.getUser().getIdUser();
        if (note.isEmpty()) {
            deleteThisNoteOfConcept("note", idC, idT, idTheso, idLang);
            return;
        }
        if (new NoteHelper().isNoteExistOfConcept(connect.getPoolConnexion(), idC, idTheso, idLang, "note")) {
            new NoteHelper().updateConceptNote(connect.getPoolConnexion(), idC, idLang, idTheso, note, "note", idUser);
        } else {
            new NoteHelper().addConceptNote(connect.getPoolConnexion(), idC, idLang, idTheso, note, "note", idUser);
        }
        majNotes(idC, idT, idTheso, idLang);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info12")));
    }
    
        /**
     * Cette fonction permet de supprimer une note suivant son type
     *
     * @param noteTypeCode
     */
    public void deleteThisNoteOfConcept(String noteTypeCode, String idC, String idT, String idTheso, String idLang) {
        int idUser = user.getUser().getIdUser();
        new NoteHelper().deletethisNoteOfConcept(connect.getPoolConnexion(), idC, idTheso, idLang, noteTypeCode);

        majNotes(idC, idT, idTheso, idLang);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info13")));
    }

    /**
     * Cette fonction permet de supprimer une note suivant son type
     *
     * @param noteTypeCode
     */
    public void deleteThisNoteOfTerm(String noteTypeCode, String idC, String idT, String idTheso, String idLang) {
        int idUser =user.getUser().getIdUser();
        new NoteHelper().deleteThisNoteOfTerm(connect.getPoolConnexion(), idT, idTheso, idLang, noteTypeCode);

        majNotes(idC, idT, idTheso, idLang);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info13")));
    }

    public void editNoteApp(String idC, String idT, String idTheso, String idLang) {
        int idUser = user.getUser().getIdUser();
        if (noteApplication.isEmpty()) {
            deleteThisNoteOfConcept("note", idC, idT, idTheso, idLang);
            return;
        }
        if (new NoteHelper().isNoteExistOfConcept(connect.getPoolConnexion(), idC, idTheso, idLang, "scopeNote")) {
            new NoteHelper().updateConceptNote(connect.getPoolConnexion(), idC, idLang, idTheso, noteApplication, "scopeNote", idUser);
        } else {
            new NoteHelper().addConceptNote(connect.getPoolConnexion(), idC, idLang, idTheso, noteApplication, "scopeNote", idUser);
        }
        majNotes(idC, idT, idTheso, idLang);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info12")));
    }

    /**
     * Crée ou modifie la note historique du terme courant
     */
    public void editNoteHisto(String idC, String idT, String idTheso, String idLang) {
        int idUser = user.getUser().getIdUser();
        if (noteHistorique.isEmpty()) {
            deleteThisNoteOfConcept("note", idC, idT, idTheso, idLang);
            return;
        }
        if (new NoteHelper().isNoteExistOfTerm(connect.getPoolConnexion(), idT, idTheso, idLang, "historyNote")) {
            new NoteHelper().updateTermNote(connect.getPoolConnexion(), idT, idLang, idTheso, noteHistorique, "historyNote", idUser);
        } else {
            new NoteHelper().addTermNote(connect.getPoolConnexion(), idT, idLang, idTheso, noteHistorique, "historyNote", idUser);
        }
        majNotes(idC, idT, idTheso, idLang);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info12")));
    }

    /**
     * Crée ou modifie la note éditoriale du terme courant
     */
    public void editNoteEdit(String idC, String idT, String idTheso, String idLang) {

        int idUser = user.getUser().getIdUser();
        if (noteEditoriale.isEmpty()) {
            deleteThisNoteOfConcept("note", idC, idT, idTheso, idLang);
            return;
        }
        if (new NoteHelper().isNoteExistOfTerm(connect.getPoolConnexion(), idT, idTheso, idLang, "editorialNote")) {
            new NoteHelper().updateTermNote(connect.getPoolConnexion(), idT, idLang, idTheso, noteEditoriale, "editorialNote", idUser);
        } else {
            new NoteHelper().addTermNote(connect.getPoolConnexion(), idT, idLang, idTheso, noteEditoriale, "editorialNote", idUser);
        }
        majNotes(idC, idT, idTheso, idLang);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("sTerme.info5")));
    }
    
    
    public void majNotes(String idC, String idT, String idTheso, String idLang) {
        // NodeNote contient la note avec le type de note, il faut filtrer pour trouver la bonne note
        // For Concept : customnote ; scopeNote ; historyNote
        // For Term : definition; editorialNote; historyNote;
        if (allLangue) {
            initNotes(idC, idT, idTheso, idLang);
            nodeNoteTermList = new NoteHelper().getListNotesTerm2(connect.getPoolConnexion(), idT, idTheso);
            nodeNoteConceptList = new NoteHelper().getListNotesConcept2(connect.getPoolConnexion(), idC, idTheso);
        } else {
            initNotes(idC, idT, idTheso, idLang);
        }
    }
    
    /**
     * Permet de voir les nouvelles notes et changer l'icon pour pouvoir voir
     * touts les notes dans les autres langues;
     */
    public void valide(String idC, String idT, String idTheso, String idLang) {
        if (allLangue == true) {
            allLangue = false;
            initNotes(idC, idT, idTheso, idLang);
            icon = "+";
        } else {
            allLangue = true;

            majNotes(idC, idT, idTheso, idLang);
            icon = "-";
        }
    }
    
    
    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getNoteApplication() {
        return noteApplication;
    }

    public void setNoteApplication(String noteApplication) {
        this.noteApplication = noteApplication;
    }

    public String getNoteHistorique() {
        return noteHistorique;
    }

    public void setNoteHistorique(String noteHistorique) {
        this.noteHistorique = noteHistorique;
    }

    public String getNoteEditoriale() {
        return noteEditoriale;
    }

    public void setNoteEditoriale(String noteEditoriale) {
        this.noteEditoriale = noteEditoriale;
    }

    public ArrayList<NodeNote> getNodeNoteTermList() {
        return nodeNoteTermList;
    }

    public void setNodeNoteTermList(ArrayList<NodeNote> nodeNoteTermList) {
        this.nodeNoteTermList = nodeNoteTermList;
    }

    public ArrayList<NodeNote> getNodeNoteConceptList() {
        return nodeNoteConceptList;
    }

    public void setNodeNoteConceptList(ArrayList<NodeNote> nodeNoteConceptList) {
        this.nodeNoteConceptList = nodeNoteConceptList;
    }
    
    public boolean isAllLangue() {
        return allLangue;
    }

    public void setAllLangue(boolean allLangue) {
        this.allLangue = allLangue;
    }
    
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }  

    public CurrentUser2 getUser() {
        return user;
    }

    public void setUser(CurrentUser2 user) {
        this.user = user;
    }
    
    
}
