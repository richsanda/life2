package w.whateva.life2.integration.email.util;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiEmailCount;

import java.util.LinkedHashMap;

public class EmailUtil {

    public static ApiArtifact toDto(ApiEmail email) {
        ApiArtifact artifact = new ApiArtifact();
        artifact.setKey(email.getKey());
        artifact.setData(new LinkedHashMap<>());
        artifact.getData().put("email", email);
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
