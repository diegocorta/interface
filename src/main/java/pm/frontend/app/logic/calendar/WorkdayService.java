package pm.frontend.app.logic.calendar;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import pm.employee.common.dto.calendar.WorkdayDto;
import pm.employee.common.dto.calendar.WorkshiftDto;
import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;

@Component
public class WorkdayService {

	private RequestComponent requestComponent;
	
	private final String employeeUrl;
	
	private static final String ALL = "/v1/workdays";	
	
	public WorkdayService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		employeeUrl = urlRequestConfig.getEmployeeUrl();
	}
	
	public CollectionModel<EntityModel<WorkdayDto>> getAllWorkdays() {
		
		return requestComponent.get(
	    		employeeUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<WorkdayDto>>>() {})
			.getBody();
		
	}
	
	public EntityModel<WorkdayDto> getOneWorkday(EntityModel<WorkdayDto> workdayDto) {
		
		return requestComponent.get(
				workdayDto.getLink(WorkdayDto.SELF_REF).get().getHref(),
	    		new ParameterizedTypeReference<EntityModel<WorkdayDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<WorkdayDto> createWorkday(WorkdayDto workdayDto) {
		
		return requestComponent.post(
				employeeUrl.concat(ALL),
				workdayDto,
	    		new ParameterizedTypeReference<EntityModel<WorkdayDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<WorkdayDto> updateWorkday(WorkdayDto workdayDto) {
		
		return requestComponent.put(
				employeeUrl.concat(ALL).concat("/").concat(workdayDto.getId().toString()),
				workdayDto,
	    		new ParameterizedTypeReference<EntityModel<WorkdayDto>>() {})
			.getBody();
		
	}
	
	public Void deleteWorkday(WorkdayDto workdayDto) {
		
		return requestComponent.delete(
				employeeUrl.concat(ALL).concat("/").concat(workdayDto.getId().toString()),
				workdayDto,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
	
	public CollectionModel<EntityModel<WorkshiftDto>> getWorkshifts(EntityModel<WorkdayDto> workdayDto) {
		
		return requestComponent.get(
				workdayDto.getLink(WorkdayDto.WORKSHIFT_REL).get().getHref(),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<WorkshiftDto>>>() {})
			.getBody();
		
	}
	
}
