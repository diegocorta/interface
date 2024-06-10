package pm.frontend.app.logic.employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import pm.employee.common.dto.employee.EmployeeDto;
import pm.employee.common.dto.recording.EmployeeJournalDto;
import pm.employee.common.dto.recording.EmployeeJournalRecordDto;
import pm.employee.common.dto.recording.EmployeeJournalRecordToRegisterDto;
import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;

@Component
public class EmployeeJournalService {

	private RequestComponent requestComponent;
	
	private final String employeeUrl;
	
	private static final String JOURNALOFDATE = "/v1/employees/%s/journals/of-date/%s";
	private static final String JOURNALRECORD = "/v1/employees/%s/journal/records/register";
	
	public EmployeeJournalService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		employeeUrl = urlRequestConfig.getEmployeeUrl();
	}
	
	public EntityModel<EmployeeJournalDto> getJournalOfDate(EntityModel<EmployeeDto> employee, LocalDate date) {
		
		try {
			return requestComponent.get(
					employeeUrl.concat(String.format(JOURNALOFDATE, employee.getContent().getId(), date.format(DateTimeFormatter.ISO_DATE))),
					new ParameterizedTypeReference<EntityModel<EmployeeJournalDto>>() {})
				.getBody();
		} catch (HttpClientErrorException.NotFound e) {
			return null;
		}
	}
	
	public Collection<EntityModel<EmployeeJournalRecordDto>> getRecordsOfJournal(EntityModel<EmployeeJournalDto> employeeJournal) {
		
		try {
			return requestComponent.get(
					employeeJournal.getLink(EmployeeJournalDto.RECORDS_REL).get().getHref(),
					new ParameterizedTypeReference<Collection<EntityModel<EmployeeJournalRecordDto>>>() {})
				.getBody();
		} catch (HttpClientErrorException.NotFound e) {
			return null;
		}
	}
	
	public EntityModel<EmployeeJournalRecordDto> createRecordsOfJournal(EmployeeJournalRecordToRegisterDto employeeJournal) {
		
		return requestComponent.post(
				employeeUrl.concat(String.format(JOURNALRECORD, employeeJournal.getEmployeeId())),
				employeeJournal,
				new ParameterizedTypeReference<EntityModel<EmployeeJournalRecordDto>>() {})
			.getBody();
	}
}
