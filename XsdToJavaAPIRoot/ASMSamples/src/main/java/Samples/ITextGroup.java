package Samples;

import java.util.function.Function;

public interface ITextGroup<T extends IElement> extends IElement<T> {

    default T text(String text){
        Text text1 = new Text(this, text);
        this.addChild(text1);
        return this.self();
    }

    default <R> T text(Function<R, String> textFunction){
        Text<R> text1 = new Text<>(this, textFunction);
        this.addChild(text1);
        return this.self();
    }

}
