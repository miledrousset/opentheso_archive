package mom.trd.opentheso.core.exports.old;

import java.util.ArrayList;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeUri;
import mom.trd.opentheso.bdd.helper.nodes.concept.NodeConceptExport;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroupLabel;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.thesaurus.NodeThesaurus;
import skos.SKOSProperty;
import skos.SKOSResource;
import skos.SKOSTopConcept;
import skos.SKOSMapping;

public class WriteFileSKOS_Frantiq {

    private int compteur = 0;
    private String skos;
    private StringBuffer skosBuff;

    private String URI = "";

    private String serverArk;
    private String serverAdress;
    
    public WriteFileSKOS_Frantiq() {
        skosBuff = new StringBuffer();
    }

    public void setURI(String uri) {
        this.URI = uri;
    }

    public void setServerArk(String serverArk) {
        this.serverArk = serverArk;
    }

    public void setServerAdress(String serverAdress) {
        this.serverAdress = serverAdress;
    }

    public boolean writeHeader() {
        String xml;
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml += "<rdf:RDF\n";
        xml += "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n";
        xml += "    xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"\n";
        xml += "    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n";
        xml += "    xmlns:dcterms=\"http://purl.org/dc/terms/\">\n";

        skosBuff.append(xml);
        return true;

    }

    public boolean writeThesaurus(NodeThesaurus nodeThesaurus) {
        //Ecriture de la balise de declaration du thesaurus

        String xml = "    <skos:ConceptScheme rdf:about=\"" + URI + "idt=" +nodeThesaurus.getIdThesaurus() + "\">\n";
        
        // balises DublinCore pour le libellé du thésaurus
        for (Thesaurus listThesaurusTraduction : nodeThesaurus.getListThesaurusTraduction()) {
            if(!listThesaurusTraduction.getTitle().trim().isEmpty()) {
                xml += "        <dc:title xml:lang=";
                xml +=  "\"" + listThesaurusTraduction.getLanguage() + "\">";
                xml += listThesaurusTraduction.getTitle();
                xml += "</dc:title>";
                xml += "\n";
            }
        }

        skosBuff.append(xml);
        return true;
    }

    boolean writeTopConcepts(String idGroup) {
        SKOSTopConcept topConcept = new SKOSTopConcept(URI + "concept#" + idGroup);
        //skos = skos + topConcept.toString();
        skosBuff.append(topConcept.toString());
        
 //       System.out.println("écriture du TopConcept : " + idGroup);

        return true;
    }

    boolean writeEndOfMicroThesaurusList() {
        //ecriture de la fermeture de balise de déclaration du thésaurus, après ecriture des microthésaurus
        String xml = "    </skos:ConceptScheme>\n";
        //skos = skos + xml;
        skosBuff.append(xml);
        return true;
    }

    /**
     *
     * @param codeLangue
     * @return le code iso 639_1 de la langue
     */
    public String GetCodeLangueXML(String codeLangue) {

        /*	String xmlLang = "";
		
         if(codeLangue.equals("fre")){
         xmlLang = "fr";
         }
         else if(codeLangue.equals("eng")){
         xmlLang = "en";
         }
         else if(codeLangue.equals("spa")){
         xmlLang = "es";
         }
         else if(codeLangue.equals("ita")){
         xmlLang = "it";
         }
         else if(codeLangue.equals("ger")){
         xmlLang = "de";
         }
		
         return xmlLang; */
        return codeLangue;
    }

