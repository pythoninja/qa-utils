package icu.random.client.fakedata;

import icu.random.client.rest.RestClient;
import icu.random.dto.fakedata.AddressDto;
import icu.random.dto.fakedata.PersonDto;
import icu.random.dto.fakedata.UuidDto;
import icu.random.exception.HttpClientFailureException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import kong.unirest.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FakedataClientImpl implements FakedataClient {

  public static final String LOCALE_ROUTE_PARAM = "locale";
  public static final String ENDPOINT_ROUTE_PARAM = "endpoint";

  private final RestClient httpClient;

  @Value("${randomicu.fakedata.remote-url}")
  private String fakedataUrl;

  @Value("${randomicu.fakedata.remote-url.locale}")
  private String fakedataLocale;

  @Value("${randomicu.fakedata.remote-url.address-endpoint}")
  private String addressEndpoint;

  @Value("${randomicu.fakedata.remote-url.person-endpoint}")
  private String personEndpoint;

  @Value("${randomicu.fakedata.remote-url.uuid-endpoint}")
  private String uuidEndpoint;

  @Autowired
  public FakedataClientImpl(RestClient client) {
    this.httpClient = client;
  }

  @Override
  public HttpResponse<AddressDto> getAdress(String language) {
    return this.getResponse(
        Map.of(
            LOCALE_ROUTE_PARAM, language,
            ENDPOINT_ROUTE_PARAM, addressEndpoint),
        AddressDto.class
    );
  }

  @Override
  public HttpResponse<PersonDto> getPerson(String language) {
    return this.getResponse(
        Map.of(
            LOCALE_ROUTE_PARAM, language,
            ENDPOINT_ROUTE_PARAM, personEndpoint),
        PersonDto.class
    );
  }

  @Override
  public HttpResponse<UuidDto> getUuid(String version, boolean uppercase) {
    return this.getResponse(
        Map.of(ENDPOINT_ROUTE_PARAM, uuidEndpoint),
        Map.of("uppercase", uppercase),
        UuidDto.class);
  }

  private <T> HttpResponse<T> getResponse(Map<String, Object> routeParams, Class<T> clazz) {
    return getResponse(routeParams, Map.of(), clazz);
  }

  private <T> HttpResponse<T> getResponse(Map<String, Object> routeParams, Map<String, Object> queryParams, Class<T> clazz) {
    String realUrl;
    String endpointPlaceholder = "{endpoint}";

    if (routeParams.containsKey(LOCALE_ROUTE_PARAM)) {
      log.debug("Params map contains locale");
      realUrl = "%s/%s/%s".formatted(fakedataUrl, fakedataLocale, endpointPlaceholder);
    } else {
      realUrl = "%s/%s".formatted(fakedataUrl, endpointPlaceholder);
    }

    log.debug("Raw url before Unirest parse it: {}", realUrl);
    var request = httpClient.get(realUrl).routeParam(routeParams).queryString(queryParams);

    var url = request.getUrl();
    log.info("Prepare request to url: {}", URLDecoder.decode(url, Charset.defaultCharset()));

    return request.asObject(clazz)
        .ifSuccess(r -> log.info("Request finished successfully. Status code: {}", r.getStatus()))
        .ifFailure(r -> {
          var status = r.getStatus();
          log.error("Request finished with error. Status code: {}", status);

          r.getParsingError().ifPresent(e -> {
            log.error("Parsing Exception: {}", e.toString());
            log.error("Original Body: {}", e.getOriginalBody());
          });

          if (status == HttpStatus.NOT_FOUND.value()) {
            throw new HttpClientFailureException(String.format("Status code from Fakedata backend: %d. " +
                "Possible invalid url: %s", status, url), HttpStatus.NOT_FOUND);
          }

          throw new HttpClientFailureException(String.format("Status code from Fakedata backend: %d. " +
              "Possible invalid url: %s", status, url), HttpStatus.BAD_REQUEST);
        });
  }
}
