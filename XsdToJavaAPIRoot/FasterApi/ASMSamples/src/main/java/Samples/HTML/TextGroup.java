package Samples.HTML;

public interface TextGroup<T extends Element, P extends Element> extends Element<T, P> {

    default T text(String text){
        new Text<>(this, text);
        return this.self();
    }

    default T comment(String text){
        new Comment<>(this, text);
        return this.self();
    }

}
