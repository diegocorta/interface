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

import pm.employee.common.dto.calendar.WorkdayDto;
import pm.frontend.app.components.forms.calendar.WorkdayForm;
import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.tables.ITableWithForm;
import pm.frontend.app.components.standard.tables.Table;
import pm.frontend.app.components.standard.tables.TableWithForm;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.calendar.WorkdayService;

@PageTitle("Workdays")
@Route(value = "/workdays", layout = AppView.class)
public class WorkdayListView extends VerticalLayout implements ITableWithForm<WorkdayDto, WorkdayForm> {

	private static final long serialVersionUID = 1035822337889363767L;

	private WorkdayService workdayService;
	
	private TableWithForm<WorkdayDto, WorkdayForm> table;
	
	private Form<WorkdayDto, WorkdayForm> formTable;
	
	public WorkdayListView(WorkdayService workdayService) {
		
		super();
						
		this.workdayService = workdayService;
		
		this.setSizeFull();
		this.setPadding(false);
		
        @SuppressWarnings("unchecked")
		Class<EntityModel<WorkdayDto>> clazz = (Class<EntityModel<WorkdayDto>>) (Class<?>) EntityModel.class;
        
		this.table = new TableWithForm<WorkdayDto, WorkdayForm>(clazz, "Jornadas", this);
		
        table.initialize();
                
        add(table);
		
    }
	
	@Override
	public GridListDataView<EntityModel<WorkdayDto>> setContent() {
		
		CollectionModel<EntityModel<WorkdayDto>> workday = workdayService.getAllWorkdays();

		return table.setTableContent(workday.getContent());
	    	
	}
	
	@Override
	public void setSimpleSearch(GridListDataView<EntityModel<WorkdayDto>> dataView) {
		
		dataView.addFilter(Workday -> {
	    	
	    	String textToSearch = table.getSimpleTextValue();

	        if (!StringUtils.hasText(textToSearch))
	            return true;

	        return Workday.getContent().getName().toLowerCase().contains(textToSearch);

	    });
		
	}

	@Override
	public void addTableColumns(Grid<EntityModel<WorkdayDto>> grid) {
		
		
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
	public void addPhoneTableColumns(Grid<EntityModel<WorkdayDto>> grid) {
		
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
	public void executeOnItemSelected(Set<EntityModel<WorkdayDto>> selectedItems) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Form<WorkdayDto, WorkdayForm> buildFormDetails() {
		
		formTable = new Form<WorkdayDto, WorkdayForm>(new WorkdayForm(workdayService), "Jornadas", true);
		formTable.setSizeFull();
		
		return formTable;
	}
	
}
