package pm.frontend.app.logic.security;

import java.security.PublicKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.vaadin.flow.server.VaadinSession;

import es.common.util.TokenUtils;
import io.jsonwebtoken.Claims;
import pm.employee.common.dto.employee.EmployeeDto;
import pm.frontend.app.Application;
import pm.frontend.app.configuration.Credentials;
import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;
import pm.frontend.app.logic.employee.EmployeeService;
import pm.security.v2.common.dto.JwtResponseDto;
import pm.security.v2.common.dto.UserLoginDto;

@Component
public class AuthenticationService {

	private EmployeeService employeeService;
	private RequestComponent requestComponent;
	private PublicKey publicKey;
	
	private final String securityUrl;
	
	private static final String LOGIN = "/v1/users/login";
	private static final String REFRESH = "/v1/users/refresh";
	
	
	public AuthenticationService(
			PublicKey publicKey,
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig,
			EmployeeService employeeService) {
		
		this.employeeService = employeeService;
		this.requestComponent = requestComponent;
		this.publicKey = publicKey;
		
		securityUrl = urlRequestConfig.getSecurityUrl();
	}
	
	public JwtResponseDto login(UserLoginDto userLoginDto) {
		
		return requestComponent.post(
	    		securityUrl.concat(LOGIN),
	    		userLoginDto,
	    		new ParameterizedTypeReference<JwtResponseDto>() {})
			.getBody();
		
	}
	
	public JwtResponseDto tryAutoLogin() {
			
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
    	
		if (auth != null) {
			
			if (auth.getCredentials() instanceof Credentials) {
				String refreshToken = ((Credentials)auth.getCredentials()).getRefreshToken();
				
				if (refreshToken != null) {
		            return refreshToken(refreshToken);
		        }
			}
		}
        
        return null;
		
	}
	
	public JwtResponseDto refreshToken(String refreshToken) {
			
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	    headers.setContentType(MediaType.APPLICATION_JSON);
		
		return requestComponent.post(
				securityUrl.concat(REFRESH), 
				"\""+refreshToken+"\"", 
				new ParameterizedTypeReference<JwtResponseDto>() {},
				headers)
			.getBody();
			
	}
	
	public Claims validateToken(String token) {
		return TokenUtils.validateToken(token, publicKey);
	}
	
	public void setTokenInSpringContext(Claims claims, String token, String refresh) {
		
		Long userId =  Long.valueOf(claims.get(TokenUtils.SECURITY_USER).toString());
		@SuppressWarnings("unchecked")
		List<String> roles =  (List<String>) claims.get(TokenUtils.ROLES);
		@SuppressWarnings("unchecked")
		List<String> grantedPermissions = (List<String>) claims.get(TokenUtils.PERMISSIONS_GRANTED);
		@SuppressWarnings("unchecked")
		List<String> revokedPermissions = (List<String>) claims.get(TokenUtils.PERMISSIONS_REVOKED);
		
		Set<String> permissions = new HashSet<>();
				permissions.addAll(roles);
				permissions.addAll(grantedPermissions);
				permissions.removeAll(revokedPermissions);
		
		Set<GrantedAuthority> auths = permissions
				.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
		
		UserDetails userDetail = new User(claims.getSubject(), "", auths);
		
		Credentials cred = new Credentials();
		cred.setUserId(userId);
		cred.setToken(token);
		cred.setRefreshToken(refresh);
		cred.setExpDate(
				ZonedDateTime.ofInstant(claims.getExpiration()
						.toInstant(),
                ZoneId.systemDefault()));
		
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetail, cred, auths);
		
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public void setVaadinSessionUser() {
		
		Long userId = ((Credentials)SecurityContextHolder.getContext().getAuthentication().getCredentials()).getUserId();
		
		setVaadinSessionUser(userId);
		
	}
	
	public void setVaadinSessionUser(Long userId) {
		
		EntityModel<EmployeeDto> employee = employeeService.getOneEmployeeOfUser(userId);
		VaadinSession.getCurrent().setAttribute(Application.USER, employee);
		
	}
	
	@SuppressWarnings("unchecked")
	public EntityModel<EmployeeDto> getVaadinSessionUser() {
		
		EntityModel<EmployeeDto> employeeDto = (EntityModel<EmployeeDto>) VaadinSession.getCurrent().getAttribute(Application.USER);
		
		if (employeeDto == null) {
			setVaadinSessionUser();
			
			employeeDto = (EntityModel<EmployeeDto>) VaadinSession.getCurrent().getAttribute(Application.USER);
		}
		
		return employeeDto;
		
	}
}
