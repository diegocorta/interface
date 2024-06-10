package pm.frontend.app.components.standard.layouts;

import java.io.ByteArrayInputStream;

import org.apache.commons.text.WordUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoIcon;

import pm.frontend.app.components.standard.buttons.DarkModeButton;
import pm.frontend.app.components.standard.buttons.LogoutButton;
import pm.frontend.app.components.standard.buttons.MessagesButton;
import pm.frontend.app.logic.security.AuthenticationService;

@CssImport("./styles/header.css")
@CssImport("./styles/icon.css")
public class MainHeader extends HorizontalLayout {
	
	private static final long serialVersionUID = 6855721306508217752L;
	
    // Left part
	private DrawerToggle toogle;
	private H1 title;
	
	private AuthenticationService authenticationService;
	
	public MainHeader(DrawerToggle toogle,
			AuthenticationService authenticationService) {
		
		super();
		
		this.toogle = toogle;
		this.authenticationService = authenticationService;
		
		HorizontalLayout leftInfo = buildLeftInfo();
		HorizontalLayout rightInfo = buildRightInfo();
		
		addClassName("header-custom");
		
	    getThemeList().set("dark", true);
	    setSpacing(false);
	    setAlignItems(FlexComponent.Alignment.CENTER);
		 
	    // Have the drawer toggle button on the left
	    add(leftInfo, rightInfo);

	}

	private HorizontalLayout buildLeftInfo() {
		
		HorizontalLayout leftInfo = new HorizontalLayout();
		
		title = new H1("Menú");
		
		title.getStyle().set("font-size", "var(--lumo-font-size-l)")
    	.set("margin", "0");
	
		leftInfo.add(toogle, title);
		leftInfo.setAlignItems(FlexComponent.Alignment.CENTER);
		leftInfo.setSpacing(false);
		
		return leftInfo;
		
	}
	
	private HorizontalLayout buildRightInfo() {
		
		HorizontalLayout rightInfo = new HorizontalLayout();
		
		rightInfo.add(
				buildNotificationsMenu(),
				buildSettingsMenu(),
				buildUserMenu()
				);
		rightInfo.setAlignItems(FlexComponent.Alignment.CENTER);
		rightInfo.getStyle().setMarginRight("15px");
		
		return rightInfo;
	}
	
	
	private Component buildUserMenu() {
		
		HorizontalLayout userInfo = new HorizontalLayout();

		var employeeDto = authenticationService.getVaadinSessionUser();
		
		String fullString = WordUtils.capitalize(employeeDto.getContent().getFullName());
		
		Span name = new Span(fullString);
		name.getStyle().set("max-width", "16vw"); // Establece el ancho máximo
		name.getStyle().set("overflow", "hidden"); // Oculta el exceso de texto que se trunca
		name.getStyle().set("text-overflow", "ellipsis"); // Agrega puntos suspensivos (...) al texto truncado
		name.getStyle().set("white-space", "nowrap");
		name.getStyle().setPaddingRight("10px");
		
		Avatar avatar = new Avatar(fullString);
		avatar.setClassName("header-avatar");
		name.setClassName("header-avatar-name");
		
		if (employeeDto.getContent().getImage() != null) {
			// Create a StreamResource from the byte array
	        StreamResource resource = new StreamResource("avatar.png", () -> {
	            return new ByteArrayInputStream(employeeDto.getContent().getImage());
	        });
	
	        // Set the image as the avatar image
	        avatar.setImageResource(resource);
		}
        avatar.getStyle().setPadding("0");
        
		userInfo.add(name, avatar);
		userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
		userInfo.setSpacing(false);
		
        ContextMenu menu = new ContextMenu();

        menu.setTarget(userInfo);
        menu.setOpenOnClick(true);

		Button profileButton = new Button("Perfil");
		profileButton.setIcon(LumoIcon.USER.create());
		
		Button config = new Button("Configuracion");
		config.setIcon(LumoIcon.COG.create());
		
		profileButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		config.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		LogoutButton logoutButton = new LogoutButton("Cerrar sesion");
		logoutButton.getIcon().getStyle().setWidth("20px");
		
        menu.addItem(profileButton).addClickListener(mc -> {
        	redirectToProfile(this);
        });
        menu.addItem(config).addClickListener(mc -> {
        	redirectToConfig(this);
        });
        menu.addItem(new Hr());
        menu.addItem(logoutButton).addClickListener(mc -> {
        	logoutButton.click();
        });
        
        menu.addClassName("mobile-context-menu");

        return userInfo;
        
	}

	private HorizontalLayout buildSettingsMenu() {
		
		HorizontalLayout hl = new HorizontalLayout();

        Button preferences = new Button(LumoIcon.COG.create());
        preferences.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        preferences.getIcon().setClassName("normal-icon");
//        preferences.getStyle().setMargin("-5px");
        
        preferences.addClickListener(click -> {
    		redirectToConfig(preferences);	
        });
        
        
        hl.add(preferences);
        hl.setAlignItems(FlexComponent.Alignment.CENTER);
//		hl.getStyle().setMargin("-5px");   
		
		return hl;
	}

	private void redirectToConfig(Component component) {
		component.getUI().ifPresent(ui ->
		   ui.navigate("configuration"));
	}
	
	private void redirectToProfile(Component component) {
		component.getUI().ifPresent(ui ->
		   ui.navigate("/profile"));
		
		
	}
	
	private HorizontalLayout buildNotificationsMenu() {
		
		HorizontalLayout hl = new HorizontalLayout();
		
		StreamResource iconResource = new StreamResource("inbox.svg",
			    () -> getClass().getResourceAsStream("/icons/inbox.svg"));
		
		SvgIcon inbox = new SvgIcon(iconResource);
		
		MessagesButton inboxButton = new MessagesButton(inbox, 10);
		MessagesButton notificationButton = new MessagesButton(LumoIcon.BELL.create(), 1);

		DarkModeButton toogleDarkModeButton = new DarkModeButton();
        toogleDarkModeButton.setTooltipText("Modo claro/oscuro");
        toogleDarkModeButton.getStyle().setMarginRight("7px");  
        toogleDarkModeButton.setClassName("header-toogle-dark");
        
		hl.add(
				toogleDarkModeButton,
				inboxButton,
				notificationButton
		);
		
		hl.setAlignItems(FlexComponent.Alignment.CENTER);
//		hl.getStyle().setMargin("-5px");
		
		return hl;
	}
	
	
 
}
