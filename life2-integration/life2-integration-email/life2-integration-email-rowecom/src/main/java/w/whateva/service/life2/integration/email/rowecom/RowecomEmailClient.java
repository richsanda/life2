package w.whateva.service.life2.integration.email.rowecom;

import org.springframework.cloud.openfeign.FeignClient;
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.life2.integration.email.EmailClientConfiguration;

@FeignClient(name = "email", url = "${email.rowecom.url}", configuration = EmailClientConfiguration.class)
public interface RowecomEmailClient extends EmailOperations {}
