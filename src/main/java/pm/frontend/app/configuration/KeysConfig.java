package pm.frontend.app.configuration;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KeysConfig {
	
	@Bean
	public PublicKey publicKey() {

		try {

			String key = getFile("keys/public-key.pem");

			KeyFactory kf = KeyFactory.getInstance("RSA");


			key = key.replaceAll("\\n", "")
					.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "");

			X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
			return (RSAPublicKey) kf.generatePublic(keySpecX509);

		} catch (Exception e) {

			throw new RuntimeException(e);
		}

	}
	
	public String getFile(String path) {
		
		String key;
		
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                throw new FileNotFoundException("key file not found with path: " + path);
            }

            key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            inputStream.close();
            
		}  catch (Exception e) {

			throw new RuntimeException(e);
		}
		
		return key; 
	}
	
}