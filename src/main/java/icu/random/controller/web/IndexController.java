package icu.random.controller.web;

import static icu.random.api.WebControllerPaths.WEB_INDEX_PATH;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping(WEB_INDEX_PATH)
  public String index() {
    return "index";
  }
}
