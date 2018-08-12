package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;

public class PersonalInfoFirstName<P extends Element> extends AbstractElement<PersonalInfoFirstName<P>, P> {

    public PersonalInfoFirstName(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoFirstName<P> self() {
        return this;
    }

    public PersonalInfoLastName<P> lastName(String value){
        new LastName<>(this).text(value).º();
        return (PersonalInfoLastName) parent;
    }

}
