package pm.frontend.app.components.customization;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

import lombok.Getter;
import pm.frontend.app.components.standard.buttons.IToogleDarkObserver;

public class ThemeConfig {
	
    private static final String THEME_CONFIG_ATTRIBUTE = "themeConfig";
    
	@Getter
    private boolean darkMode = false;
    
    private List<IToogleDarkObserver> observers = new ArrayList<>();
    
    private ThemeConfig() {
        // Private constructor to prevent instantiation
    }
    
    public static ThemeConfig getCurrent() {
        VaadinSession session = VaadinSession.getCurrent();
        ThemeConfig themeConfig = (ThemeConfig) session.getAttribute(THEME_CONFIG_ATTRIBUTE);
        
        if (themeConfig == null) {
            themeConfig = new ThemeConfig();
            session.setAttribute(THEME_CONFIG_ATTRIBUTE, themeConfig);
        }
        
        return themeConfig;
    }
    
    public void addObserver(IToogleDarkObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(IToogleDarkObserver observer) {
        observers.remove(observer);
    }
    
    /*
     * Method that toggles light and dark mode
     */
    public synchronized void toggleToDarkLightMode() {
        UI.getCurrent().access(() -> {
        	        	
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            setDarkMode(!themeList.contains(Lumo.DARK));
            
            UI.getCurrent().push();
        });
    }
    
    /**
     * Method that sets dark mode as received by the parameter
     * 
     * @param darkMode
     */
    private void setDarkMode(boolean darkMode) {
        
    	UI.getCurrent().access(() -> {
    		
	        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
	        // Add or remove the dark mode from the themeList
	        if (darkMode) {
	            themeList.add(Lumo.DARK);
	        } else {
	            themeList.remove(Lumo.DARK);
	        }
	        this.darkMode = darkMode;
	        
	        // Notify observers
	        for (IToogleDarkObserver observer : observers) {
	            observer.toogleDarkLightMode();
	        }
	        
	        // Update the localStorage information of the dark mode, so that it can be remembered
	        WebStorage.setItem("darkMode", String.valueOf(darkMode));
	        UI.getCurrent().push();
	        
        });
        
    }

    /**
     * Method that sets the initial value to the application
     */
    public void initializeDarkMode() {
        // Receive from the browser localStorage the value "darkMode".
        // Use that value to set the theme accordingly
        WebStorage.getItem("darkMode", darkMode -> {
            if (StringUtils.hasText(darkMode)) {
            	boolean darkModeBool = Boolean.valueOf(darkMode);
            	setDarkMode(darkModeBool);
            }
        });
    }
    
//	public static boolean darkMode = false;
//	
//	private static List<IToogleDarkObserver> observers = new ArrayList<>();
//	
//	/*
//	 * Method that toogles light and dark mode
//	 */
//	public static void toogleToDarkLightMode() {
//        	
//    	ThemeList themeList = UI.getCurrent().getElement().getThemeList();
//		
//		setDarkMode(!themeList.contains(Lumo.DARK));
//
//	}
//	
//	public static void addObserver(IToogleDarkObserver observer) {
//		observers.add(observer);
//	}
//	
//	public static void removeObserver(IToogleDarkObserver observer) {
//		observers.remove(observer);
//	}
//	
//	/**
//	 * Method that set dark mode as received by the parameter
//	 * 
//	 * @param darkMode
//	 */
//	private static void setDarkMode(boolean darkMode) {
//        	
//    	ThemeList themeList = UI.getCurrent().getElement().getThemeList();
//
//    	UI.getCurrent().access(() -> {
//    		// Add or removes the dark mode from the themeList
//    		if (darkMode) {
//    			themeList.add(Lumo.DARK);
//    			ThemeConfig.darkMode = true;
//    		} else {
//    			themeList.remove(Lumo.DARK);
//    			ThemeConfig.darkMode = false;
//    			
//    		}
//    		
//    		for(IToogleDarkObserver observer : observers) {
//    			observer.toogleDarkLightMode();
//    		}
//    		
//    		// Updates the localStorage information of the dark mode, so that can be remembered
//    		WebStorage.setItem("darkMode", String.valueOf(darkMode));
//    	});
//
//	}
//
//	/**
//	 * Method that sets the initial value to the application
//	 */
//	public static void initializeDarkMode() {
//
//		// Receive from the browser localStorage the value "darkMode".
//		// Use that value to set the theme accordingly
//		WebStorage.getItem("darkMode", darkMode -> {
//			if (StringUtils.hasText(darkMode)) {
//				setDarkMode(Boolean.valueOf(darkMode));
//			}
//		});
//
//	}
	
}
