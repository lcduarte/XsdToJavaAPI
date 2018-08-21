package Samples.HTML;

public interface TextGroup<T extends Element, P extends Element> extends Element<T, P> {

    default T text(String text){
        getVisitor().visitText(text);
        return this.self();
    }

    default T comment(String text){
        getVisitor().visitComment(text);
        return this.self();
    }

}
