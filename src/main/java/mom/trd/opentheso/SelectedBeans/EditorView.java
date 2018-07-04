/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.CopyrightHelper;

/**
 *
 * @author jm.prudham
 */
@ManagedBean(name = "editorView", eager = true)
@ViewScoped
public class EditorView {

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{theso}")
    private SelectedThesaurus theso;
    private boolean displayBrutText = false;
    private String text;

    /**
     * Creates a new instance of EditorView
     */
    public EditorView() {
    }

    @PostConstruct
    public void initText() {
        if (theso == null || theso.getThesaurus().getId_thesaurus() == null) {
            text = null;
        } else {
            CopyrightHelper copyrightHelper = new CopyrightHelper();
            text = copyrightHelper.getCopyright(connect.getPoolConnexion(),
                    theso.getThesaurus().getId_thesaurus());
        }
    }

    /**
     * permet d'ajouter un copyright, s'il n'existe pas, on le créé,sinon, on applique une mise à jour 
     */
    public void addCopyright() {
        CopyrightHelper copyrightHelper = new CopyrightHelper();
        if (theso == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Pas de thésaurus sélectionné"));
            return;
        }
        if (copyrightHelper.isThesoHaveCopyRight(
                connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus())) {
            if (!copyrightHelper.updateCopyright(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(), text)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Pas de thésaurus sélectionné"));
            }
        } else {
            if (!copyrightHelper.insertCopyright(connect.getPoolConnexion(),
                    theso.getThesaurus().getId_thesaurus(), text)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Pas de thésaurus sélectionné"));
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Copyright mise à jour"));
    }

    /**
     * @return
     */
    public String getText() {

        return this.text;
    }

    /**
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    public boolean isDisplayBrutText() {
        return displayBrutText;
    }

    public void setDisplayBrutText(boolean displayBrutText) {
        this.displayBrutText = displayBrutText;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public SelectedThesaurus getTheso() {
        return theso;
    }

    public void setTheso(SelectedThesaurus theso) {
        this.theso = theso;
    }

}
