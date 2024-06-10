package pm.frontend.app.components.standard.tables;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.Overflow;
import com.vaadin.flow.theme.lumo.LumoIcon;

import lombok.Getter;

@CssImport("./styles/table.css")
public class HeaderTable extends HorizontalLayout {

private static final long serialVersionUID = -8096395675294608218L;
	
	private H2 tableName;
	private Span selectedNum;

	@Getter
	private Button simpleSearch;
	@Getter
	private TextField simpleText;
	
	@Getter
	private Button showColumnsButton;
	private Button filter;
	
	@Getter
	private Button addData;
	@Getter
	private Button deleteData;
	
	private HorizontalLayout searchHorizontalLayout;
	private HorizontalLayout editionButtons;
	
	private Boolean editableTable = true;
	
	private Boolean basicSearchTable = true;
	
	public HeaderTable(String name) {
		
		this.tableName = new H2(name);
		this.selectedNum = new Span("Seleccionados: 0");
		
		this.simpleSearch = new Button(LumoIcon.SEARCH.create());
		this.simpleText = new TextField();
		simpleText.setPlaceholder("Texto");
		simpleText.setSizeFull();
		simpleText.getStyle().setOverflow(Overflow.HIDDEN);
		
		this.showColumnsButton = new Button(LumoIcon.UNORDERED_LIST.create());
		this.filter = new Button(LumoIcon.DROPDOWN.create());
		
		this.addData = new Button(LumoIcon.PLUS.create());
		this.deleteData = new Button(LumoIcon.MINUS.create());
		
		this.add(
				buildNameTable(),
				buildSearch(),
				buildSettings(),
				buildInteractSettings());
	
		setAlignItems(FlexComponent.Alignment.CENTER);
		addClassName("table-header");
		
		setItemSelected(0);
	
		this.getStyle().setPaddingRight("15px");
	}
	
	public HeaderTable(String name, Boolean editableTable) {
		
		this(name);
		
		setEditableTable(editableTable);
		
	}

	public HeaderTable(String name, Boolean editableTable, Boolean basicSearchTable) {
		
		this(name);
		
		setEditableTable(editableTable);
		setSerchableTable(basicSearchTable);
		
	}

	private VerticalLayout buildNameTable() {
		
		VerticalLayout vl = new VerticalLayout();
		
		vl.setSpacing(false);
		vl.setMinWidth("160px");
		vl.setMaxWidth("300px");
		this.selectedNum.setSizeFull();
		this.tableName.setSizeFull();
		
		this.tableName.getStyle().setFontSize("1.4em");
		this.selectedNum.getStyle().setFontSize("0.8em");
		
		vl.add(
				this.tableName,
				this.selectedNum
		);
		
		return vl;
	}
	
	private HorizontalLayout buildSearch() {
		
		searchHorizontalLayout = new HorizontalLayout();
		
		searchHorizontalLayout.setWidth("30%");
		
		if (!basicSearchTable) {
			searchHorizontalLayout.setVisible(false);
		}
		
		searchHorizontalLayout.add(
				simpleText,
				simpleSearch
		);
		

		simpleText.addKeyUpListener(Key.ENTER, key -> {
			simpleSearch.click();
		});
		
		searchHorizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		searchHorizontalLayout.setClassName("table-search");
		
		return searchHorizontalLayout;
	}
	
	private HorizontalLayout buildSettings() {
		
		HorizontalLayout hl = new HorizontalLayout();
		
		hl.setAlignItems(FlexComponent.Alignment.CENTER);

		hl.add(
				showColumnsButton,
				filter
		);
		
		hl.addClassName("table-right");
		return hl;
	}
	
	private HorizontalLayout buildInteractSettings() {
		
		editionButtons = new HorizontalLayout();
		
		if (!editableTable) {
			editionButtons.setVisible(false);
		}
		
		editionButtons.setAlignItems(FlexComponent.Alignment.CENTER);
		editionButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		editionButtons.setWidth("100px");
		
		deleteData.setVisible(false);
		
		editionButtons.add(
				addData,
				deleteData
		);
		
		return editionButtons;
		
	}
	
	public void setItemSelected(int num) {
		String message = "Seleccionados: ";
		
		if (num > 0) {
			selectedNum.setText(message.concat(String.valueOf(num)));
			selectedNum.setVisible(true);
			deleteData.setVisible(true);
		} else {
			selectedNum.setVisible(false);
			deleteData.setVisible(false);
		}
	}
	
	public void isOnEdition(boolean onEdition) {
		
		if (onEdition) {
			filter.setEnabled(false);
			simpleSearch.setEnabled(false);
		} else {
			filter.setEnabled(true);
			simpleSearch.setEnabled(true);
		}
		
	}
	
	public void hideHeader(boolean hide) {
		
		if (hide) {
			this.setVisible(false);
		} else {

			this.setVisible(true);
		}
		
	}
	
	private void setEditableTable(Boolean editableTable) {
		
		editionButtons.setVisible(editableTable);
		
	}
	
	private void setSerchableTable(Boolean basicSearchTable) {
		
		searchHorizontalLayout.setVisible(basicSearchTable);
		
	}
	
}
