package com.ena.timesheet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class AppConfig {
    @Bean
    public DynamoDbClient dynamoClient() throws URISyntaxException {
        String runtimeEnv = getRuntimeEnv();
        return getClient(runtimeEnv);
    }

    public DynamoDbClient getClient(String runtimeEnv) throws URISyntaxException {
//      ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
//      ContainerCredentialsProvider credentialsProvider = ContainerCredentialsProvider.builder()
//              .asyncCredentialUpdateEnabled(true).build();
        DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.builder()
                .asyncCredentialUpdateEnabled(true).build();

        Region region = Region.US_EAST_1;
        DynamoDbClient client = null;
        switch (runtimeEnv) {
            case "AWS":
                System.out.println("Using AWS DynamoDB");
                client = DynamoDbClient.builder()
                        .credentialsProvider(credentialsProvider)
                        .region(region)
                        .build();
                break;
            case "dev/local":
                System.out.println("Using local DynamoDB");
                client = DynamoDbClient.builder()
                        .credentialsProvider(credentialsProvider)
                        .region(region)
                        .endpointOverride(new URI("http://localhost:8001"))
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
