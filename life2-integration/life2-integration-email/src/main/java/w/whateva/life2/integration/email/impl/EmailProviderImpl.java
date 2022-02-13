package w.whateva.life2.integration.email.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.repository.EmailDao;
import w.whateva.life2.data.email.repository.EmailRepository;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.person.domain.Person;
import w.whateva.life2.data.person.repository.PersonRepository;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.artifact.ArtifactProviderBase;
import w.whateva.life2.integration.email.util.EmailUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static w.whateva.life2.integration.email.util.EmailUtil.EMAIL_PIN_TYPE;

@Slf4j
public class EmailProviderImpl extends ArtifactProviderBase<Email> {

    private final EmailRepository emailRepository;
    private final EmailDao emailDao;
    private final PersonRepository personRepository;
    private final Multimap<String, String> troves = HashMultimap.create();

    private final Map<String, String> emailsToPersons = Maps.newHashMap();

    public EmailProviderImpl(EmailRepository emailRepository, Map<String, List<String>> troves, EmailDao emailDao, PersonRepository personRepository, NoteDao noteDao, PinDao pinDao) {
        super(noteDao, pinDao);
        this.emailRepository = emailRepository;
        this.emailDao = emailDao;
        this.personRepository = personRepository;
        troves.forEach(this.troves::putAll);
    }

    @Override
    public Email read(String owner, String trove, String key) {
        return emailRepository.findUniqueByKey(key);
    }

    @Override
    public ApiArtifact toDto(Email email, Note note, RelativesAndIndex relativesAndIndex) {
        return EmailUtil.toDto(email);
    }

    @Override
    protected String getPinType() {
        return EMAIL_PIN_TYPE;
    }

    @Override
    protected List<Email> allItemsByOwnerAndTrove(String owner, String trove) {
        return emailRepository.findAllByOwnerAndTrove(owner, trove);
    }

    @Override
    protected String getKey(Email email) {
        return email.getKey();
    }

    @Override
    protected String getTrove(Email email) {
        return email.getTrove();
    }

    @Override
    protected List<Pin> toIndexPins(Email email) {
        return singletonList(EmailUtil.toIndexPin(email));
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, Set<String> from, Set<String> to, String text) {

        who = Stream.of(who, getGroups(who)).flatMap(Set::stream).collect(Collectors.toSet());

        Set<String> whoEmails = getEmailAddresses(who);
        Set<String> fromEmails = getEmailAddresses(from);
        Set<String> toEmails = getEmailAddresses(to);

        if ((null != fromEmails && 0 == fromEmails.size()) || (null != toEmails && 0 == toEmails.size())) {
            log.warn("from or to not found... no results.");
            return Collections.emptyList();
        }

        return emailDao.getEmails(whoEmails, fromEmails, toEmails, after.atStartOfDay(), before.plusDays(1).atStartOfDay())
                .stream()
                .map(EmailUtil::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, String text) {

        who = Stream.of(who, getGroups(who)).flatMap(Set::stream).collect(Collectors.toSet());

        Set<String> whoEmails = getEmailAddresses(who);
        Set<String> fromEmails = getEmailAddresses(emptySet()); // TODO: bring this back
        Set<String> toEmails = getEmailAddresses(emptySet()); // TODO: bring this back

        if ((null != fromEmails && 0 == fromEmails.size()) || (null != toEmails && 0 == toEmails.size())) {
            log.warn("from or to not found... no results.");
            return Collections.emptyList();
        }

        return emailDao.getMonthYearCounts(whoEmails, fromEmails, toEmails, after.atStartOfDay(), before.plusDays(1).atStartOfDay())
                .stream()
                .map(EmailUtil::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Integer index(String owner, String trove) {
        return (int)emailRepository.findAllByOwnerAndTrove(owner, trove)
                .stream()
                .map(this::index)
                .count();
    }

    private Set<String> processPersonKeys(Set<String> keys) {
        if (CollectionUtils.isEmpty(keys)) return null;
        return keys.stream().map(String::toLowerCase).collect(Collectors.toSet());
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