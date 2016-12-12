/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.alignment;

/**
 *
 * @author antonio.perez
 */
public class AlignementSource {
    private String source;
    private String requete;
    private String typeRequete;
    private String alignement_format;
    private  int id;
    private String description;

    public AlignementSource() {
    }
    public void init_alignementSource()
    {
        source="";
        requete="";
        alignement_format="";
        typeRequete="";
        description="";
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
    }

    public String getTypeRequete() {
        return typeRequete;
    }

    public void setTypeRequete(String typeRequete) {
        this.typeRequete = typeRequete;
    }

    public String getAlignement_format() {
        return alignement_format;
    }

    public void setAlignement_format(String alignement_format) {
        this.alignement_format = alignement_format;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
  
}
