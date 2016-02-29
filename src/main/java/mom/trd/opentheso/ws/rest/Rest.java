/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.ws.rest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.jsonld.helper.JsonHelper;
import skos.SKOSXmlDocument;

/**
 * REST Web Service
 *
 * @author miled.rousset
 */
@Path("rest")
public class Rest {

    @Context
    private UriInfo context;

    private HikariDataSource ds;
    private Properties prefs;    
    
    /**
     * Creates a new instance of resources
     */
    public Rest() {
        Properties properties= new Properties();
        prefs = new Properties();
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("hikari.properties");
            if(inputStream != null) {
                properties.load(inputStream);
                this.ds = openConnexionPool(properties);
            }
            InputStream inputStream2 = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("preferences.properties");
            if(inputStream2 != null) {
                prefs.load(inputStream2);
            }
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
    }
    
    private HikariDataSource openConnexionPool(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("minimumIdle")));
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("setMaximumPoolSize")));
        config.setAutoCommit(true);
        config.setIdleTimeout(Integer.parseInt(properties.getProperty("idleTimeout")));
        config.setConnectionTimeout(Integer.parseInt(properties.getProperty("connectionTimeout")));
        config.setConnectionTestQuery(properties.getProperty("connectionTestQuery"));
        config.setDataSourceClassName(properties.getProperty("dataSourceClassName"));
        
        config.addDataSourceProperty("user", properties.getProperty("dataSource.user"));
        config.addDataSourceProperty("password", properties.getProperty("dataSource.password"));
        config.addDataSourceProperty("databaseName", properties.getProperty("dataSource.databaseName"));
        config.addDataSourceProperty("serverName", properties.getProperty("dataSource.serverName"));
        config.addDataSourceProperty("portNumber", properties.getProperty("dataSource.serverPort"));
        
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        try {
            Connection conn = poolConnexion1.getConnection();
            if(conn == null) return null;
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, ex.getClass().getName(), ex.getMessage()); 
            FacesContext.getCurrentInstance().addMessage(null, message);
            poolConnexion1.close();
            return null;
        }
     
        return poolConnexion1;
     }
   
  /*  
    @Path("/skos/group/id={id}&th={th}")
    @GET
    //@Produces("text/plain")
    @Produces("application/json")
    public String getGroup(@PathParam("id") String idGroup,
            @PathParam("th") String idTheso){
        return "<test>la valeur du group est : </test>" + idGroup + 
                " <toto>thesaurus = </toto>" + idTheso;
    }
    */
    
    
    
    
    
    
    
    
    
    
    
    /**
     * La partie REST pour produire du SKOS
    */

    /**
     * Cette fonction renvoie un concept par son id et par l'id du thésaurus 
     * @param idConcept
     * @param idTheso
     * @return 
    */
    @Path("/skos/concept/id={id}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getConcept(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso){
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        ds.close();
        return skos.toString();
    }
    
    /**
     * pour récuperer la liste des domaines d'un thésaurus
     * @param idTheso
     * @return 
    */
    @Path("/skos/concept/th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getAllGroups(@PathParam("th") String idTheso){
        StringBuffer skos = groupsOfThesaurusToSkos(idTheso);
        ds.close();
        return skos.toString();
    }    
    
    
    /**
     * Pour retourner un domaine (Groupe) en SKOS avec ses concepts
     * @param idGroup
     * @param idTheso
     * @return 
     */
    @Path("/skos/concept/all/idg={idg}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getConceptsOfGroup(@PathParam("idg") String idGroup,
            @PathParam("th") String idTheso){
        StringBuffer skos = conceptsOfGroupToSkos(idGroup, idTheso);
        ds.close();
        return skos.toString();
    }    
    
    /**
     * Pour retourner un domaine (Groupe) en SKOS
     * @param idGroup
     * @param idTheso
     * @return 
     */
    @Path("/skos/concept/idg={idg}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getGroup(@PathParam("idg") String idGroup,
            @PathParam("th") String idTheso){
        StringBuffer skos = groupToSkos(idGroup, idTheso);
        ds.close();
        return skos.toString();
    }
    
    @Path("/skos/concept/value={value}&lang={lang}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getConceptByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("th") String idTheso){
        StringBuffer skos = ConceptByValueToSkos(value, idTheso, idLang);
        ds.close();
        return skos.toString();
    }
    
    @Path("/skos/concept/value={value}&lang={lang}&idg={idg}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getConceptByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("idg") String idGroup,            
            @PathParam("th") String idTheso){
        StringBuffer skos = ConceptByValueToSkos(value,
                idLang,
                idGroup,
                idTheso);
        ds.close();
        return skos.toString();
    }    
    
    /**
     * @param naan
     * @param ark
     * @return 
     */
    @Path("/skos/ark:/{naan}/{ark}")
    @GET

    @Produces("application/xml;charset=UTF-8")
    public String getConceptSkosArk(@PathParam("naan") String naan,
            @PathParam("ark") String ark){
        StringBuffer skos = conceptToSkos(naan +"/" + ark);
                     
        ds.close();
        return skos.toString();
    }     
    
    /**
     * Partie du REST pour produire du JsonLd
     * @param idConcept
     * @param idTheso
     * @return 
     */
    @Path("/jsonld/concept/id={id}&th={th}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public String getConceptJson(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso){
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
                       
        ds.close();
        return jsonLd.toString();
    }
    
    
    
    
    
    
    
    
    
    /**
     * Partie du REST pour produire du JsonLd
     * @param naan
     * @param ark
     * @return 
     */
    @Path("/jsonld/ark:/{naan}/{ark}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public String getConceptJsonArk(@PathParam("naan") String naan,
            @PathParam("ark") String ark){
        StringBuffer skos = conceptToSkos(naan +"/" + ark);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
                       
        ds.close();
        return jsonLd.toString();
    }    
    
    @Path("/jsonld/concept/value={value}&lang={lang}&th={th}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public String getConceptJsonByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("th") String idTheso){
        StringBuffer skos = ConceptByValueToSkos(value, idTheso, idLang);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);        
        ds.close();
        return jsonLd.toString();
    }

    @Path("/jsonld/concept/value={value}&lang={lang}&idg={idg}&th={th}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public String getConceptJsonByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("idg") String idGroup,
            @PathParam("th") String idTheso){
        StringBuffer skos = ConceptByValueToSkos(value,
                idLang,
                idGroup,
                idTheso);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);        
        ds.close();
        return jsonLd.toString();
    }    
    
    
    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     * @param idConcept
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptToSkos(String idConcept, String idThesaurus) {
                        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportConcept(ds, idThesaurus, idConcept);
        if(skos == null) return new StringBuffer("");
        return skos;
    }
    
                /**
     * Fonction qui permet de récupérer un concept skos par identifiant ark
     * @param idConcept
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptToSkos(String arkId) {
                        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportConcept(ds, arkId);
        if(skos == null) return new StringBuffer("");
        return skos;
    }                   

    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     * @param idGroup
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer groupToSkos(String idGroup, String idThesaurus) {
                        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportThisGroup(ds, idThesaurus, idGroup);
        return skos;
    }
    
    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     * @param idGroup
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptsOfGroupToSkos(String idGroup, String idThesaurus) {
                        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportGroup(ds, idThesaurus, idGroup);
        return skos;
    }    
    
    /**
     * Fonction qui permet de récupérer les groupes d'un thésauurs
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer groupsOfThesaurusToSkos(String idThesaurus) {
                        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportGroupsOfThesaurus(ds, idThesaurus);
        return skos;
    }    
    
    /**
     * cette fonction permet de retourner tous les concetps qui contiennenet la valeur recherchée 
     * @param value
     * @param idThesaurus
     * @param lang
     * @return 
     */
    private StringBuffer ConceptByValueToSkos(String value, String idThesaurus, String lang) {
        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        
        StringBuffer skos = exportFromBDD.exportMultiConcept(
                ds, idThesaurus, value, lang);
        return skos;
    }
    
    /**
     * cette fonction permet de retourner tous les concetps qui contiennenet la valeur recherchée 
     * en filtrant par domaine ou groupe
     * @param value
     * @param lang
     * @param idGroup
     * @param idThesaurus
     * @return 
     */
    private StringBuffer ConceptByValueToSkos(String value, 
           String lang,
           String idGroup,
           String idThesaurus) {
        
        if(ds == null) return null;
        if(prefs == null) return null;
        
        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        
        StringBuffer skos = exportFromBDD.exportMultiConcept(ds,
                value,
                lang,
                idGroup,
                idThesaurus);
        return skos;
    }      
    
       
}
