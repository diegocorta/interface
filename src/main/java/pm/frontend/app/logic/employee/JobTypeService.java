package pm.frontend.app.logic.employee;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import pm.employee.common.dto.employee.JobTypeDto;
import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;

@Component
public class JobTypeService {

	private RequestComponent requestComponent;
	
	private final String employeeUrl;
	
	
	private static final String ALL = "/v1/jobs";

	public JobTypeService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		employeeUrl = urlRequestConfig.getEmployeeUrl();
	}
	
	
	public CollectionModel<EntityModel<JobTypeDto>> getAllJobs() {
		
		return requestComponent.get(
				employeeUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<JobTypeDto>>>() {})
			.getBody();
		
	}
}
