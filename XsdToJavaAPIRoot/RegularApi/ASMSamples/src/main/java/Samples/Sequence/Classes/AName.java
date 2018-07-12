package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.ANameSequence1;

public class AName<P extends Element> extends AbstractElement<AName<P>, P> implements ANameSequence1<AName<P>, P> {

    public AName(P parent){
        super(parent, "address");
    }

    @Override
    public AName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public AName<P> cloneElem() {
        return null;
    }
}