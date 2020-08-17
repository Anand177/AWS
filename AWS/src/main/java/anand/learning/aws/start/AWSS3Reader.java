/**
 * 
 */
package anand.learning.aws.start;

import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import anand.learning.aws.common.AWSCredReader;

/**
 * @author anand
 *
 */
public class AWSS3Reader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

/*		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
			.build();  */
		
		Map<String, String> cred = AWSCredReader.getCredentialsMap();
				
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(cred.get("AccesskeyID"), 
				cred.get("Secretaccesskey"));
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		
		String bucketName = "anandsbkt";
		if(s3.doesBucketExistV2(bucketName)) {
			System.err.println("Bucket Already Exists");
		} else
			s3.createBucket(bucketName);
		
		for(Bucket bucket : s3.listBuckets()) {
			System.out.println("Bucket Name  -> " + bucket.getName());
			System.out.println("Bucket Owner -> " + bucket.getOwner());
		}

	}

}
