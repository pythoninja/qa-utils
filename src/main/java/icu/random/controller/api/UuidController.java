package icu.random.controller.api;

import static icu.random.api.ApiControllerPaths.API_ROOT_PATH;
import static icu.random.api.ApiControllerPaths.API_VERSION;
import static icu.random.api.ApiControllerPaths.FAKEDATA_UUID;
import icu.random.dto.fakedata.UuidDto;
import icu.random.service.FakedataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_ROOT_PATH + API_VERSION)
public class UuidController {

  private final FakedataService fakedataService;

  @Autowired
  public UuidController(FakedataService fakedataService) {
    this.fakedataService = fakedataService;
  }

  @GetMapping(FAKEDATA_UUID)
  public UuidDto uuid(@RequestParam(required = false) boolean uppercase,
                      @RequestParam(required = false, defaultValue = "4") String version) {

    return fakedataService.getUuid(version,uppercase).getBody();
  }
}
