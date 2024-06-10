package pm.frontend.app.components.standard.tables;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

import lombok.Getter;

@CssImport("./styles/table.css")
public class Table<T> extends VerticalLayout {

	private static final long serialVersionUID = 4016941117499111788L;

	// Table interface to inherit behaveour
	private ITable<T> tableView;

	// layouts
	private HeaderTable header;
	private HorizontalLayout tableLayout;


	// Elements
	@Getter
	private Grid<EntityModel<T>> grid;
	
	private ColumnToggleContextMenu<T> columnToggleContextMenu;

	// Configuration

	// Define if the table allow resize columns
	private final boolean allowTableColumnsResize;

	// Define if the table allow columns reorder
	private final boolean allowTableColumnsReorder;

	// Defines if the table allow multi-selection
	// Can toogle on/off with the users selected rows. But if initialized on
	// it will never change
	private final boolean multiSelector;

	// Define if the table has editable content
	private final boolean editableContent;

//	// Define if the table can open the form details in a split window
//	private final boolean formDetailsInSplitView;
//
//	// Button to control selectable items
//	private boolean onEdition = false;
//
//	private EntityModel<T> selected = null;
	
	private GridListDataView<EntityModel<T>> dataView;
	
	public Table(Class<EntityModel<T>> clazz, String tableName, ITable<T> tableView) {

		this(clazz, tableName, tableView, false, true, true, true, true);

	}

	public Table(Class<EntityModel<T>> clazz, String tableName, ITable<T> tableView, boolean editable) {

		this(clazz, tableName, tableView, false, true, true, editable, true);

	}

	public Table(Class<EntityModel<T>> clazz, String tableName, ITable<T> tableView, boolean initializeColumns,
			boolean allowTableColumnsResize, boolean allowTableColumnsReorder, boolean editable,
			boolean formDetailsInSplitView) {
						
		this.tableView = tableView;

		this.header = new HeaderTable(tableName, editable);
		buildTableLayout();

		// Header and body style
		header.setPadding(false);

		// sets properties
		this.allowTableColumnsResize = allowTableColumnsResize;
		this.allowTableColumnsReorder = allowTableColumnsReorder;
		this.editableContent = editable;
//		this.formDetailsInSplitView = formDetailsInSplitView;
		this.multiSelector = false;
		
		// Build UI elements
		this.grid = new Grid<EntityModel<T>>(clazz, initializeColumns);
		

		// Configuration of the component
		this.setSizeFull();
		this.setHeight("100%");
		this.setPadding(false);
		
	}
	
	public String getSimpleTextValue() {
		return header.getSimpleText().getValue().trim();
	}

	/**
	 * Method that builds the basics of the splitLayout
	 */
	private void buildTableLayout() {

		tableLayout = new HorizontalLayout();
		tableLayout.setSizeFull();

	}

	/**
	 * Method that ends the table initialization, it is not part of the constructor
	 * because the tableView will not be fully builded by that point
	 */
	public void initialize() {

		UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
		    // This is your own method that you may do something with the screen width.
		    // Note that this method runs asynchronously
		   setTableRenderer(details.getWindowInnerWidth());
		});

		UI.getCurrent().getPage().addBrowserWindowResizeListener(resize -> {
			   setTableRenderer(resize.getWidth());
		});
		
		setContent();
		tableView.setSimpleSearch(this.dataView);


		setListeners();

