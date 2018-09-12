package Samples.HTML;

public interface TextGroup<T extends Element, P extends Element> extends Element<T, P> {

    default <R> T text(R text){
        getVisitor().visitText(new Text<>(self(), getVisitor(), text));
        return this.self();
    }

    default <R> T comment(R text){
        getVisitor().visitComment(new Text<>(self(), getVisitor(), text));
        return this.self();
    }

}
