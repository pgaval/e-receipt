package gr.gousios.ereceipt;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

public final class Keys {
	public static final String CLIENT;
	public static final String APPREGKEY;
	
	static {
		
		String file = null;
		
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production)
			file = "googlekeys.properties";
		else
			file = "keys.properties";
		
		Properties p = new Properties();
		try {
			
			p.load(Properties.class.getClassLoader().getResourceAsStream(file));
		} catch (IOException e) {
			Logger.getLogger("app").log(Level.SEVERE, "Cannot load application keys");
		}
		CLIENT = p.getProperty("client");
		APPREGKEY = p.getProperty("appreg");
	}
}
