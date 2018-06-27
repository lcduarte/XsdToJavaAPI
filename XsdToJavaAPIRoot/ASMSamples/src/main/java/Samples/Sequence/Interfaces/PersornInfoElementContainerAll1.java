package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.Sequence.Classes.PersonalInfo;

public interface PersornInfoElementContainerAll1<T extends Element<T, Z>, Z extends Element> extends TextGroup<T, Z> {
    default PersonalInfo<T> personInfo() {
        return this.addChild(new PersonalInfo<>(this.self()));
    }
}
