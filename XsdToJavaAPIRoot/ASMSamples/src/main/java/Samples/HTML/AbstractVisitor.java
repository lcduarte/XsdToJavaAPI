package Samples.HTML;

public abstract class AbstractVisitor<R> implements Visitor<R>{

    abstract <T extends IElement> void initVisit(IElement elem);

    abstract <T extends IElement> void endVisit(IElement elem);

    @Override
    public void visit(H1 elem) {
        initVisit((IElement) elem);
    }

    @Override
    public void visit(Div elem) {
        initVisit((IElement) elem);
    }

    @Override
    public <U> void visit(Text<R, U, ?> elem) {
        initVisit((IElement) elem);
    }

}
