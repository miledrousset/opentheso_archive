
package fr.mom.arkeo.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ark complex type.
 * 
 * <p>Le fragment de sch\u00e9ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ark"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="creator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dcElements" type="{http://soap.arkeo.mom.fr/}dcElement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="linkup" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="naan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="owner" type="{http://soap.arkeo.mom.fr/}user" minOccurs="0"/&gt;
 *         &lt;element name="qualifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="qualifiers" type="{http://soap.arkeo.mom.fr/}arkQualifier" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="redirect" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="saved" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="urlTarget" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userArkId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ark", propOrder = {
    "ark",
    "creator",
    "date",
    "dcElements",
    "description",
    "language",
    "linkup",
    "naan",
    "name",
    "owner",
    "qualifier",
    "qualifiers",
    "redirect",
    "saved",
    "subject",
    "title",
    "type",
    "urlTarget",
    "userArkId"
})
public class Ark {

    protected String ark;
    protected String creator;
    protected String date;
    @XmlElement(nillable = true)
    protected List<DcElement> dcElements;
    protected String description;
    protected String language;
    protected boolean linkup;
    protected String naan;
    protected String name;
    protected User owner;
    protected String qualifier;
    @XmlElement(nillable = true)
    protected List<ArkQualifier> qualifiers;
    protected boolean redirect;
    protected boolean saved;
    protected String subject;
    protected String title;
    protected String type;
    protected String urlTarget;
    protected int userArkId;

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 ark.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArk() {
        return ark;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 ark.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArk(String value) {
        this.ark = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 creator.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreator() {
        return creator;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 creator.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreator(String value) {
        this.creator = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 date.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 date.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the dcElements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dcElements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDcElements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DcElement }
     * 
     * 
     */
    public List<DcElement> getDcElements() {
        if (dcElements == null) {
            dcElements = new ArrayList<DcElement>();
        }
        return this.dcElements;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 language.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 language.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 linkup.
     * 
     */
    public boolean isLinkup() {
        return linkup;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 linkup.
     * 
     */
    public void setLinkup(boolean value) {
        this.linkup = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 naan.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNaan() {
        return naan;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 naan.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNaan(String value) {
        this.naan = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 owner.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getOwner() {
        return owner;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 owner.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setOwner(User value) {
        this.owner = value;
    }

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
     * Gets the value of the qualifiers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qualifiers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQualifiers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArkQualifier }
     * 
     * 
     */
    public List<ArkQualifier> getQualifiers() {
        if (qualifiers == null) {
            qualifiers = new ArrayList<ArkQualifier>();
        }
        return this.qualifiers;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 redirect.
     * 
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 redirect.
     * 
     */
    public void setRedirect(boolean value) {
        this.redirect = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 saved.
     * 
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 saved.
     * 
     */
    public void setSaved(boolean value) {
        this.saved = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 subject.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 subject.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 title.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 title.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
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

    /**
     * Obtient la valeur de la propri\u00e9t\u00e9 userArkId.
     * 
     */
    public int getUserArkId() {
        return userArkId;
    }

    /**
     * D\u00e9finit la valeur de la propri\u00e9t\u00e9 userArkId.
     * 
     */
    public void setUserArkId(int value) {
        this.userArkId = value;
    }

}
