package icu.random.util.healthcheck;

import icu.random.client.rest.RestClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("fakedata-backend")
public class FakedataHealthIndicator implements HealthIndicator {

  @Value("${randomicu.fakedata.host}")
  private String host;

  @Value("${randomicu.fakedata.port}")
  private Integer port;

  @Value("${randomicu.fakedata.healthcheck}")
  private String healthcheck;

  private final RestClient restClient;

  private final Map<String, String> details = new HashMap<>();
  private final int unirestConnectionTimeout = Unirest.config().getConnectionTimeout();

  @Autowired
  public FakedataHealthIndicator(RestClient restClient) {
    this.restClient = restClient;
  }


  @Override
  public Health health() {
    if (!isRunning()) {
      return Health.down().withDetails(details).build();
    } else {
      return Health.up().withDetails(details).build();
    }
  }

  @NotNull
  private Boolean isRunning() {
    log.debug("Trying to open socket to {}:{}", host, port);

    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), 300);

      log.debug("Remove error from details map");
      details.remove("error");
      log.debug("Successully opened socket to {}:{}", host, port);
    } catch (IOException e) {
      log.debug("Put error message to details map");
      details.put("error", "Can't connect to the fakedata-backend server");

      log.debug("Remove version from details map");
      details.remove("version");

      log.error("Failed to open socket to {}:{}", host, port);
      return false;
    }

    log.info("Fetching version from fakedata-backend...");
    log.info("Unirest connection timeout (ms): {}", unirestConnectionTimeout);
    var response = restClient.get(
        String.format("http://%s:%s/%s", host, port, healthcheck)
    ).asString();

    var statusCode = response.getStatus();
    var body = response.getBody();
    var versionPattern = Pattern.compile("\"(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\"");
    var matcher = versionPattern.matcher(body);

    String fakedataVersion = matcher.find() ? matcher.group(1) : "unknown";
    log.info("Put fakedataVersion: {} to the healthcheck details section", fakedataVersion);
    details.put("version", fakedataVersion);

    var expectedBody = String.format("{\"version\":\"%s\",\"status\":\"UP\"}", fakedataVersion);

    log.info("Status code: {}", statusCode);
    log.info("Healthcheck response: {}", body);

    if (response.getStatus() != 200) {
      log.error("Healtcheck failed - can't find healthcheck endpoint");
      details.put("healthcheck", "Can't find healthcheck endpoint");
      return false;
    }

    if (!body.equals(expectedBody)) {
      log.error("Healtcheck failed - response body is not as expected");
      log.error("Expected: {}, got: {}", expectedBody, body);
      details.put("healthcheck", "Response from healthcheck endpoind is invalid");
      return false;
    }

    return true;
  }
}
