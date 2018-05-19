package Samples.HTML;

public interface Visitor<R> {

    <T extends IElement> void sharedVisit(IElement<T, ?> elem);

    default void visit(H1 elem){
        sharedVisit(elem);
    }

    default void visit(Div elem){
        sharedVisit(elem);
    }

    default <U> void visit(Text<R, U, ?> elem){
        sharedVisit(elem);
    }
}

