package com.checkout.payment.gateway.configuration;

import com.checkout.payment.gateway.client.BankClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Wires the HTTP infrastructure used to talk to the acquiring bank.
 *
 * <p>
 * Defines a shared {@link RestTemplate} with explicit connect and read timeouts, and a
 * {@link BankClient} bound to the configured bank URL that uses that template for outbound calls.
 */
@Configuration
public class ApplicationConfiguration {

  /**
   * Builds the {@link RestTemplate} used for outbound HTTP calls, configured with 10-second
   * connect and read timeouts.
   *
   * @param builder the autoconfigured {@link RestTemplateBuilder} supplied by Spring Boot.
   * @return a configured {@link RestTemplate} instance.
   */
  @Bean
  public RestTemplate restTemplate(final RestTemplateBuilder builder) {
    return builder.setConnectTimeout(Duration.ofMillis(10000))
        .setReadTimeout(Duration.ofMillis(10000))
        .build();
  }

  /**
   * Creates the {@link BankClient} bound to the bank URL resolved from the {@code bank.url}
   * property.
   *
   * @param restTemplate the {@link RestTemplate} used to issue HTTP requests to the bank.
   * @param bankUrl the base URL of the acquiring bank, injected from the {@code bank.url} property.
   * @return a {@link BankClient} ready to dispatch payment requests.
   */
  @Bean
  public BankClient bankClient(final RestTemplate restTemplate,
      @Value("${bank.url}") final String bankUrl) {
    return BankClient.of(restTemplate, bankUrl);
  }
}
