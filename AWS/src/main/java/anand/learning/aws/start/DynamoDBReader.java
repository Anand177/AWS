/**
 * 
 */
package anand.learning.aws.start;

import anand.learning.aws.bo.UserDynamoDBBO;
import anand.learning.aws.util.DynamoDBUtil;

/**
 * @author anand
 *
 */
public class DynamoDBReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		DynamoDBUtil dynamoDbUtil = new DynamoDBUtil();
		
		//List Tables
//		dynamoDbUtil.listTables();
		
		//Create Table PS
//		dynamoDbUtil.createTable("UserPost");
		
		//Get One Record
//		dynamoDbUtil.readTableWithPK("User", "userId", "anand");

		//Batch Read
//		dynamoDbUtil.getMultipleItems("User", "userId", "avasant5", "pgudb",
//			"UserPost", "userId", "ts", "anand", "1597668998909", "1597669182517");
		
		UserDynamoDBBO user = new UserDynamoDBBO("anand", "Anand", "V", 
			"anand123@gmail.com", "9566263749");
		
		//Put to non Sort Table
//		dynamoDbUtil.putToUserTable("User", user);

		//Put to Sort Table
//		dynamoDbUtil.putToUserTableWithSK("UserPost", user);
		
		//Delete from Table
//		dynamoDbUtil.deleteItem("User", "userId", "pgudb");
		
		//Delete Table
		dynamoDbUtil.deleteTable("UserPost");
		
	}

}
