/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author jm.prudham
 */

@ManagedBean(name = "editorView", eager = true)
@SessionScoped
public class EditorView {
   @ManagedProperty(value="#{copyrightBean}")
   private CopyrightBean copyrightBe;
   
   /**
    * Getter du manageNBean copyrightBean
    * @return 
    */

    public CopyrightBean getCopyrightBe() {
        return copyrightBe;
    }
    /**
     * Setter du managed bean copyrightBean
     * @param copyrightBe 
     */
    public void setCopyrightBe(CopyrightBean copyrightBe) {
        this.copyrightBe = copyrightBe;
    }
   
    private String text;
     

    /**
     * funciton getText()
     * 
     * Appelle sur le bean copyright pour obtenr la valeur copyright
     * @return
     * 
     */
    public String getText(){
        this.text=getCopyrightBe().getCopyright();
        return this.text;
    }
    
    /**
     * function setText
     * 
     * Setter qui appelle le bean copyrightBean pour mettre la valeur du copyright
     * @param text 
     */
    public void setText(String text) {
        
        this.text = text;
        this.copyrightBe.setCopyright(text);
        
    }
 
 
    /**
     * Creates a new instance of EditorView
     */
    public EditorView() {
    }
    
}
