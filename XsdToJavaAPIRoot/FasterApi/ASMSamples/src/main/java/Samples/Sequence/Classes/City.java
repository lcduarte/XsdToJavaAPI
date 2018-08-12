package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class City<P extends Element> extends AbstractElement<City<P>, P> implements TextGroup<City<P>, P> {

    public City(Visitor visitor) {
        super(visitor, "city");
        visitor.visit(this);
    }

    public City(P parent){
        super(parent, "city");
        visitor.visit(this);
    }

    @Override
    public City<P> self() {
        return this;
    }

}
