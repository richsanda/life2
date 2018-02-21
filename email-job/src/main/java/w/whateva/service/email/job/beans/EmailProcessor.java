package w.whateva.service.email.job.beans;

import generated.Email;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import w.whateva.service.email.sapi.sao.ApiEmail;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class EmailProcessor implements ItemProcessor<ApiEmail, ApiEmail> {

    public ApiEmail process(ApiEmail apiEmail) throws Exception {
        if (!StringUtils.isEmpty(apiEmail.getTo())) {
            apiEmail.setTos(new HashSet<>(Arrays.asList(apiEmail.getTo().split("\\s*[,;]\\s*"))));
        }
        return apiEmail;
    }
}
