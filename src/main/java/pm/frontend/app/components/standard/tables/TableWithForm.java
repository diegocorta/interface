package pm.frontend.app.components.standard.tables;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.theme.lumo.LumoIcon;

import lombok.Getter;
import pm.frontend.app.components.standard.components.AlertBuilder;
import pm.frontend.app.components.standard.components.ViewType;
import pm.frontend.app.components.standard.forms.EntityAction;
import pm.frontend.app.components.standard.forms.Form;
import pm.frontend.app.components.standard.forms.IForm;
import pm.frontend.app.components.standard.forms.EntityAction.Action;

@CssImport("./styles/table.css")
public class TableWithForm<T, F extends FormLayout & IForm<T>> extends VerticalLayout {

	private static final long serialVersionUID = 4016941117499111788L;

	// Table interface to inherit behaveour
	private ITableWithForm<T, F> tableView;

	// layouts
	private HeaderTable header;
	private SplitLayout bodySplitLayout;


	// Elements
	@Getter
	private Grid<EntityModel<T>> grid;
	@Getter
	private Form<T, F> formDetails;
	
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

	// Define if the table can open the form details in a split window
	private final boolean formDetailsInSplitView;

	// Button to control selectable items
	private boolean onEdition = false;

	private EntityModel<T> selected = null;
	
	private GridListDataView<EntityModel<T>> dataView;
	
	public TableWithForm(Class<EntityModel<T>> clazz, String tableName, ITableWithForm<T, F> tableView) {

		this(clazz, tableName, tableView, false, true, true, true, true);

	}

	public TableWithForm(Class<EntityModel<T>> clazz, String tableName, ITableWithForm<T, F> tableView, boolean editable) {

		this(clazz, tableName, tableView, false, true, true, editable, true);

	}

	public TableWithForm(Class<EntityModel<T>> clazz, String tableName, ITableWithForm<T, F> tableView, boolean initializeColumns,
			boolean allowTableColumnsResize, boolean allowTableColumnsReorder, boolean editable,
			boolean formDetailsInSplitView) {
						
		this.tableView = tableView;

		this.header = new HeaderTable(tableName, editable);
		buildSplitLayout();

		// Header and body style
		header.setPadding(false);

		// sets properties
		this.allowTableColumnsResize = allowTableColumnsResize;
		this.allowTableColumnsReorder = allowTableColumnsReorder;
		this.editableContent = editable;
		this.formDetailsInSplitView = formDetailsInSplitView;
		this.multiSelector = false;
		
		// Build UI elements
		this.grid = new Grid<EntityModel<T>>(clazz, initializeColumns);
		

		// Configuration of the component
		this.setSizeFull();
		this.setHeight("100%");
		this.setPadding(false);
		
	}
	
	public String getSimpleTextValue() {
		return header.getSimpleText().getValue().toLowerCase().trim();
	}

