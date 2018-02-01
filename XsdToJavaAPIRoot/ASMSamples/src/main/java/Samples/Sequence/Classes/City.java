package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class City extends AbstractElement<City> implements ITextGroup<City> {
    @Override
    public City self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public City cloneElem() {
        return null;
    }
}
