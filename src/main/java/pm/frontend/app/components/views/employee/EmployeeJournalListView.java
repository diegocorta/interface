package pm.frontend.app.components.views.employee;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.AlignSelf;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.JustifyContent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pm.employee.common.dto.employee.EmployeeDto;
import pm.employee.common.dto.recording.EmployeeJournalDto;
import pm.employee.common.dto.recording.EmployeeJournalRecordDto;
import pm.employee.common.dto.recording.EmployeeJournalRecordToRegisterDto;
import pm.employee.common.dto.recording.RecordTypeDto;
import pm.frontend.app.components.standard.components.AlertBuilder;
import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.employee.EmployeeJournalService;
import pm.frontend.app.logic.employee.RecordTypeService;
import pm.frontend.app.logic.security.AuthenticationService;

@PageTitle("Journals")
@Route(value = "/journal", layout = AppView.class)
public class EmployeeJournalListView extends VerticalLayout {

	private static final long serialVersionUID = 810174180011590221L;

	private LocalDate date;
	
	private TextField textDate = new TextField();
	private TextField currentState = new TextField();
	
	private Button next = new Button(VaadinIcon.ANGLE_RIGHT.create());
	private Button prev = new Button(VaadinIcon.ANGLE_LEFT.create());
	
	private Button initJournal = new Button(VaadinIcon.PLAY.create());
	private Button endJournal = new Button(VaadinIcon.STOP.create());
	
	private ComboBox<EntityModel<RecordTypeDto>> others = new ComboBox<>();
	private Button toogleOther = new Button();
	
	private EntityModel<EmployeeDto> employee;
	private Collection<EntityModel<RecordTypeDto>> recordTypes;
	
	private EmployeeJournalService employeeJournalService;
		
	private EntityModel<EmployeeJournalDto> journal;
	private Collection<EntityModel<EmployeeJournalRecordDto>> journalRecords;
	private EntityModel<EmployeeJournalRecordDto> lastRecord;
	
	public EmployeeJournalListView(AuthenticationService authenticationService,
			EmployeeJournalService employeeJournalService,
			RecordTypeService recordTypeService) {
		
		this.employee = authenticationService.getVaadinSessionUser();
		this.employeeJournalService = employeeJournalService;
		
		this.recordTypes = recordTypeService.getAllRecordTypes().getContent();
		
		setSizeFull();
		setPadding(false);
		
		getStyle().setDisplay(Display.FLEX);
		getStyle().setAlignItems(AlignItems.CENTER);
		getStyle().setJustifyContent(JustifyContent.CENTER);
		
		VerticalLayout vl = new VerticalLayout();
		vl.setPadding(false);
		vl.getStyle().setDisplay(Display.FLEX);
		vl.getStyle().setAlignItems(AlignItems.CENTER);
		vl.getStyle().setJustifyContent(JustifyContent.CENTER);

		date = LocalDate.now();
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidthFull();
		
		setDateInForm();
		textDate.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
		textDate.getStyle().setFlexGrow("1");
		
		prev.addClickListener(click -> minusDate());
		next.addClickListener(click -> plusDate());
		
		hl.add(prev, textDate, next);
		
		
		currentState.setLabel("Estado actual: ");
		currentState.setWidthFull();
		currentState.getStyle().setPadding("0px");
		
		initJournal.setText("Empezar jornada");
		initJournal.setWidth("13em");
		initJournal.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		initJournal.addClickListener(click -> initJournal());
		endJournal.setText("Finalizar jornada");
		endJournal.setWidth("13em");
		endJournal.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		endJournal.addClickListener(click -> endJournal());
		
		HorizontalLayout hl2 = new HorizontalLayout();
		
		others.setLabel("Trabajo indirecto: ");
		others.setItems(recordTypes.stream()
				.filter(recordType -> !recordType.getContent().isDefaultType())
				.sorted((o1, o2) -> o1.getContent().getName().compareTo(o2.getContent().getName()))
				.collect(Collectors.toList()));
		others.setItemLabelGenerator(recordType -> recordType.getContent().getName());
		others.getStyle().setPadding("0px");
		
		toogleOther.setText("Iniciar ...");
		toogleOther.getStyle().setAlignSelf(AlignSelf.END);
		toogleOther.getStyle().setMargin("0px");
		toogleOther.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		toogleOther.addClickListener(click -> addIndirect());

		
		hl2.add(others, toogleOther);
		hl2.setPadding(false);
		hl2.setJustifyContentMode(JustifyContentMode.CENTER);
		hl2.setAlignItems(Alignment.CENTER);
		
		vl.add(new H4("Registro de jornada para el día:"), hl, currentState, new Hr(), initJournal, endJournal, new Hr(), hl2);
		vl.setWidth("350px");
		
		toogleOther.setEnabled(false);
		endJournal.setEnabled(false);
		others.setEnabled(false);
		others.setReadOnly(false);
		textDate.setReadOnly(true);
		currentState.setReadOnly(true);
		
		loadDate(employee, date);
		
		add(vl);
	}
	
