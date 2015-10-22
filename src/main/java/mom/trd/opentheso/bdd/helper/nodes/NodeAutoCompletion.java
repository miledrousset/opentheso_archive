package mom.trd.opentheso.bdd.helper.nodes;

import java.io.Serializable;

public class NodeAutoCompletion implements Serializable {
    
    private String idConcept = "";
    private String termLexicalValue = "";
    private String groupLexicalValue = "";
    private String definition = "";
    private String idGroup = "";
    
    // Url pour l'imagette
    private String url;

    public NodeAutoCompletion() {
    }

    public String getIdConcept() {
        return idConcept;
    }

    public void setIdConcept(String idConcept) {
        this.idConcept = idConcept;
    }

    public String getTermLexicalValue() {
        return termLexicalValue;
    }

    public void setTermLexicalValue(String termLexicalValue) {
        this.termLexicalValue = termLexicalValue;
    }

    public String getGroupLexicalValue() {
        return groupLexicalValue;
    }

    public void setGroupLexicalValue(String GroupLexicalValue) {
        this.groupLexicalValue = GroupLexicalValue;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    @Override
    public String toString() {
        return termLexicalValue;
    }


}
