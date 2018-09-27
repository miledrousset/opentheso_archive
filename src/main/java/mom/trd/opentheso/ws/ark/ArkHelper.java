/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.ws.ark;

import java.util.Properties;
import mom.trd.opentheso.bdd.helper.nodes.NodeMetaData;
import mom.trd.opentheso.bdd.helper.nodes.NodePreference;

/**
 *
 * @author miled.rousset
 */
public class ArkHelper {
    
    private NodePreference nodePreference;
    private String message;
    
    private String idArk;
    private String idHandle;
    
    public ArkHelper(NodePreference nodePreference) {
        this.nodePreference = nodePreference;
    }
    
    /**
     * Permet de créer un identifiant ARK et un identifiant Handle (serveur MOM)
     * @param privateUri
     * @param nodeMetaData
     * @return
     */
    public boolean addIdArk(String privateUri,
            NodeMetaData nodeMetaData) {
        if (nodePreference == null) {
            return false;
        }
        if (!nodePreference.isUseArk()) {
            return false;
        }
        idArk = null;
        idHandle = null;
        
        //initialisation des valeurs 
        ArkClientRest arkClientRest = new ArkClientRest();

        Properties propertiesArk = new Properties();
        propertiesArk.setProperty("serverHost", "https://ark.mom.fr/Arkeo");//nodePreference.getServeurArk());
        propertiesArk.setProperty("idNaan", nodePreference.getIdNaan());
        propertiesArk.setProperty("user", nodePreference.getUserArk());
        propertiesArk.setProperty("password", nodePreference.getPassArk());
        arkClientRest.setPropertiesArk(propertiesArk);
        
       
        //préparation de l'objet Json
        String loginResp = arkClientRest.login();
        if(loginResp == null) return false;
        
        NodeJson nodeJson = new NodeJson();
        nodeJson.setUrlTarget(nodePreference.getCheminSite() + privateUri); //"?idc=" + idConcept + "&idt=" + idThesaurus);
        nodeJson.setTitle(nodeMetaData.getTitle());
        nodeJson.setCreator(nodeMetaData.getCreator());
        nodeJson.setDcElements(nodeMetaData.getDcElementsList()); // n'est pas encore exploité
        nodeJson.setType(nodePreference.getPrefixArk());  //pcrt : p= pactols, crt=code DCMI pour collection
        nodeJson.setLanguage(nodePreference.getSourceLang());
        nodeJson.setNaan(nodePreference.getIdNaan());
        nodeJson.setHandle_prefix("20.500.11859");
       
        // création de l'identifiant Ark et Handle 
        if(!arkClientRest.addArk(nodeJson.getJsonString())) return false;

        idArk = arkClientRest.getIdArk();
        idHandle = arkClientRest.getIdHandle();
        if (idArk == null) {
            message = "La connexion Ark a échouée";
            return false;
        }
        if (idHandle == null) {
            message = "La connexion Handle a échouée";
            return false;
        }
        return true;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdArk() {
        return idArk;
    }

    public void setIdArk(String idArk) {
        this.idArk = idArk;
    }

    public String getIdHandle() {
        return idHandle;
    }

    public void setIdHandle(String idHandle) {
        this.idHandle = idHandle;
    }
    
    
    
}
