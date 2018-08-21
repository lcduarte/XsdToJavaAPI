package Samples.HTML;

public interface CommonAttributeGroup extends FlowContent {

    static <T extends AbstractElement> T addAttrClass(String var1) {
        SomeAttribute.validateRestrictions(var1);
        AbstractElement.visitor.visitAttribute("SomeAttribute", var1);
        return null;
    }

}
