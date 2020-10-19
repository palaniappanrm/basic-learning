import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

public class Base64Helper {
    public static void main(String[] args) throws IOException {
//        byte[] decodedBytes = Base64.getDecoder().decode("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHByb2plY3QgeG1sbnM9Imh0dHA6Ly9tYXZlbi5hcGFjaGUub3JnL1BPTS80LjAuMCIKICAgICAgICAgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIKICAgICAgICAgeHNpOnNjaGVtYUxvY2F0aW9uPSJodHRwOi8vbWF2ZW4uYXBhY2hlLm9yZy9QT00vNC4wLjAgaHR0cDovL21hdmVuLmFwYWNoZS5vcmcveHNkL21hdmVuLTQuMC4wLnhzZCI+CgogICAgPHBhcmVudD4KICAgICAgICA8YXJ0aWZhY3RJZD5tYXZlbi1yb290PC9hcnRpZmFjdElkPgogICAgICAgIDxncm91cElkPm1lLnNvY3VyZTwvZ3JvdXBJZD4KICAgICAgICA8dmVyc2lvbj4wLjEtU05BUFNIT1Q8L3ZlcnNpb24+CiAgICAgICAgPHJlbGF0aXZlUGF0aD4gLi4vbWF2ZW4tcm9vdC9wb20ueG1sPC9yZWxhdGl2ZVBhdGg+CiAgICA8L3BhcmVudD4KCiAgICA8bW9kZWxWZXJzaW9uPjQuMC4wPC9tb2RlbFZlcnNpb24+CgogICAgPGFydGlmYWN0SWQ+ZG9jdW1lbnQtdmVyaWZpY2F0aW9uLWF1dGhlbnRpY2lkPC9hcnRpZmFjdElkPgogICAgPG5hbWU+ZG9jdW1lbnQtdmVyaWZpY2F0aW9uLWF1dGhlbnRpY2lkPC9uYW1lPgogICAgPHZlcnNpb24+MC4xLVNOQVBTSE9UPC92ZXJzaW9uPgogICAgPHBhY2thZ2luZz5wb208L3BhY2thZ2luZz4KCiAgICA8cHJvcGVydGllcz4KICAgICAgICA8Y29tbW9uLnZlcnNpb24+MC4xLVNOQVBTSE9UPC9jb21tb24udmVyc2lvbj4KICAgIDwvcHJvcGVydGllcz4KCiAgICA8bW9kdWxlcz4KICAgICAgICA8bW9kdWxlPmRvY3VtZW50LXZlcmlmaWNhdGlvbi1hdXRoZW50aWNpZC1zZXJ2aWNlPC9tb2R1bGU+CiAgICA8L21vZHVsZXM+CiAgICAgICAgPGRpc3RyaWJ1dGlvbk1hbmFnZW1lbnQ+CiAgICAgICAgPHNpdGU+CiAgICAgICAgICAgIDxpZD5zMy5zaXRlPC9pZD4KICAgICAgICAgICAgPHVybD5zMzovL21hdmVuLXNpdGUudXMtZWFzdC12cGMuc29jdXJlLmJlL2R2LWF1dGhlbnRpY2lkPC91cmw+CiAgICAgICAgPC9zaXRlPgogICAgPC9kaXN0cmlidXRpb25NYW5hZ2VtZW50PgogICAgPGJ1aWxkPgogICAgICAgIDxwbHVnaW5zPgogICAgICAgICAgICA8cGx1Z2luPgogICAgICAgICAgICAgICAgPGdyb3VwSWQ+b3JnLmN5Y2xvbmVkeDwvZ3JvdXBJZD4KICAgICAgICAgICAgICAgIDxhcnRpZmFjdElkPmN5Y2xvbmVkeC1tYXZlbi1wbHVnaW48L2FydGlmYWN0SWQ+CiAgICAgICAgICAgIDwvcGx1Z2luPgogICAgICAgIDwvcGx1Z2lucz4KICAgIDwvYnVpbGQ+CjwvcHJvamVjdD4K");
//        System.out.println(new String(decodedBytes));
//
//        System.out.println(URLEncoder.encode("document-verification-authenticid-service/pom.xml", StandardCharsets.UTF_8.toString()));

//        String key = "studio/FMVAL/file.json";
//        System.out.println(key.substring(key.lastIndexOf("/")));

        ObjectMapper objectMapper = new ObjectMapper();
//        String value = "{\n" +
//                "    \"EFX\" : {\n" +
//                "        \"1\" : {\n" +
//                "            \"jobId\" : \"1\",\n" +
//                "            \"file\" : \"test.csv\",\n" +
//                "            \"status\" : \"CREATED\",\n" +
//                "            \"triggeredBy\" : \"Palani\",\n" +
//                "            \"triggeredAt\" : 1593594750\n" +
//                "        },\n" +
//                "        \"2\" : {\n" +
//                "            \"jobId\" : \"2\",\n" +
//                "            \"file\" : \"test.csv2\",\n" +
//                "            \"status\" : \"CREATED2\",\n" +
//                "            \"triggeredBy\" : \"Palani2\",\n" +
//                "            \"triggeredAt\" : 1593594150\n" +
//                "        },\n" +
//                "        \"3\" : {\n" +
//                "            \"jobId\" : \"3\",\n" +
//                "            \"file\" : \"test.csv3\",\n" +
//                "            \"status\" : \"CREATED3\",\n" +
//                "            \"triggeredBy\" : \"Palani3\",\n" +
//                "            \"triggeredAt\" : 1593594150\n" +
//                "        }\n" +
//                "    }\n" +
//                "}";
//        JsonNode node = objectMapper.readTree(value);
//        JsonNode vendorNode = node.get("EFX");

//        List<JsonNode> jobsNode = new ArrayList<>();
//        vendorNode.elements().forEachRemaining(jobsNode::add);
//        int startIndex = 1 * 2;
//        int endIndex = startIndex + 2 > jobsNode.size() ? jobsNode.size() : startIndex + 2;
//        jobsNode = jobsNode.subList(startIndex, endIndex);
//        System.out.println(objectMapper.writeValueAsString(jobsNode));

//        String jobId = "x";
//        RuleCodeTestJobDTO ruleCodeTestJobDTO = new RuleCodeTestJobDTO();
//        ruleCodeTestJobDTO.setJobId(jobId);
//        ruleCodeTestJobDTO.setFile("test.csv4");
//        ruleCodeTestJobDTO.setStatus("CREATED");
//        ruleCodeTestJobDTO.setTriggeredBy("Palani4");
//        ruleCodeTestJobDTO.setTriggeredAt("1593594150");
//
//        ((ObjectNode)vendorNode).putPOJO(jobId, ruleCodeTestJobDTO);
//        System.out.println(objectMapper.writeValueAsString(node));

//        String jobId = "2";
//        String status = "COMPLETED";
//        JsonNode jobNode = vendorNode.get(jobId);
//        ((ObjectNode)jobNode).put("status", status);
//        System.out.println(objectMapper.writeValueAsString(node));

//        String result = "{\n" +
//                "    \"report\": [\n" +
//                "        {\n" +
//                "            \"customerUserId\": 12345,\n" +
//                "            \"status\": \"PASS\",\n" +
//                "            \"summary\": {\n" +
//                "            }\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"customerUserId\": 12346,\n" +
//                "            \"status\": \"FAIL\",\n" +
//                "            \"summary\": {\n" +
//                "            }\n" +
//                "        }\n" +
//                "    ],\n" +
//                "    \"status\": \"ok\"\n" +
//                "}";
//
//        Boolean jobPassed = true;
//
//        JsonNode jobResultNode = objectMapper.readTree(result);
//
//        if("ok".equals(jobResultNode.get("status").textValue()) && jobResultNode.get("report").isArray()){
//            for(JsonNode input : jobResultNode.get("report")){
//                if(!"PASS".equals(input.get("status").textValue())){
//                    jobPassed = false;
//                    break;
//                }
//            }
//        }
//
//        System.out.println(jobPassed);

        Map<String,Map<String,String>> map = new HashMap<>();
        Map<String,String> childMap = new HashMap<>();
        childMap.put("z","a");
        map.put("x",childMap);
//        map.put("z","a");
        System.out.println(objectMapper.writeValueAsString(map));

    }
}
