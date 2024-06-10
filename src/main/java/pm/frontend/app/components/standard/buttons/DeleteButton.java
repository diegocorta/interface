package pm.frontend.app.components.standard.buttons;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport("./styles/icon.css")
public class DeleteButton extends Button {
	
	private static final long serialVersionUID = 4945763797424390039L;

	public enum DeleteButtonType {
		DELETE("Borrar");

		private String text;
		
		DeleteButtonType(String text) {
			this.text = text;
		}
	}
	
	public DeleteButton(String text) {
		
		super(text);
		
		setDefaultTheme();

	}
	
	public DeleteButton(DeleteButtonType deleteButtonType) {
		
		super(deleteButtonType.text);

		setDefaultTheme();

	}
	
	
	private void setDefaultTheme() {
		
		this.addThemeVariants(ButtonVariant.LUMO_ERROR);
	}
	
}
