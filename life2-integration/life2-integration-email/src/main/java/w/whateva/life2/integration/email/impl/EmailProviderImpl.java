package w.whateva.life2.integration.email.impl;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.common.dto.ApiArtifact;
import w.whateva.life2.api.common.dto.ApiArtifactSearchSpec;
import w.whateva.life2.api.common.dto.ApiPersonKey;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.email.util.EmailUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailProviderImpl implements ArtifactProvider {

    private final EmailOperations emailClient;
    private final Set<String> troves;

    public EmailProviderImpl(EmailOperations client, Set<String> troves) {
        this.emailClient = client;
        this.troves = troves;
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key) {

        if (!hasTrove(trove)) return null;

        ApiEmail email = emailClient.read(key);

        if (null == email) return null;

        ApiArtifact result = EmailUtil.toDto(email);
        result.setOwner(owner);
        result.setTrove(trove);
        return result;
    }

    // TODO: almost time for a search spec
    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to) {
        return emailClient.search(after, before, who, from, to)
                .stream()
                .map(EmailUtil::toDto)
                .map(this::embellish)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        return search(
                searchSpec.getAfter(),
                searchSpec.getBefore(),
                convertToNameKeys(searchSpec.getWho()),
                convertToNameKeys(searchSpec.getFrom()),
                convertToNameKeys(searchSpec.getTo()));
    }

    public List<List<ApiArtifact>> search(String owner, LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to, Integer integer) {
        List<List<ApiArtifact>> result = Lists.newArrayList();
        result.add(search(after, before, who, from, to));
        return result;
    }

    private ApiArtifact embellish(ApiArtifact artifact) {

        artifact.setTrove(troves.stream().findFirst().orElseThrow(RuntimeException::new));

        return artifact;
    }

    private boolean hasTrove(String trove) {
        return troves.contains(trove);
    }

    // TODO: remove this, it's just for temporary backwards compatibility
    private static HashSet<String> convertToNameKeys(Set<ApiPersonKey> personKeys) {
        if (CollectionUtils.isEmpty(personKeys)) return null;
        return personKeys.stream().map(ApiPersonKey::getNameKey).distinct().collect(Collectors.toCollection(HashSet::new));
    }
}