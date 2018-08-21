package Samples.HTML;

public final class Div<P extends AbstractElement> extends AbstractElement<Div<P>, P> implements CommonAttributeGroup, MiniFlowContent {

    public static <P extends AbstractElement> P º() {
        visitor.visitParent("div");
        return null;
    }

    public static <P extends AbstractElement> Div<P> addSomeAttribute(String val){
        SomeAttribute.validateRestrictions(val);
        visitor.visitAttribute("SomeAttribute", val);
        return null;
    }

    public static <T extends AbstractElement<T, ?>> Div<T> div() {
        AbstractElement.visitor.visitElement("div");
        return null;

    }

    public static <T extends AbstractElement<T, ?>> T text(String text){
        AbstractElement.visitor.visitText(text);
        return null;
    }

}