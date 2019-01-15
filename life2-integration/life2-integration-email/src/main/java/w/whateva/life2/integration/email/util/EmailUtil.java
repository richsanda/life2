package w.whateva.life2.integration.email.util;

import w.whateva.life2.api.common.dto.ApiArtifact;
import w.whateva.life2.api.email.dto.ApiEmail;

public class EmailUtil {

    public static ApiArtifact toDto(ApiEmail email) {
        ApiArtifact artifact = new ApiArtifact();
        artifact.setKey(email.getKey());
        artifact.setFrom(email.getFrom());
        artifact.setSent(email.getSent());
        artifact.setTo(email.getTo());
        artifact.setSubject(email.getSubject());
        // artifact.setBody(email.getBody());
        return artifact;
    }
}
