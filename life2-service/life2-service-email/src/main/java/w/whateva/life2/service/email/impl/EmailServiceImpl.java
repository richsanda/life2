package w.whateva.life2.service.email.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.repository.EmailDao;
import w.whateva.life2.data.email.repository.EmailRepository;
import w.whateva.life2.data.person.repository.PersonRepository;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.data.pin.repository.PinRepository;
import w.whateva.life2.integration.email.util.EmailUtil;
import w.whateva.life2.service.email.EmailService;
import w.whateva.life2.service.email.EmailServiceConfigurationProperties;
import w.whateva.life2.service.email.dto.ApiEmail;

import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
    private final PinDao pinDao;

    private final EmailServiceConfigurationProperties.AddressStyle addressStyle;
    private final String groupAddress;

    @Autowired
    public EmailServiceImpl(EmailServiceConfigurationProperties configurationProperties, EmailRepository emailRepository, PersonRepository personRepository, EmailDao emailDao, PinDao pinDao, PinRepository pinRepository) {

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

        this.pinDao = pinDao;
    }

    public void add(ApiEmail apiEmail) {

        Email email = new Email();

        email.setId(composeId(apiEmail));

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
            pinDao.update(EmailUtil.index(email));
        } catch (Exception e) {
            log.error("Failed to save email with key: " + email.getKey());
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
            return toSimpleAddresses(addressList, "\\s*[;,]\\s*");
        }
    }

    private String composeId(ApiEmail email) {
        return email.getOwner() + ":" + email.getTrove() + ":" + email.getKey();
    }
}
