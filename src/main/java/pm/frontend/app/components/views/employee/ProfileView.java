package pm.frontend.app.components.views.employee;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pm.frontend.app.components.forms.employee.EmployeeForm;
import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.CountryService;
import pm.frontend.app.logic.employee.EmployeeServicesWrapper;
import pm.frontend.app.logic.security.AuthenticationService;
import pm.frontend.app.logic.security.UserService;

@PageTitle("Main")
@Route(value = "/profile", layout = AppView.class)
public class ProfileView extends VerticalLayout {

	private static final long serialVersionUID = -8025689310182504842L;
	
	
	public ProfileView(CountryService countryService,
			EmployeeServicesWrapper employeeServiceWrapper,
			AuthenticationService authenticationService,
			UserService userService) {
		
		setSizeFull();
		setPadding(false);
		
		var employeeDto = authenticationService.getVaadinSessionUser();

		var employeeForm = new EmployeeForm(countryService, employeeServiceWrapper, userService, employeeDto, ViewType.VIEW);
		        
		add(employeeForm);

	}
	
}
