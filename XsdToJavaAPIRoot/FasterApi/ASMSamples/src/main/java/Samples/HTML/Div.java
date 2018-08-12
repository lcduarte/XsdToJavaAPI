package Samples.HTML;

public final class Div<P extends Element> extends AbstractElement<Div<P>, P> implements CommonAttributeGroup<Div<P>, P>, MiniFlowContent<Div<P>, P> {

    public Div(Visitor visitor) {
        super(visitor, "div");
        visitor.visit(this);
    }

    public Div(P parent) {
        super(parent, "div");
        visitor.visit(this);
    }

    @Override
    public final Div<P> self() {return this; }

    public final Div<P> addSomeAttribute(String val){
        getVisitor().visit(new SomeAttribute(val));
        return this.self();
    }
}