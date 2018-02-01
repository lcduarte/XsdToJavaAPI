package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class Country extends AbstractElement<Country> implements ITextGroup<Country> {
    @Override
    public Country self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Country cloneElem() {
        return null;
    }
}
