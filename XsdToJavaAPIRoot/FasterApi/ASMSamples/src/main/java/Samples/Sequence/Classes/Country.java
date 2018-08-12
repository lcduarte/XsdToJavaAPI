package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class Country<P extends Element> extends AbstractElement<Country<P>, P> implements TextGroup<Country<P>, P> {

    public Country(Visitor visitor) {
        super(visitor, "country");
        visitor.visit(this);
    }

    public Country(P parent){
        super(parent, "country");
        visitor.visit(this);
    }

    @Override
    public Country<P> self() {
        return this;
    }

}
