package Samples;

public class H1 extends AbstractElement<H1> implements IFlowContent<H1> {
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
    public H1 cloneElem() {
        return this.clone(new H1());
    }

}
