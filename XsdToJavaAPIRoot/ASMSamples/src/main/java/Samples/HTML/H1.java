package Samples.HTML;

public class H1<P extends IElement> extends AbstractElement<H1<P>, P> implements IFlowContent<H1<P>, P> {
    public H1() {}

    public H1(P parent) {
        super(parent);
    }

    public H1 self() {return this; }

    @Override
    public void accept(Visitor visitor) {
        visitor.initVisit(this);

        getChildren().forEach(child -> child.accept(visitor));

        visitor.endVisit(this);
    }

    @Override
    public H1 cloneElem() {
        return this.clone(new H1());
    }

}
