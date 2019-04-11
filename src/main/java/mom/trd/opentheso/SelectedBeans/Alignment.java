/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.ExternalImagesHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeIdValue;
import mom.trd.opentheso.bdd.helper.nodes.NodeImage;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import mom.trd.opentheso.core.alignment.AlignementSource;
import mom.trd.opentheso.core.alignment.AlignmentQuery;
import mom.trd.opentheso.core.alignment.SelectedResource;
import mom.trd.opentheso.core.alignment.helper.CurlHelper;
import mom.trd.opentheso.helper.wikidata.WikidataHelper;

/**
 *
 * @author miled.rousset
 */
@ManagedBean(name = "alignment", eager = true)
@ViewScoped

public class Alignment {
    
    private List<String> selectedOptions;
    
    private ArrayList<AlignementSource> alignementSources;    
    private String selectedAlignement;
    private AlignementSource selectedAlignementSource;
    private ArrayList<NodeAlignment> listAlignValues;
    private NodeAlignment selectedNodeAlignment;     
    private ArrayList<Map.Entry<String, String>> alignmentTypes;
    private int selectedAlignementType;
    
    private ArrayList<String> thesaurusUsedLanguageWithoutCurrentLang;
    private ArrayList<String> thesaurusUsedLanguage;
    
    
    // permet de gérer le flux des concepts 10 par 10
    private ArrayList<String> allIdsOfBranch;
    private HashMap<String, String> idsAndValues;
    
    private ArrayList<String> idsToGet;
    private String idConceptSelectedForAlignment;
    private String conceptValueForAlignment;
    private int counter = 0; // initialisation du compteur
    
    ////
    
    private ArrayList<NodeTermTraduction> nodeTermTraductions;
    private ArrayList<NodeNote> nodeNotes; 
    private ArrayList<NodeImage> nodeImages; 
    
    
    private ArrayList<SelectedResource>  traductionsWikidata;
    private ArrayList<SelectedResource> descriptionsWikidata;
    private ArrayList<SelectedResource> imagesWikidata;
    
    private boolean isSelectedAllLang = true;
    private boolean isSelectedAllDef = true;    
    private boolean isSelectedAllImages = true;    
    
    private boolean alignmentInProgress = false;
    
    // resultat de l'alignement 
    private String alignementResult = null;
    private boolean error;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    /**
     * Creates a new instance of Alignment
     */
    public Alignment() {
    }
    
    
    
    
    /////// gestion de la pagination pour le traitement par lot ///////
    
    /**
     * retourne les dix valeurs suivantes
     * @param idLang
     * @param idTheso
     */
    public void nextTen(String idLang, String idTheso){
        
        if(allIdsOfBranch == null) {
            idsAndValues = null;
            return;
        }
        if(counter > allIdsOfBranch.size()) return;
        idsToGet.clear();
        int counterTemp = counter;
        
        for (int i = counterTemp; i < 10; i++) {
            if(counter >= allIdsOfBranch.size()) {
                counter = allIdsOfBranch.size();
                break;
            }
            idsToGet.add(allIdsOfBranch.get(i));
            counter++;
        }
        getIdsAndValues(idLang, idTheso);
    }
    
    /**
     * retourne les dix valeurs précédantes
     * @param idLang
     * @param idTheso
     */    
    public void previousTen(String idLang, String idTheso) {
        if(allIdsOfBranch == null) {
            idsAndValues = null;
            return;
        }
        if(counter < 0) return;
        idsToGet.clear();
        int counterTemp = counter;
        
        for (int i = counterTemp; i < 10; i--) {
            if(counter < 0) {
                counter = 0;
                break;
            }
            idsToGet.add(allIdsOfBranch.get(i));
            counter--;
        }
        getIdsAndValues(idLang, idTheso);        
    }
    
    /**
     * remettre le compteur à zéro
     */
    public void resetCounter() {
        if(allIdsOfBranch != null) {
            idsAndValues = null;
        }
        counter =0;
    }
    
