/**
 * 
 */
package anand.learning.aws.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;

import anand.learning.aws.common.AWSCredReader;

/**
 * @author anand
 *
 */
public class RDSConnectionUtil {
	
	private static final String KEY_STORE_TYPE = "JKS";
	private static final String DEFAULT_KEY_STORE_PASSWORD = "changeit";
	
	private static final String REGION_NAME = "us-east-2";
	private static final String SSL_CERTIFICATE = "rds-ca-2019-us-east-2.pem";
	
	
	public static void main(String[] args) {
		
		try {
			Connection conn = getDBConnectionUsingIam(
				"mydb.ci7tmv98eevs.us-east-2.rds.amazonaws.com", 3306, "admin");
            if (conn != null)
                System.out.println("Connected to the database!");
            else
                System.err.println("Failed to make connection!");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("show databases");
            
            while(rs.next()) {
            	System.out.println(rs.getString(1));
            }
            
        } catch (SQLException e) {
        	e.printStackTrace();
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection getDBConnectionUsingIam(String hostName, int portNo, 
		String dbUser)	throws Exception {

		String jdbcUrl = "jdbc:mysql://" + hostName + ":" + portNo;
		
		System.setProperty("javax.net.ssl.trustStore", createKeyStoreFile());
        System.setProperty("javax.net.ssl.trustStoreType", KEY_STORE_TYPE);
        System.setProperty("javax.net.ssl.trustStorePassword", DEFAULT_KEY_STORE_PASSWORD);
        return DriverManager.getConnection(jdbcUrl, 
        	setMySqlConnectionProperties(hostName, portNo, dbUser));
    }
	
	
	private static Properties setMySqlConnectionProperties(String hostName, int portNo,
			String dbUser) {
		
		Properties mysqlConnectionProperties = new Properties();
		mysqlConnectionProperties.setProperty("verifyServerCertificate","true");
		mysqlConnectionProperties.setProperty("useSSL", "true");
		mysqlConnectionProperties.setProperty("user", dbUser);
		mysqlConnectionProperties.setProperty("password",generateAuthToken(hostName, 
			portNo, dbUser));
		return mysqlConnectionProperties;
    
	}
	
	
	private static String generateAuthToken(String hostName, int portNo, 
		String dbUser) {
		
		Map<String, String> cred = AWSCredReader.getCredentialsMap();
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(cred.get("AccesskeyID"), 
				cred.get("Secretaccesskey"));
		RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
			.credentials(new AWSStaticCredentialsProvider(awsCredentials))
			.region(REGION_NAME).build();
		return generator.getAuthToken(GetIamAuthTokenRequest.builder()
			.hostname(hostName).port(portNo).userName(dbUser).build());
		
	}
	
	
	private static String createKeyStoreFile() throws Exception {
		
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		URL url = new File("C:\\Users\\anand\\Documents\\AWS\\" + SSL_CERTIFICATE)
			.toURI().toURL();
		if (url == null) 
			throw new Exception("NULL URL");
		
		InputStream certInputStream = url.openStream();
		return ((X509Certificate) certFactory.generateCertificate(certInputStream)).toString();
        
		
	}

}
