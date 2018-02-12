package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class Country<P extends IElement> extends AbstractElement<Country<P>, P> implements ITextGroup<Country<P>, P> {

    public Country(){

    }

    public Country(IElement element){

    }

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
