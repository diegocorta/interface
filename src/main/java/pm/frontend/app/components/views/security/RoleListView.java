package pm.frontend.app.components.views.security;

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

import pm.frontend.app.components.forms.security.RoleForm;
import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.tables.ITableWithForm;
import pm.frontend.app.components.standard.tables.Table;
import pm.frontend.app.components.standard.tables.TableWithForm;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.security.GroupService;
import pm.frontend.app.logic.security.RoleService;
import pm.security.v2.common.dto.RoleDto;

@PageTitle("Roles")
@Route(value = "/roles", layout = AppView.class)
public class RoleListView extends VerticalLayout implements ITableWithForm<RoleDto, RoleForm> {

	private static final long serialVersionUID = -8096395675294608218L;

	private RoleService roleService;
	private GroupService groupService;
	
	private TableWithForm<RoleDto, RoleForm> table;

	public RoleListView(RoleService roleService,
			GroupService groupService) {

		super();

		this.roleService = roleService;
		this.groupService = groupService;

		this.setSizeFull();
		this.setPadding(false);

		@SuppressWarnings("unchecked")
		Class<EntityModel<RoleDto>> clazz = (Class<EntityModel<RoleDto>>) (Class<?>) EntityModel.class;
		
		table = new TableWithForm<>(clazz, "Roles", this, false);
		table.initialize();

		add(table);


	}

	@Override
	public GridListDataView<EntityModel<RoleDto>> setContent() {
		// TODO Auto-generated method stub

		CollectionModel<EntityModel<RoleDto>> roles = roleService.getAllRoles();

		return table.setTableContent(roles.getContent());

	}
	
	@Override
	public void setSimpleSearch(GridListDataView<EntityModel<RoleDto>> dataView) {
		
		dataView.addFilter(person -> {
	    	
	    	String textToSearch = table.getSimpleTextValue();

	        if (!StringUtils.hasText(textToSearch))
	            return true;

	        return person.getContent().getName().toLowerCase().contains(textToSearch);

	    });
		
	}

	@Override
	public void addTableColumns(Grid<EntityModel<RoleDto>> grid) {

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
	public void addPhoneTableColumns(Grid<EntityModel<RoleDto>> grid) {
		
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
	public void executeOnItemSelected(Set<EntityModel<RoleDto>> selectedItems) {
		// TODO Auto-generated method stub
	}

	@Override
	public Form<RoleDto, RoleForm> buildFormDetails() {
		
		var formTable = new Form<RoleDto, RoleForm>(new RoleForm(roleService, groupService), "Roles", false);
		formTable.setSizeFull();

		return formTable;

	}

}
