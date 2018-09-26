package samples.html;

public class Text<P extends Element> extends AbstractElement<Text<P>, P>{

    private String text;

    private Text() {
        super("text");
    }

    public Text(P parent, String text) {
        super(parent, "text");
        this.text = text;
    }

    @Override
    public Text<P> addAttr(IAttribute attribute) {
        throw new UnsupportedOperationException("Text element can't contain attributes.");
    }

    @Override
    public Element addChild(Element child) {
        throw new UnsupportedOperationException("Text element can't contain children.");
    }

    @Override
    public Text<P> self() {
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
    public Text<P> cloneElem() {
        return this.clone(new Text<P>());
    }
}
