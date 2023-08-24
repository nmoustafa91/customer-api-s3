package com.customers.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("customers.s3-storage")
@Data
public class UploadingProperties {

  private int hourlyDeltaFrequency;
  // size of the batch to be uploaded to S3 per time
  private int batchSize = 100;
  private String customersBucket;

}

