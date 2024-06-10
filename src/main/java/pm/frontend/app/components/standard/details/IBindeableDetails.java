package pm.frontend.app.components.standard.details;

import org.springframework.hateoas.EntityModel;

public interface IBindeableDetails<T> {
	
	public String getPrincipal(EntityModel<T> entity);
		
	public void reset();
}
