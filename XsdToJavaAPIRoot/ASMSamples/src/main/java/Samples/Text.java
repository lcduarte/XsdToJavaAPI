package Samples;

import java.util.function.Function;

public class Text<R> extends AbstractElement<Text>{

    private String text;
    private Function<R, String> textFunction;

    public Text(String text) {
        this.text = text;
    }

    public Text(Function<R, String> textFunction) {
        this.textFunction = textFunction;
    }

    @Override
    public void addAttr(IAttribute attribute) {

    }

    @Override
    public void addChild(IElement child) {

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

    public String getValue(R obj) {
        if (textFunction == null){
            return text;
        }

        return textFunction.apply(obj);
    }

}
