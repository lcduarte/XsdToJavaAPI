package Samples.HTML;

public class Div<P extends Element> extends AbstractElement<Div<P>, P> implements CommonAttributeGroup<Div<P>, P>, MiniFlowContent<Div<P>, P> {

    public Div() {
        super("div", 0);
    }

    public Div(String name, int depth) {
        super(name, depth);
    }

    public Div(P parent) {
        super(parent, "div");
    }

    public Div(P parent, String name){
        super(parent, name);
    }

    @Override
    public Div<P> self() {return this; }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Div<P> cloneElem() {
        return clone(new Div<>(parent));
    }

    public Div<P> addSomeAttribute(String val){
        SomeAttribute attribute = new SomeAttribute(val);
        addAttr(attribute);
        return this;
    }
}