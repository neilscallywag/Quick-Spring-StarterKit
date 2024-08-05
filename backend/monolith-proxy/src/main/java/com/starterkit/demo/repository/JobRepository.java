package com.starterkit.demo.repository;


import com.starterkit.demo.model.Job;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, UUID> {
}
