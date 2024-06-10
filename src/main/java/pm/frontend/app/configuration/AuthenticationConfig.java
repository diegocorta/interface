package pm.frontend.app.configuration;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.component.UI;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.apachecommons.CommonsLog;
import pm.frontend.app.logic.security.AuthenticationService;
import pm.security.v2.common.dto.JwtResponseDto;

@Configuration
@CommonsLog
public class AuthenticationConfig {
	
	private AuthenticationService authService;
	
	public AuthenticationConfig(AuthenticationService authService) {

		this.authService = authService;

	}
	
	public Claims validateToken(String token) {
		return authService.validateToken(token);
	}
	
	public JwtResponseDto refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String refreshToken) {
					
	    try {
	    	
	    	JwtResponseDto response = authService.refreshToken(refreshToken);
	    	
			Cookie tokenCookie = new Cookie("token", response.getToken());
	        tokenCookie.setHttpOnly(true);
	        tokenCookie.setPath("/");
	        tokenCookie.setSecure(false);
	        tokenCookie.setAttribute("SameSite", "Strict");
			
			Cookie refreshCookie = new Cookie("refresh", response.getRefreshToken());
			refreshCookie.setHttpOnly(true);
			refreshCookie.setPath("/");
			refreshCookie.setSecure(false);
			refreshCookie.setAttribute("SameSite", "Strict");
			
			httpResponse.addCookie(tokenCookie);
			httpResponse.addCookie(refreshCookie);
			log.info("-------------------------");				
			log.info("NUEVAS CREDENCIALES ENVIANDAS AL NAVEGADOR");
			log.info("NUEVO TOKEN: "+response.getToken());
			log.info("NUEVO REFRESH: "+response.getRefreshToken());
			
			return response;
	    	
	    } catch (Exception e) {
	    	
	    	UI.getCurrent().getPage().setLocation("/pm-app/logout");
	    }
	    
	    return null;

	}
	
	/**
	 * Method that 
	 * @param response
	 * @param token
	 * @param refresh
	 * @return
	 */
	public Claims getClaims(HttpServletRequest request, HttpServletResponse response, String token, String refresh) {
		
		Claims claims = null;
		
		// If token is null, try to obtain data from the token
		if (token != null) {
    		
    		try {
    	    	
    			claims = validateToken(token);
    		
    		// If an JwtException arrises, the token may have been expired
    		} catch (JwtException e) {
    			
    			Throwable pileException = e;
    			
    			// Check if the token has expired checking the exceptions searching for an expiredJwtException
    		    while (pileException != null) {
    		    	
    		        if (pileException instanceof ExpiredJwtException) {
    		        	
    		        	// If token has expired, try a refresh, and then obtain the data as normal
    		        	token = refresh(request, response, refresh).getToken();
    	    			claims = validateToken(token);
    		            break;
    		        }
    		        
    		        pileException = pileException.getCause();
    		    }
    			
    		}
    	
    	// If token is null, but there is a refresh token pressent, use the refresh service to obtain new token
    	// and then obtain data as normal
    	} else if (token == null && refresh != null) {
    	
    		token = refresh(request, response, refresh).getToken();
			claims = validateToken(token);
    	}
		
		return claims;
	}
	
	public void setTokenInSpringContext(Claims claims, String token, String refresh) {
		authService.setTokenInSpringContext(claims, token, refresh);
	}
	
	/**
	 * Method that delete the cookies (token and refreshToken) 
	 * and redirects to the login page
	 */
	public static void logout(HttpServletRequest request, HttpServletResponse response) {
		
		Cookie[] cookies = request.getCookies();
    	
    	if (cookies != null && cookies.length > 0) {
    		
	    	Optional<Cookie> refreshCookie = Arrays.stream(cookies)
	    	      .filter(c -> "refresh".equals(c.getName()))
	    	      .findAny();
	    	
	    	if (refreshCookie.isPresent()) {
	    		
	    		Cookie c1 = refreshCookie.get();
	    		c1.setHttpOnly(true);
				c1.setPath("/");
				c1.setSecure(false);
				c1.setAttribute("SameSite", "Strict");
	    		c1.setMaxAge(0);
	    		response.addCookie(c1);
	    	}
	    	
	    	Optional<Cookie> tokenCookie = Arrays.stream(cookies)
		    	      .filter(c -> "token".equals(c.getName()))
		    	      .findAny();
		    	
		    	if (tokenCookie.isPresent()) {
		    		
		    		Cookie c1 = tokenCookie.get();
		    		c1.setHttpOnly(true);
					c1.setPath("/");
					c1.setSecure(false);
					c1.setAttribute("SameSite", "Strict");
		    		c1.setMaxAge(0);
		    		response.addCookie(c1);
		    	}
    	}
		
    	UI.getCurrent().getPage().setLocation("/pm-app/logout");
		
	}
	
}
