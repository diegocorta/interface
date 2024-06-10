package pm.frontend.app.logic.employee;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import pm.employee.common.dto.employee.EmployeeContractDto;
import pm.employee.common.dto.employee.EmployeeDto;
import pm.employee.common.dto.employee.GenderTypeDto;
import pm.employee.common.dto.employee.JobTypeDto;
import pm.frontend.app.configuration.RequestComponent;
import pm.frontend.app.configuration.UrlRequestConfig;

@Component
public class EmployeeService {

	private RequestComponent requestComponent;
	
	private final String employeeUrl;
	
	private static final String ALL = "/v1/employees";
	private static final String ONEBYSECURITY = "/v1/employees/by-security-user/%s";
	private static final String EMPLOYEECONTRACT = "/v1/employees/%s/contracts";
	
	public EmployeeService(
			RequestComponent requestComponent,
			UrlRequestConfig urlRequestConfig) {
		
		this.requestComponent = requestComponent;

		employeeUrl = urlRequestConfig.getEmployeeUrl();
	}
	
	public CollectionModel<EntityModel<EmployeeDto>> getAllEmployees() {
		
		return requestComponent.get(
				employeeUrl.concat(ALL),
	    		new ParameterizedTypeReference<CollectionModel<EntityModel<EmployeeDto>>>() {})
			.getBody();
	}
	
	public EntityModel<EmployeeDto> getEmployeeUser(EntityModel<EmployeeDto> userDto) {
			
			return requestComponent.get(
					userDto.getLink(EmployeeDto.SELF_REF).get().getHref(),
		    		new ParameterizedTypeReference<EntityModel<EmployeeDto>>() {})
				.getBody();
	}
	
	public EntityModel<EmployeeDto> updateEmployee(EntityModel<EmployeeDto> employeeDto) {
		
		return requestComponent.put(
				employeeDto.getLink(EmployeeDto.SELF_REF).get().getHref(),
				employeeDto.getContent(),
	    		new ParameterizedTypeReference<EntityModel<EmployeeDto>>() {})
			.getBody();	
	}
	
	public EntityModel<JobTypeDto> getJobOfEmployee(EntityModel<EmployeeDto> employeeDto) {
		
		return requestComponent.get(
				employeeDto.getLink(EmployeeDto.JOB_REL).get().getHref(),
	    		new ParameterizedTypeReference<EntityModel<JobTypeDto>>() {})
			.getBody();
	}
	
	public EntityModel<GenderTypeDto> getGenderOfEmployee(EntityModel<EmployeeDto> employeeDto) {
		
		return requestComponent.get(
				employeeDto.getLink(EmployeeDto.GENDER_REL).get().getHref(),
	    		new ParameterizedTypeReference<EntityModel<GenderTypeDto>>() {})
			.getBody();	
	}
	
	public EntityModel<EmployeeContractDto> getLastContractOfEmployee(EntityModel<EmployeeDto> employeeDto) {
		
		try {
			return requestComponent.get(
					employeeDto.getLink(EmployeeDto.LAST_CONTRACT_REL).get().getHref(),
		    		new ParameterizedTypeReference<EntityModel<EmployeeContractDto>>() {})
				.getBody();
		} catch (HttpClientErrorException.NotFound e) {
			return null;
		}
	}
	
	public EntityModel<EmployeeDto> getOneEmployeeOfUser(Long userId) {
		
		return requestComponent.get(
				employeeUrl.concat(String.format(ONEBYSECURITY, userId.toString())),
	    		new ParameterizedTypeReference<EntityModel<EmployeeDto>>() {})
			.getBody();
	}
	
	public EntityModel<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
		
		return requestComponent.post(
				employeeUrl.concat(ALL),
				employeeDto,
	    		new ParameterizedTypeReference<EntityModel<EmployeeDto>>() {})
			.getBody();
	}
	
	public EntityModel<EmployeeContractDto> createEmployeeContract(EmployeeContractDto employeeContractDto) {
		
		return requestComponent.post(
				employeeUrl.concat(String.format(EMPLOYEECONTRACT, employeeContractDto.getEmployeeId())),
				List.of(employeeContractDto),
	    		new ParameterizedTypeReference<EntityModel<EmployeeContractDto>>() {})
			.getBody();
	}
	
	public EntityModel<EmployeeContractDto> updateEmployeeContract(EntityModel<EmployeeContractDto> employeeContractDto) {
		
		return requestComponent.put(
				employeeContractDto.getLink(EmployeeDto.SELF_REF).get().getHref(),
				employeeContractDto.getContent(),
	    		new ParameterizedTypeReference<EntityModel<EmployeeContractDto>>() {})
			.getBody();
	}

}
