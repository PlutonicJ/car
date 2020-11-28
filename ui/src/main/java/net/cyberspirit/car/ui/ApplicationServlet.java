package net.cyberspirit.car.ui;

import com.vaadin.cdi.CdiVaadinServlet;
import com.vaadin.flow.server.Constants;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/*", name = "slot", asyncSupported = true, initParams = {
        @WebInitParam(name = Constants.I18N_PROVIDER, value = "net.cyberspirit.car.ui.TranslationProvider")})
public class ApplicationServlet extends CdiVaadinServlet {
}