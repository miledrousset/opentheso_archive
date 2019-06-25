package mom.trd.opentheso.SelectedBeans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;

@ManagedBean(name = "copyAndPaste", eager = true)
@SessionScoped

public class CopyAndPaste {
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean languageBean;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    
    /**
     * Cette classe permet de sauvegarder les actions de copier/coller dans
     * une instance Opentheso avec portée à la cession
     */
    
    private String movedFromId;
    private boolean isBranch = true;
    
    // pour distinguer un group d'un concept 
    private boolean isCopyOfGroup = false;
    
    private String conceptValueToCopy;
    
    private boolean isCopyOn = false;

  
    private MyTreeNode draggedNode;

    public void initInfo() {
        movedFromId = null;
        isBranch = true;
        isCopyOn = false;
        isCopyOfGroup = false;
        draggedNode = null;
    }
    
    /**
     * permet de couper une branche pour la replacer à un autre endroit
     * @param copiedNodeTemp
     * @param name 
     */
    public void copy(MyTreeNode copiedNodeTemp, String name) {
        if(copiedNodeTemp == null) return;
        conceptValueToCopy = name;
        isCopyOn = true;
        draggedNode = copiedNodeTemp;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    languageBean.getMsg("tools.copyBranch"), conceptValueToCopy + " (" + draggedNode.getIdConcept() + ")"));

    }

    public LanguageBean getLanguageBean() {
        return languageBean;
    }

    public void setLanguageBean(LanguageBean languageBean) {
        this.languageBean = languageBean;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getMovedFromId() {
        return movedFromId;
    }

    public void setMovedFromId(String movedFromId) {
        this.movedFromId = movedFromId;
    }

    public boolean isIsBranch() {
        return isBranch;
    }

    public void setIsBranch(boolean isBranch) {
        this.isBranch = isBranch;
    }

    public boolean isIsCopyOfGroup() {
        return isCopyOfGroup;
    }

    public void setIsCopyOfGroup(boolean isCopyOfGroup) {
        this.isCopyOfGroup = isCopyOfGroup;
    }

    public String getConceptValueToCopy() {
        return conceptValueToCopy;
    }

    public void setConceptValueToCopy(String conceptValueToCopy) {
        this.conceptValueToCopy = conceptValueToCopy;
    }

    public boolean isIsCopyOn() {
        return isCopyOn;
    }

    public void setIsCopyOn(boolean isCopyOn) {
        this.isCopyOn = isCopyOn;
    }

    public MyTreeNode getDraggedNode() {
        return draggedNode;
    }

    public void setDraggedNode(MyTreeNode draggedNode) {
        this.draggedNode = draggedNode;
    }
    
}
