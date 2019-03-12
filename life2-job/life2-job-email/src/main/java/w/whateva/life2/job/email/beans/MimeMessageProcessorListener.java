package w.whateva.life2.job.email.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.job.email.util.MimeMessageUtility;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.stream.Collectors;

public class MimeMessageProcessorListener implements ItemProcessListener<MimeMessage, ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(MimeMessageProcessorListener.class);

    @Override
    public void beforeProcess(MimeMessage message) {

    }

    @Override
    public void afterProcess(MimeMessage message, ApiEmail apiEmail) {

    }

    @Override
    public void onProcessError(MimeMessage message, Exception e) {
        try {
            log.error(e.getMessage() + "; headers are...");
            log.error(MimeMessageUtility.getHeaders(message).entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue() + "\n")
                    .collect(Collectors.joining(", ")));
        } catch (Exception e1) {
            log.error("Couldn't parse headers");
        }
    }
}
