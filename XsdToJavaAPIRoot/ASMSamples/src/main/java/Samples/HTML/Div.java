package Samples.HTML;

public class Div<P extends IElement> extends AbstractElement<Div<P>, P> implements ICommonAttributeGroup<Div<P>, P>, IFlowContent<Div<P>, P> {

    public Div() {
        super("div");
    }

    //new
    protected Div(String name) {
        super(name);
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
        return null;
    }

    public Div<P> addSomeAttribute(String val){
        SomeAttribute attribute = new SomeAttribute(val);
        addAttr(attribute);
        return this;
    }
}