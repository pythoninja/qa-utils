package icu.random.dto.fakedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UuidDto {

  private Map<String, String> uuid = Map.of();
  private boolean uppercase;
}
