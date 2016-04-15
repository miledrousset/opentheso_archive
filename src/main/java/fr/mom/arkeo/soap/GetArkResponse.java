
package fr.mom.arkeo.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour getArkResponse complex type.
 * 
 * <p>Le fragment de sch\u00e9ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="getArkResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://soap.arkeo.mom.fr/}ark" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getArkResponse", propOrder = {
    "_return"
})
public class GetArkResponse {

    @XmlElement(name = "return")
    protected Ark _return;

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 return.
     * 
     * @return
     *     possible object is
     *     {@link Ark }
     *     
     */
    public Ark getReturn() {
        return _return;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 return.
     * 
     * @param value
     *     allowed object is
     *     {@link Ark }
     *     
     */
    public void setReturn(Ark value) {
        this._return = value;
    }

}