    private void getIdsAndValues(String idLang, String idTheso) {
        ConceptHelper conceptHelper = new ConceptHelper();
        idsAndValues = conceptHelper.getIdsAndValuesOfConcepts(
                connect.getPoolConnexion(),
                idsToGet,
                idLang,
                idTheso);
        selectConceptForAlignment();
    }
    
    public void selectConceptForAlignment() {
        conceptValueForAlignment = idsAndValues.get(idConceptSelectedForAlignment);
        cancelAlignment();
        resetValuesAlignement();
    }
    
    private void resetValuesAlignement(){
        if(listAlignValues != null)
            listAlignValues.clear();
    }
    
    
    /////// fin  gestion de la pagination pour le traitement par lot ///////    
    
    
    
    public void selectDeselectTrad() {
        if(isSelectedAllLang) {
            for (SelectedResource selectedResource : traductionsWikidata) {
                selectedResource.setSelected(true);
            }
            isSelectedAllLang = true;
        } else {
            for (SelectedResource selectedResource : traductionsWikidata) {
                selectedResource.setSelected(false);
            }
            isSelectedAllLang = false;
        }
    }
    
    public void selectDeselectDef() {
        if(isSelectedAllDef) {
            for (SelectedResource selectedResource : descriptionsWikidata) {
                selectedResource.setSelected(true);
            }
            isSelectedAllDef = true;
        } else {
            for (SelectedResource selectedResource : descriptionsWikidata) {
                selectedResource.setSelected(false);
            }
            isSelectedAllDef = false;
        }
    }
    
    public void selectDeselectImages() {
        if(isSelectedAllImages) {
            for (SelectedResource selectedResource : imagesWikidata) {
                selectedResource.setSelected(true);
            }
            isSelectedAllImages = true;
        } else {
            for (SelectedResource selectedResource : imagesWikidata) {
                selectedResource.setSelected(false);
            }
            isSelectedAllImages = false;
        }
    }     

    /**
     * permet d'initialiser le tableau des concepts à aligner 
     * @param idTheso
     * @param idConcept 
     * @param currentLang 
     */
    public void initAlignementByStep(
            String idTheso,
            String idConcept,
            String currentLang) {
        // liste des NT de la branche pour l'alignement par lot
        ConceptHelper conceptHelper = new ConceptHelper();
        allIdsOfBranch = conceptHelper.getIdsOfBranch(connect.getPoolConnexion(), idConcept, idTheso);
        idConceptSelectedForAlignment = idConcept;
        idsToGet = new ArrayList<>();
        
        counter = 0;
        initAlignmentSources(idTheso, idConcept, currentLang);
    }
    
    public void initAlignmentSources(String idTheso,
            String idConcept, String currentLang) {
        alignmentInProgress = false;
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        alignementSources = alignmentHelper.getAlignementSource(connect.getPoolConnexion(), idTheso);
        

        alignmentTypes = new ArrayList<>();
        HashMap<String, String> map = new AlignmentHelper().getAlignmentType(connect.getPoolConnexion());
        alignmentTypes.addAll(map.entrySet());
        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        thesaurusUsedLanguage = thesaurusHelper.getIsoLanguagesOfThesaurus(connect.getPoolConnexion(), idTheso);
        
        thesaurusUsedLanguageWithoutCurrentLang =  thesaurusHelper.getIsoLanguagesOfThesaurus(connect.getPoolConnexion(), idTheso);
        thesaurusUsedLanguageWithoutCurrentLang.remove(currentLang);
        

        
        selectedOptions = new ArrayList<>();
        selectedOptions.add("langues");
        selectedOptions.add("images");  
        selectedOptions.add("notes");
        traductionsWikidata = new ArrayList<>();
        descriptionsWikidata = new ArrayList<>();
        imagesWikidata = new ArrayList<>();

        isSelectedAllLang = true;
        reset();
        resetAlignmentResult();
        
    }

