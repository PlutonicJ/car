package net.cyberspirit.car.ui.car.overview.component;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import net.cyberspirit.car.entity.Car;
import net.cyberspirit.car.ui.car.CarDataManager;
import net.cyberspirit.car.ui.car.overview.model.CarSearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.stream.Stream;

public class CarGridDS extends AbstractBackEndDataProvider<Car, CarSearchOptions> {

    private final Logger logger = LoggerFactory.getLogger(CarGridDS.class);

    @EJB
    private CarDataManager dataManager;

    @Override
    protected Stream<Car> fetchFromBackEnd(Query<Car, CarSearchOptions> filterQuery) {
        return dataManager.loadCars(filterQuery).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<Car, CarSearchOptions> filterQuery) {
        return dataManager.numberOfCars(filterQuery);
    }
}