    /**
     *
     * @param nodeGroupLabel
     * @param listIdOfTopConcept
     *
     * @return true ou false
     */
    public boolean writeGroup(NodeGroupLabel nodeGroupLabel, ArrayList<NodeUri> listIdOfTopConcept) {
        
        SKOSResource concept;
    //    if(nodeGroupLabel.getIdArk() == null || nodeGroupLabel.getIdArk().trim().isEmpty()) {
            /*
            Cette partie est pour l'export des PACTOLS pour Frantiq
            */
            concept = new SKOSResource(serverAdress + "concept#" + nodeGroupLabel.getIdGroup());
            
         /*   concept = new SKOSResource(serverAdress +
                    "?idd=" + nodeGroupLabel.getIdGroup() +
                    "&amp;idt=" + nodeGroupLabel.getIdThesaurus());
            
        }
        else {
            concept = new SKOSResource(serverArk + nodeGroupLabel.getIdArk());
        }
            */

        for (int i = 0; i < nodeGroupLabel.getNodeGroupTraductionses().size(); i++) {
            concept.addLabel(
                    nodeGroupLabel.getNodeGroupTraductionses().get(i).getTitle(),
                    nodeGroupLabel.getNodeGroupTraductionses().get(i).getIdLang(),
                    SKOSProperty.prefLabel);
        }
        if (!nodeGroupLabel.getNodeGroupTraductionses().isEmpty()) {
            concept.addDate(nodeGroupLabel.getNodeGroupTraductionses().get(0).getCreated().toString(), SKOSProperty.created);
            concept.addDate(nodeGroupLabel.getNodeGroupTraductionses().get(0).getModified().toString(), SKOSProperty.modified);
        }
        
        if(!nodeGroupLabel.getIdGroup().isEmpty()){
            concept.addIdentifier(nodeGroupLabel.getIdGroup(), SKOSProperty.identifier);
        }

        for (NodeUri listIdOfTopConcept1 : listIdOfTopConcept) {
        //    if(listIdOfTopConcept1.getIdArk() == null || listIdOfTopConcept1.getIdArk().trim().isEmpty()) {
            concept.addRelation(URI + "concept#" + listIdOfTopConcept1.getIdConcept(), SKOSProperty.narrower);
                /*
                concept.addRelation(serverAdress +
                        "?idc=" + listIdOfTopConcept1.getIdConcept() +
                        "&amp;idt=" + nodeGroupLabel.getIdThesaurus(),
                        SKOSProperty.narrower);*/
        /*    }
            else {
                concept.addRelation(serverArk +
                    listIdOfTopConcept1.getIdArk(),
                    SKOSProperty.narrower);
            }*/
        }
        skosBuff.append("    ").append(concept.toString());

        return true;
    }

