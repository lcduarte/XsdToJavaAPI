package Samples.HTML;

public final class Text<P extends Element> extends AbstractElement<Text<P>, P>{

    private final String text;

    private Text(Visitor visitor, String text) {
        super(visitor, "text");
        this.text = text;
        visitor.visit(this);
    }

    public Text(P parent, String text) {
        super(parent, "text");
        this.text = text;
        visitor.visit(this);
    }

    @Override
    public final Text<P> self() {
        return this;
    }

    public final String getValue() {
        return text;
    }

}
