package pm.frontend.app.components.forms.security;

import org.springframework.web.client.HttpClientErrorException;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinResponse;

import es.common.util.TokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import pm.frontend.app.components.standard.buttons.DarkModeButton;
import pm.frontend.app.components.standard.buttons.PrimaryButton;
import pm.frontend.app.components.standard.components.AlertBuilder;
import pm.frontend.app.components.views.MainView;
import pm.frontend.app.logic.security.AuthenticationService;
import pm.security.v2.common.dto.JwtResponseDto;
import pm.security.v2.common.dto.UserLoginDto;

public class LoginForm extends VerticalLayout {
    
    private static final long serialVersionUID = 8340735161131123593L;
    
    private AuthenticationService authService;
    
    private H2 header;
    private TextField username;
    private PasswordField password;
    
    private PrimaryButton login;
    private Button forgotPassword;
    private DarkModeButton toogleDarkMode;
        
	public LoginForm(AuthenticationService authService) {
		
		this.authService = authService;
		
		header = new H2("CESuite 4.0");
		username = new TextField("Usuario");

		password = new PasswordField("Contraseña");
		
		login = new PrimaryButton("Iniciar sesión");
		forgotPassword = new Button("¿Has olvidado la contraseña?");
		
		toogleDarkMode = new DarkModeButton();
		
		login.addClickListener(click -> {
			if (username.getValue().length() > 0 && password.getValue().length() > 0) {
				login();
			} else {
				AlertBuilder.info("Información", null, "Debes rellenar el nombre de usuario y la contraseña").open();
			}
		});
		
		configuration();
		
        add(
    		toogleDarkMode,
    		header,
    		username,
    		password,
    		login,
    		forgotPassword
        );
        
        addAttachListener(oa -> {
        	JwtResponseDto autoLogin = authService.tryAutoLogin();

    		if (autoLogin != null) {
    			afterLogin(autoLogin);
    		}
        });
        
	}

	private void configuration() {
		
		password.setClearButtonVisible(true);
		
		forgotPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		forgotPassword.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		setAlignSelf(Alignment.END, toogleDarkMode);
	    		
		setSizeFull();
	    setJustifyContentMode(JustifyContentMode.CENTER);
	    setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
	    
		getStyle().set("text-align", "center");
		
		setMaxWidth(20, Unit.REM);
	    
	}
	
	private void login() {
				
		UserLoginDto userLogin = new UserLoginDto();
		userLogin.setUsername(username.getValue());
		userLogin.setPassword(password.getValue());
	
		try {
			
			JwtResponseDto response = authService.login(userLogin);
		
			afterLogin(response);
		
		} catch (HttpClientErrorException.Unauthorized e) {
			AlertBuilder.warn("Advertencia", null, "El usuario o la contraseña introducidos no son validos").open();
		}
	}
	
	private void afterLogin(JwtResponseDto response) {
		
		Claims claims = authService.validateToken(response.getToken());
		Long userId = Long.valueOf(claims.get(TokenUtils.SECURITY_USER).toString());

		authService.setTokenInSpringContext(claims, response.getToken(), response.getRefreshToken());
		authService.setVaadinSessionUser(userId);
		
        Cookie tokenCookie = new Cookie("token", response.getToken());
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setSecure(false);
        tokenCookie.setAttribute("SameSite", "Strict");
		
		Cookie refreshCookie = new Cookie("refresh", response.getRefreshToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setSecure(false);
		refreshCookie.setAttribute("SameSite", "Strict");
		 
		VaadinResponse httpResponse = VaadinResponse.getCurrent();
		httpResponse.addCookie(tokenCookie);
		httpResponse.addCookie(refreshCookie);
	
		this.getUI().ifPresent(ui -> ui.navigate(
                MainView.class));
		
	}
	
}