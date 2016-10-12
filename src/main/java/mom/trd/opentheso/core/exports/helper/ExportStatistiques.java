/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.exports.helper;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import mom.trd.opentheso.SelectedBeans.LanguageBean;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;

/**
 *
 * @author antonio.perez
 */
public class ExportStatistiques {
    
    @ManagedProperty(value = "#{langueBean}")
    private LanguageBean langueBean;
    
    private String document;


    private String id_theso = "";
    private String lange="";
/**
 * cette funtion permet de recuperer tout les fils d'un thesaurus 
 * on besoin seulement id_theso et language;
 * @param ds
 * @param idtheso
 * @param language
 * @throws SQLException 
 */
    public void recuperatefils(HikariDataSource ds, String idtheso, String language) throws SQLException {
        
        lange= language;
        id_theso = idtheso;
        //Declarations de variables contents
        HashMap<String, String> map = new HashMap<>();// pour garder id_grupo et son lexivalvalue de les domaines
        ArrayList<String> candidats = new ArrayList<>();// touts le term du theso
        ArrayList<Integer> combienterm = new ArrayList<>();//combian term il y a dans chacun domain
        ArrayList<Integer> niveaux = new ArrayList<>();//savoir combiens niveaux descen de la racine
        ResultSet resultSet, resultSet1, rS;
        //Variables
        boolean first = true;
        int domines = 0;
        int niveauxterm = 0;
        String group__id = "";
        //Connections a la BDD
        Connection conn, conn2 = null;
        Statement stmt, stmt1, stmt2 = null;
       
        
        try {
            conn = conn2= ds.getConnection();
            try {
                stmt = conn.createStatement();
                stmt1 = conn2.createStatement();
                stmt2 = conn2.createStatement();
                try {
                    //ici on recupere les domaines 
                    String query = "SELECT idgroup, lexicalvalue, lang FROM concept_group_label where idthesaurus ='" + id_theso + "'";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        String lexical = "";
                        String lang = "(";
                        String idgroup = resultSet.getString(1);
                        if (group__id == null ? idgroup != null : !group__id.equals(idgroup)) {// c'est unique le domain sans traduction
                            lexical += resultSet.getString(2);//getString(2) =lexicalvalue.
                            lang += resultSet.getString(3) + ")";//getString(3) = lang.
                            lexical += lang;
                            candidats.add(lexical);
                            niveaux.add(niveauxterm);
                            domines++;
                        } else {// c'est pour si il y a les memme domain en plusieurs lang
                            String change = map.get(group__id);
                            lang = "(";
                            lexical += ", " + resultSet.getString(2);
                            lang += resultSet.getString(3) + ")";
                            lexical += lang;
                            change += lexical;
                            lexical = change;
                            int ou = candidats.size();
                            candidats.remove(ou - 1);//efface le/les ancian pour mettre les nouvelles
                            candidats.add(lexical);
                        }
                        group__id = resultSet.getString(1);
                        map.put(idgroup, lexical);//introduir dans le map
                        
                    }
                    niveauxterm++;
                    for (Map.Entry e : map.entrySet()) {
                        int combien = 0;
                        int cantitad = 0;
                        ArrayList<String> id = new ArrayList<>();
                        //ici nous avons le/les fils de chacun domaine
                        String query2 = "Select *  from concept where id_thesaurus = '" + id_theso + "' and id_group ='" + e.getKey() + "' and top_concept ='true'";
                        //ici nous avos toutes les progéniture
                        String query4 = "Select id_concept from concept where id_group ='" + e.getKey() + "' ";
                        rS = stmt2.executeQuery(query4);
                        while (rS.next()) {
                            cantitad++;//on garde toutes le progénitures d'une domaine e.getkey
                        }
                        combienterm.add(cantitad);
                        resultSet = stmt.executeQuery(query2);
                        while (resultSet.next()) {
                            combien++;
                            String query3 = "Select id_term from term where id_term ='" + resultSet.getString(1) + "' and lang ='" + lange + "'";
                            resultSet1 = stmt1.executeQuery(query3);
                            if (resultSet1.next()) {
                                candidats.add(resultSet1.getString(1));
                                id.add(resultSet1.getString(1));
                                niveaux.add(niveauxterm);
                                
                            }
                        }
                        for (int z = 0; z < combien; z++) {
                            niveauxterm++;
                            //on apple a la funtion recursive
                            genererfils(ds, id_theso, lange, candidats, id.get(z), niveaux, niveauxterm);
                            niveauxterm--;
                        }
                        
                    }
                    changenames(ds, candidats, lange);// change le id_term pour son lexical_value
                    creedocumentatlch(candidats, id_theso, lange, niveaux, combienterm, map, domines);//crée le document 
                    avoirnondescripteur(ds, document, map);//cherche le non descripteur
                } finally {
                    stmt2.close();
                    stmt1.close();
                    stmt.close(); 
                }
            } finally {
                conn.close();
                conn2.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Avec tout la information que nous avos on va a créer le documen a telecharger
     * @param ds
     * @param idTheso
     * @param langue
     * @param candidats  les term que déjà on a garde
     * @param nom le term que on veux savoir son progéniture
     * @param niveaux le niveaux que on descen pour trouver le term "nom"
     * @param niveauxterm le niveaux ou il est le term nom
     */
    private void genererfils(HikariDataSource ds, String idTheso, String langue, ArrayList<String> candidats, String nom, ArrayList<Integer> niveaux, int niveauxterm) {
        Connection conn= null;
        Statement stmt = null;
        ResultSet resultset;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    //ici on trouve le fils de "nom"
                    String query = "select * from hierarchical_relationship where id_thesaurus ='" + idTheso + "' and id_concept1='" + nom + "' and role ='NT'";
                    resultset = stmt.executeQuery(query);
                    while (resultset.next()) {
                        
                        niveaux.add(niveauxterm);
                        candidats.add(resultset.getString(4));//nous ajoutons le id_concept de chacun enfant
                        niveauxterm++;
                        //on fait l'aplelation recursive
                        genererfils(ds, idTheso, langue, candidats, resultset.getString(4), niveaux, niveauxterm);
                        niveauxterm--;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * on cherche dans la BDD le lexical value de chacun id_concept 
     * que nous avons et on fait le change
     * @param ds
     * @param candidats  
     * @param lang 
     */
    private void changenames(HikariDataSource ds, ArrayList<String> candidats, String lang) {
        Connection conn= null;
        Statement stmt= null;
        String complet="";
        ResultSet resultset;
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    for (int i = 2; i < candidats.size(); i++) {
                        int pos;
                        //pour chac id on prendre son lexical value dan la lang que nous sommes
                        String query = "select id_term, lexical_value from term where id_term ='" + candidats.get(i) + "' and lang ='"+lang+"'";
                        resultset = stmt.executeQuery(query);
                        if (resultset.next()) {
                            candidats.remove(i);//efface le id 
                            complet+= resultset.getString(2)+" ("+resultset.getString(1)+")";//on ecrit le lexical value et son id
                            candidats.add(i, complet);
                            complet="";
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/**
 * on crée le String que apres serais le fichier a telecharger
 * @param candidat tout les term que nous avons apres de chercher toutes les enfants
 * @param id_theso
 * @param lg langue
 * @param niveaux le niveaux correspondat a chacun term
 * @param cantite cantite de term que il y a dans une domaine
 * @param map le hasMap avec le domaines y son id 
 * @param domaines combien domaines on a trouvé
 */
    private void creedocumentatlch(ArrayList<String> candidat, String id_theso, String lg, ArrayList<Integer> niveaux, ArrayList<Integer> cantite, HashMap<String, String> map, int domaines) {
        int quelledomaine = 0;// le domaine que on prendre pour commencé a ecrire
        int positionmultipli=0;//combien de position on besoin avancé pour trouvée le bon term
        boolean dernier=false;
        document = "Thésaurus: " + id_theso + "\t"+ lg+"\t Nombre total de concept: " + (candidat.size()-map.size()) + "\n\n";
        //document += langueBean.getMsg("stat.statCpt4")+"\n\n";//ne marche pas et je sais pas pourquoi
        for (Map.Entry e : map.entrySet()) {
            document += e.getValue() + "   " +cantite.get(quelledomaine)+ " \n";//combiens term pour chac domaine
            //on commence aprés le domaines a chercher le term (domaines + positionmultipli)
            //cantite.get(quelledomaine) nous donne combiens term pour chac domain
            //+domaines parceque on commence aprés le domaines
            //+positionmultipli si ce n'est pas le 1° avances tout les term jusca arriver a le bonne
            for (int j = (domaines+positionmultipli); j < ((cantite.get(quelledomaine) + domaines)+positionmultipli); j++) {
                //selon le niveaux du term on fait plus or moins espace en blanche
                for (int i = 0; i < niveaux.get(j); i++) {
                    document += "    ";
                }
                document += candidat.get(j) + "\n";//et on ecrit le term
                if(quelledomaine==(domaines-1))//si quelledomaine arrive a ça, c'est parce que lui est le dernier
                {
                    dernier =true;
                }
            }
            if(!dernier)//si ce n'est pas le dernier, change pour savoir ou commence les term de le prochain domaine
            {
                quelledomaine++;
                positionmultipli+=cantite.get(quelledomaine);            
            }
        }
    }
    /**
     * on va a chercher  le term que il ne sont pas descripteur
     * @param ds
     * @param document
     * @param map
     * @return 
     */
    private String  avoirnondescripteur(HikariDataSource ds, String document, HashMap<String, String> map)
    {
        ArrayList<Integer> ouviens = new ArrayList<>();//ArrayList pour savoir combien de term "nondescripteur" il y a dans chac domaine
        Connection conn= null;
        Statement stmt= null;
        ResultSet resultset;
        int ousont=0;//pour avancé dans ouviens et savoir le/les term  "nondescripteur"
        int combien =0;//pour conté combien ils sont
        try {
            conn = ds.getConnection();
            try {
                stmt = conn.createStatement();
                try {
                    for (Map.Entry e : map.entrySet()) {
        //On cherche "nondescripteur" dans la table permuted avec le id_group, la lange, le theso et la option de ispreferredterm a false
                        String query="Select id_concept from permuted where id_group = '"+e.getKey()
                                +"' and id_lang = '"+ lange
                                +"' and ispreferredterm= 'false'"
                                + " and id_thesaurus ='"+id_theso+"'";
                        resultset= stmt.executeQuery(query);
                        while (resultset.next()) 
                        {
                            combien++;
                        }
                        ouviens.add(combien);//introduisons combin dans l'ArrayList
                        combien=0;//reinicialisons le valeur pour le prochaine
                    }
                }   
                finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Map.Entry e : map.entrySet()) {
            this.document+="\n\n"+e.getValue()+"  Non descripteurs: "+ouviens.get(ousont);
            this.document+="\n\n"+e.getValue()+"  Termes non traduits: ";//manque faire le languageBean.getMsg 
            this.document+="\n\n"+e.getValue()+"  Notes(définitions): ";//donne error le getMsg
            this.document+="\n";
            ousont++;   
        }
        return this.document;
    }
    
    
    //getter and setter
    public LanguageBean getLangueBean() {
        return langueBean;
    }

    public void setLangueBean(LanguageBean langueBean) {
        this.langueBean = langueBean;
    }
    
    
    public String getDocument() {
        return document;
    }
    
    public void setDocument(String document) {
        this.document = document;
    }
    
}
