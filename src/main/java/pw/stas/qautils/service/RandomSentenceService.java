package pw.stas.qautils.service;

import pw.stas.qautils.model.Sentence;

public interface RandomSentenceService {

  Sentence getRandomSentence();
  void setSymbolsCount(int symbolsCount);

}
