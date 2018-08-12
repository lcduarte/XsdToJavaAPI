package Samples.HTML;

public abstract class AbstractElement<T extends Element<T, P>, P extends Element> implements Element<T, P> {
    protected final String name;
    protected final P parent;
    protected final Visitor visitor;

    protected AbstractElement(Visitor visitor, String name){
        this.visitor = visitor;
        this.name = name;
        this.parent = null;
    }

    protected AbstractElement(P parent, String name){
        this.visitor = parent.getVisitor();
        this.parent = parent;
        this.name = name;
    }

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

    public final String getName(){
        return name;
    }

}