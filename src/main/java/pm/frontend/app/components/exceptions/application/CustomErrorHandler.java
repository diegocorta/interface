package pm.frontend.app.components.exceptions.application;
import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;

import pm.frontend.app.components.exceptions.ExceptionNotification;

@Configuration
public class CustomErrorHandler implements ErrorHandler {

	private static final long serialVersionUID = -467800716957349985L;

	@Override
    public void error(ErrorEvent errorEvent) {
		
		ExceptionNotification.error(errorEvent.getThrowable());
		
    }
	
}