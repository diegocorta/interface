package pm.frontend.app.components.standard.buttons;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.server.StreamResource;

@CssImport("./styles/icon.css")
public class LogoutButton extends Button {
	
	private static final long serialVersionUID = 4945763797424390039L;

	public LogoutButton(String text) {
		
		super(text);
		
		StreamResource iconResource = new StreamResource("singout.svg",
			    () -> getClass().getResourceAsStream("/icons/singout.svg"));
		
		SvgIcon icon = new SvgIcon(iconResource);
		this.setIcon(icon);
		
		addClickEvent();
		setDefaultTheme();
	}
	
	public LogoutButton(Icon icon) {
		
		super();

		this.setIcon(icon);
		
		addClickEvent();
		setDefaultTheme();
	}
	
	public LogoutButton() {
		
		super();

		StreamResource iconResource = new StreamResource("singout.svg",
			    () -> getClass().getResourceAsStream("/icons/singout.svg"));
		
		SvgIcon icon = new SvgIcon(iconResource);
		this.setIcon(icon);
		
		addClickEvent();
		setDefaultTheme();
	}
	
	private void addClickEvent() {
		
		this.addClickListener(click -> UI.getCurrent().getPage().setLocation("/pm-app/logoutview"));
	}
	
	private void setDefaultTheme() {
		
		this.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		if (this.getIcon() != null)
			this.getIcon().setClassName("normal-icon");
	}
	
}
