package Samples.HTML;


public abstract class Visitor {

    public abstract void visitElement(Element element);

    public abstract void visitAttribute(String attributeName, String attributeValue);

    public abstract void visitParent(Element elementName);

    public abstract <R> void visitText(Text<? extends Element, R> text);

    public abstract <R> void visitComment(Text<? extends Element, R> comment);

    public void visitOpenDynamic() {}

    public void visitCloseDynamic() {}

    public <P extends Element> void visitElementDiv(Div<P> div){
        visitElement(div);
    }

    public <P extends Element> void visitParentDiv(Div<P> div){
        visitParent(div);
    }

    public void visitAttributeSomeAttribute(String attributeValue){
        visitAttribute("someAttribute", attributeValue);
    }

}

