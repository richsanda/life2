package w.whateva.service.life2.integration.email.rowecom.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.life2.api.dto.DtoShred;
import w.whateva.service.life2.integration.api.ShredProvider;
import w.whateva.service.life2.integration.email.rowecom.RowecomEmailClient;
import w.whateva.service.life2.integration.email.util.EmailUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableFeignClients(basePackageClasses = RowecomEmailClient.class)
public class RowecomEmailServiceImpl implements ShredProvider {


    @Autowired
    private RowecomEmailClient emailClient;

    @Override
    public List<String> allKeys() {
        return null;
    }

    @Override
    public DtoShred readShred(String trove, String key) {
        return null;
    }

    @Override
    public List<DtoShred> allShreds(LocalDate after, LocalDate before, HashSet<String> names) {
        return emailClient.allEmails(after, before, names)
                .stream()
                .map(EmailUtil::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<List<DtoShred>> allShreds(LocalDate after, LocalDate before, HashSet<String> names, Integer integer) {
        List<List<DtoShred>> result = Lists.newArrayList();
        result.add(allShreds(after, before, names));
        return result;
    }
}