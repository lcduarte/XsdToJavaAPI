package Samples.HTML;

public class Div<P extends Element> extends AbstractElement<Div<P>, P> implements CommonAttributeGroup<Div<P>, P>, MiniFlowContent<Div<P>, P> {

    public Div(Visitor visitor) {
        super(visitor, "div", 0);
        visitor.visit(this);
    }

    public Div(Visitor visitor, int depth) {
        super(visitor, "div", depth);
        visitor.visit(this);
    }

    public Div(P parent) {
        super(parent, "div");
        visitor.visit(this);
    }

    public Div(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    @Override
    public Div<P> self() {return this; }

    public Div<P> addSomeAttribute(String val){
        getVisitor().visit(new SomeAttribute(val));
        return this.self();
    }
}