package samples.html;

public class Comment<P extends Element> extends AbstractElement<Comment<P>, P> {

    private String text;

    private Comment() {
        super("comment");
    }

    public Comment(P parent, String text) {
        super(parent, "comment");
        this.text = text;
    }

    @Override
    public Comment<P> addAttr(IAttribute attribute) {
        throw new UnsupportedOperationException("Text element can't contain attributes.");
    }

    @Override
    public Element addChild(Element child) {
        throw new UnsupportedOperationException("Text element can't contain children.");
    }

    @Override
    public Comment<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getValue() {
        return text;
    }

    @Override
    public Comment<P> cloneElem() {
        return this.clone(new Comment<P>());
    }
}