package Samples;

import java.util.function.Function;

public class Text<T> extends AbstractElement<Text>{

    private String text;
    private Function<T, String> textFunction;

    public Text(String text) {
        this.text = text;
    }

    public Text(Function<T, String> textFunction) {
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
    public void acceptInit(Visitor visitor) {
        visitor.initVisit(this);
    }

    @Override
    public void acceptEnd(Visitor visitor) {
        visitor.endVisit(this);
    }

    public String getValue() {
        return text;
    }

    public String getValue(T obj) {
        if (textFunction == null){
            return text;
        }

        return textFunction.apply(obj);
    }

}
