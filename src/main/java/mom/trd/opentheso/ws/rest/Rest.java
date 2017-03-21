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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.Encoded;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.jsonld.helper.JsonHelper;
import org.apache.http.entity.StringEntity;
import org.primefaces.json.JSONObject;
import skos.SKOSXmlDocument;

import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author miled.rousset
 */
@Path("rest")
public class Rest {

    private HikariDataSource ds;
    private Properties prefs;

    /**
     * Creates a new instance of resources
     */
    public Rest() {
        Properties properties = new Properties();
        prefs = new Properties();
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("hikari.properties");
            if (inputStream != null) {
                properties.load(inputStream);
                this.ds = openConnexionPool(properties);
            }
            InputStream inputStream2 = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("preferences.properties");
            if (inputStream2 != null) {
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
            if (conn == null) {
                return null;
            }
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

/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Partie du REST pour produire du SKOS
     * 
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////    
    /**
     * Cette fonction renvoie un concept par son id et par l'id du thésaurus
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/id={id}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getConcept(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso) {
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        ds.close();
        return skos.toString();
    }

    /**
     * Cette fonction renvoie un concept par son id et par l'id du thésaurus
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/idc={idc}&idt={idt}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getConceptClone(@PathParam("idc") String idConcept,
            @PathParam("idt") String idTheso) {
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        ds.close();
        return skos.toString();
    }

    /**
     * pour récuperer la liste des domaines d'un thésaurus
     *
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getAllGroups(@PathParam("th") String idTheso) {
        StringBuffer skos = groupsOfThesaurusToSkos(idTheso);
        ds.close();
        return skos.toString();
    }

    /**
     * Pour retourner une branche complète d'un domaine (Groupe) en SKOS avec
     * ses concepts
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/all/idg={idg}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getConceptsOfGroup(@PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {
        StringBuffer skos = conceptsOfGroupToSkos(idGroup, idTheso);
        ds.close();
        return skos.toString();
    }

    /**
     * Pour retourner un domaine (Groupe) en SKOS
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/idg={idg}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public String getGroup(@PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {
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
            @PathParam("th") String idTheso) {
        
        // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value,"UTF-8");
//            System.out.println(URLDecoder.decode("%C3%A9", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "ISO-8859-1"));
//            System.out.println(URLDecoder.decode("%E9glise", "US-ASCII"));
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }        
        StringBuffer skos = ConceptByValueToSkos(value, idTheso, idLang);
        ds.close();
        return skos.toString();
    }

    /**
     * Permet de rechercher les concepts par valeur
     *
     * @param value
     * @param idLang
     * @param idGroup
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/value={value}&lang={lang}&idg={idg}&th={th}")
    @GET

    @Produces("application/xml;charset=UTF-8")
    public String getConceptByValue(
            @PathParam("value") @Encoded String value,
            @PathParam("lang") String idLang,
            @PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {

        // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value,"UTF-8");
//            System.out.println(URLDecoder.decode("%C3%A9", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "ISO-8859-1"));
//            System.out.println(URLDecoder.decode("%E9glise", "US-ASCII"));
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }

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
            @PathParam("ark") String ark) {
        StringBuffer skos = conceptToSkos(naan + "/" + ark);

        ds.close();
        return skos.toString();
    }

    
    
    
    
    
    
    
/////////////////////////////////////////////////////    
///////////////////////////////////////////////////// 
    /*
     * Partie du REST pour produire du JsonLd
     * 
     */
///////////////////////////////////////////////////// 
/////////////////////////////////////////////////////     


    
  
    
    
    /**
     * pour tester le retour des données en Jsonp pour être compatible avec Jquery et Ajax
     */

    
    
    
    
    
    /**
     * permet de récuperer un Concept par son identifiant 
     *
     * @param idConcept
     * @param idTheso
     * @param jsonp
     * @return
     */
/*    @Path("/jsonld/concept/id={id}&th={th}&callback={jsonp}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public String getConceptJson(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso,
            @PathParam("jsonp") String jsonp) {
        
        
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
        jsonLd.insert(0, jsonp +"(");
        jsonLd.append(");");
        System.out.println(jsonLd.toString());
        ds.close();
        return jsonLd.toString();
    }    


    @GET
    @Path("jsonp/concept/{th}/{lang}/value={value}")

    /**
     * Permet de retourner les Concepts par value (en précisant un thésaurus et
     * une langue)
     *
     * @param value
     * @param idLang
     * @param idTheso
     * @return
     */
    //@Produces("text/plain")
    //@Produces("application/json")
/*    @Produces("application/json;charset=UTF-8")
    public Response getConceptJsonByValueForJsonp(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("th") String idTheso) {
                // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value,"UTF-8");
//            System.out.println(URLDecoder.decode("%C3%A9", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "ISO-8859-1"));
//            System.out.println(URLDecoder.decode("%E9glise", "US-ASCII"));
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
/*        JSONObject jSONObject = new JSONObject();
        jSONObject.put("name", "value");
        jSONObject.put("name2", "value2");
  */

/*        String s = 
        "[\"vase\",\n" +
"    [\"vase\", \"vase\", \"Vase (dépôt)\", \"Vase à bouche carrée, Culture du\", \"vase à cuire\", \"vase à étrier\", \"vase à libation\", \"vase à parfum\",\n" +
"         \"vase à pharmacie\", \"Vase campaniforme, Culture du\", \"vase canope\",\n" +
"              \"vase composite\", \"Vase cordé, Culture du\", \"vase d'albâtre\",\n" +
"                   \"vase de bronze\", \"vase de céramique\", \"vase de libation\",\n" +
"                        \"vase de métal\", \"vase de pierre\", \"Vase en entonnoir, Culture du\"],\n" +
"    [{\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtfv4IrNiWE8\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/www.eionet.europa.eu\\/gemet\\/concept\\/5417\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/data.bnf.fr\\/ark:\\/12148\\/cb12304971x\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/data.bnf.fr\\/ark:\\/12148\\/cb135347317\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtj4JceDlyFS\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtzmEytuBTdR\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrt2WTqu2BkNA\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtE3TkiyYR8i\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrt0rEs0KaVGT\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/data.bnf.fr\\/ark:\\/12148\\/cb11942230n\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtwqOtuijtQu\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtraWOfawdUB\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/data.bnf.fr\\/ark:\\/12148\\/cb12105101f\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtOd6LMcoTMh\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtrmoRMOPqj9\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrt1DMOWvDF4j\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrt2WTqu2BkNA\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtjxHvdqN73m\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/ark.frantiq.fr\\/ark:\\/26678\\/pcrtOd6LMcoTMh\"\n" +
"        }, {\n" +
"            \"uri\": \"http:\\/\\/data.bnf.fr\\/ark:\\/12148\\/cb119484779\"\n" +
"        }]]";
*/
/*        
        StringBuffer skos = ConceptByValueToSkos(value, idTheso, idLang);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
//        jsonLd.insert(0, "jsonp(");
//        jsonLd.append(");");
        ds.close();
       // return jsonLd.toString();
       return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();
     //  return s;

    }*/
    
  
    
    
    
    
    
    
    
    
    


    /**
     * permet de récuperer un Concept par son identifiant
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    @Path("/jsonld/concept/id={id}&th={th}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getConceptJson(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso) {
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);

        ds.close();
        return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();
        //return jsonLd.toString();
    }

    /**
     * retourne un concept à partir de son identifiant Ark
     *
     * @param naan
     * @param ark
     * @return
     */
    @Path("/jsonld/ark:/{naan}/{ark}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getConceptJsonArk(@PathParam("naan") String naan,
            @PathParam("ark") String ark) {
        StringBuffer skos = conceptToSkos(naan + "/" + ark);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);

        ds.close();
        return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();
        //return jsonLd.toString();
    }

    /**
     * Permet de retourner les Concepts par value (en précisant un thésaurus et
     * une langue)
     *
     * @param value
     * @param idLang
     * @param idTheso
     * @return
     */
    @Path("/jsonld/concept/value={value}&lang={lang}&th={th}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getConceptJsonByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("th") String idTheso) {
                // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value,"UTF-8");
//            System.out.println(URLDecoder.decode("%C3%A9", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "ISO-8859-1"));
//            System.out.println(URLDecoder.decode("%E9glise", "US-ASCII"));
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuffer skos = ConceptByValueToSkos(value, idTheso, idLang);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
        ds.close();
        return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();
        //return jsonLd.toString();
    }

    /**
     * Permet de retourner les Concepts par value (en précisant un thésaurus, un
     * doamine et une langue)
     *
     * @param value
     * @param idLang
     * @param idGroup
     * @param idTheso
     * @return
     */
    @Path("/jsonld/concept/value={value}&lang={lang}&idg={idg}&th={th}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getConceptJsonByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {
        // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value,"UTF-8");
//            System.out.println(URLDecoder.decode("%C3%A9", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "ISO-8859-1"));
//            System.out.println(URLDecoder.decode("%E9glise", "US-ASCII"));
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }        
        StringBuffer skos = ConceptByValueToSkos(value,
                idLang,
                idGroup,
                idTheso);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
        ds.close();
        return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();        
        //return jsonLd.toString();
    }

