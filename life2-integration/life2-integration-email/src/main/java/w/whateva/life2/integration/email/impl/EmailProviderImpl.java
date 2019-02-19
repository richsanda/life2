package w.whateva.life2.integration.email.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.data.person.domain.Person;
import w.whateva.life2.data.person.repository.PersonRepository;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.email.util.EmailUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EmailProviderImpl implements ArtifactProvider {

    private Logger log = LoggerFactory.getLogger(EmailProviderImpl.class);

    private final EmailOperations emailClient;
    private final PersonRepository personRepository;
    private final Multimap<String, String> troves = HashMultimap.create();

    public EmailProviderImpl(EmailOperations client, Map<String, List<String>> troves, PersonRepository personRepository) {
        this.emailClient = client;
        this.personRepository = personRepository;
        troves.forEach(this.troves::putAll);
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key) {

        if (!hasTrove(owner, trove)) return null;

        ApiEmail email = emailClient.read(key);

        if (null == email) return null;

        ApiArtifact result = EmailUtil.toDto(email);
        result.setOwner(owner);
        result.setTrove(trove);
        return result;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to) {

        Set<String> whoEmails = getEmailAddresses(who);
        Set<String> fromEmails = getEmailAddresses(from);
        Set<String> toEmails = getEmailAddresses(to);

        return emailClient.search(after, before, whoEmails, fromEmails, toEmails)
                .stream()
                .map(EmailUtil::toDto)
                .map(this::embellish)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        log.info("Searching email troves: " + troves);

        return search(
                searchSpec.getAfter(),
                searchSpec.getBefore(),
                CollectionUtils.isEmpty(searchSpec.getWho()) ? null : new HashSet<>(searchSpec.getWho()),
                CollectionUtils.isEmpty(searchSpec.getFrom()) ? null : new HashSet<>(searchSpec.getFrom()),
                CollectionUtils.isEmpty(searchSpec.getTo()) ? null : new HashSet<>(searchSpec.getTo()));
    }

    public List<List<ApiArtifact>> search(String owner, LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to, Integer integer) {
        List<List<ApiArtifact>> result = Lists.newArrayList();
        result.add(search(after, before, who, from, to));
        return result;
    }

    // for now still assume only one trove is supported here...
    private ApiArtifact embellish(ApiArtifact artifact) {

        Map.Entry<String, String> trove = troves.entries().stream().findFirst().orElseThrow(RuntimeException::new);

        artifact.setOwner(trove.getKey());
        artifact.setTrove(trove.getValue());

        return artifact;
    }

    private boolean hasTrove(String owner, String trove) {
        return troves.containsKey(owner) && troves.get(owner).contains(trove);
    }

    private Set<String> getEmailAddresses(Set<String> names) {

        if (CollectionUtils.isEmpty(names)) return null; // null means unspecified

        return personRepository.findByNameIn(names)
                .stream()
                .map(Person::getEmails)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}