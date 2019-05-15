/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import mom.trd.opentheso.bdd.helper.nodes.candidat.NodeCandidatValue;
import org.primefaces.context.PrimeFacesContext;

/**
 *
 * @author jm.prudham
 */
@ManagedBean(name = "searchCandidat", eager = true)
@SessionScoped
public class SearchCandidatBean {
    @ManagedProperty(value="#{theso}")
    SelectedThesaurus st;
    @ManagedProperty(value="#{selectedCandidat}")
    SelectedCandidat sc;
    private String textSearch1="";
    private String textSearch2="";
    private String textSearch3="";
    private List<NodeCandidatValue> candidats;
    private List<NodeCandidatValue> candidatsV;
    private List<NodeCandidatValue> candidatsA;
    private List<NodeCandidatValue> filteredCandidats;
    private List<NodeCandidatValue> filteredCandidatsV;
    private List<NodeCandidatValue> filteredCandidatsA;

    /**
     * Creates a new instance of SearchCandidatBean
     */
    public SearchCandidatBean() {
              
    }
    
   /* public void  filter(){
        if(candidats==null||candidatsA==null || candidatsV==null){
            this.candidats=st.listeCandidats();
            this.candidatsA=st.listeCdtArchives();
            this.candidatsV=st.listeCdtValid();
        }
   
       filteredCandidats=getTextSearchIn(this.candidats);
       filteredCandidatsV=getTextSearchIn(this.candidatsV);
       filteredCandidatsA=getTextSearchIn(this.candidatsA);
       
      
    }*/
    public void filter1(){
        if(candidats==null||candidatsA==null || candidatsV==null){
            this.candidats=st.listeCandidats();
            this.candidatsA=st.listeCdtArchives();
            this.candidatsV=st.listeCdtValid();
        }
   
       filteredCandidats=getTextSearchIn(this.candidats,this.textSearch1);
        
    }
    public void filter2(){
         if(candidats==null||candidatsA==null || candidatsV==null){
            this.candidats=st.listeCandidats();
            this.candidatsA=st.listeCdtArchives();
            this.candidatsV=st.listeCdtValid();
        }
   
        
        filteredCandidatsV=getTextSearchIn(this.candidatsV,this.textSearch2);
        
    }
    public void filter3(){
         if(candidats==null||candidatsA==null || candidatsV==null){
            this.candidats=st.listeCandidats();
            this.candidatsA=st.listeCdtArchives();
            this.candidatsV=st.listeCdtValid();
        }
         
         filteredCandidatsA=getTextSearchIn(this.candidatsA,this.textSearch3);
        
    }
    public void init(){
       if(st == null) return;
       this.candidats=st.listeCandidats();
       this.candidatsA=st.listeCdtArchives();
       this.candidatsV=st.listeCdtValid();
         
       filteredCandidats=this.candidats;
       filteredCandidatsV=this.candidatsV;
       filteredCandidatsA=this.candidatsA;
    }

    public String getTextSearch1() {
        return textSearch1;
    }

    public void setTextSearch1(String textSearch1) {
        this.textSearch1 = textSearch1;
    }

    public String getTextSearch2() {
        return textSearch2;
    }

    public void setTextSearch2(String textSearch2) {
        this.textSearch2 = textSearch2;
    }

    public String getTextSearch3() {
        return textSearch3;
    }

    public void setTextSearch3(String textSearch3) {
        this.textSearch3 = textSearch3;
    }

   
    public SelectedThesaurus getSt() {
        return st;
    }

    public void setSt(SelectedThesaurus st) {
        this.st = st;
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

    public List<NodeCandidatValue> getCandidats() {
        return candidats;
    }

    public void setCandidats(List<NodeCandidatValue> candidats) {
        this.candidats = candidats;
    }

    public List<NodeCandidatValue> getCandidatsV() {
        return candidatsV;
    }

    public void setCandidatsV(List<NodeCandidatValue> candidatsV) {
        this.candidatsV = candidatsV;
    }

    public List<NodeCandidatValue> getCandidatsA() {
        return candidatsA;
    }

    public void setCandidatsA(List<NodeCandidatValue> candidatsA) {
        this.candidatsA = candidatsA;
    }

    public SelectedCandidat getSc() {
        return sc;
    }

    public void setSc(SelectedCandidat sc) {
        this.sc = sc;
    }
    
    public void creerCandidat(){
       List<NodeCandidatValue> ret=st.creerCandidat();
       candidats=(ret==null)? candidats : ret;
       refreshView();

    }
    public void refreshView(){
        FacesContext context=FacesContext.getCurrentInstance();
        String viewId=context.getViewRoot().getViewId();
        ViewHandler handler=context.getApplication().getViewHandler();
        UIViewRoot root=handler.createView(context,viewId);
        root.setViewId(viewId);
        context.setViewRoot(root);
        
        
    }
    public void delPropCdt(){
        sc.delPropCdt();
        rendering();
    }
    public void rendering(){
        init();
        refreshView();
    }
    public void toRefus(){
        sc.toRefus();
        rendering();
    }
    public void toValid(){
        sc.toValid();
        rendering();
        
    }
    public void insertCdt(){
        sc.toInsert();
        rendering();
    }
    private List<NodeCandidatValue> getTextSearchIn(List<NodeCandidatValue> candidats,String textSearch) {
       textSearch=(textSearch.isEmpty() ? "" : textSearch );
        List<NodeCandidatValue> ret= new ArrayList<>();
        for(NodeCandidatValue  cdt : candidats){
           
            if(cdt.getValue().matches(".*"+textSearch+".*"))
            {
                ret.add(cdt);
            }
            
        }
        return ret;
        
    }
    
    
}