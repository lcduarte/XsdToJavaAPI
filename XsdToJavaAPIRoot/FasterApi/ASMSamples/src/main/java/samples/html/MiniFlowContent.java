package samples.html;

public interface MiniFlowContent<T extends Element<T, P>, P extends Element> extends FlowContent<T, P> {

    default H1<T> h4() {
        h1CountIncrement();
        h1MaxCountValidation();

        return new H1<>(self());
    }

    default H1<T> h2() {
        h1CountIncrement();

        return new H1<>(self());
    }

    default H1<T> h3() {
        return new H1<>(self());
    }

    void h1CountIncrement();
    void h1MaxCountValidation();
    default void h1MinCountValidation() { }


}
