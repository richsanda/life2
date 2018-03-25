package w.whateva.service.life2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.life2.api.ShredOperations;
import w.whateva.service.life2.api.dto.DtoShred;
import w.whateva.service.life2.integration.api.ShredProvider;
import w.whateva.service.life2.service.util.ShredUtility;
import w.whateva.service.life2.service.util.bucket.AbstractLocalDateTimeOperator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
public class ShredServiceImpl implements ShredOperations {

    @Autowired
    List<ShredProvider> providers;

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
    public List<List<DtoShred>> allShreds(LocalDate after, LocalDate before, HashSet<String> names) {

        List<List<DtoShred>> shreds = providers.get(0).allShreds(after, before, names);

        List<List<DtoShred>> buckets = shredUtility.putInBuckets(
                shreds.get(0),
                new AbstractLocalDateTimeOperator<DtoShred>() {
                    @Override
                    public LocalDateTime apply(DtoShred shred) {
                        return shred.getSent();
                    }
                },
                100,
                after.atStartOfDay(),
                before.plusDays(1).atStartOfDay()
        );
        return buckets
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }

    private static DtoShred toDto(DtoEmail email) {
        DtoShred shred = new DtoShred();
        shred.setFrom(email.getFrom());
        shred.setSent(email.getSent());
        return shred;
    }
}
