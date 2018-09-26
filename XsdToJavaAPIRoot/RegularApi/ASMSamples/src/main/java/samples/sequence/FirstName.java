package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class FirstName<P extends Element> extends AbstractElement<FirstName<P>, P> implements TextGroup<FirstName<P>, P> {

    public FirstName(P parent){
        super(parent, "firstName");
    }

    @Override
    public FirstName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public FirstName<P> cloneElem() {
        return null;
    }
}

