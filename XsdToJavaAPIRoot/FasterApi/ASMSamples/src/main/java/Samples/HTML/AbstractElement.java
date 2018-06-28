package Samples.HTML;

public abstract class AbstractElement<T extends Element<T, P>, P extends Element> implements Element<T, P> {
    protected String name;
    protected P parent;
    protected Visitor visitor;
    protected int depth;

    protected AbstractElement(Visitor visitor, String name, int depth){
        this.visitor = visitor;
        this.name = name;
        this.depth = depth;
    }

    protected AbstractElement(P parent, String name){
        this.visitor = parent.getVisitor();
        this.parent = parent;
        this.name = name;
        this.depth = parent.getDepth() + 1;
    }

    protected AbstractElement(P parent, String name, int depth){
        this.visitor = parent.getVisitor();
        this.parent = parent;
        this.name = name;
        this.depth = depth;
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
    public Visitor getVisitor() {
        return visitor;
    }

    public String getName(){
        return name;
    }

    @Override
    public int getDepth() {
        return depth;
    }

}