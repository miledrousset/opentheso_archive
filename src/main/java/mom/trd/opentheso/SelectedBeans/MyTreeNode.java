package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class MyTreeNode extends DefaultTreeNode implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private int typeMot;
    private String idMot;
    private String idTheso;
    private String langue;
    private String idDomaine;
    private String idTopConcept;
    private boolean isGroup =false;
    
    public MyTreeNode(int type, String id, String idT, String l, String idD, String idTC, String icone, Object value, TreeNode parent)
   {
      super(icone, value, parent);
      this.typeMot = type;
      this.idMot = id;
      this.idTheso = idT;
      this.langue = l;
      this.idDomaine = idD;
      this.idTopConcept = idTC;
   }

    public int getTypeMot() {
        return typeMot;
    }

    public void setTypeMot(int typeMot) {
        this.typeMot = typeMot;
    }

    public String getIdMot() {
        return idMot;
    }

    public void setIdMot(String idMot) {
        this.idMot = idMot;
    }

    public String getIdTheso() {
        return idTheso;
    }

    public void setIdTheso(String idTheso) {
        this.idTheso = idTheso;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getIdDomaine() {
        return idDomaine;
    }

    public void setIdDomaine(String idDomaine) {
        this.idDomaine = idDomaine;
    }

    public String getIdTopConcept() {
        return idTopConcept;
    }

    public void setIdTopConcept(String idTopConcept) {
        this.idTopConcept = idTopConcept;
    }    
}
