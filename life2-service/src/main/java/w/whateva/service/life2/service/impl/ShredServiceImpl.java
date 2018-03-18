package w.whateva.service.life2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.life2.api.ShredOperations;
import w.whateva.service.life2.api.dto.DtoShred;
import w.whateva.service.life2.integration.email.EmailClient;
import w.whateva.service.life2.service.util.ShredUtility;
import w.whateva.service.life2.service.util.bucket.AbstractLocalDateTimeOperator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
@EnableFeignClients(basePackageClasses = EmailClient.class)
public class ShredServiceImpl implements ShredOperations {

    @Autowired
    EmailClient emailClient;

    private final ShredUtility shredUtility;

    @Autowired
    public ShredServiceImpl(ShredUtility shredUtility) {
        this.shredUtility = shredUtility;
    }

    @Override
    public List<String> allKeys() {
        return null;
    }

    @Override
    public void addShred(DtoShred shred) {

    }

    @Override
    public DtoShred readShred(String key) {
        return null;
    }

    @Override
    public List<DtoShred> allShreds() {
        return null;
    }

    @Override
    public List<DtoShred> allShreds(LocalDate after, LocalDate before, HashSet<String> names) {
        List<DtoEmail> emails = emailClient.allEmails(after, before, names);
        System.out.println(emails.size());
        List<List<DtoEmail>> buckets = shredUtility.putInBuckets(
                emails,
                new AbstractLocalDateTimeOperator<DtoEmail>() {
                    @Override
                    public LocalDateTime apply(DtoEmail shred) {
                        return shred.getSent();
                    }
                },
                100,
                after.atStartOfDay(),
                before.plusDays(1).atStartOfDay()
        );
        return buckets
                .stream()
                .map(l -> l
                        .stream()
                        .map(e -> toDto(e))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static DtoShred toDto(DtoEmail email) {
        DtoShred shred = new DtoShred();
        shred.setFrom(email.getFrom());
        shred.setSent(email.getSent());
        return shred;
    }
}
