package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class Address<P extends IElement> extends AbstractElement<Address<P>, P> implements ITextGroup<Address<P>, P> {

    public Address(P parent){
        super(parent);
    }

    @Override
    public Address<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Address<P> cloneElem() {
        return null;
    }
}
