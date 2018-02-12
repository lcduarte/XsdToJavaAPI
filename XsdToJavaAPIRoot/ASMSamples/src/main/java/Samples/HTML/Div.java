package Samples.HTML;

public class Div<P extends IElement> extends AbstractElement<Div<P>, P> implements ICommonAttributeGroup<Div<P>, P>, IFlowContent<Div<P>, P> {

    public Div() {
        super();
    }

    public Div(P parent) {
        super(parent);
    }

    @Override
    public Div self() {return this; }

    @Override
    public void accept(Visitor visitor) {
        visitor.initVisit(this);

        getChildren().forEach(child -> child.accept(visitor));

        visitor.endVisit(this);
    }

    @Override
    public Div<P> cloneElem() {
        return this.clone(new Div<P>());
    }

    public Div<P> addSomeAttribute(SomeAttribute val){
        return this;
    }
}