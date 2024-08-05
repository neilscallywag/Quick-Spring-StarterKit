/* (C)2024 */
package com.starterkit.demo.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a clickstream event capturing user interactions with a web application.
 */
public record ClickstreamEvent(
        LocalDateTime eventDatetime,
        // To refactor to an enum for fixed set of events
        String event,
        UUID userId,
        String clientId,
        String page,
        String pagePrevious,
        JobDetails jobDetails,
        Application application,
        OfferDetails offerDetails,
        ProfileUpdate profileUpdate,
        CompanyDetails companyDetails) {

    /**
     * Represents details about a job in a clickstream event.
     */
    public static record JobDetails(
            String jobTitle,
            String jobId,
            String companyName,
            String location,
            String jobType,
            String industry,
            String salaryRange,
            LocalDateTime postedDate) {}

    /**
     * Represents information about a user's application to a job.
     */
    public static record Application(
            String jobTitle,
            String jobId,
            String companyName,
            String resumeId,
            String coverLetterId,
            String applicationStatus) {}

    /**
     * Represents details about a job offer made to a user.
     */
    public static record OfferDetails(
            String jobTitle,
            String jobId,
            String companyName,
            double salaryOffered,
            LocalDateTime startDate,
            String offerStatus) {}

    /**
     * Represents information about updates made to a user's profile.
     */
    public static record ProfileUpdate(List<String> fieldsUpdated, LocalDateTime timestamp) {}

    /**
     * Represents details about a company involved in the event.
     */
    public static record CompanyDetails(
            String companyName, String industry, String location, String size) {}
}