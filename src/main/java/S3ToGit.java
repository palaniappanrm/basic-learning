import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

public class S3ToGit {

    public static void main(String args[]) throws IOException {


        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return (
                        clientHttpResponse.getStatusCode().series() == CLIENT_ERROR
                                || clientHttpResponse.getStatusCode().series() == SERVER_ERROR);
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                System.out.println(clientHttpResponse.getStatusCode());
            }
        };

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler);

        String getUrl = "https://gitlab-ee.us-east-vpc.socure.be/api/v4/projects/166/repository/tree";
        String commitUrl = "https://gitlab-ee.us-east-vpc.socure.be/api/v4/projects/166/repository/commits";

        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", "xqsMsDXyPfVWK9y9KX5Y");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(getUrl, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        List<String> keyPaths = new ArrayList();
        List<GitCommitAction> gitCommitActions = new ArrayList<>();
        getDeleteCommitActions(jsonNode, getUrl, keyPaths, restTemplate, entity, gitCommitActions);

        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        ObjectMetadata meta = new ObjectMetadata();
//        meta.setSSEAlgorithm(SSEAlgorithm.KMS.getAlgorithm());
//        meta.setHeader(Headers.SERVER_SIDE_ENCRYPTION_AWS_KMS_KEYID,"arn:aws:kms:us-east-1:112942558241:key/5724c26b-15ed-4e0d-9833-f1164041c51b");

//        getCreateCommitActions(s3, meta, gitCommitActions, getUrl);

        GitCommit gitCommit = new GitCommit();
        gitCommit.setActions(gitCommitActions);
        String request = objectMapper.writeValueAsString(gitCommit);
        System.out.println(request);
        HttpEntity<String> newEntity = new HttpEntity(request, headers);
        response = restTemplate.exchange(commitUrl, HttpMethod.POST, newEntity, String.class);
        System.out.println(response.getBody());

    }

    private static void getCreateCommitActions(AmazonS3 s3, ObjectMetadata meta, List<GitCommitAction> gitCommitActions, String getUrl) throws IOException {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName("rulecode-config-stage");

        ObjectListing objectListing = s3.listObjects(listObjectsRequest);

        while (true) {
            Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
            while (objIter.hasNext()) {
                String key = objIter.next().getKey();
                S3ObjectInputStream s3is = s3.getObject(new GetObjectRequest("rulecode-config-stage", key)).getObjectContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(s3is, writer, StandardCharsets.UTF_8);
                GitCommitAction gitCommitAction = new GitCommitAction();
                gitCommitAction.setAction("create");
                gitCommitAction.setFile_path(key);
                gitCommitAction.setContent(writer.toString());
                gitCommitActions.add(gitCommitAction);
                s3is.close();
            }

            if (objectListing.isTruncated()) {
                objectListing = s3.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
    }

    private static void getDeleteCommitActions(JsonNode jsonNode, String getUrl, List<String> keyPaths, RestTemplate restTemplate, HttpEntity entity, List<GitCommitAction> gitCommitActions) throws IOException {

        Iterator<JsonNode> jsonNodes = jsonNode.iterator();
        while(jsonNodes.hasNext()) {
            JsonNode childNode = jsonNodes.next();
            keyPaths.add(childNode.get("name").textValue());
            if(childNode.get("type").textValue().equals("tree")){
                ResponseEntity<String> response = restTemplate.exchange(getUrl + "?path=" + keyPaths.stream().collect(Collectors.joining("/")),
                        HttpMethod.GET, entity, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(response.getBody());
                getDeleteCommitActions(node, getUrl, keyPaths, restTemplate, entity, gitCommitActions);
            } else if(childNode.get("type").textValue().equals("blob")){
                GitCommitAction gitCommitAction = new GitCommitAction();
                gitCommitAction.setAction("delete");
                gitCommitAction.setFile_path(keyPaths.stream().collect(Collectors.joining("/")));
                gitCommitActions.add(gitCommitAction);
            }
            keyPaths.remove(keyPaths.size()-1);
        }

    }

}
