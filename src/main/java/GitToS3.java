import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

public class GitToS3 {

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

        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        emptyS3Bucket("rulecode-audit-stage", "studio", s3);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler);
        String url = "https://gitlab-ee.us-east-vpc.socure.be/api/v4/projects/64/repository/tree?path=";
        String path = "rulecode-service/rulecode_definitions/stage";

        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", "xqsMsDXyPfVWK9y9KX5Y");

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(url + path, HttpMethod.GET, entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        ObjectMetadata meta = new ObjectMetadata();
        meta.setSSEAlgorithm(SSEAlgorithm.KMS.getAlgorithm());
        meta.setHeader(Headers.SERVER_SIDE_ENCRYPTION_AWS_KMS_KEYID,"arn:aws:kms:us-east-1:112942558241:key/5724c26b-15ed-4e0d-9833-f1164041c51b");

        List<String> keyPaths = new ArrayList();
        synchelper(jsonNode,url, keyPaths, restTemplate, entity, s3, path, meta);

    }

    private static void emptyS3Bucket(String bucketName, String prefix, AmazonS3 s3Client) {

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix);
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
        while (true) {
            Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
            while (objIter.hasNext()) {
                String key = objIter.next().getKey();
                s3Client.deleteObject(bucketName, key);
            }
            // If the bucket contains many objects, the listObjects() call
            // might not return all of the objects in the first listing. Check to
            // see whether the listing was truncated. If so, retrieve the next page of objects
            // and delete them.
            if (objectListing.isTruncated()) {
                objectListing = s3Client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }

        // Delete all object versions (required for versioned buckets).
        VersionListing versionList = s3Client.listVersions(new ListVersionsRequest().withBucketName(bucketName));
        while (true) {
            Iterator<S3VersionSummary> versionIter = versionList.getVersionSummaries().iterator();
            while (versionIter.hasNext()) {
                S3VersionSummary vs = versionIter.next();
                s3Client.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
            }

            if (versionList.isTruncated()) {
                versionList = s3Client.listNextBatchOfVersions(versionList);
            } else {
                break;
            }
        }
    }

    public static void synchelper(JsonNode jsonNode, String url, List<String> keyPaths, RestTemplate restTemplate, HttpEntity entity, AmazonS3 s3, String path, ObjectMetadata meta) throws IOException {

        Iterator<JsonNode> jsonNodes = jsonNode.iterator();
        while(jsonNodes.hasNext()) {
            JsonNode childNode = jsonNodes.next();
            keyPaths.add(childNode.get("name").textValue());
            if(childNode.get("type").textValue().equals("tree")){
                ResponseEntity<String> response = restTemplate.exchange(url + path + "/" + keyPaths.stream().collect(Collectors.joining("/")),
                        HttpMethod.GET, entity, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(response.getBody());
                synchelper(node, url, keyPaths, restTemplate, entity, s3, path, meta);
            } else if(childNode.get("type").textValue().equals("blob")){
                String encodedFilePath = URLEncoder.encode(path + "/" + keyPaths.stream().collect(Collectors.joining("/")), StandardCharsets.UTF_8.toString());
                String fileUrl = "https://gitlab-ee.us-east-vpc.socure.be/api/v4/projects/64/repository/files/" + encodedFilePath + "?ref=master";
                URI uri = URI.create(fileUrl);
                ResponseEntity<String> response = restTemplate.exchange(uri,
                        HttpMethod.GET, entity, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(response.getBody());
                byte[] decodedBytes = Base64.getDecoder().decode(node.get("content").textValue());
                String content = new String(decodedBytes);
                s3.putObject(new PutObjectRequest("rulecode-audit-stage", "studio/" + keyPaths.stream().collect(Collectors.joining("/")),
                        IOUtils.toInputStream(content, StandardCharsets.UTF_8), meta));
            }
            keyPaths.remove(keyPaths.size()-1);
        }
    }
}
