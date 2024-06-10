package pm.frontend.app.components.standard.buttons;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport("./styles/icon.css")
public class PrimaryButton extends Button {
	
	private static final long serialVersionUID = 4945763797424390039L;

	public enum PrimaryButtonType {
		ACCEPT("Aceptar"), SAVE("Guardar");

		private String text;
		
		PrimaryButtonType(String text) {
			this.text = text;
		}
	}
	
	public PrimaryButton(String text) {
		
		super(text);
		
		setDefaultTheme();
		setShortcuts();

	}
	
	public PrimaryButton(PrimaryButtonType primaryButtonType) {
		
		super(primaryButtonType.text);

		setDefaultTheme();
		setShortcuts();

	}
	
	
	private void setDefaultTheme() {
		
		this.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	}
	
	private void setShortcuts() {
		
	    this.addClickShortcut(Key.ENTER);
	}
	
}