	/**
	 * Method that builds the basics of the splitLayout
	 */
	private void buildSplitLayout() {

		bodySplitLayout = new SplitLayout(Orientation.HORIZONTAL);
		bodySplitLayout.setSizeFull();

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

		// Build the form detail
		buildFormDetails();

		setListeners();

		toogleDetailsForm(false, ViewType.VIEW);
		// adds the header and the gri
		add(header, bodySplitLayout);

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

	private void buildFormDetails() {
		formDetails = tableView.buildFormDetails();

		formDetails.getCancel().addClickListener(click -> {
			toogleDetailsForm(false, ViewType.VIEW);
		});
		formDetails.getClose().addClickListener(click -> {
			toogleDetailsForm(false, ViewType.VIEW);
		});
		formDetails.getSave().addClickListener(click -> {
			EntityAction<T> entitySaved = formDetails.saveEntity();
			
			if (entitySaved != null && entitySaved.getEntity() != null) {
				
				if (entitySaved.getAction().equals(Action.CREATE)) {
					
					if (dataView.getItemCount() > 0) {
						dataView.addItemBefore(entitySaved.getEntity(), dataView.getItem(0));
					} else {
						dataView.addItem(entitySaved.getEntity());
					}
					
					toogleDetailsForm(false, ViewType.NEW);
					
					AlertBuilder.success(null, entitySaved.getClassDescriptor(), "creado/a correctamente").open();

				} else if (entitySaved.getAction().equals(Action.UPDATE)) {
					
					var prevItem = dataView.getPreviousItem(entitySaved.getPrevious()).orElse(null);
					
					dataView.removeItem(entitySaved.getPrevious());
					
					if (prevItem == null) {
						if (dataView.getItemCount() > 0) {
							dataView.addItemBefore(entitySaved.getEntity(), dataView.getItem(0));
						} else {
							dataView.addItem(entitySaved.getEntity());
						}
					} else {
						dataView.addItemAfter(entitySaved.getEntity(), prevItem);
					}
					
					toogleDetailsForm(false, ViewType.NEW);
					
					AlertBuilder.success(null, entitySaved.getClassDescriptor(), "actualizado/a correctamente").open();
				
				} else {
					toogleDetailsForm(false, ViewType.NEW);
				}
				
			} else {
				toogleDetailsForm(false, ViewType.NEW);
			}
			
		});
		formDetails.getDelete().addClickListener(click -> {
			
			ConfirmDialog dialog = new ConfirmDialog();
			
			dialog.setHeader("Advertencia, borrado de datos");
			dialog.setText(new Div(
					new Paragraph("Esta accion no se puede deshacer, ¿Desea continuar?")
					));

			dialog.setRejectable(true);
			dialog.setRejectButtonTheme("tertiary");
			dialog.setRejectText("Cancelar");

			
			dialog.setConfirmText("Confirmar");
			dialog.setConfirmButtonTheme("error primary");
			dialog.addConfirmListener(event -> {
				
				EntityAction<T> entityDeleted =  formDetails.deleteEntity();
				
				if (entityDeleted != null && entityDeleted.getEntity() != null && entityDeleted.getAction().equals(Action.DELETE)) {
					dataView.removeItem(entityDeleted.getEntity());
					
					toogleDetailsForm(false, ViewType.NEW);

					AlertBuilder.success(null, entityDeleted.getClassDescriptor(), "eliminado/a correctamente").open();
				}
				
			});

			dialog.open();
		});
		
		bodySplitLayout.addToSecondary(formDetails);
		formDetails.setMinWidth("340px");
		grid.setMinWidth("360px");
	}

	private void setListeners() {

		// Selection listener. Show the number of selected items on screen
		grid.addSelectionListener(selection -> {

			int selectedElements = selection.getAllSelectedItems().size();

			if (selectedElements > 0) {
				selected = selection.getFirstSelectedItem().orElseGet(null);
			} else {
				selected = null;
			}
			header.setItemSelected(selectedElements);

//			// If one element selected and multiselector off, toogles detailForm ON
			if (!multiSelector && selectedElements == 1) {
				ViewType formStatus = (editableContent) ? ViewType.EDIT : ViewType.VIEW;
				toogleDetailsForm(true, formStatus);
			} else {
				toogleDetailsForm(false, ViewType.NEW);
			}

		});

		// Add listener to the + button on the header
		header.getAddData().addClickListener(click -> {
			grid.deselectAll();
			toogleDetailsForm(true, ViewType.NEW);
		});
		
		header.getDeleteData().addClickListener(click -> {
			formDetails.getDelete().click();
		});
		
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

	private void toogleDetailsForm(boolean open, ViewType formStatus) {
		
		if (open) {
			
			if (formDetailsInSplitView) {
				
				formDetails.setVisible(true);
				formDetails.loadData(formStatus, selected);

			} else {

				formDetails.setVisible(true);
				grid.setVisible(false);

			}

			grid.addClassName("hiddable-on-phone");
			header.addClassName("hiddable-on-phone");
			
			bodySplitLayout.addThemeName("hiddable-split-on-phone");
			bodySplitLayout.removeThemeName("hide-split");
			onEdition = true;
			header.isOnEdition(onEdition);

		} else if (!open) {
			
			formDetails.setVisible(false);
			grid.setVisible(true);
			grid.removeClassName("hiddable-on-phone");
			header.removeClassName("hiddable-on-phone");
			bodySplitLayout.removeThemeName("hiddable-split-on-phone");
			bodySplitLayout.addThemeName("hide-split");
			grid.deselectAll();
			
			onEdition = false;
			header.isOnEdition(onEdition);
		}

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
		bodySplitLayout.addToPrimary(grid);

	}
	
	public GridListDataView<EntityModel<T>> setTableContent(Collection<EntityModel<T>> entities) {

		var entitiesMutable = entities.stream().collect(Collectors.toList());
		
		return grid.setItems(entitiesMutable);

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
