package w.whateva.life2.integration.email.util;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.domain.EmailMonthYearCount;
import w.whateva.life2.data.pin.domain.Pin;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.singleton;

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

    public static Pin index(Email email) {

        return Pin.builder()
                .id(email.getId())
                .key(email.getKey())
                .owner(email.getOwner())
                .trove(email.getTrove())
                .text(email.getBody())
                .when(email.getSent())
                .title(email.getSubject())
                .to(email.getToIndex())
                .types(email.isGroup() ? Set.of("email", "email_group") : Set.of("email"))
                .from(singleton(email.getFromIndex()))
                .build();
    }
}
