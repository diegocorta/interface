package pm.frontend.app.components.forms.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.details.IBindeableAndEditableDetails;
import pm.frontend.app.components.standard.details.IBindeableDetails;
import pm.frontend.app.components.standard.details.SingleLineDetails;
import pm.frontend.app.components.standard.forms.EntityAction;
import pm.frontend.app.components.standard.forms.IBindeableForm;
import pm.frontend.app.logic.security.GroupService;
import pm.frontend.app.logic.security.RoleService;
import pm.security.v2.common.dto.GroupRoleDto;
import pm.security.v2.common.dto.RoleDto;
import pm.security.v2.common.dto.UserDto;
import pm.security.v2.common.dto.min.GroupMinDto;

public class RoleForm extends FormLayout implements IBindeableForm<RoleDto> {

	private static final long serialVersionUID = 1596400676900430164L;
		
	private TextField name;
    private TextArea description;

	private TextField code;

    private TextField parent;
    
    private Binder<RoleDto> binder;

    private SingleLineDetails<GroupMinDto> groups;
    private SingleLineDetails<UserDto> users;
    
    private EntityModel<RoleDto> entityRole;
    private Collection<EntityModel<GroupMinDto>> entityGroups = new ArrayList<>();
    private Collection<EntityModel<UserDto>> entityUsers = new ArrayList<>();
    
	private RoleService roleService;
	
	public RoleForm(RoleService roleService,
			GroupService groupService) {
	
		this.roleService = roleService;
		
        name = new TextField("Nombre");
        name.setReadOnly(true);
        code = new TextField("Codigo");
        code.setReadOnly(true);

        description = new TextArea("Descripción");
        description.setReadOnly(true);
        
        parent = new TextField("Rol padre");
        parent.setReadOnly(true);

        IBindeableDetails<UserDto> bindFunctionUser = new IBindeableDetails<UserDto>() {
			
        	@Override
			public String getPrincipal(EntityModel<UserDto> entity) {
				return entity.getContent().getUsername();
			}
        	
			@Override
			public void reset() {
				entityUsers.clear();
				entityUsers.addAll(
						roleService.getUsersOfRole(entityRole));
				
				users.setContent(entityUsers);
			}
			
		};
        
        users = new SingleLineDetails<UserDto>("Usuarios", ViewType.VIEW, bindFunctionUser);
        initializeUsers();
        
        IBindeableAndEditableDetails<GroupMinDto> bindFunction = new IBindeableAndEditableDetails<GroupMinDto>() {
			
        	@Override
			public String getPrincipal(EntityModel<GroupMinDto> entity) {
				return entity.getContent().getName();
			}
			
			@Override
			public Collection<EntityModel<GroupMinDto>> addElements(Set<EntityModel<GroupMinDto>> entities) {
				
				var roleId = entityRole.getContent().getId();
				var groupRoleEntities = entities.stream().map(entity -> {
					
					GroupRoleDto groupRoleDto = new GroupRoleDto();
					groupRoleDto.setRoleId(roleId);
					groupRoleDto.setGroupId(entity.getContent().getId());
					
					return groupRoleDto;
				}).collect(Collectors.toList());
				
				roleService.addGroupsToRole(roleId, groupRoleEntities);
				
				if (users.isOpened()) users.reset();
				
				return entities;
			}
			
			@Override
			public Collection<EntityModel<GroupMinDto>> removeElements(Set<EntityModel<GroupMinDto>> entities) {
				
				var roleId = entityRole.getContent().getId();
				var groupRoleEntities = entities.stream().map(entity -> {
					
					GroupRoleDto groupRoleDto = new GroupRoleDto();
					groupRoleDto.setRoleId(roleId);
					groupRoleDto.setGroupId(entity.getContent().getId());
					
					return groupRoleDto;
				}).collect(Collectors.toList());
				
				roleService.removeGroupsToRole(roleId, groupRoleEntities);
				
				if (users.isOpened()) users.reset();
				
				return entities;
				
			}

			@Override
			public void reset() {
				
				entityGroups.clear();
				entityGroups.addAll(
						roleService.getGroupsOfRole(entityRole));
				
				groups.setContent(entityGroups);
				
				var restGroups = new ArrayList<>(groupService.getAllGroupsMinified().getContent());
				restGroups.removeAll(entityGroups);
				
				groups.getComboBox()
					.setItems(restGroups);
				groups.getComboBox().setItemLabelGenerator( ug -> ug.getContent().getName());
				
				
			}
			
		};
        
        groups = new SingleLineDetails<GroupMinDto>("Grupos", ViewType.EDIT, bindFunction);
        initializeGroups();
        
        add(name, code, description, parent, groups, users);
          
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1));
		
        this.binder = setBinder();
	}

	@Override
	public Binder<RoleDto> setBinder() {
		
		Binder<RoleDto> binder = new Binder<>(RoleDto.class);

		binder.forField(name)
			.bind(RoleDto::getName, RoleDto::setName);
		
		binder.forField(code)
			.bind(RoleDto::getCode, RoleDto::setCode);
	
		binder.forField(description)
			.bind(RoleDto::getDescription, RoleDto::setDescription);
	
		
		return binder;
		
	}
	
	private void initializeGroups() {
		
		groups.setSummaryText("Grupos");
		groups.setOpened(false);
		groups.removeAll();
		
	}

	private void initializeUsers() {
		
		users.setSummaryText("Usuarios");
		users.setOpened(false);
		users.removeAll();
		
	}

	public void isEditMode(ViewType formStatus) {
		if (formStatus.equals(ViewType.NEW)) {
			groups.setEnabled(false);
			users.setEnabled(false);
		} else if (formStatus.equals(ViewType.EDIT)) {
			groups.setEnabled(true);
			users.setEnabled(true);
		} else {
			groups.setEnabled(true);
			users.setEnabled(true);
		}
	}

	@Override
	public void loadData(ViewType formStatus, EntityModel<RoleDto> role) {
		
		if (formStatus.equals(ViewType.NEW)) {
			entityRole = null;
			
			binder.setBean(null);
			
		} else {
			entityRole = roleService.getOneRole(role);
			
			binder.setBean(entityRole.getContent());
			
				
			var entityParentRole = roleService.getParentRole(role);
				
			if (entityParentRole != null) {
				parent.setValue(entityParentRole.getContent().getName());
			} else {
				parent.setValue("");

			}
						
		}
		
		initializeUsers();
		initializeGroups();
		
		isEditMode(formStatus);
		
	}

	@Override
	public EntityAction<RoleDto> saveEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityAction<RoleDto> deleteEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
