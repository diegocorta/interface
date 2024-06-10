package pm.frontend.app.components.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import pm.frontend.app.components.customization.ThemeConfig;
import pm.frontend.app.components.forms.security.LoginForm;
import pm.frontend.app.logic.security.AuthenticationService;

@PageTitle("Pagina de login")
@Route("/login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {
    
    private static final long serialVersionUID = -7423367689526453140L;
    
	private LoginForm loginForm;
	private ThemeConfig themeConfig = ThemeConfig.getCurrent();
    
	public LoginView(AuthenticationService authService) {
		
		loginForm = new LoginForm(authService);
		
		configureMainView();
		
        add(
        	loginForm
        );
        
    }
	
	private void configureMainView() {
				
		themeConfig.initializeDarkMode();
		
		getStyle().setHeight("100%");
	    setJustifyContentMode(JustifyContentMode.CENTER);
	    setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	    setAlignSelf(Alignment.CENTER, loginForm);
	    	    		
	}

}