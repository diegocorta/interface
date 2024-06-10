package pm.frontend.app.configuration;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class Credentials {

	private String token;
	private String refreshToken;
	private ZonedDateTime expDate;
	private Long userId;
}