//		toogleDetailsForm(false, ViewType.VIEW);
		// adds the header and the gri
		add(header, tableLayout);

	}
	
	private void setTableRenderer(int width) {
		
		grid.removeAllColumns();

		if (width > 600) {
			tableView.addTableColumns(grid);
			
		} else {
			tableView.addPhoneTableColumns(grid);
			
		}

		setTableProperties();
		setVisibilityContextMenu();
		
		grid.getColumns().forEach(column -> {
			column.setResizable(allowTableColumnsResize);

		});
		
	}
	

	private void setListeners() {

		// Selection listener. Show the number of selected items on screen
		grid.addSelectionListener(selection -> {

			int selectedElements = selection.getAllSelectedItems().size();

//			if (selectedElements > 0) {
//				selected = selection.getFirstSelectedItem().orElseGet(null);
//			} else {
//				selected = null;
//			}
			header.setItemSelected(selectedElements);
						
////			// If one element selected and multiselector off, toogles detailForm ON
//			if (!multiSelector && selectedElements == 1) {
//				ViewType formStatus = (editableContent) ? ViewType.EDIT : ViewType.VIEW;
//				toogleDetailsForm(true, formStatus);
//			} else {
//				toogleDetailsForm(false, ViewType.NEW);
//			}

			tableView.executeOnItemSelected(selection.getAllSelectedItems());

		});

//		// Add listener to the + button on the header
//		header.getAddData().addClickListener(click -> {
//			grid.deselectAll();
//			toogleDetailsForm(true, ViewType.NEW);
//		});
		
		header.getSimpleSearch().addClickListener(click -> {
			dataView.refreshAll();
		});

	}

	private void setTableProperties() {

		grid.setSizeFull();
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		// If multiple selection is enabled and also the table has editable content,
		// enable
		// multiselection
		if (multiSelector && editableContent) {
			var gridSelection = ((GridMultiSelectionModel<?>) grid.setSelectionMode(Grid.SelectionMode.MULTI));
			gridSelection.setSelectionColumnFrozen(true);
		}

		grid.setColumnReorderingAllowed(allowTableColumnsReorder);

	}
	
	public Button getAddButton() {
		return header.getAddData();
	}

	private void setVisibilityContextMenu() {

		if (columnToggleContextMenu == null) {
			// Creates a context menu
			this.columnToggleContextMenu = new ColumnToggleContextMenu<>(
					header.getShowColumnsButton());
		} else {
			columnToggleContextMenu.removeAll();
		}
		
		// If the content is editable, add multiselector to the content toogles
		if (editableContent && multiSelector) {
			columnToggleContextMenu.addSelectableToggleItem("Seleccion", grid, multiSelector);

			columnToggleContextMenu.add(new Hr());
		}
		
		List<Column<EntityModel<T>>> columns = this.grid.getColumns().stream().sorted(Comparator.comparing(Column::getHeaderText))
				.collect(Collectors.toList());

		columns.forEach(column -> {
			columnToggleContextMenu.addColumnToggleItem(column.getHeaderText(), column);
		});

	}

	public static Icon createStatusIconFromBoolean(boolean status) {

		Icon icon;
		if (status) {
			icon = LumoIcon.CHECKMARK.create();
			icon.getElement().getThemeList().add("badge success");
		} else {
			icon = LumoIcon.CROSS.create();
			icon.getElement().getThemeList().add("badge error");
		}

		return icon;
	}

	protected void setContent() {

		this.dataView = tableView.setContent();
		tableLayout.add(grid);
		
	}

	private static class ColumnToggleContextMenu<T> extends ContextMenu {

		private static final long serialVersionUID = 8375978616960033521L;

		public ColumnToggleContextMenu(Component target) {
			super(target);
			setOpenOnClick(true);
		}

		/*
		 * Method that checks the context menu field to hide/show column
		 */
		void addColumnToggleItem(String label, Grid.Column<EntityModel<T>> column) {
			MenuItem menuItem = this.addItem(label, e -> {
				column.setVisible(e.getSource().isChecked());
			});
			menuItem.setCheckable(true);
			menuItem.setChecked(column.isVisible());
			menuItem.setKeepOpen(true);
		}

		/*
		 * Method that checks the selector context menu status to enable/disable
		 * multiselection
		 */
		MenuItem addSelectableToggleItem(String label, Grid<?> grid, boolean selectorEnabled) {

			// Eliminate all selected
			grid.deselectAll();

			MenuItem menuItem = this.addItem(label, e -> {
				// Enable/disable multiselection based on the menu item selected
				if (e.getSource().isChecked()) {
					var gridSelection = ((GridMultiSelectionModel<?>) grid.setSelectionMode(Grid.SelectionMode.MULTI));
					gridSelection.setSelectionColumnFrozen(true);

				} else {
					grid.setSelectionMode(Grid.SelectionMode.NONE);
				}
			});
			menuItem.setCheckable(true);
			menuItem.setChecked(selectorEnabled);
			menuItem.setKeepOpen(true);

			return menuItem;
		}

	}

}
