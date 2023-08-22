package com.customers.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("customers.s3-storage")
@Data
public class UploadingProperties {

  private int hourlyDeltaFrequency;
  /// batch size
  private int batchSize = 100;

}

