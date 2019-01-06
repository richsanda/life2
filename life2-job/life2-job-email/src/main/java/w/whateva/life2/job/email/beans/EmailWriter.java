package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.dto.ApiEmail;

import java.util.List;

public class EmailWriter implements ItemWriter<ApiEmail> {

    private final EmailOperations emailOperations;

    @Autowired
    public EmailWriter(EmailOperations emailOperations) {
        this.emailOperations = emailOperations;
    }

    public void write(List<? extends ApiEmail> apiEmails) {
        apiEmails.forEach(emailOperations::add);
    }
}
