package pm.frontend.app.components.exceptions.route;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

import jakarta.servlet.http.HttpServletResponse;
import pm.frontend.app.components.exceptions.ExceptionNotification;

@Tag(Tag.DIV)
public class RouteExceptionError extends Component
       implements HasErrorParameter<Exception> {

	private static final long serialVersionUID = -813347393761382157L;

	@Override
    public int setErrorParameter(BeforeEnterEvent event,
          ErrorParameter<Exception> parameter) {
        
		ExceptionNotification.error(parameter.getException());
		
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
	
}