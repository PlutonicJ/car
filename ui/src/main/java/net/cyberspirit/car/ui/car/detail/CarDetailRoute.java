package net.cyberspirit.car.ui.car.detail;

import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import net.cyberspirit.car.entity.Car;
import net.cyberspirit.car.ui.ViewMode;
import net.cyberspirit.car.ui.car.CarDataManager;
import net.cyberspirit.car.ui.car.overview.CarRoute;

import javax.ejb.EJB;
import java.util.List;

@RouteScoped
@Route(CarDetailRoute.ROUTE_ID)
public class CarDetailRoute extends VerticalLayout implements RouterLayout, HasUrlParameter<Long> {
    public static final String ROUTE_ID = "carDetails";

    @EJB
    private CarDataManager dataManager;

    private IntegerField id = new IntegerField();
    private IntegerField version = new IntegerField();
    private TextField name = new TextField();
    private TextField brand = new TextField();
    private IntegerField numberOfWheels = new IntegerField();

    private Binder<Car> binder = new Binder<>();

    private ViewMode viewMode;

    private Button saveButton;
    private Button cancelButton;

    public CarDetailRoute() {
        id.setVisible(false);
        version.setVisible(false);
        name.addKeyDownListener(Key.ENTER, createEventAction());
        brand.addKeyDownListener(Key.ENTER, createEventAction());
        numberOfWheels.setHasControls(true);

        FormLayout fieldsLayout = new FormLayout();
        fieldsLayout.addFormItem(name, getTranslation("car.name"));
        fieldsLayout.addFormItem(brand, getTranslation("car.brand"));
        fieldsLayout.addFormItem(numberOfWheels, getTranslation("car.numberOfWheels"));
        add(fieldsLayout);

        saveButton = new Button(getTranslation("common.button.save"), createEventAction());
        saveButton.setWidth("150px");
        cancelButton = new Button(getTranslation("common.button.cancel"), e -> getUI().ifPresent(ui -> ui.navigate(CarRoute.ROUTE_ID)));
        cancelButton.setWidth("150px");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(saveButton);
        buttonLayout.add(cancelButton);
        add(buttonLayout);

        setupBinder();
    }

    private <E extends ComponentEvent<C>, C extends Component> ComponentEventListener<E> createEventAction() {
        return e -> {
            if (viewMode == ViewMode.EDIT || viewMode == ViewMode.CREATE) {
                Car item = new Car();
                binder.writeBeanIfValid(item);
                dataManager.save(item);
                getUI().ifPresent(ui -> ui.navigate(CarRoute.ROUTE_ID));
            }
        };
    }

    private void setupBinder() {
        binder.forField(id).asRequired().withConverter(id -> id != null ? Long.valueOf(id).longValue() : 0, id -> id != null ? id.intValue() : 0).bind(Car::getId, Car::setId);
        binder.forField(version).asRequired().withConverter(version -> version != null ? Long.valueOf(version).longValue() : 0, version -> version != null ? version.intValue() : 0).bind(Car::getVersion, Car::setVersion);
        binder.forField(name).withNullRepresentation("").bind(Car::getName, Car::setName);
        binder.forField(brand).withNullRepresentation("").bind(Car::getBrand, Car::setBrand);
        binder.forField(numberOfWheels).asRequired("common.error.required").bind(Car::getNumberOfWheels, Car::setNumberOfWheels);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long entityId) {
        viewMode = getViewMode(beforeEvent);
        if (viewMode == null) {
            viewMode = ViewMode.VIEW;
        }
        updateFields(viewMode);
        updateButtons(viewMode);
        if (entityId != null) {
            binder.readBean(dataManager.load(entityId));
        } else {
            binder.readBean(new Car());
        }
    }

    private void updateButtons(ViewMode viewMode) {
        switch (viewMode) {
            case VIEW:
                saveButton.setVisible(false);
                cancelButton.setVisible(true);
                cancelButton.setText(getTranslation("common.button.back"));
                break;
            case CREATE:
            case EDIT:
                saveButton.setVisible(true);
                cancelButton.setVisible(true);
                cancelButton.setText(getTranslation("common.button.cancel"));
                break;
        }
    }

    private void updateFields(ViewMode viewMode) {
        switch (viewMode) {
            case VIEW:
                name.setReadOnly(true);
                brand.setReadOnly(true);
                numberOfWheels.setReadOnly(true);
                break;
            case CREATE:
            case EDIT:
                name.setReadOnly(false);
                brand.setReadOnly(false);
                numberOfWheels.setReadOnly(false);
                break;
        }
    }

    private ViewMode getViewMode(BeforeEvent beforeEvent) {
        List<String> viewModeParameters = beforeEvent.getLocation().getQueryParameters().getParameters().get("viewMode");
        ViewMode viewMode = null;
        if (!viewModeParameters.isEmpty()) {
            viewMode = ViewMode.valueOf(viewModeParameters.get(0));
        }
        return viewMode;
    }
}
