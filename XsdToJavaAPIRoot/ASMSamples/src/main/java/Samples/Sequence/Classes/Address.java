package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class Address extends AbstractElement<Address> implements ITextGroup<Address> {
    @Override
    public Address self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Address cloneElem() {
        return null;
    }
}
