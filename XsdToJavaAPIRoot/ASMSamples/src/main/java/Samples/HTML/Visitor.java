package Samples.HTML;

import Samples.Sequence.Classes.PersonalInfo;

public interface Visitor<R> {

    void initVisit(H1 elem);
    void endVisit(H1 elem);

    void initVisit(Div elem);
    void endVisit(Div elem);

    <U> void initVisit(Text<R, U, ?> elem);
    <U> void endVisit(Text<R, U, ?> elem);

    <P extends IElement> void initVisit(PersonalInfo pPersonalInfo);
}