    /**
     * Pour retourner un domaine (Groupe) en JsonLd avec ses concepts
     *
     * @param idGroup
     * @param idTheso
     * @return
     */
    @Path("/jsonld/concept/all/idg={idg}&th={th}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getConceptsJsonOfGroup(@PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {
        StringBuffer skos = conceptsOfGroupToSkos(idGroup, idTheso);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
        ds.close();
        return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();        
        //return jsonLd.toString();
    }

    /**
     * pour récuperer la liste des domaines d'un thésaurus
     *
     * @param idTheso
     * @return
     */
    @Path("/jsonld/concept/th={th}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getAllGroupsJson(@PathParam("th") String idTheso) {
        StringBuffer skos = groupsOfThesaurusToSkos(idTheso);
        JsonHelper jsonHelper = new JsonHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLdForConceptScheme(sKOSXmlDocument);
        ds.close();
        return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();        
        //return jsonLd.toString();
    }

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
////////// Fonctions Privées     //////////////////////////////////////////////   
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////    
    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     *
     * @param idConcept
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptToSkos(String idConcept, String idThesaurus) {

        if (ds == null) {
            return null;
        }
        if (prefs == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportConcept(ds, idThesaurus, idConcept);
        if (skos == null) {
            return new StringBuffer("");
        }
        return skos;
    }

    /**
     * Fonction qui permet de récupérer un concept skos par identifiant ark
     *
     * @param idConcept
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptToSkos(String arkId) {

        if (ds == null) {
            return null;
        }
        if (prefs == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportConcept(ds, arkId);
        if (skos == null) {
            return new StringBuffer("");
        }
        return skos;
    }

    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     *
     * @param idGroup
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer groupToSkos(String idGroup, String idThesaurus) {

        if (ds == null) {
            return null;
        }
        if (prefs == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportThisGroup(ds, idThesaurus, idGroup);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     *
     * @param idGroup
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptsOfGroupToSkos(String idGroup, String idThesaurus) {

        if (ds == null) {
            return null;
        }
        if (prefs == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportGroup(ds, idThesaurus, idGroup);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer les groupes d'un thésauurs
     *
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer groupsOfThesaurusToSkos(String idThesaurus) {

        if (ds == null) {
            return null;
        }
        if (prefs == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));
        StringBuffer skos = exportFromBDD.exportGroupsOfThesaurus(ds, idThesaurus);
        return skos;
    }

    /**
     * cette fonction permet de retourner tous les concetps qui contiennenet la
     * valeur recherchée
     *
     * @param value
     * @param idThesaurus
     * @param lang
     * @return
     */
    private StringBuffer ConceptByValueToSkos(String value, String idThesaurus, String lang) {

        if (ds == null) {
            return null;
        }
        if (prefs == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(prefs.getProperty("serverArk"));
        exportFromBDD.setServerAdress(prefs.getProperty("cheminSite"));

        StringBuffer skos = exportFromBDD.exportMultiConcept(
                ds, idThesaurus, value, lang);
        return skos;
    }

    /**
     * cette fonction permet de retourner tous les concetps qui contiennenet la
     * valeur recherchée en filtrant par domaine ou groupe
     *
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

        if (ds == null) {
            return null;
        }
        if (prefs == null) {
            return null;
        }

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
