package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class PersonalInfoCity<P extends Element> extends AbstractElement<PersonalInfoCity<P>, P> implements TextGroup<PersonalInfoCity<P>, P> {

    public PersonalInfoCity(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoCity<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoCity<P> cloneElem() {
        return null;
    }

    public PersonalInfoComplete<P> country(String value){
        parent.addChild(new Country<>(parent).text(value));
        PersonalInfoComplete<P> var = new PersonalInfoComplete<>(parent);
        this.getChildren().forEach(var::addChild);
        return var;
    }

}

