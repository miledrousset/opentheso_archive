/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.SelectedBeans;

import mom.trd.opentheso.SelectedBeans.Vue;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import mom.trd.opentheso.bdd.datas.Thesaurus;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.StatisticHelper;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.core.exports.privatesdatas.prendrePermuted;

/**
 *
 * @author antonio.perez
 */
@ManagedBean(name = "showPermuted")
        @SessionScoped





public class showPermuted implements Serializable {
    
    @ManagedProperty(value = "#{vue}")
    private Vue vue;
    
    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;
    
    

    private ArrayList<prendrePermuted> statTheso;

    

    public void chooseStat(String idTheso, String idLange) throws SQLException {
        loadStatPermuted(idTheso, idLange);
    }

    public void loadStatPermuted(String idTheso, String idLange) throws SQLException 
    {
        Thesaurus datatheso= new Thesaurus();
        
        String id_theso =datatheso.getId_thesaurus();
        String langue = datatheso.getLanguage();
        statTheso = new ArrayList<>();
        ArrayList<NodeGroup> lng = new GroupHelper().getListConceptGroup2(connect.getPoolConnexion(), idTheso, idLange);
        StatisticHelper sh = new StatisticHelper();
        for (NodeGroup ng : lng) {
            prendrePermuted nst = new prendrePermuted();
            nst.setOrd(ng.getOrde());
            nst.setId_concept(ng.getId_concept());
            nst.setId_group(ng.getId_group());
            nst.setId_theso(idTheso);
            nst.setId_lang(idLange);
            nst.setLexical_value(ng.getLexicalValue());
            nst.setIspreferredterm(ng.isIspreferredterm());
            nst.setOriginal_value(ng.getOriginal_value());

            statTheso.add(nst);
        }
        vue.setStatPermuted(true);
        vue.setStatTheso(false);
        vue.setStatCpt(false);
    }

    public ArrayList<prendrePermuted> getStatTheso() {
        return statTheso;
    }

    public void setStatTheso(ArrayList<prendrePermuted> statTheso) {
        this.statTheso = statTheso;
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

    

}
