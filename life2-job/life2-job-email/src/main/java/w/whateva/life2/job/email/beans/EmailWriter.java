package w.whateva.life2.job.email.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import w.whateva.life2.api.email.EmailService;
import w.whateva.life2.api.email.dto.ApiEmail;

import java.util.List;

public class EmailWriter implements ItemWriter<ApiEmail> {

    private final transient Logger log = LoggerFactory.getLogger(EmailWriter.class);

    private final EmailService emailService;

    private final String troveName;
    private final String troveOwner;

    public EmailWriter(EmailService emailService, String troveName, String troveOwner) {
        this.emailService = emailService;
        this.troveName = troveName;
        this.troveOwner = troveOwner;
    }

    public void write(List<? extends ApiEmail> apiEmails) {

        apiEmails.forEach(e -> {
            e.setTrove(troveName);
            e.setOwner(troveOwner);
            log.info("writing email with key: " + e.getKey());
            emailService.add(e);
        });

        log.info(String.format("wrote %d emails...", apiEmails.size()));
    }
}
