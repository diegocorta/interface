package pm.frontend.app.configuration;

import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinServiceInitListener;

import pm.frontend.app.components.exceptions.application.CustomErrorHandler;

@Configuration
public class ApplicationServiceInitListener
        implements VaadinServiceInitListener, SessionInitListener {

    private static final long serialVersionUID = -1537757534697619039L;
    
	private CustomErrorHandler customErrorHandler;
    
    public ApplicationServiceInitListener() {
    	this.customErrorHandler = new CustomErrorHandler();
    }
    
	@Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event) {
        event.getSession().setErrorHandler(customErrorHandler);
    }
}
