package Samples.HTML;

public interface Visitor<R> {

    void visit(H1 elem);

    void visit(Div elem);

    <U> void visit(Text<R, U, ?> elem);

}

