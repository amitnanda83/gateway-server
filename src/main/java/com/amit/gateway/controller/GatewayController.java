package com.amit.gateway.controller;

import com.amit.order.export.ExportRequest;
import com.amit.order.export.ExportResponse;
import com.amit.order.export.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for the service to connect to outer world
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gateway")
public class GatewayController {

    /**
     * Queue on which export orders are to be placed
     */
    private final Queue exportQueue;

    /**
     * Template to connect to Rabbit to send and receive messages
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * Map to store the status for each order placed to order-service
     */
    private final Map<String, ExportResponse> tasksResponse = new HashMap<>();

    /**
     * API to export order report. This api accept the orders and send them to order-service to be processed.
     */
    @GetMapping(value = "/export")
    @ResponseStatus(HttpStatus.OK)
    public ExportResponse exportOrders(@RequestBody ExportRequest request) {

        String requestKey = String.valueOf(System.nanoTime());
        request.setKey(requestKey);
        rabbitTemplate.convertAndSend(this.exportQueue.getName(), request);

        tasksResponse.put(requestKey, ExportResponse.builder().id(requestKey).status(Status.SUBMITTED).build());
        return ExportResponse.builder().id(requestKey).status(Status.SUBMITTED).build();
    }

    /**
     * API to get status to each submitted task
     */
    @GetMapping(value = "/status")
    @ResponseStatus(HttpStatus.OK)
    public ExportResponse orderStatus(@RequestBody String id) {

        return tasksResponse.get(id);
    }

    /**
     * Method to receive any export status received from order-service, on receiving the status those are updated in
     * the status map
     */
    @RabbitListener(queues = {"${status.queue.name}"})
    public void receive(final ExportResponse response) {

        log.info("Received status message for {}", response.getId());
        tasksResponse.put(response.getId(), response);
    }
}
