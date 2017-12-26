package Samples;

public abstract class AbstractVisitor implements Visitor{

    abstract void initVisit(IElement elem);

    abstract void endVisit(IElement elem);

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
}
