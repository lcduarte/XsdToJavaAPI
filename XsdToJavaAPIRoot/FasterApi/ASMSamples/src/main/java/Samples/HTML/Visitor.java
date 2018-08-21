package Samples.HTML;


public abstract class Visitor {

    public abstract void visitElement(String elementName);

    public abstract void visitAttribute(String attributeName, String attributeValue);

    public abstract void visitParent(String elementName);

    public abstract void visitText(String text);

    public abstract void visitComment(String comment);

}

