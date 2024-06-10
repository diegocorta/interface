package pm.frontend.app.components.standard.tables;

import java.util.Set;

import org.springframework.hateoas.EntityModel;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;

public interface ITable<T> {

	void addTableColumns(Grid<EntityModel<T>> grid);
	
	void addPhoneTableColumns(Grid<EntityModel<T>> grid);
	
	void addSearchListener();
	
	void executeOnItemSelected(Set<EntityModel<T>> selectedItems);
	
	void addFilterComponent();
	
	GridListDataView<EntityModel<T>> setContent();
	
	void setSimpleSearch(GridListDataView<EntityModel<T>> dataView);
			
}
