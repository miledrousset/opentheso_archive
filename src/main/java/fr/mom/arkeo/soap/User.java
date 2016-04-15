
package fr.mom.arkeo.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour user complex type.
 * 
 * <p>Le fragment de sch\u00e9ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="user"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dbId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lastname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="uid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userGroup" type="{http://soap.arkeo.mom.fr/}group" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "user", propOrder = {
    "dbId",
    "firstname",
    "lastname",
    "mail",
    "uid",
    "user",
    "userGroup"
})
public class User {

    protected int dbId;
    protected String firstname;
    protected String lastname;
    protected String mail;
    protected String uid;
    protected String user;
    protected Group userGroup;

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 dbId.
     * 
     */
    public int getDbId() {
        return dbId;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 dbId.
     * 
     */
    public void setDbId(int value) {
        this.dbId = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 firstname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 firstname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstname(String value) {
        this.firstname = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 lastname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 lastname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastname(String value) {
        this.lastname = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 mail.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMail() {
        return mail;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 mail.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMail(String value) {
        this.mail = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 uid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUid() {
        return uid;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 uid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUid(String value) {
        this.uid = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 user.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 user.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 userGroup.
     * 
     * @return
     *     possible object is
     *     {@link Group }
     *     
     */
    public Group getUserGroup() {
        return userGroup;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 userGroup.
     * 
     * @param value
     *     allowed object is
     *     {@link Group }
     *     
     */
    public void setUserGroup(Group value) {
        this.userGroup = value;
    }

}
