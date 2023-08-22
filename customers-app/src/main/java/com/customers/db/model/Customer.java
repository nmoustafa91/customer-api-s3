package com.customers.db.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entity represents the customer object in DB.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "kunde")
public class Customer
    extends UserAwareEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq")
  @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq", initialValue = 5000)
  @Column(name = "kundenid")
  private Long  customerId;

  @Column(name = "vorname", nullable = false)
  private String firstName;

  @Column(name = "nachname", nullable = false)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "strasse", nullable = false)
  private String street;

  @Column(name = "strassenzusatz", nullable = false)
  private String streetAdditional;

  @Column(name = "ort", nullable = false)
  private String city;

  @Column(name = "land", nullable = false)
  private String land;

  @Column(name = "plz", nullable = false)
  private String postal;

  @Column(name = "firmenname", nullable = false)
  private String companyName;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
  private Set<Task> tasks = new HashSet<>();

}
