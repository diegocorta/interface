package pm.frontend.app.components.standard.buttons;

import java.util.Arrays;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;

@CssImport("./styles/icon.css")
public class MessagesButton extends Button {

    private static final long serialVersionUID = -8379106927486999258L;
    
	private final Element numberOfNotifications;

    public MessagesButton(Icon icon) {
        
    	super(icon);
    	
    	icon.setClassName("normal-icon");
    	numberOfNotifications = new Element("span");
        numberOfNotifications.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setTransform("translate(-55%, -85%)");
        numberOfNotifications.getThemeList().addAll(
                Arrays.asList("badge", "error", "primary", "small", "pill"));
        
        this.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    }
    
    public MessagesButton(Icon icon, int unreadMessages) {
        
    	this(icon);
    	
    	setUnreadMessages(unreadMessages);
    }

    public MessagesButton(SvgIcon inbox, int unreadMessages) {
		
    	super();
    	
    	this.setIcon(inbox);
    	
    	numberOfNotifications = new Element("span");
        numberOfNotifications.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setTransform("translate(-55%, -85%)");
        numberOfNotifications.getThemeList().addAll(
                Arrays.asList("badge", "error", "primary", "small", "pill"));
    
        this.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.getStyle().setMargin("-20px");
        
        // FIXME Side hardcoded, does not seems to work importing from the css file
        if (this.getIcon() != null) {
//        	this.getIcon().addClassName("normal-icon");
			this.getIcon().getStyle().setHeight("30px");  
        }
        
        setUnreadMessages(unreadMessages);
	}

	public void setUnreadMessages(int unread) {
        
    	numberOfNotifications.setText(unread + "");
        if(unread > 0 && numberOfNotifications.getParent() == null) {
            getElement().appendChild(numberOfNotifications);
        } else if(numberOfNotifications.getNode().isAttached()) {
            numberOfNotifications.removeFromParent();
        }
        
    }

}
