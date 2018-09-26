package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class PersonalInfoLastName<P extends Element> extends AbstractElement<PersonalInfoLastName<P>, P> implements TextGroup<PersonalInfoLastName<P>, P> {

    public PersonalInfoLastName(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoLastName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoLastName<P> cloneElem() {
        return null;
    }

    public PersonalInfoAddress<P> address(String value){
        parent.addChild(new Address<>(parent).text(value));
        PersonalInfoAddress<P> var = new PersonalInfoAddress<>(parent);
        this.getChildren().forEach(var::addChild);
        return var;
    }
}

