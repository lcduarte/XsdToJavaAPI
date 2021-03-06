package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class AName<P extends Element> extends AbstractElement<AName<P>, P> implements TextGroup<AName<P>, P> {

    public AName(P parent){
        super(parent, "aName");
    }

    @Override
    public AName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public AName<P> cloneElem() {
        return null;
    }

    public ANameElem1<AName<P>, P> elem1(String value){
        addChild(new Elem1<>(this).text(value));
        return new ANameElem1<>(this);
    }
}