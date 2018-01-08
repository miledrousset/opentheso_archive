/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.autorisation;

/**
 *
 * @author jm.prudham
 */
public class AutorisationStruct {
    
    private String typeDroitValue;
    private int typeDroitNum;
    private String thesaurus;

    public String getTypeDroitValue() {
        return typeDroitValue;
    }

    public void setTypeDroitValue(String typeDroitValue) {
        this.typeDroitValue = typeDroitValue;
    }

    public int getTypeDroitNum() {
        return typeDroitNum;
    }

    public void setTypeDroitNum(int typeDroitNum) {
        this.typeDroitNum = typeDroitNum;
    }

    public String getThesaurus() {
        return thesaurus;
    }

    public void setThesaurus(String thesaurus) {
        this.thesaurus = thesaurus;
    }
    
}
