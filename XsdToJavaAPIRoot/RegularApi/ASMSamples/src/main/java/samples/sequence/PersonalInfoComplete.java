package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class PersonalInfoComplete<P extends Element> extends AbstractElement<PersonalInfoComplete<P>, P>  implements TextGroup<PersonalInfoComplete<P>, P> {

    public PersonalInfoComplete(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoComplete<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoComplete<P> cloneElem() {
        return null;
    }


}
