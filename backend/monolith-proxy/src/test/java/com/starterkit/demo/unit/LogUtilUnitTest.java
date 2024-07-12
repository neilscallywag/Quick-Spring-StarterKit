package com.starterkit.demo.unit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.web.client.RestTemplate;

import com.starterkit.demo.util.LogUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogUtilUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LogUtil logUtil;

    @BeforeEach
    void setUp() {
        logUtil = new LogUtil(restTemplate);
    }

    @Test
    void testSendLog() {
        String logMessage = "Test log message";
        ArgumentCaptor<LogUtil.LogEntry> logEntryCaptor = ArgumentCaptor.forClass(LogUtil.LogEntry.class);

        logUtil.sendLog(logMessage);

        verify(restTemplate, times(1)).postForObject(eq("http://logging:5001/logs"), logEntryCaptor.capture(), eq(Void.class));
        LogUtil.LogEntry capturedLogEntry = logEntryCaptor.getValue();
        assertEquals(logMessage, capturedLogEntry.getMessage());
    }

    @Test
    void testGetLogId() {
        String logId = "12345";
        MDC.put("log-id", logId);

        String result = logUtil.getLogId();

        assertEquals(logId, result);

        MDC.clear();  // Clean up MDC after the test
    }

    @Test
    void testLogEntry() {
        String logMessage = "Test log entry";
        LogUtil.LogEntry logEntry = new LogUtil.LogEntry(logMessage);

        assertEquals(logMessage, logEntry.getMessage());

        String newMessage = "Updated log entry";
        logEntry.setMessage(newMessage);

        assertEquals(newMessage, logEntry.getMessage());
    }
}
