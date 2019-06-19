package mom.trd.opentheso.SelectedBeans;

import java.io.InputStream;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@ManagedBean(name = "helpfileDownload", eager = true)
@ViewScoped
public class HelpFileDownload {

    private StreamedContent file;
     
    private String resourcePath;
    private String resourceType;
    
    public HelpFileDownload() {        
/*        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(resourcePath);//"/resources/demo/images/boromir.jpg");
        file = new DefaultStreamedContent(stream, "image/jpg", resourcePath.substring(resourcePath.lastIndexOf("/"), resourcePath.length()));//"downloaded_boromir.jpg");
  */  }
    
    public StreamedContent downloadCSVSample() {
        resourcePath = "/samples/sampleCSV.csv";
    //    this.getClass().getResourceAsStream("/samples/maquetteCSV.csv");
        InputStream stream = this.getClass().getResourceAsStream(resourcePath);//"/resources/demo/images/boromir.jpg");
        file = new DefaultStreamedContent(stream, "txt/CSV", resourcePath.substring(resourcePath.lastIndexOf("/"), resourcePath.length()));//"downloaded_boromir.jpg");
        return file;
    }
    
 
    public StreamedContent getFile() {
        return file;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

}
