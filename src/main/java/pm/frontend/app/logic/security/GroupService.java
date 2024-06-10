package pm.frontend.app.logic.security;

import java.util.Collection;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;
import pm.security.v2.common.dto.GroupDto;
import pm.security.v2.common.dto.GroupRoleDto;
import pm.security.v2.common.dto.UserDto;
import pm.security.v2.common.dto.UserGroupDto;
import pm.security.v2.common.dto.min.GroupMinDto;
import pm.security.v2.common.dto.min.RoleMinDto;

@Component
public class GroupService {

	private RequestComponent requestComponent;
	
	private final String securityUrl;
	
	private static final String ALL = "/v1/groups";
	private static final String ALLMIN = "/v1/groups/minified";	
	private static final String GROUPUSERS = "/v1/groups/%s/users";	
	private static final String GROUPROLES = "/v1/groups/%s/users";	

	
	public GroupService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		securityUrl = urlRequestConfig.getSecurityUrl();
	}
	
	public CollectionModel<EntityModel<GroupDto>> getAllGroups() {
		
		return requestComponent.get(
	    		securityUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<GroupDto>>>() {})
			.getBody();
		
	}
	
	public CollectionModel<EntityModel<GroupMinDto>> getAllGroupsMinified() {
		
		return requestComponent.get(
	    		securityUrl.concat(ALLMIN),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<GroupMinDto>>>() {})
			.getBody();
		
	}
	
	public EntityModel<GroupDto> getOneGroup(EntityModel<GroupDto> groupDto) {
		
		return requestComponent.get(
				groupDto.getLink(GroupDto.SELF_REF).get().getHref(),
	    		new ParameterizedTypeReference<EntityModel<GroupDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<GroupDto> createGroup(GroupDto groupDto) {
		
		return requestComponent.post(
				securityUrl.concat(ALL),
				groupDto,
	    		new ParameterizedTypeReference<EntityModel<GroupDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<GroupDto> updateGroup(GroupDto groupDto) {
		
		return requestComponent.put(
				securityUrl.concat(ALL).concat("/").concat(groupDto.getId().toString()),
				groupDto,
	    		new ParameterizedTypeReference<EntityModel<GroupDto>>() {})
			.getBody();
		
	}
	
	public Void deleteGroup(GroupDto groupDto) {
		
		return requestComponent.delete(
				securityUrl.concat(ALL).concat("/").concat(groupDto.getId().toString()),
				groupDto,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
	
	public CollectionModel<EntityModel<UserDto>> getUsersOfGroup(EntityModel<GroupDto> groupDto) {
		
		return requestComponent.get(
				groupDto.getLink(GroupDto.USERS_REL).get().getHref(),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<UserDto>>>() {})
			.getBody();
		
	}
	
	public Collection<EntityModel<UserGroupDto>> addUsersToGroup(Long groupId, List<UserGroupDto> userGroupDtos) {
		
		return requestComponent.post(
				securityUrl.concat(String.format(GROUPUSERS, groupId)),
				userGroupDtos,
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<UserGroupDto>>>() {})
			.getBody().getContent();
		
	}

	public Void removeUsersToGroup(Long groupId, List<UserGroupDto> userGroupDtos) {
		
		return requestComponent.delete(
				securityUrl.concat(String.format(GROUPUSERS, groupId)),
				userGroupDtos,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
	
	public Collection<EntityModel<RoleMinDto>> getRolesOfGroup(EntityModel<GroupDto> groupDto) {
		
		return requestComponent.get(
				groupDto.getLink(GroupDto.ROLES_REL).get().getHref(),
	    		new ParameterizedTypeReference<Collection<EntityModel<RoleMinDto>>>() {})
			.getBody();
		
	}
	
	public Collection<EntityModel<GroupRoleDto>> addRolesToGroup(Long groupId, List<GroupRoleDto> groupRoleDtos) {
		
		return requestComponent.post(
				securityUrl.concat(String.format(GROUPROLES, groupId)),
				groupRoleDtos,
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<GroupRoleDto>>>() {})
			.getBody().getContent();
		
	}

	public Void removeRolesToGroup(Long groupId, List<GroupRoleDto> groupRoleDtos) {
		
		return requestComponent.delete(
				securityUrl.concat(String.format(GROUPUSERS, groupId)),
				groupRoleDtos,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}

	
}
