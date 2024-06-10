package pm.frontend.app.components.standard.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;

public class ComboBoxWithText<T> extends VerticalLayout {

	private static final long serialVersionUID = -3412993125635883242L;
	
	@Getter
	private Span text;
	@Getter
	private ComboBox<T> comboBox;
	
	public ComboBoxWithText (Span text, ComboBox<T> comboBox) {
		
		setMargin(false);
		setPadding(false);
		setSpacing(false);
		
		add(text, comboBox);
		
	}
}
