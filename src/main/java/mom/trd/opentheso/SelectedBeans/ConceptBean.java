/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author miled.rousset
 */

@ManagedBean(name = "conceptbean", eager = true)
@SessionScoped

public class ConceptBean implements Serializable{

    private int deleteBranchOrphan = 0;
    /**
     * Creates a new instance of ConceptBean
     */
    public ConceptBean() {
    }

    public int getDeleteBranchOrphan() {
        return deleteBranchOrphan;
    }

    public void setDeleteBranchOrphan(int deleteBranchOrphan) {
        this.deleteBranchOrphan = deleteBranchOrphan;
    }
    
    
}
