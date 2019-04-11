package mom.trd.opentheso.SelectedBeans;

//import com.hp.hpl.jena.util.OneToManyMap;
import mom.trd.opentheso.bdd.helper.nodes.MyTreeNode;
import java.io.Serializable;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.datas.Languages_iso639;
import mom.trd.opentheso.bdd.datas.Term;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.AlignmentHelper;
import mom.trd.opentheso.bdd.helper.CandidateHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.CopyrightHelper;
import mom.trd.opentheso.bdd.helper.FacetHelper;
import mom.trd.opentheso.bdd.helper.GpsHelper;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.ImagesHelper;
import mom.trd.opentheso.bdd.helper.LanguageHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.ThesaurusHelper;
import mom.trd.opentheso.bdd.helper.ToolsHelper;
import mom.trd.opentheso.bdd.helper.VerificationInternet;
import mom.trd.opentheso.bdd.helper.nodes.NodeFacet;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeCandidatValue;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeTraductionCandidat;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.tools.StringPlus;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.jsonld.helper.JsonldHelper;
import skos.SKOSXmlDocument;
import mom.trd.opentheso.core.exports.helper.ExportPrivatesDatas;
import mom.trd.opentheso.core.exports.privatesdatas.LineOfData;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import mom.trd.opentheso.ws.ark.ArkClientRest;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "theso", eager = true)
@SessionScoped

