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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.Encoded;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.jsonld.helper.JsonHelper;
import skos.SKOSXmlDocument;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;

import mom.trd.opentheso.bdd.helper.nodes.PreferencesHelper;

/**
 * REST Web Service
 *
 * @author miled.rousset
 */
@Path("rest")
public class Rest {

    private HikariDataSource ds;
    private NodePreference nodePreference;

    /**
     * Creates a new instance of resources La connexion est faite à chaque
     * question
     *
     */
    public Rest() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("hikari.properties");
            if (inputStream != null) {
                properties.load(inputStream);
                //   if(properties.getProperty("webservices").equalsIgnoreCase("false"))
                //      return;
                this.ds = openConnexionPool(properties);
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

    /**
     * Permet de lire les préférences d'un thésaurus pour savoir si le
     * webservices est activé ou non
     *
     * @param idTheso
     */
    private boolean getStatusOfWebservices(String idTheso) {
        return new PreferencesHelper().isWebservicesOn(ds, idTheso);
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
    public Response getConcept(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso) {
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
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
    public Response getConceptClone(@PathParam("idc") String idConcept,
            @PathParam("idt") String idTheso) {
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
        //return skos.toString();
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
    public Response getAllGroups(@PathParam("th") String idTheso) {
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = groupsOfThesaurusToSkos(idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
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
    public Response getGroup(@PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = groupToSkos(idGroup, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    @Path("/skos/concept/value={value}&lang={lang}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public Response getConceptByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("th") String idTheso) {

        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value, "UTF-8");
//            System.out.println(URLDecoder.decode("%C3%A9", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "ISO-8859-1"));
//            System.out.println(URLDecoder.decode("%E9glise", "US-ASCII"));

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuffer skos = ConceptByValueToSkos(value, idTheso, idLang);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
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
    public Response getConceptByValue(
            @PathParam("value") @Encoded String value,
            @PathParam("lang") String idLang,
            @PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {

        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value, "UTF-8");
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
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();

    }

    /**
     * @param naan
     * @param ark
     * @return
     */
    @Path("/skos/ark:/{naan}/{ark}")
    @GET

    @Produces("application/xml;charset=UTF-8")
    public Response getConceptSkosArk(@PathParam("naan") String naan,
            @PathParam("ark") String ark) {

        String idTheso = new ConceptHelper().getIdThesaurusFromArkId(ds, naan + "/" + ark);
        if (idTheso == null) {
            ds.close();
            return Response.noContent().build();
        }
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        StringBuffer skos = conceptToSkosFromArk(naan + "/" + ark, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
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
    public Response getConceptsOfGroup(@PathParam("idg") String idGroup,
            @PathParam("th") String idTheso) {
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptsOfGroupToSkos(idGroup, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Pour retourner une branche complète à partir d'un concept en SKOS, mais
     * en descendant la branche
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/all/idc={idc}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public Response getBrancheOfConcepts(@PathParam("idc") String idConcept,
            @PathParam("th") String idTheso) {
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = brancheOfConceptsToSkos(idConcept, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Pour retourner une branche complète à partir d'un concept en SKOS mais en
     * descendant la branche (pour répondre aussi à l'argument "id")
     *
     * @param idConcept
     * @param idTheso
     * @return
     */
    @Path("/skos/concept/all/id={id}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public Response getBrancheOfConcepts2(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso) {
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = brancheOfConceptsToSkos(idConcept, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }        
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Pour retourner une branche complète à partir d'un concept en SKOS mais en
     * remontant la branche par les BT
     *
     * @param idConcept
     * @param idTheso
     * @param way
     * @return
     */
    @Path("/skos/concept/all/id={id}&th={th}&way={way}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public Response getBrancheOfConcepts(@PathParam("id") String idConcept,
            @PathParam("th") String idTheso,
            @PathParam("way") String way) {

        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        if (way.equalsIgnoreCase("top")) {
            StringBuffer skos = brancheOfConceptsToSkosTop(idConcept, idTheso);
            if (skos == null) {
                ds.close();
                return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
            }
            if (skos.length() == 0) {
                ds.close();
                return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
            }            
            ds.close();
            return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (way.equalsIgnoreCase("down")) {
            StringBuffer skos = brancheOfConceptsToSkos(idConcept, idTheso);
            if (skos == null) {
                ds.close();
                return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
            }
            if (skos.length() == 0) {
                ds.close();
                return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
            }            
            ds.close();
            return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
        }
        return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Pour retourner tous les concepts à partir d'une date donnée (modifiés ou
     * créés) sans distinction format de la date yyyy-mm-dd
     *
     * @param idTheso
     * @param date
     * @return
     */
    @Path("/skos/concept/all/th={th}&date={date}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public Response getBrancheOfConceptsFromDate(
            @PathParam("th") String idTheso,
            @PathParam("date") String date) {

        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = brancheOfConceptsToSkosFromDate(idTheso, date);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }

        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Permet de renvoyer la branche complète en SKOS en partant d'un
     * identifiant ARK
     *
     * @param naan
     * @param ark
     * @return
     */
    @Path("/skos/all/ark:/{naan}/{ark}")
    @GET

    @Produces("application/xml;charset=UTF-8")
    public Response getBranchOfConceptSkosArk(@PathParam("naan") String naan,
            @PathParam("ark") String ark) {

        String idTheso = new ConceptHelper().getIdThesaurusFromArkId(ds, naan + "/" + ark);
        if (idTheso == null) {
            ds.close();
            return Response.noContent().build();
        }
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        StringBuffer skos = conceptToSkosFromArk(naan + "/" + ark, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptySkos()).header("Access-Control-Allow-Origin", "*").build();
        }
        ds.close();
        return Response.ok(skos.toString()).header("Access-Control-Allow-Origin", "*").build();
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
     * pour tester le retour des données en Jsonp pour être compatible avec
     * Jquery et Ajax
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
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptToSkos(idConcept, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }        
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

        String idTheso = new ConceptHelper().getIdThesaurusFromArkId(ds, naan + "/" + ark);
        if (idTheso == null) {
            ds.close();
            return Response.noContent().build();
        }
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        StringBuffer skos = conceptToSkosFromArk(naan + "/" + ark, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
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
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value, "UTF-8");
//            System.out.println(URLDecoder.decode("%C3%A9", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "UTF-8"));
//            System.out.println(URLDecoder.decode("%E9glise", "ISO-8859-1"));
//            System.out.println(URLDecoder.decode("%E9glise", "US-ASCII"));

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Rest.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuffer skos = ConceptByValueToSkos(value, idTheso, idLang);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
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

        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        // transforme le codage de la valeur de l'UTF-8
        try {
            value = URLDecoder.decode(value, "UTF-8");
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
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
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

        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptsOfGroupToSkos(idGroup, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
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
        if (!getStatusOfWebservices(idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = groupsOfThesaurusToSkos(idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
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
    private StringBuffer conceptToSkos(String idConcept, String idTheso) {

        if (ds == null) {
            return null;
        }

        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        StringBuffer skos = exportFromBDD.exportConcept(ds, idTheso, idConcept);
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
    private StringBuffer conceptToSkosFromArk(String arkId, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
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
    private StringBuffer groupToSkos(String idGroup, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        StringBuffer skos = exportFromBDD.exportThisGroup(ds, idTheso, idGroup);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     *
     * @param idGroup
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptsOfGroupToSkos(String idGroup, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        StringBuffer skos = exportFromBDD.exportGroup(ds, idTheso, idGroup);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept vers le bas
     *
     * @param idGroup
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer brancheOfConceptsToSkos(String idConcept, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        StringBuffer skos = exportFromBDD.exportBranchOfConcept(ds, idTheso, idConcept);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer une branche complète en partant d'un
     * concept vers le haut
     *
     * @param idGroup
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer brancheOfConceptsToSkosTop(String idConcept, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ArrayList<String> path = new ArrayList<>();
        ArrayList<ArrayList<String>> tabId = new ArrayList<>();

        ConceptHelper conceptHelper = new ConceptHelper();
        path.add(idConcept);
        tabId = conceptHelper.getPathOfConceptWithoutGroup(ds, idConcept, idTheso, path, tabId);

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        StringBuffer skos = exportFromBDD.exportConceptByLot(ds, idTheso, tabId);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer les groupes d'un thésauurs
     *
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer groupsOfThesaurusToSkos(String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        StringBuffer skos = exportFromBDD.exportGroupsOfThesaurus(ds, idTheso);
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
    private StringBuffer ConceptByValueToSkos(String value, String idTheso, String lang) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());

        StringBuffer skos = exportFromBDD.exportMultiConcept(
                ds, idTheso, value, lang);
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
            String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());

        StringBuffer skos = exportFromBDD.exportMultiConcept(ds,
                value,
                lang,
                idGroup,
                idTheso);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer les concepts à partir d'une date donnée
     * (modifiés ou crées)
     *
     * @param idConcept
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer brancheOfConceptsToSkosFromDate(String idTheso, String date) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreference(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ArrayList<String> tabId;
        ArrayList<ArrayList<String>> multiTabId = new ArrayList<>();

        ConceptHelper conceptHelper = new ConceptHelper();

        tabId = conceptHelper.getConceptsDelta(ds, idTheso, date);
        if (tabId == null) {
            ds.close();
            return null;
        }
        if (tabId.isEmpty()) {
            ds.close();
            return new StringBuffer("");
        }

        multiTabId.add(tabId);

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());

        StringBuffer skos = exportFromBDD.exportConceptByLot(ds, idTheso, multiTabId);
        return skos;
    }

    private String messageJson() {
        String message = "{\n"
                + "\n"
                + "    \"status_fr\":\"Le Webservices est désactivé pour ce thésaurus, demandez à l'administrateur de l'activer !!\",\n"
                + "    \"status_en\":\"The Webservices is disabled for this thesaurus, ask the administrator to activate it !!\"\n"
                + "\n"
                + "}";

        return message;
    }

    private String messageErreur() {
        String message = "{\n"
                + "\n"
                + "    \"erreur_fr\":\"Une erreur s'est produite !!\",\n"
                + "    \"erreur_en\":\"An error has occurred !!\"\n"
                + "\n"
                + "}";

        return message;
    }

    private String messageNoData() {
        String message = "{\n"
                + "\n"
                + "    \"résultat_fr\":\"Pas de résultat !!\",\n"
                + "    \"result_en\":\"No result !!\"\n"
                + "\n"
                + "}";

        return message;
    }

    private String messageEmptySkos() {
        String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<rdf:RDF\n"
                + "	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
                + "</rdf:RDF>";

        return message;
    }

    private String messageEmptyJson() {
        String message = "{\n"
                + "}";

        return message;
    }

}
