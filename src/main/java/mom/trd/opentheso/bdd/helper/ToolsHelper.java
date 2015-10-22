package mom.trd.opentheso.bdd.helper;

import com.zaxxer.hikari.HikariDataSource;
import fr.mom.arkeo.soap.DcElement;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.bdd.tools.FileUtilities;
import mom.trd.opentheso.ws.ark.Ark_Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ToolsHelper {

    private final Log log = LogFactory.getLog(ThesaurusHelper.class);

    public ToolsHelper() {

    }


    /**
     * Cette fonction permet de regénerer ou générer tous les identifiants Ark 
     * du thésaurus 
     *
     * @param ds
     * @param idThesaurus
     * @return ArrayList de NodePermute
     */
    public boolean GenerateArkIds(HikariDataSource ds,
            String idThesaurus) {

        ConceptHelper conceptHelper = new ConceptHelper();
        TermHelper termHelper = new TermHelper();
        ArrayList<NodeTermTraduction> nodeTermTraductionList;

        // Génération des Id Ark pour les concepts
        ArrayList<String> tabIdConcept = conceptHelper.getAllIdConceptOfThesaurus(ds, idThesaurus);

        String idArk;
        Ark_Client ark_Client = new Ark_Client();
        ArrayList<DcElement> dcElementsList = new ArrayList<>();
        
        for (String idConcept : tabIdConcept) {
         

            dcElementsList.clear();
            nodeTermTraductionList = termHelper.getAllTraductionsOfConcept(ds, idConcept, idThesaurus);
            for (NodeTermTraduction nodeTermTraduction : nodeTermTraductionList) {
                DcElement dcElement = new DcElement();
                // cette fonction permet de remplir la table Permutée
                dcElement.setName("description");
                dcElement.setValue(nodeTermTraduction.getLexicalValue());
                dcElement.setLanguage(nodeTermTraduction.getLang());
                dcElementsList.add(dcElement);
            }
            // String date, String url, String title, String creator, String description, String type
            idArk = ark_Client.getArkId(
                                new FileUtilities().getDate(),
                                "http://pactols.frantiq.fr/" + "?idc=" + idConcept + "&idt="+idThesaurus,
                                idConcept,
                                "Frantiq",
                                dcElementsList,
                                "pcrt"); // pcrt : p= pactols, crt=code DCMI pour collection
            conceptHelper.updateArkIdOfConcept(ds, idConcept, idThesaurus, idArk);
        }

        return true;
    }
}
