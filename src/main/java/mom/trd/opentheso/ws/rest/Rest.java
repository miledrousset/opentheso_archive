/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.ws.rest;

import com.zaxxer.hikari.HikariDataSource;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Encoded;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import mom.trd.opentheso.core.exports.old.ExportFromBDD;
import mom.trd.opentheso.core.jsonld.helper.JsonldHelper;
import skos.SKOSXmlDocument;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;

import mom.trd.opentheso.bdd.helper.PreferencesHelper;
import mom.trd.opentheso.bdd.helper.SearchHelper;
import mom.trd.opentheso.bdd.helper.nodes.search.NodeSearch;
import mom.trd.opentheso.core.exports.rdf4j.WriteRdf4j;
import mom.trd.opentheso.core.exports.rdf4j.ExportRdf4jHelper;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;


/**
 * REST Web Service
 *
 * @author miled.rousset
 */
@Path("rest")
public class Rest {

    private NodePreference nodePreference;

    /**
     * Creates a new instance of resources La connexion est faite à chaque
     * question
     *
     */
    public Rest() {
    }
    
    private HikariDataSource connect() {
        ConnexionRest connexionRest = new ConnexionRest();
        return connexionRest.getConnexion();
    }    

    /**
     * Permet de lire les préférences d'un thésaurus pour savoir si le
     * webservices est activé ou non
     *
     * @param idTheso
     */
    private boolean getStatusOfWebservices(HikariDataSource ds , String idTheso) {
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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptToSkos(ds, idConcept, idTheso);
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
    
    
    ///////////// test
    @Path("/skos/{idtheso}/{idconcept}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public Response getConceptTest(@PathParam("idconcept") String idConcept,
            @PathParam("idtheso") String idTheso) {
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptToSkos(ds, idConcept, idTheso);
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
    
    ///test

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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptToSkos(ds, idConcept, idTheso);
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
        HikariDataSource ds = connect();
        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = groupsOfThesaurusToSkos(ds, idTheso);
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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = groupToSkos(ds, idGroup, idTheso);
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
        HikariDataSource ds = connect();        

        if (!getStatusOfWebservices(ds, idTheso)) {
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
        StringBuffer skos = ConceptByValueToSkos(ds, value, idTheso, idLang);
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
    
    @Path("/skos/concept/value={value}&th={th}")
    @GET
    @Produces("application/xml;charset=UTF-8")
    public Response getConceptByValue(
            @PathParam("value") String value,
            @PathParam("th") String idTheso) {
        HikariDataSource ds = connect();        

        if (!getStatusOfWebservices(ds, idTheso)) {
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
        StringBuffer skos = ConceptByValueToSkos(ds, value, idTheso, "");
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
        HikariDataSource ds = connect();
        if (!getStatusOfWebservices(ds, idTheso)) {
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

        StringBuffer skos = ConceptByValueToSkos(ds, value,
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
        HikariDataSource ds = connect();        

        String idTheso = new ConceptHelper().getIdThesaurusFromArkId(ds, naan + "/" + ark);
        if (idTheso == null) {
            ds.close();
            return Response.noContent().build();
        }
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        StringBuffer skos = conceptToSkosFromArk(ds, naan + "/" + ark, idTheso);
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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptsOfGroupToSkos(ds, idGroup, idTheso);
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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = brancheOfConceptsToSkos(ds, idConcept, idTheso);
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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = brancheOfConceptsToSkos(ds, idConcept, idTheso);
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
        HikariDataSource ds = connect();
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        if (way.equalsIgnoreCase("top")) {
            StringBuffer skos = brancheOfConceptsToSkosTop(ds, idConcept, idTheso);
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
            StringBuffer skos = brancheOfConceptsToSkos(ds, idConcept, idTheso);
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
        HikariDataSource ds = connect();
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = brancheOfConceptsToSkosFromDate(ds, idTheso, date);
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
        HikariDataSource ds = connect();
        String idTheso = new ConceptHelper().getIdThesaurusFromArkId(ds, naan + "/" + ark);
        if (idTheso == null) {
            ds.close();
            return Response.noContent().build();
        }
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        StringBuffer skos = conceptToSkosFromArk(ds, naan + "/" + ark, idTheso);
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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptToSkos(ds, idConcept, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }        
        JsonldHelper jsonHelper = new JsonldHelper();
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
        HikariDataSource ds = connect();
        String idTheso = new ConceptHelper().getIdThesaurusFromArkId(ds, naan + "/" + ark);
        if (idTheso == null) {
            ds.close();
            return Response.noContent().build();
        }
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }

        StringBuffer skos = conceptToSkosFromArk(ds, naan + "/" + ark, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
        JsonldHelper jsonHelper = new JsonldHelper();
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
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
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
        StringBuffer skos = ConceptByValueToSkos(ds, value, idTheso, idLang);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
        JsonldHelper jsonHelper = new JsonldHelper();
        SKOSXmlDocument sKOSXmlDocument = jsonHelper.readSkosDocument(skos);
        StringBuffer jsonLd = jsonHelper.getJsonLd(sKOSXmlDocument);
        ds.close();
        return Response.ok(jsonLd.toString()).header("Access-Control-Allow-Origin", "*").build();
        //return jsonLd.toString();
    }
    
    /**
     * Permet de retourner les Concepts par value (en précisant un thésaurus)
     *
     * @param value
     * @param idTheso
     * @return
     */
    @Path("/jsonld/concept/value={value}&th={th}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getConceptJsonByValue(
            @PathParam("value") String value,
            @PathParam("th") String idTheso) {
        HikariDataSource ds = connect();        
        if (!getStatusOfWebservices(ds, idTheso)) {
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
        StringBuffer skos = ConceptByValueToSkos(ds, value, idTheso, "");
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
        JsonldHelper jsonHelper = new JsonldHelper();
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
    @Path("/json/concept/value={value}&lang={lang}&th={th}")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getConceptJsByValue(
            @PathParam("value") String value,
            @PathParam("lang") String idLang,
            @PathParam("th") String idTheso) {
        HikariDataSource ds = connect();         
        if (!getStatusOfWebservices(ds,idTheso)) {
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
        StringBuffer skos = ConceptByValueToSkos(ds, value, idTheso, idLang);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }
        String json = getJsonDatas(ds, value, idLang, idTheso);
        ds.close();
        return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        //return jsonLd.toString();
    }    
    
    /**
     * Permet de retourner les Concepts par value (en précisant un thésaurus et
     * une langue)
     *
     * @param uri
     * @return
     */
    @Path("/test")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response searchTest(@Context UriInfo uri) {
        String value = "";
        String idLang = "";
        String idTheso = "";
        HikariDataSource ds = connect();         
//        @GET()
//        @Path("param")
//        public String param(@Context UriInfo uri) {
//            String result = "";
//            result += "path: " + uri.getPath();
//            for (Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
//                for (String value : e.getValue()) {
//                    result += " ";
//                    result += e.getKey() + "=" + value;
//                }
//            }
//            return result;
//        }        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        for (Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("value")) 
                    value = valeur;
                if(e.getKey().equalsIgnoreCase("th")) 
                    idTheso = valeur;                 
            }
        }
        if(idLang.isEmpty() || value.isEmpty() || idTheso.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageBadRequest()).type(MediaType.APPLICATION_JSON).build();            
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
        StringBuffer skos = ConceptByValueToSkos(ds, value, idTheso, idLang);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }
        String json = getJsonDatas(ds, value, idLang, idTheso);
        ds.close();
        return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();        
    }        
    
    /**
     * Permet de retourner les Concepts par value (en précisant un thésaurus et
     * une langue)
     *
     * @param uri
     * @return
     */
    @Path("/searchJson")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response searchJson(@Context UriInfo uri) {
        String value = "";
        String idLang = "";
        String idTheso = "";
        HikariDataSource ds = connect();         
//        @GET()
//        @Path("param")
//        public String param(@Context UriInfo uri) {
//            String result = "";
//            result += "path: " + uri.getPath();
//            for (Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
//                for (String value : e.getValue()) {
//                    result += " ";
//                    result += e.getKey() + "=" + value;
//                }
//            }
//            return result;
//        }        
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }        
        for (Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("value")) 
                    value = valeur;
                if(e.getKey().equalsIgnoreCase("th")) 
                    idTheso = valeur;                 
            }
        }
        if(idLang.isEmpty() || value.isEmpty() || idTheso.isEmpty()) {
            ds.close();
            return Response.status(Status.BAD_REQUEST).entity(messageBadRequest()).type(MediaType.APPLICATION_JSON).build();            
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
        StringBuffer skos = ConceptByValueToSkos(ds, value, idTheso, idLang);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }
        String json = getJsonDatas(ds, value, idLang, idTheso);
        ds.close();
        return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();        
    }    

    /**
     * Permet de retourner les Concepts par value (en précisant un thésaurus et
     * une langue)
     *
     * @param uri
     * @return
     */
    @Path("/searchJsonld")
    @GET
    //@Produces("text/plain")
    //@Produces("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response searchJsonld(@Context UriInfo uri) {
        String value = "";
        String idLang = "";
        String idTheso = "0";
        String idGroup = null;
        HikariDataSource ds = connect();  
//        @GET()
//        @Path("param")
//        public String param(@Context UriInfo uri) {
//            String result = "";
//            result += "path: " + uri.getPath();
//            for (Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
//                for (String value : e.getValue()) {
//                    result += " ";
//                    result += e.getKey() + "=" + value;
//                }
//            }
//            return result;
//        }        
        
        for (Entry<String, List<String>> e : uri.getQueryParameters().entrySet()) {
            for (String valeur : e.getValue()) {
                if(e.getKey().equalsIgnoreCase("lang")) 
                    idLang = valeur;
                if(e.getKey().equalsIgnoreCase("value")) 
                    value = valeur;
                if(e.getKey().equalsIgnoreCase("th")) 
                    idTheso = valeur;
                if(e.getKey().equalsIgnoreCase("group")) 
                    idGroup = valeur;                   
            }
        }
        
        
        if (!getStatusOfWebservices(ds, idTheso)) {
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
        StringBuffer skos;
        if(idGroup == null) {
            skos = ConceptByValueToSkos(ds, value, idTheso, idLang);
        } else {
            skos = ConceptByValueToSkos(ds, value, idLang, idGroup, idTheso);
        }
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }
        String json = getJsonLdDatas(ds, value, idLang, idTheso);
        ds.close();
        return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();        
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
        HikariDataSource ds = connect();
        if (!getStatusOfWebservices(ds, idTheso)) {
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
        StringBuffer skos = ConceptByValueToSkos(ds, value,
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
        JsonldHelper jsonHelper = new JsonldHelper();
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
        HikariDataSource ds = connect();
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = conceptsOfGroupToSkos(ds, idGroup, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
        JsonldHelper jsonHelper = new JsonldHelper();
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
        HikariDataSource ds = connect();
        if (!getStatusOfWebservices(ds, idTheso)) {
            ds.close();
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(messageJson()).type(MediaType.APPLICATION_JSON).build();
        }
        StringBuffer skos = groupsOfThesaurusToSkos(ds, idTheso);
        if (skos == null) {
            ds.close();
            return Response.ok(messageErreur()).header("Access-Control-Allow-Origin", "*").build();
        }
        if (skos.length() == 0) {
            ds.close();
            return Response.ok(messageEmptyJson()).header("Access-Control-Allow-Origin", "*").build();
        }          
        JsonldHelper jsonHelper = new JsonldHelper();
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
     * Temporaire à refaire (Miled)
     * */
    
    
    /**
     * 
     * @param idConcept
     * @param idTheso
     * @return 
     */
    private String getJsonDatas(
            HikariDataSource ds,
            String value,
            String idLang, String idTheso){
        ArrayList <String> listId = getListId(ds, value, idLang, idTheso);
        
        NodePreference nodePreference1 =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference != null){
            ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
            exportRdf4jHelper.setNodePreference(nodePreference1);
            exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso,nodePreference1.getCheminSite());
            exportRdf4jHelper.setNodePreference(nodePreference1);
            for (String idConcept : listId) {
                exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
            }
            WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());

            ByteArrayOutputStream out;
            out = new ByteArrayOutputStream();
            Rio.write(writeRdf4j.getModel(), out, RDFFormat.RDFJSON);
           // System.out.println(out.toString());
            return out.toString();
        }
        return messageEmptyJson();
    }
    
    private String getJsonLdDatas(
            HikariDataSource ds,
            String value,
            String idLang, String idTheso){
        ArrayList <String> listId = getListId(ds, value, idLang, idTheso);
        
        NodePreference nodePreference1 =  new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if(nodePreference != null){
            ExportRdf4jHelper exportRdf4jHelper = new ExportRdf4jHelper();
            exportRdf4jHelper.setNodePreference(nodePreference1);
            exportRdf4jHelper.setInfos(ds, "dd-mm-yyyy", false, idTheso,nodePreference1.getCheminSite());
            exportRdf4jHelper.setNodePreference(nodePreference1);
            for (String idConcept : listId) {
                exportRdf4jHelper.addSignleConcept(idTheso, idConcept);
            }
            WriteRdf4j writeRdf4j = new WriteRdf4j(exportRdf4jHelper.getSkosXmlDocument());

            ByteArrayOutputStream out;
            out = new ByteArrayOutputStream();
            Rio.write(writeRdf4j.getModel(), out, RDFFormat.JSONLD);
           // System.out.println(out.toString());
            return out.toString();
        }
        return messageEmptyJson();
    }    
     /**
     * Fin Temporaire à refaire (Miled)
     */
    
    
    
    
    private ArrayList<String> getListId( 
            HikariDataSource ds,
            String value, String idLang, String idTheso) {
        ArrayList <String> listId = new ArrayList<>();
        ArrayList<NodeSearch> listRes = new SearchHelper().searchTermNew(ds, value, idLang, idTheso, "", 1, false);
        for (NodeSearch listRe : listRes) {
            listId.add(listRe.getIdConcept());
        }
        return listId;
    }
            
    
    /**
     * Fonction qui permet de récupérer un concept skos par identifiant
     *
     * @param idConcept
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer conceptToSkos(
            HikariDataSource ds,
            String idConcept, String idTheso) {

        if (ds == null) {
            return null;
        }

        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setNodePreference(nodePreference);
        
        
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
    private StringBuffer conceptToSkosFromArk(
            HikariDataSource ds,
            String arkId, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setNodePreference(nodePreference);
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
    private StringBuffer groupToSkos(
            HikariDataSource ds,
            String idGroup, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
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
    private StringBuffer conceptsOfGroupToSkos(
            HikariDataSource ds,
            String idGroup, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        exportFromBDD.setNodePreference(nodePreference);
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
    private StringBuffer brancheOfConceptsToSkos(
            HikariDataSource ds,
            String idConcept, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        exportFromBDD.setNodePreference(nodePreference);
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
    private StringBuffer brancheOfConceptsToSkosTop(
            HikariDataSource ds,
            String idConcept, String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
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
        exportFromBDD.setNodePreference(nodePreference);
        
        StringBuffer skos = exportFromBDD.exportConceptByLot(ds, idTheso, tabId);
        return skos;
    }

    /**
     * Fonction qui permet de récupérer les groupes d'un thésauurs
     *
     * @param idThesaurus
     * @return skos
     */
    private StringBuffer groupsOfThesaurusToSkos(
            HikariDataSource ds,
            String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
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
    private StringBuffer ConceptByValueToSkos(
            HikariDataSource ds,
            String value, String idTheso, String lang) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        exportFromBDD.setNodePreference(nodePreference);

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
    private StringBuffer ConceptByValueToSkos(
            HikariDataSource ds,
            String value,
            String lang,
            String idGroup,
            String idTheso) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
        if (nodePreference == null) {
            return null;
        }

        ExportFromBDD exportFromBDD = new ExportFromBDD();
        exportFromBDD.setServerArk(nodePreference.getServeurArk());
        exportFromBDD.setServerAdress(nodePreference.getCheminSite());
        exportFromBDD.setNodePreference(nodePreference);

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
    private StringBuffer brancheOfConceptsToSkosFromDate(
            HikariDataSource ds, 
            String idTheso, String date) {

        if (ds == null) {
            return null;
        }
        nodePreference = new PreferencesHelper().getThesaurusPreferences(ds, idTheso);
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
        exportFromBDD.setNodePreference(nodePreference);

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

    private String messageBadRequest() {
        String message = "{\n"
                + "\n"
                + "    \"résultat_fr\":\"reformuler votre requête !!\",\n"
                + "    \"result_en\":\" bad request !!\"\n"
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
