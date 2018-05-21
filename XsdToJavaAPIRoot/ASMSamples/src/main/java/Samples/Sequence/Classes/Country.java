package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class Country<P extends Element> extends AbstractElement<Country<P>, P> implements TextGroup<Country<P>, P> {

    public Country(){

    }

    public Country(P parent){
        super(parent);
    }

    @Override
    public Country<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Country<P> cloneElem() {
        return null;
    }
}
