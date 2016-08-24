package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.datas.Concept;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.SearchHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePermute;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;

@ManagedBean(name = "search", eager = true)
@SessionScoped

public class SearchBean implements Serializable {
    private String entry;
    private int typeValueSearch;
    private String idGroup;
    private int typeSearch = 1;
    private int startByOrContain = 1;
    private boolean withNote = false;
    private boolean onlyNote = false;
    private boolean onlyNotation = false;    
    private String langue;
    private int nbRes;
    ArrayList<NodeSearch> result1 = new ArrayList<>();
    ArrayList<NodePermute> result2 = new ArrayList<>();
    private String idInfo;
    
    @ManagedProperty(value = "#{theso}")
    private SelectedThesaurus theso;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    public void research() {
        result1 = new ArrayList<>();
        result2 = new ArrayList<>();
        if (typeSearch == 1) { // ALPHABETIQUE/HIERARCHIQUE
            if (typeValueSearch == 0) { // Terme
                // recherche commencant par startByOrContain = 2
                // recherche contient startByOrContain = 1
                
                if(onlyNote) {
                    result1 = new SearchHelper().searchNote(connect.getPoolConnexion(), entry, langue.trim(), theso.getThesaurus().getId_thesaurus(), idGroup,
                    startByOrContain);
                }
                else {
                    if(onlyNotation) {
                        result1 = new SearchHelper().searchNotation(connect.getPoolConnexion(), entry, langue.trim(), theso.getThesaurus().getId_thesaurus(), idGroup
                        );
                    }
                    else {
                        result1 = new SearchHelper().searchTerm(connect.getPoolConnexion(), entry, langue.trim(), theso.getThesaurus().getId_thesaurus(), idGroup,
                                startByOrContain, withNote);                        
                    }
                }
               
            } else if(typeValueSearch == 1) { // idC
                result1 = new SearchHelper().searchIdConcept(connect.getPoolConnexion(), entry, theso.getThesaurus().getId_thesaurus(), langue.trim());
            }
            nbRes = result1.size();
        } else if (typeSearch == 2) { // PERMUTEE
            if (typeValueSearch == 0) { // Terme
                if (idGroup == null || idGroup.equals("")) {
                    result2 = new SearchHelper().getListPermute(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(), langue.trim(), entry);
                   // result2.addAll(new SearchHelper().getListPermuteNonPreferredTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(), langue.trim(), entry));
                    
                } else {
                    result2 = new SearchHelper().getListPermute(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(), langue.trim(), entry, idGroup);
                //    result2.addAll(new SearchHelper().getListPermuteNonPreferredTerm(connect.getPoolConnexion(), theso.getThesaurus().getId_thesaurus(), langue.trim(), entry, idGroup));
                }
                nbRes = result2.size();
            } else if(typeValueSearch == 1) { // idC
                typeSearch = 1;
                result1 = new SearchHelper().searchIdConcept(connect.getPoolConnexion(), entry, theso.getThesaurus().getId_thesaurus());
                nbRes = result1.size();
            }
        }
        if(!langue.isEmpty()) {
            if (!langue.trim().equals(theso.getThesaurus().getLanguage())) {
                theso.getThesaurus().setLanguage(langue.trim());
                theso.update();
            }
        }
        for(NodeSearch ns : result1) {
            String temp = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), ns.getIdGroup(), theso.getThesaurus().getId_thesaurus(), theso.getThesaurus().getLanguage());
            ns.setGroupLabel(temp);
        }
        theso.getVue().setOnglet(2);
    }
    
    public ArrayList<ArrayList<String>> getHierachi(String idC) {
        ArrayList<String> first = new ArrayList<>();
        first.add(idC);
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        if(connect.getPoolConnexion() != null) {
            paths = new ConceptHelper().getPathOfConcept(connect.getPoolConnexion(), idC, theso.getThesaurus().getId_thesaurus(), first, paths);
        }
        return paths;
    }
    
    public String getNom(String id) {
        String nom;
        
        ConceptHelper ch = new ConceptHelper();
        Concept c = ch.getThisConcept(connect.getPoolConnexion(), id, theso.getThesaurus().getId_thesaurus());
        if(c == null) {
            nom = new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), id, theso.getThesaurus().getId_thesaurus(), theso.getThesaurus().getLanguage());
        } else {
            nom = ch.getLexicalValueOfConcept(connect.getPoolConnexion(), id, theso.getThesaurus().getId_thesaurus(), theso.getThesaurus().getLanguage());
        }
        
        return nom;
    }
    
    public String getValueGroup(String idG) {
        return new GroupHelper().getLexicalValueOfGroup(connect.getPoolConnexion(), idG, theso.getThesaurus().getId_thesaurus(), theso.getThesaurus().getLanguage());
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public int getTypeSearch() {
        return typeSearch;
    }

    public void setTypeSearch(int typeSearch) {
        this.typeSearch = typeSearch;
    }

    public int getStartByOrContain() {
        return startByOrContain;
    }

    public void setStartByOrContain(int startByOrContain) {
        this.startByOrContain = startByOrContain;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public ArrayList<NodeSearch> getResult1() {
        return result1;
    }

    public void setResult1(ArrayList<NodeSearch> result1) {
        this.result1 = result1;
    }

    public SelectedThesaurus getTheso() {
        return theso;
    }

    public void setTheso(SelectedThesaurus theso) {
        this.theso = theso;
    }

    public String getIdInfo() {
        return idInfo;
    }

    public void setIdInfo(String idInfo) {
        this.idInfo = idInfo;
    }

    public int getTypeValueSearch() {
        return typeValueSearch;
    }

    public void setTypeValueSearch(int typeValueSearch) {
        this.typeValueSearch = typeValueSearch;
    }

    public ArrayList<NodePermute> getResult2() {
        return result2;
    }

    public void setResult2(ArrayList<NodePermute> result2) {
        this.result2 = result2;
    }

    public int getNbRes() {
        return nbRes;
    }

    public void setNbRes(int nbRes) {
        this.nbRes = nbRes;
    }

    public boolean isWithNote() {
        return withNote;
    }

    public void setWithNote(boolean withNote) {
        this.withNote = withNote;
    }

    public boolean isOnlyNote() {
        return onlyNote;
    }

    public void setOnlyNote(boolean onlyNote) {
        this.onlyNote = onlyNote;
    }
    

    public boolean isOnlyNotation() {
        return onlyNotation;
    }

    public void setOnlyNotation(boolean onlyNotation) {
        this.onlyNotation = onlyNotation;
    }
    
    
    
}
