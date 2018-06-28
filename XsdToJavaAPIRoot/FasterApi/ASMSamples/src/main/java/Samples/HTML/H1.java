package Samples.HTML;

public class H1<P extends Element> extends AbstractElement<H1<P>, P> implements FlowContent<H1<P>, P> {

    private H1(Visitor visitor, int depth) {
        super(visitor, "h1", depth);
        visitor.visit(this);
    }

    public H1(Visitor visitor) {
        super(visitor, "h1", 0);
        visitor.visit(this);
    }

    public H1(P parent) {
        super(parent, "h1");
        visitor.visit(this);
    }

    public H1(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    public H1<P> self() {return this; }

}
