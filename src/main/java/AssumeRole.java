import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import software.amazon.awssdk.services.sts.StsAsyncClient;

public class AssumeRole {

    public static void main(String args[]) {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIATDVKWZIJ26JTRWMS", "pyVw5r34aTMYb7dQ6q6r2U1Ec3PDDafsXiik+UQy");

        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        AssumeRoleRequest roleRequest = new AssumeRoleRequest()
                .withRoleArn("arn:aws:iam::214032632339:role/s3-access-sts-role")
                .withRoleSessionName("temp-session")
                .withDurationSeconds(3600);

        AssumeRoleResult roleResponse = stsClient.assumeRole(roleRequest);

        System.out.println(roleResponse.getCredentials().getAccessKeyId());
        System.out.println(roleResponse.getCredentials().getSecretAccessKey());
        System.out.println(roleResponse.getCredentials().getSessionToken());

        BasicSessionCredentials awsCredentials = new BasicSessionCredentials(
                roleResponse.getCredentials().getAccessKeyId(),
                roleResponse.getCredentials().getSecretAccessKey(),
                roleResponse.getCredentials().getSessionToken());

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion("ap-south-1")
                .build();
//
        ObjectListing objects = s3Client.listObjects("qa-forum");
        System.out.println("No. of Objects: " + objects.getObjectSummaries().size());

    }

}
