package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class Country<P extends Element> extends AbstractElement<Country<P>, P> implements TextGroup<Country<P>, P> {

    public Country(Visitor visitor) {
        super(visitor, "country", 0);
        visitor.visit(this);
    }

    public Country(Visitor visitor, int depth) {
        super(visitor, "country", depth);
        visitor.visit(this);
    }

    public Country(P parent){
        super(parent, "country");
        visitor.visit(this);
    }

    public Country(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    @Override
    public Country<P> self() {
        return this;
    }

}
