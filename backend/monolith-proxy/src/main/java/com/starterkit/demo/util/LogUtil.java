package com.starterkit.demo.util;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LogUtil {
    private static final String LOGGING_SERVICE_URL = "http://logging:5001/logs";
    private static final String LOG_ID = "log-id";
    private final RestTemplate restTemplate;

    public LogUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendLog(String logMessage) {
        LogEntry logEntry = new LogEntry(logMessage);
        restTemplate.postForObject(LOGGING_SERVICE_URL, logEntry, Void.class);
    }

    public String getLogId() {
        return MDC.get(LOG_ID);
    }

    public static class LogEntry {
        private String message;

        public LogEntry(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
