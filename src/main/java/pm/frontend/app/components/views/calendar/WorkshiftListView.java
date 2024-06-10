package pm.frontend.app.components.views.calendar;

import java.util.Set;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pm.employee.common.dto.calendar.WorkshiftDto;
import pm.frontend.app.components.forms.calendar.WorkshiftForm;
import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.tables.ITableWithForm;
import pm.frontend.app.components.standard.tables.Table;
import pm.frontend.app.components.standard.tables.TableWithForm;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.calendar.WorkshiftService;

@PageTitle("Workshifts")
@Route(value = "/workshifts", layout = AppView.class)
public class WorkshiftListView extends VerticalLayout implements ITableWithForm<WorkshiftDto, WorkshiftForm> {

	private static final long serialVersionUID = 1035822337889363767L;

	private WorkshiftService workshiftService;
	
	private TableWithForm<WorkshiftDto, WorkshiftForm> table;
	
	private Form<WorkshiftDto, WorkshiftForm> formTable;
	
	public WorkshiftListView(WorkshiftService workshiftService) {
		
		super();
						
		this.workshiftService = workshiftService;
		
		this.setSizeFull();
		this.setPadding(false);
		
        @SuppressWarnings("unchecked")
		Class<EntityModel<WorkshiftDto>> clazz = (Class<EntityModel<WorkshiftDto>>) (Class<?>) EntityModel.class;
        
		this.table = new TableWithForm<WorkshiftDto, WorkshiftForm>(clazz, "Turnos", this);
		
        table.initialize();
                
        add(table);
		
    }
	
	@Override
	public GridListDataView<EntityModel<WorkshiftDto>> setContent() {
		
		CollectionModel<EntityModel<WorkshiftDto>> workshift = workshiftService.getAllWorkshift();

		return table.setTableContent(workshift.getContent());
	    	
	}
	
	@Override
	public void setSimpleSearch(GridListDataView<EntityModel<WorkshiftDto>> dataView) {
		
		dataView.addFilter(workshift -> {
	    	
	    	String textToSearch = table.getSimpleTextValue();

	        if (!StringUtils.hasText(textToSearch))
	            return true;

	        return workshift.getContent().getName().toLowerCase().contains(textToSearch);

	    });
		
	}

	@Override
	public void addTableColumns(Grid<EntityModel<WorkshiftDto>> grid) {
		
		
		grid.addColumn(role -> role.getContent().getName())
				.setWidth("15rem").setFlexGrow(0)
				.setHeader("Nombre");

		grid.addColumn(role -> role.getContent().getDescription())
				.setHeader("Descripcion");
		
		grid.addComponentColumn(role -> Table.createStatusIconFromBoolean(role.getContent().isActive()))
				.setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("5rem").setFlexGrow(0)
				.setHeader("Activo");

	}
	
	@Override
	public void addPhoneTableColumns(Grid<EntityModel<WorkshiftDto>> grid) {
		
		grid.addColumn(role -> role.getContent().getName())
				.setHeader("Nombre");

		grid.addComponentColumn(role -> Table.createStatusIconFromBoolean(role.getContent().isActive()))
				.setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("5rem").setFlexGrow(0)
				.setHeader("Activo");
			}

	@Override
	public void addSearchListener() {
		// TODO Auto-generated method stub
		
	}
 
	@Override
	public void addFilterComponent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeOnItemSelected(Set<EntityModel<WorkshiftDto>> selectedItems) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Form<WorkshiftDto, WorkshiftForm> buildFormDetails() {
		
		formTable = new Form<WorkshiftDto, WorkshiftForm>(new WorkshiftForm(workshiftService), "Turnos", true);
		formTable.setSizeFull();
		
		return formTable;
	}
	
}
