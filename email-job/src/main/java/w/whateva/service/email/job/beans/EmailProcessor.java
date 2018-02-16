package w.whateva.service.email.job.beans;

import generated.Email;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import w.whateva.service.email.sapi.sao.ApiEmail;

public class EmailProcessor implements ItemProcessor<ApiEmail, ApiEmail> {

    public ApiEmail process(ApiEmail apiEmail) throws Exception {
        return apiEmail;
    }
}
