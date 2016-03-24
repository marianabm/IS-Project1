//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2016.03.08 às 09:53:00 PM GMT 
//


package generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}citations"/>
 *         &lt;element ref="{}publications"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "citations",
    "publications"
})
@XmlRootElement(name = "researcher")
public class Researcher {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected Citations citations;
    @XmlElement(required = true)
    protected Publications publications;

    /**
     * Obtém o valor da propriedade name.
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
     * Define o valor da propriedade name.
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
     * Obtém o valor da propriedade citations.
     * 
     * @return
     *     possible object is
     *     {@link Citations }
     *     
     */
    public Citations getCitations() {
        return citations;
    }

    /**
     * Define o valor da propriedade citations.
     * 
     * @param value
     *     allowed object is
     *     {@link Citations }
     *     
     */
    public void setCitations(Citations value) {
        this.citations = value;
    }

    /**
     * Obtém o valor da propriedade publications.
     * 
     * @return
     *     possible object is
     *     {@link Publications }
     *     
     */
    public Publications getPublications() {
        return publications;
    }

    /**
     * Define o valor da propriedade publications.
     * 
     * @param value
     *     allowed object is
     *     {@link Publications }
     *     
     */
    public void setPublications(Publications value) {
        this.publications = value;
    }

}
