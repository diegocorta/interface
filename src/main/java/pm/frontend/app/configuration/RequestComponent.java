package pm.frontend.app.configuration;

import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import es.common.util.RestTemplateUtil;
import jakarta.annotation.Nullable;

@Component
public class RequestComponent {
	
	private RestTemplate restTemplate;
	
    public RequestComponent(RestTemplateBuilder restTemplateBuilder,
    		HttpRequestInterceptor httpRequestInterceptor) {
        
    	this.restTemplate = restTemplateBuilder.build();
    	this.restTemplate.setInterceptors(List.of(httpRequestInterceptor));
    }
    
	/////////////////
	// GET METHODS //
	/////////////////
	
	public <T> ResponseEntity<T> get(String url, ParameterizedTypeReference<T> type, HttpHeaders headers) {
				
		ResponseEntity<T> response = RestTemplateUtil.get(restTemplate, url, type, headers);
				
//		if (UI.getCurrent() != null) {
//			AlertBuilder.success(String.valueOf(response.getStatusCode().value()), null, url).open();
//		}		
		return response;
	}
	
	public <T> ResponseEntity<T> get(String url, ParameterizedTypeReference<T> type) {
		
		return get(url, type, new HttpHeaders());
	}
	
	//////////////////
	// POST METHODS //
	//////////////////
	
	public <T> ResponseEntity<T> post(String url, Object body, ParameterizedTypeReference<T> type, HttpHeaders headers) {
				
		ResponseEntity<T> response = RestTemplateUtil.post(restTemplate, url, body, type, headers);
				
//		if (UI.getCurrent() != null) {
//			AlertBuilder.success(String.valueOf(response.getStatusCode().value()), null, url).open();
//		}		
		return response;
	}
	
	public <T> ResponseEntity<T> post(String url, Object body, ParameterizedTypeReference<T> type) {
		
		return post(url, body, type, new HttpHeaders());
	}
	
	
	/////////////////
	// PUT METHODS //
	/////////////////
	
	public <T> ResponseEntity<T> put(String url, Object body, ParameterizedTypeReference<T> type, HttpHeaders headers) {
				
		ResponseEntity<T> response = RestTemplateUtil.put(restTemplate, url, body, type, headers);
				
//		if (UI.getCurrent() != null) {
//			AlertBuilder.success(String.valueOf(response.getStatusCode().value()), null, url).open();
//		}		
		return response;
	}
	
	public <T> ResponseEntity<T> put(String url, Object body, ParameterizedTypeReference<T> type) {
		
		return put(url, body, type, new HttpHeaders());
	}
	
	
	////////////////////
	// DELETE METHODS //
	////////////////////
	
	public <T> ResponseEntity<T> delete(String url, @Nullable Object body, ParameterizedTypeReference<T> type, HttpHeaders headers) {
				
		ResponseEntity<T> response = RestTemplateUtil.delete(restTemplate, url, body, type, headers);
		
//		if (UI.getCurrent() != null) {
//			AlertBuilder.success(String.valueOf(response.getStatusCode().value()), null, url).open();
//		}
		
		return response;
	}
	
	public <T> ResponseEntity<T> delete(String url, ParameterizedTypeReference<T> type) {
		
		return delete(url, null, type, new HttpHeaders());
	}
	
	public <T> ResponseEntity<T> delete(String url, Object body, ParameterizedTypeReference<T> type) {
		
		return delete(url, body, type, new HttpHeaders());
	}
	
}
