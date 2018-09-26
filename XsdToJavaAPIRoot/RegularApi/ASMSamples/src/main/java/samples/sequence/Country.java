package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class Country<P extends Element> extends AbstractElement<Country<P>, P> implements TextGroup<Country<P>, P> {

    public Country(P parent){
        super(parent, "country");
    }

    @Override
    public Country<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Country<P> cloneElem() {
        return null;
    }
}
