package mom.trd.opentheso.SelectedBeans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.StatisticHelper;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatConcept;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatTheso;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "statBean", eager = true)
@SessionScoped

public class StatBean implements Serializable {
    
    private String selectedGroup;
    private ArrayList<String> statGroup;
    private String lang=null;
    private boolean searchFromDate=false;
    private boolean searchFromGroup=false;
    private int limit=500;
    
    
    private int typeStat;
    private int typeDate=1;
    private Date begin;
    private Date end;
    private int nbCpt;
    private ArrayList<NodeStatTheso> statTheso;
    private ArrayList<NodeStatConcept> statConcept;
    private ArrayList<NodeStatConcept> cacheStatConcept=null;
    @ManagedProperty(value = "#{vue}")
    private Vue vue;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    @ManagedProperty(value="#{langueBean}")
    private LanguageBean lb;

    public StatBean() {
        statTheso = new ArrayList<>();
        statConcept = new ArrayList<>();
        
    }
    
    public void reInit() {
        typeStat = 0;
        nbCpt = 0;
        statTheso = new ArrayList<>();
        statConcept = new ArrayList<>();
    }
    
    public void chooseStat(String idTheso, String langue) {
        
      
        if(this.lang!=null){
            langue=lang;
        }else{
            lang=langue;
        }
        if(typeStat == 1) {
            loadStatTheso(idTheso, langue);
        } else if(typeStat == 2) {
           
           loadStatCpt(idTheso);
          
            
            if(searchFromDate && !searchFromGroup){
                //procedSearchDate();
                findCpt(idTheso, langue);
            }
            if(searchFromGroup && !searchFromDate){
                //procedSearchGroup();
                findCptByGroup(idTheso,langue);
            }
            if(!searchFromDate && !searchFromGroup){
                this.loadAllCpt(idTheso, langue);
            }
            if(searchFromDate && searchFromGroup){
                this.findCptByGroupAndByDate(idTheso,langue);
            }
        }
    }
    
    /**
     * #JM
     * Méthode pour avoir les grouep d'un thésaurus
     * @param idTheso
     * @param langue 
     */
    public void getGroupFromThesaurus(String idTheso,String langue){
        ArrayList<NodeGroup> ngs=new GroupHelper().getListConceptGroup(connect.getPoolConnexion(), idTheso, langue);
        statGroup=new ArrayList<>();
        for(NodeGroup ng:ngs){
            this.statGroup.add(ng.getLexicalValue() + "(" + ng.getConceptGroup().getIdgroup() + ")");
        }
    }
    public void loadStatTheso(String idTheso, String langue) {
        statTheso = new ArrayList<>();
        nbCpt = new StatisticHelper().getNbCpt(connect.getPoolConnexion(), idTheso);
        ArrayList<NodeGroup> lng = new GroupHelper().getListConceptGroup(connect.getPoolConnexion(), idTheso, langue);
        Collections.sort(lng);
        StatisticHelper sh = new StatisticHelper();
        for(NodeGroup ng : lng) {
            NodeStatTheso nst = new NodeStatTheso();
            nst.setGroup(ng.getLexicalValue() + "(" + ng.getConceptGroup().getIdgroup() + ")");
            nst.setNbDescripteur(sh.getNbDescOfGroup(connect.getPoolConnexion(), idTheso, ng.getConceptGroup().getIdgroup()));
            nst.setNbNonDescripteur(sh.getNbNonDescOfGroup(connect.getPoolConnexion(), idTheso, ng.getConceptGroup().getIdgroup(), langue));
            int termNonTraduit=nst.getNbDescripteur()-sh.getNbTradOfGroup(connect.getPoolConnexion(), idTheso, ng.getConceptGroup().getIdgroup(), langue);
            nst.setNbNoTrad(termNonTraduit);
            int nombreNote=sh.getNbDefinitionNoteOfGroup(connect.getPoolConnexion(), idTheso, langue, ng.getConceptGroup().getIdgroup());
            nst.setNbNotes(nombreNote);
            statTheso.add(nst);
        }
        vue.setStatTheso(true);
        vue.setStatCpt(false);
        vue.setStatPermuted(false);
        
    }
    
