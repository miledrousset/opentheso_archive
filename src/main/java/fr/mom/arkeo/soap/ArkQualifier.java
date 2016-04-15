
package fr.mom.arkeo.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour arkQualifier complex type.
 * 
 * <p>Le fragment de sch\u00e9ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="arkQualifier"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="qualifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="urlTarget" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arkQualifier", propOrder = {
    "qualifier",
    "urlTarget"
})
public class ArkQualifier {

    protected String qualifier;
    protected String urlTarget;

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 qualifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 qualifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualifier(String value) {
        this.qualifier = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 urlTarget.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlTarget() {
        return urlTarget;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 urlTarget.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlTarget(String value) {
        this.urlTarget = value;
    }

}
