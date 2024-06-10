package pm.frontend.app.components.forms.security;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;

import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.details.IBindeableAndEditableDetails;
import pm.frontend.app.components.standard.details.IBindeableDetails;
import pm.frontend.app.components.standard.details.SingleLineDetails;
import pm.frontend.app.components.standard.forms.EntityAction;
import pm.frontend.app.components.standard.forms.IBindeableForm;
import pm.frontend.app.components.standard.forms.EntityAction.Action;
import pm.frontend.app.logic.security.GroupService;
import pm.frontend.app.logic.security.UserService;
import pm.security.v2.common.dto.GroupDto;
import pm.security.v2.common.dto.UserDto;
import pm.security.v2.common.dto.UserGroupDto;
import pm.security.v2.common.dto.min.RoleMinDto;

@Component
public class UserForm extends FormLayout implements IBindeableForm<UserDto> {

	private static final long serialVersionUID = 1596400676900430164L;
	
	private TextField username;
	private EmailField email;
	private TextField createDate;
	private PasswordField password;
    private PasswordField newPassword;
    private PasswordField confirmPassword;
    private Checkbox loginAllowed;
	
    private Binder<UserDto> binder;
    
    private SingleLineDetails<GroupDto> groups;
    private SingleLineDetails<RoleMinDto> roles;
    
    private EntityModel<UserDto> entityUser;
    private EntityModel<UserDto> initEntityUser;

    private Collection<EntityModel<GroupDto>> entityGroups = new ArrayList<>();
    private Collection<EntityModel<RoleMinDto>> entityRoles = new ArrayList<>();
    
    private UserService userService;
    
	public UserForm(UserService userService,
			GroupService groupService) {
		
		this.userService = userService;
		
        username = new TextField("Nombre de usuario");
        email = new EmailField("Correo electrónico");
        createDate = new TextField("Fecha de alta");
        createDate.setReadOnly(true);
        
        password = new PasswordField("Contraseña");
        password.setReadOnly(true);
        password.setRevealButtonVisible(false);
        
        newPassword = new PasswordField("Nueva contraseña");
        confirmPassword = new PasswordField("Repetir contraseña");
        loginAllowed = new Checkbox("¿Puede iniciar sesion?", true);
        
        loginAllowed.getStyle().setMargin("14px 0 7px 0");
        
        IBindeableAndEditableDetails<GroupDto> bindFunction = new IBindeableAndEditableDetails<GroupDto>() {
			
        	@Override
			public String getPrincipal(EntityModel<GroupDto> entity) {
				return entity.getContent().getName();
			}

			@Override
			public Collection<EntityModel<GroupDto>> addElements(Set<EntityModel<GroupDto>> entities) {
					
				var userId = entityUser.getContent().getId();
				var userGroupEntities = entities.stream().map(entity -> {
					
					UserGroupDto userGroupDto = new UserGroupDto();
					userGroupDto.setUserId(userId);
					userGroupDto.setGroupId(entity.getContent().getId());
					
					return userGroupDto;
				}).collect(Collectors.toList());
				
				userService.addGroupsToUser(userId, userGroupEntities);
				
				if (roles.isOpened()) roles.reset();
				
				return entities;
			}

			@Override
			public Collection<EntityModel<GroupDto>> removeElements(Set<EntityModel<GroupDto>> entities) {
				
				var userId = entityUser.getContent().getId();
				var userGroupEntities = entities.stream().map(entity -> {
					
					UserGroupDto userGroupDto = new UserGroupDto();
					userGroupDto.setUserId(userId);
					userGroupDto.setGroupId(entity.getContent().getId());
					
					return userGroupDto;
				}).collect(Collectors.toList());
				
				userService.removeGroupsToUser(userId, userGroupEntities);
				
				if (roles.isOpened()) roles.reset();
				
				return entities;
				
			}

			@Override
			public void reset() {
				
				entityGroups.clear();
				entityGroups.addAll(
						userService.getGroupsOfUser(entityUser).getContent());
				
				groups.setContent(entityGroups);
				
				var restGroups = new ArrayList<>(groupService.getAllGroups().getContent());
				restGroups.removeAll(entityGroups);
				
				groups.getComboBox()
					.setItems(restGroups);
				groups.getComboBox().setItemLabelGenerator( ug -> ug.getContent().getName());
				
			}
			
		};
        
        groups = new SingleLineDetails<GroupDto>("Grupos", ViewType.EDIT, bindFunction);
        initializeGroups();
        addGroupsOpenListener();
        
        IBindeableDetails<RoleMinDto> bindFunctionRole = new IBindeableDetails<RoleMinDto>() {
			@Override
			public String getPrincipal(EntityModel<RoleMinDto> entity) {
				return entity.getContent().getName();
			}

			@Override
			public void reset() {
				
				entityRoles.clear();
				entityRoles.addAll(
						userService.getRolesOfUser(entityUser));
				
				roles.setContent(entityRoles);
				
			}

		};
        
        roles = new SingleLineDetails<RoleMinDto>("Roles", ViewType.VIEW, bindFunctionRole);
        initializeRoles();
        addRolesOpenListener();
        
        add(username, email, createDate, password, newPassword, confirmPassword, loginAllowed, groups, roles);
        
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("380px", 2));
        
