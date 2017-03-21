package mom.trd.opentheso.SelectedBeans;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.ParserConfigurationException;
import mom.trd.opentheso.bdd.datas.Languages_iso639;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.LanguageHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.UserHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeAlignment;
import mom.trd.opentheso.bdd.helper.nodes.NodeGps;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.core.alignment.AlignementPreferences;
import mom.trd.opentheso.core.alignment.AlignementSource;
import mom.trd.opentheso.core.alignment.GpsPreferences;
import mom.trd.opentheso.core.alignment.GpsQuery;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;
import org.xml.sax.SAXException;

@ManagedBean(name = "gps", eager = true)
@SessionScoped
public class GpsBeans {

    private MapModel geoModel;

    public double latitud;
    public double longitud;

    private ArrayList<AlignementSource> listeAlignementSources;
    private ArrayList<AlignementSource> alignementSources;
    private ArrayList<NodeAlignment> listAlignValues;
    private ArrayList<Languages_iso639> listLanguesInTheso;

    private NodeAlignment alignment_choisi;

    private AlignementSource alignementPreferences;
    GpsPreferences nodePreference;

    private String selectedAlignement;
    private String centerGeoMap = "41.850033, -87.6500523";
    private String coordennees = null;
    private String codeid;

    private boolean integrerTraduction = true;
    private boolean reemplacerTraduction = true;
    private boolean alignementAutomatique = true;

    // variables pour le lot
    private AlignementSource alignement_source = new AlignementSource();

    private AlignementPreferences alignementPreferencesAlignement;
    private ArrayList<String> listConceptTrates = new ArrayList<>();
    private ArrayList<String> listOfChildrenInConcept;
    private Map<Integer, String> options;
    private NodeAlignment nodeAli;
    private Term term;

    private String id_concept;
    private String id_theso;
    private String id_langue;
    private String nomduterm;
    private String message = "";
    private String erreur = "";
    private String uriSelection = "";
    private String id_concept_depart;

    private int id_user1;
    private int position = 0;
    private int alignement_id_type;
    private static int optionAllBranch = 0;
    private static int optionNonAligned = 1;
    private static int optionWorkFlow = 2;
    private int optionOfAlignement = -1;
    private boolean fin = false;
    private boolean first = true;
    private boolean last = false;
    private boolean mettreAJour = false;
    //////
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    @ManagedProperty(value = "#{user1}")
    private CurrentUser theUser;
    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @PostConstruct
    public void init() {
        geoModel = new DefaultMapModel();
    }

    /*
    public void onGeocode(GeocodeEvent event) {
        boolean premiere = true;
        List<GeocodeResult> results = event.getResults();
        geoModel = new DefaultMapModel();
        if (results != null && !results.isEmpty()) {
            LatLng center = results.get(0).getLatLng();
            centerGeoMap = center.getLat() + "," + center.getLng();

            for (int i = 0; i < results.size(); i++) {
                GeocodeResult result = results.get(i);
                geoModel.addOverlay(new Marker(result.getLatLng(), result.getAddress()));
                if (premiere) {
                    latitud = result.getLatLng().getLat();
                    longitud = result.getLatLng().getLng();
                    premiere = false;
                }
            }
        }
    }
     */
    /**
     * Ajoute le coordonées a la BDD
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    public boolean addCoordinates(String idConcept, String idTheso) {
        GpsHelper gpshelper = new GpsHelper();
        gpshelper.insertCoordonees(connect.getPoolConnexion(), idConcept, idTheso, latitud, longitud);
        return true;
    }

    /**
     * récupère les coordonées de la BDD
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    public boolean getCoordinatesGps(String idConcept, String idTheso) {
        GpsHelper gpshelper = new GpsHelper();
        NodeGps nodeGps = gpshelper.getCoordinate(connect.getPoolConnexion(), idConcept, idTheso);
        if (nodeGps != null) {
            latitud = nodeGps.getLatitude();
            longitud = nodeGps.getLongitude();
        } else {
            initcoordonees();
        }
        return true;
    }

    /**
     * permet d'ajouter le coordonées gps automatique
     *
     * @param idC
     * @param id_Theso
     * @param id_user
     * @param langEnCour
     * @param idTerm
     * @return
     * @throws ParserConfigurationException
     */
    public boolean doAll(String idC, String id_Theso,
            int id_user, String langEnCour, String idTerm) throws ParserConfigurationException {

        id_theso = id_Theso;
        id_concept = idC;

        boolean status = false;
        boolean found = false;
        if (codeid != null) {
            for (NodeAlignment na : listAlignValues) {
                if (na.getIdUrl() == null ? codeid == null : na.getIdUrl().equals(codeid) && !found) {
                    alignment_choisi = na;
                    found = true;
                }
            }
            if (found) {
                latitud = alignment_choisi.getLat();
                longitud = alignment_choisi.getLng();
                addCoordinates(idC, id_Theso);
                if (alignementAutomatique) {
                    status = alignementautomatique(idTerm);
                }
                if (listOfChildrenInConcept != null) {
                    nextPosition();
                }
                selectedTerme.majLangueConcept();
                selectedTerme.setAlign(new AlignmentHelper().getAllAlignmentOfConcept(connect.getPoolConnexion(), idC, id_theso));
                initcoordonees();
            }
        }
        return status;
    }

