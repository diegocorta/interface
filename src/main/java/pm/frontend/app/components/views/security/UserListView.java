package pm.frontend.app.components.views.security;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

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

import pm.frontend.app.components.forms.security.UserForm;
import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.tables.ITableWithForm;
import pm.frontend.app.components.standard.tables.Table;
import pm.frontend.app.components.standard.tables.TableWithForm;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.security.GroupService;
import pm.frontend.app.logic.security.UserService;
import pm.security.v2.common.dto.UserDto;

@PageTitle("Contacts")
@Route(value = "/contacts", layout = AppView.class)
public class UserListView extends VerticalLayout implements ITableWithForm<UserDto, UserForm> {

	private static final long serialVersionUID = -8096395675294608218L;
	
	private UserService userService;
	private GroupService groupService;
	
	private TableWithForm<UserDto, UserForm> table;
	
	private Form<UserDto, UserForm> formTable;
	
	public UserListView(UserService userService,
			GroupService groupService) {
		
		super();
						
		this.userService = userService;
		this.groupService = groupService;
		
		this.setSizeFull();
		this.setPadding(false);
		
        @SuppressWarnings("unchecked")
		Class<EntityModel<UserDto>> clazz = (Class<EntityModel<UserDto>>) (Class<?>) EntityModel.class;
        
		this.table = new TableWithForm<UserDto, UserForm>(clazz, "Usuarios", this);
		
        table.initialize();
        
        add(table);
		
    }
	
	@Override
	public GridListDataView<EntityModel<UserDto>> setContent() {
		// TODO Auto-generated method stub
    	
		CollectionModel<EntityModel<UserDto>> people = userService.getAllUsers();
	    
		return table.setTableContent(people.getContent());
	
	}
	
	@Override
	public void setSimpleSearch(GridListDataView<EntityModel<UserDto>> dataView) {
		
		dataView.addFilter(person -> {
	    	
	    	String textToSearch = table.getSimpleTextValue();

	        if (!StringUtils.hasText(textToSearch))
	            return true;

	        return person.getContent().getUsername().toLowerCase().contains(textToSearch);

	    });
		
	}

	private static Renderer<EntityModel<UserDto>> createAvatarRenderer() {
        return LitRenderer.<EntityModel<UserDto>> of(
        		"<div style=\"display: flex; justify-content: center; align-items: center; height: 100%;\">"
			    + "    <vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
			    + "</div>");
    }
	
	private static Renderer<EntityModel<UserDto>> createEmployeeRenderer() {
		return LitRenderer.<EntityModel<UserDto>>of(
			    "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">" +
			    "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m); overflow: hidden;\">" +
			    "    <span style=\"white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%\"> ${item.fullName} </span>" +
			    "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%\">" +
			    "      ${item.email}" +
			    "    </span>" +
			    "    ${item.hasPassword ? html`<span theme=\"badge error\" style=\"font-size: var(--lumo-font-size-xxs); margin-top: 3px;\">Sin credenciales</span>` : ''}" +
			    "  </vaadin-vertical-layout>" +
			    "</vaadin-horizontal-layout>")
			.withProperty("fullName", user -> user.getContent().getUsername())
			.withProperty("email", user -> user.getContent().getEmail())
			.withProperty("hasPassword", user -> !user.getContent().isHasPassword());

	}

	@Override
	public void addTableColumns(Grid<EntityModel<UserDto>> grid) {
		
		grid.addColumn(createAvatarRenderer())
		       .setAutoWidth(true).setFlexGrow(0).setFrozen(true)
		       .setHeader("Image");
		
		grid.addColumn(user -> user.getContent().getUsername())
				.setAutoWidth(true).setFrozen(true)
				.setHeader("Nombre");
		
		grid.addColumn(user -> user.getContent().getEmail())
	       		.setAutoWidth(true).setFlexGrow(0).setFrozen(false)
	       		.setHeader("Correo");
		
		grid.addComponentColumn(user -> Table.createStatusIconFromBoolean(user.getContent().isHasPassword()))
				.setTextAlign(ColumnTextAlign.CENTER)
				.setFlexGrow(0).setWidth("7.5rem")
				.setHeader("Credenciales");
		
		grid.addComponentColumn(user -> Table.createStatusIconFromBoolean(user.getContent().getAllowLogin()))
				.setTextAlign(ColumnTextAlign.CENTER)
				.setFlexGrow(0).setWidth("10rem")
				.setHeader("Inicio de sesión");
		
		grid.addColumn(user -> ZonedDateTime.parse(user.getContent().getCreatedAt()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("10rem").setFlexGrow(0)
				.setHeader("Fecha de alta");

	}
	
	@Override
	public void addPhoneTableColumns(Grid<EntityModel<UserDto>> grid) {
		
		grid.addColumn(createAvatarRenderer())
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true).setFlexGrow(0)
				.setHeader("Image");
		
		grid.addColumn(createEmployeeRenderer())
				.setHeader("Usuario");
		
		grid.addComponentColumn(user -> Table.createStatusIconFromBoolean(user.getContent().getAllowLogin()))
				.setTextAlign(ColumnTextAlign.CENTER).setWidth("5rem").setFlexGrow(0)
				.setHeader("Login");
		
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
	public void executeOnItemSelected(Set<EntityModel<UserDto>> selectedItems) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Form<UserDto, UserForm> buildFormDetails() {
		
		formTable = new Form<UserDto, UserForm>(new UserForm(userService, groupService), "Usuarios", true);
		formTable.setSizeFull();
		
		return formTable;
	}
	
}
