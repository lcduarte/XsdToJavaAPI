package Samples.HTML;

import java.util.function.Function;

public interface ITextGroup<T extends IElement, P extends IElement> extends IElement<T, P> {

    default T text(String text){
        Text text1 = new Text<>(this, text);
        this.addChild(text1);
        return this.self();
    }

    default <R, U> T text(Function<R, U> textFunction){
        Text text1 = new Text<>(this, textFunction);
        this.addChild(text1);
        return this.self();
    }

}
