package w.whateva.life2.job.email.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.dto.ApiEmail;

import java.util.List;

public class EmailWriter implements ItemWriter<ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(EmailWriter.class);

    private final EmailOperations emailOperations;

    private final String troveName;
    private final String troveOwner;

    @Autowired
    public EmailWriter(EmailOperations emailOperations, String troveName, String troveOwner) {
        this.emailOperations = emailOperations;
        this.troveName = troveName;
        this.troveOwner = troveOwner;
    }

    public void write(List<? extends ApiEmail> apiEmails) {

        apiEmails.forEach(e -> {
            e.setTrove(troveName);
            e.setOwner(troveOwner);
            log.info("writing email with key: " + e.getKey());
            emailOperations.add(e);
        });

        log.info(String.format("wrote %d emails...", apiEmails.size()));
    }
}
