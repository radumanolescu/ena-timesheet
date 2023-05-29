package com.ena.timesheet.dao;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PhdTemplateDao {
    public static final String tableName = "PhdTemplate";
    public static final String key = "yyyyMM";
    public static final String fileBytesAttr = "fileBytes";

    private final DynamoDbClient ddb;

    public PhdTemplateDao(DynamoDbClient ddb) {
        this.ddb = ddb;
    }

    public byte[] getItem(String keyVal) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(key, AttributeValue.builder()
                .s(keyVal)
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        byte[] fileBytes = null;
        try {
            Map<String, AttributeValue> returnedItem = ddb.getItem(request).item();
            if (returnedItem != null) {
                fileBytes = returnedItem.get(fileBytesAttr).b().asByteArray();
            } else {
                System.out.format("No item found with the key %s!\n", keyVal);
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return fileBytes;
    }

    public void putItem(String keyVal, byte[] fileBytes) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put(key, AttributeValue.fromS(keyVal));
        itemValues.put(fileBytesAttr, AttributeValue.fromB(SdkBytes.fromByteArray(fileBytes)));
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            PutItemResponse response = ddb.putItem(request);
            System.out.println(tableName + " was successfully updated. The request id is " + response.responseMetadata().requestId());

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteItem(String keyVal) {
        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(key, AttributeValue.builder().s(keyVal).build()))
                .build();

        ddb.deleteItem(deleteItemRequest);
    }

}
