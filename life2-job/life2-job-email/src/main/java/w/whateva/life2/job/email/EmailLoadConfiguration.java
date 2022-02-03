package w.whateva.life2.job.email;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EmailLoadConfiguration {

    @Value("${email.trove.name}")
    private String emailTroveName;

    @Value("${email.trove.owner}")
    private String emailTroveOwner;
}
