package pm.frontend.app.logic.security;

import java.util.Collection;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;
import pm.security.v2.common.dto.GroupDto;
import pm.security.v2.common.dto.UserDto;
import pm.security.v2.common.dto.UserGroupDto;
import pm.security.v2.common.dto.min.RoleMinDto;
import pm.security.v2.common.dto.min.UserMinDto;

@Component
public class UserService {

	private RequestComponent requestComponent;
	
	private final String securityUrl;
	
	private static final String ALL = "/v1/users";	
	private static final String ALLMIN = "/v1/users/minified";	
	private static final String USERGROUPS = "/v1/users/%s/groups";	

	
	public UserService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		securityUrl = urlRequestConfig.getSecurityUrl();
	}
	
	public CollectionModel<EntityModel<UserDto>> getAllUsers() {
		
		return requestComponent.get(
	    		securityUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<UserDto>>>() {})
			.getBody();
		
	}
	
	public CollectionModel<EntityModel<UserMinDto>> getAllUsersMinified() {
		
		return requestComponent.get(
	    		securityUrl.concat(ALLMIN),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<UserMinDto>>>() {})
			.getBody();
		
	}
	
	public EntityModel<UserDto> getOneUser(EntityModel<UserDto> userDto) {
			
			return requestComponent.get(
					userDto.getLink(UserDto.SELF_REF).get().getHref(),
		    		new ParameterizedTypeReference<EntityModel<UserDto>>() {})
				.getBody();
			
	}
	
	public EntityModel<UserDto> createUser(UserDto userDto) {
		
		return requestComponent.post(
				securityUrl.concat(ALL),
				userDto,
	    		new ParameterizedTypeReference<EntityModel<UserDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<UserDto> updateUser(UserDto userDto) {
		
		return requestComponent.put(
				securityUrl.concat(ALL).concat("/").concat(userDto.getId().toString()),
				userDto,
	    		new ParameterizedTypeReference<EntityModel<UserDto>>() {})
			.getBody();
		
	}
	
	public Void deleteUser(UserDto userDto) {
		
		return requestComponent.delete(
				securityUrl.concat(ALL).concat("/").concat(userDto.getId().toString()),
				userDto,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
	
	public CollectionModel<EntityModel<GroupDto>> getGroupsOfUser(EntityModel<UserDto> userDto) {
		
		return requestComponent.get(
				userDto.getLink(UserDto.GROUP_REL).get().getHref(),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<GroupDto>>>() {})
			.getBody();
		
	}
	
	public Collection<EntityModel<UserGroupDto>> addGroupsToUser(Long userId, Collection<UserGroupDto> userGroupDtos) {
		
		return requestComponent.post(
				securityUrl.concat(String.format(USERGROUPS, userId)),
				userGroupDtos,
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<UserGroupDto>>>() {})
			.getBody().getContent();
		
	}
	
	public Void removeGroupsToUser(Long userId, Collection<UserGroupDto> userGroupDtos) {
		
		return requestComponent.delete(
				securityUrl.concat(String.format(USERGROUPS, userId)),
				userGroupDtos,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
	
	
	
	public Collection<EntityModel<RoleMinDto>> getRolesOfUser(EntityModel<UserDto> userDto) {
		
		return requestComponent.get(
				userDto.getLink(UserDto.ROLES_REL).get().getHref(),
	    		new ParameterizedTypeReference<Collection<EntityModel<RoleMinDto>>>() {})
			.getBody();
		
	}
}
