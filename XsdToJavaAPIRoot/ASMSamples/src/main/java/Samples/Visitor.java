package Samples;

public interface Visitor {

    void initVisit(H1 elem);
    void endVisit(H1 elem);

    void initVisit(Div elem);
    void endVisit(Div elem);

    void initVisit(Text text);
    void endVisit(Text text);
}
