package w.whateva.service.email.job.beans;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import generated.Email;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import w.whateva.service.email.sapi.sao.ApiEmail;

import javax.mail.internet.InternetAddress;
import java.util.*;
import java.util.stream.Collectors;

public class EmailProcessor implements ItemProcessor<ApiEmail, ApiEmail> {

    public ApiEmail process(ApiEmail apiEmail) {
        apiEmail.setTos(toEmailAddresses(apiEmail.getTo()));
        return apiEmail;
    }

    private static Set<String> toSimpleAddresses(String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        return Arrays
                .stream(addressList.split("\\s*[,;]\\s*"))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private static Set<String> toEmailAddresses (String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        try {
            return Arrays
                    .stream(InternetAddress.parse(addressList))
                    .map(InternetAddress::getAddress)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println("Problem with: " + addressList);
            // e.printStackTrace();
        }
        return new HashSet<>();
    }
}
