package Samples.HTML;

public interface Visitor<R> {

    <T extends Element> void sharedVisit(Element<T, ?> elem);

    default void visit(H1 elem){
        sharedVisit(elem);
    }

    default void visit(Div elem){
        sharedVisit(elem);
    }

    void visit(Text elem);

    void visit(Comment elem);

    <U> void visit(TextFunction<R, U, ?> elem);
}

