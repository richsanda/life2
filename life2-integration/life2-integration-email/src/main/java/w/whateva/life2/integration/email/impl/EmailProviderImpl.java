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

    private EmailOperations emailClient;

    public EmailProviderImpl(EmailOperations client) {
        this.emailClient = client;
    }

    @Override
    public ApiArtifact read(String key) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> names) {
        return emailClient.search(after, before, names, names, names)
                .stream()
                .map(EmailUtil::toDto)
                .collect(Collectors.toList());
    }

    public List<List<ApiArtifact>> search(LocalDate after, LocalDate before, HashSet<String> names, Integer integer) {
        List<List<ApiArtifact>> result = Lists.newArrayList();
        result.add(search(after, before, names));
        return result;
    }
}