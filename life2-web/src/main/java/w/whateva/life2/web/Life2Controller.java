package w.whateva.life2.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Life2Controller {

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }
}
