package Samples;

public abstract class AbstractVisitor<R> implements Visitor<R>{

    abstract <T extends IElement> void initVisit(IElement<T> elem);

    abstract <T extends IElement> void endVisit(IElement<T> elem);

    @Override
    public void initVisit(H1 elem) {
        initVisit((IElement<H1>) elem);
    }

    @Override
    public void endVisit(H1 elem) {
        endVisit((IElement<H1>) elem);
    }

    @Override
    public void initVisit(Div elem) {
        initVisit((IElement<Div>) elem);
    }

    @Override
    public void endVisit(Div elem) {
        endVisit((IElement<Div>) elem);
    }
}