    private void reset(){
        if(traductionsWikidata != null)
            traductionsWikidata.clear();
        if(descriptionsWikidata != null)
            descriptionsWikidata.clear();
        if(imagesWikidata != null)
            imagesWikidata.clear();
        if(listAlignValues != null)
            listAlignValues.clear();
        if(nodeTermTraductions != null)
            nodeTermTraductions.clear();
        if(nodeNotes != null)
            nodeNotes.clear();
        if(nodeImages != null)
            nodeImages.clear();
        selectedNodeAlignment = null;
        
        isSelectedAllLang = true;
    }
    
    private void resetVariables(){
        if(traductionsWikidata != null)
            traductionsWikidata.clear();
        if(descriptionsWikidata != null)
            descriptionsWikidata.clear();
        if(imagesWikidata != null)
            imagesWikidata.clear();
        isSelectedAllLang = true;

    }  
    
    private void resetAlignmentResult(){
        alignementResult = null;
        error = false;
    }
    
    /**
     * permet de récupérer les traductions d'un concept en local
     * @param idConcept
     * @param idTheso 
     */
    private void getTraductionsOfConcept(
            String idTheso, 
            String idConcept){
        TermHelper termHelper = new TermHelper();
        nodeTermTraductions = 
                termHelper.getAllTraductionsOfConcept(connect.getPoolConnexion(),
                idConcept, idTheso);
    }
    
    /**
     * permet de charger dans l'objet 'traductionsWikidata' toutes les traductions 
     * qui n'existent pas en local
     * si la traduction en local est identique à celle récupérée, on l'ignore
     * si la traduction en local est différente, on l'ajoute à l'objet pour correction 
     * @param idConcept
     * @param idTheso 
     */
    private void setObjectTraductions(ArrayList<SelectedResource>  traductionsWikidataTemp){
        boolean added;
        
        // la liste des traductions de Wikidata
        for (SelectedResource selectedResource : traductionsWikidataTemp) {
            added = false;
            // la liste des traductions existantes
            for (NodeTermTraduction nodeTermTraduction : nodeTermTraductions) {
                // cas où la langue récupérée existe déjà en local
                if(selectedResource.getIdLang().equalsIgnoreCase(nodeTermTraduction.getLang())){
                    // on compare le texte si équivalent, on l'ignore
                    if(!selectedResource.getGettedValue().trim().equalsIgnoreCase(nodeTermTraduction.getLexicalValue().trim())){
                        selectedResource.setLocalValue(nodeTermTraduction.getLexicalValue());
                        traductionsWikidata.add(selectedResource);
                        added = true;
                        break;
                    } else {
                        added = true;
                        break;
                    }
                }
            }
            // si on a déjà ajouté la traduction, on l'ignore, sinon, on l'ajoute
            if(!added) {
                traductionsWikidata.add(selectedResource);
            }
        }
    }
    
    /**
     * permet de charger dans l'objet 'descriptionsWikidata' toutes les définitions 
     * qui n'existent pas en local
     * si la définition en local est identique à celle récupérée, on l'ignore
     * si la définition en local est différente, on l'ajoute à l'objet pour correction 
     * @param idConcept
     * @param idTheso 
     */
    private void setObjectDefinitions(ArrayList<SelectedResource>  descriptionsWikidataTemp){
        boolean added;
        
        // la liste des traductions de Wikidata
        for (SelectedResource selectedResource : descriptionsWikidataTemp) {
            added = false;
            // la liste des traductions existantes
            for (NodeNote nodeNote : nodeNotes) {
                // on compare le texte si équivalent, on l'ignore
                if(!selectedResource.getGettedValue().trim().equalsIgnoreCase(nodeNote.getLexicalvalue().trim())){
                    selectedResource.setLocalValue(nodeNote.getLexicalvalue());
                    descriptionsWikidata.add(selectedResource);
                    added = true;
                    break;
                } else {
                    added = true;
                    break;
                }
            }
            // si on a déjà ajouté la traduction, on l'ignore, sinon, on l'ajoute
            if(!added) {
                descriptionsWikidata.add(selectedResource);
            }
        }
    }      
    
