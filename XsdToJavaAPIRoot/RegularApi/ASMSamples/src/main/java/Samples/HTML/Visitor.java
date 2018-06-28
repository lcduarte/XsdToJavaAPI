package Samples.HTML;

public interface Visitor<R> {

    <T extends Element> void sharedVisit(Element<T, ?> elem);

    default void visit(H1 elem){
        sharedVisit(elem);
    }

    default void visit(Div elem){
        sharedVisit(elem);
    }

    default void visit(Text elem){
        sharedVisit(elem);
    }

    default <U> void visit(TextFunction<R, U, ?> elem){
        sharedVisit(elem);
    }
}

