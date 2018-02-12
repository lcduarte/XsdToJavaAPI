package Samples.HTML;

public abstract class AbstractVisitor<R> implements Visitor<R>{

    abstract <T extends IElement> void initVisit(IElement elem);

    abstract <T extends IElement> void endVisit(IElement elem);

    @Override
    public void initVisit(H1 elem) {
        initVisit((IElement) elem);
    }

    @Override
    public void endVisit(H1 elem) {
        endVisit((IElement) elem);
    }

    @Override
    public void initVisit(Div elem) {
        initVisit((IElement) elem);
    }

    @Override
    public void endVisit(Div elem) {
        endVisit((IElement) elem);
    }

    @Override
    public <U> void initVisit(Text<R, U, ?> elem) {
        initVisit((IElement) elem);
    }

    @Override
    public <U> void endVisit(Text<R, U, ?> elem) {
        endVisit((IElement) elem);
    }

}
