package w.whateva.life2.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.email.EmailService;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiEmailCount;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.domain.EmailMonthYearCount;
import w.whateva.life2.data.email.repository.EmailRepository;
import w.whateva.life2.data.email.repository.EmailDao;
import w.whateva.life2.data.person.repository.PersonRepository;

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

    private transient Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final EmailRepository emailRepository;
    private final PersonRepository personRepository;
    private final EmailDao emailDao;

    private final EmailServiceConfigurationProperties.AddressStyle addressStyle;
    private final String groupAddress;

    @Autowired
    public EmailServiceImpl(EmailServiceConfigurationProperties configurationProperties, EmailRepository emailRepository, PersonRepository personRepository, EmailDao emailDao) {

        this.emailRepository = emailRepository;
        this.personRepository = personRepository;
        this.emailDao = emailDao;

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
            email.setBodyHtml(true);
        }

        String separator;

        switch (addressStyle) {
            case SIMPLE:
                separator =  "\\s*[;,]\\s*";
                email.setToIndex(toSimpleAddresses(email.getTo(), separator));
                email.setFromIndex(toSimpleAddresses(email.getFrom(), separator).stream().findFirst().orElse(null));
                break;
            case SIMPLE_SEMICOLON:
                separator =  "\\s*[;]\\s*";
                email.setToIndex(toSimpleAddresses(email.getTo(), separator));
                email.setFromIndex(toSimpleAddresses(email.getFrom(), separator).stream().findFirst().orElse(null));
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
            log.error("Failed to save email with key: " + email.getKey());
        }
    }

    @Override
    public ApiEmail read(String key) {
        Email email = emailRepository.findById(key).orElse(null);
        return toApi(email);
    }

    @Override
    public List<ApiEmailCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {

        return emailDao.getMonthYearCounts(who,
                from,
                to,
                null == after ? null : after.atStartOfDay(),
                null == before ? null : before.atStartOfDay().plusDays(1)).stream()
                .map(EmailServiceImpl::toApi)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiEmail> search(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {

        log.info("Searching emails..." + after + " / " + before + " / " + who + " / " + from + " / " + to);

        try {

            List<ApiEmail> result = emailDao.getEmails(
                    who,
                    from,
                    to,
                    null == after ? null : after.atStartOfDay(),
                    null == before ? null : before.atStartOfDay().plusDays(1))
                    .stream()
                    .map(EmailServiceImpl::toSummaryApi)
                    .collect(Collectors.toList());

            log.info(String.format("Found %d emails", result.size()));

            return result;

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void addGroupAddressToSenders() {

        if (null == groupAddress) return;

        emailDao.getSenders().forEach(person -> {
            if (null == person.getEmails()) person.setEmails(Collections.emptySet());
            log.info(String.format("Adding group address %s to: %s", groupAddress, person.getName()));
            person.getEmails().add(groupAddress);
            personRepository.save(person);
        });
    }

    public static ApiEmailCount toApi(EmailMonthYearCount count) {
        ApiEmailCount result = new ApiEmailCount();
        BeanUtils.copyProperties(count, result);
        return result;
    }

    public static ApiEmail toApi(Email email) {
        if (null == email) return null;
        ApiEmail result = new ApiEmail();
        BeanUtils.copyProperties(email, result);
        result.setToEmails(email.getToIndex());
        result.setFromEmail(email.getFromIndex());
        return result;
    }

    public static ApiEmail toSummaryApi(Email email) {
        if (null == email) return null;
        ApiEmail result = new ApiEmail();
        BeanUtils.copyProperties(email, result, "body");
        result.setToEmails(email.getToIndex());
        result.setFromEmail(email.getFromIndex());
        return result;
    }

    private static Set<String> toSimpleAddresses(String addressList, String regex) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        return Arrays
                .stream(addressList.split(regex))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private Set<String> toEmailAddresses (String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        try {
            return Arrays
                    .stream(InternetAddress.parse(addressList))
                    .map(InternetAddress::getAddress)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Problem parsing internet email address list: " + addressList);
        }
        return new HashSet<>();
    }
}
