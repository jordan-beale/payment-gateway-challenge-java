package com.checkout.payment.gateway;

import static com.checkout.payment.gateway.model.Payment.Status.AUTHORIZED;
import static com.checkout.payment.gateway.model.Payment.Status.DECLINED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.client.BankClient;
import com.checkout.payment.gateway.controller.PaymentGatewayController;
import com.checkout.payment.gateway.exceptions.InfrastructureException;
import com.checkout.payment.gateway.exceptions.UnexpectedException;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.records.PaymentRequestRecord;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.YearMonth;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PaymentGatewayTest {

  private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayTest.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private MockMvc mvc;
  private PaymentsRepository paymentsRepository;
  private BankClient bankClient;

  @BeforeEach
  void setUp() {
    logger.trace(
        "Setting up test context — initialising repository, bank client mock, service, and controller");
    this.paymentsRepository = PaymentsRepository.of();
    this.bankClient = mock(BankClient.class);
    final var service = PaymentGatewayService.of(this.paymentsRepository, bankClient);
    final var controller = PaymentGatewayController.of(service);
    this.mvc = MockMvcBuilders.standaloneSetup(controller).build();
    logger.debug("Test context ready.");
  }

  // GET /payment/{id}

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    final var payment = Payment.builder()
        .withId(UUID.randomUUID().toString())
        .withStatus(AUTHORIZED)
        .withLastFourDigits("4321")
        .withExpiryMonth(12)
        .withExpiryYear(2029)
        .withCurrency("USD")
        .withAmount(10)
        .build();

    logger.trace("Seeding repository with payment id={}", payment.id());
    this.paymentsRepository.store(payment);
    logger.debug("Payment stored. Fetching it via GET /payment/{}", payment.id());

    this.mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.status().name()))
        .andExpect(jsonPath("$.last_four_digits").value(payment.lastFourDigits()))
        .andExpect(jsonPath("$.expiry_month").value(payment.expiryMonth()))
        .andExpect(jsonPath("$.expiry_year").value(payment.expiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.currency()))
        .andExpect(jsonPath("$.amount").value(payment.amount()));

    logger.debug("Response matched expected payment fields.");
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    final var missingId = UUID.randomUUID();
    logger.trace("Requesting payment with non-existent id={}", missingId);

    this.mvc.perform(MockMvcRequestBuilders.get("/payment/" + missingId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("No payment with provided ID"));

    logger.debug("404 received as expected for unknown payment id.");
  }

  // POST /payment - authorized and declined

  @Test
  void whenValidPaymentIsAuthorizedThenAuthorizedResponseIsReturned() throws Exception {
    logger.trace("Stubbing bank client to return AUTHORIZED");
    when(bankClient.authorize(any())).thenReturn(AUTHORIZED);

    logger.trace("Submitting valid payment request, expecting authorization");
    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(VALID_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.status").value("AUTHORIZED"))
        .andExpect(jsonPath("$.last_four_digits").value("8877"))
        .andExpect(jsonPath("$.expiry_month").value(4))
        .andExpect(jsonPath("$.expiry_year").value(2030))
        .andExpect(jsonPath("$.currency").value("GBP"))
        .andExpect(jsonPath("$.amount").value(100));

    logger.debug("Payment authorized and all response fields verified.");
  }

  @Test
  void whenValidPaymentIsDeclinedThenDeclinedResponseIsReturned() throws Exception {
    logger.trace("Stubbing bank client to return DECLINED");
    when(bankClient.authorize(any())).thenReturn(DECLINED);

    logger.trace("Posting payment request, expecting a declined response");
    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(VALID_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("DECLINED"));

    logger.debug("Declined status confirmed in response.");
  }

  @Test
  void whenPaymentIsProcessedItCanBeRetrievedById() throws Exception {
    logger.trace("Stubbing bank client to return AUTHORIZED for round-trip test");
    when(bankClient.authorize(any())).thenReturn(AUTHORIZED);

    logger.trace("Posting payment to obtain an assigned id");
    final var result = this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(VALID_REQUEST)))
        .andExpect(status().isCreated())
        .andReturn();

    final var id = MAPPER.readTree(result.getResponse().getContentAsString()).get("id").asText();
    logger.debug("Payment created with id={}. Now retrieving it.", id);

    this.mvc.perform(MockMvcRequestBuilders.get("/payment/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id));

    logger.debug("Round-trip confirmed: payment retrieved by id successfully.");
  }

  // POST /payment - bank failures

  @Test
  void whenBankIsUnavailableThen503IsReturned() throws Exception {
    logger.trace("Stubbing bank client to throw InfrastructureException simulating unavailability");
    when(bankClient.authorize(any())).thenThrow(new InfrastructureException("Bank unavailable"));

    logger.trace("Posting payment, expecting 503 Service Unavailable");
    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(VALID_REQUEST)))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.message").value("Bank unavailable"));

    logger.debug("503 returned with correct error message for bank unavailability.");
  }

  @Test
  void whenBankReturnsUnexpectedResponseThen500IsReturned() throws Exception {
    logger.trace(
        "Stubbing bank client to throw UnexpectedException simulating a malformed response");
    when(bankClient.authorize(any())).thenThrow(
        new UnexpectedException("Bank returned empty body"));

    logger.trace("Posting payment, expecting 500 Internal Server Error");
    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(VALID_REQUEST)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Bank returned empty body"));

    logger.debug("500 returned with correct error message for unexpected bank response.");
  }

  // POST /payment - validation rejections (parameterized)

  static Stream<Arguments> invalidPaymentRequests() {
    final var expiredMonth = YearMonth.now().minusMonths(1).getMonthValue();
    final var expiredYear = YearMonth.now().minusMonths(1).getYear();

    return Stream.of(
        Arguments.of("null card number",
            new PaymentRequestRecord(null, 4, 2030, "GBP", 100, "123")),
        Arguments.of("card number too short",
            new PaymentRequestRecord("4111111111111", 4, 2030, "GBP", 100, "123")),
        Arguments.of("card number too long",
            new PaymentRequestRecord("41111111111111111111", 4, 2030, "GBP", 100, "123")),
        Arguments.of("card number with letters",
            new PaymentRequestRecord("411111111111111X", 4, 2030, "GBP", 100, "123")),
        Arguments.of("expiry month 0",
            new PaymentRequestRecord("2222405343248877", 0, 2030, "GBP", 100, "123")),
        Arguments.of("expiry month 13",
            new PaymentRequestRecord("2222405343248877", 13, 2030, "GBP", 100, "123")),
        Arguments.of("expired card",
            new PaymentRequestRecord("2222405343248877", expiredMonth, expiredYear, "GBP", 100,
                "123")),
        Arguments.of("unsupported currency",
            new PaymentRequestRecord("2222405343248877", 4, 2030, "JPY", 100, "123")),
        Arguments.of("null currency",
            new PaymentRequestRecord("2222405343248877", 4, 2030, null, 100, "123")),
        Arguments.of("amount zero",
            new PaymentRequestRecord("2222405343248877", 4, 2030, "GBP", 0, "123")),
        Arguments.of("amount negative",
            new PaymentRequestRecord("2222405343248877", 4, 2030, "GBP", -1, "123")),
        Arguments.of("CVV too short",
            new PaymentRequestRecord("2222405343248877", 4, 2030, "GBP", 100, "12")),
        Arguments.of("CVV too long",
            new PaymentRequestRecord("2222405343248877", 4, 2030, "GBP", 100, "12345")),
        Arguments.of("CVV with letters",
            new PaymentRequestRecord("2222405343248877", 4, 2030, "GBP", 100, "12X")),
        Arguments.of("null CVV",
            new PaymentRequestRecord("2222405343248877", 4, 2030, "GBP", 100, null))
    );
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("invalidPaymentRequests")
  void whenPaymentRequestIsInvalidThenRequestIsRejectedWith422(final String description,
      final PaymentRequestRecord request) throws Exception {
    logger.trace("Submitting invalid request — case: {}", description);

    this.mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MAPPER.writeValueAsString(request)))
        .andExpect(status().isUnprocessableEntity());

    logger.debug("Request correctly rejected with 422 for case: {}", description);
  }

  private static final PaymentRequest VALID_REQUEST =
      PaymentRequest.builder()
          .withCardNumber("2222405343248877")
          .withExpiryMonth(4)
          .withExpiryYear(2030)
          .withCurrency("GBP")
          .withAmount(100)
          .withCvv("123")
          .build();
}