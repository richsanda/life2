package w.whateva.service.life2.integration.email.util;

import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.life2.api.dto.DtoShred;

public class EmailUtil {

    public static DtoShred toDto(DtoEmail email) {
        DtoShred shred = new DtoShred();
        shred.setFrom(email.getFrom());
        shred.setSent(email.getSent());
        shred.setTo(email.getTo());
        shred.setSubject(email.getSubject());
        shred.setBody(email.getBody());
        return shred;
    }
}
