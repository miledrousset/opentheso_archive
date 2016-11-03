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
    private String formatDonnes;

    public AlignementSource() {
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

    public String getFormatDonnes() {
        return formatDonnes;
    }

    public void setFormatDonnes(String formatDonnes) {
        this.formatDonnes = formatDonnes;
    }
    
    
}