    /**
     * (cette méthode ne doit plus servir actuellement #JM)
     * 
     * @param idTheso 
     */
    public void loadStatCpt(String idTheso) {
        vue.setStatTheso(false);
        vue.setStatCpt(true);
        vue.setStatPermuted(false);
    
    }
    
    public void findCpt(String idTheso, String langue) {
        if(begin==null)begin=new java.sql.Date(0);
        if(end==null)end=new Date();
        if(typeDate==1) {
            statConcept = new StatisticHelper().getStatConceptCreat(connect.getPoolConnexion(), begin.toString(), end.toString(), idTheso, langue,this.limit);
        } else if(typeDate== 2) {
            statConcept = new StatisticHelper().getStatConceptEdit(connect.getPoolConnexion(), begin.toString(), end.toString(), idTheso, langue,this.limit);
        }
    }
    /**
     * 
     * méthode pour charger tous les concepts 
     * @param idTheso 
     * #MR
     * 
     */
    public void loadAllCpt(String idTheso, String idLangue){
        statConcept=new StatisticHelper().getStatConcept(connect.getPoolConnexion(), idTheso, idLangue,this.limit);

    }
    /**
     * méthode pour charger des concepts en filtrant par un groupe
     * #JM
     * @param idTheso
     * @param langue 
     */
    public void findCptByGroup(String idTheso,String langue){
        if(this.selectedGroup==null){
            this.selectedGroup=this.statGroup.get(0);
        }
        String group=this.selectedGroup.split("\\(")[1];
        group=group.replace(")","");
        statConcept=new StatisticHelper().getStatConceptByGroupAndDate(connect.getPoolConnexion(), ""+new java.sql.Date(0), ""+new Date(),"created",idTheso,lang,group,this.limit);
    }
    /**
     * #JM
     * Méthode pour charger les concepts en filtrant d'après un nom de groupe
     * et d'après les dates 
     * @param idTheso
     * @param langue 
     */
    public void findCptByGroupAndByDate(String idTheso,String langue){
        if(this.selectedGroup==null){
            this.selectedGroup=this.statGroup.get(0);
        }
        if(this.begin==null)this.begin=new java.sql.Date(0);
        if(this.end==null)this.end=new Date();
        String group=this.selectedGroup.split("\\(")[1];
        group=group.replace(")","");
        String column= (typeDate==2)? "modified" : "created" ;
        statConcept=new StatisticHelper().getStatConceptByGroupAndDate(connect.getPoolConnexion(),begin.toString(),end.toString(),column,idTheso,lang,group,limit);
    }
   
   /**
    * #Jm
    * fonction pour charger les ressources de la vue
    * @param idTheso
    * @param langue 
    */
    public void managedVue(String idTheso,String langue){
        getGroupFromThesaurus(idTheso,langue);
      
    }
    
    /**
     * #jm
     * Méthode pour écrire un e ligne dans un buffer, 
     * @param fw
     * @param list
     * @throws IOException 
     */
    public void writeLine(StringBuffer fw, ArrayList<String> list) throws IOException{
        String separator="\t";
        StringBuilder sb=new StringBuilder();
        for(String str:list){
            sb.append(str).append(separator);
        }
        sb.append("\n");
        fw.append(sb.toString());
    }
    
