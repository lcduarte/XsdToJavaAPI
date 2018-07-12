package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.ANameSequence2;

public class ANameElem1<P extends Element> extends AbstractElement<ANameElem1<P>, P> implements ANameSequence2<ANameElem1<P>, P> {

    public ANameElem1(P parent, String aName){
        super(parent, aName);
    }

    @Override
    public ANameElem1<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public ANameElem1<P> cloneElem() {
        return null;
    }
}