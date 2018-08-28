package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class PersonalInfoAddress<P extends Element> extends AbstractElement<PersonalInfoAddress<P>, P> implements TextGroup<PersonalInfoAddress<P>, P> {

    public PersonalInfoAddress(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoAddress<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoAddress<P> cloneElem() {
        return null;
    }

    public PersonalInfoCity<P> city(String value){
        parent.addChild(new City<>(parent).text(value));
        PersonalInfoCity<P> var = new PersonalInfoCity<>(parent);
        this.getChildren().forEach(var::addChild);
        return var;
    }
}
