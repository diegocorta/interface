package pm.frontend.app.components.standard.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;
import lombok.Setter;
import pm.frontend.app.components.standard.buttons.DeleteButton;
import pm.frontend.app.components.standard.buttons.PrimaryButton;
import pm.frontend.app.components.standard.buttons.SecondaryButton;
import pm.frontend.app.components.standard.buttons.DeleteButton.DeleteButtonType;
import pm.frontend.app.components.standard.buttons.PrimaryButton.PrimaryButtonType;
import pm.frontend.app.components.standard.buttons.SecondaryButton.SecondaryButtonType;

public class EntityLayout extends VerticalLayout {

	private static final long serialVersionUID = 5169728995663015972L;

	private HorizontalLayout header;
	
	private HorizontalLayout buttons;
	
	@Getter
	private PrimaryButton save;
	@Getter
	private DeleteButton delete;
	@Getter
	private SecondaryButton close;
	@Getter
	private SecondaryButton cancel;

	@Getter
	@Setter
	private boolean isEditable;
	
	private VerticalLayout contentLayout;
	
	public EntityLayout(String formName, boolean isEditable) {

		header = new HorizontalLayout();
		H2 text = new H2("Detalle de "+formName.toLowerCase());
		header.getStyle().setPadding("0 20px");
		header.add(text);
		
		this.contentLayout = new VerticalLayout();
		contentLayout.setSizeFull();
		contentLayout.setPadding(false);
		contentLayout.setMargin(false);
		
		save = new PrimaryButton(PrimaryButtonType.SAVE);
		delete = new DeleteButton(DeleteButtonType.DELETE);
		close = new SecondaryButton(SecondaryButtonType.CLOSE);
		cancel = new SecondaryButton(SecondaryButtonType.CANCEL);

		this.isEditable = isEditable;
		
		buttons = buildButtonsLayout();
		
		this.setJustifyContentMode(JustifyContentMode.BETWEEN);
		this.setSizeFull();
		
		Scroller scroller = new Scroller(contentLayout);
		scroller.setSizeFull();
		
		add(header, scroller, buttons);

	}
	
	public EntityLayout(Component content, String formName, boolean isEditable) {

		this(formName, isEditable);
		
		setMainContent(content);

	}

	private HorizontalLayout buildButtonsLayout() {

		HorizontalLayout hl = new HorizontalLayout();

		hl.setAlignItems(FlexComponent.Alignment.CENTER);
		hl.getStyle().setPadding("0px 0px 10px 20px");
		
		if (isEditable) {
			hl.add(
					save,
					delete,
					cancel
			);
			
		} else {
			hl.add(
					close
			);
			
		}

		return hl;

	}
	
	public void setMainContent(Component component) {
		contentLayout.add(component);
	}
	
}
