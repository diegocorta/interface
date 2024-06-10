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

import pm.employee.common.dto.calendar.CalendarDto;
import pm.frontend.app.components.forms.calendar.CalendarForm;
import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.tables.ITableWithForm;
import pm.frontend.app.components.standard.tables.Table;
import pm.frontend.app.components.standard.tables.TableWithForm;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.calendar.CalendarService;

@PageTitle("Calendars")
@Route(value = "/calendars", layout = AppView.class)
public class CalendarListView extends VerticalLayout implements ITableWithForm<CalendarDto, CalendarForm> {

	private static final long serialVersionUID = 1035822337889363767L;

	private CalendarService calendarService;
	
	private TableWithForm<CalendarDto, CalendarForm> table;
	
	private Form<CalendarDto, CalendarForm> formTable;
	
	public CalendarListView(CalendarService calendarService) {
		
		super();
						
		this.calendarService = calendarService;
		
		this.setSizeFull();
		this.setPadding(false);
		
        @SuppressWarnings("unchecked")
		Class<EntityModel<CalendarDto>> clazz = (Class<EntityModel<CalendarDto>>) (Class<?>) EntityModel.class;
        
		this.table = new TableWithForm<CalendarDto, CalendarForm>(clazz, "Calendarios", this);
		
        table.initialize();
                
        add(table);
		
    }
	
	@Override
	public GridListDataView<EntityModel<CalendarDto>> setContent() {
		
		CollectionModel<EntityModel<CalendarDto>> calendar = calendarService.getAllCalendars();

	    return table.setTableContent(calendar.getContent());
	    	
	}
	
	@Override
	public void setSimpleSearch(GridListDataView<EntityModel<CalendarDto>> dataView) {
		
		dataView.addFilter(Calendar -> {
	    	
	    	String textToSearch = table.getSimpleTextValue();

	        if (!StringUtils.hasText(textToSearch))
	            return true;

	        return Calendar.getContent().getName().toLowerCase().contains(textToSearch);

	    });
		
	}

	@Override
	public void addTableColumns(Grid<EntityModel<CalendarDto>> grid) {
		
		
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
	public void addPhoneTableColumns(Grid<EntityModel<CalendarDto>> grid) {
		
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
	public void executeOnItemSelected(Set<EntityModel<CalendarDto>> selectedItems) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Form<CalendarDto, CalendarForm> buildFormDetails() {
		
		formTable = new Form<CalendarDto, CalendarForm>(new CalendarForm(calendarService), "Calendarios", true);
		formTable.setSizeFull();
		
		return formTable;
	}
	
}
