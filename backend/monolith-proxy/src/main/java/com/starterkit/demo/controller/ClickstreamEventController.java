/* (C)2024 */
package com.starterkit.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.starterkit.demo.kafka.ClickstreamEventProducer;
import com.starterkit.demo.model.ClickstreamEvent;

import java.util.List;

@RestController
@RequestMapping("/api/interaction")
public class ClickstreamEventController {

    private final ClickstreamEventProducer eventProducer;

    public ClickstreamEventController(ClickstreamEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @PostMapping
    public ResponseEntity<String> receiveEvent(@RequestBody ClickstreamEvent event) {
        eventProducer.sendEvent(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Event received and processed");
    }

    @PostMapping("/batch")
    public ResponseEntity<String> receiveEventsBatch(@RequestBody List<ClickstreamEvent> events) {
        events.forEach(eventProducer::sendEvent);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Batch of events received and processed");
    }
}
