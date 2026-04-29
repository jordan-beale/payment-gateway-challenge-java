package com.checkout.payment.gateway;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.client.BankClient;
import com.checkout.payment.gateway.controller.PaymentGatewayController;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

class PaymentGatewayFunctionalTest {

  private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayFunctionalTest.class);
  private static final String BANK_SIMULATOR_URL = "http://localhost:8080";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  static void startBankSimulator() throws Exception {
    logger.trace("Starting bank simulator via docker compose");
    new ProcessBuilder("docker", "compose", "up", "-d")
        .directory(new File(System.getProperty("user.dir")))
        .inheritIO()
        .start()
        .waitFor(30, TimeUnit.SECONDS);
    logger.debug("docker compose up completed. Waiting for port 8080 to become available.");
    waitForPort(8080);
    logger.debug("Bank simulator is ready on port 8080.");
  }

  @AfterAll
  static void stopBankSimulator() throws Exception {
    logger.trace("Tearing down bank simulator via docker compose");
    new ProcessBuilder("docker", "compose", "down")
        .directory(new File(System.getProperty("user.dir")))
        .inheritIO()
        .start()
        .waitFor(30, TimeUnit.SECONDS);
    logger.debug("Bank simulator stopped.");
  }

  private static void waitForPort(final int port) throws Exception {
    for (int i = 0; i < 30; i++) {
      try (var ignored = new Socket("localhost", port)) {
        logger.debug("Port {} is open after {} attempt(s).", port, i + 1);
        return;
      } catch (final Exception ex) {
        logger.trace("Port {} not yet open, retrying ({}/30)...", port, i + 1);
        Thread.sleep(1000);
      }
    }
    throw new IllegalStateException("Bank simulator did not start within 30 seconds");
  }

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    logger.trace(
        "Wiring up repository, real bank client, service, and controller for functional test");
    final var paymentsRepository = PaymentsRepository.of();
    final var bankClient = BankClient.of(new RestTemplate(), BANK_SIMULATOR_URL);
    final var service = PaymentGatewayService.of(paymentsRepository, bankClient);
    final var controller = PaymentGatewayController.of(service);
    this.mvc = MockMvcBuilders.standaloneSetup(controller).build();
    logger.debug("Test context ready, targeting simulator at {}", BANK_SIMULATOR_URL);
  }

  // POST /payment - authorized and declined

  @Test
  void whenCardEndsInOddDigitPaymentIsAuthorized() throws Exception {
    logger.trace("Posting authorizing request (card ending in odd digit 7)");
    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(AUTHORIZING_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.status").value("AUTHORIZED"))
        .andExpect(jsonPath("$.last_four_digits").value("8877"))
        .andExpect(jsonPath("$.expiry_month").value(4))
        .andExpect(jsonPath("$.expiry_year").value(2030))
        .andExpect(jsonPath("$.currency").value("GBP"))
        .andExpect(jsonPath("$.amount").value(100));
    logger.debug("Payment authorized by simulator and all response fields verified.");
  }

  @Test
  void whenCardEndsInEvenDigitPaymentIsDeclined() throws Exception {
    logger.trace("Posting declining request (card ending in even digit 2)");
    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(DECLINING_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.status").value("DECLINED"))
        .andExpect(jsonPath("$.last_four_digits").value("8872"));
    logger.debug("Payment declined by simulator as expected.");
  }

  @Test
  void whenPaymentIsProcessedItCanBeRetrievedById() throws Exception {
    logger.trace("Posting payment to obtain an assigned id for round-trip retrieval");
    final var result = this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(AUTHORIZING_REQUEST)))
        .andExpect(status().isCreated())
        .andReturn();

    final var id = MAPPER.readTree(result.getResponse().getContentAsString()).get("id").asText();
    logger.debug("Payment created with id={}. Fetching it via GET.", id);

    this.mvc.perform(MockMvcRequestBuilders.get("/payment/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id));
    logger.debug("Round-trip confirmed: payment retrieved by id successfully.");
  }

  // POST /payment - bank failure

  @Test
  void whenCardEndsInZeroThenBankUnavailableReturns503() throws Exception {
    logger.trace("Posting request with card ending in 0 to trigger simulated bank unavailability");
    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(BANK_UNAVAILABLE_REQUEST)))
        .andExpect(status().isServiceUnavailable());
    logger.debug("503 returned as expected when simulator signals bank unavailability.");
  }

  // Simulator rules: card last digit odd (1,3,5,7,9) → authorized
  //                                      even (2,4,6,8) → declined
  //                                      0              → 503
  private static final PaymentRequest AUTHORIZING_REQUEST =
      PaymentRequest.builder()
          .withCardNumber("2222405343248877")
          .withExpiryMonth(4)
          .withExpiryYear(2030)
          .withCurrency("GBP")
          .withAmount(100)
          .withCvv("123")
          .build();

  private static final PaymentRequest DECLINING_REQUEST =
      PaymentRequest.builder()
          .withCardNumber("2222405343248872")
          .withExpiryMonth(4)
          .withExpiryYear(2030)
          .withCurrency("GBP")
          .withAmount(100)
          .withCvv("123")
          .build();

  private static final PaymentRequest BANK_UNAVAILABLE_REQUEST =
      PaymentRequest.builder()
          .withCardNumber("2222405343248870")
          .withExpiryMonth(4)
          .withExpiryYear(2030)
          .withCurrency("GBP")
          .withAmount(100)
          .withCvv("123")
          .build();
}