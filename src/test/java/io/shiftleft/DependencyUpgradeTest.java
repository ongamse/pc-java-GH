package io.shiftleft;

import com.sun.net.httpserver.HttpServer;
import io.shiftleft.model.Account;
import io.shiftleft.repository.AccountRepository;
import io.shiftleft.repository.CustomerRepository;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "logging.level.io.shiftleft=OFF",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DependencyUpgradeTest {
  private static HttpServer salesforce;
  private static final AtomicReference<String> eventBody = new AtomicReference<>();
  private static final AtomicReference<String> eventAuthorization = new AtomicReference<>();
  private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  private final JsonMapper json = JsonMapper.builder().build();

  @Value("${local.server.port}") int port;
  @Autowired Environment environment;
  @Autowired AccountRepository accounts;
  @Autowired CustomerRepository customers;
  @Autowired JsonMapper applicationMapper;

  @DynamicPropertySource
  static void localSalesforce(DynamicPropertyRegistry registry) throws IOException {
    salesforce = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    salesforce.createContext("/events", exchange -> {
      eventAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
      eventBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
      exchange.sendResponseHeaders(204, -1);
      exchange.close();
    });
    salesforce.start();
    registry.add("sfdc.url", () -> "http://127.0.0.1:" + salesforce.getAddress().getPort() + "/events");
    registry.add("sfdc.username", () -> "test-user");
    registry.add("sfdc.password", () -> "test-password");
  }

  @AfterAll
  static void stopSalesforce() {
    if (salesforce != null) salesforce.stop(0);
  }

  @Test
  void startsWithSeedDataAndSerializesDatesAndNestedEntities() throws Exception {
    HttpResponse<String> response = request("GET", "/customers", null, "application/json");
    assertEquals(200, response.statusCode());
    JsonNode list = json.readTree(response.body());
    assertTrue(list.isArray());
    assertTrue(list.size() >= 3);
    JsonNode seeded = list.get(0);
    assertTrue(seeded.get("id").asLong() > 0);
    assertTrue(seeded.get("address").isObject());
    assertEquals(2, seeded.get("accounts").size());
    assertTrue(seeded.get("dateOfBirth").isTextual());
    assertEquals(java.time.LocalDate.of(1982, 1, 10)
        .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant(),
        java.time.OffsetDateTime.parse(seeded.get("dateOfBirth").asString()).toInstant());
    assertEquals(200, request("GET", "/patients", null, "application/json").statusCode());
    assertEquals(200, request("GET", "/account", null, "application/json").statusCode());
  }

  @Test
  void createsUpdatesAndDeletesCustomersUsingThePathId() throws Exception {
    String body = "{\"customerId\":\"upgrade-test\",\"firstName\":\"Before\",\"lastName\":\"Upgrade\","
        + "\"dateOfBirth\":\"1982-01-10T00:00:00.000+00:00\",\"accounts\":[]}";
    HttpResponse<String> created = request("POST", "/customers", body, "application/json");
    assertEquals(201, created.statusCode());
    long id = json.readTree(created.body()).get("id").asLong();
    assertTrue(id > 0);
    assertEquals("/customers/" + id, created.headers().firstValue("Location").orElseThrow());
    long count = customers.count();
    assertEquals(204, request("PUT", "/customers/" + id,
        "{\"id\":0,\"firstName\":\"After\",\"accounts\":[]}", "application/json").statusCode());
    assertEquals(count, customers.count(), "Updating a resource must not insert a new row");
    assertEquals("After", customers.findById(id).orElseThrow().getFirstName());
    assertEquals(204, request("DELETE", "/customers/" + id, null, "application/json").statusCode());
    assertFalse(customers.existsById(id));
    assertEquals(404, request("PUT", "/customers/" + id,
        "{\"firstName\":\"Missing\"}", "application/json").statusCode());
    assertEquals(404, request("GET", "/customers/" + id, null, "application/json").statusCode());
  }

  @Test
  void readsAndUpdatesAccountsAndHandlesMissingIds() throws Exception {
    Account account = accounts.save(new Account(123, 456, "TEST", 100, 0.1));
    String path = "/account/" + account.getId();
    assertEquals(200, request("GET", path, null, "application/json").statusCode());
    assertEquals(200, request("POST", path + "/deposit?amount=25", "", "application/json").statusCode());
    assertEquals(125, accounts.findById(account.getId()).orElseThrow().getBalance(), 0.001);
    assertEquals(200, request("POST", path + "/withdraw?amount=5", "", "application/json").statusCode());
    assertEquals(120, accounts.findById(account.getId()).orElseThrow().getBalance(), 0.001);
    assertEquals(404, request("GET", "/account/9223372036854775807", null, "application/json").statusCode());
  }

  @Test
  void dispatchesSalesforceRequestsWithBasicAuthentication() throws Exception {
    long id = customers.findAll().iterator().next().getId();
    assertEquals(200, request("GET", "/customers/" + id, null, "application/json").statusCode());
    assertNotNull(eventBody.get());
    assertTrue(eventBody.get().contains("Logged into SalesForce"));
    String expected = "Basic " + Base64.getEncoder().encodeToString(
        "test-user:test-password".getBytes(StandardCharsets.ISO_8859_1));
    assertEquals(expected, eventAuthorization.get());
  }

  @Test
  void servesJsonAndHtmlErrorsWithoutStackTraces() throws Exception {
    HttpResponse<String> jsonError = request("GET", "/missing-upgrade-route", null, "application/json");
    assertEquals(404, jsonError.statusCode());
    assertEquals(404, json.readTree(jsonError.body()).get("status").asInt());
    assertNull(json.readTree(jsonError.body()).get("trace"));
    HttpResponse<String> htmlError = request("GET", "/missing-upgrade-route", null, "text/html");
    assertEquals(404, htmlError.statusCode());
    assertTrue(htmlError.headers().firstValue("Content-Type").orElseThrow().startsWith("text/html"));
    assertTrue(htmlError.body().contains("404 Not Found"));
  }

  @Test
  void exposesHealthButNotSensitiveActuatorEndpoints() throws Exception {
    HttpResponse<String> health = request("GET", "/actuator/health", null, "application/json");
    assertEquals(200, health.statusCode());
    assertEquals("UP", json.readTree(health.body()).get("status").asString());
    assertEquals(404, request("GET", "/actuator/env", null, "application/json").statusCode());
  }

  @Test
  void doesNotEnableUnsafeDefaultTyping() {
    assertInstanceOf(java.util.Map.class, applicationMapper.readValue(
        "{\"@class\":\"java.lang.Runtime\"}", Object.class));
    assertThrows(ClassNotFoundException.class, () -> Class.forName("org.codehaus.jackson.map.ObjectMapper"));
    assertThrows(ClassNotFoundException.class, () -> Class.forName("com.fasterxml.jackson.databind.ObjectMapper"));
    assertThrows(ClassNotFoundException.class, () -> Class.forName("org.apache.http.impl.client.HttpClients"));
  }

  private HttpResponse<String> request(String method, String path, String body, String accept) throws Exception {
    HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
        .timeout(Duration.ofSeconds(15)).header("Accept", accept);
    if (body != null) builder.header("Content-Type", "application/json");
    return client.send(builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
        : HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
  }
}
