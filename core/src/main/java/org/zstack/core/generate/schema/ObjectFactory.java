//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.04.17 at 11:28:57 PM PDT 
//


package org.zstack.core.generate.schema;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.zstack.configuration.testlink.schema package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.zstack.configuration.testlink.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RequirementSpecification.ReqSpec.Requirement }
     * 
     */
    public RequirementSpecification.ReqSpec.Requirement createRequirementSpecificationReqSpecRequirement() {
        return new RequirementSpecification.ReqSpec.Requirement();
    }

    /**
     * Create an instance of {@link RequirementCategory.Req }
     * 
     */
    public RequirementCategory.Req createRequirementCategoryReq() {
        return new RequirementCategory.Req();
    }

    /**
     * Create an instance of {@link RequirementCategory }
     * 
     */
    public RequirementCategory createRequirementCategory() {
        return new RequirementCategory();
    }

    /**
     * Create an instance of {@link RequirementCategory.Req.Api }
     * 
     */
    public RequirementCategory.Req.Api createRequirementCategoryReqApi() {
        return new RequirementCategory.Req.Api();
    }

    /**
     * Create an instance of {@link RequirementSpecification.ReqSpec }
     * 
     */
    public RequirementSpecification.ReqSpec createRequirementSpecificationReqSpec() {
        return new RequirementSpecification.ReqSpec();
    }

    /**
     * Create an instance of {@link RequirementSpecification }
     * 
     */
    public RequirementSpecification createRequirementSpecification() {
        return new RequirementSpecification();
    }

}