	private void setDateInForm() {
		
		String prevText = "";
		if (date.equals(LocalDate.now())) {
			prevText = "Hoy: ";
		} else if (date.equals(LocalDate.now().plusDays(1))) {
			prevText = "Mañana: ";
		} else if (date.equals(LocalDate.now().minusDays(1))) {
			prevText = "Ayer: ";
		}
		
		textDate.setValue(prevText.concat(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
	}
	
	private void loadDate(EntityModel<EmployeeDto> employee, LocalDate date) {
		
		setEnabled(false);
		
		this.journal = null;
		this.lastRecord = null;
		this.journalRecords = null;
		
		journal = employeeJournalService.getJournalOfDate(employee, date);
		
		if (journal == null) {
			loadNew();
		} else if (journal.getContent().getJournalEnd() != null) {
			loadEnded();
		} else {
			journalRecords = employeeJournalService.getRecordsOfJournal(journal);
			
			loadJournalInfo(journalRecords);
		}
		
		setEnabled(true);
		
	}
	
	private void loadEnded() {
		
		currentState.setValue("Finalizado");
		Icon icon = VaadinIcon.CIRCLE.create();
		icon.setColor("black");
		icon.setSize("18px");
		currentState.setPrefixComponent(icon);
		
		others.setValue(null);
		toogleOther.setText("Iniciar...");
		initJournal.setEnabled(false);
		toogleOther.setEnabled(false);
		endJournal.setEnabled(false);
		others.setEnabled(false);
		others.setReadOnly(false);
		textDate.setReadOnly(true);
		currentState.setReadOnly(true);
		
	}

	private void loadNew() {
		
		currentState.setValue("Sin comenzar");
		Icon icon = VaadinIcon.CIRCLE.create();
		icon.setColor("red");
		icon.setSize("18px");
		currentState.setPrefixComponent(icon);
		
		others.setValue(null);
		toogleOther.setText("Iniciar...");
		initJournal.setEnabled(true);
		toogleOther.setEnabled(false);
		endJournal.setEnabled(false);
		others.setEnabled(false);
		others.setReadOnly(false);
		textDate.setReadOnly(true);
		currentState.setReadOnly(true);
		
	}
	
	private void loadJournalInfo(Collection<EntityModel<EmployeeJournalRecordDto>> records) {
		
		lastRecord = records.stream()
			.max((r1, r2) -> ZonedDateTime.parse(r1.getContent().getInstant())
					.compareTo(ZonedDateTime.parse(r2.getContent().getInstant()))).get();
		
		EntityModel<RecordTypeDto> recordType = recordTypes.stream()
				.filter(rt -> rt.getContent().getId().equals(lastRecord.getContent().getRecordTypeId()))
				.findFirst().orElse(null);
		
		// Estamos en trozo de jornada estandar si es el comienzo de la jornada (default type)
		// o si el ultimo registro es de salida. Por ejemplo, un "salgo de comer", haria volver al evento estandar
		if (recordType.getContent().isDefaultType() || !lastRecord.getContent().isEntry()) {
			
			currentState.setValue("En curso");
			Icon icon = VaadinIcon.CIRCLE.create();
			icon.setColor("green");
			icon.setSize("18px");
			currentState.setPrefixComponent(icon);
			
			others.setValue(null);
			toogleOther.setText("Iniciar...");
			initJournal.setEnabled(false);
			toogleOther.setEnabled(true);
			endJournal.setEnabled(true);
			others.setEnabled(true);
			others.setReadOnly(false);
			textDate.setReadOnly(true);
			currentState.setReadOnly(true);
			
			// Si estamos en la entrada de un evento productivo, solamente se va a poder fichar la salida del mismo
			// Para volver al evento estandar, pero el tiempo tiene que estar contabilizando como en verde
		} else if (recordType.getContent().isProductive() && lastRecord.getContent().isEntry()) {
			
			currentState.setValue("En ".concat(recordType.getContent().getName()));
			Icon icon = VaadinIcon.CIRCLE.create();
			icon.setColor("green");
			icon.setSize("18px");
			currentState.setPrefixComponent(icon);
			
			others.setValue(recordType);
			toogleOther.setText("Finalizar");
			initJournal.setEnabled(false);
			toogleOther.setEnabled(true);
			endJournal.setEnabled(false);
			others.setEnabled(true);
			others.setReadOnly(true);
			textDate.setReadOnly(true);
			currentState.setReadOnly(true);
			
		} else {
			
			currentState.setValue("En ".concat(recordType.getContent().getName()));
			Icon icon = VaadinIcon.CIRCLE.create();
			icon.setColor("yellow");
			icon.setSize("18px");
			currentState.setPrefixComponent(icon);
			
			others.setValue(recordType);
			toogleOther.setText("Finalizar");
			initJournal.setEnabled(false);
			toogleOther.setEnabled(true);
			endJournal.setEnabled(false);
			others.setEnabled(true);
			others.setReadOnly(true);
			textDate.setReadOnly(true);
			currentState.setReadOnly(true);
			
		}
		
	}
	
	

	private void plusDate() {
		
		if (date.plusDays(1).isAfter(LocalDate.now().plusDays(1))) {
			AlertBuilder.warn("Fecha invalida", null, "No puedes seleccionar fechas posteriores").open();;
		} else {
			
			date = date.plusDays(1);
			setDateInForm();
		}
		loadDate(employee, date);
	}
	
	private void minusDate() {
		
		if (date.minusDays(1).isBefore(LocalDate.now().minusDays(7))) {
			AlertBuilder.warn("Fecha invalida", null, "No puedes seleccionar fechas anteriores").open();
		} else {
			
			date = date.minusDays(1);
			setDateInForm();
		}
		
		loadDate(employee, date);
	}
	
	private void addIndirect() {
		
		if (others.getValue() == null) {
			AlertBuilder.warn(null, null, "Debes seleccionar un tipo de trabajo indirecto para poder iniciarlo").open();
		} else {
			
			EmployeeJournalRecordToRegisterDto ejr = new EmployeeJournalRecordToRegisterDto();
			String message = "";
			
			if (others.getValue().getContent().getId().equals(lastRecord.getContent().getRecordTypeId())
					&& lastRecord.getContent().isEntry()) {
				
				ejr.setInstant(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
				ejr.setEmployeeId(employee.getContent().getId());
				ejr.setEntry(false);
				ejr.setRecordTypeId(lastRecord.getContent().getRecordTypeId());
				ejr.setEmployeeJournalId(journal.getContent().getId());
				
				message = "Trabajo indirecto cerrado con exito";

			} else {
				
				ejr.setInstant(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
				ejr.setEmployeeId(employee.getContent().getId());
				ejr.setEntry(true);
				ejr.setRecordTypeId(others.getValue()
						.getContent().getId());
				ejr.setEmployeeJournalId(journal.getContent().getId());
				
				message = "Trabajo indirecto iniciado con exito";
			}
			
			employeeJournalService.createRecordsOfJournal(ejr);
			
			AlertBuilder.success(null, null, message).open();

			loadDate(employee, date);
		}
	}
	
	private void initJournal() {
		
		if (journal == null && lastRecord == null) {
			
			EmployeeJournalRecordToRegisterDto ejr = new EmployeeJournalRecordToRegisterDto();
			ejr.setInstant(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
			ejr.setEmployeeId(employee.getContent().getId());
			ejr.setEntry(true);
			ejr.setRecordTypeId(recordTypes.stream()
					.filter(rt -> rt.getContent().isDefaultType())
					.findFirst()
					.orElse(null)
					.getContent().getId());
			ejr.setDate(date.format(DateTimeFormatter.ISO_DATE));
			ejr.setTimezone(TimeZone.getTimeZone(ZoneId.of("Europe/Madrid")));
			
			employeeJournalService.createRecordsOfJournal(ejr);
			
			AlertBuilder.success(null, null, "Inicio de jornada realizado con exito").open();
			
			loadDate(employee, date);
		}
		
	}
	
	private void endJournal() {
		
		if (journal != null 
				&& !StringUtils.hasText(journal.getContent().getJournalEnd())
				&& lastRecord != null) {
			
			EmployeeJournalRecordToRegisterDto ejr = new EmployeeJournalRecordToRegisterDto();
			ejr.setInstant(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
			ejr.setEmployeeId(employee.getContent().getId());
			ejr.setEntry(false);
			ejr.setRecordTypeId(recordTypes.stream()
					.filter(rt -> rt.getContent().isDefaultType())
					.findFirst()
					.orElse(null)
					.getContent().getId());
			ejr.setEmployeeJournalId(journal.getContent().getId());

			employeeJournalService.createRecordsOfJournal(ejr);

			AlertBuilder.success(null, null, "Fin de jornada realizado con exito").open();
			
			loadDate(employee, date);
		}

	}
	
}
