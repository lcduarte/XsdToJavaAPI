package Samples.HTML;

import java.util.function.Function;

public class TextFunction<R, U, P extends Element> extends AbstractElement<TextFunction<R, U, P>, P>{

    private Function<R, U> textFunction;

    private TextFunction() {
        super("text", 0);
    }

    public TextFunction(P parent, Function<R, U> textFunction) {
        super(parent, "text");
        this.textFunction = textFunction;
    }

    @Override
    public TextFunction addAttr(IAttribute attribute) {
        throw new UnsupportedOperationException("Text element can't contain attributes.");
    }

    @Override
    public TextFunction addChild(Element child) {
        throw new UnsupportedOperationException("Text element can't contain children.");
    }

    @Override
    public TextFunction<R, U, P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public U getValue(R obj) {
        if (textFunction == null){
            return null;
        }

        return textFunction.apply(obj);
    }

    @Override
    public TextFunction<R, U, P> cloneElem() {
        return this.clone(new TextFunction<R, U, P>());
    }
}
