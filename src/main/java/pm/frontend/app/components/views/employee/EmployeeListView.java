package pm.frontend.app.components.views.employee;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pm.employee.common.dto.employee.EmployeeDto;
import pm.frontend.app.components.forms.employee.EmployeeForm;
import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.layouts.EntityLayout;
import pm.frontend.app.components.standard.tables.ITable;
import pm.frontend.app.components.standard.tables.Table;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.CountryService;
import pm.frontend.app.logic.employee.EmployeeService;
import pm.frontend.app.logic.employee.EmployeeServicesWrapper;
import pm.frontend.app.logic.security.UserService;

@PageTitle("Contacts")
@Route(value = "/employees", layout = AppView.class)
public class EmployeeListView extends VerticalLayout implements ITable<EmployeeDto> {

	private static final long serialVersionUID = -8096395675294608218L;
	
//	private CountryService countryService;
	private EmployeeService employeeService;
	
	private Table<EmployeeDto> table;
	
	private VerticalLayout tableLayout;
	private VerticalLayout detailsLayout;
	
	private EntityLayout entityLayout;
	
	private EmployeeForm employeeForm;
			
	public EmployeeListView(
			CountryService countryService,
			EmployeeServicesWrapper employeeServicesWrapper,
			UserService userService) {
		
		super();
		
		employeeForm = new EmployeeForm(countryService, employeeServicesWrapper, userService, ViewType.EDIT);
		
		entityLayout = new EntityLayout(employeeForm, "Empleados", true);
		
		buildEntityLayout();
		
		tableLayout = new VerticalLayout();
		tableLayout.setSizeFull();
		tableLayout.setPadding(false);
		
		detailsLayout = new VerticalLayout();
		detailsLayout.setSizeFull();
		detailsLayout.setPadding(false);
		detailsLayout.setVisible(false);
		
		this.employeeService = employeeServicesWrapper.getEmployeeService();
		
		this.setSizeFull();
		this.setPadding(false);
		
        @SuppressWarnings("unchecked")
		Class<EntityModel<EmployeeDto>> clazz = (Class<EntityModel<EmployeeDto>>) (Class<?>) EntityModel.class;
        
		this.table = new Table<EmployeeDto>(clazz, "Empleados", this);
		
        table.initialize();
                
        tableLayout.add(table);
        detailsLayout.add(entityLayout);
        
        setAddListener();
        
        add(tableLayout, detailsLayout);
		
    }
	
	private void buildEntityLayout() {
		
		entityLayout.getCancel().addClickListener(click -> {
			toogleDetailsForm(false);
		});
		entityLayout.getClose().addClickListener(click -> {
			toogleDetailsForm(false);
		});
		entityLayout.getSave().addClickListener(click -> {
			employeeForm.save();
			toogleDetailsForm(false);
		});
		entityLayout.getDelete().addClickListener(click -> {
			employeeForm.delete();
		});
	}

	@Override
	public GridListDataView<EntityModel<EmployeeDto>> setContent() {
		// TODO Auto-generated method stub
    	
		CollectionModel<EntityModel<EmployeeDto>> people = employeeService.getAllEmployees();
	    
	    return table.getGrid().setItems(people.getContent());
	
	}
	
	@Override
	public void setSimpleSearch(GridListDataView<EntityModel<EmployeeDto>> dataView) {
		
		dataView.addFilter(person -> {
	    	
	    	String textToSearch = table.getSimpleTextValue();

	        if (!StringUtils.hasText(textToSearch))
	            return true;

	        return person.getContent().getFullName().toLowerCase().contains(textToSearch);

	    });
		
	}

	private static Renderer<EntityModel<EmployeeDto>> createAvatarRenderer() {
        return LitRenderer.<EntityModel<EmployeeDto>> of(
        		"<div style=\"display: flex; justify-content: center; align-items: center; height: 100%;\">"
			    + "    <vaadin-avatar img=\"${item.image}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
			    + "</div>")
        		.withProperty("image", item -> {
        			if (item.getContent().getImage() != null) {
            			return "data:"+item.getContent().getImageType()+";base64,"+Base64.getEncoder().encodeToString(item.getContent().getImage());
        			} else {
        				return null;
        			}
        		});
    }
	
