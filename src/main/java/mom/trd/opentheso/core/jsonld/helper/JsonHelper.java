/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.jsonld.helper;

import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.imports.old.ReadFileSKOS;
import skos.SKOSXmlDocument;

/**
 *
 * @author miled.rousset
 */
public class JsonHelper {

    public JsonHelper() {
    }
   
    public SKOSXmlDocument readSkosDocument(StringBuffer skosStringBuffer){
        SKOSXmlDocument sKOSXmlDocument;
        try {
            ReadFileSKOS readFileSKOS = new ReadFileSKOS();
            sKOSXmlDocument = readFileSKOS.readStringBuffer(skosStringBuffer);
            return sKOSXmlDocument;
        } catch (Exception ex) {
            Logger.getLogger(JsonHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public StringBuffer getJsonLd(SKOSXmlDocument sKOSXmlDocument){
        if(sKOSXmlDocument == null) return null;
        SkosToJsonld skosToJsonld = new SkosToJsonld();
        StringBuffer jsonLdDocument = skosToJsonld.getJsonLdDocument(sKOSXmlDocument);
        return jsonLdDocument;
    }
    
    public StringBuffer getJsonLdForConceptScheme(SKOSXmlDocument sKOSXmlDocument){
        if(sKOSXmlDocument == null) return null;
        SkosToJsonld skosToJsonld = new SkosToJsonld();
        StringBuffer jsonLdDocument = skosToJsonld.getJsonLdConceptScheme(sKOSXmlDocument);
        return jsonLdDocument;
    }
    
    
    public StringBuffer getJsonLdForSchemaOrg(SKOSXmlDocument sKOSXmlDocument){
        if(sKOSXmlDocument == null) return null;
        SkosToJsonldSchemaOrg skosToJsonld = new SkosToJsonldSchemaOrg();
        StringBuffer jsonLdDocumentSchema = skosToJsonld.getJsonLdDocumentSchemaOrg(sKOSXmlDocument);
        return jsonLdDocumentSchema;
    }
    
    public StringBuffer getJsonLdForSchemaOrgForConceptScheme(SKOSXmlDocument sKOSXmlDocument){
        if(sKOSXmlDocument == null) return null;
        SkosToJsonldSchemaOrg skosToJsonld = new SkosToJsonldSchemaOrg();
        StringBuffer jsonLdDocument = skosToJsonld.getJsonLdConceptScheme(sKOSXmlDocument);
        return jsonLdDocument;
    }    
    
}
