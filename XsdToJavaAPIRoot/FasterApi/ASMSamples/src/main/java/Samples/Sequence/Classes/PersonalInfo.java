package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence1;

public class PersonalInfo<P extends Element> extends AbstractElement<PersonalInfo<P>, P> implements PersonalInfoSequence1<PersonalInfo<P>, P> {

    public PersonalInfo(Visitor visitor) {
        super(visitor, "personalInfo", 0);
        visitor.visit(this);
    }

    public PersonalInfo(Visitor visitor, int depth) {
        super(visitor, "personalInfo", depth);
        visitor.visit(this);
    }

    public PersonalInfo(P parent){
        super(parent, "personalInfo");
        visitor.visit(this);
    }

    public PersonalInfo(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    @Override
    public PersonalInfo<P> self() {
        return this;
    }

}
