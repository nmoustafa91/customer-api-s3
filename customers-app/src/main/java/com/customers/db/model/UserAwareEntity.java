package com.customers.db.model;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode()
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class UserAwareEntity {

  @Version
  private Long version;

  @CreatedBy
  @Column(name = "createdby")
  private String createdBy;

  @LastModifiedBy
  @Column(name = "lastchangeby")
  private String lastChangeBy;

  @CreatedDate
  @Column(name = "created")
  private OffsetDateTime created;

  @LastModifiedDate
  @Column(name = "lastchange")
  private OffsetDateTime lastChange;

}
