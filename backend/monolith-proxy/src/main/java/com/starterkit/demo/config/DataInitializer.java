/* (C)2024 */
package com.starterkit.demo.config;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;
import com.starterkit.demo.model.Company;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Job;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.CompanyRepository;
import com.starterkit.demo.repository.JobRepository;
import com.starterkit.demo.repository.RoleRepository;
import com.starterkit.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CompanyRepository companyRepository;
	private final JobRepository jobRepository;
	private final Faker faker;

	public DataInitializer(
			RoleRepository roleRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			CompanyRepository companyRepository, JobRepository jobRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.faker = new Faker();
		this.companyRepository = companyRepository;
		this.jobRepository = jobRepository;

	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		log.info("Running DataInitializer...");
		createRolesIfNotExist();
		createCompaniesAndJobs(75, 150);

		// createUsersForTesting(1000);
	}

	private void createRolesIfNotExist() {
		try {
			log.info("Checking if roles exist...");
			if (roleRepository.findByName(EnumRole.ROLE_USER).isEmpty()) {
				roleRepository.save(new Role(EnumRole.ROLE_USER));
				log.info("ROLE_USER created");
			}
			if (roleRepository.findByName(EnumRole.ROLE_OFFICER).isEmpty()) {
				roleRepository.save(new Role(EnumRole.ROLE_OFFICER));
				log.info("ROLE_OFFICER created");
			}
			if (roleRepository.findByName(EnumRole.ROLE_MANAGER).isEmpty()) {
				roleRepository.save(new Role(EnumRole.ROLE_MANAGER));
				log.info("ROLE_MANAGER created");
			}
		} catch (Exception e) {
			log.error("Error creating roles", e);
		}
	}

	@Async
	@Transactional
	// In springboot we should not call the async or transactional method
	// directly within the class it is defined because Spring generates a proxy
	// class with
	// wrapper code to manage the method’s asynchronicity (@Async) or to handle the
	// transaction
	// (@Transactional).
	// However, when called using `this`, the proxy instance is bypassed, and the
	// method is invoked
	// directly without the required wrapper code.
	public void createUsersForTesting(int count) {
		try {
			log.info("Creating test users...");
			IntStream.range(0, count)
					.forEach(
							i -> {
								String username = "testuser" + i;
								if (userRepository.findByUsername(username).isEmpty()) {
									log.info("Creating user: " + username);
									User user = new User();
									user.setUsername(username);
									user.setPassword(passwordEncoder.encode("password"));
									user.setName("Test User " + i);
									user.setEmail("testuser" + i + "@example.com");
									user.setPhoneNumber("123456789" + i);
									user.setDateOfBirth(new Date());
									user.setEmailVerified(true);
									user.setProvider("local");
									user.setProviderId("testuser-provider-id" + i);
									user.setImageUrl("http://example.com/image" + i + ".png");
									user.setAuthProvider(User.AuthProvider.LOCAL);
									user.setCreatedAt(new Date());
									user.setUpdatedAt(new Date());

									Set<Role> roles = new HashSet<>();
									Role userRole = roleRepository
											.findByName(EnumRole.ROLE_USER)
											.orElseThrow(
													() -> new RuntimeException(
															"Error: Role is not"
																	+ " found."));
									roles.add(userRole);
									user.setRoles(roles);

									userRepository.save(user);
									log.info("User created: " + username);
								}
							});
		} catch (Exception e) {
			log.error("Error creating test users", e);
		}
	}

	@Async
	@Transactional
	public CompletableFuture<Void> createCompaniesAndJobs(int companyCount, int jobCount) {
		log.info("Creating companies and jobs...");
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			IntStream.range(0, companyCount).forEach(i -> {
				String companyName = faker.company().name();
				if (companyRepository.findByCompanyName(companyName).isEmpty()) {
					Company company = new Company();
					company.setCompanyName(companyName);
					company.setIndustry(faker.company().industry());
					company.setLocation(faker.address().city());
					company.setSize(faker.number().digits(3));
					companyRepository.save(company);

					IntStream.range(0, jobCount / companyCount).forEach(j -> {
						Job job = new Job();
						job.setJobTitle(faker.job().title());
						job.setCompanyName(company.getCompanyName());
						job.setLocation(faker.address().city());
						job.setJobType(faker.job().field());
						job.setIndustry(company.getIndustry());
						job.setSalaryRange(faker.number().numberBetween(50000, 200000) + " USD");
						job.setPostedDate(new Date().toInstant()
								.atZone(ZoneId.systemDefault())
								.toLocalDateTime());
						jobRepository.save(job);
					});
				}
			});
		});
		return future;
	}
}
