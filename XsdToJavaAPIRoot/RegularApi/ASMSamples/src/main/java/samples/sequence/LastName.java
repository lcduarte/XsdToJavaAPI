package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class LastName<P extends Element> extends AbstractElement<LastName<P>, P> implements TextGroup<LastName<P>, P> {

    public LastName(P parent){
        super(parent, "lastName");
    }

    @Override
    public LastName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public LastName<P> cloneElem() {
        return null;
    }
}

