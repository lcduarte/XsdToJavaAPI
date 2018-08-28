package Samples.HTML;

public abstract class Visitor<R> {

    public abstract <T extends Element> void sharedVisit(Element<T, ?> elem);

    public void visit(H1 elem){
        sharedVisit(elem);
    }

    public void visit(Div elem){
        sharedVisit(elem);
    }

    public abstract void visit(Text elem);

    public abstract void visit(Comment elem);

    public abstract <U> void visit(TextFunction<R, U, ?> elem);
}

