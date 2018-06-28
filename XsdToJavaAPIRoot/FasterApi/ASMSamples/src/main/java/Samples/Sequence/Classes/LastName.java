package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class LastName<P extends Element> extends AbstractElement<LastName<P>, P> implements TextGroup<LastName<P>, P> {

    public LastName(Visitor visitor) {
        super(visitor, "lastName", 0);
        visitor.visit(this);
    }

    public LastName(Visitor visitor, int depth) {
        super(visitor, "lastName", depth);
        visitor.visit(this);
    }

    public LastName(P parent){
        super(parent, "lastName");
        visitor.visit(this);
    }

    public LastName(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    @Override
    public LastName<P> self() {
        return this;
    }

}

