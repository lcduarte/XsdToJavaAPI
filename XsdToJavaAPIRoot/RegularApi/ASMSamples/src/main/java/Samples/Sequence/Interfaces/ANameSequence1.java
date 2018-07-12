package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.ANameElem1;
import Samples.Sequence.Classes.Elem1;

public interface ANameSequence1<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default ANameElem1<T> elem1(String value){
        addChild(new Elem1<>(self()).text(value));
        ANameElem1<T> var = new ANameElem1<>(self(), "aName");
        this.getChildren().forEach(var::addChild);
        return var;
    }

}
