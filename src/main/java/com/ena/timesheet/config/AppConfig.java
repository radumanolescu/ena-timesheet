package com.ena.timesheet.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public AmazonDynamoDB dynamoClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        return client;
    }
}
/* To read data from the DynamoDB:
            DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
            PhdTemplateDao dao = mapper.load(PhdTemplateDao.class, yearMonth);

* */