    /**
     * permet de charger dans l'objet 'imagesWikidata' toutes les images 
     * qui n'existent pas en local
     * si l'image en local est identique à celle récupérée, on l'ignore
     * si l'image en local est différente, on l'ajoute à l'objet pour correction 
     * @param idConcept
     * @param idTheso 
     */
    private void setObjectImages(ArrayList<SelectedResource>  imagesWikidataTemp){
        boolean added;
        
        // la liste des traductions de Wikidata
        for (SelectedResource selectedResource : imagesWikidataTemp) {
            added = false;
            // la liste des traductions existantes
            for (NodeImage nodeImage : nodeImages) {
                // on compare l'URI est équivalente, on l'ignore
                if(!selectedResource.getGettedValue().trim().equalsIgnoreCase(nodeImage.getUri().trim())){
                    selectedResource.setLocalValue(nodeImage.getUri());
                    imagesWikidata.add(selectedResource);
                    added = true;
                    break;
                } else {
                    added = true;
                    break;
                }
            }
            // si on a déjà ajouté la traduction, on l'ignore, sinon, on l'ajoute
            if(!added) {
                imagesWikidata.add(selectedResource);
            }
        }
    }    
    
    /**
     * permet de récupérer les définitions existantes 
     * pour permettre de structurer l'objet pour comparer les définitions locales et distantes 
     * @param idConcept
     * @param idTheso 
     */
    private void getDefinitionsOfConcept(
            String idTheso, 
            String idConcept){
        TermHelper termHelper = new TermHelper();
        String idTerm = termHelper.getIdTermOfConcept(connect.getPoolConnexion(), idConcept, idTheso);

        NoteHelper noteHelper = new NoteHelper();
        nodeNotes = noteHelper.getListNotesTermAllLang(connect.getPoolConnexion(),
                        idTerm, idTheso);
    }
    
