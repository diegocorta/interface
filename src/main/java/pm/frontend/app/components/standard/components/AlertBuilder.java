package pm.frontend.app.components.standard.components;

import org.springframework.util.StringUtils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Class to send custom notifications
 */
@CssImport("./styles/notification.css")
public class AlertBuilder {
	
	public enum NotificationStatus { 
		SUCCESS("--lumo-success-color", VaadinIcon.CHECK_CIRCLE),
		INFO("--lumo-primary-color", VaadinIcon.INFO_CIRCLE_O),
		WARNING("--lumo-warning-color", VaadinIcon.INFO_CIRCLE),
		ERROR("--lumo-error-color", VaadinIcon.CLOSE_CIRCLE);
	
		private NotificationStatus(String color, VaadinIcon icon) {
	        this.color = color;
	        this.icon = icon;
	    }
		
		public final String color;
	    public final VaadinIcon icon;

	}
	
	public static Notification build(NotificationStatus status, String header, String reference, String message) {
		return AlertBuilder.buildNotification(status, header, reference, message, "", "");
	}
	
	public static Notification success(String header, String reference, String message) {
		return AlertBuilder.buildNotification(NotificationStatus.SUCCESS, header, reference, message, "", "");
	}
	
	public static Notification info(String header, String reference, String message) {
		return AlertBuilder.buildNotification(NotificationStatus.INFO, header, reference, message, "", "");
	}
	
	public static Notification warn(String header, String reference, String message) {
		return AlertBuilder.buildNotification(NotificationStatus.WARNING, header, reference, message, "", "");
	}
	
	public static Notification error(String header, String reference, String message) {
		return AlertBuilder.buildNotification(NotificationStatus.ERROR, header, reference, message, "", "");
	}
	
	private static Notification buildNotification(NotificationStatus status, String header, String reference, 
			String message, String url, String urlText) {
		
		Notification notification = new Notification();
		notification.setDuration(5000);
		
		if (StringUtils.hasText(reference)) {
			reference = reference.trim().concat(" ");
		}
		
		if (StringUtils.hasText(urlText)) {
			urlText = urlText.trim().concat(" ");
		}
		
        Icon icon = status.icon.create();
        icon.setColor("var("+status.color+")");
        icon.getStyle().set("min-width", "40px");

        Div uploadSuccessful = new Div(new Text(header));
        uploadSuccessful.getStyle()
                .set("font-weight", "800")
                .setColor("var("+status.color+")");

        if (StringUtils.hasText(reference) || StringUtils.hasText(message) || StringUtils.hasText(urlText)) {
        	uploadSuccessful.getStyle()
		            .setMarginBottom("0.3em");
        }
        
        Span fileName = new Span(reference);
        fileName.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .setColor("var("+status.color+")")
                .set("font-weight", "600");

        Div info = new Div(uploadSuccessful,
                new Div(fileName, new Text(message),
                        new Anchor("#", urlText)));

        info.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .setColor("var(--lumo-secondary-text-color)");

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeBtn.getStyle()
		        .setColor("var("+status.color+")");
        
        var layout = new HorizontalLayout(icon, info,
        		closeBtn);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        
        notification.add(layout);
        notification.addClassName("size");
        
        return notification;
        
	}
	
}