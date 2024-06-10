package pm.frontend.app.components.standard.forms;

import org.springframework.hateoas.EntityModel;

import com.vaadin.flow.component.formlayout.FormLayout;

import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.layouts.EntityLayout;

public class Form<T, F extends FormLayout & IForm<T>> extends EntityLayout implements IForm<T> {

	private static final long serialVersionUID = 5169728995663015972L;
	
	private F form;
	
	public Form(F form, String formName, boolean isEditable) {

		super(formName, isEditable);
		
		this.form = form;
		
		setMainContent(form);

	}

	public void isEditMode(ViewType formStatus) {
		// TODO Auto-generated method stub
		
	}
	
	public void loadData(ViewType formStatus, EntityModel<T> entity) {
		
		form.loadData(formStatus, entity);
		
	}

	@Override
	public EntityAction<T> saveEntity() {
		
		return form.saveEntity();
	}

	@Override
	public EntityAction<T> deleteEntity() {
		
		return form.deleteEntity();
	}
		
}
