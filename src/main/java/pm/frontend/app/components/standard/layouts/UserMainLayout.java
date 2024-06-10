package pm.frontend.app.components.standard.layouts;

import java.io.ByteArrayInputStream;
import java.time.Instant;

import org.apache.commons.text.WordUtils;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.JustifyContent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import pm.frontend.app.components.views.AppView;
import pm.frontend.app.logic.security.AuthenticationService;

@PageTitle("User main")
@Route(value = "/user-main", layout = AppView.class)
@CssImport("./styles/user-grid.css")
public class UserMainLayout extends Div {

	private static final long serialVersionUID = 3722405810464578697L;

	private Avatar avatar;
	private H4 text;
	private MessageList messageList;
	
	private Div main;
	
	public UserMainLayout(AuthenticationService authenticationService) {
		
		setClassName("gridcontainer-user");		
		setSizeFull();
		getStyle().setMargin("0");
		getStyle().setPadding("0");
		
		main = new Div();
		main.addClassName("container-user");
		main.setSizeFull();
		main.getStyle().setPadding("0");
		main.getStyle().setMargin("0");
				
		Div divAvatar = new Div();
		divAvatar.setSizeFull();
		divAvatar.getStyle().setDisplay(Display.FLEX);
		divAvatar.getStyle().setJustifyContent(JustifyContent.CENTER);
		divAvatar.getStyle().setAlignItems(AlignItems.CENTER);		
		
		var employeeDto = authenticationService.getVaadinSessionUser();

		String fullString = WordUtils.capitalize(employeeDto.getContent().getFullName());
		
		avatar = new Avatar();
		avatar.setName(fullString);
		avatar.setClassName("data");
		avatar.setClassName("data1");
		
		if (employeeDto.getContent().getImage() != null) {
			// Create a StreamResource from the byte array
	        StreamResource resource = new StreamResource("avatar.png", () -> {
	            return new ByteArrayInputStream(employeeDto.getContent().getImage());
	        });
	
	        // Set the image as the avatar image
	        avatar.setImageResource(resource);
		}
		
		divAvatar.add(avatar);
		
		text = new H4("Bienvenido/a, ".concat(fullString));
		text.setClassName("data");
		text.setClassName("data2");
		
		messageList = new MessageList();
		messageList.setClassName("data");
		messageList.setClassName("data3");
		messageList.getStyle().setMargin("0");
		messageList.getStyle().setPadding("0");
		
		MessageListItem message1 = new MessageListItem(
		        "3 empleados no ficharon ayer",
		        Instant.now(), "Notificacion");
		
		MessageListItem message2 = new MessageListItem(
		        "Va a caducar el DNI de un empleado",
		        Instant.now(), "Notificacion");
		
		MessageListItem message3 = new MessageListItem(
		        "Julian te ha dejado un mensaje",
		        Instant.now(), "Mensaje");
		
		
		
		messageList.setItems(message1, message2, message3);
		
		main.add(divAvatar, text, messageList);
		
		add(main);
		
	}
}
