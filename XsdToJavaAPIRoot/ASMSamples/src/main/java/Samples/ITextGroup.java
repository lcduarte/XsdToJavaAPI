package Samples;

import java.util.function.Function;

public interface ITextGroup<T extends IElement, M> extends IElement<T> {

    default T text(String text){
        Text text1 = new Text(this, text);
        this.addChild(text1);
        return this.self();
    }

    default <R, U> T text(Function<R, U> textFunction){
        Text<R, U> text1 = new Text<>(this, textFunction);
        this.addChild(text1);
        return this.self();
    }

}
