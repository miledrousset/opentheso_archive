package mom.trd.opentheso.bdd.helper.nodes.group;

import java.util.ArrayList;

public class NodeGroupLabel {

    private String idGroup;
    private String idArk;
    private String idThesaurus;
    
    private ArrayList<NodeGroupTraductions> nodeGroupTraductionses = new ArrayList<>();

    public NodeGroupLabel() {
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getIdArk() {
        return idArk;
    }

    public void setIdArk(String idArk) {
        this.idArk = idArk;
    }

    public String getIdThesaurus() {
        return idThesaurus;
    }

    public void setIdThesaurus(String idThesaurus) {
        this.idThesaurus = idThesaurus;
    }

    public ArrayList<NodeGroupTraductions> getNodeGroupTraductionses() {
        return nodeGroupTraductionses;
    }

    public void setNodeGroupTraductionses(ArrayList<NodeGroupTraductions> nodeGroupTraductionses) {
        this.nodeGroupTraductionses = nodeGroupTraductionses;
    }

           

}
