package pm.frontend.app.logic.calendar;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import pm.employee.common.dto.calendar.CalendarDto;
import pm.employee.common.dto.calendar.WorkshiftDto;
import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;

@Component
public class CalendarService {

	private RequestComponent requestComponent;
	
	private final String employeeUrl;
	
	private static final String ALL = "/v1/calendars";	
	private static final String EVENTS = "/v1/calendars/%s/effective-workshifts?start-date=%s&end-date=%s";	

	
	public CalendarService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		employeeUrl = urlRequestConfig.getEmployeeUrl();
	}
	
	public CollectionModel<EntityModel<CalendarDto>> getAllCalendars() {
		
		return requestComponent.get(
	    		employeeUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<CalendarDto>>>() {})
			.getBody();
		
	}
	
	public EntityModel<CalendarDto> getOneCalendar(EntityModel<CalendarDto> calendarDto) {
		
		return requestComponent.get(
				calendarDto.getLink(CalendarDto.SELF_REF).get().getHref(),
	    		new ParameterizedTypeReference<EntityModel<CalendarDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<CalendarDto> createCalendar(CalendarDto calendarDto) {
		
		return requestComponent.post(
				employeeUrl.concat(ALL),
				calendarDto,
	    		new ParameterizedTypeReference<EntityModel<CalendarDto>>() {})
			.getBody();
		
	}
	
	public EntityModel<CalendarDto> updateCalendar(CalendarDto calendarDto) {
		
		return requestComponent.put(
				employeeUrl.concat(ALL).concat("/").concat(calendarDto.getId().toString()),
				calendarDto,
	    		new ParameterizedTypeReference<EntityModel<CalendarDto>>() {})
			.getBody();
		
	}
	
	public Void deleteCalendar(CalendarDto calendarDto) {
		
		return requestComponent.delete(
				employeeUrl.concat(ALL).concat("/").concat(calendarDto.getId().toString()),
				calendarDto,
	    		new ParameterizedTypeReference<Void>() {})
			.getBody();
		
	}
	
	public Map<LocalDate, Collection<EntityModel<WorkshiftDto>>> getEventsFromDates(Long calendarId, LocalDate startDate, LocalDate endDate) {
		
		String url = employeeUrl.concat(String.format(EVENTS, String.valueOf(calendarId), startDate, endDate));

		return requestComponent.get(
				url,
	    		new ParameterizedTypeReference<Map<LocalDate, Collection<EntityModel<WorkshiftDto>>> >() {})
			.getBody();
		
	}
	
}
