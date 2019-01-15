package w.whateva.life2.integration.email.impl;

import com.google.common.collect.Lists;
import w.whateva.life2.api.common.dto.ApiArtifact;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.email.util.EmailUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class EmailProviderImpl implements ArtifactProvider {

    private final EmailOperations emailClient;
    private final String trove;

    public EmailProviderImpl(EmailOperations client, String trove) {
        this.emailClient = client;
        this.trove = trove;
    }

    @Override
    public ApiArtifact read(String key) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to) {
        return emailClient.search(after, before, who, from, to)
                .stream()
                .map(EmailUtil::toDto)
                .map(this::embellish)
                .collect(Collectors.toList());
    }

    public List<List<ApiArtifact>> search(LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to, Integer integer) {
        List<List<ApiArtifact>> result = Lists.newArrayList();
        result.add(search(after, before, who, from, to));
        return result;
    }

    private ApiArtifact embellish(ApiArtifact artifact) {

        artifact.setTrove(trove);

        return artifact;
    }
}