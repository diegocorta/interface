package pm.frontend.app.components.exceptions.route;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import pm.frontend.app.components.views.LoginView;

@Tag(Tag.DIV)
public class RouteNotFoundError extends Component
       implements HasErrorParameter<NotFoundException> {

	private static final long serialVersionUID = -605140341679786443L;

	@Override
    public int setErrorParameter(BeforeEnterEvent event,
          ErrorParameter<NotFoundException> parameter) {
		
        event.rerouteTo(LoginView.class);
    	
        return HttpServletResponse.SC_MOVED_TEMPORARILY;
    }
	
}