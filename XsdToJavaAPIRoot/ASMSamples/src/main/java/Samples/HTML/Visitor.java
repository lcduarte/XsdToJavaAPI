package Samples.HTML;

public interface Visitor<R> {

    <T extends IElement> void visit(IElement<T, ?> elem);

    default void visit(H1 elem){
        visit((IElement) elem);
    }

    default void visit(Div elem){
        visit((IElement) elem);
    }

    default <U> void visit(Text<R, U, ?> elem){
        visit((IElement) elem);
    }
}

