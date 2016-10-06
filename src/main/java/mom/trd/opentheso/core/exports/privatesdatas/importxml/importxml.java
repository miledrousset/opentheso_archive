/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.privatesdatas.importxml;

import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import mom.trd.opentheso.SelectedBeans.LanguageBean;
import mom.trd.opentheso.bdd.tools.CreateBDD;
import mom.trd.opentheso.core.exports.privatesdatas.LineOfData;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

@ManagedBean(name = "ImportBDD", eager = true)
/**
 *
 * @author antonio.perez
 */



public class importxml {

    
    private String nomBDD="";
    /*  
        for (Table user : DataTable) {
            for (LineOfData lineOfData : user.getLineOfDatas()) {
                writeLine(lineOfData.getColomne(), lineOfData.getValue());
            }
     */
    
    /*
    public void choisirfichier (HikariDataSource ds){
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(null);
        fichero = fileChooser.getSelectedFile();
               //Acciones que se quieran realizar
        ouvreFichier();
       
    }*/ 
    public void ouvreFichier(Connection con, File archive) throws ClassNotFoundException, SQLException {
        System.out.println("a abrir el fichero");
        LanguageBean langueBean = new LanguageBean();
        SAXBuilder builder = new SAXBuilder();
        ArrayList<Table> toutTables = new ArrayList<>();
        ArrayList<LineOfData> lineOfDatas = new ArrayList<>();
        System.out.println("ya tengo el fichero a empezar");
        try {

            //on crée le document a partir du fichier que on a selectioné
            Document document = (Document) builder.build(archive);
            Connection conn;
            //Se obtiene la raiz 'tables'
            Element rootNode = document.getRootElement();
            
            // ici on a toutes les tables (les enfants de la racine)
            List list = rootNode.getChildren("table");
            System.out.println("se supone que esta abierto y mirando los hijos");            

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
                System.out.println("Tabla hecha y pa entro");                
                    insertLine(con, lineOfDatas, nombreTabla);
                    lineOfDatas.clear();
            System.out.println("ya esta metia y borrao lineofdatas");
                }
                /// mettre à jour la table dans la BDD
            }
        System.out.println("ya esta hecho todo el archivo y vamos pa fuera");   
        } catch (IOException | JDOMException io) {
            System.out.println(io.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(langueBean.getMsg("info") + " :", langueBean.getMsg("impBDD.info1")));
    }
/**
 * cette funtion permet de faire la inyection a la BDD line par line de la table "nomtable"
 * et le données que ils sont dans une ArrayList
 * @param ds
 * @param table
 * @param nomtable 
 */
    private void insertLine(Connection con, ArrayList<LineOfData> table, String nomtable) throws ClassNotFoundException, SQLException {
        Statement stmt;
        String nomcolun = "";
        String values = "";
        boolean first = true;
                    System.out.println("dentro de la inserccion");
        for (LineOfData lineOfData : table) {
            if (!first) {
                nomcolun += ",";
                values += ",";
            }
            nomcolun += lineOfData.getColomne();
            values += "'" + lineOfData.getValue() + "'";
            first = false;
        }
        
                    System.out.println("hago la linea");
        values += ");";     
        try {
            //Connection conn = ds.getConnection();
            stmt = con.createStatement();
            try {
                // récupération des noms des colonnes de la table
                String query = "INSERT INTO " + nomtable + " ( "
                        + nomcolun + ") VALUES (" + values;
                stmt.executeUpdate(query);
                System.out.println("he metio dentro "+nomtable);
            } finally {
                stmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    
}

