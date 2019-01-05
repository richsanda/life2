package w.whateva.life2.integration.email.util;

import w.whateva.life2.api.common.dto.ApiArtifact;
import w.whateva.life2.api.email.dto.ApiEmail;

public class EmailUtil {

    public static ApiArtifact toDto(ApiEmail email) {
        ApiArtifact shred = new ApiArtifact();
        shred.setFrom(email.getFrom());
        shred.setSent(email.getSent());
        shred.setTo(email.getTo());
        shred.setSubject(email.getSubject());
        shred.setBody(email.getBody());
        return shred;
    }
}
