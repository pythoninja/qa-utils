package icu.random.controller.web;

import static icu.random.api.WebControllerPaths.WEB_INDEX_PATH;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

  @RequestMapping(value = WEB_INDEX_PATH, method = RequestMethod.GET)
  public String index() {
    return "index";
  }
}
