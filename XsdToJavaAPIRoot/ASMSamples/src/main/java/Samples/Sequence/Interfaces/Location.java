package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.City;
import Samples.Sequence.Classes.Country;

public interface Location<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default City city() {
        City city = new City(this);
        addChild(city);
        return city;
    }

    default Country country() {
        Country country = new Country(this);
        addChild(country);
        return country;
    }
}
