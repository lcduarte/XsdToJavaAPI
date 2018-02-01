package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Interfaces.PersonalInfoSequence1;
import Samples.HTML.Visitor;

public class PersonalInfo extends AbstractElement<PersonalInfo> implements PersonalInfoSequence1<PersonalInfo> {

    @Override
    public PersonalInfo self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.initVisit(this);

        getChildren().forEach(child -> child.accept(visitor));

        visitor.endVisit(this);
    }

    @Override
    public PersonalInfo cloneElem() {
        return null;
    }
}
