package com.starterkit.demo.repository;

import com.starterkit.demo.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
	Optional<Company> findByCompanyName(String companyName);
}
