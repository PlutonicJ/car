package net.cyberspirit.car.ui.car.overview.component;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.QueryParameters;
import net.cyberspirit.car.entity.Car;
import net.cyberspirit.car.ui.ViewMode;
import net.cyberspirit.car.ui.car.CarDataManager;
import net.cyberspirit.car.ui.car.detail.CarDetailRoute;
import net.cyberspirit.car.ui.car.overview.CarRoute;
import net.cyberspirit.car.ui.car.overview.event.CarSearchEvent;
import net.cyberspirit.car.ui.car.overview.model.CarSearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RouteScoped
@RouteScopeOwner(CarRoute.class)
public class CarGrid extends Grid<Car> {
    private final Logger logger = LoggerFactory.getLogger(CarGrid.class);

    private ConfigurableFilterDataProvider<Car, Void, CarSearchOptions> dataProvider;

    @EJB
    private CarDataManager dataManager;

    @Inject
    public CarGrid(CarGridDS dataSource) {
        super(Car.class);
        dataProvider = dataSource.withConfigurableFilter();
        setDataProvider(dataProvider);

        removeAllColumns();
        ComponentRenderer<Icon, Car> detailRenderer = new ComponentRenderer<>(item -> VaadinIcon.SEARCH.create());
        Column<Car> detailColumn = addColumn(detailRenderer).setResizable(false).setFlexGrow(0);
        detailColumn.setWidth("50px");

        ComponentRenderer<Icon, Car> editRenderer = new ComponentRenderer<>(item -> VaadinIcon.EDIT.create());
        Column<Car> editColumn = addColumn(editRenderer).setResizable(false).setFlexGrow(0);
        editColumn.setWidth("50px");

        ComponentRenderer<Icon, Car> deleteRenderer = new ComponentRenderer<>(item -> VaadinIcon.CLOSE.create());
        Column<Car> deleteColumn = addColumn(deleteRenderer).setResizable(false).setFlexGrow(0);
        deleteColumn.setWidth("50px");

        addColumn(Car::getName)
                .setHeader(getTranslation("car.name"))
                .setSortable(true)
                .setResizable(true).setId("name");
        addColumn(Car::getBrand)
                .setHeader(getTranslation("car.brand"))
                .setSortable(true)
                .setResizable(true).setId("brand");
        addColumn(Car::getNumberOfWheels)
                .setHeader(getTranslation("car.numberOfWheels"))
                .setSortable(true)
                .setResizable(true)
                .setId("numberOfWheels");

        addItemClickListener(e -> {
            Map<String, List<String>> parameters = new HashMap<>();
            parameters.put("viewMode", new ArrayList<>());
            if (e.getColumn().equals(detailColumn)) {
                getUI().ifPresent(ui -> {
                    parameters.get("viewMode").add(ViewMode.VIEW.name());
                    ui.navigate(CarDetailRoute.ROUTE_ID + "/" + e.getItem().getId(), new QueryParameters(parameters));
                });
            } else if (e.getColumn().equals(editColumn)) {
                getUI().ifPresent(ui -> {
                    parameters.get("viewMode").add(ViewMode.EDIT.name());
                    ui.navigate(CarDetailRoute.ROUTE_ID + "/" + e.getItem().getId(), new QueryParameters(parameters));
                });
            } else if (e.getColumn().equals(deleteColumn)) {
                getUI().ifPresent(ui -> {
                    Dialog dialog = new Dialog();
                    dialog.add(new Label(getTranslation("common.message.delete")));
                    HorizontalLayout buttonLayout = new HorizontalLayout();
                    dialog.add(buttonLayout);
                    buttonLayout.add(new Button(getTranslation("common.button.yes"), event -> {
                        dataManager.delete(e.getItem());
                        dataProvider.refreshAll();
                        dialog.close();
                    }));
                    buttonLayout.add(new Button(getTranslation("common.button.no"), event -> dialog.close()));
                    dialog.open();
                });
            }
        });
    }

    public void loadData(@Observes CarSearchEvent event) {
        CarSearchOptions searchOptions = event.getSearchOptions();
        logger.info("Search for name=" + searchOptions.getName() + " and brand=" + searchOptions.getBrand());
        dataProvider.setFilter(searchOptions);
        dataProvider.refreshAll();
    }
}
