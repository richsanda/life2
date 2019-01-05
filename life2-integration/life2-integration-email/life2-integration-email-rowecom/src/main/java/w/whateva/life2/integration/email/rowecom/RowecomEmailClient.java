package w.whateva.life2.integration.email.rowecom;

import org.springframework.cloud.openfeign.FeignClient;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.integration.email.netflix.EmailClientConfiguration;

@FeignClient(name = "email", url = "${email.rowecom.url}", configuration = EmailClientConfiguration.class)
public interface RowecomEmailClient extends EmailOperations {}
