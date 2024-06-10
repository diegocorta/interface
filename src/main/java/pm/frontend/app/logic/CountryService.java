package pm.frontend.app.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class CountryService {

	// Create a map to store the country name to ISO code mapping
    private Map<String, String> countryNameToISOCodeMap;

    
 // Initialize the map once
    @PostConstruct
    public void postConstruct() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        
        countryNameToISOCodeMap = Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode))
                .collect(Collectors.toMap(
                        locale -> locale.getDisplayCountry(currentLocale),
                        Locale::getCountry
                ));
    }
    
    public List<String> getAllCountries() {
    	return countryNameToISOCodeMap
    			.keySet()
    			.stream()
    			.sorted()
    			.collect(Collectors.toList());
        
    }
    
    public String getISOCode(String localizedCountryName) {
        return countryNameToISOCodeMap.get(localizedCountryName);
    }
    
    public String getLocaleCountry(String isoCountry) {
    	return new Locale("", isoCountry).getDisplayCountry();
    }
    
//    public List<String> getISOOfCountry(String country) {
//        
//    	Locale currentLocale = LocaleContextHolder.getLocale();
//        return Arrays.stream(Locale.getISOCountries())
//                     .map(countryCode -> new Locale("", countryCode))
//                     .map(locale -> locale.getDisplayCountry(currentLocale))
//                     .collect(Collectors.toList());
//        
//    }
	
}