    /**
     * Cette fonction permet d'exporter un Concept au format SKOS,
     * Si l'identifiant Ark existe, on l'exporte comme URI, sinon, on utilise l'adresse URI du Site par defaut.
     *
     * @param nodeConceptExport
     * 
     * @return true ou false
     */
    public boolean writeDescriptor(NodeConceptExport nodeConceptExport) {

        SKOSResource concept = new SKOSResource(serverAdress + "concept#" + nodeConceptExport.getConcept().getIdConcept());//getUri(nodeConceptExport));

        for (int i = 0; i < nodeConceptExport.getNodeTermTraductions().size(); i++) {
            concept.addLabel(
                    nodeConceptExport.getNodeTermTraductions().get(i).getLexicalValue(),
                    nodeConceptExport.getNodeTermTraductions().get(i).getLang(), SKOSProperty.prefLabel);
        }

        concept.addDate(nodeConceptExport.getConcept().getCreated().toString(), SKOSProperty.created);
        concept.addDate(nodeConceptExport.getConcept().getModified().toString(), SKOSProperty.modified);
        
        if(!nodeConceptExport.getConcept().getIdConcept().isEmpty()){
            concept.addIdentifier(nodeConceptExport.getConcept().getIdConcept(), SKOSProperty.identifier);
        }        

        for (int i = 0; i < nodeConceptExport.getNodeListIdsOfConceptGroup().size(); i++) {
            concept.addRelation(URI + "concept#" + nodeConceptExport.getNodeListIdsOfConceptGroup().get(i).getIdConcept(), SKOSProperty.inScheme);
        /*    concept.addRelation(
                    getRelationUri_inScheme(nodeConceptExport.getNodeListIdsOfConceptGroup().get(i),
                            nodeConceptExport.getConcept().getIdThesaurus()),
                    SKOSProperty.inScheme);*/
        }

        for (int i = 0; i < nodeConceptExport.getNodeListOfBT().size(); i++) {
            concept.addRelation(URI + "concept#" + nodeConceptExport.getNodeListOfBT().get(i).getUri().getIdConcept(), SKOSProperty.broader);
        /*    concept.addRelation(
                    getRelationUri(nodeConceptExport.getNodeListIdsOfBT().get(i),
                            nodeConceptExport.getConcept().getIdThesaurus()),
                    SKOSProperty.broader);
            */
        }

        for (int i = 0; i < nodeConceptExport.getNodeListOfNT().size(); i++) {
            concept.addRelation(URI + "concept#" + nodeConceptExport.getNodeListOfNT().get(i).getUri().getIdConcept(), SKOSProperty.narrower);
        /*    concept.addRelation(
                    getRelationUri(nodeConceptExport.getNodeListIdsOfNT().get(i),
                            nodeConceptExport.getConcept().getIdThesaurus()),
                    SKOSProperty.narrower);
            */
        }

        for (int i = 0; i < nodeConceptExport.getNodeListIdsOfRT().size(); i++) {
            concept.addRelation(URI + "concept#" + nodeConceptExport.getNodeListIdsOfRT().get(i).getIdConcept(), SKOSProperty.related);
        /*    concept.addRelation(
                    getRelationUri(nodeConceptExport.getNodeListIdsOfRT().get(i),
                            nodeConceptExport.getConcept().getIdThesaurus()),
                    SKOSProperty.related);
            */
        }

        for (int i = 0; i < nodeConceptExport.getNodeEM().size(); i++) {
            if(nodeConceptExport.getNodeEM().get(i).isHiden()) {
                concept.addLabel(nodeConceptExport.getNodeEM().get(i).getLexical_value(),
                        nodeConceptExport.getNodeEM().get(i).getLang(),
                        SKOSProperty.hiddenLabel);                
            }
            else {
                concept.addLabel(nodeConceptExport.getNodeEM().get(i).getLexical_value(),
                        nodeConceptExport.getNodeEM().get(i).getLang(),
                        SKOSProperty.altLabel);
            }
        }
        
        for (NodeAlignment alignment : nodeConceptExport.getNodeAlignmentsList()) {
            // alignement exactMatch
            if(alignment.getAlignement_id_type() == 1) {
                concept.addMapping(alignment.getUri_target().trim(), SKOSMapping.exactMatch);
            }

            // alignement closeMatch
            if(alignment.getAlignement_id_type() == 2) {
                concept.addMapping(alignment.getUri_target().trim(), SKOSMapping.closeMatch);
            }
        }
  /*     for (int i = 0; i < nodeConceptExport.getNodeAlignmentsList().size(); i++) {
            // alignement exactMatch
            if(nodeConceptExport.getNodeAlignmentsList().get(i).getAlignement_id_type() == 1) {
                concept.addMapping(nodeConceptExport.getNodeAlignmentsList().get(i).getUri_target().trim(), SKOSMapping.exactMatch);
            }

            // alignement closeMatch
            if(nodeConceptExport.getNodeAlignmentsList().get(i).getAlignement_id_type() == 2) {
                concept.addMapping(nodeConceptExport.getNodeAlignmentsList().get(i).getUri_target().trim(), SKOSMapping.closeMatch);
            }
        }*/
        
        for (NodeNote nodeNote : nodeConceptExport.getNodeNoteTerm()) {
            if(nodeNote.getNotetypecode().equalsIgnoreCase("scopeNote")){
                concept.addDocumentation(nodeNote.getLexicalvalue(), nodeNote.getLang(), SKOSProperty.scopeNote);
            }
            if(nodeNote.getNotetypecode().equalsIgnoreCase("historyNote")){
                concept.addDocumentation(nodeNote.getLexicalvalue(), nodeNote.getLang(), SKOSProperty.historyNote);
            }
            if(nodeNote.getNotetypecode().equalsIgnoreCase("editorialNote")){
                concept.addDocumentation(nodeNote.getLexicalvalue(), nodeNote.getLang(), SKOSProperty.editorialNote);
            }
            if(nodeNote.getNotetypecode().equalsIgnoreCase("definition")){
                concept.addDocumentation(nodeNote.getLexicalvalue(), nodeNote.getLang(), SKOSProperty.definition);
            }
        }
        skosBuff.append("    ").append(concept.toString());
        return true;
    }
    
    
    /**
     * Cette fonction permet de retourner l'URI du concept avec identifiant Ark : si renseigné
     * sinon l'URL du Site
     * 
     * @param nodeConceptExport
     * @return 
     */
    private String getUri(NodeConceptExport nodeConceptExport){
        String uri = "";
        
        /**
         * Cette partie est réservée pour l'export des PACTOLS de Frantiq vers Koha
         */
       // uri = serverAdress + "concept#" + nodeConceptExport.getConcept().getIdConcept();
        
        
        if(nodeConceptExport == null){
            System.out.println("nodeConcept = Null");
            return uri;
        }
        if(nodeConceptExport.getConcept() == null){
            System.out.println("nodeConcept.getConcept = Null");
            return uri;
        }
        if(nodeConceptExport.getConcept().getIdArk() == null){
            System.out.println("nodeConcept.getConcept().getIdArk = Null");
            uri = serverAdress + "?idc=" + nodeConceptExport.getConcept().getIdConcept()
                                + "&amp;idt=" + nodeConceptExport.getConcept().getIdThesaurus();
            return uri;
        }
        if(nodeConceptExport.getConcept().getIdArk().trim().isEmpty()){
            uri = serverAdress + "?idc=" + nodeConceptExport.getConcept().getIdConcept()
                            + "&amp;idt=" + nodeConceptExport.getConcept().getIdThesaurus();
        }
        else {
            uri = serverArk + nodeConceptExport.getConcept().getIdArk();
        }
        
        return uri;
    }
    
