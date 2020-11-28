package net.cyberspirit.car.ui.car.overview.component;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cyberspirit.car.ui.car.overview.CarRoute;
import net.cyberspirit.car.ui.car.overview.event.CarSearchEvent;
import net.cyberspirit.car.ui.car.overview.model.CarSearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@RouteScoped
@RouteScopeOwner(CarRoute.class)
public class CarSearchPanel extends VerticalLayout {
    private final Logger logger = LoggerFactory.getLogger(CarSearchPanel.class);

    private TextField nameField;
    private TextField brandField;

    private Binder<CarSearchOptions> binder = new Binder<>();

    @Inject
    private Event<CarSearchEvent> searchEvent;

    public CarSearchPanel() {
        nameField = new TextField();
        nameField.addKeyDownListener(Key.ENTER, createEventAction());
        brandField = new TextField();
        brandField.addKeyDownListener(Key.ENTER, createEventAction());

        binder.forField(nameField).withNullRepresentation("").bind(CarSearchOptions::getName, CarSearchOptions::setName);
        binder.forField(brandField).withNullRepresentation("").bind(CarSearchOptions::getBrand, CarSearchOptions::setBrand);

        FormLayout fieldsLayout = new FormLayout();
        fieldsLayout.addFormItem(nameField, getTranslation("car.name"));
        fieldsLayout.addFormItem(brandField, getTranslation("car.brand"));

        add(fieldsLayout);

        Button searchButton = new Button(getTranslation("common.button.search"), createEventAction());
        searchButton.addClickListener(createEventAction());
        searchButton.setWidth("150px");
        add(searchButton);
    }

    private <E extends ComponentEvent<C>, C extends Component> ComponentEventListener<E> createEventAction() {
        return event -> {
            CarSearchOptions searchOptions = new CarSearchOptions();
            binder.writeBeanIfValid(searchOptions);
            logger.info("Search for name=" + searchOptions.getName() + " and brand=" + searchOptions.getBrand());
            searchEvent.fire(new CarSearchEvent(this, searchOptions));
        };
    }
}
