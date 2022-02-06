package w.whateva.life2.service.email;

import w.whateva.life2.service.email.dto.ApiEmail;

public interface EmailService {

    void add(ApiEmail email);

    void addGroupAddressToSenders();
}
