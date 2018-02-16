package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.sao.ApiEmail;

import java.util.List;

public class EmailWriter implements ItemWriter<ApiEmail> {

    private final EmailService emailService;

    @Autowired
    public EmailWriter(EmailService emailService) {
        this.emailService = emailService;
    }

    public void write(List<? extends ApiEmail> apiEmails) throws Exception {
        for (ApiEmail apiEmail : apiEmails)
        emailService.addEmail(apiEmail);
    }
}
