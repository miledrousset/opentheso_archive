package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.ExternalImagesHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeImage;

@ManagedBean(name = "externalResources", eager = true)
@SessionScoped

public class ExternalResources implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
  
    private List<String> images;    
    private ArrayList<NodeImage> nodeImages;
    
    // edit
    private String inputUri;
    private String inputLable;
    
    @PostConstruct
    public void init() {
        images = new ArrayList<>();
        nodeImages = new ArrayList<>();
        
    }

    public ExternalResources() {
    }
    
    
    public void loadImages(String idTheso, String idConcept) {
        images.clear();
        ExternalImagesHelper imagesHelper = new ExternalImagesHelper();
        nodeImages = imagesHelper.getExternalImages(connect.getPoolConnexion(), idConcept, idTheso);
        for (NodeImage nodeImage : nodeImages) {
            images.add(nodeImage.getUri());
        }
    }
    
    public void deleteExternalImage(String idTheso, String idConcept, String imageUri){
        ExternalImagesHelper imagesHelper = new ExternalImagesHelper();
        if(!imagesHelper.deleteExternalImage(connect.getPoolConnexion(), idConcept, idTheso, imageUri)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    langueBean.getMsg("error") + " :", langueBean.getMsg("Error-BDD")));
            return;
        }
        loadImages(idTheso, idConcept);        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "", langueBean.getMsg("externalResources.deleteImage")));
    }
    
    public void addExternalImage(String idTheso, String idConcept, int idUser){
        String copyRight = "";
        if(inputUri == null || inputUri.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    langueBean.getMsg("error") + " :", langueBean.getMsg("sTerme.error5")));
            
            return;
        }
        ExternalImagesHelper imagesHelper = new ExternalImagesHelper();
        if(inputLable == null || inputLable.isEmpty()) {
            inputLable = idConcept;
        }
        
        if(!imagesHelper.addExternalImage(connect.getPoolConnexion(), idConcept, idTheso,
                inputLable, copyRight, inputUri, idUser)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    langueBean.getMsg("error") + " :", langueBean.getMsg("Error-BDD")));
            return;
        }
        loadImages(idTheso, idConcept);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "", langueBean.getMsg("externalResources.addedImage")));        
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public ArrayList<NodeImage> getNodeImages() {
        return nodeImages;
    }

    public void setNodeImages(ArrayList<NodeImage> nodeImages) {
        this.nodeImages = nodeImages;
    }

    public void setInputUri(String inputUri) {
        this.inputUri = inputUri;
    }

    public void setInputLable(String inputLable) {
        this.inputLable = inputLable;
    }

    public String getInputUri() {
        return inputUri;
    }

    public String getInputLable() {
        return inputLable;
    }
    
    
    
}
