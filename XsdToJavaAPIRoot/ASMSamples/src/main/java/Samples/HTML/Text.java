package Samples.HTML;

import java.util.function.Function;

public class Text<R, U> extends AbstractElement<Text>{

    private String text;
    private Function<R, U> textFunction;

    private Text() {
        super();
    }

    public Text(IElement parent, String text) {
        super(parent);
        this.text = text;
    }

    public Text(IElement parent, Function<R, U> textFunction) {
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
    public Text<R, U> cloneElem() {
        return this.clone(new Text<R, U>());
    }
}
