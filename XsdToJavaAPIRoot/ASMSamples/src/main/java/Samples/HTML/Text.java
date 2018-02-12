package Samples.HTML;

import java.util.function.Function;

public class Text<R, U, P extends IElement> extends AbstractElement<Text<R, U, P>, P>{

    private String text;
    private Function<R, U> textFunction;

    private Text() {
        super();
    }

    public Text(P parent, String text) {
        super(parent);
        this.text = text;
    }

    public Text(P parent, Function<R, U> textFunction) {
        super(parent);
        this.textFunction = textFunction;
    }

    @Override
    public Text addAttr(IAttribute attribute) {
        return null;
    }

    @Override
    public Text addChild(IElement child) {
        return null;
    }

    @Override
    public Text self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.initVisit(this);

        visitor.endVisit(this);
    }

    public String getValue() {
        return text;
    }

    public U getValue(R obj) {
        if (textFunction == null){
            return null;
        }

        return textFunction.apply(obj);
    }

    @Override
    public Text<R, U, P> cloneElem() {
        return this.clone(new Text<R, U, P>());
    }
}
