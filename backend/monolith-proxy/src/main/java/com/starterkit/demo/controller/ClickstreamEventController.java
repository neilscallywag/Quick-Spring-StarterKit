/* (C)2024 */
package com.starterkit.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.starterkit.demo.kafka.ClickstreamEventProducer;
import com.starterkit.demo.model.ClickstreamEvent;

import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/interaction")
public class ClickstreamEventController {

	private final ClickstreamEventProducer eventProducer;

	public ClickstreamEventController(ClickstreamEventProducer eventProducer) {
		this.eventProducer = eventProducer;
	}

	@PostMapping
	public ResponseEntity<String> receiveEvent(@RequestBody ClickstreamEvent event) {
		return validateAndProcessEvent(event,
			e -> eventProducer.sendEvent(e),
			"Event received and processed",
			"Error processing event"
		);
	}

	@PostMapping("/batch")
	public ResponseEntity<String> receiveEventsBatch(@RequestBody List<ClickstreamEvent> events) {
		return validateAndProcessEvents(events,
			e -> eventProducer.sendEvent(e),
			"Batch of events received and processed",
			"Error processing batch of events"
		);
	}

	private ResponseEntity<String> validateAndProcessEvent(ClickstreamEvent event,
														Consumer<ClickstreamEvent> eventConsumer,
														String successMessage,
														String errorMessage) {
		if (isValid(event)) {
			try {
				eventConsumer.accept(event);
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(successMessage);
			} catch (Exception e) {
				// Log the error for monitoring
				// e.g., logger.error("Error processing event", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + ": " + e.getMessage());
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid event data");
		}
	}

	private ResponseEntity<String> validateAndProcessEvents(List<ClickstreamEvent> events,
															Consumer<ClickstreamEvent> eventConsumer,
															String successMessage,
															String errorMessage) {
		if (events == null || events.isEmpty() || !events.stream().allMatch(this::isValid)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid events data");
		}

		try {
			events.forEach(eventConsumer);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(successMessage);
		} catch (Exception e) {
			// Log the error for monitoring
			// e.g., logger.error("Error processing events batch", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + ": " + e.getMessage());
		}
	}

	private boolean isValid(ClickstreamEvent event) {
		// Perform validation logic here
		// For example, check if necessary fields are not null
		return event != null;
	}
}
