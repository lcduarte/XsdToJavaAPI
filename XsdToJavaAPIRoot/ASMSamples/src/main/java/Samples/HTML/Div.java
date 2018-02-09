package Samples.HTML;

public class Div extends AbstractElement<Div> implements ICommonAttributeGroup<Div>, IFlowContent<Div> {

    public Div() {
        super();
    }

    public Div(IElement parent) {
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
    public Div cloneElem() {
        return this.clone(new Div());
    }
}