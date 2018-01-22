package Samples;

public class H1<M> extends AbstractElement<H1, M> implements IFlowContent<H1, M> {
    public H1() {}

    public H1(IElement parent) {
        super(parent);
    }

    public H1(IElement parent, String id) {
        super(parent, id);
    }

    public H1 self() {return this; }

    @Override
    public void accept(Visitor visitor) {
        visitor.initVisit(this);

        getChildren().forEach(child -> child.accept(visitor));

        visitor.endVisit(this);
    }

    @Override
    public H1<M> cloneElem() {
        return this.clone(new H1<M>());
    }

}
