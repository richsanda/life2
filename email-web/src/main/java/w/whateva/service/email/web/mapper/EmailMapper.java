package w.whateva.service.email.web.mapper;

import org.springframework.beans.BeanUtils;
import w.whateva.service.email.api.dto.Email;
import w.whateva.service.email.sapi.sao.ApiEmail;

public class EmailMapper {

    public static Email toDto(ApiEmail apiEmail) {
        if (null == apiEmail) return null;
        Email email = new Email();
        BeanUtils.copyProperties(apiEmail, email);
        return email;
    }
}
