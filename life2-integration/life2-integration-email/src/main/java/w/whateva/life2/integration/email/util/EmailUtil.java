package w.whateva.life2.integration.email.util;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.domain.EmailMonthYearCount;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class EmailUtil {

    public static ApiArtifact toDto(Email email) {
        ApiArtifact artifact = new ApiArtifact();
        artifact.setTypes(new LinkedHashSet<>());
        artifact.getTypes().add("email");
        artifact.setKey(email.getKey());
        artifact.setData(new LinkedHashMap<>());
        artifact.getData().put("email", email);
        artifact.setTitle(email.getSubject());
        artifact.setWhen(email.getSent().toLocalDateTime());
        artifact.setTrove(email.getTrove());
        //artifact.set(emailToPersonName(email.getFromEmail()));
        //email.setToEmails(emailToPersonNames(email.getToEmails()));
        return artifact;
    }

    public static ApiArtifactCount toDto(EmailMonthYearCount count) {
        ApiArtifactCount result = new ApiArtifactCount();
        result.setYear(count.getYear());
        result.setMonth(count.getMonth());
        result.setCount(count.getCount());
        return result;
    }
}
