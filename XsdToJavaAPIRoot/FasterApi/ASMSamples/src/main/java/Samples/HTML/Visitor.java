package Samples.HTML;


public abstract class Visitor {

    public abstract void visitElement(String elementName);

    public abstract void visitAttribute(String attributeName, String attributeValue);

    public abstract void visitParent(String elementName);

    public abstract <R> void visitText(R text);

    public abstract <R> void visitComment(R comment);

    public void visitElementDiv(){
        visitElement("div");
    }

    public void visitParentDiv(){
        visitParent("div");
    }

    public void visitAttributeSomeAttribute(String attributeValue){
        visitAttribute("someAttribute", attributeValue);
    }

}

