package icu.random.service;

import icu.random.dto.fakedata.AddressDto;
import icu.random.dto.fakedata.PersonDto;
import icu.random.dto.fakedata.UuidDto;
import kong.unirest.HttpResponse;

public interface FakedataService {
  HttpResponse<AddressDto> getAddress(String language);
  HttpResponse<PersonDto> getPerson(String language);
  HttpResponse<UuidDto> getUuid(String version, boolean uppercase);
}
