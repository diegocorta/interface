package pm.frontend.app.components.standard.tables;

import com.vaadin.flow.component.formlayout.FormLayout;

import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.forms.IForm;

public interface ITableWithForm<T, F extends FormLayout & IForm<T>> extends ITable<T> {
		
	Form<T, F> buildFormDetails();
	
}