    /**
     * Ajoute de manière automatique un alignement
     *
     * @param idTerm
     * @return
     */
    private boolean alignementautomatique(String idTerm) {
        boolean status = false;
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        if (alignmentHelper.addNewAlignment(connect.getPoolConnexion(), theUser.getUser().getId(),
                alignment_choisi.getName(), alignementPreferences.getSource(),
                alignment_choisi.getIdUrl(), 1, id_concept, id_theso, alignementPreferences.getId())) {
            if (integrerTraduction) {
                status = integreTraduction(idTerm);
            }
        }
        return status;
    }

    /**
     * Fait l'intregration du traduction du term
     *
     * @param idTerm
     * @return
     */
    private boolean integreTraduction(String idTerm) {
        boolean status = false;
        LanguageHelper languageHelper = new LanguageHelper();
        listLanguesInTheso = new ArrayList<>();
        listLanguesInTheso = languageHelper.getLanguagesOfThesaurus(connect.getPoolConnexion(), id_theso);
        if (alignment_choisi.getAlltraductions() != null) {
            if (!alignment_choisi.getAlltraductions().isEmpty()) {
                for (NodeLang languesOfGps : alignment_choisi.getAlltraductions()) {
                    for (Languages_iso639 langueTheso : listLanguesInTheso) {
                        if (langueTheso.getId_iso639_1().equals(languesOfGps.getCode())) {
                            term = new Term();
                            term.setLexical_value(languesOfGps.getValue());
                            term.setId_term(idTerm);
                            term.setId_thesaurus(id_theso);
                            term.setLang(languesOfGps.getCode());
                            term.setSource("");
                            term.setStatus("");
                            if (reemplacerTraduction) {
                                status = reemplacerTraduction(term);
                            }
                        }
                    }
                }
            }
        }
        return status;
    }

    /**
     * Reemplace les traductions de la BDD pour les nouvelles
     *
     * @param term
     * @return
     */
    private boolean reemplacerTraduction(Term term) {
        if (!new TermHelper().updateTermTraduction(connect.getPoolConnexion(), term, theUser.getUser().getId())) {
            return false;
        } else if (!new TermHelper()
                .isExitsTraduction(connect.getPoolConnexion(), term)) {
            if (!new TermHelper().updateTermTraduction(connect.getPoolConnexion(), term, theUser.getUser().getId())) {
                return false;
            }
        }
        return true;
    }

    private void initcoordonees() {
        latitud = 0.0;
        longitud = 0.0;
    }

    public void reinitBoolean() {
        integrerTraduction = true;
        reemplacerTraduction = true;
        alignementAutomatique = true;
    }

