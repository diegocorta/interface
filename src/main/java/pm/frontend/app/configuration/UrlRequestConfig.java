package pm.frontend.app.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Getter
@Configuration
@PropertySource({ "classpath:request.properties" })
public class UrlRequestConfig {

	//////////////////////////
	// URLS FROM PROPERTIES //
	//////////////////////////
	
	private String securityUrl;
	private String employeeUrl;
	
	public UrlRequestConfig(
			@Value("${base}") String base,
			@Value("${security.api}") String securityApi,
			@Value("${base.employee}") String baseEmployee,
			@Value("${employee.api}") String employeeApi
			) {

		this.securityUrl = base.concat(securityApi);
		this.employeeUrl = baseEmployee.concat(employeeApi);
	}
	
}