    /**
     * #JM
     * Méthode pour créer un fichier télécharger
     * soit un fichier pour les statistiques du thesaurus soit pour les 
     * statistiques concepts
     * @param idTheso
     * @return 
     */
    public StreamedContent creatCSVFile(String idTheso){
      
        StringBuffer sbr=new StringBuffer();
       try{
         
        
        //ajou du titre au documen CSV
        ArrayList<String> line=new ArrayList<>();
        line.add("thesaurus : "+idTheso);
        line.add(lang.toUpperCase());
        line.add(new Date().toString());
        if(this.typeStat==1)line.add(lb.getMsg("stat.forTheso"));
        if(this.typeStat==2){
            line.add(lb.getMsg("stat.forConcept"));
            if(searchFromDate){
                line.add(lb.getMsg("stat.opt3")+" : "+begin);
                line.add(lb.getMsg("stat.opt4")+" : "+end);
                line.add((typeDate==1)? lb.getMsg("stat.opt2.1") : lb.getMsg("stat.opt2.2") );
            }
            if(searchFromGroup)line.add(lb.getMsg("stat.group")+" : "+this.selectedGroup);
        }
        writeLine(sbr,line);
        line=new ArrayList<>();
        //ajout entête du tableau
        if(this.typeStat==1){
        
             line.add(lb.getMsg("stat.statTheso1"));
             line.add(lb.getMsg("stat.statTheso2"));
             line.add(lb.getMsg("stat.statTheso3"));
             line.add(lb.getMsg("stat.statTheso4"));
             line.add(lb.getMsg("stat.statTheso5"));
        }
        if(this.typeStat==2){
            line.add(lb.getMsg("stat.statCpt1"));
            line.add(lb.getMsg("stat.idC"));
            line.add(lb.getMsg("stat.statCpt2"));
            line.add(lb.getMsg("stat.statCpt3"));
            line.add(lb.getMsg("stat.statCpt4"));  
        }
        writeLine(sbr,line);
        
        if(this.typeStat==1){
       //on ajoute les valeurs du statTheso
       
            for(NodeStatTheso nst :this.statTheso){
                 line=new ArrayList<>();
                 line.add(nst.getGroup());
                 line.add(""+nst.getNbDescripteur());
                 line.add(""+nst.getNbNonDescripteur());
                 line.add(""+nst.getNbNoTrad());
                 line.add(""+nst.getNbNotes());
                 writeLine(sbr,line);
             }
        }
        if(this.typeStat==2){
            for(NodeStatConcept nsc :this.statConcept){
                line=new ArrayList<>();
                line.add(nsc.getValue());
                line.add(nsc.getIdConcept());
                line.add(""+nsc.getDateCreat());
                line.add(""+nsc.getDateEdit());
                line.add(nsc.getGroup());
                writeLine(sbr,line);
            }
        }
       
       }catch(IOException e){
           System.out.println("erreur pendant l'écriture du fichier"+e );
           return null;
        }
       
       InputStream stream;
       StreamedContent file = null;
       try{
           stream=new ByteArrayInputStream(sbr.toString().getBytes("UTF-8"));
           String name_file=(typeStat==1)?"downloadedCsv_Thesaurus_stat.csv" : "downloadedCsv_concept_stat.csv";
           file=new DefaultStreamedContent(stream, "text/csv",name_file);
       }
       catch(UnsupportedEncodingException e){
           System.out.println("erreur création inputStream "+e);
           return null;
       }
       return  file;
    }
    public int getNbCpt() {
        return nbCpt;
    }

    public void setNbCpt(int nbCpt) {
        this.nbCpt = nbCpt;
    }
    
    public Vue getVue() {
        return vue;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public ArrayList<NodeStatTheso> getStatTheso() {
        return statTheso;
    }

    public void setStatTheso(ArrayList<NodeStatTheso> statTheso) {
        this.statTheso = statTheso;
    }

    public ArrayList<NodeStatConcept> getStatConcept() {
        return statConcept;
    }

    public void setStatConcept(ArrayList<NodeStatConcept> statConcept) {
        this.statConcept = statConcept;
    }

    public int getTypeStat() {
        return typeStat;
    }

    public void setTypeStat(int typeStat) {
        this.typeStat = typeStat;
    }

    public int getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(int typeDate) {
        if(typeDate==0){
            return;
        }
        this.typeDate = typeDate;
    }

  
    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        if(begin==null){
            return;
        }
        this.begin = begin;
      
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        if(end==null){
            return;
        }
        this.end = end;
     
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isSearchFromDate() {
        return searchFromDate;
    }

    public void setSearchFromDate(boolean searchFromDate) {
        this.searchFromDate = searchFromDate;
    }

    public boolean isSearchFromGroup() {
        return searchFromGroup;
    }

    public void setSearchFromGroup(boolean searchFromGroup) {
        this.searchFromGroup = searchFromGroup;
    }

    public String getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(String sele) {
        
       
        if(sele==null || sele.equals("") || sele.isEmpty() || sele.contains("javax.faces.event.AjaxBehaviorEvent")){
            return;
        }
        this.selectedGroup = sele;
       
    }

    public ArrayList<String> getStatGroup() {
        return statGroup;
    }

    public void setStatGroup(ArrayList<String> statGroup) {
        this.statGroup = statGroup;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public LanguageBean getLb() {
        return lb;
    }

    public void setLb(LanguageBean lb) {
        this.lb = lb;
    }
    
    
    
    
    
}
