package w.whateva.service.email.web.mapper;

import org.springframework.beans.BeanUtils;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.sapi.sao.ApiEmail;

public class EmailMapper {

    public static DtoEmail toDto(ApiEmail apiEmail) {
        if (null == apiEmail) return null;
        DtoEmail dtoEmail = new DtoEmail();
        BeanUtils.copyProperties(apiEmail, dtoEmail);
        return dtoEmail;
    }
}
