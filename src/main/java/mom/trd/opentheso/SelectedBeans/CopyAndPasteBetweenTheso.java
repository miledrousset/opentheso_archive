package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.CopyAndPasteHelper;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;

@ManagedBean(name = "copyAndPastebetweenTheso", eager = true)
@SessionScoped

public class CopyAndPasteBetweenTheso {
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean languageBean;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    private static final long serialVersionUID = 1L;
    
    
    
    /**
     * Cette classe permet de sauvegarder les actions de copier / coller dans
     * une instance Opentheso avec portée à la cession
     */
    
    private String fromIdTheso;
    private String movedFromId;
    private boolean isBranch = true;
    
    // pour distinguer un group d'un concept 
    private boolean isCopyOfGroup = false;
    
    private String conceptValueToCopy;
    
    private String identifierType = "sans";
    private boolean isCopyOn = false;




    
    public interface PasteRelation{
        public static final int likeNT = 1;
        public static final int likeBT = 2;
        public static final int likeBrother = 3;
    }
    

    public void initInfo() {
        fromIdTheso = null;
        movedFromId = null;
        isBranch = true;
        isCopyOn = false;
        isCopyOfGroup = false;
    }
    
    public void copy(String idTheso, String idConcept,
            String conceptValue, boolean isBranchT,
            boolean isGroupT) {
        if(idTheso.isEmpty() || idConcept.isEmpty()) return;
        fromIdTheso = idTheso;
        movedFromId = idConcept;
        isBranch = isBranchT;
        conceptValueToCopy = conceptValue;
        isCopyOn = true;
        isCopyOfGroup = isGroupT;
        
        if(isBranchT){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    languageBean.getMsg("tools.copyBranch"), conceptValueToCopy + " (" + idConcept + ")"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    languageBean.getMsg("tools.copyConcept"), conceptValueToCopy + " (" + idConcept + ")"));            
        }
    }
    
    public void paste(String currentIdTheso, String currentIdConcept,
            SelectedTerme selectedTerme, RoleOnThesoBean roleOnThesoBean,
            NewTreeBean tree) {
        CopyAndPasteHelper copyAndPasteHelper = new CopyAndPasteHelper();
        if(tree.isGroup() || tree.isSubGroup()) {
            // traitement pour insertion sous un domaine.
            
        } else {
            if(!copyAndPasteHelper.pasteBranchLikeNT(connect.getPoolConnexion(),
                    currentIdTheso, currentIdConcept,
                    fromIdTheso, movedFromId,
                    selectedTerme, roleOnThesoBean,
                    identifierType)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    languageBean.getMsg("tools.copyBranch"), "Erreur de copie")); 
            }
        }
        
        initInfo();
        tree.reInit();
        tree.reExpand();
        tree.getSelectedTerme().majTerme((MyTreeNode) tree.getSelectedNode());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                languageBean.getMsg("tools.copyBranch"), "Copie réussie"));            
    }
    
    public void pasteGroup(String currentIdTheso,
            RoleOnThesoBean roleOnTheso, NewTreeBean tree,
            SelectedThesaurus selectedThesaurus) {
        CopyAndPasteHelper copyAndPasteHelper = new CopyAndPasteHelper();

        if(!copyAndPasteHelper.pasteGroup(connect.getPoolConnexion(),
                currentIdTheso,
                fromIdTheso, movedFromId, roleOnTheso,
                identifierType)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                languageBean.getMsg("tools.copyBranch"), "Erreur de copie")); 
        }
        
        initInfo();
        tree.reInit();
        tree.initTree(currentIdTheso, selectedThesaurus.getWorkLanguage());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                languageBean.getMsg("tools.copyBranch"), "Copie réussie"));            
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

    public String getConceptValueToCopy() {
        return conceptValueToCopy;
    }

    public void setConceptValueToCopy(String conceptValueToCopy) {
        this.conceptValueToCopy = conceptValueToCopy;
    }

    public String getFromIdTheso() {
        return fromIdTheso;
    }

    public void setFromIdTheso(String fromIdTheso) {
        this.fromIdTheso = fromIdTheso;
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

    public boolean isIsCopyOn() {
        return isCopyOn;
    }

    public void setIsCopyOn(boolean isCopyOn) {
        this.isCopyOn = isCopyOn;
    }

    public boolean isIsCopyOfGroup() {
        return isCopyOfGroup;
    }

    public void setIsCopyOfGroup(boolean isCopyOfGroup) {
        this.isCopyOfGroup = isCopyOfGroup;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }
    
}
