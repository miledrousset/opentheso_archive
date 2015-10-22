
package fr.mom.arkeo.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour updateArk complex type.
 * 
 * <p>Le fragment de sch?ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="updateArk">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://soap.arkeo.mom.fr/}account" minOccurs="0"/>
 *         &lt;element name="arg1" type="{http://soap.arkeo.mom.fr/}ark" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateArk", propOrder = {
    "arg0",
    "arg1"
})
public class UpdateArk {

    protected Account arg0;
    protected Ark arg1;

    /**
     * Obtient la valeur de la propri?t? arg0.
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
     * D?finit la valeur de la propri?t? arg0.
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
     * Obtient la valeur de la propri?t? arg1.
     * 
     * @return
     *     possible object is
     *     {@link Ark }
     *     
     */
    public Ark getArg1() {
        return arg1;
    }

    /**
     * D?finit la valeur de la propri?t? arg1.
     * 
     * @param value
     *     allowed object is
     *     {@link Ark }
     *     
     */
    public void setArg1(Ark value) {
        this.arg1 = value;
    }

}
