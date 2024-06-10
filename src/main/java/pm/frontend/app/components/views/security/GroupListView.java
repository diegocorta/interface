package pm.frontend.app.components.views.security;

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

import pm.frontend.app.components.forms.security.GroupForm;
import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.tables.ITableWithForm;
import pm.frontend.app.components.standard.tables.Table;
import pm.frontend.app.components.standard.tables.TableWithForm;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.security.GroupService;
import pm.frontend.app.logic.security.RoleService;
import pm.frontend.app.logic.security.UserService;
import pm.security.v2.common.dto.GroupDto;

@PageTitle("Grupos")
@Route(value = "/groups", layout = AppView.class)
public class GroupListView extends VerticalLayout implements ITableWithForm<GroupDto, GroupForm> {

	private static final long serialVersionUID = -8096395675294608218L;
	
	private GroupService groupService;
	private UserService userService;
	private RoleService roleService;
	
	private TableWithForm<GroupDto, GroupForm> table;
	
	private Form<GroupDto, GroupForm> formTable;
	
	public GroupListView(GroupService groupService,
			UserService userService, RoleService roleService) {
		
		super();
					
		this.groupService = groupService;
		this.userService = userService;
		this.roleService = roleService;
		
		this.setSizeFull();
        this.setPadding(false);
		
        @SuppressWarnings("unchecked")
		Class<EntityModel<GroupDto>> clazz = (Class<EntityModel<GroupDto>>) (Class<?>) EntityModel.class;
        
        
		table = new TableWithForm<>(clazz, "Grupos", this);
		table.initialize();

        add(table);
        
    }
	
	public GridListDataView<EntityModel<GroupDto>> setContent() {
		// TODO Auto-generated method stub
		
		CollectionModel<EntityModel<GroupDto>> groups = groupService.getAllGroups();
	    
		return table.setTableContent(groups.getContent());
	
	}
	
	@Override
	public void setSimpleSearch(GridListDataView<EntityModel<GroupDto>> dataView) {
		
		dataView.addFilter(person -> {
	    	
	    	String textToSearch = table.getSimpleTextValue();

	        if (!StringUtils.hasText(textToSearch))
	            return true;

	        return person.getContent().getName().toLowerCase().contains(textToSearch);

	    });
		
	}

	private static Renderer<EntityModel<GroupDto>> createAvatarRenderer() {
        return LitRenderer.<EntityModel<GroupDto>> of(
        		"<div style=\"display: flex; justify-content: center; align-items: center; height: 100%;\">"
        			    + "    <vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
        			    + "</div>");
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
	public void executeOnItemSelected(Set<EntityModel<GroupDto>> selectedItems) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Form<GroupDto, GroupForm> buildFormDetails() {
		
        formTable = new Form<GroupDto, GroupForm>(new GroupForm(groupService, userService, roleService), "Grupos", true);
        formTable.setSizeFull();
        
		return formTable;
	}

	@Override
	public void addTableColumns(Grid<EntityModel<GroupDto>> grid) {
		
		grid.addColumn(createAvatarRenderer())
		       .setAutoWidth(true).setFlexGrow(0).setFrozen(true)
		       .setHeader("Image");
		
		grid.addColumn(group -> group.getContent().getName())
				.setFrozen(true)
				.setWidth("15rem").setFlexGrow(0)
				.setHeader("Nombre");
		
		grid.addColumn(group -> group.getContent().getDescription())
				.setHeader("Descripcion");
		
		grid.addComponentColumn(group -> Table.createStatusIconFromBoolean(group.getContent().isActive()))
				.setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("5rem").setFlexGrow(0)
				.setHeader("Activo");

	}

	@Override
	public void addPhoneTableColumns(Grid<EntityModel<GroupDto>> grid) {
		
		grid.addColumn(createAvatarRenderer())
	       		.setAutoWidth(true).setFlexGrow(0).setFrozen(true)
	       		.setHeader("Image");
		
		grid.addColumn(group -> group.getContent().getName())
				.setHeader("Nombre");
		
		grid.addComponentColumn(group -> Table.createStatusIconFromBoolean(group.getContent().isActive()))
				.setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("5rem").setFlexGrow(0)
				.setHeader("Activo");
	}
	
}
