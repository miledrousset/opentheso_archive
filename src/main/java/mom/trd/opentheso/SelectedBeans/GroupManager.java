/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;

/**
 *
 * @author miled.rousset
 */

@ManagedBean(name = "groupManager", eager = true)
@SessionScoped

public class GroupManager implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
      
    
    private String notation;

        
    public GroupManager() {
    }      
    
    @PostConstruct
    public void initConf() {
    }
  
    
    public void init() {
        notation = "";
    }
    
    
    /**
     * Création d'un domaine avec mise à jour dans l'arbre
     *
     * @param idTheso
     * @param idLang
     * @param codeTypeGroup
     * @param titleGroup
     * @param nodePreference
     * @param idUser
     * @return
     */
    //   private String typeDom;
    public String addGroup(String idTheso, 
            String idLang,
            String codeTypeGroup,
            String titleGroup,
            NodePreference nodePreference,
            int idUser) {

        String idGroup = null;
        NodeGroup nodeGroup = new NodeGroup();
        if (titleGroup.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", langueBean.getMsg("theso.error7")));
        } else {
            nodeGroup.setLexicalValue(titleGroup);
            nodeGroup.setIdLang(idLang);
            nodeGroup.getConceptGroup().setIdthesaurus(idTheso);
            nodeGroup.getConceptGroup().setNotation(notation);

            if (codeTypeGroup.isEmpty()) {
                codeTypeGroup = "MT";
            }
            nodeGroup.getConceptGroup().setIdtypecode(codeTypeGroup);

            GroupHelper groupHelper = new GroupHelper();
            groupHelper.setNodePreference(nodePreference);
            
            idGroup = groupHelper.addGroup(connect.getPoolConnexion(),
                    nodeGroup,
                    idUser);
            if(idGroup == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", titleGroup + " " + langueBean.getMsg("group.errorCreate")));
                return null;
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :",
                titleGroup + " " + langueBean.getMsg("theso.info1.2")));
        return idGroup;
    }

    /**
     * permet d'ajouter un sous groupe avec un type défini, le groupe père doit
     * exister.Le sous-groupe prend le même type que le père
     * On ajoute aussi la notation 
     *
     * @param idGroupFather
     * @param idTheso
     * @param idLang
     * @param codeTypeGroupFather
     * @param titleGroupSubGroup
     * @param nodePreference
     * @param idUser
     * @return 
     */
    public boolean addSubGroup(
            String idGroupFather,
            String idTheso,
            String idLang,
            String codeTypeGroupFather,
            String titleGroupSubGroup,
            NodePreference nodePreference,
            int idUser) {
        // typeDom = "";
        //si on a bien selectioner un group
      //  String idGroup = tree.getSelectedTerme().getIdC();
        GroupHelper groupHelper = new GroupHelper();
        
        if (groupHelper.isIdOfGroup(connect.getPoolConnexion(), idGroupFather, idTheso)) {
            String idSubGroup = addGroup(
                                idTheso, 
                                idLang,
                                codeTypeGroupFather,
                                titleGroupSubGroup,
                                nodePreference,
                                idUser);
            if(idSubGroup == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", titleGroupSubGroup + " " + langueBean.getMsg("group.errorCreate")));
                return false;
            }        
            if(!groupHelper.addSubGroup(connect.getPoolConnexion(), idGroupFather, idSubGroup, idTheso)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", titleGroupSubGroup + " " + langueBean.getMsg("group.errorCreate")));
                return false;
            }
        }
        return true;
    }    
    
    /**
     * permet de changer la notation d'un groupe
     * @param idGroup
     * @param idTheso
     */
    public void changeNotation(String idGroup, String idTheso) {
        GroupHelper groupHelper = new GroupHelper();
        if(!groupHelper.setNotationOfGroup(connect.getPoolConnexion(), notation, idGroup, idTheso)){
            FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, langueBean.getMsg("error") + " :", "Erreur pendant le changement de la notation : " + notation));
            return;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, langueBean.getMsg("info") + " :",
                "Changement OK"));        
    //    reInit();
    //    this.initTree(idThesoSelected, ((MyTreeNode) this.selectedNode).getLangue());

        // reExpand();
    }

    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }

    public Connexion getConnect() {
        return connect;
    }

    public void setConnect(Connexion connect) {
        this.connect = connect;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }


    
    
}
    

