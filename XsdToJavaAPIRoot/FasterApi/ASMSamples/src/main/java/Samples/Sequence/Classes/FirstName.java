package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class FirstName<P extends Element> extends AbstractElement<FirstName<P>, P> implements TextGroup<FirstName<P>, P> {

    public FirstName(Visitor visitor) {
        super(visitor, "firstName", 0);
        visitor.visit(this);
    }

    public FirstName(Visitor visitor, int depth) {
        super(visitor, "firstName", depth);
        visitor.visit(this);
    }

    public FirstName(P parent){
        super(parent, "firstName");
        visitor.visit(this);
    }

    public FirstName(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    @Override
    public FirstName<P> self() {
        return this;
    }

}

