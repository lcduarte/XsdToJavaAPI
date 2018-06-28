package Samples.HTML;

public interface MiniFlowContent<T extends Element<T, P>, P extends Element> extends FlowContent<T, P> {

    @Override
    default H1<T> h1() {
        return FlowContent.super.h1();
    }

}
