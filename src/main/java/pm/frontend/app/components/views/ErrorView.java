package pm.frontend.app.components.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.JustifyContent;
import com.vaadin.flow.dom.Style.TextAlign;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoIcon;

import pm.frontend.app.components.customization.ThemeConfig;

@PageTitle("Pagina de error")
@Route("/err-view")
@CssImport("./styles/error-layout.css")
public class ErrorView extends VerticalLayout {
    
    private static final long serialVersionUID = -7423367689526453140L;
    
	private ThemeConfig themeConfig = ThemeConfig.getCurrent();

	private Button button = new Button("Recargar");
    
	public ErrorView() {
		
		setSizeFull();
		setPadding(false);
		
		getStyle().setDisplay(Display.FLEX);
		getStyle().setAlignItems(AlignItems.CENTER);
		getStyle().setJustifyContent(JustifyContent.CENTER);
		
        themeConfig.initializeDarkMode();
		
		button.setPrefixComponent(LumoIcon.RELOAD.create());
        button.addClickListener(clicl -> {
        	UI.getCurrent().getPage().setLocation("/pm-app/login");
        });
        button.getStyle().setWidth("40vw");
        button.getStyle().setMaxWidth("175px");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        StreamResource iconResource = new StreamResource("error-image.svg",
			    () -> getClass().getResourceAsStream("/images/error-image.svg"));
		
		SvgIcon icon = new SvgIcon(iconResource);
        
		icon.getStyle().setWidth("90vw");
		icon.getStyle().setHeight("70vw");
		icon.getStyle().setMaxWidth("700px");
		icon.getStyle().setMaxHeight("544px");
		icon.addClassName("light-color");
		
		Paragraph text = new Paragraph("Ups. Ha ocurrido algo inesperado. Si el problema persiste, contacte con el administrador.");
		text.getStyle().setPadding("0 10px");
		text.getStyle().setTextAlign(TextAlign.CENTER);
		text.getStyle().setMargin("0");
		text.getStyle().setFontSize("1.2em");		
		
        add(
        	icon,
        	text,
        	button
        );
        
    }

}