package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdSchemaVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XsdSchema extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:schema";
    public static final String XS_TAG = "xs:schema";

    private XsdSchemaVisitor visitor = new XsdSchemaVisitor(this);

    private String attributeFormDefault;
    private String elementFormDefault;
    private String blockDefault;
    private String finalDefault;
    private String targetNamespace;
    private String version;
    private String xmlns;

    private List<XsdAbstractElement> elements = new ArrayList<>();

    private XsdSchema(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam){
        super(parser, elementFieldsMapParam);
    }

    /**
     * Sets all the fields of the {@link XsdSchema} instance. Most values don't have default values except the
     * {@link XsdAttribute#use} field. Regarding the {@link XsdAttribute#type} field we check if the value present is a
     * built-in type, if not we create an {@link UnsolvedReference} object to be resolved at a later time in the
     * parsing process.
     * @param elementFieldsMapParam The Map object containing all the information previously contained in the parsed Node.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.attributeFormDefault = elementFieldsMap.getOrDefault(ATTRIBUTE_FORM_DEFAULT, "unqualified");
        this.elementFormDefault = elementFieldsMap.getOrDefault(ELEMENT_FORM_DEFAULT, "unqualified");
        this.blockDefault = elementFieldsMap.getOrDefault(BLOCK_DEFAULT, "");
        this.finalDefault = elementFieldsMap.getOrDefault(FINAL_DEFAULT, "");
        this.targetNamespace = elementFieldsMap.getOrDefault(TARGET_NAMESPACE, targetNamespace);
        this.version = elementFieldsMap.getOrDefault(VERSION, version);
        this.xmlns = elementFieldsMap.getOrDefault(XMLNS, xmlns);
    }

    @Override
    public XsdAbstractElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    public Stream<XsdAbstractElement> getXsdElements() {
        return elements.stream();
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node) {
        return xsdParseSkeleton(node, new XsdSchema(parser, convertNodeMap(node.getAttributes())));
    }

    public void add(XsdInclude element) {
        elements.add(element);
    }

    public void add(XsdImport element) {
        elements.add(element);
    }

    public void add(XsdAnnotation element) {
        elements.add(element);
    }

    public void add(XsdSimpleType element) {
        elements.add(element);
    }

    public void add(XsdComplexType element) {
        elements.add(element);
    }

    public void add(XsdGroup element) {
        elements.add(element);
    }

    public void add(XsdAttributeGroup element) {
        elements.add(element);
    }

    public void add(XsdElement element) {
        elements.add(element);
    }

    public void add(XsdAttribute element) {
        elements.add(element);
    }

    @SuppressWarnings("unused")
    public String getAttributeFormDefault() {
        return attributeFormDefault;
    }

    @SuppressWarnings("unused")
    public String getElementFormDefault() {
        return elementFormDefault;
    }

    @SuppressWarnings("unused")
    public String getBlockDefault() {
        return blockDefault;
    }

    @SuppressWarnings("unused")
    public String getFinalDefault() {
        return finalDefault;
    }

    @SuppressWarnings("unused")
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public String getVersion() {
        return version;
    }

    public String getXmlns() {
        return xmlns;
    }
}
