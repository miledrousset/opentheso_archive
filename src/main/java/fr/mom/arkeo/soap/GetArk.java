
package fr.mom.arkeo.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour getArk complex type.
 * 
 * <p>Le fragment de sch\u00e9ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="getArk"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="arg0" type="{http://soap.arkeo.mom.fr/}account" minOccurs="0"/&gt;
 *         &lt;element name="arg1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getArk", propOrder = {
    "arg0",
    "arg1"
})
public class GetArk {

    protected Account arg0;
    protected String arg1;

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 arg0.
     * 
     * @return
     *     possible object is
     *     {@link Account }
     *     
     */
    public Account getArg0() {
        return arg0;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 arg0.
     * 
     * @param value
     *     allowed object is
     *     {@link Account }
     *     
     */
    public void setArg0(Account value) {
        this.arg0 = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 arg1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg1() {
        return arg1;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 arg1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg1(String value) {
        this.arg1 = value;
    }

}
