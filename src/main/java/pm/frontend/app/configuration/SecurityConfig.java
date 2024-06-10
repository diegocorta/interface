package pm.frontend.app.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends VaadinWebSecurity {
	
	private @Value("${allowed.origins}")List<String> corsOrigins;
	
//	@Autowired
//	private RequestFilter jwtTokenFilter;
	
//	@Autowired
//	private ObjectMapper objectMapper;
	
	@Bean("VaadinSecurityFilterChainBean")
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		var data = http
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// Cross Site Request Forgery. Is used on session based environments
				.csrf(csrf -> csrf.disable())
//				// Cross-Origin Resource Sharing
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.invalidateHttpSession(true)
						.deleteCookies("token", "refresh", "JSESSIONID")
						.logoutSuccessUrl("/login"))
				.authorizeHttpRequests(authorize -> authorize
						.anyRequest().permitAll()
						);
				         
		return data.build();

	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		configuration.setAllowedOrigins(corsOrigins);
		configuration.addAllowedHeader("*");
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
	
}
