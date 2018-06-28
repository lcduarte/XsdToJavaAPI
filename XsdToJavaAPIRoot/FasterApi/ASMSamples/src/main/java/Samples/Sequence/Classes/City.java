package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class City<P extends Element> extends AbstractElement<City<P>, P> implements TextGroup<City<P>, P> {

    public City(Visitor visitor) {
        super(visitor, "city", 0);
        visitor.visit(this);
    }

    public City(Visitor visitor, int depth) {
        super(visitor, "city", depth);
        visitor.visit(this);
    }

    public City(P parent){
        super(parent, "city");
        visitor.visit(this);
    }

    public City(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    @Override
    public City<P> self() {
        return this;
    }

}
