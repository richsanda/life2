package w.whateva.life2.api.email;

import w.whateva.life2.api.email.dto.ApiEmail;

public interface EmailService {

    void add(ApiEmail email);

    void addGroupAddressToSenders();
}
