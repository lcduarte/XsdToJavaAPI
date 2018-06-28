package Samples.HTML;

public class Text<P extends Element> extends AbstractElement<Text<P>, P>{

    private String text;

    private Text(Visitor visitor, int depth) {
        super(visitor, "text", depth);
        visitor.visit(this);
    }

    public Text(P parent, String text) {
        super(parent, "text");
        this.text = text;
        visitor.visit(this);
    }

    @Override
    public Text<P> self() {
        return this;
    }

    public String getValue() {
        return text;
    }

}
