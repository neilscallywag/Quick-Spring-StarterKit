/* (C)2024 */
package com.starterkit.demo.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.starterkit.demo.clients.slack.SlackAlertClient;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private SlackAlertClient slackAlertClient;

	public GlobalExceptionHandler(SlackAlertClient slackAlertClient) {
		this.slackAlertClient = slackAlertClient;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
		log.error("ResourceNotFoundException: ", ex);
		return new ResponseEntity<>(sanitizeMessage(ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException ex) {
		log.error("InvalidRequestException: ", ex);
		return new ResponseEntity<>(sanitizeMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
		log.error("AuthenticationException: ", ex);
		return new ResponseEntity<>(sanitizeMessage(ex.getMessage()), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.error("Validation exception: ", ex);
		return ex.getBindingResult().getAllErrors().stream()
				.collect(
						Collectors.toMap(
								error ->
										((org.springframework.validation.FieldError) error)
												.getField(),
								error -> sanitizeMessage(error.getDefaultMessage())));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneralException(Exception ex) {
		log.error("Internal Server Error: ", ex);
		sendSlackNotification(ex);
		return new ResponseEntity<>(
				sanitizeMessage("An unexpected error occurred. Please try again later."),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String sanitizeMessage(String message) {
		// Logic to sanitize confidential details and PII
		return message.replace("confidential details regex", "REDACTED");
	}

	private void sendSlackNotification(Exception ex) {
		String slackMessage = String.format("Internal Server Error: %s", ex.getMessage());
		try {
			slackAlertClient.notify(slackMessage);
		} catch (Exception e) {
			log.error("Failed to send Slack notification: ", e);
		}
	}
}
