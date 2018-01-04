package Samples;

public interface Visitor<R> {

    void initVisit(H1 elem);
    void endVisit(H1 elem);

    void initVisit(Div elem);
    void endVisit(Div elem);

    void initVisit(Text<R> elem);
    void endVisit(Text<R> elem);
}

