package pm.frontend.app.components.standard.forms;

import org.springframework.hateoas.EntityModel;

import pm.frontend.app.components.standard.components.ViewType;

public interface IForm<T> {

	public void isEditMode(ViewType formStatus);
	
	public void loadData(ViewType formStatus, EntityModel<T> entityModel);
	
	public EntityAction<T> saveEntity();
	
	public EntityAction<T> deleteEntity();

}