    /**
     * permet de récuprer les images du concepts (URI des images)
     * pour pouvoir structurer l'objet pour comparer les images locales et distantes 
     * @param idConcept
     * @param idTheso 
     */
    private void getExternalImagesOfConcept(
            String idTheso, 
            String idConcept){
        ExternalImagesHelper imagesHelper = new ExternalImagesHelper();
        nodeImages = imagesHelper.getExternalImages(connect.getPoolConnexion(),
                        idConcept, idTheso);
    }    
    
    
    public void prepareAlignment(
            String idTheso,
            String idConcept,
            String lexicalValue,
            String idLang){
        reset();
        for (AlignementSource alignementSource : alignementSources) {
            if(alignementSource.getSource().equalsIgnoreCase(selectedAlignement)) {
                selectedAlignementSource = alignementSource;
                break;
            }
        }
        // si l'alignement est de type Wikidata
        if(selectedAlignementSource.getSource().equalsIgnoreCase("wikidata")) {
            getAlignmentWikidata(
                    selectedAlignementSource,
                    idTheso,
                    idConcept,
                    lexicalValue,
                    idLang);
        }
    }
    /**
     * Cette fonction permet de récupérer les concepts à aligner de la source
     *
     * @param alignementSource
     * @param idTheso
     * @param idConcept
     * @param lexicalValue
     * @param idLang
     */
    private void getAlignmentWikidata(
            AlignementSource alignementSource,
            String idTheso,
            String idConcept,
            String lexicalValue,
            String idLang
            ) {
        
        if (alignementSource == null) {
            listAlignValues = null;
            return;
        }
        AlignmentQuery alignmentQuery = new AlignmentQuery();
        listAlignValues = new ArrayList<>();
        
        // action JSON (HashMap (Wikidata)
        //ici il faut appeler le filtre de Wikidata 
        listAlignValues = alignmentQuery.queryWikidata(idConcept, idTheso, lexicalValue.trim(),
                    idLang, alignementSource.getRequete(),
                    alignementSource.getSource());
        if(listAlignValues == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Item Unselected", alignmentQuery.getMessage()));
        }
    }    

    
    
    
    /**
     * récupération des options de Wikidata
     * @param selectedNodeAlignment
     * @param idTheso
     * @param idConcept
     */
    public void getOptions(NodeAlignment selectedNodeAlignment,
            String idTheso, String idConcept){
        alignmentInProgress = true;
        resetAlignmentResult();
        // si l'alignement est de type Wikidata
        this.selectedNodeAlignment = selectedNodeAlignment;
        if(selectedAlignementSource.getSource().equalsIgnoreCase("wikidata")) {
            resetVariables();
            getOptionsWikidata(selectedNodeAlignment,
                    idTheso, idConcept);
        }
    }
    /**
     * Cette fonction permet de récupérer les options de Wikidata 
     * Images, alignements, traductions....
     *
     * @param alignementSource
     * @param idTheso
     * @param idConcept
     * @param lexicalValue
     * @param idLang
     */
    private void getOptionsWikidata(
            NodeAlignment selectedNodeAlignment,
            String idTheso, String idConcept) {
        if (selectedNodeAlignment == null) {
            listAlignValues = null;
            return;
        }
        WikidataHelper wikidataHelper = new WikidataHelper();
        ArrayList<SelectedResource> resourceWikidataTemp;
        CurlHelper curlHelper = new CurlHelper();
        curlHelper.setHeader1("Accept");
        curlHelper.setHeader2("application/json");
        
        String uri = selectedNodeAlignment.getUri_target();//."https://www.wikidata.org/entity/Q178401";//"https://www.wikidata.org/entity/Q178401";//Q7748";Q324926
        String datas = curlHelper.getDatasFromUri(uri);
        String entity = uri.substring(uri.lastIndexOf("/") + 1);

        
        for (String selectedOption : selectedOptions) {
            switch (selectedOption) {
                case "langues":
                    resourceWikidataTemp = wikidataHelper.getTraductions(datas, entity, thesaurusUsedLanguageWithoutCurrentLang);
                    getTraductionsOfConcept(idTheso, idConcept);
                    setObjectTraductions(resourceWikidataTemp);
                    break;
                case "notes":
                    resourceWikidataTemp = wikidataHelper.getDescriptions(datas, entity, thesaurusUsedLanguage);
                    getDefinitionsOfConcept(idTheso, idConcept);
                    setObjectDefinitions(resourceWikidataTemp);
                    break;
                case "images":
                    resourceWikidataTemp = wikidataHelper.getImages(datas, entity);
                    getExternalImagesOfConcept(idTheso, idConcept);
                    setObjectImages(resourceWikidataTemp);
                    break;                    
            }
        }
    }    
    
    /**
     * permet d'ajouter l'alignement et les options choisis (traductions, définitions et images)
     * la focntion gère les erreurs en cas de problème
     * @param idTheso
     * @param idConcept
     * @param idUser 
     */
    public void addAlignment(String idTheso, String idConcept,
            int idUser){
        if(selectedNodeAlignment == null) return;
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        // ajout de l'alignement séléctionné
        if(!addAlignment__(idTheso, idConcept, idUser)){
            return;
        }
        
        // ajout des traductions 
        if(!addTraductions__(idTheso, idConcept, idUser)) {
            return;
        }
        
        // ajout des définitions 
        if(!addDefinitions__(idTheso, idConcept, idUser)) {
            return;
        }        

        // ajout des images
        if(!addImages__(idTheso, idConcept, idUser)) {
            return;
        }
        
        alignementResult = alignementResult + alignmentHelper.getMessage();
        selectedNodeAlignment = null;
        alignmentInProgress = false;
        resetVariables();
    }
    
