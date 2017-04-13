/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.helper;

import com.zaxxer.hikari.HikariDataSource;
import mom.trd.opentheso.SelectedBeans.DownloadBean;
import mom.trd.opentheso.core.exports.tabulate.ThesaurusDatas;


/**
 * Cette classe permet d'exporter toutes les données d'un thésaurus
 * et les préparer pour un export spécifique (Skos, tabulé ....)
 *
 * @author miled.rousset
 */
public class ExportThesaurus {

    private ThesaurusDatas thesaurusDatas;
    
    public ExportThesaurus() {
    }
    
    public boolean exportAllDatas(HikariDataSource ds, String idThesaurus){
        this.thesaurusDatas = new ThesaurusDatas();
        if(!thesaurusDatas.exportAllDatas(ds, idThesaurus)){
            return false;
        }
        return true;
    }

    public ThesaurusDatas getThesaurusDatas() {
        return thesaurusDatas;
    }
    
    
    
}
