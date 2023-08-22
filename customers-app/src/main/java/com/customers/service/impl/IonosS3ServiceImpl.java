package com.customers.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.customers.config.properties.UploadingProperties;
import com.customers.db.model.Customer;
import com.customers.db.repository.CustomerRepository;
import com.customers.exception.general.CustomerException;
import com.customers.service.IonosS3Service;
import com.customers.storage.S3.S3Repository;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class IonosS3ServiceImpl implements IonosS3Service {


  private static final List<String> DETAIL_HEADERS = List.of("Firma", "Strasse", "Strassenzusatz", "Ort", "Land", "PLZ",
      "Vorname", "Nachname", "Kunden-ID");

  private final CustomerRepository customerRepository;
  private final S3Repository s3Repository;
  private final UploadingProperties uploadingProperties;

  @Override
  @Scheduled(cron = "${customers.s3-storage.cron.upload}")
  public void uploadCustomerDataToS3Storage() {
    OffsetDateTime deltaDate = OffsetDateTime.now().minusMonths(uploadingProperties.getHourlyDeltaFrequency());
    log.debug("Start CRON cleaner [deleteOldImports] with date [{}].", deltaDate);

    Slice<Customer> deltaCustomers;
    do {

      deltaCustomers = customerRepository.getDeltaCustomers(deltaDate,
          PageRequest.of(0, uploadingProperties.getBatchSize()));
      File file = writeCustomersSlice(deltaCustomers);
      s3Repository.uploadFileToS3Bucket("customers_bucket", file);

    } while (deltaCustomers.hasNext());
  }

  private File writeCustomersSlice(Slice<Customer> deltaCustomers) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8)) {

      ICSVWriter csvWriter = new CSVWriterBuilder(writer)
          .build();

      csvWriter.writeNext(DETAIL_HEADERS.toArray(String[]::new), false);
      deltaCustomers.get().forEach((customer -> writeDetail(customer, csvWriter)));

      writer.flush();

      File file = Files.createTempFile("Germany_" + LocalDate.now(), ".csv").toFile();
      OutputStream outputStream = new FileOutputStream(file);
      byteArrayOutputStream.writeTo(outputStream);
      return file;

    } catch (IOException ex) {
      throw new CustomerException("IO exception", ex);
    }
  }

  private void writeDetail(Customer customer, ICSVWriter csvWriter) {
    List<String> line = new ArrayList<>(DETAIL_HEADERS.size());
    line.add(customer.getCompanyName());
    line.add(customer.getStreet());
    line.add(customer.getStreetAdditional());
    line.add(customer.getCity());
    line.add(customer.getLand());
    line.add(customer.getPostal());
    line.add(customer.getFirstName());
    line.add(customer.getLastName());
    line.add(String.valueOf(customer.getCustomerId()));
    csvWriter.writeNext(line.toArray(String[]::new));
  }
}
