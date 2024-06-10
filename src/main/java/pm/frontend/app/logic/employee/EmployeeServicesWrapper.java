package pm.frontend.app.logic.employee;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class EmployeeServicesWrapper {

	private final EmployeeService employeeService;
	private final ContractTypeService contractTypeService;
	private final GenderTypeService genderTypeService;
	private final JobTypeService jobTypeService;
	
	public EmployeeServicesWrapper(EmployeeService employeeService,
			ContractTypeService contractTypeService,
			GenderTypeService genderTypeService,
			JobTypeService jobTypeService) {
		
		this.employeeService = employeeService;
		this.contractTypeService = contractTypeService;
		this.genderTypeService = genderTypeService;
		this.jobTypeService = jobTypeService;
		
	}
	
}
