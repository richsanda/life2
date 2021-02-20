package w.whateva.life2.integration.email.impl;

import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
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
import java.util.stream.Stream;

public class EmailProviderImpl implements ArtifactProvider {

    private Logger log = LoggerFactory.getLogger(EmailProviderImpl.class);

    private final EmailOperations emailClient;
    private final PersonRepository personRepository;
    private final Multimap<String, String> troves = HashMultimap.create();

    private final Map<String, String> emailsToPersons = Maps.newHashMap();

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
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {

        who = Stream.of(who, getGroups(who)).flatMap(Set::stream).collect(Collectors.toSet());

        Set<String> whoEmails = getEmailAddresses(who);
        Set<String> fromEmails = getEmailAddresses(from);
        Set<String> toEmails = getEmailAddresses(to);

        if ((null != fromEmails && 0 == fromEmails.size()) || (null != toEmails && 0 == toEmails.size())) {
            log.warn("from or to not found... no results.");
            return Collections.emptyList();
        }

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
                processPersonKeys(searchSpec.getWho()),
                processPersonKeys(searchSpec.getFrom()),
                processPersonKeys(searchSpec.getTo()));
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {

        who = Stream.of(who, getGroups(who)).flatMap(Set::stream).collect(Collectors.toSet());

        Set<String> whoEmails = getEmailAddresses(who);
        Set<String> fromEmails = getEmailAddresses(from);
        Set<String> toEmails = getEmailAddresses(to);

        if ((null != fromEmails && 0 == fromEmails.size()) || (null != toEmails && 0 == toEmails.size())) {
            log.warn("from or to not found... no results.");
            return Collections.emptyList();
        }

        return emailClient.count(after, before, whoEmails, fromEmails, toEmails)
                .stream()
                .map(EmailUtil::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {

        log.info("Searching email troves: " + troves);

        return count(
                searchSpec.getAfter(),
                searchSpec.getBefore(),
                processPersonKeys(searchSpec.getWho()),
                processPersonKeys(searchSpec.getFrom()),
                processPersonKeys(searchSpec.getTo()));
    }

    private Set<String> processPersonKeys(Set<String> keys) {
        if (CollectionUtils.isEmpty(keys)) return null;
        return keys.stream().map(String::toLowerCase).collect(Collectors.toSet());
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
        artifact.setFromEmail(emailToPersonName(artifact.getFromEmail()));
        artifact.setToEmails(emailToPersonNames(artifact.getToEmails()));

        return artifact;
    }

    private Set<String> emailToPersonNames(Set<String> emails) {
        Set<String> result = Sets.newHashSet();
        Set<String> lookup = Sets.newHashSet();
        emails.forEach(e -> {
            if (emailsToPersons.containsKey(e)) {
                result.add(emailsToPersons.get(e));
            } else {
                lookup.add(e);
            }
        });
        if (!CollectionUtils.isEmpty(lookup)) {
            Set<Person> persons = personRepository.findByEmailsIn(emails);
            if (!CollectionUtils.isEmpty(persons)) {
                persons.forEach(p -> {
                    p.getEmails().forEach(e -> {
                        emailsToPersons.put(e, p.getName());
                        result.add(p.getName());
                    });
                });
            }
        }
        return result;
    }

    private String emailToPersonName(String email) {
        return emailToPersonNames(Sets.newHashSet(email)).stream().findFirst().orElse(null);
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

    private Set<String> getGroups(Set<String> names) {

        if (CollectionUtils.isEmpty(names)) return null; // null means unspecified

        return personRepository.findByNameIn(names)
                        .stream()
                        .map(Person::getGroups)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet());
    }
}