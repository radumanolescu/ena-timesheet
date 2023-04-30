package com.ena.timesheet.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;

@DynamoDBTable(tableName = "PhdTemplate")
public class PhdTemplateDao {

    private int yyyyMM;
    private byte[] file_bytes;

    public PhdTemplateDao() {}

    @DynamoDBHashKey(attributeName = "yyyyMM")
    public int getYyyyMM() {
        return yyyyMM;
    }

    public void setYyyyMM(int yyyyMM) {
        this.yyyyMM = yyyyMM;
    }

    @DynamoDBAttribute(attributeName = "file_bytes")
    public byte[] getFile_bytes() {
        return file_bytes;
    }

    public void setFile_bytes(byte[] file_bytes) {
        this.file_bytes = file_bytes;
    }

    public void save() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.save(this);
    }

}