	private static Renderer<EntityModel<EmployeeDto>> createEmployeeRenderer() {
		return LitRenderer.<EntityModel<EmployeeDto>>of(
			    "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">" +
			    "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m); overflow: hidden;\">" +
			    "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%\"> ${item.fullName} </span>" +
			    "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%\">" +
			    "      ${item.dni}" +
			    "    </span>" +
			    "    <span theme=\"badge info\" style=\"font-size: var(--lumo-font-size-xxs); margin-top: 3px;\">" +
			    "		${item.job}" +
			    "	</span>" +
			    "  </vaadin-vertical-layout>" +
			    "</vaadin-horizontal-layout>")
			.withProperty("fullName", user -> Arrays.stream(user.getContent().getFullName().split("\\s+"))
		    		.map(StringUtils::capitalize)
		    		.collect(Collectors.joining(" ")))
			.withProperty("dni", user -> user.getContent().getCardId())
			.withProperty("job", user -> user.getContent().getJob());

	}

	@Override
	public void addTableColumns(Grid<EntityModel<EmployeeDto>> grid) {
		
		grid.addColumn(createAvatarRenderer())
		       .setAutoWidth(true).setFlexGrow(0).setFrozen(true)
		       .setHeader("Image");
		
		grid.addColumn(user -> user.getContent().getCardId().toUpperCase())
   				.setWidth("8rem").setFlexGrow(0).setFrozen(true)
   				.setHeader("DNI");	
		
		grid.addColumn(user -> 
				Arrays.stream(user.getContent().getFullName().split("\\s+"))
		    		.map(StringUtils::capitalize)
		    		.collect(Collectors.joining(" ")))
				.setWidth("14rem")
				.setFlexGrow(0)
				.setFrozen(true)
				.setHeader("Nombre");
		
		grid.addColumn(user -> user.getContent().getJob())
				.setAutoWidth(true).setFrozen(false)
				.setHeader("Puesto");
		
		grid.addColumn(user -> user.getContent().getGender())
				.setAutoWidth(true).setFlexGrow(0).setFrozen(false)
				.setHeader("Genero");

	}
	
	@Override
	public void addPhoneTableColumns(Grid<EntityModel<EmployeeDto>> grid) {
		
		grid.addColumn(createAvatarRenderer())
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true).setFlexGrow(0)
				.setHeader("Image");
		
		grid.addColumn(createEmployeeRenderer())
				.setHeader("Usuario");
		
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
	public void executeOnItemSelected(Set<EntityModel<EmployeeDto>> selectedItems) {
		
		toogleDetailsForm(true, selectedItems, ViewType.EDIT);
		
	}
	
	private void toogleDetailsForm(boolean open, Set<EntityModel<EmployeeDto>> selectedItems, ViewType viewType) {
		
		if (open && selectedItems.size() > 0 && viewType.equals(ViewType.EDIT)) {
			
			employeeForm.loadData(selectedItems.stream().findFirst().get());
			employeeForm.setViewType(ViewType.EDIT);
			
			this.detailsLayout.setVisible(true);
			this.tableLayout.setVisible(false);
		} else if (open && viewType.equals(ViewType.NEW)) {
			
			employeeForm.loadNew();
			employeeForm.setViewType(ViewType.NEW);
			
			this.detailsLayout.setVisible(true);
			this.tableLayout.setVisible(false);
		} else {
			
			this.detailsLayout.setVisible(false);
			this.tableLayout.setVisible(true);
			table.getGrid().deselectAll();
			setContent();
			
		}

	}
	
	private void toogleDetailsForm(boolean open) {	
		toogleDetailsForm(open, new HashSet<>(), ViewType.VIEW);
	}
	
	private void toogleDetailsForm(boolean open, ViewType viewType) {	
		toogleDetailsForm(open, new HashSet<>(), viewType);
	}
	
	private void setAddListener() {
		
		table.getAddButton().addClickListener(click -> {
			toogleDetailsForm(true, ViewType.NEW);
		});
				
	}
	
}