    /**
     * Permet d'inserte dans la BDD les preferences du GPS pour cette Thesaurus
     *
     * @param id_Theso
     * @param id_lang
     */
    public void validateParamretagesGps(String id_Theso, String id_lang, int id_user) {
        boolean status = true;
        if (selectedAlignement != null) {
            for (AlignementSource alignementSource : alignementSources) {
                if (alignementSource.getSource() == null ? selectedAlignement == null : alignementSource.getSource().equals(selectedAlignement)) {
                    alignementPreferences = alignementSource;
                }
            }
            GpsHelper gpsHelper = new GpsHelper();
            if (!gpsHelper.garderPreferences(connect.getPoolConnexion(), id_Theso,
                    integrerTraduction, reemplacerTraduction, alignementAutomatique, alignementPreferences.getId(), id_user)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Ne peux pas faire uptdate de preferences"));
                //message error
                status = false;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Vous besoin selectionée une source"));
        }
        if (status) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", " validation de parametrisage fait!"));
        }

    }

    public void creerAlignAuto(String idC, String idTheso, String nom, String idlangue) throws ParserConfigurationException, SAXException {

        GpsQuery gpsQuery = new GpsQuery();
        GpsHelper gpsHelper = new GpsHelper();
        if (selectedAlignement != null) {
            for (AlignementSource alignementSource : alignementSources) {
                if (alignementSource.getRequete() == null ? selectedAlignement == null : alignementSource.getRequete().equals(selectedAlignement)) {
                    alignementPreferences = alignementSource;
                }
            }
            listAlignValues = new ArrayList<>();
            if ("REST".equalsIgnoreCase(alignementPreferences.getTypeRequete())) {
                if ("xml".equals(alignementPreferences.getAlignement_format())) {

                    listAlignValues = gpsQuery.queryGps2(idC, idTheso, nom.trim(), idlangue, alignementPreferences.getRequete());
                }
            }

        }
    }

    /**
     * Crée les alignements Par Lot
     *
     * @param idC
     * @param idTheso
     * @param nom
     * @param idlangue
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void creerAlignAutoParLot(String idC, String idTheso, String nom, String idlangue) throws ParserConfigurationException, SAXException {

        GpsHelper gpsHelper = new GpsHelper();
        nodePreference = gpsHelper.getGpsPreferences(connect.getPoolConnexion(), id_theso, id_user1, alignementPreferences.getId());
        if (nodePreference != null) {

            GpsQuery gpsQuery = new GpsQuery();
            alignementPreferences = gpsHelper.find_alignement_gps(connect.getPoolConnexion(), nodePreference.getId_alignement_source());
            listAlignValues = new ArrayList<>();

            if ("REST".equalsIgnoreCase(alignementPreferences.getTypeRequete())) {
                if ("xml".equals(alignementPreferences.getAlignement_format())) {

                    listAlignValues = gpsQuery.queryGps2(idC, idTheso, nom.trim(), idlangue, alignementPreferences.getRequete());
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Ne peux pas recuperer le preferences"));
            //message ne peux pas recuperer le preferences
        }
    }

    /**
     * fait la recuperation de les alignements du GPS
     *
     * @param idTheso
     */
    public void setListeAlignementSources(String idTheso) {
        int role = theUser.getUser().getIdRole();
        if (role == 1 || role == 2) {
            AlignmentHelper alignmentHelper = new AlignmentHelper();
            listeAlignementSources = alignmentHelper.getAlignementSourceSAdmin(connect.getPoolConnexion());
        }
    }