        // Stretch the username field over 2 columns
        setColspan(username, 2);
        setColspan(email, 2);
        setColspan(createDate, 2);
        setColspan(password,2);
        setColspan(roles, 2);
        setColspan(groups, 2);
        setColspan(loginAllowed, 2);
		
		 this.binder = setBinder();
        
	}
	
	@Override
	public Binder<UserDto> setBinder() {
		
		Binder<UserDto> binder = new Binder<>(UserDto.class);

		binder.forField(username)
			.asRequired("El usuario debe tener un nombre")
			.withValidator(username -> username.length() >= 3 && username.length() <= 20,
					"El nombre de usuario debe tener entre 3 y 20 caracteres")
			.bind(user -> user.getUsername(), UserDto::setUsername);
		
		binder.forField(email)
			.asRequired("El email no puede ser vacio")
			.withValidator(new EmailValidator("Formato de email incorrecto"))
			.bind(UserDto::getEmail, UserDto::setEmail);
		
		binder.forField(createDate)
			.bind(user -> ZonedDateTime.parse(user.getCreatedAt()).format(DateTimeFormatter.ISO_LOCAL_DATE), null);

		binder.forField(loginAllowed)
			.bind(UserDto::getAllowLogin, UserDto::setAllowLogin);
		
		binder.forField(password)
			.bind(user -> user.isHasPassword() ? "1111111111": "", null);
		
		return binder;
	}



	private void initializeGroups() {
		
		groups.setSummaryText("Grupos");
		groups.setOpened(false);
		groups.removeAll();
		
	}
	
	private void initializeRoles() {
		
		roles.setSummaryText("Roles");
		roles.setOpened(false);
		roles.removeAll();
		
	}

	public void isEditMode(ViewType formStatus) {
		
		if (formStatus.equals(ViewType.NEW)) {
			this.username.setReadOnly(false);
			roles.setEnabled(false);
			groups.setEnabled(false);
			password.setVisible(false);
			createDate.setVisible(false);
		} else if (formStatus.equals(ViewType.EDIT)) {
			this.username.setReadOnly(true);
			roles.setEnabled(true);
			groups.setEnabled(true);
			password.setVisible(true);
			createDate.setVisible(true);

		} else {
			this.username.setReadOnly(true);
			roles.setEnabled(true);
			groups.setEnabled(true);
			password.setVisible(true);
			createDate.setVisible(true);


		}
	}

	@Override
	public void loadData(ViewType formStatus, EntityModel<UserDto> user) {
		
		binder.getFields().forEach(f -> f.clear());
		 
		if (formStatus.equals(ViewType.NEW)) {
			entityUser = null;
			initEntityUser = null;
			binder.setBean(null);
			
		} else {
			entityUser = userService.getOneUser(user);
			
			initEntityUser = EntityModel.of(SerializationUtils.clone(entityUser.getContent()));
			initEntityUser.add(entityUser.getLinks());
			
			binder.setBean(entityUser.getContent());
			
		}
		
		initializeGroups();
		initializeRoles();
		isEditMode(formStatus);
		
	}
	
	private void addGroupsOpenListener() {
		
		groups.addOpenedChangeListener(open -> {
			if (open.isOpened()) {
				groups.reset();
			}
		});
	}
	
	private void addRolesOpenListener() {
		
		roles.addOpenedChangeListener(open -> {
			if (open.isOpened()) {
				roles.reset();
			}
		});
	}

	@Override
	public EntityAction<UserDto> saveEntity() {
		
		EntityAction<UserDto> entitySaved = new EntityAction<>();
		entitySaved.setPrevious(initEntityUser);
		entitySaved.setClassDescriptor("Usuario");
		
		boolean isCreate = entityUser == null;		
		UserDto userDto = isCreate ? new UserDto(): entityUser.getContent();
		
		if (binder.writeBeanIfValid(userDto)) {
			if (modifiedEntitiy(initEntityUser, userDto)) {
				if (isCreate) {
					entitySaved.setAction(Action.CREATE);
					entitySaved.setEntity(userService.createUser(userDto));
				} else {
					entitySaved.setAction(Action.UPDATE);
					entitySaved.setEntity(userService.updateUser(userDto));
				}
			}
			
			return entitySaved;
		}
		
		return null;
	}

	@Override
	public EntityAction<UserDto> deleteEntity() {
		
		EntityAction<UserDto> entityDeleted = new EntityAction<>();
		entityDeleted.setAction(Action.DELETE);
		entityDeleted.setEntity(entityUser);
		entityDeleted.setClassDescriptor("Usuario");
		
		if (entityUser != null) {
			userService.deleteUser(entityUser.getContent());
			
			return entityDeleted;
		}
		
		return null;
	}
	
}
