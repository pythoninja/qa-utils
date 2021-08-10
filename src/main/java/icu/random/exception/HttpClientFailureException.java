package icu.random.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class HttpClientFailureException extends RuntimeException {

  private final HttpStatus status;

  public HttpClientFailureException(String message, HttpStatus status) {
    super(message);

    this.status = status;
  }
}
