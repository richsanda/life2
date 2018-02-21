package w.whateva.service.email.web.mapper;

import org.springframework.beans.BeanUtils;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.sapi.sao.ApiPerson;

public class PersonMapper {

    public static DtoPerson toDto(ApiPerson apiPerson) {
        if (null == apiPerson) return null;
        DtoPerson dtoPerson = new DtoPerson();
        BeanUtils.copyProperties(apiPerson, dtoPerson);
        return dtoPerson;
    }
}
