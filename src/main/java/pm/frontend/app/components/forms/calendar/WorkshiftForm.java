package pm.frontend.app.components.forms.calendar;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import org.vaadin.addons.tatu.ColorPicker;
import org.vaadin.addons.tatu.ColorPicker.ColorPreset;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import pm.employee.common.dto.calendar.WorkshiftDto;
import pm.employee.common.dto.calendar.WorkshiftEventDto;
import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.details.IBindeableDetails;
import pm.frontend.app.components.standard.details.SingleLineDetails;
import pm.frontend.app.components.standard.forms.EntityAction;
import pm.frontend.app.components.standard.forms.IBindeableForm;
import pm.frontend.app.components.standard.forms.EntityAction.Action;
import pm.frontend.app.logic.calendar.WorkshiftService;

@Component
public class WorkshiftForm extends FormLayout implements IBindeableForm<WorkshiftDto> {

	private static final long serialVersionUID = 1596400676900430164L;
		
	private TextField name;
	private TextField code;
    private TextArea description;
    
    private Checkbox isProductive;
    private ColorPicker colorPicker;

    private Binder<WorkshiftDto> binder;

    private SingleLineDetails<WorkshiftEventDto> events;
    
    private EntityModel<WorkshiftDto> entityWorkshift;
    private EntityModel<WorkshiftDto> initEntityWorkshift;

    private Collection<EntityModel<WorkshiftEventDto>> entityEvents = new ArrayList<>();
    
    private WorkshiftService workshiftService;
    
	public WorkshiftForm(WorkshiftService workshiftService) {
		
		this.workshiftService = workshiftService;
		
        name = new TextField("Nombre del turno");
        code = new TextField("Codigo del turno");

        description = new TextArea("Descripcion del turno");
        
        isProductive = new Checkbox("El turno es productivo", true);
        
        colorPicker = new ColorPicker();
        colorPicker.setLabel("Color");
        colorPicker
                .setPresets(Arrays.asList(new ColorPreset("#00ff00", "Color 1"),
                        new ColorPreset("#ff0000", "Color 2")));
        
        IBindeableDetails<WorkshiftEventDto> bindFunction = new IBindeableDetails<WorkshiftEventDto>() {
			@Override
			public String getPrincipal(EntityModel<WorkshiftEventDto> entity) {
				
				WorkshiftEventDto event = entity.getContent();
				
				if (StringUtils.hasText(event.getTime())) {
					
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
					
					LocalTime start = LocalTime.parse(event.getTime());
					LocalTime end = start.plusSeconds(event.getDuration());
					
					return start.format(formatter).concat(" - ").concat(end.format(formatter));
				} else {
					
				}
				return (entity.getContent().getDuration() / 3600)+" h";
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		};
        
        events = new SingleLineDetails<WorkshiftEventDto>("Tiempos", ViewType.EDIT, bindFunction);
        initializeEvents();
        addEventsOpenListener();
        
        add(name, code, description, isProductive, colorPicker, events);
        
        setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("380px", 2));
        
        // Stretch the workshiftname field over 2 columns
        setColspan(name, 2);
        setColspan(code, 2);
        setColspan(description, 2);
        setColspan(isProductive, 2);
        setColspan(colorPicker, 2);
        setColspan(events, 2);
		
        this.binder = setBinder();
	}
	
	@Override
	public Binder<WorkshiftDto> setBinder() {
		
		Binder<WorkshiftDto> binder = new Binder<>(WorkshiftDto.class);

		binder.forField(name)
			.asRequired("El turno debe tener un nombre")
			.withValidator(name -> name.length() >= 3 && name.length() <= 40,
					"El nombre del turno debe tener entre 3 y 40 caracteres")
			.bind(WorkshiftDto::getName, WorkshiftDto::setName);
		
		binder.forField(code)
		.asRequired("El turno debe tener un codigo")
		.withValidator(name -> name.length() >= 2 && name.length() <= 8,
				"El codigo del turno debe tener entre 2 y 8 caracteres")
		.bind(WorkshiftDto::getCode, WorkshiftDto::setCode);
		
		binder.forField(description)
			.withValidator(description -> description.length() <= 1000,
					"La descripcion no puede tener mas de 1000 caracteres")
			.bind(WorkshiftDto::getDescription, WorkshiftDto::setDescription);
		
		binder.forField(isProductive)
			.bind(WorkshiftDto::isProductive, WorkshiftDto::setProductive);
		
		binder.forField(colorPicker)
			.bind(WorkshiftDto::getColor, WorkshiftDto::setColor);
		
		return binder;
	}



	private void initializeEvents() {
		
		events.setSummaryText("Tiempos");
		events.setOpened(false);
		events.removeAll();

	}

	public void isEditMode(ViewType formStatus) {
		
		if (formStatus.equals(ViewType.NEW)) {
			events.setEnabled(false);
		} else if (formStatus.equals(ViewType.EDIT)) {
			events.setEnabled(true);
		} else {			
			events.setEnabled(true);
		}
	}

	@Override
	public void loadData(ViewType formStatus, EntityModel<WorkshiftDto> workshift) {
		
		if (formStatus.equals(ViewType.NEW)) {
			entityWorkshift = null;
			initEntityWorkshift = null;
			binder.setBean(null);
			
		} else {
			entityWorkshift = workshiftService.getOneWorkshift(workshift);
			
			initEntityWorkshift = EntityModel.of(SerializationUtils.clone(entityWorkshift.getContent()));
			initEntityWorkshift.add(entityWorkshift.getLinks());
			
			binder.setBean(entityWorkshift.getContent());
			
		}
		
		initializeEvents();
		isEditMode(formStatus);
		
	}
	
	private void addEventsOpenListener() {
		
		events.addOpenedChangeListener(open -> {
			if (open.isOpened()) {
				
				entityEvents.clear();
				entityEvents.addAll(
						workshiftService.getWorkshiftEvents(entityWorkshift).getContent());
				
				events.setContent(entityEvents);
			}
		});
	}

	@Override
	public EntityAction<WorkshiftDto> saveEntity() {
		
		EntityAction<WorkshiftDto> entitySaved = new EntityAction<>();
		entitySaved.setPrevious(initEntityWorkshift);
		entitySaved.setClassDescriptor("Turno");
		
		boolean isCreate = entityWorkshift == null;		
		WorkshiftDto workshiftDto = isCreate ? new WorkshiftDto(): entityWorkshift.getContent();
		
		if (binder.writeBeanIfValid(workshiftDto)) {
			if (modifiedEntitiy(initEntityWorkshift, workshiftDto)) {
				if (isCreate) {
					entitySaved.setAction(Action.CREATE);
					entitySaved.setEntity(workshiftService.createWorkshift(workshiftDto));
				} else {
					
					entitySaved.setAction(Action.UPDATE);
					entitySaved.setEntity(workshiftService.updateWorkshift(workshiftDto));
				}
			}
			return entitySaved;
		}
		
		return null;
	}

	@Override
	public EntityAction<WorkshiftDto> deleteEntity() {
		
		EntityAction<WorkshiftDto> entityDeleted = new EntityAction<>();
		entityDeleted.setAction(Action.DELETE);
		entityDeleted.setEntity(entityWorkshift);
		entityDeleted.setClassDescriptor("Turno");
		
		if (entityWorkshift != null) {
			workshiftService.deleteWorkshift(entityWorkshift.getContent());
			
			return entityDeleted;
		}
		
		return null;
	}
	
}
