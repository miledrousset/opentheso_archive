package mom.trd.opentheso.skosapi;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Djamel Ferhod
 *
 */
public class SKOSResource {

    private String uri;
    private String identifier;
    private int property;
    private ArrayList<SKOSLabel> labelsList;
    private ArrayList<SKOSRelation> relationsList;
    private ArrayList<SKOSDocumentation> documentationsList;
    private ArrayList<SKOSDate> dateList;
    private ArrayList<SKOSCreator> creatorList;
    private SKOSGPSCoordinates GPSCoordinates;
    private ArrayList<SKOSNotation> notationList;
    private ArrayList<SKOSMatch> matchList;

    /**
     *
     * @param uri un String URI de la ressource
     * @param property le type de ressource
     */
    public SKOSResource() {
        labelsList = new ArrayList<SKOSLabel>();
        relationsList = new ArrayList<SKOSRelation>();
        documentationsList = new ArrayList<SKOSDocumentation>();
        dateList = new ArrayList<SKOSDate>();
        creatorList = new ArrayList<>();
        GPSCoordinates = new SKOSGPSCoordinates();
        notationList = new ArrayList<>();
        matchList = new ArrayList<>();

    }

    public SKOSResource(String uri, int property) {

        labelsList = new ArrayList<SKOSLabel>();
        relationsList = new ArrayList<SKOSRelation>();
        documentationsList = new ArrayList<SKOSDocumentation>();
        dateList = new ArrayList<SKOSDate>();
        creatorList = new ArrayList<>();
        GPSCoordinates = new SKOSGPSCoordinates();
        notationList = new ArrayList<>();
        matchList = new ArrayList<>();

        this.property = property;
        this.uri = uri;
    }

    public int getProperty() {
        return property;
    }

    /**
     *
     * @return l'Identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<SKOSMatch> getMatchList() {
        return matchList;
    }

    public ArrayList<SKOSCreator> getCreatorList() {
        return creatorList;
    }

    public SKOSGPSCoordinates getGPSCoordinates() {
        return GPSCoordinates;
    }

    public ArrayList<SKOSNotation> getNotationList() {
        return notationList;
    }

    /**
     *
     * @return un String URI de la ressource
     */
    public String getUri() {
        return this.uri;
    }

    /**
     *
     * @return la liste des labels
     */
    public ArrayList<SKOSLabel> getLabelsList() {
        return labelsList;
    }

    /**
     *
     * @return la liste des relations
     */
    public ArrayList<SKOSRelation> getRelationsList() {
        return relationsList;
    }

    /**
     *
     * @return la liste des documenntations
     */
    public ArrayList<SKOSDocumentation> getDocumentationsList() {
        return documentationsList;
    }

    /**
     *
     * @return la liste des dates
     */
    public ArrayList<SKOSDate> getDateList() {
        return dateList;
    }

    /**
     *
     * @param v la valeur du match
     * @param prop le type de match
     */
    public void addMatch(String v, int prop) {
        matchList.add(new SKOSMatch(v, prop));
    }

    /**
     *
     * @param creator le nom
     * @param prop le type
     */
    public void addCreator(String creator, int prop) {
        creatorList.add(new SKOSCreator(creator, prop));
    }

    /**
     *
     * @param notation valeur
     */
    public void addNotation(String notation) {
        notationList.add(new SKOSNotation(notation));
    }

    /**
     * Méthode d'ajout des labels à la ressource, dans une ArrayList
     *
     * @param lab un String label
     * @param lang un String langue conforme xml (fr, eng...)
     * @param prop un int SKOSProperty
     */
    public void addLabel(String lab, String lang, int prop) {
        try {
            SKOSLabel label = new SKOSLabel(lab, lang, prop);
            labelsList.add(label);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Méthode d'ajout des relations à la ressource, dans une ArrayList
     *
     * @param uri un String URI
     * @param prop un int SKOSProperty
     */
    public void addRelation(String uri, int prop) {
        try {
            SKOSRelation relation = new SKOSRelation(uri, prop);
            relationsList.add(relation);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Méthode d'ajout des documentations à la ressource, dans une ArrayList
     *
     * @param text un String texte de la documentation
     * @param lang un String langue conforme xml (fr, en...)
     * @param prop un int SKOSProperty
     */
    public void addDocumentation(String text, String lang, int prop) {
        try {
            SKOSDocumentation documentation = new SKOSDocumentation(text, lang, prop);
            documentationsList.add(documentation);
        } catch (Exception e) {
            e.getMessage();
            System.out.println(e.getMessage());
        }
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public void setLabelsList(ArrayList<SKOSLabel> labelsList) {
        this.labelsList = labelsList;
    }

    public void setRelationsList(ArrayList<SKOSRelation> relationsList) {
        this.relationsList = relationsList;
    }

    public void setDocumentationsList(ArrayList<SKOSDocumentation> documentationsList) {
        this.documentationsList = documentationsList;
    }

    public void setDateList(ArrayList<SKOSDate> dateList) {
        this.dateList = dateList;
    }

    public void setCreatorList(ArrayList<SKOSCreator> creatorList) {
        this.creatorList = creatorList;
    }

    public void setNotationList(ArrayList<SKOSNotation> notationList) {
        this.notationList = notationList;
    }

    public void setMatchList(ArrayList<SKOSMatch> matchList) {
        this.matchList = matchList;
    }

    /**
     * Méthode d'ajout des dates de création et de modification à la ressource,
     * dans une ArrayList
     *
     * @param date un String date au format YYYY-MM-dd
     * @param prop un int SKOSProperty
     */
    public void addDate(String sDate, int prop) {
        try {
            SKOSDate date = new SKOSDate(sDate, prop);
            dateList.add(date);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void setGPSCoordinates(SKOSGPSCoordinates GPSCoordinates) {
        this.GPSCoordinates = GPSCoordinates;
    }

    /**
     *
     * @param uri String URI de la ressource
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Surcharge de la méthode toString() afin de mettre au format xml la
     * ressource
     */
    public String toString() {
        String xmlRessource = new String();
        Iterator<SKOSLabel> itLab = labelsList.iterator();
        Iterator<SKOSRelation> itRel = relationsList.iterator();
        Iterator<SKOSDocumentation> itDoc = documentationsList.iterator();
        Iterator<SKOSDate> itDate = dateList.iterator();

        xmlRessource = "<skos:Concept rdf:about=\"" + uri + "\">\n";

        while (itLab.hasNext()) {
            xmlRessource += "        " + itLab.next().toString();
        }

        while (itRel.hasNext()) {
            xmlRessource += "        " + itRel.next().toString();
        }

        while (itDoc.hasNext()) {
            xmlRessource += "        " + itDoc.next().toString();
        }
        while (itDate.hasNext()) {
            xmlRessource += "        " + itDate.next().toString();
        }

        xmlRessource += "    </skos:Concept>\n";

        return xmlRessource;
    }

}
