import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSAsync;
import com.amazonaws.services.kms.AWSKMSAsyncClientBuilder;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sts.StsAsyncClient;
import software.amazon.awssdk.services.sts.StsAsyncClientBuilder;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AssumeRoleAsync {

    public static void main(String args[]) throws ExecutionException, InterruptedException {

//        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIATDVKWZIJ26JTRWMS", "pyVw5r34aTMYb7dQ6q6r2U1Ec3PDDafsXiik+UQy");

//        new AWSStaticCredentialsProvider(AwsBasicCredentials.create("AKIATDVKWZIJ26JTRWMS", "pyVw5r34aTMYb7dQ6q6r2U1Ec3PDDafsXiik"))

        try {
            StsAsyncClient stsAsyncClient = StsAsyncClient.
                    builder().
//                  credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("AKIATDVKWZIJ26JTRWMS", "pyVw5r34aTMYb7dQ6q6r2U1Ec3PDDafsXiik+UQy"))).
                    build();

            CompletableFuture<AssumeRoleResponse> responseCompletableFuture = stsAsyncClient.assumeRole(AssumeRoleRequest.builder()
                    .roleArn("arn:aws:iam::112942558241:role/socure-byok-5902-stage").
                    roleSessionName("temp-session").durationSeconds(3600).build());

            AssumeRoleResponse assumeRoleResponse = responseCompletableFuture.get();

            System.out.println(assumeRoleResponse.credentials().accessKeyId());
            System.out.println(assumeRoleResponse.credentials().accessKeyId());
            System.out.println(assumeRoleResponse.credentials().sessionToken());

            AWSCredentials awsCredentials = new BasicSessionCredentials(assumeRoleResponse.credentials().accessKeyId(),assumeRoleResponse.credentials().secretAccessKey(),
                    assumeRoleResponse.credentials().sessionToken());

            AWSKMSAsync kmsAsync = AWSKMSAsyncClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.US_EAST_1)
                    .build();
            EncryptResult encryptResult = kmsAsync.encrypt(new EncryptRequest()
                    .withKeyId("arn:aws:kms:us-east-1:048536043970:key/b110cba9-042b-42a5-b8cd-c11e7ff9068a")
                    .withPlaintext(ByteBuffer.wrap("test_for_encryption".getBytes())));

            System.out.println(encryptResult.getCiphertextBlob());

        } catch (Exception e) {
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
        }


    }
}
