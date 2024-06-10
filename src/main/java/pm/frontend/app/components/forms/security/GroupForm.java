package pm.frontend.app.components.forms.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;
import org.springframework.util.SerializationUtils;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.details.IBindeableAndEditableDetails;
import pm.frontend.app.components.standard.details.SingleLineDetails;
import pm.frontend.app.components.standard.forms.EntityAction;
import pm.frontend.app.components.standard.forms.IBindeableForm;
import pm.frontend.app.components.standard.forms.EntityAction.Action;
import pm.frontend.app.logic.security.GroupService;
import pm.frontend.app.logic.security.RoleService;
import pm.frontend.app.logic.security.UserService;
import pm.security.v2.common.dto.GroupDto;
import pm.security.v2.common.dto.GroupRoleDto;
import pm.security.v2.common.dto.UserDto;
import pm.security.v2.common.dto.UserGroupDto;
import pm.security.v2.common.dto.min.RoleMinDto;

public class GroupForm extends FormLayout implements IBindeableForm<GroupDto> {

	private static final long serialVersionUID = 1596400676900430164L;
		
	private TextField name;
    private TextArea description;
	
    private Binder<GroupDto> binder;
    
    private SingleLineDetails<UserDto> users;
    private SingleLineDetails<RoleMinDto> roles;
    
    private EntityModel<GroupDto> entityGroup;
    private EntityModel<GroupDto> initEntityGroup;

    
    private Collection<EntityModel<UserDto>> entityUsers = new ArrayList<>();
    private Collection<EntityModel<RoleMinDto>> entityRoles = new ArrayList<>();
    
    private GroupService groupService;
    
