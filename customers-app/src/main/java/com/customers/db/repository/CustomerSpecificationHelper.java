package com.customers.db.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.customers.db.model.Contract;
import com.customers.db.model.Customer;
import com.customers.db.model.Customer_;
import com.customers.db.model.Contract_;
import com.customers.domain.CustomerFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerSpecificationHelper {

	public Specification<Customer> createFilter(CustomerFilter customerFilter) {
		return (root, query, cb) -> {
			final List<Predicate> predicates = new LinkedList<>();

			// first name
			if (StringUtils.hasText(customerFilter.getFirstName())) {
				predicates.add(cb.like(cb.lower(root.get(Customer_.FIRST_NAME)),
						"%" + customerFilter.getFirstName().toLowerCase(Locale.ROOT) + "%"));
			}

			// last name
			if (StringUtils.hasText(customerFilter.getLastName())) {
				predicates.add(cb.like(cb.lower(root.get(Customer_.LAST_NAME)),
						"%" + customerFilter.getLastName().toLowerCase(Locale.ROOT) + "%"));
			}

			// city
			if (StringUtils.hasText(customerFilter.getCity())) {
				predicates.add(cb.like(cb.lower(root.get(Customer_.CITY)),
						"%" + customerFilter.getCity().toLowerCase(Locale.ROOT) + "%"));
			}

			// company
			if (StringUtils.hasText(customerFilter.getCompanyName())) {
				predicates.add(cb.like(cb.lower(root.get(Customer_.COMPANY_NAME)),
						"%" + customerFilter.getCompanyName().toLowerCase(Locale.ROOT) + "%"));
			}

			// product
			if (StringUtils.hasText(customerFilter.getProductNumber())) {
				Join<Customer, Contract> contractJoin = root.join(Customer_.CONTRACTS);
				predicates.add(cb.equal(contractJoin.get(Contract_.PRODUCT_NUMBER), customerFilter.getProductNumber()));
			}

			// ids
			if (!CollectionUtils.isEmpty(customerFilter.getCustomersIds())) {
				predicates.add(root.get(Customer_.CUSTOMER_ID).in(customerFilter.getCustomersIds()));
			}

			// Search
			if (StringUtils.hasText(customerFilter.getSearch())) {
				predicates.add(cb.or(cb.equal(root.get(Customer_.CUSTOMER_ID).as(String.class), customerFilter.getSearch()),
						lowercaseLike(cb, root.get(Customer_.FIRST_NAME), customerFilter.getSearch())));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Predicate lowercaseLike(final CriteriaBuilder cb, Expression<String> attribute, final String fieldValue) {
		return cb.like(cb.lower(attribute), "%" + fieldValue.toLowerCase(Locale.getDefault()) + "%");
	}
}
