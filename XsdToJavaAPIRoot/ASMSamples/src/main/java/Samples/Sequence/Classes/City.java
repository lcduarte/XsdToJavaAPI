package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class City extends AbstractElement<City> implements ITextGroup<City> {

    public City(){

    }

    public City(IElement parent){
        super(parent);
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
