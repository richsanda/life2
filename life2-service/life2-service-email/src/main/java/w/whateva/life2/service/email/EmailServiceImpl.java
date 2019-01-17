package w.whateva.life2.service.email;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.email.EmailService;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.domain.Person;
import w.whateva.life2.data.email.repository.EmailRepository;
import w.whateva.life2.data.email.repository.PersonDao;
import w.whateva.life2.data.email.repository.PersonRepository;

import javax.mail.internet.InternetAddress;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final PersonRepository personRepository;
    private final PersonDao personDao;

    private final EmailServiceConfigurationProperties.AddressStyle addressStyle;
    private final String groupAddress;

    @Autowired
    public EmailServiceImpl(EmailServiceConfigurationProperties configurationProperties, EmailRepository emailRepository, PersonRepository personRepository, PersonDao personDao) {

        this.emailRepository = emailRepository;
        this.personRepository = personRepository;
        this.personDao = personDao;

        this.groupAddress = null != configurationProperties.getGroup() &&
                null != configurationProperties.getGroup().getRecipient() ?
                configurationProperties.getGroup().getRecipient() :
                null;

        this.addressStyle = null != configurationProperties.getAddress() &&
                null != configurationProperties.getAddress().getStyle() ?
                configurationProperties.getAddress().getStyle() :
                null;
    }

    public void add(ApiEmail apiEmail) {

        Email email = new Email();

        email.setId(apiEmail.getKey()); // eh, for now

        BeanUtils.copyProperties(apiEmail, email);

        if (null != groupAddress) {
            email.setTo(groupAddress);
            email.setGroup(true);
        }

        switch (addressStyle) {
            case SIMPLE:
                email.setToIndex(toSimpleAddresses(email.getTo()));
                email.setFromIndex(toSimpleAddresses(email.getFrom()).stream().findFirst().orElse(null));
                break;
            case INTERNET:
                email.setToIndex(toEmailAddresses(email.getTo()));
                email.setFromIndex(toEmailAddresses(email.getFrom()).stream().findFirst().orElse(null));
                break;
            default:
                throw new IllegalArgumentException("Unknown email address parser type");
        }

        try {
            emailRepository.save(email);
        } catch (Exception e) {
            System.out.println("This one failed to save" + email.getKey());
        }
    }

    @Override
    public ApiEmail read(String key) {
        Email email = emailRepository.findById(key).orElse(null);
        return toApi(email);
    }

    @Override
    public List<ApiEmail> search(LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to) {

        Set<String> whoEmails = getEmailAddresses(who);
        Set<String> fromEmails = getEmailAddresses(from);
        Set<String> toEmails = getEmailAddresses(to);

        return personDao.getEmails(
                whoEmails,
                fromEmails,
                toEmails,
                null == after ? null : after.atStartOfDay(),
                null == before ? null : before.atStartOfDay().plusDays(1))
                .stream()
                .map(EmailServiceImpl::toSummaryApi)
                .collect(Collectors.toList());
    }

    private Set<String> getEmailAddresses(Set<String> names) {

        if (CollectionUtils.isEmpty(names)) return null; // null means unspecified

        return personRepository.findByNameIn(names)
                .stream()
                .map(Person::getEmails)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public void addGroupAddressToSenders() {

        if (null == groupAddress) return;

        personDao.getSenders().forEach(person -> {
            if (null == person.getEmails()) person.setEmails(Collections.emptySet());
            System.out.println("name is: " + person.getName());
            person.getEmails().add(groupAddress);
            personRepository.save(person);
        });
    }

    public static ApiEmail toApi(Email email) {
        if (null == email) return null;
        ApiEmail ApiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, ApiEmail);
        return ApiEmail;
    }

    public static ApiEmail toSummaryApi(Email email) {
        if (null == email) return null;
        ApiEmail ApiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, ApiEmail, "body");
        return ApiEmail;
    }

    private static Set<String> toSimpleAddresses(String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        return Arrays
                .stream(addressList.split("\\s*[,;]\\s*"))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private static Set<String> toEmailAddresses (String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        try {
            return Arrays
                    .stream(InternetAddress.parse(addressList))
                    .map(InternetAddress::getAddress)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println("Problem with: " + addressList);
            // e.printStackTrace();
        }
        return new HashSet<>();
    }
}
