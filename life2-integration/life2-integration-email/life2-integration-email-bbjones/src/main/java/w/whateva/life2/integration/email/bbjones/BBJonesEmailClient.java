package w.whateva.life2.integration.email.bbjones;

import org.springframework.cloud.openfeign.FeignClient;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.integration.email.netflix.EmailClientConfiguration;

@FeignClient(name = "email", url = "${email.bbjones.url}", configuration = EmailClientConfiguration.class)
public interface BBJonesEmailClient extends EmailOperations {}
