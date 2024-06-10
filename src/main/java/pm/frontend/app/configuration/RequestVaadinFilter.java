package pm.frontend.app.configuration;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.apachecommons.CommonsLog;

@Configuration
@CommonsLog
public class RequestVaadinFilter implements VaadinServiceInitListener {

	private static final long serialVersionUID = 1L;

	private AuthenticationConfig authenticationConfig;
	
	public RequestVaadinFilter(AuthenticationConfig authenticationConfig) {
		this.authenticationConfig = authenticationConfig;
	}
	
	@Override
	public void serviceInit(ServiceInitEvent event) {
		
		event.addRequestHandler((session, request, response) -> {
			
			if (VaadinSession.getCurrent() != null) {
								
				synchronized (VaadinSession.getCurrent()) {
					
					log.info("-------------------------");				
					log.info("LOCKED BY SESSION");

					getCredentials((HttpServletRequest)request, (HttpServletResponse)response);
				}
				
			} else {
								
				synchronized (request.getRemoteAddr()) {
					
					log.info("-------------------------");				
					log.info("LOCKED BY IP: " + request.getRemoteAddr());
					
					getCredentials((HttpServletRequest)request, (HttpServletResponse)response);
				}
			}
			
			return false;
        });
		
	}
	
	private boolean getCredentials(HttpServletRequest request, HttpServletResponse response) {

		try {
			
			// Search for a token and a refresh token
			String token = getTokenIfExists(request);
			String refresh = getRefreshTokenIfExists(request);
	
			log.info("URL DE CLIENTE A VAADIN: " + request.getRequestURL().toString());
			log.info("TOKEN DE COOKIE: "+token);
			log.info("REFRESH TOKEN DE COOKIE: "+refresh);
			
			SecurityContextHolder.clearContext();
			
			// Gets the claims of the token
			Claims claims = authenticationConfig.getClaims(request, response, token, refresh);
			
			// If claims are valid, add values to spring context
			if (claims != null) {
				
				authenticationConfig.setTokenInSpringContext(claims, token, refresh);
				
				return true;
			}
		
			return false; 
			
		} catch (Exception e) {
			
			log.warn("PROBLEM GETTING CREDENTIALS: " + e);
			
			return false;
		}

	}
    
    private String getTokenIfExists(HttpServletRequest request) {
    	   
    	String token = null;
    	
    	Cookie[] cookies = request.getCookies();
    	
    	Cookie cookie = getCookie(cookies, "token");
    	
    	if (cookie != null) {
    		token = cookie.getValue();
    	}
    	
    	return token;

    }
    
    private String getRefreshTokenIfExists(HttpServletRequest request) {
    	
    	String refresh = null;
    	
    	Cookie[] cookies = request.getCookies();
    	
    	Cookie cookie = getCookie(cookies, "refresh");
    	
    	if (cookie != null) {
    		refresh = cookie.getValue();
    	}
    	
    	return refresh;
    	
    }
    
    private Cookie getCookie(Cookie[] cookies, String cookieName) {
    	
    	Cookie cookie = null;
    	
    	if (cookies != null && cookies.length > 0) {
	    	Optional<Cookie> optCookie = Arrays.stream(cookies)
	    	      .filter(c -> cookieName.equals(c.getName()))
	    	      .findAny();
	    	
	    	if (optCookie.isPresent()) {
	    		cookie = optCookie.get();
	    	}
    	}
    	
    	return cookie;
    	
    }

}
