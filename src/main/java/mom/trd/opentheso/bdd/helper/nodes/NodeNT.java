/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mom.trd.opentheso.bdd.helper.nodes;

import java.text.Normalizer;

/**
 *
 * @author miled.rousset
 */
public class NodeNT implements Comparable {
    private String title;
    private String idConcept;
    private String status;
    
    public NodeNT() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdConcept() {
        return idConcept;
    }

    public void setIdConcept(String idConcept) {
        this.idConcept = idConcept;
    }

    @Override
    public int compareTo(Object o) {
        if(title == null) {
            title = "";
        }
        if(((NodeNT)o).title == null) {
            ((NodeNT)o).title = "";
        }
        String str1, str2;
        str1 = Normalizer.normalize(this.title, Normalizer.Form.NFD);
        str1 = str1.replaceAll("[^\\p{ASCII}]", "");
        str2 = Normalizer.normalize(((NodeNT)o).title, Normalizer.Form.NFD);
        str2 = str2.replaceAll("[^\\p{ASCII}]", "");
        return str1.toUpperCase().compareTo(str2.toUpperCase());
        
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
