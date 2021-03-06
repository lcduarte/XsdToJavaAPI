package samples.html;

public class H1<P extends Element> extends AbstractElement<H1<P>, P> implements FlowContent<H1<P>, P> {
    public H1() {super("h1");}

    public H1(P parent) {
        super(parent, "h1");
    }

    public H1<P> self() {return this; }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public H1<P> cloneElem() {
        return this.clone(new H1<>());
    }

}
