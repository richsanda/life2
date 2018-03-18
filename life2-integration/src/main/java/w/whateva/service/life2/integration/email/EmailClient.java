package w.whateva.service.life2.integration.email;

import org.springframework.cloud.openfeign.FeignClient;
import w.whateva.service.email.api.EmailOperations;

@FeignClient(name = "email", url = "localhost:8990", configuration = EmailClientConfiguration.class)
public interface EmailClient extends EmailOperations {}
