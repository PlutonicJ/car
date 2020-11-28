package net.cyberspirit.car.ui.car.overview;

import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import net.cyberspirit.car.ui.ViewMode;
import net.cyberspirit.car.ui.car.detail.CarDetailRoute;
import net.cyberspirit.car.ui.car.overview.component.CarGrid;
import net.cyberspirit.car.ui.car.overview.component.CarSearchPanel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RouteScoped
@Route(CarRoute.ROUTE_ID)
public class CarRoute extends VerticalLayout implements RouterLayout {

    public static final String ROUTE_ID = "car";

    public CarRoute() {
    }

    @Inject
    public CarRoute(@RouteScopeOwner(CarRoute.class) CarSearchPanel searchPanel, @RouteScopeOwner(CarRoute.class) CarGrid grid) {
        add(searchPanel);
        VerticalLayout layout = new VerticalLayout(new Label(getTranslation("cars"))
                , grid
                , new Button(getTranslation("common.button.create"), e -> getUI().ifPresent(ui -> {
                    QueryParameters queryParameters = createQueryParameters(ViewMode.CREATE);
                    ui.navigate(CarDetailRoute.ROUTE_ID, queryParameters);
                }
        )));
        layout.setMargin(false);
        layout.setPadding(false);
        add(layout);
    }

    private QueryParameters createQueryParameters(ViewMode viewMode) {
        Map<String, List<String>> parameters = new HashMap<>();
        List<String> viewModes = new ArrayList<>();
        viewModes.add(viewMode.name());
        parameters.put("viewMode", viewModes);
        return new QueryParameters(parameters);
    }
}
