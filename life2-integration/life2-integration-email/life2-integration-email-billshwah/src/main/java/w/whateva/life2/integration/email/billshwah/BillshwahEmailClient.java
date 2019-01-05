package w.whateva.life2.integration.email.billshwah;

import org.springframework.cloud.openfeign.FeignClient;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.integration.email.netflix.EmailClientConfiguration;

@FeignClient(name = "email", url = "${email.bill_shwah.url}", configuration = EmailClientConfiguration.class)
public interface BillshwahEmailClient extends EmailOperations {}