    /**
     * Permet d'ajouter l'alignement choisi dans la base de données
     * @param idTheso
     * @param idConcept
     * @param idUser
     * @return 
     */
    private boolean addAlignment__(String idTheso, String idConcept, int idUser) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        
        // ajout de l'alignement séléctionné
        if(!alignmentHelper.addNewAlignment(connect.getPoolConnexion(),
                idUser,
                selectedNodeAlignment.getConcept_target(),
                selectedNodeAlignment.getThesaurus_target(),
                selectedNodeAlignment.getUri_target(),
                selectedAlignementType,
                idConcept, idTheso, selectedAlignementSource.getId())){
            alignementResult = "Erreur pendant l'ajout de l'alignement: " + 
                    alignmentHelper.getMessage();
            alignmentInProgress = false;
            selectedNodeAlignment = null;
            resetVariables();
            error = true;
            return false;
        }
        alignementResult = "Traductions ajoutées ##";
        return true;
    }
    
    private boolean addTraductions__(String idTheso, String idConcept, int idUser){
        TermHelper termHelper = new TermHelper();
        Term term = new Term();
        String idTerm = termHelper.getIdTermOfConcept(connect.getPoolConnexion(), idConcept, idTheso);
        if(idTerm == null) return false;
        
        for (SelectedResource selectedResource : traductionsWikidata) {
            if(selectedResource.isSelected()) {
                term.setId_thesaurus(idTheso);
                term.setLang(selectedResource.getIdLang());
                term.setLexical_value(selectedResource.getGettedValue());
                term.setId_term(idTerm);
                term.setContributor(idUser);
                term.setCreator(idUser);
                term.setSource("");
                term.setStatus("");            
                if(termHelper.isTraductionExistOfConcept(connect.getPoolConnexion(),
                        idConcept, idTheso, selectedResource.getIdLang())){
                    // update                
                    if(!termHelper.updateTermTraduction(connect.getPoolConnexion(), term, idUser)) {
                        error = true;
                        alignementResult = alignementResult + ": Erreur pendant la modification des traductions";
                    }
                } else {
                    // insert
                    if (!termHelper.addTraduction(connect.getPoolConnexion(), term, idUser)) {
                        error = true;
                        alignementResult = alignementResult + ": Erreur dans l'ajout des traductions";
                    }
                }
            }
        }
        alignementResult = alignementResult + " Traductions ajoutées ##";
        return true;
    }
    
    private boolean addDefinitions__(String idTheso, String idConcept, int idUser){
        NoteHelper noteHelper = new NoteHelper();
        TermHelper termHelper = new TermHelper();
        String idTerm = termHelper.getIdTermOfConcept(connect.getPoolConnexion(), idConcept, idTheso);
        if(idTerm == null) return false;
        
        // ajout de la note avec prefix de la source (wikidata)
        for (SelectedResource selectedResource : descriptionsWikidata) {
            if(selectedResource.isSelected()) {
                if(!noteHelper.addTermNote(connect.getPoolConnexion(),
                        idTerm, selectedResource.getIdLang(),
                        idTheso,
                        selectedResource.getGettedValue() + " (" + selectedAlignement + ")",
                        "definition",
                        idUser)){
                    error = true;
                    alignementResult = alignementResult + ": Erreur dans l'ajout des définitions";
                }
            }
        }
        alignementResult = alignementResult + " Définitions ajoutées ##";
        return true;
    }   
    
    private boolean addImages__(String idTheso, String idConcept, int idUser){
        ExternalImagesHelper imagesHelper = new ExternalImagesHelper();
        for (SelectedResource selectedResource : imagesWikidata) {
            if(selectedResource.isSelected()) {
                if(!imagesHelper.addExternalImage(connect.getPoolConnexion(),
                        idConcept, idTheso,
                        "",
                        selectedAlignement,
                        selectedResource.getGettedValue(),
                        idUser)) {
                    error = true;
                    alignementResult = alignementResult + ": Erreur dans l'ajout des images";
                }
            }
        }
        alignementResult = alignementResult + " Images ajoutées";        
        return true;
    }    
   
    public void cancelAlignment(){
        selectedNodeAlignment = null;
        alignmentInProgress = false;
        resetVariables();
    }
    
    
    public ArrayList<Map.Entry<String, String>> getAlignmentTypes() {
        return alignmentTypes;
    }

    public void setAlignmentTypes(ArrayList<Map.Entry<String, String>> alignmentTypes) {
        this.alignmentTypes = alignmentTypes;
    }
    
   
    
    public ArrayList<AlignementSource> getAlignementSources() {
        return alignementSources;
    }

    public void setAlignementSources(ArrayList<AlignementSource> alignementSources) {
        this.alignementSources = alignementSources;
    }
    
    
    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
    

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getSelectedAlignement() {
        return selectedAlignement;
    }

    public void setSelectedAlignement(String selectedAlignement) {
        this.selectedAlignement = selectedAlignement;
    }

    public AlignementSource getSelectedAlignementSource() {
        return selectedAlignementSource;
    }

    public void setSelectedAlignementSource(AlignementSource selectedAlignementSource) {
        this.selectedAlignementSource = selectedAlignementSource;
    }

    public ArrayList<NodeAlignment> getListAlignValues() {
        return listAlignValues;
    }

    public void setListAlignValues(ArrayList<NodeAlignment> listAlignValues) {
        this.listAlignValues = listAlignValues;
    }

    public int getSelectedAlignementType() {
        return selectedAlignementType;
    }

    public void setSelectedAlignementType(int selectedAlignementType) {
        this.selectedAlignementType = selectedAlignementType;
    }

    public ArrayList<SelectedResource> getTraductionsWikidata() {
        return traductionsWikidata;
    }

    public void setTraductionsWikidata(ArrayList<SelectedResource> traductionsWikidata) {
        this.traductionsWikidata = traductionsWikidata;
    }

    public ArrayList<SelectedResource> getDescriptionsWikidata() {
        return descriptionsWikidata;
    }

    public void setDescriptionsWikidata(ArrayList<SelectedResource> descriptionsWikidata) {
        this.descriptionsWikidata = descriptionsWikidata;
    }

    public boolean isIsSelectedAllLang() {
        return isSelectedAllLang;
    }

    public void setIsSelectedAllLang(boolean isSelectedAllLang) {
        this.isSelectedAllLang = isSelectedAllLang;
    }

    public boolean isIsSelectedAllDef() {
        return isSelectedAllDef;
    }

    public void setIsSelectedAllDef(boolean isSelectedAllDef) {
        this.isSelectedAllDef = isSelectedAllDef;
    }

    public ArrayList<SelectedResource> getImagesWikidata() {
        return imagesWikidata;
    }

    public void setImagesWikidata(ArrayList<SelectedResource> imagesWikidata) {
        this.imagesWikidata = imagesWikidata;
    }

    public boolean isIsSelectedAllImages() {
        return isSelectedAllImages;
    }

    public void setIsSelectedAllImages(boolean isSelectedAllImages) {
        this.isSelectedAllImages = isSelectedAllImages;
    }

    public NodeAlignment getSelectedNodeAlignment() {
        return selectedNodeAlignment;
    }

    public void setSelectedNodeAlignment(NodeAlignment selectedNodeAlignment) {
        this.selectedNodeAlignment = selectedNodeAlignment;
    }

    public boolean isAlignmentInProgress() {
        return alignmentInProgress;
    }

    public void setAlignmentInProgress(boolean alignmentInProgress) {
        this.alignmentInProgress = alignmentInProgress;
    }

    public String getAlignementResult() {
        return alignementResult;
    }

    public void setAlignementResult(String alignementResult) {
        this.alignementResult = alignementResult;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public HashMap<String, String> getIdsAndValues() {
        return idsAndValues;
    }

    public String getIdConceptSelectedForAlignment() {
        return idConceptSelectedForAlignment;
    }

    public void setIdConceptSelectedForAlignment(String idConceptSelectedForAlignment) {
        this.idConceptSelectedForAlignment = idConceptSelectedForAlignment;
    }

    public String getConceptValueForAlignment() {
        return conceptValueForAlignment;
    }

    public void setConceptValueForAlignment(String conceptValueForAlignment) {
        this.conceptValueForAlignment = conceptValueForAlignment;
    }
}
