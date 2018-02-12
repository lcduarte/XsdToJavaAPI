package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.Location;

public class City<P extends IElement> extends AbstractElement<City<P>, P> implements ITextGroup<City<P>, P> {

    public City(){

    }

    public City(IElement element) {

    }

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
