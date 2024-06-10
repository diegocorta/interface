package pm.frontend.app.components.standard.components;

import java.awt.Color;

public class ColorUtil {
	// Método para calcular la luminancia de un color hexadecimal
    private static double calculateLuminance(Color color) {
        return (0.2126 * color.getRed()/255) + (0.7152 * color.getGreen()/255) + (0.0722 * color.getBlue()/255);
    }
    
    public static boolean isDarkBackground(Color color) {
    	
    	double luminance = calculateLuminance(color);
    	System.out.println("Luminance: " + luminance);
    	if (luminance > 0.5) {
    		return false;
    	} else {
    		return true;
    	}
    	
    }
}

