package samples.html;

import java.util.function.Function;

public interface TextGroup<T extends Element, P extends Element> extends Element<T, P> {

    default <R> T text(R text){
        this.addChild(new Text<>(this, String.valueOf(text)));
        return this.self();
    }

    default <R> T comment(R comment){
        this.addChild(new Text<>(this, String.valueOf(comment)));
        return this.self();
    }

    default <R, U> T text(Function<R, U> textFunction){
        this.addChild(new TextFunction<>(this, textFunction));
        return this.self();
    }

}
