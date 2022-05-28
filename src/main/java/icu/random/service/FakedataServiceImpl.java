package icu.random.service;

import icu.random.client.fakedata.FakedataClient;
import icu.random.dto.fakedata.AddressDto;
import icu.random.dto.fakedata.PersonDto;
import icu.random.dto.fakedata.UuidDto;
import icu.random.exception.IncorrectLanguageException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FakedataServiceImpl implements FakedataService {

  private static final Set<String> ALLOWED_LANGUAGES = Set.of("ru", "en");
  private final FakedataClient client;

  @Autowired
  public FakedataServiceImpl(FakedataClient client) {
    this.client = client;
  }

  @Override
  public AddressDto getAddress(String language) {
    this.checkLanguage(language);

    return this.client.getAdress(language).getBody();
  }

  @Override
  public PersonDto getPerson(String language) {
    this.checkLanguage(language);

    return this.client.getPerson(language).getBody();
  }

  @Override
  public UuidDto getUuid(String version, boolean uppercase) {
    return this.client.getUuid(version, uppercase).getBody();
  }

  private void checkLanguage(String language) {
    if (!ALLOWED_LANGUAGES.contains(language)) {
      throw new IncorrectLanguageException(String.format("Incorrect locale, use one of: %s", ALLOWED_LANGUAGES));
    }
  }
}
