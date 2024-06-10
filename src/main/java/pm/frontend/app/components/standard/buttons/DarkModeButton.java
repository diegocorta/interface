package pm.frontend.app.components.standard.buttons;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import pm.frontend.app.components.customization.ThemeConfig;

@CssImport("./styles/icon.css")
public class DarkModeButton extends Button {

	private static final long serialVersionUID = -2978775057187912454L;
	
	private ThemeConfig themeConfig = ThemeConfig.getCurrent();
	
	public DarkModeButton(String text) {

		super(text);

		this.setIcon(new Icon(VaadinIcon.ADJUST));

		addClickEvent();
		setDefaultTheme();
	}

	public DarkModeButton(Icon icon) {

		super();

		this.setIcon(icon);

		addClickEvent();
		setDefaultTheme();
	}

	public DarkModeButton() {

		super();

		this.setIcon(new Icon(VaadinIcon.ADJUST));

		addClickEvent();
		setDefaultTheme();
	}

	private void addClickEvent() {

		this.addClickListener(click -> themeConfig.toggleToDarkLightMode());

	}

	private void setDefaultTheme() {
		this.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		if (this.getIcon() != null)
			this.getIcon().setClassName("normal-icon");
	}

}
