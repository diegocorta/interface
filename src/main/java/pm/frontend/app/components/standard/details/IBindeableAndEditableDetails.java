package pm.frontend.app.components.standard.details;

import java.util.Collection;
import java.util.Set;

import org.springframework.hateoas.EntityModel;

public interface IBindeableAndEditableDetails<T> extends IBindeableDetails<T> {
	
	public Collection<EntityModel<T>> addElements(Set<EntityModel<T>> entities);
	
	public Collection<EntityModel<T>> removeElements(Set<EntityModel<T>> entities);

}
