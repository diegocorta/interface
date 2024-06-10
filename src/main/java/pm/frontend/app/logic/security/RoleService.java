package pm.frontend.app.logic.security;

import java.util.Collection;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;
import pm.security.v2.common.dto.GroupRoleDto;
import pm.security.v2.common.dto.RoleDto;
import pm.security.v2.common.dto.UserDto;
import pm.security.v2.common.dto.min.GroupMinDto;
import pm.security.v2.common.dto.min.RoleMinDto;

@Component
public class RoleService {

	private RequestComponent requestComponent;
	
	private final String securityUrl;
	
	private static final String ALL = "/v1/roles";
	private static final String ALLMIN = "/v1/roles/minified";
	private static final String ROLEGROUPS = "/v1/roles/%s/groups";	

	
	public RoleService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		securityUrl = urlRequestConfig.getSecurityUrl();
	}
	
	public CollectionModel<EntityModel<RoleDto>> getAllRoles() {
		
		return requestComponent.get(
	    		securityUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<RoleDto>>>() {})
			.getBody();
		
	}
	
	public CollectionModel<EntityModel<RoleMinDto>> getAllRolesMinified() {
		
		return requestComponent.get(
	    		securityUrl.concat(ALLMIN),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<RoleMinDto>>>() {})
			.getBody();
		
	}
	
	public EntityModel<RoleDto> getOneRole(EntityModel<RoleDto> roleDto) {
		
		return requestComponent.get(
				roleDto.getLink(RoleDto.SELF_REF).get().getHref(),
	    		new ParameterizedTypeReference<EntityModel<RoleDto>>() {})
			.getBody();
	}
	
	public EntityModel<RoleDto> getParentRole(EntityModel<RoleDto> roleDto) {
		
		if (roleDto.getLink(RoleDto.PARENT_REL).isPresent()) {
			return requestComponent.get(
					roleDto.getLink(RoleDto.PARENT_REL).get().getHref(),
		    		new ParameterizedTypeReference<EntityModel<RoleDto>>() {})
				.getBody();
		} else {
			return null;
		}
	}
	
	public Collection<EntityModel<UserDto>> getUsersOfRole(EntityModel<RoleDto> roleDto) {
		
		return requestComponent.get(
				roleDto.getLink(RoleDto.USERS_REL).get().getHref(),
	    		new ParameterizedTypeReference<Collection<EntityModel<UserDto>>>() {})
			.getBody();
		
	}
	
	public Collection<EntityModel<GroupMinDto>> getGroupsOfRole(EntityModel<RoleDto> roleDto) {
		
		return requestComponent.get(
				roleDto.getLink(RoleDto.GROUPS_REL).get().getHref(),
	    		new ParameterizedTypeReference<Collection<EntityModel<GroupMinDto>>>() {})
			.getBody();
		
	}

	public Collection<EntityModel<GroupRoleDto>> addGroupsToRole(Long roleId, List<GroupRoleDto> groupRoleEntities) {
		
		return requestComponent.post(
				securityUrl.concat(String.format(ROLEGROUPS, roleId)),
				groupRoleEntities,
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<GroupRoleDto>>>() {})
			.getBody().getContent();
		
	}

	public Void removeGroupsToRole(Long roleId, List<GroupRoleDto> groupRoleEntities) {
		
		return requestComponent.delete(
				securityUrl.concat(String.format(ROLEGROUPS, roleId)),
				groupRoleEntities,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
}
