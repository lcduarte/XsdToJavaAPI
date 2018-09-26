package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class PersonalInfo<P extends Element> extends AbstractElement<PersonalInfo<P>, P> implements TextGroup<PersonalInfo<P>, P> {

    public PersonalInfo(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfo<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfo<P> cloneElem() {
        return null;
    }

    public PersonalInfoFirstName<PersonalInfo<P>> firstName(String value){
        addChild(new FirstName<>(this).text(value));
        PersonalInfoFirstName<PersonalInfo<P>> var = new PersonalInfoFirstName<>(this);
        this.getChildren().forEach(var::addChild);
        return var;
    }
}
