package samples.html;

public class Text<P extends Element, R extends Object> implements Element<Text<P, R>, P>{

    private final String text;
    protected final P parent;
    protected final Visitor visitor;

    public Text(P parent, Visitor visitor, R text) {
        this.parent = parent;
        this.visitor = visitor;
        this.text = text.toString();
    }

    @Override
    public Text<P, R> self() {
        return this;
    }

    @Override
    public P __() {
        visitor.visitText(this);
        return parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    public String getValue() {
        return text;
    }

}
