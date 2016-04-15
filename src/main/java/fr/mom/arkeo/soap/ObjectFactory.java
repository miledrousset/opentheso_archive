
package fr.mom.arkeo.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.mom.arkeo.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Authentification_QNAME = new QName("http://soap.arkeo.mom.fr/", "authentification");
	private final static QName _AuthentificationResponse_QNAME = new QName("http://soap.arkeo.mom.fr/", "authentificationResponse");
	private final static QName _CreateArk_QNAME = new QName("http://soap.arkeo.mom.fr/", "createArk");
    private final static QName _CreateArkResponse_QNAME = new QName("http://soap.arkeo.mom.fr/", "createArkResponse");
    private final static QName _GetArk_QNAME = new QName("http://soap.arkeo.mom.fr/", "getArk");
    private final static QName _GetArkResponse_QNAME = new QName("http://soap.arkeo.mom.fr/", "getArkResponse");
    private final static QName _UpdateArk_QNAME = new QName("http://soap.arkeo.mom.fr/", "updateArk");
    private final static QName _UpdateArkResponse_QNAME = new QName("http://soap.arkeo.mom.fr/", "updateArkResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.mom.arkeo.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Authentification }
     * 
     */
    public Authentification createAuthentification() {
        return new Authentification();
    }

	/**
     * Create an instance of {@link AuthentificationResponse }
     * 
     */
    public AuthentificationResponse createAuthentificationResponse() {
        return new AuthentificationResponse();
    }

	/**
     * Create an instance of {@link CreateArk }
     * 
     */
    public CreateArk createCreateArk() {
        return new CreateArk();
    }

    /**
     * Create an instance of {@link CreateArkResponse }
     * 
     */
    public CreateArkResponse createCreateArkResponse() {
        return new CreateArkResponse();
    }

    /**
     * Create an instance of {@link GetArk }
     * 
     */
    public GetArk createGetArk() {
        return new GetArk();
    }

    /**
     * Create an instance of {@link GetArkResponse }
     * 
     */
    public GetArkResponse createGetArkResponse() {
        return new GetArkResponse();
    }

    /**
     * Create an instance of {@link UpdateArk }
     * 
     */
    public UpdateArk createUpdateArk() {
        return new UpdateArk();
    }

    /**
     * Create an instance of {@link UpdateArkResponse }
     * 
     */
    public UpdateArkResponse createUpdateArkResponse() {
        return new UpdateArkResponse();
    }

    /**
     * Create an instance of {@link Account }
     * 
     */
    public Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link Group }
     * 
     */
    public Group createGroup() {
        return new Group();
    }

    /**
     * Create an instance of {@link User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Authentification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "authentification")
    public JAXBElement<Authentification> createAuthentification(Authentification value) {
        return new JAXBElement<Authentification>(_Authentification_QNAME, Authentification.class, null, value);
    }

	/**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthentificationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "authentificationResponse")
    public JAXBElement<AuthentificationResponse> createAuthentificationResponse(AuthentificationResponse value) {
        return new JAXBElement<AuthentificationResponse>(_AuthentificationResponse_QNAME, AuthentificationResponse.class, null, value);
    }

	/**
     * Create an instance of {@link Ark }
     * 
     */
    public Ark createArk() {
        return new Ark();
    }

    /**
     * Create an instance of {@link DcElement }
     * 
     */
    public DcElement createDcElement() {
        return new DcElement();
    }

    /**
     * Create an instance of {@link ArkQualifier }
     * 
     */
    public ArkQualifier createArkQualifier() {
        return new ArkQualifier();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateArk }{@code >}}
     * 
     */
//    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "createArk")
    public JAXBElement<CreateArk> createCreateArk(CreateArk value) {
        return new JAXBElement<CreateArk>(_CreateArk_QNAME, CreateArk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateArkResponse }{@code >}}
     * 
     */
//    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "createArkResponse")
    public JAXBElement<CreateArkResponse> createCreateArkResponse(CreateArkResponse value) {
        return new JAXBElement<CreateArkResponse>(_CreateArkResponse_QNAME, CreateArkResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArk }{@code >}}
     * 
     */
//    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "getArk")
    public JAXBElement<GetArk> createGetArk(GetArk value) {
        return new JAXBElement<GetArk>(_GetArk_QNAME, GetArk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArkResponse }{@code >}}
     * 
     */
//    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "getArkResponse")
    public JAXBElement<GetArkResponse> createGetArkResponse(GetArkResponse value) {
        return new JAXBElement<GetArkResponse>(_GetArkResponse_QNAME, GetArkResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateArk }{@code >}}
     * 
     */
//    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "updateArk")
    public JAXBElement<UpdateArk> createUpdateArk(UpdateArk value) {
        return new JAXBElement<UpdateArk>(_UpdateArk_QNAME, UpdateArk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateArkResponse }{@code >}}
     * 
     */
//    @XmlElementDecl(namespace = "http://soap.arkeo.mom.fr/", name = "updateArkResponse")
    public JAXBElement<UpdateArkResponse> createUpdateArkResponse(UpdateArkResponse value) {
        return new JAXBElement<UpdateArkResponse>(_UpdateArkResponse_QNAME, UpdateArkResponse.class, null, value);
    }

}
