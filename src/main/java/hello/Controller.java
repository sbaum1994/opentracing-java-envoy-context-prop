package hello;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RestController
public class Controller {

    final org.slf4j.Logger log = LoggerFactory.getLogger(Application.class);

    @RequestMapping("/")
    public String home(@RequestHeader Map<String, String> headers) {
        log.info("x-ot-span-context: {}", headers);
        sleep(); // inject time
        return "Hello Docker World";
    }

    public void sleep() {
        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return;
    }

}