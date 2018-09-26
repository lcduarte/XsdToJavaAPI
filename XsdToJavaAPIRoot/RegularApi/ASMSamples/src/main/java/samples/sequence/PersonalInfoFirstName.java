package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class PersonalInfoFirstName<P extends Element> extends AbstractElement<PersonalInfoFirstName<P>, P> implements TextGroup<PersonalInfoFirstName<P>, P> {

    public PersonalInfoFirstName(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoFirstName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoFirstName<P> cloneElem() {
        return null;
    }

    public PersonalInfoLastName<P> lastName(String value){
        parent.addChild(new LastName<>(parent).text(value));
        PersonalInfoLastName<P> var = new PersonalInfoLastName<>(º());
        this.getChildren().forEach(var::addChild);
        return var;
    }
}
