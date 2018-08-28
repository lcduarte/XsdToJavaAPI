package Samples.HTML;

import java.util.function.Function;

public interface TextGroup<T extends Element, P extends Element> extends Element<T, P> {

    default <R> T text(R text){
        this.addChild(new Text<>(this, String.valueOf(text)));
        return this.self();
    }

    default <R> T comment(R text){
        this.addChild(new Comment<>(this, String.valueOf(text)));
        return this.self();
    }

    default <R, U> T text(Function<R, U> textFunction){
        this.addChild(new TextFunction<>(this, textFunction));
        return this.self();
    }

}
