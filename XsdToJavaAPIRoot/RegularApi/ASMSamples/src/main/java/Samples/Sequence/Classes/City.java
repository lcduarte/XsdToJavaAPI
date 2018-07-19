package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class City<P extends Element> extends AbstractElement<City<P>, P> implements TextGroup<City<P>, P> {

    public City(P parent) {
        super(parent, "city");
    }

    @Override
    public City<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public City<P> cloneElem() {
        return null;
    }
}