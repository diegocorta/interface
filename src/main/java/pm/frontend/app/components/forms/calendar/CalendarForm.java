package pm.frontend.app.components.forms.calendar;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;

import pm.employee.common.dto.calendar.CalendarDto;
import pm.frontend.app.components.standard.components.CelendarComponent;
import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.forms.EntityAction;
import pm.frontend.app.components.standard.forms.IBindeableForm;
import pm.frontend.app.components.standard.forms.EntityAction.Action;
import pm.frontend.app.logic.calendar.CalendarService;

@Component
public class CalendarForm extends FormLayout implements IBindeableForm<CalendarDto> {

	private static final long serialVersionUID = 1596400676900430164L;
		
	private TextField name;
    private TextArea description;

    private Binder<CalendarDto> binder;

    private EntityModel<CalendarDto> initEntityCalendar;
    private EntityModel<CalendarDto> entityCalendar;
    
    private CelendarComponent calendarComponent;
    
    private CalendarService calendarService;
    
	public CalendarForm(CalendarService calendarService) {
		
		this.calendarService = calendarService;
		
        name = new TextField("Nombre del calendario");
        description = new TextArea("Descripcion del calendario");
        buildCalendar();
        
        getStyle().setDisplay(Display.GRID);
        getStyle().setAlignItems(AlignItems.STRETCH); 
        
        add(name, description, calendarComponent);
        
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("380px", 2));
        
        // Stretch the username field over 2 columns
        setColspan(name, 2);
        setColspan(description, 2);
        setColspan(calendarComponent, 2);
		
        setSizeFull();
        
        this.binder = setBinder();
	}
	
	@Override
	public Binder<CalendarDto> setBinder() {
		
		Binder<CalendarDto> binder = new Binder<>(CalendarDto.class);

		binder.forField(name)
			.asRequired("El calendario debe tener un nombre")
			.withValidator(name -> name.length() >= 3 && name.length() <= 40,
					"El nombre del calendario debe tener entre 3 y 40 caracteres")
			.bind(CalendarDto::getName, CalendarDto::setName);
		
		binder.forField(description)
			.withValidator(description -> description.length() <= 1000,
				"La descripcion no puede tener mas de 1000 caracteres")
			.bind(CalendarDto::getDescription, CalendarDto::setDescription);
		
		return binder;
	}

	public void isEditMode(ViewType formStatus) {
		
		if (formStatus.equals(ViewType.NEW)) {
		} else if (formStatus.equals(ViewType.EDIT)) {
		} else {			
		}
	}

	@Override
	public void loadData(ViewType formStatus, EntityModel<CalendarDto> workshift) {
		
		binder.getFields().forEach(f -> f.clear());
		
		if (formStatus.equals(ViewType.NEW)) {
			entityCalendar = null;
			initEntityCalendar = null;
			binder.setBean(null);
			
		} else {
			entityCalendar = calendarService.getOneCalendar(workshift);
			
			initEntityCalendar = EntityModel.of(SerializationUtils.clone(entityCalendar.getContent()));
			initEntityCalendar.add(entityCalendar.getLinks());
			
			binder.setBean(entityCalendar.getContent());
			
			calendarComponent.setCalendarId(entityCalendar.getContent().getId());
			
		}
		
		isEditMode(formStatus);
		
	}
	
	private void buildCalendar() {
		
		calendarComponent = new CelendarComponent(calendarService);

	}

	@Override
	public EntityAction<CalendarDto> saveEntity() {
		
		EntityAction<CalendarDto> entitySaved = new EntityAction<>();
		entitySaved.setPrevious(initEntityCalendar);
		entitySaved.setClassDescriptor("Calendario");
		
		boolean isCreate = entityCalendar == null;		
		CalendarDto calendarDto = isCreate ? new CalendarDto(): entityCalendar.getContent();
		
		if (binder.writeBeanIfValid(calendarDto)) {
			if (modifiedEntitiy(initEntityCalendar, calendarDto)) {
				if (isCreate) {
					entitySaved.setAction(Action.CREATE);
					entitySaved.setEntity(calendarService.createCalendar(calendarDto));
				} else {
					
					entitySaved.setAction(Action.UPDATE);
					entitySaved.setEntity(calendarService.updateCalendar(calendarDto));
				}
			}
			return entitySaved;
		}
		
		return null;
		
	}

	@Override
	public EntityAction<CalendarDto> deleteEntity() {
		
		EntityAction<CalendarDto> entityDeleted = new EntityAction<>();
		entityDeleted.setAction(Action.DELETE);
		entityDeleted.setEntity(entityCalendar);
		entityDeleted.setClassDescriptor("Calendario");
		
		if (entityCalendar != null) {
			calendarService.deleteCalendar(entityCalendar.getContent());
			
			return entityDeleted;
		}
		
		return null;
		
	}

}
