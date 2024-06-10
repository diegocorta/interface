package pm.frontend.app.components.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pm.frontend.app.configuration.AuthenticationConfig;
import pm.frontend.app.logic.security.AuthenticationService;

@PageTitle("Pagina de logout")
@Route("/logoutview")
public class LogoutView extends VerticalLayout {
    
    private static final long serialVersionUID = -7423367689526453140L;
    
	public LogoutView(AuthenticationService authService) {
		
		addAttachListener(oa -> {
			
			AuthenticationConfig.logout((HttpServletRequest) VaadinService.getCurrentRequest(), (HttpServletResponse) VaadinService.getCurrentResponse());
				    	
	    	VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                session.close();
                session.getSession().invalidate();
            }
		});
        
    }
}