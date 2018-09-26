package samples.html;

public final class H1<P extends Element> implements FlowContent<H1<P>, P>, Element<H1<P>, P> {

    private final P parent;
    private final Visitor visitor;

    public H1(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        visitor.visitElement(this);
    }

    public H1(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        visitor.visitElement(this);
    }

    @Override
    public final H1<P> self() {return this; }

    @Override
    public P º() {
        visitor.visitParent(this);
        return parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public final Visitor getVisitor() {
        return visitor;
    }

    @Override
    public String getName() {
        return "h1";
    }

}
