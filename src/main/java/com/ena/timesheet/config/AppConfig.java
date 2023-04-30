package com.ena.timesheet.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public AmazonDynamoDB dynamoClient() {
        String runtimeEnv = getRuntimeEnv();
        AmazonDynamoDB client = null;
        switch (runtimeEnv) {
            case "AWS":
                System.out.println("Using AWS DynamoDB");
                client = AmazonDynamoDBClientBuilder.standard().build();
                break;
            case "dev/local":
                System.out.println("Using local DynamoDB");
                client = AmazonDynamoDBClientBuilder.standard()
                        .withEndpointConfiguration(new AmazonDynamoDBClientBuilder.EndpointConfiguration("http://localhost:8001", "us-east-1"))
                        .build();
                break;
            default:
                System.out.println("Error: Unknown runtime environment: " + runtimeEnv);
        }
        return client;
    }

    private String getRuntimeEnv() {
        String env = System.getenv("AWS_ENV");
        if (env == null) {
            env = "AWS";
        }
        return env;
    }
}
/* To read data from the DynamoDB:
            DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
            PhdTemplateDao dao = mapper.load(PhdTemplateDao.class, yearMonth);

* */
