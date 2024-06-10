package pm.frontend.app.components.exceptions;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import com.vaadin.flow.component.UI;

import es.common.dto.ErrorResponseDto;
import lombok.extern.apachecommons.CommonsLog;
import pm.frontend.app.components.standard.components.AlertBuilder;
import pm.frontend.app.components.standard.components.AlertBuilder.NotificationStatus;

@CommonsLog
public class ExceptionNotification {

    public static void error(Throwable exception) {
		
		log.error(exception.getLocalizedMessage(), exception);
		
		if(UI.getCurrent() != null) {
						
			if (exception instanceof HttpClientErrorException) {
		        
				printErrorMessage((HttpClientErrorException) exception, NotificationStatus.WARNING);
				
			} else if (exception instanceof HttpServerErrorException) {
				
				printErrorMessage((HttpServerErrorException) exception, NotificationStatus.ERROR);
				
			} else {
								
//				AlertBuilder.error("Ha ocurrido un error, si persiste, contacte con el administrador - ".concat(exception.getClass().getName())
//						, null, exception.getLocalizedMessage()).open();
				UI.getCurrent().getPage().setLocation("/pm-app/err-view");

			}
		}
		
    }
    
	private static void printErrorMessage(HttpStatusCodeException httpException, NotificationStatus notificationStatus) {
		
		String header = null;
		String reference = null;
		String message = null;
		
		try {
			ErrorResponseDto error = httpException.getResponseBodyAs(ErrorResponseDto.class);
			
			if (error != null) {
				message = error.getMessage();
			}
			header = "Status: ".concat(
					String.valueOf(httpException.getStatusCode().value()))
					.concat(" - ")
					.concat(error.getException());
			
		} catch (Exception e) {
			log.info("body not convertible to ErrorResponseDto");
			
		}
		
		AlertBuilder.build(notificationStatus, header, reference, message).open();
		
	}

}
