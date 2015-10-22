
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
 * <p>Le fragment de sch?ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ark">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcElements" type="{http://soap.arkeo.mom.fr/}dcElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="linkup" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="naan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="owner" type="{http://soap.arkeo.mom.fr/}user" minOccurs="0"/>
 *         &lt;element name="qualifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="redirect" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="saved" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urlTarget" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    "redirect",
    "saved",
    "subject",
    "title",
    "type",
    "urlTarget"
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
    protected boolean redirect;
    protected boolean saved;
    protected String subject;
    protected String title;
    protected String type;
    protected String urlTarget;

    /**
     * Obtient la valeur de la propri?t? ark.
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
     * D?finit la valeur de la propri?t? ark.
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
     * Obtient la valeur de la propri?t? creator.
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
     * D?finit la valeur de la propri?t? creator.
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
     * Obtient la valeur de la propri?t? date.
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
     * D?finit la valeur de la propri?t? date.
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
     * Obtient la valeur de la propri?t? description.
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
     * D?finit la valeur de la propri?t? description.
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
     * Obtient la valeur de la propri?t? language.
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
     * D?finit la valeur de la propri?t? language.
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
     * Obtient la valeur de la propri?t? linkup.
     * 
     */
    public boolean isLinkup() {
        return linkup;
    }

    /**
     * D?finit la valeur de la propri?t? linkup.
     * 
     */
    public void setLinkup(boolean value) {
        this.linkup = value;
    }

    /**
     * Obtient la valeur de la propri?t? naan.
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
     * D?finit la valeur de la propri?t? naan.
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
     * Obtient la valeur de la propri?t? name.
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
     * D?finit la valeur de la propri?t? name.
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
     * Obtient la valeur de la propri?t? owner.
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
     * D?finit la valeur de la propri?t? owner.
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
     * Obtient la valeur de la propri?t? qualifier.
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
     * D?finit la valeur de la propri?t? qualifier.
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
     * Obtient la valeur de la propri?t? redirect.
     * 
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * D?finit la valeur de la propri?t? redirect.
     * 
     */
    public void setRedirect(boolean value) {
        this.redirect = value;
    }

    /**
     * Obtient la valeur de la propri?t? saved.
     * 
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * D?finit la valeur de la propri?t? saved.
     * 
     */
    public void setSaved(boolean value) {
        this.saved = value;
    }

    /**
     * Obtient la valeur de la propri?t? subject.
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
     * D?finit la valeur de la propri?t? subject.
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
     * Obtient la valeur de la propri?t? title.
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
     * D?finit la valeur de la propri?t? title.
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
     * Obtient la valeur de la propri?t? type.
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
     * D?finit la valeur de la propri?t? type.
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
     * Obtient la valeur de la propri?t? urlTarget.
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
     * D?finit la valeur de la propri?t? urlTarget.
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