	public GroupForm(GroupService groupService,
			UserService userService, RoleService roleService) {
		
		this.groupService = groupService;
		
        name = new TextField("Nombre");
        description = new TextArea("Descripcion");
        
        IBindeableAndEditableDetails<UserDto> bindFunctionUser = new IBindeableAndEditableDetails<UserDto>() {
			@Override
			public String getPrincipal(EntityModel<UserDto> entity) {
				return entity.getContent().getUsername();
			}

			@Override
			public Collection<EntityModel<UserDto>> addElements(Set<EntityModel<UserDto>> entities) {
				
				var groupId = entityGroup.getContent().getId();
				var userGroupEntities = entities.stream().map(entity -> {
					
					UserGroupDto userGroupDto = new UserGroupDto();
					userGroupDto.setGroupId(groupId);
					userGroupDto.setUserId(entity.getContent().getId());
					
					return userGroupDto;
				}).collect(Collectors.toList());
				
				groupService.addUsersToGroup(groupId, userGroupEntities);
								
				return entities;
			}

			@Override
			public Collection<EntityModel<UserDto>> removeElements(Set<EntityModel<UserDto>> entities) {
				
				var groupId = entityGroup.getContent().getId();
				var userGroupEntities = entities.stream().map(entity -> {
					
					UserGroupDto userGroupDto = new UserGroupDto();
					userGroupDto.setGroupId(groupId);
					userGroupDto.setUserId(entity.getContent().getId());
					
					return userGroupDto;
				}).collect(Collectors.toList());
				
				groupService.removeUsersToGroup(groupId, userGroupEntities);
								
				return entities;
				
			}
			
			@Override
			public void reset() {
				
				entityUsers.clear();
				entityUsers.addAll(
						groupService.getUsersOfGroup(entityGroup).getContent());
				
				users.setContent(entityUsers);
				
				var restUsers = new ArrayList<>(userService.getAllUsers().getContent());
				restUsers.removeAll(entityUsers);
				
				users.getComboBox()
					.setItems(restUsers);
				users.getComboBox().setItemLabelGenerator( ug -> ug.getContent().getUsername());
				
			}
		};
        
        users = new SingleLineDetails<UserDto>("Usuarios", ViewType.EDIT, bindFunctionUser);
        initializeUsers();
        
        IBindeableAndEditableDetails<RoleMinDto> bindFunctionRole = new IBindeableAndEditableDetails<RoleMinDto>() {
			@Override
			public String getPrincipal(EntityModel<RoleMinDto> entity) {
				return entity.getContent().getName();
			}

			@Override
			public Collection<EntityModel<RoleMinDto>> addElements(Set<EntityModel<RoleMinDto>> entities) {
				
				var groupId = entityGroup.getContent().getId();
				var groupRoleEntities = entities.stream().map(entity -> {
					
					GroupRoleDto groupRoleDto = new GroupRoleDto();
					groupRoleDto.setGroupId(groupId);
					groupRoleDto.setRoleId(entity.getContent().getId());
					
					return groupRoleDto;
				}).collect(Collectors.toList());
				
				groupService.addRolesToGroup(groupId, groupRoleEntities);
								
				return entities;
				
			}

			@Override
			public Collection<EntityModel<RoleMinDto>> removeElements(Set<EntityModel<RoleMinDto>> entities) {
				
				var groupId = entityGroup.getContent().getId();
				var groupRoleEntities = entities.stream().map(entity -> {
					
					GroupRoleDto groupRoleDto = new GroupRoleDto();
					groupRoleDto.setGroupId(groupId);
					groupRoleDto.setRoleId(entity.getContent().getId());
					
					return groupRoleDto;
				}).collect(Collectors.toList());
				
				groupService.removeRolesToGroup(groupId, groupRoleEntities);
								
				return entities;
				
			}
			
			@Override
			public void reset() {
				
				entityRoles.clear();
				entityRoles.addAll(
						groupService.getRolesOfGroup(entityGroup));
				
				roles.setContent(entityRoles);
				
				var restRoles = new ArrayList<>(roleService.getAllRolesMinified().getContent());
				restRoles.removeAll(entityRoles);
				
				roles.getComboBox()
					.setItems(restRoles);
				roles.getComboBox().setItemLabelGenerator( ug -> ug.getContent().getName());
				
				
			}

		};
        
        roles = new SingleLineDetails<RoleMinDto>("Roles", ViewType.EDIT, bindFunctionRole);
        initializeRoles();
        
        add(name, description, users, roles);
          
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1));
		
        this.binder = setBinder();
	}

	@Override
	public Binder<GroupDto> setBinder() {
		
		Binder<GroupDto> binder = new Binder<>(GroupDto.class);

		binder.forField(name)
			.asRequired("El grupo debe tener un nombre")
			.withValidator(name -> name.length() >= 3 && name.length() <= 40,
					"El nombre del grupo debe tener entre 3 y 40 caracteres")
			.bind(GroupDto::getName, GroupDto::setName);
		
		binder.forField(description)
			.withValidator(description -> description.length() <= 1000,
				"La descripcion no puede tener mas de 1000 caracteres")
			.bind(GroupDto::getDescription, GroupDto::setDescription);
		
		return binder;
		
	}
	
	@Override
	public void isEditMode(ViewType formStatus) {
		if (formStatus.equals(ViewType.NEW)) {
			roles.setEnabled(false);
			users.setEnabled(false);
		} else if (formStatus.equals(ViewType.EDIT)) {
			roles.setEnabled(true);
			users.setEnabled(true);
		} else {
			roles.setEnabled(true);
			users.setEnabled(true);
		}
	}

	@Override
	public void loadData(ViewType formStatus, EntityModel<GroupDto> group) {
		
		binder.getFields().forEach(f -> f.clear());
		
		if (formStatus.equals(ViewType.NEW)) {
			entityGroup = null;
			initEntityGroup = null;
			binder.setBean(null);
			
		} else {
			entityGroup = groupService.getOneGroup(group);
			
			initEntityGroup = EntityModel.of(SerializationUtils.clone(entityGroup.getContent()));
			initEntityGroup.add(entityGroup.getLinks());
			
			binder.setBean(entityGroup.getContent());
			
		}
		
		initializeUsers();
		initializeRoles();
		
		isEditMode(formStatus);
		
	}

	private void initializeRoles() {
		
		roles.setSummaryText("Roles");
		roles.setOpened(false);
		roles.removeAll();
		
	}

	private void initializeUsers() {
		
		users.setSummaryText("Users");
		users.setOpened(false);
		users.removeAll();
		
	}

	@Override
	public EntityAction<GroupDto> saveEntity() {
		
		EntityAction<GroupDto> entitySaved = new EntityAction<>();
		entitySaved.setPrevious(initEntityGroup);
		entitySaved.setClassDescriptor("Grupo");
		
		boolean isCreate = entityGroup == null;		
		GroupDto userDto = isCreate ? new GroupDto(): entityGroup.getContent();
		
		if (binder.writeBeanIfValid(userDto)) {
			if (modifiedEntitiy(initEntityGroup, userDto)) {
				if (isCreate) {
					entitySaved.setAction(Action.CREATE);
					entitySaved.setEntity(groupService.createGroup(userDto));
				} else {
					entitySaved.setAction(Action.UPDATE);
					entitySaved.setEntity(groupService.updateGroup(userDto));
				}
			}
			return entitySaved;
		}
		
		return null;
	}

	@Override
	public EntityAction<GroupDto> deleteEntity() {
		
		EntityAction<GroupDto> entityDeleted = new EntityAction<>();
		entityDeleted.setAction(Action.DELETE);
		entityDeleted.setEntity(entityGroup);
		entityDeleted.setClassDescriptor("Usuario");
		
		if (entityGroup != null) {
			groupService.deleteGroup(entityGroup.getContent());
			
			return entityDeleted;
		}
		
		return null;
	}
	
}
