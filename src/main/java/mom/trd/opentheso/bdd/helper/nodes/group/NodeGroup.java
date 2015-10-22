package mom.trd.opentheso.bdd.helper.nodes.group;

import java.io.Serializable;
import java.sql.Date;
import mom.trd.opentheso.bdd.datas.ConceptGroup;

public class NodeGroup implements Serializable, Comparable {
    
    private static final long serialVersionUID = 1L;

    private ConceptGroup conceptGroup;
    private String lexicalValue;
    private String idLang;
    private Date created;
    private Date modified;
    private String idUser;

    public NodeGroup() {
        conceptGroup = new ConceptGroup();
    }

    public ConceptGroup getConceptGroup() {
        return conceptGroup;
    }

    public void setConceptGroup(ConceptGroup conceptGroup) {
        this.conceptGroup = conceptGroup;
    }

    public String getLexicalValue() {
        return lexicalValue;
    }

    public void setLexicalValue(String lexicalValue) {
        this.lexicalValue = lexicalValue;
    }

    public String getIdLang() {
        return idLang;
    }

    public void setIdLang(String idLang) {
        this.idLang = idLang;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public int compareTo(Object o) {
        return this.lexicalValue.compareTo(((NodeGroup)o).lexicalValue);
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }


}
