/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.exports.tabulate.TabulateDocument;
import mom.trd.opentheso.core.imports.tabulate.ImportTabuleIntoBDD;
import mom.trd.opentheso.core.imports.tabulate.ReadFileTabule;

/**
 *
 * @author miled.rousset
 */
public class ImportTabulateHelper {

    
    private ArrayList<String> fields;
    private ArrayList<TabulateDocument> tabulateDocuments;
    
    public ImportTabulateHelper() {
    }
    
    /**
     * Permet de lire un fichier tabulé et charger les champs et les données
     * dans variables prévues dans la classe.
     * Si ca se passe mal, la fonction renvoie un false
     * 
     * @param path
     * @param colonneSeparate
     * @param fieldSeparate
     * @param subfieldSeparate
     * @param formatDate
     * @return true or false
     */
    public boolean readFile(InputStream path,
            String colonneSeparate,
            String fieldSeparate,
            String subfieldSeparate,
            String formatDate){
        
        ReadFileTabule readFileTabule = new ReadFileTabule();
        try {
            readFileTabule.setReadFile(
                    path,
                    colonneSeparate,
                    fieldSeparate,
                    subfieldSeparate,
                    formatDate);
            
        /*    readFileTabule.setReadFile(
                    new FileInputStream(path),//"/Users/Miled/Desktop/maquette_tabulé.csv"),
                    ";",
                    "##",
                    "::",
                    "dd/MM/yyyy");*/                        
            if(!readFileTabule.setFields()){
                return false;
            }
            if(!readFileTabule.setDatas()){
                return false;
            }
            this.fields = readFileTabule.getFieldsList();
            this.tabulateDocuments = readFileTabule.getTabulateDocumentList();
            return true;
            
        } catch (Exception ex) {
            Logger.getLogger(ImportTabulateHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * permet d'insérer les concepts du fichier tabulé au thésaurus sélectionné
     * les domaines et les concepts sont importés, mais pas le thésaurus,
     * il faut le créer avant.
     * @param ds
     * @param idThesaurus
     * @param idUser
     * @return 
     */
    public boolean insertIntoBDD(HikariDataSource ds, String idThesaurus, int idUser){
        if(this.fields == null) return false;
        if(this.tabulateDocuments == null) return false;
        if(this.fields.isEmpty()) return false;
        if(this.tabulateDocuments.isEmpty()) return false;
        
        ImportTabuleIntoBDD importTabuleIntoBDD = new ImportTabuleIntoBDD();
        if(!importTabuleIntoBDD.insertDatas(ds,
                   idThesaurus, this.tabulateDocuments, idUser)){
            return false;
        }
        
        /*Connection conn = null;
            
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            ImportTabuleIntoBDD importTabuleIntoBDD = new ImportTabuleIntoBDD();
            if(!importTabuleIntoBDD.insertDatas(ds,
                    idThesaurus, this.tabulateDocuments)){
                conn.rollback();
                conn.close();
                return false;
            }
            conn.commit();
            conn.close();
            return true;
        }
        catch (SQLException ex) {
            Logger.getLogger(ImportTabulateHelper.class.getName()).log(Level.SEVERE, null, ex);
            try {
                if(conn != null)
                    conn.close();
            } catch (SQLException ex1) {
            }
        }*/
        return true;
    }
    
    
}
