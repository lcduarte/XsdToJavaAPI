package Samples.HTML;

public final class Comment<P extends Element> extends AbstractElement<Comment<P>, P> {

    private final String text;

    private Comment(Visitor visitor, String text) {
        super(visitor, "text");
        this.text = text;
        visitor.visit(this);
    }

    public Comment(P parent, String text) {
        super(parent, "text");
        this.text = text;
        visitor.visit(this);
    }

    @Override
    public final Comment<P> self() {
        return this;
    }

    public final String getValue() {
        return text;
    }
}