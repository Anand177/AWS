/**
 * 
 */
package anand.learning.aws.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author anand
 *
 */
public class AWSCredReader {

	/**
	 * @param args
	 */
	public static Properties getCredentials() {

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("C:\\Users\\anand\\Documents\\AWS\\AnandVCredentials.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return prop;
	}
	
	public static Map<String, String> getCredentialsMap() {
		
		Properties prop = getCredentials();
		Map<String, String> credMap= new HashMap<String, String>();
		
		credMap.put("AccesskeyID", prop.getProperty("AccesskeyID"));
		credMap.put("Secretaccesskey", prop.getProperty("Secretaccesskey"));
		return credMap;
	}

}