    /////////////////*******************/////////////////////
    /////////////////*******************/////////////////////
    /////////////////   GPS PAR LOT     /////////////////////
    /////////////////*******************/////////////////////
    /////////////////*******************/////////////////////
    /**
     * Permet de savoir combien d'enfants a le concept selectionnée
     *
     * @param id_Theso
     * @param id_Concept
     * @param id_lang
     */
    public void getListChildren(String id_Theso, String id_Concept, String id_lang, int id_user) {
        reinitTotal();
        initOption();
        id_user1 = id_user;
        id_concept_depart = id_Concept;
        id_concept = id_Concept;
        id_theso = id_Theso;
        id_langue = id_lang;
        ConceptHelper conceptHelper = new ConceptHelper();
        listOfChildrenInConcept = new ArrayList<>();
        listOfChildrenInConcept = conceptHelper.getIdsOfBranch(
                connect.getPoolConnexion(), id_concept, id_Theso, listOfChildrenInConcept);

        if (listOfChildrenInConcept.isEmpty() || listOfChildrenInConcept.size() == 1) {
            last = true;
        }
        nomduterm = selectedTerme.nom;
    }

    private void initOption() {
        optionOfAlignement = -1;
        options = new LinkedHashMap<>();
        options.put(optionAllBranch, langueBean.getMsg("alig.TTB"));
        options.put(optionNonAligned, langueBean.getMsg("alig.nonA"));
        options.put(optionWorkFlow, langueBean.getMsg("alig.workF"));
    }

    /**
     * reinicialitation du variables
     */
    public void reinitTotal() {
        listOfChildrenInConcept = null;
        nomduterm = "";
        last = false;
        fin = false;
        position = 0;
        first = true;
        last = false;
    }

    public void nextPosition() {

        if (fin) {
            return;
        }
        listConceptTrates.add(id_concept);
        erreur = "";
        GpsHelper gpsHelper = new GpsHelper();
        ConceptHelper conceptHelper = new ConceptHelper();
        if (optionAllBranch == optionOfAlignement || optionOfAlignement == optionWorkFlow) {
            position++;
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            }
            comprobationFin();
            if (fin) {
                return;
            }
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        }
        if (optionNonAligned == optionOfAlignement) {
            position++;
            if (position < listOfChildrenInConcept.size()) {
                id_concept = listOfChildrenInConcept.get(position);
            }
            comprobationFin();
            while (gpsHelper.isHaveCoordinate(connect.getPoolConnexion(),
                    id_concept, id_theso)) {
                position++;
                if (position < listOfChildrenInConcept.size()) {
                    id_concept = listOfChildrenInConcept.get(position);
                }
                comprobationFin();
            }
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        }
        try {
            creerAlignAutoParLot(id_concept, id_theso, nomduterm, id_langue);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GpsBeans.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (SAXException ex) {
            Logger.getLogger(GpsBeans.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Permet de savoir si c'est le fin de l'Arraylist et sortir du dialog
     */
    private void comprobationFin() {

        if (position == listOfChildrenInConcept.size() - 1) {
            last = true;
        }
        if (position == listOfChildrenInConcept.size()) {
            fin = true;
        }

    }

    /**
     * Permet de savoir le premiere element que on besoin montrer
     *
     * @param id_Concept
     * @param idTheso
     * @param id_lang
     * @param id_user
     */
    public void getPreliereElement(String id_Concept, String idTheso, String id_lang, int id_user) {
        GpsHelper gpsHelper = new GpsHelper();
        nodePreference = gpsHelper.getGpsPreferences(connect.getPoolConnexion(), idTheso, id_user, alignementPreferences.getId());
        alignementPreferences = gpsHelper.find_alignement_gps(connect.getPoolConnexion(), nodePreference.getId_alignement_source());
        getPreferenceAlignement(idTheso, id_user);
        if (optionOfAlignement != -1) {
            if (optionAllBranch == optionOfAlignement) {
                ismiseAJour(idTheso, id_lang);
            }
            if (optionOfAlignement == optionNonAligned) {
                isNonAligne(id_Concept, id_theso, id_lang);
            }
            if (optionWorkFlow == optionOfAlignement) {//reprise de la suite
                if (alignementPreferencesAlignement.getId_concept_tratees() == null) {
                    ismiseAJour(idTheso, id_lang);
                } else {
                    isSuite();
                }
            }
        }
    }

    public void isSuite() {
        String dejaTratees[];
        boolean trouve = false;
        boolean sort = false;
        ConceptHelper conceptHelper = new ConceptHelper();

        dejaTratees = (alignementPreferencesAlignement.getId_concept_tratees()).split("#");
        ArrayList<String> conceptFait = new ArrayList<>();
        conceptFait.addAll(Arrays.asList(dejaTratees));
        listConceptTrates = conceptFait;

        if (!conceptFait.isEmpty()) {
            for (String traite : conceptFait) {
                if (listOfChildrenInConcept.contains(traite)) {
                    listOfChildrenInConcept.remove(traite);
                }
            }
        }
        if (listOfChildrenInConcept.isEmpty()) {
            last = true;
            fin = true;
            message = "tout la branche traités";
            return;
        }
        comprobationFin();
        id_concept = listOfChildrenInConcept.get(0);
        nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), listOfChildrenInConcept.get(0),
                selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        selectedTerme.creerAlignAuto(listOfChildrenInConcept.get(0), nomduterm);
    }

    public void isNonAligne(String id_Concept, String id_theso, String id_lang) {
        ConceptHelper conceptHelper = new ConceptHelper();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        GpsHelper gpsHelper = new GpsHelper();
        if (!gpsHelper.isHaveCoordinate(connect.getPoolConnexion(), id_Concept, id_theso)) {//si n'est pas aligne
            id_concept = listOfChildrenInConcept.get(0);
            nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), listOfChildrenInConcept.get(0),
                    selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
            try {
                creerAlignAutoParLot(listOfChildrenInConcept.get(0), id_theso, nomduterm, id_lang);
            } catch (Exception e) {
            }
        } else {//si il est déjà aligne
            nextPosition();
        }
    }

    /**
     * c'est va a faire tout les concept
     */
    public void ismiseAJour(String idTheso, String id_lang) {
        ConceptHelper conceptHelper = new ConceptHelper();
        nomduterm = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(), id_concept,
                selectedTerme.getIdTheso(), selectedTerme.getIdlangue());
        try {
            creerAlignAutoParLot(id_concept, idTheso, nomduterm, id_lang);
        } catch (Exception e) {

        }
    }

