package icu.random.service;

import icu.random.dto.fakedata.AddressDto;
import icu.random.dto.fakedata.PersonDto;
import icu.random.dto.fakedata.UuidDto;

public interface FakedataService {
  AddressDto getAddress(String language);
  PersonDto getPerson(String language);
  UuidDto getUuid(String version, boolean uppercase);
}
