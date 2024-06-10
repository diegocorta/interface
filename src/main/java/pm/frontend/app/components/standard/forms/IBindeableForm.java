package pm.frontend.app.components.standard.forms;

import org.springframework.hateoas.EntityModel;

import com.vaadin.flow.data.binder.Binder;

import jakarta.validation.constraints.NotNull;

public interface IBindeableForm<T> extends IForm<T> {

	public Binder<T> setBinder();
	
	default boolean modifiedEntitiy(EntityModel<T> entity, @NotNull T newEntity) {
		
		if (entity == null) return true;
		if (!entity.getContent().equals(newEntity)) return true;
		
		return false;
	}
}
