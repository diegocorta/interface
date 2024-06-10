package pm.frontend.app.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.apachecommons.CommonsLog;

@Configuration
@CommonsLog
public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        
    	Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
		
    	String url = request.getURI().toURL().toString();
    	
		log.info("-------------------------");				
    	log.info("URL A ENVIAR AL SERVIDOR: " + url);
    	
		if (auth != null) {
			
			if (auth.getCredentials() instanceof Credentials) {
				String token = ((Credentials)auth.getCredentials()).getToken();
				
				request.getHeaders().setBearerAuth(token);
				
				log.info("TOKEN A ENVIAR: "+ token );
			} else {
				log.info("SIN TOKEN: credenciales no extienden de la clase esperada");

			}
			
		} else {
			log.info("SIN TOKEN: no autenticacion spring");
		}
		
		if (url.contains("/v1/users/refresh")) {
			
	        String bodyy = new String(body, StandardCharsets.UTF_8);
			log.info("REFRES A ENVIAR: "+bodyy);
		}
		
        return execution.execute(request, body);
    }
}