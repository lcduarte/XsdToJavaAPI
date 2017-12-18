package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import XsdElements.XsdRestrictionElements.*;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XsdRestriction extends XsdAbstractElement {

    public static final String TAG = "xsd:restriction";

    private RestrictionVisitor visitor = new RestrictionVisitor();

    private List<XsdAttributeGroup> attributeGroups = new ArrayList<>();
    private List<ReferenceBase> attributes = new ArrayList<>();

    private XsdSimpleType simpleType;

    private List<XsdEnumeration> enumeration = new ArrayList<>();
    private XsdFractionDigits fractionDigits;
    private XsdLength length;
    private XsdMaxExclusive maxExclusive;
    private XsdMaxInclusive maxInclusive;
    private XsdMaxLength maxLength;
    private XsdMinExclusive minExclusive;
    private XsdMinInclusive minInclusive;
    private XsdMinLength minLength;
    private XsdPattern pattern;
    private XsdTotalDigits totalDigits;
    private XsdWhiteSpace whiteSpace;

    private String base;

    private XsdRestriction(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    public XsdRestriction(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    private XsdRestriction(XsdAbstractElement parent) {
        super(parent);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.base = elementFieldsMap.getOrDefault(BASE, base);
        }
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        return null;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdRestriction(convertNodeMap(node.getAttributes())));
    }

    public List<XsdEnumeration> getEnumeration() {
        return enumeration;
    }

    public XsdFractionDigits getFractionDigits() {
        return fractionDigits;
    }

    public XsdLength getLength() {
        return length;
    }

    public XsdMaxExclusive getMaxExclusive() {
        return maxExclusive;
    }

    public XsdMaxInclusive getMaxInclusive() {
        return maxInclusive;
    }

    public XsdMaxLength getMaxLength() {
        return maxLength;
    }

    public XsdMinExclusive getMinExclusive() {
        return minExclusive;
    }

    public XsdMinInclusive getMinInclusive() {
        return minInclusive;
    }

    public XsdMinLength getMinLength() {
        return minLength;
    }

    public XsdPattern getPattern() {
        return pattern;
    }

    public XsdTotalDigits getTotalDigits() {
        return totalDigits;
    }

    public XsdWhiteSpace getWhiteSpace() {
        return whiteSpace;
    }

    class RestrictionVisitor extends Visitor{

        @Override
        public XsdAbstractElement getOwner() {
            return XsdRestriction.this;
        }

        @Override
        public void visit(XsdEnumeration element) {
            super.visit(element);

            XsdRestriction.this.enumeration.add(element);
        }

        @Override
        public void visit(XsdFractionDigits element) {
            super.visit(element);

            XsdRestriction.this.fractionDigits = element;
        }

        @Override
        public void visit(XsdLength element) {
            super.visit(element);

            XsdRestriction.this.length = element;
        }

        @Override
        public void visit(XsdMaxExclusive element) {
            super.visit(element);

            XsdRestriction.this.maxExclusive = element;
        }

        @Override
        public void visit(XsdMaxInclusive element) {
            super.visit(element);

            XsdRestriction.this.maxInclusive = element;
        }

        @Override
        public void visit(XsdMaxLength element) {
            super.visit(element);

            XsdRestriction.this.maxLength = element;
        }

        @Override
        public void visit(XsdMinExclusive element) {
            super.visit(element);

            XsdRestriction.this.minExclusive = element;
        }

        @Override
        public void visit(XsdMinInclusive element) {
            super.visit(element);

            XsdRestriction.this.minInclusive = element;
        }

        @Override
        public void visit(XsdMinLength element) {
            super.visit(element);

            XsdRestriction.this.minLength = element;
        }

        @Override
        public void visit(XsdPattern element) {
            super.visit(element);

            XsdRestriction.this.pattern = element;
        }

        @Override
        public void visit(XsdTotalDigits element) {
            super.visit(element);

            XsdRestriction.this.totalDigits = element;
        }

        @Override
        public void visit(XsdWhiteSpace element) {
            super.visit(element);

            XsdRestriction.this.whiteSpace = element;
        }
    }
}
