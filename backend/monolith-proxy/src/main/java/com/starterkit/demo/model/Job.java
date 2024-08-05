package com.starterkit.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a job entity in the system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobs", indexes = {@Index(name = "idx_job_title", columnList = "job_title")})
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(updatable = false, nullable = false)
	private UUID id;

	@Column(name = "job_title", nullable = false)
	private String jobTitle;

	@Column(name = "company_name", nullable = false)
	private String companyName;

	@Column(name = "location")
	private String location;

	@Column(name = "job_type")
	private String jobType;

	@Column(name = "industry")
	private String industry;

	@Column(name = "salary_range")
	private String salaryRange;

	@Column(name = "posted_date", nullable = false)
	private LocalDateTime postedDate;

	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private Set<Application> applications = new HashSet<>();

	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private Set<Offer> offers = new HashSet<>();
}
