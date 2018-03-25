package w.whateva.service.life2.integration.email;

import org.springframework.cloud.openfeign.FeignClient;
import w.whateva.service.email.api.EmailOperations;

@FeignClient(name = "email", url = "${trove2.url}", configuration = EmailClientConfiguration.class)
public interface Trove2Client extends EmailOperations {}