public class SelectedThesaurus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String testNameTheso;
    
    private Thesaurus thesaurus;
    private Thesaurus editTheso;
    private NodeGroup nodeCG;

    private ArrayList<Entry<String, String>> arrayTheso;
    private ArrayList<Entry<String, String>> arrayTrad;
    private ArrayList<Entry<String, String>> arrayFacette;
    private SelectItem[] langues;
    private SelectItem[] languesTheso;
    private List<NodeCandidatValue> filteredCandidats;
    private List<NodeCandidatValue> filteredCandidatsV;
    private List<NodeCandidatValue> filteredCandidatsA;
    private String idEdit;
    private String langueEdit;
    private String valueEdit;

    // Variables URL
    private String idCurl;
    private String idTurl;
    private String idLurl;
    private String idGurl; // id du groupe

    // Variables resourcesBundle
    private String langueSource; // la langue de travail par thésaurus (info de la base de données)
    private String workLanguage; // la langue de travail pour démarrer (info du fichier de configuration
    private String cheminSite;
    private String defaultThesaurusId;
    private String identifierType = "2";

    private NodePreference nodePreference;
    private String version;
    private boolean arkActive;
    private String serverArk;

    // Backup
    private ArrayList<String> tablesList;
    private ArrayList<String> tablesListPrivate;
    private ArrayList<Table> sortirXml;

    private String nomdufichier;
    private StreamedContent file;
    private StreamedContent fileDownload;

    private boolean mesCandidats = false;
    private boolean tousThesos = false;

    private boolean internetConection = false;
    
    // niveaux des classes Beans
    // theso -> newtreeBean -> selectedTerme -> user1
    //   4   ->    3     ->      2        ->   1
    // ordre d'initialisation des beans
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    @ManagedProperty(value = "#{newtreeBean}")
    private NewTreeBean tree;

    @ManagedProperty(value = "#{ssTree}")
    private UnderTree uTree;

    @ManagedProperty(value = "#{selectedCandidat}")
    private SelectedCandidat candidat;

    @ManagedProperty(value = "#{statBean}")
    private StatBean statBean;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{vue}")
    private Vue vue;

    @ManagedProperty(value = "#{conceptbean}")
    private ConceptBean conceptbean;
   
    @ManagedProperty(value = "#{currentUser}")
    private CurrentUser2 currentUser;

    @ManagedProperty(value = "#{selectedTerme}")
    private SelectedTerme selectedTerme;
 
    @ManagedProperty(value = "#{roleOnTheso}")
    private RoleOnThesoBean roleOnTheso;

    @ManagedProperty(value = "#{externalResources}")
    private ExternalResources externalResources;    
    /**
     * ************************************ INITIALISATION
     * *************************************
     */


    /**
     * cette fonction permet de charger le thésaurus suiavnt son Nom 
     * c'est le Nom du thésaurus par defaut ou le nom qu'on a défini dans le fichier de conf. 
     * @param idTheso
     * @param idLang
     */
    public void loadThesaurusFromName(String idTheso, String idLang){
       // if the URL is only for thésaurus 
        if (idTheso == null) return;
        if(idLang == null) return;
                        
                ArrayList<Languages_iso639> temp = new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), idTurl);
                if (temp.isEmpty()) {
                    idCurl = null;
                    idGurl = null;
                    idTurl = null;
                    return;
                } else {
                    boolean lExist = false;
                    for (Languages_iso639 l : temp) {
                        if (l.getId_iso639_1().trim().equals(idLurl)) {
                            lExist = true;
                        }
                    }
                    if (!lExist) {
                        idLurl = temp.get(0).getId_iso639_1().trim();
                    }
                }
                /*   if (new GroupHelper().getThisConceptGroup(connect.getPoolConnexion(), idGurl, idTurl, idLurl) == null) {
                    idCurl = null;
                    idGurl = null;
                    idTurl = null;
                    return;
                }*/
                tree.getSelectedTerme().reInitTerme();

                // Initialisation du thésaurus et de l'arbre
                thesaurus.setId_thesaurus(idTurl);
                thesaurus.setLanguage(idLurl);

                tree.getSelectedTerme().reInitTerme();
                tree.reInit();
                tree.initTree(null, null);
                ThesaurusHelper th = new ThesaurusHelper();

                thesaurus = th.getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
                tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());

                languesTheso = new LanguageHelper().getSelectItemLanguagesOneThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
                roleOnTheso.initUserNodePref(idTurl);
                // Initialisation du terme séléctionné et de l'arbre
                //     NodeGroup nodeGroup = new GroupHelper().getThisConceptGroup(connect.getPoolConnexion(),idGurl, idTurl, idLurl);
                /*
                Concept c = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idCurl, idTurl);
                if (c.isTopConcept()) {
                    type = 2;
                } else {
                    type = 3;
                }*/
                tree.getSelectedTerme().setIdTheso(idTurl);
                tree.getSelectedTerme().setIdlangue(idLurl);

                //   MyTreeNode mTN = new MyTreeNode(type, idGurl, idTurl, idLurl, idGurl, "", null, null, null);
                //   tree.getSelectedTerme().majTerme(mTN);
                //    tree.reExpand();
                idCurl = null;
                idGurl = null;
                idTurl = null;
                idLurl = null;
        
    }
    

    /**
     * récupère la variable URL et affiche le terme qu'elle désigne
     */
    public void preRenderView() {
        // if the URL is for Concept
        if (idCurl != null && idTurl != null) {
            idLurl = Locale.getDefault().toString().substring(0, 2);
            ArrayList<Languages_iso639> temp = new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), idTurl);
            if (temp.isEmpty()) {
                idCurl = null;
                idGurl = null;
                idTurl = null;
                return;
            } else {
                boolean lExist = false;
                for (Languages_iso639 l : temp) {
                    if (l.getId_iso639_1().trim().equals(idLurl)) {
                        lExist = true;
                    }
                }
                if (!lExist) {
                    idLurl = temp.get(0).getId_iso639_1().trim();
                }
            }
            if (new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idCurl, idTurl) == null) {
                idCurl = null;
                idGurl = null;
                idTurl = null;
                return;
            }
            tree.getSelectedTerme().reInitTerme();

            // Initialisation du thésaurus et de l'arbre
            thesaurus.setId_thesaurus(idTurl);
            thesaurus.setLanguage(idLurl);

            tree.getSelectedTerme().reInitTerme();
            tree.reInit();
            tree.initTree(null, null);
            ThesaurusHelper th = new ThesaurusHelper();

            thesaurus = th.getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());

            languesTheso = new LanguageHelper().getSelectItemLanguagesOneThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            roleOnTheso.initUserNodePref(idTurl);

            // Initialisation du terme séléctionné et de l'arbre
            int type;
            Concept c = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idCurl, idTurl);
            if (c.isTopConcept()) {
                type = 2;
            } else {
                type = 3;
            }

            tree.getSelectedTerme().setIdTheso(idTurl);
            tree.getSelectedTerme().setIdlangue(idLurl);

            MyTreeNode mTN = new MyTreeNode(type, idCurl, idTurl, idLurl, c.getIdGroup(), "", null, null, null, null);
            tree.getSelectedTerme().majTerme(mTN);
            tree.reExpand();
            idCurl = null;
            idGurl = null;
            idTurl = null;
            idLurl = null;
            return;
        }
        // if the URL is for Groups 
        if (idGurl != null && idTurl != null) {
            idLurl = Locale.getDefault().toString().substring(0, 2);
            ArrayList<Languages_iso639> temp = new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), idTurl);
            if (temp.isEmpty()) {
                idCurl = null;
                idGurl = null;
                idTurl = null;
                return;
            } else {
                boolean lExist = false;
                for (Languages_iso639 l : temp) {
                    if (l.getId_iso639_1().trim().equals(idLurl)) {
                        lExist = true;
                    }
                }
                if (!lExist) {
                    idLurl = temp.get(0).getId_iso639_1().trim();
                }
            }
            if (new GroupHelper().getThisConceptGroup(connect.getPoolConnexion(), idGurl, idTurl, idLurl) == null) {
                idCurl = null;
                idGurl = null;
                idTurl = null;
                return;
            }
            tree.getSelectedTerme().reInitTerme();

            // Initialisation du thésaurus et de l'arbre
            thesaurus.setId_thesaurus(idTurl);
            thesaurus.setLanguage(idLurl);

            tree.getSelectedTerme().reInitTerme();
            tree.reInit();
            tree.initTree(null, null);
            ThesaurusHelper th = new ThesaurusHelper();

            thesaurus = th.getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());

            languesTheso = new LanguageHelper().getSelectItemLanguagesOneThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            roleOnTheso.initUserNodePref(idTurl);
            // Initialisation du terme séléctionné et de l'arbre
            int type = 1;

            //     NodeGroup nodeGroup = new GroupHelper().getThisConceptGroup(connect.getPoolConnexion(),idGurl, idTurl, idLurl);
            /*
            Concept c = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idCurl, idTurl);
            if (c.isTopConcept()) {
                type = 2;
            } else {
                type = 3;
            }*/
            tree.getSelectedTerme().setIdTheso(idTurl);
            tree.getSelectedTerme().setIdlangue(idLurl);

            MyTreeNode mTN = new MyTreeNode(type, idGurl, idTurl, idLurl, idGurl, "", null, null, null, null);
            tree.getSelectedTerme().majTerme(mTN);
            tree.reExpand();
            idCurl = null;
            idGurl = null;
            idTurl = null;
            idLurl = null;
            return;
        }

        // if the URL is only for thésaurus 
        if (idTurl != null) {
            idLurl = Locale.getDefault().toString().substring(0, 2);
            ArrayList<Languages_iso639> temp = new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), idTurl);
            if (temp.isEmpty()) {
                idCurl = null;
                idGurl = null;
                idTurl = null;
                return;
            } else {
                boolean lExist = false;
                for (Languages_iso639 l : temp) {
                    if (l.getId_iso639_1().trim().equals(idLurl)) {
                        lExist = true;
                    }
                }
                if (!lExist) {
                    idLurl = temp.get(0).getId_iso639_1().trim();
                }
            }
            /*   if (new GroupHelper().getThisConceptGroup(connect.getPoolConnexion(), idGurl, idTurl, idLurl) == null) {
                idCurl = null;
                idGurl = null;
                idTurl = null;
                return;
            }*/
            tree.getSelectedTerme().reInitTerme();

            // Initialisation du thésaurus et de l'arbre
            thesaurus.setId_thesaurus(idTurl);
            thesaurus.setLanguage(idLurl);

            tree.getSelectedTerme().reInitTerme();
            tree.reInit();
            tree.initTree(null, null);
            ThesaurusHelper th = new ThesaurusHelper();

            thesaurus = th.getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());

            languesTheso = new LanguageHelper().getSelectItemLanguagesOneThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            roleOnTheso.initUserNodePref(idTurl);
            // Initialisation du terme séléctionné et de l'arbre
            //     NodeGroup nodeGroup = new GroupHelper().getThisConceptGroup(connect.getPoolConnexion(),idGurl, idTurl, idLurl);
            /*
            Concept c = new ConceptHelper().getThisConcept(connect.getPoolConnexion(), idCurl, idTurl);
            if (c.isTopConcept()) {
                type = 2;
            } else {
                type = 3;
            }*/
            tree.getSelectedTerme().setIdTheso(idTurl);
            tree.getSelectedTerme().setIdlangue(idLurl);

            //   MyTreeNode mTN = new MyTreeNode(type, idGurl, idTurl, idLurl, idGurl, "", null, null, null);
            //   tree.getSelectedTerme().majTerme(mTN);
            //    tree.reExpand();
            idCurl = null;
            idGurl = null;
            idTurl = null;
            idLurl = null;
        }
    }

    /**
     * Constructeur
     */
    public SelectedThesaurus() {
        idCurl = null;
        idGurl = null;
        idTurl = null;
        idLurl = null;
        arrayTrad = new ArrayList<>();

        thesaurus = new Thesaurus();
        editTheso = new Thesaurus();
        nodeCG = new NodeGroup();
    }

    @PostConstruct
    public void initTheso() {
        // récupération de la langue de travail préférée à partir du fichier de conf (péférences.properties)

        if (connect.getPoolConnexion() != null) {
            workLanguage = connect.getWorkLanguage();
            defaultThesaurusId = connect.getDefaultThesaurusId();
            // langueSource = new UserHelper().getPreferenceUser(connect.getPoolConnexion()).getSourceLang();
            roleOnTheso.showListTheso();
        //    arrayTheso = new ArrayList<>(new ThesaurusHelper().getListThesaurus(connect.getPoolConnexion(), connect.getWorkLanguage()).entrySet());
            langues = new LanguageHelper().getSelectItemLanguages(connect.getPoolConnexion());
            //bundlePref.getString("langueSource");
            internetConection = new VerificationInternet().isConected();
        } else {
            arrayTheso = new ArrayList<>();
        }
        StartDefaultThesauriTree();
    }

    /* ******************** EDITION THESO BDD * ******************** */
    /**
     * Cette fonction permet de nettoyer et réorganiser le thésaurus
     *
     * @return
     */
    public boolean reorganizing() {
        if (thesaurus.getId_thesaurus().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error1")));
            return false;
        }
        try {
            ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
            Connection conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);

            // nettoyage des null et d'espaces
            if (!thesaurusHelper.cleaningTheso(conn, thesaurus.getId_thesaurus())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error.BDD")));
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.infoReorganizing")));

            // complète le thésaurus par les relations qui manquent NT ou BT
            reorganizingTheso();
            
            // permet de supprimer les BT pour un TopTerm, c'est incohérent
            removeBTofTopTerm();
            
            // permet de supprimer les relations en boucle (100 -> BT -> 100) ou  (100 -> NT -> 100)ou (100 -> RT -> 100)
            removeLoopRelations();
            
            // permet de supprimer les groupes qui sont orphelins (si un concept appartient à 2 groupes, mais le deuxième groupe
            // ne contient pas ce concept), c'est une erreur de cohérence.
            
       //     removeGroupOrphan();
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SelectedThesaurus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void removeGroupOrphan() {
        if (thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return;
        }
   //     new ToolsHelper().removeGroupOrphan(connect.getPoolConnexion(), thesaurus.getId_thesaurus());        
    }
    
    private void removeBTofTopTerm(){
        if (thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return;
        }
        new ToolsHelper().removeBTofTopTerm(connect.getPoolConnexion(), thesaurus.getId_thesaurus());
    }
    
    private void removeLoopRelations() {
        if (thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return;
        }
        new ToolsHelper().removeLoopRelations(connect.getPoolConnexion(), "BT", thesaurus.getId_thesaurus());
        new ToolsHelper().removeLoopRelations(connect.getPoolConnexion(), "NT", thesaurus.getId_thesaurus());
        new ToolsHelper().removeLoopRelations(connect.getPoolConnexion(), "RT", thesaurus.getId_thesaurus());          
    }
    

    private final int LENGHT_ID_ALPHANUMERIQUE = 10;

    /**
     * Cette fonction remplace tout les id des groupes et concepts du théso Roll
     * back en cas d'erreur
     *
     * @param idTheso
     */
    public void regenAllId(String idTheso) {
        Connection conn = null;
        try {
            conn = connect.getPoolConnexion().getConnection();
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(SelectedThesaurus.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (conn == null) {
            return;
        }
        try {
            ArrayList<String> idGroup = null;

            //group
            try {
                idGroup = regenIdGroup(conn, idTheso);
            } catch (Exception ex) {
                conn.rollback();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id group :", ex.getMessage()));
                throw new Exception("Error while regen id group ");
            }

            //concept
            try {
                regenIdConcept(conn, idTheso, idGroup);
            } catch (Exception ex) {
                conn.rollback();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id concept:", ex.getMessage()));
                throw new Exception("Error while regen id concept ");
            }

            conn.commit();
            maj();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regen id finished"));

        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error :", ex.getMessage()));
        } catch (Exception ex) {

        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
            }
        }

    }

    /**
     * Permet de vérifier si les Identifiants Ark sont valides s'ils n'existent
     * pas, on les ajoutes s'ils existent, on ne fait rien.
     *
     * @param idTheso #MR
     */
    public void regenerateAllArkId(String idTheso) {
        ConceptHelper conceptHelper = new ConceptHelper();
        if (!nodePreference.isUseArk()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ark non activé : ", "Vérifiez les paramètres du thésaurus"));
            return;
        }         
        try {
            ArrayList<String> idGroup = null;

            //group
            /*  try {
                if(!regenArkIdGroup(conn, idTheso)) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                conn.rollback();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id group :", ex.getMessage()));
                throw new Exception("Error while regen id group ");
            }*/
            //concept
            try {
                ArrayList<String> idConcepts = conceptHelper.getAllIdConceptOfThesaurus(connect.getPoolConnexion(), idTheso);
                if (idConcepts == null || idConcepts.isEmpty()) {
                    throw new Exception("No concept in this thesaurus");
                }
//                idConcepts.clear();
//                idConcepts.add("236999");
        //        idConcepts.add("236999");
        //        idConcepts.add("237003");
                conceptHelper.setNodePreference(nodePreference);
                conceptHelper.generateArkId(connect.getPoolConnexion(), idTheso, idConcepts);
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error during generation id:", ex.getMessage()));
                throw new Exception("Error during generation idArk for Concept : ");
            }
            maj();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "The generation of identifiers is complete"));

        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error :", ex.getMessage()));
        } catch (Exception ex) {
        }
    }

    /**
     * Permet de générere les identifiants Ark manquants
     *
     * @param idTheso #MR
     */
    public void generateAllInexistantArkId(String idTheso) {
        ConceptHelper conceptHelper = new ConceptHelper();
        if (!nodePreference.isUseArk()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ark non activé : ", "Vérifiez les paramètres du thésaurus"));
            return;
        }        
        try {
            ArrayList<String> idGroup = null;
            
            //group
            /*  try {
                if(!regenArkIdGroup(conn, idTheso)) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                conn.rollback();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id group :", ex.getMessage()));
                throw new Exception("Error while regen id group ");
            }*/
            //concept
            try {
                ArrayList<String> idConcepts = conceptHelper.getAllIdConceptOfThesaurusWithoutArk(connect.getPoolConnexion(), idTheso);
                if (idConcepts == null || idConcepts.isEmpty()) {
                    throw new Exception("No concept in this thesaurus");
                } else
                    conceptHelper.generateArkId(connect.getPoolConnexion(), idTheso, idConcepts);
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id concept:", ex.getMessage()));
                throw new Exception("Error while regen id concept ");
            }
            maj();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regen id finished"));

        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error :", ex.getMessage()));
        } catch (Exception ex) {
        }
    }
    
    /**
     * Permet de générere les identifiants Handle manquants
     *
     * @param idTheso #MR
     */
    public void generateAllInexistantHandleId(String idTheso) {
        ConceptHelper conceptHelper = new ConceptHelper();
        if (!nodePreference.isUseHandle()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Handle non activé : ", "Vérifiez les paramètres du thésaurus"));
            return;
        }        
        try {
            ArrayList<String> idGroup = null;

            //group
            /*  try {
                if(!regenArkIdGroup(conn, idTheso)) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                conn.rollback();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id group :", ex.getMessage()));
                throw new Exception("Error while regen id group ");
            }*/
            //concept
            try {
                ArrayList<String> idConcepts = conceptHelper.getAllIdConceptOfThesaurusWithoutHandle(connect.getPoolConnexion(), idTheso);
                if (idConcepts == null || idConcepts.isEmpty()) {
                    throw new Exception("No concept in this thesaurus");
                }

                regenerateHAndleIdOfConcepts(idTheso, idConcepts);
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id concept:", ex.getMessage()));
                throw new Exception("Error while regen id concept ");
            }
            maj();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regen id finished"));

        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error :", ex.getMessage()));
        } catch (Exception ex) {
        }
    }    
    
    /**
     * Permet de vérifier si les Identifiants Handle sont valides s'ils n'existent
     * pas, on les ajoutes s'ils exient, on ne fait rien.
     *
     * @param idTheso #MR
     */
    public void regenerateAllHandleId(String idTheso) {
        ConceptHelper conceptHelper = new ConceptHelper();
        if (!nodePreference.isUseHandle()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Handle non activé : ", "Vérifiez les paramètres du thésaurus"));
            return;
        }
        try {
            ArrayList<String> idGroup = null;

            //group
            /*  try {
                if(!regenArkIdGroup(conn, idTheso)) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                conn.rollback();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id group :", ex.getMessage()));
                throw new Exception("Error while regen id group ");
            }*/
            //concept
            try {
                ArrayList<String> idConcepts = conceptHelper.getAllIdConceptOfThesaurus(connect.getPoolConnexion(), idTheso);
                if (idConcepts == null || idConcepts.isEmpty()) {
                    throw new Exception("No concept in this thesaurus");
                }
                regenerateHAndleIdOfConcepts(idTheso, idConcepts);
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while regen id concept:", ex.getMessage()));
                throw new Exception("Error while regen id concept ");
            }
            maj();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regen id finished"));

        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error :", ex.getMessage()));
        } catch (Exception ex) {
        }
    }    

    /**
     * Cette fonction remplace tout les id des groupes du théso
     *
     * @param conn
     * @param idTheso
     * @return la liste des nouveaux id group #MR
     */
    private boolean regenArkIdGroup(Connection conn, String idTheso) throws Exception {

        //récup les concepts
        GroupHelper groupHelper = new GroupHelper();
        ArrayList<String> idGroups = groupHelper.getListIdOfGroup(conn, idTheso);
        if (idGroups == null || idGroups.isEmpty()) {
            throw new Exception("No group in this thesaurus");
        }

        //vérification et génération des nouveaux id Ark
        for (String idGroup : idGroups) {
            // if(!) return false;
        }
        return true;
    }

    /**
     * Cette fonction regenère tous les idHandle des concepts du théso
     * puis met à jour les identifiants Handle
     */
    private boolean regenerateHAndleIdOfConcepts(String idTheso, ArrayList<String> idConcepts) throws Exception {
        //récup les concepts
        ConceptHelper conceptHelper = new ConceptHelper();
        conceptHelper.setNodePreference(nodePreference);
        /*Vérification et génération des nouveaux id Ark*/
        for (String idConcept : idConcepts) {
            if (!conceptHelper.updateIdHandle(connect.getPoolConnexion(),
                    idConcept, idTheso)) {

                throw new Exception("Error Handle :" + conceptHelper.getMessage());
            }
        }
        return true;
    }
    
    /**
     * permet de transformer les identifiants non numériques en numériques
     */
    public void reGenerateAllNonNumericId() {
        ConceptHelper conceptHelper = new ConceptHelper();

        ArrayList<String> listIdConcept = conceptHelper.getAllNonNumericId(connect.getPoolConnexion(), thesaurus.getId_thesaurus());
        for (String idConcept : listIdConcept) {
            if (conceptHelper.setIdConceptToNumeric(connect.getPoolConnexion(),
                     thesaurus.getId_thesaurus(), idConcept)) {
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", "while regenerate id for concept"));
                return;
            }     
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regenerate id finished, total = " +listIdConcept.size()));
    }

    public void reGenerateConceptId(String idConcept) {
        ConceptHelper conceptHelper = new ConceptHelper();

        if (conceptHelper.setIdConceptToNumeric(connect.getPoolConnexion(),
                 thesaurus.getId_thesaurus(), idConcept)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regenerate id finished"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", "while regenerate id for concept"));
        }
    }
    
    /**
     * fonction qui permet de regénéerer l'identifiant ARK d'un Concept 
     * Ajout si n'exite pas
     * Mis à jour s'il n'est plus valide
     * 
     * @param idConcept 
     */
    public void reGenerateConceptArkId(String idConcept) {
        ConceptHelper conceptHelper = new ConceptHelper();
        if(nodePreference.isUseArk()) {
            conceptHelper.setNodePreference(nodePreference);
            try {
                ArrayList<String> idConcepts = new ArrayList<>();
                idConcepts.add(idConcept);
                if(!conceptHelper.generateArkId(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), idConcepts)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", "while regenerate Ark_id for concept"));
                } else
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regenerate Ark_id finished"));
            } catch (Exception ex) {
                Logger.getLogger(SelectedThesaurus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    /**
     * fonction qui permet de regénéerer l'identifiant ARK d'un Group 
     * Ajout s'il n'exite pas
     * Mis à jour s'il n'est plus valide
     * 
     * @param idGroup
     */
    public void reGenerateGroupArkId(String idGroup) {
        GroupHelper groupHelper = new GroupHelper();
        
        if(nodePreference.isUseArk()) {
            groupHelper.setNodePreference(nodePreference);
            if(!groupHelper.addIdArkGroup(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), idGroup, "")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", "while regenerate Ark_id for Group"));
            } else
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info :", "Regenerate Ark_id finished"));
        }
        
    }     

    /**
     * Cette fonction remplace tout les id des groupes du théso
     *
     * @param conn
     * @param idTheso
     * @return la liste des nouveaux id group
     */
    private ArrayList<String> regenIdGroup(Connection conn, String idTheso) throws Exception {

        /*récup les concepts*/
        GroupHelper groupHelper = new GroupHelper();
        ArrayList<String> idgroup = groupHelper.getListIdOfGroup(conn, idTheso);
        if (idgroup == null || idgroup.isEmpty()) {
            throw new Exception("No group in this thesaurus");
        }

        /*génération des nouveaux id*/
        ArrayList<String> newIdGroup = createNewId(idgroup, idgroup.size());

        /*maj des tables*/
        for (int i = 0; i < idgroup.size(); i++) {
            String id = idgroup.get(i);
            String newId = newIdGroup.get(i);
            //table concept_group
            groupHelper.setIdGroup(conn, idTheso, id, newId);
            //table concept_group_concept
            groupHelper.setIdGroupConcept(conn, idTheso, id, newId);
            //table concept_group_historique
            groupHelper.setIdGroupHisto(conn, idTheso, id, newId);
            //table concept_group_label
            groupHelper.setIdGroupLabel(conn, idTheso, id, newId);
            //table concept_group_label_historique
            groupHelper.setIdGroupLabelHisto(conn, idTheso, id, newId);
            //table relation_group
            groupHelper.setIdGroupRelation(conn, idTheso, id, newId);

        }

        return newIdGroup;
    }

    /**
     * Cette fonction remplace tout les id des concepts du théso
     */
    private void regenIdConcept(Connection conn, String idTheso, ArrayList<String> idGroup) throws Exception {

        /*récup les concepts*/
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<String> idConcepts = conceptHelper.getAllIdConceptOfThesaurus(conn, idTheso);
        if (idConcepts == null || idConcepts.isEmpty()) {
            throw new Exception("No concept in this thesaurus");
        }

        /*génération des nouveaux id*/
        ArrayList<String> reservedId = idConcepts;
        reservedId.addAll(idGroup);
        ArrayList<String> newIdConcepts = createNewId(reservedId, idConcepts.size());

        /*maj des tables*/
        NoteHelper noteHelper = new NoteHelper();
        GpsHelper gpsHelper = new GpsHelper();
        ImagesHelper imagesHelper = new ImagesHelper();
        AlignmentHelper alignmentHelper = new AlignmentHelper();
        for (int i = 0; i < idConcepts.size(); i++) {
            String id = idConcepts.get(i);
            String newId = newIdConcepts.get(i);
            //table concept
            conceptHelper.setIdConcept(conn, idTheso, id, newId);
            //table concept_group_concept
            conceptHelper.setIdConceptGroupConcept(conn, idTheso, id, newId);
            //table concept_historique
            conceptHelper.setIdConceptHistorique(conn, idTheso, id, newId);
            //table concept_orphan
            conceptHelper.setIdConceptOrphan(conn, idTheso, id, newId);
            //table gps 
            gpsHelper.setIdConceptGPS(conn, idTheso, id, newId);
            //table hierarchical_relationship
            conceptHelper.setIdConceptHieraRelation(conn, idTheso, id, newId);
            //table hierarchical_relationship_historique
            conceptHelper.setIdConceptHieraRelationHisto(conn, idTheso, id, newId);
            //table note
            noteHelper.setIdConceptNote(conn, idTheso, id, newId);
            //table note_historique
            noteHelper.setIdConceptNoteHisto(conn, idTheso, id, newId);
            //table images 
            imagesHelper.setIdConceptImage(conn, idTheso, id, newId);
            //table concept_fusion
            conceptHelper.setIdConceptFusion(conn, idTheso, id, newId);
            //table preferred_term 
            conceptHelper.setIdConceptPreferedTerm(conn, idTheso, id, newId);
            //table alignement
            alignmentHelper.setIdConceptAlignement(conn, idTheso, id, newId);
        }

    }

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    /**
     *
     * Génere de nouveaux identifiant unique
     */
    private ArrayList<String> createNewId(ArrayList<String> idList, int nbOfId) {

        ArrayList<String> newIdConcepts = new ArrayList<>();
        if (currentUser.getUser() != null
                && roleOnTheso.getNodePreference() != null
                && roleOnTheso.getNodePreference().getIdentifierType() == 2) { // numérique
            int offset = 0;
            for (int i = 0; i < nbOfId; i++) {
                boolean idNotFinded = true;
                while (idNotFinded) {
                    int number = i + offset;
                    String newId = "" + number;
                    if (!idList.contains(newId)) {
                        newIdConcepts.add(newId);
                        idNotFinded = false;
                    } else {
                        offset++;
                    }
                }
            }
        } else { // alpha-numérique
            for (int i = 0; i < nbOfId; i++) {
                String newId;
                boolean idNotFinded = true;
                while (idNotFinded) {
                    newId = randomString(LENGHT_ID_ALPHANUMERIQUE);
                    if (!newIdConcepts.contains(newId) && !idList.contains(newId)) {
                        newIdConcepts.add(newId);
                        idNotFinded = false;
                    }
                }
            }
        }
        return newIdConcepts;
    }

    /**
     * mise à jour après la création d'un thésaurus
     *
     */
    public void updateAfterNewTheso() {
        roleOnTheso.showListTheso();
 //      initRoleOnThisTheso();
    //    arrayTheso = new ArrayList<>(th.getListThesaurus(connect.getPoolConnexion(), langueSource).entrySet());
    //    tree.getSelectedTerme().getUser().updateAuthorizedTheso();
        vue.setCreat(false);
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info1.1") + " " + editTheso.getTitle() + " " + langueBean.getMsg("theso.info1.2")));
    }



    public void getAllTables() {
        ExportPrivatesDatas exportData = new ExportPrivatesDatas();
        tablesList = exportData.showAllTables(connect.getPoolConnexion());
        tablesListPrivate = exportData.showPrivateTables(connect.getPoolConnexion());
    }



    /**
     * ************************************ EDITION TRAD BDD
     * *************************************
     */




    /**
     * ************************************ CANDIDATS
     * *************************************
     */
    /**
     * Récupération de la liste des candidats propre au thésaurus courant
     *
     * @return la liste de candidats
     */
    public List<NodeCandidatValue> listeCandidats() {
        List<NodeCandidatValue> candidats = new ArrayList<>();

        if ((mesCandidats)) {
            if (thesaurus.getId_thesaurus() != null && thesaurus.getLanguage() != null && connect.getPoolConnexion() != null) {
                candidats = new CandidateHelper().getListMyCandidatsWait(connect.getPoolConnexion(), thesaurus.getId_thesaurus(),
                        thesaurus.getLanguage(), currentUser.getUser().getIdUser());
            }
        } else {
            if (thesaurus.getId_thesaurus() != null && thesaurus.getLanguage() != null && connect.getPoolConnexion() != null) {
                candidats = new CandidateHelper().getListCandidatsWaiting(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            }
        }
        filteredCandidats = candidats;
        return candidats;
    }

    /**
     * Récupération de la liste des candidats propre au thésaurus courant
     *
     * @return la liste de candidats
     */
    public List<NodeCandidatValue> listeCdtArchives() {
        List<NodeCandidatValue> candidats = new ArrayList<>();
        if (thesaurus.getId_thesaurus() != null && thesaurus.getLanguage() != null) {
            candidats = new CandidateHelper().getListCandidatsArchives(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        }
        return candidats;
    }

    /**
     * Récupération de la liste des candidats propre au thésaurus courant
     *
     * @return la liste de candidats
     */
    public List<NodeCandidatValue> listeCdtValid() {
        List<NodeCandidatValue> candidats = new ArrayList<>();
        if (thesaurus.getId_thesaurus() != null && thesaurus.getLanguage() != null) {
            candidats = new CandidateHelper().getListCandidatsValidated(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        }
        return candidats;
    }

    /**
     * Récupère la liste des domaines du thésaurus courant
     *
     * @return un tableau de SelectItem contenant les domaine permetant de les
     * afficher dans une liste déroulante
     */
    public SelectItem[] listeDomaine() {
        ArrayList<NodeGroup> lesDomaines = new GroupHelper().getListConceptGroup(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        SelectItem[] laListe = new SelectItem[lesDomaines.size() + 1];
        int i = 1;
        laListe[0] = new SelectItem("", "");
        for (NodeGroup ncg : lesDomaines) {
            laListe[i] = new SelectItem(ncg.getConceptGroup().getIdgroup(), ncg.getLexicalValue() + "(" + ncg.getConceptGroup().getIdgroup() + ")");
            i++;
        }
        return laListe;
    }
    
    /**
     * Création d'un nouveau candidat avec vérification de la valeur en entrée
     */
    public List<NodeCandidatValue> creerCandidat() {
        if (candidat.getValueEdit() == null || candidat.getValueEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error2")));
        } else if (new CandidateHelper().isCandidatExist(connect.getPoolConnexion(), candidat.getValueEdit(), thesaurus.getId_thesaurus(), thesaurus.getLanguage())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error3")));
            vue.setAddCandidat(false);

            String idCandidat = new CandidateHelper().getIdCandidatFromTitle(connect.getPoolConnexion(),
                    new StringPlus().addQuotes(candidat.getValueEdit().trim()), thesaurus.getId_thesaurus());
            if (idCandidat == null) {
                cleanEditCandidat();
                return null;
            } else {
                candidat.getSelected().setIdConcept(idCandidat);
                vue.setAddPropCandidat(true);
                // creerPropCdt();
                return null;
            }
        } else if (new TermHelper().isTermExist(connect.getPoolConnexion(), candidat.getValueEdit(), thesaurus.getId_thesaurus(), thesaurus.getLanguage())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error4")));
        } else {
            String temp = candidat.getValueEdit();
            if (!candidat.newCandidat(thesaurus.getId_thesaurus(), thesaurus.getLanguage())) {
                return null;
            }
            vue.setAddCandidat(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info2.1") + " " + temp + " " + langueBean.getMsg("theso.info2.2")));
        }
        cleanEditCandidat();
        List<NodeCandidatValue> candidats = new ArrayList<>();
        if (thesaurus.getId_thesaurus() != null && thesaurus.getLanguage() != null && connect.getPoolConnexion() != null) {
            candidats = new CandidateHelper().getListCandidatsWaiting(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        }
        return candidats;
    }

    public void creerPropCdt() {
        if (!candidat.newPropCandidat(thesaurus.getLanguage())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("error")));
            cleanEditCandidat();
            return;
        }
        vue.setAddPropCandidat(false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info3")));
        cleanEditCandidat();
    }

    private void cleanEditCandidat() {
        if (candidat != null) {
            candidat.setValueEdit("");
            candidat.setNoteEdit("");
            candidat.setNiveauEdit("");
            candidat.setDomaineEdit("");
        }
    }

    public void creerTradCdt() {
        if (candidat.getValueEdit() == null || candidat.getValueEdit().trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error5")));
        } else {
            for (NodeTraductionCandidat nt : candidat.getInfoCdt().getNodeTraductions()) {
                if (nt.getIdLang().trim().equals(candidat.getLangueEdit().trim())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error6")));
                    return;
                }
            }
            String temp = candidat.getValueEdit();
            if (!candidat.newTradCdt(thesaurus.getId_thesaurus(), thesaurus.getLanguage())) {
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info4.1") + " " + temp + " " + langueBean.getMsg("theso.info4.2")));
        }
        candidat.setValueEdit("");
        candidat.setLangueEdit("");
    }

    /**
     * Met à jour le candidat courant lors d'une sélection dans la table des
     * candidats
     */
    public void majCdt() {
        candidat.maj(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
    }
    
    /**
     * Après une connexion, 
     * permet de récupérer les droits / roles sur le thésaurus en cours 
     * au cas où l'utilisateur actuel n'a pas droit au thésaurus déjà chargé
     * on se repositionne sur le premier thésaurus de l'utilisateur
     */
    public void initRoleOnThisTheso() {
        // idTheso est le nouveau thésaurus qu'on a 
        if(roleOnTheso.getAuthorizedTheso().isEmpty()) {
            thesaurus.setId_thesaurus(null);
            roleOnTheso.setIdTheso(null);
            return;
        }
        if(!roleOnTheso.getAuthorizedTheso().contains(thesaurus.getId_thesaurus())) { // le thésaurus actuel n'est pas dans la liste des thesos de l'utilisateur
            thesaurus.setId_thesaurus(roleOnTheso.getAuthorizedTheso().get(0));
                    // si après connection, le thésaurus actuel n'appartient pas à l'utilisateur,
                    //on se positionne sur le premier thésaurus de l'utilisateur 
        }
        roleOnTheso.setIdTheso(thesaurus.getId_thesaurus());
        roleOnTheso.setUserRoleOnThisTheso();
    }

    /**
     * ************************************ MISE A JOUR
     * *************************************
     */
    /**
     * Met à jour le thésaurus courant lors d'un changement de thésaurus
     */
    public void maj() {
       
        tree.getSelectedTerme().reInitTerme();
        externalResources.init();
        tree.reInit();
        tree.initTree(null, null);
        statBean.reInit();
        ThesaurusHelper th = new ThesaurusHelper();

        initRoleOnThisTheso();
        setPreferenceOfThesaurus();
        if(nodePreference == null) return;
        if (th.getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), nodePreference.getSourceLang()) != null) {
            thesaurus = th.getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), nodePreference.getSourceLang());
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            tree.setIdThesoSelected(thesaurus.getId_thesaurus());
            tree.setDefaultLanguage(thesaurus.getLanguage());
        } else {
            thesaurus.setLanguage("");
        }

        uTree.reInit();
        uTree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        languesTheso = new LanguageHelper().getSelectItemLanguagesOneThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        candidat.maj(thesaurus.getId_thesaurus(), thesaurus.getLanguage());

/*        if (tree != null) {
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        }*/
        vue.setCreat(false);
        vue.setStatTheso(false);//#jm pour la page de statistiques
        vue.setStatCpt(false);//idem

        if (selectedTerme != null) {
            selectedTerme.initTerme();
        }
//        currentUser.setIdTheso(thesaurus.getId_thesaurus());
        if(currentUser.getUser() != null) {
            roleOnTheso.setIdTheso(thesaurus.getId_thesaurus());
   //         roleOnTheso.showListTheso();
        }
      //  tree.getSelectedTerme().getUser().setIdTheso(thesaurus.getId_thesaurus());
       /**modifiaction pour affiché le message pur le drag an drop #JM**/
        
  /*     if( (user.getUser()!= null) && (user.isIsHaveWriteToCurrentThesaurus()) )
       {
            String message="drag & dop activé !";
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("",  "info : " + message) );
       }*/
    }

    /**
     * permet d'initialiser les préférences d'un thésaurus
     */
    public void setPreferenceOfThesaurus(){
        roleOnTheso.initUserNodePref(thesaurus.getId_thesaurus());
        nodePreference = roleOnTheso.getNodePreference();
    }
    
    /**
     * Met à jour le thésaurus courant lors d'un changement de thésaurus
     */
    public void StartDefaultThesauriTree() {
        roleOnTheso.setIdTheso(defaultThesaurusId);
    //    tree.getSelectedTerme().getUser().setIdTheso(thesaurus.getId_thesaurus());
        if (connect.getPoolConnexion() == null) {
            System.err.println("!!!!! Opentheso n'a pas pu se connecter à la base de données 2!!!!!!! ");
            return;
        }
        tree.getSelectedTerme().reInitTerme();
        tree.reInit();
        tree.initTree(null, null);
        statBean.reInit();
        uTree.reInit();

        if (selectedTerme != null) {
            selectedTerme.initTerme();
        }

        ThesaurusHelper thesaurusHelper = new ThesaurusHelper();
        if (thesaurusHelper.isThesaurusExiste(connect.getPoolConnexion(), defaultThesaurusId)) {
            thesaurus = thesaurusHelper.getThisThesaurus(connect.getPoolConnexion(), defaultThesaurusId, workLanguage);
            if (thesaurus == null) {
                return;
            }
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            uTree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            languesTheso = new LanguageHelper().getSelectItemLanguagesOneThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            candidat.maj(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            vue.setCreat(false);
            setPreferenceOfThesaurus();
            tree.setIdThesoSelected(thesaurus.getId_thesaurus());
            tree.setDefaultLanguage(thesaurus.getLanguage());
        }
        setPreferenceOfThesaurus();
    }

    /**
     * Met à jour le thésaurus courant lors d'un changement de langue
     */
    public void update() {
        tree.getSelectedTerme().reInitTerme();
        tree.reInit();
        tree.initTree(null, null);
        statBean.reInit();
        thesaurus = new ThesaurusHelper().getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        uTree.reInit();
        uTree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        languesTheso = new LanguageHelper().getSelectItemLanguagesOneThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        candidat.maj(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        vue.setCreat(false);

        if (tree != null) {
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        }
    }

    /**
     * *************************************** FACETTES
     * ****************************************
     */
    /**
     * Insère une facette dans le thésaurus
     *
     * @param idParent
     * @param editFacette
     */
    public void creerFacette(String idParent, String editFacette) {
        new FacetHelper().addNewFacet(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), idParent, editFacette, thesaurus.getLanguage(), "");
        uTree.reInit();
        uTree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        vue.setFacette(false);
        vue.setOnglet(2);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", editFacette + " " + langueBean.getMsg("theso.info4.2")));
    }

    /**
     * Supprime une facette
     */
    public void delFacette() {
        new FacetHelper().deleteFacet(connect.getPoolConnexion(), Integer.parseInt(idEdit), thesaurus.getId_thesaurus());
        uTree.reInit();
        uTree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        vue.setFacette(false);
        vue.setOnglet(2);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info5")));
    }

    /**
     * Ajoute une traduction pour une facette
     */
    public void tradFacette() {
        if (valueEdit == null || valueEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error5")));
        } else {
            new FacetHelper().addFacetTraduction(connect.getPoolConnexion(), Integer.parseInt(idEdit), thesaurus.getId_thesaurus(), valueEdit, langueEdit.trim());
            valueEdit = "";
            vue.setFacette(false);
            vue.setOnglet(2);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info6")));
        }
    }

    /**
     * Change une traduction pour une facette
     */
    public void updateFacette() {
        if (valueEdit == null || valueEdit.trim().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error8")));
        } else {
            new FacetHelper().updateFacetTraduction(connect.getPoolConnexion(), Integer.parseInt(idEdit), thesaurus.getId_thesaurus(), thesaurus.getLanguage(), valueEdit);
            valueEdit = "";
            uTree.reInit();
            uTree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            vue.setFacette(false);
            vue.setOnglet(2);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("theso.info7")));
        }
    }

    /**
     * *************************************** AUTRE
     * ****************************************
     */
    /**
     * Récupère le thésaurus courant
     *
     * @return le thésaurus s'il existe, null sinon
     */
    public Thesaurus getThisTheso() {
        String idTemp = "", lTemp = "";
        if (thesaurus == null || thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return null;
        }
        if (thesaurus.getLanguage() != null) {
            lTemp = thesaurus.getLanguage();
        }
        if (thesaurus.getId_thesaurus() != null) {
            idTemp = thesaurus.getId_thesaurus();
        }
        Thesaurus tempT = new ThesaurusHelper().getThisThesaurus(connect.getPoolConnexion(), idTemp, lTemp);
        return tempT;
    }

    /**
     * Cette fonction permet de regénérer les Orphelins
     *
     * @return
     */
    public boolean reorganizingTheso() {
        if (thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return false;
        }
        if (!new ToolsHelper().reorganizingTheso(connect.getPoolConnexion(), thesaurus.getId_thesaurus())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), "error"));
            return false;
        }
        return true;
    }    
    
    /**
     * Cette fonction permet de regénérer les Orphelins
     *
     * @return
     */
    public boolean completeReciprocalRelation() {
        if (thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return false;
        }
        if (!new ToolsHelper().completeReciprocalRelation(connect.getPoolConnexion(), thesaurus.getId_thesaurus())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), "error"));
            return false;
        }
        return true;
    }       
    
    /**
     * Cette fonction permet de regénérer les Orphelins
     *
     * @return
     */
    public boolean completeLackGroup() {
        if (thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return false;
        }
        if (!new ToolsHelper().completeLackGroup(connect.getPoolConnexion(), thesaurus.getId_thesaurus())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), "error"));
            return false;
        }
        return true;
    }    
    
    
    /**
     * Cette fonction permet de regénérer les Orphelins
     *
     * @return
     */
    public boolean regenerateOrphan() {
        if (thesaurus.getLanguage() == null || thesaurus.getId_thesaurus() == null) {
            return false;
        }
        if (!new ToolsHelper().orphanDetect(connect.getPoolConnexion(), thesaurus.getId_thesaurus())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), "error"));
            vue.setRegenerateOrphan(0);
            return false;
        }
        return true;
    }
    /**
     * permet de réorganiser les orphelins
     * @return 
     */
    public boolean orphanReplace() {
    // replacement des orphelins qui ne les sont plus puis les attacher aux concepts.
        if (!new ToolsHelper().orphanReplace(connect.getPoolConnexion(), thesaurus.getId_thesaurus())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error"), "error"));
            vue.setRegenerateOrphan(0);
            return false;
        }
        return true;
    }

    /**
     * Affiche le thésaurus en cour d'édition (pas le thésaurus courant)
     *
     * @param type
     * @param nom
     * @param langue
     * @param id
     */
    public void afficheEditTheso(int type, String nom, String langue, String id) {
        if (type == 1) {
            vue.setCreat(false);
            vue.setEdit(false);
            vue.setTrad(false);
            vue.setLanguage(true);
            editTheso.setId_thesaurus(id);
            editTheso.setTitle(nom);
            remplirArrayTrad(id);
        } else {
            vue.setEdit(true);
            remplirEditTheso(id, langue);
        }
    }

    /**
     * Met à jour le thésaurus courant, l'arbre et le terme courant lors de la
     * sélection de la traduction d'un terme dans le gestionnaire de thésaurus
     *
     * @param id
     * @param l
     * @param type
     */
    public void changeTermeTrad(String id, String l, int type) {
        MyTreeNode mTN = new MyTreeNode(type, id, tree.getSelectedTerme().getIdTheso(), l, tree.getSelectedTerme().getIdDomaine(),
                tree.getSelectedTerme().getTypeDomaine(),
                tree.getSelectedTerme().getIdTopConcept(), null, null, null);
        tree.getSelectedTerme().majTerme(mTN);
        thesaurus = new ThesaurusHelper().getThisThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), l);
        //tree.reInit();
        tree.reExpand();
        vue.setOnglet(0);
    }

    /**
     * Récupération des traductions du thésaurus
     *
     * @return une liste des traductions
     */
    public ArrayList<Languages_iso639> getThisTrad() {
        thesaurus.getLanguage();
        ArrayList<Languages_iso639> languages_iso639s = new ArrayList<>();

        ArrayList<Languages_iso639> languages_iso639s_temp = new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus());

        // on replace la langue sélectionnée en premier
        for (Languages_iso639 languages_iso639 : languages_iso639s_temp) {
            if (languages_iso639.getId_iso639_1().equalsIgnoreCase(thesaurus.getLanguage())) {
                languages_iso639s.add(0, languages_iso639);
            } else {
                languages_iso639s.add(languages_iso639);
            }
        }
        return languages_iso639s;
        //return (new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus()));
    }

    /**
     * Récupération des traductions du thésaurus
     *
     * @return une liste des traductions
     */
    public ArrayList<Languages_iso639> getTradForSearch() {
        thesaurus.getLanguage();
        ArrayList<Languages_iso639> languages_iso639s = new ArrayList<>();

        ArrayList<Languages_iso639> languages_iso639s_temp = new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus());

        // on replace la langue sélectionnée en premier
        for (Languages_iso639 languages_iso639 : languages_iso639s_temp) {
            if (languages_iso639.getId_iso639_1().equalsIgnoreCase(thesaurus.getLanguage())) {
                languages_iso639s.add(0, languages_iso639);
            } else {
                languages_iso639s.add(languages_iso639);
            }
        }
        Languages_iso639 languages_iso639_all = new Languages_iso639();
        languages_iso639_all.setFrench_name("");
        languages_iso639_all.setEnglish_name("");
        languages_iso639_all.setId_iso639_1("");
        languages_iso639_all.setId_iso639_1("");
        languages_iso639s.add(languages_iso639_all);
        return languages_iso639s;
        //return (new LanguageHelper().getLanguagesOfThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus()));
    }

    public void remplirEditTheso(String id, String langue) {
        editTheso = new ThesaurusHelper().getThisThesaurus(connect.getPoolConnexion(), id, langue);
    }

    public void remplirArrayTrad(String id) {
        arrayTrad = new ArrayList<>(new ThesaurusHelper().getMapTraduction(connect.getPoolConnexion(), id).entrySet());
    }

    /**
     * Création d'un domaine avec mise à jour dans l'arbre
     *
     * @param codeTypeGroup
     * @param titleGroup
     * @return
     */
    //   private String typeDom;
    public String addGroup(String codeTypeGroup, String titleGroup) {

        String idGroup = null;

        if (titleGroup.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error7")));
        } else {
            nodeCG.setLexicalValue(titleGroup);
            nodeCG.setIdLang(thesaurus.getLanguage());
            nodeCG.getConceptGroup().setIdthesaurus(thesaurus.getId_thesaurus());

            //    boolean haveFather = false;
            /*
            if(typeDom == null || typeDom.equals("")){
                 typeDom = getTypeDomainePere();
                 haveFather = true;
            }
            

            if (typeDom != null && haveFather) {
                nodeCG.getConceptGroup().setIdtypecode(typeDom);
            } else {
                nodeCG.getConceptGroup().setIdtypecode(codeTypeGroup);

            }*/
            if (codeTypeGroup.isEmpty()) {
                codeTypeGroup = "MT";
            }
            nodeCG.getConceptGroup().setIdtypecode(codeTypeGroup);

            GroupHelper groupHelper = new GroupHelper();
            groupHelper.setNodePreference(nodePreference);
            
            idGroup = groupHelper.addGroup(connect.getPoolConnexion(),
                    nodeCG,
                    currentUser.getUser().getIdUser());
            if(idGroup == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", titleGroup + " " + langueBean.getMsg("group.errorCreate")));
                return null;
            }
            nodeCG = new NodeGroup();
            vue.setSelectedActionDom(PropertiesNames.noActionDom);
            tree.reInit();
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :", titleGroup + " " + langueBean.getMsg("theso.info1.2")));
        }
        return idGroup;
    }

    /**
     * permet d'ajouter un sous groupe avec un type défini, le groupe père doit
     * exister. Le sous-groupe prend le même type que le père
     *
     * @param codeTypeGroupFather
     * @param titleGroupSubGroup
     */
    public void addSubGroup(String codeTypeGroupFather, String titleGroupSubGroup) {
        // typeDom = "";
        //si on a bien selectioner un group
        String idGroup = tree.getSelectedTerme().getIdC();
        if (new GroupHelper().isIdOfGroup(connect.getPoolConnexion(), idGroup, thesaurus.getId_thesaurus())) {
            String idSubGroup = addGroup(codeTypeGroupFather, titleGroupSubGroup);

            new GroupHelper().addSubGroup(connect.getPoolConnexion(), idGroup, idSubGroup, thesaurus.getId_thesaurus());
            tree.reInit();
            tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        }

    }

    
    /**
     * Permet de supprimer un groupe
     * déprécé
     * #MR
     * /
    public void deleteDomaine() {
        GroupHelper groupHelper = new GroupHelper();
        int idUser = tree.getSelectedTerme().getUser().getUser().getId();
        String temp = nodeCG.getLexicalValue();
        if (!groupHelper.isEmptyDomain(connect.getPoolConnexion(),
                nodeCG.getConceptGroup().getIdgroup(),
                nodeCG.getConceptGroup().getIdthesaurus())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error7")));
            return;
        }
        groupHelper.deleteGroup(connect.getPoolConnexion(),
                nodeCG.getConceptGroup().getIdgroup(),
                nodeCG.getConceptGroup().getIdthesaurus(),
                idUser);
        vue.setSelectedActionDom(PropertiesNames.noActionDom);
        nodeCG = new NodeGroup();
        tree.reInit();
        tree.initTree(thesaurus.getId_thesaurus(), thesaurus.getLanguage());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", temp + " " + langueBean.getMsg("theso.info1.2")));
    }
*/
    /**
     * Vérifie si le thésaurus courant a des traductions
     *
     * @return true s'il en a au moins une, false sinon
     */
    public boolean haveTrad() {
        if (languesTheso == null) {
            return false;
        }
        return (languesTheso.length > 0);
    }

    /**
     * ************************************ GETTERS SETTERS
     * *************************************
     */
    /**
     *
     * @return
     */
    public String getMetaData() {
        if(nodePreference == null) return "";
        
        if (this.tree.getSelectedTerme() != null) {
            if (this.tree.getSelectedTerme().getIdC() != null) {
                if (this.tree.getSelectedTerme().getIdTheso() != null) {
                    // cas d'un domaine ou  Groupe
                    if (tree.getSelectedTerme().getType() == 1) {
                        ExportFromBDD exportFromBDD = new ExportFromBDD();
                        exportFromBDD.setNodePreference(nodePreference);
                        exportFromBDD.setServerArk(serverArk);
                        exportFromBDD.setServerAdress(cheminSite);
                        StringBuffer skos = exportFromBDD.exportThisGroup(
                                connect.getPoolConnexion(),
                                this.tree.getSelectedTerme().getIdTheso(),
                                this.tree.getSelectedTerme().getIdC());

                        JsonldHelper jsonHelper = new JsonldHelper();
                        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
                        StringBuffer jsonLd = jsonHelper.getJsonLdForSchemaOrg(sKOSXmlDocument);
                        if (jsonLd != null) {
                            return jsonLd.toString();
                        } else {
                            return "";
                        }
                    }
                    // cas d'un concept
                    if (tree.getSelectedTerme().getType() == 2 || tree.getSelectedTerme().getType() == 3) {
                        ExportFromBDD exportFromBDD = new ExportFromBDD();
                        exportFromBDD.setNodePreference(nodePreference);
                        exportFromBDD.setServerArk(serverArk);
                        exportFromBDD.setServerAdress(cheminSite);
                        StringBuffer skos = exportFromBDD.exportConcept(
                                connect.getPoolConnexion(),
                                this.tree.getSelectedTerme().getIdTheso(),
                                this.tree.getSelectedTerme().getIdC());
                        if (skos == null) {
                            return "";
                        }

                        JsonldHelper jsonHelper = new JsonldHelper();
                        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
                        if (sKOSXmlDocument == null) {
                            return "";
                        }
                        StringBuffer jsonLd = jsonHelper.getJsonLdForSchemaOrg(sKOSXmlDocument);
                        if (jsonLd != null) {
                            return jsonLd.toString();
                        } else {
                            return "";
                        }
                    }
                }
            }
        }

        // Envoye les domaines (MT)
        if (thesaurus != null && thesaurus.getId_thesaurus() != null) {
            if (!thesaurus.getId_thesaurus().trim().isEmpty()) {
                ExportFromBDD exportFromBDD = new ExportFromBDD();
                exportFromBDD.setNodePreference(nodePreference);
                exportFromBDD.setServerArk(serverArk);
                exportFromBDD.setServerAdress(cheminSite);
                StringBuffer skos = exportFromBDD.exportGroupsOfThesaurus(
                        connect.getPoolConnexion(),
                        thesaurus.getId_thesaurus());

                JsonldHelper jsonHelper = new JsonldHelper();
                SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
                StringBuffer jsonLd = jsonHelper.getJsonLdForSchemaOrgForConceptScheme(sKOSXmlDocument);
                if (jsonLd == null) {
                    return "";
                }
                return jsonLd.toString();
            }
        }
        return "";

        // ancienne version 
/*        if(this.tree.getSelectedTerme() != null ) {
            if(this.tree.getSelectedTerme().getIdC() != null ) {
                if(this.tree.getSelectedTerme().getIdTheso() != null ) {
                    // cas d'un domaine ou  Groupe
                    if(tree.getSelectedTerme().getType() == 1) {
                        ExportFromBDD exportFromBDD = new ExportFromBDD();
                        exportFromBDD.setServerArk(serverArk);
                        exportFromBDD.setServerAdress(cheminSite);
                        StringBuffer skos = exportFromBDD.exportThisGroup(
                                connect.getPoolConnexion(), 
                                this.tree.getSelectedTerme().getIdTheso(),
                                this.tree.getSelectedTerme().getIdC());

                    JsonHelper jsonHelper = new JsonHelper();
                    SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
                    StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
                    return jsonLd.toString();
                }
                // cas d'un concept
                if(tree.getSelectedTerme().getType() == 2 || tree.getSelectedTerme().getType() == 3) {
                    ExportFromBDD exportFromBDD = new ExportFromBDD();
                    exportFromBDD.setServerArk(serverArk);
                    exportFromBDD.setServerAdress(cheminSite);
                    StringBuffer skos = exportFromBDD.exportConcept(
                            connect.getPoolConnexion(), 
                            this.tree.getSelectedTerme().getIdTheso(),
                            this.tree.getSelectedTerme().getIdC());

                        JsonHelper jsonHelper = new JsonHelper();
                        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
                        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
                        return jsonLd.toString();
                    }
                }
            }
        }
        
        // Envoye les domaines (MT)
        if(thesaurus.getId_thesaurus() != null) {
            if(!thesaurus.getId_thesaurus().trim().isEmpty()) {
                ExportFromBDD exportFromBDD = new ExportFromBDD();
                exportFromBDD.setServerArk(serverArk);
                exportFromBDD.setServerAdress(cheminSite);
                StringBuffer skos = exportFromBDD.exportGroupsOfThesaurus(
                        connect.getPoolConnexion(), 
                        thesaurus.getId_thesaurus());

                JsonHelper jsonHelper = new JsonHelper();
                SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
                StringBuffer jsonLd = jsonHelper.getJsonLdForConceptScheme(sKOSXmlDocument);
                if(jsonLd == null) return "";
                return jsonLd.toString();
            }
        }
        return ""; */
    }

    public String getVersion() {
        return version;
    }

    public String getCheminSite() {
        return cheminSite;
    }

    public String getServerArk() {
        return serverArk;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public Thesaurus getThesaurus() {
        return thesaurus;
    }

    public void setThesaurus(Thesaurus thesaurus) {
        this.thesaurus = thesaurus;
    }

    public ArrayList<Entry<String, String>> getArrayTheso() {
        if (!tousThesos) {
            if (connect.getPoolConnexion() != null) {
                /*   Map p = new ThesaurusHelper().getListThesaurus(connect.getPoolConnexion(), langueSource);
                p.put("", "");*/
                if (tree.getSelectedTerme().getUser().getLangSourceEdit() == null || tree.getSelectedTerme().getUser().getLangSourceEdit().equalsIgnoreCase("")) {
                    arrayTheso = new ArrayList<>(new ThesaurusHelper().getListThesaurus(connect.getPoolConnexion(), workLanguage).entrySet());
                } else {
                    arrayTheso = new ArrayList<>(new ThesaurusHelper().getListThesaurus(
                            connect.getPoolConnexion(), tree.getSelectedTerme().getUser().getLangSourceEdit()).entrySet());
                }
            }
        } else {
            arrayTheso = new ArrayList<>(new ThesaurusHelper().getListThesaurusOfUser(
                    connect.getPoolConnexion(),
                    currentUser.getUser().getIdUser(), tree.getSelectedTerme().getUser().getLangSourceEdit()).entrySet());
        }
        return arrayTheso;
    }

    
    public String getCopyright(){
        if(thesaurus == null) return null;
        if(thesaurus.getId_thesaurus() == null) return null;

        CopyrightHelper copyrightHelper = new CopyrightHelper();
        return copyrightHelper.getCopyright(connect.getPoolConnexion(),
                    thesaurus.getId_thesaurus());
    }

    private ArrayList<Entry<String, String>> allTheso;

    public ArrayList<Entry<String, String>> getAllTheso() {
        allTheso = new ArrayList<>(new ThesaurusHelper().getListThesaurusOfAllTheso(connect.getPoolConnexion(), workLanguage).entrySet());
        return allTheso;
    }

    public void setAllTheso(ArrayList<Entry<String, String>> allTheso) {
        this.allTheso = allTheso;
    }

    public void setArrayTheso(ArrayList<Entry<String, String>> arrayTheso) {
        this.arrayTheso = arrayTheso;
    }

    public Thesaurus getEditTheso() {
        return editTheso;
    }

    public void setEditTheso(Thesaurus editTheso) {
        this.editTheso = editTheso;
    }

    public void setVueCreat(boolean creat) {
        vue.setCreat(creat);
        if (creat) {
            editTheso = new Thesaurus();
        }
    }

    public ArrayList<Entry<String, String>> getArrayTrad() {
        return arrayTrad;
    }

    public void setArrayTrad(ArrayList<Entry<String, String>> arraytrad) {
        this.arrayTrad = arraytrad;
    }

    public SelectItem[] getLangues() {
        return langues;
    }

    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public NodeGroup getNodeCG() {
        return nodeCG;
    }

    public void setNodeCG(NodeGroup nodeCG) {
        this.nodeCG = nodeCG;
    }

    public SelectItem[] getLanguesTheso() {
        return languesTheso;
    }

    public void setLanguesTheso(SelectItem[] languesTheso) {
        this.languesTheso = languesTheso;
    }

    public List<NodeCandidatValue> getFilteredCandidats() {
        return filteredCandidats;
    }

    public void setFilteredCandidats(List<NodeCandidatValue> filteredCandidats) {
        this.filteredCandidats = filteredCandidats;
    }

    public List<NodeCandidatValue> getFilteredCandidatsV() {
        return filteredCandidatsV;
    }

    public void setFilteredCandidatsV(List<NodeCandidatValue> filteredCandidatsV) {
        this.filteredCandidatsV = filteredCandidatsV;
    }

    public List<NodeCandidatValue> getFilteredCandidatsA() {
        return filteredCandidatsA;
    }

    public void setFilteredCandidatsA(List<NodeCandidatValue> filteredCandidatsA) {
        this.filteredCandidatsA = filteredCandidatsA;
    }

    public SelectedCandidat getCandidat() {
        return candidat;
    }

    public void setCandidat(SelectedCandidat candidat) {
        this.candidat = candidat;
    }

    public UnderTree getuTree() {
        return uTree;
    }

    public void setuTree(UnderTree uTree) {
        this.uTree = uTree;
    }

    public String getIdCurl() {
        return idCurl;
    }

    public void setIdCurl(String idCurl) {
        this.idCurl = idCurl;
    }

    public String getIdTurl() {
        return idTurl;
    }

    public void setIdTurl(String idTurl) {
        this.idTurl = idTurl;
    }

    public String getIdLurl() {
        return idLurl;
    }

    public void setIdLurl(String idLurl) {
        this.idLurl = idLurl;
    }

    public String getIdGurl() {
        return idGurl;
    }

    public void setIdGurl(String idGurl) {
        this.idGurl = idGurl;
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public StatBean getStatBean() {
        return statBean;
    }

    public void setStatBean(StatBean statBean) {
        this.statBean = statBean;
    }

    public ArrayList<Entry<String, String>> getArrayFacette() {
        if (connect.getPoolConnexion() != null) {

            if (thesaurus == null) {
                return arrayFacette;
            }

            ArrayList<NodeFacet> temp = new FacetHelper().getAllFacetsOfThesaurus(connect.getPoolConnexion(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
            Map<String, String> mapTemp = new HashMap<>();
            Term value;
            for (NodeFacet nf : temp) {
                value = new TermHelper().getThisTerm(connect.getPoolConnexion(), nf.getIdConceptParent(), thesaurus.getId_thesaurus(), thesaurus.getLanguage());
                if (value == null) {
                    mapTemp.put(String.valueOf(nf.getIdFacet()), nf.getLexicalValue() + " (" + nf.getIdConceptParent() + ")");
                } else {
                    mapTemp.put(String.valueOf(nf.getIdFacet()), nf.getLexicalValue() + " (" + value.getLexical_value() + ")");
                }
            }
            arrayFacette = new ArrayList<>(mapTemp.entrySet());
        }
        return arrayFacette;
    }

    public void setArrayFacette(ArrayList<Entry<String, String>> arrayFacette) {
        this.arrayFacette = arrayFacette;
    }

    public String getIdEdit() {
        return idEdit;
    }

    public void setIdEdit(String idEdit) {
        this.idEdit = idEdit;
    }

    public String getLangueEdit() {
        return langueEdit;
    }

    public void setLangueEdit(String langueEdit) {
        this.langueEdit = langueEdit;
    }

    public String getValueEdit() {
        return valueEdit;
    }

    public void setValueEdit(String valueEdit) {
        this.valueEdit = valueEdit;
    }

    public ConceptBean getConceptbean() {
        return conceptbean;
    }

    public void setConceptbean(ConceptBean conceptbean) {
        this.conceptbean = conceptbean;
    }

    public ArrayList<String> getTablesList() {
        return tablesList;
    }

    public void setTablesList(ArrayList<String> tablesList) {
        this.tablesList = tablesList;
    }

    public ArrayList<String> getTablesListPrivate() {
        return tablesListPrivate;
    }

    public void setTablesListPrivate(ArrayList<String> tablesListPrivate) {
        this.tablesListPrivate = tablesListPrivate;
    }

    public ArrayList<Table> getSortirXml() {
        return sortirXml;
    }

    public void setSortirXml(ArrayList<Table> sortirXml) {
        this.sortirXml = sortirXml;
    }

    public ArrayList<String> getValuesOfTables() {
        ArrayList<String> values = new ArrayList<>();
        for (Table tables : sortirXml) {
            for (LineOfData line : tables.getLineOfDatas()) {
                values.add(line.getValue());
            }

        }
        return values;
    }

    public String getNomdufichier() {
        return nomdufichier;
    }

    public void setNomdufichier(String nomdufichier) {
        this.nomdufichier = nomdufichier;
    }

    public boolean isMesCandidats() {
        return mesCandidats;
    }

    public void setMesCandidats(boolean mesCandidats) {
        this.mesCandidats = mesCandidats;
    }

    public boolean isTousThesos() {
        return tousThesos;
    }

    public void setTousThesos(boolean tousThesos) {
        this.tousThesos = tousThesos;
    }

    public boolean isInternetConection() {
        return internetConection;
    }

    public void setInternetConection(boolean internetConection) {
        this.internetConection = internetConection;
    }

    public NewTreeBean getTree() {
        return tree;
    }

    public void setTree(NewTreeBean tree) {
        this.tree = tree;
    }

    public String getLangueSource() {
        return langueSource;
    }

    public void setLangueSource(String langueSource) {
        this.langueSource = langueSource;
    }

    public String getWorkLanguage() {
        return workLanguage;
    }

    public void setWorkLanguage(String workLanguage) {
        this.workLanguage = workLanguage;
    }

    public String getDefaultThesaurusId() {
        return defaultThesaurusId;
    }

    public void setDefaultThesaurusId(String defaultThesaurusId) {
        this.defaultThesaurusId = defaultThesaurusId;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public NodePreference getNodePreference() {
        return nodePreference;
    }

    public void setNodePreference(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }

    public boolean isArkActive() {
        return arkActive;
    }

    public void setArkActive(boolean arkActive) {
        this.arkActive = arkActive;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public SelectedTerme getSelectedTerme() {
        return selectedTerme;
    }

    public void setSelectedTerme(SelectedTerme selectedTerme) {
        this.selectedTerme = selectedTerme;
    }

    public static SecureRandom getRnd() {
        return rnd;
    }

    public static void setRnd(SecureRandom rnd) {
        SelectedThesaurus.rnd = rnd;
    }

    public CurrentUser2 getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser2 currentUser) {
        this.currentUser = currentUser;
    }

    public RoleOnThesoBean getRoleOnTheso() {
        return roleOnTheso;
    }

    public void setRoleOnTheso(RoleOnThesoBean roleOnTheso) {
        this.roleOnTheso = roleOnTheso;
    }

    public String getTestNameTheso() {
        return testNameTheso;
    }

    public void setTestNameTheso(String testNameTheso) {
        this.testNameTheso = testNameTheso;
    }

    public ExternalResources getExternalResources() {
        return externalResources;
    }

    public void setExternalResources(ExternalResources externalResources) {
        this.externalResources = externalResources;
    }

 

    
}
