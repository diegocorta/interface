package pm.frontend.app.components.standard.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;

import lombok.Getter;
import pm.frontend.app.components.standard.components.AlertBuilder;
import pm.frontend.app.components.standard.components.ViewType;

public class SingleLineDetails<T> extends Details {

	private static final long serialVersionUID = 6684666805372942119L;

	private String detailName;
	
	@Getter
	MultiSelectComboBox<EntityModel<T>> comboBox;
	
	private Button addData;
	
	private HorizontalLayout search;
	
	private IBindeableDetails<T> details;
	
	private List<DisplayComponent> displayComponents = new ArrayList<>();
			
	private ViewType view;
	
	public SingleLineDetails(String text, ViewType view, IBindeableDetails<T> details) {
		super(text);
				
		this.view = view;
		
		detailName = text;
		this.details = details;
		this.addData = new Button(LumoIcon.PLUS.create());
		this.addData.setEnabled(false);
		
		
		comboBox = new MultiSelectComboBox<>();
		comboBox.setPlaceholder("Buscar");
		comboBox.setWidthFull();
		
		search = new HorizontalLayout();
		search.setAlignItems(FlexComponent.Alignment.CENTER);
		search.setSizeFull();
		search.getStyle().setPaddingLeft("30px");
		search.getStyle().setMarginBottom("10px");
		search.add(comboBox);
		
		if (view.equals(ViewType.EDIT)) {
			search.add(comboBox);
			search.add(addData);
		} else {
			search.remove(comboBox);
			search.remove(addData);
		}
		
		add(search);
		
		comboBox.addSelectionListener(selection -> {
			if(selection.getFirstSelectedItem().isPresent()) {
				addData.setEnabled(true);
			} else {
				addData.setEnabled(false);
			}
		});
		
		addData.addClickListener(click -> {
			
			if (IBindeableAndEditableDetails.class.isAssignableFrom(details.getClass())) {
				
				IBindeableAndEditableDetails<T> ibed = (IBindeableAndEditableDetails<T>) details;
				
				ibed.addElements(comboBox.getSelectedItems());
			}

			AlertBuilder.success(null, null, "Se han actualizado los datos correctamente").open();
			
			reset();
			
		});
		
		addOpenedChangeListener(open -> {
			if (open.isOpened()) {
				details.reset();
			}
		});
	}
	
	public void setContent(Collection<EntityModel<T>> content) {
		
		List<EntityModel<T>> list = content
				.stream()
				.collect(Collectors.toList());
		
		setSummaryText(detailName.concat(" ("+content.size()+")"));
		this.removeAll();
		
		for (var oneContent : list) {
			
			var displayComponent = new DisplayComponent(oneContent, details.getPrincipal(oneContent));
			
			this.displayComponents.add(displayComponent);
			add(displayComponent);
		}
	
	}
	
	@Override
	public void removeAll() {
		super.removeAll();
		add(search);
	}
	
	public void reset() {
		details.reset();
	}
	
	
	private class DisplayComponent extends HorizontalLayout {
		
		private static final long serialVersionUID = -7914109607557132453L;
		
		private Span text;
//		private Button button = new Button(LumoIcon.SEARCH.create());
		private Button delete = new Button(LumoIcon.MINUS.create());
		
//		private String selfLink = "";
		
		public DisplayComponent(EntityModel<T> content, String message) {
			
//			Link link = content.getLink(AbstractCommonDto.SELF_REF).orElse(null);
//			
//			if (link != null) {
////				selfLink = link.getHref();
//			} else {
////				button.setVisible(false);
//			}
						
			text = new Span(message);
			text.setWidthFull();
			
			setAlignItems(FlexComponent.Alignment.CENTER);
			setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
			setSizeFull();
			getStyle().setPaddingLeft("30px");
//			button.addClickListener(click -> {
//				AlertBuilder.success(null, null, selfLink).open();
//			});
			
			delete.addClickListener(click -> {
					
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
					
					if (IBindeableAndEditableDetails.class.isAssignableFrom(details.getClass())) {
						
						IBindeableAndEditableDetails<T> ibed = (IBindeableAndEditableDetails<T>) details;
						
						ibed.removeElements(Set.of(content));
					}

					AlertBuilder.success(null, null, "Se han eliminado los datos correctamente").open();
					
					reset();					
					
				});

				dialog.open();
			});
			
			this.setSizeFull();			
			
			add(text);
			
			if (view.equals(ViewType.EDIT)) {
				add(delete);
			}
			
		}
		
//		public String getContent() {
//			return text.getText();
//		}
		
	}
	
}
