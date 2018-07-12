package Samples.HTML;

public class Comment<P extends Element> extends AbstractElement<Comment<P>, P> {

    private String text;

    private Comment(Visitor visitor, int depth) {
        super(visitor, "text", depth);
        visitor.visit(this);
    }

    public Comment(P parent, String text) {
        super(parent, "text");
        this.text = text;
        visitor.visit(this);
    }

    @Override
    public Comment<P> self() {
        return this;
    }

    public String getValue() {
        return text;
    }
}