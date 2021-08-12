package icu.random.service;

import icu.random.dto.lipsum.LipsumDto;

public interface LipsumService {

  LipsumDto getBytes(Integer amount, boolean startWithLorem);

  LipsumDto getParagraphs(Integer amount, boolean startWithLorem, boolean additionalBreak);

  LipsumDto getWords(Integer amount, boolean startWithLorem);

  LipsumDto getLists(Integer amount, boolean startWithLorem);
}
