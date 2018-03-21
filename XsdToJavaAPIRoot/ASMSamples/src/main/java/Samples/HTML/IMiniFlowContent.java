package Samples.HTML;

public interface IMiniFlowContent<T extends IElement<T, P>, P extends IElement> extends IFlowContent<T, P> {

    @Override
    default H1<T> h1() {
        return IFlowContent.super.h1();
    }

}
