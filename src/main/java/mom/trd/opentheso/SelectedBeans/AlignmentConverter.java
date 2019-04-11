package mom.trd.opentheso.SelectedBeans;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;

@FacesConverter("alignmentConverter")
public class AlignmentConverter implements Converter{
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            NodeAlignment nodeAlignment = new NodeAlignment();
            nodeAlignment.setConcept_target(value);
            return nodeAlignment;
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if(o == null) {
            return null;
        } else {
            return ((NodeAlignment)o).getConcept_target();
        }
    }
}
