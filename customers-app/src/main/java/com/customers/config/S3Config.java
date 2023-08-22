package com.customers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.context.config.annotation.EnableContextInstanceData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@EnableContextInstanceData
public class S3Config {
        @Value("${cloud.aws.region.static}")
        private String region;
        @Value("${cloud.aws.s3.endpoint}")
        private String endpoint;
        @Value("${cloud.aws.credentials.accessKey}")
        private String accessKey;
        @Value("${cloud.aws.credentials.secretKey}")
        private String secretKey;

        @Bean
        public AmazonS3 amazonS3() {
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

                return AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,region))
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();
        }

}
