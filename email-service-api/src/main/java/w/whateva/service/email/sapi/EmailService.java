package w.whateva.service.email.sapi;

import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.sapi.sao.ApiEmailCount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EmailService {

    void addEmail(ApiEmail email);

    ApiEmail readEmail(String key);

    List<String> allKeys();

    List<ApiEmail> allEmails();

    List<ApiEmailCount> emailCounts();

    List<ApiEmail> emails(Set<String> names, LocalDateTime after, LocalDateTime before);
}
