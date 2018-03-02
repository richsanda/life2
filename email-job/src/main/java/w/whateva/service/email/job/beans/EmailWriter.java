package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.email.api.dto.DtoEmail;

import java.util.List;

public class EmailWriter implements ItemWriter<DtoEmail> {

    private final EmailOperations emailOperations;

    @Autowired
    public EmailWriter(EmailOperations emailOperations) {
        this.emailOperations = emailOperations;
    }

    public void write(List<? extends DtoEmail> apiEmails) throws Exception {
        for (DtoEmail apiEmail : apiEmails)
        emailOperations.addEmail(apiEmail);
    }
}
