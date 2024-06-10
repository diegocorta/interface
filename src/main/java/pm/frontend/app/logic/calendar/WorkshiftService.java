package pm.frontend.app.logic.calendar;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import pm.employee.common.dto.calendar.WorkshiftDto;
import pm.employee.common.dto.calendar.WorkshiftEventDto;
import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;

@Component
public class WorkshiftService {

	private RequestComponent requestComponent;
	
	private final String employeeUrl;
	
	private static final String ALL = "/v1/workshifts";	
	
	public WorkshiftService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		employeeUrl = urlRequestConfig.getEmployeeUrl();
	}
	
	public CollectionModel<EntityModel<WorkshiftDto>> getAllWorkshift() {
		
		return requestComponent.get(
	    		employeeUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<WorkshiftDto>>>() {})
			.getBody();
		
	}
	
	public EntityModel<WorkshiftDto> getOneWorkshift(EntityModel<WorkshiftDto> workshiftDto) {
		
		return requestComponent.get(
				workshiftDto.getLink(WorkshiftDto.SELF_REF).get().getHref(),
	    		new ParameterizedTypeReference<EntityModel<WorkshiftDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<WorkshiftDto> createWorkshift(WorkshiftDto workshiftDto) {
		
		return requestComponent.post(
				employeeUrl.concat(ALL),
				workshiftDto,
	    		new ParameterizedTypeReference<EntityModel<WorkshiftDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<WorkshiftDto> updateWorkshift(WorkshiftDto workshiftDto) {
		
		return requestComponent.put(
				employeeUrl.concat(ALL).concat("/").concat(workshiftDto.getId().toString()),
				workshiftDto,
	    		new ParameterizedTypeReference<EntityModel<WorkshiftDto>>() {})
			.getBody();
		
	}
	
	public Void deleteWorkshift(WorkshiftDto workshiftDto) {
		
		return requestComponent.delete(
				employeeUrl.concat(ALL).concat("/").concat(workshiftDto.getId().toString()),
				workshiftDto,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
	
	public CollectionModel<EntityModel<WorkshiftEventDto>> getWorkshiftEvents(EntityModel<WorkshiftDto> workshiftDto) {
		
		return requestComponent.get(
				workshiftDto.getLink(WorkshiftDto.EVENTS_REL).get().getHref(),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<WorkshiftEventDto>>>() {})
			.getBody();
		
	}
	
}
