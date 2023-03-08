package com.amit.gateway;

import com.amit.gateway.controller.GatewayController;
import com.amit.order.OrderRequest;
import com.amit.order.export.ExportResponse;
import com.amit.order.export.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class to validate the scenarios for {@link GatewayController}
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewayServiceApplicationTests {

    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:management");

    private static String key;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Order(1)
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void shouldSubmitJob() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/gateway/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getOrderRequest()))
                .andExpect(status().isOk())
                .andReturn();

        ExportResponse response = mapper.readValue(result.getResponse().getContentAsString(), ExportResponse.class);

        Assertions.assertNotNull(response.getId());
        Assertions.assertNull(response.getFilePath());
        Assertions.assertEquals(response.getStatus(), Status.SUBMITTED);

        key = response.getId();
    }

    private String getOrderRequest() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .orderDate(System.currentTimeMillis())
                .productName("Test Product")
                .customerName("Test Customer")
                .build();

        return new ObjectMapper().writeValueAsString(request);
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    void shouldReportStatus() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/gateway/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(key))
                .andExpect(status().isOk())
                .andReturn();

        ExportResponse response = mapper.readValue(result.getResponse().getContentAsString(), ExportResponse.class);

        Assertions.assertNull(response.getFilePath());
        Assertions.assertEquals(response.getId(), key);
        Assertions.assertEquals(response.getStatus(), Status.SUBMITTED);
    }
}
