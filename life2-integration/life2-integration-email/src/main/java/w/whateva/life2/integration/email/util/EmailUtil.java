package w.whateva.life2.integration.email.util;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiEmailCount;

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


    public static ApiArtifactCount toDto(ApiEmailCount count) {
        ApiArtifactCount result = new ApiArtifactCount();
        result.setYear(count.getYear());
        result.setMonth(count.getMonth());
        result.setCount(count.getCount());
        return result;
    }
}
