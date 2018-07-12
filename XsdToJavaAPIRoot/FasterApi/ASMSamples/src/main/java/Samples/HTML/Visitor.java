package Samples.HTML;


public abstract class Visitor {

    public abstract void visit(Element elem);

    abstract void visit(BaseAttribute attr);

    abstract void visitParent(Element element);

    abstract void visit(Text elem);

    abstract void visit(Comment elem);

}