    /**
     * Cette fonction permet de retourner l'URI d'une relation 
     * avec identifiant Ark : si renseigné
     * sinon l'URL du Site
     * 
     * @param nodeConceptExport
     * @param id
     * @return 
     */
    private String getRelationUri(NodeUri nodeUri, String idThesaurus){
        String uri; 
        
        /**
         * Cette partie est réservée pour l'export des PACTOLS de Frantiq vers Koha
         */
        //uri = serverAdress + "concept#" + nodeUri.getIdConcept();
        
        if(nodeUri.getIdArk().trim().isEmpty()){
            uri = serverAdress + "?idc=" + nodeUri.getIdConcept()
                    + "&amp;idt=" + idThesaurus;
        }
        else {
            uri = serverArk + nodeUri.getIdArk();
        }
        
        return uri;
    }
    
    /**
     * Cette fonction permet de retourner l'URI d'une relation inScheme
     * avec identifiant Ark : si renseigné
     * sinon l'URL du Site
     * 
     * @param nodeConceptExport
     * @param id
     * @return 
     */
    private String getRelationUri_inScheme(NodeUri nodeUri, String idThesaurus){
        String uri; 
        
        /**
         * Cette partie est réservée pour l'export des PACTOLS de Frantiq vers Koha
         */
        
        //uri = serverAdress + "concept#" + nodeUri.getIdConcept();
        
        if(nodeUri.getIdArk().trim().isEmpty()){
            uri = serverAdress + "?idd=" + nodeUri.getIdConcept()
                    + "&amp;idt=" + idThesaurus;
        }
        else {
            uri = serverArk + nodeUri.getIdArk();
        }
               
        return uri;
    }
    
    
    public void endSkos() {
        //skos = skos + "</rdf:RDF>";
        skosBuff.append("</rdf:RDF>");
    }
    
    public String getSkos() {
        return skos;
    }

    public StringBuffer getSkosBuff() {
        return skosBuff;
    }
}
