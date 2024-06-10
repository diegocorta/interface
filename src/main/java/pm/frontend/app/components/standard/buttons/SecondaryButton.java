package pm.frontend.app.components.standard.buttons;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport("./styles/icon.css")
public class SecondaryButton extends Button {
	
	private static final long serialVersionUID = 4945763797424390039L;

	public enum SecondaryButtonType {
		CANCEL("Cancelar"), CLOSE("Cerrar");

		private String text;
		
		SecondaryButtonType(String text) {
			this.text = text;
		}
	}
	
	public SecondaryButton(String text) {
		
		super(text);
		
		setDefaultTheme();
		setShortcuts();
	}
	
	public SecondaryButton(SecondaryButtonType secondaryButtonType) {
		
		super(secondaryButtonType.text);

		setDefaultTheme();
		setShortcuts();

	}
	
	
	private void setDefaultTheme() {
		
		this.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
	}
	
	private void setShortcuts() {
		
	    this.addClickShortcut(Key.ESCAPE);
	}
	
}
