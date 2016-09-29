/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.privatesdatas.importxml;

import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.swing.JFileChooser;
import mom.trd.opentheso.SelectedBeans.LanguageBean;
import mom.trd.opentheso.core.exports.privatesdatas.LineOfData;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import mom.trd.opentheso.bdd.helper.Connexion;

@ManagedBean(name = "ImportBDD", eager = true)
/**
 *
 * @author antonio.perez
 */



public class importxml {

    
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;
    /*  
        for (Table user : DataTable) {
            for (LineOfData lineOfData : user.getLineOfDatas()) {
                writeLine(lineOfData.getColomne(), lineOfData.getValue());
            }
     */
    
    /*
    public void cansado (HikariDataSource ds){
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(null);
        fichero = fileChooser.getSelectedFile();
               //Acciones que se quieran realizar
        ouvreFichier();
       
    }*/ 
    public void ouvreFichier(HikariDataSource ds, File archive) {
       
        SAXBuilder builder = new SAXBuilder();
        ArrayList<Table> toutTables = new ArrayList<>();
        ArrayList<LineOfData> lineOfDatas = new ArrayList<>();
        
        try {

            //on crée le document a partir du fichier que on a selectioné
            Document document = (Document) builder.build(archive);

            //Se obtiene la raiz 'tables'
            Element rootNode = document.getRootElement();

            // ici on a toutes les tables (les enfants de la racine)
            List list = rootNode.getChildren("table");

            // ici on fait le tour pour les enfants de 'tables'
            for (int i = 0; i < list.size(); i++) {

                // ici on a la première table
                Element tabla = (Element) list.get(i);

                //ici on a le nom de la table
                String nombreTabla = tabla.getAttributeValue("nom");

                // ici c'est la liste des lignes de la table
                List lista_campos = tabla.getChildren();

                //ici on découpe la liste des lignes
                for (int j = 0; j < lista_campos.size(); j++) {
                    
                    //ici on a une ligne de la table
                    Element campo = (Element) lista_campos.get(j);
                    for (Element colonne : campo.getChildren()) {
                        LineOfData lineOfData = new LineOfData();
                        lineOfData.setColomne(colonne.getName());
                        lineOfData.setValue(colonne.getText());
                        lineOfDatas.add(lineOfData);
                    }
                    insertLine(ds, lineOfDatas, nombreTabla);
                    lineOfDatas.clear();

                }
                /// mettre à jour la table dans la BDD
            }

        } catch (IOException io) {
            System.out.println(io.getMessage());
        } catch (JDOMException jdomex) {
            System.out.println(jdomex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("impBDD.info1")));
    }

    private void insertLine(HikariDataSource ds, ArrayList<LineOfData> table, String nomtable) {
        Statement stmt;
        String nomcolun = "";
        String values = "";
        boolean first = true;
        for (LineOfData lineOfData : table) {
            if (!first) {
                nomcolun += ",";
                values += ",";
            }
            nomcolun += lineOfData.getColomne();
            values += "'" + lineOfData.getValue() + "'";
            first = false;
        }
        values += ");";
        try {
            Connection conn = ds.getConnection();
            stmt = conn.createStatement();
            System.out.println(nomtable);
            try {
                // récupération des noms des colonnes de la table
                String query = "INSERT INTO " + nomtable + " ( "
                        + nomcolun + ") VALUES (" + values;
                stmt.executeUpdate(query);
            } finally {
                stmt.close();
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}

