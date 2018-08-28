package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class ANameElem1<P extends AName<GP>, GP extends Element> extends AbstractElement<ANameElem1<P, GP>, P> implements TextGroup<ANameElem1<P, GP>, P> {

    public ANameElem1(P parent){
        super(parent, "aName");
    }

    @Override
    public ANameElem1<P, GP> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public ANameElem1<P, GP> cloneElem() {
        return null;
    }

    public P elem2(String value){
        parent.addChild(new Elem2<>(parent).text(value));
        return parent;
    }
}