    /**
     * Recuperation des preferences pour alignement
     *
     * @param idTheso
     * @param id_user
     */
    public void getPreferenceAlignement(String idTheso, int id_user) {
        alignementPreferencesAlignement = new AlignementPreferences();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        alignementPreferencesAlignement = alignmentHelper.getListPreferencesAlignement(
                connect.getPoolConnexion(), idTheso, id_user, id_concept_depart, nodePreference.getId_alignement_source());
    }

    public void doForLot(int id_user) {
        ConceptHelper conceptHelper = new ConceptHelper();
        String id_term = conceptHelper.getLexicalValueOfConcept(connect.getPoolConnexion(),
                id_concept, id_theso, id_langue);
        try {
            doAll(id_concept, id_theso, id_user, id_langue, id_term);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GpsBeans.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * permet d'ecrire dans la bdd les concept que sont déjà tratées
     *
     * @param id_user
     * @return
     */
    public boolean enregister_Des_Progres(int id_user) {
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        if (optionWorkFlow == optionOfAlignement) {
            if (!alignmentHelper.validate_Preferences(connect.getPoolConnexion(), id_theso, id_user,
                    id_concept_depart, listConceptTrates, selectedTerme.alignementSource.getId())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Ne peux pas faire uptdate de preferences"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("alig.ok") + " :", ""));
            }
        }
        return true;
    }

    public void onRowSelect(SelectEvent event) {

        AlignmentHelper alignmentHelper = new AlignmentHelper();
        GpsHelper gpsHelper = new GpsHelper();
        GpsPreferences gpsPreferences;
        gpsPreferences = gpsHelper.getGpsPreferences(connect.getPoolConnexion(), id_theso, id_user1, ((AlignementSource) event.getObject()).getId());
        integrerTraduction = gpsPreferences.isGps_integrertraduction();
        alignementAutomatique = gpsPreferences.isGps_alignementautomatique();
        reemplacerTraduction = gpsPreferences.isGps_reemplacertraduction();
        //   alignement_source = "" + gpsPreferences.getId_alignement_source();
        for (AlignementSource alignementSource : alignementSources) {
            if (((AlignementSource) event.getObject()).getId() == alignementSource.getId()) {
                alignement_source = alignementSource;
            }
        }
        selectedAlignement = alignement_source.getRequete();

    }

    public void recuperatePreferences() {
        if (!selectedAlignement.isEmpty()) {
            GpsHelper gpsHelper = new GpsHelper();
            GpsPreferences gpsPreferences;
            for (AlignementSource alignementSource : alignementSources) {
                if (alignementSource.getSource().equals(selectedAlignement)) {
                    alignement_source = alignementSource;
                }
            }
            gpsPreferences = gpsHelper.getGpsPreferences(connect.getPoolConnexion(), id_theso, id_user1, alignement_source.getId());
            integrerTraduction = gpsPreferences.isGps_integrertraduction();
            alignementAutomatique = gpsPreferences.isGps_alignementautomatique();
            reemplacerTraduction = gpsPreferences.isGps_reemplacertraduction();
        }
    }

    ///////////////GET & SET////////////////////////////
    public MapModel getGeoModel() {
        return geoModel;
    }

    public void setGeoModel(MapModel geoModel) {
        this.geoModel = geoModel;
    }

    public String getCenterGeoMap() {
        return centerGeoMap;
    }

    public void setCenterGeoMap(String centerGeoMap) {
        this.centerGeoMap = centerGeoMap;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getCoordennees() {
        return coordennees;
    }

    public void setCoordennees(String coordennees) {
        this.coordennees = coordennees;
    }

    public ArrayList<AlignementSource> getAlignementSources() {
        return alignementSources;
    }

    public void setAlignementSources(ArrayList<AlignementSource> alignementSources) {
        this.alignementSources = alignementSources;
    }

    public ArrayList<NodeAlignment> getListAlignValues() {
        return listAlignValues;
    }

    public void setListAlignValues(ArrayList<NodeAlignment> listAlignValues) {
        this.listAlignValues = listAlignValues;
    }

    public String getSelectedAlignement() {
        return selectedAlignement;
    }

    public void setSelectedAlignement(String selectedAlignement) {

        this.selectedAlignement = selectedAlignement;
    }

    public ArrayList<AlignementSource> getListeAlignementSources() {
        return listeAlignementSources;
    }

    public void setListeAlignementSources1(String idtheso, int id_user) {
        id_theso = idtheso;
        id_user1 = id_user;
        GpsHelper gpsHelper = new GpsHelper();
        alignementSources = gpsHelper.getAlignementSource(connect.getPoolConnexion());
        if (!alignementSources.isEmpty()) {
            selectedAlignement = alignementSources.get(0).getSource();
        }
    }

    public CurrentUser getTheUser() {
        return theUser;
    }

    public void setTheUser(CurrentUser theUser) {
        this.theUser = theUser;
    }

    public NodeAlignment getAlignment_choisi() {
        return alignment_choisi;
    }

    public void setAlignment_choisi(NodeAlignment alignment_choisi) {
        this.alignment_choisi = alignment_choisi;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public boolean isIntegrerTraduction() {
        return integrerTraduction;
    }

    public void setIntegrerTraduction(boolean integrerTraduction) {
        this.integrerTraduction = integrerTraduction;
    }

    public boolean isReemplacerTraduction() {
        return reemplacerTraduction;
    }

    public void setReemplacerTraduction(boolean reemplacerTraduction) {
        this.reemplacerTraduction = reemplacerTraduction;
    }

    public boolean isAlignementAutomatique() {
        return alignementAutomatique;
    }

    public void setAlignementAutomatique(boolean alignementAutomatique) {
        this.alignementAutomatique = alignementAutomatique;
    }

    public AlignementSource getAlignementPreferences() {
        return alignementPreferences;
    }

    public void setAlignementPreferences(AlignementSource alignementPreferences) {
        this.alignementPreferences = alignementPreferences;
    }

    public ArrayList<Languages_iso639> getListLanguesInTheso() {
        return listLanguesInTheso;
    }

    public void setListLanguesInTheso(ArrayList<Languages_iso639> listLanguesInTheso) {
        this.listLanguesInTheso = listLanguesInTheso;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public String getId_concept() {
        return id_concept;
    }

    public void setId_concept(String id_concept) {
        this.id_concept = id_concept;
    }

    public String getId_theso() {
        return id_theso;
    }

    public void setId_theso(String id_theso) {
        this.id_theso = id_theso;
    }

    public String getNomduterm() {
        return nomduterm;
    }

    public void setNomduterm(String nomduterm) {
        this.nomduterm = nomduterm;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isMettreAJour() {
        return mettreAJour;
    }

    public void setMettreAJour(boolean mettreAJour) {
        this.mettreAJour = mettreAJour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErreur() {
        return erreur;
    }

    public void setErreur(String erreur) {
        this.erreur = erreur;
    }

    public ArrayList<String> getListOfChildrenInConcept() {
        return listOfChildrenInConcept;
    }

    public void setListOfChildrenInConcept(ArrayList<String> listOfChildrenInConcept) {
        this.listOfChildrenInConcept = listOfChildrenInConcept;
    }

    public String getUriSelection() {
        return uriSelection;
    }

    public void setUriSelection(String uriSelection) {
        this.uriSelection = uriSelection;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public String getId_langue() {
        return id_langue;
    }

    public void setId_langue(String id_langue) {
        this.id_langue = id_langue;
    }

    public int getAlignement_id_type() {
        return alignement_id_type;
    }

    public void setAlignement_id_type(int alignement_id_type) {
        this.alignement_id_type = alignement_id_type;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public Map<Integer, String> getOptions() {
        return options;
    }

    public void setOptions(Map<Integer, String> options) {
        this.options = options;
    }

    public static int getOptionAllBranch() {
        return optionAllBranch;
    }

    public static void setOptionAllBranch(int optionAllBranch) {
        GpsBeans.optionAllBranch = optionAllBranch;
    }

    public static int getOptionNonAligned() {
        return optionNonAligned;
    }

    public static void setOptionNonAligned(int optionNonAligned) {
        GpsBeans.optionNonAligned = optionNonAligned;
    }

    public static int getOptionWorkFlow() {
        return optionWorkFlow;
    }

    public static void setOptionWorkFlow(int optionWorkFlow) {
        GpsBeans.optionWorkFlow = optionWorkFlow;
    }

    public int getOptionOfAlignement() {
        return optionOfAlignement;
    }

    public void setOptionOfAlignement(int optionOfAlignement) {
        this.optionOfAlignement = optionOfAlignement;
    }

    public AlignementPreferences getAlignementPreferencesAlignement() {
        return alignementPreferencesAlignement;
    }

    public void setAlignementPreferencesAlignement(AlignementPreferences alignementPreferencesAlignement) {
        this.alignementPreferencesAlignement = alignementPreferencesAlignement;
    }

    public String getId_concept_depart() {
        return id_concept_depart;
    }

    public void setId_concept_depart(String id_concept_depart) {
        this.id_concept_depart = id_concept_depart;
    }

    public int getId_user1() {
        return id_user1;
    }

    public void setId_user1(int id_user1) {
        this.id_user1 = id_user1;
    }

    public AlignementSource getAlignement_source() {
        return alignement_source;
    }

    public void setAlignement_source(AlignementSource alignement_source) {
        this.alignement_source = alignement_source;
    }

}
