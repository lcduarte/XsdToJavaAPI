package Samples.HTML;

public class Div<P extends IElement> extends AbstractElement<Div<P>, P> implements ICommonAttributeGroup<Div<P>, P>, IFlowContent<Div<P>, P> {

    public Div() {
        super("div");
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
        return this.clone(new Div<P>());
    }

    public Div<P> addSomeAttribute(SomeAttribute val){
        return this;
    }
}