package pm.frontend.app.components.forms.calendar;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import pm.employee.common.dto.calendar.WorkdayDto;
import pm.employee.common.dto.calendar.WorkshiftDto;
import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.details.IBindeableDetails;
import pm.frontend.app.components.standard.details.SingleLineDetails;
import pm.frontend.app.components.standard.forms.EntityAction;
import pm.frontend.app.components.standard.forms.IBindeableForm;
import pm.frontend.app.components.standard.forms.EntityAction.Action;
import pm.frontend.app.logic.calendar.WorkdayService;

@Component
public class WorkdayForm extends FormLayout implements IBindeableForm<WorkdayDto> {

	private static final long serialVersionUID = 1596400676900430164L;
		
	private TextField name;
    private TextArea description;

    private Checkbox startsPreviousDay;

    
    private Binder<WorkdayDto> binder;

    private SingleLineDetails<WorkshiftDto> workshifts;
    
    private EntityModel<WorkdayDto> entityWorkday;
    private EntityModel<WorkdayDto> initEntityWorkday;

    
    private Collection<EntityModel<WorkshiftDto>> entityWorkshifts = new ArrayList<>();
    
    private WorkdayService workdayService;
    
	public WorkdayForm(WorkdayService WorkdayService) {
		
		this.workdayService = WorkdayService;
		
        name = new TextField("Nombre de jornada");
        description = new TextArea("Descripcion de jornada");
        
        startsPreviousDay = new Checkbox("La jornada empieza el dia antes", false);
        startsPreviousDay.setTooltipText("Si la jornada empieza con un turno de noche, que comienza el dia previo, marcar esta casilla. Por ejemplo, la semana comienza con un turno de noche que comienza el domingo a las 22:00 de la noche");
        
        IBindeableDetails<WorkshiftDto> bindFunction = new IBindeableDetails<WorkshiftDto>() {
			@Override
			public String getPrincipal(EntityModel<WorkshiftDto> entity) {
				return entity.getContent().getName();
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		};
        
        workshifts = new SingleLineDetails<WorkshiftDto>("Turnos", ViewType.EDIT, bindFunction);
        initializeWorkshifts();
        addWorkshiftsOpenListener();
        
        add(name, description, startsPreviousDay, workshifts);
        
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("380px", 2));
        
        // Stretch the workshiftname field over 2 columns
        setColspan(name, 2);
        setColspan(description, 2);
        setColspan(workshifts, 2);
        setColspan(startsPreviousDay, 2);
		
        this.binder = setBinder();
	}
	
	@Override
	public Binder<WorkdayDto> setBinder() {
		
		Binder<WorkdayDto> binder = new Binder<>(WorkdayDto.class);

		binder.forField(name)
			.asRequired("La jornada debe tener un nombre")
			.withValidator(name -> name.length() >= 3 && name.length() <= 40,
					"La jornada del calendario debe tener entre 3 y 40 caracteres")
			.bind(WorkdayDto::getName, WorkdayDto::setName);
		
		binder.forField(description)
			.withValidator(description -> description.length() <= 1000,
					"La descripcion no puede tener mas de 1000 caracteres")
			.bind(WorkdayDto::getDescription, WorkdayDto::setDescription);
		
		binder.forField(startsPreviousDay)
			.bind(WorkdayDto::isStartsPreviousDay, WorkdayDto::setStartsPreviousDay);
		
		
		return binder;
	}



	private void initializeWorkshifts() {
		
		workshifts.setSummaryText("Turnos");
		workshifts.setOpened(false);
		workshifts.removeAll();

	}

	public void isEditMode(ViewType formStatus) {
		
		if (formStatus.equals(ViewType.NEW)) {
			workshifts.setEnabled(false);
		} else if (formStatus.equals(ViewType.EDIT)) {
			workshifts.setEnabled(true);
		} else {			
			workshifts.setEnabled(true);
		}
	}

	@Override
	public void loadData(ViewType formStatus, EntityModel<WorkdayDto> Workday) {
		
		if (formStatus.equals(ViewType.NEW)) {
			entityWorkday = null;
			initEntityWorkday = null;
			binder.setBean(null);
			
		} else {
			entityWorkday = workdayService.getOneWorkday(Workday);

			initEntityWorkday = EntityModel.of(SerializationUtils.clone(entityWorkday.getContent()));
			initEntityWorkday.add(entityWorkday.getLinks());
			
			
			binder.setBean(entityWorkday.getContent());
			
		}
		
		initializeWorkshifts();
		isEditMode(formStatus);
		
	}
	
	private void addWorkshiftsOpenListener() {
		
		workshifts.addOpenedChangeListener(open -> {
			if (open.isOpened()) {
				
				entityWorkshifts.clear();
				entityWorkshifts.addAll(
						workdayService.getWorkshifts(entityWorkday).getContent());
				
				workshifts.setContent(entityWorkshifts);
			}
		});
	}

	@Override
	public EntityAction<WorkdayDto> saveEntity() {
		
		EntityAction<WorkdayDto> entitySaved = new EntityAction<>();
		entitySaved.setPrevious(initEntityWorkday);
		entitySaved.setClassDescriptor("Jornada");
		
		boolean isCreate = entityWorkday == null;		
		WorkdayDto workdayDto = isCreate ? new WorkdayDto(): entityWorkday.getContent();
		
		if (binder.writeBeanIfValid(workdayDto)) {
			if (modifiedEntitiy(initEntityWorkday, workdayDto)) {
				if (isCreate) {
					
					workdayDto.setTimezone(TimeZone.getTimeZone(ZoneId.of("Europe/Madrid")));
					
					entitySaved.setAction(Action.CREATE);
					entitySaved.setEntity(workdayService.createWorkday(workdayDto));
				} else {
					
					entitySaved.setAction(Action.UPDATE);
					entitySaved.setEntity(workdayService.updateWorkday(workdayDto));
				}
			}
			
			return entitySaved;
		}
		
		return null;
	}

	@Override
	public EntityAction<WorkdayDto> deleteEntity() {
		
		EntityAction<WorkdayDto> entityDeleted = new EntityAction<>();
		entityDeleted.setAction(Action.DELETE);
		entityDeleted.setEntity(entityWorkday);
		entityDeleted.setClassDescriptor("Jornada");
		
		if (entityWorkday != null) {
			workdayService.deleteWorkday(entityWorkday.getContent());
			
			return entityDeleted;
		}
		
		return null;
		
	}
	
}
