package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class LastName<P extends Element> extends AbstractElement<LastName<P>, P> implements TextGroup<LastName<P>, P> {

    public LastName(Visitor visitor) {
        super(visitor, "lastName");
        visitor.visit(this);
    }

    public LastName(P parent){
        super(parent, "lastName");
        visitor.visit(this);
    }

    @Override
    public LastName<P> self() {
        return this;
    }

}

