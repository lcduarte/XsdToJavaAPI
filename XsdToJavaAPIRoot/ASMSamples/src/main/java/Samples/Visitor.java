package Samples;

public interface Visitor<T> {

    void initVisit(H1 elem);
    void endVisit(H1 elem);

    void initVisit(Div elem);
    void endVisit(Div elem);

    void initVisit(Text<T> text);
    void endVisit(Text<T> text);
}
