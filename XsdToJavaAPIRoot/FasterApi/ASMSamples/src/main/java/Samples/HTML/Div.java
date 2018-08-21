package Samples.HTML;

public final class Div<P extends Element> implements CommonAttributeGroup<Div<P>, P>, MiniFlowContent<Div<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public Div(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        visitor.visitElement("div");
    }

    public Div(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        visitor.visitElement("div");
    }

    public final Div<P> addSomeAttribute(String val){
        //SomeAttribute.validateRestrictions(val);
        visitor.visitAttribute("SomeAttribute", val);
        return this;
    }

    @Override
    public final Div<P> self() { return this; }

    @Override
    public P º() {
        visitor.visitParent("div");
        return parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public final Visitor getVisitor() {
        return visitor;
    }

    @Override
    public String getName() {
        return "div";
    }
}