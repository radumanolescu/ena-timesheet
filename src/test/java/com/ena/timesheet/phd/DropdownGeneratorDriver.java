package com.ena.timesheet.phd;

//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
//import software.amazon.awssdk.services.dynamodb.mapper.DynamoDbMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DropdownGeneratorDriver {
//    private final DynamoDbMapper mapper;

    public static void main(String[] args) throws URISyntaxException {
//        AppConfig appConfig = new AppConfig();
//        AmazonDynamoDB dynamoDBClient = appConfig.getClient("dev/local");
//        DropdownGeneratorDriver driver = new DropdownGeneratorDriver(dynamoDBClient);
//        driver.generateDropdowns(dynamoDBClient);
        DropdownGeneratorDriver driver = new DropdownGeneratorDriver();
        driver.retrieveData();
    }

    private void retrieveData() throws URISyntaxException {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .endpointOverride(new URI("http://localhost:8001"))
                .build();

        String tableName = "PhdTemplate";
        String key = "yyyyMM";
        int keyVal = 202303;

        getDynamoDBItem(ddb, tableName, key, keyVal);
    }

    public void getDynamoDBItem(DynamoDbClient ddb, String tableName, String key, int keyVal) {

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(key, AttributeValue.builder()
                .n(String.valueOf(keyVal))
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String, AttributeValue> returnedItem = ddb.getItem(request).item();
            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", key);
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

//    private void generateDropdowns(AmazonDynamoDB client) {
//        DynamoDBMapper mapper = new DynamoDBMapper(client);
//        int yyyyMM = 202304;
//        PhdTemplateDao dao = mapper.load(PhdTemplateDao.class, yyyyMM);
//        if (dao != null) {
//            System.out.println("dao is not null");
//        } else {
//            System.out.println("dao is null");
//        }
//    }

//    private final AmazonDynamoDB dynamoDBClient;

//    @Autowired
//    public DropdownGeneratorDriver(AmazonDynamoDB dynamoDBClient) {
//        this.dynamoDBClient = dynamoDBClient;
//    }
}
