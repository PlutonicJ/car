package net.cyberspirit.car.ui.car.overview.event;

import net.cyberspirit.car.ui.car.overview.model.CarSearchOptions;

import java.util.EventObject;

public class CarSearchEvent extends EventObject {

    private CarSearchOptions searchOptions;

    public CarSearchEvent(Object source, CarSearchOptions searchOptions) {
        super(source);
        this.searchOptions = searchOptions;
    }

    public CarSearchOptions getSearchOptions() {
        return searchOptions;
    }
}
