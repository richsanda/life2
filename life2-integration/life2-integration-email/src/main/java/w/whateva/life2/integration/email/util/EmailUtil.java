package w.whateva.life2.integration.email.util;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.email.dto.ApiEmail;

public class EmailUtil {

    public static ApiArtifact toDto(ApiEmail email) {
        ApiArtifact artifact = new ApiArtifact();
        artifact.setKey(email.getKey());
        artifact.setFrom(email.getFrom());
        artifact.setFromEmail(email.getFromEmail());
        artifact.setSent(email.getSent());
        artifact.setTo(email.getTo());
        artifact.setToEmails(email.getToEmails());
        artifact.setSubject(email.getSubject());
        artifact.setBody(email.getBody());
        artifact.setBodyHtml(email.isBodyHtml());
        return artifact;
    }
}
