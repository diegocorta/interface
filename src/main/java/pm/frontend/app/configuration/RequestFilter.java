//package com.example.application.configuration;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Optional;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.vaadin.flow.server.VaadinSession;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.apachecommons.CommonsLog;
//
//@Component
//@CommonsLog
//public class RequestFilter extends OncePerRequestFilter {
//	
//	private AuthenticationConfig authenticationConfig;
//	
//	public RequestFilter(AuthenticationConfig tokenConfig) {
//		
//		this.authenticationConfig = tokenConfig;
//	}
//	
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//			throws ServletException, IOException {
//		
//		log.info("-------------------------");				
//		
//		if (VaadinSession.getCurrent() != null) {
//			
//			log.info("LOCKED BY SESSION: " + VaadinSession.getCurrent());
//			
//			synchronized (VaadinSession.getCurrent()) {
//				getCredentials(request, response);
//			}
//			
//		} else {
//			
//			log.info("LOCKED BY IP: " + request.getRemoteAddr());
//			
//			synchronized (request.getRemoteAddr()) {
//				getCredentials(request, response);
//			}
//		}
//		
//        chain.doFilter(request, response);
//               
//    }
//
//	private void getCredentials(HttpServletRequest request, HttpServletResponse response) {
//
//		// Search for a token and a refresh token
//		String token = getTokenIfExists(request);
//		String refresh = getRefreshTokenIfExists(request);
//
//		log.info("URL DE CLIENTE A VAADIN: " + request.getRequestURL().toString());
//		log.info("TOKEN DE COOKIE: "+token);
//		log.info("REFRESH TOKEN DE COOKIE: "+refresh);
//		
//		SecurityContextHolder.clearContext();
//		
//		// Gets the claims of the token
//		Claims claims = authenticationConfig.getClaims(request, response, token, refresh);
//		
//		// If claims are valid, add values to spring context
//		if (claims != null) {
//			
//			authenticationConfig.setTokenInSpringContext(claims, token, refresh);
//			
//		}
//	}
//    
//    private String getTokenIfExists(HttpServletRequest request) {
//    	   
//    	String token = null;
//    	
//    	Cookie[] cookies = request.getCookies();
//    	
//    	Cookie cookie = getCookie(cookies, "token");
//    	
//    	if (cookie != null) {
//    		token = cookie.getValue();
//    	}
//    	
//    	return token;
//
//    }
//    
//    private String getRefreshTokenIfExists(HttpServletRequest request) {
//    	
//    	String refresh = null;
//    	
//    	Cookie[] cookies = request.getCookies();
//    	
//    	Cookie cookie = getCookie(cookies, "refresh");
//    	
//    	if (cookie != null) {
//    		refresh = cookie.getValue();
//    	}
//    	
//    	return refresh;
//    	
//    }
//    
//    private Cookie getCookie(Cookie[] cookies, String cookieName) {
//    	
//    	Cookie cookie = null;
//    	
//    	if (cookies != null && cookies.length > 0) {
//	    	Optional<Cookie> optCookie = Arrays.stream(cookies)
//	    	      .filter(c -> cookieName.equals(c.getName()))
//	    	      .findAny();
//	    	
//	    	if (optCookie.isPresent()) {
//	    		cookie = optCookie.get();
//	    	}
//    	}
//    	
//    	return cookie;
//    	
//    }
//        
//}