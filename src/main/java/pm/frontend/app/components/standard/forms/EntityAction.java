package pm.frontend.app.components.standard.forms;

import org.springframework.hateoas.EntityModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntityAction<T> {

	public enum Action {
		CREATE, UPDATE, DELETE
	}
	
	private Action action;
	private EntityModel<T> entity;
	private EntityModel<T> previous;
	private String classDescriptor;
}
