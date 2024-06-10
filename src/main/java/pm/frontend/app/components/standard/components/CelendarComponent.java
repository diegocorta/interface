package pm.frontend.app.components.standard.components;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.hateoas.EntityModel;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.FullCalendarVariant;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

import pm.employee.common.dto.calendar.WorkshiftDto;
import pm.frontend.app.logic.calendar.CalendarService;

@CssImport("./styles/calendar.css")
public class CelendarComponent extends VerticalLayout {

	private static final long serialVersionUID = -8025689310182504842L;

	private LocalDate selectedDate = LocalDate.now();
	private FullCalendar calendar;
	
	private Long calendarId;
	
	private DatePicker gotoDate;
	private Button buttonDatePicker;
	
	private CalendarService calendarService;
	
	private Map<LocalDate, Collection<EntityModel<WorkshiftDto>>> workshiftMap = new HashMap<>();

	public CelendarComponent(CalendarService calendarService) {

		this.calendarService = calendarService;
		
		setSizeFull();
		setAlignItems(Alignment.STRETCH);
		setPadding(false);
				
		buildCalendar();
		
		add(buildHeader(), calendar);
		setFlexGrow(1, calendar);

	}

	private void buildCalendar() {
		calendar = FullCalendarBuilder.create().build();

		calendar.setLocale(Locale.getDefault());
		calendar.setWeekNumbersVisible(false);
		calendar.setFixedWeekCount(false);
		calendar.addThemeVariants(FullCalendarVariant.LUMO);
		
		calendar.getEntryProvider().asInMemory().removeAllEntries();
		calendar.render();
		
		calendar.addDatesRenderedListener(event -> {
			buttonDatePicker.setText(
					selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.getDefault())));
			
		});
	}

	private VerticalLayout buildHeader() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setAlignItems(Alignment.CENTER);
		
		MenuBar mb = new MenuBar();
		
		gotoDate = new DatePicker();
        gotoDate.setLocale(new Locale("es", "ES"));
		
        gotoDate.addValueChangeListener(event1 -> {
			
        	calendar.gotoDate(event1.getValue());
        	selectedDate = event1.getValue();
        	
        	if (calendarId != null) {
				buildEvents();
        	}
			
		});
        
		gotoDate.getElement().getStyle().set("visibility", "hidden");
		gotoDate.getElement().getStyle().set("position", "fixed");
		gotoDate.setWeekNumbersVisible(true);
		
		buttonDatePicker = new Button(LumoIcon.CALENDAR.create());
		buttonDatePicker.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		buttonDatePicker.getElement().appendChild(gotoDate.getElement());
		buttonDatePicker.setWidth("10em");
		
		buttonDatePicker.addClickListener(event -> {
			gotoDate.open();
		});
		
		mb.addItem(VaadinIcon.ANGLE_LEFT.create(), e -> {
			setSelectedDay(selectedDate.minusMonths(1));
		});
		
		mb.addItem(buttonDatePicker);
		
		mb.addItem(VaadinIcon.ANGLE_RIGHT.create(), e -> {
			setSelectedDay(selectedDate.plusMonths(1));
		}).getStyle().setMinWidth("auto");
		
		mb.addItem("Hoy", e -> {
			setSelectedDay(LocalDate.now());
		});
		
		vl.add(mb);
		vl.getStyle().setPaddingBottom("0px");
		
		gotoDate.setValue(LocalDate.now());
		
		return vl;
	}

	private void buildEvents() {
		
		workshiftMap = calendarService
				.getEventsFromDates(calendarId, selectedDate.with(TemporalAdjusters.firstDayOfMonth()).minusWeeks(1),
						selectedDate.with(TemporalAdjusters.lastDayOfMonth()).plusWeeks(1));
		setEvents();
	}
	
	private void setSelectedDay(LocalDate date) {
		gotoDate.setValue(date);
	}
	
	private void setEvents() {
		
		// Create a initial sample entry
		List<Entry> entries = new ArrayList<>();
		
		for (LocalDate date : workshiftMap.keySet()) {
			
			workshiftMap.get(date).stream().forEach(wk -> {
				Entry entry = new Entry();
				entry.setTitle(wk.getContent().getCode());
				entry.setColor(wk.getContent().getColor());

		        // Obtener el color de texto con suficiente contraste
				if (!ColorUtil.isDarkBackground(Color.decode(wk.getContent().getColor()))) {
					entry.addClassNames("light-chip");
				} else {
					entry.addClassNames("dark-chip");
				}
				
				entry.setStart(date);
				entry.setEnd(date);
				entry.setAllDay(true);
				
				entries.add(entry);
			});
			
		}
		
		calendar.getEntryProvider().asInMemory().removeAllEntries();
		calendar.getEntryProvider().asInMemory().addEntries(entries);
		calendar.render();
	}

	public void setCalendarId(Long id) {

		remove(calendar);
		
		buildCalendar();

		add(calendar);
		
		calendarId = id;
			
		buildEvents();
		
	}

}
