
package fr.mom.arkeo.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour createArkResponse complex type.
 * 
 * <p>Le fragment de sch?ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="createArkResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://soap.arkeo.mom.fr/}ark" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createArkResponse", propOrder = {
    "_return"
})
public class CreateArkResponse {

    @XmlElement(name = "return")
    protected Ark _return;

    /**
     * Obtient la valeur de la propri?t? return.
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
     * D?finit la valeur de la propri?t? return.
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
