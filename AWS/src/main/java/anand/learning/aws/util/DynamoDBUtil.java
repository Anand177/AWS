/**
 * 
 */
package anand.learning.aws.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import anand.learning.aws.bo.UserDynamoDBBO;
import anand.learning.aws.common.AWSCredReader;

/**
 * @author anand
 *
 */
public class DynamoDBUtil {
	
	BasicAWSCredentials awsCreds;
	DynamoDB dynamoDb;
	Table table;
	
	public DynamoDBUtil() {
		
		Map<String, String> cred = AWSCredReader.getCredentialsMap();
		awsCreds = new BasicAWSCredentials(cred.get("AccesskeyID"), 
			cred.get("Secretaccesskey"));
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion(Regions.US_EAST_2)
			.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		dynamoDb = new DynamoDB(client);
		
	}
	
	
	public void listTables() {
		
		Iterator<Table> tableList = dynamoDb.listTables().iterator();
		
		while(tableList.hasNext()) {
			Table table = tableList.next();
			System.out.println(table.getTableName());
		}
	}
	
	
	public void createTable(String tableName) {
		
		List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
		keySchema.add(new KeySchemaElement().withAttributeName("userId")
			.withKeyType(KeyType.HASH)); // For PK
		keySchema.add(new KeySchemaElement().withAttributeName("ts")
				.withKeyType(KeyType.RANGE)); //For Sort Key
		
		List<AttributeDefinition> attrDef= new ArrayList<AttributeDefinition>();
		attrDef.add(new AttributeDefinition().withAttributeName("userId")
			.withAttributeType(ScalarAttributeType.S)); //PK
		attrDef.add(new AttributeDefinition().withAttributeName("ts")
			.withAttributeType(ScalarAttributeType.S));	//SK
		
		CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
			.withKeySchema(keySchema).withAttributeDefinitions(attrDef)
		    .withProvisionedThroughput(new ProvisionedThroughput()
		    .withReadCapacityUnits(5L).withWriteCapacityUnits(6L));
		
		table = dynamoDb.createTable(request);

		try {
			table.waitForActive();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void readTableWithPK(String tableName, String pkName, String pkValue) {
		
		table = dynamoDb.getTable(tableName);
//		GetItemSpec spec = new GetItemSpec().withPrimaryKey(pkName, pkValue);
		
		// With Projection
		GetItemSpec spec = new GetItemSpec().withPrimaryKey(pkName, pkValue)
			.withProjectionExpression("FName, LName").withConsistentRead(true);
		Item outcome = table.getItem(spec);
		
		System.out.println(outcome.toJSONPretty());
		
		for(Entry<String, Object> attribute : outcome.attributes()) {
			System.out.println(attribute.getKey() + " -> " + attribute.getValue());
		}
		
	}
	
	
	public void getMultipleItems(String tableName1, String pkName1, String value11, 
		String value12, String tableName2, String pkName2, String skname2, 
		String value2, String ts1, String ts2) {
		
		TableKeysAndAttributes tkaa1 = new TableKeysAndAttributes(tableName1);
		tkaa1.addHashOnlyPrimaryKeys(pkName1, value11, value12);
		
		TableKeysAndAttributes tkaa2 = new TableKeysAndAttributes(tableName2);
		tkaa2.addHashAndRangePrimaryKeys(pkName2, skname2, value2, ts1, value2, ts2);
		
//		BatchGetItemOutcome outcome = dynamoDb.batchGetItem(tkaa1);
		BatchGetItemOutcome outcome = dynamoDb.batchGetItem(tkaa1, tkaa2);
		
		for (String tbName : outcome.getTableItems().keySet()) {
			System.out.println("Items in table " + tbName);
		    
			List<Item> items = outcome.getTableItems().get(tbName);
		    for (Item item : items) { 
		        System.out.println(item);
		    }
		}
		
	}
	
	
	public void putToUserTable(String tableName, UserDynamoDBBO user) {
		
		table = dynamoDb.getTable(tableName);
		Item item = new Item().withPrimaryKey("userId", user.getUserId())
			.withString("FName", user.getFirstName())
			.withString("LName", user.getLastName())
			.withString("Mobile", user.getMobileNumber())
			.withString("Email", user.getEmail());
		
		PutItemOutcome outcome = table.putItem(item);
			
			if(Objects.nonNull(outcome))
				System.out.println("Success");
			else
				System.out.println("Failure");

	}
	
	
	public void putToUserTableWithSK(String tableName, UserDynamoDBBO user) {
		
		Date currentTime = Calendar.getInstance().getTime();
		String epochTime = Long.toString(currentTime.getTime());
		
		table = dynamoDb.getTable(tableName);
		Item item = new Item().withPrimaryKey("userId", user.getUserId(), "ts", epochTime)
			.withString("FName", user.getFirstName())
			.withString("LName", user.getLastName())
			.withString("Mobile", user.getMobileNumber())
			.withString("Email", user.getEmail())
			.withString("Post", "This post was added @ " 
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(currentTime));
		
		PutItemOutcome outcome = table.putItem(item);
			
			if(Objects.nonNull(outcome))
				System.out.println("Success");
			else
				System.out.println("Failure");

	}
	
	
	public void deleteItem(String tableName, String pkName, String pkValue) {
		
		table = dynamoDb.getTable(tableName);
		DeleteItemOutcome outcome = table.deleteItem(pkName, pkValue);
		
		if(Objects.nonNull(outcome))
			System.out.println("Success");
		else
			System.out.println("Failure");

	}
	
	
	public void deleteTable(String tableName) {
		
		Table table = dynamoDb.getTable(tableName);
		table.delete();
	}

}
