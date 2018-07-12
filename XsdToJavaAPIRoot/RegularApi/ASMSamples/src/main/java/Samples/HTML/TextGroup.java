package Samples.HTML;

import java.util.function.Function;

public interface TextGroup<T extends Element, P extends Element> extends Element<T, P> {

    default T text(String text){
        this.addChild(new Text<>(this, text));
        return this.self();
    }

    default T comment(String text){
        this.addChild(new Comment<>(this, text));
        return this.self();
    }

    default <R, U> T text(Function<R, U> textFunction){
        this.addChild(new TextFunction<>(this, textFunction));
        return this.self();
    }

}
