package w.whateva.service.email.sapi;

import w.whateva.service.email.sapi.sao.ApiEmail;

import java.util.List;

public interface EmailService {

    void addEmail(ApiEmail email);

    ApiEmail readEmail(String key);

    List<String> allKeys();

    List<ApiEmail> allEmails();
}
